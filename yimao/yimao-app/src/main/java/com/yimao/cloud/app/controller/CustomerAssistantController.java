package com.yimao.cloud.app.controller;

import com.yimao.cloud.app.feign.SystemFeign;
import com.yimao.cloud.base.utils.CollectionUtil;
import com.yimao.cloud.pojo.dto.system.CustomerAssistantTypeDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 客服助手
 * created by liuhao@yimaokeji.com
 * 2018052018/5/14
 */
@RestController
@RequestMapping(produces = {MediaType.APPLICATION_JSON_VALUE})
@Slf4j
@Api(tags = {"CustomerAssistantController"})
public class CustomerAssistantController {

    @Resource
    private SystemFeign systemFeign;

    /**
     * 客服问答列表
     *
     * @return
     */
    @GetMapping(value = "/customer/list")
    @ApiOperation(value = "客服问答列表", notes = "客服问答列表")
    public Object listAssistant() {
//        端：1-服务站  2-经销商app 3-健康e家
        List<CustomerAssistantTypeDTO> assistantList = systemFeign.getListAssistant(2);
        if (CollectionUtil.isNotEmpty(assistantList)) {
            return ResponseEntity.ok(assistantList);
        }
        return ResponseEntity.ok();
    }


    /**
     * 获取展示端下所有分类
     *
     * @param terminal
     * @return
     */
    @GetMapping("/customer/classify/{terminal}")
    @ApiOperation(value = "展示端分类列表", notes = "展示端分类列表")
    @ApiImplicitParam(name = "terminal", value = "展示端", dataType = "Long",defaultValue = "2",required = true, paramType = "path")
    public Object getClassifyList(@PathVariable(required = true) Integer terminal) {
        List<CustomerAssistantTypeDTO> classifyList = systemFeign.getClassifyList(terminal);
        return ResponseEntity.ok(classifyList);
    }
}
