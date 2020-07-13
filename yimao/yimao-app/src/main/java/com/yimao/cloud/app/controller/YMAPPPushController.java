package com.yimao.cloud.app.controller;

import com.yimao.cloud.app.feign.SystemFeign;
import com.yimao.cloud.base.cache.UserCache;
import com.yimao.cloud.base.enums.MessagePushObjectEnum;
import com.yimao.cloud.base.exception.BadRequestException;
import com.yimao.cloud.pojo.dto.system.MessagePushDTO;
import com.yimao.cloud.pojo.vo.PageVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Liu Yi
 * @description 经销商app系统消息
 * @date 2019/10/17 14:12
 */
@Slf4j
@RestController
@Api("YMAPPPushController")
public class YMAPPPushController {

    @Resource
    private SystemFeign systemFeign;
    @Resource
    private UserCache userCache;

    @GetMapping("/ym/message/list")
    @ApiOperation(notes = "查看消息列表", value = "查看消息列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "页码", defaultValue = "1", dataType = "Long", required = true, paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "页数", defaultValue = "10", dataType = "Long", required = true, paramType = "query")
    })
    public Object ymMessagePage(@RequestParam("pageNum") Integer pageNum,@RequestParam(value = "pageSize") Integer pageSize) {
        Integer userId = userCache.getUserId();
        PageVO<MessagePushDTO> page = systemFeign.getMessagePushPage(userId, null, null, null, null, null,
                MessagePushObjectEnum.DISTRIBUTOR.value, pageSize, pageNum);
        return ResponseEntity.ok(page);
    }

    @PatchMapping("/ym/message/{id}/read")
    @ApiOperation(notes = "已读更新", value = "已读更新")
    @ApiImplicitParam(name = "id", value = "id", dataType = "Long", required = true, paramType = "path")
    public Object messageRead(@PathVariable("id") Integer id) {
        systemFeign.setMessagePushIsRead(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/ym/message/{id}")
    @ApiOperation(notes = "删除消息", value = "删除消息")
    @ApiImplicitParam(name = "id", value = "id", dataType = "Long", required = true, paramType = "path")
    public Object messageDelete(@PathVariable("id") Integer id) {
        if (id == null) {
            throw new BadRequestException("消息id无能为空！");
        }
        systemFeign.deleteMessage(String.valueOf(id));
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/ym/message/unReadCount")
    @ApiOperation(value = "未读消息条数")
    public Map<String, Integer> unReadCount() {
        Integer userId = userCache.getUserId();
        Integer unReadCount = systemFeign.getMessageUnReadNum(userId, null, null, MessagePushObjectEnum.DISTRIBUTOR.value);
        if (unReadCount == null) {
            unReadCount = 0;
        }
        Map<String, Integer> map = new HashMap<>();
        map.put("count", unReadCount);
        return map;
    }
}
