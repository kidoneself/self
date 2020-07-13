package com.yimao.cloud.user.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.yimao.cloud.base.auth.JWTInfo;
import com.yimao.cloud.base.baideApi.utils.BaideApiUtil;
import com.yimao.cloud.base.cache.UserCache;
import com.yimao.cloud.base.constant.RabbitConstant;
import com.yimao.cloud.base.enums.*;
import com.yimao.cloud.base.exception.BadRequestException;
import com.yimao.cloud.base.exception.NotFoundException;
import com.yimao.cloud.base.exception.YimaoException;
import com.yimao.cloud.base.handler.JWTHandler;
import com.yimao.cloud.base.utils.CollectionUtil;
import com.yimao.cloud.base.utils.MD5Util;
import com.yimao.cloud.base.utils.StringUtil;
import com.yimao.cloud.framework.aop.OperationType;
import com.yimao.cloud.framework.aop.annotation.EnableOperationLog;
import com.yimao.cloud.framework.aop.parser.DefaultContentParse;
import com.yimao.cloud.framework.cache.RedisCache;
import com.yimao.cloud.pojo.dto.station.StationMessageDTO;
import com.yimao.cloud.pojo.dto.system.*;
import com.yimao.cloud.pojo.dto.user.EngineerChangeRecordDTO;
import com.yimao.cloud.pojo.dto.user.EngineerDTO;
import com.yimao.cloud.pojo.dto.user.EngineerServiceAreaDTO;
import com.yimao.cloud.pojo.dto.water.WaterDeviceDTO;
import com.yimao.cloud.pojo.query.station.StationEngineerQuery;
import com.yimao.cloud.pojo.query.user.EngineerQuery;
import com.yimao.cloud.pojo.vo.PageVO;
import com.yimao.cloud.pojo.vo.station.EngineerVO;
import com.yimao.cloud.user.feign.OrderFeign;
import com.yimao.cloud.user.feign.SystemFeign;
import com.yimao.cloud.user.feign.WaterFeign;
import com.yimao.cloud.user.mapper.EngineerChangeRecordMapper;
import com.yimao.cloud.user.mapper.EngineerMapper;
import com.yimao.cloud.user.mapper.EngineerServiceAreaMapper;
import com.yimao.cloud.user.po.Engineer;
import com.yimao.cloud.user.po.EngineerServiceArea;
import com.yimao.cloud.user.service.EngineerService;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Update;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.*;

/**
 * 描述：安装工Service
 *
 * @Author Zhang Bo
 * @Date 2019/2/25
 */
@Service
@Slf4j
public class EngineerServiceImpl implements EngineerService {

    @Resource
    private JWTHandler jwtHandler;
    @Resource
    private UserCache userCache;
    @Resource
    private EngineerMapper engineerMapper;
    @Resource
    private EngineerChangeRecordMapper engineerChangeRecordMapper;
    @Resource
    private RabbitTemplate rabbitTemplate;
    @Resource
    private SystemFeign systemFeign;

    @Resource
    private EngineerServiceAreaMapper engineerServiceAreaMapper;

    @Resource
    private OrderFeign orderFeign;

    @Resource
    private WaterFeign waterFeign;

    /**
     * 安装工登录
     *
     * @param userName 用户名
     * @param password 密码
     * @param appType  登录的手机系统：1-Android；2-ios
     */
    @Override
    public EngineerDTO login(String userName, String password, Integer appType) {
        Engineer query = new Engineer();
        query.setUserName(userName);
        Engineer engineer = engineerMapper.selectOne(query);
        if (engineer == null || !password.equalsIgnoreCase(engineer.getPassword())) {
            throw new BadRequestException("用户不存在或账户密码错误。");
        }
        JWTInfo jwtInfo = new JWTInfo();
        jwtInfo.setId(engineer.getId());
        jwtInfo.setRealName(engineer.getRealName());
        jwtInfo.setType(SystemType.ENGINEER.value);

        String token = jwtHandler.createJWTToken(jwtInfo);

        //更新安装工登录的手机系统，接收消息推送时需要用到
        if (appType != null) {
            Engineer update = new Engineer();
            update.setId(engineer.getId());
            update.setAppType(appType);
            engineerMapper.updateByPrimaryKeySelective(update);
        }

        EngineerDTO dto = new EngineerDTO();
        engineer.convert(dto);
        dto.setToken(token);
        return dto;
    }

    /**
     * 创建安装工
     *
     * @param engineer 安装工信息
     */
    @EnableOperationLog(
            name = "创建安装工",
            type = OperationType.SAVE,
            daoClass = EngineerMapper.class,
            parseClass = DefaultContentParse.class,
            fields = {"userName", "realName", "phone"},
            index = 0,
            queue = RabbitConstant.ENGINEER_CHANGE_RECORD
    )

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
    @Override
    public void save(Engineer engineer) {
        if (engineer == null) {
            throw new BadRequestException("安装工信息不能为空。");
        }

        //安装工必要信息校验
        this.checkEngineer(engineer);

        if (StringUtil.isBlank(engineer.getPassword())) {
            throw new BadRequestException("安装工密码不能为空。");
        }
        //保留原密码
        String pwd = engineer.getPassword();
        engineer.setPassword(MD5Util.encodeMD5(pwd));
        // 校验用户名是否已被使用
        Engineer dbRecord = engineerMapper.selectOneByUserName(engineer.getUserName());
        if (dbRecord != null) {
            throw new BadRequestException("该安装工账号已被别人使用。");
        }
        // 校验手机号是否已被使用
        dbRecord = engineerMapper.selectOneByPhone(engineer.getPhone());
        if (dbRecord != null) {
            throw new BadRequestException("该手机号已被别人使用。");
        }

        // 设置创建人
        engineer.setCreator(userCache.getCurrentAdminRealName());
        // 设置创建时间
        engineer.setCreateTime(new Date());
        engineer.setUpdater(engineer.getCreator());
        engineer.setUpdateTime(engineer.getCreateTime());
        // 设置禁用状态为不禁用（默认）
        engineer.setForbidden(false);
        //安装工工作状态：1-忙碌，0-空闲
        engineer.setState(0);
        //正在进行的工单数
        engineer.setCount(0);
        //累计登录次数
        engineer.setLoginCount(0);
        engineer.setId(null);
        //添加服务站老id
        StationDTO station = systemFeign.getStationById(engineer.getStationId());
        if (station == null) {
            throw new BadRequestException("该安装工没有选择服务站。");
        }
        engineer.setOldSiteId(station.getOldId());

        //设置区域ID，站务系统用此字段区分所属服务站
        /*
         * Integer areaId = redisCache.get(Constant.AREA_CACHE + engineer.getProvince()
         * + "_" + engineer.getCity() + "_" + engineer.getRegion(), Integer.class); if
         * (areaId == null) { areaId =
         * systemFeign.getRegionIdByPCR(engineer.getProvince(), engineer.getCity(),
         * engineer.getRegion()); } engineer.setAreaId(areaId);
         */

        int result = engineerMapper.insert(engineer);
        if (result < 1) {
            throw new YimaoException("操作失败。");
        }
        Engineer update = new Engineer();
        update.setId(engineer.getId());
        update.setOldId(engineer.getId() + "");//设置oldId
        engineerMapper.updateByPrimaryKeySelective(update);

        //根据stationid获取所有售后的服务区域,保存安装工服务区域关系,没有服务区域也可以指新增安装工
        if (!CollectionUtil.isEmpty(station.getServiceAreas())) {
            List<EngineerServiceArea> list = initData(engineer.getId(), station.getServiceAreas(), engineer.getCreator());
            engineerServiceAreaMapper.batchInsert(list);
        }


        //新建安装工主动同步到百得
        try {
            BaideApiUtil.syncEngineerAddOrUpdate(BaideApiUtil.INSERT, engineer.getOldId(), engineer.getProvince(), engineer.getCity(), engineer.getRegion(),
                    engineer.getOldSiteId(), engineer.getStationName(), engineer.getIdCard(), engineer.getSex() == 1 ? "M" : "F",
                    engineer.getForbidden() ? "1" : "0", engineer.getUserName(), engineer.getRealName(), engineer.getPhone());
        } catch (Exception e) {
            log.error("新建安装工主动同步百得失败" + e.getMessage(), e);
            throw new YimaoException("新建安装工主动同步百得失败");
        }

        //成功发送短信
        Map<String, String> map = new HashMap<String, String>();
        map.put("#code#", engineer.getUserName());
        map.put("#code1#", pwd);
        sendMessage(MessageModelTypeEnum.CREATE_ACCOUNT.value, MessagePushObjectEnum.ENGINEER.value, MessageMechanismEnum.REGISTER_SUCCESS.value, MessagePushModeEnum.YIMAO_SMS.value, engineer.getPhone(), map);

        //新增站务系统消息
        StationMessageDTO message = new StationMessageDTO();
        message.setPushType(MessageModelTypeEnum.CREATE_STATION_ENGINEER_ACCOUNT.value);
        message.setCreateTime(new Date());
        message.setReceiverId(engineer.getId());
        message.setTitle("账号创建成功");
        message.setMechanism(MessageMechanismEnum.REGISTER_SUCCESS.value);
        Map<String, String> stationMap = new HashMap<>();
        stationMap.put("#code#", "安装工程师");
        stationMap.put("#code1#", engineer.getUserName());
        message.setContentMap(stationMap);
        message.setPushObject(MessagePushObjectEnum.SYSTEM.value);
        message.setMessageType(0);
        message.setFilterType(null);
        rabbitTemplate.convertAndSend(RabbitConstant.STATION_MESSAGE_PUSH, message);


    }

    /***
     * 初始化数据
     * @param integer
     * @param serviceAreas
     * @param string
     * @return
     */
    private List<EngineerServiceArea> initData(Integer engineerId, List<StationServiceAreaDTO> serviceAreas, String creator) {
        List<EngineerServiceArea> esaList = new ArrayList<EngineerServiceArea>();
        EngineerServiceArea esa = null;
        for (StationServiceAreaDTO ssad : serviceAreas) {
            esa = new EngineerServiceArea();
            esa.setAreaId(ssad.getAreaId());
            esa.setCreateTime(new Date());
            esa.setEngineerId(engineerId);
            esa.setCreator(creator);
            esa.setProvince(ssad.getProvince());
            esa.setCity(ssad.getCity());
            esa.setRegion(ssad.getRegion());
            esaList.add(esa);
        }
        return esaList;
    }

    //安装工注册成功发送短信
    private void sendMessage(Integer type, Integer pushObject, Integer mechanism, Integer pushMode, String phone, Map<String, String> map) {
        SmsMessageDTO smsMessage = new SmsMessageDTO();
        smsMessage.setType(type);
        smsMessage.setPushObject(pushObject);
        smsMessage.setPhone(phone);
        smsMessage.setMechanism(mechanism);
        smsMessage.setPushMode(pushMode);
        smsMessage.setContentMap(map);
        // 发送短信
        rabbitTemplate.convertAndSend(RabbitConstant.SMS_MESSAGE_PUSH, smsMessage);

    }

    /**
     * 根据安装工ID获取安装工信息
     *
     * @param id 安装工ID
     */
    @Override
    public Engineer getById(Integer id) {
        return engineerMapper.selectByPrimaryKey(id);
    }

    /**
     * 根据安装工ID获取安装工信息（消息推送时只需获取很少的几个字段）
     *
     * @param id 安装工ID
     */
    @Override
    public EngineerDTO getBasicInfoByIdForMsgPushInfo(Integer id) {
        return engineerMapper.selectBasicInfoByIdForMsgPushInfo(id);
    }

    /**
     * 根据安装工用户名获取安装工信息
     *
     * @param userName 用户名
     */
    @Override
    public Engineer getByUserName(String userName, String oldId) {
        Engineer query = new Engineer();
        if (StringUtil.isNotEmpty(userName)) {
            query.setUserName(userName);
        }
        if (StringUtil.isNotEmpty(oldId)) {
            query.setOldId(oldId);
        }
        return engineerMapper.selectOne(query);
    }

    /**
     * 根据安装工手机号获取安装工信息
     *
     * @param userName 用户名
     */
    @Override
    public Engineer getByUserPhone(String phone) {
        Engineer query = new Engineer();
        if (StringUtil.isNotEmpty(phone)) {
            query.setPhone(phone);
        }
        query.setForbidden(false);
        List<Engineer> list = engineerMapper.select(query);
        if (CollectionUtil.isEmpty(list)) {
            return null;
        }
        return list.get(0);
    }

    /**
     * 更新安装工信息
     *
     * @param engineer 安装工信息
     */
    /*@EnableOperationLog(
            name = "修改安装工",
            type = OperationType.UPDATE,
            daoClass = EngineerMapper.class,
            parseClass = DefaultContentParse.class,
            fields = {"id", "userName", "realName", "phone", "idCard", "sex", "province", "city", "region", "stationName", "stationCompanyName"},
            index = 0,
            queue = RabbitConstant.ENGINEER_CHANGE_RECORD
    )*/
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
    @Override
    public void update(Engineer engineer) {
        if (engineer.getId() == null) {
            throw new BadRequestException("安装工ID不能为空。");
        }
        //安装工必要信息校验
        this.checkEngineer(engineer);
        // 校验用户名是否已被使用
        Engineer dbRecord = engineerMapper.selectOneByUserName(engineer.getUserName());
        if (dbRecord != null && !Objects.equals(dbRecord.getId(), engineer.getId())) {
            throw new BadRequestException("该安装工账号已被别人使用。");
        }
        //设置区域ID，站务系统用此字段区分所属服务站
        /*
         * Integer areaId = redisCache.get(Constant.AREA_CACHE + engineer.getProvince()
         * + "_" + engineer.getCity() + "_" + engineer.getRegion(), Integer.class); if
         * (areaId == null) { areaId =
         * systemFeign.getRegionIdByPCR(engineer.getProvince(), engineer.getCity(),
         * engineer.getRegion()); } engineer.setAreaId(areaId);
         */
        // 校验手机号是否已被使用
        // 2019-12-01注释掉
        // dbRecord = engineerMapper.selectOneByPhone(engineer.getPhone());
        // if (dbRecord != null && !Objects.equals(dbRecord.getId(), engineer.getId())) {
        //     throw new BadRequestException("该手机号已被别人使用。");
        // }

        //安装工手机号变更，需要更新设备上的手机号
        boolean changedPhone = checkChangedPhone(engineer);
        if (changedPhone) {
            WaterDeviceDTO update = new WaterDeviceDTO();
            update.setEngineerId(engineer.getId());
            update.setEngineerPhone(engineer.getPhone());
            waterFeign.updateDeviceForEngineerPhone(update);
        }
        engineerMapper.updateByPrimaryKeySelective(engineer);

        engineer = engineerMapper.selectByPrimaryKey(engineer.getId());

        //更新完安装工信息,保存安装工服务区域关系
        StationDTO station = systemFeign.getStationById(engineer.getStationId());
        if (station != null && !CollectionUtil.isEmpty(station.getServiceAreas())) {
            //先删除老的关系
            engineerServiceAreaMapper.deleteByEngineerId(engineer.getId());

            List<EngineerServiceArea> list = initData(engineer.getId(), station.getServiceAreas(), engineer.getCreator());
            engineerServiceAreaMapper.batchInsert(list);
        }

        //修改安装工信息主动同步到百得
       /* try {
            BaideApiUtil.syncEngineerAddOrUpdate(BaideApiUtil.UPDATE, engineer.getOldId(), engineer.getProvince(), engineer.getCity(), engineer.getRegion(),
                    engineer.getOldSiteId(), engineer.getStationName(), engineer.getIdCard(), engineer.getSex() == 1 ? "M" : "F",
                    engineer.getForbidden() ? "1" : "0", engineer.getUserName(), engineer.getRealName(), engineer.getPhone());
        } catch (Exception e) {
            log.error("修改安装工信息主动同步到百得失败" + e.getMessage(), e);
            throw new YimaoException("修改安装工信息主动同步到百得失败");
        }*/
    }

    /***
     * 校验有没有更换手机号
     * @param engineer
     * @return
     */
    private boolean checkChangedPhone(Engineer engineer) {
        Engineer en = engineerMapper.selectByPrimaryKey(engineer.getId());
        if (en != null && !StringUtil.isEmpty(en.getPhone()) && !engineer.getPhone().equals(en.getPhone())) {
            return true;
        }
        return false;
    }

    /**
     * 更新安装工密码
     *
     * @param engineer 安装工信息
     */
    @Override
    public void updatePassword(Engineer engineer) {
        if (engineer.getId() == null) {
            throw new BadRequestException("安装工ID不能为空。");
        }
        if (StringUtil.isBlank(engineer.getPassword())) {
            throw new BadRequestException("安装工密码不能为空。");
        }

        engineerMapper.updateByPrimaryKeySelective(engineer);
    }

    /**
     * 禁用/启用安装工账号
     *
     * @param id 安装工ID
     */
    @EnableOperationLog(
            name = "禁用/启用安装工",
            type = OperationType.UPDATE,
            daoClass = EngineerMapper.class,
            parseClass = DefaultContentParse.class,
            fields = {"id", "userName", "realName", "phone", "forbidden"},
            index = 0,
            queue = RabbitConstant.ENGINEER_CHANGE_RECORD
    )
    @Override
    public void forbidden(Engineer engineer) {
        //局部字段更新
        Engineer update = new Engineer();
        update.setId(engineer.getId());
        // 设置更新人
        update.setUpdater(userCache.getCurrentAdminRealName());
        // 设置更新时间
        update.setUpdateTime(new Date());
        if (engineer.getForbidden()) {
            // 设置禁用状态为false
            update.setForbidden(false);
        } else {
            // 设置禁用状态为true
            update.setForbidden(true);
        }
        int result = engineerMapper.updateByPrimaryKeySelective(update);
        if (result < 1) {
            throw new YimaoException("操作失败。");
        }
    }

    /**
     * 解绑（安装工账号和手机ICCID的绑定）
     *
     * @param engineer 安装工
     * @param type
     */
 /*   @EnableOperationLog(
            name = "解绑安装工ICCID",
            type = OperationType.UPDATE,
            daoClass = EngineerMapper.class,
            parseClass = DefaultContentParse.class,
            fields = {"id", "userName", "realName", "phone", "iccid"},
            index = 0,
            queue = RabbitConstant.ENGINEER_CHANGE_RECORD
    )*/
    @Override
    public void unbind(Engineer engineer, Integer type) {
        String updater = null;
        if (type != null && type == 2) {
            //请求来自售后平台
            updater = "售后系统";
        } else {
            updater = userCache.getCurrentAdminRealName();
        }
        Date updateTime = new Date();
        int result = engineerMapper.updateIccidToNull(engineer.getId(), updater, updateTime);
        if (result < 1) {
            throw new YimaoException("操作失败。");
        }
    }

    /**
     * 绑定ICCID
     */
    @EnableOperationLog(
            name = "绑定ICCID",
            type = OperationType.UPDATE,
            daoClass = EngineerMapper.class,
            parseClass = DefaultContentParse.class,
            fields = {"id", "userName", "realName", "phone", "iccid"},
            index = 0,
            queue = RabbitConstant.ENGINEER_CHANGE_RECORD
    )
    @Override
    public void binding(Engineer engineer, String iccid) {
        String updater = userCache.getCurrentAdminRealName();
        int result = engineerMapper.bindingIccid(engineer.getId(), updater, iccid);
        if (result < 1) {
            throw new YimaoException("操作失败。");
        }
    }

    /**
     * 检查iccid是否已经存在
     *
     * @param iccid SIM卡卡号
     */
    @Override
    public Boolean checkEngineerIccid(String iccid) {
        Engineer query = new Engineer();
        query.setIccid(iccid);
        int count = engineerMapper.selectCount(query);
        return count > 0;
    }

    /**
     * 根据省市区查询安装工
     *
     * @param province 省
     * @param city     市
     * @param region   区
     */
    @Override
    public List<EngineerDTO> getEngineerByArea(String province, String city, String region) {
        Example example = new Example(Engineer.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("province", province);
        criteria.andEqualTo("city", city);
        criteria.andEqualTo("region", region);
        criteria.andEqualTo("forbidden", false);
        example.orderBy("count").asc();
        List<Engineer> list = engineerMapper.selectByExample(example);
        return CollectionUtil.batchConvert(list, Engineer.class, EngineerDTO.class);
    }

    /**
     * 根据省市区查询安装工数量（包含已经禁用的）
     */
    @Override
    public int countEngineerByArea(Integer areaId) {
        Engineer query = new Engineer();
        query.setAreaId(areaId);
        return engineerMapper.selectCountByArea(query);
    }

    /**
     * 校验安装工是否存在
     *
     * @param province 省
     * @param city     市
     * @param region   区
     */
    @Override
    public boolean checkEngineerExistsByPCR(Integer areaId) {
        Engineer query = new Engineer();
        query.setAreaId(areaId);
        query.setForbidden(false);
        int count = engineerMapper.selectCountByArea(query);
        return count > 0;
    }

    @Override
    public List<Integer> getEngineerIdsByAreaIds(Set<Integer> areaIds) {
        return engineerMapper.getEngineerIdsByAreaIds(areaIds);
    }

    @Override
    public PageVO<EngineerVO> pageEngineerInfoToStation(Integer pageNum, Integer pageSize, StationEngineerQuery query) {
        // 分页查询
        PageHelper.startPage(pageNum, pageSize);
        Page<EngineerVO> page = engineerMapper.pageEngineerInfoToStation(query);
        return new PageVO<>(pageNum, page);
    }

    /**
     * 分页查询安装工
     *
     * @param pageNum  当前页
     * @param pageSize 每页大小
     * @param query    查询条件
     */
    @Override
    public PageVO<EngineerDTO> page(Integer pageNum, Integer pageSize, EngineerQuery query) {
        // 分页查询
        PageHelper.startPage(pageNum, pageSize);
        Page<EngineerDTO> page = engineerMapper.selectPage(query);
        // PO对象转成DTO对象
        return new PageVO<>(pageNum, page);
    }

    /**
     * 获取安装工修改记录
     *
     * @param engineerId 安装工ID
     */
    @Override
    public List<EngineerChangeRecordDTO> getEngineerChangeRecords(Integer engineerId) {
        return engineerChangeRecordMapper.selectByEngineerId(engineerId);
    }

    /**
     * 安装工必要信息校验
     *
     * @param engineer 安装工
     */
    private void checkEngineer(Engineer engineer) {
        if (StringUtil.isEmpty(engineer.getUserName())) {
            throw new BadRequestException("安装工账号不能为空。");
        }
        if (StringUtil.isEmpty(engineer.getRealName())) {
            throw new BadRequestException("安装工姓名不能为空。");
        }
        if (StringUtil.isEmpty(engineer.getPhone())) {
            throw new BadRequestException("安装工手机号不能为空。");
        }
        if (StringUtil.isEmpty(engineer.getProvince())) {
            throw new BadRequestException("安装工服务的省不能为空。");
        }
        if (StringUtil.isEmpty(engineer.getCity())) {
            throw new BadRequestException("安装工服务的市不能为空。");
        }
        if (StringUtil.isEmpty(engineer.getRegion())) {
            throw new BadRequestException("安装工服务的区不能为空。");
        }
        if (engineer.getSex() == null || SexType.find(engineer.getSex()) == null) {
            throw new BadRequestException("安装工性别选择错误。");
        }
        if (StringUtil.isEmpty(engineer.getIdCard())) {
            throw new BadRequestException("安装工身份证号不能为空。");
        }
        if (engineer.getStationCompanyId() == null || StringUtil.isEmpty(engineer.getStationCompanyName())) {
            throw new BadRequestException("安装工所属服务站公司不能为空。");
        }
        if (engineer.getStationId() == null || StringUtil.isEmpty(engineer.getStationName())) {
            throw new BadRequestException("安装工所属服务站门店不能为空。");
        }
    }

    /**
     * 根据老的安装工ID获取安装工信息
     *
     * @param oldId 老的安装工ID
     */
    @Override
    public Engineer getEngineerByOldId(String oldId) {
        Engineer query = new Engineer();
        query.setOldId(oldId);
        return engineerMapper.selectOne(query);
    }

    /**
     * 站务系统--订单--安装工单--安装工程师筛选条件
     *
     * @param stationIds
     * @return
     */
    @Override
    public List<EngineerVO> getEngineerListByStationIds(Set<Integer> stationIds) {

        return engineerMapper.getEngineerListByStationIds(stationIds);
    }

    /***
     * 获取服务站下所有有效的安装工信息
     */
    @Override
    public List<EngineerDTO> getEngineerListByEngineerId(Integer engineerId) {
        //获取安装工所属服务站
        Engineer e = engineerMapper.selectByPrimaryKey(engineerId);
        if (e == null) {
            log.error("==============安装工信息未找到======" + engineerId);
            throw new BadRequestException("安装工信息未找到");
        }

        if (e.getStationId() == null) {
            log.error("==============安装工未绑定服务站======" + engineerId);
            throw new BadRequestException("安装工未绑定服务站");
        }
        return engineerMapper.getEngineerListByStationId(e.getStationId(), e.getId());
    }

    /***
     * 根据服务站id查询该服务站下的所有安装工
     */
    @Override
    public List<EngineerDTO> getEngineerListByStationId(Integer stationId) {
        return engineerMapper.getEngineerListByStationId(stationId, null);
    }

    /***
     * 安装工禁用(需要转让订单等信息)/启用
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
    @Override
    public void transferEngineer(Integer oldId, Integer newId) {
        Engineer engineer = engineerMapper.selectByPrimaryKey(oldId);
        if (engineer == null) {
            throw new NotFoundException("旧安装工信息未找到");
        }
        Engineer update = new Engineer();
        update.setId(engineer.getId());

        // 设置更新人
        update.setUpdater(userCache.getCurrentAdminRealName());

        //设置更新时间
        update.setUpdateTime(new Date());
        if (engineer.getForbidden()) {
            //启用
            update.setForbidden(false);
        } else {

            //先将旧的安装工的未完成的订单和水机、收益等数据转让给新的安装工
            transferData(oldId, newId);
            //禁用
            update.setForbidden(true);

            //记录转让日志
            try {
                TransferOperationLogDTO trans = new TransferOperationLogDTO();
                trans.setTransferorId(oldId);
                trans.setReceiverId(newId);
                trans.setOperateType(TransferOperateTypeEnum.ENGINEER_TRANSFER.value);
                trans.setOperator(userCache.getCurrentAdminRealName());
                trans.setCreateTime(new Date());
                rabbitTemplate.convertAndSend(RabbitConstant.TRANSFER_OPERATION_LOG, trans);
            } catch (AmqpException e) {
                log.error("===========安装工转让日志保存失败:" + e.getMessage() + "oldId:" + oldId + ",newId:" + newId);
            }
        }
        int result = engineerMapper.updateByPrimaryKeySelective(update);
        if (result < 1) {
            throw new YimaoException("操作失败。");
        }

    }

    /***
     * 安装工转让数据(工单、订单、续费单、收益、水机)
     * @param oldId
     * @param newId
     */
    private void transferData(Integer oldId, Integer newId) {
        Engineer engineer = engineerMapper.selectByPrimaryKey(newId);
        if (engineer == null) {
            throw new NotFoundException("新安装工信息未找到");
        }

        try {
            //转让工单、订单、续费单、收益、水机,此处没有将转让水机单独调用water服务,是为了保持数据的的一致性,在order里控制事务。
            orderFeign.transferData(oldId, newId);
        } catch (Exception e) {
            log.error("============安装工转让数据异常(oldId=" + oldId + ",newId=" + newId + "),异常信息===" + e.getMessage());
            throw new YimaoException("安装工转让数据异常");
        }

    }

    /**
     * 将指定服务区域下的安装工转给指定服务站门店
     *
     * @param transferAreaInfo 转让区域及承包方信息
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
    public void transferEngineerToNewStationByServiceArea(TransferAreaInfoDTO transferAreaInfo) {
        //查询承包方服务站门店信息
        StationDTO station = systemFeign.getStationById(transferAreaInfo.getStationId());
        if (station == null) {
            throw new BadRequestException("承包方服务站门店不存在！");
        }
        //查询承包方服务站公司，一个服务站门店只会对应一个服务站公司
        StationCompanyDTO stationCompany = systemFeign.getStationCompanyByStationId(station.getId()).get(0);
        if (stationCompany == null) {
            throw new BadRequestException("承包方服务站公司不存在！");
        }
        Set<Integer> areaIds = new HashSet<>();
        areaIds.add(transferAreaInfo.getAreaId());
        //查询指定服务区域下的所有安装工id
        List<Integer> engineerIds = this.getEngineerIdsByAreaIds(areaIds);
        if (CollectionUtil.isNotEmpty(engineerIds)) {
            for (Integer engineerId : engineerIds) {
                Engineer update = new Engineer();
                update.setId(engineerId);
                update.setStationId(station.getId());
                update.setStationName(station.getName());
                update.setStationCompanyId(stationCompany.getId());
                update.setStationCompanyName(stationCompany.getName());
                update.setUpdater(userCache.getCurrentAdminRealName());
                update.setUpdateTime(new Date());
                engineerMapper.updateByPrimaryKeySelective(update);
            }
        }
    }

    /**
     * 将原服务于指定服务区域的安装工对该区域的服务权限删除，给指定安装工新增对该地区的服务权限
     *
     * @param transferAreaInfo 转让区域及承包方信息
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
    public void engineerUpdateServiceArea(TransferAreaInfoDTO transferAreaInfo) {
        if (transferAreaInfo.getEngineerId() == null) {
            throw new BadRequestException("请指定服务人员！");
        }
        if (transferAreaInfo.getAreaId() == null) {
            throw new BadRequestException("请指定服务区域！");
        }
        //删除该服务区域原安装工对该区域的服务权限
        engineerServiceAreaMapper.deleteByAreaId(transferAreaInfo.getAreaId());
        //给指定安装工新增对该地区的服务权限
        EngineerServiceArea engineerServiceArea = new EngineerServiceArea();
        engineerServiceArea.setAreaId(transferAreaInfo.getAreaId());
        engineerServiceArea.setEngineerId(transferAreaInfo.getEngineerId());
        engineerServiceArea.setProvince(transferAreaInfo.getProvince());
        engineerServiceArea.setCity(transferAreaInfo.getCity());
        engineerServiceArea.setRegion(transferAreaInfo.getRegion());
        engineerServiceArea.setCreator(userCache.getCurrentAdminRealName());
        engineerServiceArea.setCreateTime(new Date());
        engineerServiceAreaMapper.insertSelective(engineerServiceArea);
    }

    @Override
    public List<EngineerDTO> getEngineerListByArea(Integer areaId) {
        return engineerMapper.getEngineerListByArea(areaId);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
    public void updateEngineerServiceArea(List<StationServiceAreaDTO> serviceAreaList, Integer stationId) {
        Date now = new Date();//定义当前时间
        //查询服务站门店下的所有安装工
        List<EngineerDTO> engineerList = engineerMapper.getEngineerByStationId(stationId);
        if (CollectionUtil.isNotEmpty(engineerList)) {
            List<EngineerServiceArea> list = new ArrayList<>();
            for (EngineerDTO engineer : engineerList) {
                for (StationServiceAreaDTO serviceArea : serviceAreaList) {
                    EngineerServiceArea engineerServiceArea = new EngineerServiceArea();
                    engineerServiceArea.setEngineerId(engineer.getId());
                    engineerServiceArea.setAreaId(serviceArea.getAreaId());
                    engineerServiceArea.setCreator(userCache.getCurrentAdminRealName());
                    engineerServiceArea.setCreateTime(now);
                    engineerServiceArea.setProvince(serviceArea.getProvince());
                    engineerServiceArea.setCity(serviceArea.getCity());
                    engineerServiceArea.setRegion(serviceArea.getRegion());
                    list.add(engineerServiceArea);
                }
                //删除该安装工绑定的服务区域，重新新增
                engineerServiceAreaMapper.deleteByEngineerId(engineer.getId());
            }
            engineerServiceAreaMapper.batchInsert(list);
        }
    }

    @Override
    public List<EngineerServiceAreaDTO> getEngineerServiceArea(Integer id) {
        Example example = new Example(EngineerServiceArea.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("engineerId", id);
        List<EngineerServiceArea> engineerServiceAreas = engineerServiceAreaMapper.selectByExample(example);
        List<EngineerServiceAreaDTO> list = new ArrayList<>();
        EngineerServiceAreaDTO dto = new EngineerServiceAreaDTO();
        if (CollectionUtil.isEmpty(engineerServiceAreas)) {
            return null;
        }
        for (EngineerServiceArea e : engineerServiceAreas) {
            e.convert(dto);
            list.add(dto);
        }
        return list;
    }

    @Override
    public void updateHeadImg(EngineerDTO dto) {
        if (dto.getId() == null) {
            throw new BadRequestException("安装工ID不能为空。");
        }
        if (dto.getHeadImg() == null) {
            throw new BadRequestException("头像不能为空");
        }
        Engineer engineer = new Engineer();
        engineer.setId(dto.getId());
        engineer.setHeadImg(dto.getHeadImg());
        engineerMapper.updateByPrimaryKeySelective(engineer);
    }

    /**
     * 服务站门店修改名称同步修改安装工表中该服务站对应的名称字段
     *
     * @param stationInfo
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
    public void updateStationName(Map<String, Object> stationInfo) {
        Integer stationId = (Integer) stationInfo.get("stationId");
        String newStationName = (String) stationInfo.get("newStationName");
        Engineer query = new Engineer();
        query.setStationId(stationId);
        List<Engineer> engineerList = engineerMapper.select(query);
        if (CollectionUtil.isNotEmpty(engineerList)) {
            Engineer update = new Engineer();
            for (Engineer engineer : engineerList) {
                update.setId(engineer.getId());
                update.setStationName(newStationName);
                engineerMapper.updateByPrimaryKeySelective(update);
            }
        }
    }
}
