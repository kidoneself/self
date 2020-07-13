package com.yimao.cloud.system.feign;

import com.github.pagehelper.Page;
import com.yimao.cloud.base.constant.Constant;
import com.yimao.cloud.pojo.dto.hra.*;
import com.yimao.cloud.pojo.vo.PageVO;
import com.yimao.cloud.pojo.vo.hra.HraCardVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

/**
 * @author Lizhqiang
 * @date 2019/1/26
 */
@FeignClient(name = Constant.MICROSERVICE_HRA)
public interface HraFeign {


    /**
     * @param pageNum
     * @param pageSize
     * @param beginTime     预约开始时间
     * @param endTime       预约结束时间
     * @param userSource    用户来源
     * @param mobile        手机号
     * @param ticketNo      评估卡
     * @param province      省
     * @param city          市
     * @param region        区
     * @param name          姓名
     * @param reserveStatus 预约状态  1-预约中 2-预约到期
     * @Description: HRA评估-预约列表
     * @author ycl
     * @Return: com.yimao.cloud.pojo.vo.PageVO<com.yimao.cloud.pojo.dto.hra.HraTicketResultDTO>
     * @Create: 2019/2/12 10:53
     */
    @RequestMapping(value = "/ticket/reservation/{pageNum}/{pageSize}", method = RequestMethod.GET)
    PageVO<HraTicketResultDTO> listTicket(@PathVariable(value = "pageNum") Integer pageNum,
                                          @PathVariable(value = "pageSize") Integer pageSize,
                                          @RequestParam(value = "beginTime", required = false) String beginTime,
                                          @RequestParam(value = "endTime", required = false) String endTime,
                                          @RequestParam(value = "userSource", required = false) Integer userSource,
                                          @RequestParam(value = "mobile", required = false) String mobile,
                                          @RequestParam(value = "ticketNo", required = false) String ticketNo,
                                          @RequestParam(value = "province", required = false) String province,
                                          @RequestParam(value = "city", required = false) String city,
                                          @RequestParam(value = "region", required = false) String region,
                                          @RequestParam(value = "name", required = false) String name,
                                          @RequestParam(value = "reserveStatus", required = false) Integer reserveStatus,
                                          @RequestParam(value = "flag", required = false) Integer flag,
                                          @RequestParam(value = "hasUpload", required = false) Integer hasUpload,
                                          @RequestParam(value = "userId", required = false) Integer userId,
                                          @RequestParam(value = "ticketType", required = false) String ticketType);

    /**
     * RA评估-评估列表(已体检的)
     *
     * @param pageNum       分页页码
     * @param pageSize      分页大小
     * @param beginTime     开始时间
     * @param endTime       结束时间
     * @param userSource    用户来源
     * @param mobile        手机号
     * @param ticketNo      评估卡
     * @param province      省
     * @param city          市
     * @param region        区
     * @param name          姓名
     * @param reserveStatus 预约状态
     * @param hasUpload     是否上传
     * @param userId        用户ID
     * @param ticketType    体检卡型号
     * @Description: HRA评估-评估列表(已体检的)
     * @author ycl
     * @Return: com.yimao.cloud.pojo.vo.PageVO<com.yimao.cloud.pojo.dto.hra.HraTicketResultDTO>
     * @Create: 2019/2/12 11:19
     */
    @RequestMapping(value = "/ticket/use/{pageNum}/{pageSize}", method = RequestMethod.GET)
    PageVO<HraTicketResultDTO> list(@PathVariable(value = "pageNum") Integer pageNum,
                                    @PathVariable(value = "pageSize") Integer pageSize,
                                    @RequestParam(value = "beginTime", required = false) String beginTime,
                                    @RequestParam(value = "endTime", required = false) String endTime,
                                    @RequestParam(value = "userSource", required = false) String userSource,
                                    @RequestParam(value = "mobile", required = false) String mobile,
                                    @RequestParam(value = "ticketNo", required = false) String ticketNo,
                                    @RequestParam(value = "province", required = false) String province,
                                    @RequestParam(value = "city", required = false) String city,
                                    @RequestParam(value = "region", required = false) String region,
                                    @RequestParam(value = "name", required = false) String name,
                                    @RequestParam(value = "flag", required = false) Integer flag,
                                    @RequestParam(value = "reserveStatus", required = false) Integer reserveStatus,
                                    @RequestParam(value = "hasUpload", required = false) Integer hasUpload,
                                    @RequestParam(value = "userId", required = false) Integer userId,
                                    @RequestParam(value = "ticketType", required = false) String ticketType);


    /**
     * 评估卡管理-F卡管理(服务站专用卡)
     *
     * @param pageNum     第几页
     * @param pageSize    每页显示条数
     * @return page
     */
    @RequestMapping(value = "/ticket/special/{pageNum}/{pageSize}", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    PageVO<HraCardVO> allotTicket(@RequestBody HraQueryDTO query,
                                  @PathVariable(value = "pageNum") Integer pageNum,
                                  @PathVariable(value = "pageSize") Integer pageSize);

    /**
     * 评估卡管理-体检卡列表
     *
     * @param pageNum  页数大小
     * @param pageSize 每页显示数量
     * @Description: 评估卡管理-体检卡列表
     * @author ycl
     * @param: province 省
     * @Return: org.springframework.http.ResponseEntity
     * @Create: 2019/2/19 9:45
     */
    @RequestMapping(value = "/ticket/physical/{pageNum}/{pageSize}", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    PageVO<HraPhysicalDTO> physical(@RequestBody HraQueryDTO query,
                                    @PathVariable(value = "pageNum", required = false) Integer pageNum,
                                    @PathVariable(value = "pageSize", required = false) Integer pageSize);


    /**
     * @Description: F卡管理-分配F卡
     * @author ycl
     * @param: userName
     * @Return: java.util.List<com.yimao.cloud.pojo.dto.hra.HraCardDTO>
     * @Create: 2019/2/19 16:24
     */
    @RequestMapping(value = "/ticket/allot", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    void allot(@RequestBody HraFCardDTO hraFCardDTO);

    //===========================================Hra 设备管理开始===================================================================

    /**
     * HRA设备管理
     *
     * @param pageNum  第几页
     * @param pageSize 每页显示条数
     * @Description: HRA评估-HRA设备管理
     * @author ycl
     * @Return: org.springframework.http.ResponseEntity
     * @Create: 2019/2/14 10:40
     */
    @RequestMapping(value = {"/device/{pageNum}/{pageSize}"}, method = RequestMethod.POST,consumes = MediaType.APPLICATION_JSON_VALUE)
    PageVO<HraDeviceDTO> queryStationOnline(@RequestBody HraDeviceQuery query,
                                            @PathVariable(value = "pageNum") Integer pageNum,
                                            @PathVariable(value = "pageSize") Integer pageSize);

    @RequestMapping(value = "/device/{id}", method = RequestMethod.DELETE)
    void deleteDevice(@PathVariable("id") Integer id);

    /**
     * @Description: HRA设备管理-设备上线
     * @author ycl
     * @param: hraDeviceDTO
     * @Return: void
     * @Create: 2019/2/18 11:17
     */
    @RequestMapping(value = "/device/online", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    void saveStationOnline(@RequestBody HraDeviceOnlineDTO hraDeviceOnlineDTO);

    /**
     * @Description: 修改设备上下线
     * @author ycl
     * @param: * @param hraDeviceOnlineDTO
     * @Return: void
     * @Create: 2019/3/1 9:24
     */
    @RequestMapping(value = "/device", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE)
    void updateDevice(@RequestBody HraDeviceOnlineDTO hraDeviceOnlineDTO);


    @RequestMapping(value = "/device", method = RequestMethod.DELETE)
    void batchDelete(@RequestParam("ids") Integer[] ids);

    //===========================================Hra 设备管理结束===================================================================

    @RequestMapping(value = "/assign", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    void assignCard(@RequestBody HraAssignDTO hraAssignDTO);


    /**
     * @Description: 体检卡-编辑
     * @author ycl
     * @param: id
     * @Return: void
     * @Create: 2019/2/27 15:11
     */
    @RequestMapping(value = "/ticket/physical", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE)
    void update(@RequestBody HraTicketDTO hraAssignDTO);


    /**
     * @Description: F卡管理-是否禁用功能
     * @author ycl
     * @param: hraAllotTicketDTO
     * @Return: void
     * @Create: 2019/2/28 14:59
     */
    @RequestMapping(value = "/ticket/status", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE)
    void updateStatus(@RequestBody HraAllotTicketDTO hraAllotTicketDTO);


    //根据服务站ID获取服务站状态
    @RequestMapping(value = "/device/status/{stationId}", method = RequestMethod.GET)
    boolean getHraDeviceStatus(@PathVariable("stationId") Integer stationId);

    @RequestMapping(value = "/activity/exchange/{pageNum}/{pageSize}", method = RequestMethod.GET)
    PageVO<ActivityExchangeDTO> page(@RequestParam(value = "side", required = false) Integer side,
                                     @RequestParam(value = "channel", required = false) Integer channel,
                                     @RequestParam(value = "exchangeCode", required = false) String exchangeCode,
                                     @RequestParam(value = "batchNumber", required = false) String batchNumber,
                                     @RequestParam(value = "exchangeStatus", required = false) String exchangeStatus,
                                     @RequestParam(value = "ticketNo", required = false) String ticketNo,
                                     @RequestParam(value = "ticketStatus", required = false) Integer ticketStatus,
                                     @PathVariable(value = "pageNum") Integer pageNum,
                                     @PathVariable(value = "pageSize") Integer pageSize);

    @RequestMapping(value = "/activity/create/ticket", method = RequestMethod.POST)
    void saveHraExchange(@RequestParam(value = "count") Integer count,
                         @RequestParam(value = "beginTime", required = false) Date beginTime,
                         @RequestParam(value = "endTime", required = false) Date endTime,
                         @RequestParam(value = "side") Integer side,
                         @RequestParam(value = "channel") Integer channel,
                         @RequestParam(value = "channelName") String channelName);

    @RequestMapping(value = "/exchange/set", method = RequestMethod.POST)
    void exChangeSet(@RequestParam(value = "terminal") Integer terminal,
                     @RequestParam(value = "limitType") Integer limitType,
                     @RequestParam(value = "times") Integer times);


    /*@RequestMapping(value = "/ticket/physical/export", method = RequestMethod.GET)
    List<HraExportPhysicalDTO> exportPhysical(@RequestParam(value = "province", required = false) String province,
                                              @RequestParam(value = "city", required = false) String city,
                                              @RequestParam(value = "region", required = false) String region,
                                              @RequestParam(value = "stationName", required = false) String stationName,
                                              @RequestParam(value = "currentUserId", required = false) String currentUserId,
                                              @RequestParam(value = "cardId", required = false) String cardId,
                                              @RequestParam(value = "userType", required = false) String userType,
                                              @RequestParam(value = "minSurplus", required = false) Integer minSurplus,
                                              @RequestParam(value = "maxSurplus", required = false) Integer maxSurplus,
                                              @RequestParam(value = "ticketStatus", required = false) Integer ticketStatus,
                                              @RequestParam(value = "beginTime", required = false) String beginTime,
                                              @RequestParam(value = "endTime", required = false) String endTime,
                                              @RequestParam(value = "cardType", required = false) String cardType);*/

    @RequestMapping(value = "/device/export", method = RequestMethod.GET)
    List<HraDeviceExportDTO> exportDevice(@RequestParam(value = "province", required = false) String province,
                                          @RequestParam(value = "city", required = false) String city,
                                          @RequestParam(value = "region", required = false) String region,
                                          @RequestParam(value = "name", required = false) String name,
                                          @RequestParam(value = "deviceType", required = false) Integer deviceType,
                                          @RequestParam(value = "deviceId", required = false) String deviceId,
                                          @RequestParam(value = "online", required = false) Integer online,
                                          @RequestParam(value = "minTime", required = false) String minTime,
                                          @RequestParam(value = "maxTime", required = false) String maxTime);

    @RequestMapping(value = "/ticket/special/export", method = RequestMethod.GET)
    List<HraAllotTicketExportDTO> exportSpecialTicket(@RequestParam(value = "province", required = false) String province,
                                                      @RequestParam(value = "city", required = false) String city,
                                                      @RequestParam(value = "region", required = false) String region,
                                                      @RequestParam(value = "stationName", required = false) String stationName,
                                                      @RequestParam(value = "isExpire", required = false) String isExpire,
                                                      @RequestParam(value = "minSurplus", required = false) Integer minSurplus,
                                                      @RequestParam(value = "maxSurplus", required = false) Integer maxSurplus,
                                                      @RequestParam(value = "state", required = false) Integer state,
                                                      @RequestParam(value = "beginTime", required = false) String beginTime,
                                                      @RequestParam(value = "endTime", required = false) String endTime,
                                                      @RequestParam(value = "ticketNo", required = false) String ticketNo);


    /**
     * 评估列表导出
     */
    /*@RequestMapping(value = "/ticket/export", method = RequestMethod.GET)
    List<HraExportReservationDTO> getTicketInfoToExport(@RequestParam(value = "beginTime", required = false) String beginTime,
                                                        @RequestParam(value = "endTime", required = false) String endTime,
                                                        @RequestParam(value = "userSource", required = false) Integer userSource,
                                                        @RequestParam(value = "mobile", required = false) String mobile,
                                                        @RequestParam(value = "ticketNo", required = false) String ticketNo,
                                                        @RequestParam(value = "province", required = false) String province,
                                                        @RequestParam(value = "city", required = false) String city,
                                                        @RequestParam(value = "region", required = false) String region,
                                                        @RequestParam(value = "name", required = false) String name,
                                                        @RequestParam(value = "reserveStatus", required = false) Integer reserveStatus,
                                                        @RequestParam(value = "userId", required = false) Integer userId,
                                                        @RequestParam(value = "ticketType", required = false) String ticketType);
*/

    @RequestMapping(value = "/device/station", method = RequestMethod.GET)
    List<Integer> getHraStationIds();

}




