package com.yimao.cloud.app.feign;

import com.yimao.cloud.base.constant.Constant;
import com.yimao.cloud.pojo.dto.system.AreaDTO;
import com.yimao.cloud.pojo.dto.system.CustomerAssistantTypeDTO;
import com.yimao.cloud.pojo.dto.system.MessagePushDTO;
import com.yimao.cloud.pojo.dto.system.StationCompanyDTO;
import com.yimao.cloud.pojo.dto.system.StationDTO;
import com.yimao.cloud.pojo.vo.PageVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Date;
import java.util.List;

@FeignClient(name = Constant.MICROSERVICE_SYSTEM)
public interface SystemFeign {

    /**
     * 服务站查询
     *
     * @param province
     * @param city
     * @param region
     * @param online
     * @param pageNum
     * @param pageSize
     */
    @RequestMapping(value = "/station/{pageNum}/{pageSize}", method = RequestMethod.GET, consumes = MediaType.APPLICATION_JSON_VALUE)
    PageVO<StationDTO> listStation(@RequestParam(value = "province", required = false) String province,
                                   @RequestParam(value = "city", required = false) String city,
                                   @RequestParam(value = "region", required = false) String region,
                                   @RequestParam(value = "hraIsOnline", required = false) Boolean hraIsOnline,
                                   @RequestParam(value = "online") Integer online,
                                   @PathVariable(value = "pageNum") Integer pageNum,
                                   @PathVariable(value = "pageSize") Integer pageSize);

    /**
     * 获取附近的服务站门店
     *
     * @param lng
     * @param lat
     * @return
     */
    @RequestMapping(value = "/station/nearby", method = RequestMethod.GET, consumes = MediaType.APPLICATION_JSON_VALUE)
    List<StationDTO> findStationByLngAndLat(@RequestParam(value = "lng") Double lng,
                                            @RequestParam(value = "lat") Double lat,
                                            @RequestParam(value = "online") Integer online,
                                            @RequestParam(value = "hraIsOnline") Boolean hraIsOnline);

    /**
     * 根据省市区查询服务站公司
     */
    @GetMapping(value = "/station/company/information")
    StationCompanyDTO getStationCompanyByPCR(@RequestParam("province") String province,
                                             @RequestParam("city") String city,
                                             @RequestParam("region") String region,
                                             @RequestParam("type") Integer type);

    /**
     * 获取所有省份
     *
     * @return java.util.List<java.lang.Integer>
     * @Author lizhiqiang
     * @Date 2019/3/14
     * @Param []
     */
    @RequestMapping(value = "/area/provinces", method = RequestMethod.GET, consumes = MediaType.APPLICATION_JSON_VALUE)
    List<AreaDTO> listProvince();


    /**
     * lizhiqang 7.8
     *
     * @param terminal
     * @return
     */
    @RequestMapping(value = "/customer/list", method = RequestMethod.GET)
    List<CustomerAssistantTypeDTO> getListAssistant(@RequestParam("terminal") Integer terminal);

    /**
     * 获取展示端下所有分类
     *
     * @param terminal
     * @return
     */
    @RequestMapping(value = "/customer/classify/{terminal}", method = RequestMethod.GET)
    List<CustomerAssistantTypeDTO> getClassifyList(@PathVariable("terminal") Integer terminal);

    /***
     * 功能描述:分页查询消息推送
     *
     * @param: [pageSize, pageNum, type, content, startTime, endTime, devices]
     * @auther: liu yi
     * @date: 2019/5/5 11:34
     * @return: java.lang.Object
     */
    @GetMapping({"/messagePush/query/{pageSize}/{pageNum}"})
    PageVO<MessagePushDTO>  getMessagePushPage(@RequestParam(value = "receiverId", required = false) Integer receiverId,
                                               @RequestParam(value = "pushType", required = false) Integer pushType,
                                               @RequestParam(value = "content", required = false) String content,
                                               @RequestParam(value = "startTime", required = false) Date startTime,
                                               @RequestParam(value = "endTime", required = false) Date endTime,
                                               @RequestParam(value = "devices", required = false) Integer devices,
                                               @RequestParam(value = "app", required = false) Integer app,
                                               @PathVariable(value = "pageSize") Integer pageSize,
                                               @PathVariable(value = "pageNum") Integer pageNum);
    /***
     * 功能描述:更新信息为已读
     *
     * @param: [model, id]
     * @auther: liu yi
     * @date: 2019/5/5 11:36
     * @return: java.lang.String
     */
    @PatchMapping(value = {"/messagePush/{id}"})
    void setMessagePushIsRead(@PathVariable(value = "id") Integer id);

    /***
     * 功能描述:根据id删除消息
     *
     * @param: [id]
     * @auther: liu yi
     * @date: 2019/5/5 11:36
     * @return: java.lang.String
     */
    @DeleteMapping(value = {"/messagePush/{ids}/batch"})
    void deleteMessage(@PathVariable(value = "ids") String ids);
    /***
     * 功能描述:获取用户未读取的信息数量
     *
     * @param: [pageSize, pageNum, type, content, startTime, endTime, devices]
     * @auther: liu yi
     * @date: 2019/5/5 11:34
     * @return: java.lang.Object
     */
    @GetMapping(value = {"/messagePush/unReadCount"})
    Integer getMessageUnReadNum(@RequestParam(value = "receiverId") Integer receiverId,
                             @RequestParam(value = "pushType", required = false) Integer pushType,
                             @RequestParam(value = "content", required = false) String content,
                             @RequestParam(value = "app", required = false) Integer app);

    @RequestMapping(value = "/area", method = RequestMethod.GET)
    List<AreaDTO> areaList();

    /**
     * 查询区县级公司（单个）
     *
     * @param id 区县级公司ID
     */
    @GetMapping(value = "/station/company/{id}")
    StationCompanyDTO getStationCompanyById(@PathVariable(value = "id") Integer id);

    /**
     * 根据省市区查询system_area区域id
     * @param province
     * @param city
     * @param region
     * @return
     */
    @GetMapping(value = "/area/getRegionIdByPCR")
    Integer getRegionIdByPCR(@RequestParam(value = "province") String province, @RequestParam(value = "city") String city, @RequestParam(value = "region") String region);
}
