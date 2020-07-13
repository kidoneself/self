package com.yimao.cloud.wechat.controller;

import com.yimao.cloud.base.utils.CollectionUtil;
import com.yimao.cloud.pojo.dto.system.CustomerAssistantTypeDTO;
import com.yimao.cloud.wechat.feign.SystemFeign;
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
     * 分页查询客服问答列表
     *
     * @return list
     */
    @GetMapping(value = "/customer/assistant/{pageNum}/{pageSize}")
    @ApiOperation(value = "分页查询客服问答列表", notes = "分页查询客服问答列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "页码", dataType = "Long", required = true, paramType = "path"),
            @ApiImplicitParam(name = "pageSize", value = "页数", dataType = "Long", required = true, paramType = "path"),
            @ApiImplicitParam(name = "typeCodes", value = "类型code", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "questions", value = "问题", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "publish", value = "状态", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "recommend", value = "是否推荐", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "terminal", value = "展示端", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "typeName", value = "分类名", dataType = "String", paramType = "query")
    })
    public Object listAssistant(@RequestParam(value = "typeCodes", required = false) Integer typeCodes,
                                @RequestParam(value = "questions", required = false) String questions,
                                @RequestParam(value = "publish", required = false) Integer publish,
                                @RequestParam(value = "recommend", required = false) Integer recommend,
                                @RequestParam(value = "terminal", required = false) Integer terminal,
                                @RequestParam(value = "typeName", required = false) String typeName,
                                @PathVariable(value = "pageNum") Integer pageNum,
                                @PathVariable(value = "pageSize") Integer pageSize) {
        return ResponseEntity.ok(systemFeign.listAssistant(typeCodes, questions, publish, recommend, terminal, typeName, pageNum, pageSize));
    }


    /**
     * 客服问答列表
     *
     * @return
     */
    @GetMapping(value = "/customer/list")
    @ApiOperation(value = "客服问答列表", notes = "客服问答列表")
    public Object listAssistant() {
//        端：1-服务站  2-经销商app 3-健康e家
        List<CustomerAssistantTypeDTO> assistantList = systemFeign.getListAssistant(3);
        if (CollectionUtil.isNotEmpty(assistantList)) {
            return ResponseEntity.ok(assistantList);
        }
        return ResponseEntity.ok();
    }
}
