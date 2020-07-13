package com.yimao.cloud.engineer.controller;

import com.yimao.cloud.base.cache.UserCache;
import com.yimao.cloud.base.enums.MessagePushModeEnum;
import com.yimao.cloud.base.enums.MessagePushObjectEnum;
import com.yimao.cloud.engineer.feign.CmsFeign;
import com.yimao.cloud.engineer.feign.OrderFeign;
import com.yimao.cloud.engineer.feign.SystemFeign;
import com.yimao.cloud.pojo.dto.cms.ContentDTO;
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

/**
 * @ClassName PushMessageController
 * @Description 系统消息相关
 * @Author yuchunlei
 * @Date 2020/6/30 16:03
 * @Version 1.0
 */
@Slf4j
@RestController
@Api(tags = "PushMessageController")
public class PushMessageController {

    @Resource
    private SystemFeign systemFeign;
    @Resource
    private UserCache userCache;

    @Resource
    private CmsFeign cmsFeign;

    /**
     * 系统消息列表
     * @param pageNum
     * @param pageSize
     * @return
     */
    @GetMapping("/engineer/message/list/{pageNum}/{pageSize}")
    @ApiOperation(notes = "查看消息列表", value = "查看消息列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageSize", value = "页数", defaultValue = "10", dataType = "Long", required = true, paramType = "path"),
            @ApiImplicitParam(name = "pageNum", value = "页码", defaultValue = "1", dataType = "Long", required = true, paramType = "path"),
            @ApiImplicitParam(name = "pushType", value = "推送类型", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "content", value = "消息关键字", dataType = "String", paramType = "query")
    })
    public Object engineerMessagePage(@PathVariable(value = "pageSize") Integer pageSize,
                                      @PathVariable(value = "pageNum") Integer pageNum,
                                      @RequestParam(value = "pushType",required = false) Integer pushType,
                                      @RequestParam(value = "content",required = false) String content) {
        Integer engineerId = userCache.getCurrentEngineerId();
        PageVO<MessagePushDTO> page = systemFeign.getMessagePushPage(engineerId, pushType, content, null, null, null,
                MessagePushModeEnum.YIMAO_ENGINEER_APP_NOTICE.value, pageSize, pageNum);
        return ResponseEntity.ok(page);
    }


    /**
     * 更新系统消息为已读
     * @param id
     * @return
     */
    @PatchMapping("/engineer/message/{id}/read")
    @ApiOperation(notes = "更新系统消息为已读", value = "更新系统消息为已读")
    @ApiImplicitParam(name = "id", value = "id", dataType = "Long", required = true, paramType = "path")
    public Object messageRead(@PathVariable("id") Integer id) {
        systemFeign.setMessagePushIsRead(id);
        return ResponseEntity.noContent().build();
    }


    /**
     * 删除系统消息
     * @param id
     * @return
     */
    @DeleteMapping(value = {"/messagePush/{id}"})
    @ApiOperation("根据id删除消息")
    @ApiImplicitParam(name = "id", value = "id", required = true, dataType = "Long", paramType = "path")
    public Object delete(@PathVariable("id") Integer id){
        systemFeign.delete(id);
        return ResponseEntity.noContent().build();
    }


    /**
     *批量删除系统消息
     * @param ids
     * @return
     */
    @DeleteMapping(value = {"/messagePush/{ids}/batch"})
    @ApiOperation("根据id删除消息")
    @ApiImplicitParam(name = "ids", value = "多个id用逗号隔开", required = true, dataType = "String", paramType = "path")
    public Object delete(@PathVariable("ids") String ids) {
        systemFeign.deleteMessage(ids);
        return ResponseEntity.noContent().build();
    }



    /**
     * 安装工APP公告列表
     * @param pageNum
     * @param pageSize
     * @return
     */
    @GetMapping("/content/engineer/noticeMessage/{pageNum}/{pageSize}")
    @ApiOperation(notes = "安装工APP公告列表", value = "安装工APP公告列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "页码", defaultValue = "1", dataType = "Long", required = true, paramType = "path"),
            @ApiImplicitParam(name = "pageSize", value = "页数", defaultValue = "10", dataType = "Long", required = true, paramType = "path")
    })
    public PageVO<ContentDTO> getNoticeMessage(@PathVariable("pageNum") Integer pageNum,
                                               @PathVariable("pageSize") Integer pageSize){
        return cmsFeign.getNoticeMessage(pageNum,pageSize);
    }


    /**
     * 更新公告
     * @param id
     * @return
     */
    @PatchMapping("/engineer/notice/read/{id}")
    @ApiOperation(notes = "更新公告", value = "更新公告")
    @ApiImplicitParam(name = "id", value = "公告id", dataType = "Long", required = true, paramType = "path")
    public Object updateContentToRead(@PathVariable(value = "id") Integer id){
        return cmsFeign.updateContentToRead(id);
    }


    /**
     * 删除公告
     * @param ids
     * @return
     */
    @DeleteMapping(value = {"/engineer/notice/{ids}/batch"})
    @ApiOperation(notes = "删除公告", value = "删除公告")
    @ApiImplicitParam(name = "ids", value = "多个id用逗号隔开", required = true, dataType = "String", paramType = "path")
    public Object deleteNotice(@PathVariable("ids") String ids){
        return cmsFeign.deleteNotice(ids);
    }
}
