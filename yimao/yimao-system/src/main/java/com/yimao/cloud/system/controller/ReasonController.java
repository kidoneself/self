package com.yimao.cloud.system.controller;

import com.yimao.cloud.system.service.ReasonService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
public class ReasonController {

    @Resource
    private ReasonService reasonService;

    @GetMapping(value = "/reason")
    public Object getReasonList(@RequestParam(required = false) Integer type) {
        return reasonService.listByType(type);
    }

}
