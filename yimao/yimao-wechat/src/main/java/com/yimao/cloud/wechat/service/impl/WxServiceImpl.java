package com.yimao.cloud.wechat.service.impl;

import com.yimao.cloud.base.constant.Constant;
import com.yimao.cloud.base.constant.WechatConstant;
import com.yimao.cloud.base.context.SpringContextHolder;
import com.yimao.cloud.base.entity.FileItem;
import com.yimao.cloud.base.enums.EnvironmentEnum;
import com.yimao.cloud.base.enums.HraShareTypeEnum;
import com.yimao.cloud.base.exception.NotFoundException;
import com.yimao.cloud.base.exception.YimaoException;
import com.yimao.cloud.base.properties.DomainProperties;
import com.yimao.cloud.base.properties.WechatProperties;
import com.yimao.cloud.base.utils.DateUtil;
import com.yimao.cloud.base.utils.HttpUtil;
import com.yimao.cloud.base.utils.ImageUtil;
import com.yimao.cloud.base.utils.QRCodeUtil;
import com.yimao.cloud.base.utils.SFTPUtil;
import com.yimao.cloud.base.utils.StringUtil;
import com.yimao.cloud.framework.cache.RedisCache;
import com.yimao.cloud.pojo.dto.user.UserDTO;
import com.yimao.cloud.pojo.dto.wechat.WxOauth2Result;
import com.yimao.cloud.pojo.dto.wechat.WxUserInfo;
import com.yimao.cloud.wechat.feign.CmsFeign;
import com.yimao.cloud.wechat.feign.UserFeign;
import com.yimao.cloud.wechat.service.WxService;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author Zhang Bo
 * @date 2017/12/22.
 */
@Service
@Slf4j
public class WxServiceImpl implements WxService {

    @Resource
    private DomainProperties domainProperties;
    @Resource
    private WechatProperties wechatProperties;
    @Resource
    private RedisCache redisCache;
    @Resource
    private UserFeign userFeign;
    @Resource
    private CmsFeign cmsFeign;

    /**
     * 获取access_token
     */
    @Override
    public String getJSAPIAccessToken() throws Exception {
        try {
            String accessToken = redisCache.get(Constant.WX_JSAPI_ACCESS_TOKEN);
            if (StringUtil.isEmpty(accessToken)) {
                //获取授权 access_token
                StringBuffer url = new StringBuffer();
                url.append("https://api.weixin.qq.com/cgi-bin/token");
                url.append("?appid=").append(wechatProperties.getJsapi().getAppid());
                url.append("&secret=").append(wechatProperties.getJsapi().getSecret());
                url.append("&grant_type=client_credential");
                //向微信服务器发送请求
                String response = HttpUtil.executeGet(url.toString());

                System.out.println("H5获取access_token = " + response);

                JSONObject json = new JSONObject(response);

                if (json.has("errcode")) {
                    log.error("公众号获取access_token失败。{}", json.getString("errmsg"));
                    return null;
                }

                String access_token = json.getString("access_token");
                int expires_in = json.getInt("expires_in");

                redisCache.set(Constant.WX_JSAPI_ACCESS_TOKEN, access_token, expires_in - 30);
                return access_token;
            }
            return accessToken;
        } catch (Exception e) {
            log.error("H5获取access_token时发生异常！", e);
            throw new Exception("H5获取access_token时发生异常！");
        }
    }

    /**
     * 健康自测小程序获取access_token
     *
     * @return
     * @throws Exception
     */
    @Override
    public String getMiniProgramAccessToken() throws Exception {
        log.debug("==================================getMiniProgramAccessToken()===============================");
        try {
            String accessToken = redisCache.getForHealthTestMini(Constant.WX_HEALTH_MINI_ACCESS_TOKEN);
            if (StringUtil.isEmpty(accessToken)) {
                //获取授权 access_token
                StringBuffer url = new StringBuffer();
                url.append("https://api.weixin.qq.com/cgi-bin/token");
                url.append("?appid=").append(wechatProperties.getHealth().getAppid());
                url.append("&secret=").append(wechatProperties.getHealth().getSecret());
                url.append("&grant_type=client_credential");
                //向微信服务器发送请求
                String response = HttpUtil.executeGet(url.toString());

//                System.out.println("健康自测小程序获取access_token = " + response);
                log.info("健康自测小程序获取access_token = " + response);

                JSONObject json = new JSONObject(response);

                if (json.has("errcode")) {
                    log.error("健康自测小程序获取access_token失败。{}", json.getString("errmsg"));
                    return null;
                }

                String access_token = json.getString("access_token");
                int expires_in = json.getInt("expires_in");

                redisCache.setForHealthTestMini(Constant.WX_HEALTH_MINI_ACCESS_TOKEN, access_token, expires_in - 30);

                return access_token;
            }
            return accessToken;
        } catch (Exception e) {
            e.printStackTrace();
            log.error("健康自测小程序获取access_token时发生异常！", e);
            throw new Exception(e.getMessage(), e);
        }
    }

    /**
     * 小程序商城获取access_token
     *
     * @return
     * @throws Exception
     */
    @Override
    public String getStoreCodeAccessToken() throws Exception {
        try {
            String accessToken = redisCache.getForHealthTestMini(Constant.WX_MINISTORE_ACCESS_TOKEN);
            if (StringUtil.isEmpty(accessToken)) {
                //获取授权 access_token
                StringBuffer url = new StringBuffer();
                url.append("https://api.weixin.qq.com/cgi-bin/token");
                url.append("?appid=").append(wechatProperties.getCat().getAppid());
                url.append("&secret=").append(wechatProperties.getCat().getSecret());
                url.append("&grant_type=client_credential");
                //向微信服务器发送请求
                String response = HttpUtil.executeGet(url.toString());

                System.out.println("小程序商城获取access_token = " + response);

                JSONObject json = new JSONObject(response);

                if (json.has("errcode")) {
                    log.error("小程序商城获取access_token失败。{}", json.getString("errmsg"));
                    return null;
                }

                String access_token = json.getString("access_token");
                int expires_in = json.getInt("expires_in");

                redisCache.setForHealthTestMini(Constant.WX_MINISTORE_ACCESS_TOKEN, access_token, expires_in - 30);
                return access_token;
            }
            return accessToken;
        } catch (Exception e) {
            e.printStackTrace();
            log.error("健康自测小程序获取access_token时发生异常！", e);
            throw new Exception(e.getMessage(), e);
        }
    }

    /**
     * 通过code换取网页授权access_token
     */
    @Override
    public WxOauth2Result getAccessTokenByCode(String code, String appid, String secret) {
        try {
            //获取授权信息（accessToken、openid等）
            StringBuffer url = new StringBuffer();
            url.append("https://api.weixin.qq.com/sns/oauth2/access_token");
            url.append("?appid=").append(appid);
            url.append("&secret=").append(secret);
            url.append("&code=").append(code);
            url.append("&grant_type=authorization_code");
            //向微信服务器发送请求
            String response = HttpUtil.executeGet(url.toString());
            log.info("通过code换取网页授权access_token返回结果为：{}", response);
            WxOauth2Result result = com.alibaba.fastjson.JSONObject.parseObject(response, WxOauth2Result.class);
            if (result == null || result.getErrcode() != null) {
                return null;
            }
            return result;
        } catch (Exception e) {
            log.error("通过code换取网页授权access_token出错---" + e.getMessage(), e);
            return null;
        }
    }

    /**
     * 获取用户个人信息（UnionID机制）
     */
    @Override
    public WxUserInfo getWxUserInfo(String accessToken, String openid) {
        try {
            //获取用户个人信息（UnionID机制）
            StringBuffer url = new StringBuffer();
            url.append("https://api.weixin.qq.com/sns/userinfo");
            url.append("?access_token=").append(accessToken);
            url.append("&openid=").append(openid);
            //向微信服务器发送请求
            String response = HttpUtil.executeGet(url.toString());
            log.info("获取用户个人信息（UnionID机制）返回结果为：{}", response);
            WxUserInfo result = com.alibaba.fastjson.JSONObject.parseObject(response, WxUserInfo.class);
            if (result == null || result.getErrcode() != null) {
                return null;
            }
            return result;
        } catch (Exception e) {
            log.error("获取用户个人信息（UnionID机制）---" + e.getMessage(), e);
            return null;
        }
    }

    /**
     * 获取用户基本信息(UnionID机制)
     */
    @Override
    public WxUserInfo getWxUserInfo(String openid) {
        try {
            String accessToken = this.getJSAPIAccessToken();
            StringBuffer url = new StringBuffer();
            url.append("https://api.weixin.qq.com/cgi-bin/user/info");
            url.append("?access_token=").append(accessToken);
            url.append("&openid=").append(openid);
            //向微信服务器发送请求
            String response = HttpUtil.executeGet(url.toString());
            log.info("获取用户基本信息(UnionID机制)返回结果为：{}", response);
            WxUserInfo result = com.alibaba.fastjson.JSONObject.parseObject(response, WxUserInfo.class);
            if (result == null || result.getErrcode() != null) {
                return null;
            }
            return result;
        } catch (Exception e) {
            log.error("获取用户基本信息(UnionID机制)出错---" + e.getMessage(), e);
            return null;
        }
    }

    /**
     * 生成带参数的二维码
     *
     * @param userId
     * @param shareType
     * @param shareNo   @return
     * @throws Exception
     */
    @Override
    public String getQRCodeWithParam(Integer userId, Integer shareType, String shareNo, Long dateTime) {
        try {
            String ticket = redisCache.get(userId + "_" + shareType + "_" + shareNo + "_" + dateTime + Constant.QRCODE_TICKET);
            if (StringUtil.isNotEmpty(ticket)) {
                return ticket;
            }
            String accessToken = this.getJSAPIAccessToken();
            StringBuffer url = new StringBuffer();
            url.append("https://api.weixin.qq.com/cgi-bin/qrcode/create?access_token=").append(accessToken);

            com.alibaba.fastjson.JSONObject postData = new com.alibaba.fastjson.JSONObject();
            //7天，二维码有效时间，以秒为单位。 最大不超过2592000（即30天），此字段如果不填，则默认有效期为30秒
            if (Objects.equals(HraShareTypeEnum.HRA_SHARE_CARD.value, shareType) || Objects.equals(HraShareTypeEnum.HRA_SHARE_TICKET.value, shareType) || Objects.equals(HraShareTypeEnum.HRA_SHARE_BATCH.value, shareType)) {
                postData.put("expire_seconds", 86400);//1天
            } else {
                postData.put("expire_seconds", 2592000);//30天
            }
            //二维码类型，QR_SCENE为临时的整型参数值，QR_STR_SCENE为临时的字符串参数值，QR_LIMIT_SCENE为永久的整型参数值，QR_LIMIT_STR_SCENE为永久的字符串参数值
            //postData.put("action_name", "QR_SCENE");//整形参数
            postData.put("action_name", "QR_STR_SCENE");//字符串形式

            com.alibaba.fastjson.JSONObject scene = new com.alibaba.fastjson.JSONObject();
            //场景值ID，临时二维码时为32位非0整型，永久二维码时最大值为100000（目前参数只支持1--100000）
            if (userId == null) {
                scene.put("scene_str", "");
            } else {
                scene.put("scene_str", userId + "_" + shareType + "_" + shareNo + "_" + dateTime);
            }
            com.alibaba.fastjson.JSONObject actionInfo = new com.alibaba.fastjson.JSONObject();
            actionInfo.put("scene", scene);
            //二维码详细信息
            postData.put("action_info", actionInfo);

            //向微信服务器发送请求
            String resp = HttpUtil.postJSONData(url.toString(), postData);
            System.out.println("===获取二维码ticket的返回结果===" + resp);
            JSONObject json = new JSONObject(resp);

            if (json.has("ticket")) {
                ticket = json.getString("ticket");
                int expireSeconds = json.getInt("expire_seconds");
                redisCache.set(userId + "_" + shareType + "_" + shareNo + "_" + dateTime + Constant.QRCODE_TICKET, ticket, expireSeconds - 60);
                return ticket;
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            log.error("生成带参数的二维码时发生异常！", e);
            throw new YimaoException("生成带参数的二维码时发生异常！");
        }
    }

    /**
     * 获取微信JS接口的临时票据
     *
     * @return
     * @throws Exception
     */
    @Override
    public String getWxTicket() {
        try {
            String jsapi_ticket = redisCache.get(Constant.WX_TICKET);
            if (StringUtil.isEmpty(jsapi_ticket)) {
                //获取授权 access_token
                String accessToken = this.getJSAPIAccessToken();
                StringBuffer url = new StringBuffer();
                url.append(WechatConstant.WX_GETTICKET_URL).append("?access_token=").append(accessToken).append("&type=jsapi");
                //向微信服务器发送请求
                String response = HttpUtil.executeGet(url.toString());

                System.out.println(response);

                JSONObject json = new JSONObject(response);

                int errcode = json.getInt("errcode");
                if (errcode != 0) {
                    return null;
                }

                String ticket = json.getString("ticket");
                int expires_in = json.getInt("expires_in");
                //缓存
                redisCache.set(Constant.WX_TICKET, ticket, expires_in - 30);
                return ticket;
            }
            return jsapi_ticket;
        } catch (Exception e) {
            e.printStackTrace();
            log.error("获取微信JS接口的临时票据时发生异常！", e);
            throw new YimaoException("获取微信JS接口的临时票据时发生异常！");
        }
    }

    @Override
    public void sendTemplateMessage(Map<String, Object> msgMap) {
        try {
            int type = Integer.parseInt(String.valueOf(msgMap.get("type")));
            String touser = String.valueOf(msgMap.get("touser"));
            com.alibaba.fastjson.JSONObject root = new com.alibaba.fastjson.JSONObject();

            root.put("touser", touser);
            if (type == 1 || type == 2) {
                if (type == 1) {
                    root.put("template_id", wechatProperties.getMessageTemplate().getBindSuccess());//绑定成功通知
                } else {
//                    root.put("template_id", "2PDjRwoEZS_uxLeO8vFCdRqPbiTct0f7uZxrbWGIOys");//解绑成功通知
                    root.put("template_id", wechatProperties.getMessageTemplate().getClientInvalid());//解绑成功通知
                }
                root.put("url", domainProperties.getWechat() + "/#/my?openid=" + touser);
                root.put("data", this.buildData(msgMap));
            } else if (type == 11 || type == 12 || type == 13 || type == 14) {
                root.put("template_id", wechatProperties.getMessageTemplate().getHandselSuccess());//礼品领取成功通知
                if (type == 11 || type == 12 || type == 14) {
                    root.put("url", domainProperties.getWechat() + "/#/healthy?openid=" + touser);
                } else {
                    root.put("url", domainProperties.getWechat() + "/#/my?openid=" + touser);
                }
                root.put("data", this.buildData(msgMap));
            } else if (type == 21 || type == 22) {
                root.put("template_id", wechatProperties.getMessageTemplate().getDealResult());
                root.put("data", this.buildData(msgMap));
            } else if (type == 31) {
                root.put("template_id", wechatProperties.getMessageTemplate().getOrderPaySuccess());
                root.put("data", this.buildData(msgMap));
            } else if (type == 32) {
                root.put("template_id", wechatProperties.getMessageTemplate().getOrderCheck());
                root.put("data", this.buildData(msgMap));
            } else if (type == 41) {
                root.put("template_id", wechatProperties.getMessageTemplate().getHireMaturity());
                root.put("data", this.buildData(msgMap));
            } else if (type == 51) {
                root.put("template_id", wechatProperties.getMessageTemplate().getRefundSuccess());
                root.put("data", this.buildData(msgMap));
            } else if (type == 61 || type == 62) {
                root.put("template_id", wechatProperties.getMessageTemplate().getLevelUpgrade());
                root.put("data", this.buildData(msgMap));
            } else if (type == 63 || type == 64 || type == 65) {
                root.put("template_id", wechatProperties.getMessageTemplate().getDealResult());
                root.put("data", this.buildData(msgMap));
            } else if (type == 66) {
                root.put("template_id", wechatProperties.getMessageTemplate().getReportUpload());
                root.put("data", this.buildData(msgMap));
            }

            StringBuffer url = new StringBuffer();
            url.append("https://api.weixin.qq.com/cgi-bin/message/template/send?access_token=").append(this.getJSAPIAccessToken());

            String resp = HttpUtil.postJSONData(url.toString(), root);
            JSONObject json = new JSONObject(resp);

            int errcode = json.getInt("errcode");
            if (errcode != 0) {
                log.error("调用模板消息接口失败！" + json.getString("errmsg"));
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("发送模板消息时发生异常！", e);
            throw new YimaoException("发送模板消息时发生异常！");
        }
    }

    /**
     * 新增临时素材
     *
     * @param msgType
     * @return
     * @throws Exception
     */
    @Override
    public String getMediaId(String msgType, File image) throws Exception {
        try {
            String accessToken = this.getJSAPIAccessToken();
            Map<String, String> paramMap = new HashMap<>();
            paramMap.put("access_token", accessToken);
            paramMap.put("type", msgType);

            FileItem item = new FileItem();
            item.setFieldName("media");
            item.setFile(image);
            List<FileItem> items = new ArrayList<>();
            items.add(item);
            String resp = HttpUtil.postFile("https://api.weixin.qq.com/cgi-bin/media/upload", items, paramMap);

            JSONObject json = new JSONObject(resp);
            if (json.has("errcode")) {
                log.error("新增临时素材失败。");
                log.error("resp=" + resp);
                return null;
            }

            return json.getString("media_id");
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage(), e);
            throw new Exception(e.getMessage(), e);
        }
    }

    /**
     * 健康自测小程序获取用户专属二维码
     *
     * @param userId
     * @return
     */
    @Override
    public String getWxacode(Integer userId, String scene, String page, String headImg, String oldWxacode) throws Exception {
        String accessToken = this.getMiniProgramAccessToken();
        URL url = new URL("https://api.weixin.qq.com/wxa/getwxacodeunlimit?access_token=" + accessToken);
        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
        httpURLConnection.setRequestMethod("POST");// 提交模式
        httpURLConnection.setDoOutput(true);
        httpURLConnection.setDoInput(true);
        // 获取URLConnection对象对应的输出流
        PrintWriter printWriter = new PrintWriter(httpURLConnection.getOutputStream());

        com.alibaba.fastjson.JSONObject postData = new com.alibaba.fastjson.JSONObject();
        postData.put("scene", scene);//自定义参数
        postData.put("page", page);
        postData.put("width", 430);
        postData.put("auto_color", false);

        com.alibaba.fastjson.JSONObject lineColor = new com.alibaba.fastjson.JSONObject();
        lineColor.put("r", 0);
        lineColor.put("g", 0);
        lineColor.put("b", 0);
        postData.put("line_color", lineColor);
        postData.put("is_hyaline", false);

        printWriter.write(postData.toString());
        // flush输出流的缓冲
        printWriter.flush();
        //开始获取数据
        BufferedInputStream bis = new BufferedInputStream(httpURLConnection.getInputStream());

        File wxacodeFile = new File(Constant.IMAGE_TEMP_FOLDER + userId + "_wxacode.png");
        if (EnvironmentEnum.LOCAL.code.equalsIgnoreCase(SpringContextHolder.getEnvironment())) {
            wxacodeFile = new File("D:\\tmp\\" + userId + "_wxacode.jpg");
        }
        OutputStream os = new FileOutputStream(wxacodeFile);
        int len;
        byte[] arr = new byte[1024];
        while ((len = bis.read(arr)) != -1) {
            os.write(arr, 0, len);
            os.flush();
        }
        os.flush();
        os.close();
        if (wxacodeFile.exists()) {
            //为小程序码写入头像logo
            ImageUtil.writeLogoToWxacode(wxacodeFile, headImg);
            return SFTPUtil.upload(new FileInputStream(wxacodeFile), "wxacode", "temp.png", oldWxacode);
        }
        return null;
    }


    private com.alibaba.fastjson.JSONObject buildData(Map<String, Object> msgMap) {
        String userId = String.valueOf(msgMap.get("userId"));
        String nickName = String.valueOf(msgMap.get("nickName"));
        int type = Integer.parseInt(String.valueOf(msgMap.get("type")));

        com.alibaba.fastjson.JSONObject data = new com.alibaba.fastjson.JSONObject();
        if (type == 1 || type == 2) {
            int userType = Integer.parseInt(String.valueOf(msgMap.get("userType")));
            if (type == 1) {
                data.put("first", toJson("恭喜您，【" + nickName + "】已通过扫码变为您的客户。\n"));
                data.put("keyword2", toJson(this.getUserTypeName(userType)));
            } else {
                data.put("first", toJson("真抱歉，由于【" + nickName + "】之前已有健康大使，将从您的客户中消失。\n"));
                data.put("keyword2", toJson("已失效"));
            }
            data.put("keyword1", toJson(nickName + "（e家号：" + userId + "）"));
            data.put("keyword3", toJson(DateUtil.formatCurrentTime("yyyy-MM-dd HH:mm:ss")));
            data.put("remark", toJson("\n可到【健康e家】→【我的e家】→【我的客户】中查看。\n"));
        } else if (type == 11 || type == 12 || type == 13 || type == 14) {
            String shareNo = String.valueOf(msgMap.get("shareNo"));
            if (type == 11) {
                data.put("first", toJson("您赠送的评估卡已被成功领取！\n"));
                data.put("keyword2", toJson("健康风险评估评估卡"));
                data.put("remark", toJson("\n可到【健康e家】→【健康服务】→【我的评估卡】中查看。\n"));
            } else if (type == 12) {
                data.put("first", toJson("您赠送的评估卡：" + shareNo + "已被成功领取！\n"));
                data.put("keyword2", toJson("健康风险评估评估卡"));
                data.put("remark", toJson("\n可到【健康e家】→【健康服务】→【我的评估卡】中查看。\n"));
            } else if (type == 13) {
                data.put("first", toJson("您赠送的优惠卡：" + shareNo + "已被成功领取！\n"));
                data.put("keyword2", toJson("健康风险评估优惠卡"));
                data.put("remark", toJson("\n可到【健康e家】→【我的e家】→【我的优惠卡】中查看。\n"));
            } else {
//                int receiveCount = (int) msgMap.get("receiveCount");
                int expiredCount = (int) msgMap.get("expiredCount");
                int totalCount = (int) msgMap.get("totalCount");
                if (expiredCount == 0) {
                    data.put("first", toJson("您赠送的" + totalCount + "张优惠卡,已被全部领取！\n"));
                } else {
                    data.put("first", toJson("您赠送的" + totalCount + "张优惠卡,已被领取" + (totalCount - expiredCount) + "张,其中" + expiredCount + "张已过期无法领取!\n"));
                }
                data.put("keyword2", toJson("健康风险评估优惠卡"));
                data.put("remark", toJson("\n可到【健康e家】→【我的e家】→【我的优惠卡】中查看。\n"));
            }
            data.put("keyword1", toJson(nickName + "（e家号：" + userId + "）"));
            data.put("keyword3", toJson(DateUtil.formatCurrentTime("yyyy-MM-dd HH:mm:ss")));
        } else if (type == 21 || type == 22) {
            String ticketNos = String.valueOf(msgMap.get("ticketNos"));
            int size = 1;
            if (StringUtil.isNotEmpty(ticketNos)) {
                size = ticketNos.split(",").length;
            }
            if (type == 21) {
                if (size > 1) {
                    data.put("first", toJson("您赠送的" + size + "张评估卡/优惠卡,已过期无法赠送\n"));
                } else {
                    data.put("first", toJson("您赠出的评估卡/优惠卡：" + ticketNos + "已过期,无法赠送\n"));
                }
                data.put("keyword1", toJson("赠送评估卡/优惠卡"));
                data.put("keyword2", toJson("过期提醒"));
                data.put("keyword3", toJson("已过期"));
            } else {
                if (size > 1) {
                    data.put("first", toJson("您赠送的" + size + "张评估卡/优惠卡,超时未被领取\n"));
                } else {
                    data.put("first", toJson("您赠出的评估卡/优惠卡：" + ticketNos + "超时未被领取\n"));
                }
                data.put("keyword1", toJson("赠送评估卡/优惠卡"));
                data.put("keyword2", toJson("超时提醒"));
                data.put("keyword3", toJson("已超时"));
            }
        } else if (type == 31) {
            data.put("first", toJson(msgMap.get("content")));
            data.put("keyword1", toJson(msgMap.get("nickName")));
            data.put("keyword2", toJson(msgMap.get("orderTime")));
            data.put("keyword3", toJson(msgMap.get("productName")));
            data.put("keyword4", toJson(msgMap.get("orderFee")));
            data.put("keyword5", toJson(msgMap.get("mobile")));
            data.put("remark", toJson(msgMap.get("remark")));
        } else if (type == 32) {
            data.put("first", toJson(msgMap.get("content")));
            data.put("keyword1", toJson(msgMap.get("orderId")));
            data.put("keyword2", toJson(msgMap.get("orderCheckTime")));
            data.put("keyword3", toJson(msgMap.get("checkResult")));
            data.put("remark", toJson(msgMap.get("remark")));
        } else if (type == 41) {
            data.put("first", toJson(msgMap.get("content")));
            data.put("keyword1", toJson(msgMap.get("orderId")));
            data.put("keyword2", toJson(msgMap.get("productName")));
            data.put("keyword3", toJson(msgMap.get("hireDay")));
            data.put("keyword4", toJson(msgMap.get("contractDate")));
            data.put("keyword5", toJson(msgMap.get("maturityDate")));
            data.put("remark", toJson(msgMap.get("remark")));
        } else if (type == 51) {
            data.put("first", toJson(msgMap.get("content")));
            data.put("keyword1", toJson(msgMap.get("orderId")));
            data.put("keyword2", toJson(msgMap.get("orderFee")));
            data.put("remark", toJson(msgMap.get("remark")));
        } else if (type == 61 || type == 62) {
            if (type == 61) { //推送给分销用户自己
                data.put("first", toJson("尊敬的用户，恭喜您升级为会员用户！您将拥有商城产品的会员权限!"));
                data.put("keyword1", toJson(nickName + "（e家号：" + userId + "）"));
                data.put("keyword2", toJson("会员用户"));
                data.put("keyword3", toJson(DateUtil.formatCurrentTime("yyyy-MM-dd HH:mm:ss")));
                data.put("remark", toJson("请前往【健康e家】→【我的e家】→【用户身份】查看！"));
            } else { //推送给分销用户的经销商
                data.put("first", toJson("尊敬的用户，您的客户（" + nickName + "）升级为会员用户啦! （" + nickName + "）将拥有商城产品的会员权限!"));
                data.put("keyword1", toJson(nickName + "（e家号：" + userId + "）"));
                data.put("keyword2", toJson("会员用户"));
                data.put("keyword3", toJson(DateUtil.formatCurrentTime("yyyy-MM-dd HH:mm:ss")));
                data.put("remark", toJson("请前往【健康e家】→【我的e家】→【我的客户】查看。"));
            }
        } else if (type == 63 || type == 64 || type == 65) {
            String counts = String.valueOf(msgMap.get("counts"));
            if (type == 63) {//优惠卡过期提醒
                data.put("first", toJson("尊敬的用户，您有" + counts + "张优惠卡将在7天后过期，快去翼猫体验服务中心使用吧！"));
                data.put("keyword1", toJson("优惠卡过期提醒"));
                data.put("keyword2", toJson("过期提醒"));
                data.put("keyword3", toJson("请前往[健康e家]->［我的e家］->［我的优惠卡］进行查看"));
                data.put("remark", toJson("如果遇到问题，请及时联系我们。"));
            } else if (type == 64) { //评估卡过期
                log.info("评估卡过期");
                data.put("first", toJson("尊敬的用户，您有" + counts + "张健康风险评估卡将在7天后过期，快去翼猫体验服务中心使用吧！"));
                data.put("keyword1", toJson("评估卡过期提醒"));
                data.put("keyword2", toJson("过期提醒"));
                data.put("keyword3", toJson("请前往[健康e家]->[健康服务]->［我的评估卡］进行查看"));
                data.put("remark", toJson("如果遇到问题，请及时联系我们。"));
            } else { //预约过期
                log.info("预约过期");
                data.put("first", toJson("尊敬的用户，您预约了明天（" + DateUtil.transferDateToString(DateUtil.addDays(new Date(), 1), "MM月dd日") + "）的健康检查 ，请您记得及时前往！"));
                data.put("keyword1", toJson("预约检查提醒"));
                data.put("keyword2", toJson("预约提醒"));
                data.put("keyword3", toJson("请您及时前往" + msgMap.get("stationName") + "进行检查"));
                data.put("remark", toJson("如果遇到问题，请及时联系我们。"));
            }
        } else if (type == 66) {//体检报告上传提醒
            log.info("体检报告上传提醒");
            data.put("first", toJson("尊敬的用户，您有一份电子评估报告已上传成功，请前往查看。"));
            data.put("keyword1", toJson("电子评估报告上传"));
            data.put("keyword2", toJson(DateUtil.formatCurrentTime("yyyy-MM-dd HH:mm:ss")));
            data.put("keyword3", toJson("上传成功"));
            data.put("remark", toJson("请前往[健康e家]->［健康服务］->［评估报告］查看电子报告。点击[评估报告小程序]可查看可视化评估报告。"));
        }
        return data;
    }

    private String getUserTypeName(int userType) {
        if (userType == 1) {
            return "微创版经销商";
        } else if (userType == 2) {
            return "个人版经销商";
        } else if (userType == 3) {
            return "普通用户";  //分享用户
        } else if (userType == 5) {
            return "企业版经销商（主）";
        } else if (userType == 6) {
            return "企业版经销商（子）";
        } else if (userType == 7) {
            return "会员用户";
        } else {
            return "普通用户";
        }
    }

    private com.alibaba.fastjson.JSONObject toJson(Object value) {
        com.alibaba.fastjson.JSONObject json = new com.alibaba.fastjson.JSONObject();
        json.put("value", value);
        json.put("color", "#208ad6");
        return json;
    }

    /**
     * 通过客服接口发送消息给用户
     *
     * @param openid
     * @param msgtype
     * @param content
     */
    @Override
    public void replyUserRequest(String openid, String msgtype, String content) throws Exception {
        try {
            String accessToken = this.getJSAPIAccessToken();
            StringBuffer url = new StringBuffer();
            url.append("https://api.weixin.qq.com/cgi-bin/message/custom/send?access_token=").append(accessToken);

            //参数
            com.alibaba.fastjson.JSONObject postData = new com.alibaba.fastjson.JSONObject();
            //普通用户openid
            postData.put("touser", openid);
            //消息类型，
            //文本为text，图片为image，语音为voice，视频消息为video，音乐消息为music，
            //图文消息（点击跳转到外链）为news，图文消息（点击跳转到图文消息页面）为mpnews，卡券为wxcard，小程序为miniprogrampage
            postData.put("msgtype", msgtype);
            com.alibaba.fastjson.JSONObject textField = new com.alibaba.fastjson.JSONObject();
            if (Objects.equals(WechatConstant.KEFUMSG_TYPE_TEXT, msgtype)) {
                //文本消息
                textField.put("content", content);
                postData.put("text", textField);
            } else if (Objects.equals(WechatConstant.KEFUMSG_TYPE_IMAGE, msgtype)) {
                //图片消息
                textField.put("media_id", content);
                postData.put("image", textField);
            } else {
                return;
            }

            //向微信服务器发送请求
            String resp = HttpUtil.postJSONData(url.toString(), postData);

            JSONObject json = new JSONObject(resp);

            int errcode = json.getInt("errcode");
            if (errcode != 0) {
                log.error("resp=" + resp);
                log.error("调用客服接口发送消息失败。" + json.getString("errmsg"));
            }
        } catch (Exception e) {
            throw new Exception(e.getMessage(), e);
        }
    }


    /**
     * 小程序商城--店铺二维码
     *
     * @param userId
     * @param scene
     * @param page
     * @return
     */
    @Override
    public String getStoreCode(Integer userId, String scene, String page) {

        try {
            String accessToken = this.getStoreCodeAccessToken();
            log.debug("**************获取的accessToken为：" + accessToken + "**************");
            URL url = new URL("https://api.weixin.qq.com/wxa/getwxacodeunlimit?access_token=" + accessToken);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("POST");// 提交模式
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setDoInput(true);
            // 获取URLConnection对象对应的输出流
            PrintWriter printWriter = new PrintWriter(httpURLConnection.getOutputStream());

            com.alibaba.fastjson.JSONObject postData = new com.alibaba.fastjson.JSONObject();
            postData.put("scene", "a=1234567890");//自定义参数
            //postData.put("page", page);
            postData.put("page", "");
            postData.put("width", 430);
            postData.put("auto_color", true);

            com.alibaba.fastjson.JSONObject lineColor = new com.alibaba.fastjson.JSONObject();
            lineColor.put("r", 0);
            lineColor.put("g", 0);
            lineColor.put("b", 0);
            postData.put("line_color", lineColor);
            postData.put("is_hyaline", false);


            printWriter.write(postData.toString());
            // flush输出流的缓冲
            printWriter.flush();
            //开始获取数据
            BufferedInputStream bis = new BufferedInputStream(httpURLConnection.getInputStream());

            File file = new File("D:/code/1.png");
            if (!file.exists()) {
                file.createNewFile();
            }
            OutputStream outputStream = new FileOutputStream(file);
            int len = 0;
            byte[] buf = new byte[1024];
            while ((len = bis.read(buf, 0, 1024)) != -1) {
                outputStream.write(buf, 0, len);
            }
            outputStream.flush();

//            File wxacodeFile = new File(Constant.IMAGE_TEMP_FOLDER + userId + "_mini_mall_acode.png");
//            OutputStream os = new FileOutputStream(wxacodeFile);
//            int len;
//            byte[] arr = new byte[1024];
//            while ((len = bis.read(arr)) != -1) {
//                os.write(arr, 0, len);
//                os.flush();
//            }
//            os.flush();
//            os.close();

//            if (wxacodeFile.exists()) {
//                //为小程序码写入头像logo
//                ImageUtil.writeLogoToWxacode(wxacodeFile, headImg);
//
//                String wxacodeUrl = SFTPUtil.upload(new FileInputStream(wxacodeFile), "minimallacode", null, null);
//                if (StringUtil.isNotEmpty(wxacodeUrl)) {
//                    return wxacodeUrl;
//                }
//            }

            String wxacodeUrl = SFTPUtil.upload(new FileInputStream(file), "minimallacode", null, null);
            log.debug("****************wxacodeUrl=" + wxacodeUrl + "****************");
            if (StringUtil.isNotEmpty(wxacodeUrl)) {
                return wxacodeUrl;
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            log.error("*************************异常*******************************");
            log.error(e.getMessage(), e);
            throw new NotFoundException("异常");
        }
    }

    @Override
    public File myFixedQRCodeImage(String openid) {
        try {
            Integer userId = userFeign.getUserIdByOpenid(openid);
            if (userId == null) {
                log.error("根据此openId,获取不到用户信息。 openId" + openid);
                throw new YimaoException("获取不到用户信息");
            }
            UserDTO user = userFeign.getUserDTOById(userId);
            if (user == null) {
                log.error("获取个人专属二维码失败，获取不到用户信息");
                throw new YimaoException("获取不到用户信息");
            }

            //查找本地缓存中是否有该用户的卡片
            File destFile = new File(Constant.IMAGE_TEMP_FOLDER + userId + "_qrcode.png");
            log.info("destFile.exists()=" + destFile.exists());
            // if (destFile.exists()) {
            //     if (StringUtil.isEmpty(user.getQrcode())) {
            //         //处理用户二维码保存到用户信息表中
            //         this.saveQrcode(destFile, user);
            //     }
            //     return destFile;
            // }
            //从用户信息中查找用户的固定二维码图片
            if (StringUtil.isNotEmpty(user.getQrcode())) {
                log.info("------------------------------------1----------------------------------------------");
                SFTPUtil.download("qrcode", userId + "_qrcode.png", destFile);
                if (destFile.exists()) {
                    log.info("------------------------------------2----------------------------------------------");
                    return destFile;
                }
            } else {
                log.info("------------------------------------3----------------------------------------------");
                SFTPUtil.download("qrcode", "qrcode_template.png", destFile);
            }

            Integer brandCardId = cmsFeign.getBrandCardId();
            log.info("------------------------------------4----------------------------------------------");
            String nickName = user.getNickName();
            String headImg = user.getHeadImg();
            if (!headImg.startsWith("http")) {
                log.info("------------------------------------5----------------------------------------------");
                headImg = domainProperties.getWechat() + user.getHeadImg();
            }
            String delimiter = Constant.SPECIAL_CHARACTER_SHARE;
            String content;
            content = domainProperties.getWechat() + "/#/daiyanshare/" + URLEncoder.encode(userId + delimiter + nickName + delimiter + headImg + delimiter + (brandCardId != null ? brandCardId : "21"), "UTF-8");
            //生成带用户头像logo的个人固定专属二维码
            QRCodeUtil.createQrCode(content, destFile, headImg, true, 74, 1052);

            //处理用户二维码保存到用户信息表中
            this.saveQrcode(destFile, user);
            log.info("------------------------------------20----------------------------------------------");
            return destFile;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new YimaoException(e.getMessage(), e);
        }
    }

    /**
     * 保存用户固定二维码
     *
     * @param file
     * @param userDTO
     */
    private void saveQrcode(File file, UserDTO userDTO) {
        try {
            log.info("------------------------------------17----------------------------------------------");
            String url = SFTPUtil.upload(new FileInputStream(file), "qrcode", userDTO.getId() + "_qrcode.png", null);
            log.info("------------------------------------18----------------------------------------------");
            if (StringUtil.isNotEmpty(url)) {
                log.info("------------------------------------19----------------------------------------------");
                userDTO.setQrcode(url);
                userFeign.update(userDTO);
            }
        } catch (Exception e) {
            log.info("保存用户固定二维码失败，用户e家号：" + userDTO.getId());
            log.error(e.getMessage(), e);
        }
    }
}
