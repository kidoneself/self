package com.yimao.cloud.station.controller;

import com.yimao.cloud.pojo.dto.cms.ContentDTO;
import com.yimao.cloud.pojo.dto.cms.VideoDTO;
import com.yimao.cloud.pojo.vo.PageVO;
import com.yimao.cloud.station.feign.CmsFeign;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Date;

/**
 * 内容 模块
 *
 * @author Liu Long Jie
 */
@RestController
@Api(tags = "CmsController")
@Slf4j
public class CmsController {

    @Resource
    private CmsFeign cmsFeign;

    /**
     * 站务系统--内容--翼猫资讯--列表展示
     *
     * @param pageNum
     * @param pageSize
     * @param platform
     * @param status
     * @param title
     * @param parentCategoryId
     * @param startReleaseTime
     * @param endReleaseTime
     * @return
     */
    @GetMapping("/content/station/{pageNum}/{pageSize}")
    @ApiOperation(value = "查询内容分页信息", notes = "查询内容分页信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", required = true, value = "页码", dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "pageSize", required = true, value = "页大小", dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "platform", value = "来源 端 1 经销商app 2 微信公众号  3 小程序 4 站务系统", required = true, dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "status", value = "状态 是否删除：1已发布 2未发布，3已删除 ,", required = true, dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "sort", value = "站务系统排序规则 1-最新逆序 2-推荐", required = true, dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "location", value = "位置：1资讯 2公号 3协议", required = true, dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "title", value = "标题", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "parentCategoryId", value = "类型ID", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "startReleaseTime", value = "发布时间（始）", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "endReleaseTime", value = "发布时间（终）", dataType = "String", paramType = "query")
    })
    public PageVO<ContentDTO> findContentPage(@PathVariable(value = "pageNum") Integer pageNum,
                                              @PathVariable(value = "pageSize") Integer pageSize,
                                              @RequestParam(value = "platform") Integer platform,
                                              @RequestParam(value = "status") Integer status,
                                              @RequestParam(value = "sort") Integer sort,
                                              @RequestParam(value = "location") Integer location,
                                              @RequestParam(value = "title", required = false) String title,
                                              @RequestParam(value = "parentCategoryId", required = false) Integer parentCategoryId,
                                              @RequestParam(value = "startReleaseTime", required = false) Date startReleaseTime,
                                              @RequestParam(value = "endReleaseTime", required = false) Date endReleaseTime) {

        return cmsFeign.findContentPage(pageNum, pageSize, platform, status, sort, location, title, parentCategoryId, startReleaseTime, endReleaseTime);
    }

    @GetMapping("/content/station/{contentId}")
    @ApiOperation(value = "查看资讯增加访问量", notes = "查看资讯增加访问量")
    @ApiImplicitParam(name = "contentId", required = true, value = "资讯id", paramType = "path", dataType = "Long")
    public Object addViewCount(@PathVariable(value = "contentId") Integer id) {
        cmsFeign.addViewCount(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * 观看次数累加
     *
     * @param id 视频id
     */
    @GetMapping(value = {"/video/watch/{id}"})
    @ApiOperation(value = "观看次数累加", notes = "观看次数累加")
    @ApiImplicitParam(name = "id", value = "视频id", dataType = "Long", paramType = "path")
    public Object videoClickWatch(@PathVariable("id") Integer id) {
        cmsFeign.videoClickWatch(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * 站务系统--内容--翼猫视频--列表展示
     *
     * @param platform
     * @param sort
     * @param deleteFlag
     * @param liveTypeId
     * @param name
     * @param startReleaseTime
     * @param endReleaseTime
     * @param pageNum
     * @param pageSize
     * @return
     */
    @GetMapping(value = {"/video/{pageNum}/{pageSize}"})
    @ApiOperation("视频列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", required = true, value = "页码", dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "pageSize", required = true, value = "页大小", dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "platform", value = "展示端", required = true, dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "sort", value = "站务系统排序规则 1-最新逆序 2-推荐", required = true, dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "deleteFlag", value = "是否删除：0-未删除，1-已删除", required = true, dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "liveTypeId", value = "一级视频分类ID", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "name", value = "视频内容（视频名称关键字搜索）", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "startReleaseTime", value = "发布时间（始）", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "endReleaseTime", value = "发布时间（终）", dataType = "String", paramType = "query")
    })
    public PageVO<VideoDTO> getVideoByTypeId(@RequestParam(value = "platform") Integer platform,
                                             @RequestParam(value = "sort") Integer sort,
                                             @RequestParam(value = "deleteFlag") Integer deleteFlag,
                                             @RequestParam(value = "liveTypeId", required = false) Integer liveTypeId,
                                             @RequestParam(value = "name", required = false) String name,
                                             @RequestParam(value = "startReleaseTime", required = false) Date startReleaseTime,
                                             @RequestParam(value = "endReleaseTime", required = false) Date endReleaseTime,
                                             @PathVariable(value = "pageNum") Integer pageNum,
                                             @PathVariable(value = "pageSize") Integer pageSize) {
        return cmsFeign.listVideo(platform, sort, liveTypeId, name, deleteFlag, startReleaseTime, endReleaseTime, pageNum, pageSize);
    }

    /**
     * 站务系统 资讯类型分类下拉框
     *
     * @param pageNum
     * @param pageSize
     * @return
     */
    @GetMapping("/content/category/station/{pageNum}/{pageSize}")
    @ApiOperation(value = "站务系统资讯类型下拉框", notes = "站务系统资讯类型下拉框")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "type", required = true, value = "类别类型 ，1 总部视频分类，2 总部文章分类，3 服务站视频分类，4 服务站文章分类 ", paramType = "query", dataType = "Long"),
            @ApiImplicitParam(name = "platform", required = true, value = "展示端", paramType = "query", dataType = "Long"),
            @ApiImplicitParam(name = "level", required = true, value = "分类等级", paramType = "query", dataType = "Integer"),
            @ApiImplicitParam(name = "location", required = true, value = "位置：1资讯 2公号 3协议", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "pageNum", required = true, value = "当前页", paramType = "path", dataType = "Long"),
            @ApiImplicitParam(name = "pageSize", required = true, value = "页大小", paramType = "path", dataType = "Long")
    })
    public Object findContentCategoryPage(@RequestParam(value = "type") Integer type,
                                          @RequestParam(value = "platform") Integer platform,
                                          @RequestParam(value = "level") Integer level,
                                          @RequestParam(value = "location") Integer location,
                                          @PathVariable(value = "pageNum") Integer pageNum,
                                          @PathVariable(value = "pageSize") Integer pageSize) {

        return cmsFeign.cmsCategoryFindPage(pageNum, pageSize, platform, level, location, type);
    }

    @GetMapping(value = {"/video/type/station/{pageNum}/{pageSize}"})
    @ApiOperation(value = "站务系统视频类型下拉框")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "level", required = true, value = "分类等级", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "platform", required = true, value = " 端 1-终端app；2-微信公众号；3-经销商APP；4-小程序,5-站务系统", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "pageNum", required = true, value = "当前页", dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "pageSize", required = true, value = "每页条数", dataType = "Long", paramType = "path")
    })
    public Object videoTypePage(@PathVariable(value = "pageNum") Integer pageNum,
                                @PathVariable(value = "pageSize") Integer pageSize,
                                @RequestParam(value = "platform") Integer platform,
                                @RequestParam(value = "level") Integer level) {

        return cmsFeign.videoTypePage(platform, level, pageNum, pageSize);
    }

}
