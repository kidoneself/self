package com.yimao.cloud.station.feign;

import com.yimao.cloud.base.constant.Constant;
import com.yimao.cloud.pojo.dto.hra.HraTicketResultDTO;
import com.yimao.cloud.pojo.dto.station.HraStatisticsDTO;
import com.yimao.cloud.pojo.dto.station.StationScheduleDTO;
import com.yimao.cloud.pojo.query.station.HraSpecialQuery;
import com.yimao.cloud.pojo.query.station.HraTicketQuery;
import com.yimao.cloud.pojo.vo.PageVO;
import com.yimao.cloud.pojo.vo.station.StationHraCardVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

/**
 * @author yaowejun
 * @date 2019/12/23
 */
@FeignClient(name = Constant.MICROSERVICE_HRA)
public interface HraFeign {

    /**
     * hra--评估卡管理--F卡管理 列表查询 (服务站站务系统调用)
     *
     * @param pageNum
     * @param pageSize
     * @param query
     * @return
     */
    @RequestMapping(value = "/ticket/station/special/{pageNum}/{pageSize}", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    PageVO<StationHraCardVO> stationAllotTicket(@PathVariable(value = "pageNum") Integer pageNum,
                                                @PathVariable(value = "pageSize") Integer pageSize,
                                                @RequestBody HraSpecialQuery query);


    /*
     * 查询体检评估列表
     */
    @PostMapping(value = "/ticket/station/use/list/{pageNum}/{pageSize}", consumes = MediaType.APPLICATION_JSON_VALUE)
    PageVO<HraTicketResultDTO> getStationHraTicketUsedList(@PathVariable(value = "pageNum") Integer pageNum,
                                                           @PathVariable(value = "pageSize") Integer pageSize, @RequestBody HraTicketQuery query);

    /**
     * 查询体预约列表
     *
     * @param pageNum
     * @param pageSize
     * @param query
     * @return
     */
    @PostMapping(value = "/ticket/station/reservation/list/{pageNum}/{pageSize}", consumes = MediaType.APPLICATION_JSON_VALUE)
    PageVO<HraTicketResultDTO> getStationHraTicketReservationList(@PathVariable(value = "pageNum") Integer pageNum,
                                                                  @PathVariable(value = "pageSize") Integer pageSize, @RequestBody HraTicketQuery query);

    /**
     * 控制台-待办事项(昨日已评估数，今日待评估，总评估数)
     *
     * @param areas
     * @param distributorIds
     * @return
     */
    @PostMapping(value = "/ticket/station/stationHraAssessNum", consumes = MediaType.APPLICATION_JSON_VALUE)
    StationScheduleDTO getStationHraNum(@RequestBody Set<Integer> stations);

    /**
     * 统计-评估统计-表格数据
     *
     * @param hraQuery
     * @return
     */
    @PostMapping(value = "/ticket/station/stationHraData", consumes = MediaType.APPLICATION_JSON_VALUE)
    List<HraStatisticsDTO> getStationHraData(@RequestBody HraTicketQuery hraQuery);

    /**
     * 统计-评估统计-图表数据
     *
     * @param hraQuery
     * @return
     */
    @PostMapping(value = "/ticket/station/stationHraPicData", consumes = MediaType.APPLICATION_JSON_VALUE)
    HraStatisticsDTO getStationHraPicData(@RequestBody HraTicketQuery hraQuery);

}




