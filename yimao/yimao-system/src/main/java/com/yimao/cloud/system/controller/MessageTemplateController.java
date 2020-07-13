package com.yimao.cloud.system.controller;

import com.yimao.cloud.base.enums.MessageMechanismEnum;
import com.yimao.cloud.base.enums.MessageModelTypeEnum;
import com.yimao.cloud.base.enums.MessagePushModeEnum;
import com.yimao.cloud.base.enums.MessagePushObjectEnum;
import com.yimao.cloud.pojo.dto.system.MessageTemplateDTO;
import com.yimao.cloud.pojo.vo.PageVO;
import com.yimao.cloud.system.service.MessageTemplateService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/***
 * 功能描述:消息模版
 *
 * @auther: liu yi
 * @date: 2019/4/30 10:10
 */
@Slf4j
@RestController
@Api(tags = "MessageTemplateController")
public class MessageTemplateController {
    @Resource
    private MessageTemplateService messageTemplateService;

    @RequestMapping(value = {"/messageTemplate/{pageSize}/{pageNum}"}, method = {RequestMethod.GET})
    @ApiOperation(value = "分页查询模版信息", notes = "分页查询模版信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "type", value = "类型：1-阈值提醒 2-续费推送 3-续费推送 4-经销商注册系统 5-净水工单 6-健康产品工单", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "mechanism", value = "机制", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "pushObject", value = "推送对象", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "pushMode", value = "推送方式", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "pageNum", value = "分页页码", defaultValue = "1", required = true, dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "pageSize", value = "分页大小", defaultValue = "10", required = true, dataType = "Long", paramType = "path")
    })
    public Object pageQueryMessageTemplate(@RequestParam(value = "type", required = false) Integer type,
                                           @RequestParam(value = "mechanism", required = false) Integer mechanism,
                                           @RequestParam(value = "pushObject", required = false) Integer pushObject,
                                           @RequestParam(value = "pushMode", required = false) Integer pushMode,
                                           @PathVariable(value = "pageNum") Integer pageNum,
                                           @PathVariable(value = "pageSize") Integer pageSize) {
        PageVO<MessageTemplateDTO> page = this.messageTemplateService.page(type, mechanism, pushObject, pushMode, pageSize, pageNum);

        return ResponseEntity.ok(page);
    }

    @RequestMapping(value = {"/messageTemplate"}, method = {RequestMethod.GET})
    @ApiOperation(value = "根据参数查找模版信息", notes = "根据参数查找模版信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "type", value = "类型", dataType = "Long", required = true, paramType = "query"),
            @ApiImplicitParam(name = "mechanism", value = "机制", dataType = "Long", required = true, paramType = "query"),
            @ApiImplicitParam(name = "pushObject", value = "推送对象", dataType = "Long", required = true, paramType = "query"),
            @ApiImplicitParam(name = "pushMode", value = "推送方式", dataType = "Long", required = true, paramType = "query")
    })
    public Object findMessageTemplateByParam(@RequestParam(value = "type") Integer type,
                                             @RequestParam(value = "mechanism") Integer mechanism,
                                             @RequestParam(value = "pushObject") Integer pushObject,
                                             @RequestParam(value = "pushMode") Integer pushMode) {
        MessageTemplateDTO mtDTO = this.messageTemplateService.getMessageTemplateByParam(type, mechanism, pushObject, pushMode);

        return ResponseEntity.ok(mtDTO);
    }

    @RequestMapping(value = {"/messageTemplate/params"}, method = {RequestMethod.GET})
    @ApiOperation("获取需要的消息模版参数")
    public Object getMessageTemplateParams() {
        Map<String, Object> map = new HashMap();

        map.put("types", MessageModelTypeEnum.values());
        map.put("mechanisms", MessageMechanismEnum.values());
        map.put("pushObjects", MessagePushObjectEnum.values());
        map.put("pushModes", MessagePushModeEnum.values());

        return ResponseEntity.ok(map);
    }

    @RequestMapping(value = {"/messageTemplate"}, method = {RequestMethod.POST})
    @ApiOperation("新增消息模版")
    @ApiImplicitParam(name = "dto", value = "消息模版", required = true, dataType = "MessageTemplateDTO", paramType = "body")
    public Object add(@RequestBody MessageTemplateDTO dto) {
        messageTemplateService.addMessageTemplate(dto);
        log.info("添加了消息推送模板");
        return ResponseEntity.noContent().build();
    }

    @RequestMapping(value = {"/messageTemplate"}, method = {RequestMethod.PUT})
    @ApiOperation("更新消息模版")
    @ApiImplicitParam(name = "dto", value = "更新消息模版", required = true, dataType = "MessageTemplateDTO", paramType = "body")
    public Object update(@RequestBody MessageTemplateDTO dto) {
        messageTemplateService.updateMessageTemplate(dto);
        return ResponseEntity.noContent().build();
    }

    @RequestMapping(value = {"/messageTemplate/{id}"}, method = {RequestMethod.GET})
    @ApiOperation("获取消息模版")
    @ApiImplicitParam(name = "id", value = "获取消息模版", required = true, dataType = "Long", paramType = "path")
    public Object getMessageTemplateById(@PathVariable(value = "id") Integer id) {
        MessageTemplateDTO dto = messageTemplateService.getMessageTemplateById(id);
        return ResponseEntity.ok(dto);
    }
}
