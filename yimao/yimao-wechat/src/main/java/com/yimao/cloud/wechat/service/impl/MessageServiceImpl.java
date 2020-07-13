package com.yimao.cloud.wechat.service.impl;

import com.yimao.cloud.base.enums.DistributorRoleLevel;
import com.yimao.cloud.base.enums.ProductModeEnum;
import com.yimao.cloud.base.enums.UserType;
import com.yimao.cloud.base.exception.YimaoException;
import com.yimao.cloud.base.utils.*;
import com.yimao.cloud.pojo.dto.order.OrderSubDTO;
import com.yimao.cloud.pojo.dto.product.ProductDTO;
import com.yimao.cloud.pojo.dto.system.MessageContentDTO;
import com.yimao.cloud.pojo.dto.system.MessageRecordDTO;
import com.yimao.cloud.pojo.dto.user.DistributorDTO;
import com.yimao.cloud.pojo.dto.user.UserDTO;
import com.yimao.cloud.wechat.feign.OrderFeign;
import com.yimao.cloud.wechat.feign.ProductFeign;
import com.yimao.cloud.wechat.feign.SystemFeign;
import com.yimao.cloud.wechat.feign.UserFeign;
import com.yimao.cloud.wechat.service.MessageService;
import com.yimao.cloud.wechat.service.WxService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.text.DecimalFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author liuhao@yimaokeji.com
 * @version v2.0
 * @date 2019/5/29
 */
@Slf4j
@Service
public class MessageServiceImpl implements MessageService {

    @Resource
    private SystemFeign systemFeign;
    @Resource
    private WxService wxService;
    @Resource
    private UserFeign userFeign;
    @Resource
    private OrderFeign orderFeign;
    @Resource
    private ProductFeign productFeign;

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
    @Override
    public void orderPaySuccessMessage(String openId, Long orderId) {
        try {
            try {
                //发送短信
                this.sendMessageAfterOrderSuccess(openId, orderId);
            } catch (Exception e) {
                log.error("下单成功发送短信失败");
                log.error("下单成功发送短信失败", e.getMessage());
            }

            //查询是否已经推送了消息,如果已经推送,则直接返回不做处理
            List<MessageRecordDTO> messageRecords = systemFeign.messageRecordListByOrderId(orderId, 2);
            if (CollectionUtil.isNotEmpty(messageRecords)) {
                return;
            }
            //获取用户信息
            Integer userId = userFeign.getUserIdByOpenid(openId);
            UserDTO userInfo = null;
            if (Objects.nonNull(userId)) {
                userInfo = userFeign.getUserDTOById(userId);
            }
            if (userInfo == null) {
                log.error("获取用户信息错误！！");
                return;
            }
            // 如果下单用户是经销商且不是子账户, 不推送消息
            if (UserType.isDistributor(userInfo.getUserType()) && userInfo.getUserType() != UserType.DISTRIBUTOR_1000.value) {
                return;
            }
            //获取订单信息
            OrderSubDTO order = orderFeign.findOrderInfoById(orderId);
            if (Objects.isNull(order)) {
                log.error("获取不到订单信息" + orderId);
                return;
            }
            //1-实物 2-虚拟 3-租赁
            Integer orderModel = order.getProductType();
            //获取产品信息
            ProductDTO product = productFeign.getProductById(order.getProductId());

            //**********************************************************
            //如果下单用户为经销企业版子账户
            if (Objects.equals(userInfo.getUserType(), UserType.DISTRIBUTOR_1000.value)) {
                sendOrderSuccessWeiXinMessage(31, userInfo, userInfo.getId() + "", userInfo.getNickName(), order.getCreateTime(),
                        product.getName(), new DecimalFormat("###,##0.00").format(order.getFee()), userInfo.getMobile(), orderId);
            }

            //用户不是经销商
            if (!UserType.isDistributor(userInfo.getUserType())) {
                if (Objects.nonNull(userInfo.getAmbassadorId())) {
                    UserDTO ambassador = userFeign.getUserDTOById(userInfo.getAmbassadorId());
                    //判断用户的健康大使是不是经销商
                    if (!UserType.isDistributor(ambassador.getUserType())) {
                        String incomePermission = ambassador.getIncomePermission();
                        if (StringUtil.isNotEmpty(incomePermission)) {
                            String[] permission = incomePermission.split(",");
                            if (permission.length != 0) {
                                for (int i = 0; i < permission.length; i++) {
                                    int type = Integer.parseInt(permission[i].trim());
                                    if (orderModel == type) {
                                        //推送消息给分销商
                                        sendOrderSuccessWeiXinMessage(31, ambassador, userInfo.getId() + "", userInfo.getNickName(), order.getCreateTime(),
                                                product.getName(), new DecimalFormat("###,##0.00").format(order.getFee()), userInfo.getMobile(), orderId);
                                        break;
                                    }
                                }
                            }
                        }

                        //根据用户获取经销商信息
                        DistributorDTO myDistributor = this.getDistributorInfo(userInfo);
                        if (myDistributor == null) {
                            return;
                        }
                        Integer myDistributorRoleLevel = myDistributor.getRoleLevel();
                        // 如果下单用户的直属经销商为微创版经销商, 判断购买的产品是否是水机,不是的话,直接结束   微创版经销商只享受水机收益
                        if (Objects.equals(myDistributorRoleLevel, DistributorRoleLevel.D_350.value) && orderModel != ProductModeEnum.LEASE.value) {
                            return;
                        } else if (Objects.equals(myDistributorRoleLevel, DistributorRoleLevel.D_1000.value)) {
                            // 如果直属经销商是企业版子账号, 则给子账号和母账号推送消息
                            this.pushMessageByChildDistributor(userInfo, order, myDistributor, product);
                        } else {
                            //如果此经销商绑定了健康商城，推送消息
                            if (Objects.nonNull(myDistributor.getUserId())) {
                                UserDTO dto = userFeign.getUserDTOById(myDistributor.getUserId());
                                sendOrderSuccessWeiXinMessage(31, dto, userInfo.getId() + "", userInfo.getNickName(), order.getCreateTime(),
                                        product.getName(), new DecimalFormat("###,##0.00").format(order.getFee()), userInfo.getMobile(), orderId);
                            }
                        }
                    } else { //如果他的健康大使是经销商
                        DistributorDTO myDistributor = this.getDistributorInfo(ambassador);
                        if (myDistributor == null) {
                            return;
                        }
                        //判断直属上级是否是企业版子账户
                        if (Objects.equals(ambassador.getUserType(), UserType.DISTRIBUTOR_1000.value)) {
                            this.pushMessageByChildDistributor(userInfo, order, myDistributor, product);
                        } else {
                            if (Objects.equals(userInfo.getUserType(), UserType.DISTRIBUTOR_350.value) && orderModel != ProductModeEnum.LEASE.value) {
                                return;
                            }
                            sendOrderSuccessWeiXinMessage(31, ambassador, userInfo.getId() + "", userInfo.getNickName(), order.getCreateTime(),
                                    product.getName(), new DecimalFormat("###,##0.00").format(order.getFee()), userInfo.getMobile(), orderId);
                        }
                    }
                }
            } else { //下单用户是经销商
                //获取用户的经销商信息
                DistributorDTO distributor = this.getDistributorInfo(userInfo);
                if (distributor == null) {
                    return;
                }
                Integer distributorRoleLevel = distributor.getRoleLevel();
                if (Objects.equals(DistributorRoleLevel.D_1000.value, distributorRoleLevel)) {
                    // 如果直属经销商是企业版子账号, 则给子账号和母账号推送消息
                    this.pushMessageByChildDistributor(userInfo, order, distributor, product);
                } else {
                    if (Objects.equals(DistributorRoleLevel.D_350.value, distributorRoleLevel) && orderModel != 3) {
                        return;
                    }
                    //如果此经销商绑定了健康商城，推送消息模板
                    if (Objects.nonNull(distributor.getUserId())) {
                        UserDTO dto = userFeign.getUserDTOById(distributor.getUserId());
                        sendOrderSuccessWeiXinMessage(31, dto, userInfo.getId() + "", userInfo.getNickName(), order.getCreateTime(),
                                product.getName(), new DecimalFormat("###,##0.00").format(order.getFee()), userInfo.getMobile(), orderId);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("MessageApiImpl.orderPaySuccess error : ", e);
            throw new YimaoException(e.getMessage(), e);
        }
    }

    /**
     * 推送消息
     *
     * @param toUser
     * @param userInfo
     * @param order
     * @param productName
     */
    private void accordingOrderUserType(UserDTO toUser, UserDTO userInfo, OrderSubDTO order, String productName) {
        sendOrderSuccessWeiXinMessage(31, toUser, userInfo.getId() + "", userInfo.getNickName(), order.getCreateTime(),
                productName, new DecimalFormat("###,##0.00").format(order.getFee()), userInfo.getMobile(), order.getId());
    }

    private void sendOrderSuccessWeiXinMessage(Integer type, UserDTO touser, String id, String nickName,
                                               Date orderTime, String productName, String orderFee, String mobile, Long orderId) {
        if (touser == null) {
            return;
        }
        Map<String, Object> msgMap = new HashMap<>(8);
        msgMap.put("type", type);                            //模板编号
        msgMap.put("touser", touser.getOpenid());            //发送给谁
        msgMap.put("userId", id);                            //下单人id
        msgMap.put("nickName", id + "（昵称：" + nickName + ")");                    //下单人昵称
        msgMap.put("orderTime", DateUtil.transferDateToString(orderTime, DateUtil.DATEFORMAT_02));                  //下单时间
        msgMap.put("productName", productName);              //下单产品名称
        msgMap.put("orderFee", orderFee);                    //订单总金额
        msgMap.put("mobile", mobile);                        //下单人联系方式
        msgMap.put("remark", "请前往【翼猫商城】→【我的e家】→【我的客户订单】进行查看");
        msgMap.put("content", "尊敬的用户，您有一笔新的订单（订单号：" + orderId + "）\n");

        Long contentId = UUIDUtil.buildOrderId(21);
        MessageContentDTO messageContent = new MessageContentDTO();
        messageContent.setId(contentId);
        messageContent.setContent(msgMap.get("content") + "");
        systemFeign.messageContentSave(messageContent);

        MessageRecordDTO messageRecord = new MessageRecordDTO();
        messageRecord.setOrderId(orderId);
        messageRecord.setUserId(touser.getId());
        messageRecord.setOpenid(touser.getOpenid());
        messageRecord.setContentId(contentId);
        messageRecord.setType(2);
        messageRecord.setCreateTime(orderTime);
        systemFeign.messageRecordSave(messageRecord);
        wxService.sendTemplateMessage(msgMap);
    }


    /**
     * 发送短信
     *
     * @param openid  openid
     * @param orderId 订单号
     */
    private void sendMessageAfterOrderSuccess(String openid, Long orderId) {
        log.info("短信发送开始。订单号为：=============" + orderId + "==================================");
        log.info("======================================================================================");
        // 查询数据库,根据是否有数据判断信息是否已经发送成功
        List<MessageRecordDTO> messageRecords = systemFeign.messageRecordListByOrderId(orderId, 1);
        if (CollectionUtil.isNotEmpty(messageRecords)) {
            return;
        }
        Integer userId = userFeign.getUserIdByOpenid(openid);
        if (userId == null) {
            return;
        }
        UserDTO orderUser = userFeign.getUserDTOById(userId);
        if (orderUser == null) {
            return;
        }
        //如果下单用户是经销商且不是子账户,直接返回,不发送短信通知
        if (UserType.isDistributor(orderUser.getUserType()) && orderUser.getUserType() != 6) {
            return;
        }

        //获取用户的经销商信息
        DistributorDTO myDistributor = this.getDistributorInfo(orderUser);
        if (myDistributor == null || Objects.equals(orderUser.getId(), myDistributor.getId())) {
            return;
        }
        OrderSubDTO order = orderFeign.findOrderInfoById(orderId);
        //微创 只推送水机消息
        if (Objects.equals(myDistributor.getRoleLevel(), DistributorRoleLevel.D_350.value) && order.getProductType() != 3) {
            return;
        }
        if (Objects.equals(myDistributor.getRoleLevel(), DistributorRoleLevel.D_1000.value)) {
            if (myDistributor.getPid() == null) {
                log.error("经销商子账号的pid不能为空");
                return;
            }
            myDistributor = userFeign.getDistributorById(myDistributor.getPid());
            if (myDistributor == null) {
                return;
            }
        }
        String str = orderId + "";
        String tailNumber = str.substring(str.length() - 6, str.length());
        // Date payTime = order.getPayTime();
        String text;

        Long contentId = UUIDUtil.buildOrderId(22);
        MessageContentDTO messageContent = new MessageContentDTO();
        messageContent.setId(contentId);

        MessageRecordDTO messageRecord = new MessageRecordDTO();
        messageRecord.setOrderId(orderId);
        messageRecord.setPhone(myDistributor.getPhone());
        messageRecord.setContentId(contentId);
        messageRecord.setType(1);
        messageRecord.setCreateTime(new Date());

        String myDistributorMobile = myDistributor.getPhone();
        if (StringUtil.isNotEmpty(myDistributorMobile)) {
            String regex = "^1[3|4|5|6|7|8|9][0-9]{9}$";
            if (myDistributorMobile.length() != 11) {
                System.out.println("手机号应为11位数");
                log.error("手机号应为11位数");
            } else {
                Pattern p = Pattern.compile(regex);
                Matcher m = p.matcher(myDistributorMobile);
                boolean isMatch = m.matches();
                if (isMatch) {
                    text = "【翼猫健康e家】尊敬的用户，您有一笔新的订单（订单尾数" + tailNumber + "），关注“翼猫健康e家”微信公众号，可至［ 我的e家］－［我的客户订单］查看。";
                    if (StringUtil.isNotEmpty(myDistributor.getUserName())) {
                        text = "【翼猫健康e家】尊敬的用户（经销商账号：" + myDistributor.getUserName() + "），您有一笔新的订单（订单尾数" + tailNumber + "），关注“翼猫健康e家”微信公众号，可至［ 我的e家］－［我的客户订单］查看。";
                    }
                    messageContent.setContent(text);
                    systemFeign.messageContentSave(messageContent);
                    systemFeign.messageRecordSave(messageRecord);
                    SmsUtil.sendSms(text, myDistributor.getPhone());
                }
            }
        }
    }

    /**
     * 获取经销商信息
     *
     * @param userDTO
     * @return
     */
    private DistributorDTO getDistributorInfo(UserDTO userDTO) {
        if (Objects.isNull(userDTO)) {
            throw new YimaoException("获取用户信息失败~");
        }
        Integer distributorId;
        if (UserType.isDistributor(userDTO.getUserType())) {
            distributorId = userDTO.getMid();
        } else {
            distributorId = userDTO.getDistributorId();
        }
        return userFeign.getDistributorById(distributorId);
    }

    /**
     * 如果此用户是企业版子账号
     *
     * @param userInfo      用户信息
     * @param order         订单信息
     * @param myDistributor 经销商信息
     * @param product       产品信息
     */
    private void pushMessageByChildDistributor(UserDTO userInfo, OrderSubDTO order, DistributorDTO myDistributor, ProductDTO product) {
        if (Objects.nonNull(myDistributor.getUserId())) {
            UserDTO userDTO = userFeign.getUserDTOById(myDistributor.getUserId());
            accordingOrderUserType(userDTO, userInfo, order, product.getName());
            if (Objects.nonNull(myDistributor.getPid())) {
                DistributorDTO distributorDTO = userFeign.getDistributorById(myDistributor.getPid());
                if (Objects.nonNull(distributorDTO) && Objects.nonNull(distributorDTO.getUserId())) {
                    UserDTO disUserDTO = userFeign.getUserDTOById(distributorDTO.getUserId());
                    accordingOrderUserType(disUserDTO, userInfo, order, product.getName());
                }
            }
        }
    }
}
