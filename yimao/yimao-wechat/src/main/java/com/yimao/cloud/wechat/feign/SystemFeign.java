package com.yimao.cloud.wechat.feign;

import com.yimao.cloud.base.constant.Constant;
import com.yimao.cloud.pojo.dto.system.*;
import com.yimao.cloud.pojo.vo.PageVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author liuhao@yimaokeji.com
 * @date 2018-11-12
 */
@FeignClient(name = Constant.MICROSERVICE_SYSTEM)
public interface SystemFeign {

    /**
     * 根据地区查询区域经理
     *
     * @param province 省
     * @param city     市
     * @param region   去
     * @return customerAreaManager
     */
    @GetMapping(value = "/customer/area/manager")
    CustomerAreaManagerDTO customerAreaManagerByArea(@RequestParam(value = "province") String province,
                                                     @RequestParam(value = "city", required = false) String city,
                                                     @RequestParam(value = "region", required = false) String region);

    /**
     * 保存客户留言消息
     *
     * @param customerMessageDTO 留言
     * @return boolean
     */
    @GetMapping(value = "/customer/manager")
    CustomerMessageDTO saveCustomerMessage(@RequestBody CustomerMessageDTO customerMessageDTO);


    /**
     * 分页查询客服问答列表
     *
     * @return list
     */
    @GetMapping(value = "/customer/assistant/{pageNum}/{pageSize}")
    List<CustomerAssistantTypeDTO> listAssistant(@RequestParam(value = "typeCode", required = false) Integer typeCode,
                                                 @RequestParam(value = "questions", required = false) String questions,
                                                 @RequestParam(value = "publish", required = false) Integer publish,
                                                 @RequestParam(value = "recommend", required = false) Integer recommend,
                                                 @RequestParam(value = "terminal", required = false) Integer terminal,
                                                 @RequestParam(value = "typeName", required = false) String typeName,
                                                 @PathVariable(value = "pageNum", required = false) Integer pageNum,
                                                 @PathVariable(value = "pageSize", required = false) Integer pageSize);

    /**
     * 分页获取活动列表
     *
     * @param side       端 1-公众号  2-小程序
     * @param acType     1：普通活动  2：京东兑换活动
     * @param title
     * @param deleteFlag
     * @param pageNum
     * @param pageSize
     * @return
     */
    @GetMapping(value = "/activity/{pageNum}/{pageSize}")
    PageVO<ActivityDTO> listActivity(@RequestParam(value = "side", defaultValue = "1") Integer side,
                                     @RequestParam(value = "acType", required = false) Integer acType,
                                     @RequestParam(value = "title", required = false) String title,
                                     @RequestParam(value = "deleteFlag", required = false) Integer deleteFlag,
                                     @PathVariable(value = "pageNum") Integer pageNum,
                                     @PathVariable(value = "pageSize") Integer pageSize);

    /**
     * 根据活动id查询活动
     *
     * @param id
     * @return
     */
    @GetMapping(value = "/activity/{id}")
    ActivityDTO activityById(@PathVariable("id") Integer id);

    /**
     * 获取广告列表
     *
     * @param apId
     * @param title
     * @param conditions
     * @param pageNum
     * @param pageSize
     * @return
     */
    @GetMapping(value = {"/advert/{pageNum}/{pageSize}"})
    PageVO<AdvertDTO> listAdvert(@RequestParam(value = "apId", required = false) Integer apId,
                                 @RequestParam(value = "title", required = false) String title,
                                 @RequestParam(value = "conditions", required = false) Integer conditions,
                                 @PathVariable(value = "pageNum") Integer pageNum,
                                 @PathVariable(value = "pageSize") Integer pageSize);

    /**
     * 获取广告位列表
     *
     * @param pageNum
     * @param pageSize
     * @return
     */
    @GetMapping(value = {"/advert/position/{pageNum}/{pageSize}"})
    PageVO<AdvertPositionDTO> listAdvertPosition(@PathVariable(value = "pageNum") Integer pageNum,
                                                 @PathVariable(value = "pageSize") Integer pageSize);

    /**
     * 根据id查询代言卡
     *
     * @param id
     * @return
     */
//    @GetMapping("/speak/card/{id}")
//    ProductSpeakCardDTO getSpeakCardById(@PathVariable(value = "id") Integer id);

    /**
     * 根据id查询代言卡
     *
     * @param
     * @return
     */
//    @GetMapping("/speak/card")
//    List<ProductSpeakCardDTO> listSpeakCardByCategory(@RequestParam(value = "cardCategory", defaultValue = "1") Integer cardCategory);

    /**
     * 获取代言卡列表
     *
     * @param cardCategory
     * @param cardTypeCode
     * @param title
     * @param online
     * @param pageNum
     * @param pageSize
     * @return
     */
//    @GetMapping("/speak/card/{pageNum}/{pageSize}")
//    PageVO<ProductSpeakCardDTO> listSpeakCard(@RequestParam(value = "cardCategory", defaultValue = "1") Integer cardCategory,
//                                              @RequestParam(value = "cardTypeCode", required = false) Integer cardTypeCode,
//                                              @RequestParam(value = "title", required = false) String title,
//                                              @RequestParam(value = "online", required = false) Integer online,
//                                              @PathVariable("pageNum") Integer pageNum,
//                                              @PathVariable("pageSize") Integer pageSize);

    /**
     * 根据型号获取代言卡列表
     *
     * @param cardCategory 1-产品型  2-宣传型
     * @return page
     */
//    @GetMapping("/speak/card/type/{pageNum}/{pageSize}")
//    PageVO<ProductSpeakTypeDTO> listProductSpeakType(@RequestParam(value = "cardCategory", required = false) Integer cardCategory,
//                                                     @PathVariable(value = "pageNum") Integer pageNum,
//                                                     @PathVariable(value = "pageSize") Integer pageSize);


    /**==================================================线下门店==========================================================*/


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
     * 根据字典类型查询 字典数据列表
     *
     * @param pageNum   页码
     * @param pageSize  页数
     * @param name      名称
     * @param code      code
     * @param groupCode 类型
     * @param pid       父类id
     * @return dto
     */
    @RequestMapping(value = "/dictionary/{pageNum}/{pageSize}", method = RequestMethod.GET)
    PageVO<DictionaryDTO> findDictionaryByType(@PathVariable(value = "pageNum") Integer pageNum,
                                               @PathVariable(value = "pageSize") Integer pageSize,
                                               @RequestParam(value = "name") String name,
                                               @RequestParam(value = "code") String code,
                                               @RequestParam(value = "groupCode") String groupCode,
                                               @RequestParam(value = "pid") Integer pid);


    /**
     * 获取所有省市区
     *
     * @return list
     * @author liuhao@yimaokeji.com
     */
    @RequestMapping(value = "/area", method = RequestMethod.GET)
    List<AreaDTO> areaList();

    @RequestMapping(value = "/station/referrerStation", method = RequestMethod.GET)
    StationDTO referrerStation();

    @RequestMapping(value = "/customer/list", method = RequestMethod.GET)
    List<CustomerAssistantTypeDTO> getListAssistant(@RequestParam("terminal") Integer terminal);

    /**
     * 根据条件查询 消息记录
     *
     * @param orderId 订单id
     * @param type   1-手机短信 2-消息推送
     * @return list
     */
    @RequestMapping(value = "/message/record", method = RequestMethod.GET)
    List<MessageRecordDTO> messageRecordListByOrderId(@RequestParam("orderId") Long orderId,
                                                      @RequestParam(value = "type", required = false) Integer type);

    @RequestMapping(value = "/message/record", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    void messageRecordSave(@RequestBody MessageRecordDTO recordDTO);

    @RequestMapping(value = "/message/content", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    void messageContentSave(@RequestBody MessageContentDTO dto);

    @GetMapping(value = "/station/information")
    Object getStationByPCR(@RequestParam(value = "province") String province,
                           @RequestParam(value = "city") String city,
                           @RequestParam(value = "region") String region,
                           @RequestParam(value = "type") Integer type);
    
    /***
     * 根据省市区获取区域id
     * @param province
     * @param city
     * @param region
     * @return
     */
    @GetMapping(value = "/area/getRegionIdByPCR")
    public Integer getRegionIdByPCR(@RequestParam String province, @RequestParam String city, @RequestParam String region);
}
