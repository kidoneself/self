package com.yimao.cloud.cms.controller;

import com.yimao.cloud.base.exception.BadRequestException;
import com.yimao.cloud.base.exception.YimaoException;
import com.yimao.cloud.base.utils.CollectionUtil;
import com.yimao.cloud.cms.dto.ContentGeneralSituationDTO;
import com.yimao.cloud.cms.po.Content;
import com.yimao.cloud.cms.service.ContentService;
import com.yimao.cloud.pojo.dto.cms.CmsCategoryDTO;
import com.yimao.cloud.pojo.dto.cms.ContentDTO;
import com.yimao.cloud.pojo.query.station.HeadOfficeMessageQuery;
import com.yimao.cloud.pojo.vo.PageVO;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.ws.rs.core.MediaType;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 内容 信息
 *
 * @author liu.lin
 * @since 2018-12-29 16:00:28
 */
@RestController
@Slf4j
public class ContentController {

    @Resource
    private ContentService contentService;


    @PostMapping("/content")
    @ApiOperation(value = "内容保存", notes = "内容保存")
    @ApiImplicitParam(name = "dto", value = "内容保存对象", dataType = "ContentDTO", paramType = "body")
    public void saveContent(@RequestBody ContentDTO dto) {
        Date now = new Date();
        dto.setCreateTime(now);
        if (dto.getStatus() == 1) {
            //设置发布时间
            dto.setReleaseTime(now);
        }
        contentService.save(dto);
    }

    @PutMapping("/content")
    @ApiOperation(value = "更新内容", notes = "更新内容")
    @ApiImplicitParam(name = "dto", value = "更新内容对象", dataType = "ContentDTO", paramType = "body")
    public Object updateContent(@RequestBody ContentDTO dto) {
        if (null == dto.getId()) {
            throw new BadRequestException("内容ID不能为空");
        }
        dto.setUpdateTime(new Date());
        Integer count = contentService.update(dto);
        if (count > 0) {
            return ResponseEntity.noContent().build();
        }
        throw new YimaoException("系统异常");
    }


    @PatchMapping("/content")
    @ApiOperation(value = "批量更新内容", notes = "批量更新内容")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "contentIds", value = "批量更新的id,隔开", paramType = "Long", allowMultiple = true, dataType = "query"),
            @ApiImplicitParam(name = "status", value = "内容信息容器", paramType = "Long", dataType = "query")
    })
    public Object batchUpdate(@RequestParam("contentIds") List<Integer> contentIds,
                              @RequestParam("status") Integer status) {
        try {
            if (CollectionUtil.isNotEmpty(contentIds)) {
                Content content = new Content();
                content.setStatus(status);
                if (status == 1) {
                    content.setReleaseTime(new Date());
                }
                contentService.batchUpdate(contentIds, content);
                return ResponseEntity.noContent().build();
            } else {
                throw new BadRequestException("更新内容ID不能为空！");
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new YimaoException("系统异常！");
        }
    }

    @GetMapping("/content/{pageNum}/{pageSize}")
    @ApiOperation(value = "查询内容分页信息", notes = "查询内容分页信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", required = true, value = "页码", dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "pageSize", required = true, value = "页大小", dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "title", value = "标题", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "platform", value = "来源 端 1 经销商app 2 微信公众号  3 小程序", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "parentCategoryId", value = "类型ID", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "top", value = "是否推送", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "status", value = "状态 是否删除：1已发布 2未发布，3已删除 ,", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "type", value = "1总部 内容  2服务站内容", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "contentMode", value = "资讯类型 1、普通文本内容，2、公告 带红头文件附件", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "location", value = "位置：1资讯 2公号 3协议", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "userId", value = "当前登录用户e家号", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "sort", value = "站务系统排序规则 1-最新逆序 2-推荐", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "startReleaseTime", value = "发布时间（始）", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "endReleaseTime", value = "发布时间（终）", dataType = "String", paramType = "query")
    })
    public ResponseEntity<PageVO<ContentDTO>> findPage(@PathVariable(value = "pageNum") Integer pageNum,
                                                       @PathVariable(value = "pageSize") Integer pageSize,
                                                       @RequestParam(value = "title", required = false) String title,
                                                       @RequestParam(value = "platform", required = false) Integer platform,
                                                       @RequestParam(value = "parentCategoryId", required = false) Integer parentCategoryId,
                                                       @RequestParam(value = "top", required = false) Integer top,
                                                       @RequestParam(value = "status", required = false) Integer status,
                                                       @RequestParam(value = "type", required = false) Integer type,
                                                       @RequestParam(value = "contentMode", required = false) Integer contentMode,
                                                       @RequestParam(value = "location", required = false) Integer location,
                                                       @RequestParam(value = "userId", required = false) Integer userId,
                                                       @RequestParam(value = "sort", required = false) Integer sort,
                                                       @RequestParam(value = "startReleaseTime", required = false) Date startReleaseTime,
                                                       @RequestParam(value = "endReleaseTime", required = false) Date endReleaseTime) {
        ContentDTO content = new ContentDTO();
        content.setTitle(title);
        content.setParentCategoryId(parentCategoryId);
        content.setTop(top);
        content.setPlatform(platform);
        content.setStatus(status);
        content.setType(type);
        content.setContentMode(contentMode);
        content.setLocation(location);
        content.setStartReleaseTime(startReleaseTime);
        content.setEndReleaseTime(endReleaseTime);
        content.setSort(sort);
        PageVO<ContentDTO> page = contentService.findPage(pageNum, pageSize, content, userId);
        return ResponseEntity.ok(page);
    }

    /**
     * 站务系统--首页--控制台--获取推荐位在前三的服务站展示的资讯
     *
     * @return
     */
    @GetMapping("/content/station/getInformationByTop")
    public Object getInformationByTop() {

        return ResponseEntity.ok(contentService.getInformationByTop());
    }


    @GetMapping("/content/{contentId}")
    @ApiOperation(value = "单个查询", notes = "单个查询接口")
    @ApiImplicitParam(name = "contentId", required = true, value = "单个查询的id", paramType = "path", dataType = "Long")
    public Object findById(@PathVariable(value = "contentId") Integer id) {
        ContentDTO dto = contentService.queryById(id);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/content/station/{contentId}")
    @ApiOperation(value = "查看资讯增加访问量", notes = "查看资讯增加访问量")
    @ApiImplicitParam(name = "contentId", required = true, value = "资讯id", paramType = "path", dataType = "Long")
    public Object addViewCount(@PathVariable(value = "contentId") Integer id) {
        contentService.addViewCount(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * 获取今日内容概况
     *
     * @return org.springframework.http.ResponseEntity
     * @Author lizhiqiang
     * @Date 2019/3/12
     * @Param []
     */
    @GetMapping(value = "/content/general")
    @ApiOperation(value = "内容概况", notes = "内容概况")
    public ResponseEntity getContentGeneralSituation() {
        ContentGeneralSituationDTO dto = contentService.getContentGeneralSituation();
        if (dto == null) {
            throw new YimaoException("获取内容概况失败");
        }
        return ResponseEntity.ok(dto);
    }

    /**
     * 内容文章变化趋势
     *
     * @return org.springframework.http.ResponseEntity
     * @Author lizhiqiang
     * @Date 2019/3/12
     * @Param []
     */
    @GetMapping("/content/tendency/article")
    @ApiOperation(value = "内容文章变化趋势", notes = "内容文章变化趋势")
    @ApiImplicitParam(name = "days", required = true, value = "天数", paramType = "query", dataType = "Long", defaultValue = "7")
    public ResponseEntity getContentArticleTendency(@RequestParam(value = "days", defaultValue = "7") Integer days) {
        List<Map<String, Integer>> list = contentService.getContentArticleTendency(days);
        return ResponseEntity.ok(list);
    }
    
    
    /**
     * 站务系统总部消息列表
     * @param pageNum
     * @param pageSize
     * @param query
     * @return
     */
    @PostMapping("/content/station/console/headOfficeMessage/{pageSize}/{pageNum}")
    public PageVO<ContentDTO> getHeadOfficeMessageList(@PathVariable("pageNum") Integer pageNum,
                             @PathVariable("pageSize") Integer pageSize,@RequestBody HeadOfficeMessageQuery query){
    	 	
		return contentService.getHeadOfficeMessageList(pageNum,pageSize,query);
		
	}


    /**
     * 安装工APP通知公告列表
     * @param pageNum
     * @param pageSize
     * @return
     */
    @GetMapping("/content/engineer/noticeMessage/{pageSize}/{pageNum}")
	public PageVO<ContentDTO> getNoticeMessage(@PathVariable("pageNum") Integer pageNum,
                                               @PathVariable("pageSize") Integer pageSize){
        return contentService.getNoticeMessage(pageNum,pageSize);
    }
    // /**
    //  * 内容视频变化趋势
    //  *
    //  * @return org.springframework.http.ResponseEntity
    //  * @Author lizhiqiang
    //  * @Date 2019/3/12
    //  * @Param []
    //  */
    // @GetMapping("/content/tendency")
    // @ApiOperation(value = "内容变化趋势", notes = "内容变化趋势")
    // @ApiImplicitParam(name = "days", required = true, value = "天数", paramType = "query", dataType = "Long", defaultValue = "7")
    // public ResponseEntity getContentVideoTendency(@RequestParam(value = "days", defaultValue = "7") Integer days) {
    //     List<Map<String, Integer>> list = contentService.getContentVideoTendency(days);
    //     return ResponseEntity.ok(list);
    // }


    // @GetMapping("/content/system/{pageNum}/{pageSize}")
    // @ApiOperation(value = "查询内容分页信息", notes = "查询内容分页信息")
    // @ApiImplicitParams({
    //         @ApiImplicitParam(name = "pageNum", required = true, value = "页码", defaultValue = "1", dataType = "Long", paramType = "path"),
    //         @ApiImplicitParam(name = "pageSize", required = true, value = "页大小", defaultValue = "10", dataType = "Long", paramType = "path"),
    //         @ApiImplicitParam(name = "parentCategoryId", value = "父类型ID", dataType = "Long", paramType = "query"),
    //         @ApiImplicitParam(name = "userId", value = "用户id", dataType = "Long", paramType = "query")
    // })
    // public ResponseEntity<PageVO<ContentDTO>> systemNews(@PathVariable(value = "pageNum") Integer pageNum,
    //                                                      @PathVariable(value = "pageSize") Integer pageSize,
    //                                                      @RequestParam(value = "parentCategoryId", required = false) Integer parentCategoryId,
    //                                                      @RequestParam(value = "userId", required = false) Integer userId) {
    //     PageVO<ContentDTO> page = contentService.systemNews(pageNum, pageSize, parentCategoryId, userId);
    //     return ResponseEntity.ok(page);
    // }
}
