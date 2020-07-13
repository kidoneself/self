package com.yimao.cloud.user.service.impl;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.yimao.cloud.base.cache.UserCache;
import com.yimao.cloud.base.constant.Constant;
import com.yimao.cloud.base.constant.RabbitConstant;
import com.yimao.cloud.base.context.SpringContextHolder;
import com.yimao.cloud.base.enums.AgentLevel;
import com.yimao.cloud.base.enums.ChooseRecommendType;
import com.yimao.cloud.base.enums.CreateDistributorAccountType;
import com.yimao.cloud.base.enums.CreateDistributorSourceType;
import com.yimao.cloud.base.enums.CreateDistributorTerminal;
import com.yimao.cloud.base.enums.DistributorOrderStateEnum;
import com.yimao.cloud.base.enums.DistributorOrderType;
import com.yimao.cloud.base.enums.DistributorProtocolSignStateEnum;
import com.yimao.cloud.base.enums.DistributorRoleLevel;
import com.yimao.cloud.base.enums.DistributorType;
import com.yimao.cloud.base.enums.EnterpriseStateEnum;
import com.yimao.cloud.base.enums.EnvironmentEnum;
import com.yimao.cloud.base.enums.FinancialStateEnum;
import com.yimao.cloud.base.enums.MessageMechanismEnum;
import com.yimao.cloud.base.enums.MessageModelTypeEnum;
import com.yimao.cloud.base.enums.MessagePushModeEnum;
import com.yimao.cloud.base.enums.MessagePushObjectEnum;
import com.yimao.cloud.base.enums.OriginEnum;
import com.yimao.cloud.base.enums.OriginatorEnum;
import com.yimao.cloud.base.enums.PayStateEnum;
import com.yimao.cloud.base.enums.PermissionTypeEnum;
import com.yimao.cloud.base.enums.SexType;
import com.yimao.cloud.base.enums.SponsorLevel;
import com.yimao.cloud.base.enums.Terminal;
import com.yimao.cloud.base.enums.UserChangeRecordEnum;
import com.yimao.cloud.base.enums.UserType;
import com.yimao.cloud.base.exception.BadRequestException;
import com.yimao.cloud.base.exception.DialogException;
import com.yimao.cloud.base.exception.NotFoundException;
import com.yimao.cloud.base.exception.YimaoException;
import com.yimao.cloud.base.msg.CommResult;
import com.yimao.cloud.base.properties.DomainProperties;
import com.yimao.cloud.base.service.SmsService;
import com.yimao.cloud.base.utils.BeanHelper;
import com.yimao.cloud.base.utils.CollectionUtil;
import com.yimao.cloud.base.utils.CopyUtil;
import com.yimao.cloud.base.utils.EmailUtil;
import com.yimao.cloud.base.utils.IdcardUtils;
import com.yimao.cloud.base.utils.MD5Util;
import com.yimao.cloud.base.utils.RandomUtil;
import com.yimao.cloud.base.utils.SmsUtil;
import com.yimao.cloud.base.utils.StringUtil;
import com.yimao.cloud.base.utils.ThreadUtil;
import com.yimao.cloud.base.utils.UUIDUtil;
import com.yimao.cloud.base.utils.yunSignUtil.YunSignRegister;
import com.yimao.cloud.base.utils.yunSignUtil.YunSignResult;
import com.yimao.cloud.framework.cache.RedisCache;
import com.yimao.cloud.pojo.dto.station.StationMessageDTO;
import com.yimao.cloud.pojo.dto.system.SmsMessageDTO;
import com.yimao.cloud.pojo.dto.system.StationDTO;
import com.yimao.cloud.pojo.dto.user.DistributorCountDTO;
import com.yimao.cloud.pojo.dto.user.DistributorDTO;
import com.yimao.cloud.pojo.dto.user.DistributorExportDTO;
import com.yimao.cloud.pojo.dto.user.DistributorOrderDTO;
import com.yimao.cloud.pojo.dto.user.DistributorQueryDTO;
import com.yimao.cloud.pojo.dto.user.DistributorRoleDTO;
import com.yimao.cloud.pojo.dto.user.OwnerDistributorDTO;
import com.yimao.cloud.pojo.dto.user.SubDistributorAccountDTO;
import com.yimao.cloud.pojo.dto.user.TransferDistributorDTO;
import com.yimao.cloud.pojo.dto.user.UserAccountDTO;
import com.yimao.cloud.pojo.dto.user.UserChangeRecordDTO;
import com.yimao.cloud.pojo.dto.user.UserChangeRecordListDTO;
import com.yimao.cloud.pojo.dto.user.UserCompanyApplyDTO;
import com.yimao.cloud.pojo.dto.user.UserCompanyDTO;
import com.yimao.cloud.pojo.dto.user.UserCountDTO;
import com.yimao.cloud.pojo.dto.user.UserDTO;
import com.yimao.cloud.pojo.query.station.DistributorQuery;
import com.yimao.cloud.pojo.vo.PageVO;
import com.yimao.cloud.pojo.vo.station.DistributorVO;
import com.yimao.cloud.user.feign.ProductFeign;
import com.yimao.cloud.user.feign.SystemFeign;
import com.yimao.cloud.user.feign.WaterFeign;
import com.yimao.cloud.user.mapper.DistributorMapper;
import com.yimao.cloud.user.mapper.DistributorOrderMapper;
import com.yimao.cloud.user.mapper.DistributorProtocolMapper;
import com.yimao.cloud.user.mapper.DistributorRoleMapper;
import com.yimao.cloud.user.mapper.UserChangeMapper;
import com.yimao.cloud.user.mapper.UserCompanyMapper;
import com.yimao.cloud.user.mapper.UserDistributorApplyMapper;
import com.yimao.cloud.user.mapper.UserMapper;
import com.yimao.cloud.user.po.Distributor;
import com.yimao.cloud.user.po.DistributorOrder;
import com.yimao.cloud.user.po.DistributorProtocol;
import com.yimao.cloud.user.po.DistributorRole;
import com.yimao.cloud.user.po.User;
import com.yimao.cloud.user.po.UserChangeRecord;
import com.yimao.cloud.user.po.UserCompany;
import com.yimao.cloud.user.po.UserCompanyApply;
import com.yimao.cloud.user.po.UserDistributorApply;
import com.yimao.cloud.user.po.WaterDeviceUser;
import com.yimao.cloud.user.service.DistributorOrderService;
import com.yimao.cloud.user.service.DistributorProtocolService;
import com.yimao.cloud.user.service.DistributorRoleService;
import com.yimao.cloud.user.service.DistributorService;
import com.yimao.cloud.user.service.UserCompanyApplyService;
import com.yimao.cloud.user.service.UserService;
import com.yimao.cloud.user.service.WaterDeviceUserService;
import com.yimao.cloud.user.utils.ObjectUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

import static com.yimao.cloud.base.enums.DistributorRoleLevel.D_50;

@Service
@Slf4j
public class DistributorServiceImpl implements DistributorService {

    @Resource
    private UserCache userCache;
    @Resource
    private DistributorMapper distributorMapper;
    @Resource
    private UserService userService;
    @Resource
    private UserMapper userMapper;
    @Resource
    private DistributorRoleMapper distributorRoleMapper;
    @Resource
    private UserCompanyMapper userCompanyMapper;
    @Resource
    private UserCompanyApplyService userCompanyApplyService;
    @Resource
    private DistributorOrderMapper distributorOrderMapper;
    @Resource
    private DistributorOrderService distributorOrderService;
    @Resource
    private RedisCache redisCache;
    @Resource
    private UserChangeMapper userChangeMapper;
    @Resource
    private SystemFeign systemFeign;
    @Resource
    private DistributorProtocolService distributorProtocolService;
    @Resource
    private SmsService smsService;
    @Resource
    private DistributorRoleService distributorRoleService;
    @Resource
    private UserDistributorApplyMapper userDistributorApplyMapper;
    @Resource
    private DomainProperties domainProperties;
    @Resource
    private DistributorProtocolMapper distributorProtocolMapper;
    @Resource
    private RabbitTemplate rabbitTemplate;
    @Resource
    private WaterDeviceUserService waterDeviceUserService;
    @Resource
    private WaterFeign waterFeign;
    @Resource
    private ProductFeign productFeign;
    @Resource
    private YunSignRegister yunSignRegister;

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void insertDistributor(UserDistributorApply userDistributorApply, DistributorOrder distributorOrder) {
        if (userDistributorApply == null) {
            throw new YimaoException("申请信息不能为空");
        }
        if (distributorOrder == null) {
            throw new YimaoException("订单实体类不能为空");
        }
        Date now = new Date();

        Distributor distributor = BeanHelper.copyProperties(userDistributorApply, Distributor.class);
        distributor.setId(null);
        //设置默认属性
        setDefaultValue(distributor);
        //设置经销商账号逻辑
        setAccount(distributor, CreateDistributorAccountType.SYS.value);
        // 设置默认密码(8为随机数)
        String pwd = UUIDUtil.numuuid(8);
        distributor.setPassword(MD5Util.encodeMD5(pwd));
        distributor.setMoney(distributorOrder.getPrice());
        distributor.setQuota(distributorOrder.getQuotaNumber());
        distributor.setRemainingQuota(distributorOrder.getQuotaNumber());
        distributor.setCreateTime(now);

        //设置区域ID，站务系统用此字段区分所属服务站
        if (StringUtil.isNotEmpty(distributor.getProvince()) &&
                StringUtil.isNotEmpty(distributor.getCity()) &&
                StringUtil.isNotEmpty(distributor.getRegion())) {
        	Integer areaId = redisCache.get(Constant.AREA_CACHE + distributor.getProvince() + "_" + distributor.getCity() + "_" + distributor.getRegion(), Integer.class);
            if (areaId == null) {
                areaId = systemFeign.getRegionIdByPCR(distributor.getProvince(), distributor.getCity(), distributor.getRegion());
            }
            distributor.setAreaId(areaId);
        }

        //经销商其它身份信息
        //1-经销商代理身份未设置时 且不是创始人 ，默认设置为经销商
        distributor.setType(DistributorType.DEALER.value);
        distributor.setTerminal(2);//经销商创建端：1-翼猫业务系统；2-经销商app；
        distributor.setSourceType(3);//来源方式：1-翼猫企业二维码;2-翼猫后台上线; 3:经销商app创建账号; 4-资讯分享 ; 5-视频分享; 6-权益卡分享 ; 7-发展经销商二维码；
        distributor.setCompleteTime(now);//设置成为当前经销商身份时间

        if (DistributorRoleLevel.D_950.value == distributorOrder.getRoleLevel()) {
            UserCompanyApplyDTO userCompanyApplyDTO = userCompanyApplyService.getCompanyByOrderId(distributorOrder.getId());
            if (userCompanyApplyDTO == null) {
                throw new YimaoException("升级企业版本需要填写企业资料！");
            }
            //创建企业申请信息
            UserCompany userCompany = BeanHelper.copyProperties(userCompanyApplyDTO, UserCompany.class);
            userCompany.setId(null);
            userCompany.setCreateTime(now);
            userCompany.setUpdater(null);
            userCompany.setUpdateTime(null);
            int count = userCompanyMapper.insertSelective(userCompany);
            if (count != 1) {
                throw new YimaoException("新增企业信息失败");
            }

            distributor.setCompanyId(userCompany.getId());
            distributor.setCompanyName(userCompany.getCompanyName());
        }

        //新增经销商
        int count = distributorMapper.insertSelective(distributor);
        if (count != 1) {
            throw new YimaoException("新增经销商失败");
        }

        //查询订单对应的合同信息
        Example example = new Example(DistributorProtocol.class);
        example.createCriteria().andEqualTo("orderId", distributorOrder.getId());
        DistributorProtocol distributorProtocol = distributorProtocolMapper.selectOneByExample(example);
        if (distributorProtocol != null) {
            //添加经销商id和经销商账号
            distributorProtocol.setDistributorId(distributor.getId());
            distributorProtocol.setDistributorAccount(distributor.getUserName());
            count = distributorProtocolMapper.updateByPrimaryKeySelective(distributorProtocol);
            if (count != 1) {
                throw new YimaoException("修改合同信息失败");
            }
        }

        // 保存注册记录
        saveUserChangeRecord(distributor, distributorOrder.getId());

        //用户信息创建逻辑
        User user = createOrBindingUserWhileCreateDistributor(distributor);

        //将用户id回写该经销商代理商
        Distributor update = new Distributor();
        update.setId(distributor.getId());
        update.setUserId(user.getId());
        update.setOldId(distributor.getId().toString());
        distributorMapper.updateByPrimaryKeySelective(update);

        distributorOrder.setOrderState(1);
        distributorOrder.setCompletionTime(new Date());
        distributorOrder.setDistributorAccount(distributor.getUserName());
        distributorOrder.setDistributorId(distributor.getId());
        distributorOrderMapper.updateByPrimaryKeySelective(distributorOrder);

        //经销商账号注册成功后发送短信给经销商
        sendMessage(distributor.getType(), distributor.getUserName(), pwd, distributor.getPhone(),distributor.getAreaId());
    }

    /**
     * 创建经销商信息时，根据经销商手机号查询是否已经存在未绑定经销商信息的用户数据了
     *
     * @param distributor 经销商信息
     */
    private User createOrBindingUserWhileCreateDistributor(Distributor distributor) {
        Date now = new Date();
        //根据经销商手机号查询是否已经存在未绑定经销商信息的用户数据了
        Example userExample = new Example(User.class);
        Example.Criteria criteria = userExample.createCriteria();
        criteria.andEqualTo("mobile", distributor.getPhone());
        criteria.andIsNull("mid");
        userExample.orderBy("createTime").desc();
        List<User> existsUserList = userMapper.selectByExample(userExample);

        User user = new User();
        if (CollectionUtil.isNotEmpty(existsUserList)) {
            user = existsUserList.get(0);
        } else {
            user.setId(null);
            user.setMobile(distributor.getPhone());
            user.setBindPhoneTime(now);
            user.setNickName(distributor.getRealName());
            user.setSubscribe(false);//是否关注公众号
            user.setOrigin(OriginEnum.YIMAO_BACK_ONLINE.value);
            user.setOriginTerminal(Terminal.ADMIN.value);
            user.setHeadImg(Constant.DEFAULT_HEADIMAGE);
            user.setEmail(distributor.getEmail());
            user.setSex(distributor.getSex());
            user.setCreateTime(now);
        }
        user.setMid(distributor.getId());
        user.setUserName(distributor.getUserName());
        user.setPassword(distributor.getPassword());
        user.setRealName(distributor.getRealName());
        if (distributor.getType() != null && distributor.getType() == DistributorType.PROXY.value) {
            //只是代理商
            WaterDeviceUser deviceUser = waterDeviceUserService.getByPhone(distributor.getPhone());
            if (deviceUser != null) {
                user.setUserType(UserType.USER_3.value);
                user.setUserTypeName(UserType.USER_3.name);
                //设置代理商作为用户身份时的经销商
                Integer distributorId = deviceUser.getDistributorId();
                if (deviceUser.getChildDistributorId() != null) {
                    distributorId = deviceUser.getChildDistributorId();
                }
                if (user.getId() != null && !Objects.equals(user.getDistributorId(), distributorId)) {
                    user.setDistributorId(distributorId);
                    user.setAmbassadorId(null);
                } else {
                    user.setDistributorId(distributorId);
                }
            } else {
                user.setUserType(UserType.USER_4.value);
                user.setUserTypeName(UserType.USER_4.name);
            }
        } else {
            user.setUserType(DistributorRoleLevel.getUserType(distributor.getRoleLevel()));
            user.setUserTypeName(UserType.getNameByType(user.getUserType()));
            user.setDistributorId(distributor.getRecommendId());
            user.setAmbassadorId(null);
            user.setOldDistributorId(distributor.getId().toString());//非老系统的数据oldId和id是一样的
            user.setBindDistributorTime(now);
            user.setBeDistributorTime(now);
        }
        // user.setBindDistributorTime(now);
        // user.setBeDistributorTime(now);
        // user.setDistributorId(distributor.getRecommendId());
        // user.setAmbassadorId(null);
        // user.setOldDistributorId(distributor.getId().toString());//非老系统的数据oldId和id是一样的
        user.setAvailable(true);
        user.setIdCard(distributor.getIdCard());
        user.setProvince(distributor.getProvince());
        user.setCity(distributor.getCity());
        user.setRegion(distributor.getRegion());
        user.setAddress(distributor.getAddress());
        //经销商企业名称
        user.setCompanyName(distributor.getCompanyName());
        if (user.getId() == null) {
            userMapper.insert(user);
            UserChangeRecordDTO dto = new UserChangeRecordDTO(user.getId(), 1, user.getUserType(), user.getMobile(),
                    "创建用户");
            dto.setTerminal(3);
            rabbitTemplate.convertAndSend(RabbitConstant.USER_CHANGE_QUEUE, dto);
        } else {
            userMapper.updateByPrimaryKey(user);
        }
        return user;
    }

    /**
     * 业务系统后台-保存经销商信息
     *
     * @param distributorDTO 经销商信息
     */
    //@UserChangeRecordLog(opeName = "创建账号", changeType = UserChangeRecordEnum.CREATE_ACCOUNT_EVENT)
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
    public void save(DistributorDTO distributorDTO) {
        Date now = new Date();
        //参数校验
        checkSaveUserInfo(distributorDTO);
        Distributor distributor = new Distributor(distributorDTO);
        //设置默认属性
        setDefaultValue(distributor);
        //设置经销商账号逻辑
        setAccount(distributor, distributorDTO.getCreateAccountType());
        //设置默认密码(8为随机数)
        String password = UUIDUtil.numuuid(8);
        distributor.setPassword(password);
        //设置经销商推荐人信息
        if (Objects.nonNull(distributorDTO.getHasRecommend()) && distributorDTO.getHasRecommend()) {
            this.setRecommend(distributor, distributorDTO.getChooseRecommendType());
        }
        // 对密码进行MD5加密 
        distributor.setPassword(MD5Util.encodeMD5(distributor.getPassword()));
        // 设置创建人
        distributor.setCreator(userCache.getCurrentAdminRealName());
        // 设置创建时间
        distributor.setCreateTime(now);
        //经销商禁用状态设置
        distributor.setForbidden(false);
        distributor.setForbiddenOrder(false);
        //地址
        if (StringUtil.isNotEmpty(distributor.getProvince()) &&
                StringUtil.isNotEmpty(distributor.getCity()) &&
                StringUtil.isNotEmpty(distributor.getRegion())) {
            distributor.setAddress(distributor.getProvince() + "/" + distributor.getCity() + "/" + distributor.getRegion());

            //设置区域ID，站务系统用此字段区分所属服务站
            Integer areaId = redisCache.get(Constant.AREA_CACHE + distributor.getProvince() + "_" + distributor.getCity() + "_" + distributor.getRegion(), Integer.class);
            if (areaId == null) {
                areaId = systemFeign.getRegionIdByPCR(distributor.getProvince(), distributor.getCity(), distributor.getRegion());
            }
            distributor.setAreaId(areaId);
        }


        //获取创建用户类型
        Integer dType = distributor.getType();
        //订单号
        Long orderId = null;

        //是否是经销商(仅为业务系统后台提交使用)
        if (DistributorType.isDealer(dType)) {
            //经销商支付金额
            if (distributorDTO.getPayIsZero() != null && distributorDTO.getPayIsZero()) {
                distributor.setMoney(new BigDecimal(0));
            }
            distributor.setCompleteTime(now);

            log.info("新增经销商数据=" + JSON.toJSONString(distributor));
            int result = distributorMapper.insert(distributor);
            if (result < 1) {
                throw new YimaoException("添加经销商失败。");
            } else {
                log.info("新增经销商的主键为=" + distributor.getId());
                //若设置本人为推荐人，则更新新增数据中的推荐人相关数据
                if (ChooseRecommendType.SELF.value == distributorDTO.getChooseRecommendType()) {
                    Distributor updateDistributor = new Distributor();
                    updateDistributor.setId(distributor.getId());
                    updateDistributor.setRecommendId(distributor.getId());
                    updateDistributor.setRecommendName(distributorDTO.getRealName());
                    distributorMapper.updateByPrimaryKeySelective(updateDistributor);
                    //需要回写入用户经销商
                    distributor.setRecommendId(distributor.getId());
                }

                //创建用户信息逻辑
                User user = createOrBindingUserWhileCreateDistributor(distributor);
                //将用户id回写该经销商代理商
                Distributor update = new Distributor();
                update.setId(distributor.getId());
                update.setUserId(user.getId());
                update.setOldId(distributor.getId().toString());
                distributorMapper.updateByPrimaryKeySelective(update);

                //创建订单
                DistributorOrderDTO distributorOrderDTO = new DistributorOrderDTO();

                DistributorOrder distributorOrder = transDistributor2Order(distributor);
                if (distributorDTO.getPayIsZero()) {//为0
                    distributorOrder.setPrice(new BigDecimal(0));
                    distributorOrder.setPayState(PayStateEnum.PAY_UNREQUIRED.value);
                } else {
                    //设置后台配置金额
                    distributorOrder.setPrice(distributor.getMoney());
                    distributorOrder.setPayState(PayStateEnum.PAY.value);
                    distributorOrder.setQuotaNumber(distributor.getQuota());
                }
                if (distributorOrder.getRoleLevel() == DistributorRoleLevel.D_350.value) {
                    //生成微创版经销商需要添加一个升级有效时间
                    DistributorRoleDTO role = distributorRoleService.getByLevel(distributorOrder.getRoleLevel());
                    if (role == null) {
                        throw new YimaoException("经销商类型不存在!");
                    }
                    distributorOrder.setPeriodValidity(role.getUpgradeLimitDays());
                }
                distributorOrder.convert(distributorOrderDTO);
                //获取企业信息
                if (DistributorRoleLevel.D_950.value == distributorDTO.getRoleLevel()) {
                    UserCompanyDTO userCompanyDTO = distributorDTO.getUserCompany();
                    UserCompanyApplyDTO userCompanyApply = BeanHelper.copyProperties(userCompanyDTO, UserCompanyApplyDTO.class);
                    distributorOrderDTO.setUserCompanyApply(userCompanyApply);
                    //设置一下企业名称，再创建或更新用户时设置到用户信息上
                    distributor.setCompanyName(userCompanyApply.getCompanyName());
                }
                log.info("新增经销商订单数据=" + JSON.toJSONString(distributorOrderDTO));

                distributorOrder = distributorOrderService.insertOrderBySystem(distributorOrderDTO);

                orderId = distributorOrder.getId();
                log.info("订单号为=" + orderId);

                // 保存注册记录
                saveUserChangeRecord(distributor, orderId);
                //经销商账号注册成功后发送短信给经销商
                sendMessage(dType, distributor.getUserName(), password, distributor.getPhone(),distributor.getAreaId());
            }
        } else {
            //为代理人或创始人
            log.info("新增代理商或创始人数据=" + JSON.toJSONString(distributor));
            int result = distributorMapper.insert(distributor);
            if (result < 1) {
                throw new YimaoException("添加代理商失败。");
            }
            // 保存注册记录
            saveUserChangeRecord(distributor, orderId);

            //创建用户信息逻辑
            User user = createOrBindingUserWhileCreateDistributor(distributor);
            //将用户id回写该经销商代理商
            Distributor update = new Distributor();
            update.setId(distributor.getId());
            update.setUserId(user.getId());
            update.setOldId(distributor.getId().toString());
            distributorMapper.updateByPrimaryKeySelective(update);

            //经销商账号注册成功后发送短信给经销商
            sendMessage(dType, distributor.getUserName(), password, distributor.getPhone(),distributor.getAreaId());
        }
    }

    /**
     * 经销商账号注册成功后发送短信给经销商
     */
    private void sendMessage(Integer dType, String userName, String password, String phone,Integer areaId) {
        // String text = "【翼猫APP】恭喜您成为翼猫经销商/代理商，您的账号：" + distributor.getUserName() + "，密码： " + distributor.getPassword() + "，请您前往“翼猫APP”及时修改密码。";
        // SmsUtil.sendSms(text, distributor.getPhone());
        log.info("经销商账号注册成功后发送短信给经销商");
        String typename = "";
        if (dType == DistributorType.BOTH.value) {
            typename = "经销商/代 理商";
        } else if (dType == DistributorType.DEALER.value) {
            typename = "经销商";
        } else if (dType == DistributorType.PROXY.value) {
            typename = "代 理商";
        }
        //成功发送短信
        Map<String, String> map = new HashMap<>();
        map.put("#code#", typename);
        map.put("#code1#", userName);
        map.put("#code2#", password);
        sendMessage(MessageModelTypeEnum.CREATE_ACCOUNT.value, MessagePushObjectEnum.DISTRIBUTOR.value, MessageMechanismEnum.REGISTER_SUCCESS.value, MessagePushModeEnum.YIMAO_SMS.value, phone, map);


        //新增站务系统消息
        if(Objects.nonNull(areaId)) {
        	StationMessageDTO message=new StationMessageDTO();
    		message.setPushType(MessageModelTypeEnum.CREATE_STATION_DISTRIBUTOR_ACCOUNT.value);
    		message.setCreateTime(new Date());
    		message.setReceiverId(areaId);
    		message.setTitle("账号创建成功");
    		message.setMechanism(MessageMechanismEnum.REGISTER_SUCCESS.value);
    		Map<String, String> stationMap = new HashMap<>();
    		stationMap.put("#code#", typename);
    		stationMap.put("#code1#", userName);
    		message.setContentMap(stationMap);
    		message.setPushObject(MessagePushObjectEnum.SYSTEM.value);
    		message.setMessageType(0);
    		message.setFilterType(null);
    		rabbitTemplate.convertAndSend(RabbitConstant.STATION_MESSAGE_PUSH, message);
        }
    }

    @Override
    public DistributorVO getDistributorInfoByIdToStation(Integer id) {
        Distributor distributor = distributorMapper.selectByPrimaryKey(id);
        if (distributor == null) {
            throw new YimaoException("该用户不存在。");
        }
        DistributorVO vo = BeanHelper.copyProperties(distributor, DistributorVO.class);
        //只有创始人角色
        if (Objects.nonNull(distributor.getFounder()) && distributor.getFounder() && distributor.getType() == DistributorType.PROXY.value && Objects.equals(null, distributor.getAgentLevel())) {
            //将默认为代理商的角色类型清空
            vo.setType(null);
            return vo;
        }
        //包含代理商
        if (DistributorType.isProxy(distributor.getType())) {
            if (Objects.nonNull(distributor.getAgentLevel())) {
                //判断是否区代
                if (AgentLevel.isRegionAgent(distributor.getAgentLevel())) {
                    //查询发展经销商数量
                    Example example = new Example(Distributor.class);
                    Example.Criteria criteria = example.createCriteria();
                    criteria.andEqualTo("recommendId", id);
                    int count = distributorMapper.selectCountByExample(example);
                    vo.setDistributorCount(count);
                }
            }
        }
        //包含经销商
        if (DistributorType.isDealer(distributor.getType())) {
            //判断是否是企业经销商
            if (DistributorRoleLevel.D_950.value == distributor.getRoleLevel()) {
                //查询企业信息
                if (distributor.getCompanyId() != null) {
                    UserCompany compony = userCompanyMapper.selectByPrimaryKey(distributor.getCompanyId());
                    if (compony != null) {
                        UserCompanyDTO userCompanyDTO = new UserCompanyDTO();
                        compony.convert(userCompanyDTO);
                        vo.setUserCompany(userCompanyDTO);
                    }
                }
                //查询子账号数量
                Example example = new Example(Distributor.class);
                Example.Criteria criteria = example.createCriteria();
                criteria.andEqualTo("pid", id);
                criteria.andEqualTo("roleLevel", DistributorRoleLevel.D_1000.value);
                int count = distributorMapper.selectCountByExample(example);
                vo.setChildAccountCount(count);
            }
            //普通用户数量
            int commonUserCount = countDistributorCustomer(null, id, UserType.USER_3.value);
            vo.setSharerUserCount(commonUserCount);
            //会员用户数量
            int userCount = countDistributorCustomer(null, id, UserType.USER_7.value);
            vo.setSaleUserCount(userCount);
            //经销商销售的水机数量
            int soldWaterDeviceCount = waterFeign.getWaterDeviceCountByDistributorId(vo.getId());
            vo.setSoldWaterDeviceCount(soldWaterDeviceCount);
            //是否有推荐人
            if (vo.getRecommendId() != null) {
                //查询推荐人
                Distributor recommmender = distributorMapper.selectByPrimaryKey(distributor.getRecommendId());

                if (recommmender != null) {
                    //查询推荐人的e家号
                    User recommmenderUser = userMapper.selectByPrimaryKey(recommmender.getUserId());
                    //这里展示经销商对应用户id
                    if (Objects.nonNull(recommmenderUser)) {
                        vo.setRecommendAccountId(recommmenderUser.getId());
                    }

                    vo.setRecommendAccount(recommmender.getUserName());
                    vo.setRecommendName(recommmender.getRealName());
                    vo.setRecommendNickName(recommmender.getNickName());
                    vo.setRecommendPhone(recommmender.getPhone());
                    vo.setRecommendProvince(recommmender.getProvince());
                    vo.setRecommendCity(recommmender.getCity());
                    vo.setRecommendRegion(recommmender.getRegion());
                }
                //是否是经销商子账号
                if (DistributorRoleLevel.D_1000.value == distributor.getRoleLevel()) {
                    //查询主账号信息
                    Distributor mainAccount = distributorMapper.selectByPrimaryKey(distributor.getPid());
                    if (mainAccount != null) {
                        vo.setMainAccount(mainAccount.getUserName());
                        vo.setMainAccountName(mainAccount.getRealName());
                        vo.setMainNickName(mainAccount.getNickName());
                        vo.setMainPhone(mainAccount.getPhone());
                        vo.setMainIdCard(mainAccount.getIdCard());
                        vo.setMainProvince(mainAccount.getProvince());
                        vo.setMainCity(mainAccount.getCity());
                        vo.setMainRegion(mainAccount.getRegion());
                        //查询企业信息
                        UserCompany compony = userCompanyMapper.selectByPrimaryKey(distributor.getCompanyId());
                        if (compony != null) {
                            UserCompanyDTO userCompanyDTO = new UserCompanyDTO();
                            compony.convert(userCompanyDTO);
                            vo.setUserCompany(userCompanyDTO);
                        }
                    }
                }
            }
        }
        return vo;
    }

    /**
     * 查看经销商代理商详情(翼猫后台)
     */
    @Override
    public DistributorDTO getDistirbutorInfoById(Integer id) {
        Distributor distributor = distributorMapper.selectByPrimaryKey(id);
        if (distributor == null) {
            throw new YimaoException("该用户不存在。");
        }
        distributor.setPassword(null);
        DistributorDTO dto = new DistributorDTO();
        distributor.convert(dto);
        //只有创始人角色
        if (Objects.nonNull(distributor.getFounder()) && distributor.getFounder() && distributor.getType() == DistributorType.PROXY.value && Objects.equals(null, distributor.getAgentLevel())) {
            //将默认为代理商的角色类型清空
            dto.setType(null);
            return dto;
        }
        //包含代理商
        if (DistributorType.isProxy(distributor.getType())) {
            if (Objects.nonNull(distributor.getAgentLevel())) {
                //判断是否区代
                if (AgentLevel.isRegionAgent(distributor.getAgentLevel())) {
                    //查询发展经销商数量
                    Example example = new Example(Distributor.class);
                    Example.Criteria criteria = example.createCriteria();
                    criteria.andEqualTo("recommendId", id);
                    int count = distributorMapper.selectCountByExample(example);
                    dto.setDistributorCount(count);
                }
            }
        }
        //包含经销商
        if (DistributorType.isDealer(distributor.getType())) {
            //判断是否是企业经销商
            if (DistributorRoleLevel.D_950.value == distributor.getRoleLevel()) {
                //查询企业信息
                if (distributor.getCompanyId() != null) {
                    UserCompany compony = userCompanyMapper.selectByPrimaryKey(distributor.getCompanyId());
                    if (compony != null) {
                        UserCompanyDTO userCompanyDTO = new UserCompanyDTO();
                        compony.convert(userCompanyDTO);
                        dto.setUserCompany(userCompanyDTO);
                    }
                }
                //查询子账号数量
                Example example = new Example(Distributor.class);
                Example.Criteria criteria = example.createCriteria();
                criteria.andEqualTo("pid", id);
                criteria.andEqualTo("roleLevel", DistributorRoleLevel.D_1000.value);
                int count = distributorMapper.selectCountByExample(example);
                dto.setChildAccountCount(count);
                //查询企业经销商订单id
            }
            //普通用户数量
            int commonUserCount = countDistributorCustomer(null, id, UserType.USER_3.value);
            dto.setSharerUserCount(commonUserCount);
            //会员用户数量
            int userCount = countDistributorCustomer(null, id, UserType.USER_7.value);
            dto.setSaleUserCount(userCount);
            //是否有推荐人
            if (dto.getRecommendId() != null) {
                //查询推荐人
                Distributor recommmender = distributorMapper.selectByPrimaryKey(distributor.getRecommendId());

                if (recommmender != null) {
                    //查询推荐人的e家号
                    User recommmenderUser = userMapper.selectByPrimaryKey(recommmender.getUserId());
                    //这里展示经销商对应用户id
                    if (Objects.nonNull(recommmenderUser)) {
                        dto.setRecommendAccountId(recommmenderUser.getId());
                    }

                    dto.setRecommendAccount(recommmender.getUserName());
                    dto.setRecommendName(recommmender.getRealName());
                    dto.setRecommendNickName(recommmender.getNickName());
                    dto.setRecommendPhone(recommmender.getPhone());
                    dto.setRecommendIdCard(recommmender.getIdCard());
                    dto.setRecommendProvince(recommmender.getProvince());
                    dto.setRecommendCity(recommmender.getCity());
                    dto.setRecommendRegion(recommmender.getRegion());
                }
            }
            //是否是经销商子账号
            if (DistributorRoleLevel.D_1000.value == distributor.getRoleLevel()) {
                //查询主账号信息
                Distributor mainAccount = distributorMapper.selectByPrimaryKey(distributor.getPid());
                if (mainAccount != null) {
                    dto.setMainAccountId(mainAccount.getId());
                    dto.setMainAccount(mainAccount.getUserName());
                    dto.setMainAccountName(mainAccount.getRealName());
                    dto.setMainNickName(mainAccount.getNickName());
                    dto.setMainPhone(mainAccount.getPhone());
                    dto.setMainIdCard(mainAccount.getIdCard());
                    dto.setMainProvince(mainAccount.getProvince());
                    dto.setMainCity(mainAccount.getCity());
                    dto.setMainRegion(mainAccount.getRegion());
                    //查询企业信息
                    UserCompany compony = userCompanyMapper.selectByPrimaryKey(distributor.getCompanyId());
                    if (compony != null) {
                        UserCompanyDTO userCompanyDTO = new UserCompanyDTO();
                        compony.convert(userCompanyDTO);
                        dto.setUserCompany(userCompanyDTO);
                    }
                }
            }
        }
        return dto;
    }

    @Override
    public List<Integer> getDistributorIdsByAreaIds(Set<Integer> areaIds) {

        return distributorMapper.getDistributorIdsByAreaIds(areaIds);
    }

    @Override
    public UserChangeRecordListDTO getChangeInfoByUserIdToStation(Integer id) {
        List<UserChangeRecordDTO> mainPointChangeRecord = new ArrayList<>();
        UserChangeRecordDTO registRecord = null;
        List<UserChangeRecordDTO> renewRecord = new ArrayList<>();
        List<UserChangeRecordDTO> upgradeRecord = new ArrayList<>();
        List<UserChangeRecordDTO> transferRecord = new ArrayList<>();
        List<UserChangeRecordDTO> otherChangeRecord = new ArrayList<>();
        Example example = new Example(UserChangeRecord.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("origDistributorId", id);
        List<UserChangeRecord> channgeRecordList = userChangeMapper.selectByExample(example);

        //获取被转让纪录
        Example transferExample = new Example(UserChangeRecord.class);
        Example.Criteria transferCriteria = transferExample.createCriteria();
        transferCriteria.andEqualTo("destDistributorId", id).andEqualTo("type", UserChangeRecordEnum.TRANSFER_EVENT.value);
        UserChangeRecord transferDestRecord = userChangeMapper.selectOneByExample(transferExample);

        if (Objects.nonNull(transferDestRecord)) {
            UserChangeRecordDTO dto = new UserChangeRecordDTO();
            transferDestRecord.convert(dto);
            transferRecord.add(dto);
        }
        if (CollectionUtil.isNotEmpty(channgeRecordList)) {
            for (UserChangeRecord userChangeRecord : channgeRecordList) {
                Integer type = userChangeRecord.getType();
                if (UserChangeRecordEnum.REGISTERED_EVENT.value == type && userChangeRecord.getOrigDistributorType() != null) {
                    //注册(为经销商注册)
                    registRecord = new UserChangeRecordDTO();
                    registRecord.setTime(userChangeRecord.getTime());
                    registRecord.setCreator(userChangeRecord.getCreator());
                    //有问题-------------------------------------------------------------------------------------------------
                    registRecord.setOrigDistributorType(userChangeRecord.getOrigDistributorType());
                    registRecord.setAmount(userChangeRecord.getAmount());
                    registRecord.setTerminal(userChangeRecord.getTerminal());
                    registRecord.setOrderId(userChangeRecord.getOrderId());
                } else if (UserChangeRecordEnum.RENEWAL_EVENT.value == type) {
                    //续费
                    UserChangeRecordDTO dto = new UserChangeRecordDTO();
                    userChangeRecord.convert(dto);
                    renewRecord.add(dto);
                } else if (UserChangeRecordEnum.UPGRADE_EVENT.value == type) {
                    //升级
                    UserChangeRecordDTO dto = new UserChangeRecordDTO();
                    userChangeRecord.convert(dto);
                    upgradeRecord.add(dto);
                } else if (UserChangeRecordEnum.TRANSFER_EVENT.value == type) {
                    //转让纪录
                    UserChangeRecordDTO dto = new UserChangeRecordDTO();
                    userChangeRecord.convert(dto);
                    transferRecord.add(dto);
                } else if (UserChangeRecordEnum.EDIT.value == type) {
                    //编辑
                    UserChangeRecordDTO dto = new UserChangeRecordDTO();
                    userChangeRecord.convert(dto);
                    //获取json串
                    String origJson = dto.getOrigDistributorData();
                    String destJson = dto.getDestDistributorData();
                    DistributorDTO origDis = JSON.parseObject(origJson, DistributorDTO.class);
                    DistributorDTO destDis = JSON.parseObject(destJson, DistributorDTO.class);
                    dto.setOriginDistributor(origDis);
                    dto.setDestDistributor(destDis);
                    otherChangeRecord.add(dto);
                } else {
                    //主要节点
                    UserChangeRecordDTO dto = new UserChangeRecordDTO();
                    userChangeRecord.convert(dto);
                    mainPointChangeRecord.add(dto);
                }
            }
        }
        return new UserChangeRecordListDTO(mainPointChangeRecord, registRecord, renewRecord, upgradeRecord, transferRecord, otherChangeRecord);
    }

    /**
     * 获取变更记录
     */
    @Override
    public UserChangeRecordListDTO getChangeInfoByUserId(Integer userId) {
        List<UserChangeRecordDTO> mainPointChangeRecord = new ArrayList<>();
        UserChangeRecordDTO registRecord = null;
        List<UserChangeRecordDTO> renewRecord = new ArrayList<>();
        List<UserChangeRecordDTO> upgradeRecord = new ArrayList<>();
        List<UserChangeRecordDTO> transferRecord = new ArrayList<>();
        List<UserChangeRecordDTO> otherChangeRecord = new ArrayList<>();

        List<UserChangeRecordDTO> channgeRecordList = userChangeMapper.selectChangeRecordByDistributorId(userId);

        //获取被转让纪录
        UserChangeRecordDTO transferDestRecord = userChangeMapper.selectDestTransferRecord(userId);

        if (Objects.nonNull(transferDestRecord)) {

            transferRecord.add(transferDestRecord);
        }
        if (CollectionUtil.isNotEmpty(channgeRecordList)) {
            for (UserChangeRecordDTO userChangeRecord : channgeRecordList) {
                Integer type = userChangeRecord.getType();
                if (UserChangeRecordEnum.REGISTERED_EVENT.value == type && userChangeRecord.getOrigDistributorType() != null) {
                    //注册(为经销商注册)
                    registRecord = new UserChangeRecordDTO();
                    registRecord.setTime(userChangeRecord.getTime());
                    registRecord.setCreator(userChangeRecord.getCreator());
                    //有问题-------------------------------------------------------------------------------------------------
                    registRecord.setOrigDistributorType(userChangeRecord.getOrigDistributorType());
                    registRecord.setAmount(userChangeRecord.getAmount());
                    registRecord.setTerminal(userChangeRecord.getTerminal());
                    registRecord.setOrderId(userChangeRecord.getOrderId());
                } else if (UserChangeRecordEnum.RENEWAL_EVENT.value == type) {
                    //续费

                    renewRecord.add(userChangeRecord);
                } else if (UserChangeRecordEnum.UPGRADE_EVENT.value == type) {
                    //升级

                    upgradeRecord.add(userChangeRecord);
                } else if (UserChangeRecordEnum.TRANSFER_EVENT.value == type) {
                    //转让纪录

                    transferRecord.add(userChangeRecord);
                } else if (UserChangeRecordEnum.EDIT.value == type) {
                    //编辑
                    //获取json串
                    String origJson = userChangeRecord.getOrigDistributorData();
                    String destJson = userChangeRecord.getDestDistributorData();
                    DistributorDTO origDis = JSON.parseObject(origJson, DistributorDTO.class);
                    DistributorDTO destDis = JSON.parseObject(destJson, DistributorDTO.class);
                    userChangeRecord.setOriginDistributor(origDis);
                    userChangeRecord.setDestDistributor(destDis);
                    otherChangeRecord.add(userChangeRecord);
                } else {
                    //主要节点

                    mainPointChangeRecord.add(userChangeRecord);
                }
            }
        }
        return new UserChangeRecordListDTO(mainPointChangeRecord, registRecord, renewRecord, upgradeRecord, transferRecord, otherChangeRecord);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Map<String, Object> registDistributorForApp(String mobile, String countryCode, String smsCode, Integer registLevel, UserCompanyApplyDTO userCompanyApplyDTO) {
        log.info("*********传入参数:mobile=" + mobile + ",countryCode=" + countryCode + ",smsCode=" + smsCode + "*********");
        if (mobile == null) {
            throw new BadRequestException("手机号不能空！");
        }
        if (StringUtil.isBlank(smsCode)) {
            throw new BadRequestException("验证码不能空！");
        }
        //校验验证码
        if (!smsService.verifyCode(mobile, Constant.COUNTRY_CODE, smsCode)) {
            throw new BadRequestException("验证码错误");
        }
        if (registLevel == null) {
            throw new BadRequestException("注册类型不能空！");
        }
        //检验注册类型是否正确
        if (DistributorRoleLevel.D_650.value != registLevel &&
                DistributorRoleLevel.D_950.value != registLevel) {
            throw new BadRequestException("注册经销商类型不存在！");
        }
        UserDTO currentUser = userService.getBasicUserById(userCache.getUserId());
        if (currentUser == null) {
            throw new BadRequestException("用户未登录！");
        }
        if (currentUser.getMid() == null) {
            throw new BadRequestException("您还没有创建新账号的权限！");
        }
        DistributorDTO currentDistributor = this.getBasicInfoById(currentUser.getMid());
        //判断当前类型是否可注册
        if (DistributorRoleLevel.D_350.value != currentDistributor.getRoleLevel() &&
                DistributorRoleLevel.D_650.value != currentDistributor.getRoleLevel() &&
                DistributorRoleLevel.D_950.value != currentDistributor.getRoleLevel()) {
            throw new BadRequestException("当前经销商身份不能创建新经销商！");
        }

        DistributorOrder distributorOrder = new DistributorOrder();
        DistributorRoleDTO distributorRole = distributorRoleService.getByLevel(registLevel);

        distributorOrder.setMobile(mobile);
        distributorOrder.setOrderSouce(1);//订单来源:0-H5页面、1-经销商app、2-翼猫业务系统
        distributorOrder.setOrderType(0);//订单类型:0-注册、1-升级、2-续费
        distributorOrder.setPrice(distributorRole.getPrice());
        distributorOrder.setRoleId(distributorRole.getId());
        distributorOrder.setRoleLevel(registLevel);
        distributorOrder.setCreator(currentUser.getId());//关联上
        //冗余字段 审核导出需要
        distributorOrder.setRecommendId(currentDistributor.getRecommendId());
        distributorOrder.setRecommendName(currentDistributor.getRecommendName());
        distributorOrder.setDistributorIdCard(currentDistributor.getIdCard());
        distributorOrder.setProvince(currentDistributor.getProvince());
        distributorOrder.setCity(currentDistributor.getCity());
        distributorOrder.setRegion(currentDistributor.getRegion());
        distributorOrder.setAddress(currentDistributor.getAddress());
        distributorOrder.setRealName(currentDistributor.getRealName());
        //设置区域ID，站务系统用此字段区分所属服务站
        Integer areaId = redisCache.get(Constant.AREA_CACHE + currentDistributor.getProvince() + "_" + currentDistributor.getCity() + "_" + currentDistributor.getRegion(), Integer.class);
        if (areaId == null) {
            areaId = systemFeign.getRegionIdByPCR(currentDistributor.getProvince(), currentDistributor.getCity(), currentDistributor.getRegion());
        }
        distributorOrder.setAreaId(areaId);

        Map<String, Object> resultMap = distributorOrderService.registerOrder(distributorOrder, mobile, userCompanyApplyDTO, currentDistributor.getRealName());
        if (resultMap != null && resultMap.size() > 0) {
            //直接返回老订单号
            return resultMap;
        }

        UserDistributorApply apply = new UserDistributorApply();

        apply.setOrderId(distributorOrder.getId());
        apply.setRealName(currentDistributor.getRealName());
        apply.setSex(currentDistributor.getSex());
        apply.setNickName(currentDistributor.getNickName());
        apply.setPhone(mobile);
        apply.setProvince(currentDistributor.getProvince());
        apply.setCity(currentDistributor.getCity());
        apply.setRegion(currentDistributor.getRegion());
        apply.setAddress(currentDistributor.getAddress());
        apply.setIdCard(currentDistributor.getIdCard());
        apply.setRecommendId(currentDistributor.getRecommendId());
        apply.setOldRecommendId(currentDistributor.getOldRecommendId());
        apply.setRecommendName(currentDistributor.getRecommendName());
        apply.setTerminal(2);
        apply.setRoleId(distributorRole.getId());
        apply.setRoleLevel(distributorRole.getLevel());
        apply.setRoleName(distributorRole.getName());
        apply.setEmail(currentDistributor.getEmail());

        if (DistributorRoleLevel.D_950.value == registLevel) {
            //注册为企业版经销商需要企业信息
            if (userCompanyApplyDTO == null) {
                throw new BadRequestException("当前身份注册企业版账号需要填写企业信息！");
            }
            //企业信息
            UserCompanyApply userCompany = new UserCompanyApply(userCompanyApplyDTO);
            userCompany.setOrderId(distributorOrder.getId());
            userCompany.setCreateTime(new Date());
            userCompany.setCreator(currentDistributor.getRealName());
            userCompanyApplyService.insert(userCompany);
            //经销商订单中应该企业审核为待审核状态( 微创、个人、企业可创建个人、企业账号，个人创建企业账号需要审核企业信息)
            distributorOrder.setEnterpriseState(EnterpriseStateEnum.UN_AUDIT.value);
        }
        apply.setCreator(currentDistributor.getUserName());
        apply.setCreateTime(new Date());
        //保存apply
        userDistributorApplyMapper.insertSelective(apply);

        Map<String, Object> map = new HashMap<>();
        map.put("orderId", distributorOrder.getId());
        return map;
    }

    /**
     * @param
     * @return void
     * @description 注册成功发送经销商短信
     * @author Liu Yi
     * @date 2019/10/10 14:26
     */
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
     * 保存经销商代理商编辑变更操作
     *
     * @param distributor
     */
    public void saveUserChangeRecord(Distributor distributor, Distributor updatedistributor, boolean SubAccount) {
        Integer count = 0;

        UserChangeRecord changeRecord = new UserChangeRecord();

//		  UserDTO user = userMapper.selectBasicUserByMid(distributor.getId());
//		  if (null != user) {
//			  changeRecord.setOrigUserId(user.getId());
//			  changeRecord.setOrigUserType(user.getUserType());
//	        }

        changeRecord.setOrigDistributorId(distributor.getId());
        changeRecord.setTime(new Date());
        changeRecord.setTerminal(distributor.getTerminal());
        changeRecord.setType(UserChangeRecordEnum.EDIT.value);
        changeRecord.setCreator(userCache.getCurrentAdminRealName());

        //考虑新增角色后的内容变化修改
        Integer type = updatedistributor.getType();

        List<String> fieldNames = new ArrayList<String>();
        if (SubAccount) {//子账号
            fieldNames.add("realName");
            fieldNames.add("sex");
            fieldNames.add("phone");
            fieldNames.add("idCard");
            //将其余字段设置为null,若集合中的字段相同也设置为null
            count = ObjectUtil.compareObject(distributor, updatedistributor, fieldNames);
            changeRecord.setOrigDistributorData(JSON.toJSONString(distributor));
            changeRecord.setDestDistributorData(JSON.toJSONString(updatedistributor));
        } else {

            if (DistributorType.PROXY.value == type && Objects.isNull(updatedistributor.getAgentLevel())) {
                //角色类型
                fieldNames.add("province");
                fieldNames.add("city");
                fieldNames.add("region");
                fieldNames.add("userName");
                fieldNames.add("realName");
                fieldNames.add("phone");
                fieldNames.add("founder");
                fieldNames.add("idCard");
                fieldNames.add("fuhuishun");
                fieldNames.add("type");
                fieldNames.add("sex");
                fieldNames.add("agentLevel");
                fieldNames.add("founder");
                //将其余字段设置为null,若集合中的字段相同也设置为null
                count = ObjectUtil.compareObject(distributor, updatedistributor, fieldNames);
                changeRecord.setOrigDistributorData(JSON.toJSONString(distributor));
                changeRecord.setDestDistributorData(JSON.toJSONString(updatedistributor));

            } else if (DistributorType.DEALER.value == type) {
                fieldNames.add("province");
                fieldNames.add("city");
                fieldNames.add("region");
                fieldNames.add("userName");
                fieldNames.add("realName");
                fieldNames.add("phone");
                fieldNames.add("idCard");
                fieldNames.add("fuhuishun");
                fieldNames.add("type");
                fieldNames.add("sex");
                fieldNames.add("recommendId");
                fieldNames.add("recommendName");
                fieldNames.add("agentLevel");
                fieldNames.add("founder");
                //将其余字段设置为null,若集合中的字段相同也设置为null
                count = ObjectUtil.compareObject(distributor, updatedistributor, fieldNames);
                changeRecord.setOrigDistributorData(JSON.toJSONString(distributor));
                changeRecord.setDestDistributorData(JSON.toJSONString(updatedistributor));

            } else if (DistributorType.BOTH.value == type) {
                fieldNames.add("province");
                fieldNames.add("city");
                fieldNames.add("region");
                fieldNames.add("userName");
                fieldNames.add("realName");
                fieldNames.add("phone");
                fieldNames.add("idCard");
                fieldNames.add("fuhuishun");
                fieldNames.add("type");
                fieldNames.add("sex");
                fieldNames.add("recommendId");
                fieldNames.add("recommendName");
                fieldNames.add("sponsor");
                fieldNames.add("sponsorLevel");
                fieldNames.add("provinceRanking");
                fieldNames.add("cityRanking");
                fieldNames.add("regionRanking");
                fieldNames.add("agentLevel");
                fieldNames.add("founder");
                //将其余字段设置为null,若集合中的字段相同也设置为null
                count = ObjectUtil.compareObject(distributor, updatedistributor, fieldNames);
                changeRecord.setOrigDistributorData(JSON.toJSONString(distributor));
                changeRecord.setDestDistributorData(JSON.toJSONString(updatedistributor));

            } else if (DistributorType.PROXY.value == type && Objects.nonNull(updatedistributor.getAgentLevel())) {
                fieldNames.add("province");
                fieldNames.add("city");
                fieldNames.add("region");
                fieldNames.add("userName");
                fieldNames.add("realName");
                fieldNames.add("phone");
                fieldNames.add("idCard");
                fieldNames.add("fuhuishun");
                fieldNames.add("type");
                fieldNames.add("sex");
                fieldNames.add("agentLevel");
                fieldNames.add("sponsor");
                fieldNames.add("sponsorLevel");
                fieldNames.add("provinceRanking");
                fieldNames.add("cityRanking");
                fieldNames.add("regionRanking");
                fieldNames.add("agentLevel");
                fieldNames.add("founder");
                //将其余字段设置为null,若集合中的字段相同也设置为null
                count = ObjectUtil.compareObject(distributor, updatedistributor, fieldNames);
                changeRecord.setOrigDistributorData(JSON.toJSONString(distributor));
                changeRecord.setDestDistributorData(JSON.toJSONString(updatedistributor));

            }
        }

        if (count > 0) {
            //查看变更的数据
            userChangeMapper.insert(changeRecord);
        }

    }

    /**
     * 保存经销商代理商新增变更操作
     *
     * @param distributor
     * @param orderId
     */
    private void saveUserChangeRecord(Distributor distributor, Long orderId) {
        UserChangeRecord changeRecord = new UserChangeRecord();
        changeRecord.setCreator(distributor.getCreator());
        changeRecord.setOrigDistributorId(distributor.getId());
        changeRecord.setOrigAccount(distributor.getUserName());
        changeRecord.setOrigPhone(distributor.getPhone());
//        changeRecord.setOrigUserType(userType);
        changeRecord.setType(UserChangeRecordEnum.REGISTERED_EVENT.value);
        if (Objects.nonNull(distributor.getRoleLevel())) {
            changeRecord.setOrigDistributorType(distributor.getRoleLevel());
        }
        if (Objects.nonNull(orderId)) {
            changeRecord.setOrderId(orderId);
        }
        changeRecord.setAmount(distributor.getMoney());
        changeRecord.setTime(new Date());
        changeRecord.setTerminal(distributor.getTerminal());
        userChangeMapper.insert(changeRecord);
    }

    /**
     * 设置默认属性
     *
     * @param distributor 经销商信息
     */
    private void setDefaultValue(Distributor distributor) {
        Date now = new Date();
        if (distributor.getFounder() == null) {
            distributor.setFounder(false);
        }
        //3-是否为站长：0-否；1-是；默认设置为0
        if (distributor.getStationMaster() == null) {
            distributor.setStationMaster(false);
        }
        //5-是否为发起人：0-不是；1-是；默认设置为0
        if (distributor.getSponsor() == null) {
            distributor.setSponsor(false);
        }
        //6-是否是福慧顺：0-否，1-是；默认设置为0
        if (distributor.getFuhuishun() == null) {
            distributor.setFuhuishun(false);
        }
        //是否禁用
        if (distributor.getForbidden() == null) {
            distributor.setForbidden(false);
        }
        //是否禁止下单
        if (distributor.getForbiddenOrder() == null) {
            distributor.setForbiddenOrder(false);
        }
        //7-经销商角色校验
        Integer type = distributor.getType();
        if (type != null && (type == DistributorType.DEALER.value || type == DistributorType.BOTH.value)) {
            Example e = new Example(DistributorRole.class);
            Example.Criteria criteria = e.createCriteria();
            criteria.andEqualTo("level", distributor.getRoleLevel());
            DistributorRole distributorRole = distributorRoleMapper.selectOneByExample(e);
            if (distributorRole == null) {
                throw new BadRequestException("经销商类型错误。");
            }
            //经销商角色信息设置
            distributor.setMoney(distributorRole.getPrice());
            distributor.setRoleId(distributorRole.getId());
            distributor.setRoleLevel(distributorRole.getLevel());//经销商角色等级
            distributor.setRoleName(distributorRole.getName());//经销商角色名称

            if (distributorRole.getLevel() == DistributorRoleLevel.DISCOUNT.value) {
                //折机版经销商的置换金额设置
                if (Objects.nonNull(distributor.getReplacementAmount())) {
                    distributor.setRemainingReplacementAmount(distributor.getReplacementAmount());
                }
                if (Objects.nonNull(distributor.getQuota())) {
                    distributor.setRemainingQuota(distributor.getQuota());
                }
            } else {
                //其他经销商只会有配额 不会有置换金额
                //distributor.setReplacementAmount(distributorRole.getPrice());
                //distributor.setRemainingReplacementAmount(distributorRole.getPrice());
                distributor.setQuota(distributorRole.getWaterDeviceQuota());//水机配额设置
                distributor.setRemainingQuota(distributorRole.getWaterDeviceQuota());
            }
        }
        //8-设置经销商企业信息
        if (distributor.getCompanyId() != null) {
            UserCompany userCompany = userCompanyMapper.selectByPrimaryKey(distributor.getCompanyId());
            if (userCompany == null) {
                throw new BadRequestException("经销商企业信息填写错误。");
            }
            distributor.setCompanyName(userCompany.getCompanyName());
        }

        //9-是否删除：0-不是；1-是；默认设置为0
        if (distributor.getDeleted() == null) {
            distributor.setDeleted(false);
        }

        if (distributor.getAgentLevel() != null && AgentLevel.isCityAgent(distributor.getAgentLevel())) {
            distributor.setCityTime(now);
        }
        if (distributor.getAgentLevel() != null && AgentLevel.isProvinceAgent(distributor.getAgentLevel())) {
            distributor.setProvincialTime(now);
        }
        if (distributor.getAgentLevel() != null && AgentLevel.isRegionAgent(distributor.getAgentLevel())) {
            distributor.setDistrictTime(now);
        }
        //设置区域ID，站务系统用此字段区分所属服务站
        Integer areaId = redisCache.get(Constant.AREA_CACHE + distributor.getProvince() + "_" + distributor.getCity() + "_" + distributor.getRegion(), Integer.class);
        if (areaId == null) {
            areaId = systemFeign.getRegionIdByPCR(distributor.getProvince(), distributor.getCity(), distributor.getRegion());
        }
        distributor.setAreaId(areaId);
    }

    /**
     * 经销商信息校验
     *
     * @param distributor 经销商信息
     */
    private void checkBasicInfo(Distributor distributor) {
        Integer type = distributor.getType();
        if (StringUtil.isBlank(distributor.getRealName())) {
            throw new BadRequestException("姓名不能为空。");
        }
        if (distributor.getSex() == null) {
            throw new BadRequestException("性别不能为空。");
        }
        if (SexType.find(distributor.getSex()) == null) {
            throw new BadRequestException("性别填写错误。");
        }
        if (StringUtil.isBlank(distributor.getPhone())) {
            throw new BadRequestException("手机号不能为空。");
        }
        if (StringUtil.isBlank(distributor.getIdCard())) {
            throw new BadRequestException("身份证号码不能为空。");
        }
        // 要求：如果只是创始人不需要填写省市区信息
        if (type != null) {
            if (StringUtil.isBlank(distributor.getProvince())) {
                throw new BadRequestException("所属省不能为空。");
            }
            if (StringUtil.isBlank(distributor.getCity())) {
                throw new BadRequestException("所属市不能为空。");
            }
            if (StringUtil.isBlank(distributor.getRegion())) {
                throw new BadRequestException("所属区不能为空。");
            }
        }

        //发起人级别校验
        if (distributor.getSponsor() && (distributor.getSponsorLevel() == null || SponsorLevel.find(distributor.getSponsorLevel()) == null)) {
            throw new BadRequestException("发起人级别填写错误。");
        }
        if (type != null) {
            if (DistributorType.find(type) == null) {
                throw new BadRequestException("经销代理身份填写错误。");
            }
            //代理级别校验
            if (type == DistributorType.PROXY.value || type == DistributorType.BOTH.value) {
                if (distributor.getAgentLevel() == null || AgentLevel.find(distributor.getAgentLevel()) == null) {
                    throw new BadRequestException("代理商级别填写错误。");
                }
                if (AgentLevel.isProvinceAgent(distributor.getAgentLevel()) && distributor.getProvinceRanking() == null) {
                    throw new BadRequestException("代理商省级排名不能为空。");
                }
                if (AgentLevel.isCityAgent(distributor.getAgentLevel()) && distributor.getCityRanking() == null) {
                    throw new BadRequestException("代理商市级排名不能为空。");
                }
                if (AgentLevel.isRegionAgent(distributor.getAgentLevel()) && distributor.getRegionRanking() == null) {
                    throw new BadRequestException("代理商区县级排名不能为空。");
                }
            }
        }

    }

    /**
     * 设置经销商账号
     *
     * @param distributor       经销商信息
     * @param createAccountType 经销商账号创建方式：1-系统自动生成；2-自定义创建；
     */
    private void setAccount(Distributor distributor, Integer createAccountType) {
        //处理经销商账号逻辑
        if (createAccountType == CreateDistributorAccountType.SYS.value) {
            //1-系统创建经销商账号
            String account = generateDistributorAccount();
            distributor.setUserName(account);
        } else {
            //2-自定义经销商账号
            if (StringUtil.isBlank(distributor.getUserName())) {
                throw new BadRequestException("请填写经销商账号。");
            }
            //校验经销商账号是否已被别人使用
            Boolean accountExist = existDistributorAccount(distributor.getUserName().trim());
            if (accountExist) {
                throw new BadRequestException("经销商账号已被使用，请重新填写。");
            }
        }
        distributor.setUserName(distributor.getUserName().trim());
    }

    /**
     * 设置经销商推荐人信息
     *
     * @param distributor         经销商信息
     * @param chooseRecommendType 选择推荐人方式：1-系统自动分配；2-手动选择；3-设置自己为推荐人；
     */
    private void setRecommend(Distributor distributor, Integer chooseRecommendType) {
        //处理推荐人逻辑
        if (chooseRecommendType == ChooseRecommendType.SYS.value) {
            // 设置推荐人
            Example example = new Example(Distributor.class);
            Example.Criteria criteria = example.createCriteria();
            criteria.andEqualTo("province", distributor.getProvince());
            criteria.andEqualTo("city", distributor.getCity());
            criteria.andEqualTo("region", distributor.getRegion());

            List<Integer> agentLevelList = new ArrayList<>();
            agentLevelList.add(AgentLevel.AGENT_R.value);
            agentLevelList.add(AgentLevel.AGENT_CR.value);
            agentLevelList.add(AgentLevel.AGENT_PR.value);
            agentLevelList.add(AgentLevel.AGENT_PCR.value);
            criteria.andIn("agentLevel", agentLevelList);
            List<Distributor> recommendList = distributorMapper.selectByExample(example);
            if (CollectionUtil.isEmpty(recommendList)) {
                throw new BadRequestException("系统自动分配推荐人失败，您选择的地区没有推荐人信息。");
            }
            //随机选择
            int index = RandomUtil.nextInt(0, recommendList.size());
            Distributor recommend = recommendList.get(index);
            distributor.setRecommendId(recommend.getId());
            distributor.setRecommendName(recommend.getRealName());
            distributor.setOldRecommendId(recommend.getOldId());
        } else if (chooseRecommendType == ChooseRecommendType.HAND.value) {
            if (distributor.getRecommendId() == null) {
                throw new BadRequestException("请选择推荐人信息。");
            }
            Distributor recommend = distributorMapper.selectByPrimaryKey(distributor.getRecommendId());
            if (recommend == null) {
                throw new BadRequestException("请选择推荐人信息。");
            }
            distributor.setRecommendId(recommend.getId());
            distributor.setRecommendName(recommend.getRealName());
            distributor.setOldRecommendId(recommend.getOldId());
        }
    }

    /**
     * 获取经销商信息，带相关属性
     *
     * @param distributorId 经销商ID
     */
    @Override
    public DistributorDTO getFullDistributorDTOById(Integer distributorId) {
        Distributor distributor = distributorMapper.selectByPrimaryKey(distributorId);
        return this.getFullDistributorDTOById(distributor);
    }

    /**
     * 获取经销商信息，带相关属性
     *
     * @param distributor 经销商信息
     * @return
     */
    @Override
    public DistributorDTO getFullDistributorDTOById(Distributor distributor) {
        DistributorDTO distributorDTO = new DistributorDTO();
        distributor.convert(distributorDTO);

        // 填充用户信息到经销商信息上
        UserDTO userDTO = userService.getBasicUserById(distributor.getUserId());
        if (Objects.nonNull(userDTO)) {
            distributorDTO.setHeadImage(userDTO.getHeadImg());
            distributorDTO.setUser(userDTO);
        }
        // 填充经销商企业信息
        if (Objects.nonNull(distributor.getCompanyId())) {
            UserCompany userCompany = userCompanyMapper.selectByPrimaryKey(distributor.getCompanyId());
            UserCompanyDTO userCompanyDTO = new UserCompanyDTO();
            userCompany.convert(userCompanyDTO);
            distributorDTO.setUserCompany(userCompanyDTO);
        }

        return distributorDTO;
    }

    @Override
    public PageVO<DistributorVO> pageQueryDistributorToStation(DistributorQuery query, Integer pageNum, Integer pageSize) {
        // 判断是否是经销商或者代理商
        judgeDistributorType(query);
        PageHelper.startPage(pageNum, pageSize);
        Page<DistributorVO> page = distributorMapper.pageQueryDistributorToStation(query);
        return new PageVO<>(pageNum, page);
    }

    /**
     * 分页查询
     *
     * @param query    查询信息
     * @param pageNum  分页页码
     * @param pageSize 分页大小
     * @return com.yimao.cloud.pojo.vo.PageVO<com.yimao.cloud.pojo.dto.user.DistributorDTO>
     * @author hhf
     * @date 2018/12/20
     */
    @Override
    public PageVO<DistributorDTO> pageQueryDistributor(DistributorQueryDTO query, Integer pageNum, Integer pageSize) {
        // 判断是否是经销商或者代理商
        judgeDistributorType(query);
        PageHelper.startPage(pageNum, pageSize);
        Page<DistributorDTO> page = distributorMapper.pageQueryDistributor(query);
        return new PageVO<>(pageNum, page);
    }

    private void judgeDistributorType(DistributorQuery query) {
        Boolean isAgent = query.getIsAgent();
        List<Integer> ids = new ArrayList<>();
        Boolean flag = null;
        if (isAgent != null) {
            if (isAgent) {
                // 是代理商 in （1,3）
                flag = true;
                ids.add(DistributorType.PROXY.value);
                ids.add(DistributorType.BOTH.value);
            } else {
                // 不是代理商 not in （1,3）
                flag = false;
                ids.add(DistributorType.PROXY.value);
                ids.add(DistributorType.BOTH.value);
            }
        }
        query.setFlag(flag);
        query.setTypes(ids);
    }

    private void judgeDistributorType(DistributorQueryDTO query) {
        Boolean isAgent = query.getIsAgent();
        Boolean isDistributor = query.getIsDistributor();
        List<Integer> ids = new ArrayList<>();
        Boolean flag = null;
        if (isAgent != null) {
            if (isDistributor != null) {//经销商代理商条件都不为空
                if (isAgent && isDistributor) {//代理商并且经销商
                    // 1,2,3
                    flag = true;
                    ids.add(DistributorType.BOTH.value);
                }
                if (!isAgent && isDistributor) {//经销商非代理商
                    // 2
                    flag = true;
                    ids.add(DistributorType.DEALER.value);

                }
                if (isAgent && !isDistributor) {//代理商非经销商
                    // 1
                    flag = true;
                    ids.add(DistributorType.PROXY.value);

                }
                if (!isAgent && !isDistributor) {//都不是
                    flag = false;
                    ids.add(DistributorType.PROXY.value);
                    ids.add(DistributorType.DEALER.value);
                    ids.add(DistributorType.BOTH.value);
                }
            } else {//仅判断代理商

                if (isAgent) {
                    // 是代理商 in （1,3）
                    flag = true;
                    ids.add(DistributorType.PROXY.value);
                    ids.add(DistributorType.BOTH.value);
                } else {
                    // 不是代理商 not in （1,3）
                    flag = false;
                    ids.add(DistributorType.PROXY.value);
                    ids.add(DistributorType.BOTH.value);
                }
            }
        } else {
            if (isDistributor != null) {
                // 经销商判断
                if (isDistributor) {
                    // 是经销商  in (2,3)
                    flag = true;
                    ids.add(DistributorType.DEALER.value);
                    ids.add(DistributorType.BOTH.value);

                } else {
                    // 不是经销商 not in (2,3)
                    flag = false;
                    ids.add(DistributorType.DEALER.value);
                    ids.add(DistributorType.BOTH.value);
                }
            }
        }
        query.setFlag(flag);
        query.setTypes(ids);
    }

    private DistributorOrder transDistributor2Order(Distributor distributor) {
        DistributorOrder distributorOrder = new DistributorOrder();
        //distributorOrder.setId(UUIDUtil.buildRegisterWorkOrderId());
        distributorOrder.setMobile(distributor.getPhone());
        distributorOrder.setOrderSouce(2);
        distributorOrder.setOrderType(0);
        distributorOrder.setDistributorAccount(distributor.getUserName());
        distributorOrder.setDistributorId(distributor.getId());
        //distributorOrder.setPrice(distributor.getMoney());
        distributorOrder.setOrderState(DistributorOrderStateEnum.COMPLETED.value);
        distributorOrder.setCompletionTime(new Date());
        distributorOrder.setFinancialState(FinancialStateEnum.NO_NEED_AUDIT.value);

        if (distributor.getRoleLevel() == DistributorRoleLevel.D_950.value) {
            distributorOrder.setEnterpriseState(EnterpriseStateEnum.NO_NEED_AUDIT.value);
        }

        distributorOrder.setRoleId(distributor.getRoleId());
        distributorOrder.setRoleLevel(distributor.getRoleLevel());
        distributorOrder.setCreateTime(new Date());
        return distributorOrder;
    }

    /**
     * 生成经销商账号 YM + year + month + （7 位自随机数字）
     *
     * @author hhf
     * @date 2018/12/24
     */
    public String generateDistributorAccount() {
        String year = new SimpleDateFormat("yy", Locale.CHINESE).format(new Date());
        String month = new SimpleDateFormat("MM", Locale.CHINESE).format(new Date());
        //生成经销商账号 YM + 18 + 12 + （7 位自随机数字）
        String account = "YM" + year + month + UUIDUtil.numuuid(7);
        while (existDistributorAccount(account)) {
            //账号已存在则重新生成
            account = "YM" + year + month + UUIDUtil.numuuid(7);
        }
        //账号不存在则返回
        return account;
    }

    /**
     * 验证经销商账号是否已存在
     *
     * @param account 账号
     * @return java.lang.Boolean
     * @author hhf
     * @date 2018/12/24
     */
    @Override
    public Boolean existDistributorAccount(String account) {
        Distributor query = new Distributor();
        query.setUserName(account);
        query.setDeleted(false);
        int count = distributorMapper.selectCount(query);
        return count > 0;
    }

    /**
     * 删除经销商账号
     *
     * @param id 主键
     */
    @Override
    public void delete(Integer id) {
        Distributor distributor = distributorMapper.selectByPrimaryKey(id);
        if (distributor != null) {
            // 设置更新人
            distributor.setUpdater(userCache.getCurrentAdminRealName());
            // 设置更新时间
            distributor.setUpdateTime(new Date());
            distributor.setDeleted(true);
            distributorMapper.updateByPrimaryKeySelective(distributor);
        }
    }

    /**
     * 禁用/启用经销商账号
     *
     * @param id 主键
     */
    @Override
    public void forbidden(Integer id) {
        Distributor record = distributorMapper.selectByPrimaryKey(id);
        if (record == null) {
            throw new NotFoundException("未找到经销商信息。");
        } else {
            Distributor update = new Distributor();
            update.setId(id);
            // 设置更新人
            update.setUpdater(userCache.getCurrentAdminRealName());
            // 设置更新时间
            update.setUpdateTime(new Date());
            if (record.getForbidden()) {
                update.setForbidden(false);
            } else {
                update.setForbidden(true);
            }
            distributorMapper.updateByPrimaryKeySelective(update);
        }
    }

    /**
     * 禁止/启用经销商下单
     *
     * @param id 主键
     * @return void
     * @author hhf
     * @date 2018/12/24
     */
    @Override
    public void forbiddenOrder(Integer id) {
        Distributor record = distributorMapper.selectByPrimaryKey(id);
        if (record == null) {
            throw new NotFoundException("未找到经销商信息。");
        } else {
            Distributor update = new Distributor();
            update.setId(id);
            // 设置更新人
            update.setUpdater(userCache.getCurrentAdminRealName());
            // 设置更新时间
            update.setUpdateTime(new Date());
            if (record.getForbiddenOrder()) {
                update.setForbiddenOrder(false);
            } else {
                update.setForbiddenOrder(true);
            }
            distributorMapper.updateByPrimaryKeySelective(update);
        }
    }

    /**
     * 修改经销商信息
     *
     * @param dto 经销商信息
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void update(DistributorDTO dto) {

        //是否是子账号
        if (dto.getSubAccount() != null && dto.getSubAccount()) {

            Distributor record = distributorMapper.selectByPrimaryKey(dto.getId());

            Distributor distributor = new Distributor();
            distributor.setId(dto.getId());
            distributor.setRealName(dto.getRealName());
            distributor.setSex(dto.getSex());
            distributor.setPhone(dto.getPhone());
            distributor.setIdCard(dto.getIdCard());
            int result = distributorMapper.updateByPrimaryKeySelective(distributor);
            if (result < 1) {
                throw new YimaoException("修改经销商信息失败。");
            }
            //变更纪录
            saveUserChangeRecord(record, distributor, true);
            return;
        }
        //基本信息校验
        checkSaveBasticInfo(dto);

        //校验角色类型
        if (dto.getFounder() != null && !dto.getFounder() && dto.getType() == null) {
            throw new BadRequestException("用户类型不能为空");
        }

        Distributor updatedistributor = new Distributor();
        //主账号
        Distributor record = distributorMapper.selectByPrimaryKey(dto.getId());

        //如果是站长则省市区无法变更
        if (record.getStationMaster()) {
            if (!record.getProvince().equals(dto.getProvince()) || !record.getCity().equals(dto.getCity()) || !record.getRegion().equals(dto.getRegion())) {
                throw new YimaoException("站长无法编辑省市区。");
            }
            boolean flag = false;
            //判断姓名，手机号，身份证有没有变化，有变化更新站长表
            StationDTO station = new StationDTO();
            station.setMasterDistributorId(record.getId());
            if (Objects.nonNull(dto.getRealName()) && !dto.getRealName().equals(record.getRealName())) {
                station.setMasterName(dto.getRealName());
                flag = true;
            }
            if (Objects.nonNull(dto.getPhone()) && !dto.getPhone().equals(record.getPhone())) {
                station.setMasterPhone(dto.getPhone());
                flag = true;
            }
            if (Objects.nonNull(dto.getIdCard()) && !dto.getIdCard().equals(record.getIdCard())) {
                station.setMasterIdCard(dto.getIdCard());
                flag = true;
            }
            if (flag) {
                systemFeign.updateStationMasterInfo(station);
            }

        }

        if (!record.getUserName().trim().equals(dto.getUserName())) {

            //校验经销商账号是否已被别人使用
            Boolean accountExist = existDistributorAccount(dto.getUserName().trim());
            if (accountExist) {
                throw new YimaoException("经销商账号已被使用，请重新填写。");
            }

            updatedistributor.setUserName(dto.getUserName());
        }
        updatedistributor.setId(record.getId());
        updatedistributor.setRealName(dto.getRealName());
        updatedistributor.setSex(dto.getSex());
        updatedistributor.setPhone(dto.getPhone());
        updatedistributor.setIdCard(dto.getIdCard());
        updatedistributor.setEmail(dto.getEmail());
        updatedistributor.setUserName(dto.getUserName());
        updatedistributor.setProvince(dto.getProvince());
        updatedistributor.setCity(dto.getCity());
        updatedistributor.setRegion(dto.getRegion());
        updatedistributor.setFuhuishun(dto.getFuhuishun());
        updatedistributor.setAttachment(dto.getAttachment());
        updatedistributor.setRemark(dto.getRemark());
        //设置是否为创始人
        updatedistributor.setFounder(dto.getFounder());
        // 设置创建人
        updatedistributor.setUpdater(userCache.getCurrentAdminRealName());
        // 设置创建时间
        updatedistributor.setUpdateTime(new Date());
        updatedistributor.setRecommendId(dto.getRecommendId());
        if (StringUtil.isNotBlank(updatedistributor.getProvince()) &&
                StringUtil.isNotBlank(updatedistributor.getCity()) &&
                StringUtil.isNotBlank(updatedistributor.getRegion())) {
            //设置区域ID，站务系统用此字段区分所属服务站
            Integer areaId = redisCache.get(Constant.AREA_CACHE + updatedistributor.getProvince() + "_" + updatedistributor.getCity() + "_" + updatedistributor.getRegion(), Integer.class);
            if (areaId == null) {
                areaId = systemFeign.getRegionIdByPCR(updatedistributor.getProvince(), updatedistributor.getCity(), updatedistributor.getRegion());
            }
            updatedistributor.setAreaId(areaId);
        }
        //获取原类型
        Integer type = record.getType();
        //如果仅是创始人，则将查询出的类型置为空
        if (record.getFounder() && record.getType() == DistributorType.PROXY.value && Objects.isNull(record.getAgentLevel())) {
            record.setType(null);
        }
        //用户信息更新类
        User user = new User();
        user.setUpdateTime(new Date());
        user.setMid(updatedistributor.getId());
        //姓名、性别、手机号、身份证号、所属区域、经销商账号，需要同步修改用户
        user.setUserName(updatedistributor.getUserName());
        user.setRealName(updatedistributor.getRealName());
        user.setSex(updatedistributor.getSex());
        user.setMobile(updatedistributor.getPhone());
        user.setIdCard(updatedistributor.getIdCard());
        user.setProvince(updatedistributor.getProvince());
        user.setCity(updatedistributor.getCity());
        user.setRegion(updatedistributor.getRegion());

        //仅创始人
        if (Objects.isNull(record.getType())) {

            if (dto.getType() != null) {

                log.info("仅创始人角色，新增角色={}", DistributorType.getDistributorTypeName(dto.getType()));

                if (dto.getType() == DistributorType.PROXY.value) {
                    //代理校验
                    checkSaveProxyInfo(dto);
                    //添加参数
                    updatedistributor.setType(DistributorType.PROXY.value);
                    editProxyParam(dto, updatedistributor, record);
                    //编辑
                    log.info("编辑经销商参数={}", JSON.toJSONString(updatedistributor));

                    int result = distributorMapper.updateByPrimaryKeySelective(updatedistributor);

                    if (result < 1) {
                        throw new YimaoException("修改经销商信息失败。");
                    } else {
                        if (Objects.nonNull(user.getUserName()) ||
                                Objects.nonNull(user.getSex()) ||
                                Objects.nonNull(user.getRealName()) ||
                                Objects.nonNull(user.getMobile()) ||
                                Objects.nonNull(user.getIdCard()) ||
                                Objects.nonNull(user.getProvince()) ||
                                Objects.nonNull(user.getCity()) ||
                                Objects.nonNull(user.getRegion())
                                ) {
                            log.info("编辑用户参数={}", JSON.toJSONString(user));
                            userMapper.updateUserByMid(user);
                        }
                    }

                    //变更纪录
                    saveUserChangeRecord(record, updatedistributor, false);

                    return;

                } else if (dto.getType() == DistributorType.DEALER.value) {
                    //经销商校验
                    checkSaveDealerInfo(dto);
                    //添加参数
                    updatedistributor.setType(DistributorType.DEALER.value);
                    // 添加经销商角色数据
                    editDealerParam(dto, updatedistributor, user, true);
                    user.setUserType(DistributorRoleLevel.getUserType(updatedistributor.getRoleLevel()));
                    user.setUserTypeName(UserType.getNameByType(user.getUserType()));
                    log.info("编辑用户参数={}", JSON.toJSONString(user));
                    userMapper.updateUserByMid(user);

                    //变更纪录
                    saveUserChangeRecord(record, updatedistributor, false);

                    return;

                } else if (dto.getType() == DistributorType.BOTH.value) {
                    //经销商校验
                    checkSaveDealerInfo(dto);
                    //代理校验
                    checkSaveProxyInfo(dto);
                    //添加参数
                    updatedistributor.setType(DistributorType.BOTH.value);

                    editProxyParam(dto, updatedistributor, record);

                    editDealerParam(dto, updatedistributor, user, true);

                    user.setUserType(DistributorRoleLevel.getUserType(updatedistributor.getRoleLevel()));
                    user.setUserTypeName(UserType.getNameByType(user.getUserType()));
                    log.info("编辑用户参数={}", JSON.toJSONString(user));
                    userMapper.updateUserByMid(user);
                    //变更记录
                    saveUserChangeRecord(record, updatedistributor, false);
                    return;

                } else {
                    log.info("error:用户类型不存在");

                    throw new BadRequestException("角色类型错误。");
                }

            } else {
                log.info("仅创始人角色，编辑内容={}", JSON.toJSONString(updatedistributor));
                int result = distributorMapper.updateByPrimaryKeySelective(updatedistributor);
                if (result < 1) {
                    throw new YimaoException("修改经销商信息失败。");
                } else {
                    if (Objects.nonNull(user.getUserName()) ||
                            Objects.nonNull(user.getSex()) ||
                            Objects.nonNull(user.getRealName()) ||
                            Objects.nonNull(user.getMobile()) ||
                            Objects.nonNull(user.getIdCard()) ||
                            Objects.nonNull(user.getProvince()) ||
                            Objects.nonNull(user.getCity()) ||
                            Objects.nonNull(user.getRegion())
                            ) {
                        log.info("编辑用户参数={}", JSON.toJSONString(user));
                        userMapper.updateUserByMid(user);
                    }
                }
                //变更记录
                saveUserChangeRecord(record, updatedistributor, false);

                return;
            }
        }
        //判断是否新增用户角色
        if (record.getType() != dto.getType()) {
            //代理校验
            checkSaveProxyInfo(dto);
            //经销商校验
            checkSaveDealerInfo(dto);

            if (record.getType() == DistributorType.PROXY.value && dto.getType() == DistributorType.BOTH.value) {
                log.info("加入经销商角色");
                //添加参数
                updatedistributor.setType(DistributorType.BOTH.value);
                //设置代理的修改参数，存在代理可编辑参数变更
                editProxyParam(dto, updatedistributor, record);

                distributorMapper.updateAgentLevelAndRecommend(updatedistributor, DistributorType.PROXY.value);

                editDealerParam(dto, updatedistributor, user, true);

                user.setUserType(DistributorRoleLevel.getUserType(updatedistributor.getRoleLevel()));
                user.setUserTypeName(UserType.getNameByType(user.getUserType()));
                log.info("编辑用户参数={}", JSON.toJSONString(user));
                userMapper.updateUserByMid(user);
                //变更记录
                saveUserChangeRecord(record, updatedistributor, false);

                return;

            } else if (record.getType() == DistributorType.DEALER.value && dto.getType() == DistributorType.BOTH.value) {
                log.info("加入代理商角色");
                //添加参数
                updatedistributor.setType(DistributorType.BOTH.value);
                //设置经销商的修改参数，存在经销商可编辑参数变更
                //设置经销商推荐人信息
                if (dto.getHasRecommend()) {
                    setRecommend(updatedistributor, dto.getChooseRecommendType());
                    //若设置本人为推荐人，则更新新增数据中的推荐人相关数据
                    if (ChooseRecommendType.SELF.value == dto.getChooseRecommendType()) {
                        updatedistributor.setRecommendId(dto.getId());
                        updatedistributor.setRecommendName(dto.getRealName());
                    }
                    user.setDistributorId(updatedistributor.getRecommendId());
                } else {
                    //将经销商推荐人以及改经销商对应用户的上级经销商置为空
                    updatedistributor.setRecommendId(null);
                    updatedistributor.setRecommendName(null);
                    //distributorMapper.updateNullFieldByDistributorId(updatedistributor);
                    user.setDistributorId(null);
                    userMapper.updateNullFieldByMidId(user);
                }

                distributorMapper.updateAgentLevelAndRecommend(updatedistributor, DistributorType.DEALER.value);

                editProxyParam(dto, updatedistributor, record);

                log.info("编辑经销商参数={}", JSON.toJSONString(updatedistributor));

                int result = distributorMapper.updateByPrimaryKeySelective(updatedistributor);

                if (result < 1) {
                    throw new YimaoException("修改经销商信息失败。");
                } else {
                    if (Objects.nonNull(user.getUserName()) ||
                            Objects.nonNull(user.getSex()) ||
                            Objects.nonNull(user.getRealName()) ||
                            Objects.nonNull(user.getMobile()) ||
                            Objects.nonNull(user.getIdCard()) ||
                            Objects.nonNull(user.getProvince()) ||
                            Objects.nonNull(user.getCity()) ||
                            Objects.nonNull(user.getRegion()) ||
                            Objects.nonNull(user.getDistributorId())
                            ) {
                        log.info("编辑用户参数={}", JSON.toJSONString(user));
                        userMapper.updateUserByMid(user);
                    }
                }

                //变更记录
                saveUserChangeRecord(record, updatedistributor, false);

                return;
            } else {
                //出现角色减少的问题
                log.info("error:原用户类型=" + DistributorType.getDistributorTypeName(record.getType()) + ",现编辑用户类型=" + DistributorType.getDistributorTypeName(dto.getType()));

                throw new BadRequestException("角色类型错误。");
            }

        } else {

            log.info("用户仅编辑，角色={}", DistributorType.getDistributorTypeName(dto.getType()));
            updatedistributor.setType(dto.getType());
            if (dto.getType() == DistributorType.PROXY.value || dto.getType() == DistributorType.BOTH.value) {
                //代理校验
                checkSaveProxyInfo(dto);

                editProxyParam(dto, updatedistributor, record);

                log.info("编辑代理参数={}", JSON.toJSONString(updatedistributor));

                distributorMapper.updateAgentLevelAndRecommend(updatedistributor, DistributorType.PROXY.value);
            }

            //用户仅编辑,判断现用户角色
            if (dto.getType() == DistributorType.DEALER.value || dto.getType() == DistributorType.BOTH.value) {
                //经销商校验
                checkSaveDealerInfo(dto);

                //设置经销商推荐人信息
                if (dto.getHasRecommend()) {
                    setRecommend(updatedistributor, dto.getChooseRecommendType());
                    //若设置本人为推荐人，则更新新增数据中的推荐人相关数据
                    if (ChooseRecommendType.SELF.value == dto.getChooseRecommendType()) {
                        updatedistributor.setRecommendId(dto.getId());
                        updatedistributor.setRecommendName(dto.getRealName());
                    }

                    user.setDistributorId(updatedistributor.getRecommendId());
                } else {
                    //
                    updatedistributor.setRecommendId(null);
                    updatedistributor.setRecommendName(null);
                    distributorMapper.updateNullFieldByDistributorId(updatedistributor);
                    user.setDistributorId(null);
                    userMapper.updateNullFieldByMidId(user);
                }
                log.info("编辑经销商参数={}", JSON.toJSONString(updatedistributor));

            }
            int result = distributorMapper.updateDistributorById(updatedistributor);
            if (result < 1) {
                throw new YimaoException("修改经销商信息失败。");
            } else {
                if (Objects.nonNull(user.getUserName()) ||
                        Objects.nonNull(user.getSex()) ||
                        Objects.nonNull(user.getRealName()) ||
                        Objects.nonNull(user.getMobile()) ||
                        Objects.nonNull(user.getIdCard()) ||
                        Objects.nonNull(user.getProvince()) ||
                        Objects.nonNull(user.getCity()) ||
                        Objects.nonNull(user.getRegion()) ||
                        Objects.nonNull(user.getDistributorId())
                        ) {
                    log.info("编辑用户参数={}", JSON.toJSONString(user));
                    userMapper.updateUserByMid(user);
                }
            }

            //变更记录
            saveUserChangeRecord(record, updatedistributor, false);

            return;

        }

    }

    /**
     * 将需要编辑经销商参数写入更新类中并更新
     *
     * @param dto               前台传入参数
     * @param updatedistributor 更新数据库的入参
     * @param user
     * @param isNewDealer       是否是新建用户
     */
    private void editDealerParam(DistributorDTO dto, Distributor updatedistributor, User user, boolean isNewDealer) {
        Example e = new Example(DistributorRole.class);
        Example.Criteria criteria = e.createCriteria();
        criteria.andEqualTo("level", dto.getRoleLevel());
        DistributorRole distributorRole = distributorRoleMapper.selectOneByExample(e);
        if (distributorRole == null) {
            throw new BadRequestException("经销商类型错误。");
        }

        //设置经销商推荐人信息
        if (dto.getHasRecommend()) {
            this.setRecommend(updatedistributor, dto.getChooseRecommendType());

            //若设置本人为推荐人，则更新新增数据中的推荐人相关数据
            if (ChooseRecommendType.SELF.value == dto.getChooseRecommendType()) {
                updatedistributor.setRecommendId(dto.getId());
                updatedistributor.setRecommendName(dto.getRealName());
            }
            //这里的user必为经销商用户
            user.setDistributorId(updatedistributor.getRecommendId());
        }

        updatedistributor.setCompleteTime(new Date());
        updatedistributor.setRoleLevel(distributorRole.getLevel());
        if (dto.getPayIsZero()) {
            updatedistributor.setMoney(new BigDecimal(0));
        } else {
            updatedistributor.setMoney(distributorRole.getPrice());
        }

        updatedistributor.setRoleName(distributorRole.getName());
        updatedistributor.setQuota(distributorRole.getWaterDeviceQuota());
        updatedistributor.setRemainingQuota(distributorRole.getWaterDeviceQuota());
        updatedistributor.setRoleId(distributorRole.getId());
        updatedistributor.setReplacementAmount(dto.getReplacementAmount());
        updatedistributor.setRemainingReplacementAmount(dto.getRemainingReplacementAmount());

        log.info("经销商编辑参数=" + JSON.toJSONString(updatedistributor));

        int result = distributorMapper.updateByPrimaryKeySelective(updatedistributor);

        if (result < 1) {
            throw new YimaoException("修改经销商信息失败。");
        }

        //更新user表中的user信息


        if (isNewDealer) {
            //创建订单
            DistributorOrderDTO distributorOrderDTO = new DistributorOrderDTO();

            DistributorOrder distributorOrder = transDistributor2Order(updatedistributor);
            distributorOrder.setCreator(userCache.getUserId());
            if (dto.getPayIsZero()) {//为0
                distributorOrder.setPrice(new BigDecimal(0));
                distributorOrder.setPayState(PayStateEnum.PAY_UNREQUIRED.value);

            } else {
                //设置后台配置金额
                distributorOrder.setPrice(updatedistributor.getMoney());
                distributorOrder.setPayState(PayStateEnum.PAY.value);
                distributorOrder.setQuotaNumber(updatedistributor.getQuota());
            }

            distributorOrder.convert(distributorOrderDTO);
            //获取企业信息
            if (DistributorRoleLevel.D_950.value == dto.getRoleLevel()) {
                UserCompanyDTO userCompanyDTO = dto.getUserCompany();
                UserCompanyApplyDTO userCompanyApply = BeanHelper.copyProperties(userCompanyDTO, UserCompanyApplyDTO.class);
                distributorOrderDTO.setUserCompanyApply(userCompanyApply);
            }

            log.info("新增经销商订单数据=" + JSON.toJSONString(distributorOrderDTO));

            DistributorOrder order = distributorOrderService.insertOrderBySystem(distributorOrderDTO);

            log.info("订单号为=" + order.getId());

            //保存变更纪录
            saveUserChangeRecord(updatedistributor, order.getId());
        }

    }

    /**
     * 将需要编辑代理商参数写入更新类中
     *
     * @param dto               前台传入参数
     * @param updatedistributor 更新数据库的入参
     * @param record            原始数据
     */
    private void editProxyParam(DistributorDTO dto, Distributor updatedistributor, Distributor record) {

        updatedistributor.setAgentLevel(dto.getAgentLevel());
        updatedistributor.setSponsor(dto.getSponsor());
        if (dto.getAgentLevel() != null && AgentLevel.isCityAgent(dto.getAgentLevel()) && Objects.isNull(record.getCityTime())) {
            updatedistributor.setCityTime(new Date());
        }
        if (dto.getAgentLevel() != null && AgentLevel.isProvinceAgent(dto.getAgentLevel()) && Objects.isNull(record.getProvincialTime())) {
            updatedistributor.setProvincialTime(new Date());
        }
        if (dto.getAgentLevel() != null && AgentLevel.isRegionAgent(dto.getAgentLevel()) && Objects.isNull(record.getDistrictTime())) {
            updatedistributor.setDistrictTime(new Date());
        }
//        if (dto.getAgentLevel() != null && ! AgentLevel.isCityAgent(dto.getAgentLevel()) && Objects.nonNull(record.getCityTime())) {
//            updatedistributor.setCityTime(null);
//        }
//        if (dto.getAgentLevel() != null && ! AgentLevel.isProvinceAgent(dto.getAgentLevel()) && Objects.nonNull(record.getProvincialTime())) {
//            updatedistributor.setProvincialTime(null);
//        }
//        if (dto.getAgentLevel() != null && ! AgentLevel.isRegionAgent(dto.getAgentLevel()) && Objects.nonNull(record.getDistrictTime())) {
//            updatedistributor.setDistrictTime(null);
//        }

        //发起人
        if (dto.getSponsor()) {
            updatedistributor.setSponsorLevel(dto.getSponsorLevel());
            if (SponsorLevel.isSponsorProvince(dto.getSponsorLevel())) {
                updatedistributor.setProvinceRanking(dto.getProvinceRanking());
            } else {
                updatedistributor.setProvinceRanking(null);
            }

            if (SponsorLevel.isSponsorCity(dto.getSponsorLevel())) {
                updatedistributor.setCityRanking(dto.getCityRanking());
            } else {
                updatedistributor.setCityRanking(null);
            }

            if (SponsorLevel.isSponsorRegion(dto.getSponsorLevel())) {
                updatedistributor.setRegionRanking(dto.getRegionRanking());
            } else {
                updatedistributor.setRegionRanking(null);
            }

        } else {
            //设置空
            updatedistributor.setSponsorLevel(null);
            updatedistributor.setProvinceRanking(null);
            updatedistributor.setCityRanking(null);
            updatedistributor.setRegionRanking(null);
        }
    }

    /**
     * 根据经销商ID查询经销商（单表信息）
     *
     * @param id 经销商ID
     */
    @Override
    public DistributorDTO getBasicInfoById(Integer id) {
        Distributor distributor = distributorMapper.selectByPrimaryKey(id);
        if (distributor != null) {
            DistributorDTO dto = new DistributorDTO();
            distributor.convert(dto);
            if (null != dto.getCompanyId()) {
                UserCompany userCompany = userCompanyMapper.selectByPrimaryKey(dto.getCompanyId());
                if (userCompany != null) {
                    dto.setCompanyName(userCompany.getCompanyName());
                    dto.setIndustry(userCompany.getIndustry());
                }
            }

            StationDTO station = systemFeign.getStationByPRC(dto.getProvince(), dto.getCity(), dto.getRegion(), PermissionTypeEnum.PRE_SALE.value);
            if (station != null) {
                dto.setStation(station.getName());
            }
            return dto;
        }
        return null;
    }

    /**
     * 根据经销商ID查询经销商（消息推送时只需获取很少的几个字段）
     *
     * @param id 经销商ID
     */
    @Override
    public DistributorDTO getBasicInfoByIdForMsgPushInfo(Integer id) {
        return distributorMapper.selectBasicInfoByIdForMsgPushInfo(id);
    }

    /**
     * 根据经销商ID查询经销商信息（返回基本信息+扩展信息）
     *
     * @param id 经销商ID
     */
    @Override
    public DistributorDTO getExpansionInfoById(Integer id) {

        Distributor distributor = distributorMapper.selectByPrimaryKey(id);
        if (distributor == null) {
            return null;
        }
        DistributorDTO dto = new DistributorDTO();
        distributor.convert(dto);

        Integer recommendId = dto.getRecommendId();
        Integer roleLevel = dto.getRoleLevel();
        Integer distributorId = distributor.getId();
        Integer pid = distributor.getPid();
        // UserDTO userDTO = null;
        DistributorDTO recommend = null;
        DistributorDTO main = null;
        Integer subAccountAmount = null;
        try {
            FutureTask<DistributorDTO> recommendTask = null;
            FutureTask<DistributorDTO> mainTask = null;
            FutureTask<Integer> subAccountAmountTask = null;

            // 推荐人信息
            if (recommendId != null) {
                Callable<DistributorDTO> recommendCallable = () -> this.getBasicInfoById(recommendId);
                recommendTask = new FutureTask<>(recommendCallable);
                ThreadUtil.executor.submit(recommendTask);
            }

            // 主账号信息
            if (pid != null) {
                Callable<DistributorDTO> mainCallable = () -> this.getBasicInfoById(pid);
                mainTask = new FutureTask<>(mainCallable);
                ThreadUtil.executor.submit(mainTask);
            }

            // 企业版经销商当前拥有的子账号数量
            if (roleLevel != null && roleLevel == DistributorRoleLevel.D_950.value) {
                Callable<Integer> subAccountCallable = () -> {
                    Example example = new Example(Distributor.class);
                    Example.Criteria criteria = example.createCriteria();
                    criteria.andEqualTo("pid", id);
                    criteria.andEqualTo("roleLevel", DistributorRoleLevel.D_1000.value);
                    return distributorMapper.selectCountByExample(example);
                };
                subAccountAmountTask = new FutureTask<>(subAccountCallable);
                ThreadUtil.executor.submit(subAccountAmountTask);
            }

            // 分享用户数量
            Callable<Integer> callableShare = () -> countDistributorCustomer(roleLevel, distributorId, UserType.USER_3.value);
            FutureTask<Integer> shareFutureTask = new FutureTask<>(callableShare);
            ThreadUtil.executor.submit(shareFutureTask);

            // 分销用户数量
            Callable<Integer> callableSale = () -> countDistributorCustomer(roleLevel, distributorId, UserType.USER_7.value);
            FutureTask<Integer> saleFutureTask = new FutureTask<>(callableSale);
            ThreadUtil.executor.submit(saleFutureTask);

            // 发展的经销商数量
            Callable<Integer> callableDistributor = () -> {
                Example example = new Example(Distributor.class);
                Example.Criteria criteria = example.createCriteria();
                criteria.andEqualTo("recommendId", id);
                return distributorMapper.selectCountByExample(example);
            };
            FutureTask<Integer> distributorFutureTask = new FutureTask<>(callableDistributor);
            ThreadUtil.executor.submit(distributorFutureTask);

            if (recommendTask != null) {
                recommend = recommendTask.get();
            }
            if (mainTask != null) {
                main = mainTask.get();
            }
            if (subAccountAmountTask != null) {
                subAccountAmount = subAccountAmountTask.get();
            }
            dto.setSharerUserCount(shareFutureTask.get());
            dto.setSaleUserCount(saleFutureTask.get());
            dto.setDistributorCount(distributorFutureTask.get());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
        if (recommend != null) {
            dto.setRecommendAccountId(recommend.getUserId());
            dto.setRecommendPhone(recommend.getPhone());
            dto.setRecommendAccount(recommend.getUserName());
            dto.setRecommendName(recommend.getRealName());
            dto.setRecommendIdCard(recommend.getIdCard());
            dto.setRecommendProvince(recommend.getProvince());
            dto.setRecommendCity(recommend.getCity());
            dto.setRecommendRegion(recommend.getRegion());
            dto.setRecommendName(recommend.getRealName());
        }
        // 如果子账号获取主账号信息
        if (main != null) {
            dto.setMainAccountId(main.getUserId());
            dto.setMainAccount(main.getUserName());
            dto.setRecommendName(main.getRealName());
            dto.setMainPhone(main.getPhone());
            dto.setMainIdCard(main.getIdCard());
            dto.setMainProvince(main.getProvince());
            dto.setMainCity(main.getCity());
            dto.setMainRegion(main.getRegion());
        }
        if (subAccountAmount != null) {
            dto.setChildAccountCount(subAccountAmount);
        }
        return dto;
    }


    /**
     * 获取经销商的客户数量
     *
     * @param roleLevel     经销商角色
     * @param distributorId 经销商ID
     * @param userType      用户类型
     * @return java.lang.Integer
     * @author hhf
     * @date 2018/12/19
     */
    private Integer countDistributorCustomer(Integer roleLevel, Integer distributorId, Integer userType) {
        List<Integer> list = new ArrayList<>();
        if (Objects.nonNull(roleLevel) && roleLevel == DistributorRoleLevel.D_950.value) {
            // 企业版 获取所有的子账号id
            Example example = new Example(Distributor.class);
            Example.Criteria criteria = example.createCriteria();
            criteria.andEqualTo("pid", distributorId);
            criteria.andEqualTo("roleLevel", DistributorRoleLevel.D_1000.value);
            List<Distributor> distributors = distributorMapper.selectByExample(example);
            distributors.forEach(o -> list.add(o.getId()));
        }
        list.add(distributorId);
        Example example = new Example(User.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("userType", userType);
        criteria.andIn("distributorId", list);
        return userMapper.selectCountByExample(example);
    }

    @Override
    public UserCountDTO getUserNum(Integer roleLevel, Integer distributorId) {
        UserCountDTO countDTO = new UserCountDTO();
        Integer shareUserCount = this.countDistributorCustomer(roleLevel, distributorId, UserType.USER_3.value);//分享用户数量
        Integer distUserCount = this.countDistributorCustomer(roleLevel, distributorId, UserType.USER_7.value);//分销商数量
        countDTO.setTotalNum(shareUserCount + distUserCount);//用户总数数量
        Integer todayCount = userMapper.getTotalNum(distributorId);
        Integer monthAddNum = userMapper.getMonthAddNum(distributorId);
        Integer yearAddNum = userMapper.getYearAddNum(distributorId);
        countDTO.setTotalNum(todayCount);//今日新增人数
        countDTO.setMonthAddNum(monthAddNum);//本月新增人数
        countDTO.setYearAddNum(yearAddNum);//本年新增人数
        return countDTO;
    }

    /**
     * 经销商/代理商转让
     *
     * @param id          经销商ID
     * @param transferDTO 变更受理人信息
     */
    // @UserChangeRecordLog(name = "转让", changeType = UserChangeRecordEnum.TRANSFER_EVENT)
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void transferDistributor(Integer id, TransferDistributorDTO transferDTO) {

        Distributor oldDist = distributorMapper.selectByPrimaryKey(id);
        if (oldDist == null) {
            throw new BadRequestException("转让失败，转让人信息错误。");
        }

        //校验经销商账号是否已被别人使用
        boolean accountExist = existDistributorAccount(transferDTO.getUserName().trim());
        if (accountExist) {
            throw new BadRequestException("经销商账号已被使用，请重新填写。");
        }

        //是否转让省代
        boolean transferProvinceAgent = transferDTO.getTransferProvinceAgent() != null && transferDTO.getTransferProvinceAgent();
        //是否转让市代
        boolean transferCityAgent = transferDTO.getTransferCityAgent() != null && transferDTO.getTransferCityAgent();
        //是否转让区县代
        boolean transferRegionAgent = transferDTO.getTransferRegionAgent() != null && transferDTO.getTransferRegionAgent();
        //是否转让经销商
        boolean transferDistributor = transferDTO.getTransferDistributor() != null && transferDTO.getTransferDistributor();
        log.info("转让类型：省代-" + transferProvinceAgent + ",市代-" + transferCityAgent + ",区代-" + transferRegionAgent + ",经销商-" + transferDistributor);
        if (!transferProvinceAgent && !transferCityAgent && !transferRegionAgent && !transferDistributor) {
            throw new BadRequestException("转让失败，请选择转让代理身份或经销身份。");
        }
        //设置新用户默认值
        Distributor newDistributor = new Distributor();
        newDistributor.setCreateTime(new Date());
        newDistributor.setCreator(userCache.getCurrentAdminRealName());
        newDistributor.setUserName(transferDTO.getUserName());
        newDistributor.setSex(transferDTO.getSex());
        newDistributor.setIdCard(transferDTO.getIdCard());
        newDistributor.setPhone(transferDTO.getPhone());
        newDistributor.setAttachment(transferDTO.getAttachment());
        newDistributor.setRemark(transferDTO.getRemark());
        newDistributor.setRealName(transferDTO.getRealName());
        String password = UUIDUtil.numuuid(8);
        newDistributor.setPassword(MD5Util.encodeMD5(password));
        newDistributor.setTerminal(CreateDistributorTerminal.SYSTEM.value);
        newDistributor.setSourceType(2);
        newDistributor.setFounder(false);
        newDistributor.setStationMaster(false);
        newDistributor.setForbidden(false);
        newDistributor.setForbiddenOrder(false);
        newDistributor.setDeleted(false);
        newDistributor.setFuhuishun(false);
        //旧用户更新数据
        Distributor oldDistributor = new Distributor();
        BeanUtils.copyProperties(oldDist, oldDistributor);
        oldDistributor.setUpdateTime(new Date());
        oldDistributor.setUpdater(userCache.getCurrentAdminRealName());
        //转让用户的角色类型，代理级别变化
        Integer newType = 0;
        Integer oldType = oldDist.getType();
        Integer newAgentLevel = 0;
        Integer oldAgentLevel = oldDist.getAgentLevel();
        //发起人
        Integer oldSponsorLevel = 0;
        Integer newSponsorLevel = 0;
        if (oldDist.getSponsor()) {
            oldSponsorLevel = oldDist.getSponsorLevel();

        }
        //是否触发转让
        boolean isProxyTransfer = false;
        boolean isDealerTransfer = false;
        //转让省代,有省代理的权限并转让
        if (transferProvinceAgent) {
            if (!AgentLevel.isProvinceAgent(oldAgentLevel)) {
                throw new BadRequestException("转让失败，转让人没有省代身份。");
            }
            if (!DistributorType.isProxy(oldType)) {
                throw new BadRequestException("转让失败，转让人不是代理商身份，不能转让省代身份。");
            }
            newType = DistributorType.PROXY.value;
            newAgentLevel = newAgentLevel | AgentLevel.AGENT_P.value;
            //将转让人代理身份中的省代身份去掉
            oldAgentLevel = oldAgentLevel ^ AgentLevel.AGENT_P.value;
            if (oldAgentLevel == 0) {
                if (oldType == DistributorType.BOTH.value) {
                    oldType = DistributorType.DEALER.value;
                } else {
                    oldType = 0;
                }
            }
            oldDistributor.setProvincialTime(null);
            newDistributor.setProvincialTime(oldDist.getProvincialTime());
            //去掉省级发起人身份
            if (oldDist.getSponsorLevel() != null && SponsorLevel.isSponsorProvince(oldSponsorLevel)) {
                oldSponsorLevel = oldSponsorLevel ^ SponsorLevel.SPONSOR_P.value;
                newSponsorLevel = newSponsorLevel | SponsorLevel.SPONSOR_P.value;
                newDistributor.setProvinceRanking(oldDist.getProvinceRanking());
                oldDistributor.setProvinceRanking(null);
            }
            isProxyTransfer = true;
        }
        //转让市代，有市代理的权限并转让
        if (transferCityAgent) {
            if (!AgentLevel.isCityAgent(oldAgentLevel)) {
                throw new BadRequestException("转让失败，转让人没有市代身份。");
            }
            if (!DistributorType.isProxy(oldType)) {
                throw new BadRequestException("转让失败，转让人不是代理商身份，不能转让市代身份。");
            }
            newType = DistributorType.PROXY.value;
            newAgentLevel = newAgentLevel | AgentLevel.AGENT_C.value;
            //将转让人代理身份中的市代身份去掉
            oldAgentLevel = oldAgentLevel ^ AgentLevel.AGENT_C.value;
            if (oldAgentLevel == 0) {
                if (oldType == DistributorType.BOTH.value) {
                    oldType = DistributorType.DEALER.value;
                } else {
                    oldType = 0;
                }
            }
            oldDistributor.setCityTime(null);
            newDistributor.setCityTime(oldDist.getCityTime());
            if (oldDist.getSponsorLevel() != null && SponsorLevel.isSponsorCity(oldSponsorLevel)) {
                oldSponsorLevel = oldSponsorLevel ^ SponsorLevel.SPONSOR_C.value;
                newSponsorLevel = newSponsorLevel | SponsorLevel.SPONSOR_C.value;
                newDistributor.setCityRanking(oldDist.getCityRanking());
                oldDistributor.setCityRanking(null);
            }
            isProxyTransfer = true;
        }
        //转让区代
        if (transferRegionAgent) {
            if (!AgentLevel.isRegionAgent(oldAgentLevel)) {
                throw new BadRequestException("转让失败，转让人没有区代身份。");
            }
            if (!DistributorType.isProxy(oldType)) {
                throw new BadRequestException("转让失败，转让人不是代理商身份，不能转让区代身份。");
            }
            newType = DistributorType.PROXY.value;
            newAgentLevel = newAgentLevel | AgentLevel.AGENT_R.value;
            //将转让人代理身份中的区代身份去掉
            oldAgentLevel = oldAgentLevel ^ AgentLevel.AGENT_R.value;
            if (oldAgentLevel == 0) {
                if (oldType == DistributorType.BOTH.value) {
                    oldType = DistributorType.DEALER.value;
                } else {
                    oldType = 0;
                }
            }
            oldDistributor.setDistrictTime(null);
            newDistributor.setDistrictTime(oldDist.getDistrictTime());
            newDistributor.setAreaId(oldDist.getAreaId());
            if (oldDist.getSponsorLevel() != null && SponsorLevel.isSponsorRegion(oldSponsorLevel)) {
                oldSponsorLevel = oldSponsorLevel ^ SponsorLevel.SPONSOR_R.value;
                newSponsorLevel = newSponsorLevel | SponsorLevel.SPONSOR_R.value;
                newDistributor.setRegionRanking(oldDist.getRegionRanking());
                oldDistributor.setRegionRanking(null);
            }
            isProxyTransfer = true;
        }

        if (transferDistributor) {//转让经销商
            if (newType == 0) {
                newType = DistributorType.DEALER.value;
            } else {
                newType = DistributorType.BOTH.value;
            }
            if (oldType == DistributorType.BOTH.value) {
                oldType = DistributorType.PROXY.value;
            } else {
                oldType = 0;
            }
            oldDistributor.setCompleteTime(null);
            newDistributor.setCompleteTime(new Date());
            isDealerTransfer = true;
        }
        log.info("转让新用户类型:newtype=" + newType + ",原用户转让后的类型:oldType=" + oldType);
        log.info("转让新用户的代理级别newAgentLevel=" + newAgentLevel + ",原用户转让后的代理级别:oldAgentLevel=" + oldAgentLevel);

        //设置发起人的更改
        if (oldDist.getSponsor()) {
            if (newSponsorLevel > 0) {
                newDistributor.setSponsor(true);
                newDistributor.setSponsorLevel(newSponsorLevel);
                oldDistributor.setSponsorLevel(oldSponsorLevel);
            }
            if (oldSponsorLevel == 0) {
                oldDistributor.setSponsor(false);
            }
        } else {
            newDistributor.setSponsor(false);
        }
        //更新信息
        newDistributor.setType(newType);
        newDistributor.setProvince(oldDist.getProvince());
        newDistributor.setCity(oldDist.getCity());
        newDistributor.setRegion(oldDist.getRegion());
        newDistributor.setAreaId(oldDist.getAreaId());
        oldDistributor.setType(oldType);


        //更新与新增用户信息设置
        User updateTransferUser = new User();
        updateTransferUser.setId(oldDistributor.getUserId());
        updateTransferUser.setUpdateTime(new Date());

        if (isProxyTransfer) {//转让代理
            //设置转让用户代理相关数据
            if (oldAgentLevel == 0) {
                oldDistributor.setAgentLevel(null);
            } else {
                oldDistributor.setAgentLevel(oldAgentLevel);
            }

            //设置新用户代理相关数据
            newDistributor.setAgentLevel(newAgentLevel);
        }
        if (isDealerTransfer) {//转让经销商
            newDistributor.setMoney(oldDist.getMoney());
            newDistributor.setRoleId(oldDist.getRoleId());
            newDistributor.setRoleLevel(oldDist.getRoleLevel());
            newDistributor.setRoleName(oldDist.getRoleName());
            newDistributor.setCompanyId(oldDist.getCompanyId());
            newDistributor.setCompanyName(oldDist.getCompanyName());
            newDistributor.setPid(oldDist.getPid());
            newDistributor.setQuota(oldDist.getQuota());
            newDistributor.setRemainingQuota(oldDist.getRemainingQuota());
            newDistributor.setReplacementAmount(oldDist.getReplacementAmount());
            newDistributor.setRemainingReplacementAmount(oldDist.getRemainingReplacementAmount());
            newDistributor.setRecommendId(oldDist.getRecommendId());
            newDistributor.setRecommendName(oldDist.getRecommendName());
            newDistributor.setAreaId(oldDist.getAreaId());
            //删除转让用户的经销商内容
            oldDistributor.setMoney(null);
            oldDistributor.setRoleId(null);
            oldDistributor.setRoleLevel(null);
            oldDistributor.setRoleName(null);
            oldDistributor.setCompanyId(null);
            oldDistributor.setCompanyName(null);
            oldDistributor.setPid(null);
            oldDistributor.setQuota(null);
            oldDistributor.setRemainingQuota(null);
            oldDistributor.setReplacementAmount(null);
            oldDistributor.setRemainingReplacementAmount(null);
            oldDistributor.setRecommendId(null);
            oldDistributor.setRecommendName(null);

            updateTransferUser.setUserType(UserType.USER_4.value);
            updateTransferUser.setUserTypeName(UserType.USER_4.name);
            updateTransferUser.setMid(null);
            updateTransferUser.setUserName(null);
            updateTransferUser.setPassword(null);
            updateTransferUser.setDistributorId(null);
            updateTransferUser.setOldDistributorId(null);
        }
        //更新原经销商对应的用户信息
        // userMapper.updateUserByMid(updateTransferUser);
        userMapper.updateUserByIdForTransfer(updateTransferUser);
        if (oldType == 0) {
            newDistributor.setStationMaster(oldDist.getStationMaster());
        }
        int newRes = distributorMapper.insert(newDistributor);
        if (newRes < 1) {
            throw new YimaoException("添加经销商失败。");
        }

        User newUser = createOrBindingUserWhileCreateDistributor(newDistributor);
        //将用户id回写该经销商代理商
        Distributor updateUserIdDistributor = new Distributor();
        updateUserIdDistributor.setId(newDistributor.getId());
        updateUserIdDistributor.setUserId(newUser.getId());
        updateUserIdDistributor.setOldId(newDistributor.getId().toString());
        distributorMapper.updateByPrimaryKeySelective(updateUserIdDistributor);

        //更新转让用户
        if (oldType == 0) {//全部转让
            //注销转让原账号
            oldDistributor.setStationMaster(false);
            oldDistributor.setForbidden(true);
            oldDistributor.setForbiddenOrder(true);
            oldDistributor.setDeleted(true);
            distributorMapper.updateByPrimaryKey(oldDistributor);

            //如果是站长需要更新服务站的站长内容
            if (oldDist.getStationMaster()) {
                StationDTO originStation = systemFeign.getStationByDistributorId(oldDistributor.getId());
                if (Objects.nonNull(originStation)) {
                    //将转让的经销商id替换为新创建的经销商id
                    StationDTO station = new StationDTO();
                    station.setId(originStation.getId());
                    station.setMasterDistributorId(newDistributor.getId());
                    station.setMasterIdCard(newDistributor.getIdCard());
                    station.setMasterName(newDistributor.getRealName());
                    station.setMasterPhone(newDistributor.getPhone());
                    systemFeign.updateStationMasterInfo(station);
                }
            }
            
            //TODO 判断经销商app该用户是否登录
            //String appToken =AuthConstants.JWT_TOKEN_PREFIX + "_" + SystemType.JXSAPP.value + "_" + oldDistributor.getUserId();
            
            
        } else {
        	//若转让用户是代理并且不是区代，将areaID设置为null
        	if( (oldDistributor.getType() == DistributorType.PROXY.value && Objects.isNull(oldDistributor.getAgentLevel()))
        			||
        		(oldDistributor.getType() == DistributorType.PROXY.value  && ! AgentLevel.isRegionAgent(oldDistributor.getAgentLevel()))) {

        		oldDistributor.setAreaId(null);
        	}

            //更新部分
            distributorMapper.updateByPrimaryKey(oldDistributor);
        }

        //新增转让纪录
        saveUserTransferRecord(oldDist, newDistributor, newType, newAgentLevel);

        if (transferRegionAgent) {//转让区代
            List<Integer> dealerUserType = new ArrayList<Integer>();
            dealerUserType.add(UserType.DISTRIBUTOR_50.value);
            dealerUserType.add(UserType.DISTRIBUTOR_350.value);
            dealerUserType.add(UserType.DISTRIBUTOR_650.value);
            dealerUserType.add(UserType.DISTRIBUTOR_950.value);
            dealerUserType.add(UserType.DISTRIBUTOR_1000.value);
            dealerUserType.add(UserType.DISTRIBUTOR_DISCOUNT_50.value);
            //更新经销商推荐人关联id
            Example dis = new Example(Distributor.class);
            Example.Criteria disCriteria = dis.createCriteria();
            disCriteria.andEqualTo("recommendId", oldDistributor.getId());
            Distributor updateDistributor = new Distributor();
            updateDistributor.setRecommendId(newDistributor.getId());
            updateDistributor.setRecommendName(newDistributor.getRealName());
            distributorMapper.updateByExampleSelective(updateDistributor, dis);

            //更新用户表中的上级经销商（用户类型为经销商）
            Example u = new Example(User.class);
            Example.Criteria uCriteria = u.createCriteria();
            uCriteria.andEqualTo("distributorId", oldDistributor.getId());
            uCriteria.andIn("userType", dealerUserType);
            User user = new User();
            user.setDistributorId(newDistributor.getId());
            userMapper.updateByExampleSelective(user, u);
        }
        if (isDealerTransfer) {//更新经销商相关数据
            List<Integer> userTypeList = new ArrayList<>();
            userTypeList.add(UserType.USER_3.value);
            userTypeList.add(UserType.USER_4.value);
            userTypeList.add(UserType.USER_7.value);
            Example u = new Example(User.class);
            Example.Criteria uCriteria = u.createCriteria();
            uCriteria.andEqualTo("distributorId", oldDistributor.getId());
            uCriteria.andIn("userType", userTypeList);
            User user = new User();
            user.setDistributorId(newDistributor.getId());
            userMapper.updateByExampleSelective(user, u);

            if (newDistributor.getRoleLevel() == DistributorRoleLevel.D_950.value) {
                //更新企业子账号关联的主账号
                Example comp = new Example(Distributor.class);
                Example.Criteria compCriteria = comp.createCriteria();
                compCriteria.andEqualTo("pid", oldDist.getId());
                Distributor compDistributor = new Distributor();
                compDistributor.setPid(newDistributor.getId());
                distributorMapper.updateByExampleSelective(compDistributor, comp);
            }
            
            if (newDistributor.getRoleLevel() == DistributorRoleLevel.DISCOUNT.value) {
            	//更新折机经销商绑定的经销商商品
            	productFeign.changeProductDistributor(oldDistributor.getId(), newDistributor.getId());
            	 
            }
            
            //还需更新水机用户绑定的经销商id
            waterDeviceUserService.updateDeviceUserDistributorId(oldDistributor.getId(), newDistributor);

            //经销商转让修改设备上的经销商信息
            Map<String, Object> map = new HashMap<>();
            map.put("oldDistributorId", oldDistributor.getId());
            map.put("newDistributorId", newDistributor.getId());
            map.put("newDistributorName", newDistributor.getRealName());
            map.put("newDistributorPhone", newDistributor.getPhone());
            rabbitTemplate.convertAndSend(RabbitConstant.CHANGE_DEVICE_DISTRIBUTOR, map);
        }

        //发送注册成功推送信息
        this.sendMessage(newType, newDistributor.getUserName(), password, newDistributor.getPhone(),newDistributor.getAreaId());
    }

    /**
     * 保存用户转让纪录
     *
     * @param oldDistributor
     * @param newDistributor
     * @param newAgentLevel
     * @param newtype
     */
    public void saveUserTransferRecord(Distributor oldDistributor, Distributor newDistributor, Integer newtype, Integer newAgentLevel) {
        String remark = "";
        UserChangeRecord record = new UserChangeRecord();
        record.setType(UserChangeRecordEnum.TRANSFER_EVENT.value);
        record.setOrigDistributorId(oldDistributor.getId());
        record.setOrigDistributorType(oldDistributor.getType());
        record.setOrigPhone(oldDistributor.getPhone());
        record.setOrigAccount(oldDistributor.getUserName());
        record.setDestDistributorId(newDistributor.getId());
        record.setDestDistributorType(newDistributor.getType());
        record.setDestPhone(newDistributor.getPhone());
        record.setDestAccount(newDistributor.getUserName());
        record.setTime(new Date());
        record.setCreator(userCache.getCurrentAdminRealName());
        record.setTerminal(CreateDistributorTerminal.SYSTEM.value);
        if (newtype != 0) {
            remark = "转让类型:" + DistributorType.find(newtype).name;
        }

        if (newAgentLevel != 0) {
            remark += "转让代理级别:" + AgentLevel.find(newAgentLevel).name;
        }
        record.setRemark(remark);
        userChangeMapper.insertSelective(record);

    }

    /**
     * 修改转让人信息，创建受理人信息（无论哪种转让情形，受理人信息都是新建出来的）
     *
     * @param transferAgent       转让代理商
     * @param transferDistributor 转让经销商
     * @param destAgentLevel      转让的代理等级
     * @param oldDist             转让人信息
     * @param transferDTO         受理人信息
     */
    private void transferAgent(boolean transferAgent, boolean transferDistributor, Integer destAgentLevel, Distributor oldDist, TransferDistributorDTO transferDTO) {
        int oldType = oldDist.getType();
        Integer oldAgentLevel = oldDist.getAgentLevel();
        //转让后的type值
        int type = oldType;
        int newType = 0;
        //转让全部代理商身份，把type值中的代理商值去掉
        if (transferAgent) {
            if (Objects.equals(destAgentLevel, oldAgentLevel)) {
                type = type ^ DistributorType.PROXY.value;
            }
            newType = newType | DistributorType.PROXY.value;
        }
        //转让经销商，把type值中的经销商值去掉
        if (transferDistributor) {
            type = type ^ DistributorType.DEALER.value;
            newType = newType | DistributorType.DEALER.value;
        }
        Date now = new Date();
        int agentLevel = oldAgentLevel ^ destAgentLevel;
        //第一步：设置转让人需要更新的信息
        Distributor updateDist = new Distributor();
        //设置转让人转让部分代理商身份后的所剩代理级别
        updateDist.setAgentLevel(agentLevel);
        //设置经销代理身份
        updateDist.setType(type);
        //只有代理商身份，并且全部转让
        boolean transferAllAgent = oldType == DistributorType.PROXY.value && transferAgent && Objects.equals(destAgentLevel, oldDist.getAgentLevel());
        //有代理商和经销商身份，并且全部转让
        boolean transferAllAgentAndDistributor = oldType == DistributorType.BOTH.value && transferAgent && Objects.equals(destAgentLevel, oldDist.getAgentLevel()) && transferDistributor;
        //只有经销商身份，并且转让
        boolean transferAllDistributor = oldType == DistributorType.DEALER.value && transferDistributor;
        //全部转让的情况，把转让人转让人账号设置为禁用
        if (transferAllAgent || transferAllAgentAndDistributor || transferAllDistributor) {
            //转让所有代理商身份，则设置转让人账号不可用
            updateDist.setForbidden(true);
            updateDist.setForbiddenOrder(true);
        }
        if (transferDistributor) {
            updateDist.setRoleId(0);
            updateDist.setRoleLevel(0);
            updateDist.setRoleName("代理商");
        }
        updateDist.setUpdater(userCache.getCurrentAdminRealName());
        updateDist.setUpdateTime(now);
        updateDist.setId(oldDist.getId());
        distributorMapper.updateByPrimaryKeySelective(updateDist);

        //TODO 区代转让：该区代发展的经销商，归属在转让后的区代下；

        //第二步：创建受理人信息
        Distributor newDist = oldDist;
        newDist.setId(null);
        newDist.setAgentLevel(destAgentLevel);
        newDist.setType(newType);
        if (!transferDistributor) {
            newDist.setRoleId(0);
            newDist.setRoleLevel(0);
            newDist.setRoleName("代理商");
        }
        //创建受理人账号信息
        this.setAccount(newDist, transferDTO.getCreateAccountType());
        newDist.setUserName(newDist.getUserName().trim());
        newDist.setRealName(transferDTO.getRealName().trim());
        newDist.setSex(transferDTO.getSex());
        newDist.setPhone(transferDTO.getPhone().trim());
        newDist.setIdCard(transferDTO.getIdCard().trim());
        newDist.setAttachment(transferDTO.getAttachment());
        newDist.setRemark(transferDTO.getRemark());
        newDist.setCreator(userCache.getCurrentAdminRealName());
        newDist.setCreateTime(now);
        newDist.setUpdater(null);
        newDist.setUpdateTime(null);
        newDist.setForbidden(false);
        newDist.setForbiddenOrder(false);
        distributorMapper.insert(oldDist);

        //TODO 经销商转让：该经销商的客户，归属在转让后的经销商下。该经销商以往的水机续费，也归属在转让后的经销商下。

        //TODO 保存身份变更记录日志
    }

    /**
     * @author lizhiqiang
     * @date 2019/1/2 14:53
     */
    @Override
    public void updateDistributorQuota(Integer distributorId, Integer number, Long orderId) {
        Distributor distributor = distributorMapper.selectByPrimaryKey(distributorId);
        if (distributor == null) {
            throw new YimaoException("未找到相应经销商信息");
        }

        //财务，企业审核状态,合同签署状态在各自service层都判断过了
        int remainingQuota = distributor.getRemainingQuota() + number;
        int quota = distributor.getQuota() + number;
        distributor.setRemainingQuota(remainingQuota);
        distributor.setQuota(quota);
        distributorMapper.updateByPrimaryKey(distributor);
    }

    /**
     * 根据经销商账号统计经销商信息
     *
     * @param id 经销商ID
     * @return com.yimao.cloud.pojo.dto.user.DistributorCountDTO
     * @author hhf
     * @date 2019/1/21
     */
    @Override
    public DistributorCountDTO countDistributorById(Integer id) {
        List<Map<String, Object>> countMap;
        Integer total;
        Integer yetNumber;
        Integer weekNumber;
        Integer experienceNumber;
        try {
            // 各种类型经销商数量
            Callable<List<Map<String, Object>>> callableDistributor = () -> distributorMapper.countDistributor4ConfigName(id);
            FutureTask<List<Map<String, Object>>> distributorFutureTask = new FutureTask<>(callableDistributor);
            ThreadUtil.executor.submit(distributorFutureTask);

            // 经销商总数量
            Callable<Integer> totalCallable = () -> distributorMapper.countDistributor4total(id, null);
            FutureTask<Integer> totalFutureTask = new FutureTask<>(totalCallable);
            ThreadUtil.executor.submit(totalFutureTask);

            // 体验版总数量
            Callable<Integer> experienceCallable = () -> distributorMapper.countDistributor4total(id, D_50.value);
            FutureTask<Integer> experienceFutureTask = new FutureTask<>(experienceCallable);
            ThreadUtil.executor.submit(experienceFutureTask);

            // 经销商升级统计 ture -> 昨日  else -> 本周
            // 昨日升级
            Callable<Integer> yetCallable = () -> distributorMapper.countDistributor4Update(id, true);
            FutureTask<Integer> yetFuture = new FutureTask<>(yetCallable);
            ThreadUtil.executor.submit(yetFuture);
            // 本周升级
            Callable<Integer> weekCallable = () -> distributorMapper.countDistributor4Update(id, false);
            FutureTask<Integer> weekFuture = new FutureTask<>(weekCallable);
            ThreadUtil.executor.submit(weekFuture);

            countMap = distributorFutureTask.get();
            total = totalFutureTask.get();
            yetNumber = yetFuture.get();
            weekNumber = weekFuture.get();
            experienceNumber = experienceFutureTask.get();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new NotFoundException("查询出错，请稍后重试。");
        }
        DistributorCountDTO dto = new DistributorCountDTO();
        if (countMap != null) {
            Map<Object, Object> map = new HashMap<>();
            for (Map<String, Object> objectMap : countMap) {
                if (objectMap.size() == 2) {
                    Object[] values = objectMap.values().toArray();
                    map.put(values[0].toString(), values[1]);
                }
            }
            dto.setMap(map);
        }
        if (total != null)
            dto.setTotalNumber(total);
        if (yetNumber != null)
            dto.setYetNumber(yetNumber);
        if (weekNumber != null)
            dto.setWeekNumber(weekNumber);
        if (experienceNumber != null)
            dto.setExperienceNumber(experienceNumber);
        return dto;
    }

    /**
     * 根据省市区获取发起人信息
     *
     * @param province 省
     * @param city     市
     * @param region   区
     * @return com.yimao.cloud.pojo.dto.user.DistributorDTO
     * @author hhf
     * @date 2019/1/24
     */
    @Override
    public DistributorDTO getOriginatorByAddress(String province, String city, String region) {
        DistributorDTO dto = null;
        int originator = 0;
        // 获取省市区下的 经销商发起人
        Example example = new Example(Distributor.class);
        Example.Criteria criteria = example.createCriteria();
        if (null != province) {
            criteria.andEqualTo("province", province);
            originator = OriginatorEnum.PROVINCE_ORIGINATOR.value;
        }
        if (null != city) {
            criteria.andEqualTo("city", city);
            originator = originator | OriginatorEnum.CITY_ORIGINATOR.value;
        }
        if (null != region) {
            criteria.andEqualTo("region", region);
            originator = originator | OriginatorEnum.REGION_ORIGINATOR.value;
        }
        criteria.andEqualTo("sponsorLevel", originator);
        criteria.andEqualTo("deleted", 0);
        criteria.andEqualTo("forbidden", 0);
        List<Distributor> distributors = distributorMapper.selectByExample(example);

        if (CollectionUtil.isNotEmpty(distributors)) {
            dto = new DistributorDTO();
            Distributor distributor = distributors.get(0);
            distributor.convert(dto);
        }
        return dto;
    }

    /**
     * 根据经销商ID获取推荐人信息
     *
     * @param distributorId 经销商Id
     * @return com.yimao.cloud.user.po.Distributor
     * @author hhf
     * @date 2019/1/24
     */
    @Override
    public DistributorDTO getRecommendByDistributorId(Integer distributorId) {
        DistributorDTO dto = null;
        Distributor distributor = distributorMapper.getRecommendByDistributorId(distributorId);
        if (distributor != null) {
            dto = new DistributorDTO();
            distributor.convert(dto);
        }
        return dto;
    }

    /**
     * 根据经销商ID获取经销商主账号信息
     *
     * @param distributorId 经销商Id
     * @return com.yimao.cloud.user.po.Distributor
     * @author hhf
     * @date 2019/1/24
     */
    @Override
    public DistributorDTO getMainAccountByDistributorId(Integer distributorId) {
        DistributorDTO dto = null;
        Distributor distributor = distributorMapper.getMainAccountByDistributorId(distributorId);
        if (distributor != null) {
            dto = new DistributorDTO();
            distributor.convert(dto);
        }
        return dto;
    }

    @Override
    public Boolean existDistributorByIdCard(String idCard) {
        if (idCard == null || "".equals(idCard)) {
            throw new BadRequestException("身份证信息不能为空。");
        }
        // 校验身份证的合法性
        boolean validateCard = IdcardUtils.validateCard(idCard);
        if (!validateCard) {
            throw new YimaoException("身份证信息格式有误");
        }
        Example example = new Example(Distributor.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("idCard", idCard);
        List<Distributor> distributors = distributorMapper.selectByExample(example);
        return !CollectionUtil.isEmpty(distributors);
    }

    @Override
    public List<Integer> getDistributorIdByParam(Integer userId, Integer distributorType, String distributorAccount, String distributorName, String province, String city, String region, String recommendName, String recommendAccount) {
        return distributorMapper.getDistributorIdByParam(userId, distributorType, distributorAccount, distributorName, province, city, region, recommendName, recommendAccount);
    }

    @Override
    public List<DistributorDTO> getDistributorByDistributorIds(List<Integer> distributorIds) {
        List<DistributorDTO> list = new ArrayList<>();
        Example example = new Example(Distributor.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andIn("id", distributorIds);
        List<Distributor> distributors = distributorMapper.selectByExample(example);
        for (Distributor distributor : distributors) {
            DistributorDTO dto = new DistributorDTO();
            distributor.convert(dto);
            list.add(dto);
        }
        return list;
    }

    /**
     * 验证代理排名的值是否存在
     *
     * @param agentLevel 代理类型1-省代；2-市代；4-区代
     * @param ranking    排名
     * @return java.lang.Boolean
     * @author hhf
     * @date 2019/4/3
     */
    @Override
    public Boolean checkAgentRanking(Integer agentLevel, Integer ranking) {
        Example example = new Example(Distributor.class);
        Example.Criteria criteria = example.createCriteria();
        if (AgentLevel.AGENT_P.value == agentLevel) {
            criteria.andEqualTo("provinceRanking", ranking);
        } else if (AgentLevel.AGENT_C.value == agentLevel) {
            criteria.andEqualTo("cityRanking", ranking);
        } else {
            criteria.andEqualTo("regionRanking", ranking);
        }
        List<Distributor> distributors = distributorMapper.selectByExample(example);
        return !CollectionUtil.isEmpty(distributors);

    }

    /**
     * 根据省市区获取推荐人信息
     *
     * @param province 省
     * @param city     市
     * @param region   区
     * @author hhf
     * @date 2019/4/3
     */
    @Override
    public List<DistributorDTO> getRecommendByAddress(String province, String city, String region) {
        return distributorMapper.selectRecommendByAddress(province, city, region);
    }

    @Override
    public DistributorDTO getExpansionInfoByUserId(Integer userId) {
        UserDTO user = userService.getBasicUserById(userId);
        if (user != null) {
            //Integer mid = user.getMid();
            Integer id = user.getDistributorId();
            if (id != null) {
                return this.getFullDistributorDTOById(id);
            }
        }
        return null;
    }

    @Override
    public List<DistributorExportDTO> distributorExport(DistributorQueryDTO query) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        judgeDistributorType(query);
        List<DistributorExportDTO> list = distributorMapper.distributorExport(query);
        //查询所有升级纪录
        Example example = new Example(UserChangeRecord.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("type", 6);
        List<UserChangeRecord> userChangeList = userChangeMapper.selectByExample(example);
        if (Objects.isNull(userChangeList) || userChangeList.size() < 1) {
            return list;
        }
        Map<Integer, List<UserChangeRecord>> userChangeMap = new HashMap<Integer, List<UserChangeRecord>>();
        for (UserChangeRecord userChangeRecord : userChangeList) {
            if (userChangeRecord.getOrigDistributorId() != null) {
                if (userChangeMap.containsKey(userChangeRecord.getOrigDistributorId())) {
                    userChangeMap.get(userChangeRecord.getOrigDistributorId()).add(userChangeRecord);
                } else {
                    List<UserChangeRecord> changeRecordList = new ArrayList<UserChangeRecord>();
                    changeRecordList.add(userChangeRecord);
                    userChangeMap.put(userChangeRecord.getOrigDistributorId(), changeRecordList);
                }
            }

        }

        for (DistributorExportDTO export : list) {
            //获取经销商id
            Integer id = export.getId();

            if (userChangeMap.containsKey(id)) {
                //获取升级纪录
                List<UserChangeRecord> userChangeRecordList = userChangeMap.get(id);
                for (UserChangeRecord userChangeRecord : userChangeRecordList) {
                    if (Objects.nonNull(userChangeRecord.getDestDistributorType())) {
                        if (userChangeRecord.getDestDistributorType() == DistributorRoleLevel.D_50.value) {//体验版经销商
                            export.setFirstUpdateTime(userChangeRecord.getTime() == null ? null : sdf.format(userChangeRecord.getTime()));

                        } else if (userChangeRecord.getDestDistributorType() == DistributorRoleLevel.D_350.value) {//微创
                            export.setPayTimeforMin(userChangeRecord.getTime() == null ? null : sdf.format(userChangeRecord.getTime()));
                            export.setPayAmountforMin(userChangeRecord.getAmount());
                        } else if (userChangeRecord.getDestDistributorType() == DistributorRoleLevel.D_650.value) {//个人版经销商
                            export.setPayTimeforPerson(userChangeRecord.getTime() == null ? null : sdf.format(userChangeRecord.getTime()));
                            export.setPayAmountforPerson(userChangeRecord.getAmount());
                        } else if (userChangeRecord.getDestDistributorType() == DistributorRoleLevel.D_950.value) {//企业版经销商
                            export.setPayTimeforEnterprise(userChangeRecord.getTime() == null ? null : sdf.format(userChangeRecord.getTime()));
                            export.setPayAmountforEnterprise(userChangeRecord.getAmount());
                        }
                    }

                }

            }


        }


        // if (CollectionUtil.isNotEmpty(list)) {
        //     for (DistributorExportDTO dto : list) {
        //         Integer roleLevel = dto.getRoleLevel();
        //         if (roleLevel != null && DistributorRoleLevel.D_950.value == roleLevel) {
        //             // 子账号数量
        //             Example example = new Example(Distributor.class);
        //             Example.Criteria criteria = example.createCriteria();
        //             criteria.andEqualTo("pid", dto.getId());
        //             criteria.andEqualTo("roleLevel", DistributorRoleLevel.D_1000.value);
        //             dto.setCount(distributorMapper.selectCountByExample(example));
        //         }
        //     }
        // }
        return list;
    }

    /**
     * 更新经销商信息
     *
     * @param dto 经销商信息
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
    public void updateDistributor(DistributorDTO dto) {
        Distributor distributor = distributorMapper.selectByPrimaryKey(dto.getId());
        if (distributor == null) {
            throw new BadRequestException(dto.getId() + "该经销商不存在！");
        }
        distributor = new Distributor(dto);
        int result = distributorMapper.updateByPrimaryKeySelective(distributor);
        if (result < 1) {
            throw new YimaoException("经销商更新失败！");
        }
        // todo 生成经销商订单

    }

    /**
     * 获取所有的子账号经销商
     *
     * @param mid
     * @return java.util.List<com.yimao.cloud.pojo.dto.user.DistributorDTO>
     * @author hhf
     * @date 2019/5/30
     */
    @Override
    public List<DistributorDTO> getSonDistributorByMid(Integer mid) {
        return distributorMapper.selectSonDistributorById(mid);
    }

    /**
     * 模糊查询经销商信息 （给服务站使用）
     *
     * @author hhf
     * @date 2019/6/13
     */
    @Override
    public List<DistributorDTO> getDistributorByParams(String param, List<String> provinces, List<String> citys, List<String> regions) {
        return distributorMapper.getDistributorByParams(param, provinces, citys, regions);
    }

    @Override
    public DistributorDTO getDistributorByUserName(String userName) {
        return distributorMapper.selectDistributorBasicInfoByUserName(userName);
    }


    @Override
    public Object getSonAccountInfo(Integer id) {
        Distributor distributor = distributorMapper.selectByPrimaryKey(id);
        if (null == distributor) {
            throw new NotFoundException("该经销商账号不存在");
        }

        if (DistributorRoleLevel.D_950.value != distributor.getRoleLevel()) {
            throw new NotFoundException("该账号不是企业版主账号");
        }

        Map<String, Object> map = new HashMap<>(8);
        Integer accountNum = distributorMapper.getSonAccountNum(id);
        //子账号列表
        List<DistributorDTO> subAccountList = distributorMapper.querySubAccountList(id);
        String url = domainProperties.getWechat() + "/?#/developmentSubSignUp/" + distributor.getUserId();
        map.put("count", accountNum);
        map.put("distributorList", subAccountList);
        map.put("url", url);
        return map;
    }

    @Override
    public List<DistributorDTO> queryDistributorByMobile(String mobile) {
        Example example = new Example(Distributor.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("phone", mobile);
        List<Distributor> distributorList = distributorMapper.selectByExample(example);
        if (CollectionUtil.isNotEmpty(distributorList)) {
            List<DistributorDTO> distributorDTOS = CopyUtil.copyList(distributorList, Distributor.class, DistributorDTO.class);
            return distributorDTOS;
        }
        return null;
    }

    @Override
    public Map<String, Object> getDistributorAccountInfoForApp(Integer distributorId) {
        Map<String, Object> map = new HashMap<>();
        Distributor distributor = distributorMapper.selectByPrimaryKey(distributorId);
        if (null == distributor) {
            throw new BadRequestException("用户经销商不存在或未绑定！");
        }
        UserDTO user = userService.getBasicUserById(userCache.getUserId());
        if (null == user) {
            throw new BadRequestException("用户未登录！");
        }

        //检查经销商权限
        boolean flag = checkPermission(user.getUserType());
        if (flag) {
            map.put("distributorPermission", true);
        } else {
            map.put("distributorPermission", false);
        }
        //查询我的合同列表
        List<DistributorProtocol> protocolList = distributorProtocolService.getDistributorProtocolListByDistributorAccount(distributor.getUserName());
        List<DistributorOrderDTO> allUnfinishedOrderList = new ArrayList<>();
        //查询是否有未完成记录(注册记录)
        List<DistributorOrderDTO> unfinishedOrderList = distributorOrderService.unfinishedOrderListByCreator(distributor.getId());

        //查询是否有未完成记录(升级续费)
        List<DistributorOrderDTO> otherUnfinishedOrderList = distributorOrderService.listByDistributorAccount(distributor.getUserName(), null);
        if (unfinishedOrderList != null) {
            allUnfinishedOrderList.addAll(unfinishedOrderList);
        }
        if (otherUnfinishedOrderList != null) {
            allUnfinishedOrderList.addAll(otherUnfinishedOrderList);
        }
        //订单所对应的合同用户是否签署
        for (DistributorOrderDTO distributorOrderDTO : allUnfinishedOrderList) {
            Example example = new Example(DistributorProtocol.class);
            Example.Criteria criteria = example.createCriteria();
            criteria.andEqualTo("orderId", distributorOrderDTO.getId());
            DistributorProtocol distributorProtocol = distributorProtocolMapper.selectOneByExample(example);
            if (distributorProtocol != null && distributorProtocol.getUserSignState() != null) {
                distributorOrderDTO.setUserSignState(distributorProtocol.getUserSignState());
            } else {
                //未签署合同
                distributorOrderDTO.setUserSignState(DistributorProtocolSignStateEnum.NOT_SIGN.value);
                //如果订单无需签署合同则直接返回合同签署状态为已签署
                if (distributorOrderDTO.getOrderType() == DistributorOrderType.UPGRADE.value) {
                    if (distributorOrderDTO.getRoleLevel() == DistributorRoleLevel.D_350.value &&
                            distributorOrderDTO.getDestRoleLevel() == DistributorRoleLevel.D_650.value) {
                        //微创版经销商升级个人经销商如果在30天升级有效期内升级，则不需要签署合同
                        DistributorRoleDTO origDistributorRole = distributorRoleService.getByLevel(distributorOrderDTO.getRoleLevel());
                        if (origDistributorRole == null) {
                            throw new YimaoException("经销商类型不存在！");
                        }
                        //判断是否在有效期内
                        flag = distributorOrderService.upgradeValidityTime(origDistributorRole.getUpgradeLimitDays(), distributor);
                        if (flag) {
                            //如果在升级剩余有效期内则不需要签署合同
                            distributorOrderDTO.setUserSignState(DistributorProtocolSignStateEnum.SIGN.value);
                        }
                    } else if ((distributorOrderDTO.getRoleLevel() == DistributorRoleLevel.D_350.value || distributorOrderDTO.getRoleLevel() == DistributorRoleLevel.D_650.value) &&
                            distributorOrderDTO.getDestRoleLevel() == DistributorRoleLevel.D_950.value) {
                        //微创和个人升企业只需补差价无需签署合同
                        distributorOrderDTO.setUserSignState(DistributorProtocolSignStateEnum.SIGN.value);
                    }
                }
            }
            //如果有未支付的订单，则让订单失效
            if (distributorOrderDTO.getPayState() == PayStateEnum.UN_PAY.value) {
                distributorOrderDTO.setOrderState(DistributorOrderStateEnum.CLOSE.value);
                DistributorOrder update = new DistributorOrder();
                update.setId(distributorOrderDTO.getId());
                update.setOrderState(DistributorOrderStateEnum.CLOSE.value);//使订单失效
                distributorOrderMapper.updateByPrimaryKeySelective(update);
            }
        }
        map.put("distributor", distributor);
        map.put("unfinishedOrderList", allUnfinishedOrderList);
        map.put("protocolList", protocolList);
        return map;
    }

    public boolean checkPermission(Integer userType) {
        if (UserType.DISTRIBUTOR_350.value == userType) {
            return true;
        } else if (UserType.DISTRIBUTOR_650.value == userType) {
            return true;
        } else if (UserType.DISTRIBUTOR_950.value == userType) {
            return true;
        } else if (UserType.DISTRIBUTOR_1000.value == userType) {
            return true;
        }
        return false;
    }

    @Override
    public PageVO<DistributorDTO> getMyDistributors(Integer pageNum, Integer pageSize, Integer distributorId, Integer distributorType, String province, String city, String region) {

        Map map = new HashMap(8);
        map.put("province", province);
        map.put("city", city);
        map.put("region", region);
        map.put("type", DistributorType.DEALER.value);
        map.put("roleLevel", distributorType);
        map.put("distributorId", distributorId);
        PageHelper.startPage(pageNum, pageSize);
        Page<DistributorDTO> distributorList = distributorMapper.getMyDistributors(map);
        if (CollectionUtil.isNotEmpty(distributorList.getResult())) {
            for (DistributorDTO dto : distributorList.getResult()) {
                StationDTO station = systemFeign.getStationByDistributorId(dto.getId());
                if (null != station) {
                    dto.setStation(station.getName());
                }

                if (null != dto.getCompanyId()) {
                    UserCompany company = userCompanyMapper.selectByPrimaryKey(dto.getCompanyId());
                    UserCompanyDTO companyDTO = new UserCompanyDTO();
                    company.convert(companyDTO);
                    dto.setUserCompany(companyDTO);
                }
            }
            return new PageVO<>(pageNum, distributorList);

        }
        return null;
    }

    @Override
    public Object developSonAccount(String realName, Integer sex, String idCard, String email, Integer userId) {
        Map<String, Object> map = new HashMap<>(8);

        DistributorDTO distributor = distributorMapper.getDistributorByUserId(userId);
        if (null == distributor) {
            throw new NotFoundException("该经销商账号不存在");
        }

        if (DistributorRoleLevel.D_950.value != distributor.getRoleLevel()) {
            throw new NotFoundException("该账号不是企业版主账号");
        }

        DistributorRole distributorRole = distributorRoleMapper.selectByPrimaryKey(distributor.getRoleId());
        if (null == distributorRole) {
            log.info("===============该账号经销商角色不存在===============");
            throw new NotFoundException("该账号经销商角色不存在");
        }

        Integer count = distributorMapper.getSonAccountNum(userId);
        if (count.compareTo(distributorRole.getSubAccountAmount()) >= 0) {
            log.info("===============注册失败,您所属企业的子账号注册次数已用完===============");
            map.put("msg", "您所属企业的子账号注册次数已用完");
            map.put("code", 100000);
        } else {
            map.put("msg", "成功");
            map.put("companyName", distributor.getCompanyName());
            map.put("code", 100010);
        }
        return map;
    }

    /**
     * 校验保存用户信息参数(非空及正确性)
     *
     * @param distributorDTO
     */
    private void checkSaveUserInfo(DistributorDTO distributorDTO) {
        //基础信息校验
        checkSaveBasticInfo(distributorDTO);
        //根据用户类型进行校验
        if (distributorDTO.getFounder() == null || !distributorDTO.getFounder()) {//非创始人
            if (Objects.equals(null, distributorDTO.getType())) {
                throw new BadRequestException("用户类型不能为空");
            }
        } else {
            //仅为创始人
            if (Objects.equals(null, distributorDTO.getType())) {
                //默认用户类型为代理人
                distributorDTO.setType(1);
                return;
            }
        }
        if (DistributorType.PROXY.value == distributorDTO.getType()) {
            //代理人校验
            checkSaveProxyInfo(distributorDTO);
        } else if (DistributorType.DEALER.value == distributorDTO.getType()) {
            //经销商校验
            checkSaveDealerInfo(distributorDTO);
        } else if (DistributorType.BOTH.value == distributorDTO.getType()) {
            //经销商+代理人校验
            checkSaveDealerInfo(distributorDTO);
            checkSaveProxyInfo(distributorDTO);
            //不是区代无法设置自己为推荐人
            if (!AgentLevel.isRegionAgent(distributorDTO.getAgentLevel()) && ChooseRecommendType.SELF.value == distributorDTO.getChooseRecommendType()) {
                throw new BadRequestException("无法设置自己为推荐人。");
            }
        }
    }

    /**
     * 经销商注册信息校验
     *
     * @param distributorDTO
     */
    public void checkSaveDealerInfo(DistributorDTO distributorDTO) {

        if (Objects.equals(null, distributorDTO.getFuhuishun())) {
            throw new BadRequestException("是否为福慧顺不能为空。");
        }
        if (StringUtil.isBlank(distributorDTO.getProvince())) {
            throw new BadRequestException("所属省不能为空。");
        }
        if (StringUtil.isBlank(distributorDTO.getCity())) {
            throw new BadRequestException("所属市不能为空。");
        }
        if (StringUtil.isBlank(distributorDTO.getRegion())) {
            throw new BadRequestException("所属区不能为空。");
        }

        if (Objects.equals(null, distributorDTO.getRoleLevel()) || DistributorRoleLevel.find(distributorDTO.getRoleLevel()) == null) {
            throw new BadRequestException("经销商类型不能为空");
        }
        //判断配额和置换金额是否同时选择
        if (Objects.nonNull(distributorDTO.getQuota()) && Objects.nonNull(distributorDTO.getReplacementAmount())) {
            throw new BadRequestException("折机经销商置换金额与配额不能同时选择");
        }

        //是否有推荐人
        if (Objects.equals(null, distributorDTO.getHasRecommend())) {
            throw new BadRequestException("请选择是否有推荐人。");
        }

        if (distributorDTO.getHasRecommend()) {
            Integer chooseRecommendType = distributorDTO.getChooseRecommendType();
            // 有推荐人才有设置推荐人的方式
            if (chooseRecommendType == null || ChooseRecommendType.find(chooseRecommendType) == null) {
                throw new BadRequestException("请选择推荐人类型。");
            }
            //若设置自己为推荐人，必须是区代理商
            if (ChooseRecommendType.SELF.value == chooseRecommendType) {
                if (Objects.equals(null, distributorDTO.getAgentLevel()) || !AgentLevel.isRegionAgent(distributorDTO.getAgentLevel())) {
                    throw new BadRequestException("推荐人类型错误。");
                }
            }

        } else {
            distributorDTO.setChooseRecommendType(0);
        }

        //经销商类型
        if (Objects.equals(null, distributorDTO.getRoleLevel()) || DistributorRoleLevel.find(distributorDTO.getRoleLevel()) == null) {
            throw new BadRequestException("请选择经销商类型。");
        }

        //经销商类型为特批折机
        if (DistributorRoleLevel.DISCOUNT.value == distributorDTO.getRoleLevel()) {
            if (distributorDTO.getReplacementAmount() == null && distributorDTO.getQuota() == null) {
                throw new BadRequestException("置换金额/配额配置不能为空。");
            }
        }

        //若设置支付金额是否为0为空，默认设置支付金额是否为0为否
        if (Objects.equals(null, distributorDTO.getPayIsZero())) {
            distributorDTO.setPayIsZero(false);
        }


        //经销商类型为企业版
        if (DistributorRoleLevel.D_950.value == distributorDTO.getRoleLevel() && distributorDTO.getCompanyId() == null) {
            UserCompanyDTO compony = distributorDTO.getUserCompany();
            if (Objects.equals(null, compony)) {
                throw new BadRequestException("企业信息不能为空。");
            }

            if (Objects.equals(null, compony.getCompanyType())) {
                throw new BadRequestException("企业类型不能为空。");
            }
            if (StringUtil.isBlank(compony.getIndustry())) {
                throw new BadRequestException("所属行业不能为空。");
            }
            if (StringUtil.isBlank(compony.getCompanyName())) {
                throw new BadRequestException("企业名称不能为空。");
            }
            if (StringUtil.isBlank(compony.getCreditCode())) {
                throw new BadRequestException("信用代码不能为空。");
            }
            if (StringUtil.isBlank(compony.getTaxInformation())) {
                throw new BadRequestException("税务信息不能为空。");
            }
            if (StringUtil.isBlank(compony.getCorporateRepresentative())) {
                throw new BadRequestException("法人代表不能为空。");
            }
            if (StringUtil.isBlank(compony.getBankAccount())) {
                throw new BadRequestException("银行账号不能为空。");
            }
            if (StringUtil.isBlank(compony.getBank())) {
                throw new BadRequestException("开户银行不能为空。");
            }
            if (StringUtil.isBlank(compony.getPhone())) {
                throw new BadRequestException("联系方式不能为空。");
            }
            if (StringUtil.isBlank(compony.getEmail())) {
                throw new BadRequestException("公司邮箱不能为空。");
            }
            if (StringUtil.isBlank(compony.getAddress())) {
                throw new BadRequestException("公司地址不能为空。");
            }


        }

    }

    /**
     * 代理商注册信息校验
     *
     * @param distributorDTO
     */
    public void checkSaveProxyInfo(DistributorDTO distributorDTO) {

        if (Objects.equals(null, distributorDTO.getFuhuishun())) {
            throw new BadRequestException("是否为福慧顺不能为空。");
        }
        if (Objects.equals(null, distributorDTO.getAgentLevel())) {
            throw new BadRequestException("代理级别不能为空。");
        }
        if (StringUtil.isBlank(distributorDTO.getProvince())) {
            throw new BadRequestException("所属省不能为空。");
        }


        if (AgentLevel.isCityAgent(distributorDTO.getAgentLevel()) && StringUtil.isBlank(distributorDTO.getCity())) {//市代
            throw new BadRequestException("所属市不能为空。");
        }
        if (AgentLevel.isRegionAgent(distributorDTO.getAgentLevel())) {//区代
            if (StringUtil.isBlank(distributorDTO.getCity())) {
                throw new BadRequestException("所属市不能为空。");
            }
            if (StringUtil.isBlank(distributorDTO.getRegion())) {
                throw new BadRequestException("所属区不能为空。");
            }

        }
        if (Objects.isNull(distributorDTO.getSponsor())) {
            throw new BadRequestException("是否为发起人不能为空。");
        }

        if (distributorDTO.getSponsor() && Objects.isNull(distributorDTO.getSponsorLevel())) {
            throw new BadRequestException("发起人级别不能为空。");
        }
        if (!distributorDTO.getSponsor() && Objects.nonNull(distributorDTO.getSponsorLevel())) {
            throw new BadRequestException("是否为发起人错误。");
        }

        if (distributorDTO.getSponsorLevel() != null) {
            if (SponsorLevel.isSponsorProvince(distributorDTO.getSponsorLevel())) {
                if (Objects.equals(null, distributorDTO.getProvinceRanking())) {
                    throw new BadRequestException("省代排名不能为空。");
                }
                if (checkAgentRanking(AgentLevel.AGENT_P.value, distributorDTO.getProvinceRanking())) {
                    throw new BadRequestException("省代排名已存在。");
                }
            }

            if (SponsorLevel.isSponsorCity(distributorDTO.getSponsorLevel())) {
                if (Objects.equals(null, distributorDTO.getCityRanking())) {
                    throw new BadRequestException("市代排名不能为空。");
                }
                if (checkAgentRanking(AgentLevel.AGENT_C.value, distributorDTO.getCityRanking())) {
                    throw new BadRequestException("市代排名已存在。");
                }
            }

            if (SponsorLevel.isSponsorRegion(distributorDTO.getSponsorLevel())) {
                if (Objects.equals(null, distributorDTO.getRegionRanking())) {
                    throw new BadRequestException("区代排名不能为空。");
                }
                if (checkAgentRanking(AgentLevel.AGENT_R.value, distributorDTO.getRegionRanking())) {
                    throw new BadRequestException("区代排名已存在。");
                }
            }

        }


    }

    /**
     * 校验用户基本信息
     *
     * @param distributorDTO
     */
    public void checkSaveBasticInfo(DistributorDTO distributorDTO) {
        //校验基本信息
        if (distributorDTO.getTerminal() == null) {
            throw new BadRequestException("经销商上线端不能为空。");
        }
        if (CreateDistributorTerminal.SYSTEM.value != distributorDTO.getTerminal()) {
            throw new BadRequestException("经销商上线端错误。");
        }
        if (StringUtil.isBlank(distributorDTO.getRealName())) {
            throw new BadRequestException("姓名不能为空。");
        }
        if (distributorDTO.getSex() == null) {
            throw new BadRequestException("性别不能为空。");
        }
        if (SexType.find(distributorDTO.getSex()) == null) {
            throw new BadRequestException("性别填写错误。");
        }
        if (StringUtil.isBlank(distributorDTO.getPhone())) {
            throw new BadRequestException("手机号不能为空。");
        }
        if (StringUtil.isBlank(distributorDTO.getIdCard())) {
            throw new BadRequestException("身份证号码不能为空。");
        }

//        if (StringUtil.isBlank(distributorDTO.getProvince())) {
//            throw new BadRequestException("所属省不能为空。");
//        }
//        if (StringUtil.isBlank(distributorDTO.getCity())) {
//            throw new BadRequestException("所属市不能为空。");
//        }
//        if (StringUtil.isBlank(distributorDTO.getRegion())) {
//            throw new BadRequestException("所属区不能为空。");
//        }
//        if(Objects.equals(null,distributorDTO.getFuhuishun())) {
//        	throw new BadRequestException("是否为福慧顺不能为空。");
//        }

        if (distributorDTO.getSourceType() == null) {
            throw new BadRequestException("经销商上线端不能为空。");
        }
        if (CreateDistributorSourceType.find(distributorDTO.getSourceType()) == null) {
            throw new BadRequestException("经销商上线方式填写错误。");
        }
        if (Objects.equals(null, distributorDTO.getCreateAccountType()) || CreateDistributorAccountType.find(distributorDTO.getCreateAccountType()) == null) {
            throw new BadRequestException("请选择系统自动创建或手动创建经销商账号。");
        } else if (StringUtil.isBlank(distributorDTO.getUserName()) && distributorDTO.getCreateAccountType() == CreateDistributorAccountType.HAND.value) {
            throw new BadRequestException("用户账号不能为空");
        }

    }

    /**
     * 企业版主账号和子账号列表
     */
    @Override
    public List<DistributorDTO> getDistributorAndSonByMid(Integer mid) {
        DistributorDTO distributorDTO = distributorMapper.getDistributorById(mid);
        if (distributorDTO == null) {
            throw new NotFoundException("该账号不存在");
        }
        if (distributorDTO.getRoleLevel() != null && distributorDTO.getRoleLevel() != DistributorRoleLevel.D_950.value) {
            throw new BadRequestException("该账号不是主账号");
        }

        List<DistributorDTO> result = new ArrayList<>();
        result.add(distributorDTO);
        List<DistributorDTO> list = distributorMapper.selectSonDistributorById(mid);
        if (CollectionUtil.isNotEmpty(list)) {
            result.addAll(list);
        }

        return result;
    }


    @Override
    public List<UserAccountDTO> queryDistributors(List<Integer> distributorIds) {
        Map<String, Object> map = new HashMap<>(8);
        map.put("distributorIds", distributorIds);
        List<UserAccountDTO> accountDTOList = distributorMapper.queryDistributors(map);
        return accountDTOList;
    }


    @Override
    public CommResult checkDistributor(OwnerDistributorDTO ownerDistributorDTO) {
        if (StringUtil.isEmpty(ownerDistributorDTO.getRealName())) {
            throw new BadRequestException("姓名不能为空");
        }
        if (null == ownerDistributorDTO.getSex()) {
            throw new BadRequestException("性别不能为空");
        }
        if (StringUtil.isEmpty(ownerDistributorDTO.getIdCard())) {
            throw new BadRequestException("身份证号不能为空");
        }
        if (!IdcardUtils.validateCard(ownerDistributorDTO.getIdCard())) {
            throw new BadRequestException("身份证号格式错误");
        }

        if (EnvironmentEnum.PRO.code.equalsIgnoreCase(SpringContextHolder.getEnvironment())) {
            //校验身份证号和姓名是否一致
            YunSignResult yunSignResult = yunSignRegister.checkidentityNumber(ownerDistributorDTO.getIdCard(), ownerDistributorDTO.getRealName());
            if (!yunSignResult.isSuccess()) {
                throw new BadRequestException("身份证号和姓名不一致");
            }
        }

        /*if (StringUtil.isEmpty(ownerDistributorDTO.getEmail())) {
            throw new BadRequestException("邮箱不能为空");
        }*/

        //校验邮箱格式
        if (StringUtil.isNotEmpty(ownerDistributorDTO.getEmail())) {
            if (!EmailUtil.isEmail(ownerDistributorDTO.getEmail())) {
                throw new BadRequestException("邮箱格式不正确");
            }
        }

        if (Objects.isNull(ownerDistributorDTO.getUserId())) {
            throw new BadRequestException("区代的e家号不能为空");
        }
        //判断是否存在该用户
        UserDTO userDTO = userService.getBasicUserById(ownerDistributorDTO.getUserId());
        if (null == userDTO) {
            throw new DialogException("不存在该用户");
        }
        //判断分享用户是否为代理商
        DistributorDTO agent = distributorMapper.getAgentByUserId(ownerDistributorDTO.getUserId());
        if (null == agent) {
            throw new BadRequestException("该分享用户不是代理商");
        }
        if (!AgentLevel.isRegionAgent(agent.getAgentLevel())) {
            throw new BadRequestException("该分享用户不是区县级代理商");
        }
        //身份证验证经销商是否已经注册
        Boolean boo = this.existDistributorByIdCard(ownerDistributorDTO.getIdCard());
        if (boo) {
            throw new DialogException("当前身份已存在经销商账号");
        }
        //设置一个随机字符串到缓存中，手机号验证码接口用此key进行验证
        String key = UUIDUtil.longuuid();
        redisCache.set(Constant.H5_DISTRIBUTOR_CHECK + "_" + ownerDistributorDTO.getIdCard(), key, 3600L);
        return CommResult.ok(key);
    }


    @Override
    public CommResult distributorSendSmsCode(OwnerDistributorDTO ownerDistributorDTO, String key) {
        //关联接口生成的随机字符串，校验此key进行验证，确保接口的安全性
        String cachedKey = redisCache.get(Constant.H5_DISTRIBUTOR_CHECK + "_" + ownerDistributorDTO.getIdCard());
        if (StringUtil.isEmpty(cachedKey) || !key.equals(cachedKey)) {
            throw new BadRequestException("非法操作");
        }

        if (StringUtil.isEmpty(ownerDistributorDTO.getMobile())) {
            throw new BadRequestException("手机号为空");
        }

        String code = smsService.getCode(ownerDistributorDTO.getMobile(), Constant.COUNTRY_CODE);
        String text = "【翼猫健康e家】您的验证码是" + code + "。如非本人操作，请忽略本短信";
        String s = SmsUtil.sendSms(text, ownerDistributorDTO.getMobile());
        log.info("发送的验证码为：" + code + "，短信接口返回为：" + s);

        //设置一个随机字符串到缓存中，用此key进行验证，确保下一步接口的安全性
        String mobileKey = UUIDUtil.longuuid();
        redisCache.set(Constant.H5_DISTRIBUTOR_MOBILE_CODE + "_" + ownerDistributorDTO.getIdCard() + "_" + ownerDistributorDTO.getMobile(), mobileKey, 3600L);
        return CommResult.ok(mobileKey);
    }


    @Override
    public CommResult distributorCheckSmsCode(OwnerDistributorDTO ownerDistributorDTO, String smsCode,
                                              String key) {
        //关联接口生成的随机字符串，校验此key进行验证，确保接口的安全性
        String cachedKey = redisCache.get(Constant.H5_DISTRIBUTOR_MOBILE_CODE + "_" + ownerDistributorDTO.getIdCard() + "_" + ownerDistributorDTO.getMobile());
        if (StringUtil.isEmpty(cachedKey) || !key.equals(cachedKey)) {
            throw new BadRequestException("非法操作");
        }

        //校验手机验证码
        Boolean bool = smsService.verifyCode(ownerDistributorDTO.getMobile(), Constant.COUNTRY_CODE, smsCode);
        if (!bool) {
            throw new BadRequestException("验证码错误，请重新输入");
        }
        Map<String, Object> map = null;
        //1、注册人有多个区县级代理商
        List<DistributorDTO> distributorDtoList = distributorMapper.queryAgentByIdCard(ownerDistributorDTO.getIdCard());
        if (CollectionUtil.isNotEmpty(distributorDtoList)) {
            //取最先成为区代的代理商为该经销商的推荐人
            map = this.getRecommendAndStation(distributorDtoList.get(0));
        } else {
            //2、分享用户为该经销商的推荐人
            DistributorDTO distributor = distributorMapper.getAgentByUserId(ownerDistributorDTO.getUserId());
            if (distributor != null) {
                map = this.getRecommendAndStation(distributor);
            }
        }

        //设置key
        String smsKey = UUIDUtil.longuuid();
        redisCache.set(Constant.H5_DISTRIBUTOR_SMS_CODE + "_" + ownerDistributorDTO.getSex() + "_" + ownerDistributorDTO.getIdCard() + "_" + ownerDistributorDTO.getMobile() + smsCode, smsKey, 3600L);
        map.put("smsKey", smsKey);
        return CommResult.ok(map);
    }


    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
    @Override
    public CommResult determineDistributor(OwnerDistributorDTO ownerDistributorDTO, String key, String smsCode) {
        String cachedKey = redisCache.get(Constant.H5_DISTRIBUTOR_SMS_CODE + "_" + ownerDistributorDTO.getSex() + "_" + ownerDistributorDTO.getIdCard() + "_" + ownerDistributorDTO.getMobile() + smsCode);
        if (StringUtil.isEmpty(cachedKey) || !key.equals(cachedKey)) {
            throw new BadRequestException("非法操作");
        }

        if (StringUtil.isEmpty(ownerDistributorDTO.getProvince()) || StringUtil.isEmpty(ownerDistributorDTO.getCity()) || StringUtil.isEmpty(ownerDistributorDTO.getRegion())) {
            throw new BadRequestException("省市区为空");
        }

        DistributorRoleDTO role = distributorRoleService.getByLevel(DistributorRoleLevel.D_50.value);
        if (null == role) {
            throw new BadRequestException("经销商角色不存在或已禁用");
        }

        Distributor distributor = new Distributor();
        distributor.setRealName(ownerDistributorDTO.getRealName());
        distributor.setSex(ownerDistributorDTO.getSex());
        distributor.setIdCard(ownerDistributorDTO.getIdCard());
        distributor.setPhone(ownerDistributorDTO.getMobile());
        distributor.setEmail(ownerDistributorDTO.getEmail());
        distributor.setProvince(ownerDistributorDTO.getProvince());
        distributor.setCity(ownerDistributorDTO.getCity());
        distributor.setRegion(ownerDistributorDTO.getRegion());
        //设置区域ID，站务系统用此字段区分所属服务站
        Integer areaId = redisCache.get(Constant.AREA_CACHE + distributor.getProvince() + "_" + distributor.getCity() + "_" + distributor.getRegion(), Integer.class);
        if (areaId == null) {
            areaId = systemFeign.getRegionIdByPCR(distributor.getProvince(), distributor.getCity(), distributor.getRegion());
        }
        distributor.setAreaId(areaId);
        distributor.setFounder(false);
        distributor.setStationMaster(false);
        distributor.setSponsor(false);

        //来源端：1-H5页面
        distributor.setTerminal(CreateDistributorTerminal.H5.value);
        distributor.setSourceType(7);
        distributor.setRoleId(role.getId());
        distributor.setRoleLevel(DistributorRoleLevel.D_50.value);
        distributor.setRoleName(role.getName());
        distributor.setQuota(role.getWaterDeviceQuota());
        distributor.setRemainingQuota(role.getWaterDeviceQuota());
        distributor.setType(DistributorType.DEALER.value);
        distributor.setRecommendId(ownerDistributorDTO.getRecommendId());
        distributor.setRecommendName(ownerDistributorDTO.getRecommendName());
        distributor.setDeleted(false);
        distributor.setForbidden(false);
        distributor.setForbiddenOrder(false);
        distributor.setFuhuishun(false);
        distributor.setCompleteTime(new Date());
        String account = generateDistributorAccount();
        distributor.setUserName(account);
        String pwd = UUIDUtil.numuuid(8);
        distributor.setPassword(MD5Util.encodeMD5(pwd));
        distributor.setCreateTime(new Date());
        int count = distributorMapper.insertSelective(distributor);
        if (count < 1) {
            throw new BadRequestException("体验版经销商创建失败");
        }

        // 保存注册记录
        saveUserChangeRecord(distributor, null);

        //用户信息创建逻辑
        User user = createOrBindingUserWhileCreateDistributor(distributor);

        //将用户id回写该经销商代理商
        Distributor update = new Distributor();
        update.setId(distributor.getId());
        update.setUserId(user.getId());
        update.setOldId(distributor.getId().toString());
        distributorMapper.updateByPrimaryKeySelective(update);

        //成功发送短信
        sendMessage(DistributorType.DEALER.value, distributor.getUserName(), pwd, distributor.getPhone(),areaId);
        Map<String, Object> map = new HashMap<>(8);
        map.put("mobile", distributor.getPhone());
        map.put("userName", distributor.getUserName());
        map.put("password", pwd);
        return CommResult.ok(map);
    }

    @Override
    public CommResult checkSubaccount(SubDistributorAccountDTO subAccountDTO) {
        if (null == subAccountDTO.getUserId()) {
            throw new BadRequestException("分享用户id不能为空");
        }
        if (StringUtil.isEmpty(subAccountDTO.getRealName())) {
            throw new BadRequestException("姓名不能为空");
        }
        if (null == subAccountDTO.getSex()) {
            throw new BadRequestException("性别不能为空");
        }
        if (StringUtil.isEmpty(subAccountDTO.getIdCard())) {
            throw new BadRequestException("身份证号不能为空");
        }
        if (!IdcardUtils.validateCard(subAccountDTO.getIdCard())) {
            throw new BadRequestException("身份证号格式错误");
        }

        if (EnvironmentEnum.PRO.code.equalsIgnoreCase(SpringContextHolder.getEnvironment())) {
            //校验身份证号和姓名是否一致
            YunSignResult yunSignResult = yunSignRegister.checkidentityNumber(subAccountDTO.getIdCard(), subAccountDTO.getRealName());
            if (!yunSignResult.isSuccess()) {
                throw new BadRequestException("身份证号和姓名不一致");
            }
        }

        DistributorDTO distributor = distributorMapper.getDistributorByUserId(subAccountDTO.getUserId());
        if (null == distributor) {
            throw new BadRequestException("分享用户不是经销商");
        }
        if (DistributorRoleLevel.D_950.value != distributor.getRoleLevel()) {
            throw new BadRequestException("分享用户不是企业版经销商");
        }
        //企业版经销商账号下的子账号注册次数是否用完
        Integer count = distributorMapper.getSonAccountNum(subAccountDTO.getUserId());
        DistributorRole distributorRole = distributorRoleMapper.selectByPrimaryKey(distributor.getRoleId());
        if (null == distributorRole) {
            throw new BadRequestException("分享用户经销商上角色不存在");
        }
        if (null == distributorRole.getSubAccountAmount()) {
            throw new BadRequestException("该经销商角色未配置子账号数量");
        }
        if (count.compareTo(distributorRole.getSubAccountAmount()) >= 0) {
            throw new DialogException("分享用户所属企业的子账号注册次数已用完");
        }
        //设置key
        String key = UUIDUtil.longuuid();
        redisCache.set(Constant.H5_SUBACCOUNT_CHECK + "_" + subAccountDTO.getIdCard(), key, 3600L);
        return CommResult.ok(key);
    }


    @Override
    public CommResult subaccountSendsmscode(SubDistributorAccountDTO subAccountDTO, String key) {
        String cachedKey = redisCache.get(Constant.H5_SUBACCOUNT_CHECK + "_" + subAccountDTO.getIdCard());
        if (StringUtil.isEmpty(cachedKey) || !key.equals(cachedKey)) {
            throw new BadRequestException("非法操作");
        }
        String code = smsService.getCode(subAccountDTO.getMobile(), Constant.COUNTRY_CODE);
        String text = "【翼猫健康e家】您的验证码是" + code + "。如非本人操作，请忽略本短信";
        String s = SmsUtil.sendSms(text, subAccountDTO.getMobile());
        log.info("发送的验证码为：" + code + "，短信接口返回为：" + s);

        //设置一个随机字符串到缓存中，用此key进行验证，确保下一步接口的安全性
        String mobileKey = UUIDUtil.longuuid();
        redisCache.set(Constant.H5_SUBACCOUNT_MOBILE_CODE + "_" + subAccountDTO.getIdCard() + "_" + subAccountDTO.getMobile(), mobileKey, 3600L);
        return CommResult.ok(mobileKey);
    }


    @Override
    public CommResult subaccountCheckSmsCode(SubDistributorAccountDTO subAccountDTO, String key, String smsCode) {
        String cachedKey = redisCache.get(Constant.H5_SUBACCOUNT_MOBILE_CODE + "_" + subAccountDTO.getIdCard() + "_" + subAccountDTO.getMobile());
        if (StringUtil.isEmpty(cachedKey) || !key.equals(cachedKey)) {
            throw new BadRequestException("非法操作");
        }

        //校验手机验证码
        Boolean bool = smsService.verifyCode(subAccountDTO.getMobile(), Constant.COUNTRY_CODE, smsCode);
        if (!bool) {
            throw new BadRequestException("验证码错误，请重新输入");
        }

        Map<String, Object> map = new HashMap<>(8);
        DistributorDTO distributorDTO = distributorMapper.getDistributorByUserId(subAccountDTO.getUserId());
        if (null == distributorDTO) {
            throw new BadRequestException("该经销商不存在");
        }

        map.put("companyName", distributorDTO.getCompanyName());
        map.put("province", distributorDTO.getProvince());
        map.put("city", distributorDTO.getCity());
        map.put("region", distributorDTO.getRegion());
        //设置key
        String codeKey = UUIDUtil.longuuid();
        redisCache.set(Constant.H5_SUBACCOUNT_SMS_CODE + "_" + subAccountDTO.getIdCard() + "_" + subAccountDTO.getMobile() + smsCode, codeKey, 3600L);
        map.put("key", codeKey);
        return CommResult.ok(map);
    }


    @Override
    public DistributorDTO getDistributorById(Integer id) {
        DistributorDTO distributorDTO = distributorMapper.getDistInfoById(id);
        if (distributorDTO != null) {
            return distributorDTO;
        }
        return null;
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
    @Override
    public CommResult determineSubaccount(SubDistributorAccountDTO subAccountDTO, String key, String smsCode) {
        String cachedKey = redisCache.get(Constant.H5_SUBACCOUNT_SMS_CODE + "_" + subAccountDTO.getIdCard() + "_" + subAccountDTO.getMobile() + smsCode);
        if (StringUtil.isEmpty(cachedKey) || !key.equals(cachedKey)) {
            throw new BadRequestException("非法操作");
        }

        if (StringUtil.isEmpty(subAccountDTO.getProvince()) || StringUtil.isEmpty(subAccountDTO.getCity()) || StringUtil.isEmpty(subAccountDTO.getRegion())) {
            throw new BadRequestException("参数为空");
        }

        Distributor query = new Distributor();
        query.setUserId(subAccountDTO.getUserId());
        Distributor mainDistributor = distributorMapper.selectOne(query);
        if (null == mainDistributor) {
            throw new BadRequestException("企业版经销商未找到");
        }

        log.info("***************保存子账号信息*****************");
        DistributorRoleDTO role = distributorRoleService.getByLevel(DistributorRoleLevel.D_1000.value);
        if (null == role) {
            throw new BadRequestException("经销商角色不存在或已禁用");
        }
        Distributor dist = new Distributor();
        dist.setRoleId(role.getId());
        dist.setRoleName(role.getName());
        dist.setRoleLevel(DistributorRoleLevel.D_1000.value);
        dist.setQuota(role.getWaterDeviceQuota());
        dist.setRemainingQuota(role.getWaterDeviceQuota());
        dist.setRealName(subAccountDTO.getRealName());
        dist.setSex(subAccountDTO.getSex());
        dist.setIdCard(subAccountDTO.getIdCard());
        dist.setPhone(subAccountDTO.getMobile());
        dist.setEmail(subAccountDTO.getEmail());
        dist.setProvince(subAccountDTO.getProvince());
        dist.setCity(subAccountDTO.getCity());
        dist.setRegion(subAccountDTO.getRegion());
        //设置区域ID，站务系统用此字段区分所属服务站
        Integer areaId = redisCache.get(Constant.AREA_CACHE + dist.getProvince() + "_" + dist.getCity() + "_" + dist.getRegion(), Integer.class);
        if (areaId == null) {
            areaId = systemFeign.getRegionIdByPCR(dist.getProvince(), dist.getCity(), dist.getRegion());
        }
        dist.setAreaId(areaId);
        String account = generateDistributorAccount();
        dist.setUserName(account);
        String pwd = UUIDUtil.numuuid(8);
        dist.setPassword(MD5Util.encodeMD5(pwd.toUpperCase()));
        dist.setTerminal(CreateDistributorTerminal.H5.value);
        dist.setCompanyId(mainDistributor.getCompanyId());
        dist.setCompanyName(mainDistributor.getCompanyName());
        dist.setPid(mainDistributor.getId());
        dist.setCompleteTime(new Date());
        dist.setRecommendId(mainDistributor.getId());
        dist.setRecommendName(mainDistributor.getRealName());
        dist.setCreateTime(new Date());
        int num = distributorMapper.insertSelective(dist);
        if (num < 1) {
            throw new BadRequestException("企业版经销商子账号创建失败");
        }

        // 保存注册记录
        saveUserChangeRecord(dist, null);

        //用户信息创建逻辑
        User user = createOrBindingUserWhileCreateDistributor(dist);

        //将用户id回写该经销商代理商
        Distributor update = new Distributor();
        update.setId(dist.getId());
        update.setUserId(user.getId());
        update.setOldId(dist.getId().toString());
        distributorMapper.updateByPrimaryKeySelective(update);

        //成功发送短信
        sendMessage(DistributorType.DEALER.value, dist.getUserName(), pwd, dist.getPhone(),areaId);

        Map<String, Object> map = new HashMap<>(8);
        map.put("companyName", subAccountDTO.getCompanyName());
        map.put("mobile", subAccountDTO.getMobile());
        map.put("userName", dist.getUserName());
        map.put("password", pwd);
        return CommResult.ok(map);
    }


    public Map<String, Object> getRecommendAndStation(DistributorDTO distributorDTO) {
        Map<String, Object> map = new HashMap<>(8);
        StationDTO stationDTO = systemFeign.getStationByPRC(distributorDTO.getProvince(), distributorDTO.getCity(), distributorDTO.getRegion(),PermissionTypeEnum.PRE_SALE.value);
        if (null != stationDTO) {
            map.put("stationName", stationDTO.getCompanyName());
        }
        map.put("recommendName", distributorDTO.getRealName());
        map.put("recommendId", distributorDTO.getId());
        map.put("province", distributorDTO.getProvince());
        map.put("city", distributorDTO.getCity());
        map.put("region", distributorDTO.getRegion());
        map.put("address", distributorDTO.getAddress());
        return map;
    }
}