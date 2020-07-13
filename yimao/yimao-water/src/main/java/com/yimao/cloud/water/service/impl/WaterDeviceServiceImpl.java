package com.yimao.cloud.water.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.yimao.cloud.base.baideApi.utils.BaideApiUtil;
import com.yimao.cloud.base.cache.UserCache;
import com.yimao.cloud.base.constant.Constant;
import com.yimao.cloud.base.constant.RabbitConstant;
import com.yimao.cloud.base.enums.DeviceFaultFilterType;
import com.yimao.cloud.base.enums.DeviceFaultState;
import com.yimao.cloud.base.enums.DeviceFaultType;
import com.yimao.cloud.base.enums.MessageMechanismEnum;
import com.yimao.cloud.base.enums.MessageModelTypeEnum;
import com.yimao.cloud.base.enums.MessagePushModeEnum;
import com.yimao.cloud.base.enums.MessagePushObjectEnum;
import com.yimao.cloud.base.enums.PayTerminal;
import com.yimao.cloud.base.enums.PayType;
import com.yimao.cloud.base.enums.ProductCostModelTypeEnum;
import com.yimao.cloud.base.enums.ProductCostTypeEnum;
import com.yimao.cloud.base.enums.RenewStatus;
import com.yimao.cloud.base.enums.Terminal;
import com.yimao.cloud.base.enums.WaterDeviceRenewStatus;
import com.yimao.cloud.base.exception.BadRequestException;
import com.yimao.cloud.base.exception.NotFoundException;
import com.yimao.cloud.base.exception.YimaoException;
import com.yimao.cloud.base.mail.MailSender;
import com.yimao.cloud.base.properties.DomainProperties;
import com.yimao.cloud.base.utils.CollectionUtil;
import com.yimao.cloud.base.utils.DateUtil;
import com.yimao.cloud.base.utils.StringUtil;
import com.yimao.cloud.base.utils.ThreadUtil;
import com.yimao.cloud.base.utils.UUIDUtil;
import com.yimao.cloud.base.utils.yun.YunOldIdUtil;
import com.yimao.cloud.framework.cache.RedisCache;
import com.yimao.cloud.pojo.bo.ProvinceCityRegion;
import com.yimao.cloud.pojo.dto.order.OrderRenewDTO;
import com.yimao.cloud.pojo.dto.order.SalesStatsDTO;
import com.yimao.cloud.pojo.dto.order.WorkOrderDTO;
import com.yimao.cloud.pojo.dto.out.DeviceQuery;
import com.yimao.cloud.pojo.dto.product.ProductCostDTO;
import com.yimao.cloud.pojo.dto.station.RenewStatisticsDTO;
import com.yimao.cloud.pojo.dto.system.SmsMessageDTO;
import com.yimao.cloud.pojo.dto.user.DistributorDTO;
import com.yimao.cloud.pojo.dto.user.EngineerDTO;
import com.yimao.cloud.pojo.dto.water.*;
import com.yimao.cloud.pojo.query.station.StationWaterDeviceQuery;
import com.yimao.cloud.pojo.query.water.WaterDeviceQuery;
import com.yimao.cloud.pojo.vo.PageVO;
import com.yimao.cloud.pojo.vo.station.DeviceGeneralSituationVO;
import com.yimao.cloud.pojo.vo.station.StationWaterDeviceVO;
import com.yimao.cloud.pojo.vo.water.WaterDeviceAreaClassificationVO;
import com.yimao.cloud.pojo.vo.water.WaterDeviceVO;
import com.yimao.cloud.water.feign.OrderFeign;
import com.yimao.cloud.water.feign.SystemFeign;
import com.yimao.cloud.water.feign.UserFeign;
import com.yimao.cloud.water.handler.ProductFeignHandler;
import com.yimao.cloud.water.mapper.DeductionPlanMapper;
import com.yimao.cloud.water.mapper.FilterMarksMapper;
import com.yimao.cloud.water.mapper.WaterDeviceCostChangeRecordMapper;
import com.yimao.cloud.water.mapper.WaterDeviceMapper;
import com.yimao.cloud.water.mapper.WaterDeviceRenewConfigMapper;
import com.yimao.cloud.water.mapper.WaterDeviceReplaceRecordMapper;
import com.yimao.cloud.water.po.DeductionPlan;
import com.yimao.cloud.water.po.FilterMarks;
import com.yimao.cloud.water.po.WaterDevice;
import com.yimao.cloud.water.po.WaterDeviceCostChangeRecord;
import com.yimao.cloud.water.po.WaterDeviceRenewConfig;
import com.yimao.cloud.water.po.WaterDeviceReplaceRecord;
import com.yimao.cloud.water.service.DeductionPlanService;
import com.yimao.cloud.water.service.PadApiService;
import com.yimao.cloud.water.service.WaterDeviceFaultService;
import com.yimao.cloud.water.service.WaterDevicePlaceChangeRecordService;
import com.yimao.cloud.water.service.WaterDeviceService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

/**
 * 描述：水机设备
 *
 * @Author Zhang Bo
 * @Date 2019/2/25 14:43
 */
@Service
@Slf4j
public class WaterDeviceServiceImpl implements WaterDeviceService {

    @Resource
    private UserCache userCache;
    @Resource
    private OrderFeign orderFeign;
    @Resource
    private ProductFeignHandler productFeignHandler;
    @Resource
    private UserFeign userFeign;
    @Resource
    private SimCardService simCardService;
    @Resource
    private WaterDeviceMapper waterDeviceMapper;
    @Resource
    private WaterDeviceRenewConfigMapper waterDeviceRenewConfigMapper;
    @Resource
    private DeductionPlanMapper deductionPlanMapper;
    @Resource
    private FilterMarksMapper filterMarksMapper;
    @Resource
    private WaterDeviceFaultService waterDeviceFaultService;
    @Resource
    private WaterDeviceReplaceRecordMapper waterDeviceReplaceRecordMapper;
    @Resource
    private WaterDeviceCostChangeRecordMapper waterDeviceCostChangeRecordMapper;
    @Resource
    private DeductionPlanService deductionPlanService;
    @Resource
    private WaterDevicePlaceChangeRecordService waterDevicePlaceChangeRecordService;
    @Resource
    private PadApiService padApiService;

    @Resource
    private AmqpTemplate rabbitTemplate;
    @Resource
    private DomainProperties domainProperties;
    @Resource
    private MailSender mailSender;
    @Resource
    private SystemFeign systemFeign;
    @Resource
    private RedisCache redisCache;

    /**
     * 创建水机设备信息
     *
     * @param device 水机设备信息
     */
    @Override
    public WaterDeviceDTO save(WaterDevice device) {
        waterDeviceMapper.insert(device);
        WaterDeviceDTO dto = new WaterDeviceDTO();
        device.convert(dto);
        return dto;
    }

    /**
     * 根据ID获取水机设备信息
     *
     * @param id 设备ID
     */
    @Override
    public WaterDevice getById(Integer id) {
        return waterDeviceMapper.selectByPrimaryKey(id);
    }

    /**
     * 根据SN码获取水机设备信息
     *
     * @param sn SN码
     */
    @Override
    public WaterDevice getBySnCode(String sn) {
        Example example = new Example(WaterDevice.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("sn", sn);
        example.orderBy("snEntryTime").desc();
        List<WaterDevice> deviceList = waterDeviceMapper.selectByExample(example);
        if (CollectionUtil.isEmpty(deviceList)) {
            return null;
        }
        return deviceList.get(0);
    }

    /**
     * 根据SN码获取水机设备基本信息
     *
     * @param sn SN码
     */
    @Override
    public WaterDevice getBasicInfoBySnCode(String sn) {
        return waterDeviceMapper.selectBasicInfoBySn(sn);
    }

    /**
     * 根据水机设备iccid获取设备信息
     *
     * @param iccid 设备sim卡号
     */
    @Override
    public WaterDevice getByIccid(String iccid) {
        WaterDevice query = new WaterDevice();
        query.setIccid(iccid);
        WaterDevice waterDevice = waterDeviceMapper.selectOne(query);
        if (Objects.nonNull(waterDevice)) {
            return waterDevice;
        }
        return null;
    }

    /**
     * 更新
     *
     * @param waterDevice 水机设备
     */
    @Override
    public void update(WaterDevice waterDevice) {
        waterDeviceMapper.updateByPrimaryKeySelective(waterDevice);
    }

    /**
     * 根据条件分页获取设备信息（站务系统调用）
     *
     * @param pageNum  页码
     * @param pageSize 页面大小
     * @param query    查询条件
     */
    @Override
    public PageVO<StationWaterDeviceVO> stationPageWaterDeviceInfo(Integer pageNum, Integer pageSize, StationWaterDeviceQuery query) {
        if (query.getQueryType() == null) {
            throw new BadRequestException("请指明查询类型！");
        }
        if (query.getQueryType() == 1) { //根据安装工查询
            //根据areaId获取安装工id集合
            List<Integer> engineerIds = userFeign.getEngineerIdsByAreaIds(query.getAreas());
            query.setEngineerIds(engineerIds);
        } else if (query.getQueryType() == 2) {
            List<Integer> distributorIds = userFeign.getDistributorIdsByAreaIds(query.getAreas());
            query.setDistributorIds(distributorIds);
        }
        PageHelper.startPage(pageNum, pageSize);
        Page<StationWaterDeviceVO> page = waterDeviceMapper.selectPageToStation(query);
        return new PageVO<>(pageNum, page);
    }

    /**
     * 根据条件分页获取设备信息
     *
     * @param pageNum  页码
     * @param pageSize 页面大小
     * @param query    查询条件
     */
    @Override
    public PageVO<WaterDeviceDTO> page(Integer pageNum, Integer pageSize, WaterDeviceQuery query) {
        PageHelper.startPage(pageNum, pageSize);
        Page<WaterDeviceDTO> page = waterDeviceMapper.selectPage(query);
        return new PageVO<>(pageNum, page);
    }

    /**
     * 安装工app-设备管理-查询
     *
     * @param pageNum  页码
     * @param pageSize 页面大小
     * @param query    查询条件
     */
    @Override
    public PageVO<WaterDeviceDTO> pageDeviceForEngineerApp(Integer pageNum, Integer pageSize, Integer engineerId, String search) {
        PageHelper.startPage(pageNum, pageSize);
        Page<WaterDeviceDTO> page = waterDeviceMapper.pageDeviceForEngineerApp(engineerId, search);
        return new PageVO<>(pageNum, page);
    }

    /**
     * 检查sn是否存在
     */
    @Override
    public Boolean checkSnExists(Integer id, String sn) {
        WaterDevice device = getBySnCode(sn);
        if (device != null && !Objects.equals(device.getId(), id)) {
            return true;
        }

        return false;
    }

    /**
     * 检查sim是否存在
     */
    @Override
    public Boolean checkIccidExists(Integer id, String iccid) {
        WaterDevice device = getBySIM(iccid);
        if (device != null && !Objects.equals(device.getIccid(), iccid)) {
            return true;
        }
        return false;
    }

    /**
     * 根据sim获取水机设备信息
     *
     * @param iccid
     */
    @Override
    public WaterDevice getBySIM(String iccid) {
        WaterDevice query = new WaterDevice();
        query.setIccid(iccid);
        WaterDevice waterDevice = waterDeviceMapper.selectOne(query);
        if (Objects.nonNull(waterDevice)) {
            return waterDevice;
        }
        return null;
    }

    /**
     * 更新水机设备信息
     *
     * @param dto
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
    public void update(WaterDeviceDTO dto) {
        WaterDevice oldDevice = waterDeviceMapper.selectByPrimaryKey(dto.getId());
        WaterDevice waterDevice = new WaterDevice(dto);
        waterDeviceMapper.updateByPrimaryKeySelective(waterDevice);

        //sn码、batchCode、iccId变化了,保存设备更换记录
        if (isChange(oldDevice, dto)) {
            //保存设备更换记录信息
            WaterDeviceReplaceRecord replaceRecord = new WaterDeviceReplaceRecord();
            replaceRecord.setOldSn(oldDevice.getSn());
            replaceRecord.setNewSn(dto.getSn());
            replaceRecord.setOldIccid(oldDevice.getIccid());
            replaceRecord.setNewIccid(dto.getIccid());
            replaceRecord.setOldBatchCode(oldDevice.getLogisticsCode());
            replaceRecord.setNewBatchCode(dto.getLogisticsCode());
            replaceRecord.setTime(oldDevice.getCurrentTotalTime());
            replaceRecord.setFlow(oldDevice.getCurrentTotalFlow());
            replaceRecord.setCostId(oldDevice.getCostId());
            replaceRecord.setCostName(oldDevice.getCostName());
            replaceRecord.setMoney(oldDevice.getMoney());
            replaceRecord.setDeviceModel(oldDevice.getDeviceModel());
            replaceRecord.setProvince(oldDevice.getProvince());
            replaceRecord.setCity(oldDevice.getCity());
            replaceRecord.setRegion(oldDevice.getRegion());
            replaceRecord.setCreator(oldDevice.getEngineerName());
            replaceRecord.setCreateTime(new Date());
            waterDeviceReplaceRecordMapper.insert(replaceRecord);
        }

    }

    /****
     * 判断sn码、batchCode、iccId是否有变化
     * @param oldDevice
     * @param dto
     * @return
     */
    private boolean isChange(WaterDevice oldDevice, WaterDeviceDTO newDevice) {
        if (!StringUtil.isEmpty(oldDevice.getSn()) && !StringUtil.isEmpty(newDevice.getSn()) &&
                !oldDevice.getSn().equals(newDevice.getSn())) {
            return true;
        }

        if (!StringUtil.isEmpty(oldDevice.getLogisticsCode()) && !StringUtil.isEmpty(newDevice.getLogisticsCode()) &&
                !oldDevice.getLogisticsCode().equals(newDevice.getLogisticsCode())) {
            return true;
        }
        if (!StringUtil.isEmpty(oldDevice.getIccid()) && !StringUtil.isEmpty(newDevice.getIccid()) &&
                !oldDevice.getIccid().equals(newDevice.getIccid())) {
            return true;
        }

        return false;
    }

    /**
     * 设备概况：按在线离线统计分类
     */
    @Override
    public Map<String, Integer> classificationByOnline() {
        Example example = new Example(WaterDevice.class);
        //所有设备数量
        int count = waterDeviceMapper.selectCountByExample(example);
        Calendar calendar = Calendar.getInstance();
        //125分钟之前的时间
        calendar.add(Calendar.MINUTE, -125);
        Date time = calendar.getTime();
        Example.Criteria criteria = example.createCriteria();
        criteria.andGreaterThan("lastOnlineTime", time);
        //125分钟内在线设备数量
        int onlineCount = waterDeviceMapper.selectCountByExample(example);
        Map<String, Integer> map = new HashMap<>();
        map.put("count", count);
        map.put("onlineCount", onlineCount);
        map.put("offlineCount", count - onlineCount);
        return map;
    }

    /**
     * 设备概况：按设备地区统计分类
     */
    @Override
    public Map<String, Integer> classificationByArea() {
        Map<String, Integer> map = new HashMap<>();
        //所有设备数量
        int otherCount = waterDeviceMapper.selectCount(new WaterDevice());
        //按省分组统计设备数量并倒叙排序
        List<WaterDeviceAreaClassificationVO> list = waterDeviceMapper.countByProvince();
        if (CollectionUtil.isNotEmpty(list)) {
            for (WaterDeviceAreaClassificationVO wdac : list) {
                map.put(wdac.getName(), wdac.getAmount());
                Integer provinceCount = wdac.getAmount();
                if (provinceCount != null) {
                    otherCount -= provinceCount;
                }
            }
            map.put("其它", otherCount);
        }
        return map;
    }

    /**
     * 设备概况：按设产品型号统计分类
     */
    @Override
    public Map<String, Integer> classificationByModel() {
        Map<String, Integer> map = new HashMap<>();
        //按设备型号分组统计设备数量并倒叙排序
        List<WaterDeviceAreaClassificationVO> list = waterDeviceMapper.countByDeviceModel();
        if (CollectionUtil.isNotEmpty(list)) {
            for (WaterDeviceAreaClassificationVO wdac : list) {
                map.put(wdac.getName(), wdac.getAmount());
            }
        }
        return map;
    }

    /**
     * 设备概况：按激活日期统计分类
     *
     * @param startTime 开始日期
     * @param endTime   结束日期
     */
    @Override
    public Map<String, Integer> classificationByTrend(Date startTime, Date endTime) {
        Map<String, Integer> map = new TreeMap<>();

        String start;
        String end;
        Date now = new Date();
        //30天以前的日期
        Date monthAgo = DateUtil.dayAfter(now, -30);
        if (startTime == null && endTime == null) {
            startTime = monthAgo;
            endTime = now;
        } else if (startTime == null) {
            startTime = DateUtil.dayAfter(endTime, -30);
        } else if (endTime == null) {
            endTime = DateUtil.dayAfter(startTime, 30);
            if (endTime.after(now)) {
                endTime = now;
            }
        }
        start = DateUtil.transferDateToString(startTime, DateUtil.DEFAULT_TIME_FORMAT);
        end = DateUtil.transferDateToString(endTime, DateUtil.DEFAULT_TIME_FORMAT);

        //按设备型号分组统计设备数量并倒叙排序
        List<WaterDeviceAreaClassificationVO> list = waterDeviceMapper.countByTrend(start, end);

        //把两个日期之间的所有天数都放到map中
        int days = DateUtil.betweenDays(startTime, endTime);
        for (int i = 0; i <= days; i++) {
            map.put(DateUtil.transferDateToString(DateUtil.dayAfter(startTime, i), DateUtil.DEFAULT_TIME_FORMAT), 0);
        }
        if (CollectionUtil.isNotEmpty(list)) {
            //把数据库查询出来的某天水机设备激活数量更新到map中
            for (WaterDeviceAreaClassificationVO wdac : list) {
                map.put(wdac.getName(), wdac.getAmount());
            }
        }
        return map;
    }

    /**
     * 根据ID获取水机设备信息
     *
     * @param id 设备ID
     */
    @Override
    public WaterDeviceVO getDetailById(Integer id) {
        WaterDeviceVO vo = waterDeviceMapper.selectDetailById(id);
        if (vo.getDistributorId() != null) {
            //获取经销商信息
            EngineerDTO queryEngineer = userFeign.getEngineerDTOById(vo.getEngineerId());
            if (queryEngineer != null) {
                //设置需要返回的areaId，用于权限校验
                vo.setAreaId(queryEngineer.getAreaId());
            }
        }

        //sim卡运营商
        Integer simAccountId = vo.getSimAccountId();
        //查询SIM详情信息
        String iccid = vo.getIccid();
        //通过调用运营商的接口获取SIM卡详细信息
        SimCardDTO simCard = simCardService.callGetDetail(id, simAccountId, iccid);
        if (simCard != null) {
            //SIM本月使用流量
            vo.setMonthDataFlowUsed(simCard.getMonthDataFlowUsed());
            if (vo.getSimActivatingTime() == null && StringUtil.isNotEmpty(simCard.getActivatingTime())) {
                vo.setSimActivatingTime(DateUtil.transferStringToDate(simCard.getActivatingTime()));
            }
            if (StringUtil.isEmpty(vo.getSimCompany()) && StringUtil.isNotEmpty(simCard.getSimCompany())) {
                vo.setSimCompany(simCard.getSimCompany());
            }
        }
        WaterDeviceFaultDTO fault = waterDeviceFaultService.getByDeviceIdAndSn(id, vo.getSn());
        if (fault != null) {
            //设备状态，故障状态
            vo.setDeviceStatus(fault.getFault());
        }

        //填充工单信息，订单号，下单时间等
        String workOrderId = vo.getWorkOrderId();
        if (StringUtil.isNotEmpty(workOrderId)) {
            WorkOrderDTO workOrder = orderFeign.getWorkOrderById(workOrderId);
            if (workOrder != null) {
                vo.setWorkOrderId(workOrder.getId());
                vo.setSubOrderId(String.valueOf(workOrder.getSubOrderId()));
                vo.setOrderCreateTime(workOrder.getCreateTime());
                //来源
                Integer terminal = workOrder.getTerminal();
                if (terminal != null) {
                    vo.setOrderFrom(Terminal.getName(terminal));
                }
            }
        }

        //水机更换位置信息查询
        WaterDevicePlaceChangeRecordDTO placeChangeRecord = waterDevicePlaceChangeRecordService.getBySn(vo.getSn());
        if (placeChangeRecord != null) {
            vo.setChangePlace("从【" + placeChangeRecord.getOldPlace() + "】更换到【" + placeChangeRecord.getNewPlace() + "】");
            //更换过地区，详细地址应该展示变更记录里面的详细地址
            vo.setAddress(placeChangeRecord.getDetailAddress());
        }

        return vo;
    }

    /**
     * 修改水机计费方式
     *
     * @param id        设备ID
     * @param newCostId 新的计费方式ID
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
    public void changeCost(Integer id, Integer newCostId) {
        //获取设备的扣费计划列表
        List<DeductionPlan> plans = deductionPlanService.listByDeviceId(id);
        if (CollectionUtil.isNotEmpty(plans)) {
            throw new BadRequestException("该设备已使用新续费套餐，无法更换计费方式。");
        }
        ProductCostDTO newCost = productFeignHandler.getProductCostById(newCostId);
        if (newCost == null) {
            throw new BadRequestException("修改计费方式失败，新计费方式信息有误。");
        } else {
            Date now = new Date();
            //根据ID获取水机设备对应的计费方式信息
            WaterDeviceDTO device = waterDeviceMapper.selectByIdForChangeCost(id);
            Integer currentCostId = device.getCostId();
            List<ProductCostDTO> costList = productFeignHandler.listProductCostByOldCostId(currentCostId);
            //校验新计费方式是否合法
            boolean check = false;
            for (ProductCostDTO cost : costList) {
                if (Objects.equals(cost.getId(), newCostId)) {
                    check = true;
                }
            }
            if (!check) {
                throw new BadRequestException("该产品不支持该计费方式，无法修改。");
            } else if (Objects.equals(newCostId, currentCostId)) {
                //计费方式没有变化
            } else {
                WaterDeviceCostChangeRecord record = new WaterDeviceCostChangeRecord();
                //水机SN码
                record.setSn(device.getSn());
                record.setOldCostId(currentCostId);
                record.setOldCostName(device.getCostName());
                record.setNewCostId(newCostId);
                record.setNewCostName(newCost.getName());
                //修改计费方式时使用了的时长
                record.setTime(device.getCurrentTotalTime());
                //修改计费方式时使用了的流量
                record.setFlow(device.getCurrentTotalFlow());
                //修改计费方式时的余额
                record.setMoney(device.getMoney());
                record.setCreator(userCache.getCurrentAdminRealName());
                record.setCreateTime(now);
                waterDeviceCostChangeRecordMapper.insert(record);

                WaterDevice update = new WaterDevice();
                update.setId(id);
                update.setCostChanged(true);
                update.setLastCostChangeTime(now);
                update.setNewCostId(newCostId);
                waterDeviceMapper.updateByPrimaryKeySelective(update);
            }
        }
    }

    /**
     * 激活SIM卡
     *
     * @param id 设备ID
     */
    @Override
    public void activatingSimCard(Integer id, String iccid) {
        WaterDevice device = waterDeviceMapper.selectByPrimaryKey(id);
        if (device == null) {
            throw new BadRequestException("激活SIM卡失败。");
        } else {
            //查询SIM详情信息
            Integer simAccountId = device.getSimAccountId();
            // if (simAccountId == null) {
            //     throw new BadRequestException("该设备未设置SIM账号信息。");
            // }
            String iccid2 = StringUtil.isNotEmpty(iccid) ? iccid : device.getIccid();
            //激活SIM卡
            simCardService.callEditTerminal(id, simAccountId, iccid2);
        }
    }

    /**
     * 停用SIM卡
     *
     * @param id 设备ID
     */
    @Override
    public void deactivatedSimCard(Integer id) {
        WaterDevice device = waterDeviceMapper.selectByPrimaryKey(id);
        if (device == null || StringUtil.isEmpty(device.getIccid())) {
            throw new BadRequestException("停用SIM卡失败。");
        } else {
            //查询SIM详情信息
            Integer simAccountId = device.getSimAccountId();
            // if (simAccountId == null) {
            //     throw new BadRequestException("该设备未设置SIM账号信息。");
            // }
            String iccid = device.getIccid();
            //停用SIM卡
            simCardService.deactivatedTerminal(id, simAccountId, iccid);
        }
    }

    /**
     * 水机设备列表-解除绑定
     *
     * @param id 设备ID
     */
    @Override
    public void unbundling(Integer id) {
        WaterDevice update = new WaterDevice();
        update.setId(id);
        update.setBind(false);
        int i = waterDeviceMapper.updateByPrimaryKeySelective(update);
        if (i < 1) {
            throw new BadRequestException("操作失败。");
        }
    }

    /**
     * 水机设备列表-恢复满额
     *
     * @param id 设备ID
     */
    @Override
    public void restoreFullAmount(Integer id) {
        WaterDevice device = waterDeviceMapper.selectByPrimaryKey(id);
        if (device == null) {
            throw new BadRequestException("操作对象不存在。");
        } else {
            //判断是否存在扣费计划，如果存在不能恢复满额
            DeductionPlan query = new DeductionPlan();
            query.setDeviceId(id);
            int count = deductionPlanMapper.selectCount(query);
            if (count > 0) {
                throw new BadRequestException("该水机已续费，无法恢复满额。");
            }
            WaterDevice update = new WaterDevice();
            update.setId(id);
            update.setMoney(device.getInitMoney());
            int i = waterDeviceMapper.updateByPrimaryKeySelective(update);
            if (i < 1) {
                throw new BadRequestException("操作失败。");
            }
        }
    }

    /**
     * 水机设备列表-修改设备信息
     *
     * @param id           设备ID
     * @param oldSn        旧SN码
     * @param newSn        新SN码
     * @param oldIccid     旧iccid
     * @param newIccid     新iccid
     * @param oldBatchCode 旧批次码
     * @param newBatchCode 新批次码
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
    public void update(Integer id, String oldSn, String newSn, String oldIccid, String newIccid, String oldBatchCode, String newBatchCode, String address) {
        WaterDevice device = waterDeviceMapper.selectByPrimaryKey(id);
        if (device == null) {
            throw new BadRequestException("操作对象不存在。");
        } else {
            oldSn = device.getSn();
            oldIccid = device.getIccid();
            oldBatchCode = device.getLogisticsCode();
            WaterDevice update = new WaterDevice();
            update.setId(id);
            update.setSn(newSn);
            update.setIccid(newIccid);
            update.setLogisticsCode(newBatchCode);
            // update.setSimCardAccountId(null);
            if (StringUtils.isNotBlank(address)) {
                WaterDevicePlaceChangeRecordDTO wpc = waterDevicePlaceChangeRecordService.getBySn(oldSn);
                if (Objects.nonNull(wpc)) {
                    wpc.setSn(newSn);
                    wpc.setDetailAddress(address.trim());

                    waterDevicePlaceChangeRecordService.update(wpc);
                } else {
                    update.setAddress(address.trim());
                }
            }
            int i = waterDeviceMapper.updateByPrimaryKeySelective(update);
            if (i < 1) {
                throw new BadRequestException("操作失败。");
            }
            if (!oldSn.equals(newSn) || !oldIccid.equals(newIccid) || !oldBatchCode.equals(newBatchCode)) {
                Integer simAccountId = device.getSimAccountId();
                //停用原SIM卡
                simCardService.deactivatedTerminal(id, simAccountId, oldIccid);
                //激活新SIM卡
                simCardService.callEditTerminal(id, null, newIccid);

                //保存设备更换记录信息
                WaterDeviceReplaceRecord replaceRecord = new WaterDeviceReplaceRecord();
                replaceRecord.setOldSn(oldSn);
                replaceRecord.setNewSn(newSn);
                replaceRecord.setOldIccid(oldIccid);
                replaceRecord.setNewIccid(newIccid);
                replaceRecord.setOldBatchCode(oldBatchCode);
                replaceRecord.setNewBatchCode(newBatchCode);
                replaceRecord.setTime(device.getCurrentTotalTime());
                replaceRecord.setFlow(device.getCurrentTotalFlow());
                replaceRecord.setCostId(device.getCostId());
                replaceRecord.setCostName(device.getCostName());
                replaceRecord.setMoney(device.getMoney());
                replaceRecord.setDeviceModel(device.getDeviceModel());
                replaceRecord.setProvince(device.getProvince());
                replaceRecord.setCity(device.getCity());
                replaceRecord.setRegion(device.getRegion());
                replaceRecord.setCreator(userCache.getCurrentAdminRealName());
                replaceRecord.setCreateTime(new Date());
                waterDeviceReplaceRecordMapper.insert(replaceRecord);
                //TODO 同步修改售后系统中的设备信息
            }
        }
    }

    /**
     * 水机设备列表-更换设备
     *
     * @param deviceId     设备ID
     * @param oldSn        旧SN码
     * @param newSn        新SN码
     * @param oldIccid     旧iccid
     * @param newIccid     新iccid
     * @param oldBatchCode 旧批次码
     * @param newBatchCode 新批次码
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
    public void replace(Integer deviceId, String oldSn, String newSn, String oldIccid, String newIccid, String oldBatchCode, String newBatchCode) {
        WaterDevice device;
        if (deviceId != null) {
            device = waterDeviceMapper.selectByPrimaryKey(deviceId);
        } else {
            device = waterDeviceMapper.selectBasicInfoBySn(oldSn);
        }
        if (device == null) {
            throw new BadRequestException("操作对象不存在。");
        } else {
            //校验新SN码是否已经存在
            if (this.checkSnExists(device.getId(), newSn)) {
                throw new BadRequestException("新的SN码已存在，不能使用。");
            }

            oldSn = device.getSn();
            oldIccid = device.getIccid();
            oldBatchCode = device.getLogisticsCode();

            WaterDevice update = new WaterDevice();
            update.setId(device.getId());
            update.setSn(newSn);
            update.setIccid(newIccid);
            update.setLogisticsCode(newBatchCode);
            update.setBind(false);
            update.setLastTotalTime(0);
            update.setLastTotalFlow(0);
            update.setCurrentTotalTime(0);
            update.setCurrentTotalFlow(0);
            update.setUseTime(0);
            update.setUseFlow(0);
            update.setSimAccountId(null);
            update.setLastPpChangeTime(null);
            update.setLastT33ChangeTime(null);
            update.setLastCtoChangeTime(null);
            update.setLastUdfChangeTime(null);
            waterDeviceMapper.updateForChangeDevice(update);

            Integer simAccountId = device.getSimAccountId();
            String iccid = device.getIccid();
            //停用原SIM卡
            simCardService.deactivatedTerminal(device.getId(), simAccountId, iccid);
            //激活新SIM卡
            simCardService.callEditTerminal(device.getId(), null, newIccid);

            //保存设备更换记录信息
            WaterDeviceReplaceRecord replaceRecord = new WaterDeviceReplaceRecord();
            replaceRecord.setOldSn(oldSn);
            replaceRecord.setNewSn(newSn);
            replaceRecord.setOldIccid(oldIccid);
            replaceRecord.setNewIccid(newIccid);
            replaceRecord.setOldBatchCode(oldBatchCode);
            replaceRecord.setNewBatchCode(newBatchCode);
            replaceRecord.setTime(device.getCurrentTotalTime());
            replaceRecord.setFlow(device.getCurrentTotalFlow());
            replaceRecord.setCostId(device.getCostId());
            replaceRecord.setCostName(device.getCostName());
            replaceRecord.setMoney(device.getMoney());
            replaceRecord.setDeviceModel(device.getDeviceModel());
            replaceRecord.setProvince(device.getProvince());
            replaceRecord.setCity(device.getCity());
            replaceRecord.setRegion(device.getRegion());

            //判断是安装工更新 还是业务平台更新,安装工更新deviceId传null
            if (deviceId == null) {
                replaceRecord.setCreator(device.getEngineerName());
            } else {
                replaceRecord.setCreator(userCache.getCurrentAdminRealName());
            }
            replaceRecord.setCreateTime(new Date());
            waterDeviceReplaceRecordMapper.insert(replaceRecord);

            //更新扣费计划
            deductionPlanService.updatePlansForChangeDevice(device.getId(), device.getCurrentTotalFlow(), oldSn, newSn);

            //更新原来的设备故障信息状态
            // waterDeviceFaultService.resolve(device.getId(), device.getSn(), DeviceFaultType.TDS.value, "");
            // waterDeviceFaultService.resolve(device.getId(), device.getSn(), DeviceFaultType.WATER.value, "");
            // waterDeviceFaultService.resolve(device.getId(), device.getSn(), DeviceFaultType.FILTER.value, "PP");
            // waterDeviceFaultService.resolve(device.getId(), device.getSn(), DeviceFaultType.FILTER.value, "T33");
            // waterDeviceFaultService.resolve(device.getId(), device.getSn(), DeviceFaultType.FILTER.value, "CTO");
            // waterDeviceFaultService.resolve(device.getId(), device.getSn(), DeviceFaultType.FILTER.value, "UDF");

            //解除设备故障
            this.resolveDeviceFault(device.getId(), device.getSn());

            //移除滤芯初始工作标记
            FilterMarks fm = new FilterMarks();
            fm.setDeviceId(device.getId());
            filterMarksMapper.delete(fm);

            //修改工单上的SN码
            if (StringUtil.isNotEmpty(device.getWorkOrderId())) {
                try {
                    WorkOrderDTO workOrderUpdate = new WorkOrderDTO();
                    workOrderUpdate.setId(device.getWorkOrderId());
                    workOrderUpdate.setSn(newSn);
                    orderFeign.updateWorkOrderPart(workOrderUpdate);
                } catch (Exception e) {
                    log.error("更换设备失败，修改工单上的SN码出错。" + e.getMessage(), e);
                    throw new YimaoException("更换设备失败，修改工单上的SN码出错。");
                }
            }

            //修改续费单上的SN码
            if (device.getRenewTimes() != null && device.getRenewTimes() > 0) {
                try {
                    orderFeign.updateRenewOrderSn(device.getId(), oldSn, newSn);
                } catch (Exception e) {
                    log.error("更换设备失败，修改续费单上的SN码出错。" + e.getMessage(), e);
                    throw new YimaoException("更换设备失败，修改续费单上的SN码出错。");
                }
            }

            //TODO 同步修改售后系统中的设备信息
        }
    }

    /**
     * 解除设备故障
     *
     * @param deviceId 设备ID
     * @param sn       SN码
     */
    private void resolveDeviceFault(Integer deviceId, String sn) {
        //解除TDS异常故障
        WaterDeviceFaultDTO deviceFault = new WaterDeviceFaultDTO();
        deviceFault.setDeviceId(deviceId);
        deviceFault.setSn(sn);
        deviceFault.setType(DeviceFaultType.TDS.value);
        deviceFault.setState(DeviceFaultState.RESOLVE.value);
        rabbitTemplate.convertAndSend(RabbitConstant.DEVICE_FAULT, deviceFault);

        //解除制水故障
        deviceFault.setType(DeviceFaultType.WATER.value);
        rabbitTemplate.convertAndSend(RabbitConstant.DEVICE_FAULT, deviceFault);

        //解除PP滤芯需要更换故障
        deviceFault.setType(DeviceFaultType.FILTER.value);
        deviceFault.setFilterType(DeviceFaultFilterType.PP.name);
        rabbitTemplate.convertAndSend(RabbitConstant.DEVICE_FAULT, deviceFault);

        //解除CTO滤芯需要更换故障
        deviceFault.setFilterType(DeviceFaultFilterType.CTO.name);
        rabbitTemplate.convertAndSend(RabbitConstant.DEVICE_FAULT, deviceFault);

        //解除UDF滤芯需要更换故障
        deviceFault.setFilterType(DeviceFaultFilterType.UDF.name);
        rabbitTemplate.convertAndSend(RabbitConstant.DEVICE_FAULT, deviceFault);

        //解除T33滤芯需要更换故障
        deviceFault.setFilterType(DeviceFaultFilterType.T33.name);
        rabbitTemplate.convertAndSend(RabbitConstant.DEVICE_FAULT, deviceFault);

        //TODO 需不需要把故障表里的老SN换成新SN
    }

    /**
     * 水机设备列表-续费
     *
     * @param id        设备ID
     * @param costId    新的计费方式ID
     * @param filePaths 附件文件路径
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
    public void renew(Integer id, Integer costId, Integer payType, String filePaths, MultipartFile[] files) {
        WaterDevice device = waterDeviceMapper.selectByPrimaryKey(id);
        if (device == null) {
            throw new BadRequestException("操作对象不存在。");
        } else {
            Integer oldCostId = device.getCostId();
            ProductCostDTO newCost = productFeignHandler.getProductCostById(costId);
            //新续费套餐合法性校验
            if (newCost == null || newCost.getModelType() != ProductCostModelTypeEnum.RENEW_FEE.value) {
                throw new BadRequestException("新续费套餐有误。");
            }
            // 如果计费方式发生更改（指流量改为时间、或时间改为流量），记录到设备信息上
            if (!Objects.equals(oldCostId, costId) && !Objects.equals(device.getCostType(), newCost.getType())) {
                WaterDevice updateDevice = new WaterDevice();
                updateDevice.setId(device.getId());
                updateDevice.setCostChanged(true);
                updateDevice.setLastCostChangeTime(new Date());
                waterDeviceMapper.updateByPrimaryKeySelective(updateDevice);
            }

            OrderRenewDTO renewOrder = new OrderRenewDTO();
            renewOrder.setId(UUIDUtil.buildRenewWorkOrderId());
            renewOrder.setSnCode(device.getSn());
            renewOrder.setDeviceId(device.getId());
            renewOrder.setDeviceModel(device.getDeviceModel());
            renewOrder.setDeviceAddress(device.getAddress());

            renewOrder.setCostId(newCost.getId());
            renewOrder.setCostName(newCost.getName());
            renewOrder.setCostType(newCost.getType());
            renewOrder.setCostTypeName(ProductCostTypeEnum.getName(newCost.getType()));
            renewOrder.setLastCostId(device.getCostId());
            renewOrder.setLastCostName(device.getCostName());
            renewOrder.setLastCostType(device.getCostType());
            renewOrder.setLastCostTypeName(ProductCostTypeEnum.getName(device.getCostType()));

            //续费时要支付的是租赁费（不含安装费）
            renewOrder.setAmountFee(newCost.getRentalFee());

            renewOrder.setPayCredential(filePaths);
            renewOrder.setPay(true);
            //支付类型：1-微信；2-支付宝；3-POS机；4-转账；
            renewOrder.setPayType(payType);
            renewOrder.setPayTypeName(PayType.find(payType).name);
            //支付状态：1-待审核，2-支付成功，3-支付失败
            renewOrder.setStatus(RenewStatus.AUDITED.value);
            renewOrder.setStatusName(RenewStatus.AUDITED.name);
            Date date = new Date();
            renewOrder.setPayTime(date);
            renewOrder.setCreateTime(date);
            renewOrder.setPayCredentialSubmitTime(date);
            renewOrder.setTerminal(PayTerminal.SYS.value);
            renewOrder.setTerminalName(PayTerminal.SYS.name);
            //续费订单的经销商信息要从水机设备上取
            renewOrder.setDistributorId(device.getDistributorId());
            //续费订单的安装工信息要从水机设备上取
            renewOrder.setEngineerId(device.getEngineerId());

            //同步售后
            try {
                afterWaterDeviceRenew(renewOrder, device, newCost, files);
            } catch (Exception e) {
                StringWriter sw = new StringWriter();
                e.printStackTrace(new PrintWriter(sw, true));
                String subject = "后台创建续费订单-同步续费订单到百得出错提醒" + domainProperties.getApi();
                String content = "后台创建续费订单-同步续费订单到百得出错。renewOrderId=" + renewOrder.getId() + "\n" + sw.toString();
                mailSender.send(null, subject, content);
            }

            //保存续费订单
            orderFeign.saveRenewWorkOrder(renewOrder);

            // 更新原来的设备故障信息状态
            // 放到审核通过时修改故障信息状态
            // waterDeviceFaultService.update(2, device.getId(), device.getSn(), 1, 1, "");
            // waterDeviceFaultService.update(2, device.getId(), device.getSn(), 5, 1, "");

        }
    }

    /**
     * 根据水机设备ID删除设备
     *
     * @param id 设备ID
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
    public void delete(Integer id) {
        WaterDevice device = waterDeviceMapper.selectByPrimaryKey(id);
        if (device == null) {
            throw new BadRequestException("操作对象不存在。");
        } else {
            int result = waterDeviceMapper.deleteByPrimaryKey(id);
            if (result < 1) {
                throw new YimaoException("删除设备失败！");
            }
        }
    }

/*    public BusinessProfileDTO orderOverviewBusiness() {
        BusinessProfileDTO dto = new BusinessProfileDTO();
        // 有效销售总额,有效订单总数,
        Map<String, Object> totalBusiness = orderSubMapper.selectTotalBusiness(null);
        if (totalBusiness != null) {
            dto.setSaleTotal((BigDecimal) totalBusiness.get("fee"));
            dto.setOrderTotal(((Long) totalBusiness.get("count")).intValue());
        }
        // 昨日有效销售额,昨日订单数
        Map<String, Object> map = orderSubMapper.selectTotalBusiness(1);
        if (map != null) {
            dto.setYestSaleTotal((BigDecimal) map.get("fee"));
            dto.setYestOrderTotal(((Long) map.get("count")).intValue());
        }
        //下单次数 成交笔数
        dto.setBargainTotal(orderSubMapper.selectCount4Pay());
        dto.setBuyTotal(orderSubMapper.selectCount4Create());
        // 获取前五的一级类目产品销售金额和销售数量
        List<BusinessProfileDetailDTO> list = orderSubMapper.selectBusiness();
        dto.setDetails(list);
        return dto;
    }*/

    /**
     * 设备增长趋势概况统计
     * type 类型按1-1周 2-8周 3-6个月
     *
     * @return UserOverviewDTO
     * @author hhf
     * @date 2019/3/19
     */
    @Override
    public Map getWaterDeviceGrowthTrend() {
        Map returnMap = new HashMap();
        try {
            // 安装水机趋势(1周)
            Callable<List<Map<String, Object>>> totalCountCallable = () -> waterDeviceMapper.oneWeekWaterDeviceTotalCount(1);
            FutureTask<List<Map<String, Object>>> totalCountTask = new FutureTask<>(totalCountCallable);
            ThreadUtil.executor.submit(totalCountTask);
            // 续费水机趋势(1周)
            Callable<List<Map<String, Object>>> renewTotalCountCallable = () -> waterDeviceMapper.oneWeekWaterDeviceTotalCount(2);
            FutureTask<List<Map<String, Object>>> renewTotalCountTask = new FutureTask<>(renewTotalCountCallable);
            ThreadUtil.executor.submit(renewTotalCountTask);

            // 安装水机趋势(8周)
            Callable<List<Map<String, Object>>> eightWeekTotalCountCallable = () -> waterDeviceMapper.eightWeekWaterDeviceTotalCount(1);
            FutureTask<List<Map<String, Object>>> eightWeekTotalCountTask = new FutureTask<>(eightWeekTotalCountCallable);
            ThreadUtil.executor.submit(eightWeekTotalCountTask);
            // 续费水机趋势(8周)
            Callable<List<Map<String, Object>>> eightWeekRenewTotalCountCallable = () -> waterDeviceMapper.eightWeekWaterDeviceTotalCount(2);
            FutureTask<List<Map<String, Object>>> eightWeekRenewTotalCountTask = new FutureTask<>(eightWeekRenewTotalCountCallable);
            ThreadUtil.executor.submit(eightWeekRenewTotalCountTask);
            // 安装水机趋势(6个月)
            Callable<List<Map<String, Object>>> sixMonthTotalCountCallable = () -> waterDeviceMapper.sixMonthWaterDeviceTotalCount(1);
            FutureTask<List<Map<String, Object>>> sixMonthTotalCountTask = new FutureTask<>(sixMonthTotalCountCallable);
            ThreadUtil.executor.submit(sixMonthTotalCountTask);
            // 续费水机趋势(6个月)
            Callable<List<Map<String, Object>>> sixMonthRenewTotalCountCallable = () -> waterDeviceMapper.sixMonthWaterDeviceTotalCount(2);
            FutureTask<List<Map<String, Object>>> sixMonthRenewTotalCountTask = new FutureTask<>(sixMonthRenewTotalCountCallable);
            ThreadUtil.executor.submit(sixMonthRenewTotalCountTask);

            List<Map<String, Object>> oneWeekInstalListMap = totalCountTask.get();
            List<Map<String, Object>> oneWeekRenewListMap = renewTotalCountTask.get();
            List<Map<String, Object>> eightWeekInstalListMap = eightWeekTotalCountTask.get();
            List<Map<String, Object>> eightWeekRenewListMap = eightWeekRenewTotalCountTask.get();
            List<Map<String, Object>> sixMonthInstalListMap = sixMonthTotalCountTask.get();
            List<Map<String, Object>> sixMonthRenewListMap = sixMonthRenewTotalCountTask.get();

            Map<String, Object> oneWeekInstalMap = null;
            if (oneWeekInstalListMap != null) {
                oneWeekInstalMap = new HashMap<>();
                for (Map<String, Object> objectMap : oneWeekInstalListMap) {
                    oneWeekInstalMap.put(objectMap.get("dateTime").toString(), objectMap.get("count"));
                }
            }
            Map<String, Object> oneWeekRenewMap = null;
            if (oneWeekRenewListMap != null) {
                oneWeekRenewMap = new HashMap<>();
                for (Map<String, Object> objectMap : oneWeekRenewListMap) {
                    oneWeekRenewMap.put(objectMap.get("dateTime").toString(), objectMap.get("count"));
                }
            }
            Map<String, Object> egithWeekInstalMap = null;
            if (eightWeekInstalListMap != null) {
                egithWeekInstalMap = new HashMap<>();
                for (Map<String, Object> objectMap : eightWeekInstalListMap) {
                    egithWeekInstalMap.put(objectMap.get("dateTime").toString(), objectMap.get("count"));
                }
            }
            Map<String, Object> egithWeekRenewMap = null;
            if (eightWeekRenewListMap != null) {
                egithWeekRenewMap = new HashMap<>();
                for (Map<String, Object> objectMap : eightWeekRenewListMap) {
                    egithWeekRenewMap.put(objectMap.get("dateTime").toString(), objectMap.get("count"));
                }
            }
            Map<String, Object> sixMonthInstalMap = null;
            if (sixMonthInstalListMap != null) {
                sixMonthInstalMap = new HashMap<>();
                for (Map<String, Object> objectMap : sixMonthInstalListMap) {
                    sixMonthInstalMap.put(objectMap.get("dateTime").toString(), objectMap.get("count"));
                }
            }
            Map<String, Object> sixMonthRenewMap = null;
            if (sixMonthRenewListMap != null) {
                sixMonthRenewMap = new HashMap<>();
                for (Map<String, Object> objectMap : sixMonthRenewListMap) {
                    sixMonthRenewMap.put(objectMap.get("dateTime").toString(), objectMap.get("count"));
                }
            }

            returnMap.put("oneWeekInstalCount", oneWeekInstalListMap);
            returnMap.put("oneWeekRenewCount", oneWeekRenewListMap);
            returnMap.put("eightWeekInstalCount", eightWeekInstalListMap);
            returnMap.put("eightWeekRenewCount", eightWeekRenewListMap);
            returnMap.put("sixMonthInstalCount", sixMonthInstalListMap);
            returnMap.put("sixMonthRenewCount", sixMonthRenewListMap);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new NotFoundException("查询出错，请稍后重试。");
        }
        return returnMap;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
    public void updateWaterDeviceForEngineer(List<WaterDeviceDTO> wdds) {
        if (CollectionUtil.isNotEmpty(wdds)) {
            for (WaterDeviceDTO wdd : wdds) {
                waterDeviceMapper.updateWaterDeviceForEngineer(wdd);
            }
        }
    }

    @Override
    public PageVO<WaterDeviceReplaceRecordDTO> getWaterDeviceReplaceBySn(String sn, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        Page<WaterDeviceReplaceRecordDTO> page = waterDeviceMapper.getWaterDeviceReplaceBySn(sn);
        return new PageVO<>(pageNum, page);
    }

    @Override
    public DeviceGeneralSituationVO getStationWaterDeviceGeneralChart(List<Integer> engineerIds) {
        DeviceGeneralSituationVO vo = new DeviceGeneralSituationVO();
        try {
            Map<String, Object> queryMap = new HashMap<String, Object>();
            queryMap.put("online", 0);
            queryMap.put("engineerIds", engineerIds);
            //离线数量
            Integer offlineTotal = waterDeviceMapper.stationOnlineTotalCount(queryMap);
            vo.setOfflineTotal(offlineTotal);
            // 在线数量
            queryMap.put("online", 1);
            Integer onlineTotal = waterDeviceMapper.stationOnlineTotalCount(queryMap);
            vo.setOnlineTotal(onlineTotal);

            //设备型号统计
            Callable<List<Map<String, Object>>> modelCountCallable = () -> waterDeviceMapper.stationModelTotalCount(engineerIds);
            FutureTask<List<Map<String, Object>>> modelCountTask = new FutureTask<>(modelCountCallable);
            ThreadUtil.executor.submit(modelCountTask);

            List<Map<String, Object>> modelCountMap = modelCountTask.get();
            Map<String, Object> modelMap = null;
            if (modelCountMap != null) {
                modelMap = new HashMap<>();
                for (Map<String, Object> objectMap : modelCountMap) {
                    if (objectMap.get("deviceModel") != null) {
                        modelMap.put(objectMap.get("deviceModel").toString(), objectMap.get("total"));
                    }
                }
                vo.setModelCount(modelMap);
            }

            // 计费类型统计
            Callable<List<Map<String, Object>>> costTotalCallable = () -> waterDeviceMapper.stationCostTotalCount(engineerIds);
            FutureTask<List<Map<String, Object>>> costTotalTask = new FutureTask<>(costTotalCallable);
            ThreadUtil.executor.submit(costTotalTask);

            List<Map<String, Object>> costTotalMap = costTotalTask.get();
            Map<String, Object> costlMap = null;
            if (costTotalMap != null) {
                costlMap = new HashMap<>();
                for (Map<String, Object> objectMap : costTotalMap) {
                    if (objectMap.get("costName") != null) {
                        costlMap.put(objectMap.get("costName").toString(), objectMap.get("costCount"));
                    }
                }
                vo.setCostTotal(costlMap);
            }
            // 所有设备续费状态统计
            Callable<List<Map<String, Object>>> allWaterDeviceRenewTotalCountCallable = () -> waterDeviceMapper.stationAllWaterDeviceRenewTotalCount(engineerIds);
            FutureTask<List<Map<String, Object>>> allWaterDeviceRenewTotalCountTask = new FutureTask<>(allWaterDeviceRenewTotalCountCallable);
            ThreadUtil.executor.submit(allWaterDeviceRenewTotalCountTask);

            List<Map<String, Object>> allWaterDeviceRenewTotalCountMap = allWaterDeviceRenewTotalCountTask.get();
            Map<String, Object> allWaterDeviceRenewTotalCountTempMap = null;
            if (allWaterDeviceRenewTotalCountMap != null) {
                allWaterDeviceRenewTotalCountTempMap = new HashMap<>();
                for (Map<String, Object> objectMap : allWaterDeviceRenewTotalCountMap) {
                    if (objectMap.get("renewStatusText") != null) {
                        allWaterDeviceRenewTotalCountTempMap.put(objectMap.get("renewStatusText").toString(), objectMap.get("total"));
                    }
                }
                Long notRenewNum = 0l;
                Long awaitRenewNum = 0l;
                if (allWaterDeviceRenewTotalCountTempMap.get("未续费") != null) {
                    log.info(allWaterDeviceRenewTotalCountTempMap.get("未续费") + "");
                    notRenewNum = (Long) allWaterDeviceRenewTotalCountTempMap.get("未续费");
                    allWaterDeviceRenewTotalCountTempMap.remove("未续费");
                }
                if (allWaterDeviceRenewTotalCountTempMap.get("待续费") != null) {
                    awaitRenewNum = (Long) allWaterDeviceRenewTotalCountTempMap.get("待续费");
                    allWaterDeviceRenewTotalCountTempMap.remove("待续费");
                }
                if ((notRenewNum + awaitRenewNum) != 0) {
                    allWaterDeviceRenewTotalCountTempMap.put("应续费", notRenewNum + awaitRenewNum);
                }
                vo.setAllWaterDeviceRenewTotal(allWaterDeviceRenewTotalCountTempMap);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new NotFoundException("查询出错，请稍后重试。");
        }
        return vo;
    }

    /**
     * 设备概况统计
     *
     * @return UserOverviewDTO
     * @author hhf
     * @date 2019/3/19
     */
    @Override
    public WaterDeviceOverviewDTO waterDeviceOverview() {
        WaterDeviceOverviewDTO overview = new WaterDeviceOverviewDTO();
        try {
            // 设备总数
            Callable<Integer> deviceTotalCountCallable = () -> waterDeviceMapper.deviceTotalCount(0);
            FutureTask<Integer> deviceTotalCountTask = new FutureTask<>(deviceTotalCountCallable);
            ThreadUtil.executor.submit(deviceTotalCountTask);
            // 昨日新增设备总数
            Callable<Integer> yesterdayTotalCountCallable = () -> waterDeviceMapper.deviceTotalCount(1);
            FutureTask<Integer> yesterdayTotalCountTask = new FutureTask<>(yesterdayTotalCountCallable);
            ThreadUtil.executor.submit(yesterdayTotalCountTask);

            overview.setDeviceTotalCount(deviceTotalCountTask.get());
            overview.setYesterdayDeviceTotalCount(yesterdayTotalCountTask.get());

            // 续费设备总数
            Callable<Integer> renewTotalCountCallable = () -> waterDeviceMapper.renewTotalCount(0);
            FutureTask<Integer> renewTotalCountTask = new FutureTask<>(renewTotalCountCallable);
            ThreadUtil.executor.submit(renewTotalCountTask);
            // 昨日新增续费设备总数
            Callable<Integer> yesterdayRenewTotalCountCallable = () -> waterDeviceMapper.renewTotalCount(1);
            FutureTask<Integer> yesterdayRenewTotalCountTask = new FutureTask<>(yesterdayRenewTotalCountCallable);
            ThreadUtil.executor.submit(yesterdayRenewTotalCountTask);

            overview.setRenewTotalCount(renewTotalCountTask.get());
            overview.setYesterdayRenewTotalCount(yesterdayRenewTotalCountTask.get());

            // 首年销售总额
            Callable<BigDecimal> saleTotalCallable = () -> waterDeviceMapper.deviceTotal(0);
            FutureTask<BigDecimal> saleTotalTask = new FutureTask<>(saleTotalCallable);
            ThreadUtil.executor.submit(saleTotalTask);
            // 昨日新增销售总额
            Callable<BigDecimal> yesterdaySaleTotalCallable = () -> waterDeviceMapper.deviceTotal(1);
            FutureTask<BigDecimal> yesterdaySaleTotalTask = new FutureTask<>(yesterdaySaleTotalCallable);
            ThreadUtil.executor.submit(yesterdaySaleTotalTask);

            overview.setSaleTotal(saleTotalTask.get());
            overview.setYesterdaySaleTotal(yesterdaySaleTotalTask.get());

            // 续费销售总额
            Callable<BigDecimal> renewSaleTotalCallable = () -> waterDeviceMapper.renewDeviceTotal(0);
            FutureTask<BigDecimal> renewSaleTotalTask = new FutureTask<>(renewSaleTotalCallable);
            ThreadUtil.executor.submit(renewSaleTotalTask);
            // 昨日续费销售总额
            Callable<BigDecimal> yesterdayRenewSaleTotalCallable = () -> waterDeviceMapper.renewDeviceTotal(1);
            FutureTask<BigDecimal> yesterdayRenewSaleTotalTask = new FutureTask<>(yesterdayRenewSaleTotalCallable);
            ThreadUtil.executor.submit(yesterdayRenewSaleTotalTask);

            overview.setRenewTotal(renewSaleTotalTask.get());
            overview.setYesterdayRenewTotal(yesterdayRenewSaleTotalTask.get());

            // 离线数量
            Callable<Integer> offlineTotalCallable = () -> waterDeviceMapper.onlineTotalCount(0);
            FutureTask<Integer> offlineTotalTask = new FutureTask<>(offlineTotalCallable);
            ThreadUtil.executor.submit(offlineTotalTask);
            // 在线数量
            Callable<Integer> onlineTotalCallable = () -> waterDeviceMapper.onlineTotalCount(1);
            FutureTask<Integer> onlineTotalTask = new FutureTask<>(onlineTotalCallable);
            ThreadUtil.executor.submit(onlineTotalTask);

            overview.setOnlineTotal(onlineTotalTask.get());
            overview.setOfflineTotal(offlineTotalTask.get());

            // 设备型号统计
            Callable<List<Map<String, Object>>> modelCountCallable = () -> waterDeviceMapper.modelTotalCount();
            FutureTask<List<Map<String, Object>>> modelCountTask = new FutureTask<>(modelCountCallable);
            ThreadUtil.executor.submit(modelCountTask);

            List<Map<String, Object>> modelCountMap = modelCountTask.get();
            Map<String, Object> modelMap = null;
            if (modelCountMap != null) {
                modelMap = new HashMap<>();
                for (Map<String, Object> objectMap : modelCountMap) {
                    if (objectMap.get("deviceModel") != null) {
                        modelMap.put(objectMap.get("deviceModel").toString(), objectMap.get("total"));
                    }
                }
                overview.setModelCount(modelMap);
            }

            // 计费类型统计
            Callable<List<Map<String, Object>>> costTotalCallable = () -> waterDeviceMapper.costTotalCount();
            FutureTask<List<Map<String, Object>>> costTotalTask = new FutureTask<>(costTotalCallable);
            ThreadUtil.executor.submit(costTotalTask);

            List<Map<String, Object>> costTotalMap = costTotalTask.get();
            Map<String, Object> costlMap = null;
            if (costTotalMap != null) {
                costlMap = new HashMap<>();
                for (Map<String, Object> objectMap : costTotalMap) {
                    if (objectMap.get("costName") != null) {
                        costlMap.put(objectMap.get("costName").toString(), objectMap.get("costCount"));
                    }
                }
                overview.setCostTotal(costlMap);
            }

            // 按省份首年设备统计
            Callable<List<Map<String, Object>>> provinceInstalTotalCallable = () -> waterDeviceMapper.provinceWaterDeviceTotalCount(0);
            FutureTask<List<Map<String, Object>>> provinceInstalTotalTask = new FutureTask<>(provinceInstalTotalCallable);
            ThreadUtil.executor.submit(provinceInstalTotalTask);

            List<Map<String, Object>> provinceInstalTotalMap = provinceInstalTotalTask.get();
            Map<String, Object> provinceInstalTotalTempMap = null;
            if (provinceInstalTotalMap != null) {
                provinceInstalTotalTempMap = new HashMap<>();
                for (Map<String, Object> objectMap : provinceInstalTotalMap) {
                    if (objectMap.get("province") != null) {
                        provinceInstalTotalTempMap.put(objectMap.get("province").toString(), objectMap.get("total"));
                    }
                }
                overview.setInstalProvinceTotal(provinceInstalTotalTempMap);
            }

            // 按省份续费设备统计
            Callable<List<Map<String, Object>>> provinceRenewTotalCallable = () -> waterDeviceMapper.provinceWaterDeviceTotalCount(1);
            FutureTask<List<Map<String, Object>>> provinceRenewTotalTask = new FutureTask<>(provinceRenewTotalCallable);
            ThreadUtil.executor.submit(provinceRenewTotalTask);

            List<Map<String, Object>> provinceRenewTotalMap = provinceRenewTotalTask.get();
            Map<String, Object> provinceRenewTTotalTempMap = null;
            if (provinceRenewTotalMap != null) {
                provinceRenewTTotalTempMap = new HashMap<>();
                for (Map<String, Object> objectMap : provinceRenewTotalMap) {
                    if (objectMap.get("province") != null) {
                        provinceRenewTTotalTempMap.put(objectMap.get("province").toString(), objectMap.get("total"));
                    }
                }
                overview.setRenewProvinceTotal(provinceRenewTTotalTempMap);
            }

            // 所有设备续费状态统计
            Callable<List<Map<String, Object>>> allWaterDeviceRenewTotalCountCallable = () -> waterDeviceMapper.allWaterDeviceRenewTotalCount();
            FutureTask<List<Map<String, Object>>> allWaterDeviceRenewTotalCountTask = new FutureTask<>(allWaterDeviceRenewTotalCountCallable);
            ThreadUtil.executor.submit(allWaterDeviceRenewTotalCountTask);

            List<Map<String, Object>> allWaterDeviceRenewTotalCountMap = allWaterDeviceRenewTotalCountTask.get();
            Map<String, Object> allWaterDeviceRenewTotalCountTempMap = null;
            if (allWaterDeviceRenewTotalCountMap != null) {
                allWaterDeviceRenewTotalCountTempMap = new HashMap<>();
                for (Map<String, Object> objectMap : allWaterDeviceRenewTotalCountMap) {
                    if (objectMap.get("renewStatusText") != null) {
                        allWaterDeviceRenewTotalCountTempMap.put(objectMap.get("renewStatusText").toString(), objectMap.get("total"));
                    }
                }
                overview.setAllWaterDeviceRenewTotal(allWaterDeviceRenewTotalCountTempMap);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new NotFoundException("查询出错，请稍后重试。");
        }
        return overview;
    }

    /**
     * 根据条件查询设备数量
     *
     * @param query 条件
     */
    @Override
    public int countDevice(DeviceQuery query) {
        //省市区范围
        List<Map<String, String>> pcrs = query.getPcr();
        List<ProvinceCityRegion> list = null;
        if (CollectionUtil.isNotEmpty(pcrs)) {
            int size = pcrs.size();
            list = new ArrayList<>(size);
            for (int i = 0; i < size; i++) {
                Map<String, String> pcrMap = pcrs.get(i);
                ProvinceCityRegion pcr = new ProvinceCityRegion();
                pcr.setProvince(pcrMap.get("province"));
                pcr.setCity(pcrMap.get("city"));
                pcr.setRegion(pcrMap.get("region"));
                list.add(pcr);
            }
        }
        //最后在线时间范围-开始时间
        Date beginTime = query.getLastOnlineBeginTime();
        //最后在线时间范围-结束时间
        Date endTime = query.getLastOnlineEndTime();
        //设备网络类型范围
        Integer connType = query.getConnectionType();
        //型号范围
        List<String> models = query.getModels();
        //是否在线
        Boolean online = query.getOnline();
        //位置标签
        String location = query.getLocation();
        //设备编码
        String sn = query.getSnCode();
        //关键词模糊查询，address
        String keyWord = query.getKeyWord();
        //创建时间
        Date createTime = query.getCreateTime();

        return waterDeviceMapper.countDevice(list, beginTime, endTime, connType, models, online, location, sn, keyWord, createTime);
    }

    @Override
    public PageVO<WaterDeviceVO> pageWaterDevice(Integer pageNum, Integer pageSize, WaterDeviceQuery query) {
        PageHelper.startPage(pageNum, pageSize);
        Page<WaterDeviceVO> page = waterDeviceMapper.selectWaterDeviceVO(query);
        return new PageVO<>(pageNum, page);
    }

    /**
     * 续费之后，扣费计划添加
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
    public void renewProcessor(WaterDeviceDTO dto) {
        log.info("续费之后，扣费计划添加------开始执行");
        long l1 = System.currentTimeMillis();
        WaterDevice device = waterDeviceMapper.selectByPrimaryKey(dto.getId());
        if (device == null) {
            log.error("续费回调处理，获取设备信息失败，SN=" + dto.getSn());
            return;
        }
        //更新设备续费信息
        WaterDevice updateDevice = new WaterDevice();
        updateDevice.setId(dto.getId());
        updateDevice.setMoney(dto.getMoney());
        updateDevice.setLastRenewTime(dto.getLastRenewTime());
        updateDevice.setRenewTimes(dto.getRenewTimes());
        updateDevice.setRenewStatus(dto.getRenewStatus());
        updateDevice.setRenewStatusText(dto.getRenewStatusText());
        waterDeviceMapper.updateByPrimaryKeySelective(updateDevice);

        long l2 = System.currentTimeMillis();
        log.info("续费之后，扣费计划添加------执行01，耗时：{}", l2 - l1);

        //修改设备故障状态
        this.resolveDeviceFaultForRenew(dto.getId(), dto.getSn());

        l2 = System.currentTimeMillis();
        log.info("续费之后，扣费计划添加------执行02，耗时：{}", l2 - l1);

        // //续费扣费计划添加
        // DeductionPlan lastPlan = deductionPlanService.getLastDeductionPlan(device.getId());
        // int sorts = 1;
        // if (lastPlan != null) {
        //     sorts = lastPlan.getSorts() + 1;
        // }
        // ProductCostDTO cost = productFeign.getProductCostById(dto.getCostId());
        // if (cost == null) {
        //     log.error("续费回调处理，获取计费方式信息失败，costId=" + dto.getCostId());
        //     return;
        // }
        // //添加新的续费扣费计划
        // DeductionPlan newPlan = new DeductionPlan();
        // newPlan.setDeviceId(device.getId());
        // newPlan.setSnCode(device.getSn());
        // newPlan.setDeductionsType(cost.getType());
        // //阀值
        // newPlan.setThreshold(cost.getThreshold1());
        // newPlan.setCostId(cost.getId());
        // newPlan.setUnitMoney(cost.getUnitPrice());
        // newPlan.setCreateTime(new Date());
        // newPlan.setSorts(sorts);
        // newPlan.setStatus(DeductionPlanStatus.TO_BE_USED.value);
        // newPlan.setInitMoney(cost.getTotalFee());
        // newPlan.setUsedMoney(new BigDecimal(0));
        // deductionPlanService.save(newPlan);

        //续费扣费计划添加
        List<DeductionPlan> planList = deductionPlanService.listByDeviceId(device.getId());
        DeductionPlan lastPlan;
        if (CollectionUtil.isEmpty(planList)) {
            lastPlan = deductionPlanService.caculate(device);
        } else {
            lastPlan = planList.get(planList.size() - 1);
        }

        l2 = System.currentTimeMillis();
        log.info("续费之后，扣费计划添加------执行03，耗时：{}", l2 - l1);

        //续费的计费方式
        ProductCostDTO cost = productFeignHandler.getProductCostById(dto.getCostId());

        l2 = System.currentTimeMillis();
        log.info("续费之后，扣费计划添加------执行04，耗时：{}", l2 - l1);

        //添加新的续费扣费计划
        DeductionPlan newPlan = new DeductionPlan();
        newPlan.setDeviceId(device.getId());
        newPlan.setSnCode(device.getSn());
        newPlan.setDeductionsType(cost.getType());
        //阀值
        newPlan.setThreshold(cost.getThreshold1());
        // if (ProductCostTypeEnum.isFlow(cost.getType())) {
        //     newPlan.setFirstDayFlow(cost.getValue());
        // }
        newPlan.setFirstDayMoney(cost.getRentalFee());
        newPlan.setCostId(cost.getId());
        newPlan.setUnitMoney(cost.getUnitPrice());
        newPlan.setDeductionsNum(cost.getValue());
        Date startTime = null;
        Date now = new Date();
        int sorts;
        if (lastPlan != null) {
            Date endTime = lastPlan.getEndTime();
            if (endTime != null && endTime.before(now)) {
                startTime = now;
            }
            sorts = lastPlan.getSorts() + 1;
        } else {
            startTime = now;
            sorts = 1;
        }
        if (startTime != null) {
            newPlan.setStartTime(startTime);
            if (ProductCostTypeEnum.isTime(cost.getType())) {
                newPlan.setEndTime(DateUtil.dayAfter(startTime, cost.getValue()));
            }
        }
        newPlan.setCreateTime(now);
        newPlan.setSorts(sorts);
        newPlan.setCostChanged(false);
        newPlan.setStatus(1);
        deductionPlanService.save(newPlan);

        l2 = System.currentTimeMillis();
        log.info("续费之后，扣费计划添加------结束执行，耗时：{}", l2 - l1);
    }

    /**
     * 解除设备故障
     *
     * @param deviceId 设备ID
     * @param sn       SN码
     */
    private void resolveDeviceFaultForRenew(Integer deviceId, String sn) {
        //解除余额不足故障
        WaterDeviceFaultDTO deviceFault = new WaterDeviceFaultDTO();
        deviceFault.setDeviceId(deviceId);
        deviceFault.setSn(sn);
        deviceFault.setType(DeviceFaultType.MONEY.value);
        deviceFault.setState(DeviceFaultState.RESOLVE.value);
        rabbitTemplate.convertAndSend(RabbitConstant.DEVICE_FAULT, deviceFault);

        //解除阈值提醒故障
        deviceFault.setType(DeviceFaultType.NOTICE.value);
        rabbitTemplate.convertAndSend(RabbitConstant.DEVICE_FAULT, deviceFault);

        //解除续费超期故障
        deviceFault.setType(DeviceFaultType.OVERDUE.value);
        rabbitTemplate.convertAndSend(RabbitConstant.DEVICE_FAULT, deviceFault);
    }

    @Override
    public void updateForPadSyncData(WaterDevice update) {
        waterDeviceMapper.updateForPadSyncData(update);
    }

    /***
     * 同步售后水机续费订单
     * @param renew
     * @param device
     * @param newCost
     * @param wo
     * @param subOrderDetail
     */
    private void afterWaterDeviceRenew(OrderRenewDTO renew, WaterDevice device, ProductCostDTO newCost, MultipartFile[] files) {
        try {
            Map<String, Object> reqMap = initRenewOrderSyncData(renew, device, newCost, files);
            log.info("调用售后系统接口请求参数：{}", JSONObject.toJSON(reqMap));
            if (!BaideApiUtil.success(BaideApiUtil.renewOrderSync(reqMap))) {
                log.error("创建续费订单失败，同步售后水机续费订单失败--workOrderId={}", device.getWorkOrderId());
                throw new YimaoException("创建续费订单失败，同步售后水机续费订单失败");
            }
        } catch (Exception e) {
            log.error("创建续费订单失败，同步售后水机续费订单失败--workOrderId={}", device.getWorkOrderId() + "异常信息:" + e.getMessage());
            throw new YimaoException("创建续费订单失败，同步售后水机续费订单失败");
        }

    }

    /***
     * 组装请求数据
     * @param renewOrder
     * @param device
     * @param wo
     * @param subOrderDetail
     * @param device
     * @param newCost
     * @param newCost
     * @param files
     * @return
     */
    private Map<String, Object> initRenewOrderSyncData(OrderRenewDTO renewOrder, WaterDevice device, ProductCostDTO newCost, MultipartFile[] files) {
        if (device == null) {
            log.error("创建续费订单失败，没有获取到设备信息--workOrderId={}", renewOrder.getDeviceId());
            throw new YimaoException("创建续费订单失败，没有获取到设备或计费信息");
        }
        WorkOrderDTO wo = orderFeign.getWorkOrderById(device.getWorkOrderId());
        if (newCost == null) {
            log.error("创建续费订单失败，没有获取到设备或计费信息--workOrderId={}", renewOrder.getDeviceId());
            throw new YimaoException("创建续费订单失败，没有获取到设备或计费信息");
        }
        Integer times = orderFeign.getCurrentRenewTimes(device.getId());//获取设备续费次数
        Map<String, Object> params = new HashMap<>();
        params.put("renewOrderId", renewOrder.getId());
        params.put("workOrderId", wo.getId());
        params.put("otherPayVerifyRejectReason", "");//审核不通过原因,此处不赋值
        params.put("renewStatus", "2");//售后系统续费状态：1-已续费2-待续费	3-到期未续费
        params.put("money", device.getMoney().doubleValue());//设备余额
        params.put("moneyTime", DateUtil.dateToString(device.getLastRenewTime()));
        params.put("provinceName", device.getProvince());
        params.put("cityName", device.getCity());
        params.put("areaName", device.getRegion());
        params.put("costId", newCost.getOldId() + "");
        params.put("costName", newCost.getName());
        params.put("lastCostId", device.getOldCostId());
        params.put("lastCostName", device.getCostName());
        params.put("sncode", device.getSn());
        params.put("batchCode", wo.getLogisticsCode());
        params.put("iccid", device.getIccid());
        params.put("productModel", YunOldIdUtil.getProductId(device.getDeviceModel()));
        params.put("productModelName", YunOldIdUtil.getProductName(device.getDeviceModel()));
        params.put("activeTimeD", DateUtil.dateToString(wo.getCompleteTime()));//设备安装时间
        params.put("payTerminal", "2");// 续费端(支付端) (1-设备端,2-后台)
        params.put("payMoney", renewOrder.getAmountFee().doubleValue());//续费时要支付的是租赁费（不含安装费）
        params.put("renewTimeD", DateUtil.dateToString(renewOrder.getPayTime()));//续费时间
        params.put("payCount", (times + 1) + "");//续费次数
        params.put("payTimeD", DateUtil.dateToString(renewOrder.getPayTime()));//支付时间
        params.put("payType", renewOrder.getPayType() + "");//支付 方式
        params.put("tradeNo", renewOrder.getTradeNo() + "");// 交易单号
        params.put("userName", wo.getUserName());//水机用户姓名
        params.put("userPhone", wo.getUserPhone());
        params.put("engineerId", wo.getOldEngineerId());
        params.put("engineerName", wo.getEngineerName());
        params.put("engineerServiceSite", renewOrder.getEngineerStationName());
        params.put("engineerPhone", wo.getEngineerPhone());
        params.put("distributorName", wo.getDistributorName());
        params.put("distributorPhone", wo.getDistributorPhone());
        params.put("distributorAddress", wo.getDistributorProvince() + "-" + wo.getDistributorCity() + "-" + wo.getDistributorRegion());//// 经销商归属地(省-市-区格式)
        params.put("distributorLoginName", wo.getDistributorAccount());//经销商登录名
        params.put("distributorIdNo", wo.getDistributorIdCard());//经销商身份证号
        params.put("distributorServiceiceSiteName", renewOrder.getDistributorStationName());
        params.put("distributorRefereeName", renewOrder.getDistributorRecommendName());//经销商推荐人
		/*if(!StringUtil.isEmpty(subOrderDetail.getRecommendProvince())&&!StringUtil.isEmpty(subOrderDetail.getRecommendCity())
				&&!StringUtil.isEmpty(subOrderDetail.getRecommendRegion())){
			params.put("distributorRefereeAddress", subOrderDetail.getRecommendProvince()+"-"+subOrderDetail.getRecommendCity()+"-"+subOrderDetail.getRecommendRegion());// 经销商推荐人归属地(省-市-区格式)
			StationCompanyDTO station=systemFeign.getStationCompanyByPCR(subOrderDetail.getRecommendProvince(),subOrderDetail.getRecommendCity(),subOrderDetail.getRecommendRegion());
		    if(station !=null){
		          params.put("distributorRefereeServiceiceSiteName", station.getName());//推荐人服务站名称
		    }
		}*/
        String payImage = null;
        try {
            payImage = BaideApiUtil.pictureEncodeData(files);
        } catch (Exception e) {
        }
        params.put("otherPayVerifyState", "0");// 售后系统审核状态(0-审核中、1-审核已通过、2-审核未通过 )
        params.put("payImage", payImage);//支付凭证
        params.put("invoice", "false");
        return params;
    }

    /**
     * 根据计算的金额.核验设备的续费状态
     */
    @Override
    public void checkDeviceRenewTypeAndPutMir(WaterDevice device, BigDecimal money, WorkOrderDTO workOrder) {
        if (workOrder != null && workOrder.getPayTime() == null) {
            log.info("设备:" + device.getSn() + "未支付,不执行续费状态校验");
            return;
        }
        log.info("设备:" + device.getSn() + "开始执行续费状态校验, 余额为: " + money + " $");

        WaterDeviceRenewConfig renewConfig = waterDeviceRenewConfigMapper.selectByType(WaterDeviceRenewStatus.WAIT.value);

        WaterDevice update = new WaterDevice();
        update.setId(device.getId());
        //记录余额为0的时间
        Date arrearsDate = device.getArrearsTime();
        if (money.compareTo(renewConfig.getBalanceAfter()) <= 0) {
            Integer postponeDay = renewConfig.getPostponeDay();
            if (arrearsDate == null) {
                //设备金额初次低于警戒线下.记录欠费时间
                update.setArrearsTime(new Date());

                if (postponeDay != null && postponeDay > 0) {
                    update.setRenewStatus(WaterDeviceRenewStatus.WAIT.value);
                    update.setRenewStatusText(WaterDeviceRenewStatus.WAIT.name);
                    waterDeviceMapper.updateByPrimaryKeySelective(update);
                    return;
                }
                arrearsDate = new Date();
            }
            //判断延期天数

            //欠费时间 + 延期的天数
            Date arrangeTime = DateUtil.dayAfter(arrearsDate, postponeDay);
            if (arrangeTime.getTime() > System.currentTimeMillis()) {
                //欠费时间+延期时间 . 当前时间在边界线内,待续费
                update.setRenewStatus(WaterDeviceRenewStatus.WAIT.value);
                update.setRenewStatusText(WaterDeviceRenewStatus.WAIT.name);
                waterDeviceMapper.updateByPrimaryKeySelective(update);
            } else {
                //未续费,需要推送超期未续费推送
                update.setRenewStatus(WaterDeviceRenewStatus.NOTYET.value);
                update.setRenewStatusText(WaterDeviceRenewStatus.NOTYET.name);
                waterDeviceMapper.updateByPrimaryKeySelective(update);

                //消息推送提醒
                boolean exists = waterDeviceFaultService.existsWith(device.getId(), device.getSn(), DeviceFaultType.OVERDUE.value, null);
                if (!exists) {
                    //发送短信给用户
                    SmsMessageDTO smsMessage = new SmsMessageDTO();
                    smsMessage.setType(MessageModelTypeEnum.WARM_PUSH.value);
                    smsMessage.setPushObject(MessagePushObjectEnum.WATER_USER.value);
                    smsMessage.setPhone(device.getDeviceUserPhone());
                    smsMessage.setMechanism(MessageMechanismEnum.NO_RENEWAL_OF_THE_PERIOD.value);
                    smsMessage.setPushMode(MessagePushModeEnum.YIMAO_SMS.value);
                    Map<String, String> messageMap = new HashMap<>();
                    messageMap.put("#code#", device.getSn());
                    messageMap.put("#code1#", device.getEngineerName());
                    messageMap.put("#code2#", device.getEngineerPhone());
                    smsMessage.setContentMap(messageMap);
                    rabbitTemplate.convertAndSend(RabbitConstant.SMS_MESSAGE_PUSH, smsMessage);

                    //1-推送给安装工
                    EngineerDTO engineer = userFeign.getEngineerBasicInfoByIdForMsgPushInfo(device.getEngineerId());

                    //水机更换位置信息查询,如果更换了，更换上的地址,否则取水机上的地址
                    String address = "";
                    if (!StringUtil.isEmpty(device.getSn())) {
                        WaterDevicePlaceChangeRecordDTO placeChangeRecord = waterDevicePlaceChangeRecordService.getBySn(device.getSn());
                        if (null != placeChangeRecord && !StringUtil.isEmpty(placeChangeRecord.getDetailAddress())) {
                            address = placeChangeRecord.getNewPlace() + placeChangeRecord.getDetailAddress();
                        } else {
                            address = device.getProvince() + device.getCity() + device.getRegion() + device.getAddress();
                        }
                    }

                    if (engineer != null) {
                        Map<String, String> appMessage = new HashMap<>();
                        appMessage.put("#code#", device.getSn());
                        appMessage.put("#code1#", device.getDeviceUserName());
                        appMessage.put("#code2#", device.getDeviceUserPhone());
                        appMessage.put("#code3#", address);
                        padApiService.pushMsgToApp(device, MessageModelTypeEnum.WARM_PUSH.value, MessageMechanismEnum.NO_RENEWAL_OF_THE_PERIOD.value, MessagePushModeEnum.YIMAO_ENGINEER_APP_NOTICE.value, MessageModelTypeEnum.WARM_PUSH.name, engineer.getId(), engineer.getRealName(), engineer.getAppType(), appMessage, null);

                    }
                    //1-推送给经销商
                    DistributorDTO distributor = userFeign.getDistributorBasicInfoByIdForMsgPushInfo(device.getDistributorId());
                    if (distributor != null) {
                        Map<String, String> distributorMessage = new HashMap<>();
                        distributorMessage.put("#code#", device.getSn());
                        distributorMessage.put("#code1#", device.getDeviceUserName());
                        distributorMessage.put("#code2#", device.getDeviceUserPhone());
                        distributorMessage.put("#code3#", device.getEngineerName());
                        distributorMessage.put("#code4#", device.getEngineerPhone());
                        padApiService.pushMsgToApp(device, MessageModelTypeEnum.WARM_PUSH.value, MessageMechanismEnum.NO_RENEWAL_OF_THE_PERIOD.value, MessagePushModeEnum.YIMAO_APP_NOTICE.value, MessageModelTypeEnum.WARM_PUSH.name, distributor.getUserId(), distributor.getUserName(), distributor.getAppType(), distributorMessage, null);
                    }

                    //推送站务系统
                    //根据工单的省市区查询区域id
                    Integer areaId = redisCache.get(Constant.AREA_CACHE + workOrder.getProvince() + "_" + workOrder.getCity() + "_" + workOrder.getRegion(), Integer.class);
                    if (areaId == null) {
                        areaId = systemFeign.getRegionIdByPCR(workOrder.getProvince(), workOrder.getCity(), workOrder.getRegion());
                    }
                    if (engineer != null && Objects.nonNull(areaId)) {
                        Map<String, String> stationMessage = new HashMap<>();
                        stationMessage.put("#code#", device.getSn());
                        stationMessage.put("#code1#", device.getDeviceUserName());
                        stationMessage.put("#code2#", device.getDeviceUserPhone());
                        stationMessage.put("#code3#", engineer.getRealName());
                        stationMessage.put("#code4#", engineer.getPhone());
                        if (distributor != null) {
                            stationMessage.put("#code5#", distributor.getRealName());
                            stationMessage.put("#code6#", distributor.getPhone());
                        } else {
                            stationMessage.put("#code5#", "");
                            stationMessage.put("#code6#", "");
                        }

                        padApiService.pushMsgToStation(device, MessageModelTypeEnum.WARM_PUSH.value, MessageMechanismEnum.NO_RENEWAL_OF_THE_PERIOD.value, "超过期限未续费", areaId, stationMessage, null);
                    }


                }


                //添加水机续费超期故障信息
                WaterDeviceFaultDTO deviceFault = new WaterDeviceFaultDTO();
                deviceFault.setDeviceId(device.getId());
                deviceFault.setSn(device.getSn());
                deviceFault.setType(DeviceFaultType.OVERDUE.value);
                deviceFault.setFault(DeviceFaultType.OVERDUE.name);
                deviceFault.setState(DeviceFaultState.FAULT.value);
                rabbitTemplate.convertAndSend(RabbitConstant.DEVICE_FAULT, deviceFault);
            }
            return;
        }

        //余额大于0
        if (money.compareTo(new BigDecimal(0)) > 0 && device.getArrearsTime() != null) {
            waterDeviceMapper.updateArrearsTimeToNullById(device.getId());
            return;
        }

        if (money.compareTo(renewConfig.getBalanceBefore()) > 0) {
            //余额在待续费余额范围之外.判断是否续费过
            boolean isRenew = device.getRenewTimes() != null && device.getRenewTimes() > 0;
            if (isRenew) {
                //已续费,状态需变更为已续费
                update.setRenewStatus(WaterDeviceRenewStatus.ALREADY.value);
                update.setRenewStatusText(WaterDeviceRenewStatus.ALREADY.name);
                waterDeviceMapper.updateByPrimaryKeySelective(update);
            }
            return;
        }

        //处于待续费余额范围内.
        update.setRenewStatus(WaterDeviceRenewStatus.WAIT.value);
        update.setRenewStatusText(WaterDeviceRenewStatus.WAIT.name);
        waterDeviceMapper.updateByPrimaryKeySelective(update);
    }

    /**
     * 查询续费统计表格数据
     */
    @Override
    public RenewStatisticsDTO getWaterDeviceRenewData(StationWaterDeviceQuery waterDeviceQuery) {

        return waterDeviceMapper.getWaterDeviceRenewData(waterDeviceQuery);
    }


    /**
     * 站务系统-统计-续费统计-图表数据（新安装+应续费）
     * 这里应续费指代当前为止还未续费的水机
     *
     * @param waterDeviceQuery
     * @return
     */
    public List<RenewStatisticsDTO> getWaterDeviceRenewPicData(StationWaterDeviceQuery waterDeviceQuery) {
        return waterDeviceMapper.getWaterDeviceRenewPicData(waterDeviceQuery);
    }

    /**
     * 站务系统查询总设备数
     *
     * @param engineerIds
     * @return
     */
    public Integer getDeviceTotalNum(List<Integer> engineerIds) {

        return waterDeviceMapper.getDeviceTotalNum(engineerIds);
    }

    /**
     * 站务系统查询昨日新增设备数
     *
     * @param engineerIds
     * @return
     */
    @Override
    public Integer getYesterdayNewInstallNum(List<Integer> engineerIds) {

        return waterDeviceMapper.getYesterdayNewInstallNum(engineerIds);
    }

    @Override
    public List<SalesStatsDTO> getDeviceRenewPropList(List<Integer> ids) {
        List<SalesStatsDTO> result = waterDeviceMapper.getDeviceRenewPropList(ids);
        return completeData(result);
    }


    @Override
    public PageVO<WaterDeviceCompleteDTO> getCompleteWorkOrderList(Integer pageNum, Integer pageSize, String key, Integer engineerId) {
        PageHelper.startPage(pageNum, pageSize);
        Page<WaterDeviceCompleteDTO> page = waterDeviceMapper.getCompleteWorkOrderList(key,engineerId);
        return new PageVO<>(pageNum, page);
    }

    /***
     * 补全设备续费率数据
     * @param result
     * @return
     */
    private List<SalesStatsDTO> completeData(List<SalesStatsDTO> result) {
        NumberFormat numberFormat = NumberFormat.getInstance();
        // 设置精确到小数点后2位
        numberFormat.setMaximumFractionDigits(2);
        List<SalesStatsDTO> renewData = new ArrayList<SalesStatsDTO>();
        SalesStatsDTO ssd = new SalesStatsDTO();
        ssd.setRenewStatusName("新安装");
        ssd.setRenewNum(0);
        renewData.add(ssd);

        ssd = new SalesStatsDTO();
        ssd.setRenewStatusName("待续费");
        ssd.setRenewNum(0);
        renewData.add(ssd);

        ssd = new SalesStatsDTO();
        ssd.setRenewStatusName("已续费");
        ssd.setRenewNum(0);
        renewData.add(ssd);

        Integer total = 0;
        if (!result.isEmpty()) {
            for (SalesStatsDTO ss : renewData) {
                for (SalesStatsDTO rs : result) {
                    if (rs.getRenewStatusName().equals(ss.getRenewStatusName())) {
                        ss.setRenewNum(rs.getRenewNum());
                        total += rs.getRenewNum();
                        break;
                    }
                }
            }
        }
        //计算百分比
        if (total > 0) {
            for (SalesStatsDTO ss : renewData) {
                ss.setRenewProp(numberFormat.format((float) ss.getRenewNum() / (float) total * 100) + "%");
            }
        }
        return renewData;
    }

}
