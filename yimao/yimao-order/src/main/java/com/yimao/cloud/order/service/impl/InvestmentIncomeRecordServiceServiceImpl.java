package com.yimao.cloud.order.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.yimao.cloud.base.enums.*;
import com.yimao.cloud.base.exception.BadRequestException;
import com.yimao.cloud.base.exception.YimaoException;
import com.yimao.cloud.base.mail.MailSender;
import com.yimao.cloud.base.properties.DomainProperties;
import com.yimao.cloud.base.utils.CollectionUtil;
import com.yimao.cloud.base.utils.DateUtil;
import com.yimao.cloud.base.utils.StringUtil;
import com.yimao.cloud.pojo.query.order.InvestmentIncomeQueryDTO;
import com.yimao.cloud.order.feign.SystemFeign;
import com.yimao.cloud.order.feign.UserFeign;
import com.yimao.cloud.order.mapper.IncomeSubjectMapper;
import com.yimao.cloud.order.mapper.InvestmentIncomeRecordMapper;
import com.yimao.cloud.order.mapper.InvestmentIncomeRecordPartMapper;
import com.yimao.cloud.order.po.IncomeSubject;
import com.yimao.cloud.order.po.InvestmentIncomeRecord;
import com.yimao.cloud.order.po.InvestmentIncomeRecordPart;
import com.yimao.cloud.order.service.IncomeRuleService;
import com.yimao.cloud.order.service.InvestmentIncomeRecordService;
import com.yimao.cloud.pojo.dto.order.IncomeRecordPartResultDTO;
import com.yimao.cloud.pojo.dto.order.IncomeRecordResultDTO;
import com.yimao.cloud.pojo.dto.order.IncomeRuleDTO;
import com.yimao.cloud.pojo.dto.order.IncomeRulePartDTO;
import com.yimao.cloud.pojo.dto.system.StationCompanyDTO;
import com.yimao.cloud.pojo.dto.user.DistributorDTO;
import com.yimao.cloud.pojo.dto.user.DistributorOrderDTO;
import com.yimao.cloud.pojo.dto.user.UserDTO;
import com.yimao.cloud.pojo.vo.PageVO;
import com.yimao.cloud.pojo.vo.order.InvestmentIncomeVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.util.*;

/**
 * 招商收益
 *
 * @author Liu Yi
 * @date 2019/1/129
 */
@Service
@Slf4j
public class InvestmentIncomeRecordServiceServiceImpl implements InvestmentIncomeRecordService {
    @Resource
    private DomainProperties domainProperties;
    @Resource
    private IncomeSubjectMapper incomeSubjectMapper;
    @Resource
    private InvestmentIncomeRecordMapper investmentIncomeRecordMapper;
    @Resource
    private InvestmentIncomeRecordPartMapper investmentIncomeRecordPartMapper;
    @Resource
    private MailSender mailSender;
    @Resource
    private UserFeign userFeign;
    @Resource
    private IncomeRuleService incomeRuleService;
    @Resource
    private SystemFeign systemFeign;

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
    @Override
    public void investmentIncomeAllot(DistributorOrderDTO distributorOrderDTO) {
        if (distributorOrderDTO == null) {
            throw new BadRequestException("经销商订单实体类不能为空");
        }

        try {
            log.info("订单号：{} 开始分配收益", distributorOrderDTO.getId());
            DistributorDTO distributorDTO = distributorOrderDTO.getDistributor();
            if (Objects.isNull(distributorDTO)) {
                throw new BadRequestException("经销商不存在,订单id：" + distributorOrderDTO.getId());
            }
            UserDTO userDTO = distributorOrderDTO.getUser();
            if (Objects.isNull(userDTO)) {
                throw new BadRequestException("经销商收益分配失败-用户不存在，订单id：" + distributorOrderDTO.getId());
            }
            //2-获取收益分配规则
            IncomeRuleDTO incomeRule = incomeRuleService.getIncomeRuleByIdAndIncomeType(null, IncomeType.INVESTMENT_INCOME.value);
            if (Objects.isNull(incomeRule)) {
                throw new BadRequestException("经销商收益分配失败-没有查询到经销商订单的收益分配规则，订单id：" + distributorOrderDTO.getId());
            }

            //生成收益结算信息
            InvestmentIncomeRecord record = new InvestmentIncomeRecord();
            Map<String, IncomeRulePartDTO> rulePartMap = new HashMap<>();
            List<IncomeRulePartDTO> rulePartList = incomeRule.getIncomeRuleParts();
            for (IncomeRulePartDTO rulePart : rulePartList) {
                rulePartMap.put(rulePart.getSubjectCode(), rulePart);
            }
            //获取收益分配规则详细
            List<InvestmentIncomeRecordPart> recordPartList = new ArrayList<>();
            //1-翼猫总部
            IncomeRulePartDTO rulePart = rulePartMap.get(IncomeSubjectEnum.MAIN_COMPANY.value);

            BigDecimal balanceFee = distributorOrderDTO.getPrice();
            if (balanceFee == null) {
                balanceFee = new BigDecimal(0);
            }
            record.setRealPayment(balanceFee);//实付款
            BigDecimal mainCompanyFee;
            if (incomeRule.getAllotType() == IncomeAllotType.RATIO.value) {//收益比例
                mainCompanyFee = balanceFee.multiply(rulePart.getValue().divide(new BigDecimal(100)));
            } else {
                mainCompanyFee = rulePart.getValue(); //收益金额
            }
            record.setReceivableMoney(mainCompanyFee);//应收款（总部收款）
            //可分配收益金额=订单金额-开户费用-运费
            balanceFee = balanceFee.subtract(mainCompanyFee);
            record.setMoreMoney(balanceFee);//多收款 =总额-总部款项

            if (distributorOrderDTO.getOrderType() == 1) {//升级
                record.setDestDistributorLevel(record.getDestDistributorLevel());
            }

            //收益状态设置
            if (DistributorOrderStateEnum.COMPLETED.value == distributorOrderDTO.getOrderState()) {
                record.setOrderCompleteStatus(1);//订单完成状态 0-未完成 1-(已完成)可结算 2-已结算 3-已失效
                record.setOrderCompleteTime(new Date());

                record.setSettlementTime(DateUtil.monthAfter(new Date(), 1));//结算时间在下一个月
            } else {
                //标记为冻结收益，等对应的订单完成时再讲状态标记为可计算或可体现
                record.setOrderCompleteStatus(0);//订单完成状态 0-未完成 1-(已完成)可结算 2-已结算 3-已失效
            }

            record.setOrderId(distributorOrderDTO.getId());
            record.setUserId(userDTO.getId());
            record.setUserName(userDTO.getUserName());
            record.setUserPhone(userDTO.getMobile());
            record.setDistributorOrderType(distributorOrderDTO.getOrderType());
            record.setIncomeRuleId(incomeRule.getId());
            record.setDistributorId(distributorDTO.getId());
            record.setDistributorLevel(distributorOrderDTO.getRoleLevel());
            if (distributorOrderDTO.getOrderType() == DistributorOrderType.UPGRADE.value) {
                //升级订单有升级经销商类型
                record.setDestDistributorLevel(distributorOrderDTO.getDestRoleLevel());
            }
            record.setAllotType(incomeRule.getAllotType());
            record.setOrderCompleteStatus(distributorOrderDTO.getOrderState());
            record.setOrderCompleteTime(distributorOrderDTO.getCompletionTime());
            record.setDistributorName(distributorDTO.getRealName());
            record.setDistributorAccount(distributorDTO.getUserName());
            record.setProvince(distributorDTO.getProvince());
            record.setCity(distributorDTO.getCity());
            record.setRegion(distributorDTO.getRegion());
            record.setPayType(distributorOrderDTO.getPayType());
            record.setPayTime(distributorOrderDTO.getPayTime());
            record.setTradeNo(distributorOrderDTO.getTradeNo());

            //获取相应经销商订单的经销商的推荐人信息
            DistributorDTO recommendDistributor = distributorOrderDTO.getRecommendDistributor();

            if (Objects.nonNull(recommendDistributor)) {
                //获取推荐人所属区域对应的服务站公司
            	String province=recommendDistributor.getProvince();
            	String city=recommendDistributor.getCity();
            	String region=recommendDistributor.getRegion();
                StationCompanyDTO stationCompany = systemFeign.getStationCompanyByPCR(province,	city, region, StationAreaServiceTypeEnum.PRE_SALE.value);
                if (stationCompany != null) {
                    //付款主体 （相应经销商订单的经销商的推荐人所属区域对应的服务站公司(推荐人所在的区县级公司)-----招商收益总部待打款的对象）
                    record.setPaySubject(stationCompany.getName());
                }
                record.setRefereeName(recommendDistributor.getRealName());
                record.setRefereeAccount(recommendDistributor.getUserName());
            }
            record.setCreateTime(new Date());
            //record.setCreator();
            record.setUpdateTime(new Date());

            investmentIncomeRecordMapper.insert(record);

            //1-翼猫总部
            if (Objects.nonNull(rulePart)) {
                InvestmentIncomeRecordPart recordPart = this.createRecordPart(record, incomeRule, rulePart);
                //收益主体信息
                recordPart.setSubjectPhone("4001519999");
                recordPart.setSubjectProvince("上海市");
                recordPart.setSubjectCity("上海市");
                recordPart.setSubjectRegion("嘉定区");
                recordPartList.add(recordPart);
            }

            //2-智慧助理
            rulePart = rulePartMap.get(IncomeSubjectEnum.ASSISTANT.value);
            if (Objects.nonNull(rulePart)) {
                InvestmentIncomeRecordPart recordPart = this.createRecordPart(record, incomeRule, rulePart);
                //TODO 智慧助理本版本不做
                //recordPart.setSubjectId();
                recordPart.setSubjectName("智慧助理");
                //recordPart.setSubjectPhone();
                recordPart.setSubjectProvince(distributorDTO.getProvince());
                recordPart.setSubjectCity(distributorDTO.getCity());
                recordPart.setSubjectRegion(distributorDTO.getRegion());
                //结算主体为经销商所属区县级公司
                /*recordPart.setSettlementSubjectId();
                recordPart.setSettlementSubjectName();*/
                recordPartList.add(recordPart);
            }

            //3-经销商推荐人所在区县级公司
            rulePart = rulePartMap.get(IncomeSubjectEnum.RECOMMEND_STATION_COMPANY.value);
            if (Objects.nonNull(rulePart)) {
                InvestmentIncomeRecordPart recordPart = this.createRecordPart(record, incomeRule, rulePart);
                if (Objects.nonNull(recommendDistributor)) {
                    String province = recommendDistributor.getProvince();
                    String city = recommendDistributor.getCity();
                    String region = recommendDistributor.getRegion();
                    //根据经销商推荐人所在省市区查询其所属的区县级公司
                    StationCompanyDTO stationCompany = systemFeign.getStationCompanyByPCR(province, city, region,StationAreaServiceTypeEnum.PRE_SALE.value);
                    if (stationCompany != null) {
                        //收益主体为经销商推荐人所属区县级公司
                        recordPart.setSubjectId(stationCompany.getId());
                        recordPart.setSubjectName(stationCompany.getName());
                        recordPart.setSubjectPhone(stationCompany.getContactPhone());
                        recordPart.setSubjectProvince(stationCompany.getProvince());
                        recordPart.setSubjectCity(stationCompany.getCity());
                        recordPart.setSubjectRegion(stationCompany.getRegion());
                        //结算主体为经销商推荐人所属区县级公司
                        recordPart.setSettlementSubjectId(stationCompany.getId());
                        recordPart.setSettlementSubjectName(stationCompany.getName());
                    }
                }
                recordPartList.add(recordPart);
            }

            //4-区县级股东（推荐人）
            rulePart = rulePartMap.get(IncomeSubjectEnum.REGION_SHAREHOLDER.value);
            if (Objects.nonNull(rulePart)) {
                InvestmentIncomeRecordPart recordPart = this.createRecordPart(record, incomeRule, rulePart);
                if (Objects.nonNull(recommendDistributor)) {
                    //收益主体为推荐人所属区县级公司
                    recordPart.setSubjectId(recommendDistributor.getId());
                    recordPart.setSubjectIdCard(recommendDistributor.getIdCard());
                    recordPart.setSubjectName(recommendDistributor.getRealName());
                    recordPart.setSubjectPhone(recommendDistributor.getPhone());
                    recordPart.setSubjectProvince(recommendDistributor.getProvince());
                    recordPart.setSubjectCity(recommendDistributor.getCity());
                    recordPart.setSubjectRegion(recommendDistributor.getRegion());
                    recordPart.setUserId(recommendDistributor.getUserId());
                    recordPart.setRealName(recommendDistributor.getRealName());
                    recordPart.setUserName(recommendDistributor.getUserName());
                    String province = recommendDistributor.getProvince();
                    String city = recommendDistributor.getCity();
                    String region = recommendDistributor.getRegion();
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

            // todo 分配有问题 先让他往下走
            if (CollectionUtil.isNotEmpty(recordPartList)) {
                investmentIncomeRecordPartMapper.insertBatch(recordPartList);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw, true));
            String subject = "招商收益分配失败提醒" + domainProperties.getApi();
            String content = "招商收益分配时出错。orderId=" + distributorOrderDTO.getId() + "\n" + sw.toString();
            mailSender.send(null, subject, content);
            throw new YimaoException(e.getMessage(), e);
        }
    }

    /**
     * 创建收益分配记录（明细）
     *
     * @param record   收益分配记录
     * @param rule     收益分配规则
     * @param rulePart 收益分配规则
     */
    private InvestmentIncomeRecordPart createRecordPart(InvestmentIncomeRecord record, IncomeRuleDTO rule, IncomeRulePartDTO rulePart) {
        InvestmentIncomeRecordPart recordPart = new InvestmentIncomeRecordPart();
        //收益ID
        recordPart.setIncomeRecordId(record.getId());
        //获取收益主体
        Integer subjectId = rulePart.getSubjectId();
        IncomeSubject subject = incomeSubjectMapper.selectByPrimaryKey(subjectId);
        if (rule.getAllotType() == IncomeAllotType.RATIO.value) {
            //收益比例
            recordPart.setSubjectRatio(rulePart.getValue());
            //收益金额
            recordPart.setSubjectMoney(record.getRealPayment().multiply(rulePart.getValue().divide(new BigDecimal(100))));
        } else {
            //收益金额
            recordPart.setSubjectMoney(rulePart.getValue());
        }
        /*recordPart.setSubjectId(subjectId);*/
        recordPart.setSubjectCode(subject.getIncomeSubjectCode());
        recordPart.setSubjectName(subject.getIncomeSubjectName());
        recordPart.setSettlementSubjectCode(subject.getSettlementSubjectCode());
        recordPart.setSettlementSubjectName(subject.getSettlementSubjectName());
        recordPart.setDistributorType(2);
        recordPart.setAuditStatus(3);
        //recordPart.setSettlementTime(record.getCreateTime());
        recordPart.setCreateTime(record.getCreateTime());
        // 设置是否提现
        recordPart.setHasWithdraw(0);
        return recordPart;
    }

    @Override
    public PageVO<InvestmentIncomeVO> page(InvestmentIncomeQueryDTO incomeQueryDTO, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        Page<InvestmentIncomeVO> page = investmentIncomeRecordMapper.queryInvestmentIncomeList(incomeQueryDTO);
        //结算月份加1
        return new PageVO<>(pageNum, page);
    }

    /**
     * 查询分销商的提现收益
     *
     * @param incomeQueryDTO 查询dto
     * @param pageNum        第几页
     * @param pageSize       每页长度
     */
    /*@Override
    public PageVO<InvestmentIncomeVO> pageQueryInvestmentIncome(InvestmentIncomeQueryDTO incomeQueryDTO, Integer pageNum, Integer pageSize) {
        //查询经销商名称、账户、省市区筛选经销商id
        List<Integer> distributorIdList = userFeign.getDistributorIdByParam(incomeQueryDTO.getUserId(), incomeQueryDTO.getDistributorType(), incomeQueryDTO.getDistributorAccount(), incomeQueryDTO.getDistributorName(),
                incomeQueryDTO.getProvince(), incomeQueryDTO.getCity(), incomeQueryDTO.getRegion(), incomeQueryDTO.getRecommendDistributorName(), incomeQueryDTO.getRecommendDistributorAccount());


        incomeQueryDTO.setDistributorIds(distributorIdList);
        PageHelper.startPage(pageNum, pageSize);
        Page<InvestmentIncomeVO> page = investmentIncomeRecordMapper.queryInvestmentIncomeList(incomeQueryDTO);

        //取出所有的经销商id
        List<Integer> listTemp = new ArrayList<>();
        List<Integer> recommendIdList = new ArrayList<>();
        for (InvestmentIncomeVO dto : page) {
            listTemp.add(dto.getDistributorId());
        }
        List<DistributorDTO> distributorList = null;
        if (listTemp.size() > 0) {
            distributorList = userFeign.getDistributorByDistributorIdList(listTemp);
        }
        if (distributorList != null) {
            for (InvestmentIncomeVO incomeDTO : page) {
                for (DistributorDTO distributorDTO : distributorList) {
                    if (distributorDTO.getId() == incomeDTO.getDistributorId()) {
                        incomeDTO.setDistributorName(distributorDTO.getRealName());
                        incomeDTO.setDistributorAccount(distributorDTO.getUserName());
                        incomeDTO.setDistributorProvince(distributorDTO.getProvince());
                        incomeDTO.setDistributorCity(distributorDTO.getCity());
                        incomeDTO.setDistributorRegion(distributorDTO.getRegion());
                        recommendIdList.add(distributorDTO.getRecommendId());
                    }
                }
            }
        }

        List<DistributorDTO> recommendList = null;
        if (recommendIdList.size() > 0) {
            recommendList = userFeign.getDistributorByDistributorIdList(recommendIdList);
        }
        if (recommendList != null) {
            for (InvestmentIncomeVO incomeDTO : page) {
                for (DistributorDTO distributorDTO : recommendList) {
                    if (distributorDTO.getId() == Integer.valueOf(incomeDTO.getDistributorId())) {
                        //设置推荐人和推荐人帐号
                        incomeDTO.setRecommendDistributorName(distributorDTO.getRealName());
                        incomeDTO.setRecommendDistributorAccount(distributorDTO.getUserName());
                    }
                }
            }
        }
        PageHelper.startPage(pageNum, pageSize);
        Page<InvestmentIncomeVO> page = investmentIncomeRecordMapper.queryInvestmentIncomeList(incomeQueryDTO);

        return new PageVO<>(pageNum, page);
    }*/

    /**
     * 根据id查询招商收益
     *
     * @param id
     * @return InvestmentIncomeRecordDTO
     */
    public IncomeRecordResultDTO getInvestmentIncomeById(Integer id) {
        if (id == null) {
            throw new BadRequestException("招商收益id必填！");
        }
        IncomeRecordResultDTO incomeRecord = investmentIncomeRecordMapper.selectIncomeRecordByPrimaryKey(id);
        if (incomeRecord == null) {
            throw new BadRequestException("收益id信息有误！");
        }

        List<IncomeRecordPartResultDTO> list = investmentIncomeRecordPartMapper.getInvestmentIncomeRecordByPid(id);
        for (IncomeRecordPartResultDTO dto : list) {
            if (StringUtil.isNotBlank(dto.getIncomeSubjectCode())) {
                if (IncomeSubjectEnum.MAIN_COMPANY.value.equals(dto.getIncomeSubjectCode())) {
                    dto.setIncomeSubjectCode(IncomeSubjectEnum.MAIN_COMPANY.name);
                    continue;
                }
                if (IncomeSubjectEnum.ASSISTANT.value.equals(dto.getIncomeSubjectCode())) {
                    dto.setIncomeSubjectCode(IncomeSubjectEnum.ASSISTANT.name);
                    continue;
                }
                if (IncomeSubjectEnum.REGION_SHAREHOLDER.value.equals(dto.getIncomeSubjectCode())) {
                    dto.setIncomeSubjectCode(IncomeSubjectEnum.REGION_SHAREHOLDER.name);
                    continue;
                }
                if (IncomeSubjectEnum.RECOMMEND_STATION_COMPANY.value.equals(dto.getIncomeSubjectCode())) {
                    dto.setCompanyName(dto.getSettlementSubjectName());
                    dto.setIncomeSubjectCode(IncomeSubjectEnum.RECOMMEND_STATION_COMPANY.name);
                }
            }
        }
        incomeRecord.setIncomePartResults(list);
        return incomeRecord;
    }
}
