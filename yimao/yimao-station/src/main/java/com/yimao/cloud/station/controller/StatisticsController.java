package com.yimao.cloud.station.controller;

import com.alibaba.fastjson.JSON;
import com.yimao.cloud.base.cache.UserCache;
import com.yimao.cloud.base.enums.PermissionTypeEnum;
import com.yimao.cloud.base.exception.BadRequestException;
import com.yimao.cloud.base.exception.YimaoException;
import com.yimao.cloud.base.utils.CollectionUtil;
import com.yimao.cloud.pojo.dto.station.*;
import com.yimao.cloud.pojo.query.station.HraTicketQuery;
import com.yimao.cloud.pojo.query.station.StationWaterDeviceQuery;
import com.yimao.cloud.pojo.query.station.StatisticsQuery;
import com.yimao.cloud.pojo.query.station.StatisticsStationQuery;
import com.yimao.cloud.pojo.vo.station.*;
import com.yimao.cloud.station.feign.*;
import com.yimao.cloud.station.service.StationRoleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 统计模块
 *
 * @author yaoweijun
 */
@RestController
@Api(tags = "StatisticsController")
@Slf4j
public class StatisticsController {
    @Resource
    private OrderFeign orderFeign;
    @Resource
    private HraFeign hraFeign;
    @Resource
    private UserFeign userFeign;
    @Resource
    private ProductFeign productFeign;
    @Resource
    private StationRoleService stationRoleService;
    @Resource
    private UserCache userCache;
    @Resource
    private WaterFeign waterFeign;


    /**
     * 统计-流水统计(经销商角度)
     *
     * @param type 0-全部 1-产品 2-hra 3-招商
     */
    @PostMapping("/statistics/station/performanceFlow")
    @ApiOperation(value = "统计-流水统计")
    @ApiImplicitParam(name = "query", value = "统计筛选条件", required = true, dataType = "StatisticsQuery", paramType = "body")
    public FlowStatisticsVO getPerformanceFlow(@RequestBody StatisticsQuery query) {

        checkParam(query,PermissionTypeEnum.PRE_SALE.value);

        log.info("query={}", JSON.toJSONString(query));

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        FlowStatisticsDTO productAndHraRes = orderFeign.getProductAndHraSaleData(query);
        //产品销量统计
        List<FlowStatisticsDTO> productRes = productAndHraRes.getProductRes();
        //hra卡片统计
        List<FlowStatisticsDTO> hraRes = productAndHraRes.getHraRes();
        //招商统计
        FlowStatisticsDTO businessOrdersRes = userFeign.getDistributorOrderData(query);
        List<FlowStatisticsDTO> distributorOrderRes = businessOrdersRes.getDistributorOrderRes();
        //表格数据展示服务站及其查询区域集合
        List<StatisticsStationQuery> stations = query.getStationList();
        //表格数据的返回对象
        List<FlowStatisticsDTO> dataRes = new ArrayList();

        for (StatisticsStationQuery statisticsStationQuery : stations) {
            //获取服务站
            Integer stationId = statisticsStationQuery.getId();
            //该服务站区域集合
            Set<Integer> areas = statisticsStationQuery.getAreas();

            FlowStatisticsDTO vo = new FlowStatisticsDTO();
            vo.setStationName(statisticsStationQuery.getName());
            if (Objects.isNull(query.getStartTime())) {
                vo.setDate("全部");
            } else {
                vo.setDate(sdf.format(query.getStartTime()) + "-" + sdf.format(query.getEndTime()));
            }


            if (CollectionUtil.isNotEmpty(productRes)) {
                Integer jsfwSaleNum = 0;
                Integer ywlSaleNum = 0;
                Integer swkjSaleNum = 0;
                BigDecimal jsfwSaleFee = new BigDecimal(0);
                BigDecimal ywlSaleFee = new BigDecimal(0);
                BigDecimal swkjSaleFee = new BigDecimal(0);
                Integer productTotalSaleNum = 0;
                BigDecimal productTotalSaleFee = new BigDecimal(0);
                for (FlowStatisticsDTO productDto : productRes) {
                    if (areas.contains(productDto.getAreaId())) {
                        jsfwSaleNum += productDto.getJsfwSaleNum();
                        ywlSaleNum += productDto.getYwlSaleNum();
                        swkjSaleNum += productDto.getSwkjSaleNum();
                        jsfwSaleFee = jsfwSaleFee.add(productDto.getJsfwSaleFee());
                        ywlSaleFee = ywlSaleFee.add(productDto.getYwlSaleFee());
                        swkjSaleFee = swkjSaleFee.add(productDto.getSwkjSaleFee());
                        productTotalSaleNum += productDto.getProductTotalSaleNum();
                        productTotalSaleFee = productTotalSaleFee.add(productDto.getProductTotalSaleFee());
                    }
                }

                vo.setJsfwSaleNum(jsfwSaleNum);
                vo.setYwlSaleNum(ywlSaleNum);
                vo.setSwkjSaleNum(swkjSaleNum);
                vo.setJsfwSaleFee(jsfwSaleFee);
                vo.setYwlSaleFee(ywlSaleFee);
                vo.setSwkjSaleFee(swkjSaleFee);
                vo.setProductTotalSaleNum(productTotalSaleNum);
                vo.setProductTotalSaleFee(productTotalSaleFee);
            }


            if (CollectionUtil.isNotEmpty(hraRes)) {
                Integer pgSaleNum = 0;
                Integer yhSaleNum = 0;
                BigDecimal pgSaleFee = new BigDecimal(0);
                BigDecimal yhSaleFee = new BigDecimal(0);
                Integer hraTotalSaleNum = 0;
                BigDecimal hraTotalSaleFee = new BigDecimal(0);

                for (FlowStatisticsDTO hraDto : hraRes) {
                    if (areas.contains(hraDto.getAreaId())) {
                        pgSaleNum += hraDto.getPgSaleNum();
                        yhSaleNum += hraDto.getYhSaleNum();
                        pgSaleFee = pgSaleFee.add(hraDto.getPgSaleFee());
                        yhSaleFee = yhSaleFee.add(hraDto.getYhSaleFee());
                        hraTotalSaleNum += hraDto.getHraTotalSaleNum();
                        hraTotalSaleFee = hraTotalSaleFee.add(hraDto.getHraTotalSaleFee());
                    }
                }

                vo.setPgSaleNum(pgSaleNum);
                vo.setYhSaleNum(yhSaleNum);
                vo.setPgSaleFee(pgSaleFee);
                vo.setYhSaleFee(yhSaleFee);
                vo.setHraTotalSaleNum(hraTotalSaleNum);
                vo.setHraTotalSaleFee(hraTotalSaleFee);
            }

            if (CollectionUtil.isNotEmpty(distributorOrderRes)) {
                Integer registNum = 0;
                Integer upgradeNum = 0;
                BigDecimal registFee = new BigDecimal(0);
                BigDecimal upgradeFee = new BigDecimal(0);
                Integer distributorTotalSaleNum = 0;
                BigDecimal distributorTotalSaleFee = new BigDecimal(0);

                for (FlowStatisticsDTO distributorOrderDto : distributorOrderRes) {
                    if (areas.contains(distributorOrderDto.getAreaId())) {
                        registNum += distributorOrderDto.getRegistNum();
                        upgradeNum += distributorOrderDto.getUpgradeNum();
                        registFee = registFee.add(distributorOrderDto.getRegistFee());
                        upgradeFee = upgradeFee.add(distributorOrderDto.getUpgradeFee());
                        distributorTotalSaleNum += distributorOrderDto.getDistributorTotalSaleNum();
                        distributorTotalSaleFee = distributorTotalSaleFee.add(distributorOrderDto.getDistributorTotalSaleFee());
                    }
                }

                vo.setRegistNum(registNum);
                vo.setUpgradeNum(upgradeNum);
                vo.setRegistFee(registFee);
                vo.setUpgradeFee(upgradeFee);
                vo.setDistributorTotalSaleNum(distributorTotalSaleNum);
                vo.setDistributorTotalSaleFee(distributorTotalSaleFee);
            }

            dataRes.add(vo);
        }

        FlowStatisticsVO res = new FlowStatisticsVO();
        List<FlowStatisticsDTO> productPicRes = productAndHraRes.getProductPicRes();
        List<FlowStatisticsDTO> hraPicRes = productAndHraRes.getHraPicRes();
        List<FlowStatisticsDTO> distributorOrderPicRes = businessOrdersRes.getDistributorOrderPicRes();

        List<FlowStatisticsDTO> totalProductAndHraPicRes = productAndHraRes.getTotalProductAndHraPicRes();
        List<FlowStatisticsDTO> totalDistributorOrderPicRes = businessOrdersRes.getTotalDistributorOrderPicRes();
        //setPicDara(productPicRes,hraPicRes,distributorOrderPicRes);

        res.setFlowStatistics(dataRes);
        res.setProductPicRes(productPicRes);
        res.setHraPicRes(hraPicRes);
        res.setDistributorOrderPicRes(distributorOrderPicRes);

        res.setTotalProductAndHraPicRes(totalProductAndHraPicRes);
        res.setTotalDistributorOrderPicRes(totalDistributorOrderPicRes);

        return res;
    }

    /**
     * 每个集合都是按日期排序并且日期不为空(未使用)
     *
     * @param productPicRes
     * @param hraPicRes
     * @param distributorOrderPicRes
     */
    private void setPicDara(List<FlowStatisticsDTO> productPicRes, List<FlowStatisticsDTO> hraPicRes, List<FlowStatisticsDTO> distributorOrderPicRes, Date startTime, Date endTime, SimpleDateFormat sdf) {
        String startDate = "";
        String endDate = "";

        int productLength = productPicRes.size();
        int hraLength = hraPicRes.size();
        int distributorOrderLength = distributorOrderPicRes.size();

        int max = productLength > hraLength ? productLength : hraLength;
        max = max > distributorOrderLength ? max : distributorOrderLength;

        if (Objects.isNull(startTime)) {
            startDate = "2016";
            endDate = sdf.format(new Date());

        } else {
            startDate = sdf.format(startTime);
            endDate = sdf.format(endTime);
        }
        for (int i = 0; i < max; i++) {

        }

    }

    /**
     * 统计-用户统计
     */
    @PostMapping("/statistics/station/performanceUser")
    @ApiOperation(value = "统计-用户统计")
    @ApiImplicitParam(name = "query", value = "统计筛选条件", required = true, dataType = "StatisticsQuery", paramType = "body")
    public UserStatisticsVO getPerformanceUser(@RequestBody StatisticsQuery query) {

        checkParam(query,PermissionTypeEnum.PRE_SALE.value);

        log.info("query={}", JSON.toJSONString(query));

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        UserStatisticsDTO userStatisticsInfo = userFeign.getUserStatisticsInfoToStation(query);
        //经销商统计
        List<UserStatisticsDTO> distributorRes = userStatisticsInfo.getDistributorRes();
        //代理商统计
        List<UserStatisticsDTO> agentRes = userStatisticsInfo.getAgentRes();

        //表格数据展示服务站及其查询区域集合
        List<StatisticsStationQuery> stations = query.getStationList();
        //表格数据的返回对象
        List<UserStatisticsDTO> dataRes = new ArrayList();

        for (StatisticsStationQuery statisticsStationQuery : stations) {
            //该服务站区域集合
            Set<Integer> areas = statisticsStationQuery.getAreas();

            UserStatisticsDTO vo = new UserStatisticsDTO();
            vo.setStationName(statisticsStationQuery.getName());
            if (Objects.isNull(query.getStartTime())) {
                vo.setDate("全部");
            } else {
                vo.setDate(sdf.format(query.getStartTime()) + "-" + sdf.format(query.getEndTime()));
            }
            query.setAreas(areas);

            //用户统计表（普通用户，会员用户数量）
            UserStatisticsDTO userRes = userFeign.getUserRes(query);
            if (userRes != null) {
                vo.setGeneralUserNum(userRes.getGeneralUserNum());
                vo.setVipUserNum(userRes.getVipUserNum());
            }

            if (CollectionUtil.isNotEmpty(distributorRes)) {
                Integer distributorTotalNum = 0;
                Integer microinvasiveDistributorNum = 0;
                Integer personageDistributorNum = 0;
                Integer experienceDistributorNum = 0;
                Integer enterpriseDistributorNum = 0;
                for (UserStatisticsDTO distributorDto : distributorRes) {
                    if (areas.contains(distributorDto.getAreaId())) {
                        microinvasiveDistributorNum += distributorDto.getMicroinvasiveDistributorNum();
                        personageDistributorNum += distributorDto.getPersonageDistributorNum();
                        experienceDistributorNum += distributorDto.getExperienceDistributorNum();
                        enterpriseDistributorNum += distributorDto.getEnterpriseDistributorNum();
                        distributorTotalNum += distributorDto.getDistributorTotalNum();
                    }
                }
                vo.setDistributorTotalNum(distributorTotalNum);
                vo.setMicroinvasiveDistributorNum(microinvasiveDistributorNum);
                vo.setPersonageDistributorNum(personageDistributorNum);
                vo.setExperienceDistributorNum(experienceDistributorNum);
                vo.setEnterpriseDistributorNum(enterpriseDistributorNum);
            }

            if (CollectionUtil.isNotEmpty(agentRes)) {
                Integer agentNum = 0;
                Integer districtAgentNum = 0;

                for (UserStatisticsDTO agentDto : agentRes) {
                    if (areas.contains(agentDto.getAreaId())) {
                        agentNum += agentDto.getAgentTotalNum();
                        districtAgentNum += agentDto.getDistrictAgentNum();
                    }
                }
                vo.setAgentTotalNum(agentNum);
                vo.setDistrictAgentNum(districtAgentNum);
            }

            dataRes.add(vo);
        }

        UserStatisticsVO res = new UserStatisticsVO();
        res.setUserStatistics(dataRes);
        res.setUserPicRes(userStatisticsInfo.getUserPicRes());
        res.setDistributorPicRes(userStatisticsInfo.getDistributorPicRes());
        res.setAgentPicRes(userStatisticsInfo.getAgentPicRes());
        return res;
    }

    /**
     * 统计-商品统计-汇总数据
     */
    @PostMapping("/statistics/station/getProductTabulateData")
    @ApiOperation(value = "统计-商品统计-汇总数据")
    @ApiImplicitParam(name = "query", value = "统计筛选条件", required = true, dataType = "StatisticsQuery", paramType = "body")
    public List<ProductTabulateDataDTO> getProductTabulateData(@RequestBody StatisticsQuery query) {
        checkParam(query,PermissionTypeEnum.PRE_SALE.value);
        return orderFeign.getProductStatisticsInfoToStation(query);
    }

    /**
     * 统计-商品统计-商品销售情况以及二级分类图表
     */
    @PostMapping("/statistics/station/getProductSalesStatusAndTwoCategoryPicRes")
    @ApiOperation(value = "统计-商品统计-商品销售情况以及二级分类图表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "query", value = "统计筛选条件", required = true, dataType = "StatisticsQuery", paramType = "body"),
            @ApiImplicitParam(name = "categoryName", value = "一级分类名称", required = true, dataType = "String", paramType = "query"),
    })
    public ProductSalesStatusAndTwoCategoryPicResVO getProductSalesStatusAndTwoCategoryPicRes(@RequestBody StatisticsQuery query, @RequestParam("categoryName") String categoryName) {
        checkParam(query,PermissionTypeEnum.PRE_SALE.value);
        return orderFeign.getProductSalesStatusAndTwoCategoryPicRes(query, categoryName);
    }

    /**
     * 统计-续费统计
     */
    @PostMapping("/statistics/station/renewFlow")
    @ApiOperation(value = "统计-续费统计")
    @ApiImplicitParam(name = "query", value = "统计筛选条件", required = true, dataType = "StatisticsQuery", paramType = "body")
    public RenewStatisticsVO getRenewFlow(@RequestBody StatisticsQuery query) {

        checkParam(query,PermissionTypeEnum.AFTER_SALE.value);

        log.info("query={}", JSON.toJSONString(query));

        //表格数据展示服务站及其查询区域集合
        List<StatisticsStationQuery> stations = query.getStationList();

        //所有选定区域的安装工id
        List<Integer> allEngineerIds = new ArrayList();
        //续费表格数据集合(只针对当前最新数据)
        List<RenewStatisticsDTO> renewDataRes = new ArrayList();

        for (StatisticsStationQuery statisticsStationQuery : stations) {
            //服务站id
            Integer stationId = statisticsStationQuery.getId();
            //服务站名称
            String stationName = statisticsStationQuery.getName();

            //获取该服务站区域集合
            Set<Integer> areas = statisticsStationQuery.getAreas();
            //获取售后服务区域
            Set<Integer> afterServiceAreas = userCache.getStationUserAreas(1, PermissionTypeEnum.AFTER_SALE.value);
            Iterator<Integer> iterator = areas.iterator();
            while (iterator.hasNext()) {
                Integer areaId = iterator.next();
                //删除标识
                Boolean flag = true;
                for (Integer afterServiceArea : afterServiceAreas) {
                    if (afterServiceArea.intValue() == areaId) {
                        //该区域属于售后服务区域
                        flag = false;
                        break;
                    }
                }
                if (flag) {
                    //当前区域不属于售后服务区域，删除
                    iterator.remove();
                }
            }
            //查询当前服务站下所有安装工id(目前服务站用户大部分只绑定一个服务站，循环中调用)
            List<Integer> engineerIds = userFeign.getEngineerIdsByAreaIds(areas);

            if (CollectionUtil.isEmpty(engineerIds)) {

                RenewStatisticsDTO renewData = new RenewStatisticsDTO();
                renewData.setStationName(stationName);
                renewData.setDate("全部");

                renewDataRes.add(renewData);

                continue;
            }

            allEngineerIds.addAll(engineerIds);

            //查询当前续费统计列表（water device）
            StationWaterDeviceQuery waterDeviceQuery = new StationWaterDeviceQuery();
            waterDeviceQuery.setEngineerIds(engineerIds);
            //waterDeviceQuery.setEndTime(new Date());

            RenewStatisticsDTO renewData = waterFeign.getWaterDeviceRenewData(waterDeviceQuery);

            renewData.setStationName(stationName);
            renewData.setDate("全部");

            renewDataRes.add(renewData);

        }

        List<RenewStatisticsDTO> isRenewPicData = new ArrayList();
        List<RenewStatisticsDTO> newInstallPicData = new ArrayList();

        if (CollectionUtil.isNotEmpty(allEngineerIds)) {
            StationWaterDeviceQuery waterDeviceQuery = new StationWaterDeviceQuery();

            waterDeviceQuery.setEngineerIds(allEngineerIds);
            waterDeviceQuery.setStartTime(query.getStartTime());
            waterDeviceQuery.setEndTime(query.getEndTime());

            //查询历史续费统计图表数据已续费（order_renew）
            isRenewPicData = orderFeign.getIsRenewPicData(waterDeviceQuery);

            //查询历史续费统计图表数据新安装与应续费（water_device），该应续费数据只针对当前应续费，无法展示特定日期下的应续费个数
            newInstallPicData = waterFeign.getWaterDeviceRenewPicData(waterDeviceQuery);
        }


        RenewStatisticsVO res = new RenewStatisticsVO();
        res.setRenewStatistics(renewDataRes);
        res.setIsRenewPicData(isRenewPicData);
        res.setNewInstallPicData(newInstallPicData);

        return res;
    }


    /**
     * 统计-评估统计
     */
    @PostMapping("/statistics/station/hraFlow")
    @ApiOperation(value = "统计评估统计")
    @ApiImplicitParam(name = "query", value = "统计筛选条件", required = true, dataType = "StatisticsQuery", paramType = "body")
    public HraStatisticsVO getHraFlow(@RequestBody StatisticsQuery query) {
        checkParam(query,PermissionTypeEnum.ALL.value);

        log.info("query={}", JSON.toJSONString(query));

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        HraTicketQuery hraQuery = new HraTicketQuery();
        hraQuery.setStations(query.getStations());
        if (Objects.isNull(query.getStartTime())) {
            hraQuery.setBeginTime(null);
            hraQuery.setEndTime(null);
        } else {
            hraQuery.setBeginTime(sdf.format(query.getStartTime()));
            hraQuery.setEndTime(sdf.format(query.getEndTime()));
        }

        //评估表格数据集合
        List<HraStatisticsDTO> hraDataRes = hraFeign.getStationHraData(hraQuery);

        //表格数据展示服务站及其查询区域集合
        List<StatisticsStationQuery> stations = query.getStationList();

        for (HraStatisticsDTO hraDTO : hraDataRes) {

            if (Objects.isNull(query.getStartTime())) {
                hraDTO.setDate("全部");
            } else {
                hraDTO.setDate(sdf.format(query.getStartTime()) + "-" + sdf.format(query.getEndTime()));
            }

            for (StatisticsStationQuery statisticsStationQuery : stations) {
                //服务站id
                Integer stationId = statisticsStationQuery.getId();
                //服务站名称
                if (hraDTO.getStationId().equals(stationId)) {
                    hraDTO.setStationName(statisticsStationQuery.getName());
                }

            }

        }

        //评估图表
        HraStatisticsDTO hraDataPicRes = hraFeign.getStationHraPicData(hraQuery);

        HraStatisticsVO res = new HraStatisticsVO();
        res.setHraStatistics(hraDataRes);
        res.setReservationPicRes(hraDataPicRes.getReservationPicRes());
        res.setUsedPicRes(hraDataPicRes.getUsedPicRes());

        return res;

    }


    /**
     * 查询时间筛选统一处理方法
     *
     * @param query
     * @param serviceType 
     */
    public void checkParam(StatisticsQuery query, int serviceType) {

        if (Objects.isNull(query.getDateType())) {
            throw new BadRequestException("未选定时间筛选类型。");
        }
        
        if (Objects.isNull(serviceType)) {
            throw new YimaoException("未指定售前售后");
        }

        //时间筛选设置
        if (query.getDateType() == 0) {
            query.setStartTime(null);
            query.setEndTime(null);
        } else if (query.getDateType() == 1) {
            //七日
            Date end = new Date();

            Date start = getDate(-7);

            query.setStartTime(start);
            query.setEndTime(end);
        } else if (query.getDateType() == 2) {
            //三十日
            Date end = new Date();

            Date start = getDate(-30);

            query.setStartTime(start);
            query.setEndTime(end);
        } else {
            if (Objects.isNull(query.getStartTime())) {
                throw new BadRequestException("筛选开始时间不能为空。");
            }

            if (Objects.isNull(query.getEndTime())) {
                throw new BadRequestException("筛选结束时间不能为空。");
            }
        }

        //根据售前售后类型获取用户区域
        Set<Integer> areas= userCache.getStationUserAreas(1,serviceType);
              
        //获取用户服务站
        Set<Integer> stations = userCache.getStationUserAreas(0,null);

        if (CollectionUtil.isEmpty(areas)) {
            throw new BadRequestException("用户未绑定区域。");
        }

        //区域设置
        List<StatisticsStationQuery> stationList = query.getStationList();

        if (CollectionUtil.isEmpty(stationList)) {
            throw new BadRequestException("筛选区域不能为空。");
        } else {
        	//最终查询的区域id集合
            Set<Integer> queryAreas = new HashSet<>();
            //最终查询的服务站id集合
            Set<Integer> queryStations = new HashSet<>();

            Iterator<StatisticsStationQuery> stationIterator = stationList.iterator();

            while (stationIterator.hasNext()) {
                StatisticsStationQuery stationQuery = stationIterator.next();

                Integer stationId = stationQuery.getId();
                
                Integer stationServiceType = stationQuery.getServiceType();
                
                log.info("查询门店的售前售后类型={}",stationServiceType);
                
                if(Objects.isNull(stationServiceType)) {
                	throw new BadRequestException("筛选门店"+stationQuery.getName()+"未有售前售后类型。");
                }
                
                //查询的门店不是售前+售后类型并且与接口查询的售前售后类型不符删除
                if(serviceType != PermissionTypeEnum.ALL.value) {
                	 if(stationServiceType != PermissionTypeEnum.ALL.value && ! stationServiceType.equals(serviceType)) {
                     	stationIterator.remove();
                     	continue;
                     }
                }
               
                
                if (stations.contains(stationId)) {
                    queryStations.add(stationId);
                    //判断筛选条件中的区域
                    Set<Integer> checkAreas = stationQuery.getAreas();

                    if (CollectionUtil.isEmpty(checkAreas)) {
                        continue;
                    }

                    Iterator<Integer> areaIterator = checkAreas.iterator();

                    while (areaIterator.hasNext()) {
                        Integer areaId = areaIterator.next();

                        if (areas.contains(areaId)) {
                            queryAreas.add(areaId);
                        } else {
                            //移除该区域
                            areaIterator.remove();
                        }
                    }


                } else {
                    //删除该服务站
                    stationIterator.remove();
                }

            }

            if (queryStations.size() < 1) {
                throw new BadRequestException("用户选择门店异常。");
            } else {
                query.setStations(queryStations);
            }

            if (queryAreas.size() < 1) {
                throw new BadRequestException("用户选择区域异常。");
            } else {
                query.setAreas(queryAreas);
            }

        }


    }

    public static Date getDate(int day) {
        Calendar now = Calendar.getInstance();
        now.add(Calendar.DAY_OF_MONTH, day);
        return now.getTime();

    }


}
