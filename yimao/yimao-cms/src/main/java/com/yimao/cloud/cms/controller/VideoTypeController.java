package com.yimao.cloud.cms.controller;

import com.yimao.cloud.base.exception.BadRequestException;
import com.yimao.cloud.cms.service.VideoTypeService;
import com.yimao.cloud.pojo.dto.cms.VideoTypeDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 视频直播类型
 *
 * @author liuhao@yimaokeji.com
 * 2019/5/21
 */
@RestController
@Slf4j
@Api(tags = {"VideoTypeController"})
public class VideoTypeController {

    @Resource
    private VideoTypeService videoTypeService;


    @GetMapping(value = {"/video/type/{pageNum}/{pageSize}"})
    @ApiOperation(value = "视频类型")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "typeId", value = "视频类型ID", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "level", value = "分类等级", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "platform", value = " 端 1-终端app；2-微信公众号；3-经销商APP；4-小程序；5-站务系统", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "pageNum", value = "当前页", dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "pageSize", value = "每页条数", dataType = "Long", paramType = "path")
    })
    public Object list(@RequestParam(required = false) Integer typeId,
                       @RequestParam(required = false) Integer platform,
                       @RequestParam(required = false) Integer level,
                       @PathVariable(value = "pageNum") Integer pageNum,
                       @PathVariable(value = "pageSize") Integer pageSize) {
        return videoTypeService.videoTypeList(typeId, platform, level, pageNum, pageSize);
    }


    @PostMapping(value = {"/video/type"})
    @ApiOperation(value = "添加视频类型")
    public void addLiveType(@RequestBody VideoTypeDTO videoTypeDTO) {
        if (videoTypeDTO == null || videoTypeDTO.getName() == null) {
            throw new BadRequestException("对象不存在！");
        }
        videoTypeService.addLiveType(videoTypeDTO);
    }

    @PutMapping(value = {"/video/type"})
    @ApiOperation(value = "修改视频类型")
    public void updateLiveType(@RequestBody VideoTypeDTO videoTypeDTO) {
        if (videoTypeDTO == null || videoTypeDTO.getId() == null) {
            throw new BadRequestException("对象不存在！");
        }
        if (videoTypeDTO.getName() == null) {
            throw new BadRequestException("分类名称不能为空！");
        }
        videoTypeService.updateLiveType(videoTypeDTO);
    }

    @DeleteMapping(value = {"/video/type/{id}"})
    @ApiOperation(value = "删除视频类型")
    public void deleteLiveType(@PathVariable("id") Integer id) {
        videoTypeService.deleteLiveType(id);
    }


    @GetMapping(value = {"/video/type/{id}"})
    @ApiOperation(value = "按照id查询视频分类")
    public VideoTypeDTO getLiveType(@PathVariable("id") Integer id) {
        VideoTypeDTO videoTypeDTO = videoTypeService.getLiveType(id);
        return videoTypeDTO;
    }

    @GetMapping(value = "/video/first/type")
    @ApiOperation(notes = "获取视频所有一级分类", value = "获取视频所有一级分类")
    public Object getVideoType() {
        List<VideoTypeDTO> list = videoTypeService.getAllVideoType();
        return list;
    }
}
