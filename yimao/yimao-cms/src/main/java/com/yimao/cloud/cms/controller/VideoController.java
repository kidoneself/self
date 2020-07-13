package com.yimao.cloud.cms.controller;

import com.yimao.cloud.base.cache.UserCache;
import com.yimao.cloud.base.enums.VideoOperationEnum;
import com.yimao.cloud.base.exception.BadRequestException;
import com.yimao.cloud.base.exception.YimaoException;
import com.yimao.cloud.base.utils.StringUtil;
import com.yimao.cloud.cms.po.Video;
import com.yimao.cloud.cms.service.VideoService;
import com.yimao.cloud.pojo.dto.cms.VideoDTO;
import com.yimao.cloud.pojo.dto.cms.VideoTypeDTO;
import com.yimao.cloud.pojo.vo.PageVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * 视频直播类型
 *
 * @author liuhao@yimaokeji.com
 * 2019/5/21
 */
@RestController
@Slf4j
@Api(tags = {"VideoController"})
public class VideoController {

    @Resource
    private VideoService videoService;
    @Resource
    private UserCache userCache;

    //=======================后台管理系统接口开始=============================

    /**
     * 视频列表
     *
     * @param liveId        频道id
     * @param liveTypeId    分类一级标题
     * @param liveTypeSubId 分类二级标题
     * @param liveStatus    状态 1-直播中，2-回放，3-视频，4-预告
     * @param recommend     是否推荐  1：是  0:否
     * @param deleteFlag    是否删除  1：是 0：否
     * @return
     */
    @GetMapping(value = {"/video/{pageNum}/{pageSize}"})
    @ApiOperation("视频列表")
    public PageVO<VideoDTO> list(@RequestParam(required = false) Integer liveId,
                                 @RequestParam(required = false) Integer liveTypeId,
                                 @RequestParam(required = false) Integer liveTypeSubId,
                                 @RequestParam(required = false) String name,
                                 @RequestParam(required = false) Integer recommend,
                                 @RequestParam(required = false) Integer liveStatus,
                                 @RequestParam(required = false) Integer deleteFlag,
                                 @RequestParam(required = false) Integer platform,
                                 @RequestParam(required = false) Integer status,
                                 @RequestParam(required = false) Integer sort,
                                 @RequestParam(required = false) Date startReleaseTime,
                                 @RequestParam(required = false) Date endReleaseTime,
                                 @PathVariable(value = "pageNum") Integer pageNum,
                                 @PathVariable(value = "pageSize") Integer pageSize) {
        return videoService.listVideo(liveId, liveTypeId, liveTypeSubId, recommend, liveStatus, deleteFlag, platform, status, name,sort, startReleaseTime, endReleaseTime, pageNum, pageSize);
    }


    @PostMapping(value = {"/video"})
    @ApiOperation("保存视频")
    public VideoDTO saveVideo(@RequestBody VideoDTO dto) {
        String currentAdmin = userCache.getCurrentAdminRealName();
        if (dto == null) {
            throw new BadRequestException("频道不能为空");
        }
        if (StringUtil.isEmpty(dto.getName()) || dto.getLiveTypeId() == null) {
            throw new BadRequestException("标题或分类不能为空");
        }
        Video video = new Video(dto);
        Integer count = videoService.saveVideo(video, currentAdmin);
        if (count > 0) {
            return dto;
        }
        throw new YimaoException("新增视频失败");
    }


    @PatchMapping(value = {"/video"})
    @ApiOperation("修改视频")
    public VideoDTO updateVideo(@RequestBody VideoDTO dto) {
        String currentAdmin = userCache.getCurrentAdminRealName();
        if (dto == null || dto.getLiveId() == null) {
            throw new BadRequestException("频道不能为空");
        }
        if (StringUtil.isEmpty(dto.getName()) || StringUtil.isEmpty(dto.getShareContent()) || dto.getLiveTypeId() == null) {
            throw new BadRequestException("标题或分类不能为空");
        }
        Video video = new Video(dto);
        Integer count = videoService.updateVideo(video, currentAdmin);
        if (count > 0) {
            return dto;
        }
        throw new YimaoException("修改视频失败");
    }


    @DeleteMapping(value = {"/video/{id}"})
    @ApiOperation("删除视频")
    public void deleteVideo(@PathVariable("id") Integer id) {
        videoService.deleteVideo(id);
    }


    @PatchMapping(value = {"/video/line/{id}"})
    @ApiOperation("上线/下线视频")
    public VideoDTO onLine(@PathVariable("id") Integer id) {
        Video live = videoService.onLine(id);
        if (live != null) {
            VideoDTO dto = new VideoDTO();
            live.convert(dto);
            return dto;
        }
        throw new YimaoException("对象不存在");
    }


    //============================公众号开始================================================

    /**
     * 视频分类(加上推荐分类)
     *
     * @return
     */
    @GetMapping(value = {"/video/category"})
    public List<VideoTypeDTO> videoTypeList() {
        return videoService.videoTypeList();
    }

    /**
     * 根据视频id查询视频详情
     *
     * @param id
     * @return
     */
    @GetMapping(value = {"/video/{id}"})
    public VideoDTO videoById(@PathVariable("id") Integer id) {
        Integer userId;
        try {
            userId = userCache.getUserId();
        } catch (Exception e) {
            userId = null;
        }
        VideoDTO video = videoService.videoById(id, userId);
        if (video == null) {
            throw new BadRequestException("视频不存在");
        }
        return video;
    }

    /**
     * 点赞数累加
     *
     * @param id 视频id
     */
    @GetMapping(value = {"/video/like/{id}"})
    public Object videoClickLike(@PathVariable("id") Integer id) {
        Integer userId;
        try {
            userId = userCache.getUserId();
//        } catch (NoLoginException e) {
//            userDTO = null;
        } catch (Exception e) {
            userId = null;
        }
        videoService.updateCount(id, VideoOperationEnum.LIKE.value, userId);
        return ResponseEntity.noContent().build();
    }

    /**
     * 分享数累加
     *
     * @param id 视频id
     */
    @GetMapping(value = {"/video/share/{id}"})
    public Object videoClickShare(@PathVariable("id") Integer id) {
        videoService.updateCount(id, VideoOperationEnum.SHARE.value, null);
        return ResponseEntity.noContent().build();
    }

    /**
     * 观看次数累加
     *
     * @param id 视频id
     */
    @GetMapping(value = {"/video/watch/{id}"})
    public Object videoClickWatch(@PathVariable("id") Integer id) {
        videoService.updateCount(id, VideoOperationEnum.WATCH.value, null);
        return ResponseEntity.noContent().build();
    }

    /**
     * 站务系统--首页--控制台--获取推荐位在前三的服务站展示的视频
     *
     * @return
     */
    @GetMapping("/video/station/getVideoByRecommend")
    public Object getVideoByRecommend() {

        return ResponseEntity.ok(videoService.getVideoByRecommend());
    }

}
