package com.yimao.cloud.system.controller;

import com.yimao.cloud.system.service.GeneralSituationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author Lizhqiang
 * @date 2019/3/13
 */
@Slf4j
@RestController
public class GeneralSituationController {


    @Resource
    private GeneralSituationService generalSituationService;


    /**
     * 概况--产品订单
     *
     * @return
     */
    @GetMapping("/general/situation/product/order")
    public ResponseEntity ProductOrder() {
        return null;
    }


    /**
     * 概况--退款退货订单
     *
     * @return
     */
    @GetMapping("/general/situation/return/order")
    public ResponseEntity ReturnOrder() {
        return null;
    }

    /**
     * 经销商上线
     *
     * @return
     */
    @GetMapping("/general/situation/dealer/online")
    public ResponseEntity DealerOnline() {
        return null;
    }


    /**
     * 财务处理
     *
     * @return
     */
    @GetMapping("/general/situation/finance/dispose")
    public ResponseEntity FinanceDispose() {
        return null;
    }

}
