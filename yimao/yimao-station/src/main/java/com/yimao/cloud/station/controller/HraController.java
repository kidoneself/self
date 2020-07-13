package com.yimao.cloud.station.controller;

import com.alibaba.fastjson.JSON;
import com.yimao.cloud.base.cache.UserCache;
import com.yimao.cloud.base.enums.StationQueryEnum;
import com.yimao.cloud.base.utils.CollectionUtil;
import com.yimao.cloud.pojo.dto.hra.HraTicketResultDTO;
import com.yimao.cloud.pojo.dto.station.StationScheduleDTO;
import com.yimao.cloud.pojo.dto.system.StationDTO;
import com.yimao.cloud.pojo.query.station.HraSpecialQuery;
import com.yimao.cloud.pojo.query.station.HraTicketQuery;
import com.yimao.cloud.pojo.vo.PageVO;
import com.yimao.cloud.pojo.vo.station.HraGeneralSituationVO;
import com.yimao.cloud.station.aop.annotation.StationQuery;
import com.yimao.cloud.station.feign.HraFeign;
import com.yimao.cloud.station.feign.SystemFeign;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * 体检模块
 *
 * @author yaoweijun
 */
@RestController
@Api(tags = "HraController")
@Slf4j
public class HraController {

    @Resource
    private UserCache userCache;
    @Resource
    private HraFeign hraFeign;
    @Resource
    private SystemFeign systemFeign;

    /**
     * hra--概况
     *
     * @return
     */
    @GetMapping("/hra/station/generalSituation")
    @ApiOperation(value = "概况")
    public Object generalSituation() {
        Set<Integer> stationIds = userCache.getStationUserAreas(0,null);
        if (CollectionUtil.isEmpty(stationIds)) {
            return new HraGeneralSituationVO(0, 0);
        }
        //预约评估
        StationScheduleDTO hra = hraFeign.getStationHraNum(stationIds);
        HraGeneralSituationVO vo = new HraGeneralSituationVO();
        vo.setTodayNeedAssess(hra.getTodayNeedAssess());
        vo.setTotalFinishAssess(hra.getTotalFinishAssess());
        return vo;
    }

    /**
     * hra--根据查询条件获取预约列表
     *
     * @param pageNum
     * @param pageSize
     * @param query
     * @return
     */
    @StationQuery(StationQueryEnum.ListQuery)
    @PostMapping(value = "/ticket/station/reservation/list/{pageNum}/{pageSize}")
    @ApiOperation(value = "根据查询条件获取预约列表")
    public Object getReservationList(@PathVariable(value = "pageNum") Integer pageNum,
                                     @PathVariable(value = "pageSize") Integer pageSize,
                                     @RequestBody HraTicketQuery query) {
        log.info("查询={}", JSON.toJSONString(query));

        PageVO<HraTicketResultDTO> page = hraFeign.getStationHraTicketReservationList(pageNum, pageSize, query);

        List<HraTicketResultDTO> hraTicketResult = page.getResult();

        if (CollectionUtil.isEmpty(hraTicketResult)) {
            return page;
        }
        //查询用户查询服务站id服务站所在区域及其服务站名称       
        com.yimao.cloud.pojo.query.system.StationQuery stationQuery = new com.yimao.cloud.pojo.query.system.StationQuery();
        stationQuery.setIds(query.getStations());
        PageVO<StationDTO> stationPage = systemFeign.adminStationList(0, 0, stationQuery);

        List<StationDTO> stationList = stationPage.getResult();

        if (CollectionUtil.isEmpty(stationList)) {
            return page;
        }

        for (HraTicketResultDTO hraTicketResultDTO : hraTicketResult) {
            Integer stationId = hraTicketResultDTO.getStationId();

            if (Objects.isNull(stationId)) {
                continue;
            }

            for (StationDTO stationDTO : stationList) {
                if (stationId.equals(stationDTO.getId())) {
                    hraTicketResultDTO.setStationProvince(stationDTO.getProvince());
                    hraTicketResultDTO.setStationCity(stationDTO.getCity());
                    hraTicketResultDTO.setStationRegion(stationDTO.getRegion());
                    hraTicketResultDTO.setStationName(stationDTO.getName());
                    break;
                }
            }
        }

        return page;
    }

    /**
     * hra--根据查询条件获取体检列表
     *
     * @param pageNum
     * @param pageSize
     * @param query
     * @return
     */
    @StationQuery(StationQueryEnum.ListQuery)
    @PostMapping(value = "/ticket/station/use/list/{pageNum}/{pageSize}")
    @ApiOperation(value = "根据查询条件获取体检列表")
    public Object getList(@PathVariable(value = "pageNum") Integer pageNum,
                          @PathVariable(value = "pageSize") Integer pageSize,
                          @RequestBody HraTicketQuery query) {
        log.info("查询={}", JSON.toJSONString(query));

        PageVO<HraTicketResultDTO> page = hraFeign.getStationHraTicketUsedList(pageNum, pageSize, query);

        List<HraTicketResultDTO> hraTicketResult = page.getResult();

        if (CollectionUtil.isEmpty(hraTicketResult)) {
            return page;
        }
        //查询用户查询服务站id服务站所在区域及其服务站名称       
        com.yimao.cloud.pojo.query.system.StationQuery stationQuery = new com.yimao.cloud.pojo.query.system.StationQuery();
        stationQuery.setIds(query.getStations());
        PageVO<StationDTO> stationPage = systemFeign.adminStationList(0, 0, stationQuery);

        List<StationDTO> stationList = stationPage.getResult();

        if (CollectionUtil.isEmpty(stationList)) {
            return page;
        }

        for (HraTicketResultDTO hraTicketResultDTO : hraTicketResult) {
            Integer stationId = hraTicketResultDTO.getStationId();

            if (Objects.isNull(stationId)) {
                continue;
            }

            for (StationDTO stationDTO : stationList) {
                if (stationId.equals(stationDTO.getId())) {
                    hraTicketResultDTO.setStationProvince(stationDTO.getProvince());
                    hraTicketResultDTO.setStationCity(stationDTO.getCity());
                    hraTicketResultDTO.setStationRegion(stationDTO.getRegion());
                    hraTicketResultDTO.setStationName(stationDTO.getName());
                    break;
                }
            }
        }

        return page;
    }

    /**
     * hra--评估卡管理--F卡管理 列表查询
     * 此接口根据用户绑定门店id非区域id查询
     *
     * @param pageNum
     * @param pageSize
     * @param query
     * @return
     */
    @StationQuery(StationQueryEnum.ListQuery)
    @PostMapping(value = "/ticket/station/special/{pageNum}/{pageSize}")
    @ApiOperation(value = "F卡管理 列表查询")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "第几页", required = true, dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "pageSize", value = "每页大小", required = true, dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "query", value = "查询条件", dataType = "HraSpecialQuery", paramType = "body")
    })
    public Object allotTicket(@PathVariable(value = "pageNum") Integer pageNum,
                              @PathVariable(value = "pageSize") Integer pageSize,
                              @RequestBody(required = false) HraSpecialQuery query) {
        log.info("查询={}", JSON.toJSONString(query));

        return hraFeign.stationAllotTicket(pageNum, pageSize, query);
    }
}
