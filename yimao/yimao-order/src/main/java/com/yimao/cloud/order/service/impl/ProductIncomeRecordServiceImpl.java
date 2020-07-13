package com.yimao.cloud.order.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.yimao.cloud.base.cache.UserCache;
import com.yimao.cloud.base.enums.*;
import com.yimao.cloud.base.exception.BadRequestException;
import com.yimao.cloud.base.exception.YimaoException;
import com.yimao.cloud.base.mail.MailSender;
import com.yimao.cloud.base.properties.DomainProperties;
import com.yimao.cloud.base.utils.CollectionUtil;
import com.yimao.cloud.base.utils.CopyUtil;
import com.yimao.cloud.base.utils.DateUtil;
import com.yimao.cloud.base.utils.StringUtil;
import com.yimao.cloud.order.dto.DistributorIncomeDTO;
import com.yimao.cloud.order.feign.ProductFeign;
import com.yimao.cloud.order.feign.SystemFeign;
import com.yimao.cloud.order.feign.UserFeign;
import com.yimao.cloud.order.mapper.IncomeSubjectMapper;
import com.yimao.cloud.order.mapper.ProductIncomeRecordMapper;
import com.yimao.cloud.order.mapper.ProductIncomeRecordPartMapper;
import com.yimao.cloud.order.mapper.SubOrderDetailMapper;
import com.yimao.cloud.order.mapper.WithdrawSubMapper;
import com.yimao.cloud.order.po.IncomeSubject;
import com.yimao.cloud.order.po.OrderSub;
import com.yimao.cloud.order.po.ProductIncomeRecord;
import com.yimao.cloud.order.po.ProductIncomeRecordPart;
import com.yimao.cloud.order.po.SubOrderDetail;
import com.yimao.cloud.order.po.WorkOrder;
import com.yimao.cloud.order.service.IncomeRuleService;
import com.yimao.cloud.order.service.OrderSubService;
import com.yimao.cloud.order.service.ProductIncomeRecordService;
import com.yimao.cloud.order.service.ProductIncomeRuleService;
import com.yimao.cloud.pojo.dto.order.IncomeExportDTO;
import com.yimao.cloud.pojo.dto.order.IncomeRecordPartResultDTO;
import com.yimao.cloud.pojo.dto.order.IncomeRecordResultDTO;
import com.yimao.cloud.pojo.dto.order.IncomeRuleDTO;
import com.yimao.cloud.pojo.dto.order.IncomeRulePartDTO;
import com.yimao.cloud.pojo.dto.order.IncomeStatsQueryDTO;
import com.yimao.cloud.pojo.dto.order.IncomeStatsResultDTO;
import com.yimao.cloud.pojo.dto.order.OrderSubDTO;
import com.yimao.cloud.pojo.dto.order.ProductIncomeQueryDTO;
import com.yimao.cloud.pojo.dto.order.ProductIncomeRecordDTO;
import com.yimao.cloud.pojo.dto.order.ProductIncomeRecordPartDTO;
import com.yimao.cloud.pojo.dto.order.SalesProductDTO;
import com.yimao.cloud.pojo.dto.order.UserIncomeDTO;
import com.yimao.cloud.pojo.dto.product.ProductCategoryDTO;
import com.yimao.cloud.pojo.dto.product.ProductCompanyDTO;
import com.yimao.cloud.pojo.dto.product.ProductCostDTO;
import com.yimao.cloud.pojo.dto.product.ProductDTO;
import com.yimao.cloud.pojo.dto.system.StationCompanyDTO;
import com.yimao.cloud.pojo.dto.system.StationDTO;
import com.yimao.cloud.pojo.dto.user.DistributorDTO;
import com.yimao.cloud.pojo.dto.user.UserDTO;
import com.yimao.cloud.pojo.vo.PageVO;
import com.yimao.cloud.pojo.vo.order.ProductIncomeGrandTotalVO;
import com.yimao.cloud.pojo.vo.order.ProductIncomeVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * 产品（包涵产品订单和续费）相关收益结算
 *
 * @author Liu Yi
 * @date 2019/1/21.
 */
@Service
@Slf4j
public class ProductIncomeRecordServiceImpl implements ProductIncomeRecordService {

    @Resource
    private DomainProperties domainProperties;
    @Resource
    private MailSender mailSender;
    @Resource
    private UserCache userCache;
    @Resource
    private OrderSubService orderSubService;
    @Resource
    private IncomeRuleService incomeRuleService;
    @Resource
    private ProductIncomeRuleService productIncomeRuleService;
    @Resource
    private ProductIncomeRecordMapper productIncomeRecordMapper;
    @Resource
    private ProductIncomeRecordPartMapper productIncomeRecordPartMapper;
    @Resource
    private IncomeSubjectMapper incomeSubjectMapper;
    @Resource
    private WithdrawSubMapper withdrawSubMapper;
    @Resource
    private SubOrderDetailMapper subOrderDetailMapper;

    @Resource
    private SystemFeign systemFeign;
    @Resource
    private UserFeign userFeign;
    @Resource
    private ProductFeign productFeign;

    /**
     * 产品收益分配
     *
     * @param orderId 订单id
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
    public void allotSellIncome(Long orderId) {
        try {
            log.info("订单号：{} 开始分配收益", orderId);
            //1-先获取子订单
            OrderSubDTO subOrder = orderSubService.findFullOrderById(orderId);
            //2-获取收益分配规则
            Set<Integer> incomeRuleIds = productIncomeRuleService.listIncomeRuleIdByProductId(subOrder.getProductId());
            IncomeRuleDTO incomeRule = incomeRuleService.getIncomeRuleByIdAndIncomeType(incomeRuleIds, IncomeType.PRODUCT_INCOME.value);
            if (incomeRule == null) {
                log.error("产品销售收益分配失败-没有查询到产品的收益分配规则。");
                return;
            }

            boolean exists = productIncomeRecordMapper.existsWithOrderId(String.valueOf(orderId), IncomeType.PRODUCT_INCOME.value);
            if (exists) {
                log.info("产品销售收益分配结束-已分配过收益了。orderId={}", orderId);
                return;
            }

            //生成收益结算信息
            ProductIncomeRecord record = new ProductIncomeRecord();
            record.setMainOrderId(subOrder.getMainOrderId());
            record.setOrderId(String.valueOf(orderId));
            record.setOrderFee(subOrder.getFee());

            record.setProductId(subOrder.getProductId());
            record.setProductName(subOrder.getProductName());
            record.setProductName(subOrder.getProductName());
            record.setProductCompanyId(subOrder.getProductCompanyId());
            record.setProductCompanyName(subOrder.getProductCompanyName());
            record.setProductCategoryId(subOrder.getProductCategoryId());
            record.setProductCategoryName(subOrder.getProductCategoryName());
            //
            record.setProductOneCategoryId(subOrder.getProductOneCategoryId());
            record.setProductOneCategoryName(subOrder.getProductOneCategoryName());
            record.setProductTwoCategoryId(subOrder.getProductTwoCategoryId());
            record.setProductTwoCategoryName(subOrder.getProductTwoCategoryName());

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
            BigDecimal balanceFee = subOrder.getFee();
            if (balanceFee == null) {
                balanceFee = new BigDecimal(0);
            }
            BigDecimal openAccountFee = subOrder.getOpenAccountFee();
            if (openAccountFee == null) {
                openAccountFee = new BigDecimal(0);
            }
            //可分配收益金额=订单金额-开户费用-运费
            balanceFee = balanceFee.subtract(openAccountFee);
            record.setSettlementFee(balanceFee);//可分配金额
            record.setSettlementAmount(subOrder.getCount() == null ? 1 : subOrder.getCount());//可结算产品数量

            //收益结算状态：0-不可结算（订单未完成）；1-可结算（订单已完成）；2-已结算；3-已失效；
            if (OrderStatusEnum.SUCCESSFUL_TRADE.value == subOrder.getStatus()) {
                record.setStatus(OrderCompleteStatusEnum.UNCOMPLETED.value);
                record.setOrderCompleteTime(subOrder.getCompleteTime());
                //设置结算时间
                String settlementMonth = DateUtil.transferDateToString(DateUtil.monthAfter(subOrder.getCompleteTime(), 1), "yyyy-MM");
                if (Objects.nonNull(subOrder.getDistributorId())) {
                    DistributorDTO distributor = userFeign.getDistributorById(subOrder.getDistributorId());
                    if (Objects.isNull(distributor) || Objects.isNull(distributor.getRoleLevel())) {
                        log.error("产品销售收益分配失败-经销商信息有误。");
                        throw new YimaoException("产品销售收益分配失败-经销商信息有误");
                    }
                    //以下经销商身份可以在工单完成的时候进行收益可结算状态设置，其它身份的经销商收益暂不可结算
                    if (distributor.getRoleLevel() == DistributorRoleLevel.D_350.value
                            || distributor.getRoleLevel() == DistributorRoleLevel.D_650.value
                            || distributor.getRoleLevel() == DistributorRoleLevel.D_950.value
                            || distributor.getRoleLevel() == DistributorRoleLevel.D_1000.value) {
                        record.setStatus(OrderCompleteStatusEnum.CAN_BE_SETTLED.value);
                        //结算月份设值
                        record.setSettlementMonth(settlementMonth);
                    }
                }
                //如果是租赁，设置安装工结算时间
                if (Objects.nonNull(subOrder.getProductType()) && Objects.equals(subOrder.getProductType(), ProductModeEnum.LEASE.value)) {
                    record.setEngineerSettlementMonth(settlementMonth);
                }
            } else {
                //标记为冻结收益，等对应的订单完成时再讲状态标记为可计算或可体现
                record.setStatus(OrderCompleteStatusEnum.UNCOMPLETED.value);
            }
            record.setCreateTime(new Date());

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
                recordPart.setSubjectProvince(subOrder.getDistributorProvince());
                recordPartList.add(recordPart);
            }

            //3-市级公司
            rulePart = rulePartMap.get(IncomeSubjectEnum.CITY_COMPANY.value);
            if (rulePart != null) {
                ProductIncomeRecordPart recordPart = this.createProductIncomeRecordPart(record, incomeRule, rulePart);
                //收益主体信息
                recordPart.setSubjectProvince(subOrder.getDistributorProvince());
                recordPart.setSubjectCity(subOrder.getDistributorCity());
                recordPartList.add(recordPart);
            }

            //4-产品公司
            rulePart = rulePartMap.get(IncomeSubjectEnum.PRODUCT_COMPANY.value);
            if (rulePart != null) {
                ProductIncomeRecordPart recordPart = this.createProductIncomeRecordPart(record, incomeRule, rulePart);
                //获取产品公司
                ProductCompanyDTO productCompany = productFeign.getProductCompanyById(subOrder.getProductCompanyId());
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

            //5-市级合伙人
            rulePart = rulePartMap.get(IncomeSubjectEnum.CITY_PARTNER.value);
            if (rulePart != null) {
                ProductIncomeRecordPart recordPart = this.createProductIncomeRecordPart(record, incomeRule, rulePart);
                //TODO
                recordPartList.add(recordPart);
            }

            //6-市级发起人
            rulePart = rulePartMap.get(IncomeSubjectEnum.CITY_SPONSOR.value);
            if (rulePart != null) {
                ProductIncomeRecordPart recordPart = this.createProductIncomeRecordPart(record, incomeRule, rulePart);
                //TODO
                recordPartList.add(recordPart);
            }

            //7-推荐人所在区县级公司
            rulePart = rulePartMap.get(IncomeSubjectEnum.RECOMMEND_STATION_COMPANY.value);
            if (rulePart != null) {
                ProductIncomeRecordPart recordPart = this.createProductIncomeRecordPart(record, incomeRule, rulePart);
                String province = subOrder.getRecommendProvince();
                String city = subOrder.getRecommendCity();
                String region = subOrder.getRecommendRegion();
                if (province != null && city != null && region != null) {
                    //根据推荐人省市区、售前类型查询其所属的区县级公司
                    StationCompanyDTO stationCompany = systemFeign.getStationCompanyByPCR(province, city, region,StationAreaServiceTypeEnum.PRE_SALE.value);
                    if (stationCompany != null) {
                        //收益主体为推荐人所属区县级公司
                        recordPart.setSubjectId(stationCompany.getId());
                        recordPart.setSubjectName(stationCompany.getName());
                        recordPart.setSubjectPhone(stationCompany.getContactPhone());
                        recordPart.setSubjectProvince(stationCompany.getProvince());
                        recordPart.setSubjectCity(stationCompany.getCity());
                        recordPart.setSubjectRegion(stationCompany.getRegion());
                        //结算主体为推荐人所属区县级公司
                        recordPart.setSettlementSubjectId(stationCompany.getId());
                        recordPart.setSettlementSubjectName(stationCompany.getName());
                    }
                }
                recordPartList.add(recordPart);
            }

            //8-智慧助理
            rulePart = rulePartMap.get(IncomeSubjectEnum.ASSISTANT.value);
            if (rulePart != null) {
                ProductIncomeRecordPart recordPart = this.createProductIncomeRecordPart(record, incomeRule, rulePart);
                //TODO 智慧助理本版本不做
                recordPart.setSubjectName("智慧助理");
                recordPartList.add(recordPart);
            }

            //9-经销商所在区县级公司
            rulePart = rulePartMap.get(IncomeSubjectEnum.DISTRIBUTOR_STATION_COMPANY.value);
            if (rulePart != null) {
                ProductIncomeRecordPart recordPart = this.createProductIncomeRecordPart(record, incomeRule, rulePart);
                String province = subOrder.getDistributorProvince();
                String city = subOrder.getDistributorCity();
                String region = subOrder.getDistributorRegion();
                //根据经销商所在省市区查询其所属的区县级公司
                StationCompanyDTO stationCompany = systemFeign.getStationCompanyByPCR(province, city, region,StationAreaServiceTypeEnum.PRE_SALE.value);
                if (stationCompany != null) {
                    //收益主体为经销商所属区县级公司
                    recordPart.setSubjectId(stationCompany.getId());
                    recordPart.setSubjectName(stationCompany.getName());
                    recordPart.setSubjectPhone(stationCompany.getContactPhone());
                    recordPart.setSubjectProvince(stationCompany.getProvince());
                    recordPart.setSubjectCity(stationCompany.getCity());
                    recordPart.setSubjectRegion(stationCompany.getRegion());
                    //结算主体为经销商所属区县级公司
                    recordPart.setSettlementSubjectId(stationCompany.getId());
                    recordPart.setSettlementSubjectName(stationCompany.getName());
                }
                recordPartList.add(recordPart);
            }

            //10-区县级发起人
            rulePart = rulePartMap.get(IncomeSubjectEnum.REGION_SPONSOR.value);
            if (rulePart != null) {
                ProductIncomeRecordPart recordPart = this.createProductIncomeRecordPart(record, incomeRule, rulePart);
                String province = subOrder.getDistributorProvince();
                String city = subOrder.getDistributorCity();
                String region = subOrder.getDistributorRegion();
                if (StringUtil.isNotEmpty(province)) {
                    DistributorDTO sponsor = userFeign.getOriginatorByAddress(province, city, region);
                    if (sponsor != null) {
                        //收益主体为区县级发起人
                        recordPart.setSubjectId(sponsor.getId());
                        recordPart.setSubjectName(sponsor.getRealName());
                        recordPart.setSubjectPhone(sponsor.getPhone());
                        recordPart.setSubjectIdCard(sponsor.getIdCard());
                        recordPart.setSubjectProvince(sponsor.getProvince());
                        recordPart.setSubjectCity(sponsor.getCity());
                        recordPart.setSubjectRegion(sponsor.getRegion());
                    }
                }
                if (StringUtil.isNotEmpty(province) && StringUtil.isNotEmpty(city) && StringUtil.isNotEmpty(region)) {
                    //根据发起人所在省市区查询其所属的区县级公司
                    StationCompanyDTO stationCompany = systemFeign.getStationCompanyByPCR(province, city, region,StationAreaServiceTypeEnum.PRE_SALE.value);
                    if (stationCompany != null) {
                        //结算主体为区县级发起人所属区县级公司
                        recordPart.setSettlementSubjectId(stationCompany.getId());
                        recordPart.setSettlementSubjectName(stationCompany.getName());
                    }
                }
                recordPartList.add(recordPart);
            }

            //11-服务站站长
            rulePart = rulePartMap.get(IncomeSubjectEnum.STATION_MASTER.value);
            if (rulePart != null) {
                ProductIncomeRecordPart recordPart = this.createProductIncomeRecordPart(record, incomeRule, rulePart);
                String province = subOrder.getDistributorProvince();
                String city = subOrder.getDistributorCity();
                String region = subOrder.getDistributorRegion();
                // 根据经销商获取服务站
                StationDTO station = systemFeign.getStationByPRC(province, city, region,StationAreaServiceTypeEnum.PRE_SALE.value);
                if (station != null && Objects.nonNull(station.getMasterDistributorId())) {
                    DistributorDTO masterDistributor = userFeign.getDistributorById(station.getMasterDistributorId());
                    //收益主体为服务站站长
                    recordPart.setSubjectId(station.getMasterDistributorId());
                    recordPart.setSubjectName(station.getMasterName());
                    recordPart.setSubjectPhone(station.getMasterPhone());
                    recordPart.setSubjectIdCard(masterDistributor.getIdCard());
                    recordPart.setSubjectProvince(station.getProvince());
                    recordPart.setSubjectCity(station.getCity());
                    recordPart.setSubjectRegion(station.getRegion());
                }
                //根据经销商所在省市区查询其所属的区县级公司
                StationCompanyDTO stationCompany = systemFeign.getStationCompanyByPCR(province, city, region,StationAreaServiceTypeEnum.PRE_SALE.value);
                if (stationCompany != null) {
                    //结算主体为服务站站长所属区县级公司
                    recordPart.setSettlementSubjectId(stationCompany.getId());
                    recordPart.setSettlementSubjectName(stationCompany.getName());
                }
                recordPartList.add(recordPart);
            }

            //12-区县级股东（推荐人）
            rulePart = rulePartMap.get(IncomeSubjectEnum.REGION_SHAREHOLDER.value);
            if (rulePart != null) {
                ProductIncomeRecordPart recordPart = this.createProductIncomeRecordPart(record, incomeRule, rulePart);
                DistributorDTO recommend = userFeign.getRecommendByDistributorId(subOrder.getDistributorId());
                if (recommend != null) {
                    //收益主体为推荐人所属区县级公司
                    recordPart.setSubjectId(recommend.getId());
                    recordPart.setSubjectIdCard(recommend.getIdCard());
                    recordPart.setSubjectName(recommend.getRealName());
                    recordPart.setSubjectPhone(recommend.getPhone());
                    recordPart.setSubjectProvince(recommend.getProvince());
                    recordPart.setSubjectCity(recommend.getCity());
                    recordPart.setSubjectRegion(recommend.getRegion());
                    String province = recommend.getProvince();
                    String city = recommend.getCity();
                    String region = recommend.getRegion();
                    //根据推荐人省市区查询其所属的区县级公司
                    StationCompanyDTO stationCompany = systemFeign.getStationCompanyByPCR(province, city, region,StationAreaServiceTypeEnum.PRE_SALE.value);
                    if (stationCompany != null) {
                        //结算主体为推荐人所属区县级公司
                        recordPart.setSettlementSubjectId(stationCompany.getId());
                        recordPart.setSettlementSubjectName(stationCompany.getName());
                    }
                }
                recordPartList.add(recordPart);
            }

            //13-分销用户（会员用户）
            BigDecimal saleUserIncome = new BigDecimal(0);
            rulePart = rulePartMap.get(IncomeSubjectEnum.DISTRIBUTOR_USER.value);
            if (rulePart != null) {
                ProductIncomeRecordPart recordPart = this.createProductIncomeRecordPart(record, incomeRule, rulePart);
                //获取分销用户
                Integer userId = subOrder.getUserId();
                UserDTO saleUser = userFeign.getMySaleUserById(userId);
                if (saleUser != null) {
                    //收益主体为分销用户
                    recordPart.setSubjectId(saleUser.getId());
                    recordPart.setSubjectName(StringUtil.isNotEmpty(saleUser.getRealName()) ? saleUser.getRealName() : saleUser.getNickName());
                    recordPart.setSubjectPhone(saleUser.getMobile());
                    recordPart.setSubjectIdCard(saleUser.getIdCard());
                    String province = subOrder.getDistributorProvince();
                    String city = subOrder.getDistributorCity();
                    String region = subOrder.getDistributorRegion();
                    // 分销用户省市区为空取经销商的省市区
                    //TODO 新版本是要把分销用户的收益结算到服务站？待定
                    recordPart.setSubjectProvince(province);
                    recordPart.setSubjectCity(city);
                    recordPart.setSubjectRegion(region);
                    // 暂定结算主体为分销用户
                    // 确认分销用户的结算主体为经销商的区县级公司
                    // 根据经销商所在省市区查询其所属的区县级公司
                    StationCompanyDTO stationCompany = systemFeign.getStationCompanyByPCR(province, city, region,StationAreaServiceTypeEnum.PRE_SALE.value);
                    if (stationCompany != null) {
                        //结算主体为服务站站长所属区县级公司
                        recordPart.setSettlementSubjectId(stationCompany.getId());
                        recordPart.setSettlementSubjectName(stationCompany.getName());
                    }
                    recordPartList.add(recordPart);

                    //下单和支付间隔过大，可能健康大使的身份已经变为会员用户，故在此判断订单详情中的会员用户信息是否需要更新
                    SubOrderDetail orderDetail = subOrderDetailMapper.selectByPrimaryKey(orderId);
                    if (orderDetail.getVipUserId() == null || !Objects.equals(saleUser.getId(), orderDetail.getVipUserId())) {
                        SubOrderDetail update = new SubOrderDetail();
                        update.setSubOrderId(orderId);
                        update.setVipUserId(saleUser.getId());
                        update.setVipUserName(saleUser.getRealName());
                        update.setVipUserPhone(saleUser.getMobile());
                        update.setVipUserType(saleUser.getUserType());
                        update.setVipUserTypeName(UserType.getNameByType(saleUser.getUserType()));
                        update.setVipUserHasIncome(true);
                        update.setUpdateTime(new Date());
                        subOrderDetailMapper.updateByPrimaryKeySelective(update);
                    }
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
                recordPart.setSubjectId(subOrder.getDistributorId());
                recordPart.setSubjectName(subOrder.getDistributorName());
                recordPart.setSubjectCode(subject.getIncomeSubjectCode());
                recordPart.setSubjectPhone(subOrder.getDistributorPhone());
                DistributorDTO distributor = userFeign.getDistributorById(subOrder.getDistributorId());
                if (distributor != null) {
                    recordPart.setSubjectIdCard(distributor.getIdCard());
                }
                recordPart.setSubjectProvince(subOrder.getDistributorProvince());
                recordPart.setSubjectCity(subOrder.getDistributorCity());
                recordPart.setSubjectRegion(subOrder.getDistributorRegion());
                //结算主体（先设置后面会覆盖）
                recordPart.setSettlementSubjectCode(subject.getSettlementSubjectCode());
                recordPart.setSettlementSubjectName(subject.getSettlementSubjectName());
                recordPart.setSettlementTime(new Date());
                recordPart.setHasWithdraw(0);
                recordPart.setCreateTime(new Date());

                String province = subOrder.getDistributorProvince();
                String city = subOrder.getDistributorCity();
                String region = subOrder.getDistributorRegion();
                //根据经销商所在省市区查询其所属的区县级公司
                StationCompanyDTO stationCompany = systemFeign.getStationCompanyByPCR(province, city, region,StationAreaServiceTypeEnum.PRE_SALE.value);
                if (stationCompany != null) {
                    //结算主体为服务站站长所属区县级公司
                    recordPart.setSettlementSubjectId(stationCompany.getId());
                    recordPart.setSettlementSubjectName(stationCompany.getName());
                }
                recordPartList.add(recordPart);
            }


            //15-服务站承包人（产品收益）
            rulePart = rulePartMap.get(IncomeSubjectEnum.STATION_CONTRACTOR_PRODUCT.value);
            if (rulePart != null) {
                ProductIncomeRecordPart recordPart = this.createProductIncomeRecordPart(record, incomeRule, rulePart);
                Date balanceTime = recordPart.getSettlementTime();
                String province = subOrder.getDistributorProvince();
                String city = subOrder.getDistributorCity();
                String region = subOrder.getDistributorRegion();
                StationDTO station = systemFeign.getStationByPRC(province, city, region,PermissionTypeEnum.PRE_SALE.value);
                boolean canBalanceToStation = station != null &&
                        station.getContract() != null &&
                        station.getContract() &&
                        station.getContractStartTime() != null &&
                        station.getContractStartTime().before(balanceTime) &&
                        station.getContractEndTime() != null &&
                        station.getContractEndTime().after(balanceTime);
                if (canBalanceToStation) {
                    //收益主体为服务站承包人
                    recordPart.setSubjectId(station.getId());
                    recordPart.setSubjectName(station.getContractor());//服务站承包人姓名
                    recordPart.setSubjectPhone(station.getContractorPhone());
                    recordPart.setSubjectIdCard(station.getContractorIdCard());
                    recordPart.setSubjectProvince(station.getProvince());
                    recordPart.setSubjectCity(station.getCity());
                    recordPart.setSubjectRegion(station.getRegion());
                }
                //根据经销商所在省市区查询其所属的区县级公司
                StationCompanyDTO stationCompany = systemFeign.getStationCompanyByPCR(province, city, region,StationAreaServiceTypeEnum.PRE_SALE.value);
                if (stationCompany != null) {
                    //结算主体为服务站站长所属区县级公司
                    recordPart.setSettlementSubjectId(stationCompany.getId());
                    recordPart.setSettlementSubjectName(stationCompany.getName());
                }
                recordPartList.add(recordPart);
            }

            // //16-服务站承包人（服务收益）
            // rulePart = rulePartMap.get(IncomeSubjectEnum.STATION_CONTRACTOR_SERVICE.value);
            // if (rulePart != null) {
            //     ProductIncomeRecordPart recordPart = this.createProductIncomeRecordPart(record, incomeRule, rulePart);
            //     recordPartList.add(recordPart);
            // }

            //17-安装工程师所在区县级公司
            rulePart = rulePartMap.get(IncomeSubjectEnum.ENGINEER_STATION_COMPANY.value);
            if (rulePart != null) {
                ProductIncomeRecordPart recordPart = this.createProductIncomeRecordPart(record, incomeRule, rulePart);
                String province = subOrder.getEngineerProvince();
                String city = subOrder.getEngineerCity();
                String region = subOrder.getEngineerRegion();
                //根据安装工程师所在省市区查询其所属的区县级公司
                StationCompanyDTO stationCompany = systemFeign.getStationCompanyByPCR(province, city, region,StationAreaServiceTypeEnum.PRE_SALE.value);
                if (stationCompany != null) {
                    //收益主体为安装工程师所属区县级公司
                    recordPart.setSubjectId(stationCompany.getId());
                    recordPart.setSubjectName(stationCompany.getName());
                    recordPart.setSubjectPhone(stationCompany.getContactPhone());
                    //TODO 是否需要
                    //recordPart.setSubjectIdCard(stationCompany.getContactIdCard());
                    recordPart.setSubjectProvince(stationCompany.getProvince());
                    recordPart.setSubjectCity(stationCompany.getCity());
                    recordPart.setSubjectRegion(stationCompany.getRegion());
                    //结算主体为安装工程师所属区县级公司
                    recordPart.setSettlementSubjectId(stationCompany.getId());
                    recordPart.setSettlementSubjectName(stationCompany.getName());
                }
                recordPartList.add(recordPart);
            }

            //18-安装工程师
            rulePart = rulePartMap.get(IncomeSubjectEnum.ENGINEER.value);
            if (rulePart != null) {
                ProductIncomeRecordPart recordPart = this.createProductIncomeRecordPart(record, incomeRule, rulePart);
                String province = subOrder.getEngineerProvince();
                String city = subOrder.getEngineerCity();
                String region = subOrder.getEngineerRegion();
                //收益主体为安装工程师
                recordPart.setSubjectId(subOrder.getEngineerId());
                recordPart.setSubjectName(subOrder.getEngineerName());
                recordPart.setSubjectPhone(subOrder.getEngineerPhone());
                recordPart.setSubjectIdCard(subOrder.getEngineerIdCard());

                recordPart.setSubjectProvince(subOrder.getEngineerProvince());
                recordPart.setSubjectCity(subOrder.getEngineerCity());
                recordPart.setSubjectRegion(subOrder.getEngineerRegion());
                //根据安装工程师省市区查询其所属的区县级公司
                StationCompanyDTO stationCompany = systemFeign.getStationCompanyByPCR(province, city, region,StationAreaServiceTypeEnum.PRE_SALE.value);
                if (stationCompany != null) {
                    //结算主体为安装工程师所属区县级公司
                    recordPart.setSettlementSubjectId(stationCompany.getId());
                    recordPart.setSettlementSubjectName(stationCompany.getName());
                }
                recordPartList.add(recordPart);
            }

            // todo 分配有问题 先让他往下走
            if (CollectionUtil.isNotEmpty(recordPartList)) {
                productIncomeRecordPartMapper.batchInsert(recordPartList);
            }

            /* 收益结算时，根据对应的收益结算模板进行结算，若遇以下情况，需特殊处理： 1.若没有分销商，则分销商所占收益归经销商 2.若服务站未承包，则“服务站承包人”所占收益归产品公司*/
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw, true));
            String subject = "收益分配失败提醒" + domainProperties.getApi();
            String content = "收益分配时出错。orderId=" + orderId + "\n" + sw.toString();
            mailSender.send(null, subject, content);
            throw new YimaoException(e.getMessage(), e);
        }
    }


    // /**
    //  * 分配收益明细
    //  * 根据订单号和收益记录分配收益明细
    //  *
    //  * @param subOrder   订单
    //  * @param incomeRule 收益规则
    //  * @param record     产品收益记录
    //  * @author liuhao
    //  */
    // @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    // @Override
    // public void allotSellIncomePart(OrderSubDTO subOrder, IncomeRuleDTO incomeRule, ProductIncomeRecord record) {
    //
    //     log.info("订单号：{1} 开始分配收益明细", subOrder.getId());
    //     Long orderId = subOrder.getId();
    //     try {
    //         //获取收益分配规则详细
    //         List<IncomeRulePartDTO> rulePartList = incomeRule.getIncomeRuleParts();
    //         Map<String, IncomeRulePartDTO> rulePartMap = new HashMap<>();
    //         for (IncomeRulePartDTO rulePart : rulePartList) {
    //             rulePartMap.put(rulePart.getSubjectCode(), rulePart);
    //         }
    //         List<ProductIncomeRecordPart> recordPartList = new ArrayList<>();
    //         //1-翼猫总部
    //         IncomeRulePartDTO rulePart = rulePartMap.get(IncomeSubjectEnum.MAIN_COMPANY.value);
    //         if (rulePart != null) {
    //             ProductIncomeRecordPart recordPart = this.createProductIncomeRecordPart(record, incomeRule, rulePart);
    //             //收益主体信息
    //             recordPart.setSubjectPhone("4001519999");
    //             recordPart.setSubjectProvince("上海市");
    //             recordPart.setSubjectCity("上海市");
    //             recordPart.setSubjectRegion("嘉定区");
    //             recordPartList.add(recordPart);
    //         }
    //
    //         //2-省级公司
    //         rulePart = rulePartMap.get(IncomeSubjectEnum.PROVINCE_COMPANY.value);
    //         if (rulePart != null) {
    //             ProductIncomeRecordPart recordPart = this.createProductIncomeRecordPart(record, incomeRule, rulePart);
    //             //收益主体信息
    //             recordPart.setSubjectProvince(subOrder.getDistributorProvince());
    //             recordPartList.add(recordPart);
    //         }
    //
    //         //3-市级公司
    //         rulePart = rulePartMap.get(IncomeSubjectEnum.CITY_COMPANY.value);
    //         if (rulePart != null) {
    //             ProductIncomeRecordPart recordPart = this.createProductIncomeRecordPart(record, incomeRule, rulePart);
    //             //收益主体信息
    //             recordPart.setSubjectProvince(subOrder.getDistributorProvince());
    //             recordPart.setSubjectCity(subOrder.getDistributorCity());
    //             recordPartList.add(recordPart);
    //         }
    //
    //         //4-产品公司
    //         rulePart = rulePartMap.get(IncomeSubjectEnum.PRODUCT_COMPANY.value);
    //         if (rulePart != null) {
    //             ProductIncomeRecordPart recordPart = this.createProductIncomeRecordPart(record, incomeRule, rulePart);
    //             //获取产品公司
    //             ProductCompanyDTO productCompany = productFeign.getProductCompanyById(subOrder.getProductCompanyId());
    //             if (productCompany != null) {
    //                 //收益主体为产品公司
    //                 recordPart.setSubjectId(productCompany.getId());
    //                 recordPart.setSubjectName(productCompany.getName());
    //                 //结算主体为产品公司
    //                 recordPart.setSettlementSubjectId(productCompany.getId());
    //                 recordPart.setSettlementSubjectName(productCompany.getName());
    //             }
    //             recordPartList.add(recordPart);
    //         }
    //
    //         //5-市级合伙人
    //         rulePart = rulePartMap.get(IncomeSubjectEnum.CITY_PARTNER.value);
    //         if (rulePart != null) {
    //             ProductIncomeRecordPart recordPart = this.createProductIncomeRecordPart(record, incomeRule, rulePart);
    //             recordPartList.add(recordPart);
    //         }
    //
    //         //6-市级发起人
    //         rulePart = rulePartMap.get(IncomeSubjectEnum.CITY_SPONSOR.value);
    //         if (rulePart != null) {
    //             ProductIncomeRecordPart recordPart = this.createProductIncomeRecordPart(record, incomeRule, rulePart);
    //             recordPartList.add(recordPart);
    //         }
    //
    //         //7-推荐人所在区县级公司
    //         rulePart = rulePartMap.get(IncomeSubjectEnum.RECOMMEND_STATION_COMPANY.value);
    //         if (rulePart != null) {
    //             ProductIncomeRecordPart recordPart = this.createProductIncomeRecordPart(record, incomeRule, rulePart);
    //             String province = subOrder.getRecommendProvince();
    //             String city = subOrder.getRecommendCity();
    //             String region = subOrder.getRecommendRegion();
    //             if (province != null && city != null && region != null) {
    //                 //根据推荐人省市区查询其所属的区县级公司
    //                 StationCompanyDTO stationCompany = systemFeign.getStationCompanyByPCR(province, city, region);
    //                 if (stationCompany != null) {
    //                     //收益主体为推荐人所属区县级公司
    //                     recordPart.setSubjectId(stationCompany.getId());
    //                     recordPart.setSubjectName(stationCompany.getName());
    //                     recordPart.setSubjectPhone(stationCompany.getContactPhone());
    //                     recordPart.setSubjectProvince(stationCompany.getProvince());
    //                     recordPart.setSubjectCity(stationCompany.getCity());
    //                     recordPart.setSubjectRegion(stationCompany.getRegion());
    //                     //结算主体为推荐人所属区县级公司
    //                     recordPart.setSettlementSubjectId(stationCompany.getId());
    //                     recordPart.setSettlementSubjectName(stationCompany.getName());
    //                 }
    //             }
    //             recordPartList.add(recordPart);
    //         }
    //
    //         //8-智慧助理
    //         rulePart = rulePartMap.get(IncomeSubjectEnum.ASSISTANT.value);
    //         if (rulePart != null) {
    //             ProductIncomeRecordPart recordPart = this.createProductIncomeRecordPart(record, incomeRule, rulePart);
    //             //TODO 智慧助理本版本不做
    //             recordPart.setSubjectName("智慧助理");
    //             recordPartList.add(recordPart);
    //         }
    //
    //         //9-经销商所在区县级公司
    //         rulePart = rulePartMap.get(IncomeSubjectEnum.DISTRIBUTOR_STATION_COMPANY.value);
    //         if (rulePart != null) {
    //             ProductIncomeRecordPart recordPart = this.createProductIncomeRecordPart(record, incomeRule, rulePart);
    //             String province = subOrder.getDistributorProvince();
    //             String city = subOrder.getDistributorCity();
    //             String region = subOrder.getDistributorRegion();
    //             //根据经销商所在省市区查询其所属的区县级公司
    //             StationCompanyDTO stationCompany = systemFeign.getStationCompanyByPCR(province, city, region);
    //             if (stationCompany != null) {
    //                 //收益主体为经销商所属区县级公司
    //                 recordPart.setSubjectId(stationCompany.getId());
    //                 recordPart.setSubjectName(stationCompany.getName());
    //                 recordPart.setSubjectPhone(stationCompany.getContactPhone());
    //                 recordPart.setSubjectProvince(stationCompany.getProvince());
    //                 recordPart.setSubjectCity(stationCompany.getCity());
    //                 recordPart.setSubjectRegion(stationCompany.getRegion());
    //                 //结算主体为经销商所属区县级公司
    //                 recordPart.setSettlementSubjectId(stationCompany.getId());
    //                 recordPart.setSettlementSubjectName(stationCompany.getName());
    //             }
    //             recordPartList.add(recordPart);
    //         }
    //
    //         //10-区县级发起人
    //         rulePart = rulePartMap.get(IncomeSubjectEnum.REGION_SPONSOR.value);
    //         if (rulePart != null) {
    //             ProductIncomeRecordPart recordPart = this.createProductIncomeRecordPart(record, incomeRule, rulePart);
    //             String province = subOrder.getDistributorProvince();
    //             String city = subOrder.getDistributorCity();
    //             String region = subOrder.getDistributorRegion();
    //             if (StringUtil.isNotEmpty(province)) {
    //                 DistributorDTO sponsor = userFeign.getOriginatorByAddress(province, city, region);
    //                 if (sponsor != null) {
    //                     //收益主体为区县级发起人
    //                     recordPart.setSubjectId(sponsor.getId());
    //                     recordPart.setSubjectName(sponsor.getRealName());
    //                     recordPart.setSubjectPhone(sponsor.getPhone());
    //                     recordPart.setSubjectIdCard(sponsor.getIdCard());
    //                     recordPart.setSubjectProvince(sponsor.getProvince());
    //                     recordPart.setSubjectCity(sponsor.getCity());
    //                     recordPart.setSubjectRegion(sponsor.getRegion());
    //                 }
    //             }
    //             if (StringUtil.isNotEmpty(province) && StringUtil.isNotEmpty(city) && StringUtil.isNotEmpty(region)) {
    //                 //根据发起人所在省市区查询其所属的区县级公司
    //                 StationCompanyDTO stationCompany = systemFeign.getStationCompanyByPCR(province, city, region);
    //                 if (stationCompany != null) {
    //                     //结算主体为区县级发起人所属区县级公司
    //                     recordPart.setSettlementSubjectId(stationCompany.getId());
    //                     recordPart.setSettlementSubjectName(stationCompany.getName());
    //                 }
    //             }
    //             recordPartList.add(recordPart);
    //         }
    //
    //         //11-服务站站长
    //         rulePart = rulePartMap.get(IncomeSubjectEnum.STATION_MASTER.value);
    //         if (rulePart != null) {
    //             ProductIncomeRecordPart recordPart = this.createProductIncomeRecordPart(record, incomeRule, rulePart);
    //             String province = subOrder.getDistributorProvince();
    //             String city = subOrder.getDistributorCity();
    //             String region = subOrder.getDistributorRegion();
    //             // 根据经销商获取服务站
    //             StationDTO station = systemFeign.getStationByPRC(province, city, region);
    //             if (station != null) {
    //                 DistributorDTO masterDistributor = userFeign.getDistributorById(station.getMasterDistributorId());
    //                 //收益主体为服务站站长
    //                 recordPart.setSubjectId(station.getMasterDistributorId());
    //                 recordPart.setSubjectName(station.getMasterName());
    //                 recordPart.setSubjectPhone(station.getMasterPhone());
    //                 recordPart.setSubjectIdCard(masterDistributor.getIdCard());
    //                 recordPart.setSubjectProvince(station.getProvince());
    //                 recordPart.setSubjectCity(station.getCity());
    //                 recordPart.setSubjectRegion(station.getRegion());
    //             }
    //             //根据经销商所在省市区查询其所属的区县级公司
    //             StationCompanyDTO stationCompany = systemFeign.getStationCompanyByPCR(province, city, region);
    //             if (stationCompany != null) {
    //                 //结算主体为服务站站长所属区县级公司
    //                 recordPart.setSettlementSubjectId(stationCompany.getId());
    //                 recordPart.setSettlementSubjectName(stationCompany.getName());
    //             }
    //             recordPartList.add(recordPart);
    //         }
    //
    //         //12-区县级股东（推荐人）
    //         rulePart = rulePartMap.get(IncomeSubjectEnum.REGION_SHAREHOLDER.value);
    //         if (rulePart != null) {
    //             ProductIncomeRecordPart recordPart = this.createProductIncomeRecordPart(record, incomeRule, rulePart);
    //             DistributorDTO recommend = userFeign.getRecommendByDistributorId(subOrder.getDistributorId());
    //             if (recommend != null) {
    //                 //收益主体为推荐人所属区县级公司
    //                 recordPart.setSubjectId(recommend.getId());
    //                 recordPart.setSubjectIdCard(recommend.getIdCard());
    //                 recordPart.setSubjectName(recommend.getRealName());
    //                 recordPart.setSubjectPhone(recommend.getPhone());
    //                 recordPart.setSubjectProvince(recommend.getProvince());
    //                 recordPart.setSubjectCity(recommend.getCity());
    //                 recordPart.setSubjectRegion(recommend.getRegion());
    //                 String province = recommend.getProvince();
    //                 String city = recommend.getCity();
    //                 String region = recommend.getRegion();
    //                 StationCompanyDTO stationCompany = systemFeign.getStationCompanyByPCR(province, city, region);
    //                 if (stationCompany != null) {
    //                     //结算主体为推荐人所属区县级公司
    //                     recordPart.setSettlementSubjectId(stationCompany.getId());
    //                     recordPart.setSettlementSubjectName(stationCompany.getName());
    //                 }
    //             }
    //             recordPartList.add(recordPart);
    //         }
    //
    //         //13-分销用户（会员用户）
    //         BigDecimal saleUserIncome = new BigDecimal(0);
    //         rulePart = rulePartMap.get(IncomeSubjectEnum.DISTRIBUTOR_USER.value);
    //         if (rulePart != null) {
    //             ProductIncomeRecordPart recordPart = this.createProductIncomeRecordPart(record, incomeRule, rulePart);
    //             //获取分销用户
    //             Integer userId = subOrder.getUserId();
    //             UserDTO saleUser = userFeign.getMySaleUserById(userId);
    //             if (saleUser != null) {
    //                 //收益主体为分销用户
    //                 recordPart.setSubjectId(saleUser.getId());
    //                 recordPart.setSubjectName(StringUtil.isNotEmpty(saleUser.getRealName()) ? saleUser.getRealName() : saleUser.getNickName());
    //                 recordPart.setSubjectPhone(saleUser.getMobile());
    //                 recordPart.setSubjectIdCard(saleUser.getIdCard());
    //                 String province = subOrder.getDistributorProvince();
    //                 String city = subOrder.getDistributorCity();
    //                 String region = subOrder.getDistributorRegion();
    //                 // 分销用户省市区为空取经销商的省市区
    //                 //TODO 新版本是要把分销用户的收益结算到服务站？待定
    //                 recordPart.setSubjectProvince(province);
    //                 recordPart.setSubjectCity(city);
    //                 recordPart.setSubjectRegion(region);
    //                 // 暂定结算主体为分销用户
    //                 // 确认分销用户的结算主体为经销商的区县级公司
    //                 // 根据经销商所在省市区查询其所属的区县级公司
    //                 StationCompanyDTO stationCompany = systemFeign.getStationCompanyByPCR(province, city, region);
    //                 if (stationCompany != null) {
    //                     //结算主体为服务站站长所属区县级公司
    //                     recordPart.setSettlementSubjectId(stationCompany.getId());
    //                     recordPart.setSettlementSubjectName(stationCompany.getName());
    //                 }
    //                 recordPartList.add(recordPart);
    //
    //                 //下单和支付间隔过大，可能健康大使的身份已经变为会员用户，故在此判断订单详情中的会员用户信息是否需要更新
    //                 SubOrderDetail orderDetail = subOrderDetailMapper.selectByPrimaryKey(orderId);
    //                 if (orderDetail.getVipUserId() == null || !Objects.equals(saleUser.getId(), orderDetail.getVipUserId())) {
    //                     SubOrderDetail update = new SubOrderDetail();
    //                     update.setSubOrderId(orderId);
    //                     update.setVipUserId(saleUser.getId());
    //                     update.setVipUserName(saleUser.getRealName());
    //                     update.setVipUserPhone(saleUser.getMobile());
    //                     update.setVipUserType(saleUser.getUserType());
    //                     update.setVipUserTypeName(UserType.getNameByType(saleUser.getUserType()));
    //                     update.setVipUserHasIncome(true);
    //                     update.setUpdateTime(new Date());
    //                     subOrderDetailMapper.updateByPrimaryKeySelective(update);
    //                 }
    //             } else {
    //                 saleUserIncome = rulePart.getValue();
    //             }
    //         }
    //
    //         //14-经销商
    //         rulePart = rulePartMap.get(IncomeSubjectEnum.DISTRIBUTOR.value);
    //         if (rulePart != null) {
    //             ProductIncomeRecordPart recordPart = new ProductIncomeRecordPart();
    //             //收益ID
    //             recordPart.setRecordId(record.getId());
    //             //获取收益主体
    //             Integer subjectId = rulePart.getSubjectId();
    //             IncomeSubject subject = incomeSubjectMapper.selectByPrimaryKey(subjectId);
    //             //如果分销用户没找到那收益归属到经销商
    //             BigDecimal value = rulePart.getValue().add(saleUserIncome);
    //             if (incomeRule.getAllotType() == IncomeAllotType.RATIO.value) {
    //                 //收益比例
    //                 recordPart.setSubjectRatio(value);
    //                 //收益金额
    //                 recordPart.setSubjectMoney(record.getSettlementFee().multiply(value.divide(new BigDecimal(100))));
    //             } else {
    //                 //收益金额
    //                 recordPart.setSubjectMoney(value);
    //             }
    //             //收益主体为经销商
    //             recordPart.setSubjectId(subOrder.getDistributorId());
    //             recordPart.setSubjectName(subOrder.getDistributorName());
    //             recordPart.setSubjectCode(subject.getIncomeSubjectCode());
    //             recordPart.setSubjectPhone(subOrder.getDistributorPhone());
    //             DistributorDTO distributor = userFeign.getDistributorById(subOrder.getDistributorId());
    //             if (distributor != null) {
    //                 recordPart.setSubjectIdCard(distributor.getIdCard());
    //             }
    //             recordPart.setSubjectProvince(subOrder.getDistributorProvince());
    //             recordPart.setSubjectCity(subOrder.getDistributorCity());
    //             recordPart.setSubjectRegion(subOrder.getDistributorRegion());
    //             //结算主体（先设置后面会覆盖）
    //             recordPart.setSettlementSubjectCode(subject.getSettlementSubjectCode());
    //             recordPart.setSettlementSubjectName(subject.getSettlementSubjectName());
    //             recordPart.setSettlementTime(new Date());
    //             recordPart.setHasWithdraw(0);
    //             recordPart.setCreateTime(new Date());
    //
    //             String province = subOrder.getDistributorProvince();
    //             String city = subOrder.getDistributorCity();
    //             String region = subOrder.getDistributorRegion();
    //             //根据经销商所在省市区查询其所属的区县级公司
    //             StationCompanyDTO stationCompany = systemFeign.getStationCompanyByPCR(province, city, region);
    //             if (stationCompany != null) {
    //                 //结算主体为服务站站长所属区县级公司
    //                 recordPart.setSettlementSubjectId(stationCompany.getId());
    //                 recordPart.setSettlementSubjectName(stationCompany.getName());
    //             }
    //             recordPartList.add(recordPart);
    //         }
    //
    //
    //         //15-服务站承包人（产品收益）
    //         rulePart = rulePartMap.get(IncomeSubjectEnum.STATION_CONTRACTOR_PRODUCT.value);
    //         if (rulePart != null) {
    //             ProductIncomeRecordPart recordPart = this.createProductIncomeRecordPart(record, incomeRule, rulePart);
    //             Date balanceTime = recordPart.getSettlementTime();
    //             String province = subOrder.getDistributorProvince();
    //             String city = subOrder.getDistributorCity();
    //             String region = subOrder.getDistributorRegion();
    //             StationDTO station = systemFeign.getStationByPRC(province, city, region);
    //             boolean canBalanceToStation = station != null &&
    //                     station.getContract() != null &&
    //                     station.getContract() &&
    //                     station.getContractStartTime() != null &&
    //                     station.getContractStartTime().before(balanceTime) &&
    //                     station.getContractEndTime() != null &&
    //                     station.getContractEndTime().after(balanceTime);
    //             if (canBalanceToStation) {
    //                 //收益主体为服务站承包人
    //                 recordPart.setSubjectId(station.getId());
    //                 recordPart.setSubjectName(station.getContractor());//服务站承包人姓名
    //                 recordPart.setSubjectPhone(station.getContractorPhone());
    //                 recordPart.setSubjectIdCard(station.getContractorIdCard());
    //                 recordPart.setSubjectProvince(station.getProvince());
    //                 recordPart.setSubjectCity(station.getCity());
    //                 recordPart.setSubjectRegion(station.getRegion());
    //             }
    //             //根据经销商所在省市区查询其所属的区县级公司
    //             StationCompanyDTO stationCompany = systemFeign.getStationCompanyByPCR(province, city, region);
    //             if (stationCompany != null) {
    //                 //结算主体为服务站站长所属区县级公司
    //                 recordPart.setSettlementSubjectId(stationCompany.getId());
    //                 recordPart.setSettlementSubjectName(stationCompany.getName());
    //             }
    //             recordPartList.add(recordPart);
    //         }
    //
    //         //17-安装工程师所在区县级公司
    //         rulePart = rulePartMap.get(IncomeSubjectEnum.ENGINEER_STATION_COMPANY.value);
    //         if (rulePart != null) {
    //             ProductIncomeRecordPart recordPart = this.createProductIncomeRecordPart(record, incomeRule, rulePart);
    //             String province = subOrder.getEngineerProvince();
    //             String city = subOrder.getEngineerCity();
    //             String region = subOrder.getEngineerRegion();
    //             //根据安装工程师所在省市区查询其所属的区县级公司
    //             StationCompanyDTO stationCompany = systemFeign.getStationCompanyByPCR(province, city, region);
    //             if (stationCompany != null) {
    //                 //收益主体为安装工程师所属区县级公司
    //                 recordPart.setSubjectId(stationCompany.getId());
    //                 recordPart.setSubjectName(stationCompany.getName());
    //                 recordPart.setSubjectPhone(stationCompany.getContactPhone());
    //                 //TODO 是否需要
    //                 //recordPart.setSubjectIdCard(stationCompany.getContactIdCard());
    //                 recordPart.setSubjectProvince(stationCompany.getProvince());
    //                 recordPart.setSubjectCity(stationCompany.getCity());
    //                 recordPart.setSubjectRegion(stationCompany.getRegion());
    //                 //结算主体为安装工程师所属区县级公司
    //                 recordPart.setSettlementSubjectId(stationCompany.getId());
    //                 recordPart.setSettlementSubjectName(stationCompany.getName());
    //             }
    //             recordPartList.add(recordPart);
    //         }
    //
    //         //18-安装工程师
    //         rulePart = rulePartMap.get(IncomeSubjectEnum.ENGINEER.value);
    //         if (rulePart != null) {
    //             ProductIncomeRecordPart recordPart = this.createProductIncomeRecordPart(record, incomeRule, rulePart);
    //             String province = subOrder.getEngineerProvince();
    //             String city = subOrder.getEngineerCity();
    //             String region = subOrder.getEngineerRegion();
    //             //收益主体为安装工程师
    //             recordPart.setSubjectId(subOrder.getEngineerId());
    //             recordPart.setSubjectName(subOrder.getEngineerName());
    //             recordPart.setSubjectPhone(subOrder.getEngineerPhone());
    //             recordPart.setSubjectIdCard(subOrder.getEngineerIdCard());
    //
    //             recordPart.setSubjectProvince(subOrder.getEngineerProvince());
    //             recordPart.setSubjectCity(subOrder.getEngineerCity());
    //             recordPart.setSubjectRegion(subOrder.getEngineerRegion());
    //             //根据安装工程师省市区查询其所属的区县级公司
    //             if (StringUtil.isNotEmpty(province) && StringUtil.isNotEmpty(city) && StringUtil.isNotEmpty(region)) {
    //                 StationCompanyDTO stationCompany = systemFeign.getStationCompanyByPCR(province, city, region);
    //                 if (stationCompany != null) {
    //                     //结算主体为安装工程师所属区县级公司
    //                     recordPart.setSettlementSubjectId(stationCompany.getId());
    //                     recordPart.setSettlementSubjectName(stationCompany.getName());
    //                 }
    //             }
    //             recordPartList.add(recordPart);
    //         }
    //
    //         if (CollectionUtil.isNotEmpty(recordPartList)) {
    //             productIncomeRecordPartMapper.batchInsert(recordPartList);
    //         }
    //     } catch (Exception e) {
    //         log.error(e.getMessage(), e);
    //         StringWriter sw = new StringWriter();
    //         e.printStackTrace(new PrintWriter(sw, true));
    //         String subject = "修改订单收益分配失败提醒" + domainProperties.getApi();
    //         String content = "修改订单收益分配时出错。orderId=" + orderId + "\n" + sw.toString();
    //         mailSender.send(null, subject, content);
    //         throw new YimaoException(e.getMessage(), e);
    //     }
    // }


    /**
     * 创建收益分配记录（明细）
     *
     * @param record   收益分配记录
     * @param rule     收益分配规则
     * @param rulePart 收益分配规则明细
     */
    @Override
    public ProductIncomeRecordPart createProductIncomeRecordPart(ProductIncomeRecord record, IncomeRuleDTO rule, IncomeRulePartDTO rulePart) {
        ProductIncomeRecordPart recordPart = new ProductIncomeRecordPart();
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
        recordPart.setSubjectId(subjectId);
        recordPart.setSubjectCode(subject.getIncomeSubjectCode());
        recordPart.setSubjectName(subject.getIncomeSubjectName());
        recordPart.setSettlementSubjectCode(subject.getSettlementSubjectCode());
        recordPart.setSettlementSubjectName(subject.getSettlementSubjectName());
        recordPart.setSettlementTime(record.getCreateTime());
        recordPart.setCreateTime(record.getCreateTime());
        // 设置是否提现
        recordPart.setHasWithdraw(0);
        return recordPart;
    }

    /**
     * 查询产品和续费收益
     *
     * @param incomeQueryDTO 查询dto
     * @param pageNum        页码
     * @param pageSize       页数
     */
    @Override
    public PageVO<ProductIncomeVO> pageQueryProductIncome(ProductIncomeQueryDTO incomeQueryDTO, Integer pageNum, Integer pageSize) {
        Page<ProductIncomeVO> page;
        PageHelper.startPage(pageNum, pageSize);
        // 1-产品收益  2-续费收益
        if (IncomeType.RENEW_INCOME.value == incomeQueryDTO.getIncomeType()) {
            page = productIncomeRecordMapper.queryRenewProductIncomeList(incomeQueryDTO);
        } else {
            page = productIncomeRecordMapper.queryProductIncomeList(incomeQueryDTO);
        }

        return new PageVO<>(pageNum, page);
    }

    /**
     * 根据订单id查询收益记录
     *
     * @param orderId 订单id
     */
    @Override
    public List<ProductIncomeRecordPartDTO> getProductIncomeRecordPartList(Integer orderId) {
        if (orderId == null) {
            throw new BadRequestException("订单id必须填写！");
        }
        Example example = new Example(ProductIncomeRecord.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("orderId", orderId);

        List<ProductIncomeRecord> list = productIncomeRecordMapper.selectByExample(example);

        if (list == null || list.size() <= 0) {
            throw new BadRequestException("订单信息有误！");
        }

        Example example1 = new Example(ProductIncomeRecordPart.class);
        Example.Criteria criteria1 = example1.createCriteria();
        criteria1.andEqualTo("productIncomeRecordId", list.get(0).getId());
        List<ProductIncomeRecordPart> listTemp = productIncomeRecordPartMapper.selectByExample(example1);

        ProductIncomeRecordPartDTO dto;
        List<ProductIncomeRecordPartDTO> dtoList = new ArrayList<>();
        for (ProductIncomeRecordPart productIncomeRecordPart : listTemp) {
            dto = new ProductIncomeRecordPartDTO();
            productIncomeRecordPart.convert(dto);
            dtoList.add(dto);
        }
        return dtoList;
    }

    /**
     * 根据id查询收益记录
     *
     * @param id id
     */
    @Override
    public IncomeRecordResultDTO getProductIncomeById(Integer id) {
        IncomeRecordResultDTO productIncomeRecord = productIncomeRecordMapper.selectProductIncomeByPrimaryKey(id);
        if (productIncomeRecord == null) {
            throw new BadRequestException("收益id信息有误！");
        }
        List<IncomeRecordPartResultDTO> list = productIncomeRecordPartMapper.getPartByIncomeRecordId(id);
        for (IncomeRecordPartResultDTO dto : list) {
            Integer subjectId = dto.getSubjectId();
            String province = dto.getProvince();
            String city = dto.getCity();
            String region = dto.getRegion();
            String incomeSubjectCode = dto.getIncomeSubjectCode();
            Example example = new Example(IncomeSubject.class);
            Example.Criteria criteria1 = example.createCriteria();
            criteria1.andEqualTo("incomeSubjectCode", incomeSubjectCode);
            IncomeSubject incomeSubject = incomeSubjectMapper.selectOneByExample(example);
            if (incomeSubject != null) {
                dto.setIncomeSubjectName(incomeSubject.getIncomeSubjectName());
                dto.setSettlementSubjectName(incomeSubject.getSettlementSubjectName());
            }
            if (province != null && city != null && region != null) {
                StationCompanyDTO stationCompany = systemFeign.getStationCompanyByPCR(province, city, region,StationAreaServiceTypeEnum.PRE_SALE.value);
                if (stationCompany != null) {
                    dto.setCompanyName(stationCompany.getName());
                }
            }
            if (IncomeSubjectEnum.DISTRIBUTOR_USER.value.equals(incomeSubjectCode)) {
                // 分销商设置e家号
                dto.setUserId(subjectId);
            }
            boolean sign = IncomeSubjectEnum.REGION_SPONSOR.value.equals(incomeSubjectCode) ||
                    IncomeSubjectEnum.DISTRIBUTOR.value.equals(incomeSubjectCode) ||
                    IncomeSubjectEnum.REGION_SHAREHOLDER.value.equals(incomeSubjectCode) ||
                    IncomeSubjectEnum.STATION_MASTER.value.equals(incomeSubjectCode);
            if (sign) {
                if (subjectId != null) {
                    DistributorDTO distributor = userFeign.getDistributorBasicInfoById(subjectId);
                    // 设置经销商账号
                    if (distributor != null) {
                        dto.setAccount(distributor.getUserName());
                        dto.setSubjectPhone(distributor.getPhone());
                        dto.setUserId(distributor.getUserId());
                    }
                }
            }
        }
        productIncomeRecord.setIncomePartResults(list);

        return productIncomeRecord;
    }

    @Override
    public void changeProductIncomeRecordToComplete(Long orderId, Date orderCompleteTime) {
        try {
            Example example = new Example(ProductIncomeRecord.class);
            Example.Criteria criteria = example.createCriteria();
            criteria.andEqualTo("orderCompleteStatus", 0);
            criteria.andEqualTo("orderId", orderId);
            List<ProductIncomeRecord> productIncomeRecords = productIncomeRecordMapper.selectByExample(example);
            if (CollectionUtil.isNotEmpty(productIncomeRecords)) {
                for (ProductIncomeRecord record : productIncomeRecords) {
                    record.setOrderCompleteTime(orderCompleteTime);
                    //计算结算月份
                    String settlementMonth = DateUtil.transferDateToString(DateUtil.monthAfter(orderCompleteTime, 1), "yyyy-MM");
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
                    productIncomeRecordMapper.updateByPrimaryKeySelective(record);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new YimaoException("产品收益状态更新失败:", e);
        }

    }

    @Override
    public void refundOrder(Long orderId) {
        Example example = new Example(ProductIncomeRecord.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("orderCompleteStatus", 0);
        criteria.andEqualTo("orderId", orderId);
        List<ProductIncomeRecord> productIncomeRecords = productIncomeRecordMapper.selectByExample(example);

        if (CollectionUtil.isNotEmpty(productIncomeRecords)) {
            for (ProductIncomeRecord productIncomeRecord : productIncomeRecords) {
                productIncomeRecord.setStatus(3);//状态改为已失效
                productIncomeRecord.setUpdateTime(new Date());

                productIncomeRecordMapper.updateByPrimaryKeySelective(productIncomeRecord);
            }
        }
    }

    @Override
    public void refundGoods(Long orderId, Integer num) {
        if (null == orderId) {
            throw new BadRequestException("订单ID必须传参！");
        }
        if (null == num) {
            throw new BadRequestException("退货数量必须传参！");
        }
        Example example = new Example(ProductIncomeRecord.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("status", 0);
        criteria.andEqualTo("orderId", orderId);
        List<ProductIncomeRecord> productIncomeRecords = productIncomeRecordMapper.selectByExample(example);

        if (CollectionUtil.isNotEmpty(productIncomeRecords)) {
            ProductIncomeRecord productIncomeRecord = productIncomeRecords.get(0);
            if (num > productIncomeRecord.getSettlementAmount()) {
                throw new BadRequestException("退货数量必须小于可退数量！");
            } else if (Objects.equals(num, productIncomeRecord.getSettlementAmount())) {//退货全退
                productIncomeRecord.setStatus(3);//状态改为已失效
                productIncomeRecord.setUpdateTime(new Date());
            } else {
                Integer settlementCount = productIncomeRecord.getSettlementAmount() - num;
                BigDecimal money = productIncomeRecord.getSettlementFee().divide(new BigDecimal(productIncomeRecord.getSettlementAmount()), 2, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(num));
                productIncomeRecord.setSettlementAmount(settlementCount);//变更可结算数量

                //变更各收益方的金额
                List<ProductIncomeRecordPartDTO> list = productIncomeRecordPartMapper.getPartByProductIncomeRecordId(productIncomeRecord.getId());
                for (ProductIncomeRecordPartDTO productIncomeRecordPartDTO : list) {
                    productIncomeRecordPartDTO.setSubjectMoney(money.multiply(productIncomeRecordPartDTO.getSubjectRatio()));
                    productIncomeRecordPartDTO.setUpdateTime(new Date());
                    productIncomeRecordPartMapper.updateByPrimaryKeySelective(new ProductIncomeRecordPart(productIncomeRecordPartDTO));
                }
                productIncomeRecord.setSettlementFee(money);//变更可结算金额
                productIncomeRecord.setUpdateTime(new Date());

                //如果结算数量为0，结算金额为0,表示全退。结算状态改为已失效(退单完成)
//                if ((settlementCount == 0) && Objects.equals(money, new BigDecimal(0))) {
                // TODO 开发环境暂时只用数量判断
                if (settlementCount == 0) {
                    productIncomeRecord.setStatus(3);//状态改为已失效
                    productIncomeRecord.setUpdateTime(new Date());
                }
            }
            productIncomeRecordMapper.updateByPrimaryKeySelective(productIncomeRecord);
        }
    }

    @Override
    public Map<String, Object> listIncomeByProductType(Integer userId, Integer incomeType) {
        // 该方法的公用变量
        List<DistributorIncomeDTO> incomeList;
        BigDecimal totalIncome;
        Map<String, Object> map = new HashMap<>(8);
        // 累计提现/结算金额
        BigDecimal totalWithdrawCash;
        // 冻结/不可提现金额/待结算金额
        BigDecimal canNotBeWithdrawCash;
        Map<String, Object> productTypeMap = new HashMap<>(8);
        Map<String, Object> canBeWithdrawCashMap = new HashMap<>(8);
        //通过用户id获取用户
        UserDTO user = userFeign.getBasicUserById(userId);
        if (user == null) {
            return null;
        }
        Integer distributorId = null;
        Integer userType = user.getUserType();
        Integer mid = user.getMid();
        boolean isVipUser = UserType.isVipUser(userType);
        boolean isDistributor = UserType.isDistributor(userType);
        if (!isVipUser && !isDistributor) {
            return null;
        }
        if (isDistributor) {
            if (mid == null) {
                return null;
            }
            // 用户是经销商 subject_id 为user_distributor表主键ID
            distributorId = mid;
        }
        List<ProductCategoryDTO> productTypeList = productFeign.getFirstProductCategory();

        //可提现的金额：按产品区分
        List<DistributorIncomeDTO> canBeWithdrawCashList = productIncomeRecordPartMapper.listUserIncomeByProductType(userId, distributorId, 1, isVipUser, isDistributor, incomeType);
        if (CollectionUtil.isNotEmpty(canBeWithdrawCashList)) {
            for (DistributorIncomeDTO income : canBeWithdrawCashList) {
                canBeWithdrawCashMap.computeIfAbsent(income.getProductTypeName(), k -> income.getIncome());
            }
        }
        // 如果该用户是企业版'子'账户,直接返回null
        if (userType == UserType.DISTRIBUTOR_1000.value) {
            //可提现总金额
            map.put("totalIncome", 0);
            //已提现金额
            map.put("totalWithdrawCash", 0);
            //可提现金额
            map.put("canBeWithdrawCash", 0);
            //不可提现金额/冻结
            map.put("canNotBeWithdrawCash", 0);
            for (ProductCategoryDTO productType : productTypeList) {
                productTypeMap.put(productType.getName(), 0);
            }
            map.put("productTypeList", productTypeMap);
            return map;
        }
        //获取不同产品类型的所有收益
        incomeList = productIncomeRecordPartMapper.listUserIncomeByProductType(userId, distributorId, null, isVipUser, isDistributor, incomeType);
        //获取总收益
        totalIncome = productIncomeRecordPartMapper.listUserTotalIncome(userId, distributorId, OrderCompleteStatusEnum.CAN_BE_SETTLED.value, null, null, null, isVipUser, isDistributor, incomeType);
        canNotBeWithdrawCash = productIncomeRecordPartMapper.listUserTotalIncome(userId, distributorId, OrderCompleteStatusEnum.UNCOMPLETED.value, null, null, null, isVipUser, isDistributor, incomeType);
        totalWithdrawCash = withdrawSubMapper.getWithdrawnAmount(userId, incomeType);

        //可提现总金额
        map.put("totalIncome", totalIncome == null ? 0 : totalIncome);
        //已提现金额
        map.put("totalWithdrawCash", totalWithdrawCash == null ? 0 : totalWithdrawCash);

        if (totalIncome == null) {
            map.put("canBeWithdrawCash", 0);
        }
        if (totalWithdrawCash == null) {
            map.put("canBeWithdrawCash", totalIncome == null ? 0 : totalIncome);
        }
        if (totalIncome != null && totalWithdrawCash != null) {
            map.put("canBeWithdrawCash", new BigDecimal(totalIncome + "").subtract(new BigDecimal(totalWithdrawCash + "")).doubleValue());
        }
        //不可提现金额/冻结
        map.put("canNotBeWithdrawCash", canNotBeWithdrawCash == null ? 0 : canNotBeWithdrawCash);

        if (CollectionUtil.isEmpty(incomeList)) {
            for (ProductCategoryDTO productType : productTypeList) {
                productTypeMap.put(productType.getName(), 0);
            }
        }
        if (CollectionUtil.isNotEmpty(productTypeList) && CollectionUtil.isNotEmpty(incomeList)) {
            for (ProductCategoryDTO productType : productTypeList) {
                for (DistributorIncomeDTO distributorIncome : incomeList) {
                    if (distributorIncome.getProductTypeId() != null) {
                        if (productType.getId() == distributorIncome.getProductTypeId().intValue()) {
                            productTypeMap.put(productType.getName(), distributorIncome.getIncome());
                        }
                    }
                }
                boolean b = productTypeMap.containsKey(productType.getName());
                if (!b) {
                    productTypeMap.put(productType.getName(), 0);
                }
            }
        }
        //产品类型：所有收益
        map.put("productTypeList", productTypeMap);
        //产品类型：可提现收益
        map.put("canBeWithdrawCashMap", canBeWithdrawCashMap);
        return map;
    }

    @Override
    public Map<String, Object> listIncomeDetail(Integer userId, Integer productTypeId, String year, String month, Integer incomeType, Integer pageNum, Integer pageSize) {
        // 通过userId获取收益明细
        Map<String, Object> map = new HashMap<>(8);
        BigDecimal totalIncome;
        Page<UserIncomeDTO> page;
        List<DistributorDTO> distributorInfos = new ArrayList<>();
        UserDTO user;
        Integer userType;
        DistributorDTO info = null;
        String name = null;
        //通过用户id获取用户
        user = userFeign.getBasicUserById(userId);
        if (user == null) {
            return null;
        }
        userType = user.getUserType();
        boolean isVipUser = UserType.isVipUser(userType);
        boolean isDistributor = UserType.isDistributor(userType);
        if (!isVipUser && !isDistributor) {
            return null;
        }
        Integer mid = user.getMid();
        Integer distributorId = null;
        if (UserType.isDistributor(userType)) {
            info = userFeign.getDistributorBasicInfoById(mid);
            name = info.getRealName();
            // 查询用户是经销商 还需通过经销商Id 获取收益
            distributorId = mid;
        }
        if (userType == UserType.DISTRIBUTOR_1000.value) {
            return null;
        }
        if (userType == UserType.DISTRIBUTOR_950.value) {
            // 获取子账号的信息(用户信息)
            distributorInfos = userFeign.getSonDistributorByMid(user.getMid());
        }
        PageHelper.startPage(pageNum, pageSize);
        //根据条件分页查询用户的收益明细
        page = productIncomeRecordPartMapper.listUserIncomeDetail(userId, distributorId, OrderCompleteStatusEnum.CAN_BE_SETTLED.value, year, month, productTypeId, isVipUser, isDistributor, incomeType);
        //根据条件查询用户的总收益
        totalIncome = productIncomeRecordPartMapper.listUserTotalIncome(userId, distributorId, OrderCompleteStatusEnum.CAN_BE_SETTLED.value, year, month, productTypeId, isVipUser, isDistributor, incomeType);
        // 当用户非企业版经销商母账号时,或者是企业版经销商母账号但没有子账号
        if (info != null) {
            distributorInfos.add(info);
        }
        Collections.reverse(distributorInfos);
        if (CollectionUtil.isEmpty(page)) {
            if (userType != UserType.DISTRIBUTOR_950.value) {            // 如果为企业版母账户,但收益明细记录为空时,把子账户的信息返回
                return null;
            }
            map.put("basePage", null);
        } else {
            // 设置来源账号和享受收益分销用户信息
            for (UserIncomeDTO userIncomeDTO : page) {
                if (CollectionUtil.isNotEmpty(distributorInfos)) {
                    if (userIncomeDTO.getSubDistributorName() != null) {
                        userIncomeDTO.setResourceName(userIncomeDTO.getSubDistributorName());      // 收益来源, 企业版母账号的收益来源有自己和子账号, 其他类型的只有自己
                    } else {
                        userIncomeDTO.setResourceName(name);
                    }
                }
                if (userIncomeDTO.getUserId() != null) {
                    UserDTO userById = userFeign.getBasicUserById(userIncomeDTO.getUserId());
                    userIncomeDTO.setHeadImg(userById.getHeadImg());
                    userIncomeDTO.setNickName(userById.getNickName());
                    //userIncomeDTO.setResourceName(userById.getRealName() == null ? userById.getNickName() : userById.getRealName());
                }
                // 如果是经销商
                if (UserType.isDistributor(userType)) {
                    // 获取分销用户的收益
                    Integer recordId = userIncomeDTO.getRecordId();
                    ProductIncomeRecordPart disPart = this.selectDistributorOrRecommend(recordId, IncomeSubjectEnum.DISTRIBUTOR_USER.value);
                    if (disPart != null) {
                        Integer subjectId = disPart.getSubjectId();
                        UserDTO saleDto = userFeign.getBasicUserById(subjectId);
                        userIncomeDTO.setUserSaleHeadImg(saleDto.getHeadImg());
                        userIncomeDTO.setUserSaleNickName(saleDto.getNickName());
                        userIncomeDTO.setUserSaleId(subjectId);
                        userIncomeDTO.setUserSaleMoney(disPart.getSubjectMoney());
                    }
                }
            }
            map.put("basePage", new PageVO<>(pageNum, page));
        }
        map.put("totalIncome", totalIncome);
        if (userType == UserType.DISTRIBUTOR_950.value) {
            map.put("sonDistributor", distributorInfos);
        } else {
            map.put("sonDistributor", null);
        }
        return map;
    }

    /**
     * 用户查看收益明细（公众号）主账号/子账号
     *
     * @author hhf
     * @date 2019/6/27
     */
    @Override
    public Map<String, Object> listDistributorIncomeDetail(Integer distributorId, Integer productTypeId, String year, String month, Integer incomeType, Integer pageNum, Integer pageSize) {
        // 主账号 或者 子账号查询
        Map<String, Object> map = new HashMap<>(8);
        BigDecimal totalIncome;
        Page<UserIncomeDTO> page;
        Integer userType = null;
        Integer mainId = null;
        Integer sonId = null;
        DistributorDTO info = userFeign.getDistributorBasicInfoById(distributorId);
        String name = info.getRealName();
        Integer roleLevel = info.getRoleLevel();
        if (roleLevel != null) {
            if (roleLevel == DistributorRoleLevel.D_950.value) {
                // 主账号
                userType = UserType.DISTRIBUTOR_950.value;
                // 传入经销商Id则根据经销商id查询
                mainId = distributorId;
            } else {
                // 子账号
                userType = UserType.DISTRIBUTOR_1000.value;
                // 获取主账号
                DistributorDTO main = userFeign.getMainAccountByDistributorId(distributorId);
                mainId = main.getId();
                sonId = distributorId;
            }
        }
        // 主账号自己的收益 子账号的收益
        PageHelper.startPage(pageNum, pageSize);
        page = productIncomeRecordPartMapper.listDistributorIncomeDetail(mainId, OrderCompleteStatusEnum.CAN_BE_SETTLED.value, year, month, productTypeId, sonId, incomeType);        //根据条件分页查询收益明细
        totalIncome = productIncomeRecordPartMapper.listDistributorTotalIncome(mainId, OrderCompleteStatusEnum.CAN_BE_SETTLED.value, year, month, productTypeId, sonId);
        if (CollectionUtil.isEmpty(page)) {
            if (userType != null && userType != UserType.DISTRIBUTOR_950.value) {            // 如果为企业版母账户,但收益明细记录为空时,把子账户的信息返回
                return null;
            }
            map.put("basePage", null);
        } else {
            // 设置来源账号和享受收益分销用户信息
            for (UserIncomeDTO userIncomeDTO : page) {
                if (userIncomeDTO.getSubDistributorName() != null) {
                    userIncomeDTO.setResourceName(userIncomeDTO.getSubDistributorName());      // 收益来源, 企业版母账号的收益来源有自己和子账号, 其他类型的只有自己
                } else {
                    userIncomeDTO.setResourceName(name);
                }
                if (userIncomeDTO.getUserId() != null) {
                    UserDTO userById = userFeign.getBasicUserById(userIncomeDTO.getUserId());
                    userIncomeDTO.setHeadImg(userById.getHeadImg());
                    userIncomeDTO.setNickName(userById.getNickName());
                    //userIncomeDTO.setResourceName(userById.getRealName() == null ? userById.getNickName() : userById.getRealName());
                }
                // 如果是经销商
                if (UserType.isDistributor(userType)) {
                    // 获取分销用户的收益
                    Integer recordId = userIncomeDTO.getRecordId();
                    ProductIncomeRecordPart disPart = this.selectDistributorOrRecommend(recordId, IncomeSubjectEnum.DISTRIBUTOR_USER.value);
                    if (disPart != null) {
                        Integer subjectId = disPart.getSubjectId();
                        UserDTO saleDto = userFeign.getBasicUserById(subjectId);
                        userIncomeDTO.setUserSaleHeadImg(saleDto.getHeadImg());
                        userIncomeDTO.setUserSaleNickName(saleDto.getNickName());
                        userIncomeDTO.setUserSaleId(subjectId);
                        userIncomeDTO.setUserSaleMoney(disPart.getSubjectMoney());
                    }
                }
            }
            map.put("basePage", new PageVO<>(pageNum, page));
        }
        map.put("totalIncome", totalIncome);
        map.put("sonDistributor", null);
        return map;
    }

    /**
     * 导出
     *
     * @param query
     * @return
     */
    @Override
    public Page<IncomeExportDTO> productIncomeExport(ProductIncomeQueryDTO query, Integer pageNum, Integer pageSize) {
        Page<IncomeExportDTO> list;
        if (query.getIncomeType() == 2) {
            //续费收益导出
            PageHelper.startPage(pageNum, pageSize);
            list = productIncomeRecordMapper.productRenewIncomeExport(query);
        } else {
            PageHelper.startPage(pageNum, pageSize);
            list = productIncomeRecordMapper.productIncomeExport(query);
        }
        this.setOtherInfo(list);
        return list;
    }

    // @Override
    // public Integer productIncomeExportCount(ProductIncomeQueryDTO query) {
    //     Integer count;
    //     if (query.getIncomeType() == 2) {
    //         //续费收益导出
    //         count = productIncomeRecordMapper.productRenewIncomeExportCount(query);
    //     } else {
    //         count = productIncomeRecordMapper.productIncomeExportCount(query);
    //     }
    //     return count;
    // }

    private void setOtherInfo(List<IncomeExportDTO> incomeExportDTOS) {
        for (IncomeExportDTO incomeExportDTO : incomeExportDTOS) {
            List<ProductIncomeRecordPartDTO> productIncomeRecordPartDTOList = productIncomeRecordPartMapper.getPartByProductIncomeRecordId(incomeExportDTO.getId());
            if (CollectionUtil.isNotEmpty(productIncomeRecordPartDTOList)) {
                for (ProductIncomeRecordPartDTO productIncomeRecordPartDTO : productIncomeRecordPartDTOList) {
                    //收益分配
                    if (StringUtil.isNotEmpty(productIncomeRecordPartDTO.getSubjectCode()) && productIncomeRecordPartDTO.getSubjectCode().equalsIgnoreCase(IncomeSubjectEnum.PRODUCT_COMPANY.value)) {
                        //产品公司
                        incomeExportDTO.setProductCompanyIncome(productIncomeRecordPartDTO.getSubjectMoney());
                    }
                    if (StringUtil.isNotEmpty(productIncomeRecordPartDTO.getSubjectCode()) && productIncomeRecordPartDTO.getSubjectCode().equalsIgnoreCase(IncomeSubjectEnum.MAIN_COMPANY.value)) {
                        //总部公司
                        incomeExportDTO.setProductCompanyIncome(productIncomeRecordPartDTO.getSubjectMoney());
                    }
                    if (StringUtil.isNotEmpty(productIncomeRecordPartDTO.getSubjectCode()) && productIncomeRecordPartDTO.getSubjectCode().equalsIgnoreCase(IncomeSubjectEnum.CITY_PARTNER.value)) {
                        //市级合伙人
                        incomeExportDTO.setCityPartnerIncome(productIncomeRecordPartDTO.getSubjectMoney());
                    }
                    if (StringUtil.isNotEmpty(productIncomeRecordPartDTO.getSubjectCode()) && productIncomeRecordPartDTO.getSubjectCode().equalsIgnoreCase(IncomeSubjectEnum.CITY_SPONSOR.value)) {
                        //市级发起人
                        incomeExportDTO.setCitySponsorIncome(productIncomeRecordPartDTO.getSubjectMoney());
                    }
                    if (StringUtil.isNotEmpty(productIncomeRecordPartDTO.getSubjectCode()) && productIncomeRecordPartDTO.getSubjectCode().equalsIgnoreCase(IncomeSubjectEnum.DISTRIBUTOR_STATION_COMPANY.value)) {
                        //经销商所在区县级公司
                        incomeExportDTO.setRegionDistributorIncome(productIncomeRecordPartDTO.getSubjectMoney());
                        incomeExportDTO.setDistributorStationName(productIncomeRecordPartDTO.getSubjectName());
                    }
                    if (StringUtil.isNotEmpty(productIncomeRecordPartDTO.getSubjectCode()) && productIncomeRecordPartDTO.getSubjectCode().equalsIgnoreCase(IncomeSubjectEnum.STATION_MASTER.value)) {
                        //服务站站长
                        incomeExportDTO.setStationMasterIncome(productIncomeRecordPartDTO.getSubjectMoney());
                        incomeExportDTO.setStationMasterIdCard(productIncomeRecordPartDTO.getSubjectIdCard());
                    }
                    if (StringUtil.isNotEmpty(productIncomeRecordPartDTO.getSubjectCode()) && productIncomeRecordPartDTO.getSubjectCode().equalsIgnoreCase(IncomeSubjectEnum.REGION_SPONSOR.value)) {
                        //区县级发起人
                        incomeExportDTO.setRegionSponsorIncome(productIncomeRecordPartDTO.getSubjectMoney());
                        incomeExportDTO.setRegionSponsorIdCard(productIncomeRecordPartDTO.getSubjectIdCard());
                    }
                    if (StringUtil.isNotEmpty(productIncomeRecordPartDTO.getSubjectCode()) && productIncomeRecordPartDTO.getSubjectCode().equalsIgnoreCase(IncomeSubjectEnum.REGION_SHAREHOLDER.value)) {
                        //区县级股东（推荐人）
                        incomeExportDTO.setRefereeIncome(productIncomeRecordPartDTO.getSubjectMoney());
                        incomeExportDTO.setRefereeStationName(productIncomeRecordPartDTO.getSettlementSubjectName());
                    }
                    if (StringUtil.isNotEmpty(productIncomeRecordPartDTO.getSubjectCode()) && productIncomeRecordPartDTO.getSubjectCode().equalsIgnoreCase(IncomeSubjectEnum.DISTRIBUTOR_USER.value)) {
                        //会员用户
                        incomeExportDTO.setSaleIncome(productIncomeRecordPartDTO.getSubjectMoney());
                        incomeExportDTO.setSaleIdCard(productIncomeRecordPartDTO.getSubjectIdCard());
                    }
                    if (StringUtil.isNotEmpty(productIncomeRecordPartDTO.getSubjectCode()) && productIncomeRecordPartDTO.getSubjectCode().equalsIgnoreCase(IncomeSubjectEnum.DISTRIBUTOR.value)) {
                        //经销商
                        incomeExportDTO.setDistributorIncome(productIncomeRecordPartDTO.getSubjectMoney());
                        incomeExportDTO.setDistributorStationName(productIncomeRecordPartDTO.getSettlementSubjectName());
                        incomeExportDTO.setDistributorIdCard(productIncomeRecordPartDTO.getSubjectIdCard());
                    }
                    if (StringUtil.isNotEmpty(productIncomeRecordPartDTO.getSubjectCode()) && productIncomeRecordPartDTO.getSubjectCode().equalsIgnoreCase(IncomeSubjectEnum.ENGINEER.value)) {
                        //安装服务人员
                        incomeExportDTO.setRegionInstallerIncome(productIncomeRecordPartDTO.getSubjectMoney());
                        incomeExportDTO.setEngineerStationName(productIncomeRecordPartDTO.getSettlementSubjectName());
                    }
                    if (StringUtil.isNotEmpty(productIncomeRecordPartDTO.getSubjectCode()) && productIncomeRecordPartDTO.getSubjectCode().equalsIgnoreCase(IncomeSubjectEnum.STATION_CONTRACTOR_PRODUCT.value)) {
                        //服务站承包人（产品收益）
                        incomeExportDTO.setStationContractorCompany(productIncomeRecordPartDTO.getSubjectMoney());
                        incomeExportDTO.setContractPhone(productIncomeRecordPartDTO.getSubjectPhone());
                        incomeExportDTO.setContractName(productIncomeRecordPartDTO.getSubjectName());
                        incomeExportDTO.setStationContractorIdCard(productIncomeRecordPartDTO.getSubjectIdCard());
                    }
                    if (StringUtil.isNotEmpty(productIncomeRecordPartDTO.getSubjectCode()) && productIncomeRecordPartDTO.getSubjectCode().equalsIgnoreCase(IncomeSubjectEnum.PROVINCE_COMPANY.value)) {
                        //省级公司
                        incomeExportDTO.setProvincialCompanyIncome(productIncomeRecordPartDTO.getSubjectMoney());
                    }
                    if (StringUtil.isNotEmpty(productIncomeRecordPartDTO.getSubjectCode()) && productIncomeRecordPartDTO.getSubjectCode().equalsIgnoreCase(IncomeSubjectEnum.CITY_COMPANY.value)) {
                        //市级公司
                        incomeExportDTO.setCityCompanyIncome(productIncomeRecordPartDTO.getSubjectMoney());
                    }
                    if (StringUtil.isNotEmpty(productIncomeRecordPartDTO.getSubjectCode()) && productIncomeRecordPartDTO.getSubjectCode().equalsIgnoreCase(IncomeSubjectEnum.ENGINEER_STATION_COMPANY.value)) {
                        //区县级公司（安装工）收益
                        incomeExportDTO.setEngineerStationCompanyIncome(productIncomeRecordPartDTO.getSubjectMoney());
                    }
                }
            }
            //经销商所属服务站公司
            if (StringUtil.isNotEmpty(incomeExportDTO.getDistributorProvince()) && StringUtil.isNotEmpty(incomeExportDTO.getDistributorCity()) && StringUtil.isNotEmpty(incomeExportDTO.getDistributorRegion())) {
                StationDTO station = systemFeign.getStationByPRC(incomeExportDTO.getDistributorProvince(), incomeExportDTO.getDistributorCity(), incomeExportDTO.getDistributorRegion(),PermissionTypeEnum.PRE_SALE.value);
                if (Objects.nonNull(station)) {
                    incomeExportDTO.setDistributorStationName(station.getName());
                    if (station.getContract() != null && station.getContract()) {
                        incomeExportDTO.setDistributorStationContract("是");
                        incomeExportDTO.setDistributorStationIncome("是");
                    } else {
                        incomeExportDTO.setDistributorStationContract("否");
                        incomeExportDTO.setDistributorStationIncome("否");
                    }
                    incomeExportDTO.setContractName(station.getContractor());
                    incomeExportDTO.setContractPhone(station.getContractorPhone());
                    incomeExportDTO.setContractIdCard(station.getContractorIdCard());
                }
            }

//            // 设置经销和推荐人信息
//            String subAccount = incomeExportDTO.getSubAccount();
//            if (subAccount != null) {
//                // 有子账号
//                incomeExportDTO.setHasSubAccount("是");
//            } else {
//                incomeExportDTO.setHasSubAccount("否");
//            }
//            String orderCompleteTime = incomeExportDTO.getOrderCompleteTime();
//            if (orderCompleteTime != null) {
//                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
//                Date dt = null;
//                try {
//                    dt = sdf.parse(orderCompleteTime);
//                } catch (ParseException e) {
//                    e.printStackTrace();
//                }
//                Calendar cal = Calendar.getInstance();
//                if (dt != null) {
//                    cal.setTime(dt);//设置起时间
//                    cal.add(Calendar.MONTH, 1);//增加一个月
//                    String format = sdf.format(cal.getTime());
//                    incomeExportDTO.setSettlementMonth(format);
//                }
//            }
        }
    }

    private ProductIncomeRecordPart selectDistributorOrRecommend(Integer recordId, String value) {
        Example example = new Example(ProductIncomeRecordPart.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("recordId", recordId);
        criteria.andEqualTo("subjectCode", value);
        return productIncomeRecordPartMapper.selectOneByExample(example);
    }

    @Override
    public Integer countSaleOrder(Integer userSaleId) {
        try {
            Integer count = productIncomeRecordPartMapper.countSaleOrder(userSaleId);
            return count != null ? count : 0;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return 0;
        }
    }

    /**
     * 将受益标记为已完成
     *
     * @param order 订单
     */
    @Override
    public void completeIncome(OrderSub order) {
        Long orderId = order.getId();
        Date completeTime = order.getCompleteTime();
        ProductIncomeRecord income = productIncomeRecordMapper.selectForComplete(orderId, IncomeType.PRODUCT_INCOME.value);
        //查出受益分配，如果受益分配状态是【0-未完成】，则将其状态设置为【1-(已完成)可结算】
        if (income != null) {
        	log.info("===========工单完成(订单号="+orderId+")更新收益数据=====income="+JSONObject.toJSONString(income));
            // income.setStatus(OrderCompleteStatusEnum.CAN_BE_SETTLED.value);
            income.setOrderCompleteTime(completeTime);
            if (completeTime != null) {
                //计算结算月份
                String settlementMonth = DateUtil.transferDateToString(DateUtil.monthAfter(completeTime, 1), "yyyy-MM");
                //获取经销商信息
                Integer distributorId = income.getDistributorId();
                DistributorDTO distributor = userFeign.getDistributorBasicInfoById(distributorId);
                //以下经销商身份可以在工单完成的时候进行收益可结算状态设置，其它身份的经销商收益暂不可结算
                if (distributor != null && distributor.getRoleLevel() != null && (distributor.getRoleLevel() == DistributorRoleLevel.D_350.value
                        || distributor.getRoleLevel() == DistributorRoleLevel.D_650.value
                        || distributor.getRoleLevel() == DistributorRoleLevel.D_950.value
                        || distributor.getRoleLevel() == DistributorRoleLevel.D_1000.value)) {
                    income.setStatus(OrderCompleteStatusEnum.CAN_BE_SETTLED.value);
                    income.setSettlementMonth(settlementMonth);
                }
                if (order.getProductType() == ProductModeEnum.LEASE.value) {
                    //安装工收益结算月份设值
                    income.setEngineerSettlementMonth(settlementMonth);
                }
            }
            productIncomeRecordMapper.updateByPrimaryKeySelective(income);
        }
    }

    @Override
    public ProductIncomeGrandTotalVO productIncomeGrandTotalforApp(Integer incomeType) {
        UserDTO user = userFeign.getBasicUserById(userCache.getUserId());

        ProductIncomeGrandTotalVO vo = new ProductIncomeGrandTotalVO();
        if (UserType.DISTRIBUTOR_1000.value == user.getUserType() || UserType.DISTRIBUTOR_DISCOUNT_50.value == user.getUserType()) {
            throw new BadRequestException("该用户类型没有收益查看权限！");
        }
        if (UserType.USER_3.value == user.getUserType() || UserType.USER_4.value == user.getUserType()) {
            vo.setTotal(new BigDecimal(0));
            vo.setUnfinishedTotal(new BigDecimal(0));
            vo.setFinishTotal(new BigDecimal(0));
            vo.setReturnTotal(new BigDecimal(0));
            //vo.setYesterdayTotal(new BigDecimal(0));
            vo.setTodayTotal(new BigDecimal(0));
            vo.setCurrentMonthTotal(new BigDecimal(0));
            return vo;
        }

        //收益汇总
        BigDecimal total = productIncomeRecordPartMapper.getProductIncomeGrandTotalforApp(user.getMid(), user.getId(), user.getUserType(), null, null, incomeType, null);
        //未完成订单收益汇总
        BigDecimal unfinishedTotal = productIncomeRecordPartMapper.getProductIncomeGrandTotalforApp(user.getMid(), user.getId(), user.getUserType(), 1, null, incomeType, null);
        //已完成订单收益汇总
        BigDecimal finishTotal = productIncomeRecordPartMapper.getProductIncomeGrandTotalforApp(user.getMid(), user.getId(), user.getUserType(), 2, null, incomeType, null);
        //退单单收益汇总
        BigDecimal returnTotal = productIncomeRecordPartMapper.getProductIncomeGrandTotalforApp(user.getMid(), user.getId(), user.getUserType(), 3, null, incomeType, null);
        //昨日订单收益汇总
        //BigDecimal yesterdayTotal = productIncomeRecordPartMapper.getProductIncomeGrandTotalforApp(user.getMid(), user.getId(), user.getUserType(), null, 1, incomeType, DateUtil.getBeforeEndDay());
        //本月订单收益汇总
        BigDecimal currentMonthTotal = productIncomeRecordPartMapper.getProductIncomeGrandTotalforApp(user.getMid(), user.getId(), user.getUserType(), null, 2, incomeType, new Date());

        //今日预估收益
        BigDecimal todayTotal = productIncomeRecordPartMapper.getProductIncomeGrandTotalforApp(user.getMid(), user.getId(), user.getUserType(), null, 1, incomeType, new Date());
        vo.setTotal(total);
        vo.setUnfinishedTotal(unfinishedTotal);
        vo.setFinishTotal(finishTotal);
        vo.setReturnTotal(returnTotal);
        //vo.setYesterdayTotal(yesterdayTotal);
        vo.setCurrentMonthTotal(currentMonthTotal);
        vo.setTodayTotal(todayTotal);
        return vo;
    }

    // app经销商收益概况(按日和按月)
    //type 1-按日 2-按月
    @Override
    public Map<String, Object> productIncomeReportOverviewforApp(Date dateTime, Integer type, Integer incomeType) {
        UserDTO user = userFeign.getBasicUserById(userCache.getUserId());
        Map<String, Object> listMap = new HashMap<>(8);
        if (dateTime == null) {
            throw new BadRequestException("日期必须选择！");
        }
        if (type == null) {
            throw new BadRequestException("类型必须选择！");
        }
        if (UserType.DISTRIBUTOR_1000.value == user.getUserType()) {
            throw new BadRequestException("用户类型没有收益查看权限！");
        }
        if (UserType.USER_3.value == user.getUserType() || UserType.USER_4.value == user.getUserType() || UserType.DISTRIBUTOR_DISCOUNT_50.value == user.getUserType()) {
            listMap.put("incomeList", new ArrayList<>());
            listMap.put("totalMoney", new BigDecimal(0));
            return listMap;
        }

        List<Map<String, Object>> list;
        if (incomeType == 1) {
            list = productIncomeRecordPartMapper.productIncomeReportOverviewforApp(user.getMid(), user.getId(), user.getUserType(), dateTime, type, incomeType);
        } else {
            list = productIncomeRecordPartMapper.renewIncomeReportOverviewforApp(user.getMid(), user.getId(), user.getUserType(), dateTime, type, incomeType);
        }
        if (list == null) {
            listMap.put("incomeList", new ArrayList<>());
        } else {
            listMap.put("incomeList", list);
        }

        BigDecimal totalMoney = productIncomeRecordPartMapper.productIncomeTotalMoney(user.getMid(), user.getId(), user.getUserType(), dateTime, type, incomeType);
        listMap.put("totalMoney", totalMoney);
        return listMap;
    }

    // app经销商收益金额明细列表
    @Override
    public Map<String, Object> productIncomeReportOverviewListforApp(Date createTime, Integer type, Integer incomeType, Integer pageNum, Integer pageSize) {
        UserDTO user = userFeign.getBasicUserById(userCache.getUserId());
        Integer userId;
        Map<String, Object> listMap = new HashMap<>(8);
        listMap.put("incomeList", new ArrayList<>());
        listMap.put("incomeTotal", 0);
        listMap.put("incomeCount", 0);
        listMap.put("pages", 0);

        if (UserType.DISTRIBUTOR_1000.value == user.getUserType()) {
            throw new BadRequestException("用户类型没有收益查看权限！");
        }
        if (UserType.USER_3.value == user.getUserType() || UserType.USER_4.value == user.getUserType() || UserType.DISTRIBUTOR_DISCOUNT_50.value == user.getUserType()) {
            return listMap;
        }

        PageHelper.startPage(pageNum, pageSize);
        Page<Map<String, Object>> page = productIncomeRecordPartMapper.productIncomeListForApp(user.getMid(), null, null, user.getId(), user.getUserType(), null, null, null, null, null, type, incomeType, createTime);
        if (page == null) {
            return listMap;
        }
        Date startTime = null;
        for (Map<String, Object> objectMap : page.getResult()) {
            userId = (Integer) objectMap.get("userId");
            UserDTO dto = userFeign.getBasicUserById(userId);
            objectMap.put("nickName", dto.getNickName());
            objectMap.put("headImg", dto.getHeadImg());

            //根据时间查询天总金额
            startTime = DateUtil.transferStringToDate(objectMap.get("createTime").toString());
            BigDecimal incomeMoney = productIncomeRecordPartMapper.getProductIncomeGrandTotalforApp(user.getMid(), user.getId(), user.getUserType(), null, 1, incomeType, startTime);
            objectMap.put("incomeMoney", incomeMoney);

            //查询续费方式计费金额
            Integer costId = (Integer) objectMap.get("costId");
            if (null != costId) {
                ProductCostDTO cost = productFeign.getProductCostById(costId);
                if (null != cost) {
                    objectMap.put("costMoney", cost.getRentalFee());
                }
            }

        }

        //BigDecimal total = productIncomeRecordPartMapper.getProductIncomeGrandTotalforApp(user.getMid(), user.getId(), user.getUserType(), null, type, incomeType, createTime);
        listMap.put("incomeList", page.getResult());
        //listMap.put("incomeTotal", total);
        listMap.put("incomeCount", page.getTotal());
        listMap.put("pages", page.getPages());
        return listMap;
    }

    //app经销商收益金额明细列表（经销商所有收益列表）
    @Override
    public Map<String, Object> productIncomeListForApp(Integer terminal, Integer productFirstCategoryId, Integer distributorId, Integer queryType, Integer status, Integer timeType, Integer incomeType, Date beginDate, Date endDate, Integer pageNum, Integer pageSize) {
        Map<String, Object> map = new HashMap<>();
        UserDTO user = userFeign.getBasicUserById(userCache.getUserId());

        if (UserType.USER_3.value == user.getUserType() || UserType.USER_4.value == user.getUserType() || UserType.DISTRIBUTOR_DISCOUNT_50.value == user.getUserType()) {
            map.put("incomeList", new ArrayList<>());
            map.put("incomeCount", 0);

            return map;
        }
        if (UserType.DISTRIBUTOR_1000.value == user.getUserType()) {
            throw new BadRequestException("用户类型没有收益查看权限！");
        }

        //一级分类转换，通过前端分类拿到对应的后台一级分类
        if (productFirstCategoryId != null) {
            List<ProductDTO> list = productFeign.getProductByFrontId(productFirstCategoryId);
            if (list.isEmpty()) {
                throw new BadRequestException("一级分类传参有误，该分类无对应产品！");
            }
            Integer id = list.get(0).getId();
            ProductDTO product = productFeign.getProductById(id);
            if (Objects.isNull(product)) {
                throw new BadRequestException("系统数据有误，该分类无对应产品！");
            }
            ProductCategoryDTO oneCategory = productFeign.getOneProductCategory(product.getCategoryId());
            if (Objects.isNull(oneCategory)) {
                throw new BadRequestException("一级分类传参有误，无对应分类！");
            }
            productFirstCategoryId = oneCategory.getId();
        }

        Date startTime = null;
        Date endTime = null;
        Date currentTime = new Date();
        //下单时间类型校验
        if (Objects.nonNull(beginDate) || Objects.nonNull(endDate)) {
            timeType = 7;
        }
        switch (timeType) {
            case 1:
                startTime = DateUtil.dayAfter(currentTime, -3);
                endTime = currentTime;
                break;
            case 2:
                startTime = DateUtil.dayAfter(currentTime, -7);
                endTime = currentTime;
                break;
            case 3:
                startTime = DateUtil.dayAfter(currentTime, -30);
                endTime = currentTime;
                break;
            case 4:
                startTime = DateUtil.monthAfter(currentTime, -3);
                endTime = currentTime;
                break;
            case 5:
                startTime = DateUtil.getCurrentYearBeginTime();
                endTime = currentTime;
                break;
            case 6:
                //获取上一年的开始结束日期时间
                Calendar calendar = Calendar.getInstance();
                calendar.add(Calendar.YEAR, -1);
                calendar.set(calendar.get(Calendar.YEAR), 0, 1, 0, 0, 0);
                startTime = calendar.getTime();
                calendar.set(calendar.get(Calendar.YEAR), 11, 31, 23, 59, 59);
                endTime = calendar.getTime();
                break;
            case 7:
                startTime = beginDate;
                endTime = endDate;
                break;
            default:
                break;
        }

        Page<Map<String, Object>> page;
        PageHelper.startPage(pageNum, pageSize);
        if (UserType.USER_7.value == user.getUserType()) {
            //用户
            page = productIncomeRecordPartMapper.productIncomeListForApp(null, null, null, user.getId(), user.getUserType(), terminal, productFirstCategoryId, status, startTime, endTime, null, incomeType, null);
            for (Map<String, Object> objectMap : page.getResult()) {
                //根据时间查询天总金额
                startTime = DateUtil.transferStringToDate(objectMap.get("createTime").toString());
                objectMap.put("incomeMoney", productIncomeRecordPartMapper.incomeMoneyForDate(null, null, user.getId(), user.getUserType(), terminal, productFirstCategoryId, status, null, null, 1, incomeType, startTime));
                //设置下单用户昵称
                setNickNameData(objectMap);
                //查询续费方式计费金额
                Integer costId = (Integer) objectMap.get("costId");
                if (null != costId) {
                    ProductCostDTO cost = productFeign.getProductCostById(costId);
                    if (null != cost) {
                        objectMap.put("costMoney", cost.getRentalFee());
                    }
                }
            }
        } else {
            //经销商
            Integer mainId;
            Integer sonId = null;
            // 主账号 或者 子账号查询
            DistributorDTO distributor = userFeign.getDistributorBasicInfoById(distributorId);
            if (distributor == null) {
                throw new BadRequestException("经销商不存在！");
            }
            Integer roleLevel = distributor.getRoleLevel();
            if (roleLevel == null) {
                throw new BadRequestException("经销商角色有误！");
            }
            if (roleLevel == DistributorRoleLevel.D_1000.value) {
                // 子账号 则获取主账号信息
                DistributorDTO main = userFeign.getMainAccountByDistributorId(distributorId);
                mainId = main.getId();
                sonId = distributorId;
            } else {
                // 非子账号
                mainId = distributorId;
            }

            page = productIncomeRecordPartMapper.productIncomeListForApp(mainId, sonId, queryType, user.getId(), user.getUserType(), terminal, productFirstCategoryId, status, startTime, endTime, null, incomeType, null);
            for (Map<String, Object> objectMap : page.getResult()) {
                //根据时间查询天总金额
                startTime = DateUtil.transferStringToDate(objectMap.get("createTime").toString());
                objectMap.put("incomeMoney", productIncomeRecordPartMapper.incomeMoneyForDate(mainId, sonId, user.getId(), user.getUserType(), terminal, productFirstCategoryId, status, null, null, 1, incomeType, startTime));
                //设置下单用户昵称
                setNickNameData(objectMap);

                //查询续费方式计费金额
                Integer costId = (Integer) objectMap.get("costId");
                if (null != costId) {
                    ProductCostDTO cost = productFeign.getProductCostById(costId);
                    if (null != cost) {
                        objectMap.put("costMoney", cost.getRentalFee());
                    }
                }
            }
        }

        map.put("incomeList", new PageVO<>(pageNum, page));
        map.put("incomeCount", page.getTotal());
        return map;
    }

    //将返回结果赋值用户昵称信息
    private void setNickNameData(Map<String, Object> map) {
        try {
            if (!map.isEmpty() && map.containsKey("userId")) {
                //查询用户的昵称
                Integer userId = (Integer) map.get("userId");
                if (null != userId) {
                    UserDTO userinfo = userFeign.getBasicUserById(userId);
                    if (null != userinfo) {
                        map.put("nickName", userinfo.getNickName());
                    }
                }
            }
        } catch (Exception e) {
            log.error("================查询用户昵称异常(" + map + ")=====信息：" + e.getMessage());
        }

    }

    //app经销商收益收益详情
    @Override
    public Map<String, Object> productIncomeDetailForApp(Integer id, Integer incomeType) {
        //登录人用户
        UserDTO user = userFeign.getBasicUserById(userCache.getUserId());
        if (id == null) {
            throw new BadRequestException("id不能为空！");
        }
        if (UserType.DISTRIBUTOR_1000.value == user.getUserType()) {
            throw new BadRequestException("用户类型没有收益查看权限！");
        }
        Map<String, Object> map;
        //1-产品收益 2-续费收益
        if (incomeType == 1) {
            map = productIncomeRecordPartMapper.productIncomeDetailForApp(id, user.getMid(), user.getId(), user.getUserType());
        } else {
            map = productIncomeRecordPartMapper.renewIncomeDetailForApp(id, user.getMid(), user.getId(), user.getUserType());
        }

        if (map == null) {
            throw new BadRequestException("收益不存在！");
        }

        //设置下单用户昵称
        setNickNameData(map);

        //会员用户
        Integer vipUserId = (Integer) map.get("vipUserId");
        if (Objects.nonNull(vipUserId)) {
            //查询会员用户的收益金额
            Integer recordId = (Integer) map.get("recordId");
            if (Objects.nonNull(recordId)) {
                map.put("vipUserMoney", productIncomeRecordPartMapper.vipUserIncomeMoneyByPartId(vipUserId, recordId) != null ? productIncomeRecordPartMapper.vipUserIncomeMoneyByPartId(vipUserId, recordId) : 0);
            }
            //查询会员用户的昵称
            UserDTO userTemp = userFeign.getBasicUserById(vipUserId);
            if (Objects.nonNull(userTemp)) {
                map.put("vipNickName", userTemp.getNickName());
                map.put("vipHeadImg", userTemp.getHeadImg());
            }
        }
        return map;
    }

    @Override
    public void setSettlementIncomeStatus(Integer id, Integer status) {
        if (id == null) {
            throw new BadRequestException("id不能为空！");
        }
        if (status == null) {
            throw new BadRequestException("结算状态值不能为空！");
        }
        if (status != OrderCompleteStatusEnum.CAN_BE_SETTLED.value && status != OrderCompleteStatusEnum.STOP_SETTLED.value) {
            throw new BadRequestException("结算状态值不合法！");
        }
        ProductIncomeRecord record = productIncomeRecordMapper.selectByPrimaryKey(id);
        if (record == null) {
            throw new YimaoException("收益不存在！");
        }

        // 0-未完成 1-(已完成)可结算 2-已结算 3-已失效，4-暂停结算
        //暂停结算:  订单完成时间必须为本月。才能暂停结算
        if (status == OrderCompleteStatusEnum.STOP_SETTLED.value) {
            if (record.getOrderCompleteTime() == null) {
                throw new YimaoException("订单未完成，不能暂停结算！");
            }
            if (!Objects.equals(DateUtil.getCurrentMonth(), DateUtil.getTimeField(record.getOrderCompleteTime(), Calendar.MONDAY))) {
                throw new YimaoException("该订单不在当月，不可暂停结算！");
            }
        }
        if (record.getStatus() == null) {
            throw new YimaoException("当前结算状态不可更改，不可暂停结算/恢复结算！");
        }
        if (record.getStatus() != OrderCompleteStatusEnum.CAN_BE_SETTLED.value && record.getStatus() != OrderCompleteStatusEnum.STOP_SETTLED.value) {
            throw new YimaoException("当前结算状态不可更改！");
        }

        record.setStatus(status);
        String settlementMonth = null;
        if (status == OrderCompleteStatusEnum.CAN_BE_SETTLED.value) {
            //计算结算月份
            //注意：当选择恢复可结算，标记时间为当前时间（结算日期在当前日期上加一个月）
            settlementMonth = DateUtil.transferDateToString(DateUtil.monthAfter(new Date(), 1), "yyyy-MM");
        }
        productIncomeRecordMapper.updateStatusAndSettlementMonth(id, record.getStatus(), settlementMonth);
    }


    @Override
    public List<ProductIncomeRecordDTO> getProductIncomeRecord(Integer id) {
        Example example = new Example(ProductIncomeRecord.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("distributorId", id);
        List<ProductIncomeRecord> productIncomeRecords = productIncomeRecordMapper.selectByExample(example);
        return CopyUtil.copyList(productIncomeRecords, ProductIncomeRecord.class, ProductIncomeRecordDTO.class);
    }

    @Override
    public List<SalesProductDTO> getSaleProductListById(Integer id) {
        return productIncomeRecordMapper.getSaleProductListById(id);
    }

    /**
     * 安装工操作更换水机型号，需要进行收益重分配时执行此方法
     */
    @Override
    public void editProductIncomeRecordFee(WorkOrder workOrder, ProductDTO product, ProductCostDTO newCost) {
        //根据订单号查询当前收益分配记录
        ProductIncomeRecord productIncomeRecord = productIncomeRecordMapper.selectProductIncomeRecordByOrderId(workOrder.getSubOrderId(), IncomeType.PRODUCT_INCOME.value);
        if (Objects.isNull(productIncomeRecord)) {
            log.info("该订单产品收益分配纪录不存在，subOrderId={}", workOrder.getSubOrderId());
            return;
        }
        //获取更改的产品id
        Integer changeProductId = workOrder.getProductId();
        //获取该产品的收益id
        Set<Integer> incomeRuleIds = productIncomeRuleService.listIncomeRuleIdByProductId(changeProductId);
        IncomeRuleDTO incomeRule = incomeRuleService.getIncomeRuleByIdAndIncomeType(incomeRuleIds, IncomeType.PRODUCT_INCOME.value);
        if (incomeRule == null) {
            throw new YimaoException("产品销售收益分配失败-没有查询到产品的收益分配规则。");
        }
        if (!Objects.equals(incomeRule.getId(), productIncomeRecord.getIncomeRuleId())) {
            throw new YimaoException("产品销售收益分配失败-原收益分配规则与更改产品收益分配规则不一致");
        }
        //若收益分配按金额收费则无需修改
        if (incomeRule.getAllotType() == IncomeAllotType.MONEY.value) {
            return;
        } else if (incomeRule.getAllotType() == IncomeAllotType.RATIO.value) {
            ProductIncomeRecord updateEntity = this.editProductIncomeRecord(product);
            updateEntity.setId(productIncomeRecord.getId());
            updateEntity.setSettlementFee(newCost.getRentalFee());
            updateEntity.setOrderFee(newCost.getTotalFee());
            int res = productIncomeRecordMapper.updateByPrimaryKeySelective(updateEntity);
            if (res >= 1) {
                //更改后的可分配收益金额
                BigDecimal balanceFee = newCost.getRentalFee();
                List<ProductIncomeRecordPartDTO> partList = productIncomeRecordPartMapper.getPartByProductIncomeRecordId(productIncomeRecord.getId());
                for (ProductIncomeRecordPartDTO part : partList) {
                    ProductIncomeRecordPart recordPart = new ProductIncomeRecordPart();
                    recordPart.setId(part.getId());
                    //计算收益金额
                    recordPart.setSubjectMoney(balanceFee.multiply(part.getSubjectRatio().divide(new BigDecimal(100))));
                    productIncomeRecordPartMapper.updateByPrimaryKeySelective(recordPart);
                }
            } else {
                throw new YimaoException("产品销售收益分配失败-未更新产品的收益分配纪录成功。");
            }
        }
    }

    /**
     * 安装工操作更换水机型号，需要同步修改收益分配数据上的产品信息
     */
    @Override
    public void editProductIncomeRecordProductInfo(WorkOrder workOrder, ProductDTO product) {
        //根据订单号查询当前收益分配记录
        ProductIncomeRecord productIncomeRecord = productIncomeRecordMapper.selectProductIncomeRecordByOrderId(workOrder.getSubOrderId(), IncomeType.PRODUCT_INCOME.value);
        if (Objects.isNull(productIncomeRecord)) {
            log.info("该订单产品收益分配纪录不存在，subOrderId={}", workOrder.getSubOrderId());
            return;
        }
        ProductIncomeRecord updateEntity = this.editProductIncomeRecord(product);
        updateEntity.setId(productIncomeRecord.getId());
        productIncomeRecordMapper.updateByPrimaryKeySelective(updateEntity);
    }

    private ProductIncomeRecord editProductIncomeRecord(ProductDTO product) {
        //更改产品收益的金额及产品类型
        ProductIncomeRecord updateEntity = new ProductIncomeRecord();
        updateEntity.setProductId(product.getId());
        updateEntity.setProductName(product.getName());
        updateEntity.setProductCategoryId(product.getCategoryId());
        updateEntity.setProductCategoryName(product.getCategoryName());
        ProductCategoryDTO oneCategory = productFeign.getOneProductCategory(product.getCategoryId());
        if (Objects.nonNull(oneCategory)) {
            updateEntity.setProductOneCategoryId(oneCategory.getId());
            updateEntity.setProductOneCategoryName(oneCategory.getName());
        }
        ProductCategoryDTO twoCategory = productFeign.getTwoProductCategory(product.getCategoryId());
        if (Objects.nonNull(twoCategory)) {
            updateEntity.setProductTwoCategoryId(twoCategory.getId());
            updateEntity.setProductTwoCategoryName(twoCategory.getName());
        }
        return updateEntity;
    }


    @Override
    public List<IncomeStatsResultDTO> productIncomeStats(IncomeStatsQueryDTO query) {
        UserDTO user = userFeign.getBasicUserById(userCache.getUserId());
        checkParams(query, user);
        if (UserType.USER_3.value == user.getUserType() || UserType.USER_4.value == user.getUserType() || UserType.DISTRIBUTOR_DISCOUNT_50.value == user.getUserType()) {
            return new ArrayList<IncomeStatsResultDTO>();
        }
        query.setUserId(user.getId());
        query.setDistributorId(user.getMid());
        query.setUserType(user.getUserType());
        log.info("===收益统计请求参数======" + JSONObject.toJSONString(query));
        List<IncomeStatsResultDTO> data = productIncomeRecordPartMapper.productIncomeStatInfo(query);
        //补全数据
        completeData(data, query);
        log.info("===收益统计返回数据======" + JSONObject.toJSONString(data));
        return data;
    }

    /***
     * 补全收益数据
     * @param data
     * @param query
     */
    private void completeData(List<IncomeStatsResultDTO> data, IncomeStatsQueryDTO query) {
        calculateDate(query);
        if (query.getType() == 1 || query.getType() == 2) {
            for (String date : query.getDates()) {
                if (notExists(date, data, query.getType())) {
                    IncomeStatsResultDTO statsRs = new IncomeStatsResultDTO();
                    statsRs.setCompleteTime(date);
                    statsRs.setAmount(BigDecimal.ZERO.setScale(2));
                    data.add(statsRs);
                }
            }

            //数据排序
            Collections.sort(data, new Comparator<IncomeStatsResultDTO>() {
                @Override
                public int compare(IncomeStatsResultDTO u1, IncomeStatsResultDTO u2) {
                    long diff = DateUtil.transferStringToDate(query.getType() == 2 ? u1.getCompleteTime() + "-01" : u1.getCompleteTime()).getTime() - DateUtil.transferStringToDate(query.getType() == 2 ? u2.getCompleteTime() + "-01" : u2.getCompleteTime()).getTime();
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
     * 校验数据是否存在
     * @param date
     * @param data
     * @return
     */
    private boolean notExists(String date, List<IncomeStatsResultDTO> data, Integer type) {
        if (data.isEmpty()) {
            return true;
        }
        boolean flag = true;
        for (IncomeStatsResultDTO stats : data) {
            if (!StringUtil.isEmpty(stats.getCompleteTime()) && stats.getCompleteTime().equals(date)) {
                return false;
            } else {
                flag = true;
            }
        }
        return flag;
    }


    /***
     * 计算日期集合
     * @param query
     */
    private void calculateDate(IncomeStatsQueryDTO query) {
        List<String> dates = new ArrayList<String>();
        dates.add(query.getCompleteTime());
        if (query.getType() == 1) {
            //按日统计
            for (int i = 1; i < 30; i++) {
                dates.add(DateUtil.getChangeDayByDate(DateUtil.transferStringToDate(query.getCompleteTime()), -i));
            }
        } else if (query.getType() == 2) {
            //按月统计
            String time = query.getCompleteTime() + "-01";
            for (int i = 1; i < 12; i++) {
                dates.add(DateUtil.getChangeMonthByDate(DateUtil.transferStringToDate(time), -i));
            }
        }
        query.setDates(dates);

    }


    /***
     * 收益统计查询参数校验
     * @param query
     * @param user
     */
    private void checkParams(IncomeStatsQueryDTO query, UserDTO user) {
        if (null == user) {
            throw new YimaoException("用户信息不能为空");
        }
        if (null == query) {
            throw new YimaoException("请求参数不能为空");
        }
        if (null == query.getIncomeType()) {
            throw new YimaoException("收益类型不能为空");
        }
        if (null == query.getType()) {
            throw new YimaoException("收益日期类型不能为空");
        }
        if (StringUtil.isEmpty(query.getCompleteTime())) {
            throw new YimaoException("完成日期不能为空");
        }

    }

}
