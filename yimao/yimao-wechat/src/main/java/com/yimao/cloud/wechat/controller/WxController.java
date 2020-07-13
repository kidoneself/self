package com.yimao.cloud.wechat.controller;

import com.yimao.cloud.base.cache.UserCache;
import com.yimao.cloud.base.constant.Constant;
import com.yimao.cloud.base.constant.WechatConstant;
import com.yimao.cloud.base.context.SpringContextHolder;
import com.yimao.cloud.base.enums.EnvironmentEnum;
import com.yimao.cloud.base.enums.UserType;
import com.yimao.cloud.base.exception.BadRequestException;
import com.yimao.cloud.base.exception.YimaoException;
import com.yimao.cloud.base.properties.DomainProperties;
import com.yimao.cloud.base.properties.WechatProperties;
import com.yimao.cloud.base.service.SmsService;
import com.yimao.cloud.base.utils.CookieUtil;
import com.yimao.cloud.base.utils.StringUtil;
import com.yimao.cloud.base.utils.UUIDUtil;
import com.yimao.cloud.base.utils.WXPayUtil;
import com.yimao.cloud.pojo.dto.user.UserDTO;
import com.yimao.cloud.pojo.dto.wechat.WxOauth2Result;
import com.yimao.cloud.wechat.feign.OrderFeign;
import com.yimao.cloud.wechat.feign.UserFeign;
import com.yimao.cloud.wechat.service.WxService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Objects;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * created by liuhao@yimaokeji.com
 * 2018/4/28
 */
@RestController
@Slf4j
@Api(tags = "WxController")
public class WxController {

    @Resource
    private DomainProperties domainProperties;
    @Resource
    private WechatProperties wechatProperties;
    @Resource
    private WxService wxService;
    @Resource
    private UserCache userCache;
    @Resource
    private OrderFeign orderFeign;
    @Resource
    private SmsService smsService;
    @Resource
    private UserFeign userFeign;

    @RequestMapping(value = "/wxgzh/jump")
    public void jump(HttpServletRequest request, HttpServletResponse response) {
        try {
            //第二步：通过code换取网页授权access_token
            String code = request.getParameter("code");
            String state = request.getParameter("state");
            log.info("/wxgzh/jump------code={}", code);
            log.info("/wxgzh/jump------state={}", state);

            //获取授权 access_token
            WxOauth2Result result = wxService.getAccessTokenByCode(code, wechatProperties.getJsapi().getAppid(), wechatProperties.getJsapi().getSecret());
            if (result == null) {
                return;
            }
            String openid = result.getOpenid();
            log.info("/wxgzh/jump------openid={}", openid);

            //清理cookie
            CookieUtil.deleteCookie(response, WechatConstant.COOKIE_LOGIN_TOKEN, "hratest.yimaokeji.com");
            CookieUtil.deleteCookie(response, WechatConstant.COOKIE_LOGIN_TOKEN, "hra.yimaokeji.com");
            CookieUtil.deleteCookie(response, WechatConstant.COOKIE_LOGIN_TOKEN, "yimaokeji.com");

            this.jump(response, state, openid);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    private void jump(HttpServletResponse response, String state, String openid) {
        try {
            String version = "&v=" + WechatConstant.VERSION_NUMBER;
            if (Objects.equals(state, "1")) {
                log.info("/wxgzh/menu/openid===sendRedirect===" + domainProperties.getWechat() + "/#/shop?openid=" + openid + version);
                response.sendRedirect(domainProperties.getWechat() + "/#/shop?openid=" + openid + version);
            } else if (Objects.equals(state, "2")) {
                response.sendRedirect(domainProperties.getWechat() + "/#/healthy?openid=" + openid + version);
            } else if (Objects.equals(state, "3")) {
                response.sendRedirect(domainProperties.getWechat() + "/#/my?openid=" + openid + version);
            } else if (Objects.equals(state, "21")) {
                response.sendRedirect(domainProperties.getWechat() + "/#/z/1?openid=" + openid + version);//净水用户最新优惠
            } else if (Objects.equals(state, "22")) {
                response.sendRedirect(domainProperties.getWechat() + "/#/z/2?openid=" + openid + version);//微创版经销加盟
            } else if (Objects.equals(state, "23")) {
                response.sendRedirect(domainProperties.getWechat() + "/#/z/3?openid=" + openid + version);//个人版经销加盟
            } else if (Objects.equals(state, "24")) {
                response.sendRedirect(domainProperties.getWechat() + "/#/z/4?openid=" + openid + version);//企业合作加盟
            } else if (Objects.equals(state, "25")) {
                response.sendRedirect(domainProperties.getWechat() + "/#/z/5?openid=" + openid + version);//区域代理商加盟
            } else if (Objects.equals(state, "31")) {
                response.sendRedirect(domainProperties.getWechat() + "/?#/profit?openid=" + openid + version);//区域代理商加盟
            } else if (Objects.equals(state, "32")) {
                response.sendRedirect(domainProperties.getWechat() + "/?#/my/order?openid=" + openid + version);//区域代理商加盟
            } else if (Objects.equals(state, "33")) {
                response.sendRedirect(domainProperties.getWechat() + "/?#/exprshop2?openid=" + openid + version);//区域代理商加盟
            } else if (Objects.equals(state, "50")) {//媒体中心
                Integer userId = userFeign.getUserIdByOpenid(openid);
                if (Objects.isNull(userId)) {
                    response.sendRedirect("http://e.yimaokeji.com/index.php?t=mhtml&pageID=1" + version);
                } else {
                    response.sendRedirect("http://e.yimaokeji.com/index.php?t=mhtml&pageID=1&e=" + userId + version);
                }
            } else if (Objects.equals(state, "51")) {//视频中心
                Integer userId = userFeign.getUserIdByOpenid(openid);
                String vodDomain = "https://wxtest.yimaokeji.com";
                if (EnvironmentEnum.PRO.code.equalsIgnoreCase(SpringContextHolder.getEnvironment())) {
                    vodDomain = "https://vod.yimaokeji.com";
                }
                if (Objects.isNull(userId)) {
                    response.sendRedirect(vodDomain + "/video/#/video");
                } else {
                    response.sendRedirect(vodDomain + "/video/#/video?e=" + userId);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * 初始化
     * 获取微信JS接口的临时票据
     */
    @GetMapping(value = {"/wx/init"})
    @ApiOperation(value = "获取微信JS接口的临时票据", notes = "获取微信JS接口的临时票据")
    public ResponseEntity wxTicket(@RequestParam(required = false) String url) {
        try {
            String jsapiTicket = wxService.getWxTicket();
            if (StringUtil.isEmpty(jsapiTicket)) {
                log.info("获取微信JS接口的临时票据失败");
                throw new YimaoException("获取微信JS接口的临时票据失败");
            }
            SortedMap<String, Object> map = new TreeMap<>();
            map.put("jsapi_ticket", jsapiTicket);
            map.put("noncestr", UUIDUtil.longuuid32());
            map.put("timestamp", Long.toString(System.currentTimeMillis() / 1000));
            if (StringUtil.isNotEmpty(url)) {
                map.put("url", url);
            }
            //生成sha1加密
            map.put("signature", WXPayUtil.createSign(map, false, WechatConstant.SHA1, wechatProperties.getJsapi().getKey()));
            map.put("appid", wechatProperties.getJsapi().getAppid());
            return ResponseEntity.ok(map);
        } catch (Exception e) {
            log.error("微信初始化失败" + url);
            log.error(e.getMessage(), e);
            throw new YimaoException("微信初始化失败");
        }
    }

    /**
     * 申请提现接口,检测是否满足提现要求
     *
     * @return Object
     */
    @PostMapping(value = {"/withdraw/apply"})
    @ApiOperation(value = "申请提现接口,检测是否满足提现要求", notes = "申请提现接口,检测是否满足提现要求")
    @ApiImplicitParam(name = "incomeType", value = "收益类型 1-产品收益 2-续费收益 默认1", required = true, dataType = "Long", paramType = "query")
    public Object apply(@RequestParam(value = "incomeType", defaultValue = "1") Integer incomeType) {
        // 1 获取当前用户信息
        UserDTO userDTO = userFeign.getBasicUserById(userCache.getUserId());
        // 2 判断用户身份
        if (userDTO.getUserType() != 7) {
            throw new BadRequestException("您没有提现权限");
        }
        // 3 判断用户是否绑定了手机号
        if (StringUtil.isEmpty(userDTO.getMobile())) {
            throw new BadRequestException("您还未绑定手机号，绑定后才能提现");
        }
        // 4 判断用户的可提现金额是否符合要求
        return orderFeign.checkCashCondition(userDTO.getId(), incomeType);

    }

    /**
     * 微信提现
     *
     * @param verifyCode     短信验证码
     * @param countryCode    区码
     * @param partnerTradeNo 提现单号
     * @param amount         提现金额
     * @return java.lang.Object
     * @author hhf
     * @date 2019/4/10
     */
    @PostMapping(value = {"/wxpay/withdraw/verify"})
    @ApiOperation(value = "微信提现", notes = "微信提现")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "verifyCode", value = "短信验证码", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "partnerTradeNo", value = "提现单号", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "amount", value = "提现金额", dataType = "BigDecimal", paramType = "query"),
            @ApiImplicitParam(name = "incomeType", value = "收益类型 1-产品收益 2-续费收益 默认1", required = true, dataType = "Long", paramType = "query")
    })
    public void verify(@RequestParam(name = "verifyCode") String verifyCode,
                       @RequestParam(name = "partnerTradeNo") String partnerTradeNo,
                       @RequestParam(name = "amount") BigDecimal amount,
                       @RequestParam(name = "incomeType", defaultValue = "1") Integer incomeType) {
        UserDTO user = userFeign.getBasicUserById(userCache.getUserId());
        if (!verifyCode.trim().matches("^[0-9]{6}$")) {
            throw new BadRequestException("验证码不合法");
        }
        /* 1 校验手机验证码 */
        if (!smsService.verifyCode(user.getMobile(), Constant.COUNTRY_CODE, verifyCode)) {
            throw new BadRequestException("验证码输入错误");
        }
        /* 2 判断用户是否是分销用户 */
        if (user.getUserType() != UserType.USER_7.value) {
            throw new BadRequestException("您没有提现权限");
        }
        boolean flag = orderFeign.insertCashRecord(partnerTradeNo, amount, user.getId(), incomeType);
        if (!flag) {
            throw new YimaoException("申请失败，请稍后重试！");
        }
    }


    /**
     * 生成带参数的二维码(包含单张卡的赠送)
     *
     * @param userId    用户id
     * @param shareType 分享类型
     * @param shareNo   存在redis里面的key
     * @param dateTime  时间戳
     * @return 二维码url
     */
    @GetMapping(value = "/wx/qrcode")
    @ApiOperation(value = "生成带参数的二维码", notes = "生成带参数的二维码")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "用户ID", dataType = "Long", required = true, paramType = "query"),
            @ApiImplicitParam(name = "shareType", value = "分享类型", dataType = "Long", required = true, paramType = "query"),
            @ApiImplicitParam(name = "shareNo", value = "体检卡号", dataType = "String", required = true, paramType = "query"),
            @ApiImplicitParam(name = "dateTime", value = "时间戳", dataType = "Long", required = true, paramType = "query")
    })
    public ResponseEntity getQRCodeWithParam(@RequestParam(value = "userId") Integer userId,
                                             @RequestParam(value = "shareType", required = false) Integer shareType,
                                             @RequestParam(value = "shareNo", required = false) String shareNo,
                                             @RequestParam(value = "dateTime", required = false) Long dateTime) {
        try {
            if (shareType == null) {
                shareType = 0;
            }
            if (StringUtil.isEmpty(shareNo)) {
                shareNo = "";
            }
            if (dateTime == null) {
                dateTime = 0L;
            }

            String ticket = wxService.getQRCodeWithParam(userId, shareType, shareNo, dateTime);
            String qrcode = "https://mp.weixin.qq.com/cgi-bin/showqrcode?ticket=" + ticket;
            return ResponseEntity.ok(qrcode);
        } catch (Exception e) {
            e.printStackTrace();
            throw new YimaoException("生成带参数的二维码失败!");
        }
    }


}
