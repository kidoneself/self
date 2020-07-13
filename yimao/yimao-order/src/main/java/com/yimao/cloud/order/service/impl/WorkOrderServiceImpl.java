package com.yimao.cloud.order.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.google.common.collect.Maps;
import com.yimao.cloud.base.baideApi.utils.BaideApiUtil;
import com.yimao.cloud.base.cache.UserCache;
import com.yimao.cloud.base.constant.Constant;
import com.yimao.cloud.base.constant.RabbitConstant;
import com.yimao.cloud.base.enums.*;
import com.yimao.cloud.base.exception.BadRequestException;
import com.yimao.cloud.base.exception.NotFoundException;
import com.yimao.cloud.base.exception.YimaoException;
import com.yimao.cloud.base.mail.MailSender;
import com.yimao.cloud.base.properties.DomainProperties;
import com.yimao.cloud.base.utils.*;
import com.yimao.cloud.base.utils.yun.YunOldIdUtil;
import com.yimao.cloud.framework.cache.RedisCache;
import com.yimao.cloud.framework.cache.RedisLock;
import com.yimao.cloud.order.feign.ProductFeign;
import com.yimao.cloud.order.feign.SystemFeign;
import com.yimao.cloud.order.feign.UserFeign;
import com.yimao.cloud.order.feign.WaterFeign;
import com.yimao.cloud.order.mapper.*;
import com.yimao.cloud.order.po.*;
import com.yimao.cloud.order.service.*;
import com.yimao.cloud.order.utils.ResultUtil;
import com.yimao.cloud.pojo.dto.order.*;
import com.yimao.cloud.pojo.dto.product.ProductCategoryCascadeDTO;
import com.yimao.cloud.pojo.dto.product.ProductCategoryDTO;
import com.yimao.cloud.pojo.dto.product.ProductCostDTO;
import com.yimao.cloud.pojo.dto.product.ProductDTO;
import com.yimao.cloud.pojo.dto.station.StationMessageDTO;
import com.yimao.cloud.pojo.dto.station.StationScheduleDTO;
import com.yimao.cloud.pojo.dto.system.AppMessageDTO;
import com.yimao.cloud.pojo.dto.system.GoodsMaterialsDTO;
import com.yimao.cloud.pojo.dto.system.OnlineAreaDTO;
import com.yimao.cloud.pojo.dto.system.TransferAreaInfoDTO;
import com.yimao.cloud.pojo.dto.user.*;
import com.yimao.cloud.pojo.dto.water.WaterDeviceDTO;
import com.yimao.cloud.pojo.query.station.WorkOrderQuery;
import com.yimao.cloud.pojo.vo.PageVO;
import com.yimao.cloud.pojo.vo.order.EngineerWorkOrderVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

/**
 * 描述：水机安装工单
 *
 * @Author Zhang Bo
 * @Date 2019/2/26 15:37
 * @Version 1.0
 */
@Service
@Slf4j
public class WorkOrderServiceImpl implements WorkOrderService {

    private static final String CREATE_WORKORDER_AND_SYNC_AFTERSALE_LOCK = "CREATE_WORKORDER_AND_SYNC_AFTERSALE_LOCK";

    @Resource
    private WorkOrderMapper workOrderMapper;
    @Resource
    private OrderMainMapper orderMainMapper;
    @Resource
    private OrderSubMapper orderSubMapper;
    @Resource
    private UserCache userCache;
    @Resource
    private UserFeign userFeign;
    @Resource
    private ProductFeign productFeign;
    @Resource
    private SystemFeign systemFeign;
    @Resource
    private OrderSubService orderSubService;
    @Resource
    private SyncWorkOrderService syncWorkOrderService;
    @Resource
    private WaterFeign waterFeign;
    @Resource
    private WorkOrderOperationMapper workOrderOperationMapper;
    @Resource
    private WorkOrderRefuseMapper workOrderRefuseMapper;
    @Resource
    private ProductIncomeRecordPartMapper productIncomeRecordPartMapper;

    @Resource
    private MailSender mailSender;
    @Resource
    private DomainProperties domainProperties;

    @Resource
    private SubOrderDetailMapper subOrderDetailMapper;

    @Resource
    private InstallerApiService installerApiService;

    @Resource
    private ProductIncomeRecordMapper productIncomeRecordMapper;

    @Resource
    private ProductIncomeRecordService productIncomeRecordService;

    @Resource
    private RabbitTemplate rabbitTemplate;

    @Resource
    private OrderInvoiceMapper orderInvoiceMapper;

    @Resource
    private RedisLock redisLock;

    @Resource
    private RedisCache redisCache;

    @Resource
    private WorkRepairOrderMapper workRepairOrderMapper;

    @Resource
    private MaintenanceWorkOrderMapper maintenanceWorkOrderMapper;

    @Resource
    private MoveWaterDeviceOrderMapper moveWaterDeviceOrderMapper;

    @Resource
    private WorkOrderBackMapper workOrderBackMapper;

    /**
     * 描述：获取安装工某个状态工单的数量
     *
     * @param engineerId 安装工ID
     * @param status     工单状态
     * @Creator Zhang Bo
     * @CreateTime 2019/3/9 12:03
     **/
    @Override
    public Integer countWorkOrderByEngineerId(Integer engineerId, Integer status) {
        WorkOrder query = new WorkOrder();
        query.setEngineerId(engineerId);
        //status为空时，统计安装工的所有工单数量
        if (status != null) {
            query.setStatus(status);
        }
        return workOrderMapper.selectCount(query);
    }

    /**
     * 描述：获取安装工某段时间的工单数量
     *
     * @param engineerId 安装工ID
     * @param startTime  开始时间
     * @param endTime    结束时间
     * @Creator Zhang Bo
     * @CreateTime 2019/3/9 12:03
     **/
    @Override
    public Integer countWorkOrderByEngineerId(Integer engineerId, Date startTime, Date endTime) {
        Example example = new Example(WorkOrder.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("engineerId", engineerId);
        criteria.andGreaterThanOrEqualTo("createTime", startTime);
        criteria.andLessThanOrEqualTo("createTime", endTime);
        return workOrderMapper.selectCountByExample(example);
    }

    /**
     * 描述：获取安装工某段时间某类产品的工单数量
     *
     * @param engineerId 安装工ID
     * @param startTime  开始时间
     * @param endTime    结束时间
     * @param productIds 产品ID集合
     * @Creator Zhang Bo
     * @CreateTime 2019/3/9 12:03
     **/
    @Override
    public Integer countWorkOrderByEngineerId(Integer engineerId, Date startTime, Date endTime, List<Integer> productIds) {
        Example example = new Example(WorkOrder.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("engineerId", engineerId);
        criteria.andGreaterThanOrEqualTo("createTime", startTime);
        criteria.andLessThanOrEqualTo("createTime", endTime);
        criteria.andIn("productId", productIds);
        return workOrderMapper.selectCountByExample(example);
    }

    @Override
    public Integer countWorkOrderByEngineerId(Integer engineerId, Integer status, Date startTime, Date endTime, List<Integer> productIds) {
        Example example = new Example(WorkOrder.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("engineerId", engineerId);
        criteria.andEqualTo("status", status);
        criteria.andGreaterThanOrEqualTo("createTime", startTime);
        criteria.andLessThanOrEqualTo("createTime", endTime);
        criteria.andIn("productId", productIds);
        return workOrderMapper.selectCountByExample(example);
    }

    /**
     * 创建安装工单
     *
     * @param workOrder 工单对象
     */
    @Override
    public void createWorkOrder(WorkOrder workOrder) {
        WorkOrder query = new WorkOrder();
        query.setId(workOrder.getId());
        int count = workOrderMapper.selectCount(query);
        if (count > 0) {
            throw new BadRequestException("该工单号已存在！");
        }
        workOrderMapper.insert(workOrder);
    }

    /**
     * 根据条件查询安装工单信息
     */
    @Override
    public PageVO<WorkOrderDTO> getWorkOrderList(WorkOrderQueryDTO workOrderQueryDTO, Integer pageNum, Integer pageSize) {
        workOrderQueryDTO.setDelStatus("N");
        PageHelper.startPage(pageNum, pageSize);
        Page<WorkOrderDTO> ptPage = workOrderMapper.getWorkOrderList(workOrderQueryDTO);
        return new PageVO<>(pageNum, ptPage);
    }

    /**
     * 描述：根据工单id获取工单信息
     *
     * @param workOrderId 工单ID
     **/
    @Override
    public WorkOrderDTO getWorkOrderById(String workOrderId) {
        WorkOrderDTO dto = new WorkOrderDTO();
        WorkOrder workOrder = workOrderMapper.selectByPrimaryKey(workOrderId);
        if (workOrder != null) {
            workOrder.convert(dto);

            //获取结算月份
            ProductIncomeRecord income = productIncomeRecordMapper.selectProductIncomeRecordByOrderId(workOrder.getSubOrderId(), IncomeType.PRODUCT_INCOME.value);
            if (null != income && null != income.getSettlementMonth()) {
                dto.setSettlementMonth(income.getSettlementMonth());
            }
            //填充下单人和经销商信息
            getOrderInfo(dto, workOrder.getSubOrderId());

        } else {
            dto = null;
        }
        return dto;
    }

    /****
     * 获取下单、收货人、经销商信息填充到工单对象
     * @param dto
     * @param subOrderId
     */
    private void getOrderInfo(WorkOrderDTO dto, Long subOrderId) {
        SubOrderDetail ordersub = subOrderDetailMapper.selectByPrimaryKey(subOrderId);
        if (ordersub != null) {
            if (StringUtil.isNotEmpty(ordersub.getAddresseeName())) {
                dto.setReceiveName(ordersub.getAddresseeName());
            }
            if (StringUtil.isNotEmpty(ordersub.getAddresseePhone())) {
                dto.setReceivePhone(ordersub.getAddresseePhone());
            }
            if (StringUtil.isNotEmpty(ordersub.getAddresseeProvince())) {
                dto.setReceiveProvice(ordersub.getAddresseeProvince());
            }
            if (StringUtil.isNotEmpty(ordersub.getAddresseeCity())) {
                dto.setReceiveCity(ordersub.getAddresseeCity());
            }
            if (StringUtil.isNotEmpty(ordersub.getAddresseeRegion())) {
                dto.setReceiveRegion(ordersub.getAddresseeRegion());
            }
            if (StringUtil.isNotEmpty(ordersub.getAddresseeStreet())) {
                dto.setReceiveAdress(ordersub.getAddresseeStreet());
            }
            if (StringUtil.isNotEmpty(ordersub.getUserTypeName())) {
                dto.setUserTypeName(ordersub.getUserTypeName());
            }

            if (StringUtil.isNotEmpty(ordersub.getUserName())) {
                dto.setCreateUserName(ordersub.getUserName());
            }
            if (StringUtil.isNotEmpty(ordersub.getUserPhone())) {
                dto.setCreateUserPhone(ordersub.getUserPhone());
            }
            if (null != ordersub.getUserId()) {
                dto.setCreateUserId(ordersub.getUserId());
            }

            if (StringUtil.isNotEmpty(ordersub.getEngineerRegion())) {
                dto.setEngineerRegion(ordersub.getEngineerRegion());
            }

        }

        Integer step = dto.getStep();
        Integer payTerminal = dto.getPayTerminal();
        Integer status = dto.getStatus();
        Integer dispatchType = dto.getDispatchType();
        Boolean flag = step != null && step >= 2 && (step != 6 || status != WorkOrderStateEnum.WORKORDER_STATE_NOT_ACCEPTED.state);
        if (flag) {
            dto.setIsTake("已提货");
        } else {
            dto.setIsTake("未提货");
        }

        if (payTerminal == null) {
            dto.setPayTerminalText("");
        } else if (payTerminal == 1) {
            dto.setPayTerminalText("立即支付");//经销商支付->立即支付
        } else if (payTerminal == 2) {
            dto.setPayTerminalText("货到付款");//用户支付->货到付款
        }

        if (dispatchType == null) {
            dto.setDispatchTypeText("");
        } else if (dispatchType == 1) {
            dto.setDispatchTypeText("指定客服");
        } else if (dispatchType == 2) {
            dto.setDispatchTypeText("系统分配");
        }

        Boolean completePay = dto.getCompletePay();
        Boolean needCompletePay = dto.getNeedCompletePay();
        if (completePay != null && (needCompletePay || completePay)) {
            dto.setCompletePayText(completePay ? "已完款" : "未完款");
        } else {
            dto.setCompletePayText("无需完款");
        }
    }

    /**
     * 工单评分内容修改
     *
     * @param workOrderId     工单ID
     * @param appraiseContent 评分内容
     **/
    @Override
    public WorkOrderDTO updateWorkOrderAppraise(String workOrderId, String appraiseContent) {
        WorkOrderDTO dto = new WorkOrderDTO();
        WorkOrder workOrder = workOrderMapper.selectByPrimaryKey(workOrderId);
        if (workOrder == null) {
            throw new BadRequestException("该工单号不存在！");
        }
        workOrder.setUserAppraiseTime(new Date());
        workOrder.setUserAppraiseStatus(StatusEnum.YES.value());
        workOrder.setUserAppraiseContent(appraiseContent);
        workOrder.setUpdateTime(new Date());
        int result = workOrderMapper.updateByPrimaryKeySelective(workOrder);
        if (result < 1) {
            throw new BadRequestException("修改工单评分内容时失败！");
        }
        workOrder.convert(dto);
        return dto;
    }

    /**
     * 工单评分等级修改
     *
     * @param workOrderId 工单ID
     * @param levelId     评分等级
     **/
    @Override
    public WorkOrderDTO updateDistributorApprise(String workOrderId, Integer levelId) {
        WorkOrderDTO dto = new WorkOrderDTO();
        WorkOrder workOrder = workOrderMapper.selectByPrimaryKey(workOrderId);
        if (workOrder == null) {
            throw new BadRequestException("该工单号不存在！");
        }
        workOrder.setDistributorAppraiseTime(new Date());
        workOrder.setDistributorAppraiseStatus(StatusEnum.YES.value());
        workOrder.setOperationTime(new Date());
        workOrder.setUpdateTime(new Date());
        switch (levelId) {
            case 1:
                workOrder.setDistributorAppraiseContent("好");
                break;
            case 2:
                workOrder.setDistributorAppraiseContent("一般");
                break;
            case 3:
                workOrder.setDistributorAppraiseContent("差");
                break;
            default:
                workOrder.setDistributorAppraiseContent("");
        }
        int result = workOrderMapper.updateByPrimaryKeySelective(workOrder);
        if (result < 1) {
            throw new BadRequestException("修改工单评分等级失败！");
        }
        workOrder.convert(dto);
        return dto;

    }

    /**
     * 拒单后重新指派安装工
     *
     * @param workOrderId 工单ID
     * @param engineerId  安装工id
     **/
    @Override
    public WorkOrderDTO updateWorkOrderEngineer(String workOrderId, Integer engineerId) {
        WorkOrderDTO dto = new WorkOrderDTO();
        WorkOrder workOrder = workOrderMapper.selectByPrimaryKey(workOrderId);
        if (workOrder == null) {
            throw new BadRequestException("该工单号不存在！");
        }
        EngineerDTO engineer = userFeign.getEngineerById(engineerId);
        if (engineer == null) {
            throw new BadRequestException("该安装工不存在！");
        }
        workOrder.setEngineerId(engineer.getId());
        workOrder.setEngineerName(engineer.getRealName());
        workOrder.setEngineerPhone(engineer.getPhone());
        workOrder.setEngineerIdCard(engineer.getIdCard());
        workOrder.setStationId(engineer.getStationId());
        workOrder.setStationName(engineer.getStationName());
        workOrder.setNotRefuse(StatusEnum.YES.value());
        workOrder.setUpdateTime(new Date());
        int result = workOrderMapper.updateByPrimaryKeySelective(workOrder);
        if (result < 1) {
            throw new BadRequestException("修改重新指派的安装工信息失败！");
        }

        OrderSub order = new OrderSub();
        order.setId(workOrder.getSubOrderId());
        order.setEngineerId(engineer.getId());
        orderSubMapper.updateByPrimaryKeySelective(order);

        //更新子订单详情
        SubOrderDetail detail = new SubOrderDetail();
        detail.setEngineerId(engineer.getId());
        detail.setEngineerName(engineer.getRealName());
        detail.setEngineerPhone(engineer.getPhone());
        detail.setEngineerIdCard(engineer.getIdCard());
        detail.setEngineerCity(engineer.getCity());
        detail.setEngineerProvince(engineer.getProvince());
        detail.setEngineerRegion(engineer.getRegion());
        detail.setSubOrderId(workOrder.getSubOrderId());
        subOrderDetailMapper.updateByPrimaryKeySelective(detail);

        //修改收益安装工  2020-04-21 修改
        ProductIncomeRecordPart part = productIncomeRecordPartMapper.selectPartIdBySubOrderId(workOrder.getSubOrderId());
        if (part != null) {
            part.setSubjectId(engineer.getId());
            part.setSubjectName(engineer.getRealName());
            part.setSubjectPhone(engineer.getPhone());
            part.setSubjectIdCard(engineer.getIdCard());
            productIncomeRecordPartMapper.updateByPrimaryKeySelective(part);
        }

        workOrder.convert(dto);
        return dto;

    }

    /**
     * 修改安装工单
     *
     * @param dto 工单对象
     */
    @Override
    public WorkOrderDTO updateWorkOrder(WorkOrderDTO dto) {
        if (dto.getId() == null) {
            throw new BadRequestException("该工单号参数不能为空！");
        }
        WorkOrder query = new WorkOrder();
        query.setId(dto.getId());
        int count = workOrderMapper.selectCount(query);
        if (count < 1) {
            throw new BadRequestException("该工单号不存在！");
        }
        WorkOrder workOrder = new WorkOrder(dto);
        workOrder.setUpdateTime(new Date());
        int result = workOrderMapper.updateByPrimaryKeySelective(workOrder);
        if (result < 1) {
            log.error("修改工单失败！");
            return null;
        }

        //修改收益安装工  2020-04-21 修改
        ProductIncomeRecordPart part = productIncomeRecordPartMapper.selectPartIdBySubOrderId(workOrder.getSubOrderId());
        if (part != null && !Objects.equals(part.getSubjectId(), workOrder.getEngineerId())) {
            EngineerDTO engineer = userFeign.getEngineerById(workOrder.getEngineerId());
            if (engineer != null) {
                OrderSub order = new OrderSub();
                order.setId(workOrder.getSubOrderId());
                order.setEngineerId(engineer.getId());
                orderSubMapper.updateByPrimaryKeySelective(order);

                //更新子订单详情
                SubOrderDetail detail = new SubOrderDetail();
                detail.setEngineerId(engineer.getId());
                detail.setEngineerName(engineer.getRealName());
                detail.setEngineerPhone(engineer.getPhone());
                detail.setEngineerIdCard(engineer.getIdCard());
                detail.setEngineerCity(engineer.getCity());
                detail.setEngineerProvince(engineer.getProvince());
                detail.setEngineerRegion(engineer.getRegion());
                detail.setSubOrderId(workOrder.getSubOrderId());
                subOrderDetailMapper.updateByPrimaryKeySelective(detail);

                part.setSubjectId(engineer.getId());
                part.setSubjectName(engineer.getRealName());
                part.setSubjectPhone(engineer.getPhone());
                part.setSubjectIdCard(engineer.getIdCard());
                productIncomeRecordPartMapper.updateByPrimaryKeySelective(part);
            }
        }

        return dto;
    }

    /**
     * 修改安装工单局部字段
     */
    @Override
    public WorkOrderDTO updateWorkOrderPart(WorkOrderDTO dto) {
        if (dto.getId() == null) {
            throw new BadRequestException("工单号不能为空");
        }
        WorkOrder query = new WorkOrder();
        query.setId(dto.getId());
        int count = workOrderMapper.selectCount(query);
        if (count < 1) {
            throw new BadRequestException("工单不存在");
        }
        WorkOrder update = new WorkOrder(dto);
        update.setUpdateTime(new Date());
        //局部更新
        int result = workOrderMapper.updateByPrimaryKeySelective(update);
        if (result < 1) {
            log.error("局部更新工单失败，工单号={}", dto.getId());
            return null;
        }
        return this.getWorkOrderById(dto.getId());
    }

    /**
     * 云平台--根据条件查询工单列表信息
     */
    @Override
    public PageVO<WorkOrderResultDTO> selectWorkOrderList(WorkOrderQueryDTO workOrderQueryDTO, Integer pageNum, Integer pageSize) {
        //工单查询条件转换
        workOrderQueryDTO = this.workOrderQuery(workOrderQueryDTO);
        PageHelper.startPage(pageNum, pageSize);
        Page<WorkOrderResultDTO> ptPage = workOrderMapper.getWorkOrderListBypage(workOrderQueryDTO);
        if (CollectionUtil.isNotEmpty(ptPage)) {
            for (WorkOrderResultDTO n : ptPage.getResult()) {
                //云平台通过type、scanCodeType组合来鉴别工单来源,微服务通过：workOrderIntallType、scanCodeType（比云平台多了经销商app下单）
                if (n.getType() != null) {
                    if (n.getType() == 1) {
                        n.setTerminalText(ScanCodeTypeEnum.DISTRIBUTOR.getDescribe());
                    } else {
                        if (n.getScanCodeType() == null) {
                            n.setTerminalText("");
                        } else if (n.getScanCodeType() == ScanCodeTypeEnum.LIVE.getStatus()) {
                            n.setTerminalText(ScanCodeTypeEnum.LIVE.getDescribe());
                        } else if (n.getScanCodeType() == ScanCodeTypeEnum.PERSON.getStatus()) {
                            n.setTerminalText(ScanCodeTypeEnum.PERSON.getDescribe());
                        } else if (n.getScanCodeType() == ScanCodeTypeEnum.SOFT.getStatus()) {
                            n.setTerminalText(ScanCodeTypeEnum.SOFT.getDescribe());
                        } else if (n.getScanCodeType() == ScanCodeTypeEnum.MALL.getStatus()) {
                            n.setTerminalText(ScanCodeTypeEnum.MALL.getDescribe());
                        } else if (n.getScanCodeType() == ScanCodeTypeEnum.DISTRIBUTOR.getStatus()) {
                            n.setTerminalText(ScanCodeTypeEnum.DISTRIBUTOR.getDescribe());
                        } else {
                            n.setTerminalText("");
                        }
                    }
                }
            }
        }
        return new PageVO<>(pageNum, ptPage);
    }

    /**
     * 云平台导出工单信息
     */
    @Override
    public Page<WorkOrderExportDTO> exportWorkOrderList(WorkOrderQueryDTO query, Integer pageNum, Integer pageSize) {

        //工单查询条件转换
        PageHelper.startPage(pageNum, pageSize);
        return workOrderMapper.getWorkOrderListExport(query);
    }

    /***
     * 获取产品类目
     * @param dto
     * @param workOrder
     */
    private void setCategoryNameInfo(WorkOrderExportDTO dto, WorkOrderDTO workOrder, ProductDTO product) {
        if (null != product) {
            ProductCategoryCascadeDTO category = productFeign.getProductCategoryCascadeById(product.getCategoryId());
            if (category != null) {
                dto.setFirstCategoryName(category.getProductCategoryFirstName());
                dto.setTwoCategoryName(category.getProductCategoryTwoName());
                dto.setThreeCategoryName(category.getProductCategoryName());
            }
        }

    }

    /**
     * 工单查询条件转换
     */
    public WorkOrderQueryDTO workOrderQuery(WorkOrderQueryDTO workOrderQueryDTO) {
        if (workOrderQueryDTO == null) {
            return null;
        }
        if (workOrderQueryDTO.getOperationType() == WorkOrderOperationType.WORK_ORDER.value) {
            //支付状态：1、未支付；2、待审核；3、支付成功；4、支付失败
            if (workOrderQueryDTO.getStatus() == null || workOrderQueryDTO.getStatus() == WorkOrderStateEnum.STATUS.state) {
                workOrderQueryDTO.setStatus(null);
            } else if (workOrderQueryDTO.getStatus() == WorkOrderStateEnum.PAY.state) {
                workOrderQueryDTO.setStatus(null);
                workOrderQueryDTO.setPay(true);
                workOrderQueryDTO.setIsBackWorkOrder(StatusEnum.NO.value());
            } else if (workOrderQueryDTO.getStatus() == WorkOrderStateEnum.AUDITING.state) {
                workOrderQueryDTO.setStatus(WorkOrderStateEnum.WORKORDER_STATE_SERVING.state);
                workOrderQueryDTO.setPayStatus(PayStatus.WAITING_AUDIT.value);//原值：1
            } else if (workOrderQueryDTO.getStatus() == WorkOrderStateEnum.NOT_PASS.state) {
                workOrderQueryDTO.setStatus(WorkOrderStateEnum.WORKORDER_STATE_SERVING.state);
                workOrderQueryDTO.setPayStatus(PayStatus.FAIL.value);//原值：2
            } else if (workOrderQueryDTO.getStatus() == WorkOrderStateEnum.WAITING_PAY.state) {
                workOrderQueryDTO.setStatus(null);
                workOrderQueryDTO.setPay(false);
                workOrderQueryDTO.setIsBackWorkOrder(StatusEnum.NO.value());
            } else if (workOrderQueryDTO.getStatus() == WorkOrderStateEnum.CUSTOMER_REJECT.state) {
                workOrderQueryDTO.setStatus(null);
                workOrderQueryDTO.setNotRefuse(StatusEnum.YES.value());
            } else if (workOrderQueryDTO.getStatus() == WorkOrderStateEnum.CUSTOMER_DISPATCH.state) {
                workOrderQueryDTO.setStatus(WorkOrderStateEnum.WORKORDER_STATE_NOT_ACCEPTED.state);
                workOrderQueryDTO.setIsNotAllot(true);
            } else {
                workOrderQueryDTO.setStatus(workOrderQueryDTO.getStatus());
            }
        } else if (workOrderQueryDTO.getOperationType() == WorkOrderOperationType.CANCEL.value) {
            workOrderQueryDTO.setIsBackWorkOrder(StatusEnum.YES.value());
            workOrderQueryDTO.setStatus(null);
        } else if (workOrderQueryDTO.getOperationType() == WorkOrderOperationType.DELETE.value) {
            workOrderQueryDTO.setStatus(null);
        } else if (WorkOrderOperationType.find(workOrderQueryDTO.getOperationType()) == null) {
            throw new BadRequestException("没有此类型的操作！");
        }
        if (workOrderQueryDTO.getType() != null && -1 != workOrderQueryDTO.getType()) {
            if (workOrderQueryDTO.getType() == 0) {
                workOrderQueryDTO.setType(WorkOrderInstallTypeEnum.JXS_APP_ORDER.getType());
            } else {
                workOrderQueryDTO.setScanCodeType(workOrderQueryDTO.getType());
            }
        } else {
            workOrderQueryDTO.setType(null);
        }
        workOrderQueryDTO.setDelStatus("N");//业务系统查询和导出未删除的工单列表
        return workOrderQueryDTO;
    }

    /**
     * 云平台删除工单
     **/
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
    public void deleteWorkOrder(String workOrderId) {
        Integer adminId = userCache.getUserId();
        AdminDTO admin = userFeign.getAdminDTOById(adminId);
        WorkOrder workOrder = workOrderMapper.selectByPrimaryKey(workOrderId);
        if (workOrder == null) {
            throw new BadRequestException("该工单号不存在！");
        }
        Map<String, Object> ru = new HashMap<>();
        try {
            ru = BaideApiUtil.delete(workOrder.getId(), "", "");
            if (BaideApiUtil.success(ru)) {
                //TODO 红包相关
                this.operation(workOrder);
                log.debug("删除工单:  管理员：" + admin.getRealName() + ",工单号：" + workOrder.getId());
                workOrder.setDeleteTime(new Date());
                workOrder.setStep(0);
                workOrder.setDelStatus("Y");//已删除
                workOrder.setUpdateTime(new Date());
                workOrderMapper.updateByPrimaryKey(workOrder);
            } else {
                throw new YimaoException("售后系统删除失败");
            }
        } catch (Exception var8) {
            throw new YimaoException("售后系统删除失败");
        }

    }

    private void operation(WorkOrder workOrder) {
        if (workOrder.getProductId() == null) {
            log.error("删除工单失败，没有获取到产品信息");
            return;
        }
        if (workOrder.getDeviceId() != null) {
            WaterDeviceDTO device = waterFeign.getWaterDeviceById(workOrder.getDeviceId());
            if (device != null) {
                waterFeign.deleteWaterDevice(device.getId());
                //同步微服务时是业务删除
//                SyncDevice.del(device.getId());
            }
        }
        boolean flag = workOrder.getStep() < 2 || workOrder.getPayTerminal() == 1 && workOrder.getStep() == 6;
        if (!flag) {
            DistributorDTO distributor = userFeign.getDistributorById(workOrder.getDistributorId());
            if (distributor == null) {
                throw new BadRequestException(workOrder.getDistributorId() + "该经销商不存在！");
            }
            Integer remainingQuota = distributor.getRemainingQuota();
            if (remainingQuota == null) {
                remainingQuota = 0;
            }
            distributor.setRemainingQuota(remainingQuota + 1);
            userFeign.updateDistributor(distributor);

            //删除工单，要把库存还回去
            Map<String, Object> map = new HashMap<>();
            map.put("province", workOrder.getProvince());
            map.put("city", workOrder.getCity());
            map.put("region", workOrder.getRegion());
            map.put("productModel", workOrder.getDeviceModel());
            map.put("count", 1);

            // if (distributor.getUserName().toLowerCase().contains("clxd") && DistributorRoleLevel.DISCOUNT.value == distributor.getRoleLevel()) {
            //     map.put("special", 1);
            // } else {
            map.put("special", 0);
            // }
            rabbitTemplate.convertAndSend(RabbitConstant.INCREASE_OR_DECREASE_STOCK, map);
        }

        //安装工正在服务的工单数减1
        Map<String, Integer> map = new HashMap<>();
        map.put("engineerId", workOrder.getEngineerId());
        map.put("num", -1);
        rabbitTemplate.convertAndSend(RabbitConstant.ENGINEER_BUSY_COUNT, map);
    }

    /**
     * 云平台分配安装工
     *
     * @param workOrderId 工单ID
     * @param engineerId  安装工ID
     **/
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
    public void allotEngineer(String workOrderId, Integer engineerId) {
        WorkOrder workOrder = workOrderMapper.selectByPrimaryKey(workOrderId);

        //判断工单状态是否已分配
        if (workOrder == null || !workOrder.getDispatch()) {
            throw new BadRequestException("工单号不存在或不需要分配");
        }
        EngineerDTO engineer = userFeign.getEngineerById(engineerId);
        if (engineer == null) {
            throw new BadRequestException("该安装工不存在！");
        }
        //如果是后台人工进行派单的话，那么安装工是不能拒单的
        workOrder.setNotRefuse(StatusEnum.YES.value());
        workOrder.setEngineerId(engineer.getId());
        workOrder.setEngineerName(engineer.getRealName());
        workOrder.setEngineerPhone(engineer.getPhone());
        workOrder.setEngineerIdCard(engineer.getIdCard());
        workOrder.setStationId(engineer.getStationId());
        workOrder.setStationName(engineer.getStationName());
        workOrder.setDispatch(false);
        // workOrder.setServiceEngineerId(engineer.getId());
        // workOrder.setServiceEngineerName(engineer.getRealName());
        // workOrder.setServiceEngineerPhone(engineer.getPhone());
        // workOrder.setServiceEngineerIdCard(engineer.getIdCard());
        workOrder.setOldEngineerId(engineer.getOldId());
        int result = workOrderMapper.updateByPrimaryKeySelective(workOrder);
        if (result < 1) {
            throw new BadRequestException("修改分配安装工信息失败！");
        }
        OrderSub order = new OrderSub();
        order.setId(workOrder.getSubOrderId());
        order.setEngineerId(engineer.getId());
        orderSubMapper.updateByPrimaryKeySelective(order);

        //更新子订单详情
        SubOrderDetail detail = new SubOrderDetail();
        detail.setEngineerId(engineer.getId());
        detail.setEngineerName(engineer.getRealName());
        detail.setEngineerPhone(engineer.getPhone());
        detail.setEngineerIdCard(engineer.getIdCard());
        detail.setEngineerCity(engineer.getCity());
        detail.setEngineerProvince(engineer.getProvince());
        detail.setEngineerRegion(engineer.getRegion());
        detail.setSubOrderId(workOrder.getSubOrderId());
        subOrderDetailMapper.updateByPrimaryKeySelective(detail);

        //修改收益安装工  2020-04-21 修改
        ProductIncomeRecordPart part = productIncomeRecordPartMapper.selectPartIdBySubOrderId(workOrder.getSubOrderId());
        if (part != null) {
            part.setSubjectId(engineer.getId());
            part.setSubjectName(engineer.getRealName());
            part.setSubjectPhone(engineer.getPhone());
            part.setSubjectIdCard(engineer.getIdCard());
            productIncomeRecordPartMapper.updateByPrimaryKeySelective(part);
        }

        //同步售后,推送消息给安装工
        afterAllotEngineer(workOrder, engineer);

    }

    /***
     * //同步售后,推送消息给安装工
     * @param workOrder
     * @param engineer
     */
    private void afterAllotEngineer(WorkOrder workOrder, EngineerDTO engineer) {

        //同步售后
        try {
            rabbitTemplate.convertAndSend(RabbitConstant.SYNC_WORK_ORDER, workOrder.getId());
        } catch (Exception e) {
            log.error("========(workOrderId=" + workOrder.getId() + ")客服分配安装工同步售后异常=========" + e.getMessage());
            throw new BadRequestException("修改分配安装工信息失败！");
        }

        //推送安装工消息
        try {
            pushMessage(workOrder, MessageModelTypeEnum.WATER_ORDER.value);
        } catch (Exception e) {
            log.error("=================(workOrderId=" + workOrder.getId() + ")客服分配安装工推送安装工消息异常=========" + e.getMessage());
        }

    }

    /**
     * 云平台工单评价
     **/
    @Override
    public void updateWorkOrderAppraiseScore(String workOrderId, Integer score, String appraiseContent) {
        WorkOrder workOrder = workOrderMapper.selectByPrimaryKey(workOrderId);
        if (workOrder == null) {
            throw new BadRequestException("该工单号不存在！");
        }
        workOrder.setUserAppraiseTime(new Date());
        workOrder.setUserAppraiseStatus(StatusEnum.YES.value());
        workOrder.setUserScore(score);
        workOrder.setUserAppraiseContent(appraiseContent);
        int result = workOrderMapper.updateByPrimaryKeySelective(workOrder);
        if (result < 1) {
            throw new BadRequestException("工单评价失败！");
        }
    }

    /**
     * 描述：云平台工单概况
     **/
    @Override
    public List<WorkOrderCountDTO> countWorkOrderByStatus() {
        List<WorkOrderCountDTO> list = workOrderMapper.countWorkOrderByStatus();
        if (CollectionUtil.isNotEmpty(list)) {
            for (WorkOrderCountDTO dto : list) {
                if (dto != null) {
                    if (dto.getStatus() == WorkOrderStateEnum.WORKORDER_STATE_NOT_ACCEPTED.state) {
                        dto.setStatusText(WorkOrderStateEnum.WORKORDER_STATE_NOT_ACCEPTED.stateText);
                    } else if (dto.getStatus() == WorkOrderStateEnum.WORKORDER_STATE_ACCEPTED.state) {
                        dto.setStatusText(WorkOrderStateEnum.WORKORDER_STATE_ACCEPTED.stateText);
                    } else if (dto.getStatus() == WorkOrderStateEnum.WORKORDER_STATE_SERVING.state) {
                        dto.setStatusText(WorkOrderStateEnum.WORKORDER_STATE_SERVING.stateText);
                    } else if (dto.getStatus() == WorkOrderStateEnum.WORKORDER_STATE_COMPLETE.state) {
                        dto.setStatusText(WorkOrderStateEnum.WORKORDER_STATE_COMPLETE.stateText);
                    } else {
                        dto.setStatusText("其他");
                    }
                }
            }
        }
        return list;
    }

    /**
     * 描述：云平台工单趋势
     **/
    @Override
    public List<WorkOrderCountDTO> countWorkOrderByCreateTime(String days, Date startTime, Date endTime) {
        WorkOrderQueryDTO query = new WorkOrderQueryDTO();
        query.setStartTime(startTime);
        query.setEndTime(endTime);
        //默认时根据天数参数间隔时间统计，否则根据起止时间参数统计
        if (days != null && Integer.valueOf(days) > 0) {
            query.setStartTime(DateUtil.dayAfter(new Date(), -Integer.valueOf(days)));
            query.setEndTime(new Date());
            return workOrderMapper.countWorkOrderByCreateTime(query);
        } else {
            return workOrderMapper.countWorkOrderByCreateTime(query);
        }
    }

    /**
     * 描述：云平台根据条件查询安装工单支付信息
     */
    @Override
    public PageVO<PayRecordDTO> getWorkOrderPayList(WorkOrderQueryDTO workOrderQueryDTO, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        Page<PayRecordDTO> ptPage = workOrderMapper.getWorkOrderPayList(workOrderQueryDTO);
        return new PageVO<>(pageNum, ptPage);
    }

    /**
     * 描述：云平台--工单退单
     *
     * @param id 工单ID
     **/
    @Override
    public void backOrderUpdate(String id) {
        WorkOrder workOrder = workOrderMapper.selectByPrimaryKey(id);
        if (workOrder == null) {
            throw new BadRequestException("该工单号不存在！");
        }
        this.operation(workOrder);
        Date now = new Date();
        workOrder.setCompleteType(WorkOrderCompleteEnum.AUTO_COMPLETE.getState());
        workOrder.setCompleteTime(now);
        workOrder.setChargeback(true);
        workOrder.setChargebackType(3);//1-经销商退单；2-客服退单
        workOrder.setChargebackTime(now);
        int result = workOrderMapper.updateByPrimaryKey(workOrder);
        if (result < 1) {
            throw new YimaoException("工单退单确认失败！");
        }
    }

    /**
     * 根据条件查询工单发票列表信息
     */
    @Override
    public PageVO<WorkOrderDTO> getWorkOrderInvoiceList(WorkOrderQueryDTO workOrderQueryDTO, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        Page<WorkOrderDTO> ptPage = workOrderMapper.getWorkOrderInvoiceList(workOrderQueryDTO);
        return new PageVO<>(pageNum, ptPage);
    }

    /**
     * 云平台导出工单发票信息
     */
    @Override
    public Page<WorkOrderInvoiceExportDTO> exportWorkOrderInvoiceList(WorkOrderQueryDTO workOrderQueryDTO, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        return workOrderMapper.exportWorkOrderInvoiceList(workOrderQueryDTO);
//        Page<WorkOrderInvoiceExportDTO> page = workOrderMapper.exportWorkOrderInvoiceList(workOrderQueryDTO);
//        Page<WorkOrderInvoiceExportDTO> list = new Page<>();
//        WorkOrderInvoiceExportDTO dto;
//        for (WorkOrderDTO workOrder : page) {
//            dto = new WorkOrderInvoiceExportDTO();
//            dto.setWorkOrderId(workOrder.getId());
//            dto.setProvince(workOrder.getProvince());
//            dto.setCity(workOrder.getCity());
//            dto.setRegion(workOrder.getRegion());
//            //服务工程师信息
//            dto.setEngineerName(workOrder.getServiceEngineerName());
//            dto.setEngineerPhone(workOrder.getServiceEngineerPhone());
//            dto.setUserRealName(workOrder.getUserName());
//            dto.setUserPhone(workOrder.getUserPhone());
//            dto.setDeviceModel(workOrder.getDeviceModel());
//            dto.setCostName(workOrder.getCostName());
//            dto.setAmountFee(workOrder.getFee());
//            dto.setCreateTime(DateUtil.getDateToString(workOrder.getCreateTime(), "yyyy-MM-dd HH:mm:ss"));
//            dto.setPayTime(DateUtil.getDateToString(workOrder.getPayTime(), "yyyy-MM-dd HH:mm:ss"));
//            //开票信息
//            dto.setApplyTime(DateUtil.getDateToString(workOrder.getBillTime(), "yyyy-MM-dd HH:mm:ss"));
//            if (workOrder.getInvoiceType() == null) {
//                dto.setInvoiceType("");
//            } else if (workOrder.getInvoiceType() == InvoiceTypeEnum.ORDINARY.value) {
//                dto.setInvoiceType(InvoiceTypeEnum.ORDINARY.name);
//            } else if (workOrder.getInvoiceType() == InvoiceTypeEnum.RENEW.value) {
//                dto.setInvoiceType(InvoiceTypeEnum.RENEW.name);
//            }
//            if (workOrder.getInvoiceHead() == null) {
//                dto.setInvoiceHead("");
//            } else if (workOrder.getInvoiceHead() == InvoiceHeadEnum.COMPANY.value) {
//                dto.setInvoiceHead(InvoiceHeadEnum.COMPANY.name);
//            } else if (workOrder.getInvoiceHead() == InvoiceHeadEnum.ONE.value) {
//                dto.setInvoiceHead(InvoiceHeadEnum.ONE.name);
//            }
//            dto.setCompanyName(workOrder.getCompanyName());
//            dto.setCompanyAddress(workOrder.getBillAddress());
//            dto.setCompanyPhone(workOrder.getBillPhone());
//            dto.setBankName(workOrder.getBankName());
//            dto.setBankAccount(workOrder.getBankAccount());
//            dto.setDutyNo(workOrder.getDutyNo());
//            dto.setConfirmTime(DateUtil.getDateToString(workOrder.getConfirmTime(), "yyyy-MM-dd HH:mm:ss"));
//            list.add(dto);
//        }
//        list.setPages(page.getPages());
    }

    /**
     * @description 根据工单id查询工单操作记录列表
     */
    @Override
    public PageVO<WorkOrderOperationDTO> getWorkOrderOperationList(String id, Integer pageNum, Integer pageSize) {
        boolean exists = workOrderMapper.existsWithPrimaryKey(id);
        if (!exists) {
            throw new NotFoundException("该工单号不存在！");
        }
        Example example = new Example(WorkOrderOperation.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("workOrderId", id);
        PageHelper.startPage(pageNum, pageSize);
        Page<WorkOrderOperation> page = (Page<WorkOrderOperation>) workOrderOperationMapper.selectByExample(example);
        return new PageVO<>(pageNum, page, WorkOrderOperation.class, WorkOrderOperationDTO.class);
    }

    /**
     * 根据状态查询安装工单信息
     */
    @Override
    public List<WorkOrderDTO> getWorkOrderCompleteList(Integer engineerId, String consumerName, String consumerPhone, String deviceSncode, Integer Status) {
        WorkOrderQueryDTO query = new WorkOrderQueryDTO();
        query.setStatus(Status);
        query.setEngineerId(engineerId);
        query.setUserName(consumerName);
        query.setUserPhone(consumerPhone);
        query.setSnCode(deviceSncode);
        query.setDelStatus("N");//未删除
        List<WorkOrderDTO> list = workOrderMapper.getWorkOrderList(query);
        //部分信息取水机信息的
        getWaterDeviceData(list);
        return list;
    }

    private void getWaterDeviceData(List<WorkOrderDTO> list) {
        if (!list.isEmpty()) {
            for (WorkOrderDTO work : list) {
                if (null != work.getDeviceId()) {
                    WaterDeviceDTO device = waterFeign.getWaterDeviceById(work.getDeviceId());
                    if (null != device) {
                        if (!StringUtil.isEmpty(device.getSn())) {
                            work.setSn(device.getSn());
                        }
                        if (!StringUtil.isEmpty(device.getIccid())) {
                            work.setSimCard(device.getIccid());
                        }
                        if (!StringUtil.isEmpty(device.getLogisticsCode())) {
                            work.setLogisticsCode(device.getLogisticsCode());
                        }
                        if (!StringUtil.isEmpty(device.getDeviceUserPhone())) {
                            work.setUserPhone(device.getDeviceUserPhone());
                        }
                        if (!StringUtil.isEmpty(device.getDeviceUserName())) {
                            work.setUserName(device.getDeviceUserName());
                        }

                    }
                }
            }
        }

    }

    @Override
    public List<EngineerWorkOrderVO> listWorkOrderByEngineerId(Integer engineerId) {
        return workOrderMapper.selectWorkOrderByEngineerId(engineerId);
    }

    @Override
    public void updateLasteFinishTime(Integer hour) {
        workOrderMapper.updateLasteFinishTime(hour);
    }

    /**
     * 描述：根据签约单号获取工单
     *
     * @param signOrderId 签约单号
     **/
    @Override
    public WorkOrderDTO getWorkOrderBySignOrderId(String signOrderId) {
        WorkOrder workOrder = workOrderMapper.selectSignInfoBySignOrderId(signOrderId);
        if (workOrder != null) {
            WorkOrderDTO dto = new WorkOrderDTO();
            workOrder.convert(dto);
            return dto;
        }
        return null;
    }

    /**
     * 根据用户手机号查询正常完成的工单
     *
     * @param mobile 手机号
     */
    @Override
    public WorkOrderDTO getCompletedWorkOrderByUserPhone(String mobile) {
        List<WorkOrderDTO> workOrderList = workOrderMapper.selectCompletedWorkOrderByUserPhone(mobile);
        if (CollectionUtil.isEmpty(workOrderList)) {
            return null;
        }
        return workOrderList.get(0);
    }

    @Override
    public Boolean checkWorkOrder180ComplatePay(String completeOutTradeNo) {
        return workOrderMapper.checkWorkOrder180ComplatePay(completeOutTradeNo);
    }

    @Override
    public List<WorkOrderDTO> getWorkOrderListByquery(Integer engineerId, Integer status, Integer payStatus, Integer completeType) {
        WorkOrderQueryDTO query = new WorkOrderQueryDTO();
        query.setDelStatus("N");
        query.setEngineerId(engineerId);
        if (null != status) {
            query.setStatus(status);
        }
        if (null != payStatus) {
            query.setPayStatus(payStatus);
        }
        if (null != completeType) {
            query.setCompleteType(completeType);
        }
        log.info("工单查询参数=" + JSON.toJSONString(query));
        return workOrderMapper.getWorkOrderList(query);
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
    public Map<String, Object> generateWorkOrderAndSyncaAfterSale(OrderSub subOrder, SubOrderDetail subOrderDetail) {

        Map<String, Object> ru = null;
        try {
            redisLock.lock(CREATE_WORKORDER_AND_SYNC_AFTERSALE_LOCK);

            Integer costId = subOrderDetail.getCostId();// 计费code
            String userProvince = subOrderDetail.getAddresseeProvince();// 省份
            String userCity = subOrderDetail.getAddresseeCity();// 城市
            String userRegion = subOrderDetail.getAddresseeRegion();// 区
            String address = subOrderDetail.getAddresseeStreet();// 具体地址
            Integer distributorId = subOrder.getDistributorId();
            String time = "";
            if (null != subOrderDetail.getServiceTime()) {
                time = DateUtil.transferDateToString(subOrderDetail.getServiceTime());// 预约安装时间
            }
            String remark = subOrder.getRemark();
            Integer payTerminal = subOrder.getPayTerminal();
            String name = subOrderDetail.getAddresseeName();
            String phone = subOrderDetail.getAddresseePhone();
            Integer count = subOrder.getCount();
            Integer subDistributorId = subOrderDetail.getSubDistributorId();
            String childDistributorId = null;
            String childDistributorName = null;
            String childDistributorAccount = null;
            ru = Maps.newHashMap();

            //经销商id不能为空
            if (distributorId == null) {
                ResultUtil.error(ru, "生成工单失败，订单经销商信息不完整!");
                return ru;
            }
            DistributorDTO distributor = userFeign.getDistributorById(distributorId);
            DistributorDTO subDistributor = null;

            // 判断是否是经销商主账号
            if (distributor == null) {
                ResultUtil.error(ru, "生成工单失败，未获取到经销商信息");
                return ru;
            }
            if (subDistributorId != null) {
                subDistributor = userFeign.getDistributorById(subDistributorId);
                if (subDistributor != null) {
                    childDistributorId = subDistributor.getOldId();
                    childDistributorName = subDistributor.getRealName();
                    childDistributorAccount = subDistributor.getUserName();
                }
            }

            ProductCostDTO cost = productFeign.productCostGetById(costId);
            if (cost == null || cost.getDeleted() != null && cost.getDeleted()) {
                ResultUtil.error(ru, "计费方式为空");
                return ru;
            }
            OnlineAreaDTO onlineArea = systemFeign.getOnlineAreaByPCR(userProvince, userCity, userRegion);
            boolean isOldOrder = onlineArea == null;

            // 验证经销商是否已禁止下单
            if (distributor.getForbiddenOrder() == null || distributor.getForbiddenOrder()) {
                ResultUtil.error(ru, "下单是否禁止参数为空！或者已禁止下单");
                return ru;
            }

            WaterDeviceUserDTO dto = new WaterDeviceUserDTO();
            dto.setRealName(subOrderDetail.getAddresseeName());
            dto.setPhone(subOrderDetail.getAddresseePhone());
            dto.setProvince(subOrderDetail.getAddresseeProvince());
            dto.setCity(subOrderDetail.getAddresseeCity());
            dto.setRegion(subOrderDetail.getAddresseeRegion());
            dto.setAddress(subOrderDetail.getAddresseeStreet());
            dto.setSex(subOrderDetail.getAddresseeSex());
//        dto.setCustomerType(1);//默认为个人
            dto.setType(1);
            dto.setOrigin(7);
            dto.setOriginTerminal(Terminal.WATER_DEVICE.value);
            dto.setDistributorId(subOrderDetail.getDistributorId());
            dto.setDistributorAccount(subOrderDetail.getDistributorAccount());
            dto.setDistributorName(subOrderDetail.getDistributorName());
            dto.setDistributorPhone(subOrderDetail.getDistributorPhone());

            dto.setRoleName(distributor.getRoleName());
            dto.setDistributorIdCard(distributor.getIdCard());
            dto.setDistributorProvince(distributor.getProvince());
            dto.setDistributorCity(distributor.getCity());
            dto.setDistributorRegion(distributor.getRegion());
            dto.setDistributorAddress(distributor.getAddress());

            if (null != subDistributor) {
                dto.setChildDistributorId(subDistributor.getId());
                dto.setChildDistributorAccount(subDistributor.getUserName());
                dto.setChildDistributorName(subDistributor.getRealName());
                dto.setChildDistributorPhone(subDistributor.getPhone());
            }
            dto.setCreateTime(new Date());
            WaterDeviceUserDTO deviceUser = userFeign.saveOrGetWaterDeviceUserByPhone(dto);
            if (null == deviceUser) {
                log.error("===============用户信息为空,手机号:" + subOrderDetail.getAddresseePhone());
                ResultUtil.error(ru, "用户信息为空!");
                return ru;
            }
            ProductDTO product = productFeign.getProductById(subOrder.getProductId());
            WorkOrder workorder;
            try {
                workorder = this.initWorkOrderData(subOrder, subOrderDetail, distributor, subDistributor, product, deviceUser, cost);
            } catch (Exception e) {
                log.error("初始化工单对象失败，子订单为：{}", subOrder.getId());
                throw new YimaoException("初始化工单对象失败");
            }

            /*
             * Map<String, Object> baideResult = null; try { String isPay = null !=
             * subOrder.getPay() && subOrder.getPay() ? "1" : "0"; boolean isUserOrder =
             * false;
             *
             * //服务费 double serviceMoney = subOrder.getFee().doubleValue() -
             * cost.getInstallationFee().doubleValue(); Date payDate =
             * subOrder.getPayTime();//支付时间 String paymentMeans =
             * convertPayType(subOrder.getPayType());//支付类型 // 将之前同步到微服务的数据直接同步到售后
             * baideResult = BaideApiUtil.add(deviceUser.getOldId(), name, phone, 0, time,
             * userProvince, userCity, userRegion, address, subOrder.getId().toString(),
             * subOrder.getId().toString(), distributor.getOldId(),
             * distributor.getRealName(), distributor.getPhone(),
             * workorder.getDispatchType().toString(), workorder.getOldEngineerId(),
             * YunOldIdUtil.getProductId(workorder.getDeviceModel()),
             * workorder.getDeviceModel(), cost.getOldId(), cost.getName(), payTerminal,
             * cost.getInstallationFee().doubleValue(), serviceMoney, count, remark, "N",
             * distributor.getIdCard(), distributor.getUserName(),
             * distributor.getRoleName(), childDistributorId, childDistributorName,
             * childDistributorAccount, Terminal.getName(subOrder.getTerminal()),
             * isOldOrder, isUserOrder, isPay, payDate, paymentMeans,
             * workorder.getTradeNo()); } catch (Exception e) { log.error("售后创建工单失败" +
             * e.getMessage(), e); ResultUtil.error(ru, "00", "上线地区百得工单创建失败"); return ru; }
             * String workOrderId; if (isOldOrder) { workOrderId =
             * UUIDUtil.getWorkOrderNoToStr(); } else { if
             * (BaideApiUtil.success(baideResult)) { JSONObject obj =
             * JSONObject.fromObject(baideResult.get("data")); workOrderId =
             * obj.getString("code"); } else { JSONObject obj =
             * JSONObject.fromObject(baideResult.get("data")); ResultUtil.error(ru, "00",
             * obj.toString()); return ru; } if (StringUtil.isEmpty(workOrderId)) {
             * ResultUtil.error(ru, "00", "上线地区百得工单创建失败"); return ru; } }
             */
            String workOrderId = UUIDUtil.getWorkOrderNoToStr();
            try {
                workorder.setId(workOrderId);
                createWorkOrder(workorder);
                try {
                    //创建工单完成,将消息推送给安装工/经销商
                    pushMessage(workorder, MessageModelTypeEnum.WATER_ORDER.value);
                } catch (Exception e) {
                    log.error("创建工单推送消息给安装工失败(工单号=" + workOrderId + ")===engineerId=" + workorder.getEngineerId());
                }
            } catch (Exception e) {
                log.error("=========售后创建工单失败异常========={}====", workOrderId + "," + e.getMessage());
                String subject = "创建工单失败" + domainProperties.getApi();
                String content = "创建工单失败,以下售后单需要删除" + workOrderId + "\n 开始出问题的主单号 : " + subOrder.getMainOrderId() + "\n 子订单号： " + subOrder.getId();
                mailSender.send(null, subject, content);
                throw new YimaoException("售后创建工单失败");
            }
            ru.put("workorderId", workOrderId);
            ru.put("success", true);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new YimaoException("售后创建工单失败");
        } finally {
            redisLock.unLock(CREATE_WORKORDER_AND_SYNC_AFTERSALE_LOCK);
        }
        return ru;
    }

    /***
     * 支付类型转换为售后的值
     * 0：支付宝1：微信支付2：其他
     * @param payType
     * @return
     */
    private String convertPayType(Integer payType) {
        String paymentMeans = "";
        if (null != payType) {
            if (PayType.WECHAT.value == payType) {
                paymentMeans = "1";
            } else if (PayType.ALIPAY.value == payType) {
                paymentMeans = "0";
            } else {
                paymentMeans = "2";

            }
        }
        return paymentMeans;
    }

    //推送消息给安装工和经销商、水机用户
    private void pushMessage(WorkOrder workorder, int value) {

        //推送消息给安装工
        Map<String, String> content = new HashMap<>();
        List<AppMessageDTO> appMessageList = new ArrayList<AppMessageDTO>();
        Date now = new Date();
        AppMessageDTO appMessage = new AppMessageDTO();
        DistributorDTO distributorDTO = userFeign.getDistributorById(workorder.getDistributorId());
        if (null == distributorDTO) {
            log.error("=============新工单分配推送消息失败[经销商信息为空]==============distributorId=" + workorder.getDistributorId());
            return;
        }

        EngineerDTO engineerDTO = userFeign.getEngineerById(workorder.getEngineerId());
        if (null == engineerDTO) {
            log.error("=============新工单分配推送消息失败[安装工信息为空]==============engineerId=" + workorder.getEngineerId());
            return;
        }
        if (value == MessageModelTypeEnum.ORDER_FINISH.value) {
            //经销商app 工单完成消息
            WaterDeviceDTO device = waterFeign.getBySnCode(workorder.getSn());
            content.put("#code#", device.getSn());
            appMessage.setDevices(distributorDTO.getAppType());//取经销商的apptype
            appMessage.setDeviceId(device.getId() + "");
            appMessage.setSncode(device.getSn());
            appMessage.setReceiverId(distributorDTO.getUserId());
            appMessage.setReceiver(workorder.getDistributorName());
            appMessage.setPushType(MessageModelTypeEnum.ORDER_FINISH.value);
            appMessage.setMechanism(MessageMechanismEnum.WATER_ORDER_COMPLETION.value);
            appMessage.setApp(MessagePushModeEnum.YIMAO_APP_NOTICE.value);
            appMessage.setTitle(MessageModelTypeEnum.ORDER_FINISH.name);
            appMessage.setCreateTime(now);
            appMessage.setContentMap(content);
            appMessage.setMsgType(Constant.MSG_TYPE_NOTICE);
            appMessageList.add(appMessage);

            //推送消息至站务系统
            Map<String, String> stationMessage = new HashMap<>();
            stationMessage.put("#code#", device.getSn());
            stationMessage.put("#code1#", device.getDeviceUserName());
            stationMessage.put("#code2#", device.getDeviceUserPhone());
            stationMessage.put("#code3#", engineerDTO.getRealName());
            stationMessage.put("#code4#", engineerDTO.getPhone());
            stationMessage.put("#code5#", distributorDTO.getRealName());
            stationMessage.put("#code6#", distributorDTO.getPhone());
            pushMsgToStation(workorder, "工单完成", MessageModelTypeEnum.ORDER_FINISH.value, MessageMechanismEnum.WATER_ORDER_COMPLETION.value, stationMessage);

        } else if (value == MessageModelTypeEnum.WATER_ORDER.value) {
            //新工单分配 推送安装工
            content.put("#code#", workorder.getId());
            appMessage.setWorkorderId(workorder.getId());
            //appMessage.setReceiver(workorder.getEngineerId() + "");
            appMessage.setDevices(engineerDTO.getAppType());//取安装工的apptype
            appMessage.setReceiverId(workorder.getEngineerId());
            appMessage.setReceiver(workorder.getEngineerName());
            appMessage.setPushType(MessageModelTypeEnum.WATER_ORDER.value);
            appMessage.setMechanism(MessageMechanismEnum.WATER_ORDER_DISTRIBUTION.value);
            appMessage.setApp(MessagePushModeEnum.YIMAO_ENGINEER_APP_NOTICE.value);
            appMessage.setTitle(MessageModelTypeEnum.WATER_ORDER.name);
            appMessage.setCreateTime(now);
            appMessage.setContentMap(content);
            appMessage.setMsgType(Constant.MSG_TYPE_NOTICE);
            appMessageList.add(appMessage);

            //下单人不是经销商才推消息给上级经销商
            if (!checkUserIsDistributor(workorder.getSubOrderId())) {
                Map<String, String> contentDis = new HashMap<>();
                AppMessageDTO appMessageDis = new AppMessageDTO();
                contentDis.put("#code#", workorder.getSubOrderId() + "");//订单号
                appMessageDis.setDevices(distributorDTO.getAppType());//取经销商的apptype
                appMessageDis.setReceiverId(distributorDTO.getUserId());
                appMessageDis.setReceiver(workorder.getDistributorName());
                appMessageDis.setPushType(MessageModelTypeEnum.WATER_ORDER.value);
                appMessageDis.setMechanism(MessageMechanismEnum.USER_CREATE_ORDER.value);
                appMessageDis.setApp(MessagePushModeEnum.YIMAO_APP_NOTICE.value);
                appMessageDis.setTitle(MessageModelTypeEnum.WATER_ORDER.name);
                appMessageDis.setCreateTime(now);
                appMessageDis.setContentMap(contentDis);
                appMessageDis.setMsgType(Constant.MSG_TYPE_NOTICE);
                appMessageList.add(appMessageDis);
            }

            //推送消息至站务系统
            Map<String, String> stationMessage = new HashMap<>();
            stationMessage.put("#code#", engineerDTO.getRealName());
            stationMessage.put("#code1#", workorder.getId());
            stationMessage.put("#code2#", workorder.getUserName());
            stationMessage.put("#code3#", workorder.getUserPhone());
            stationMessage.put("#code4#", distributorDTO.getRealName());
            stationMessage.put("#code5#", distributorDTO.getPhone());
            pushMsgToStation(workorder, "新工单分配", MessageModelTypeEnum.WATER_ORDER.value, MessageMechanismEnum.WATER_ORDER_DISTRIBUTION.value, stationMessage);

        }
        installerApiService.pushMsgToApp(appMessageList);

    }

    public void pushMsgToStation(WorkOrder workorder, String title, int pushType, int mechanism, Map<String, String> stationMessage) {
        try {

            //根据工单的省市区查询区域id
            Integer areaId = redisCache.get(Constant.AREA_CACHE + workorder.getProvince() + "_" + workorder.getCity() + "_" + workorder.getRegion(), Integer.class);
            if (areaId == null) {
                areaId = systemFeign.getRegionIdByPCR(workorder.getProvince(), workorder.getCity(), workorder.getRegion());
            }
            if (Objects.nonNull(areaId)) {
                StationMessageDTO message = new StationMessageDTO();
                message.setDeviceId(String.valueOf(workorder.getDeviceId()));
                message.setSncode(workorder.getSn());
                message.setWorkorderId(workorder.getId());
                message.setPushType(pushType);
                message.setCreateTime(new Date());
                message.setReceiverId(areaId);
                message.setTitle(title);
                message.setMechanism(mechanism);
                message.setContentMap(stationMessage);
                message.setPushObject(MessagePushObjectEnum.SYSTEM.value);
                message.setMessageType(0);
                message.setFilterType(null);
                rabbitTemplate.convertAndSend(RabbitConstant.STATION_MESSAGE_PUSH, message);

            }
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

    }

    /****
     * 判断下单用户是否是经销商
     * @param subOrderId
     * @return
     */
    private boolean checkUserIsDistributor(Long subOrderId) {
        OrderSub sub = orderSubMapper.selectByPrimaryKey(subOrderId);
        if (null != sub && sub.getUserId() != null) {
            UserDTO user = userFeign.getUserById(sub.getUserId());
            if (null != user && user.getUserType() != null && UserType.isDistributor(user.getUserType())) {
                return true;
            }
        }
        return false;
    }

    /**
     * 安装工拒单
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
    public void refuseWorkOrder(String workOrderId, Integer engineerId, String engineerName, String reason) {
        WorkOrder workOrder = workOrderMapper.selectByPrimaryKey(workOrderId);
        if (workOrder == null) {
            throw new YimaoException("安装工拒单-获取工单失败");
        }
        Date now = new Date();
        String province = workOrder.getProvince();
        String city = workOrder.getCity();
        String region = workOrder.getRegion();
        //保存拒单记录
        WorkOrderRefuse refuse = new WorkOrderRefuse();
        refuse.setWorkOrderId(workOrderId);
        refuse.setProvince(province);
        refuse.setCity(city);
        refuse.setRegion(region);
        refuse.setEngineerId(engineerId);
        refuse.setEngineerName(engineerName);
        refuse.setReason(reason);
        refuse.setRefuseTime(now);
        workOrderRefuseMapper.insertSelective(refuse);
        //保存工单操作记录
        WorkOrderOperation operation = new WorkOrderOperation();
        operation.setAdmin(workOrder.getEngineerName());
        operation.setWorkOrderId(workOrderId);
        operation.setReason(reason);
        operation.setOperation("拒绝工单");
        operation.setCreateTime(now);
        workOrderOperationMapper.insertSelective(operation);

        //获取该省市区的所有安装工（未禁用的）
        Integer areaId = systemFeign.getRegionIdByPCR(province, city, region);
        List<EngineerDTO> engineerList = userFeign.listEngineerByRegion(areaId);

        //获取该省市区的安装工数量（包含被禁用的）

        int engineerCount = userFeign.countAllEngineerByArea(areaId);

        //获取该笔工单的所有拒单记录
        List<WorkOrderRefuse> refuseList = workOrderRefuseMapper.selectByWorkOrderId(workOrderId, province, city, region);
        int refuseCount = refuseList.size();

        //从安装工列表里选择一个新的安装工
        EngineerDTO newEngineer = null;
        for (EngineerDTO dto : engineerList) {
            newEngineer = dto;
            if (!isAlreadyRefuseThis(engineerId, engineerName, refuseList)) {
                break;
            }
        }
        workOrder.setStep(WorkOrderInstallStep.START.value);
        if (refuseCount >= engineerCount) {
            newEngineer = null;
        } else {
            //老安装工正在服务的工单数减1
            Map<String, Integer> map = new HashMap<>();
            map.put("engineerId", engineerId);
            map.put("num", -1);
            rabbitTemplate.convertAndSend(RabbitConstant.ENGINEER_BUSY_COUNT, map);
            //新安装工正在服务的工单数加1
            // TODO 发送消息给新安装工
        }
        if (newEngineer != null) {
            workOrder.setEngineerId(newEngineer.getId());
            workOrder.setEngineerName(newEngineer.getRealName());
            workOrder.setEngineerPhone(newEngineer.getPhone());
            workOrder.setEngineerIdCard(newEngineer.getIdCard());
            workOrder.setStationId(newEngineer.getStationId());
            workOrder.setStationName(newEngineer.getStationName());
            workOrder.setOldEngineerId(newEngineer.getOldId());
            workOrder.setOldStationId(newEngineer.getOldSiteId());
            workOrder.setStatus(WorkOrderStatusEnum.ASSIGNED.value);

            OrderSub order = new OrderSub();
            order.setId(workOrder.getSubOrderId());
            order.setEngineerId(newEngineer.getId());
            orderSubMapper.updateByPrimaryKeySelective(order);

            //更新子订单详情
            SubOrderDetail detail = new SubOrderDetail();
            detail.setEngineerId(newEngineer.getId());
            detail.setEngineerName(newEngineer.getRealName());
            detail.setEngineerPhone(newEngineer.getPhone());
            detail.setEngineerIdCard(newEngineer.getIdCard());
            detail.setEngineerCity(newEngineer.getCity());
            detail.setEngineerProvince(newEngineer.getProvince());
            detail.setEngineerRegion(newEngineer.getRegion());
            detail.setSubOrderId(workOrder.getSubOrderId());
            subOrderDetailMapper.updateByPrimaryKeySelective(detail);

            //修改收益安装工  2020-04-21 修改
            ProductIncomeRecordPart part = productIncomeRecordPartMapper.selectPartIdBySubOrderId(workOrder.getSubOrderId());
            if (part != null) {
                part.setSubjectId(newEngineer.getId());
                part.setSubjectName(newEngineer.getRealName());
                part.setSubjectPhone(newEngineer.getPhone());
                part.setSubjectIdCard(newEngineer.getIdCard());
                productIncomeRecordPartMapper.updateByPrimaryKeySelective(part);
            }
        } else {
            workOrder.setDispatch(true);
            workOrder.setEngineerId(null);
            workOrder.setEngineerName(null);
            workOrder.setEngineerPhone(null);
            workOrder.setEngineerIdCard(null);
            workOrder.setStationId(null);
            workOrder.setStationName(null);
            workOrder.setOldEngineerId(null);
            workOrder.setOldStationId(null);
        }
        workOrder.setRefuse(true);
        workOrder.setRefuseReason(reason);
        workOrder.setRefuseTime(now);
        workOrderMapper.updateByPrimaryKey(workOrder);

        rabbitTemplate.convertAndSend(RabbitConstant.SYNC_WORK_ORDER, workOrderId);

    }

    /**
     * 判断安装工是否已经拒绝了这个工单
     */
    private boolean isAlreadyRefuseThis(Integer engineerId, String engineerName, List<WorkOrderRefuse> refuseList) {
        for (WorkOrderRefuse refuse : refuseList) {
            if (Objects.equals(refuse.getEngineerId(), engineerId) && Objects.equals(refuse.getEngineerName(), engineerName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 安装工接单（老流程、新流程）
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
    public void acceptWorkOrder(String workOrderId, Integer engineerId, Integer nextStep) {
        WorkOrder workOrder = workOrderMapper.selectBasicInfoById(workOrderId);
        if (workOrder == null) {
            throw new YimaoException("安装工接单-获取工单失败");
        }
        Date now = new Date();
        WorkOrder update = new WorkOrder();
        update.setId(workOrderId);
        update.setAcceptTime(now);
        update.setOperationTime(now);
        update.setStatus(WorkOrderNewStatusEnum.PROCESSING.value);
        update.setStep(WorkOrderInstallNewStep.START.value);
        update.setNextStep(nextStep);
        workOrderMapper.updateByPrimaryKeySelective(update);

        //子订单状态设置为待收货
        OrderSub order = new OrderSub();
        order.setId(workOrder.getSubOrderId());
        order.setStatus(OrderStatusEnum.PENDING_RECEIPT.value);
        orderSubMapper.updateByPrimaryKeySelective(order);

        //安装工正在服务的工单数加1
        Map<String, Integer> map = new HashMap<>();
        map.put("engineerId", engineerId);
        map.put("num", 1);
        rabbitTemplate.convertAndSend(RabbitConstant.ENGINEER_BUSY_COUNT, map);
    }

    /**
     * 安装工中止工单
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
    public void discontinueWorkOrder(String workOrderId, String reason, String remark, Integer reasonNum) {
        WorkOrder workOrder = workOrderMapper.selectBasicInfoById(workOrderId);
        if (workOrder == null) {
            throw new YimaoException("安装工中止工单-获取工单失败");
        }
        WaterDeviceDTO device = null;
        if (workOrder.getDeviceId() != null) {
            device = waterFeign.getWaterDeviceById(workOrder.getDeviceId());
            if (device != null && StringUtil.isNotEmpty(device.getIccid())) {
                waterFeign.deactivatedSimCard(device.getId());
            }
        }
        Date now = new Date();
        WorkOrder update = new WorkOrder();
        update.setId(workOrderId);
        update.setOperationTime(now);
        update.setDiscontinue(true);
        update.setDiscontinueNum(reasonNum);
        update.setDiscontinueReason(reason);
        update.setDiscontinueRemark(remark);
        workOrderMapper.updateByPrimaryKeySelective(update);
        //保存工单操作记录
        WorkOrderOperation operation = new WorkOrderOperation();
        operation.setAdmin(workOrder.getEngineerName());
        operation.setWorkOrderId(workOrderId);
        operation.setOperation("中止工单");
        operation.setReason(reason);
        operation.setRemark(remark);
        operation.setCreateTime(now);
        if (device != null) {
            operation.setSnCode(device.getSn());
            operation.setSimCard(device.getIccid());
            operation.setBatchCode(device.getLogisticsCode());
        }
        workOrderOperationMapper.insertSelective(operation);
    }

    /**
     * 安装工更换设备（老流程）
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
    public void changedeviceWorkOrder(String workOrderId) {
        WorkOrder workOrder = workOrderMapper.selectBasicInfoById(workOrderId);
        if (workOrder == null) {
            throw new YimaoException("安装工更换设备-获取工单失败");
        }
        //如果已经创建了设备
        WaterDeviceDTO device = null;
        if (workOrder.getDeviceId() != null) {
            device = waterFeign.getWaterDeviceById(workOrder.getDeviceId());
            if (device != null) {
                if (StringUtil.isNotEmpty(device.getIccid())) {
                    waterFeign.deactivatedSimCard(device.getId());
                }
                waterFeign.deleteWaterDevice(device.getId());
            }
        }
        Date now = new Date();
        WorkOrder update = new WorkOrder();
        update.setId(workOrderId);
        update.setOperationTime(now);
        update.setStatus(WorkOrderStatusEnum.INSTALLING.value);
        update.setStep(WorkOrderInstallStep.PICK.value);
        if (workOrder.getInvoice() != null && workOrder.getInvoice()) {
            update.setInvoice(false);
        }
        workOrderMapper.updateForChangeDevice(update);
        //保存工单操作记录
        WorkOrderOperation operation = new WorkOrderOperation();
        operation.setAdmin(workOrder.getEngineerName());
        operation.setWorkOrderId(workOrderId);
        operation.setOperation("更换设备");
        operation.setCreateTime(now);
        if (device != null) {
            operation.setSnCode(device.getSn());
            operation.setSimCard(device.getIccid());
            operation.setBatchCode(device.getLogisticsCode());
        }
        workOrderOperationMapper.insertSelective(operation);
    }

    /**
     * 安装工继续服务
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
    public void continueWorkOrder(String workOrderId) {
        WorkOrder workOrder = workOrderMapper.selectBasicInfoById(workOrderId);
        if (workOrder == null) {
            throw new YimaoException("安装工继续服务-获取工单失败");
        }
        Date now = new Date();
        WorkOrder update = new WorkOrder();
        update.setId(workOrderId);
        update.setOperationTime(now);
        workOrderMapper.updateForContinue(update);
    }

    /**
     * 安装工退单
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
    public void chargebackWorkOrder(String workOrderId, String reason, String remark, Integer reasonNum) {
        WorkOrder workOrder = workOrderMapper.selectBasicInfoById(workOrderId);
        if (workOrder == null) {
            throw new YimaoException("安装工退单-获取工单失败");
        }
        //如果已经创建了设备
        String sncode = "";
        String iccid = "";
        String logisticsCode = "";
        if (workOrder.getDeviceId() != null) {
            WaterDeviceDTO device = waterFeign.getWaterDeviceById(workOrder.getDeviceId());
            if (device != null) {
                sncode = device.getSn();
                iccid = device.getIccid();
                logisticsCode = device.getLogisticsCode();
                if (StringUtil.isNotEmpty(device.getIccid())) {
                    waterFeign.deactivatedSimCard(device.getId());
                }
                waterFeign.deleteWaterDevice(device.getId());
            }
        }
        Date now = new Date();
        WorkOrder update = new WorkOrder();
        update.setId(workOrderId);
        update.setOperationTime(now);
        update.setStatus(WorkOrderStatusEnum.COMPLETED.value);
        update.setChargeback(true);
        update.setChargebackType(2);//1-经销商退单；2-客服退单
        update.setChargebackNum(reasonNum);
        update.setChargebackReason(reason);
        update.setChargebackRemark(remark);
        update.setChargebackSncode(sncode);
        update.setChargebackStatus(Integer.parseInt(WorkOrderChargebackEnum.BACKING.getState()));
        update.setChargebackTime(now);
        update.setCompleteType(WorkOrderCompleteEnum.AUTO_COMPLETE.getState());//异常完成
        update.setCompleteTime(now);
        workOrderMapper.updateForChargeback(update);
        //保存工单操作记录
        WorkOrderOperation operation = new WorkOrderOperation();
        operation.setAdmin(workOrder.getEngineerName());
        operation.setWorkOrderId(workOrderId);
        operation.setOperation("客服退单");
        operation.setRemark("当前sncode:" + sncode + ",simcard:" + iccid + ",batchCode:" + logisticsCode);
        operation.setCreateTime(now);
        workOrderOperationMapper.insertSelective(operation);

        //修改订单状态为待经销商审核
        List<Long> ids = new ArrayList<>();
        ids.add(workOrder.getSubOrderId());
        OrderSubDTO orderSubDTO = new OrderSubDTO();
        orderSubDTO.setCancelReason(reason);
        orderSubDTO.setRemark(remark);
        com.alibaba.fastjson.JSONObject jsonObject = orderSubService.updateOrderStatusBatch(orderSubDTO, ids);
        int failureSize = ((JSONArray) jsonObject.get("failureArray")).size();
        int successSize = ((JSONArray) jsonObject.get("successArray")).size();
        if (failureSize > 0 && successSize > 0) {
            throw new YimaoException("退单订单，部分成功，部分失败。" + jsonObject);
        }

        rabbitTemplate.convertAndSend(RabbitConstant.SYNC_WORK_ORDER, workOrderId);

        //退单，要把库存还回去
        Map<String, Object> map = new HashMap<>();
        map.put("province", workOrder.getProvince());
        map.put("city", workOrder.getCity());
        map.put("region", workOrder.getRegion());
        map.put("productModel", workOrder.getDeviceModel());
        map.put("count", 1);

        DistributorDTO distributor = userFeign.getDistributorById(workOrder.getDistributorId());
        if (distributor == null) {
            throw new BadRequestException("退单失败：" + workOrder.getDistributorId() + "该经销商不存在！");
        }
        // if (distributor.getUserName().toLowerCase().contains("clxd") && DistributorRoleLevel.DISCOUNT.value == distributor.getRoleLevel()) {
        //     map.put("special", 1);
        // } else {
        map.put("special", 0);
        // }
        rabbitTemplate.convertAndSend(RabbitConstant.INCREASE_OR_DECREASE_STOCK, map);

        for (Long subOrderId : ids) {
            //根据订单号查询订单
            OrderSub sbuOrder = orderSubMapper.selectByPrimaryKey(subOrderId);
            //发送通知
            Map<String, String> messageMap = new HashMap<>();
            messageMap.put("#code#", sbuOrder.getMainOrderId().toString());
            DistributorDTO distributorDTO = userFeign.getDistributorBasicInfoByIdForMsgPushInfo(sbuOrder.getDistributorId());
            if (Objects.isNull(distributorDTO)) {
                log.error("安装工退单通知推送失败，子订单未能找到经销商，子订单id=" + subOrderId);
                continue;
            }
            //为自己下单的订单
            if (sbuOrder.getDistributorId() != null && sbuOrder.getUserId().equals(distributorDTO.getUserId())) {
                //消息推送
                pushMsgToDistributorUser(MessageModelTypeEnum.VERIFY_NOTICE.value, MessageMechanismEnum.DISTRIBUTION_SELF_ORDER_AUDIT.value, MessageModelTypeEnum.VERIFY_NOTICE.name, distributorDTO.getUserId(), distributorDTO.getUserName(), distributorDTO.getAppType(), messageMap);
            } else {//为客户下单的订单
                //消息推送
                pushMsgToDistributorUser(MessageModelTypeEnum.VERIFY_NOTICE.value, MessageMechanismEnum.DISTRIBUTION_LATTER_ORDER_AUDIT.value, MessageModelTypeEnum.VERIFY_NOTICE.name, distributorDTO.getUserId(), distributorDTO.getUserName(), distributorDTO.getAppType(), messageMap);
            }
        }
    }

    public void pushMsgToDistributorUser(Integer pushType, Integer mechanism, String title, Integer receiverId, String receiverName, Integer devices, Map<String, String> distributorMessage) {
        //构建APP消息推送实体
        AppMessageDTO appMessage = new AppMessageDTO();
        appMessage.setMsgType(Constant.MSG_TYPE_NOTICE);
        appMessage.setPushType(pushType);
        appMessage.setCreateTime(new Date());
        appMessage.setReceiverId(receiverId);
        appMessage.setReceiver(receiverName);
        appMessage.setTitle(title);
        appMessage.setApp(MessagePushModeEnum.YIMAO_APP_NOTICE.value);//1-推送给安装工；2-推送消息给经销商
        appMessage.setMechanism(mechanism);
        appMessage.setDevices(devices);
        appMessage.setContentMap(distributorMessage);
        //2-推送消息给经销商
        appMessage.setPushObject(MessagePushObjectEnum.DISTRIBUTOR.value);
        rabbitTemplate.convertAndSend(RabbitConstant.YIMAO_APP_MESSAGE_PUSH, appMessage);
    }

    /**
     * 安装工完成工单
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
    public void completeWorkOrder(String workOrderId) {
        WorkOrder workOrder = workOrderMapper.selectBasicInfoById(workOrderId);
        if (workOrder == null) {
            throw new YimaoException("安装工完成工单-获取工单失败");
        }
        Date now = new Date();
        WorkOrder update = new WorkOrder();
        update.setId(workOrderId);
        update.setStep(WorkOrderInstallNewStep.COMPLETE_WORK_ORDER.value);//完成工单
        update.setNextStep(WorkOrderInstallNewStep.END.value);//结束
        update.setOperationTime(now);
        update.setStatus(WorkOrderStatusEnum.COMPLETED.value);
        update.setStatusText(WorkOrderStatusEnum.COMPLETED.name);
        update.setCompleteTime(now);
        update.setCompleteType(WorkOrderCompleteEnum.NORMAL_COMPLETE.getState());//完成类型: 1、正常完成；2、非正常完成
        workOrderMapper.updateByPrimaryKeySelective(update);

        //将订单设置为【5-交易成功】状态
        orderSubService.completeOrder(workOrder.getSubOrderId());

        //安装工正在服务的工单数减1
        Map<String, Integer> map = new HashMap<>();
        map.put("engineerId", workOrder.getEngineerId());
        map.put("num", -1);
        rabbitTemplate.convertAndSend(RabbitConstant.ENGINEER_BUSY_COUNT, map);

        try {
            //发送消息给经销商
            pushMessage(workOrder, MessageModelTypeEnum.ORDER_FINISH.value);
            rabbitTemplate.convertAndSend(RabbitConstant.SYNC_WORK_ORDER, workOrderId);
            // TODO 收益结算状态修改
        } catch (Exception e) {
            log.error("==========完成工单(workOrderId=" + workOrderId + ")后续处理异常信息======" + e.getMessage());
        }
    }

    @Override
    public Boolean existsWithLogisticsCode(String logisticsCode) {
        return workOrderMapper.existsWithLogisticsCode(logisticsCode);
    }

    /**
     * 安装工APP工单支付回调
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
    public void payCallback(PayRecord record) {
        String workOrderId = record.getMainOrderId();
        WorkOrder workOrder = workOrderMapper.selectBasicInfoById(workOrderId);
        if (workOrder == null) {
            throw new YimaoException("安装工工单支付回调失败，未查询到工单信息，out_trade_no=" + workOrderId);
        }
        WorkOrder update = new WorkOrder();
        update.setId(workOrderId);
        update.setPay(true);
        update.setStep(WorkOrderInstallNewStep.PAY.value);
        update.setPayType(record.getPayType());
        update.setPayTime(record.getPayTime());
        update.setPayStatus(PayStatus.PAY.value);
        update.setTradeNo(record.getTradeNo());
        update.setNextStep(WorkOrderInstallNewStep.SIGN_CONTRACT.value);//签约
        workOrderMapper.updateByPrimaryKeySelective(update);

        // rabbitTemplate.convertAndSend(RabbitConstant.SYNC_WORK_ORDER, workOrderId);
        try {
            syncWorkOrderService.syncWorkOrder(workOrderId);
        } catch (Exception e) {
            log.error("安装工APP工单支付回调同步支付状态到百得失败，workOrderId={}，meg={}", workOrderId, e.getMessage());
        }

        Long mainOrderId = workOrder.getMainOrderId();
        OrderMain mainOrder = new OrderMain();
        mainOrder.setId(mainOrderId);
        mainOrder.setPay(true);
        mainOrder.setPayType(record.getPayType());
        mainOrder.setPayTime(record.getPayTime());
        mainOrder.setTradeNo(record.getTradeNo());
        mainOrder.setUpdateTime(new Date());
        orderMainMapper.updateByPrimaryKeySelective(mainOrder);

        Long subOrderId = workOrder.getSubOrderId();
        OrderSub order = new OrderSub();
        order.setId(subOrderId);
        //支付完成后把订单的可见性改为可见
        order.setDeleted(false);
        order.setPay(true);
        order.setPayStatus(PayStatus.PAY.value);
        order.setPayType(record.getPayType());
        order.setPayTime(record.getPayTime());
        order.setTradeNo(record.getTradeNo());
        // order.setStatus(OrderStatusEnum.TO_BE_DELIVERED.value);
        orderSubMapper.updateByPrimaryKeySelective(order);

        // //工单在线支付回调同步支付状态到百得
        // try {
        //     String payType = "0";
        //     if (record.getPayType() == PayType.WECHAT.value) {
        //         payType = "1";
        //     }
        //     Map<String, Object> map = BaideApiUtil.payment(workOrderId, payType, null, null, null);
        //     log.info("工单在线支付回调=====BaidePayPaymentResp=" + JSON.toJSONString(map));
        //     if ("00000000".equals(map.get("code").toString())) {
        //         update.setNextStep(8);//TODO 售后系统没有返回该字段，所以写死了
        //         workOrderMapper.updateByPrimaryKeySelective(update);
        //     }
        // } catch (Exception e) {
        //     log.error("工单在线支付回调同步支付状态到百得失败，workOrderId={}，meg={}", workOrderId, e.getMessage());
        // }
    }

	/**
	 * 安装工操作更换设备型号（新流程）
	 */
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@Override
	public void changeProductAndCost(String workOrderId, Integer productId, Integer costId, String logisticsCode, String snCode, String simCard, Integer type) {
		WorkOrder workOrder = workOrderMapper.selectByPrimaryKey(workOrderId);
		WaterDeviceDTO device = null;
		if (workOrder == null) {
			throw new BadRequestException("工单数据错误");
		}
		if (workOrder.getDeviceId() != null) {
			device = waterFeign.getWaterDeviceById(workOrder.getDeviceId());
			if (Objects.isNull(device)) {
				throw new YimaoException("原工单设备不存在！");
			}
		}

		ProductDTO product = productFeign.getProductById(productId);
		if (product == null) {
			throw new BadRequestException("产品数据错误");
		}
		ProductCostDTO newCost = productFeign.productCostGetById(costId);
		if (newCost == null) {
			throw new BadRequestException("计费方式数据错误");
		}

		//更改产品型号计费的实际金额为安装费+租赁费
		BigDecimal newTotalMoney = newCost.getTotalFee();
		//更改产品型号计费的实际金额为安装费+租赁费
		BigDecimal newRentalFee = newCost.getRentalFee();
		//当前工单的费用(workorder中的fee显示安装费+租赁费后的数据)
		BigDecimal oldTotalMoney = workOrder.getFee();
		//当前的租赁费
		BigDecimal oldInstallationFee = workOrder.getOpenAccountFee();
		//当前工单的可收益分配金额
		BigDecimal oldRentalFee = oldTotalMoney.subtract(oldInstallationFee);
		log.info("更改计费方式前的费用=" + oldTotalMoney.toString() + ",更改计费方式后的费用=" + newTotalMoney.toString());
		log.info("当前工单的可收益金额=" + oldRentalFee.toString() + ",更改后的可收益金额=" + newRentalFee.toString());
		//判断型号是否变更,变更情况下处理库存
		if (!product.getCategoryName().equalsIgnoreCase(workOrder.getDeviceModel())) {
			//获取产品水机类目
			ProductCategoryDTO categoryDto = productFeign.getProductCategory(product.getCategoryId());

			if (Objects.isNull(categoryDto)) {
				throw new YimaoException("水机类目不存在");
			}

			//获取产品水机类目上的库存物资id
			if (Objects.isNull(categoryDto.getStoreGoodsId())) {
				throw new YimaoException("产品类目未绑定对应库存id");
			}
			GoodsMaterialsDTO goodsMaterials = systemFeign.checkStationGoodsIsHaveStock(workOrder.getStationId(), categoryDto.getStoreGoodsId());
            if (Objects.isNull(goodsMaterials)) {
                throw new YimaoException("服务站当前没有该型号库存！");
            }

            //原库存退还
            systemFeign.addStock(workOrder.getProductId(), workOrder.getStationId(), 1);
            //新库存预减
            systemFeign.stockPrune(productId, workOrder.getStationId(), 1);
        }

        //判断工单是否支付
        if (workOrder.getPay()) {
            //已支付，只能选择同等价位的产品类型
            if (!Objects.equals(newTotalMoney, oldTotalMoney) || !Objects.equals(newRentalFee, oldRentalFee)) {
                throw new BadRequestException("操作失败，只能更换同价位的型号和计费方式");
            }
            //工单编辑产品及计费方式相关字段
            editWorkOrderProductAndCost(workOrder.getId(), newCost, product, workOrder.getStep());
            if (workOrder.getDeviceId() != null) {
                changeWaterDeviceInfo(workOrder.getDeviceId(), newCost, product);
            }
            //子订单及子订单详情更新产品与计费方式相关字段
            editSubOrderProductAndCost(workOrder.getSubOrderId(), newCost, product);
        } else {
            //未支付
            //修改工单
            editWorkOrderProductAndCost(workOrder.getId(), newCost, product, workOrder.getStep());
            if (workOrder.getDeviceId() != null) {
                changeWaterDeviceInfo(workOrder.getDeviceId(), newCost, product);
            }
            //套餐价格不一致
            if (!Objects.equals(newTotalMoney, oldTotalMoney)) {
                //若子订单总金额不同还需修改主订单总金额和相关金额
                List<OrderSubDTO> orderSubList = orderSubMapper.selectSubOrdersByMainOrderId(workOrder.getMainOrderId());
                BigDecimal newTotalFee = new BigDecimal(0);
                //原先价格核算
                BigDecimal oldTotalFee = new BigDecimal(0);
                for (OrderSubDTO sub : orderSubList) {
                    Long subOrderId = sub.getId();
                    if (Objects.equals(subOrderId, workOrder.getSubOrderId())) {
                        newTotalFee = newTotalFee.add(newTotalMoney);
                    } else {
                        newTotalFee = newTotalFee.add(sub.getFee());
                    }
                    oldTotalFee = oldTotalFee.add(sub.getFee());
                }
                log.info("计算后的总金额=" + newTotalFee.toString() + "原先总金额应为:" + oldTotalFee.toString());
                changeMainOrderInfo(workOrder.getMainOrderId(), newTotalFee, oldTotalFee);
            }
            //修改子订单
            editSubOrderProductAndCost(workOrder.getSubOrderId(), newCost, product);
        }

        //只要修改了型号，都要计算
        if (!Objects.equals(newCost.getRentalFee(), oldRentalFee)) {
            //若可分配金额不同需修改收益分配相关数据
            log.info("订单号：{} 安装工更换设备型号---重新分配收益金额", workOrder.getSubOrderId());
            productIncomeRecordService.editProductIncomeRecordFee(workOrder, product, newCost);
        } else {
            log.info("订单号：{} 安装工更换设备型号---只修改收益数据上的产品信息", workOrder.getSubOrderId());
            productIncomeRecordService.editProductIncomeRecordProductInfo(workOrder, product);
        }
    }

    /**
     * 修改主订单信息
     */
    private void changeMainOrderInfo(Long mainOrderId, BigDecimal newTotalFee, BigDecimal oldTotalFee) {
        //更新至主订单
        OrderMain orderMain = new OrderMain();
        orderMain.setId(mainOrderId);
        orderMain.setProductAmountFee(newTotalFee);
        orderMain.setOrderAmountFee(newTotalFee);
        orderMain.setUpdateTime(new Date());
        //根据id查询改订单
        if (!orderMainMapper.existsWithPrimaryKey(mainOrderId)) {
            throw new BadRequestException("主订单不存在");
        }
        // if (!Objects.equals(ordermain.getProductAmountFee(), oldTotalFee)) {
        //     throw new BadRequestException("主订单金额更新失败，金额不一致");
        // }
        orderMainMapper.updateByPrimaryKeySelective(orderMain);
        //TODO 同步售后
    }

    /**
     * 修改设备表上的产品和计费方式数据
     */
    private void changeWaterDeviceInfo(Integer deviceId, ProductCostDTO newCost, ProductDTO product) {
        WaterDeviceDTO device = waterFeign.getWaterDeviceById(deviceId);
        if (device != null) {
            WaterDeviceDTO update = new WaterDeviceDTO();
            update.setId(device.getId());
            update.setCostId(newCost.getId());
            update.setCostName(newCost.getName());
            update.setCostType(newCost.getType());
            update.setOldCostId(newCost.getOldId());
            update.setInitMoney(newCost.getTotalFee());
            update.setMoney(newCost.getTotalFee());
            device.setDeviceModel(product.getCategoryName());
            device.setDeviceScope(YunOldIdUtil.getProductScope(product.getCategoryName()));
            device.setDeviceType(YunOldIdUtil.getProductTypeName());
            waterFeign.updateDevice(update);
        }
    }


    /**
     * 更改子订单详情的产品型号及计费方式相关
     */
    private void editSubOrderProductAndCost(Long subOrderId, ProductCostDTO newCost, ProductDTO product) {
        OrderSub update = new OrderSub();
        update.setId(subOrderId);
        //金额
        update.setFee(newCost.getTotalFee());
        //产品
        update.setProductId(product.getId());
        update.setProductPrice(product.getPrice());
        update.setProductType(product.getMode());
        update.setProductModel(product.getCategoryName());
        orderSubMapper.updateByPrimaryKeySelective(update);

        SubOrderDetail detail = new SubOrderDetail();
        //计费方式
        detail.setSubOrderId(subOrderId);
        detail.setCostId(newCost.getId());
        detail.setCostName(newCost.getName());
        detail.setOpenAccountFee(newCost.getInstallationFee());
        //产品
        detail.setProductId(product.getId());
        detail.setProductName(product.getName());
        detail.setProductImg(product.getCoverImg());
        detail.setProductCompanyId(product.getCompanyId());
        detail.setProductCompanyName(product.getCompanyName());
        detail.setProductCategoryId(product.getCategoryId());
        detail.setProductCategoryName(product.getCategoryName());

        ProductCategoryDTO oneCategory = productFeign.getOneProductCategory(product.getCategoryId());
        if (Objects.nonNull(oneCategory)) {
            detail.setProductFirstCategoryId(oneCategory.getId());
            detail.setProductFirstCategoryName(oneCategory.getName());
        }

        ProductCategoryDTO twoCategory = productFeign.getTwoProductCategory(product.getCategoryId());
        if (Objects.nonNull(twoCategory)) {
            detail.setProductTwoCategoryId(twoCategory.getId());
            detail.setProductTwoCategoryName(twoCategory.getName());
        }
        subOrderDetailMapper.updateByPrimaryKeySelective(detail);

    }

    /**
     * 更改工单的产品型号及计费方式相关
     */
    private void editWorkOrderProductAndCost(String workOrderId, ProductCostDTO newCost, ProductDTO product, Integer step) {
        WorkOrder update = new WorkOrder();
        update.setId(workOrderId);
        //产品
        update.setProductId(product.getId());
        update.setProductName(product.getName());
        update.setDeviceModel(product.getCategoryName());
        //金额
        update.setFee(newCost.getTotalFee());
        update.setOpenAccountFee(newCost.getInstallationFee());
        //计费方式
        update.setCostId(newCost.getId());
        update.setCostType(newCost.getType());
        update.setCostName(newCost.getName());
        update.setOldCostId(newCost.getOldId());
        update.setOperationTime(new Date());

        //修改工单产品型号时，将缺货状态修改为初始状态
        if (step != null && step == WorkOrderInstallStep.OUTSTOCK.value) {
            update.setStep(WorkOrderInstallStep.START.value);
        }

        int result = workOrderMapper.updateByPrimaryKeySelective(update);
        try {
            if (result != 1) {
                throw new YimaoException("后台[订单列表]更换型号[更新工单失败]");
            }
            syncWorkOrderService.syncWorkOrder(update.getId());
        } catch (Exception e) {
            log.error("============后台[订单列表]更换型号[更新工单失败]workOrderId=" + update.getId() + "异常信息:" + e.getMessage());
            throw new YimaoException("后台[订单列表]更换型号[更新工单失败]");
        }
    }

    /**
     * 初始化工单对象
     */
    private WorkOrder initWorkOrderData(OrderSub subOrder, SubOrderDetail subOrderDetail, DistributorDTO distributor, DistributorDTO subDistributor, ProductDTO product, WaterDeviceUserDTO deviceUser, ProductCostDTO cost) throws Exception {
        String userProvince = subOrderDetail.getAddresseeProvince();
        String userCity = subOrderDetail.getAddresseeCity();
        String userRegion = subOrderDetail.getAddresseeRegion();
        String address = subOrderDetail.getAddresseeStreet();
        Integer dispatchType = subOrderDetail.getDispatchType();

        WorkOrder workOrder = new WorkOrder();
        workOrder.setStatus(WorkOrderStatusEnum.ASSIGNED.value);
        //workOrder.setStep(0);
        workOrder.setNextStep(0);
        workOrder.setChargeback(false);
        workOrder.setSignStatus(StatusEnum.NO.value());
        workOrder.setUserConfirm(false);
        workOrder.setInvoice(false);
        workOrder.setNeedCompletePay(false);
        workOrder.setCompletePay(false);
        EngineerDTO engineer = null;

        Date now = new Date();
        //添加经销商信息
        workOrder.setDistributorId(distributor.getId());
        workOrder.setOldDistributorId(distributor.getOldId());
        workOrder.setDistributorName(distributor.getRealName());
        workOrder.setDistributorPhone(distributor.getPhone());
        workOrder.setDistributorIdCard(distributor.getIdCard());
        workOrder.setDistributorAccount(distributor.getUserName());
        workOrder.setDistributorProvince(distributor.getProvince());
        workOrder.setDistributorCity(distributor.getCity());
        workOrder.setDistributorRegion(distributor.getRegion());
        workOrder.setDistributorType(DistributorRoleLevel.getUserType(distributor.getRoleLevel()));
        workOrder.setDistributorTypeName(DistributorRoleLevel.find(distributor.getRoleLevel()).name);
        workOrder.setDistributorRoleId(distributor.getRoleId());
        workOrder.setDistributorRoleName(distributor.getRoleName());
        workOrder.setDistributorRoleLevel(distributor.getRoleLevel());
        if (subDistributor != null) {
            workOrder.setSubDistributorId(subDistributor.getId());
            workOrder.setOldSubDistributorId(subDistributor.getOldId());
            workOrder.setSubDistributorName(subDistributor.getUserName());
            workOrder.setSubDistributorPhone(subDistributor.getPhone());
            workOrder.setSubDistributorAccount(subDistributor.getUserName());
        }
        //水机设备型号
        workOrder.setDeviceModel(product.getCategoryName());
        workOrder.setTerminal(subOrder.getTerminal());
        workOrder.setServiceTime(subOrderDetail.getServiceTime());
        workOrder.setCount(1);
        workOrder.setCreateTime(now);
        workOrder.setRemark(subOrder.getRemark());

        //添加用户信息
        workOrder.setUserId(deviceUser.getId());
        if (!StringUtil.isEmpty(deviceUser.getOldId())) {
            workOrder.setOldUserId(deviceUser.getOldId());
        }
        workOrder.setUserName(subOrderDetail.getAddresseeName());
        workOrder.setUserPhone(subOrderDetail.getAddresseePhone());
        workOrder.setUserIdCard(deviceUser.getIdCard());
        workOrder.setProvince(userProvince);
        workOrder.setCity(userCity);
        workOrder.setRegion(userRegion);
        workOrder.setAddress(address);
        String addr = userProvince + userCity + userRegion + address;
        workOrder.setAddressDetail(addr);

        try {
            //根据地址获取经纬度
            Map<String, Double> map = BaiduMapUtil.getLngAndLatByAddress(addr);
            if (null != map) {
                workOrder.setAddrLatitude(map.get("lat").toString());
                workOrder.setAddrLongitude(map.get("lng").toString());
            }
        } catch (Exception e) {
            log.error("==========创建工单根据地址获取经纬度异常====addr" + addr);
        }

        //原云平台为单价*（总年份或总流量），相当于租赁费。
        workOrder.setFee(cost.getTotalFee());
        workOrder.setOpenAccountFee(cost.getInstallationFee());
        workOrder.setCostName(cost.getName());
        workOrder.setOldCostId(cost.getOldId());
        workOrder.setCostType(cost.getType());//计费方式保存到工单
        workOrder.setCostId(subOrderDetail.getCostId());

        workOrder.setPay(subOrder.getPay()); //是否支付:0-否；1-是
        workOrder.setPayType(subOrder.getPayType());
        workOrder.setPayStatus(subOrder.getPayStatus());
        if (subOrder.getPay() != null && subOrder.getPay()) {
            workOrder.setStep(WorkOrderInstallStep.PAID.value);
            workOrder.setTradeNo(subOrder.getTradeNo());
            workOrder.setPayTime(subOrder.getPayTime());
        }
        //支付端--云平台-- 1 经销商支付  2 用户支付  ； 翼猫V2.0-- 1:立即支付（经销商支付） 2:货到付款（用户支付）
        //派单方式：1-用户派单；2-系统派单
        if (dispatchType == DispatchType.OWN_SELECT.value) {
            engineer = userFeign.getEngineerById(subOrder.getEngineerId());
            // workOrder.setDispatchType(DispatchType.OWN_SELECT.value);
           /* workOrder.setEngineerId(engineer.getId());
            workOrder.setEngineerName(engineer.getRealName());
            workOrder.setEngineerPhone(engineer.getPhone());
            workOrder.setEngineerIdCard(engineer.getIdCard());
            workOrder.setStationId(engineer.getStationId());
            workOrder.setStationName(engineer.getStationName());*/
        } else {
            Integer areaId = systemFeign.getRegionIdByPCR(userProvince, userCity, userRegion);
            List<EngineerDTO> engineerList = userFeign.listEngineerByRegion(areaId);
            if (CollectionUtil.isNotEmpty(engineerList)) {
                engineer = engineerList.get(0);
                //系统自动派单更新子订单上的安装工信息
                OrderSub order = new OrderSub();
                order.setEngineerId(engineer.getId());
                order.setId(subOrder.getId());
                orderSubMapper.updateByPrimaryKeySelective(order);
                //更新子订单详情
                SubOrderDetail detail = new SubOrderDetail();
                detail.setEngineerId(engineer.getId());
                detail.setEngineerName(engineer.getRealName());
                detail.setEngineerPhone(engineer.getPhone());
                detail.setEngineerIdCard(engineer.getIdCard());
                detail.setEngineerProvince(userProvince);
                detail.setEngineerCity(userCity);
                detail.setEngineerRegion(userRegion);
                detail.setSubOrderId(subOrder.getId());
                subOrderDetailMapper.updateByPrimaryKeySelective(detail);

                //修改收益安装工  2020-04-21 修改
                ProductIncomeRecordPart part = productIncomeRecordPartMapper.selectPartIdBySubOrderId(workOrder.getSubOrderId());
                if (part != null) {
                    part.setSubjectId(engineer.getId());
                    part.setSubjectName(engineer.getRealName());
                    part.setSubjectPhone(engineer.getPhone());
                    part.setSubjectIdCard(engineer.getIdCard());
                    productIncomeRecordPartMapper.updateByPrimaryKeySelective(part);
                }
            }
        }
        workOrder.setDispatchType(dispatchType);
        //添加安装工信息
        if (engineer == null) {
            throw new YimaoException("创建工单失败，未获取到安装工信息");
        }
        workOrder.setEngineerId(engineer.getId());
        workOrder.setOldEngineerId(engineer.getOldId());
        workOrder.setEngineerName(engineer.getRealName());
        workOrder.setEngineerPhone(engineer.getPhone());
        workOrder.setEngineerIdCard(engineer.getIdCard());
        workOrder.setStationId(engineer.getStationId());
        workOrder.setStationName(engineer.getStationName());
        if (!StringUtil.isEmpty(engineer.getOldSiteId())) {
            workOrder.setOldStationId(engineer.getOldSiteId());
        }
        //更新安装工正在服务的工单数
        // userFeign.updateEngineerCount(engineer.getId(), 1);

        workOrder.setPayTerminal(subOrder.getPayTerminal());
        // workOrder.setType(WorkOrderInstallTypeEnum.USER_SELF_HELP_ORDER.getType());
        // workOrder.setWorkOrderIntallType(WorkOrderInstallTypeEnum.USER_SELF_HELP_ORDER.getMsg());
        workOrder.setSubOrderId(subOrder.getId());
        workOrder.setMainOrderId(subOrder.getMainOrderId());
        workOrder.setOldSubOrderId(subOrder.getId().toString());
        workOrder.setProductId(subOrder.getProductId());
        workOrder.setProductName(subOrderDetail.getProductName());
        workOrder.setDelStatus("N");//默认未删除
        if (workOrder.getDistributorType() != null && workOrder.getDistributorType() == UserType.DISTRIBUTOR_DISCOUNT_50.value
                && StringUtil.isNotEmpty(workOrder.getDistributorAccount()) && workOrder.getDistributorAccount().startsWith("clxd")) {
            workOrder.setRemark("该笔工单的安装服务费用，由经销商直接联系并支付给安装服务工程师。" + workOrder.getRemark());
        }
        return workOrder;
    }


    /**
     * 站务系统获取工单列表
     */
    @Override
    public PageVO<WorkOrderResultDTO> getStationWorkOrderList(Integer pageNum, Integer pageSize, WorkOrderQuery query) {
        //支付状态：1、未支付；2、待审核；3、支付成功；4、支付失败
        if (query.getStatus() == null || query.getStatus() == WorkOrderStateEnum.STATUS.state) {
            query.setStatus(null);
        } else if (query.getStatus() == WorkOrderStateEnum.PAY.state) {
            query.setStatus(null);
            query.setPay(true);
            query.setIsBackWorkOrder(StatusEnum.NO.value());
        } else if (query.getStatus() == WorkOrderStateEnum.AUDITING.state) {
            query.setStatus(WorkOrderStateEnum.WORKORDER_STATE_SERVING.state);
            query.setPayStatus(PayStatus.WAITING_AUDIT.value);//原值：1
        } else if (query.getStatus() == WorkOrderStateEnum.NOT_PASS.state) {
            query.setStatus(WorkOrderStateEnum.WORKORDER_STATE_SERVING.state);
            query.setPayStatus(PayStatus.FAIL.value);//原值：2
        } else if (query.getStatus() == WorkOrderStateEnum.WAITING_PAY.state) {
            query.setStatus(null);
            query.setPay(false);
            query.setIsBackWorkOrder(StatusEnum.NO.value());
        } else if (query.getStatus() == WorkOrderStateEnum.CUSTOMER_REJECT.state) {
            query.setStatus(null);
            query.setNotRefuse(StatusEnum.YES.value());
        } else if (query.getStatus() == WorkOrderStateEnum.CUSTOMER_DISPATCH.state) {
            query.setStatus(WorkOrderStateEnum.WORKORDER_STATE_NOT_ACCEPTED.state);
            query.setIsNotAllot(true);
        } else {
            query.setStatus(query.getStatus());
        }

        query.setDelStatus("N");//业务系统查询和导出未删除的工单列表

        PageHelper.startPage(pageNum, pageSize);

        //Page<WorkOrderResultDTO> ptPage = workOrderMapper.getWorkOrderListBypage(workOrderQueryDTO);

        Page<WorkOrderResultDTO> page = workOrderMapper.getStationWorkOrderList(query);

        return new PageVO<>(pageNum, page);
    }

    /**
     * 根据区域查询未受理，已受理，处理中，正常完成工单数
     */
    @Override
    public StationScheduleDTO getStationWorkerOrderNum(List<Integer> engineerIds) {

        return workOrderMapper.getStationWorkOrderNum(engineerIds);
    }

    /**
     * 给指定经销商下的工单“经销商第一次升级时间”字段赋值
     *
     * @param info
     */
    @Override
    public void setFirstUpgradeTime(Map<String, Object> info) {
        Integer distributorId = (Integer) info.get("distributorId");//经销商id
        Date time = (Date) info.get("time");//升级时间
        if (Objects.isNull(time) || distributorId == null) {
            throw new YimaoException("经销商id或升级时间不能为空！");
        }
        log.info("经销商id为{}，升级时间为{}", distributorId, DateUtil.transferDateToString(time, DateUtil.DATEFORMAT_02));

        WorkOrder query = new WorkOrder();
        query.setDistributorId(distributorId);
        List<WorkOrder> workOrders = workOrderMapper.select(query);
        if (CollectionUtil.isNotEmpty(workOrders)) {
            //给工单“经销商第一次升级时间”字段赋值
            for (WorkOrder workOrder : workOrders) {
                WorkOrder update = new WorkOrder();
                update.setId(workOrder.getId());
                update.setFirstUpgradeTime(time);
                workOrderMapper.updateByPrimaryKeySelective(update);
            }
        }
    }

    @Override
    public List<WorkOrderUnfinishedRsDTO> queryWorkOrderForUnfinished(TransferAreaInfoDTO trans) {
        return workOrderMapper.queryWorkOrderForUnfinished(trans.getProvince(), trans.getCity(), trans.getRegion());
    }

    @Override
    public List<WorkOrderStatDTO> getWorkOrderStatInfo(Integer engineerId) {
        List<WorkOrderStatDTO> list = initStatData();
        List<WorkOrderStatDTO> result = workOrderMapper.getWorkOrderStatInfo(engineerId);
        if (!CollectionUtil.isEmpty(result)) {
            for (WorkOrderStatDTO init : list) {
                for (WorkOrderStatDTO wo : result) {
                    if (init.getStatus() == wo.getStatus() && wo.getCount() != null) {
                        init.setCount(wo.getCount());
                        break;
                    }
                }
            }

            //按状态升序
            Collections.sort(list, new Comparator<WorkOrderStatDTO>() {
                @Override
                public int compare(WorkOrderStatDTO u1, WorkOrderStatDTO u2) {
                    long diff = u1.getStatus() - u2.getStatus();
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

        return list;
    }

    /***
     * 初始化数据
     * @return
     */
    private List<WorkOrderStatDTO> initStatData() {
        List<WorkOrderStatDTO> list = new ArrayList<WorkOrderStatDTO>();
        WorkOrderStatDTO wosd = new WorkOrderStatDTO();
        wosd.setStatus(1);
        wosd.setStatusName("待安装");
        wosd.setCount(0);
        list.add(wosd);
        wosd = new WorkOrderStatDTO();
        wosd.setStatus(2);
        wosd.setStatusName("处理中");
        wosd.setCount(0);
        list.add(wosd);
        wosd = new WorkOrderStatDTO();
        wosd.setStatus(3);
        wosd.setStatusName("挂单");
        wosd.setCount(0);
        list.add(wosd);
        wosd = new WorkOrderStatDTO();
        wosd.setStatus(4);
        wosd.setStatusName("已完成");
        wosd.setCount(0);
        list.add(wosd);
        wosd = new WorkOrderStatDTO();
        wosd.setStatus(5);
        wosd.setStatusName("退单");
        wosd.setCount(0);
        list.add(wosd);
        return list;
    }

    @Override
    public PageVO<WorkOrderRsDTO> getWorkOrderListForEngineer(WorkOrderReqDTO query, Integer pageNum, Integer pageSize) {
        query.setDelStatus("N");
        PageHelper.startPage(pageNum, pageSize);
        Page<WorkOrderRsDTO> ptPage = workOrderMapper.getWorkOrderListForEngineer(query);
        return new PageVO<>(pageNum, ptPage);
    }

    /****
     * 安装工app退单---:接单之后，签约之前才可以退单
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
    public void chargeback(WorkOrderReqDTO req) {
        WorkOrder wo = workOrderMapper.selectBasicInfoById(req.getWorkOrderId());
        log.info("====安装工退单，工单信息:" + JSONObject.toJSONString(wo));
        if (wo == null) {
            throw new YimaoException("安装工退单-获取工单失败");
        }

        //工单接单之后，签约之前才可以退单
        if (wo.getStep().intValue() < WorkOrderInstallNewStep.START.value
                || wo.getStep().intValue() > WorkOrderInstallNewStep.COMPLETE_WORK_ORDER.value) {
            throw new YimaoException("工单在当前安装步骤不可退单");
        }

        //如果已经创建了设备,则要删除设备
        String sncode = "";
        String iccid = "";
        String logisticsCode = "";
        if (wo.getDeviceId() != null) {
            WaterDeviceDTO device = waterFeign.getWaterDeviceById(wo.getDeviceId());
            if (device != null) {
                sncode = device.getSn();
                iccid = device.getIccid();
                logisticsCode = device.getLogisticsCode();
                if (StringUtil.isNotEmpty(device.getIccid())) {
                    waterFeign.deactivatedSimCard(device.getId());
                }
                waterFeign.deleteWaterDevice(device.getId());
            }
        }

        //更新工单
        Date now = new Date();
        WorkOrder update = new WorkOrder();
        update.setId(wo.getId());
        update.setOperationTime(now);
        update.setStatus(WorkOrderNewStatusEnum.COMPLETED.value);
        update.setChargeback(true);
        update.setChargebackType(req.getChargeBcakType());//1-经销商退单；2-客服退单
        update.setChargebackReason(req.getChargeBcakReason());
        update.setChargebackRemark(ChargeBcakTypeEnum.getChargeBcakTypeDesc(req.getChargeBcakType()));
        update.setChargebackSncode(sncode);
        update.setChargebackStatus(Integer.parseInt(WorkOrderChargebackEnum.BACKING.getState()));
        update.setChargebackTime(now);
        update.setCompleteType(WorkOrderCompleteEnum.AUTO_COMPLETE.getState());//异常完成
        update.setCompleteTime(now);
        workOrderMapper.updateForChargeback(update);

        //保存工单操作记录
        WorkOrderOperation operation = new WorkOrderOperation();
        operation.setAdmin(wo.getEngineerName());
        operation.setWorkOrderId(wo.getId());
        operation.setOperation("客服退单");
        operation.setRemark("当前sncode:" + sncode + ",simcard:" + iccid + ",batchCode:" + logisticsCode);
        operation.setCreateTime(now);
        workOrderOperationMapper.insertSelective(operation);

        //修改订单状态为待经销商审核
        orderSubService.updateOrderStatusForEngineer(req, wo.getSubOrderId());

        // TODO 退单，要把库存还回去

        DistributorDTO distributor = userFeign.getDistributorById(wo.getDistributorId());
        if (distributor == null) {
            throw new BadRequestException("退单失败：" + wo.getDistributorId() + "该经销商不存在！");
        }


        //根据订单号查询订单
        OrderSub sbuOrder = orderSubMapper.selectByPrimaryKey(wo.getSubOrderId());
        //发送通知
        Map<String, String> messageMap = new HashMap<>();
        messageMap.put("#code#", sbuOrder.getMainOrderId().toString());
        DistributorDTO distributorDTO = userFeign.getDistributorBasicInfoByIdForMsgPushInfo(sbuOrder.getDistributorId());
        if (Objects.isNull(distributorDTO)) {
            log.error("安装工退单通知推送失败，子订单未能找到经销商，子订单id=" + wo.getSubOrderId());
        }

        //为自己下单的订单
        if (sbuOrder.getDistributorId() != null && sbuOrder.getUserId().equals(distributorDTO.getUserId())) {
            //消息推送
            pushMsgToDistributorUser(MessageModelTypeEnum.VERIFY_NOTICE.value, MessageMechanismEnum.DISTRIBUTION_SELF_ORDER_AUDIT.value, MessageModelTypeEnum.VERIFY_NOTICE.name, distributorDTO.getUserId(), distributorDTO.getUserName(), distributorDTO.getAppType(), messageMap);
        } else {//为客户下单的订单
            //消息推送
            pushMsgToDistributorUser(MessageModelTypeEnum.VERIFY_NOTICE.value, MessageMechanismEnum.DISTRIBUTION_LATTER_ORDER_AUDIT.value, MessageModelTypeEnum.VERIFY_NOTICE.name, distributorDTO.getUserId(), distributorDTO.getUserName(), distributorDTO.getAppType(), messageMap);
        }


    }

    @Override
    public HashMap<String, List<MapOrderDTO>> getInstallWorkOrder(Integer engineerId) {
        HashMap<String, List<MapOrderDTO>> mapOrderList = new HashMap<>();
        try {
            //获取安装工的安装工单
            Callable<List<MapOrderDTO>> installCallable = () -> workOrderMapper.getInstallWorkOrder(engineerId);
            FutureTask<List<MapOrderDTO>> installFutureTask = new FutureTask<>(installCallable);
            ThreadUtil.executor.submit(installFutureTask);
            mapOrderList.put(MapOrderEnum.INSTALL.type, installFutureTask.get());


            //获取安装工的维修工单
            Callable<List<MapOrderDTO>> repairCallable = () -> workRepairOrderMapper.getRepairWorkOrder(engineerId);
            FutureTask<List<MapOrderDTO>> repairFutureTask = new FutureTask<>(repairCallable);
            ThreadUtil.executor.submit(repairFutureTask);
            mapOrderList.put(MapOrderEnum.REPAIR.type, repairFutureTask.get());


            //获取安装工的维护工单
            Callable<List<MapOrderDTO>> maintainCallable = () -> maintenanceWorkOrderMapper.getMaintenanceWorkOrder(engineerId);
            FutureTask<List<MapOrderDTO>> maintainFutureTask = new FutureTask<>(maintainCallable);
            ThreadUtil.executor.submit(maintainFutureTask);
            List<MapOrderDTO> dtos = maintainFutureTask.get();
            if (CollectionUtil.isNotEmpty(dtos)) {
                for (MapOrderDTO dto : dtos) {
                    for (int i = 1; i < 3; i++) {
                        MapOrderDTO dto1 = workOrderMapper.mergeOrder(dto.getSn(), i, engineerId);
                        if (dto1 != null) {
                            dto.setCreateTimes(dto1.getCreateTimes());
                            dto.setMaterielDetailNames(dto1.getMaterielDetailNames());
                        }
                    }
                }
            }
            mapOrderList.put(MapOrderEnum.MAINTAIN.type, dtos);


            //获取安装工的移机工单
            Callable<List<MapOrderDTO>> moveCallable = () -> moveWaterDeviceOrderMapper.getMoveWaterDeviceOrder(engineerId);
            FutureTask<List<MapOrderDTO>> moveFutureTask = new FutureTask<>(moveCallable);
            ThreadUtil.executor.submit(moveFutureTask);
            mapOrderList.put(MapOrderEnum.MOVE.type, moveFutureTask.get());


            //获取安装工的退机工单
            Callable<List<MapOrderDTO>> backCallable = () -> workOrderBackMapper.getBackWaterDeviceOrder(engineerId);
            FutureTask<List<MapOrderDTO>> backFutureTask = new FutureTask<>(backCallable);
            ThreadUtil.executor.submit(backFutureTask);
            mapOrderList.put(MapOrderEnum.BACK.type, backFutureTask.get());

        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new YimaoException("安装工工单地图查询出错");
        }

        return mapOrderList;
    }


    @Override
    public List<RenewDTO> getInstallWorderOrderList(String completeTime, Integer engineerId, Integer timeType) {
        return workOrderMapper.getInstallWorderOrderList(completeTime, engineerId, timeType);
    }

    @Override
    public Map<String, Integer> getInstallWaterDeviceCount(Integer engineerId) {
        Map<String, Integer> installMap = new HashMap<String, Integer>();
        //待安装
        installMap.put("toBeInstallCount", workOrderMapper.getWorkOrderCount(engineerId, 1));
        //处理中
        installMap.put("processingWorkOrderCount", workOrderMapper.getWorkOrderCount(engineerId, 2));
        //挂单
        installMap.put("putUpWorkOrderCount", workOrderMapper.getWorkOrderCount(engineerId, 3));
        //已完成
        installMap.put("completeWorkOrderCount", workOrderMapper.getWorkOrderCount(engineerId, 4));
        //退单
        installMap.put("backWorkOrderCount", workOrderMapper.getWorkOrderCount(engineerId, 5));
        return installMap;
    }


    @Override
    public Integer getInstallWaterDeviceTotalNum(Integer engineerId) {
        return workOrderMapper.getInstallWaterDeviceTotalCount(engineerId);
    }
}
