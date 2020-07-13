package com.yimao.cloud.order.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.yimao.cloud.base.baideApi.utils.BaideApiUtil;
import com.yimao.cloud.base.constant.Constant;
import com.yimao.cloud.base.constant.RabbitConstant;
import com.yimao.cloud.base.enums.DistributorRoleLevel;
import com.yimao.cloud.base.enums.IncomeAllotType;
import com.yimao.cloud.base.enums.IncomeSubjectEnum;
import com.yimao.cloud.base.enums.IncomeType;
import com.yimao.cloud.base.enums.MessageMechanismEnum;
import com.yimao.cloud.base.enums.MessageModelTypeEnum;
import com.yimao.cloud.base.enums.MessagePushModeEnum;
import com.yimao.cloud.base.enums.MessagePushObjectEnum;
import com.yimao.cloud.base.enums.OrderCompleteStatusEnum;
import com.yimao.cloud.base.enums.PayType;
import com.yimao.cloud.base.enums.PermissionTypeEnum;
import com.yimao.cloud.base.enums.ProductCostTypeEnum;
import com.yimao.cloud.base.enums.RenewStatus;
import com.yimao.cloud.base.enums.StationAreaServiceTypeEnum;
import com.yimao.cloud.base.enums.UserType;
import com.yimao.cloud.base.enums.WaterDeviceRenewStatus;
import com.yimao.cloud.base.exception.BadRequestException;
import com.yimao.cloud.base.exception.NotFoundException;
import com.yimao.cloud.base.exception.YimaoException;
import com.yimao.cloud.base.mail.MailSender;
import com.yimao.cloud.base.properties.DomainProperties;
import com.yimao.cloud.base.utils.CollectionUtil;
import com.yimao.cloud.base.utils.DateUtil;
import com.yimao.cloud.base.utils.StringUtil;
import com.yimao.cloud.base.utils.yun.YunOldIdUtil;
import com.yimao.cloud.framework.cache.RedisCache;
import com.yimao.cloud.order.feign.ProductFeign;
import com.yimao.cloud.order.feign.SystemFeign;
import com.yimao.cloud.order.feign.UserFeign;
import com.yimao.cloud.order.feign.WaterFeign;
import com.yimao.cloud.order.mapper.IncomeSubjectMapper;
import com.yimao.cloud.order.mapper.OrderPayCheckMapper;
import com.yimao.cloud.order.mapper.OrderRenewMapper;
import com.yimao.cloud.order.mapper.ProductIncomeRecordMapper;
import com.yimao.cloud.order.mapper.ProductIncomeRecordPartMapper;
import com.yimao.cloud.order.mapper.SubOrderDetailMapper;
import com.yimao.cloud.order.mapper.WorkOrderMapper;
import com.yimao.cloud.order.po.IncomeSubject;
import com.yimao.cloud.order.po.OrderMain;
import com.yimao.cloud.order.po.OrderPayCheck;
import com.yimao.cloud.order.po.OrderRenew;
import com.yimao.cloud.order.po.ProductIncomeRecord;
import com.yimao.cloud.order.po.ProductIncomeRecordPart;
import com.yimao.cloud.order.po.SubOrderDetail;
import com.yimao.cloud.order.po.WorkOrder;
import com.yimao.cloud.order.service.IncomeRuleService;
import com.yimao.cloud.order.service.InstallerApiService;
import com.yimao.cloud.order.service.OrderMainService;
import com.yimao.cloud.order.service.OrderRenewService;
import com.yimao.cloud.order.service.OrderSubService;
import com.yimao.cloud.order.service.ProductIncomeRecordService;
import com.yimao.cloud.pojo.dto.order.AgentSalesOverviewDTO;
import com.yimao.cloud.pojo.dto.order.IncomeRuleDTO;
import com.yimao.cloud.pojo.dto.order.IncomeRulePartDTO;
import com.yimao.cloud.pojo.dto.order.OrderSubDTO;
import com.yimao.cloud.pojo.dto.order.RenewDTO;
import com.yimao.cloud.pojo.dto.order.SalesStatsDTO;
import com.yimao.cloud.pojo.dto.order.SalesStatsQueryDTO;
import com.yimao.cloud.pojo.dto.product.ProductCompanyDTO;
import com.yimao.cloud.pojo.dto.product.ProductCostDTO;
import com.yimao.cloud.pojo.dto.station.StationMessageDTO;
import com.yimao.cloud.pojo.dto.station.StationScheduleDTO;
import com.yimao.cloud.pojo.dto.system.AppMessageDTO;
import com.yimao.cloud.pojo.dto.system.SmsMessageDTO;
import com.yimao.cloud.pojo.dto.system.StationCompanyDTO;
import com.yimao.cloud.pojo.dto.system.StationDTO;
import com.yimao.cloud.pojo.dto.user.DistributorDTO;
import com.yimao.cloud.pojo.dto.user.EngineerDTO;
import com.yimao.cloud.pojo.dto.user.WaterDeviceUserDTO;
import com.yimao.cloud.pojo.dto.water.WaterDeviceDTO;
import com.yimao.cloud.pojo.dto.water.WaterDevicePlaceChangeRecordDTO;
import com.yimao.cloud.pojo.query.order.RenewOrderQuery;
import com.yimao.cloud.pojo.vo.PageVO;
import com.yimao.cloud.pojo.vo.order.OrderRenewVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author Zhang Bo
 * @date 2018/11/12.
 */
@Service
@Slf4j
public class OrderRenewServiceImpl implements OrderRenewService {

    @Resource
    private DomainProperties domainProperties;
    @Resource
    private MailSender mailSender;

    @Resource
    private OrderRenewMapper orderRenewMapper;
    @Resource
    private WorkOrderMapper workOrderMapper;
    @Resource
    private SubOrderDetailMapper subOrderDetailMapper;
    @Resource
    private OrderPayCheckMapper orderPayCheckMapper;
    @Resource
    private ProductIncomeRecordMapper productIncomeRecordMapper;
    @Resource
    private ProductIncomeRecordPartMapper productIncomeRecordPartMapper;
    @Resource
    private IncomeSubjectMapper incomeSubjectMapper;

    @Resource
    private ProductIncomeRecordService productIncomeRecordService;
    @Resource
    private IncomeRuleService incomeRuleService;

    @Resource
    private SystemFeign systemFeign;
    @Resource
    private WaterFeign waterFeign;
    @Resource
    private ProductFeign productFeign;
    @Resource
    private UserFeign userFeign;

    @Resource
    private RabbitTemplate rabbitTemplate;
    @Resource
    private OrderMainService orderMainService;

    @Resource
    private OrderSubService orderSubService;

    @Resource
    private InstallerApiService installerApiService;
    @Resource
    private RedisCache redisCache;

    /**
     * 创建续费工单
     *
     * @param renew 续费工单
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
    public void save(OrderRenew renew) {
        WorkOrder wo = workOrderMapper.selectWorkOrderByDeviceIdForRenewOrder(renew.getDeviceId());
        if (wo == null) {
            log.error("创建续费订单失败，没有获取到工单信息--snCode={}", renew.getSnCode());
            throw new YimaoException("创建续费订单失败，没有获取到工单信息");
        }

        //由于PAD端每次点击续费页面都会产品续费订单，所以先删除一下之前生成的但未支付的续费订单
        // OrderRenew delete = new OrderRenew();
        // delete.setWorkOrderId(wo.getId());
        // delete.setDeviceId(renew.getDeviceId());
        // delete.setCostId(renew.getCostId());
        // delete.setPay(false);
        // delete.setPayType(renew.getPayType());
        // delete.setPayTypeName(renew.getPayTypeName());
        // delete.setTerminal(PayTerminal.PAD.value);
        // delete.setTerminalName(PayTerminal.PAD.name);
        // orderRenewMapper.delete(delete);

        renew.setMainOrderId(wo.getMainOrderId());
        renew.setOrderId(wo.getSubOrderId());
        renew.setWorkOrderId(wo.getId());
        renew.setProvince(wo.getProvince());
        renew.setCity(wo.getCity());
        renew.setRegion(wo.getRegion());

        //续费订单经销商信息设置
        Integer distributorId = renew.getDistributorId();
        if (distributorId != null) {
            DistributorDTO distributor = userFeign.getDistributorBasicInfoById(distributorId);
            if (distributor != null) {
                renew.setDistributorId(distributor.getId());
                renew.setDistributorType(DistributorRoleLevel.getUserType(distributor.getRoleLevel()));
                renew.setDistributorTypeName(UserType.getNameByType(renew.getDistributorType()));
                renew.setDistributorAccount(distributor.getUserName());
                renew.setDistributorIdCard(distributor.getIdCard());
                renew.setDistributorName(distributor.getRealName());
                renew.setDistributorPhone(distributor.getPhone());
                renew.setDistributorProvince(distributor.getProvince());
                renew.setDistributorCity(distributor.getCity());
                renew.setDistributorRegion(distributor.getRegion());
            }
            DistributorDTO recommendDistributor = userFeign.getRecommendByDistributorId(distributorId);
            if (!Objects.isNull(recommendDistributor)) {
                renew.setDistributorRecommendName(recommendDistributor.getRealName());
                renew.setDistributorRecommendProvince(recommendDistributor.getProvince());
                renew.setDistributorRecommendCity(recommendDistributor.getCity());
                renew.setDistributorRecommendRegion(recommendDistributor.getRegion());

                StationDTO recommendStation = systemFeign.getStationByPRC(recommendDistributor.getProvince(), recommendDistributor.getCity(), recommendDistributor.getRegion(), PermissionTypeEnum.PRE_SALE.value);
                if (!Objects.isNull(recommendStation)) {
                    renew.setDistributorRecommendStationName(recommendStation.getName());
                }
            }
        }

        //设备安装时间
        renew.setWaterUserName(wo.getUserName());
        renew.setWaterUserPhone(wo.getUserPhone());

        //续费订单安装工信息设置
        if (renew.getEngineerId() != null) {
            EngineerDTO engineer = userFeign.getEngineerById(renew.getEngineerId());
            if (!Objects.isNull(engineer)) {
                renew.setEngineerId(engineer.getId());
                renew.setEngineerName(engineer.getRealName());
                renew.setEngineerPhone(engineer.getPhone());
                renew.setEngineerStationName(engineer.getStationName());
            }
        }
        OrderMain orderMain = orderMainService.findById(wo.getMainOrderId());
        if (orderMain == null) {
            log.error("创建续费订单失败，没有获取到主订单信息--mainOrderId={}", wo.getMainOrderId());
            throw new YimaoException("创建续费订单失败，没有获取到主订单信息");
        }
        if (renew.getDeviceInstallationTime() == null) {
            if (wo.getCompleteTime() != null) {
                renew.setDeviceInstallationTime(wo.getCompleteTime());
            } else {
                renew.setDeviceInstallationTime(wo.getOperationTime());
            }
        }
        String province=wo.getDistributorProvince();
        String city=wo.getDistributorCity();
        String region=wo.getDistributorRegion();
        StationCompanyDTO station = systemFeign.getStationCompanyByPCR(province, city, region, StationAreaServiceTypeEnum.PRE_SALE.value);
        if (station != null) {
            renew.setDistributorStationName(station.getName());
        }

        SubOrderDetail subOrderDetail = subOrderDetailMapper.selectProductInfoById(wo.getSubOrderId());
        if (subOrderDetail == null) {
            log.error("创建续费订单失败，没有获取到子订单详情信息--subOrderId={}", wo.getSubOrderId());
            throw new YimaoException("创建续费订单失败，没有获取到子订单详情信息");
        }
        renew.setProductCompanyId(subOrderDetail.getProductCompanyId());
        renew.setProductCompanyName(subOrderDetail.getProductCompanyName());
        renew.setProductFirstCategoryId(subOrderDetail.getProductFirstCategoryId());
        renew.setProductFirstCategoryName(subOrderDetail.getProductFirstCategoryName());
        renew.setProductSecondCategoryId(subOrderDetail.getProductTwoCategoryId());
        renew.setProductSecondCategoryName(subOrderDetail.getProductTwoCategoryName());
        renew.setProductCategoryId(subOrderDetail.getProductCategoryId());
        renew.setProductCategoryName(subOrderDetail.getProductCategoryName());

        Example example = new Example(OrderRenew.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("workOrderId", renew.getWorkOrderId());
        criteria.andEqualTo("snCode", renew.getSnCode());
        criteria.andEqualTo("pay", true);
        criteria.andEqualTo("deleted", false);
        criteria.andIsNotNull("payTime");
        criteria.andIsNotNull("payType");
        int count = orderRenewMapper.selectCountByExample(example);
        renew.setTimes(count + 1);
        renew.setDeleted(false);
        int result = orderRenewMapper.insert(renew);
        if (result != 1) {
            log.error("创建续费订单失败，保存续费订单失败--workOrderId={}", wo.getId());
            throw new YimaoException("创建续费订单失败，保存续费订单失败");
        }

    }


    /**
     * @description 查询续费订单对账列表根据订单查询条件
     */
    @Override
    public PageVO<OrderRenewVO> orderRenewFinanceList(RenewOrderQuery query, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        //查询续费订单列表
        Page<OrderRenewVO> renewPage = orderRenewMapper.orderRenewFinanceList(query);
        return new PageVO<>(pageNum, renewPage);
    }

    /**
     * @param id
     * @description 查询续费订单详情
     * @author zhilin.he
     * @date 2019/1/21 17:30
     */
    @Override
    public OrderRenewVO getOrderRenewDetail(String id) {
        return orderRenewMapper.selectOrderRenewDetail(id);
    }

    @Override
    public OrderRenew getById(String id) {
        return orderRenewMapper.selectByPrimaryKey(id);
    }

    /**
     * 续费工单--续费工单列表
     */
    @Override
    public PageVO<OrderRenewVO> getOrderRenewList(RenewOrderQuery query, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        //查询续费订单列表
        Page<OrderRenewVO> page = orderRenewMapper.getOrderRenewList(query);
        return new PageVO<>(pageNum, page);
    }

    /**
     * 续费工单--凭证修改
     */
    @Override
    public void resubmit(String id, Integer payType, String payCredential) {
        boolean exists = orderRenewMapper.existsWithPrimaryKey(id);
        if (!exists) {
            throw new NotFoundException("该续费单号不存在。");
        } else {
            if (PayType.find(payType) == null) {
                throw new NotFoundException("支付方式选择错误。");
            }
            OrderRenew update = new OrderRenew();
            update.setId(id);
            update.setPayType(payType);
            update.setPayCredential(payCredential);
            //重新提交支付凭证时把状态设置为【待审核】
            update.setStatus(RenewStatus.AUDITED.value);
            update.setStatusName(RenewStatus.AUDITED.name);
            Date date = new Date();
            update.setPayCredentialSubmitTime(date);
            update.setUpdateTime(date);
            int result = orderRenewMapper.updateByPrimaryKeySelective(update);
            if (result < 1) {
                throw new YimaoException("凭证修改失败!");
            }
        }
    }

    /**
     * 财务管理-支付审核-续费支付待审核-审核
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
    @Override
    public void orderRenewPayCheckSingle(String id, Boolean pass, String reason, String adminName) {
        OrderRenew renew = orderRenewMapper.selectByPrimaryKey(id);
        if (renew == null) {
            throw new NotFoundException("该续费单不存在。");
        }
        if (renew.getStatus() == RenewStatus.SUCCESS.value) {
            throw new BadRequestException("该续费单已经审核通过，不能重复审核。");
        }
        if (renew.getStatus() == RenewStatus.FAIL.value) {
            throw new BadRequestException("只有状态为待审核的续费单才可以进行审核。");
        }
        if (!pass && StringUtil.isEmpty(reason)) {
            throw new BadRequestException("请填写审核不通过原因。");
        }
        WaterDeviceDTO device = waterFeign.getBySnCode(renew.getSnCode());
        if (device == null) {
            throw new NotFoundException("根据设备SN码未获取到设备信息。");
        }
        WorkOrder workOrder = workOrderMapper.selectWorkOrderByDeviceIdForRenewOrder(renew.getDeviceId());
        if (workOrder == null) {
            throw new NotFoundException("根据工单号未获取到工单信息。");
        }

        Date checkDate = new Date();//审核日期
        BigDecimal renewMoney = renew.getAmountFee();

        //设置水机设备金额和续费信息
        BigDecimal money = device.getMoney().add(renewMoney);

        //同步售后 审核状态和余额更新
        try {
            this.afterRenewPayOrVerify(renew, device, workOrder, money, pass, reason, checkDate);
        } catch (Exception e) {
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw, true));
            String subject = "水机续费支付审核-同步续费订单到百得出错提醒" + domainProperties.getApi();
            String content = "水机续费支付审核-同步续费订单到百得出错。renewOrderId=" + id + "\n" + sw.toString();
            mailSender.send(null, subject, content);
        }

        //审核成功才更新设备余额
        if (pass) {
            //更新水机设备续费信息
            WaterDeviceDTO deviceDTO = new WaterDeviceDTO();
            deviceDTO.setId(renew.getDeviceId());
            deviceDTO.setSn(renew.getSnCode());
            deviceDTO.setMoney(money);
            deviceDTO.setLastRenewTime(renew.getPayTime());
            deviceDTO.setRenewTimes(renew.getTimes());
            deviceDTO.setRenewStatus(WaterDeviceRenewStatus.ALREADY.value);
            deviceDTO.setRenewStatusText(WaterDeviceRenewStatus.ALREADY.name);
            deviceDTO.setCostId(renew.getCostId());
            deviceDTO.setCostType(renew.getCostType());
            deviceDTO.setCostName(renew.getCostName());
            waterFeign.renewProcessor(deviceDTO);
        }

        updateRenewStatus(pass, id, reason, adminName, checkDate);

        // UserDTO userDTO = userFeign.getUserById(workOrder.getUserId());
        // if (userDTO == null) {
        //     throw new NotFoundException(workOrder.getUserId() + "该用户信息不存在！");
        // }
        // DistributorDTO distributorDTO = userFeign.getDistributorById(workOrder.getDistributorId());
        // if (distributorDTO == null) {
        //     throw new NotFoundException(workOrder.getDistributorId() + "该经销商信息不存在！");
        // }
        // EngineerDTO engineerDTO = userFeign.getEngineerById(workOrder.getEngineerId());
        // if (engineerDTO == null) {
        //     throw new NotFoundException(workOrder.getEngineerId() + "该设备信息不存在！");
        // }
        // this.sendAndPush(engineerDTO, device, money.intValue(), userDTO, distributorDTO, workOrderId, renewMoney.intValue());

        //收益分配 支付状态为已支付
        if (pass) {
            this.allotRenewIncome(renew);
            //给水机用户发送短信、给经销商、客服推送消息
            device.setMoney(money);
            pushMessage(renew, device);
        }
    }

    /**
     * 更新续费单状态
     *
     * @param pass      是否审核通过
     * @param id        续费单号
     * @param reason    审核不通过原因
     * @param adminName 后台操作人
     */
    private void updateRenewStatus(Boolean pass, String id, String reason, String adminName, Date date) {
        OrderRenew renew = new OrderRenew();
        //续费单号
        renew.setId(id);
        OrderPayCheck payCheck = new OrderPayCheck();
        //订单类型：1-产品订单；2-水机续费单；
        payCheck.setOrderType(2);
        payCheck.setOrderId(renew.getId());
        if (!pass) {
            renew.setStatus(RenewStatus.FAIL.value);
            renew.setStatusName(RenewStatus.FAIL.name);
            //审核状态：1-通过；2-不通过；
            payCheck.setStatus(2);
            payCheck.setStatusName("不通过");
            payCheck.setReason(reason);
        } else {
            renew.setStatus(RenewStatus.SUCCESS.value);
            renew.setStatusName(RenewStatus.SUCCESS.name);
            //审核状态：1-通过；2-不通过；
            payCheck.setStatus(1);
            payCheck.setStatusName("通过");
        }
        renew.setCheckTime(date);
        renew.setUpdateTime(date);
        int result = orderRenewMapper.updateByPrimaryKeySelective(renew);
        if (result < 1) {
            throw new YimaoException("续费单支付审核失败。");
        }
        payCheck.setCreator(adminName);
        payCheck.setCreateTime(date);
        orderPayCheckMapper.insert(payCheck);
    }

    //续费线上支付成功/线下审核通过推送消息 
    private void pushMessage(OrderRenew renewOrder, WaterDeviceDTO device) {
        try {
            Date now = new Date();
            List<AppMessageDTO> appMessageList = new ArrayList<AppMessageDTO>();
            Map<String, String> content = new HashMap<>();
            //查询安装工信息
            EngineerDTO enDto = userFeign.getEngineerById(renewOrder.getEngineerId());

            //查询经销商信息
            DistributorDTO dto = userFeign.getDistributorById(renewOrder.getDistributorId());
            //水机用户短信
            content.put("#code#", device.getSn());//设备编号
            content.put("#code1#", DateUtil.getYearByDate(renewOrder.getPayTime()) + "");//续费年份
            content.put("#code2#", DateUtil.getMonthByDate(renewOrder.getPayTime()) + "");//续费月份
            content.put("#code3#", DateUtil.getDayByDate(renewOrder.getPayTime()) + "");//续费日
            content.put("#code4#", renewOrder.getAmountFee().toString());//本次续费金额
            content.put("#code5#", device.getMoney().toString());//当前设备余额
            content.put("#code6#", enDto.getRealName());//服务人员
            content.put("#code7#", enDto.getPhone());//服务电话

            //水机用户发送短信
            SmsMessageDTO smsMessage = new SmsMessageDTO();
            smsMessage.setType(MessageModelTypeEnum.RENEW_ORDER_PUSH.value);
            smsMessage.setPushObject(MessagePushObjectEnum.WATER_USER.value);
            smsMessage.setPhone(device.getDeviceUserPhone());
            smsMessage.setMechanism(MessageMechanismEnum.RENEW_SUCCESS.value);
            smsMessage.setPushMode(MessagePushModeEnum.YIMAO_SMS.value);
            smsMessage.setContentMap(content);
            rabbitTemplate.convertAndSend(RabbitConstant.SMS_MESSAGE_PUSH, smsMessage);

            //安装工消息推送,adress地址 取值:如果设备有更换地址,取新地址,不然取水机地址
	        String address="";
	        if(!StringUtil.isEmpty(device.getSn())) {
	        	//如果水机更换了位置,则取更换后的adress,否则取水机上的adress
	        	 WaterDevicePlaceChangeRecordDTO placeChangeRecord =waterFeign.getWaterDevicePlaceChangeRecordBySn(device.getSn());
	        	 if(null!=placeChangeRecord&&!StringUtil.isEmpty(placeChangeRecord.getDetailAddress())) {
	        		 address=placeChangeRecord.getNewPlace()+placeChangeRecord.getDetailAddress();
	        	 }else {
	        		 address=device.getProvince()+device.getCity()+device.getRegion()+device.getAddress();
	        	 }
	        }
            content = new HashMap<>();
            AppMessageDTO appMessage = new AppMessageDTO();
            content.put("#code#", device.getSn());//设备编号
            content.put("#code1#", renewOrder.getAmountFee().toString());//充值金额
            content.put("#code2#", device.getDeviceUserName());//水机用户
            content.put("#code3#", device.getDeviceUserPhone());
            content.put("#code4#", address);
            //appMessage.setReceiver(workorder.getEngineerId() + "");
            appMessage.setDevices(enDto.getAppType());
            appMessage.setReceiverId(enDto.getId());
            appMessage.setReceiver(enDto.getRealName());
            appMessage.setPushType(MessageModelTypeEnum.RENEW_ORDER_PUSH.value);
            appMessage.setMechanism(MessageMechanismEnum.RENEW_SUCCESS.value);
            appMessage.setApp(MessagePushModeEnum.YIMAO_ENGINEER_APP_NOTICE.value);
            appMessage.setTitle(MessageModelTypeEnum.RENEW_ORDER_PUSH.name);
            appMessage.setCreateTime(now);
            appMessage.setContentMap(content);
            appMessage.setMsgType(Constant.MSG_TYPE_NOTICE);
            appMessageList.add(appMessage);

            //推送给经销商
            Map<String, String> contentDis = new HashMap<>();
            AppMessageDTO appMessageDis = new AppMessageDTO();
            contentDis.put("#code#", device.getSn());//设备编号
            contentDis.put("#code1#", renewOrder.getAmountFee().toString());//充值金额
            contentDis.put("#code2#", device.getDeviceUserName());//水机用户
            contentDis.put("#code3#", device.getDeviceUserPhone());
            contentDis.put("#code4#", enDto.getRealName());//服务人员
            contentDis.put("#code5#", enDto.getPhone());//服务电话
            appMessageDis.setReceiverId(dto.getUserId());
            appMessageDis.setReceiver(dto.getRealName());
            appMessageDis.setDevices(dto.getAppType());
            appMessageDis.setPushType(MessageModelTypeEnum.RENEW_ORDER_PUSH.value);
            appMessageDis.setMechanism(MessageMechanismEnum.RENEW_SUCCESS.value);
            appMessageDis.setApp(MessagePushModeEnum.YIMAO_APP_NOTICE.value);
            appMessageDis.setTitle(MessageModelTypeEnum.RENEW_ORDER_PUSH.name);
            appMessageDis.setCreateTime(now);
            appMessageDis.setContentMap(contentDis);
            appMessageDis.setMsgType(Constant.MSG_TYPE_NOTICE);
            appMessageList.add(appMessageDis);
            installerApiService.pushMsgToApp(appMessageList);
            
            //推送站务系统
            Integer areaId = redisCache.get(Constant.AREA_CACHE + renewOrder.getProvince() + "_" + renewOrder.getCity() + "_" + renewOrder.getRegion(), Integer.class);
            if (areaId == null) {
                areaId = systemFeign.getRegionIdByPCR(renewOrder.getProvince(), renewOrder.getCity(), renewOrder.getRegion());
            }

            if(Objects.nonNull(areaId)) {
            	Map<String, String> stationMessage = new HashMap<>();
                stationMessage.put("#code#", device.getSn());
                stationMessage.put("#code1#", renewOrder.getAmountFee().toString());
                stationMessage.put("#code2#", device.getDeviceUserName());
                stationMessage.put("#code3#", device.getDeviceUserPhone());
                stationMessage.put("#code4#", enDto.getRealName());
                stationMessage.put("#code5#", enDto.getPhone());
                stationMessage.put("#code6#", dto.getRealName());
                stationMessage.put("#code7#", dto.getPhone());
                StationMessageDTO statMessage=new StationMessageDTO();
                statMessage.setDeviceId(String.valueOf(device.getId()));
                statMessage.setSncode(device.getSn());
                statMessage.setWorkorderId(device.getWorkOrderId());
                statMessage.setPushType(MessageModelTypeEnum.RENEW_ORDER_PUSH.value);
                statMessage.setCreateTime(new Date());
        		statMessage.setReceiverId(areaId);
        		statMessage.setTitle(MessageModelTypeEnum.RENEW_ORDER_PUSH.name);
        		statMessage.setMechanism(MessageMechanismEnum.RENEW_SUCCESS.value);
        		statMessage.setContentMap(stationMessage);
        		statMessage.setPushObject(MessagePushObjectEnum.SYSTEM.value);
        		statMessage.setMessageType(0);

        		rabbitTemplate.convertAndSend(RabbitConstant.STATION_MESSAGE_PUSH, statMessage);
            }

            
            
        } catch (Exception e) {
            log.error("=============水机续费成功推送短信异常信息========" + e.getMessage());
        }

    }

    @Override
    public OrderRenewVO getOrderRenewFinanceDetail(String id) {
        return orderRenewMapper.selectOrderRenewFinanceDetail(id);
    }

    @Override
    public PageVO<OrderRenewVO> orderRenewPayCheckList(RenewOrderQuery query, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        //查询续费订单列表
        Page<OrderRenewVO> page = orderRenewMapper.orderRenewPayCheckList(query);
        return new PageVO<>(pageNum, page);
    }

    /**
     * 水机续费支付回调
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
    public void payCallback(String renewOrderId) {
        try {
            OrderRenew renewOrder = orderRenewMapper.selectByPrimaryKey(renewOrderId);

            WorkOrder workOrder = workOrderMapper.selectWorkOrderByDeviceIdForRenewOrder(renewOrder.getDeviceId());
            if (workOrder == null) {
                throw new NotFoundException("根据工单号未获取到工单信息。");
            }

            long l1 = System.currentTimeMillis();
            WaterDeviceDTO device1 = waterFeign.getWaterDeviceById(renewOrder.getDeviceId());
            long l2 = System.currentTimeMillis();
            log.info("水机续费支付回调---waterFeign.getWaterDeviceById({})执行耗时---{}毫秒", renewOrder.getDeviceId(), l2 - l1);

            //全量同步一条续费工单数据到百得
            BigDecimal newMoney = device1.getMoney().add(renewOrder.getAmountFee());

            try {
                this.afterRenewPayOrVerify(renewOrder, device1, workOrder, newMoney, true, "", renewOrder.getPayTime());
            } catch (Exception e) {
                StringWriter sw = new StringWriter();
                e.printStackTrace(new PrintWriter(sw, true));
                String subject = "水机续费支付回调-同步续费订单到百得出错提醒" + domainProperties.getApi();
                String content = "水机续费支付回调-同步续费订单到百得出错。renewOrderId=" + renewOrderId + "\n" + sw.toString();
                mailSender.send(null, subject, content);
            }

            //更新水机设备续费信息
            WaterDeviceDTO device = new WaterDeviceDTO();
            device.setId(renewOrder.getDeviceId());
            device.setSn(renewOrder.getSnCode());
            device.setMoney(newMoney);
            device.setLastRenewTime(renewOrder.getPayTime());
            device.setRenewTimes(renewOrder.getTimes());
            device.setRenewStatus(WaterDeviceRenewStatus.ALREADY.value);
            device.setRenewStatusText(WaterDeviceRenewStatus.ALREADY.name);
            device.setCostId(renewOrder.getCostId());
            device.setCostType(renewOrder.getCostType());
            device.setCostName(renewOrder.getCostName());
            try {
                long l3 = System.currentTimeMillis();
                waterFeign.renewProcessor(device);
                long l4 = System.currentTimeMillis();
                log.info("水机续费支付回调---waterFeign.renewProcessor()执行耗时---{}毫秒", l4 - l3);
            } catch (Exception e) {
                log.error("水机续费线上支付回调-调用water服务失败" + e.getMessage(), e);
                StringWriter sw = new StringWriter();
                e.printStackTrace(new PrintWriter(sw, true));
                String subject = "水机续费线上支付回调-调用water服务失败" + domainProperties.getApi();
                String content = "水机续费线上支付回调-调用water服务失败。renewOrderId=" + renewOrderId + "\n" + sw.toString();
                mailSender.send(null, subject, content);
            }
            //收益分配
            this.allotRenewIncome(renewOrder);

            //给水机用户发送短信、给经销商、客服推送消息
            device1.setMoney(newMoney);
            pushMessage(renewOrder, device1);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw, true));
            String subject = "水机续费线上支付回调失败提醒" + domainProperties.getApi();
            String content = "水机续费线上支付回调时出错。renewOrderId=" + renewOrderId + "\n" + sw.toString();
            mailSender.send(null, subject, content);
            throw new YimaoException(e.getMessage(), e);
        }
    }

    @Override
    public OrderRenew getPayInfoById(String id) {
        return orderRenewMapper.selectPayInfoById(id);
    }

    @Override
    public void allotRenewIncome(OrderRenew renewOrder) {
        log.info("===========收益分配-开始==============");
        Integer costId = renewOrder.getCostId();
        ProductCostDTO cost = productFeign.getProductCostById(costId);
        if (cost == null || cost.getIncomeRuleId() == null) {
            log.error("收益分配-失败-[计费方式信息有误]-renewOrderId={}", renewOrder.getId());
            throw new YimaoException("收益分配-失败-[计费方式信息有误]");
        }
        IncomeRuleDTO incomeRule = incomeRuleService.getIncomeRuleById(cost.getIncomeRuleId());
        if (incomeRule == null) {
            log.error("收益分配-失败-[分配规则信息有误]-renewOrderId={}", renewOrder.getId());
            throw new YimaoException("收益分配-失败-[分配规则信息有误]");
        }

        this.createIncome(renewOrder, incomeRule);
        log.info("===========收益分配-结束==============");
    }

    private void createIncome(OrderRenew renewOrder, IncomeRuleDTO incomeRule) {

        boolean exists = productIncomeRecordMapper.existsWithOrderId(renewOrder.getId(), IncomeType.RENEW_INCOME.value);
        if (exists) {
            log.info("续费收益分配结束-已分配过收益了。renewOrderId={}", renewOrder.getId());
            return;
        }

        //根据原始子订单号获取首年产品收益分配
        //1-先获取子订单
        OrderSubDTO subOrder = orderSubService.findFullOrderById(renewOrder.getOrderId());

        ProductIncomeRecord record = new ProductIncomeRecord();
        //生成收益结算信息
        record.setMainOrderId(renewOrder.getMainOrderId());
        record.setOrderId(renewOrder.getOrderId().toString());
        record.setRenewOrderId(renewOrder.getId());//续费单号
        record.setOrderFee(renewOrder.getAmountFee());
        record.setProductId(subOrder.getProductId());
        record.setProductName(subOrder.getProductName());
        record.setProductCompanyId(subOrder.getProductCompanyId());
        record.setProductCompanyName(subOrder.getProductCompanyName());
        record.setProductCategoryId(subOrder.getProductCategoryId());
        record.setProductCategoryName(subOrder.getProductCategoryName());
        record.setProductOneCategoryId(renewOrder.getProductFirstCategoryId());
        record.setProductOneCategoryName(renewOrder.getProductFirstCategoryName());
        record.setProductTwoCategoryId(renewOrder.getProductSecondCategoryId());
        record.setProductTwoCategoryName(renewOrder.getProductSecondCategoryName());

        record.setUserId(subOrder.getUserId());
        record.setUserType(subOrder.getUserType());
        record.setUserTypeName(subOrder.getUserTypeName());
        record.setUserName(subOrder.getUserName());
        record.setUserPhone(subOrder.getUserPhone());

        record.setDistributorId(renewOrder.getDistributorId());
        record.setDistributorName(renewOrder.getDistributorName());
        record.setDistributorTypeName(renewOrder.getDistributorTypeName());
        record.setDistributorAccount(renewOrder.getDistributorAccount());
        record.setDistributorProvince(renewOrder.getDistributorProvince());
        record.setDistributorCity(renewOrder.getDistributorCity());
        record.setDistributorRegion(renewOrder.getDistributorRegion());

        record.setIncomeRuleId(incomeRule.getId());
        record.setIncomeType(incomeRule.getIncomeType());
        record.setAllotType(incomeRule.getAllotType());
        BigDecimal balanceFee = renewOrder.getAmountFee();
        if (balanceFee == null) {
            balanceFee = new BigDecimal(0);
        }
        //可分配金额
        record.setSettlementFee(balanceFee);
        //结算数量，续费收益设置为1
        record.setSettlementAmount(1);

        record.setStatus(OrderCompleteStatusEnum.UNCOMPLETED.value);
        record.setOrderCompleteTime(renewOrder.getPayTime());
        //计算结算月份
        String settlementMonth = DateUtil.transferDateToString(DateUtil.monthAfter(renewOrder.getPayTime(), 1), "yyyy-MM");
        //获取经销商信息
        Integer distributorId = record.getDistributorId();
        DistributorDTO distributor = userFeign.getDistributorBasicInfoById(distributorId);
        //以下经销商身份可以在工单完成的时候进行收益可结算状态设置，其它身份的经销商收益暂不可结算
        if (distributor != null && distributor.getRoleLevel() != null && (distributor.getRoleLevel() == DistributorRoleLevel.D_350.value
                || distributor.getRoleLevel() == DistributorRoleLevel.D_650.value
                || distributor.getRoleLevel() == DistributorRoleLevel.D_950.value
                || distributor.getRoleLevel() == DistributorRoleLevel.D_1000.value)) {
            record.setStatus(OrderCompleteStatusEnum.CAN_BE_SETTLED.value);
            //结算月份设值
            record.setSettlementMonth(settlementMonth);
        }
        //安装工收益结算月份设值,体验版经销商和折机版经销商的续费收益需要设置安装工结算月份
        record.setEngineerSettlementMonth(settlementMonth);
        record.setCreateTime(renewOrder.getPayTime());

        productIncomeRecordMapper.insert(record);


        //获取收益分配规则详细
        List<IncomeRulePartDTO> rulePartList = incomeRule.getIncomeRuleParts();
        Map<String, IncomeRulePartDTO> rulePartMap = new HashMap<>();
        for (IncomeRulePartDTO rulePart : rulePartList) {
            rulePartMap.put(rulePart.getSubjectCode(), rulePart);
        }
        List<ProductIncomeRecordPart> recordPartList = new ArrayList<>();
        //1-翼猫总部
        IncomeRulePartDTO rulePart = rulePartMap.get(IncomeSubjectEnum.MAIN_COMPANY.value);
        if (rulePart != null) {
            ProductIncomeRecordPart recordPart = this.createProductIncomeRecordPart(record, incomeRule, rulePart);
            //收益主体信息
            recordPart.setSubjectPhone("4001519999");
            recordPart.setSubjectProvince("上海市");
            recordPart.setSubjectCity("上海市");
            recordPart.setSubjectRegion("嘉定区");
            recordPartList.add(recordPart);
        }

        //2-省级公司
        rulePart = rulePartMap.get(IncomeSubjectEnum.PROVINCE_COMPANY.value);
        if (rulePart != null) {
            ProductIncomeRecordPart recordPart = this.createProductIncomeRecordPart(record, incomeRule, rulePart);
            //收益主体信息
            recordPart.setSubjectProvince(renewOrder.getDistributorProvince());
            recordPartList.add(recordPart);
        }

        //3-市级公司
        rulePart = rulePartMap.get(IncomeSubjectEnum.CITY_COMPANY.value);
        if (rulePart != null) {
            ProductIncomeRecordPart recordPart = this.createProductIncomeRecordPart(record, incomeRule, rulePart);
            //收益主体信息
            recordPart.setSubjectProvince(renewOrder.getDistributorProvince());
            recordPart.setSubjectCity(renewOrder.getDistributorCity());
            recordPartList.add(recordPart);
        }

        //4-产品公司
        rulePart = rulePartMap.get(IncomeSubjectEnum.PRODUCT_COMPANY.value);
        if (rulePart != null) {
            ProductIncomeRecordPart recordPart = this.createProductIncomeRecordPart(record, incomeRule, rulePart);
            //获取产品公司
            ProductCompanyDTO productCompany = productFeign.getProductCompanyById(renewOrder.getProductCompanyId());
            if (productCompany != null) {
                //收益主体为产品公司
                recordPart.setSubjectId(productCompany.getId());
                recordPart.setSubjectName(productCompany.getName());
                //结算主体为产品公司
                recordPart.setSettlementSubjectId(productCompany.getId());
                recordPart.setSettlementSubjectName(productCompany.getName());
            }
            recordPartList.add(recordPart);
        }

        //13-分销用户（会员用户）
        BigDecimal saleUserIncome = new BigDecimal(0);
        rulePart = rulePartMap.get(IncomeSubjectEnum.DISTRIBUTOR_USER.value);
        if (rulePart != null) {
            ProductIncomeRecordPart recordPart = this.createProductIncomeRecordPart(record, incomeRule, rulePart);
            //获取分销用户
            ProductIncomeRecordPart vipUserRecordPart = productIncomeRecordPartMapper.selectVipUserIncome(record.getMainOrderId(), record.getOrderId(), IncomeSubjectEnum.DISTRIBUTOR_USER.value);
            if (Objects.nonNull(vipUserRecordPart)) {
                //收益主体为分销用户
                recordPart.setSubjectId(vipUserRecordPart.getSubjectId());
                recordPart.setSubjectName(vipUserRecordPart.getSubjectName());
                recordPart.setSubjectPhone(vipUserRecordPart.getSubjectPhone());
                recordPart.setSubjectIdCard(vipUserRecordPart.getSubjectIdCard());
                recordPart.setSubjectProvince(vipUserRecordPart.getSubjectProvince());
                recordPart.setSubjectCity(vipUserRecordPart.getSubjectCity());
                recordPart.setSubjectRegion(vipUserRecordPart.getSubjectRegion());

                //结算主体为服务站站长所属区县级公司
                recordPart.setSettlementSubjectId(vipUserRecordPart.getSettlementSubjectId());
                recordPart.setSettlementSubjectName(vipUserRecordPart.getSettlementSubjectName());
                recordPartList.add(recordPart);
            } else {
                saleUserIncome = rulePart.getValue();
            }
        }

        //14-经销商
        rulePart = rulePartMap.get(IncomeSubjectEnum.DISTRIBUTOR.value);
        if (rulePart != null) {
            ProductIncomeRecordPart recordPart = new ProductIncomeRecordPart();
            //收益ID
            recordPart.setRecordId(record.getId());
            //获取收益主体
            Integer subjectId = rulePart.getSubjectId();
            IncomeSubject subject = incomeSubjectMapper.selectByPrimaryKey(subjectId);
            //如果分销用户没找到那收益归属到经销商
            BigDecimal value = rulePart.getValue().add(saleUserIncome);
            if (incomeRule.getAllotType() == IncomeAllotType.RATIO.value) {
                //收益比例
                recordPart.setSubjectRatio(value);
                //收益金额
                recordPart.setSubjectMoney(record.getSettlementFee().multiply(value.divide(new BigDecimal(100))));
            } else {
                //收益金额
                recordPart.setSubjectMoney(value);
            }
            //收益主体为经销商
            recordPart.setSubjectId(renewOrder.getDistributorId());
            recordPart.setSubjectName(renewOrder.getDistributorName());
            recordPart.setSubjectCode(subject.getIncomeSubjectCode());
            recordPart.setSubjectPhone(renewOrder.getDistributorPhone());
            recordPart.setSubjectIdCard(renewOrder.getDistributorIdCard());
            recordPart.setSubjectProvince(renewOrder.getDistributorProvince());
            recordPart.setSubjectCity(renewOrder.getDistributorCity());
            recordPart.setSubjectRegion(renewOrder.getDistributorRegion());
            //结算主体（先设置后面会覆盖）
            recordPart.setSettlementSubjectCode(subject.getSettlementSubjectCode());
            recordPart.setSettlementSubjectName(subject.getSettlementSubjectName());
            recordPart.setSettlementTime(new Date());
            recordPart.setHasWithdraw(0);
            recordPart.setCreateTime(new Date());

            String province = renewOrder.getDistributorProvince();
            String city = renewOrder.getDistributorCity();
            String region = renewOrder.getDistributorRegion();
            //根据经销商所在省市区查询其所属的区县级公司
            StationCompanyDTO stationCompany = systemFeign.getStationCompanyByPCR(province, city, region,StationAreaServiceTypeEnum.PRE_SALE.value);
            if (stationCompany != null) {
                //结算主体为服务站站长所属区县级公司
                recordPart.setSettlementSubjectId(stationCompany.getId());
                recordPart.setSettlementSubjectName(stationCompany.getName());
            }
            recordPartList.add(recordPart);
        }

        //17-安装工程师所在区县级公司
        rulePart = rulePartMap.get(IncomeSubjectEnum.ENGINEER_STATION_COMPANY.value);
        EngineerDTO engineer = null;
        StationCompanyDTO engineerStationCompany = null;
        if (rulePart != null) {
            ProductIncomeRecordPart recordPart = this.createProductIncomeRecordPart(record, incomeRule, rulePart);
            //获取安装工信息
            engineer = userFeign.getEngineerById(renewOrder.getEngineerId());
            if (engineer != null) {
                String province = engineer.getProvince();
                String city = engineer.getCity();
                String region = engineer.getRegion();
                //根据安装工程师所在省市区查询其所属的区县级公司
                engineerStationCompany = systemFeign.getStationCompanyByPCR(province, city, region,StationAreaServiceTypeEnum.AFTER_SALE.value);
                if (engineerStationCompany != null) {
                    //收益主体为安装工程师所属区县级公司
                    recordPart.setSubjectId(engineerStationCompany.getId());
                    recordPart.setSubjectName(engineerStationCompany.getName());
                    recordPart.setSubjectPhone(engineerStationCompany.getContactPhone());
                    recordPart.setSubjectProvince(engineerStationCompany.getProvince());
                    recordPart.setSubjectCity(engineerStationCompany.getCity());
                    recordPart.setSubjectRegion(engineerStationCompany.getRegion());
                    //结算主体为安装工程师所属区县级公司
                    recordPart.setSettlementSubjectId(engineerStationCompany.getId());
                    recordPart.setSettlementSubjectName(engineerStationCompany.getName());
                }
            }
            recordPartList.add(recordPart);
        }

        //18-安装工程师
        rulePart = rulePartMap.get(IncomeSubjectEnum.ENGINEER.value);
        if (rulePart != null) {
            ProductIncomeRecordPart recordPart = this.createProductIncomeRecordPart(record, incomeRule, rulePart);
            if (engineer != null) {
                String province = engineer.getProvince();
                String city = engineer.getCity();
                String region = engineer.getRegion();
                //收益主体为安装工程师
                recordPart.setSubjectId(engineer.getId());
                recordPart.setSubjectName(engineer.getRealName());
                recordPart.setSubjectPhone(engineer.getPhone());
                recordPart.setSubjectIdCard(engineer.getIdCard());

                recordPart.setSubjectProvince(province);
                recordPart.setSubjectCity(city);
                recordPart.setSubjectRegion(region);
            }
            //根据安装工程师省市区查询其所属的区县级公司
            if (engineerStationCompany != null) {
                //结算主体为安装工程师所属区县级公司
                recordPart.setSettlementSubjectId(engineerStationCompany.getId());
                recordPart.setSettlementSubjectName(engineerStationCompany.getName());
            }
            recordPartList.add(recordPart);
        }

        if (CollectionUtil.isNotEmpty(recordPartList)) {
            productIncomeRecordPartMapper.batchInsert(recordPartList);
        }
    }

    /**
     * 创建收益分配记录（明细）
     *
     * @param record   收益分配记录
     * @param rule     收益分配规则
     * @param rulePart 收益分配规则明细
     */
    private ProductIncomeRecordPart createProductIncomeRecordPart(ProductIncomeRecord record, IncomeRuleDTO rule, IncomeRulePartDTO rulePart) {
        return productIncomeRecordService.createProductIncomeRecordPart(record, rule, rulePart);
    }


    public void afterRenewPayOrVerify(OrderRenew renew, WaterDeviceDTO device, WorkOrder workOrder, BigDecimal newMoney,
                                      Boolean pass, String reason, Date checkDate) {
        try {
            ProductCostDTO newCost = productFeign.getProductCostById(renew.getCostId());
            if (null == newCost) {
                log.error("=========审核失败,根据计费code未获取到计费信息 ,costid=" + renew.getCostId());
                throw new YimaoException("根据计费code未获取到计费信息");
            }
            WaterDeviceUserDTO deviceUser = userFeign.getDeviceUserDTOInfoById(device.getDeviceUserId());
            if (Objects.isNull(deviceUser)) {
                log.error("=========审核失败,水机用户信息不存在 ,costid=" + device.getDeviceUserId());
                throw new YimaoException("水机用户信息不存在！");
            }
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("renewOrderId", renew.getId());
            params.put("workOrderId", workOrder.getId());
            params.put("moneyTime", DateUtil.dateToString(device.getLastRenewTime()));
           /* params.put("provinceName", device.getProvince());
            params.put("cityName", device.getCity());
            params.put("areaName", device.getRegion());*/
            params.put("provinceName", deviceUser.getProvince());
            params.put("cityName", deviceUser.getCity());
            params.put("areaName", deviceUser.getRegion());
            params.put("costId", newCost.getOldId() + "");
            //params.put("costName", newCost.getName());
            params.put("lastCostId", device.getOldCostId());
            params.put("lastCostName", device.getCostName());
            params.put("sncode", device.getSn());
            params.put("batchCode", workOrder.getLogisticsCode());
            params.put("iccid", device.getIccid());
            params.put("productModel", YunOldIdUtil.getProductId(device.getDeviceModel()));
            params.put("productModelName", YunOldIdUtil.getProductName(device.getDeviceModel()));
            params.put("activeTimeD", DateUtil.dateToString(workOrder.getCompleteTime()));//设备安装时间
            params.put("payTerminal", getPayTerminal(renew.getPayType()));// 续费端(支付端) (1-设备端,2-后台)
            params.put("payMoney", renew.getAmountFee().doubleValue());//续费时要支付的是租赁费（不含安装费）
            params.put("renewTimeD", DateUtil.dateToString(renew.getPayTime()));//续费时间
            params.put("payCount", renew.getTimes() + "");//续费次数
            params.put("payTimeD", DateUtil.dateToString(renew.getPayTime()));//支付时间
            //售后支付方式 1 微信,2支付宝,3其他
            //新支付方式：1-微信；2-支付宝；3-POS机；4-转账；
            Integer payType = renew.getPayType();
            if (null != payType && renew.getPayType() == PayType.TRANSFER.value) {
                payType = PayType.OTHER.value;
            }

            params.put("payType", payType + "");//支付 方式
            params.put("tradeNo", renew.getTradeNo() + "");// 交易单号
            params.put("userName", workOrder.getUserName());//水机用户姓名
            params.put("userPhone", workOrder.getUserPhone());
            params.put("engineerId", workOrder.getOldEngineerId());
            params.put("engineerName", workOrder.getEngineerName());
            params.put("engineerServiceSite", renew.getEngineerStationName());
            params.put("engineerPhone", workOrder.getEngineerPhone());
            params.put("distributorName", workOrder.getDistributorName());
            params.put("distributorPhone", workOrder.getDistributorPhone());
            params.put("distributorAddress", workOrder.getDistributorProvince() + "-" + workOrder.getDistributorCity() + "-" + workOrder.getDistributorRegion());//// 经销商归属地(省-市-区格式)
            params.put("distributorLoginName", workOrder.getDistributorAccount());//经销商登录名
            params.put("distributorIdNo", workOrder.getDistributorIdCard());//经销商身份证号
            params.put("distributorServiceiceSiteName", renew.getDistributorStationName());
            params.put("distributorRefereeName", renew.getDistributorRecommendName());//经销商推荐人
            params.put("distributorRefereeName", renew.getDistributorRecommendName());//经销商推荐人

            params.put("packageName", newCost.getName());//续费套餐名称
            //1:流量扣费,2:时间扣费
            if (null != newCost.getType()) {
                if (newCost.getType() == ProductCostTypeEnum.FLOW.value) {
                    params.put("deductionsType", "流量扣费");
                } else if (newCost.getType() == ProductCostTypeEnum.TIME.value) {
                    params.put("deductionsType", "时间扣费");
                }

            }

            params.put("packagePrice", newCost.getTotalFee().doubleValue());//套餐原金额=计费总额(安装费+租赁费)
            if (null != renew.getPayTime()) {
                params.put("settlementTimeD", DateUtil.dateToString(DateUtil.monthAfter(renew.getPayTime(), 1)));//结算时间
            }
            if (!pass) {
                params.put("otherPayVerifyRejectReason", reason);//审核不通过原因
                params.put("renewStatus", "2");//售后系统续费状态：1-已续费2-待续费	3-到期未续费
                params.put("otherPayVerifyState", "2");//售后审核状态(0-审核中、1-审核已通过、2-审核未通过 )
            } else {
                params.put("renewStatus", "1");
                params.put("otherPayVerifyState", "1");//审核通过
                params.put("money", newMoney.doubleValue());//同步余额
            }
            params.put("invoice", "false");
            log.info("续费审核调用售后系统接口请求参数：{}", JSONObject.toJSON(params));
            if (!BaideApiUtil.success(BaideApiUtil.renewOrderSync(params))) {
                log.error("审核续费订单失败，同步售后水机续费订单审核失败--renewid={}", renew.getId());
                throw new YimaoException("审核续费续费订单失败，同步售后水机续费订单审核失败");
            }

            //同步余额
            if (pass) {
                Map<String, Object> updateMoneyReq = new HashMap<String, Object>();
                updateMoneyReq.put("workOrderId", renew.getWorkOrderId());
                updateMoneyReq.put("sncode", device.getSn());
                updateMoneyReq.put("money", newMoney.doubleValue());//同步余额
                log.info("续费审核更新设备余额调用售后系统接口请求参数：{}", JSONObject.toJSON(updateMoneyReq));
                if (!BaideApiUtil.success(BaideApiUtil.renewOrderUpdateMoney(updateMoneyReq))) {
                    log.error("审核续费订单失败，同步售后设备余额失败--renewid={}", renew.getId());
                    throw new YimaoException("审核续费续费订单失败，同步售后设备余额失败");
                }
            }
        } catch (Exception e) {
            log.error("审核失败,同步售后失败--errmsg={}", e.getMessage());
            throw new YimaoException("审核失败,同步售后失败");
        }

    }


    /**
     * 根据支付方式转换支付端
     * 续费端(支付端) (1-设备端(线上支付),2-后台(线下支付))
     *
     * @param payType
     * @return
     */
    private String getPayTerminal(Integer payType) {
        if (payType != null) {
            //线上支付
            if ((payType == PayType.WECHAT.value || payType == PayType.ALIPAY.value)) {
                return "1";
            }
            //线下支付
            return "2";
        }
        return "";

    }

    /**
     * 站务系统查询昨日续费数与总续费次数
     */
	@Override
	public StationScheduleDTO getStationRenewOrderNum(List<Integer> engineerIds) {
		int renewNum=orderRenewMapper.getStationRenewNum(engineerIds);
				
		int	yesterdayRenewNum=orderRenewMapper.getStationYesterdayRenewNum(engineerIds);
		
		StationScheduleDTO res=new StationScheduleDTO();
		res.setRenewNum(renewNum);
		res.setYesterdayRenewNum(yesterdayRenewNum);
		
		return res;
	}


	@Override
	public List<SalesStatsDTO> getRenewSaleStats(SalesStatsQueryDTO query) {
		query.setDay(6);
		List<SalesStatsDTO> ssdList=orderRenewMapper.getRenewSaleStats(query);
		completeData(ssdList, query, 7, 12);
		return ssdList;
	}

    @Override
    public List<SalesStatsDTO> getWaterDeviceRenewSale(SalesStatsQueryDTO query) {
        query.setDay(29);
        List<SalesStatsDTO> ssdList=orderRenewMapper.getRenewSaleStats(query);
        completeData(ssdList, query, 30, 12);
        return ssdList;
    }


    /**
     * 净水设备续费
     * @param query
     * @return
     */
    @Override
    public AgentSalesOverviewDTO getOrderSalesHomeReport(SalesStatsQueryDTO query) {
        return orderRenewMapper.getOrderSalesHomeReport(query);
    }

    @Override
    public List<String> getRenewOrderTypeNames() {
        List<RenewDTO> list = orderRenewMapper.getCostTypeName();
        List<String> costTypeNames = new ArrayList<>();
        if(CollectionUtil.isNotEmpty(list)){
            for(RenewDTO renewDTO : list){
                costTypeNames.add(renewDTO.getCategoryName() + renewDTO.getCostNameType());
            }
        }
        return costTypeNames;
    }


    /***
     * 补足数据,比如30天的数据查出来只有5条，那么将其他25个日期补上
     * @param investSalesList
     * @param query
     */
    private void completeData(List<SalesStatsDTO> investSalesList, SalesStatsQueryDTO query,int day,int month) {
        calculateDate(query,day,month);
        if (query.getTimeType() == 1 || query.getTimeType() == 2) {
            for (String date : query.getDates()) {
                if (notExists(date, investSalesList)) {
                	SalesStatsDTO statsRs = new SalesStatsDTO();
                    statsRs.setCompleteTime(date);
                    statsRs.setSaleAmount(BigDecimal.ZERO.setScale(2));
                    statsRs.setIncreaseNum(0);
                    investSalesList.add(statsRs);
                }
            }

            //数据排序
            Collections.sort(investSalesList, new Comparator<SalesStatsDTO>() {
                @Override
                public int compare(SalesStatsDTO u1, SalesStatsDTO u2) {
                    long diff = DateUtil.transferStringToDate(query.getTimeType() == 2 ? u1.getCompleteTime() + "-01" : u1.getCompleteTime()).getTime()
                    		- DateUtil.transferStringToDate(query.getTimeType() == 2 ? u2.getCompleteTime() + "-01" : u2.getCompleteTime()).getTime();
                    if (diff > 0) {
                        return 1;
                    } else if (diff < 0) {
                        return -1;
                    } else {
                        return 0;
                    }
                }
            });

        }

    }

	/***
     * 计算日期集合
     * @param query
     * @param day 近几天
     * @param month 近几月
     */
    private void calculateDate(SalesStatsQueryDTO query,int day, int month) {
        List<String> dates = new ArrayList<String>();
        dates.add(query.getCompleteTime());
        if (query.getTimeType() == 1) {
            //按日统计
            for (int i = 1; i < day; i++) {
                dates.add(DateUtil.getChangeDayByDate(DateUtil.transferStringToDate(query.getCompleteTime()), -i));
            }
        } else if (query.getTimeType() == 2) {
            //按月统计
            String time = query.getCompleteTime() + "-01";
            for (int i = 1; i < month; i++) {
                dates.add(DateUtil.getChangeMonthByDate(DateUtil.transferStringToDate(time), -i));
            }
        }
        query.setDates(dates);

    }

    /***
     * 校验数据是否存在
     * @param date
     * @param investSalesList
     * @return
     */
    private boolean notExists(String date, List<SalesStatsDTO> investSalesList) {
        if (investSalesList.isEmpty()) {
            return true;
        }
        boolean flag = true;
        for (SalesStatsDTO stats : investSalesList) {
            if (!StringUtil.isEmpty(stats.getCompleteTime()) && stats.getCompleteTime().equals(date)) {
                return false;
            } else {
                flag = true;
            }
        }
        return flag;
    }

}
