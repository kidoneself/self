package com.yimao.cloud.hra.controller;

import com.yimao.cloud.base.auth.JWTInfo;
import com.yimao.cloud.base.cache.UserCache;
import com.yimao.cloud.base.enums.HraGiveTypeEnum;
import com.yimao.cloud.base.enums.HraShareTypeEnum;
import com.yimao.cloud.base.enums.HraTicketStatusEnum;
import com.yimao.cloud.base.exception.NotFoundException;
import com.yimao.cloud.base.exception.YimaoException;
import com.yimao.cloud.base.utils.*;
import com.yimao.cloud.framework.cache.RedisCache;
import com.yimao.cloud.hra.feign.OrderFeign;
import com.yimao.cloud.hra.feign.SystemFeign;
import com.yimao.cloud.hra.feign.UserFeign;
import com.yimao.cloud.hra.feign.WeChatFeign;
import com.yimao.cloud.hra.po.DiscountCardRecord;
import com.yimao.cloud.hra.po.HraTicket;
import com.yimao.cloud.hra.po.HraTicketLifeCycle;
import com.yimao.cloud.hra.service.DiscountCardRecordService;
import com.yimao.cloud.hra.service.HraCardService;
import com.yimao.cloud.hra.service.HraTicketService;
import com.yimao.cloud.hra.service.TicketLifeCycleService;
import com.yimao.cloud.pojo.dto.hra.*;
import com.yimao.cloud.pojo.dto.station.HraStatisticsDTO;
import com.yimao.cloud.pojo.dto.station.StationScheduleDTO;
import com.yimao.cloud.pojo.query.station.HraSpecialQuery;
import com.yimao.cloud.pojo.query.station.HraTicketQuery;
import com.yimao.cloud.pojo.dto.system.MessageContentDTO;
import com.yimao.cloud.pojo.dto.system.MessageRecordDTO;
import com.yimao.cloud.pojo.dto.user.UserDTO;
import com.yimao.cloud.pojo.vo.PageVO;
import com.yimao.cloud.pojo.vo.hra.HraCardVO;
import com.yimao.cloud.pojo.vo.station.StationHraCardVO;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.*;

/**
 * created by liuhao@yimaokeji.com
 * 2017122017/12/13
 */
@RestController
@Slf4j
@Api(tags = "HraTicketController")
public class HraTicketController {

    @Resource
    private HraTicketService hraTicketService;
    @Resource
    private UserCache userCache;
    @Resource
    private UserFeign userFeign;
    @Resource
    private HraCardService hraCardService;
    @Resource
    private DiscountCardRecordService discountCardRecordService;
    @Resource
    private TicketLifeCycleService ticketLifeCycleService;
    @Resource
    private SystemFeign systemFeign;
    @Resource
    private WeChatFeign weChatFeign;
    @Resource
    private RedisCache redisCache;

    @Resource
    private OrderFeign orderFeign;

    /**
     * HRA评估-预约列表(已预约的/已过期的)
     *
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
     * @param userId        用户e家号
     * @return
     * @author hhf
     * @date 2018/1/11
     */
    @GetMapping(value = {"/ticket/reservation/{pageNum}/{pageSize}"})
    public ResponseEntity<PageVO<HraTicketResultDTO>> listReserve(@PathVariable(value = "pageNum") Integer pageNum,
                                                                  @PathVariable(value = "pageSize") Integer pageSize,
                                                                  @RequestParam(required = false) String beginTime,
                                                                  @RequestParam(required = false) String endTime,
                                                                  @RequestParam(required = false) Integer userSource,
                                                                  @RequestParam(required = false) String mobile,
                                                                  @RequestParam(required = false) String ticketNo,
                                                                  @RequestParam(required = false) String province,
                                                                  @RequestParam(required = false) String city,
                                                                  @RequestParam(required = false) String region,
                                                                  @RequestParam(required = false) String name,
                                                                  @RequestParam(required = false) Integer reserveStatus,
                                                                  @RequestParam(required = false) Integer userId) {

        PageVO<HraTicketResultDTO> page = hraTicketService.listTicket(beginTime, endTime, userSource, mobile, ticketNo, province, city, region, name, 2, pageNum, pageSize, reserveStatus, null, userId, null);
        return ResponseEntity.ok(page);
    }

    /**
     * HRA评估-评估列表(已体检的)
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
     * @return
     * @author hhf
     * @date 2018/1/11
     */
    @GetMapping(value = {"/ticket/use/{pageNum}/{pageSize}"})
    public ResponseEntity list(@PathVariable(value = "pageNum") Integer pageNum,
                               @PathVariable(value = "pageSize") Integer pageSize,
                               @RequestParam(required = false) String beginTime,
                               @RequestParam(required = false) String endTime,
                               @RequestParam(required = false) Integer userSource,
                               @RequestParam(required = false) String mobile,
                               @RequestParam(required = false) String ticketNo,
                               @RequestParam(required = false) String province,
                               @RequestParam(required = false) String city,
                               @RequestParam(required = false) String region,
                               @RequestParam(required = false) String name,
                               @RequestParam(required = false) Integer reserveStatus,
                               @RequestParam(required = false) Integer hasUpload,
                               @RequestParam(required = false) Integer userId,
                               @RequestParam(required = false) String ticketType) {
        PageVO<HraTicketResultDTO> page = hraTicketService.listTicket(beginTime, endTime, userSource, mobile, ticketNo, province, city, region, name, 1, pageNum, pageSize, reserveStatus, hasUpload, userId, ticketType);
        return ResponseEntity.ok(page);
    }


    /**
     * @param query
     * @param pageNum
     * @param pageSize
     * @return
     */
    @PostMapping(value = {"/ticket/physical/{pageNum}/{pageSize}"})
    public ResponseEntity physical(@RequestBody HraQueryDTO query,
                                   @PathVariable(value = "pageNum") Integer pageNum,
                                   @PathVariable(value = "pageSize") Integer pageSize) {

        PageVO<HraPhysicalDTO> page = hraTicketService.physical(query, pageNum, pageSize);
        return ResponseEntity.ok(page);
    }


    @PostMapping(value = {"/ticket/special/{pageNum}/{pageSize}"})
    public PageVO<HraCardVO> allotTicket(@RequestBody HraQueryDTO query,
                                         @PathVariable(value = "pageNum") Integer pageNum,
                                         @PathVariable(value = "pageSize") Integer pageSize) {
        return hraTicketService.allotTicket(query, pageNum, pageSize);
    }

    /**
     * hra--评估卡管理--F卡管理 列表查询 (服务站站务系统调用)
     *
     * @param pageNum
     * @param pageSize
     * @param query
     * @return
     */
    @PostMapping(value = "/ticket/station/special/{pageNum}/{pageSize}")
    public Object stationAllotTicket(@PathVariable(value = "pageNum") Integer pageNum,
                                     @PathVariable(value = "pageSize") Integer pageSize,
                                     @RequestBody(required = false) HraSpecialQuery query) {
        PageVO<StationHraCardVO> page = hraTicketService.stationAllotTicket(pageNum,pageSize,query);
        return ResponseEntity.ok(page);
    }

    /**
     * @Description: 体检卡列表-分配卡(M卡)
     * @author ycl
     * @Return: org.springframework.http.ResponseEntity
     * @Create: 2019/2/19 14:01
     */
    @RequestMapping(value = "/assign", method = RequestMethod.POST)
    public ResponseEntity assignCard(@RequestBody HraAssignDTO hraAssignDTO) {
        UserDTO userDTO = userFeign.getUserById(hraAssignDTO.getUserId());
        if (userDTO == null) {
            throw new YimaoException("找不到e家号为【" + hraAssignDTO.getUserId() + "】的用户");
        }
        //生成发卡记录 1-经销商固定配额，2-经销商业绩发放，3-用户业绩，5-手动发放卡，7-分销用户业绩卡 8-兑换码兑换
        hraTicketService.generateHraRecord(userDTO, hraAssignDTO.getCardCount(), "后台手动发放优惠卡", HraGiveTypeEnum.HRA_APPLY.value, null);
        //发放
        hraTicketService.assignCard(userDTO, hraAssignDTO.getProductId(), null);
        return ResponseEntity.noContent().build();
    }


    /**
     * @Description: 体检卡-编辑
     * @author ycl
     * @param: id
     * @Return: org.springframework.http.ResponseEntity
     * @Create: 2019/2/27 15:12
     */
    @PutMapping("/ticket/physical")
    public ResponseEntity update(@RequestBody HraTicketDTO hraTicketDTO) {
        hraTicketService.updateTicket(hraTicketDTO);
        return ResponseEntity.noContent().build();
    }


    /**
     * @Description: F卡管理-分配F卡
     * @author ycl
     * @param: stationId
     * @Return: org.springframework.http.ResponseEntity
     * @Create: 2019/2/19 16:18
     */
    @RequestMapping(value = "/ticket/allot", method = RequestMethod.POST)
    public void allot(@RequestBody HraFCardDTO dto) {
        List<HraCardDTO> cardList = hraTicketService.allot(dto);
        if (CollectionUtil.isNotEmpty(cardList)) {
            if (StringUtil.isNotBlank(dto.getPhone())) {
                HraCardDTO card = cardList.get(0);
                if (Objects.nonNull(card) && CollectionUtil.isNotEmpty(card.getTicketList())) {
                    HraTicketDTO ticket = card.getTicketList().get(0);
                    //发送短信提醒给服务站人员，告知F卡分配情况
                    String msg = "【翼猫健康e家】恭喜您安装HRA健康风险评估设备。已为您分配了服务站培训专用的评估卡号：" + ticket.getTicketNo()
                            + "。该卡有效期至" + DateUtil.transferDateToString(ticket.getValidEndTime(), "yyyy-MM-dd HH:mm") + "，共可使用" + ticket.getTotal() + "次。";
                    try {
                        SmsUtil.sendSms(msg, dto.getPhone());
                        this.saveMessageRecord(msg, dto.getPhone());
                    } catch (Exception e) {
                        log.error("分配成功，短信通知发送失败。");
                        log.error(e.getMessage(), e);
                    }
                }
            }
        } else {
            throw new YimaoException("分配失败");
        }
    }

    /**
     * F卡分配短信记录
     *
     * @param msg
     * @param phone
     */
    private void saveMessageRecord(String msg, String phone) {
        Long contentId = UUIDUtil.buildOrderId(23);
        MessageContentDTO messageContent = new MessageContentDTO();
        messageContent.setId(contentId);
        messageContent.setContent(msg);
        systemFeign.messageContentSave(messageContent);
        MessageRecordDTO messageRecord = new MessageRecordDTO();
        messageRecord.setContentId(contentId);
        messageRecord.setType(1);//1-手机短信
        messageRecord.setPhone(phone);
        messageRecord.setCreateTime(new Date());
        systemFeign.messageRecordSave(messageRecord);
    }

    /**
     * 根据体检卡号查询体检卡
     *
     * @param ticketNo
     */
    @GetMapping(value = "/hra/ticket/{ticketNo}")
    public HraTicketDTO getTicketByNo(@PathVariable String ticketNo) {
        return hraTicketService.getTicketByTicketNo(ticketNo);
    }

    /**
     * @Description: F卡管理-是否禁用功能
     * @author ycl
     * @param: hraAllotTicketDTO
     * @Return: org.springframework.http.ResponseEntity
     * @Create: 2019/2/28 15:00
     */
    @PutMapping("/ticket/status")
    public void updateStatus(@RequestBody HraAllotTicketDTO dto) {
        hraTicketService.updateStatus(dto);
    }


    //===========================================================================================================================================
    //==============================================HRA导出================================================================================
    //==============================================修改lizhiqiang==================================================================================
    //==============================================时间-2019-05-16======================================================================================
    //===========================================================================================================================================

    /**
     * @param beginTime
     * @param endTime
     * @param userSource
     * @param mobile
     * @param ticketNo
     * @param province
     * @param city
     * @param region
     * @param name
     * @param reserveStatus
     * @param userId
     * @param ticketType
     * @return
     *//*
    @GetMapping(value = {"/ticket/export"})
    public Object listUsed(@RequestParam(required = false) String beginTime,
                           @RequestParam(required = false) String endTime,
                           @RequestParam(required = false) Integer userSource,
                           @RequestParam(required = false) String mobile,
                           @RequestParam(required = false) String ticketNo,
                           @RequestParam(required = false) String province,
                           @RequestParam(required = false) String city,
                           @RequestParam(required = false) String region,
                           @RequestParam(required = false) String name,
                           @RequestParam(required = false) Integer reserveStatus,
                           @RequestParam(required = false) Integer userId,
                           @RequestParam(required = false) String ticketType) {
        List<HraExportReservationDTO> resultList = hraTicketService.getTicketInfoToExport(beginTime, endTime, userSource, mobile, ticketNo, province, city, region, name, reserveStatus, userId, ticketType);
        return resultList;
    }*/

    /**
     * 评估卡管理页面的导出
     *
     * @param province
     * @param city
     * @param region
     * @param stationName
     * @param currentUserId
     * @param
     * @param userType
     * @param minSurplus
     * @param maxSurplus
     * @param beginTime
     * @param endTime
     * @param cardType
     * @return
     */
    /*@GetMapping(value = {"/ticket/physical/export"})
    public ResponseEntity exportPhysical(@RequestParam(required = false) String province,
                                         @RequestParam(required = false) String city,
                                         @RequestParam(required = false) String region,
                                         @RequestParam(required = false) String stationName,
                                         @RequestParam(required = false) Integer currentUserId,
                                         @RequestParam(required = false) String ticketNo,
                                         @RequestParam(required = false) Integer userType,
                                         @RequestParam(required = false) Integer minSurplus,
                                         @RequestParam(required = false) Integer maxSurplus,
                                         @RequestParam(required = false) Integer ticketStatus,
                                         @RequestParam(required = false) Date beginTime,
                                         @RequestParam(required = false) Date endTime,
                                         @RequestParam(required = false) String cardType) {

        List<HraExportPhysicalDTO> resultList = hraTicketService.exportPhysical(province, city, region, stationName, currentUserId, ticketNo, userType, minSurplus, maxSurplus, ticketStatus, beginTime, endTime, cardType);

        return ResponseEntity.ok(resultList);
    }*/


    /**
     * F卡导出
     *
     * @param province
     * @param city
     * @param region
     * @param stationName
     * @param isExpire
     * @param minSurplus
     * @param maxSurplus
     * @param state
     * @param beginTime
     * @param endTime
     * @param ticketNo
     * @return
     */
    /*@GetMapping(value = {"/ticket/special/export"})
    public ResponseEntity<Object> exportSpecialTicket(@RequestParam(value = "province", required = false) String province,
                                                      @RequestParam(value = "city", required = false) String city,
                                                      @RequestParam(value = "region", required = false) String region,
                                                      @RequestParam(value = "stationName", required = false) String stationName,
                                                      @RequestParam(value = "isExpire", required = false) String isExpire,
                                                      @RequestParam(value = "minSurplus", required = false) Integer minSurplus,
                                                      @RequestParam(value = "maxSurplus", required = false) Integer maxSurplus,
                                                      @RequestParam(value = "state", required = false) Integer state,
                                                      @RequestParam(value = "beginTime", required = false) String beginTime,
                                                      @RequestParam(value = "endTime", required = false) String endTime,
                                                      @RequestParam(value = "ticketNo", required = false) String ticketNo) {
        List<HraAllotTicketExportDTO> list = hraTicketService.exportSpecialTicket(province, city, region, stationName, isExpire, minSurplus, maxSurplus, state, beginTime, endTime, ticketNo);
        return ResponseEntity.ok(list);
    }*/


    //===========================================================================================================================================
    //===========================================================================================================================================
    //==============================================修改-liuhao@yimaokeji.com==================================================================================
    //==============================================时间-2019-04-01-新修改======================================================================================
    //===========================================================================================================================================


    /**
     * 根据评估卡查询多张评估劵
     *
     * @param cardId
     * @return page
     * @author liuhao@yimaokeji.com
     */
    @GetMapping(value = {"/ticket/card/{cardId}"})
    public ResponseEntity ticketByCardId(@PathVariable(name = "cardId") String cardId) {
        return ResponseEntity.ok(hraTicketService.ticketByCardId(cardId));
    }

    /**
     * 查询所有可预约的评估卷
     *
     * @author liuhao@yimaokeji.com
     * @date 2019-04-03
     */
    @GetMapping(value = {"/ticket/reserve/{pageNum}/{pageSize}"})
    public ResponseEntity reserveTicket(@PathVariable(name = "pageNum") Integer pageNum, @PathVariable(name = "pageSize") Integer pageSize) {
        JWTInfo jwtInfo = userCache.getJWTInfo();
        Integer userId = jwtInfo.getId();
        PageVO<HraTicketDTO> page = hraTicketService.reserveTicketList(pageNum, pageSize, userId);
        return ResponseEntity.ok(page);
    }


    /**
     * 获取已使用但未添加报告的评估劵和优惠券（保留）
     * 现在是采用自动添加的方式
     */
    @GetMapping(value = "/ticket/report/{pageNum}/{pageSize}")
    public ResponseEntity<PageVO<HraTicketDTO>> reportTicket(@PathVariable(value = "pageNum") Integer pageNum,
                                                             @PathVariable(value = "pageSize") Integer pageSize) {
        JWTInfo jwtInfo = userCache.getJWTInfo();
        Integer userId = jwtInfo.getId();
        PageVO<HraTicketDTO> pageVO = hraTicketService.reportTicket(pageNum, pageSize, userId);
        return ResponseEntity.ok(pageVO);
    }


    /**
     * 我的优惠卡
     *
     * @param pageNum
     * @param pageSize
     * @param ticketStatus 1-未使用，2-已使用，3-待付款，4-已过期
     * @return page
     * @author liuhao@yimaokeji.com
     */
    @GetMapping(value = {"/ticket/free/{pageNum}/{pageSize}"})
    public ResponseEntity listFree(@PathVariable(value = "pageNum") Integer pageNum,
                                   @PathVariable(value = "pageSize") Integer pageSize,
                                   @RequestParam(required = false) Integer ticketStatus,
                                   @RequestParam(required = false) Long orderId) {
        Integer userId = userCache.getUserId();
        UserDTO userDTO = userFeign.getUserById(userId);
        if (userDTO == null) {
            throw new NotFoundException("用户信息获取失败。");
        }
        //生成要发放的优惠卡记录
        hraCardService.freeCardHandsel(userDTO);
        //根据优惠卡发放记录发放优惠卡
        hraTicketService.assignCard(userDTO, null, null);
        return ResponseEntity.ok(hraTicketService.listFreeCard(pageNum, pageSize, orderId, userId, ticketStatus));
    }

    /**
     * 我的优惠卡红点提醒（如果有可发放的优惠卡）
     *
     * @return 0-无 1-有
     */
    @GetMapping(value = {"/ticket/hint"})
    public ResponseEntity freeCardHint() {
        JWTInfo jwtInfo = userCache.getJWTInfo();
        Integer userId = jwtInfo.getId();
        UserDTO userDTO = userFeign.getUserById(userId);
        if (userDTO == null) {
            return ResponseEntity.ok(0);
        }

        //生成要发放的优惠卡记录
//        hraCardService.freeCardHandsel(userDTO);
        //是否存在未发卡的记录
        List<DiscountCardRecord> discountCardRecords = discountCardRecordService.listNotReceivedRecords(userId, null);
        if (CollectionUtil.isEmpty(discountCardRecords)) {
            return ResponseEntity.ok(0);
        }
        return ResponseEntity.ok(1);
    }


    /**
     * 体检卡生命流程
     */
    @GetMapping(value = "/ticket/lifecycle")
    public ResponseEntity getLifecyleByTicket(@RequestParam(value = "ticketNo") String ticketNo,
                                              @RequestParam(value = "userId") Integer userId) {
        Map<String, Object> resultList = hraTicketService.getLifecyleByTicket(ticketNo, userId);
        return ResponseEntity.ok(resultList);
    }

    /**
     * 体检卡生命流程
     */
    @GetMapping(value = "/ticket/lifecycle/handsel")
    public HraTicketLifeCycleDTO findTicketLifeCycleByHandselFlag(@RequestParam(value = "handselFlag") Long handselFlag) {
        HraTicketLifeCycle ticketLifeCycle = ticketLifeCycleService.findTicketLifeCycleByHandselFlag(handselFlag);
        if (ticketLifeCycle != null) {
            HraTicketLifeCycleDTO lifeCycleDTO = new HraTicketLifeCycleDTO();
            lifeCycleDTO.setId(ticketLifeCycle.getId());
            lifeCycleDTO.setHandselTime(ticketLifeCycle.getHandselTime());
            lifeCycleDTO.setDestUserId(lifeCycleDTO.getDestUserId());
            lifeCycleDTO.setExpiredFlag(ticketLifeCycle.getExpiredFlag());
            return lifeCycleDTO;
        }
        return null;
    }

    /**
     * 生成分享链接中的二维码（体检劵分享）
     *
     * @param userId    用户id
     * @param shareType 分享类型
     * @param shareNo   分享卡号
     * @param dateTime  时间戳
     * @return Int
     */
    @PostMapping(value = {"/ticket/share"})
    public ResponseEntity buildShareInfo(@RequestParam(value = "userId") Integer userId,
                                         @RequestParam(value = "shareType", required = false) Integer shareType,
                                         @RequestParam(value = "shareNo", required = false) String shareNo,
                                         @RequestParam(value = "dateTime", required = false) Long dateTime) {

        log.info("userId=" + userId + "===shareType=" + shareType + "===shareNo=" + shareNo + "===dateTime=" + dateTime);
        UserDTO userDTO = userFeign.getUserById(userId);
        if (userDTO == null) {
            throw new YimaoException("获取不到用户信息!");
        }

        if (shareType == null) {
            shareType = HraShareTypeEnum.HRA_SHARE_OWNER.value;
        }
        if (StringUtil.isEmpty(shareNo)) {
            shareNo = "";
        }
        if (dateTime == null) {
            dateTime = 0L;
        }

        //生成分享二维码
        String ticket = weChatFeign.getQRCodeWithParam(userId, shareType, shareNo, dateTime);
        if (StringUtil.isEmpty(ticket)) {
            log.error("====================================================");
            log.error("=============调用微信接口生成带参数二维码失败=============");
            log.error("====================================================");
            throw new YimaoException("调用微信接口生成带参数二维码失败!");
        }

        String qrcode = "https://mp.weixin.qq.com/cgi-bin/showqrcode?ticket=" + ticket;


        boolean isExpired = false; // 是否赠送超时 true - 赠送超时
        boolean isReceived = false;// 是否被领取 true - 被领取
        boolean isTicketExpired = false; // 是否是卡过期 true - 卡过期

        Map<String, Object> result = new HashMap<>(8);
        if (Objects.equals(HraShareTypeEnum.HRA_SHARE_OWNER.value, shareType) || Objects.equals(HraShareTypeEnum.HRA_SHARE_TICKET.value, shareType)) {
            HraTicketLifeCycle ticketLifeCycle = ticketLifeCycleService.findTicketLifeCycleByHandselFlag(dateTime);
            if (ticketLifeCycle != null) {
                if (ticketLifeCycle.getExpiredFlag() == 2) {
                    isExpired = true;
                }
                if (ticketLifeCycle.getDestUserId() != null || ticketLifeCycle.getExpiredFlag() == 1) {
                    isReceived = true;
                }

                if (Objects.equals(HraShareTypeEnum.HRA_SHARE_CARD.value, shareType)) {
                    List<HraTicket> hraTickets = hraTicketService.findByCardId(shareNo);
                    if (CollectionUtil.isNotEmpty(hraTickets)) {
                        Integer ticketStatus = hraTickets.get(0).getTicketStatus();
                        Date validEndTime = hraTickets.get(0).getValidEndTime();
                        if (Objects.equals(HraTicketStatusEnum.HRA_EXPIRE.value, ticketStatus) || validEndTime.before(new Date())) {
                            isTicketExpired = true;
                        }
                    }
                } else {
                    HraTicket hraTicket = hraTicketService.findHraTicketByTicketNo(shareNo);
                    if (hraTicket != null) {
                        Integer ticketStatus = hraTicket.getTicketStatus();
                        Date validEndTime = hraTicket.getValidEndTime();
                        if (Objects.equals(HraTicketStatusEnum.HRA_EXPIRE.value, ticketStatus) || validEndTime.before(new Date())) {
                            isTicketExpired = true;
                        }
                    }
                }
            }
        } else if (Objects.equals(HraShareTypeEnum.HRA_SHARE_BATCH.value, shareType)) {    //批量赠送优惠卡
            List<String> list = redisCache.getCacheList(shareNo, String.class);
            if (CollectionUtil.isEmpty(list) || new Date().after(DateUtil.dayAfter(new Date(dateTime), 1))) {
                isExpired = true;
                result.put("qrcode", qrcode);
                result.put("isExpired", isExpired);
                result.put("isReceived", isReceived);
                result.put("isTicketExpired", isTicketExpired);
                return ResponseEntity.ok(result);
            }
            List<HraTicket> ticketList = hraTicketService.getHraTicketListByTicketNoList(list, dateTime);
            int handselCount = 0;               //赠送中卡的数量
            int unExpiredCount = 0;             //未过期卡的数量
            for (HraTicket h : ticketList) {
                Integer ticketStatus = h.getTicketStatus();
                Integer handselStatus = h.getHandselStatus();
                if (!Objects.equals(HraTicketStatusEnum.HRA_EXPIRE.value, ticketStatus)) {
                    unExpiredCount++;
                }
                if (handselStatus == 1) {
                    handselCount++;
                }
            }

            if (handselCount == 0) {
                isReceived = true;
            }
            if (unExpiredCount == 0) {
                isTicketExpired = true;
            }
        }
        result.put("qrcode", qrcode);
        result.put("isExpired", isExpired);
        result.put("isReceived", isReceived);
        result.put("isTicketExpired", isTicketExpired);
        return ResponseEntity.ok(result);
    }


    @RequestMapping(value = "/ticket/owner", method = RequestMethod.GET)
    public ResponseEntity changeTicketOwner(@RequestParam("sharerId") Integer sharerId,
                                            @RequestParam("userId") Integer userId,
                                            @RequestParam("cardIdOrTicketNo") String cardIdOrTicketNo,
                                            @RequestParam("type") int type,
                                            @RequestParam("handselFlag") Long handselFlag) {

        return ResponseEntity.ok(hraTicketService.changeTicketOwner(sharerId, userId, cardIdOrTicketNo, type, handselFlag));
    }

    @RequestMapping(value = "/ticket/owner/batch", method = RequestMethod.GET)
    public ResponseEntity changeTicketOwnerBatch(@RequestParam("shareId") Integer sharerId,
                                                 @RequestParam("userId") Integer userId,
                                                 @RequestParam("handselList") List<String> handselList,
                                                 @RequestParam(value = "date") Date date,
                                                 @RequestParam("handselFlag") Long handselFlag) {

        return ResponseEntity.ok(hraTicketService.changeTicketOwnerBatch(sharerId, userId, handselList, date, handselFlag));
    }


    @PatchMapping(value = "/hra/refer")
    public void doRefer(@RequestParam Long orderId, @RequestParam String ticketNo, @RequestParam Integer terminal) {
        hraTicketService.doRefer(orderId, ticketNo, terminal);
    }


    @PostMapping(value = "/ticket/card")
    public void createHraCardAndTicket(@RequestParam Long orderId) {
        hraTicketService.createHraCardAndTicket(orderId);
    }


    /**
     * 可赠送的优惠卡的数量
     *
     * @param ticketStatus 优惠卡状态 1 待使用 3 待支付(已禁用)
     * @return
     */
    @GetMapping(value = "/ticket/send/count")
    public ResponseEntity getCanBeSendCount(@RequestParam("ticketStatus") Integer ticketStatus,
                                            @RequestParam(value = "ticketType", defaultValue = "M") String ticketType) {
        JWTInfo jwtInfo = userCache.getJWTInfo();
        Integer userId = jwtInfo.getId();
        //确保赠送的数量小于持有的可赠送的卡的数量
        Integer count = hraTicketService.getCanBeSendCardCount(userId, ticketStatus, ticketType);
        return ResponseEntity.ok(count);
    }


    /**
     * 优惠卡赠送 把要赠送出去的卡保存到redis中
     *
     * @param sendCount    赠送数量
     * @param ticketStatus 赠送状态
     * @param ticketType   赠送型号
     * @return 赠送返回唯一key
     */
    @PostMapping(value = {"/ticket/save/redis"})
    public ResponseEntity saveTicketNoRedis(@RequestParam(value = "sendCount") Integer sendCount,
                                            @RequestParam(value = "ticketStatus") Integer ticketStatus,
                                            @RequestParam(value = "ticketType", defaultValue = "M") String ticketType) {
        JWTInfo jwtInfo = userCache.getJWTInfo();
        Integer userId = jwtInfo.getId();
        String key = hraTicketService.saveTicketNoRedis(userId, sendCount, ticketStatus, ticketType);
        return ResponseEntity.ok(key);
    }


    /**
     * 用户点击批量赠送并且确定赠送之后调用的接口
     *
     * @param userId   用户Id
     * @param shareNo  卡类型
     * @param dateTime 时间戳
     * @return
     */
    @PutMapping(value = {"/ticket/send/callback"})
    public ResponseEntity updateTicketStatusAndInsertSendRecordBatch(@RequestParam(value = "userId") Integer userId,
                                                                     @RequestParam(value = "shareNo") String shareNo,
                                                                     @RequestParam(value = "dateTime") Long dateTime) {

        hraTicketService.updateTicketStatusAndInsertSendRecord(userId, shareNo, dateTime);
        return ResponseEntity.noContent().build();
    }

    /**
     * 修改评估卡优惠卡赠送状态为赠送中(单张)
     *
     * @param userId
     * @return
     */
    @PutMapping(value = {"/ticket/handsel"})
    public ResponseEntity handsel(@RequestParam(value = "userId") Integer userId,
                                  @RequestParam(value = "shareType", required = false) Integer shareType,
                                  @RequestParam(value = "shareNo", required = false) String shareNo,
                                  @RequestParam(value = "dateTime") Long dateTime) {

        UserDTO userDTO = userFeign.getUserById(userId);
        if (userDTO == null) {
            throw new YimaoException("未获取到赠送者信息");
        }
        //赠送前修改评估卡状态为赠送中
        boolean flag = shareType != null && (shareType == 1 || shareType == 2) && StringUtil.isNotEmpty(shareNo);
        if (flag) {
            boolean b;
            if (shareType == 1) {//赠送卡
                b = hraTicketService.changeTicketHandselStatus(userId, shareNo, 1, dateTime);
            } else {
                if (shareNo.startsWith("Y")) {
                    //评估券
                    b = hraTicketService.changeTicketHandselStatus(userId, shareNo, 2, dateTime);
                } else {
                    //优惠券
                    b = hraTicketService.changeTicketHandselStatus(userId, shareNo, 3, dateTime);
                }
            }
            if (!b) {
                throw new YimaoException("赠送遇到问题，请稍后重试");
            }
        }
        return ResponseEntity.noContent().build();
    }

    /**
     * @param
     * @param
     * @Description: 获取需要支付的优惠卡
     */
    @GetMapping(value = "/hra/batchpay/tickets")
    public List<HraTicketDTO> listTicketForPay(@RequestParam Integer userId,
                                               @RequestParam Integer count) {
        return hraTicketService.listTicketForPay(userId, count);
    }

    /**
     * 根据体检卡号，获取体检卡信息
     *
     * @param ticketList 体检卡集合
     * @param dateTime   时间戳
     * @return list
     */
    @RequestMapping(value = "/ticket/share/batch", method = RequestMethod.GET)
    public List<HraTicketDTO> getHraTicketListByTicketNoList(@RequestParam("ticketList") List<String> ticketList,
                                                             @RequestParam("dateTime") Long dateTime) {

        List<HraTicket> hraTickets = hraTicketService.getHraTicketListByTicketNoList(ticketList, dateTime);
        if (CollectionUtil.isNotEmpty(hraTickets)) {
            List<HraTicketDTO> hraTicketsDto = new ArrayList<>();
            hraTickets.forEach(item -> {
                HraTicketDTO dto = new HraTicketDTO();
                BeanUtils.copyProperties(item, dto);
                hraTicketsDto.add(dto);
            });
            return hraTicketsDto;
        }
        return null;
    }
    
    
    /**
     * 站务系统-hra-体检评估列表
     * @param query
     * @return
     */
    @PostMapping(value = "/ticket/station/use/list/{pageNum}/{pageSize}")
    public PageVO<HraTicketResultDTO> getStationHraTicketUsedList(@PathVariable(value = "pageNum") Integer pageNum,
            @PathVariable(value = "pageSize") Integer pageSize,@RequestBody HraTicketQuery query){
    	 PageVO<HraTicketResultDTO> page = hraTicketService.getStationHraTicketUsedList(pageNum,pageSize,query);
    	 
         return page;
    }
    
    /**
     * 站务系统-hra-体检预约列表
     * @param query
     * @return
     */
    @PostMapping(value = "/ticket/station/reservation/list/{pageNum}/{pageSize}")
    public PageVO<HraTicketResultDTO> getStationHraTicketReservationList(@PathVariable(value = "pageNum") Integer pageNum,
            @PathVariable(value = "pageSize") Integer pageSize,@RequestBody HraTicketQuery query){
    	PageVO<HraTicketResultDTO> page = hraTicketService.getStationHraTicketReservationList(pageNum,pageSize,query);
    	
        return page;

    }
    
    /**
     * 站务系统-控制台-待办事项(昨日已评估数，今日待评估，总评估数)
     * @param areas
     * @param distributorIds 
     * @return
     */
    @PostMapping(value = "/ticket/station/stationHraAssessNum")
	public StationScheduleDTO getStationHraNum(@RequestBody List<Integer> stations) {
    	
    	return hraTicketService.getStationHraNum(stations);
    	
    }
    

    /**
     * 站务系统-统计-评估统计-表格数据
     * @param hraQuery
     * @return
     */
    @PostMapping(value = "/ticket/station/stationHraData")
	public List<HraStatisticsDTO> getStationHraData(@RequestBody HraTicketQuery hraQuery){
    	
    	return hraTicketService.getStationHraData(hraQuery);
    	
    }
    
    /**
     * 站务系统-统计-评估统计-图表数据
     * @param hraQuery
     * @return
     */
    @PostMapping(value = "/ticket/station/stationHraPicData")
   	public HraStatisticsDTO getStationHraPicData(@RequestBody HraTicketQuery hraQuery){
    	return hraTicketService.getStationHraPicData(hraQuery);
    }


}
