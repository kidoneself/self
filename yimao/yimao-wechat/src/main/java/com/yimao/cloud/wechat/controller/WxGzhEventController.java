package com.yimao.cloud.wechat.controller;

import com.yimao.cloud.base.constant.Constant;
import com.yimao.cloud.base.constant.RabbitConstant;
import com.yimao.cloud.base.constant.WechatConstant;
import com.yimao.cloud.base.enums.HraShareTypeEnum;
import com.yimao.cloud.base.enums.HraTicketStatusEnum;
import com.yimao.cloud.base.enums.HraType;
import com.yimao.cloud.base.enums.IdentityTypeEnum;
import com.yimao.cloud.base.properties.DomainProperties;
import com.yimao.cloud.base.utils.CollectionUtil;
import com.yimao.cloud.base.utils.DateUtil;
import com.yimao.cloud.base.utils.StringUtil;
import com.yimao.cloud.base.utils.WXPayUtil;
import com.yimao.cloud.framework.cache.RedisCache;
import com.yimao.cloud.pojo.dto.hra.HraTicketDTO;
import com.yimao.cloud.pojo.dto.hra.HraTicketLifeCycleDTO;
import com.yimao.cloud.pojo.dto.user.UserDTO;
import com.yimao.cloud.pojo.dto.wechat.WxUserInfo;
import com.yimao.cloud.wechat.feign.HraFeign;
import com.yimao.cloud.wechat.feign.UserFeign;
import com.yimao.cloud.wechat.service.WxService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author Zhang Bo
 * @date 2017/12/16.
 */
@RestController
@Api(tags = "WxGzhEventController")
@Slf4j
public class WxGzhEventController {

    @Resource
    private DomainProperties domainProperties;
    @Resource
    private UserFeign userFeign;
    @Resource
    private WxService wxService;
    @Resource
    private AmqpTemplate rabbitTemplate;
    @Resource
    private HraFeign hraFeign;
    @Resource
    private RedisCache redisCache;


    @RequestMapping(value = {"/wxgzh/event"}, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public void wxgzhEvent(HttpServletRequest request, HttpServletResponse response) {
        try {
            log.info("==/wxgzh/event===执行回调事件1===");
            response.setCharacterEncoding("utf-8");
            PrintWriter out = response.getWriter();

            InputStream inStream = request.getInputStream();
            ByteArrayOutputStream outSteam = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len;
            while ((len = inStream.read(buffer)) != -1) {
                outSteam.write(buffer, 0, len);
            }
            outSteam.close();
            inStream.close();
            String result = new String(outSteam.toByteArray(), "utf-8");
            Map<String, String> map = WXPayUtil.xmlToMap(result);

            String toUserName = map.get("ToUserName");
            String msgType = map.get("MsgType");
            String event = map.get("Event");
            String openid = map.get("FromUserName");

            log.info("======================================================");
            log.info("公众号事件类型===MsgType=" + msgType + "，Event=" + event + "，EventKey=" + map.get("EventKey"));
            log.info("======================================================");

            String content = "";
            if (Objects.equals(msgType.toUpperCase(), "TEXT")) {
                //文本回复
            } else if (Objects.equals(msgType.toUpperCase(), "EVENT")) {
                Integer sharerId = null;
                Integer shareType = null;
                String shareNo = null;
                Long dateTime = null;
                String eventKey = map.get("EventKey");
                Integer origin = null;
                if (Objects.equals(event.toUpperCase(), "SUBSCRIBE")) {//关注
                    //先回复微信防止超时
                    String text = "亲，见到你真好。\n\n"
                            + "翼猫健康e家将成为您的健康使者，为您提供最专业的健康服务[爱心][爱心][爱心]。\n\n"
                            + "1.查找\"翼猫线下体验店\"，享受优质服务\n"
                            + "<a href=\"" + domainProperties.getWechat() + "/?#/shop/exprshop2\">点击进入</a>\n"
                            + "2.查看\"老客户最新福利\"，限时参与\n"
                            + "<a href=\"" + domainProperties.getWechat() + "/cms/c?id=85\">点击查看</a>\n"
                            + "3.查看\"新客户优惠活动\"，限时参与\n"
                            + "<a href=\"" + domainProperties.getWechat() + "/cms/c?id=86\">点击查看</a>\n"
                            + "4.进入\"健康自测小神器\"，轻松掌握健康\n"
                            + "<a href=\"" + domainProperties.getWechat() + "/cms/c?id=20\">点击进入</a>\n"
                            + "5.不太会操作？客服助手来帮忙\n"
                            + "<a href=\"" + domainProperties.getWechat() + "/?#/my/kf\">点击进入</a>\n"
                            + "6.在线咨询\n"
                            + "点击左下角键盘小图标，切换到聊天互动界面~\n"
                            + "7.客服热线\n"
                            + "4001519999\n\n"
                            + "点击下方菜单栏，更多惊喜等你哦！";
                    out.println(buildTextMessage(openid, toUserName, text));
                    out.flush();
                    out.close();
                    String[] arr = eventKey.split("_");
                    int length = arr.length;
                    if (length == 2) {
                        origin = Integer.parseInt(arr[1]);
                    } else if (length == 3) {
                        sharerId = Integer.parseInt(arr[1]);
                        shareType = Integer.parseInt(arr[2]);
                    } else if (length == 5) {
                        sharerId = Integer.parseInt(arr[1]);
                        shareType = Integer.parseInt(arr[2]);
                        shareNo = arr[3];
                        dateTime = Long.parseLong(arr[4]);
                    }
                    //根据关注事件保存微信用户信息以及为分享用户绑定健康大使
                    WxUserInfo wxUserInfo = wxService.getWxUserInfo(openid);
                    //创建用户
                    log.info("关注公众号开始");
                    UserDTO user = userFeign.userProcess(IdentityTypeEnum.WECHAT_GZH.value, sharerId, wxUserInfo, origin);
                    log.info("关注公众号结束" + sharerId + "<====>" + wxUserInfo.getOpenid());
                    //保存关注记录
                    rabbitTemplate.convertAndSend(RabbitConstant.USER_SUBSCRIBE_QUEUE, user);
                    if (sharerId != null) {
                        UserDTO sharer = userFeign.getUserDTOById(sharerId);
                        //赠送逻辑
                        if (shareType != 0) {
                            content = this.handleTicketOwner(sharer, user, shareType, shareNo, dateTime);
                            if (StringUtil.isNotEmpty(content)) {
                                //再用客服接口发送消息给用户
                                wxService.replyUserRequest(openid, WechatConstant.KEFUMSG_TYPE_TEXT, content);
                            }
                        }
                    }
                } else if (Objects.equals(event.toUpperCase(), "SCAN")) {//扫码（识别二维码）
                    log.info("识别二维码开始");
                    //先回复微信防止超时
                    out.println("success");
                    out.flush();
                    out.close();
                    String[] arr = eventKey.split("_");
                    int length = arr.length;
                    if (length == 2) {
                        sharerId = Integer.parseInt(arr[0]);
                        shareType = Integer.parseInt(arr[1]);
                    } else if (length == 4) {
                        sharerId = Integer.parseInt(arr[0]);
                        shareType = Integer.parseInt(arr[1]);
                        shareNo = arr[2];
                        dateTime = Long.parseLong(arr[3]);
                    }

                    log.info("====解析二维码===:" + sharerId + "=========:" + shareType + "======:" + shareNo + "========:" + dateTime);
                    //根据关注事件保存微信用户信息以及为分享用户绑定健康大使
                    WxUserInfo wxUserInfo = wxService.getWxUserInfo(openid);

                    //根据openid获取用户id
                    Integer userid = userFeign.getUserIdByOpenid(openid);
                    //排除 自己扫描自己的卡和二维码
                    if (Objects.isNull(userid) || Objects.equals(sharerId, userid)) {
                        log.error("自己不能领取自己赠送出去的卡");
                        return;
                    }
                    UserDTO user = userFeign.userProcess(IdentityTypeEnum.WECHAT_GZH.value, sharerId, wxUserInfo, null);
                    if (sharerId != null) {
                        UserDTO sharer = userFeign.getUserDTOById(sharerId);
                        //赠送逻辑
                        content = this.handleTicketOwner(sharer, user, shareType, shareNo, dateTime);
                        if (StringUtil.isNotEmpty(content)) {
                            //再用客服接口发送消息给用户
                            wxService.replyUserRequest(openid, WechatConstant.KEFUMSG_TYPE_TEXT, content);
                        }
                    }
                } else if (Objects.equals(event.toUpperCase(), "UNSUBSCRIBE")) {//取消关注
                    //记录取关信息
                    log.info("取消关注公众号开始");
                    rabbitTemplate.convertAndSend(RabbitConstant.USER_UNSUBSCRIBE_QUEUE, openid);
                    log.info("取消关注公众号结束");

                } else if (Objects.equals(event.toUpperCase(), "CLICK")) {
                    if (Objects.equals(eventKey, "我的固定二维码")) {
                        out.println("success");
                        out.flush();
                        out.close();
                        //获取我的专属二维码
                        File image = wxService.myFixedQRCodeImage(openid);
                        if (image != null) {
                            String mediaId = wxService.getMediaId("image", image);
                            wxService.replyUserRequest(openid, WechatConstant.KEFUMSG_TYPE_IMAGE, mediaId);
                        }
                    } else if (Objects.equals(eventKey, "总部客服")) {
                        // content = "请拨打热线：4001519999。";
                        content = "1.人工咨询\n" +
                                "点击左下角键盘小图标，切换到微信公众号聊天互动界面。可直接与客服聊天~\n" +
                                "\n" +
                                "2.客服热线\n" +
                                "4001519999";
                    }
                }
            }

            if (StringUtil.isEmpty(content)) {
                out.println("success");
                out.flush();
                out.close();
            } else {
                out.println(buildTextMessage(openid, toUserName, content));
                out.flush();
                out.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("WxGzhEventController.wxgzhEvent error : ", e);
        }
    }

    /**
     * 领取逻辑
     *
     * @param sharer      分享人
     * @param user        用户
     * @param shareType   分享类型 1-体检卡 2-体检劵  4-批量赠送
     * @param shareNo     分享卡
     * @param handselFlag 标志
     * @return String
     */
    private String handleTicketOwner(UserDTO sharer, UserDTO user, Integer shareType, String shareNo, Long handselFlag) {
        if (shareType == null || Objects.equals(HraShareTypeEnum.HRA_SHARE_OWNER.value, shareType)) {
            return "";
        } else if (Objects.equals(HraShareTypeEnum.HRA_SHARE_CARD.value, shareType) || Objects.equals(HraShareTypeEnum.HRA_SHARE_CARD_TICKET.value, shareType) || Objects.equals(HraShareTypeEnum.HRA_SHARE_TICKET.value, shareType)) {
            if (sharer != null && sharer.getId().intValue() != user.getId().intValue() && StringUtil.isNotEmpty(shareNo)) {
                HraTicketLifeCycleDTO ticketLifeCycle = hraFeign.findTicketLifeCycleByHandselFlag(handselFlag);
                if (ticketLifeCycle == null) {
                    return "记录不存在";
                }
                Date handselTime = ticketLifeCycle.getHandselTime();
                Integer expiredFlag = ticketLifeCycle.getExpiredFlag();
                if (expiredFlag == 2 || new Date().after(DateUtil.addDays(handselTime, 1))) {
                    if (shareNo.startsWith(HraType.Y.value)) {
                        return "很抱歉,【" + sharer.getNickName() + "】赠送的评估卡领取超时,已被退回";
                    } else if (shareNo.startsWith(HraType.M.value)) {
                        return "很抱歉,【" + sharer.getNickName() + "】赠送的优惠卡领取超时,已被退回";
                    }
                }
                boolean b;
                Date validEndTime;
                Integer ticketStatus;
                switch (shareType) {
                    case 1://赠送卡
                        List<HraTicketDTO> hraTickets = hraFeign.ticketByCardId(shareNo);
                        if (CollectionUtil.isEmpty(hraTickets)) {
                            return "评估卡不存在";
                        }
                        validEndTime = hraTickets.get(0).getValidEndTime();
                        ticketStatus = hraTickets.get(0).getTicketStatus();
                        if (validEndTime == null || validEndTime.before(new Date()) || Objects.equals(HraTicketStatusEnum.HRA_EXPIRE.value, ticketStatus)) {
                            if (shareNo.startsWith(HraType.Y.value)) {
                                return "很抱歉,【" + sharer.getNickName() + "】赠送的评估卡已过期,无法领取";
                            }
                        }
                        b = hraFeign.changeTicketOwner(sharer.getId(), user.getId(), shareNo, HraShareTypeEnum.HRA_SHARE_CARD.value, handselFlag);
                        if (b) {
                            Map<String, Object> msgMap = new HashMap<>(8);
                            msgMap.put("touser", sharer.getOpenid());
                            msgMap.put("userId", user.getId());
                            msgMap.put("nickName", user.getNickName());
                            msgMap.put("shareNo", shareNo);
                            msgMap.put("type", 11);
                            rabbitTemplate.convertAndSend(RabbitConstant.WX_TEMPLATE_MESSAGE_QUEUE, msgMap);
                            return "恭喜您，领取到【" + sharer.getNickName() + "】赠送的评估卡，"
                                    + "请到【翼猫商城】→【健康服务】→【我的评估卡】中查看。\n"
                                    + DateUtil.formatCurrentTime("yyyy-MM-dd HH:mm:ss");
                        } else {
                            return "很遗憾，该评估卡已被别人领取。";
                        }
                    case 2://赠送券
                        HraTicketDTO hraTicket = hraFeign.ticketByTicketNo(shareNo);
                        if (hraTicket == null) {
                            return "评估卡不存在";
                        }
                        validEndTime = hraTicket.getValidEndTime();
                        ticketStatus = hraTicket.getTicketStatus();
                        if (validEndTime == null || validEndTime.before(new Date()) || Objects.equals(HraTicketStatusEnum.HRA_EXPIRE.value, ticketStatus)) {
                            if (shareNo.startsWith(HraType.Y.value)) {
                                return "很抱歉,【" + sharer.getNickName() + "】赠送的评估卡已过期,无法领取";
                            } else if (shareNo.startsWith(HraType.M.value)) {
                                return "很抱歉,【" + sharer.getNickName() + "】赠送的优惠卡已过期,无法领取";
                            }
                        }
                        if (shareNo.startsWith(HraType.Y.value)) {//评估券
                            b = hraFeign.changeTicketOwner(sharer.getId(), user.getId(), shareNo, HraShareTypeEnum.HRA_SHARE_CARD_TICKET.value, handselFlag);
                        } else {//优惠券
                            b = hraFeign.changeTicketOwner(sharer.getId(), user.getId(), shareNo, HraShareTypeEnum.HRA_SHARE_TICKET.value, handselFlag);
                        }
                        if (b) {
                            if (StringUtil.isNotEmpty(shareNo) && shareNo.startsWith(HraType.Y.value)) {
                                Map<String, Object> msgMap = new HashMap<>(8);
                                msgMap.put("touser", sharer.getOpenid());
                                msgMap.put("userId", user.getId());
                                msgMap.put("nickName", user.getNickName());
                                msgMap.put("shareNo", shareNo);
                                msgMap.put("type", 12);
                                rabbitTemplate.convertAndSend(RabbitConstant.WX_TEMPLATE_MESSAGE_QUEUE, msgMap);
                                return "恭喜您，领取到【" + sharer.getNickName() + "】赠送的评估卡：\n" + shareNo + "，\n"
                                        + "请到【翼猫商城】→【健康服务】→【我的评估卡】中查看。\n"
                                        + DateUtil.formatCurrentTime("yyyy-MM-dd HH:mm:ss");
                            }
                            if (StringUtil.isNotEmpty(shareNo) && shareNo.startsWith(HraType.M.value)) {
                                Map<String, Object> msgMap = new HashMap<>(8);
                                msgMap.put("touser", sharer.getOpenid());
                                msgMap.put("userId", user.getId());
                                msgMap.put("nickName", user.getNickName());
                                msgMap.put("shareNo", shareNo);
                                msgMap.put("type", 13);
                                wxService.sendTemplateMessage(msgMap);
                                return "恭喜您，领取到【" + sharer.getNickName() + "】赠送的优惠卡：\n" + shareNo + "，\n"
                                        + "请到【翼猫商城】→【我的e家】→【我的优惠卡】中查看。\n"
                                        + DateUtil.formatCurrentTime("yyyy-MM-dd HH:mm:ss");
                            }
                        } else {
                            if (StringUtil.isNotEmpty(shareNo) && shareNo.startsWith(HraType.Y.value)) {
                                return "很遗憾，该评估卡已被别人领取。";
                            }
                            if (StringUtil.isNotEmpty(shareNo) && shareNo.startsWith(HraType.M.value)) {
                                return "很遗憾，该优惠卡已被别人领取。";
                            }
                        }
                    default:
                        return "";
                }
            }
        } else if (Objects.equals(HraShareTypeEnum.HRA_SHARE_BATCH.value, shareType)) {
            log.info("#############################批量赠送#######################################");
            log.info("############################" + sharer.getId() + "################################");
            log.info("############################" + shareType + "################################");
            log.info("############################" + shareNo + "################################");
            log.info("############################" + handselFlag + "################################");
            List<String> list = redisCache.getCacheList(Constant.TICKETNO + shareNo, String.class);
            if (CollectionUtil.isEmpty(list) || new Date().after(DateUtil.dayAfter(new Date(handselFlag), 1))) {
                return "很抱歉,【" + sharer.getNickName() + "】赠送的评估卡领取超时,已被退回";
            }
            List<HraTicketDTO> hraTicketList = hraFeign.getHraTicketListByTicketNoList(list, handselFlag);
            if (CollectionUtil.isEmpty(hraTicketList)) {
                return "";
            }
            List<String> expiredList = new ArrayList<>();
            List<String> handselList = new ArrayList<>();
            List<String> receiveList = new ArrayList<>();
            for (HraTicketDTO h : hraTicketList) {
                Integer ticketStatus = h.getTicketStatus();
                Integer handselStatus = h.getHandselStatus();
                String ticketNo = h.getTicketNo();
                if (ticketStatus == 4) {
                    expiredList.add(ticketNo);
                }
                if (handselStatus == 1) {
                    handselList.add(ticketNo);
                } else if (handselStatus == 2) {
                    receiveList.add(ticketNo);
                }
            }
            int totalCount = hraTicketList.size();       //赠送卡数量
            int expiredCount = expiredList.size();       //过期卡数量
            int handselCount = handselList.size();       //处于赠送中状态的卡的数量

            if (handselCount == 0) {
                return "很遗憾，该优惠卡已被领取。";
            } else if (expiredCount == totalCount) {
                return "很抱歉,【" + sharer.getNickName() + "】赠送的优惠卡已过期,无法领取";
            }
            Date receiveTime = new Date();
            Integer sharerId = sharer.getId();
            Integer destId = user.getId();

            //赠送卡,采用事务处理,全部领取成功或者领取不成功 同时添加体检卡生命周期表中的领取信息
            hraFeign.changeTicketOwnerBatch(sharerId, destId, handselList, receiveTime, handselFlag);

            Map<String, Object> msgMap = new HashMap<>(8);
            msgMap.put("touser", sharer.getOpenid());
            msgMap.put("userId", user.getId());
            msgMap.put("nickName", user.getNickName());
            msgMap.put("shareNo", shareNo);
            msgMap.put("receiveCount", handselCount);
            msgMap.put("expiredCount", expiredCount);
            msgMap.put("totalCount", totalCount);
            msgMap.put("type", 14);
            rabbitTemplate.convertAndSend(RabbitConstant.WX_TEMPLATE_MESSAGE_QUEUE, msgMap);

            return "恭喜您，领取到【" + sharer.getNickName() + "】赠送的" + (totalCount - expiredCount) + "张优惠卡!\n"
                    + "请到【翼猫商城】→【我的e家】→【我的优惠卡】中查看。\n"
                    + DateUtil.formatCurrentTime("yyyy-MM-dd HH:mm:ss");

        }
        return "";
    }

    /**
     * 回复文本消息
     *
     * @param openid
     * @param toUserName
     * @param content
     * @return
     */
    private String buildTextMessage(String openid, String toUserName, String content) {
        return "<xml>"
                + "<ToUserName><![CDATA[" + openid + "]]></ToUserName>"
                + "<FromUserName><![CDATA[" + toUserName + "]]></FromUserName>"
                + "<CreateTime>" + System.currentTimeMillis() + "</CreateTime>"
                + "<MsgType><![CDATA[text]]></MsgType>"
                + "<Content><![CDATA[" + content + "]]></Content>"
                + "</xml>";
    }

    /**
     * 回复图片消息
     *
     * @param openid
     * @param toUserName
     * @param content
     * @return
     */
    private String buildImageMessage(String openid, String toUserName, String content) {
        return "<xml>"
                + "<ToUserName><![CDATA[" + openid + "]]></ToUserName>"
                + "<FromUserName><![CDATA[" + toUserName + "]]></FromUserName>"
                + "<CreateTime>" + System.currentTimeMillis() + "</CreateTime>"
                + "<MsgType><![CDATA[image]]></MsgType>"
                + "<Image><MediaId><![CDATA[" + content + "]]></MediaId></Image>"
                + "</xml>";
    }

}
