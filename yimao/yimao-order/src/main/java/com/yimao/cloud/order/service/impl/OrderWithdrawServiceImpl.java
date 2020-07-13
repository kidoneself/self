package com.yimao.cloud.order.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.yimao.cloud.base.constant.Constant;
import com.yimao.cloud.base.constant.RabbitConstant;
import com.yimao.cloud.base.constant.WechatConstant;
import com.yimao.cloud.base.enums.IncomeType;
import com.yimao.cloud.base.enums.WithdrawFlag;
import com.yimao.cloud.base.enums.WithdrawStatus;
import com.yimao.cloud.base.exception.YimaoException;
import com.yimao.cloud.base.mail.MailSender;
import com.yimao.cloud.base.properties.DomainProperties;
import com.yimao.cloud.base.properties.WechatProperties;
import com.yimao.cloud.base.utils.CollectionUtil;
import com.yimao.cloud.base.utils.DateUtil;
import com.yimao.cloud.base.utils.StringUtil;
import com.yimao.cloud.base.utils.UUIDUtil;
import com.yimao.cloud.base.utils.WXPayUtil;
import com.yimao.cloud.order.feign.ProductFeign;
import com.yimao.cloud.order.feign.UserFeign;
import com.yimao.cloud.order.mapper.OrderWithdrawMapper;
import com.yimao.cloud.order.mapper.ProductIncomeRecordMapper;
import com.yimao.cloud.order.mapper.ProductIncomeRecordPartMapper;
import com.yimao.cloud.order.mapper.WithdrawSubMapper;
import com.yimao.cloud.order.po.ProductIncomeRecord;
import com.yimao.cloud.order.po.ProductIncomeRecordPart;
import com.yimao.cloud.order.po.WithdrawMain;
import com.yimao.cloud.order.po.WithdrawSub;
import com.yimao.cloud.order.service.OrderWithdrawService;
import com.yimao.cloud.pojo.dto.order.OrderWithdrawDTO;
import com.yimao.cloud.pojo.dto.order.ProductIncomeRecordPartDTO;
import com.yimao.cloud.pojo.dto.order.WithdrawExportDTO;
import com.yimao.cloud.pojo.dto.order.WithdrawQueryDTO;
import com.yimao.cloud.pojo.dto.order.WithdrawSubDTO;
import com.yimao.cloud.pojo.dto.product.ProductCompanyDTO;
import com.yimao.cloud.pojo.dto.user.UserDTO;
import com.yimao.cloud.pojo.vo.PageVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.DefaultHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.EntityUtils;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import javax.net.ssl.SSLContext;
import java.io.File;
import java.io.FileInputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * @author Zhang Bo
 * @date 2018/11/12.
 */
@Slf4j
@Service
public class OrderWithdrawServiceImpl implements OrderWithdrawService {

    @Resource
    private DomainProperties domainProperties;
    @Resource
    private WechatProperties wechatProperties;
    @Resource
    private OrderWithdrawMapper orderWithdrawMapper;
    @Resource
    private WithdrawSubMapper withdrawSubMapper;
    @Resource
    private ProductIncomeRecordPartMapper productIncomeRecordPartMapper;
    @Resource
    private MailSender mailSender;
    @Resource
    private RabbitTemplate rabbitTemplate;
    @Resource
    private ProductIncomeRecordMapper productIncomeRecordMapper;
    @Resource
    private ProductFeign productFeign;
    @Resource
    private UserFeign userFeign;

    /**
     * 应体现订单
     *
     * @param pageNum   分页页数
     * @param pageSize  分页大小
     * @param orderId   订单号
     * @param userId    用户Id
     * @param phone     用户手机号
     * @param startTime 订单完成开始时间
     * @param endTime   订单完成结束时间
     * @return com.yimao.cloud.pojo.vo.PageVO<com.yimao.cloud.pojo.dto.order.OrderWithdrawDTO>
     * @author hhf
     * @date 2019/2/23
     */
    @Override
    public PageVO<OrderWithdrawDTO> withdrawList(Integer pageNum, Integer pageSize, String orderId, Integer userId, String phone, String startTime, Integer incomeType, Integer status, String endTime) {
        PageHelper.startPage(pageNum, pageSize);
        Page<OrderWithdrawDTO> page = orderWithdrawMapper.withdrawList(orderId, userId, phone, startTime, incomeType, status, endTime);
        return new PageVO<>(pageNum, page);
    }

    /**
     * 提现审核列表
     *
     * @param pageNum        分页页数
     * @param pageSize       分页大小
     * @param partnerTradeNo 提现订单号
     * @param userId         用户Id
     * @param phone          用户手机号
     * @param startTime      申请提现开始时间
     * @param endTime        申请提现结束时间
     * @return com.yimao.cloud.pojo.vo.PageVO<com.yimao.cloud.pojo.dto.order.WithdrawSubDTO>
     * @author hhf
     * @date 2019/3/1
     */
    @Override
    public PageVO<WithdrawSubDTO> withdrawAuditList(Integer pageNum, Integer pageSize, Long partnerTradeNo, Integer userId, Integer incomeType, String phone, String startTime, String endTime) {
        PageHelper.startPage(pageNum, pageSize);
        Page<WithdrawSubDTO> page = orderWithdrawMapper.withdrawAuditList(partnerTradeNo, userId, phone, startTime, incomeType, endTime);
        return new PageVO<>(pageNum, page);
    }

    /**
     * 提现审核不通过
     *
     * @param ids         体现审核主键
     * @param updater     操作人
     * @param auditReason 审核不通过原因
     * @param ip
     * @return void
     * @author hhf
     * @date 2019/3/1
     */
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void auditNotPassed(String updater, List<Long> tradeNos, String auditReason, String ip) {
        // 审核不通过
        Example example = new Example(ProductIncomeRecordPart.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("auditStatus", WithdrawStatus.WAIT_REVIEW.value);
        criteria.andIn("partnerTradeNo", tradeNos);
        List<ProductIncomeRecordPart> parts = productIncomeRecordPartMapper.selectByExample(example);
        if (CollectionUtil.isNotEmpty(parts)) {
            int i = withdrawSubMapper.updateCashRecordFlagAndStatus(tradeNos, WithdrawFlag.FAIL.value, WithdrawStatus.NOT_PASS.value,
                    null, new Date(), WithdrawStatus.WAIT_REVIEW.value, auditReason, updater);
            if (i <= 0) {
                throw new YimaoException("操作失败，更新提现记录提现审核状态为【审核失败】时遇到错误。");
            }

            //3-给收益记录设置需要更新的属性
            ProductIncomeRecordPart partUpdate = new ProductIncomeRecordPart();
            partUpdate.setAuditStatus(WithdrawStatus.NOT_PASS.value);
            i = productIncomeRecordPartMapper.updateByExampleSelective(partUpdate, example);
            if (i <= 0) {
                throw new YimaoException("操作失败，更新收益记录提现审核状态为【审核失败】时遇到错误。");
            }
            try {
                Map<String, Object> map = new HashMap<>(8);
                map.put("partnerTradeNos", tradeNos);
                map.put("operator", updater);
                map.put("type", 2);
                map.put("ip", ip);
                map.put("content", "审核不通过");
                map.put("reason", auditReason);
                rabbitTemplate.convertAndSend(RabbitConstant.WITHDRAW_AUDIT, map);
            } catch (AmqpException e) {
                e.printStackTrace();
                log.error(e.getMessage(), e);
            }
        }
    }

    /**
     * 提现审核（批量）
     *
     * @param updater     操作人
     * @param ids         体现审核主键
     * @param auditStatus 提现状态
     * @param auditReason 审核不通过原因
     * @return void
     * @author hhf
     * @date 2019/3/1
     */
    @Override
    public String batchAudit(String updater, List<Long> ids, Integer auditStatus, String auditReason, String ip) {
        String message = "";
        if (auditStatus == 2) {
            // 审核不通过
            this.auditNotPassed(updater, ids, auditReason, ip);
            message = "操作成功";
        } else {
            message += this.auditPassed(updater, ids, ip);
        }
        return message;
    }

    /**
     * 提现审核通过
     *
     * @param updater 操作人
     * @param ids     体现审核主键
     * @return void
     * @author hhf
     * @date 2019/3/1
     */
    public String auditPassed(String updater, List<Long> ids, String ip) {
        log.info("===============================审核通过，执行提现逻辑================================================");
        //返回给前台的
        String message = "";
        // 打印错误信息
        String errorMessage = "";
        int errorCount = 0;
        int total = ids.size();
        String returnMessage = "提现成功";
        for (int i = 0; i < total; i++) {
            Integer x;
            try {
                x = withdraw(ids.get(i), updater, ip);
            } catch (Exception e) {
                throw new YimaoException("OrderWithdrawServiceImpl.withdraw error", e);
            }
            if (x != 1) {
                ++errorCount;
                if (x == 2) {
                    errorMessage = "获取收益/提现记录失败";
                } else if (x == 3) {
                    errorMessage = "账户余额不足";
                } else {
                    errorMessage = "其他原因";
                }

                //只记录第一次错误，返给用户
                if (StringUtil.isEmpty(message) && StringUtil.isNotEmpty(errorMessage)) {
                    message = errorMessage;
                }
                log.info("===============================第" + (i + 1) + "个提现失败======" + errorMessage + "==========================================");
                log.info("提现失败：提现单号为：" + ids.get(i));
                log.info("===============================提现失败================================================");
            }
        }

        if (total == 1) {
            if (errorCount == 1) {
                returnMessage = "提现失败，原因：" + message;
            }
        } else {
            if (errorCount != 0 && errorCount != total) {
                returnMessage = "本次共提现为:" + total + "笔,提现成功" + (total - errorCount) + "笔,提现失败" + errorCount + "笔，原因：" + message;
            } else if (errorCount == total) {
                returnMessage = "提现失败，原因：" + message;
            }
        }
        return returnMessage;

    }

    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public Integer withdraw(Long partnerTradeNo, String operator, String ip) throws Exception {
        Example example = new Example(ProductIncomeRecordPart.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("partnerTradeNo", partnerTradeNo);
        List<ProductIncomeRecordPart> parts = productIncomeRecordPartMapper.selectByExample(example);
        if (CollectionUtil.isEmpty(parts)) {
            return 2;
        }
        for (ProductIncomeRecordPart part : parts) {
            ProductIncomeRecordPart update = new ProductIncomeRecordPart();
            update.setId(part.getId());
            update.setAuditStatus(WithdrawStatus.PASS.value);
            update.setHasWithdraw(1);
            productIncomeRecordPartMapper.updateByPrimaryKeySelective(update);
        }

        WithdrawSub withdrawSub = withdrawSubMapper.selectByPrimaryKey(partnerTradeNo);
        if (withdrawSub == null) {
            return 2;
        }
        //审核时间和提现时间
        Date date = new Date();
        withdrawSub.setWithdrawTime(date);
        withdrawSub.setAuditTime(date);
        withdrawSub.setWithdrawFlag(WithdrawFlag.SUCCESS.value);
        withdrawSub.setStatus(WithdrawStatus.PASS.value);
        withdrawSub.setUpdater(operator);
        withdrawSub.setUpdateTime(date);
        withdrawSubMapper.updateByPrimaryKey(withdrawSub);

        SortedMap<String, Object> params = new TreeMap<>();
        // 公众账号ID
        params.put("mch_appid", wechatProperties.getJsapi().getAppid());
        // 商户号
        params.put("mchid", wechatProperties.getJsapi().getMchId());
        // 随机字符串
        params.put("nonce_str", UUIDUtil.longuuid32());
        // 商户订单号 商户系统内部的订单号,32个字符内、可包含字母
        params.put("partner_trade_no", withdrawSub.getId());
        // openid
        params.put("openid", withdrawSub.getOpenid());
        // 订单总金额，单位为分，只能为整数
        params.put("amount", withdrawSub.getWithdrawFee().multiply(new BigDecimal(100)).intValue() + "");
        // 检验用户姓名
        params.put("check_name", "NO_CHECK");
        // 企业付款描述信息
        params.put("desc", "微信提现");
        // Ip地址
        params.put("spbill_create_ip", "127.0.0.1");
        // 签名
        params.put("sign", WXPayUtil.createSign(params, true, WechatConstant.MD5, wechatProperties.getJsapi().getKey()));
        // url
        String url = "https://api.mch.weixin.qq.com/mmpaymkttransfers/promotion/transfers";

        HttpPost httpPost = new HttpPost(url);
        String param = WXPayUtil.mapToXml(params);
        if (StringUtil.isNotEmpty(param)) {
            httpPost.setEntity(new StringEntity(param, "UTF-8"));
        }

        // 指定读取证书格式为PKCS12
        KeyStore keyStore = KeyStore.getInstance("PKCS12");
        // 读取本机存放的PKCS12证书文件
        FileInputStream instream = new FileInputStream(new File("/usr/wxcert/" + wechatProperties.getJsapi().getMchId() + ".p12"));
//        FileInputStream instream = new FileInputStream(new File("D:\\utils\\" + WxConstant.JSAPI_MCH_ID + ".p12"));
        try {
            // 指定PKCS12的密码(商户ID)
            keyStore.load(instream, wechatProperties.getJsapi().getMchId().toCharArray());
        } finally {
            instream.close();
        }
        SSLContext sslcontext = SSLContexts
                .custom()
                .loadKeyMaterial(keyStore, wechatProperties.getJsapi().getMchId().toCharArray())
                .build();
        // 指定TLS版本
        SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslcontext, new String[]{"TLSv1"}, null, new DefaultHostnameVerifier());
        // 设置httpclient的SSLSocketFactory
        CloseableHttpClient httpClient = HttpClients.custom().setSSLSocketFactory(sslsf).build();
        HttpResponse response = httpClient.execute(httpPost);
        HttpEntity entity = response.getEntity();
        String result = EntityUtils.toString(entity, "UTF-8");
        Map<String, String> map = WXPayUtil.xmlToMap(result);
        boolean flag = "SUCCESS".equals(map.get("return_code")) && "SUCCESS".equals(map.get("result_code"));
        log.info("==============================================================================================================================");
        log.info("微信提现返回结果：" + result);
        log.info("==============================================================================================================================");

        String operationMessage = "审核通过[失败]";
        try {
            if (!flag) {
                // 修改提现记录表的数据
                withdrawSub.setStatus(WithdrawStatus.WAIT_REVIEW.value);
                withdrawSub.setWithdrawFlag(null);
                withdrawSub.setAuditTime(null);

                final List<String> errList = new ArrayList<>();
                for (ProductIncomeRecordPart part : parts) {
                    //提现失败，恢复数据状态
                    ProductIncomeRecordPart update = new ProductIncomeRecordPart();
                    update.setId(part.getId());
                    update.setAuditStatus(WithdrawStatus.WAIT_REVIEW.value);
                    update.setHasWithdraw(0);
                    productIncomeRecordPartMapper.updateByPrimaryKeySelective(update);

                    ProductIncomeRecord record = productIncomeRecordMapper.selectByPrimaryKey(part.getRecordId());
                    String orderId = null;
                    if (Objects.nonNull(record)) {
                        orderId = record.getOrderId();
                    }
                    errList.add("收益表主键 : " + part.getId() + " --> 订单号 : " + orderId + "\n");
                }
                try {
                    withdrawSubMapper.updateByPrimaryKey(withdrawSub);
                } catch (Exception e) {
                    log.error(errList.toString() + " : 提现失败的时候,恢复收益数据状态失败。");
                    StringWriter sw = new StringWriter();
                    e.printStackTrace(new PrintWriter(sw, true));
                    String subject = "提现失败,恢复数据状态失败" + domainProperties.getApi();
                    String content = "提现失败,以下订单状态可能修改失败\n" + errList.toString() + "\n 开始出问题的提现单号 : " + partnerTradeNo + "\n" + sw.toString();
                    mailSender.send(null, subject, content);
                    // 更新数据
                    // 若更新数据失败,会抛出异常,事务回滚
                    throw new YimaoException("提现失败,更新数据(调用微信提现接口之后)时出错");
                }

                String err_code_des = map.get("err_code_des");
                if (err_code_des != null && "余额不足".equals(err_code_des.trim())) {
                    log.info("提现失败：余额不足");
                    operationMessage = "审核通过[余额不足]！";
                    return 3;
                }
                return 4;
            } else {
                operationMessage = "审核通过[成功]！";
                try {
                    withdrawSub.setPaymentNo(map.get("payment_no"));
                    withdrawSub.setPaymentTime(DateUtil.transferStringToDate(map.get("payment_time"), DateUtil.defaultDateTimeFormat));
                    withdrawSubMapper.updateByPrimaryKeySelective(withdrawSub);
                } catch (Exception e) {
                    log.error("微信提现成功，更新提现记录失败");
                    StringWriter sw = new StringWriter();
                    e.printStackTrace(new PrintWriter(sw, true));
                    String subject = "提现成功,更新提现流水号失败" + domainProperties.getApi();
                    String content = "提现成功,更新提现流水号失败,流水号为：" + map.get("payment_no") + "\n 开始出问题的提现单号 : " + partnerTradeNo + "\n" + sw.toString();
                    mailSender.send(null, subject, content);
                }
            }
            return 1;
        } catch (Exception e) {
            log.error("微信提现，记录日志失败");
            log.error(e.getMessage(), e);
            return 4;
        } finally {
            try {
                Map<String, Object> content = new HashMap<>(8);
                content.put("tradeNo", partnerTradeNo);
                content.put("type", 2);
                content.put("content", operationMessage);
                content.put("ip", ip);
                content.put("operator", operator);
                rabbitTemplate.convertAndSend(RabbitConstant.WITHDRAW_AUDIT, content);
            } catch (AmqpException e) {
                log.error("微信提现，记录日志失败");
                log.error(e.getMessage(), e);
            }
        }
    }

    /**
     * 提现记录列表
     *
     * @param pageNum          分页页数
     * @param pageSize         分页大小
     * @param withdrawQueryDTO 查询信息
     * @return com.yimao.cloud.pojo.vo.PageVO<com.yimao.cloud.pojo.dto.order.WithdrawSubDTO>
     * @author hhf
     * @date 2019/3/4
     */
    @Override
    public PageVO<WithdrawSubDTO> withdrawRecordList(Integer pageNum, Integer pageSize, WithdrawQueryDTO withdrawQueryDTO) {
        PageHelper.startPage(pageNum, pageSize);
        Page<WithdrawSubDTO> page = orderWithdrawMapper.withdrawRecordList(withdrawQueryDTO);
        return new PageVO<>(pageNum, page);
    }

    /**
     * 提现明细列表
     *
     * @param pageNum          分页页数
     * @param pageSize         分页大小
     * @param withdrawQueryDTO 查询信息
     * @return com.yimao.cloud.pojo.vo.PageVO<com.yimao.cloud.pojo.dto.order.WithdrawSubDTO>
     * @author hhf
     * @date 2019/3/4
     */
    @Override
    public PageVO<WithdrawSubDTO> withdrawRecordDetailList(Integer pageNum, Integer pageSize, WithdrawQueryDTO withdrawQueryDTO) {
        PageHelper.startPage(pageNum, pageSize);
        Page<WithdrawSubDTO> page = orderWithdrawMapper.withdrawRecordDetailList(withdrawQueryDTO);
        return new PageVO<>(pageNum, page);
    }

    /**
     * 提现操作日志
     *
     * @param pageNum        分页页数
     * @param pageSize       分页大小
     * @param partnerTradeNo 自提现单号
     * @param withdrawFlag   提现成功与否
     * @param startTime      操作开始时间
     * @param endTime        操作结束时间
     * @return com.yimao.cloud.pojo.vo.PageVO<com.yimao.cloud.pojo.dto.order.WithdrawSubDTO>
     * @author hhf
     * @date 2019/3/4
     */
    @Override
    public PageVO<WithdrawSubDTO> withdrawRecordLogList(Integer pageNum, Integer pageSize, Long partnerTradeNo, Integer withdrawFlag, String startTime, String endTime) {
        PageHelper.startPage(pageNum, pageSize);
        Page<WithdrawSubDTO> page = orderWithdrawMapper.withdrawRecordLogList(partnerTradeNo, withdrawFlag, startTime, endTime);
        return new PageVO<>(pageNum, page);
    }

    /**
     * 申请提现接口,检测是否满足提现要求
     *
     * @param userId 用户信息
     * @return java.util.Map<java.lang.String, java.lang.Object>
     * @author hhf
     * @date 2019/4/9
     */
    @Override
    public Map<String, Object> checkCashCondition(Integer userId, Integer incomeType) {

        Map<String, Object> result = new HashMap<>(16);

        // 1 判断用户当日提现次数是否超出可提现次数
        int withdrawCount = orderWithdrawMapper.getWithdrawnCount(userId);
        if (withdrawCount >= Constant.WITHDRAW_CASH_COUNT) {
            throw new YimaoException("每日提现次数不能超过三次");
        }
        // 2 获取用户的当前可提现收益
        List<ProductIncomeRecordPartDTO> list = productIncomeRecordPartMapper.getUserSaleWithdrawIncome(userId, 0, null, incomeType, null);
        if (CollectionUtil.isEmpty(list)) {
            throw new YimaoException("当前没有可提现收益");
        }
        // 3 获取用户单日已提现金额
        BigDecimal withdrawnAmountToday = withdrawSubMapper.getWithdrawnAmountToday(userId);
        if (withdrawnAmountToday == null) {
            withdrawnAmountToday = BigDecimal.ZERO;
        }
        // 获取最近要提现的一笔金额
        BigDecimal tmpMoney = list.get(0).getSubjectMoney().add(withdrawnAmountToday);
        if (tmpMoney.compareTo(new BigDecimal(Constant.MAX_CASH_DAY)) > 0) {
            throw new YimaoException("您今日已超出提现额度,可明日再来");
        }
        List<ProductIncomeRecordPart> tmpList = new ArrayList<>();
        StringBuffer buffer = new StringBuffer();
        // 遍历提现订单记录
        BigDecimal totalIncome = new BigDecimal("0");
        Map<Integer, Long> ptnMap = new HashMap<>(8);
        // 遍历之前按金额排序
        list.sort((m, n) -> m.getSubjectMoney().compareTo(n.getSubjectMoney()) > 0 ? -1 : 1);
        for (ProductIncomeRecordPartDTO dto : list) {
            Integer id = dto.getProductCompanyId();
            Long partnerTradeNo;
            if (ptnMap.containsKey(id)) {
                partnerTradeNo = ptnMap.get(id);
            } else {
                //把所有子提现单号，发送给前台
                partnerTradeNo = UUIDUtil.buildOrderId(2);
                ptnMap.put(id, partnerTradeNo);
                buffer.append(partnerTradeNo).append(",");
            }
            BigDecimal tmp = dto.getSubjectMoney();
            withdrawnAmountToday = withdrawnAmountToday.add(tmp);
            if (withdrawnAmountToday.compareTo(new BigDecimal(Constant.MAX_CASH_DAY)) > 0) {
                break;
            }
            totalIncome = totalIncome.add(tmp);
            if (totalIncome.compareTo(new BigDecimal(Constant.MAX_CASH_DAY)) <= 0) {
                ProductIncomeRecordPart productIncomeRecordPart = new ProductIncomeRecordPart();
                productIncomeRecordPart.setId(dto.getId());
                productIncomeRecordPart.setAuditStatus(WithdrawStatus.CHECK_FAIL.value);
                productIncomeRecordPart.setPartnerTradeNo(partnerTradeNo);
                tmpList.add(productIncomeRecordPart);
            } else {
                break;
            }
        }
        if (totalIncome.compareTo(new BigDecimal(Constant.MIN_CASH_DAY)) < 0) {
            throw new YimaoException("单次提现金额不可低于1元");
        }
        // 更新提现记录
        tmpList.forEach(o -> productIncomeRecordPartMapper.updateByPrimaryKeySelective(o));

        result.put("result_code", 1);
        result.put("partnerTradeNo", buffer.toString());
        result.put("amount", totalIncome);
        return result;
    }

    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    @Override
    public Boolean insertCashRecord(String partnerTradeNo, BigDecimal amount, Integer userId, Integer incomeType) {
        UserDTO user = userFeign.getUserById(userId);
        List<String> partnerTradeNoList = StringUtil.spiltListByStr(partnerTradeNo);
        List<ProductIncomeRecordPartDTO> income = productIncomeRecordPartMapper.getUserSaleWithdrawIncome(userId, 0, WithdrawStatus.CHECK_FAIL.value, incomeType, partnerTradeNoList);
        //用于记录本次提现的总金额
        BigDecimal totalIncome = new BigDecimal("0");
        //用于保存各个产品类型本次提现金额
        Map<Integer, BigDecimal> incomeMap = new HashMap<>(8);
        //用于保存各个产品类型对应的提现单号
        Map<Integer, Long> partnerNoMap = new HashMap<>(8);
        //用于保存要修改状态的收益条目
        List<ProductIncomeRecordPart> tmpList = new ArrayList<>();
        for (ProductIncomeRecordPartDTO dto : income) {
            BigDecimal tmp = dto.getSubjectMoney();
            Integer id = dto.getProductCompanyId();
            if (incomeMap.containsKey(id)) {
                BigDecimal totalAmount = incomeMap.get(id);
                totalAmount = totalAmount.add(tmp);
                incomeMap.put(id, totalAmount);
            } else {
                incomeMap.put(id, tmp);
            }
            partnerNoMap.put(id, dto.getPartnerTradeNo());
            totalIncome = totalIncome.add(tmp);
            ProductIncomeRecordPart part = new ProductIncomeRecordPart();
            part.setId(dto.getId());
            part.setAuditStatus(WithdrawStatus.WAIT_REVIEW.value);
            tmpList.add(part);
        }

        //判断传递过来的金额与数据查询出的金额是否一致, 确保数据的准确性
        BigDecimal amountTmp = new BigDecimal(amount + "");
        if (totalIncome.compareTo(amountTmp) != 0) {
            return false;
        }

        //生成主提现单号
        WithdrawMain withdrawMain = new WithdrawMain();
        withdrawMain.setMainPartnerTradeNo(UUIDUtil.buildMainOrderId());
        withdrawMain.setUserId(userId);
        //withdrawMain.setOpenid(userInfo.getOpenid());
        withdrawMain.setAmountFee(amountTmp);
        withdrawMain.setWithdrawType(1);
        withdrawMain.setTerminal(1);
        withdrawMain.setApplyTime(new Date());
        orderWithdrawMapper.insert(withdrawMain);

        // 获取用户已累积提现的金额
        Set<Map.Entry<Integer, BigDecimal>> entries = incomeMap.entrySet();
        Date applyTime = new Date();
        for (Map.Entry<Integer, BigDecimal> e : entries) {
            Integer key = e.getKey();
            BigDecimal value = e.getValue();
            WithdrawSub withdrawSub = new WithdrawSub();
            withdrawSub.setId(partnerNoMap.get(key));
            withdrawSub.setMainPartnerTradeNo(withdrawMain.getMainPartnerTradeNo());
            withdrawSub.setUserId(userId);
            if (user != null) {
                withdrawSub.setUserType(user.getUserType());
                withdrawSub.setUserName(user.getRealName());
                withdrawSub.setMobile(user.getMobile());
                withdrawSub.setOpenid(user.getOpenid());
            }
            withdrawSub.setWithdrawFee(value);
            withdrawSub.setWithdrawType(1);
            withdrawSub.setOrigCash("健康e家公众号");
            withdrawSub.setDestCash("微信零钱");
            withdrawSub.setFormalitiesFee(new BigDecimal(0));
            withdrawSub.setApplyTime(applyTime);
            withdrawSub.setStatus(WithdrawStatus.WAIT_REVIEW.value);
            withdrawSub.setProductCompanyId(key);
            if (Objects.equals(incomeType, IncomeType.PRODUCT_INCOME.value)) {
                withdrawSub.setRemark("产品收益");
            } else {
                withdrawSub.setRemark("续费收益");
            }
            ProductCompanyDTO company = productFeign.getProductCompanyById(key);
            if (company != null) {
                withdrawSub.setProductCompanyName(company.getName());
            }
            withdrawSubMapper.insert(withdrawSub);
        }
        tmpList.forEach(o -> productIncomeRecordPartMapper.updateByPrimaryKeySelective(o));
        return true;
    }

}
