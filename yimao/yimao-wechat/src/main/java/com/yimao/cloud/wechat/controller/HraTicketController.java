package com.yimao.cloud.wechat.controller;

import com.yimao.cloud.base.exception.YimaoException;
import com.yimao.cloud.pojo.dto.hra.HraCardDTO;
import com.yimao.cloud.pojo.dto.hra.HraTicketDTO;
import com.yimao.cloud.pojo.dto.hra.ReportDTO;
import com.yimao.cloud.pojo.vo.PageVO;
import com.yimao.cloud.wechat.feign.HraFeign;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * @author liuhao@yimaokeji.com
 * @version v2.0
 * @date 2019/4/2
 */
@RestController
@Slf4j
@Api(tags = "HraTicketController")
public class HraTicketController {

    @Resource
    private HraFeign hraFeign;


    @GetMapping(value = "/ticket/card/{pageNum}/{pageSize}")
    @ApiOperation(value = "查询当前用户所有的评估卡", notes = "查询当前用户所有的评估卡")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "页码", required = true, defaultValue = "1", dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "pageSize", value = "页数", required = true, defaultValue = "10", dataType = "Long", paramType = "path")
    })
    public ResponseEntity<PageVO<HraCardDTO>> cardList(@PathVariable("pageNum") Integer pageNum, @PathVariable("pageSize") Integer pageSize) {
        PageVO<HraCardDTO> page = hraFeign.hraCardByUser(pageNum, pageSize);
        return ResponseEntity.ok(page);
    }


    @GetMapping(value = "/ticket/card/{id}")
    @ApiOperation(value = "根据评估卡ID查询评估卷", notes = "根据评估卡ID查询评估卷")
    @ApiImplicitParam(name = "id", value = "页码", required = true, dataType = "String", paramType = "path")
    public ResponseEntity<List<HraTicketDTO>> ticketByCardId(@PathVariable("id") String id) {
        List<HraTicketDTO> page = hraFeign.ticketByCardId(id);
        return ResponseEntity.ok(page);
    }

    @GetMapping(value = "/ticket/reserve/{pageNum}/{pageSize}")
    @ApiOperation(value = "查询所有可预约的评估卷", notes = "查询所有可预约的评估卷")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "页码", required = true, defaultValue = "1", dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "pageSize", value = "页码", required = true, defaultValue = "10", dataType = "Long", paramType = "path")
    })
    public ResponseEntity<PageVO<HraTicketDTO>> reserveTicket(@PathVariable("pageNum") Integer pageNum, @PathVariable("pageSize") Integer pageSize) {
        PageVO<HraTicketDTO> page = hraFeign.reserveTicket(pageNum, pageSize);
        return ResponseEntity.ok(page);
    }

    /**
     * 获取已使用但未添加报告的评估劵和优惠券（保留）
     * 现在是采用自动添加的方式
     */
    @GetMapping(value = "/ticket/report/{pageNum}/{pageSize}")
    @ApiOperation(value = "获取已使用但未添加报告的评估劵和优惠券", notes = "获取已使用但未添加报告的评估劵和优惠券")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "页码", required = true, defaultValue = "1", dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "pageSize", value = "页数", required = true, defaultValue = "10", dataType = "Long", paramType = "path")
    })
    public ResponseEntity<PageVO<ReportDTO>> reportTicket(@PathVariable(value = "pageNum") Integer pageNum,
                                                          @PathVariable(value = "pageSize") Integer pageSize) {
        PageVO<ReportDTO> pageVO = hraFeign.reportTicket(pageNum, pageSize);
        return ResponseEntity.ok(pageVO);
    }


    /**
     * 我的优惠卡
     *
     * @param pageNum      页码
     * @param pageSize     页数
     * @param ticketStatus 1-未使用，2-已使用，3-待付款，4-已过期 5-已赠出
     * @return page
     */
    @GetMapping(value = {"/ticket/free/{pageNum}/{pageSize}"})
    @ApiOperation(value = "我的优惠卡", notes = "我的优惠卡")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "页码", required = true, dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "pageSize", value = "页数", required = true, dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "ticketStatus", value = "每页显示行数", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "orderId", value = "每页显示行数", dataType = "Long", paramType = "query")
    })
    public ResponseEntity<Map<String, Object>> listFree(@PathVariable(value = "pageNum") Integer pageNum,
                                                        @PathVariable(value = "pageSize") Integer pageSize,
                                                        @RequestParam(value = "ticketStatus", required = false) Integer ticketStatus,
                                                        @RequestParam(value = "orderId", required = false) Long orderId) {

        Map<String, Object> pageVO = hraFeign.listFree(pageNum, pageSize, ticketStatus, orderId);
        return ResponseEntity.ok(pageVO);
    }


    /**
     * 我的优惠卡红点提醒（如果有可发放的优惠卡）
     */
    @GetMapping(value = {"/ticket/hint"})
    @ApiOperation(value = "优惠卡红点提醒（如果有可发放的优惠卡）", notes = "优惠卡红点提醒（如果有可发放的优惠卡）")
    public ResponseEntity freeCardHint() {
        return ResponseEntity.ok(hraFeign.freeCardHint());
    }


    /**
     * 体检卡生命流程
     */
    @GetMapping(value = "/ticket/lifecycle")
    @ApiOperation(value = "体检卡生命流程", notes = "体检卡生命流程")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "ticketNo", value = "体检卡号", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "userId", value = "用户id", required = true, dataType = "Long", paramType = "query")
    })
    public ResponseEntity getLifecyleByTicket(@RequestParam(value = "ticketNo") String ticketNo,
                                              @RequestParam(value = "userId") Integer userId) {
        return ResponseEntity.ok(hraFeign.getLifecyleByTicket(ticketNo, userId));
    }


    @DeleteMapping(value = {"/ticket/{ticketId}"})
    @ApiOperation(value = "删除评估劵", notes = "删除评估劵")
    @ApiImplicitParam(name = "ticketId", value = "评估券ID", required = true, dataType = "Long", paramType = "query")
    public ResponseEntity deleteTicket(@PathVariable(name = "ticketId") Integer ticketId) {
        hraFeign.deleteTicket(ticketId);
        return ResponseEntity.noContent().build();
    }


    @GetMapping(value = {"/ticket/send/count"})
    @ApiOperation(value = "可赠送出去的卡数量", notes = "可赠送出去的卡数量")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "ticketStatus", value = "状态 1-已支付 3-未支付", required = true, dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "ticketType", value = "卡类型(M,Y)", defaultValue = "M", dataType = "String", paramType = "query")
    })
    public ResponseEntity getCanBeSendCount(@RequestParam(name = "ticketStatus") Integer ticketStatus,
                                            @RequestParam(name = "ticketType", defaultValue = "M") String ticketType) {
        return ResponseEntity.ok(hraFeign.canBeSendCount(ticketStatus, ticketType));
    }

    @GetMapping(value = "/ticket/share/batch")
    @ApiOperation(value = "根据时间戳+体检劵集合查询体检卡", notes = "根据时间戳+体检劵集合查询体检卡")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "ticketList", value = "体检卡号", required = true, dataType = "String", allowMultiple = true, paramType = "query"),
            @ApiImplicitParam(name = "dateTime", value = "体检卡号", required = true, dataType = "Long", paramType = "query")
    })
    public ResponseEntity getHraTicketListByTicketNoList(@RequestParam(value = "ticketList") List<String> ticketList,
                                                         @RequestParam(value = "dateTime") Long dateTime) {

        return ResponseEntity.ok(hraFeign.getHraTicketListByTicketNoList(ticketList, dateTime));
    }


    /**
     * 批量赠送(将要赠送的卡保存在redis中)
     *
     * @return 返回的是保存在redis中的key (在调用生成二维码的时候作为ticketNo传递至后台)
     */
    @RequestMapping(value = {"/ticket/batch/give"}, method = RequestMethod.POST)
    @ApiOperation(value = "批量赠送(将要赠送的卡保存在redis中)", notes = "批量赠送(将要赠送的卡保存在redis中)")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "sendCount", value = "数量", required = true, dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "ticketStatus", value = "体检卡状态", required = true, dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "ticketType", value = "体检卡型号(M,Y)", required = true, defaultValue = "M", dataType = "String", paramType = "query")
    })
    public ResponseEntity buildBatchShareInfo(@RequestParam(value = "sendCount") Integer sendCount,
                                              @RequestParam(value = "ticketStatus") Integer ticketStatus,
                                              @RequestParam(value = "ticketType", defaultValue = "M") String ticketType) {
            // 1 获取当前用户信息

            //2 确保赠送的数量小于持有的可赠送的卡的数量()
            Integer count = hraFeign.canBeSendCount(ticketStatus, ticketType);
            if (count < sendCount) {
                throw new YimaoException("赠送数量超出持有卡数量(可赠送)");
            }
            //3 将要转赠的体检卡号保存至redis中
            //4 并将选中的卡的状态改为赠送中(下一个方法)
            String redisKey = hraFeign.saveTicketNoRedis(sendCount, ticketStatus, ticketType);
            return ResponseEntity.ok(redisKey);
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
    @ApiOperation(value = "生成分享链接中的二维码（体检劵分享）", notes = "生成分享链接中的二维码（体检劵分享）")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "用户ID", required = true, dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "shareType", value = "分享类型 1-体检卡 2-体检劵 4-批量", required = true, dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "shareNo", value = "体检卡号", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "dateTime", value = "时间戳", required = true, dataType = "Long", paramType = "query")
    })
    public ResponseEntity buildShareInfo(@RequestParam(value = "userId") Integer userId,
                                         @RequestParam(value = "shareType", required = false) Integer shareType,
                                         @RequestParam(value = "shareNo", required = false) String shareNo,
                                         @RequestParam(value = "dateTime", required = false) Long dateTime) {
        return ResponseEntity.ok(hraFeign.buildShareInfo(userId, shareType, shareNo, dateTime));
    }

    /**
     * 用户点击批量赠送并且确定赠送后调用的接口
     *
     * @param userId   用户Id
     * @param shareNo  卡类型
     * @param dateTime 时间戳
     * @return
     */
    @PutMapping(value = {"/ticket/send/callback"})
    @ApiOperation(value = "修改卡的赠送状态(赠送出去回调接口)", notes = "修改卡的赠送状态(赠送出去回调接口)")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "用户Id", required = true, dataType = "Long", allowMultiple = true, paramType = "query"),
            @ApiImplicitParam(name = "shareNo", value = "卡号", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "dateTime", value = "时间戳", required = true, dataType = "Long", paramType = "query")
    })
    public ResponseEntity updateTicketStatusAndInsertSendRecordBatch(@RequestParam(value = "userId") Integer userId,
                                                                     @RequestParam(value = "shareNo") String shareNo,
                                                                     @RequestParam(value = "dateTime") Long dateTime) {

        hraFeign.updateTicketStatusAndInsertSendRecord(userId, shareNo, dateTime);
        return ResponseEntity.noContent().build();
    }

    @PutMapping(value = {"/ticket/handsel"})
    @ApiOperation(value = "修改评估卡优惠卡赠送状态为赠送中", notes = "修改评估卡优惠卡赠送状态为赠送中(单张)")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "用户Id", required = true, dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "shareType", value = "类型 1-卡 2-劵 4-批量", required = true, dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "shareNo", value = "卡号", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "dateTime", value = "时间戳", required = true, dataType = "Long", paramType = "query")
    })
    public ResponseEntity handsel(@RequestParam(value = "userId") Integer userId,
                                  @RequestParam(value = "shareType", required = false) Integer shareType,
                                  @RequestParam(value = "shareNo", required = false) String shareNo,
                                  @RequestParam(value = "dateTime") Long dateTime) {

        hraFeign.updateHandselStatus(userId, shareType, shareNo, dateTime);
        return ResponseEntity.noContent().build();
    }
}
