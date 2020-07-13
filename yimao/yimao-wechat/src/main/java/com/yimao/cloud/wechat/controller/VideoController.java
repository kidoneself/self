package com.yimao.cloud.wechat.controller;

import com.yimao.cloud.wechat.feign.CmsFeign;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 视频
 */
@RestController
@Slf4j
@Api(tags = "VideoController")
public class VideoController {

    @Resource
    private CmsFeign cmsFeign;

    /**
     * 视频类型列表
     *
     * @return
     */
    @GetMapping(value = {"/video/type"})
    public Object videoTypeList() {
        return ResponseEntity.ok(cmsFeign.videoTypeList());
    }

    /**
     * 分类查询视频列表
     */
    @GetMapping(value = {"/video/{pageNum}/{pageSize}"})
    public Object videoList(@RequestParam(required = false) Integer liveId,
                            @RequestParam(required = false) Integer typeId,
                            @RequestParam(required = false) Integer liveTypeId,
                            @RequestParam(required = false) Integer recommend,
                            @RequestParam(required = false) Integer liveStatus,
                            @RequestParam(required = false) Integer deleteFlag,
                            @PathVariable(value = "pageNum") Integer pageNum,
                            @PathVariable(value = "pageSize") Integer pageSize) {
        return cmsFeign.list(liveId, typeId, liveTypeId, recommend, liveStatus, 0, pageNum, pageSize);
    }

    /**
     * 根据视频id查询视频详情
     *
     * @param id
     * @return
     */
    @GetMapping(value = {"/video/{id}"})
    public Object videoById(@PathVariable("id") Integer id) {
        return ResponseEntity.ok(cmsFeign.videoById(id));
    }


    /**
     * 点赞数累加
     *
     * @param id 视频id
     */
    @GetMapping(value = {"/video/{id}/like"})
    public Object videoClickLike(@PathVariable("id") Integer id) {
        cmsFeign.videoClickLike(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * 分享数累加
     *
     * @param id 视频id
     */
    @GetMapping(value = {"/video/{id}/share"})
    public Object videoClickShare(@PathVariable("id") Integer id) {
        cmsFeign.videoClickShare(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * 观看次数累加
     *
     * @param id 视频id
     */
    @GetMapping(value = {"/video/{id}/watch"})
    public Object videoClickWatch(@PathVariable("id") Integer id) {
        cmsFeign.videoClickWatch(id);
        return ResponseEntity.noContent().build();
    }
}
