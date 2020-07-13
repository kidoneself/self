package com.yimao.cloud.hra.controller;

import com.yimao.cloud.base.cache.UserCache;
import com.yimao.cloud.base.enums.HraType;
import com.yimao.cloud.base.exception.BadRequestException;
import com.yimao.cloud.base.utils.IpUtil;
import com.yimao.cloud.hra.service.ActivityExchangeService;
import com.yimao.cloud.pojo.dto.hra.ActivityExchangeDTO;
import com.yimao.cloud.pojo.vo.PageVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;

/**
 * @author Lizhqiang
 * @date 2019/4/10
 */
@RestController
@Slf4j
@Api(tags = "ActivityExchangeController")
public class ActivityExchangeController {

    @Resource
    private ActivityExchangeService activityExchangeService;

    @Resource
    private UserCache userCache;

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
    @ApiOperation(value = "兑换码列表", notes = "兑换码列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "side", value = "活动端", dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "channel", value = "渠道", dataType = "Long", paramType = "path"),
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

        PageVO<ActivityExchangeDTO> page = activityExchangeService.page(side, channel, exchangeCode, batchNumber, exchangeStatus, ticketNo, ticketStatus, pageNum, pageSize);
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
        activityExchangeService.saveHraExchange(count, beginTime, endTime, side, channel, channelName);
        return ResponseEntity.noContent().build();
    }

    /**
     * 兑换
     *
     * @param exchangeCode 兑换码
     * @param exchangeFrom 兑换来源 1-公众号 2-小程序
     * @param channel      兑换渠道
     */
    @PostMapping(value = "/exchange/ticket")
    @ApiOperation(value = "兑换")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "exchangeCode", value = "兑换码", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "exchangeFrom", value = "兑换来源", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "channel", value = "兑换渠道", dataType = "String", paramType = "query"),
    })
    public Object exChangeTicketByCode(@RequestParam("exchangeCode") String exchangeCode,
                                       @RequestParam("exchangeFrom") Integer exchangeFrom,
                                       @RequestParam("channel") String channel,
                                       HttpServletRequest request) {

        //只能输入5-8位之间的【数字+字母】
        String regex = "^[0-9a-zA-Z]{5,8}$";
        if (!exchangeCode.trim().matches(regex)) {
            throw new BadRequestException("抱歉，兑换码不存在，请输入正确的兑换码哦~ ");
        }
        String msg = activityExchangeService.exChangeTicketByCode(exchangeCode, exchangeFrom, channel, IpUtil.getIp(request));
        return ResponseEntity.ok(msg);

    }


    /**
     * 兑换设置
     */
    @PostMapping(value = "/exchange/set")
    @ApiOperation(value = "兑换设置")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "terminal", value = "端  1-终端app；2-微信公众号；3-经销商APP；4-小程序", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "limitType", value = "限制类型:0-无限  1-每天，2-每周，3-每月，4-季度，5-每年", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "times", value = "次数  0-无限 ", dataType = "Long", paramType = "query"),
    })
    public Object exChangeSet(@RequestParam Integer terminal,
                              @RequestParam Integer limitType,
                              @RequestParam Integer times) {
        activityExchangeService.exChangeSet(terminal, limitType, times);
        return ResponseEntity.noContent().build();

    }
}
