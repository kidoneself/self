package com.yimao.cloud.wechat.controller;

import com.yimao.cloud.base.auth.AuthConstants;
import com.yimao.cloud.base.constant.Constant;
import com.yimao.cloud.base.enums.IdentityTypeEnum;
import com.yimao.cloud.base.enums.OriginEnum;
import com.yimao.cloud.base.enums.SystemType;
import com.yimao.cloud.base.exception.BadRequestException;
import com.yimao.cloud.base.properties.DomainProperties;
import com.yimao.cloud.base.properties.WechatProperties;
import com.yimao.cloud.base.utils.CookieUtil;
import com.yimao.cloud.base.utils.StringUtil;
import com.yimao.cloud.base.utils.UUIDUtil;
import com.yimao.cloud.framework.cache.RedisCache;
import com.yimao.cloud.pojo.dto.user.UserDTO;
import com.yimao.cloud.pojo.dto.wechat.WxOauth2Result;
import com.yimao.cloud.pojo.dto.wechat.WxUserInfo;
import com.yimao.cloud.wechat.feign.UserFeign;
import com.yimao.cloud.wechat.service.WxService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Objects;

/**
 * 描述：H5页面（直升会员、经销商自注册）
 *
 * @Author Zhang Bo
 * @Date 2019/9/18
 */
@RestController
@Slf4j
@Api(tags = "H5Controller")
public class H5Controller {

    @Resource
    private DomainProperties domainProperties;
    @Resource
    private WechatProperties wechatProperties;
    @Resource
    private WxService wxService;
    @Resource
    private RedisCache redisCache;
    @Resource
    private UserFeign userFeign;

    /**
     * H5-获取用户微信授权url
     */
    @GetMapping(value = "/h5/getwxauthurl")
    @ApiOperation(value = "获取用户微信授权url")
    @ApiImplicitParam(name = "sharerId", value = "分享者ID", dataType = "Long", paramType = "query")
    public String getOauth2Url(@RequestParam(required = false) Integer sharerId, @RequestParam(required = false) String productId, @RequestParam(required = false) String activityId) throws UnsupportedEncodingException {
        StringBuffer url = new StringBuffer();
        url.append("https://open.weixin.qq.com/connect/oauth2/authorize");
        url.append("?appid=").append(wechatProperties.getJsapi().getAppid());
        if (StringUtil.isNotEmpty(productId)) {
            productId = URLEncoder.encode(productId, "UTF-8");
            //如果是抢购产品
            if (StringUtil.isNotEmpty(activityId)) {
                activityId = URLEncoder.encode(activityId, "UTF-8");
                url.append("&redirect_uri=").append(URLEncoder.encode(domainProperties.getWechat() + "/api/wechat/h5/register/user?productId=" + productId + "&activityId=" + activityId, "UTF-8"));
            } else {
                url.append("&redirect_uri=").append(URLEncoder.encode(domainProperties.getWechat() + "/api/wechat/h5/register/user?productId=" + productId, "UTF-8"));
            }
        } else {
            url.append("&redirect_uri=").append(URLEncoder.encode(domainProperties.getWechat() + "/api/wechat/h5/register/user", "UTF-8"));
        }
        url.append("&response_type=code");
        url.append("&scope=snsapi_userinfo");
        if (sharerId != null) {
            url.append("&state=").append(sharerId);
        }
        url.append("#wechat_redirect");
        return url.toString();
    }

    /**
     * H5-用户直升会员(分享商品)
     */
    @RequestMapping(value = "/h5/register/user")
    public void h5RegisterUser(@RequestParam(value = "productId", required = false) String productId, @RequestParam(value = "activityId", required = false) String activityId, HttpServletRequest request, HttpServletResponse response) throws IOException {
        String code = request.getParameter("code");
        String state = request.getParameter("state");
        log.info("/h5/register/user------code={},state={},productId={},activityId={}", code, state, productId, activityId);
        Integer sharerId = null;
        if (StringUtil.isNotEmpty(state)) {
            //state为分享者的e家号
            sharerId = Integer.parseInt(state);
        }
        WxOauth2Result result = wxService.getAccessTokenByCode(code, wechatProperties.getJsapi().getAppid(), wechatProperties.getJsapi().getSecret());
        if (result == null) {
            throw new BadRequestException("获取授权遇到错误【001】");
        }
        WxUserInfo wxUserInfo = wxService.getWxUserInfo(result.getAccessToken(), result.getOpenid());
        if (wxUserInfo == null) {
            throw new BadRequestException("获取授权遇到错误【002】");
        }
        UserDTO user = userFeign.userProcess(IdentityTypeEnum.H5.value, null, wxUserInfo, OriginEnum.H5_SHARE.value);
        if (Objects.nonNull(user)) {
            log.info("重定向至：{}/?#/shop/gooddetail/", domainProperties.getWechat());
//            if (Objects.isNull(user.getMobile())) {
//                //根据用户信息进行登录
//                UserDTO userDTO = userFeign.loginBySystemType(user.getId(), SystemType.H5.value);
//                if (Constant.LOCAL_ENVIRONMENT || Constant.DEV_ENVIRONMENT) {
//                    CookieUtil.setCookie(request, response, AuthConstants.USER_TOKEN, user.getToken(), 2592000, true);
//                }
//                response.setHeader(AuthConstants.USER_TOKEN, userDTO.getToken());
//            }
            String key = UUIDUtil.longuuid();
            //设置一个随机字符串到缓存中
            redisCache.set(Constant.H5_SHARE_LOGIN_SETP + key, key, 3600L);
            response.sendRedirect(domainProperties.getWechat() + "/?#/shop/gooddetail/" + productId + "?sharerId=" + sharerId + "&mobile=" + user.getMobile() + "&key=" + key + "&activityId=" + activityId + "&userId=" + user.getId());
        }
    }
}
