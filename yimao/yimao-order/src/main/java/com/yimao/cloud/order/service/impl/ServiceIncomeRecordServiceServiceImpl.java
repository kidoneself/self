package com.yimao.cloud.order.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.yimao.cloud.base.enums.HraType;
import com.yimao.cloud.base.enums.IncomeAllotType;
import com.yimao.cloud.base.enums.IncomeSubjectEnum;
import com.yimao.cloud.base.enums.IncomeType;
import com.yimao.cloud.base.enums.ProductModeEnum;
import com.yimao.cloud.base.exception.BadRequestException;
import com.yimao.cloud.order.feign.HraFeign;
import com.yimao.cloud.order.feign.SystemFeign;
import com.yimao.cloud.order.mapper.IncomeSubjectMapper;
import com.yimao.cloud.order.mapper.ServiceIncomeRecordMapper;
import com.yimao.cloud.order.mapper.ServiceIncomeRecordPartMapper;
import com.yimao.cloud.order.po.IncomeSubject;
import com.yimao.cloud.order.po.ServiceIncomeRecord;
import com.yimao.cloud.order.po.ServiceIncomeRecordPart;
import com.yimao.cloud.order.service.IncomeRuleService;
import com.yimao.cloud.order.service.OrderSubService;
import com.yimao.cloud.order.service.ProductIncomeRuleService;
import com.yimao.cloud.order.service.ServiceIncomeRecordService;
import com.yimao.cloud.pojo.dto.hra.HraDeviceDTO;
import com.yimao.cloud.pojo.dto.hra.HraTicketDTO;
import com.yimao.cloud.pojo.dto.order.IncomeExportDTO;
import com.yimao.cloud.pojo.dto.order.IncomeRecordPartResultDTO;
import com.yimao.cloud.pojo.dto.order.IncomeRecordResultDTO;
import com.yimao.cloud.pojo.dto.order.IncomeRuleDTO;
import com.yimao.cloud.pojo.dto.order.IncomeRulePartDTO;
import com.yimao.cloud.pojo.dto.order.OrderSubDTO;
import com.yimao.cloud.pojo.dto.order.ProductIncomeQueryDTO;
import com.yimao.cloud.pojo.dto.order.ServiceIncomeRecordPartDTO;
import com.yimao.cloud.pojo.dto.system.StationCompanyDTO;
import com.yimao.cloud.pojo.dto.system.StationDTO;
import com.yimao.cloud.pojo.vo.PageVO;
import com.yimao.cloud.pojo.vo.order.ProductIncomeVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 服务收益
 *
 * @author Liu Yi
 * @date 2019/1/129
 */
@Service
@Slf4j
public class ServiceIncomeRecordServiceServiceImpl implements ServiceIncomeRecordService {

    @Resource
    private HraFeign hraFeign;
    @Resource
    private ProductIncomeRuleService productIncomeRuleService;
    @Resource
    private SystemFeign systemFeign;
    @Resource
    private ServiceIncomeRecordMapper serviceIncomeRecordMapper;
    @Resource
    private ServiceIncomeRecordPartMapper serviceIncomeRecordPartMapper;
    @Resource
    private OrderSubService orderSubService;
    @Resource
    private IncomeRuleService incomeRuleService;
    @Resource
    private IncomeSubjectMapper incomeSubjectMapper;
    // @Resource
    // private SubOrderDetailMapper subOrderDetailMapper;

    /**
     * 描述：分配服务收益（HRA）
     *
     * @param ticketNo  评估券号
     * @param stationId 服务站门店ID
     * @param deviceId  HRA设备ID
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
    public void serviceAllot(String ticketNo, Integer stationId, String deviceId) {
        try {
            //根据评估卡号获取评估信息
            HraTicketDTO ticket = hraFeign.getTicketByNo(ticketNo);//获取评估信息
            if (ticket == null) {
                log.error("产品服务收益分配失败-没有查询到评估卡信息。ticketNo=" + ticketNo);
                return;
            }
            // 考虑到异步执行状态一致性问题，暂时先注释掉  2020-04-16
            // if (ticket.getTicketStatus() != HraTicketStatusEnum.HRA_USE.value) {
            //     log.error("产品服务收益分配失败-评估卡状态没有变为已完成。ticketNo=" + ticketNo);
            //     return;
            // }
            if (ticketNo.startsWith(HraType.Y.value) || ticketNo.startsWith(HraType.M.value)) {//"Y"表示用户用卡，"F"表示服务站专用卡，"M"表示免费用卡
                //1-先获取子订单
                Long orderId = ticket.getOrderId();//获取订单号id
                OrderSubDTO subOrder = orderSubService.findOrderById(orderId);
                if (subOrder == null) {
                    log.error("产品服务收益分配失败-没有查询到订单信息。");
                    return;
                }
                //2-获取收益分配规则
                Set<Integer> incomeRuleIds = productIncomeRuleService.listIncomeRuleIdByProductId(subOrder.getProductId());
                IncomeRuleDTO incomeRule = incomeRuleService.getIncomeRuleByIdAndIncomeType(incomeRuleIds, IncomeType.SERVICE_INCOME.value);
                if (incomeRule == null) {
                    log.error("产品服务收益分配失败-没有查询到产品的收益分配规则。ticketNo=" + ticketNo);
                    return;
                }

                //获取服务站信息
                StationDTO station = systemFeign.getStationById(stationId);
                if (station == null) {
                    log.error("产品服务收益分配失败-没有查询到服务站信息。ticketNo=" + ticketNo);
                    return;
                }

                //商品类型（大类）:1实物商品，2电子卡券，3租赁商品 对应product:type
                if (ProductModeEnum.VIRTUAL.value == subOrder.getProductType()) {
                    ServiceIncomeRecord record = new ServiceIncomeRecord();
                    record.setMainOrderId(subOrder.getMainOrderId());
                    record.setOrderId(subOrder.getId());
                    record.setOrderFee(subOrder.getFee());
                    record.setTicketNo(ticketNo);
                    record.setDeviceId(deviceId);
                    record.setServiceTime(ticket.getUseTime());
                    record.setStationId(station.getId());
                    record.setStationName(station.getName());
                    record.setStationProvince(station.getProvince());
                    record.setStationCity(station.getCity());
                    record.setStationRegion(station.getRegion());

                    record.setProductId(subOrder.getProductId());
                    record.setProductPrice(subOrder.getProductPrice());
                    record.setProductName(subOrder.getProductName());
                    record.setProductCompanyId(subOrder.getProductCompanyId());
                    record.setProductCompanyName(subOrder.getProductCompanyName());
                    record.setProductCategoryId(subOrder.getProductCategoryId());
                    record.setProductCategoryName(subOrder.getProductCategoryName());

                    record.setUserId(subOrder.getUserId());
                    record.setUserType(subOrder.getUserType());
                    record.setUserTypeName(subOrder.getUserTypeName());
                    record.setUserName(subOrder.getUserName());
                    record.setUserPhone(subOrder.getUserPhone());

                    record.setDistributorId(subOrder.getDistributorId());
                    record.setDistributorName(subOrder.getDistributorName());
                    record.setDistributorTypeName(subOrder.getDistributorTypeName());
                    record.setDistributorAccount(subOrder.getDistributorAccount());
                    record.setDistributorProvince(subOrder.getDistributorProvince());
                    record.setDistributorCity(subOrder.getDistributorCity());
                    record.setDistributorRegion(subOrder.getDistributorRegion());

                    record.setIncomeRuleId(incomeRule.getId());
                    record.setIncomeType(incomeRule.getIncomeType());
                    record.setAllotType(incomeRule.getAllotType());
                    record.setSettlementFee(subOrder.getFee());//可分配金额

                    record.setCreateTime(new Date());
                    serviceIncomeRecordMapper.insert(record);

                    //结算规则=>根据订单类型，查询服务结算规则
                    //根据收益规则信息获取到用户结算对象
                    List<IncomeRulePartDTO> rulePartList = incomeRule.getIncomeRuleParts();
                    Map<String, IncomeRulePartDTO> rulePartMap = new HashMap<>();
                    for (IncomeRulePartDTO rulePart : rulePartList) {
                        rulePartMap.put(rulePart.getSubjectCode(), rulePart);
                    }
                    List<ServiceIncomeRecordPart> recordPartList = new ArrayList<>();

                    //1-服务站承包人（服务收益）
                    IncomeRulePartDTO rulePart = rulePartMap.get(IncomeSubjectEnum.STATION_CONTRACTOR_SERVICE.value);
                    if (rulePart != null) {
                        ServiceIncomeRecordPart recordPart = this.createServiceIncomeRecordPart(record, incomeRule, rulePart);

                        //修改 服务收益不管承包是否
//                        Date balanceTime = recordPart.getSettlementTime();
//                        boolean canBalanceToStation = station.getContract() != null &&
//                                                      station.getContract() &&
//                                                      station.getContractStartTime() != null &&
//                                                      station.getContractStartTime().before(balanceTime) &&
//                                                      station.getContractEndTime() != null &&
//                                                      station.getContractEndTime().after(balanceTime);
//                      if(canBalanceToStation){
                        //收益主体为服务站承包人
                        recordPart.setSubjectId(station.getId());
                        recordPart.setSubjectCode(rulePart.getSubjectCode());
                        recordPart.setSubjectName(station.getContractor());//服务站承包人姓名
                        recordPart.setSubjectPhone(station.getContractorPhone());
                        recordPart.setSubjectProvince(station.getProvince());
                        recordPart.setSubjectCity(station.getCity());
                        recordPart.setSubjectRegion(station.getRegion());
//                      }

                        HraDeviceDTO hraDevice = hraFeign.getHraDeviceByDeviceId(deviceId);
                        if (hraDevice != null) {
                            Integer stationCompanyId = hraDevice.getStationCompanyId();
                            if (stationCompanyId != null) {
                                StationCompanyDTO stationCompany = systemFeign.getStationCompanyById(stationCompanyId);
                                if (stationCompany != null) {
                                    //结算主体为服务站承包人所属区县级公司
                                    recordPart.setSettlementSubjectId(stationCompany.getId());
                                    recordPart.setSettlementSubjectName(stationCompany.getName());
                                }
                            }
                        }
                        recordPartList.add(recordPart);
                    }
                    serviceIncomeRecordPartMapper.batchInsert(recordPartList);
                }
            }
        } catch (Exception e) {
            log.error("服务站评估服务收益分配---出错", e.getMessage());
            log.error("服务站评估服务收益分配---出错---ticketNo=" + ticketNo);
        }
    }

    /**
     * 查询服务收益
     *
     * @param incomeQueryDTO 查询dto
     * @param pageNum        页码
     * @param pageSize       页数
     */
    @Override
    public PageVO<ProductIncomeVO> pageQueryServiceIncome(ProductIncomeQueryDTO incomeQueryDTO, Integer pageNum, Integer pageSize) {

        PageHelper.startPage(pageNum, pageSize);
        Page<ProductIncomeVO> page = serviceIncomeRecordMapper.queryServiceIncomeList(incomeQueryDTO);
        SimpleDateFormat sdf;
        Calendar cal;

        for (ProductIncomeVO incomeVO : page) {
            if (incomeVO.getOrderCompleteTime() != null) {
                sdf = new SimpleDateFormat("yyyy-MM");
                cal = Calendar.getInstance();
                cal.setTime(incomeVO.getOrderCompleteTime());//设置起时间
                cal.add(Calendar.MONTH, 1);//增加一个月
                incomeVO.setOrderSettlementTime(sdf.format(cal.getTime()));
            }
        }
        return new PageVO<>(pageNum, page);
    }

    /**
     * 根据订单id查询收益记录
     *
     * @param orderId 订单id
     */
    @Override
    public List<ServiceIncomeRecordPartDTO> getServiceIncomeRecordPartList(Long orderId) {
        if (orderId == null) {
            throw new BadRequestException("订单id必须填写！");
        }

        Example example = new Example(ServiceIncomeRecord.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("orderId", orderId);

        List<ServiceIncomeRecord> list = serviceIncomeRecordMapper.selectByExample(example);

        if (list == null || list.size() <= 0) {
            throw new BadRequestException("订单信息有误！");
        }

        Example example1 = new Example(ServiceIncomeRecordPart.class);
        Example.Criteria criteria1 = example1.createCriteria();
        criteria1.andEqualTo("serviceIncomeRecordId", list.get(0).getId());
        List<ServiceIncomeRecordPart> listTemp = serviceIncomeRecordPartMapper.selectByExample(example1);

        List<ServiceIncomeRecordPartDTO> dtoList = new ArrayList<>();
        ServiceIncomeRecordPartDTO serviceIncomeRecordPartDTO;
        for (ServiceIncomeRecordPart serviceIncomeRecordPart : listTemp) {
            serviceIncomeRecordPartDTO = new ServiceIncomeRecordPartDTO();
            serviceIncomeRecordPart.convert(serviceIncomeRecordPartDTO);

            dtoList.add(serviceIncomeRecordPartDTO);
        }

        return dtoList;
    }

    /**
     * 根据id查询收益记录
     *
     * @param id id
     */
    @Override
    public IncomeRecordResultDTO getServiceIncomeById(Integer id) {
        if (id == null) {
            throw new BadRequestException("id必须填写！");
        }

        IncomeRecordResultDTO incomeRecord = serviceIncomeRecordMapper.selectIncomeRecordByPrimaryKey(id);
        if (incomeRecord == null) {
            throw new BadRequestException("收益id信息有误！");
        }

        List<IncomeRecordPartResultDTO> list = serviceIncomeRecordPartMapper.getPartByServiceIncomeRecordId(id);
        for (IncomeRecordPartResultDTO dto : list) {
            dto.setCompanyName(dto.getSettlementSubjectName());
            String incomeSubjectCode = dto.getIncomeSubjectCode();
            Example example = new Example(IncomeSubject.class);
            Example.Criteria criteria1 = example.createCriteria();
            criteria1.andEqualTo("incomeSubjectCode", incomeSubjectCode);
            IncomeSubject incomeSubject = incomeSubjectMapper.selectOneByExample(example);
            if (incomeSubject != null) {
                dto.setIncomeSubjectName(incomeSubject.getIncomeSubjectName());
            }

        }
        incomeRecord.setIncomePartResults(list);

        return incomeRecord;
    }


    /**
     * 创建收益分配记录（明细）
     *
     * @param record   收益分配记录
     * @param rule     收益分配规则
     * @param rulePart 收益分配规则
     */
    private ServiceIncomeRecordPart createServiceIncomeRecordPart(ServiceIncomeRecord record, IncomeRuleDTO rule, IncomeRulePartDTO rulePart) {
        ServiceIncomeRecordPart recordPart = new ServiceIncomeRecordPart();
        //收益ID
        recordPart.setRecordId(record.getId());
        //获取收益主体
        Integer subjectId = rulePart.getSubjectId();
        IncomeSubject subject = incomeSubjectMapper.selectByPrimaryKey(subjectId);
        if (rule.getAllotType() == IncomeAllotType.RATIO.value) {
            //收益比例
            recordPart.setSubjectRatio(rulePart.getValue());
            //收益金额
            recordPart.setSubjectMoney(record.getSettlementFee().multiply(rulePart.getValue().divide(new BigDecimal(100))));
        } else {
            //收益金额
            recordPart.setSubjectMoney(rulePart.getValue());
        }
        recordPart.setSubjectCode(subject.getIncomeSubjectCode());
        recordPart.setSubjectName(subject.getIncomeSubjectName());
        recordPart.setSettlementSubjectCode(subject.getSettlementSubjectCode());
        recordPart.setSettlementSubjectName(subject.getSettlementSubjectName());
        recordPart.setSettlementTime(record.getCreateTime());
        recordPart.setCreateTime(record.getCreateTime());
        return recordPart;
    }

    @Override
    public List<IncomeExportDTO> serviceIncomeExport(ProductIncomeQueryDTO query) {
        long l1 = System.currentTimeMillis();
        List<IncomeExportDTO> incomeExportDTOS = serviceIncomeRecordMapper.serviceIncomeExport(query);
        long l2 = System.currentTimeMillis();
        log.info("serviceIncomeExport---SQL执行耗时：" + (l2 - l1) + "毫秒。");
        /*if (CollectionUtil.isNotEmpty(incomeExportDTOS)) {
            for (IncomeExportDTO incomeExportDTO : incomeExportDTOS) {
                String ticketNo = incomeExportDTO.getTicketNo();
                if (ticketNo != null) {
                    HraTicketDTO dto = hraFeign.getTicketByNo(ticketNo);
                    if (dto != null) {
                        incomeExportDTO.setMedicalName(dto.getCustomerName());
                        incomeExportDTO.setMedicalPhone(dto.getCustomerPhone());
                    }
                }
                *//*List<ServiceIncomeRecordPartDTO> serviceRecordPartList = incomeExportDTO.getServiceRecordPartList();
                if (CollectionUtil.isNotEmpty(serviceRecordPartList)) {
                    serviceRecordPartList.forEach(o -> {
                        incomeExportDTO.setStationCompanyName(o.getSettlementSubjectName());
                        String subjectCode = o.getSubjectCode();
                        if (subjectCode != null && subjectCode.equals(OrderConstant.STATION_CONTRACTOR_SERVICE)) {
                            incomeExportDTO.setStationContractorService(o.getSubjectMoney());
                        }
                    });
                }*//*
                String orderCompleteTime = incomeExportDTO.getOrderCompleteTime();
                if (orderCompleteTime != null) {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
                    Date dt = null;
                    try {
                        dt = sdf.parse(orderCompleteTime);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    Calendar cal = Calendar.getInstance();
                    if (dt != null) {
                        cal.setTime(dt);//设置起时间
                        cal.add(Calendar.MONTH, 1);//增加一个月
                        String format = sdf.format(cal.getTime());
                        incomeExportDTO.setSettlementMonth(format);
                    }
                }
            }

        }*/
        return incomeExportDTOS;
    }

}
