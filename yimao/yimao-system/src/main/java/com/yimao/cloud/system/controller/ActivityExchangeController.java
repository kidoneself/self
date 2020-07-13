package com.yimao.cloud.system.controller;

import com.yimao.cloud.pojo.dto.hra.ActivityExchangeDTO;
import com.yimao.cloud.pojo.dto.system.ActivityDTO;
import com.yimao.cloud.pojo.vo.PageVO;
import com.yimao.cloud.system.feign.HraFeign;
import com.yimao.cloud.system.po.Dictionary;
import com.yimao.cloud.system.service.ActivityExchangeService;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * @author Lizhqiang
 * @date 2019/4/10
 */
@RestController
@Slf4j
public class ActivityExchangeController {

    @Resource
    private HraFeign hraFeign;

    @Resource
    private ActivityExchangeService activityExchangeService;

    /**
     * 兑换码列表
     *
     * @param side           端
     * @param channel        渠道
     * @param exchangeCode   兑换码
     * @param batchNumber    批次号
     * @param exchangeStatus 兑换状态
     * @param ticketNo       体检卡号
     * @param ticketStatus   体检卡状态
     * @param pageNum        当前页
     * @param pageSize       每页条数
     * @return object
     */
    @GetMapping("/activity/exchange/{pageNum}/{pageSize}")
    @ApiOperation(value = "兑换活动列表", notes = "兑换活动列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "side", value = "活动端", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "channel", value = "渠道", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "exchangeCode", value = "兑换码", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "batchNumber", value = "兑换码批次号", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "exchangeStatus", value = "兑换状态", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "ticketNo", value = "关联优惠卡", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "ticketStatus", value = "优惠卡状态", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "pageNum", value = "页码", defaultValue = "1", dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "pageSize", value = "每页显示数", defaultValue = "10", dataType = "Long", paramType = "path")
    })
    public ResponseEntity<PageVO<ActivityExchangeDTO>> page(@RequestParam(required = false) Integer side,
                                                            @RequestParam(required = false) Integer channel,
                                                            @RequestParam(required = false) String exchangeCode,
                                                            @RequestParam(required = false) String batchNumber,
                                                            @RequestParam(required = false) String exchangeStatus,
                                                            @RequestParam(required = false) String ticketNo,
                                                            @RequestParam(required = false) Integer ticketStatus,
                                                            @PathVariable(value = "pageNum") Integer pageNum,
                                                            @PathVariable(value = "pageSize") Integer pageSize) {
        PageVO<ActivityExchangeDTO> page = hraFeign.page(side, channel, exchangeCode, batchNumber, exchangeStatus, ticketNo, ticketStatus, pageNum, pageSize);
        return ResponseEntity.ok(page);
    }


    /**
     * 生成兑换码
     *
     * @param count     生成数量
     * @param beginTime 兑换开始时间
     * @param endTime   兑换截止时间
     * @param side      端        1-公众号  2-小程序
     * @param channel   渠道      1-京东(JD)    2-天猫(TM)
     * @return Object
     */
    @PostMapping("/activity/create/ticket")
    @ApiOperation(value = "生成兑换码")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "count", value = "生成数量", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "beginTime", value = "兑换开始时间", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "endTime", value = "兑换截止时间", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "side", value = "端        1-公众号  2-小程序", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "channel", value = "渠道      1-京东(JD)    2-天猫(TM)", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "channelName", value = "渠道名字", dataType = "String", paramType = "query"),
    })
    public Object create(@RequestParam Integer count,
                         @RequestParam(required = false) Date beginTime,
                         @RequestParam(required = false) Date endTime,
                         @RequestParam Integer side,
                         @RequestParam Integer channel,
                         @RequestParam String channelName) {
        hraFeign.saveHraExchange(count, beginTime, endTime, side, channel, channelName);
        return ResponseEntity.noContent().build();
    }


    //兑换限制
    @GetMapping("/exchange/limit")
    @ApiOperation(value = "兑换限制(调用接口忽略)", notes = "兑换限制(调用接口忽略)")
    @ApiImplicitParam(name = "exchange", value = "字典字段", dataType = "String", paramType = "query")
    public Object exchangeLimit(String exchange) {
        Dictionary dictionary = activityExchangeService.exchangeAstrict(exchange);
        return ResponseEntity.ok(dictionary);
    }

    //获取渠道和端
    @GetMapping("/exchange/channel")
    @ApiOperation(value = "获取渠道和端(调用接口忽略)", notes = "获取渠道和端调用接口忽略)")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "groupCode", value = "字典字段", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "code", value = "字典字段", dataType = "String", allowMultiple = true, paramType = "query"),
            @ApiImplicitParam(name = "msgName", value = "字典字段", dataType = "String", paramType = "query"),
    })
    public Object findSideOrChannel(@RequestParam("groupCode") String groupCode,
                                    @RequestParam("code") List<String> code,
                                    @RequestParam("msgName") String msgName) {
        String channel = activityExchangeService.findSideOrChannel(groupCode, code, msgName);
        return ResponseEntity.ok(channel);
    }


    /**
     * 兑换设置
     */
    @PostMapping(value = "/exchange/set")
    @ApiOperation(value = "兑换设置", notes = "兑换设置")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "terminal", value = "端  1-终端app；2-微信公众号；3-经销商APP；4-小程序", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "limitType", value = "限制类型:0-无限  1-每天，2-每周，3-每月，4-季度，5-每年", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "times", value = "次数  0-无限 ", dataType = "Long", paramType = "query"),
    })
    public Object exChangeSet(@RequestParam Integer terminal,
                              @RequestParam Integer limitType,
                              @RequestParam Integer times) {
        hraFeign.exChangeSet(terminal, limitType, times);
        return ResponseEntity.noContent().build();

    }
}
