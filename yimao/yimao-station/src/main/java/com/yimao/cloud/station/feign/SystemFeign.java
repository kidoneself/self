package com.yimao.cloud.station.feign;

import com.yimao.cloud.base.constant.Constant;
import com.yimao.cloud.pojo.dto.station.StationMessageDTO;
import com.yimao.cloud.pojo.dto.station.SystemAreaTypeDto;
import com.yimao.cloud.pojo.dto.system.*;
import com.yimao.cloud.pojo.query.station.StationAreaTypeQuery;
import com.yimao.cloud.pojo.query.station.StationMessageQuery;
import com.yimao.cloud.pojo.query.system.*;
import com.yimao.cloud.pojo.vo.PageVO;
import com.yimao.feign.configuration.MultipartSupportConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.ws.rs.core.MediaType;
import java.util.Date;
import java.util.List;

/**
 * 站务管理Feign
 *
 * @author yaoweijun
 * @date 2019-12-24
 */
@FeignClient(name = Constant.MICROSERVICE_SYSTEM, configuration = MultipartSupportConfig.class)
public interface SystemFeign {

    /**
     * 查询区县级公司（单个）
     *
     * @param id 区县级公司ID
     */
    @GetMapping(value = "/station/company/{id}")
    StationCompanyDTO getStationCompanyById(@PathVariable(value = "id") Integer id);

    /**
     * 获取服务站信息
     *
     * @param id 服务站ID
     */
    @GetMapping(value = "/station/{id}")
    StationDTO getStationById(@PathVariable("id") Integer id);

    /**
     * 是否是上线地区校验
     *
     * @param province 省
     * @param city     市
     * @param region   区
     */
    @GetMapping(value = "/online/area/is")
    boolean checkIsOnline(@RequestParam(name = "province") String province,
                          @RequestParam(name = "city") String city,
                          @RequestParam(name = "region") String region);

    /**
     * @Author lizhiqiang
     * @Date 2019/3/1
     */
    @GetMapping(value = "/station/information")
    StationDTO getStationByPRC(@RequestParam(name = "province") String province,
                               @RequestParam(name = "city") String city,
                               @RequestParam(name = "region") String region,
                               @RequestParam(name = "type") Integer type);


    /**
     * 根据区域ID查询区域级联信息
     *
     * @param id 区域ID
     * @return
     */
    @GetMapping(value = "/area/name/{id}")
    AreaInfoDTO getAreaInfoById(@PathVariable("id") Integer id);


    /***
     * 根据省市区、服务类型(售前售后)查询服务站公司
     * @param province
     * @param city
     * @param region
     * @param type
     * @return
     */
    @GetMapping(value = "/station/company/information")
    StationCompanyDTO getStationCompanyByPCR(@RequestParam(name = "province") String province,
                                             @RequestParam(name = "city") String city,
                                             @RequestParam(name = "region") String region,
                                             @RequestParam(name = "type") Integer type);

    //根据服务站id查询服务站公司名称
    @RequestMapping(value = {"/station/company/name/{stationId}"}, method = {RequestMethod.GET})
    String getStationCompanyNameById(@PathVariable("stationId") Integer stationId);

    @GetMapping(value = "/station/recommendId")
    StationDTO getStationByDistributorId(@RequestParam(name = "recommendId") Integer recommendId);

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
     * 根据服务站门店id查询服务站公司信息
     *
     * @param stationId
     * @return
     */
    @GetMapping("/station/company/findByStationId")
    List<StationCompanyDTO> getStationCompanyByStationId(@RequestParam("stationId") Integer stationId);

    /**
     * 根据服务站公司id获取服务站门店和服务站服务区域
     *
     * @param stationCompanyId 服务站公司id
     * @return
     */
    @GetMapping(value = "/stationAndServiceArea/byCompanyId")
    List<StationDTO> getStationAndServiceArea(@RequestParam("stationCompanyId") Integer stationCompanyId);

    /**
     * 获取所有省市区
     */
    @GetMapping(value = "/area")
    List<AreaDTO> areaList();

    /**
     * 获取所有服务站及其服务区域（超级管理员查询）
     */
    @GetMapping(value = "/allStation")
    List<StationDTO> getAllStation();

    @GetMapping(value = "/station/admin/{pageNum}/{pageSize}", consumes = MediaType.APPLICATION_JSON)
    public PageVO<StationDTO> adminStationList(@PathVariable("pageNum") Integer pageNum,
                                               @PathVariable("pageSize") Integer pageSize,
                                               @RequestBody StationQuery query);

    /**
     * 概况-消息通知-系统消息
     *
     * @param query
     * @return
     */
    @GetMapping(value = "/messagePush/station/query/{pageSize}/{pageNum}", consumes = MediaType.APPLICATION_JSON)
    PageVO<MessagePushDTO> getStationMessage(@PathVariable("pageNum") Integer pageNum,
                                             @PathVariable("pageSize") Integer pageSize, @RequestBody StationMessageQuery query);

    /**
     * 概况-消息通知-系统消息-消息已读
     *
     * @param messagePush
     * @return
     */
    @PostMapping(value = "/messagePush/station/readMessage", consumes = MediaType.APPLICATION_JSON)
    void readMessage(@RequestBody MessagePushDTO messagePush);

    /**
     * 概况-消息通知-系统消息-短信发送
     *
     * @param stationMessageDTO
     * @return
     */
    @PostMapping(value = "/messagePush/station/sendMessage", consumes = MediaType.APPLICATION_JSON)
    void sendMessage(@RequestBody StationMessageDTO stationMessageDTO);

    /**
     * 服务站--服务站公司-修改联系人
     *
     * @return
     */
    @PostMapping(value = "/station/company/editContactInfo", consumes = MediaType.APPLICATION_JSON)
    void editStationCompanyContactInfo(@RequestBody StationCompanyDTO update);

    /**
     * 服务站--服务站公司-修改联系人
     *
     * @return
     */
    @PostMapping(value = "/station/editContactInfo", consumes = MediaType.APPLICATION_JSON)
    void editStationContactInfo(@RequestBody StationDTO update);

    /**
     * 帮助中心根据问答类型查询更多问答内容
     *
     * @param pageNum
     * @param pageSize
     * @param typeCode
     * @return
     */
    @GetMapping(value = "/station/helpCenter/more/{typeCode}/{pageSize}/{pageNum}")
    Object helpCenterMoreByTypeCode(@PathVariable("pageNum") Integer pageNum,
                                    @PathVariable("pageSize") Integer pageSize, @PathVariable("typeCode") Integer typeCode);

    /**
     * 帮助中心默认展示数据
     *
     * @return
     */
    @GetMapping(value = "/station/helpCenter/list")
    Object helpCenterData();

    /**
     * 帮助中心搜索查询
     *
     * @param pageNum
     * @param pageSize
     * @param keywords
     * @return
     */
    @GetMapping(value = "/station/helpCenter/searchList/{pageNum}/{pageSize}")
    Object helpCenterQuestionSearchList(@PathVariable("pageNum") Integer pageNum,
                                        @PathVariable("pageSize") Integer pageSize, @RequestParam("keywords") String keywords);

    @PostMapping(value = "/suggest", consumes = MediaType.APPLICATION_JSON)
    void submitSuggest(@RequestBody SuggestDTO dto);

    /**
     * 内容--建议反馈--建议列表查询
     *
     * @param pageNum  第几页
     * @param pageSize 每页大小
     * @return
     */
    @RequestMapping(value = "/suggest/{pageNum}/{pageSize}", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON)
    PageVO<SuggestDTO> pageSuggest(@PathVariable(value = "pageNum") Integer pageNum,
                                   @PathVariable(value = "pageSize") Integer pageSize,
                                   @RequestBody SuggestQuery query);

    /**
     * 建议类型筛选条件下拉框
     */
    @RequestMapping(value = "/suggestType/list", method = RequestMethod.GET)
    List<SuggestTypeDTO> listSuggestType(@RequestParam("terminal") Integer terminal);

    /**
     * 共通单个文件上传接口
     *
     * @param file   文件
     * @param folder 上传目录，为空会上传到common目录下
     * @param remark 备注
     */
    @RequestMapping(value = "/common/upload/single", method = RequestMethod.POST, consumes = org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE)
    String upload(@RequestPart("file") MultipartFile file,
                  @RequestParam(value = "folder", required = false) String folder,
                  @RequestParam(value = "remark", required = false) String remark);

    /**
     * 获取附件名称
     *
     * @param path   文件路径(相对路径)
     * @param folder 文件目录
     */
    @RequestMapping(value = "/suggest/getFileName", method = RequestMethod.GET)
    String getFileName(@RequestParam(value = "path") String path,
                       @RequestParam(value = "folder") String folder);

    /**
     * 全局帮助与服务展示列表
     *
     * @return
     */
    @GetMapping("/station/helpAndService/list")
    Object getHelpAndServiceList();

    /**
     * 根据id集合查询服务站基本信息
     *
     * @param stationQuery
     * @return
     */
    @PostMapping(value = "/station/getStationListByIds", consumes = MediaType.APPLICATION_JSON)
    List<StationDTO> getStationListByIds(@RequestBody StationQuery stationQuery);

    /**
     * 根据绑定门店查询绑定区域的售前售后属性
     *
     * @param areaList
     * @return
     */
    @PostMapping(value = "/station/getAdminStationAreaType", consumes = MediaType.APPLICATION_JSON)
    List<SystemAreaTypeDto> getAdminStationAreaType(@RequestBody StationAreaTypeQuery query);

    /**
     * 根据省市区查询售前售后门店集合
     *
     * @param province
     * @param city
     * @param region
     * @return
     */
    @GetMapping(value = "/station/companyList")
    List<StationCompanyDTO> getStationCompanyListByPCR(@RequestParam(name = "province") String province,
                                                       @RequestParam(name = "city") String city,
                                                       @RequestParam(name = "region") String region);

    /**
     * 分页查询服务站公司库存
     */
    @PostMapping(value = "/store/house/stationCompany/{pageNum}/{pageSize}", consumes = MediaType.APPLICATION_JSON)
    PageVO<StationCompanyStoreHouseDTO> pageStationCompanyStoreHouse(@RequestBody StationCompanyStoreHouseQuery query,
                                                                     @PathVariable(value = "pageNum") Integer pageNum,
                                                                     @PathVariable(value = "pageSize") Integer pageSize);

    /**
     * 分页查询服务站门店库存
     */
    @PostMapping(value = "/store/house/station/{pageNum}/{pageSize}", consumes = MediaType.APPLICATION_JSON)
    PageVO<StationStoreHouseDTO> pageStationStoreHouse(@RequestBody StationStoreHouseQuery query,
                                                       @PathVariable(value = "pageNum") Integer pageNum,
                                                       @PathVariable(value = "pageSize") Integer pageSize);

    @PostMapping(value = "/goods/apply/station/history/list/{pageNum}/{pageSize}")
    PageVO<StationCompanyGoodsApplyDTO> pageGoodsApplyStationHistory(@PathVariable(value = "pageNum") Integer pageNum,
                                                                     @PathVariable(value = "pageSize") Integer pageSize,
                                                                     @RequestParam(value = "stationCompanyId") Integer stationCompanyId,
                                                                     @RequestParam(value = "categoryId", required = false) Integer categoryId,
                                                                     @RequestParam(value = "status", required = false) Integer status,
                                                                     @RequestParam(value = "startTime", required = false) Date startTime,
                                                                     @RequestParam(value = "endTime", required = false) Date endTime);

    @PostMapping(value = "/goods/apply/station/list/{pageNum}/{pageSize}")
    PageVO<StationCompanyGoodsApplyDTO> pageGoodsApplyStation(@PathVariable(value = "pageNum") Integer pageNum,
                                                              @PathVariable(value = "pageSize") Integer pageSize,
                                                              @RequestParam(value = "stationCompanyId") Integer stationCompanyId,
                                                              @RequestParam(value = "categoryId", required = false) Integer categoryId,
                                                              @RequestParam(value = "startTime", required = false) Date startTime,
                                                              @RequestParam(value = "endTime", required = false) Date endTime);

    @PostMapping(value = "/goods/apply/anewSubmit", consumes = MediaType.APPLICATION_JSON)
    Void goodsApplyAnewSubmit(@RequestBody StationCompanyGoodsApplyDTO dto);

    @PostMapping(value = "/goods/apply/save", consumes = MediaType.APPLICATION_JSON)
    Void goodsApplySave(@RequestBody StationCompanyGoodsApplyDTO dto);

    @PostMapping(value = "/goods/apply/confirm/{id}")
    Void goodsApplyConfirm(@PathVariable(value = "id") String id,
                           @RequestParam(value = "confirmImg") String confirmImg);

    @PostMapping(value = "/goods/apply/cancel/{id}")
    Void goodsApplyCancel(@PathVariable(value = "id") String id);

    /**
     * 物资分类一二级筛选类
     */
    @GetMapping("/goods/category/filterItem")
    List<GoodsCategoryDTO> getGoodsCategoryFilter(@RequestParam(value = "type", required = false) Integer type);

    @GetMapping("/goods/filterItem/{goodCategoryId}")
    List<GoodsMaterialsDTO> getGoodsByCategoryId(@PathVariable(value = "goodCategoryId") Integer goodCategoryId);

    @GetMapping("/goods/{type}/{pageNum}/{pageSize}")
    PageVO<GoodsMaterialsDTO> goodsPage(@PathVariable("pageNum") Integer pageNum,
                                        @PathVariable("pageSize") Integer pageSize,
                                        @PathVariable("type") Integer type,
                                        GoodsMaterialsQuery query);

    /**
     * 校验某服务站是否有指定物资的库存，有则返回该物资信息，无则返回null
     */
    @PostMapping(value = "/store/house/check/{stationId}/{goodsId}")
    GoodsMaterialsDTO checkStationGoodsIsHaveStock(@PathVariable(value = "stationId") Integer stationId,
                                                   @PathVariable(value = "goodsId") Integer goodsId);

    /**
     * 服务站库存借调操作
     *
     * @param transfer
     * @return
     */
    @PostMapping(value = "/store/house/station/transfer}", consumes = MediaType.APPLICATION_JSON)
    boolean transferStationStock(@RequestBody StationStockTransferDTO transfer);

    @GetMapping(value = "/area/getRegionIdByPCR")
    Integer getRegionIdByPCR(@RequestParam(value = "province") String province, @RequestParam(value = "city") String city, @RequestParam(value = "region") String region);


}
