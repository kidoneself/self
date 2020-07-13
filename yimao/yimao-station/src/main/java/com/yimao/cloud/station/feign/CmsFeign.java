package com.yimao.cloud.station.feign;

import com.yimao.cloud.base.constant.Constant;
import com.yimao.cloud.pojo.dto.cms.CmsCategoryDTO;
import com.yimao.cloud.pojo.dto.cms.ContentDTO;
import com.yimao.cloud.pojo.dto.cms.VideoDTO;
import com.yimao.cloud.pojo.query.station.HeadOfficeMessageQuery;
import com.yimao.cloud.pojo.vo.PageVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

import javax.ws.rs.core.MediaType;

/**
 * @auther: Liu Long Jie
 * @date: 2020-3-20
 */
@FeignClient(name = Constant.MICROSERVICE_CMS)
public interface CmsFeign {

    /**
     * 分页查询类目信息
     *
     * @param pageNum
     * @param pageSize
     * @param platform
     * @return
     * @author liulin
     */
    @GetMapping("/category/{pageNum}/{pageSize}")
    Object cmsCategoryFindPage(@PathVariable(value = "pageNum") Integer pageNum,
                               @PathVariable(value = "pageSize") Integer pageSize,
                               @RequestParam(value = "platform") Integer platform,
                               @RequestParam(value = "level") Integer level,
                               @RequestParam(value = "location") Integer location,
                               @RequestParam(value = "type") Integer type);

    @GetMapping(value = {"/video/type/{pageNum}/{pageSize}"})
    Object videoTypePage(@RequestParam(value = "platform", required = false) Integer platform,
                         @RequestParam(value = "level", required = false) Integer level,
                         @PathVariable(value = "pageNum") Integer pageNum,
                         @PathVariable(value = "pageSize") Integer pageSize);

    /**
     * 视频列表
     */
    @GetMapping(value = {"/video/{pageNum}/{pageSize}"})
    PageVO<VideoDTO> listVideo(@RequestParam(value = "platform") Integer platform,
                               @RequestParam(value = "sort") Integer sort,
                               @RequestParam(value = "liveTypeId", required = false) Integer liveTypeId,
                               @RequestParam(value = "name", required = false) String name,
                               @RequestParam(value = "deleteFlag") Integer deleteFlag,
                               @RequestParam(value = "startReleaseTime", required = false) Date startReleaseTime,
                               @RequestParam(value = "endReleaseTime", required = false) Date endReleaseTime,
                               @PathVariable(value = "pageNum") Integer pageNum,
                               @PathVariable(value = "pageSize") Integer pageSize);

    /**
     * 资讯列表
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
    @GetMapping("/content/{pageNum}/{pageSize}")
    PageVO<ContentDTO> findContentPage(@PathVariable(value = "pageNum") Integer pageNum,
                                       @PathVariable(value = "pageSize") Integer pageSize,
                                       @RequestParam(value = "platform") Integer platform,
                                       @RequestParam(value = "status") Integer status,
                                       @RequestParam(value = "sort") Integer sort,
                                       @RequestParam(value = "location") Integer location,
                                       @RequestParam(value = "title", required = false) String title,
                                       @RequestParam(value = "parentCategoryId", required = false) Integer parentCategoryId,
                                       @RequestParam(value = "startReleaseTime", required = false) Date startReleaseTime,
                                       @RequestParam(value = "endReleaseTime", required = false) Date endReleaseTime);

    /**
     * 资讯浏览量增加
     *
     * @param id
     * @return
     */
    @GetMapping("/content/station/{contentId}")
    void addViewCount(@PathVariable(value = "contentId") Integer id);

    /**
     * 观看次数累加
     *
     * @param id 视频id
     */
    @RequestMapping(value = "/video/watch/{id}", method = RequestMethod.GET)
    Void videoClickWatch(@PathVariable("id") Integer id);

    /**
     * 站务系统--首页--控制台--获取推荐位在前三的服务站展示的视频
     *
     * @return
     */
    @GetMapping(value = "/video/station/getVideoByRecommend")
    List<VideoDTO> getVideoByRecommend();

    /**
     * 站务系统--首页--控制台--获取推荐位在前三的服务站展示的资讯
     *
     * @return
     */
    @GetMapping(value = "/content/station/getInformationByTop")
    List<ContentDTO> getInformationByTop();

    /**
     * 站务系统- 概况-消息通知-总部消息-消息分类筛选项获取
     * @return
     */
    @GetMapping("/category/station/headOfficeMessageType")
    List<CmsCategoryDTO>  getHeadOfficeMessageType();

    @PostMapping(value="/content/station/console/headOfficeMessage/{pageSize}/{pageNum}",consumes = MediaType.APPLICATION_JSON)
	PageVO<ContentDTO> getHeadOfficeMessageList(@PathVariable("pageNum") Integer pageNum,
                             @PathVariable("pageSize") Integer pageSize,@RequestBody HeadOfficeMessageQuery query);

}
