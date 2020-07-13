package com.yimao.cloud.order.pay.other;

import com.yimao.cloud.order.service.OfflinePayService;
import com.yimao.cloud.pojo.dto.order.WorkOrderDTO;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import javax.annotation.Resource;

/**
 * 描述：线下支付（POS机、转账）
 *
 * @Author Zhang Bo
 * @Date 2019/9/18
 */
@RestController
@Slf4j
@Api(tags = "OfflinePayController")
public class OfflinePayController {

    @Resource
    private OfflinePayService offlinePayService;

    /**
     * 线下支付提交支付凭证
     */
    @PostMapping(value = "/otherpay/submitcredential")
    public void submitCredential(@RequestParam(required = false) Long mainOrderId,
                                 @RequestParam(required = false) Long subOrderId,
                                 @RequestParam(required = false) String workOrderId,
                                 @RequestParam Integer payType, @RequestParam String payCredential) {
        log.info("线下支付提交支付凭证：/otherpay/submitcredential，mainOrderId={}, subOrderId={}, workOrderId={}, payType={}, payCredential={}",
                mainOrderId, subOrderId, workOrderId, payType, payCredential);
        offlinePayService.submitCredential(mainOrderId, subOrderId, workOrderId, payType, payCredential);
    }
    
    /***
     * 其他支付
     * @param wo
     */
    @PostMapping(value = "/otherpay/submit", consumes = MediaType.APPLICATION_JSON_VALUE)
	public void otherPay(@RequestBody WorkOrderDTO wo) {
    	offlinePayService.otherPay(wo);
    }
}
