package com.yimao.cloud.hra.feign;

import com.yimao.cloud.base.constant.Constant;
import com.yimao.cloud.pojo.dto.system.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

/**
 * @author Zhang Bo
 * @date 2018/11/1.
 */
@FeignClient(name = Constant.MICROSERVICE_SYSTEM)
public interface SystemFeign {

    /**
     * 根据服务站ID获取服务站信息
     *
     * @param id 服务站ID
     * @return
     */
    @RequestMapping(value = "/station/{id}", method = RequestMethod.GET)
    StationDTO getStationById(@PathVariable(value = "id") Integer id);

    /**
     * @param province 省
     * @param city     市
     * @param region   区
     * @Description:根据省市区获取服务站
     * @author ycl
     * @Return: com.yimao.cloud.pojo.dto.system.StationDTO
     * @Create: 2019/1/12 16:03
     */
    @RequestMapping(value = "/station/store", method = RequestMethod.GET)
    List<StationDTO> findStationByPCR(@RequestParam(value = "province", required = false) String province,
                                      @RequestParam(value = "city", required = false) String city,
                                      @RequestParam(value = "region", required = false) String region,
                                      @RequestParam(value = "stationName", required = false) String stationName);

    /**
     * 根据省市区获取服务站Ids
     *
     * @param province 省
     * @param city     市
     * @param region   区
     * @return Ids
     * @author liuhao@yimaokeji.com
     */
    @RequestMapping(value = "/station/ids", method = RequestMethod.GET)
    List<Integer> findStationIdsByPCR(@RequestParam(value = "province", required = false) String province,
                                      @RequestParam(value = "city", required = false) String city,
                                      @RequestParam(value = "region", required = false) String region,
                                      @RequestParam(value = "stationName", required = false) String stationName);

    /**
     * @param stationName
     * @Description: 根据服务站门店
     * @author ycl
     * @Return: java.util.List<java.lang.Integer>
     * @Create: 2019/1/12 16:07
     */
    @RequestMapping(value = "/station/{stationName}", method = RequestMethod.GET)
    List<Integer> findStationIdByName(@PathVariable(value = "stationName") String stationName);


    /**
     * 获取渠道和端
     */
    @RequestMapping(value = "/exchange/channel", method = RequestMethod.GET)
    String findSideOrChannel(@RequestParam("groupCode") String groupCode,
                             @RequestParam("code") List<String> code,
                             @RequestParam("msgName") String msgName);

    /**
     * 兑换限制
     */
    @RequestMapping(value = "/exchange/limit", method = RequestMethod.GET)
    DictionaryDTO exchangeLimit(@RequestParam("exchange") String exchange);

    /**
     * 根据服务站ids查询服务站名称
     *
     * @param stationIds
     * @return
     */
    @RequestMapping(value = "/station/name/ids", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    List<StationServiceAreaDTO> getStationNameByIds(@RequestParam("stationIds") Set<Integer> stationIds);


    @RequestMapping(value = "/station/company/name/{stationId}", method = RequestMethod.GET)
    String getStationCompanyNameById(@PathVariable("stationId") Integer stationId);

    @RequestMapping(value = "/message/record", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    void messageRecordSave(@RequestBody MessageRecordDTO recordDTO);

    @RequestMapping(value = "/message/content", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    void messageContentSave(@RequestBody MessageContentDTO dto);
}
