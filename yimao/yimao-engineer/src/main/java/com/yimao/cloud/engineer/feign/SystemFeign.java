package com.yimao.cloud.engineer.feign;

import com.yimao.cloud.base.constant.Constant;
import com.yimao.cloud.pojo.dto.system.*;
import com.yimao.cloud.pojo.vo.PageVO;
import com.yimao.feign.configuration.MultipartSupportConfig;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.List;

/**
 * 描述：系统微服务的接口列表
 *
 * @author Zhang Bo
 * @date 2019/3/9.
 */
@FeignClient(name = Constant.MICROSERVICE_SYSTEM, configuration = MultipartSupportConfig.class)
public interface SystemFeign {

    /**
     * 共通单个文件上传接口
     *
     * @param file   文件
     * @param folder 上传目录，为空会上传到common目录下
     * @param remark 备注
     */
    @RequestMapping(value = "/common/upload/single", method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    String upload(@RequestPart("file") MultipartFile file,
                  @RequestParam(value = "folder", required = false) String folder,
                  @RequestParam(value = "remark", required = false) String remark);

    @RequestMapping(value = "/station", method = RequestMethod.GET)
    List<StationDTO> getStationByPCR(@RequestParam(value = "province") String province,
                                     @RequestParam(value = "city") String city,
                                     @RequestParam(value = "region") String region,
                                     @RequestParam(value = "stationName", required = false) String stationName);

    /**
     * 服务站是否升级新流程校验
     *
     * @param province 省
     * @param city     市
     * @param region   区
     */
    @GetMapping(value = "/onlinearea")
    OnlineAreaDTO getOnlineAreaByPCR(@RequestParam(name = "province") String province,
                                     @RequestParam(name = "city") String city,
                                     @RequestParam(name = "region") String region);

    /**
     * 根据老服务站id查询服务站门店信息
     *
     * @param oldId 老服务站门店ID
     */
    @GetMapping(value = "/station/old/{oldId}")
    StationDTO getStationByOldId(@PathVariable("oldId") String oldId);

    /**
     * 根据服务站门店id查询服务站公司信息
     *
     * @param stationId
     * @return
     */
    @GetMapping("/station/company/findByStationId")
    List<StationCompanyDTO> getStationCompanyByStationId(@RequestParam("stationId") Integer stationId);

    /***
     *
     * 根据省市区、服务类型查询服务站公司
     * @param province
     * @param city
     * @param region
     * @param type
     * @return
     */
    @GetMapping(value = "/station/company/information")
    StationCompanyDTO getStationCompanyByPCR(@RequestParam("province") String province,
                                             @RequestParam("city") String city,
                                             @RequestParam("region") String region,
                                             @RequestParam("type") Integer type);

//---------------------消息记录 start----------------------------------

    /***
     * 功能描述:分页查询消息推送
     *
     * @param: [pageSize, pageNum, type, content, startTime, endTime, devices]
     * @auther: liu yi
     * @date: 2019/5/5 11:34
     * @return: java.lang.Object
     */
    @GetMapping("/messagePush/query/{pageSize}/{pageNum}")
    PageVO<MessagePushDTO> getMessagePushPage(@RequestParam(value = "receiverId", required = false) Integer receiverId,
                                              @RequestParam(value = "pushType", required = false) Integer pushType,
                                              @RequestParam(value = "content", required = false) String content,
                                              @RequestParam(value = "startTime", required = false) Date startTime,
                                              @RequestParam(value = "endTime", required = false) Date endTime,
                                              @RequestParam(value = "devices", required = false) Integer devices,
                                              @RequestParam(value = "app", required = false) Integer app,
                                              @PathVariable(value = "pageSize") Integer pageSize,
                                              @PathVariable(value = "pageNum") Integer pageNum);

    /***
     * 功能描述:根据id获取详情
     *
     * @param: [model, id]
     * @auther: liu yi
     * @date: 2019/5/5 11:35
     * @return: java.lang.String
     */
    @GetMapping(value = {"/messagePush/{id}"})
    MessagePushDTO getMessagePushDetails(@PathVariable(value = "id") Integer id);

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


    /**
     * 根据id删除消息
     * @param id
     * @return
     */
    @DeleteMapping(value = {"/messagePush/{id}"})
    Object delete(@PathVariable("id") Integer id);


    /***
     * 功能描述:根据ids删除消息
     *
     * @param: [id]
     * @auther: liu yi
     * @date: 2019/5/5 11:36
     * @return: java.lang.String
     */
    @DeleteMapping(value = {"/messagePush/{ids}/batch"})
    void deleteMessage(@PathVariable(value = "ids") String ids);

    /**
     * 获取所有省市区
     */
    @GetMapping(value = "/area")
    List<AreaDTO> areaList();

    /**
     * 查询所有下级区域
     */
    @GetMapping("/area/subs")
    List<AreaDTO> findCityOrCounty(@RequestParam(value = "pid", required = false) Integer pid);

    /**
     * 描述：根据省市区和产品ID查询库存数量
     */
    @GetMapping(value = "/store/house/count")
    Integer getStoreHouseCount(@RequestParam(value = "province") String province,
                               @RequestParam(value = "city") String city,
                               @RequestParam(value = "region") String region,
                               @RequestParam(value = "productId") Integer productId,
                               @RequestParam(value = "special", required = false, defaultValue = "0") Integer special);

    @GetMapping(value = "/reason")
    List<ReasonDTO> listReason();

    @GetMapping(value = "/station/information")
    StationDTO getStationByPRC(@RequestParam(name = "province") String province,
                               @RequestParam(name = "city") String city,
                               @RequestParam(name = "region") String region,
                               @RequestParam(name = "type") Integer type);

    /***
     * 根据省市区查区域id
     * @param province
     * @param city
     * @param region
     * @return
     */
    @GetMapping(value = "/area/getRegionIdByPCR")
    Integer getRegionIdByPCR(@RequestParam(value = "province") String province, @RequestParam(value = "city") String city, @RequestParam(value = "region") String region);
    
    /**
     * 根据产品三级类目id查询匹配耗材列表
     * @param productCategoryId
     * @return
     */
    @GetMapping(value ="/goods/material/{productCategoryId}")
    List<GoodsMaterialsDTO> getMaterialListByCategoryId(@PathVariable(value = "productCategoryId")Integer productCategoryId);
    
    /**
     * 安装工app获取门店的可用库存
     * @param stationId
     * @param type
     * @return
     */
    @GetMapping(value ="/store/house/station/availableDeviceStock/{stationId}")
    List<StationStoreHouseDTO> availableStationDeviceStock(@PathVariable(value = "stationId")Integer stationId);
    
    /**
     * 安装工app获取门店的可用库存(耗材)
     * @param stationId
     * @param adaptionModel
     * @return
     */
    @GetMapping(value ="/store/house/station/availableMaterialStock/{stationId}")
    List<GoodsCategoryDTO> availableStationMaterialStock(@PathVariable(value = "stationId")Integer stationId,@RequestParam(value = "adaptionModel" ,required=false)String adaptionModel);
}
