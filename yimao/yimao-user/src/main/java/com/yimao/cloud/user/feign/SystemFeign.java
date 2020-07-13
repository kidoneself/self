package com.yimao.cloud.user.feign;

import com.yimao.cloud.base.constant.Constant;
import com.yimao.cloud.pojo.dto.system.MessageTemplateDTO;
import com.yimao.cloud.pojo.dto.system.StationCompanyDTO;
import com.yimao.cloud.pojo.dto.system.StationDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @author Zhang Bo
 * @date 2018/7/4.
 */
@FeignClient(name = Constant.MICROSERVICE_SYSTEM)
public interface SystemFeign {

    @GetMapping(value = "/area/getRegionIdByPCR")
    Integer getRegionIdByPCR(@RequestParam(value = "province") String province, @RequestParam(value = "city") String city, @RequestParam(value = "region") String region);

    /**
     * @return java.lang.Object
     * @Author lizhiqiang
     * @Date 2019/3/1
     * @Param [province, city, region]
     */
    @GetMapping(value = "/station/information")
    StationDTO getStationByPRC(@RequestParam(name = "province") String province,
                               @RequestParam(name = "city") String city,
                               @RequestParam(name = "region") String region,
                               @RequestParam(name = "type") Integer type);

    @GetMapping(value = "/station/recommendId")
    StationDTO getStationByDistributorId(@RequestParam(name = "recommendId") Integer recommendId);

    /**
     * 根据省市区、服务类型查询服务站公司
     */
    @GetMapping(value = "/station/company/information")
    StationCompanyDTO getStationCompanyByPCR(@RequestParam("province") String province,
                                             @RequestParam("city") String city,
                                             @RequestParam("region") String region,
                                             @RequestParam("type") Integer type);

    /***
     * 功能描述:根据参数查找模版信息
     *
     * @param: [type, mechanism, pushObject, pushMode]
     * @auther: liu yi
     * @date: 2019/7/15 10:26
     * @return: com.yimao.cloud.pojo.dto.system.MessageTemplateDTO
     */
    @GetMapping(value = {"/messageTemplate"})
    MessageTemplateDTO findMessageTemplateByParam(@RequestParam(value = "type") String type,
                                                  @RequestParam(value = "mechanism") String mechanism,
                                                  @RequestParam(value = "pushObject") String pushObject,
                                                  @RequestParam(value = "pushMode") String pushMode);

    /**
     * 查询服务站门店信息（单个）
     *
     * @param id 服务站门店ID
     */
    @GetMapping(value = "/station/{id}")
    StationDTO getStationById(@PathVariable("id") Integer id);
    
    
    @PutMapping(value = "/station/master/info",consumes = MediaType.APPLICATION_JSON_VALUE)
    public void updateStationMasterInfo(@RequestBody StationDTO dto);

    /**
     * 根据服务站门店id查询服务站公司信息
     *
     * @param stationId
     * @return
     */
    @GetMapping("/station/company/findByStationId")
    List<StationCompanyDTO> getStationCompanyByStationId(@RequestParam("stationId") Integer stationId);
}