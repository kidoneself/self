package com.yimao.cloud.out.controller.api;

import com.yimao.cloud.base.cache.UserCache;
import com.yimao.cloud.base.exception.BadRequestException;
import com.yimao.cloud.out.feign.UserFeign;
import com.yimao.cloud.out.utils.ApiResult;
import com.yimao.cloud.pojo.dto.user.RedAccountDTO;
import io.swagger.annotations.ApiOperation;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * @description   组件服务-红包组件-红包账户API
 * @author Liu Yi
 * @date 2019/9/9 18:12
 */
@Controller
@RequestMapping({"/webapi/assembly/red"})
public class RedAccountController {
    @Resource
    private UserCache userCache;
    @Resource
    private UserFeign userFeign;

    @RequestMapping(value = "/canBindRedlist" ,method = RequestMethod.GET)
    @ApiOperation(value = "可绑定的红包列表",httpMethod = "GET")
    @ResponseBody
    public Map canBindRedList(String isMaster,HttpServletRequest request) {
        // PageVO<Map> page = new PageVO<Map>();
        return ApiResult.result(request, null);
    }

    /**
     * @description   经销商读取我的红包账户信息
     * @author Liu Yi
     * @date 2019/9/9 17:50
     * @param
     * @return java.util.Map
     */
    @ResponseBody
    @RequestMapping({"/account/get"})
    @ApiOperation("我的红包账户")
    public Map redAccount(HttpServletRequest request, HttpServletResponse response) {
      Integer engineerId=userCache.getCurrentEngineerId();
        if(null == engineerId){
            throw new BadRequestException("用户未登录！");
        }
        RedAccountDTO redAccount =userFeign.getRedAccountByAccountId(engineerId,1);

        if(redAccount==null){
            redAccount=new RedAccountDTO();
            redAccount.setTotalMoney(0);
            redAccount.setLockedMoney(0);
        }
        return ApiResult.result(request, redAccount);
    }

    /**
     * @description   创建充值订单
     * @author Liu Yi
     * @date 2019/9/9 17:48
     * @param
     * @return java.util.Map
     */
    @RequestMapping( value = {"/account/recharge/create"},method = {RequestMethod.POST})
    @ApiOperation( value = "红包账户API")
    public Map createRechargeOrder(HttpServletRequest request, HttpServletResponse response, Double rechargeMoney) {
        return ApiResult.error(request, "因新系统升级，暂时无法使用此功能，敬请期待！");
    }

    @ApiOperation(value = "绑定支付宝提现账号")
    @RequestMapping(value = {"/account/bindAlipayId"}, method = {RequestMethod.POST})
    public Map bindAliPayAccount(HttpServletRequest request, String alipayId, String realName) {
        return ApiResult.error(request, "因新系统升级，暂时无法使用此功能，敬请期待！");
    }

    /**
     * @description   读取提现金额建议列表
     * @author Liu Yi
     * @date 2019/9/9 17:44
     * @param
     * @return java.util.Map
     */
    @ResponseBody
    @RequestMapping(value={"/account/takeMoney/suggestMoneyList"}, method = {RequestMethod.GET})
    @ApiOperation("读取提现建议金额列表")
    public Map suggestMoneyList(HttpServletRequest request, HttpServletResponse response) {
        return ApiResult.error(request, "因新系统升级，暂时无法使用此功能，敬请期待！");
    }

    /**
     * @description   创建提现订单
     * @author Liu Yi
     * @date 2019/9/9 17:45
     * @param
     * @return java.util.Map
     */
    @RequestMapping( value = {"/account/takeMoney/create"}, method = {RequestMethod.POST})
    @ApiOperation(value = "创建红包账户提现单")
    public Map createTackeMoneyOrder(HttpServletRequest request, HttpServletResponse response, Double takeMoneyPrice) {
        return ApiResult.error(request, "因新系统升级，暂时无法使用此功能，敬请期待！");
    }

    /**
     * @description   检查充值订单
     * @author Liu Yi
     * @date 2019/9/9 17:47
     * @param
     * @return java.util.Map
     */
    @RequestMapping({"/account/recharge/check"})
    public Map checkRechargeOrder(HttpServletRequest request, HttpServletResponse response) {
        return ApiResult.error(request, "因新系统升级，暂时无法使用此功能，敬请期待！");
    }
}
