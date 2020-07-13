package com.yimao.cloud.system.controller;

import com.yimao.cloud.pojo.dto.system.MessageContentDTO;
import com.yimao.cloud.pojo.dto.system.MessageRecordDTO;
import com.yimao.cloud.system.service.MessageContentService;
import com.yimao.cloud.system.service.MessageRecordService;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author liuhao@yimaokeji.com
 * @version v2.0
 * @date 2019/5/29
 */
@RestController
@Slf4j
@ApiModel(description = "MessageRecordController")
public class MessageRecordController {

    @Resource
    private MessageRecordService recordService;
    @Resource
    private MessageContentService contentService;

    /**
     * 根据条件查询 消息记录
     *
     * @param orderId 订单id
     * @param type   1-手机 2-消息
     * @return list
     */
    @GetMapping(value = "/message/record")
    @ApiOperation(value = "根据条件查询消息记录")
    public Object  messageRecordListByOrderId(@RequestParam("orderId") Long orderId, @RequestParam(value = "type", required = false) Integer type) {
        List<MessageRecordDTO> list=recordService.messageRecordListByOrderId(orderId, type);
        return ResponseEntity.ok(list);
    }

    @PostMapping(value = "/message/record")
    @ApiOperation(value = "保存消息记录")
    public Object messageRecordSave(@RequestBody MessageRecordDTO recordDTO) {
        recordService.messageRecordSave(recordDTO);
        return ResponseEntity.noContent().build();
    }

    @PostMapping(value = "/message/content")
    @ApiOperation(value = "保存消息内容")
    public Object messageContentSave(@RequestBody MessageContentDTO dto) {
        contentService.messageContentSave(dto);
        return ResponseEntity.noContent().build();
    }
}
