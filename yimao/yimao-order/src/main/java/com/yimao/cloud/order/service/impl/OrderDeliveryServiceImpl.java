package com.yimao.cloud.order.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.yimao.cloud.base.cache.UserCache;
import com.yimao.cloud.base.constant.Constant;
import com.yimao.cloud.base.enums.OrderStatusEnum;
import com.yimao.cloud.base.exception.BadRequestException;
import com.yimao.cloud.base.exception.NotFoundException;
import com.yimao.cloud.base.exception.YimaoException;
import com.yimao.cloud.base.utils.*;
import com.yimao.cloud.order.mapper.*;
import com.yimao.cloud.order.po.*;
import com.yimao.cloud.order.service.OrderDeliveryService;
import com.yimao.cloud.pojo.dto.order.*;
import com.yimao.cloud.pojo.vo.PageVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.*;

/**
 * @author Chen Hui Yang
 * @date 2018/12/27
 */
@Slf4j
@Service
public class OrderDeliveryServiceImpl implements OrderDeliveryService {

    @Resource
    private OrderDeliveryMapper orderDeliveryMapper;
    @Resource
    private UserCache userCache;
    @Resource
    private OrderSubMapper orderSubMapper;
    @Resource
    private OrderStatusRecordMapper orderStatusRecordMapper;
    @Resource
    private SubOrderDetailMapper subOrderDetailMapper;
    @Resource
    private OrderDeveryRecordMapper orderDeveryRecordMapper;

    @Resource
    private DeliveryAddressMapper deliveryAddressMapper;

    @Override
    public void add(OrderDelivery orderDelivery) {
        if (orderDelivery.getOrderId() == null) {
            throw new BadRequestException("子订单号不能为空");
        }
        if (StringUtil.isEmpty(orderDelivery.getLogisticsNo())) {
            throw new BadRequestException("物流单号不能为空");
        }
        if (StringUtil.isEmpty(orderDelivery.getLogisticsCompany())) {
            throw new BadRequestException("物流公司名称不能为空");
        }
        int i = orderDeliveryMapper.insert(orderDelivery);
        if (i == 0) {
            throw new BadRequestException("新增失败");
        }
    }


    /**
     * 导入物流信息，修改订单状态
     *
     * @param multipartFile
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
    @Override
    public Object importExcel(MultipartFile multipartFile) {
        DeliveryAddress address = new DeliveryAddress();
        address.setOperatorId(userCache.getUserId());
        address.setHasDelivery(1);
        DeliveryAddress deliveryAddress = deliveryAddressMapper.selectOne(address);
        if (null == deliveryAddress) {
            throw new NotFoundException("操作人没有设置默认发货地址");
        }

        DeliveryAddressDTO dto = new DeliveryAddressDTO();
        deliveryAddress.convert(dto);

        if (multipartFile != null && multipartFile.getSize() > 0L) {
            String fileName = multipartFile.getOriginalFilename();
            String suffixName = fileName.substring(fileName.lastIndexOf(".") + 1);
            if (!suffixName.equals("xls") && !suffixName.equals("xlsx")) {
                throw new BadRequestException("请使用.xls或者.xlsx格式导入！");
            }

            String[] beanPropertys = new String[]{"deliveryTime", "orderId", "logisticsCompany", "logisticsNo",
                    "num", "boxNum", "weigh", "payType", "logisticsFee", "remark"};
            Map<String, Object> map = new HashMap<>(8);
            try {
                List<OrderDeliveryDTO> deliveryList = ExcelUtil.parserExcel(OrderDeliveryDTO.class, multipartFile,
                        beanPropertys, 1);
                if (CollectionUtil.isNotEmpty(deliveryList)) {
                    log.info(">>>>>>>>>>>>>>导入订单数量:" + deliveryList.size() + ">>>>>>>>>>>>");
                    Map<String, Object> resultMap = this.checkLogisticsTrackingOrder(deliveryList);
                    if (CollectionUtil.isNotEmpty((List) resultMap.get("failedOrdersList"))) {
                        map.put("failedOrdersList", resultMap.get("failedOrdersList"));
                    }
                    List<OrderDeliveryDTO> orderDeliveryDTOList = (List<OrderDeliveryDTO>) resultMap.get("deliveryList");
                    if (CollectionUtil.isNotEmpty(orderDeliveryDTOList)) {
                        Date date = new Date();
                        String currentAdminRealName = userCache.getCurrentAdminRealName();
                        List<OrderStatusRecord> list = new ArrayList<>();
                        List<OrderDeliveryRecord> deliveryRecords = new ArrayList<>();
                        for (OrderDeliveryDTO delivery : orderDeliveryDTOList) {
                            log.info("=========导入物流跟踪表,订单号：" + delivery.getOrderId() + "=========");
                            Long orderId = delivery.getOrderId();
                            delivery.setCreateTime(date);
                            delivery.setCreator(currentAdminRealName);
                            delivery.setPayType(delivery.getPayType());
                            //修改子订单状态 由待出库变为待收货
                            OrderSub orderSub = new OrderSub();
                            orderSub.setStatus(OrderStatusEnum.PENDING_RECEIPT.value);
                            //orderSub.setDeliveryTime(delivery.getDeliveryTime()); //发货时间
                            orderSub.setId(orderId);
                            orderSubMapper.updateByPrimaryKeySelective(orderSub);

                            OrderStatusRecord orderStatusRecord = new OrderStatusRecord();
                            orderStatusRecord.setId(orderId);
                            orderStatusRecord.setCreateTime(date);
                            orderStatusRecord.setCreator(currentAdminRealName);
                            orderStatusRecord.setDestStatus(OrderStatusEnum.PENDING_RECEIPT.value);//变更之后的状态
                            orderStatusRecord.setOrigStatus(OrderStatusEnum.TO_BE_OUT_OF_STOCK.value);//变更之前的状态
                            list.add(orderStatusRecord);

                            OrderDeliveryRecord deliveryRecord = new OrderDeliveryRecord();
                            deliveryRecord.setLogisticsNo(delivery.getLogisticsNo());
                            deliveryRecord.setLogisticsCompany(delivery.getLogisticsCompany());
                            deliveryRecord.setLogisticsFee(delivery.getLogisticsFee());
                            deliveryRecord.setRemark(delivery.getRemark());
                            deliveryRecord.setOrderId(orderId);

                            OrderSub subOrder = orderSubMapper.selectByPrimaryKey(orderId);
                            if (null != subOrder) {
                                deliveryRecord.setMainOrderId(subOrder.getMainOrderId());
                                deliveryRecord.setTerminal(subOrder.getTerminal());
                            }

                            //发货记录
                            SubOrderDetail subOrderDetail = subOrderDetailMapper.selectByPrimaryKey(orderId);
                            if(null != subOrderDetail){
                                this.setDeliveryRecordPartInfo(subOrderDetail, deliveryRecord, dto);
                            }
                            deliveryRecord.setPayType(delivery.getPayType());
                            deliveryRecord.setNum(delivery.getNum());
                            deliveryRecord.setBoxNum(delivery.getBoxNum());
                            deliveryRecord.setDeliveryTime(delivery.getDeliveryTime());
                            deliveryRecord.setWeigh(delivery.getWeigh());
                            deliveryRecords.add(deliveryRecord);
                        }
                        //批量添加物流信息
                        orderDeliveryMapper.insertBatch(orderDeliveryDTOList);
                        log.info("============导入物流跟踪表,添加物流信息成功============");
                        //批量添加订单状态变更记录
                        orderStatusRecordMapper.insertBatch(list);
                        log.info("============导入物流跟踪表,添加订单状态变更记录成功============");
                        //批量添加发货记录
                        orderDeveryRecordMapper.insertBatch(deliveryRecords);
                        log.info(">>>>>>>>>>>>>>批量添加发货记录>>>>>>>>>");
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
                throw new YimaoException("导入物流信息失败！");
            }
            return map;
        }
        throw new YimaoException("导入物流信息失败！");
    }

    private Map<String, Object> checkLogisticsTrackingOrder(List<OrderDeliveryDTO> deliveryList) {
        //需要排除的
        List<OrderDeliveryDTO> excludeList = new ArrayList<>();
        Map<Long, Integer> allMap = new HashMap<>(8);

        List<FailedOrdersDTO> failedOrdersList = new ArrayList<>();
        //1-取出重复的
        for (OrderDeliveryDTO dto : deliveryList) {
            Integer count = 1;
            if (Objects.nonNull(allMap.get(dto.getOrderId()))) {
                count = allMap.get(dto.getOrderId()) + 1;
            }
            allMap.put(dto.getOrderId(), count);
        }

        //1 取出所有 >1 订单ID
        if (CollectionUtil.isNotEmpty(deliveryList)) {
            for (OrderDeliveryDTO orderDelivery : deliveryList) {
                Long orderId = orderDelivery.getOrderId();

                if (null == orderId) {
                    throw new BadRequestException("子订单号不能为空");
                }
                if (null == orderDelivery.getDeliveryTime()) {
                    throw new BadRequestException("发货日期不能为空");
                }
                if (StringUtil.isEmpty(orderDelivery.getLogisticsCompany())) {
                    throw new BadRequestException("快递公司不能为空");
                }
                if (StringUtil.isEmpty(orderDelivery.getLogisticsNo())) {
                    throw new BadRequestException("快递单号不能为空");
                }
                if (null == orderDelivery.getNum()) {
                    throw new BadRequestException("发货件数不能为空");
                }
                if (null == orderDelivery.getBoxNum()) {
                    throw new BadRequestException("数量/盒不能为空");
                }
                if (null == orderDelivery.getWeigh()) {
                    throw new BadRequestException("重量不能为空");
                }
                if (null == orderDelivery.getPayType()) {
                    throw new BadRequestException("付款方式不能为空");
                }
                if (null == orderDelivery.getLogisticsFee()) {
                    throw new BadRequestException("快递费不能为空");
                }

                //不同的快递公司对于不同的code，暂时只有下面四种
                if (orderDelivery.getLogisticsCompany().equals(Constant.UC)) {
                    orderDelivery.setLogisticsCompanyCode(Constant.UC_CODE);
                } else if (orderDelivery.getLogisticsCompany().equals(Constant.YTO)) {
                    orderDelivery.setLogisticsCompanyCode(Constant.YTO_CODE);
                } else if (orderDelivery.getLogisticsCompany().equals(Constant.ANE)) {
                    orderDelivery.setLogisticsCompanyCode(Constant.ANE_CODE);
                } else if (orderDelivery.getLogisticsCompany().equals(Constant.YMDD)) {
                    orderDelivery.setLogisticsCompanyCode(Constant.YMDD_CODE);
                } else if (orderDelivery.getLogisticsCompany().equals(Constant.SF)) {
                    orderDelivery.setLogisticsCompanyCode(Constant.SF_CODE);
                } else if (orderDelivery.getLogisticsCompany().equals(Constant.ZTO)) {
                    orderDelivery.setLogisticsCompanyCode(Constant.ZTO_CODE);
                } else if (orderDelivery.getLogisticsCompany().equals(Constant.STO)) {
                    orderDelivery.setLogisticsCompanyCode(Constant.STO_CODE);
                } else if (orderDelivery.getLogisticsCompany().equals(Constant.YD)) {
                    orderDelivery.setLogisticsCompanyCode(Constant.YD_CODE);
                } else if (orderDelivery.getLogisticsCompany().equals(Constant.YZPY)) {
                    orderDelivery.setLogisticsCompanyCode(Constant.YZPY_CODE);
                } else if (orderDelivery.getLogisticsCompany().equals(Constant.EMS)) {
                    orderDelivery.setLogisticsCompanyCode(Constant.EMS_CODE);
                } else {
                    log.info("错误的物流公司：订单号" + orderId);
                    createFailedOrder(failedOrdersList, orderId, null, "错误的物流公司：订单号" + orderId);
                    excludeList.add(orderDelivery);
                    continue;
                }

                //订单号不存在
                OrderSub orderSub = orderSubMapper.selectByPrimaryKey(orderId);
                if (null == orderSub) {
                    createFailedOrder(failedOrdersList, orderId, null, "该订单不存在");
                    excludeList.add(orderDelivery);
                } else {
                    //待出库状态的订单
                    if (OrderStatusEnum.TO_BE_OUT_OF_STOCK.value != orderSub.getStatus()) {
                        createFailedOrder(failedOrdersList, orderId, orderSub.getStatus(), "非待出库订单");
                        excludeList.add(orderDelivery);
                    } else {
                        //重复的订单
                        if (Objects.nonNull(allMap.get(orderId)) && allMap.get(orderId) > 1) {
                            createFailedOrder(failedOrdersList, orderId, orderSub.getStatus(), "订单号重复");
                            excludeList.add(orderDelivery);
                        } else {
                            //如果发货列表已存在
                            Example example = new Example(OrderDelivery.class);
                            Example.Criteria criteria = example.createCriteria();
                            criteria.andEqualTo("orderId", orderDelivery.getOrderId());
                            List<OrderDelivery> orderDeliveries = orderDeliveryMapper.selectByExample(example);
                            if (CollectionUtil.isNotEmpty(orderDeliveries)) {
                                orderDeliveryMapper.deleteByPrimaryKey(orderDeliveries.get(0).getId());
                            }
                        }
                    }
                }
            }


            //移除不正常的元素
            if (CollectionUtil.isNotEmpty(excludeList) && CollectionUtil.isNotEmpty(deliveryList)) {
                deliveryList.removeAll(excludeList);
            }
        }
        Map<String, Object> map = new HashMap<>(8);
        map.put("failedOrdersList", failedOrdersList);
        map.put("deliveryList", deliveryList);
        return map;
    }


    private void createFailedOrder(List<FailedOrdersDTO> failedOrdersList, Long orderId, Integer status, String reason) {
        FailedOrdersDTO failedOrdersDTO = new FailedOrdersDTO();
        failedOrdersDTO.setOrderId(orderId);
        failedOrdersDTO.setStatus(status + "");
        failedOrdersDTO.setFailReason(reason);
        failedOrdersList.add(failedOrdersDTO);
    }

//    private Integer getPayType(String payTypeName) {
//        if ("微信".equals(payTypeName)) {
//            return 1;
//        } else if ("支付宝".equals(payTypeName)) {
//            return 2;
//        } else if ("POS机".equals(payTypeName)) {
//            return 3;
//        } else {
//            return 4;
//        }
//    }


    @Override
    public Object query(String logisticsNo, Long orderId) {
        Map<String, Object> map = new HashMap();
        OrderDelivery orderDelivery = new OrderDelivery();
        orderDelivery.setLogisticsNo(logisticsNo);
        orderDelivery.setOrderId(orderId);
        OrderDelivery dto = orderDeliveryMapper.selectOne(orderDelivery);
        String result = null;
        if (dto != null) {
            try {
                OrderSub orderSub = orderSubMapper.selectByPrimaryKey(dto.getOrderId());
                //查询物流信息
                result = KdniaoTrackQueryUtil.getOrderTracesByJson(dto.getLogisticsCompanyCode(), dto.getLogisticsNo());
                JSONObject parse = JSONObject.parseObject(result);
                JSONArray jsonArray = (JSONArray) parse.get("Traces");
                if (jsonArray.size() != 0) {
                    //物流信息
                    map.put("traces", jsonArray);
                } else {
                    //暂无轨迹信息
                    map.put("reason", parse.get("Reason"));
                }
                //子订单号
                map.put("orderSubId", dto.getOrderId());
                //主订单号
                map.put("orderMainId", orderSub.getMainOrderId());
                //物流公司
                map.put("logisticsCompany", dto.getLogisticsCompany());
                //物流单号
                map.put("logisticsNo", logisticsNo);
                //发货时间
                map.put("deliveryTime", DateUtil.transferDateToString(dto.getDeliveryTime(), "yyyy-MM-dd " +
                        "hh:mm:ss"));
                return map;
            } catch (Exception e) {
                e.printStackTrace();
                throw new YimaoException("查询物流信息失败！");
            }
        }
        log.error("发货记录不存在");
        throw new YimaoException("查询不到您的物流信息，请确认是否发货！");
    }


    /**
     * 分页查询发货信息
     *
     * @param deliveryDTO
     * @return
     */
    @Override
    public PageVO<DeliveryInfoDTO> list(DeliveryDTO deliveryDTO, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        Page<DeliveryInfoDTO> page = orderDeliveryMapper.pageQueryList(deliveryDTO);
        return new PageVO<>(pageNum, page);
    }


    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
    @Override
    public void setDelivery(Long id) {
        OrderSub orderSub = orderSubMapper.selectByPrimaryKey(id);
        if(null == orderSub){
            log.info("=========订单号：" + id + "==========");
            throw new YimaoException("该订单不存在");
        }

        if (orderSub.getStatus() == OrderStatusEnum.TO_BE_OUT_OF_STOCK.value) {
            throw new YimaoException("订单已经发货");
        }
        orderSub.setStatus(OrderStatusEnum.TO_BE_OUT_OF_STOCK.value);
        //设置订单发货时间
        orderSub.setDeliveryTime(new Date());
        orderSub.setUpdateTime(new Date());
        int i = orderSubMapper.updateByPrimaryKeySelective(orderSub);
        if (i > 0) {
            log.info("*******************订单号：" + id + ",发货成功" + "*******************");
        } else {
            throw new YimaoException("订单状态更新失败，设置发货失败");
        }

        DeliveryAddress address = new DeliveryAddress();
        address.setOperatorId(userCache.getUserId());
        address.setHasDelivery(1);
        DeliveryAddress deliveryAddress = deliveryAddressMapper.selectOne(address);
        if (null == deliveryAddress) {
            throw new NotFoundException("操作人没有设置默认发货地址");
        }

        DeliveryAddressDTO deliveryAddressDTO = new DeliveryAddressDTO();
        deliveryAddress.convert(deliveryAddressDTO);

        Example example = new Example(OrderDelivery.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("orderId",id);
        List<OrderDelivery> orderDeliveries = orderDeliveryMapper.selectByExample(example);
        if(CollectionUtil.isEmpty(orderDeliveries)){
            OrderDelivery orderDelivery = new OrderDelivery();
            orderDelivery.setOrderId(orderSub.getId());
            orderDelivery.setDeliveryTime(new Date());
            orderDelivery.setSendingCompany(deliveryAddressDTO.getCompany());
            orderDelivery.setSender(deliveryAddressDTO.getContact());
            orderDelivery.setSenderProvince(deliveryAddressDTO.getProvince());
            orderDelivery.setSenderCity(deliveryAddressDTO.getCity());
            orderDelivery.setSenderRegion(deliveryAddressDTO.getRegion());
            orderDelivery.setSenderStreet(deliveryAddressDTO.getStreet());
            orderDelivery.setDeliveryPhone(deliveryAddressDTO.getMobile());
            orderDelivery.setCreateTime(new Date());
            orderDeliveryMapper.insertSelective(orderDelivery);
        }
    }

    private void setDeliveryRecordPartInfo(SubOrderDetail subOrderDetail, OrderDeliveryRecord deliveryRecord,
                                           DeliveryAddressDTO dto) {
        if (null != subOrderDetail) {
            deliveryRecord.setProductOneCategoryId(subOrderDetail.getProductFirstCategoryId());
            deliveryRecord.setProductOneCategoryId(subOrderDetail.getProductFirstCategoryId());
            deliveryRecord.setProductOneCategoryName(subOrderDetail.getProductFirstCategoryName());
            deliveryRecord.setProductTwoCategoryId(subOrderDetail.getProductTwoCategoryId());
            deliveryRecord.setProductTwoCategoryName(subOrderDetail.getProductTwoCategoryName());
            deliveryRecord.setProductCategoryId(subOrderDetail.getProductCategoryId());
            deliveryRecord.setProductCategoryName(subOrderDetail.getProductCategoryName());
            deliveryRecord.setProductImg(subOrderDetail.getProductImg());
            deliveryRecord.setUserId(subOrderDetail.getUserId());
            deliveryRecord.setAddressProvince(subOrderDetail.getAddresseeProvince());
            deliveryRecord.setAddressCity(subOrderDetail.getAddresseeCity());
            deliveryRecord.setAddressRegion(subOrderDetail.getAddresseeRegion());
            deliveryRecord.setAddressStreet(subOrderDetail.getAddresseeStreet());
            deliveryRecord.setAddressPhone(subOrderDetail.getAddresseePhone());
            deliveryRecord.setAddressName(subOrderDetail.getAddresseeName());
            deliveryRecord.setStationId(subOrderDetail.getStationId());
            deliveryRecord.setStationName(subOrderDetail.getStationName());
        }

        deliveryRecord.setSendingCompany(dto.getCompany());
        deliveryRecord.setSender(dto.getContact());
        deliveryRecord.setSenderProvince(dto.getProvince());
        deliveryRecord.setSenderCity(dto.getCity());
        deliveryRecord.setSenderRegion(dto.getRegion());
        deliveryRecord.setSenderStreet(dto.getStreet());
        deliveryRecord.setSenderPhone(dto.getMobile());
        deliveryRecord.setSetShipper(userCache.getCurrentAdminRealName());
        deliveryRecord.setDeliveryPeople(userCache.getCurrentAdminRealName());
        deliveryRecord.setSetShipTime(new Date());
        deliveryRecord.setCreateTime(new Date());

    }


    @Override
    public void setBatchDelivery(List<Long> ids) {
        for (Long id : ids) {
            setDelivery(id);
        }
    }


    @Override
    public PageVO<OrderDeliveryRecordDTO> deliveryRecordList(Integer pageNum, Integer pageSize, String orderId,
                                                             String logisticsNo, String startTime, String endTime,
                                                             Integer userId, String addreessName, Integer terminal) {
        PageHelper.startPage(pageNum, pageSize);
        Page<OrderDeliveryRecordDTO> page = orderDeveryRecordMapper.deliveryRecordList(orderId, logisticsNo,
                startTime, endTime, userId, addreessName, terminal);
        return new PageVO<>(pageNum, page);
    }


    @Override
    public Page<Object> deliveryExport(Integer exportType, DeliveryConditionDTO dto) {
        dto.setExportType(exportType);
        Page<Object> list = null;
        if (exportType == 1) {
            //发货台账登记列表
            list = orderDeliveryMapper.deliveryLedgerExport(dto);
        } else if (exportType == 2) {
            //订单发货信息列表
            list = orderDeliveryMapper.deliveryInfoExport(dto);
        } else if (exportType == 3) {
            //快递信息表
            list = orderDeliveryMapper.queryDeliveryList(dto);
        }
        return list;
    }

    @Override
    public DeliveryDetailInfoDTO findDeliveryDetail(Integer id) {

        DeliveryDetailInfoDTO detailInfoDTO = new DeliveryDetailInfoDTO();
        OrderDeliveryRecord deliveryRecord = orderDeveryRecordMapper.selectByPrimaryKey(id);
        OrderSub orderSub = null;
        OrderDelivery orderDelivery = null;
        if (null != deliveryRecord.getOrderId()) {
            orderSub = orderSubMapper.selectByPrimaryKey(deliveryRecord.getOrderId());
            Example example = new Example(OrderDelivery.class);
            Example.Criteria criteria = example.createCriteria();
            criteria.andEqualTo("orderId", deliveryRecord.getOrderId());
            List<OrderDelivery> orderDeliveryList = orderDeliveryMapper.selectByExample(example);
            if (CollectionUtil.isNotEmpty(orderDeliveryList)) {
                orderDelivery = orderDeliveryList.get(0);
            }
        }
        if (null != deliveryRecord) {
            detailInfoDTO.setMainOrderId(deliveryRecord.getMainOrderId());
            detailInfoDTO.setOrderId(deliveryRecord.getOrderId());
            detailInfoDTO.setProductImg(deliveryRecord.getProductImg());
            detailInfoDTO.setProductOneCategoryName(deliveryRecord.getProductOneCategoryName());
            detailInfoDTO.setProductTwoCategoryName(deliveryRecord.getProductTwoCategoryName());
            detailInfoDTO.setProductCategoryName(deliveryRecord.getProductCategoryName());

            if (null != orderSub) {
                detailInfoDTO.setNum(orderSub.getCount());
                //设置发货时间
                detailInfoDTO.setSetShipTime(orderSub.getDeliveryTime());
            }

            detailInfoDTO.setTerminal(deliveryRecord.getTerminal());
            detailInfoDTO.setUserId(deliveryRecord.getUserId());
            detailInfoDTO.setStationName(deliveryRecord.getStationName());

            detailInfoDTO.setAddressName(deliveryRecord.getAddressName());
            detailInfoDTO.setAddresseePhone(deliveryRecord.getAddressPhone());
            detailInfoDTO.setAddresseeProvince(deliveryRecord.getAddressProvince());
            detailInfoDTO.setAddresseeCity(deliveryRecord.getAddressCity());
            detailInfoDTO.setAddresseeRegion(deliveryRecord.getAddressRegion());
            detailInfoDTO.setAddresseeStreet(deliveryRecord.getAddressStreet());
            detailInfoDTO.setCurrentUser(deliveryRecord.getSetShipper());
            //出库时间
            detailInfoDTO.setDeliveryTime(deliveryRecord.getCreateTime());
            detailInfoDTO.setDeliveryPeople(deliveryRecord.getDeliveryPeople());

            detailInfoDTO.setLogisticsNo(deliveryRecord.getLogisticsNo());
            detailInfoDTO.setDeliveryNum(deliveryRecord.getNum());
            detailInfoDTO.setBoxNum(deliveryRecord.getBoxNum());

            detailInfoDTO.setContact(deliveryRecord.getSender());
            detailInfoDTO.setProvince(deliveryRecord.getSenderProvince());
            detailInfoDTO.setCity(deliveryRecord.getSenderCity());
            detailInfoDTO.setRegion(deliveryRecord.getSenderRegion());
            detailInfoDTO.setStreet(deliveryRecord.getSenderStreet());

            if (null != orderDelivery) {
                detailInfoDTO.setLogisticsCompany(orderDelivery.getLogisticsCompany());
                detailInfoDTO.setWeigh(orderDelivery.getWeigh());
                detailInfoDTO.setPayType(orderDelivery.getPayType());
                detailInfoDTO.setLogisticsFee(orderDelivery.getLogisticsFee());
                detailInfoDTO.setRemark(orderDelivery.getRemark());
            }

        }
        return detailInfoDTO;
    }

    /*@Override
    public List<DeliveryInfoExportDTO> queryDeliveryRecordList(DeliveryConditionDTO dto) {
        List<DeliveryInfoExportDTO> list = null;
        if (dto.getExportType() == 1) {
            //发货台账登记表
            list = orderDeliveryMapper.deliveryRecordLedgerExport(dto);
        } else if (dto.getExportType() == 2) {
            //订单发货信息表
            list = orderDeliveryMapper.deliveryRecordInfoExport(dto);
        }
        return list;
    }*/
}


