package com.yimao.cloud.order.controller;

import com.yimao.cloud.base.utils.StringUtil;
import com.yimao.cloud.order.mapper.OrderSubMapper;
import com.yimao.cloud.order.service.PayAccountService;
import com.yimao.cloud.pojo.dto.order.PayAccountDetail;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @desc: 支付账号配置
 * @author: zhangbaobao
 * @create: 2019/9/19
 */
@RestController
@Slf4j
@Api(tags = "PayAccountController")
public class PayAccountController {

    @Resource
    private OrderSubMapper orderSubMapper;
    @Resource
    private PayAccountService payAccountService;

    /**
     * 获取支付账号信息
     *
     * @param companyId   公司编号
     * @param platform    支付平台（和PayPlatform枚举类对应）：1-微信；2-支付宝；3-银行；
     * @param clientType  客户端（和SystemType枚举类对应）：1-健康e家公众号；4-经销商APP；5-安装工APP；6-水机PAD；10-H5页面；
     * @param receiveType 款项收取类型（默认1）：1-商品费用；2-经销代理费用
     */
    @PostMapping(value = "/payaccount")
    @ApiOperation(value = "获取支付账号信息")
    public PayAccountDetail getPayAccount(@RequestParam(required = false) String outTradeNo,
                                          @RequestParam(required = false) Integer companyId,
                                          @RequestParam Integer platform,
                                          @RequestParam Integer clientType,
                                          @RequestParam Integer receiveType) {
        if (StringUtil.isEmpty(outTradeNo) && companyId == null) {
            log.error("获取支付账号信息失败，产品ID和产品公司ID不能同时为空");
            return null;
        }
        if (companyId == null) {
            companyId = orderSubMapper.getProductCompanyIdByMainOrderId(Long.parseLong(outTradeNo));
        }
        if (companyId == null) {
            log.error("获取支付账号信息失败，产品公司ID不能为空");
            return null;
        }
        return payAccountService.getPayAccountDetail(companyId, platform, clientType, receiveType);
    }
}
