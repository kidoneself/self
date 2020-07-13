package com.yimao.cloud.hra.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.yimao.cloud.base.constant.RabbitConstant;
import com.yimao.cloud.base.utils.AESUtil;
import com.yimao.cloud.base.utils.SFTPUtil;
import com.yimao.cloud.base.utils.StringUtil;
import com.yimao.cloud.hra.dto.HraRequest;
import com.yimao.cloud.hra.dto.StationInfo;
import com.yimao.cloud.hra.mapper.*;
import com.yimao.cloud.hra.msg.HraResult;
import com.yimao.cloud.hra.po.*;
import com.yimao.cloud.hra.service.HraAesService;
import com.yimao.cloud.hra.service.HraTicketLimitService;
import com.yimao.cloud.pojo.dto.hra.HraCustomerDTO;
import com.yimao.cloud.pojo.dto.hra.HraTicketDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import javax.servlet.http.Part;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.zip.ZipException;

/**
 * @author Zhang Bo
 * @date 2017/12/6.
 */
@Service
@Slf4j
public class HraAesServiceImpl implements HraAesService {

    @Resource
    private AmqpTemplate rabbitTemplate;
    @Resource
    private HraDeviceMapper hraDeviceMapper;
    @Resource
    private HraCustomerMapper hraCustomerMapper;
    @Resource
    private HraReportMapper hraReportMapper;
    @Resource
    private HraReportJsonMapper hraReportJsonMapper;
    @Resource
    private HraTicketMapper hraTicketMapper;
    @Resource
    private HraTicketLimitService hraTicketLimitService;
    @Resource
    private HraCardMapper hraCardMapper;

    /**
     * 接口编号：Interface_001
     * 接口名称：新增或修改翼猫服务站信息
     * 接口功能说明：把本地配置的翼猫服务站信息上传到云端
     * 涉及功能编号：func_001
     * URL地址：http://hraapi.yimaokeji.com:10090/ServiceStation
     */
    @Override
    public HraResult serviceStation(String aesContent) throws Exception {
        try {
            String content = AESUtil.AESDncode(aesContent);

            // content = content.replaceAll("\r|\n|\t", "");
            log.info("新增或修改翼猫服务站信息->解密后的请求参数为：" + content);

            // HraRequest hraRequest = JsonUtil.jsonToPojo(content, HraRequest.class);
            HraRequest hraRequest = JSONObject.parseObject(content, HraRequest.class);
            if (hraRequest == null) {
                log.error("新增或修改翼猫服务站信息->输入参数错误：" + content);
                return HraResult.build("100", "输入参数错误。");
            }
            String deviceId = hraRequest.getDeviceId();
            StationInfo stationInfo = hraRequest.getStationInfo();

            //①当输入参数缺失、不完整或错误时，“code”:“100”， “msg”:“输入参数错误。”，“data”:null。
            if (StringUtil.isEmpty(deviceId)) {
                log.error("新增或修改翼猫服务站信息->设备ID为空。" + content);
                return HraResult.build("100", "输入参数错误。设备ID不能为空。");
            }
            if (stationInfo == null) {
                log.error("新增或修改翼猫服务站信息->服务站信息为空。" + content);
                return HraResult.build("100", "输入参数错误。服务站信息不能为空。");
            }
            String name = stationInfo.getName();
            String add = stationInfo.getAdd();
            String tel = stationInfo.getTel();
            if (StringUtil.isEmpty(name) || StringUtil.isEmpty(add) || StringUtil.isEmpty(tel)) {
                log.error("新增或修改翼猫服务站信息->输入参数错误：" + content);
                return HraResult.build("100", "输入参数错误。");
            }

            deviceId = this.formatDeviceId(deviceId);
            HraDevice hraDevice = this.getHraDeviceById(deviceId);
            //④当输入参数正确时，“code”:“500”， “msg”:“服务站设备添加失败。”，“data”:null。
            if (hraDevice == null) {
                log.error("新增或修改翼猫服务站信息->设备不存在，请联系翼猫总部：" + deviceId);
                return HraResult.build("500", deviceId + "设备不存在，请联系翼猫总部。");
            }
            //在HRA设备信息表中添加服务站信息
            if (StringUtil.isEmpty(hraDevice.getStationName())) {
                hraDevice.setStationName(name);
                hraDevice.setStationAddress(add);
                hraDevice.setStationTel(tel);
                if (hraDeviceMapper.updateByPrimaryKey(hraDevice) > 0) {
                    //②当输入参数正确时，“code”:“200”， “msg”:“服务站设备添加成功。”，“data”:null。
                    return HraResult.build("200", "服务站设备添加成功。");
                } else {
                    ////④当输入参数正确时，“code”:“500”， “msg”:“服务站设备添加失败。”，“data”:null。
                    log.error("新增或修改翼猫服务站信息->服务站设备添加失败：" + deviceId);
                    return HraResult.build("500", "服务站设备添加失败。");
                }
            } else {//在HRA设备信息表中修改服务站信息
                hraDevice.setStationName(name);
                hraDevice.setStationAddress(add);
                hraDevice.setStationTel(tel);
                if (hraDeviceMapper.updateByPrimaryKey(hraDevice) > 0) {
                    //③当输入参数正确时，“code”:“210”， “msg”:“服务站设备修改成功。”，“data”:null。
                    return HraResult.build("210", "服务站设备修改成功。");
                } else {
                    //⑤当输入参数正确时，“code”:“510”， “msg”:“服务站设备修改失败。”，“data”:null。
                    log.error("新增或修改翼猫服务站信息->服务站设备修改失败：" + deviceId);
                    return HraResult.build("510", "服务站设备修改失败。");
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new Exception(e.getMessage(), e);
        }
    }

    /**
     * 接口编号：Interface_002
     * 接口名称：翼猫HRA服务器测试接口
     * 接口功能说明：测试翼猫HRA服务器提供的接口服务工作是否正常
     * 涉及功能编号：func_002、func_003
     * URL地址：http://hraapi.yimaokeji.com:10090/DeviceTest
     */
    @Override
    public HraResult deviceTest(String aesContent) throws Exception {
        try {
            String content = AESUtil.AESDncode(aesContent);

            // content = content.replaceAll("\r|\n|\t", "");
            log.info("翼猫HRA服务器测试接口->解密后的请求参数为：" + content);

            // HraRequest hraRequest = JsonUtil.jsonToPojo(content, HraRequest.class);
            HraRequest hraRequest = JSONObject.parseObject(content, HraRequest.class);
            if (hraRequest == null) {
                log.error("翼猫HRA服务器测试接口->输入参数错误：" + content);
                return HraResult.build("100", "输入参数错误。");
            }
            String deviceId = hraRequest.getDeviceId();

            //①当必须的输入参数缺失、不完整、校验错误时，“code”:“100”， “msg”:“输入参数错误。”，“data”:null。
            if (StringUtil.isEmpty(deviceId)) {
                log.error("翼猫HRA服务器测试接口->设备ID为空。" + content);
                return HraResult.build("100", "输入参数错误。设备ID不能为空。");
            }
            deviceId = this.formatDeviceId(deviceId);
            HraDevice hraDevice = this.getHraDeviceById(deviceId);
            //③当输入参数正确时，“code”:“404”， “msg”:“deviceId不存在，请联系翼猫总部。”，“data”:null。
            if (hraDevice == null) {
                log.error("翼猫HRA服务器测试接口->设备不存在，请联系翼猫总部：" + deviceId);
                return HraResult.build("404", deviceId + "设备不存在，请联系翼猫总部。");
            }
            //④当输入参数正确时，“code”:“400”， “msg”: “deviceid设备锁定，不允许使用，请联系翼猫总部。”，“data”:null。
            if (hraDevice.getDeviceStatus() == 2) {
                log.error("翼猫HRA服务器测试接口->设备被锁定，请联系翼猫总部开通：" + deviceId);
                return HraResult.build("400", deviceId + "设备被锁定，请联系翼猫总部开通。");
            }
            //②当输入参数正确时，“code”:“200”， “msg”: “deviceid设备允许使用。”，“data”:null。
            return HraResult.build("200", deviceId + "设备允许使用。");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new Exception(e.getMessage(), e);
        }
    }

    /**
     * 接口编号：Interface_003
     * 接口名称：通过评估卡号获取预约用户信息
     * 接口功能说明：通过评估卡号获取预约用户信息
     * 涉及功能编号：func_005
     * URL地址：http://hraapi.yimaokeji.com:10090/GetCustomerByTicketNo
     */
    @Override
    public HraResult getCustomerByTicketNo(String aesContent) throws Exception {
        try {
            log.info("通过评估卡号获取预约用户信息->密文为：" + aesContent);

            String content = AESUtil.AESDncode(aesContent);

            // content = content.replaceAll("\r|\n|\t", "");
            log.info("通过评估卡号获取预约用户信息->解密后的请求参数为：" + content);

            // HraRequest hraRequest = JsonUtil.jsonToPojo(content, HraRequest.class);
            HraRequest hraRequest = JSONObject.parseObject(content, HraRequest.class);
            if (hraRequest == null) {
                log.error("通过评估卡号获取预约用户信息->输入参数错误：" + content);
                return HraResult.build("100", "输入参数错误。");
            }
            String deviceId = hraRequest.getDeviceId();
            String ticketNo = hraRequest.getTicketNo();

            //①当输入参数缺失、不完整或错误时，“code”:“100”， “msg”:“输入参数错误。”，“data”:null。
            if (StringUtil.isEmpty(deviceId)) {
                log.error("通过评估卡号获取预约用户信息->设备ID为空。" + content);
                return HraResult.build("100", "输入参数错误。设备ID不能为空。");
            }
            if (StringUtil.isEmpty(ticketNo)) {
                log.error("通过评估卡号获取预约用户信息->体检卡号为空。" + content);
                return HraResult.build("100", "输入参数错误。体检卡号不能为空。");
            }
            deviceId = this.formatDeviceId(deviceId);
            ticketNo = ticketNo.toUpperCase();
            HraDevice hraDevice = this.getHraDeviceById(deviceId);
            //②当输入参数正确时，“code”:“404”， “msg”:“deviceId设备不存在，请联系翼猫总部。”，“data”:null。
            if (hraDevice == null) {
                log.error("通过评估卡号获取预约用户信息->设备不存在，请联系翼猫总部：" + deviceId);
                return HraResult.build("404", deviceId + "设备不存在，请联系翼猫总部。");
            }
            //③当输入参数正确时，“code”:“400”， “msg”:“deviceId设备被锁定，请联系翼猫总部开通。”，“data”:null。
            if (hraDevice.getDeviceStatus() == 2) {
                log.error("通过评估卡号获取预约用户信息->设备被锁定，请联系翼猫总部开通：" + deviceId);
                return HraResult.build("400", deviceId + "设备被锁定，请联系翼猫总部开通。");
            }
            //④当输入参数正确时，“code”:“405”， “msg”:“卡号不存在。”，“data”:null。
            // HraTicketDTO hraTicket = hraTicketService.findHraTicketByTicketNo(ticketNo);
            HraTicketDTO hraTicket = hraTicketMapper.findTicketByTicketNo(ticketNo);
            if (hraTicket == null) {
                log.error("通过评估卡号获取预约用户信息->卡号不存在：" + ticketNo);
                return HraResult.build("405", "卡号不存在。");
            }
            //⑤当输入参数正确时，“code”:“401”， “msg”:“卡号存在，但已被使用。”，“data”:{“userInfo”:“使用本卡的userInfo JSON数据”}。
            if (hraTicket.getTicketStatus() == 2) {
                HraCustomer hraCustomer = hraCustomerMapper.selectByPrimaryKey(hraTicket.getCustomerId());
                log.error("通过评估卡号获取预约用户信息->卡号存在，但已被使用：" + ticketNo);
                return HraResult.build("401", "卡号存在，但已被使用。", hraCustomer);
            }
            if (hraTicket.getHandselStatus() != null && hraTicket.getHandselStatus() == 1) {
                //赠送中的卡
                log.error("通过评估卡号获取预约用户信息->卡号存在，但处于赠送中，不能使用：" + ticketNo);
                return HraResult.build("402", "卡号存在，但处于赠送中，不能使用。");
            }
            if (hraTicket.getTicketStatus() == 3) {
                if (ticketNo.startsWith("M")) {
                    //未付款的优惠卡
                    log.error("通过评估卡号获取预约用户信息->该卡尚未付款，在【翼猫健康e家】公众号中支付后方可使用：" + ticketNo);
                    return HraResult.build("402", "该卡尚未付款，在【翼猫健康e家】公众号中支付后方可使用。");
                }
                //⑧当输入参数正确时，“code”:“402”， “msg”:“卡号存在，但已被禁用。”，“data”:null。
                log.error("通过评估卡号获取预约用户信息->卡号存在，但已被禁用：" + ticketNo);
                return HraResult.build("402", "卡号存在，但已被禁用。");
            }
            Date validEndTime = hraTicket.getValidEndTime();
            if (validEndTime != null && validEndTime.before(new Date()) || hraTicket.getTicketStatus() == 4) {
                //⑨当输入参数正确时，“code”:“403”， “msg”:“卡号存在，但已过期。”，“data”:null。
                log.error("通过评估卡号获取预约用户信息->卡号存在，但已过期：" + ticketNo);
                return HraResult.build("403", "卡号存在，但已过期。");
            }
            if (hraTicket.getTicketStatus() == 1) {
                //v1.5加入优惠卡使用限制
                if (ticketNo.startsWith("M")) {
                    String msg = hraTicketLimitService.verify(hraTicket.getUserId());
                    if (StringUtil.isNotEmpty(msg)) {
                        log.error("通过评估卡号获取预约用户信息->" + msg);
                        return HraResult.build("100", msg);
                    }
                }

                Integer useCount = hraTicket.getUseCount();
                BigDecimal price = hraTicket.getTicketPrice();

                Map<String, Object> map = new HashMap<>();
                map.put("deviceId", deviceId);
                map.put("ticketNo", ticketNo);
                map.put("stationId", hraDevice.getStationId());
                map.put("customerId", hraTicket.getCustomerId());
                map.put("step", 3);
                rabbitTemplate.convertAndSend(RabbitConstant.HRA_STEP, map);

                if (hraTicket.getCustomerId() == null) {
                    //⑥当输入参数正确时，“code”:“210”， “msg”:“卡号存在，且未绑定预约用户。”，“data”:null。
                    if (ticketNo.startsWith("F") && useCount != null) {
                        return HraResult.build("210", "卡号存在，还可使用" + useCount + "次。");
                    } else {
//                        return HraResult.build("210", "卡号存在，且未绑定预约用户。");
                        return HraResult.build("210", "此卡实价" + price + "元，未使用，且未绑定预约用户。");
                    }
                } else {
                    //⑦当输入参数正确时，“code”:“200”， “msg”:“卡号存在，但已绑定预约用户，且未使用。”，“data”:{“userInfo”:“预约用户的userInfo JSON数据”}。
                    if (ticketNo.startsWith("F") && useCount != null) {
                        return HraResult.build("200", "卡号存在，还可使用" + useCount + "次。");
                    } else {
                        HraCustomer hraCustomer = hraCustomerMapper.selectByPrimaryKey(hraTicket.getCustomerId());
                        return HraResult.build("200", "此卡实价" + price + "元，未使用，但已绑定预约用户。", hraCustomer);
                    }
                }
            }
            log.error("通过评估卡号获取预约用户信息->输入参数错误：" + content);
            return HraResult.build("100", "输入参数错误。");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new Exception(e.getMessage(), e);
        }
    }

    /**
     * 接口编号：Interface_004
     * 接口名称：评估卡号校验并绑定用户
     * 接口功能说明：评估卡号校验并绑定用户
     * 涉及功能编号：func_007
     * URL地址：http://hraapi.yimaokeji.com:10090/TicketBindCustomer
     */
    @Override
    public HraResult ticketBindCustomer(String aesContent) throws Exception {
        try {
            String content = AESUtil.AESDncode(aesContent);

            // content = content.replaceAll("\r|\n|\t", "");
            log.info("评估卡号校验并绑定用户->解密后的请求参数为：" + content);

            // HraRequest hraRequest = JsonUtil.jsonToPojo(content, HraRequest.class);
            HraRequest hraRequest = JSONObject.parseObject(content, HraRequest.class);
            if (hraRequest == null) {
                log.error("评估卡号校验并绑定用户->输入参数错误：" + content);
                return HraResult.build("100", "输入参数错误。");
            }
            String deviceId = hraRequest.getDeviceId();
            String ticketNo = hraRequest.getTicketNo();
            HraCustomerDTO hraCustomerDTO = hraRequest.getUserInfo();

            //①当输入参数缺失、不完整或错误时，“code”:“100”， “msg”:“输入参数错误。”，“data”:null。
            if (StringUtil.isEmpty(deviceId)) {
                log.error("评估卡号校验并绑定用户->设备ID为空。" + content);
                return HraResult.build("100", "输入参数错误。设备ID不能为空。");
            }
            if (StringUtil.isEmpty(ticketNo)) {
                log.error("评估卡号校验并绑定用户->体检卡号为空。" + content);
                return HraResult.build("100", "输入参数错误。体检卡号不能为空。");
            }
            if (hraCustomerDTO == null) {
                log.error("评估卡号校验并绑定用户->体检人信息为空。" + content);
                return HraResult.build("100", "输入参数错误。体检人信息不能为空。");
            }
            deviceId = this.formatDeviceId(deviceId);
            ticketNo = ticketNo.toUpperCase();
            HraDevice hraDevice = this.getHraDeviceById(deviceId);
            //②当输入参数正确时，“code”:“404”， “msg”:“deviceId设备不存在，请联系翼猫总部。”，“data”:null。
            if (hraDevice == null) {
                log.error("评估卡号校验并绑定用户->设备不存在，请联系翼猫总部：" + deviceId);
                return HraResult.build("404", deviceId + "设备不存在，请联系翼猫总部。");
            }
            //③当输入参数正确时，“code”:“400”， “msg”:“设备被锁定，请联系翼猫总部开通。”，“data”:null。
            if (hraDevice.getDeviceStatus() == 2) {
                log.error("评估卡号校验并绑定用户->设备被锁定，请联系翼猫总部开通：" + deviceId);
                return HraResult.build("400", deviceId + "设备被锁定，请联系翼猫总部开通。");
            }
            HraTicketDTO hraTicket = hraTicketMapper.findTicketByTicketNo(ticketNo);
            //④当输入参数正确时，“code”:“405”， “msg”:“卡号不存在。”，“data”:null。
            if (hraTicket == null) {
                log.error("评估卡号校验并绑定用户->卡号不存在：" + ticketNo);
                return HraResult.build("405", "卡号不存在。");
            }
            //⑤当输入参数正确时，“code”:“401”， “msg”:“卡号存在，但已被使用。”，“data”:{“userInfo”:“使用本卡的userInfo JSON数据”}。
            if (hraTicket.getTicketStatus() == 2) {
                HraCustomer customer = hraCustomerMapper.selectByPrimaryKey(hraTicket.getCustomerId());
                log.error("评估卡号校验并绑定用户->卡号存在，但已被使用：" + ticketNo + "，使用人信息：" + customer);
                return HraResult.build("401", "卡号存在，但已被使用。", customer);
            }
            if (hraTicket.getHandselStatus() != null && hraTicket.getHandselStatus() == 1) {
                //赠送中的卡
                log.error("评估卡号校验并绑定用户->卡号存在，但处于赠送中，不能使用：" + ticketNo);
                return HraResult.build("402", "卡号存在，但处于赠送中，不能使用。");
            }
            if (hraTicket.getTicketStatus() == 3) {
                if (ticketNo.startsWith("M")) {
                    //未付款的优惠卡
                    log.error("评估卡号校验并绑定用户->该卡尚未付款，在【翼猫健康e家】公众号中支付后方可使用：" + ticketNo);
                    return HraResult.build("402", "该卡尚未付款，在【翼猫健康e家】公众号中支付后方可使用。");
                }
                //⑦当输入参数正确时，“code”:“402”， “msg”:“卡号存在，但已被禁用。”，“data”:null。
                log.error("评估卡号校验并绑定用户->卡号存在，但已被禁用：" + ticketNo);
                return HraResult.build("402", "卡号存在，但已被禁用。");
            }
            //有效期校验
            Date validEndTime = hraTicket.getValidEndTime();
            if (validEndTime != null && validEndTime.before(new Date()) || hraTicket.getTicketStatus() == 4) {
                //⑧当输入参数正确时，“code”:“403”， “msg”:“卡号存在，但已过期。”，“data”:null。
                log.error("评估卡号校验并绑定用户->卡号存在，但已过期：" + ticketNo);
                return HraResult.build("403", "卡号存在，但已过期。");
            }
            //⑥当输入参数正确时，“code”:“200”， “msg”:“卡号存在，绑定用户成功。”，“data”:null。
            if (hraTicket.getTicketStatus() == 1) {

                //v1.5加入优惠卡使用限制
                if (ticketNo.startsWith("M")) {
                    String msg = hraTicketLimitService.verify(hraTicket.getUserId());
                    if (StringUtil.isNotEmpty(msg)) {
                        log.error("通过评估卡号获取预约用户信息->" + msg);
                        return HraResult.build("100", msg);
                    }
                }

                HraCustomer hraCustomer = new HraCustomer(hraCustomerDTO);
                int r = hraCustomerMapper.insert(hraCustomer);
                if (r > 0) {
                    HraTicket ticket = new HraTicket();
                    ticket.setId(hraTicket.getId());
                    ticket.setCustomerId(hraCustomer.getId());
                    hraTicketMapper.updateByPrimaryKeySelective(ticket);

                    Map<String, Object> map = new HashMap<>();
                    map.put("deviceId", deviceId);
                    map.put("ticketNo", ticketNo);
                    map.put("stationId", hraDevice.getStationId());
                    map.put("customerId", hraCustomer.getId());
                    map.put("step", 4);
                    rabbitTemplate.convertAndSend(RabbitConstant.HRA_STEP, map);

                    return HraResult.build("200", "卡号存在，绑定用户成功。", hraCustomer);
//                    int c = hraTicketApi.update(hraTicket);
//                    if (c > 0) {
//                        return HraResult.build("200", "卡号存在，绑定用户成功。", hraCustomer);
//                    }
                }
                log.error("评估卡号校验并绑定用户->：卡号存在，绑定用户失败。" + content);
            }
            log.error("评估卡号校验并绑定用户->输入参数错误：" + content);
            return HraResult.build("100", "输入参数错误。");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new Exception(e.getMessage(), e);
        }
    }

    /**
     * 接口编号：Interface_005
     * 接口名称：验证评估卡是否被使用
     * 接口功能说明：验证评估卡是否被使用
     * 涉及功能编号：func_006
     * URL地址：http://hraapi.yimaokeji.com:10090/TicketValidate
     */
    @Override
    public HraResult ticketValidate(String aesContent) throws Exception {
        try {
            log.info("验证评估卡是否被使用->密文为：" + aesContent);

            String content = AESUtil.AESDncode(aesContent);

            log.info("验证评估卡是否被使用->解密后的请求参数为（没去换行符）：" + content);
            // content = content.replaceAll("\r|\n|\t", "");
            // log.info("验证评估卡是否被使用->解密后的请求参数为（去除换行符）：" + content);

            // HraRequest hraRequest = JsonUtil.jsonToPojo(content, HraRequest.class);
            HraRequest hraRequest = JSONObject.parseObject(content, HraRequest.class);
            if (hraRequest == null) {
                log.error("验证评估卡是否被使用->输入参数错误：" + content);
                return HraResult.build("100", "输入参数错误。");
            }
            String deviceId = hraRequest.getDeviceId();
            String ticketNo = hraRequest.getTicketNo();

            //①当输入参数缺失、不完整或错误时，“code”:“100”， “msg”:“输入参数错误。”，“data”:null。
            if (StringUtil.isEmpty(deviceId)) {
                log.error("验证评估卡是否被使用->设备ID为空。" + content);
                return HraResult.build("100", "输入参数错误。设备ID不能为空。");
            }
            if (StringUtil.isEmpty(ticketNo)) {
                log.error("验证评估卡是否被使用->体检卡号为空。" + content);
                return HraResult.build("100", "输入参数错误。体检卡号不能为空。");
            }
            deviceId = this.formatDeviceId(deviceId);
            ticketNo = ticketNo.toUpperCase();
            HraDevice hraDevice = this.getHraDeviceById(deviceId);
            //②当输入参数正确时，“code”:“404”， “msg”:“deviceId设备不存在，请联系翼猫总部。”，“data”:null。
            if (hraDevice == null) {
                log.error("验证评估卡是否被使用->设备不存在，请联系翼猫总部：" + deviceId);
                return HraResult.build("404", deviceId + "设备不存在，请联系翼猫总部。");
            }
            //③当输入参数正确时，“code”:“400”， “msg”:“设备被锁定，请联系翼猫总部开通。”，“data”:null。
            if (hraDevice.getDeviceStatus() == 2) {
                log.error("验证评估卡是否被使用->设备被锁定，请联系翼猫总部开通：" + deviceId);
                return HraResult.build("400", deviceId + "设备被锁定，请联系翼猫总部开通。");
            }
            HraTicketDTO hraTicket = hraTicketMapper.findTicketByTicketNo(ticketNo);
            //④当输入参数正确时，“code”:“405”， “msg”:“卡号不存在。”，“data”:null。
            if (hraTicket == null) {
                log.error("验证评估卡是否被使用->卡号不存在：" + ticketNo);
                return HraResult.build("405", "卡号不存在。");
            }
            //是否只允许在本服务站使用校验
            if (hraTicket.getSelfStation() != null && hraTicket.getSelfStation()) {
                if (ticketNo.startsWith("F")) {//服务站免费卡
                    if (hraTicket.getStationId() != null && !Objects.equals(hraTicket.getStationId(), hraDevice.getStationId())) {
                        //该评估券只能在分配的服务站使用
                        log.error("验证评估卡是否被使用->该卡只能在" + hraTicket.getStationName() + "使用：" + content);
                        return HraResult.build("100", "该卡只能在" + hraTicket.getStationName() + "使用");
                    }
                }
            }

            //⑤当输入参数正确时，“code”:“401”， “msg”:“卡号存在，但已被使用。”，“data”:{“userInfo”:“使用本卡的userInfo JSON数据”}。
            if (hraTicket.getTicketStatus() == 2) {
                HraCustomer hraCustomer = hraCustomerMapper.selectByPrimaryKey(hraTicket.getCustomerId());
                log.error("验证评估卡是否被使用->卡号存在，但已被使用：" + ticketNo + "，使用人信息：" + hraCustomer);
                return HraResult.build("401", "卡号存在，但已被使用。", hraCustomer);
            }
            if (hraTicket.getHandselStatus() != null && hraTicket.getHandselStatus() == 1) {
                //赠送中的卡
                log.error("验证评估卡是否被使用->卡号存在，但处于赠送中，不能使用：" + ticketNo);
                return HraResult.build("402", "卡号存在，但处于赠送中，不能使用。");
            }
            if (hraTicket.getTicketStatus() == 3) {
                if (ticketNo.startsWith("M")) {
                    //未付款的优惠卡
                    log.error("验证评估卡是否被使用->该卡尚未付款，在【翼猫健康e家】公众号中支付后方可使用：" + ticketNo);
                    return HraResult.build("402", "该卡尚未付款，在【翼猫健康e家】公众号中支付后方可使用。");
                }
                //⑦当输入参数正确时，“code”:“402”， “msg”:“卡号存在，但已被禁用。”，“data”:null。
                log.error("验证评估卡是否被使用->卡号存在，但已被禁用：" + ticketNo);
                return HraResult.build("402", "卡号存在，但已被禁用。");
            }
            //有效期校验
            Date validEndTime = hraTicket.getValidEndTime();
            if (validEndTime != null && validEndTime.before(new Date()) || hraTicket.getTicketStatus() == 4) {
                //⑧当输入参数正确时，“code”:“403”， “msg”:“卡号存在，但已过期。”，“data”:null。
                log.error("验证评估卡是否被使用->卡号存在，但已过期：" + ticketNo);
                return HraResult.build("403", "卡号存在，但已过期。");
            }
            //⑥当输入参数正确时，“code”:“200”， “msg”:“卡号存在，且未使用。”，“data”:null}。
            Integer useCount = hraTicket.getUseCount();
            if (hraTicket.getTicketStatus() == 1) {

                //v1.5加入优惠卡使用限制
                if (ticketNo.startsWith("M")) {
                    String msg = hraTicketLimitService.verify(hraTicket.getUserId());
                    if (StringUtil.isNotEmpty(msg)) {
                        log.error("通过评估卡号获取预约用户信息->" + msg);
                        return HraResult.build("100", msg);
                    }
                }

                //校验体检卡没有被设置为已使用状态的情况
                if (!ticketNo.startsWith("F")) {
                    Example example = new Example(HraReport.class);
                    Example.Criteria criteria = example.createCriteria();
                    criteria.andEqualTo("deviceId", deviceId);
                    criteria.andEqualTo("ticketNo", ticketNo);
                    List<HraReport> reportList = hraReportMapper.selectByExample(example);
                    if (!CollectionUtils.isEmpty(reportList)) {
                        HraCustomer hraCustomer = null;
                        if (reportList.get(0).getCustomerId() != null) {
                            hraCustomer = hraCustomerMapper.selectByPrimaryKey(reportList.get(0).getCustomerId());
                        }
                        log.error("校验体检卡没有被设置为已使用状态的情况");
                        log.error("验证评估卡是否被使用->卡号存在，但已被使用：" + ticketNo + "，使用人信息：" + hraCustomer);
                        return HraResult.build("401", "卡号存在，但已被使用。", hraCustomer);
                    }
                }

                Map<String, Object> map = new HashMap<>();
                map.put("deviceId", deviceId);
                map.put("ticketNo", ticketNo);
                map.put("stationId", hraDevice.getStationId());
                map.put("customerId", hraTicket.getCustomerId());
                map.put("step", 5);
                rabbitTemplate.convertAndSend(RabbitConstant.HRA_STEP, map);

                if (ticketNo.startsWith("F") && useCount != null) {
                    return HraResult.build("200", "卡号存在，还可使用" + useCount + "次。");
                } else {
                    return HraResult.build("200", "卡号存在，且未使用。");
                }
            }
            log.error("验证评估卡是否被使用->输入参数错误：" + content);
            return HraResult.build("100", "输入参数错误。");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new Exception(e.getMessage(), e);
        }
    }

    /**
     * 接口编号：Interface_006
     * 接口名称：评估卡变更为已使用状态
     * 接口功能说明：评估卡变更为已使用状态
     * 涉及功能编号：func_008
     * URL地址：http://hraapi.yimaokeji.com:10090/TicketMarkupUsed
     */
    @Override
    public HraResult ticketMarkupUsed(String aesContent) throws Exception {
        try {
            String content = AESUtil.AESDncode(aesContent);

            // content = content.replaceAll("\r|\n|\t", "");
            log.info("评估卡变更为已使用状态->解密后的请求参数为：" + content);

            // HraRequest hraRequest = JsonUtil.jsonToPojo(content, HraRequest.class);
            HraRequest hraRequest = JSONObject.parseObject(content, HraRequest.class);
            if (hraRequest == null) {
                log.error("评估卡变更为已使用状态时，输入参数错误，" + content);
                return HraResult.build("100", "输入参数错误。");
            }
            String deviceId = hraRequest.getDeviceId();
            String ticketNo = hraRequest.getTicketNo();
//            HraCustomer hraCustomer = hraRequest.getUserInfo();
            //①当输入参数缺失、不完整或错误时，“code”:“100”， “msg”:“输入参数错误。”，“data”:null。
//            if (StringUtil.isEmpty(deviceId) || StringUtil.isEmpty(ticketNo) || hraCustomer == null) {
            if (StringUtil.isEmpty(deviceId)) {//2018-05-17调整用户信息在此步骤中为非必要信息
                log.error("评估卡变更为已使用状态时，设备ID为空。" + content);
                return HraResult.build("100", "输入参数错误。设备ID不能为空。");
            }
            if (StringUtil.isEmpty(ticketNo)) {//2018-05-17调整用户信息在此步骤中为非必要信息
                log.error("评估卡变更为已使用状态时，体检卡号为空。" + content);
                return HraResult.build("100", "输入参数错误。体检卡号不能为空。");
            }
            deviceId = this.formatDeviceId(deviceId);
            ticketNo = ticketNo.toUpperCase();
            HraTicketDTO hraTicket = hraTicketMapper.findTicketByTicketNo(ticketNo);
            //③当输入参数正确时，“code”:“500”， “msg”:“更新失败。”，“data”:null。
            if (hraTicket == null) {
                log.error("评估卡变更为已使用状态时，查询hraTicket失败，ticketNo=" + ticketNo);
                return HraResult.build("500", "更新失败。");
            }
            HraDevice hraDevice = this.getHraDeviceById(deviceId);
            if (hraDevice == null) {
                log.error("评估卡变更为已使用状态时，查询hraDevice失败，deviceId=" + deviceId);
                return HraResult.build("100", "输入参数错误。");
            }
            if (hraTicket.getTicketStatus() == 2) {
                return HraResult.build("200", "更新成功。");
            }

            HraTicket ticket = new HraTicket();
            //设置评估设备ID
            ticket.setDeviceId(hraDevice.getDeviceId());
            //评估完成设置评估服务站
            ticket.setStationId(hraDevice.getStationId());

            Integer useCount = hraTicket.getUseCount();
            if (ticketNo.startsWith("F")) {//F卡有使用次数递减
                if (useCount == null || useCount == 0 || useCount == 1) {//当可用次数是1次时，设置可用次数为0，设置评估券状态为已使用
                    ticket.setUseCount(0);
                    ticket.setTicketStatus(2);
                } else if (useCount > 1) {
                    //当可用次数是大于1次时，设置可用次数为现有次数减1，设置评估券状态为未使用（应该是可使用）
                    ticket.setUseCount(useCount - 1);
                    //hraTicket.setTicketStatus(1);//可使用
                }
            } else {//Y卡或M卡直接设置为已使用
                ticket.setUseCount(0);
                ticket.setTicketStatus(2);
            }
            //设置使用时间
            ticket.setUseTime(new Date());
            ticket.setId(hraTicket.getId());
            int r = hraTicketMapper.updateByPrimaryKeySelective(ticket);
            if (r <= 0) {
                //③当输入参数正确时，“code”:“500”， “msg”:“更新失败。”，“data”:null。
                log.error("评估卡变更为已使用状态时，更新hraTicket失败，ticketNo=" + ticketNo);
                return HraResult.build("500", "更新失败。");
            }

            //服务站评估收益分配
            //hraServiceApi.allot(deviceId, ticketNo, hraDevice.getStationId());
            Map<String, String> map = new HashMap<>();
            map.put("deviceId", deviceId);
            map.put("ticketNo", ticketNo);
            map.put("stationId", String.valueOf(hraDevice.getStationId()));
            if (ticketNo.startsWith("M")) {
                HraCard hraCard = hraCardMapper.selectByPrimaryKey(hraTicket.getCardId());
                if (hraCard != null) {
                    map.put("orderId", hraCard.getOrderId() + "");
                }
            }
            rabbitTemplate.convertAndSend(RabbitConstant.HRA_ALLOT, map);

            Map<String, Object> mmap = new HashMap<>();
            mmap.put("deviceId", deviceId);
            mmap.put("ticketNo", ticketNo);
            mmap.put("stationId", hraDevice.getStationId());
            mmap.put("customerId", hraTicket.getCustomerId());
            mmap.put("step", 6);
            rabbitTemplate.convertAndSend(RabbitConstant.HRA_STEP, mmap);

            //②当输入参数正确时，“code”:“200”， “msg”:“更新成功。”，“data”:null。
            return HraResult.build("200", "更新成功。");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new Exception(e.getMessage(), e);
        }
    }

    /**
     * 评估报告上传
     */
    @Override
    public HraResult reportUpload(String aesContent, Map<String, Part> partMap) throws Exception {
        try {
            String content = AESUtil.AESDncode(aesContent);

            // content = content.replaceAll("\r|\n|\t", "");
            log.info("评估报告上传->解密后的请求参数为：" + content);

            // HraRequest hraRequest = JsonUtil.jsonToPojo(content, HraRequest.class);
            HraRequest hraRequest = JSONObject.parseObject(content, HraRequest.class);
            if (hraRequest == null) {
                log.error("上传数据在进行JSON对象转换时出错");
                return HraResult.build("100", "输入参数错误。");
            }

            String deviceId = hraRequest.getDeviceId();
            String ticketNo = hraRequest.getTicketNo();
//            HraCustomer hraCustomer = hraRequest.getUserinfo();
            String json = hraRequest.getDetail();

            //①当输入参数缺失、不完整或错误时，“code”:“100”， “msg”:“输入参数错误。”，“data”:null。
            if (StringUtil.isEmpty(deviceId)) {
                log.error("设备ID为空");
                return HraResult.build("100", "输入参数错误。设备ID不能为空。");
            }
            if (StringUtil.isEmpty(ticketNo)) {
                log.error("体检卡号为空");
                return HraResult.build("100", "输入参数错误。体检卡号不能为空。");
            }
            if (StringUtil.isEmpty(json)) {
                log.error("json格式体检报告为空");
                return HraResult.build("100", "输入参数错误。json格式体检报告不能为空。");
            }
            deviceId = this.formatDeviceId(deviceId);
            ticketNo = ticketNo.toUpperCase();

            //对输入参数进行校验，修改日期2018-07-03
            HraDevice hraDevice = this.getHraDeviceById(deviceId);
            if (hraDevice == null) {
                log.error("评估报告上传，查询hraDevice失败，deviceId=" + deviceId);
                return HraResult.build("100", "输入的设备ID参数错误。");
            }
            HraTicketDTO hraTicket = hraTicketMapper.findTicketByTicketNo(ticketNo);
            if (hraTicket == null) {
                log.error("评估报告上传，查询hraTicket失败，ticketNo=" + ticketNo);
                return HraResult.build("100", "输入的卡号参数错误。");
            }

            HraReport report;
            Example example = new Example(HraReport.class);
            Example.Criteria criteria = example.createCriteria();
            criteria.andEqualTo("deviceId", deviceId);
            criteria.andEqualTo("ticketNo", ticketNo);
            List<HraReport> reportList = hraReportMapper.selectByExample(example);
            //F卡不保存体检报告
            if (ticketNo.startsWith("F")) {
                if (CollectionUtils.isEmpty(reportList)) {
                    report = new HraReport();
                } else {
                    report = reportList.get(0);
                }
            } else {
                if (CollectionUtils.isEmpty(reportList)) {
                    report = new HraReport();
                } else {
                    log.info("评估报告上传，改评估卡已经上传过体检报告了。ticketNo=" + ticketNo);
                    //return HraResult.ok();
                    report = reportList.get(0);
                }
            }
            report.setShowFlag(0);//默认用户未添加报告时，不显示在手机客户端【我的评估报告】页面上
            //保存评估报告信息
            report.setDeviceId(deviceId);
            report.setTicketNo(ticketNo);
            //JSON格式评估报告数据
            //report.setReportJson(json);
            //HraTicket hraTicket = hraTicketApi.findHraTicketByTicketNo(ticketNo);
            //if (hraTicket != null) {
            report.setCustomerId(hraTicket.getCustomerId());
            //}
            if (report.getId() == null) {
                report.setExamDate(new Date());
                hraReportMapper.insert(report);
            } else {
                report.setUpdateTime(new Date());
                hraReportMapper.updateByPrimaryKey(report);
            }
            HraReportJson reportJson = hraReportJsonMapper.selectByPrimaryKey(report.getId());
            if (reportJson == null) {
                reportJson = new HraReportJson();
                reportJson.setReportId(report.getId());
                reportJson.setReportJson(json);
                hraReportJsonMapper.insert(reportJson);
            } else {
                reportJson.setReportJson(json);
                hraReportJsonMapper.updateByPrimaryKey(reportJson);
            }
            try {
                Part pdf = partMap.get("pdf");
                Part pic_erbihout = partMap.get("pic_erbihout");
                Part pic_guge = partMap.get("pic_guge");
                Part pic_huxi = partMap.get("pic_huxi");
                Part pic_miniaoshengzhi = partMap.get("pic_miniaoshengzhi");
                Part pic_shenjing = partMap.get("pic_shenjing");
                Part pic_xiaohua = partMap.get("pic_xiaohua");
                Part pic_xinxueguan = partMap.get("pic_xinxueguan");
                String pdfUrl;
                if (pdf != null) {
                    // pdfUrl = FileUploadUtil.saveZipToLocal(pdf, "hraReport", AESUtil.ENCODE_RULES, deviceId, ticketNo);
                    pdfUrl = SFTPUtil.uploadZipFile(pdf.getInputStream(), "hraReport", pdf.getSubmittedFileName(), deviceId, ticketNo, AESUtil.ENCODE_RULES);
                    System.out.println(pdfUrl);
                    report.setReportPdf(pdfUrl);
                    report.setPic1(pdfUrl);
                } else {
                    log.error("上传数据pdf文件为空，ticketNo=" + ticketNo);
                    return HraResult.build("100", "输入参数错误。pdf文件为空。");
                }
                String erbihoutUrl;
                if (pic_erbihout != null) {
                    // erbihoutUrl = FileUploadUtil.saveZipToLocal(pic_erbihout, "hraReport", AESUtil.ENCODE_RULES, deviceId, ticketNo);
                    erbihoutUrl = SFTPUtil.uploadZipFile(pic_erbihout.getInputStream(), "hraReport", pic_erbihout.getSubmittedFileName(), deviceId, ticketNo, AESUtil.ENCODE_RULES);
                    System.out.println(erbihoutUrl);
                    report.setPic2(erbihoutUrl);
                }
                String gugeUrl;
                if (pic_guge != null) {
                    // gugeUrl = FileUploadUtil.saveZipToLocal(pic_guge, "hraReport", AESUtil.ENCODE_RULES, deviceId, ticketNo);
                    gugeUrl = SFTPUtil.uploadZipFile(pic_guge.getInputStream(), "hraReport", pic_guge.getSubmittedFileName(), deviceId, ticketNo, AESUtil.ENCODE_RULES);
                    System.out.println(gugeUrl);
                    report.setPic3(gugeUrl);
                }
                String huxiUrl;
                if (pic_huxi != null) {
                    // huxiUrl = FileUploadUtil.saveZipToLocal(pic_huxi, "hraReport", AESUtil.ENCODE_RULES, deviceId, ticketNo);
                    huxiUrl = SFTPUtil.uploadZipFile(pic_huxi.getInputStream(), "hraReport", pic_huxi.getSubmittedFileName(), deviceId, ticketNo, AESUtil.ENCODE_RULES);
                    System.out.println(huxiUrl);
                    report.setPic4(huxiUrl);
                }
                String miniaoshengzhiUrl;
                if (pic_miniaoshengzhi != null) {
                    // miniaoshengzhiUrl = FileUploadUtil.saveZipToLocal(pic_miniaoshengzhi, "hraReport", AESUtil.ENCODE_RULES, deviceId, ticketNo);
                    miniaoshengzhiUrl = SFTPUtil.uploadZipFile(pic_miniaoshengzhi.getInputStream(), "hraReport", pic_miniaoshengzhi.getSubmittedFileName(), deviceId, ticketNo, AESUtil.ENCODE_RULES);
                    System.out.println(miniaoshengzhiUrl);
                    report.setPic5(miniaoshengzhiUrl);
                }
                String shenjingUrl;
                if (pic_shenjing != null) {
                    // shenjingUrl = FileUploadUtil.saveZipToLocal(pic_shenjing, "hraReport", AESUtil.ENCODE_RULES, deviceId, ticketNo);
                    shenjingUrl = SFTPUtil.uploadZipFile(pic_shenjing.getInputStream(), "hraReport", pic_shenjing.getSubmittedFileName(), deviceId, ticketNo, AESUtil.ENCODE_RULES);
                    System.out.println(shenjingUrl);
                    report.setPic6(shenjingUrl);
                }
                String xiaohuaUrl;
                if (pic_xiaohua != null) {
                    // xiaohuaUrl = FileUploadUtil.saveZipToLocal(pic_xiaohua, "hraReport", AESUtil.ENCODE_RULES, deviceId, ticketNo);
                    xiaohuaUrl = SFTPUtil.uploadZipFile(pic_xiaohua.getInputStream(), "hraReport", pic_xiaohua.getSubmittedFileName(), deviceId, ticketNo, AESUtil.ENCODE_RULES);
                    System.out.println(xiaohuaUrl);
                    report.setPic7(xiaohuaUrl);
                }
                String xinxueguanUrl;
                if (pic_xinxueguan != null) {
                    // xinxueguanUrl = FileUploadUtil.saveZipToLocal(pic_xinxueguan, "hraReport", AESUtil.ENCODE_RULES, deviceId, ticketNo);
                    xinxueguanUrl = SFTPUtil.uploadZipFile(pic_xinxueguan.getInputStream(), "hraReport", pic_xinxueguan.getSubmittedFileName(), deviceId, ticketNo, AESUtil.ENCODE_RULES);
                    System.out.println(xinxueguanUrl);
                    report.setPic8(xinxueguanUrl);
                }
                hraReportMapper.updateByPrimaryKey(report);

                Map<String, Object> map = new HashMap<>();
                map.put("deviceId", deviceId);
                map.put("ticketNo", ticketNo);
//                map.put("stationId", hraTicket != null ? hraTicket.getStationId() : null);
                map.put("stationId", hraTicket.getStationId());
                map.put("customerId", report.getCustomerId());
                map.put("step", 7);
                rabbitTemplate.convertAndSend(RabbitConstant.HRA_STEP, map);

                return HraResult.ok();
            } catch (ZipException e) {
                log.error("=============上传的压缩文件不合法===========ticketNo=" + ticketNo);
                return HraResult.build("100", "输入参数错误。");
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new Exception(e.getMessage(), e);
        }
    }

    /**
     * 去掉设备ID中间的"-"字符，同意设备ID格式
     *
     * @param deviceId 设备编号
     * @return
     */
    private String formatDeviceId(String deviceId) {
        if (deviceId.contains("-")) {
            deviceId = deviceId.replace("-", "");
        }
        return deviceId.toUpperCase();
    }

    /**
     * 根据评估设备编号查询评估设备信息
     *
     * @param deviceId 设备编号
     * @return 评估设备信息
     */
    private HraDevice getHraDeviceById(String deviceId) {
        Example example = new Example(HraDevice.class);
        example.createCriteria().andEqualTo("deviceId", deviceId);
        example.setOrderByClause("create_time desc");
        List<HraDevice> list = hraDeviceMapper.selectByExample(example);
        if (CollectionUtils.isEmpty(list)) {
            return null;
        }
        return list.get(0);
    }

}
