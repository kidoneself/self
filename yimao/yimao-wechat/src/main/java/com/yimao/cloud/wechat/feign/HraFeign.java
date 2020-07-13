package com.yimao.cloud.wechat.feign;

import com.yimao.cloud.base.constant.Constant;
import com.yimao.cloud.pojo.dto.hra.*;
import com.yimao.cloud.pojo.vo.PageVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author liuhao@yimaokeji.com
 * @version v2.0
 * @date 2019/4/2
 */
@FeignClient(name = Constant.MICROSERVICE_HRA)
public interface HraFeign {

    /**
     * 查询当前用户所有的评估卡
     *
     * @param pageNum  页码
     * @param pageSize 页数
     * @return page
     */
    @RequestMapping(value = {"/ticket/card/{pageNum}/{pageSize}"}, method = RequestMethod.GET)
    PageVO<HraCardDTO> hraCardByUser(@PathVariable("pageNum") Integer pageNum,
                                     @PathVariable("pageSize") Integer pageSize);

    /**
     * 根据评估卡ID查询评估卷
     *
     * @param cardId 卡ID
     * @return page
     */
    @RequestMapping(value = {"/ticket/card/{cardId}"}, method = RequestMethod.GET)
    List<HraTicketDTO> ticketByCardId(@PathVariable("cardId") String cardId);


    /**
     * 根据体检卡号获取ticket
     *
     * @param ticketNo 体检卡号
     * @return HraTicketDTO
     */
    @RequestMapping(value = {"/hra/ticket/{ticketNo}"}, method = RequestMethod.GET)
    HraTicketDTO ticketByTicketNo(@PathVariable(value = "ticketNo") String ticketNo);

    /**
     * 查询所有可预约的评估卷
     *
     * @param pageNum  页码
     * @param pageSize 页数
     * @return page
     */
    @RequestMapping(value = {"/ticket/reserve/{pageNum}/{pageSize}"}, method = RequestMethod.GET)
    PageVO<HraTicketDTO> reserveTicket(@PathVariable("pageNum") Integer pageNum,
                                       @PathVariable("pageSize") Integer pageSize);

    /**
     * 预约评估
     *
     * @param reserveDTO 预约对象
     * @return page
     */
    @RequestMapping(value = "/reserve", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    HraTicketDTO reserveCreate(@RequestBody ReserveDTO reserveDTO);

    /**
     * 我的预约列表
     *
     * @param pageNum  页码
     * @param pageSize 页数
     * @return page
     */
    @RequestMapping(value = {"/reserve/{pageNum}/{pageSize}"}, method = RequestMethod.GET)
    PageVO<HraTicketDTO> reserveList(@PathVariable("pageNum") Integer pageNum,
                                     @PathVariable("pageSize") Integer pageSize);

    /**
     * 取消预约
     *
     * @param ticketId 体检劵id
     * @return int
     */
    @RequestMapping(value = {"/reserve/cancel/{ticketId}"}, method = RequestMethod.PUT)
    Integer cancelReserve(@PathVariable("ticketId") Integer ticketId);

    /**
     * 体检报告列表
     *
     * @param pageNum  页码
     * @param pageSize 页数
     * @return page
     */
    @RequestMapping(value = {"/report/{pageNum}/{pageSize}"}, method = RequestMethod.GET)
    PageVO<ReportDTO> reportRecordList(@PathVariable("pageNum") Integer pageNum,
                                       @PathVariable("pageSize") Integer pageSize);

    /**
     * 添加体检报告
     *
     * @param phone    手机号
     * @param ticketNo 体检劵
     * @return int
     */
    @RequestMapping(value = {"/report"}, method = RequestMethod.POST)
    Integer addReportRecord(@RequestParam("phone") String phone, @RequestParam("ticketNo") String ticketNo);


    /**
     * 删除体检报告(逻辑删除)
     *
     * @param reportRecordId 体检报告id
     * @return int
     */
    @RequestMapping(value = "/report/{reportRecordId}", method = RequestMethod.DELETE)
    Integer deleteRecord(@PathVariable("reportRecordId") Integer reportRecordId);


    /**
     * 获取已使用但未添加报告的评估劵和优惠券
     *
     * @param pageNum  页码
     * @param pageSize 页数
     * @return page
     */
    @RequestMapping(value = "/ticket/report/{pageNum}/{pageSize}", method = RequestMethod.GET)
    PageVO<ReportDTO> reportTicket(@PathVariable(value = "pageNum") Integer pageNum,
                                   @PathVariable(value = "pageSize") Integer pageSize);

    /**
     * 我的优惠卡
     *
     * @param pageNum      页码
     * @param pageSize     页数
     * @param ticketStatus 状态
     * @param orderId      订单id
     * @return page
     */
    @RequestMapping(value = "/ticket/free/{pageNum}/{pageSize}", method = RequestMethod.GET)
    Map<String, Object> listFree(@PathVariable(value = "pageNum") Integer pageNum,
                                 @PathVariable(value = "pageSize") Integer pageSize,
                                 @RequestParam(value = "ticketStatus", required = false) Integer ticketStatus,
                                 @RequestParam(value = "orderId", required = false) Long orderId);


    /**
     * 优惠卡红点提醒
     *
     * @return int
     */
    @RequestMapping(value = "/ticket/hint", method = RequestMethod.GET)
    Integer freeCardHint();


    /**
     * 体检卡生命流程
     *
     * @param ticketNo 体检卡
     * @param userId   用户id
     */
    @RequestMapping(value = "/ticket/lifecycle", method = RequestMethod.GET)
    Map<String, Object> getLifecyleByTicket(@RequestParam(value = "ticketNo") String ticketNo,
                                            @RequestParam(value = "userId") Integer userId);


    /**
     * 根据赠送标志查询生命周期记录
     *
     * @param handselFlag 赠送标志
     * @return 生命周期
     */
    @RequestMapping(value = "/ticket/lifecycle/handsel", method = RequestMethod.GET)
    HraTicketLifeCycleDTO findTicketLifeCycleByHandselFlag(@RequestParam(value = "handselFlag") Long handselFlag);


    /**
     * 单个修改体检卡的归属关系
     *
     * @param sharerId         分享用户
     * @param userId           用户
     * @param cardIdOrTicketNo 体检卡或者劵
     * @param type             类型
     * @param handselFlag      标志
     * @return bool
     */
    @RequestMapping(value = "/ticket/owner", method = RequestMethod.GET)
    boolean changeTicketOwner(@RequestParam("sharerId") Integer sharerId,
                              @RequestParam("userId") Integer userId,
                              @RequestParam("cardIdOrTicketNo") String cardIdOrTicketNo,
                              @RequestParam("type") int type,
                              @RequestParam("handselFlag") Long handselFlag);

    /**
     * 批量修改体检卡的归属关系
     *
     * @param shareId     分享用户
     * @param userId      用户
     * @param handselList 体检卡或者劵
     * @param date        1-卡 4-劵
     * @param handselFlag 标志
     * @return bool
     */
    @RequestMapping(value = "/ticket/owner/batch", method = RequestMethod.GET)
    boolean changeTicketOwnerBatch(@RequestParam("shareId") Integer shareId,
                                   @RequestParam("userId") Integer userId,
                                   @RequestParam("handselList") List<String> handselList,
                                   @RequestParam("date") Date date,
                                   @RequestParam("handselFlag") Long handselFlag);


    /**
     * 根据时间戳+体检劵集合查询体检卡
     *
     * @param ticketList 体检号集合
     * @param dateTime   时间戳
     * @return list
     */
    @RequestMapping(value = "/ticket/share/batch", method = RequestMethod.GET)
    List<HraTicketDTO> getHraTicketListByTicketNoList(@RequestParam("ticketList") List<String> ticketList,
                                                      @RequestParam("dateTime") Long dateTime);

    /**
     * 删除评估卷
     *
     * @param ticketId 评估卷ID
     * @return int
     */
    @RequestMapping(value = {"/ticket/{ticketId}"}, method = RequestMethod.DELETE)
    void deleteTicket(@PathVariable("ticketId") Integer ticketId);


    /**
     * 可赠送出去的卡数量
     *
     * @param ticketStatus 卡状态
     * @return int
     */
    @RequestMapping(value = {"/ticket/send/count"}, method = RequestMethod.GET)
    Integer canBeSendCount(@RequestParam("ticketStatus") Integer ticketStatus,
                           @RequestParam("ticketType") String ticketType);

    /**
     * 兑换
     *
     * @param exchangeCode
     * @param exchangeFrom
     * @param channel
     * @return
     */
    @RequestMapping(value = {"/exchange/ticket"}, method = RequestMethod.POST)
    String exChangeTicketByCode(@RequestParam(value = "exchangeCode") String exchangeCode,
                                @RequestParam(value = "exchangeFrom") Integer exchangeFrom,
                                @RequestParam(value = "channel") String channel);

    /**
     * 把要赠送出去的卡保存到redis中
     *
     * @param sendCount    赠送数量
     * @param ticketStatus 赠送状态
     * @param ticketType   体检卡号
     * @return String
     * @author liuhao@yimaokeji.com
     */
    @RequestMapping(value = {"/ticket/save/redis"}, method = RequestMethod.POST)
    String saveTicketNoRedis(@RequestParam("sendCount") Integer sendCount,
                             @RequestParam("ticketStatus") Integer ticketStatus,
                             @RequestParam("ticketType") String ticketType);


    /**
     * 生成分享链接中的二维码（批量）
     *
     * @param userId    用户id
     * @param shareType 分享类型
     * @param shareNo   分享号
     * @param dateTime  时间戳
     * @return map
     * @author liuhao@yimaokeji.com
     */
    @RequestMapping(value = {"/ticket/share"}, method = RequestMethod.POST)
    Map<String, Object> buildShareInfo(@RequestParam(value = "userId") Integer userId,
                                       @RequestParam(value = "shareType", required = false) Integer shareType,
                                       @RequestParam(value = "shareNo", required = false) String shareNo,
                                       @RequestParam(value = "dateTime", required = false) Long dateTime);

    /**
     * 修改卡的赠送状态(赠送出去回调接口)
     *
     * @param userId   用户Id
     * @param shareNo  体检卡号
     * @param dateTime 时间戳
     * @author liuhao@yimaokeji.com
     */
    @RequestMapping(value = {"/ticket/send/callback"}, method = RequestMethod.PUT)
    void updateTicketStatusAndInsertSendRecord(@RequestParam(value = "userId") Integer userId,
                                               @RequestParam(value = "shareNo") String shareNo,
                                               @RequestParam(value = "dateTime") Long dateTime);


    /**
     * 修改评估卡/优惠卡赠送状态为赠送中(单张)
     *
     * @param userId    用户id
     * @param shareType 分享类型
     * @param shareNo   体检卡/劵号
     * @param dateTime  时间戳
     * @author liuhao@yimaokeji.com
     */
    @RequestMapping(value = {"/ticket/handsel"}, method = RequestMethod.PUT)
    void updateHandselStatus(@RequestParam(value = "userId") Integer userId,
                             @RequestParam(value = "shareType", required = false) Integer shareType,
                             @RequestParam(value = "shareNo", required = false) String shareNo,
                             @RequestParam(value = "dateTime") Long dateTime);


    /**
     * @param orderId
     * @param userId
     * @param pageNum
     * @param pageSize
     * @Description: 根据订单号查询评估卡列表
     * @author ycl
     * @Return: com.yimao.cloud.pojo.vo.PageVO<com.yimao.cloud.pojo.dto.hra.HraCardDTO>
     * @Create: 2019/4/19 10:16
     */
    @RequestMapping(value = "/order/hraCard/{pageNum}/{pageSize}", method = RequestMethod.GET)
    PageVO<HraCardDTO> listCardByUserId(@RequestParam(value = "orderId") Long orderId,
                                        @RequestParam(value = "userId") Integer userId,
                                        @PathVariable(value = "pageNum") Integer pageNum,
                                        @PathVariable(value = "pageSize") Integer pageSize);
}
