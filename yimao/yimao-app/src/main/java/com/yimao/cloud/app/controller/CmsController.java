package com.yimao.cloud.app.controller;

import com.yimao.cloud.app.feign.CmsFeign;
import com.yimao.cloud.pojo.dto.cms.BannerDTO;
import com.yimao.cloud.pojo.dto.cms.CmsCategoryDTO;
import com.yimao.cloud.pojo.dto.cms.ContentDTO;
import com.yimao.cloud.pojo.dto.cms.VideoDTO;
import com.yimao.cloud.pojo.dto.cms.VideoTypeDTO;
import com.yimao.cloud.pojo.vo.PageVO;
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
import java.util.List;

/**
 * 功能描述:  内容管理
 *
 * @auther: liu.lin
 * @date: 2019/1/22
 */
@RestController
@Slf4j
@Api(tags = "CmsController")
public class CmsController {

    @Resource
    private CmsFeign cmsFeign;


    @GetMapping("/live/{pageNum}/{pageSize}")
    @ApiOperation(value = "分页查询", notes = "分页查询")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", required = true, value = "页码", paramType = "path", dataType = "Long"),
            @ApiImplicitParam(name = "pageSize", required = true, value = "页大小", paramType = "path", dataType = "Long"),
            @ApiImplicitParam(name = "title", value = "标题", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "platform", value = "来源 端 1 经销商app 2 微信公众号  3 小程序", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "categoryId", value = "类型ID", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "parentCategoryId", value = "父类型ID", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "recommend", value = "是否推送", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "status", value = "状态 是否删除：1已发布 2未发布，3已删除 ,", dataType = "Long", paramType = "query")})

    public Object cmsLiveFindPage(@PathVariable("pageNum") Integer pageNum,
                                  @PathVariable("pageSize") Integer pageSize,
                                  @RequestParam(value = "title", required = false) String title,
                                  @RequestParam(value = "platform", required = false) Integer platform,
                                  @RequestParam(value = "categoryId", required = false) Integer categoryId,
                                  @RequestParam(value = "parentCategoryId", required = false) Integer parentCategoryId,
                                  @RequestParam(value = "recommend", required = false) Integer recommend,
                                  @RequestParam(value = "status", required = false) Integer status) {
        return cmsFeign.cmsLiveFindPage(pageNum, pageSize, title, platform, categoryId, parentCategoryId, recommend, status);
    }


    @GetMapping("/live/{liveId}")
    @ApiOperation(value = "单个查询", notes = "单个查询")
    @ApiImplicitParam(name = "liveId", value = "单个id", paramType = "path", dataType = "Long")
    public Object cmsLiveFindById(@PathVariable("liveId") Integer liveId) {
        return cmsFeign.cmsLiveFindById(liveId);
    }


    @GetMapping("/content/{contentId}")
    @ApiOperation(value = "单个查询", notes = "单个查询接口")
    @ApiImplicitParam(name = "contentId", required = true, value = "单个查询的id", paramType = "path", dataType = "Long")
    public Object cmsContentFindById(@PathVariable("contentId") Integer id) {
        return cmsFeign.cmsContentFindById(id);
    }


    /**
     * ======================================================= 评论
     */
    @GetMapping("/cms/comment/{commentId}")
    @ApiOperation(value = "查询单个评论信息", notes = "查询单个评论信息")
    @ApiImplicitParam(name = "comentId", required = true, paramType = "path", dataType = "Long", value = "评论ID")
    public Object cmsCommentFindById(@PathVariable("commentId") Integer commentId) {
        return cmsFeign.cmsCommentFindById(commentId);
    }


    @GetMapping("/comment/{pageNum}/{pageSize}")
    @ApiOperation(value = "分页查询评论", notes = "查询单个评论信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", required = true, paramType = "path", dataType = "Long", value = "页码"),
            @ApiImplicitParam(name = "pageSize", required = true, dataType = "Long", paramType = "path", value = "页大小")
    })
    public Object cmsCommentFindPage(@PathVariable("pageNum") Integer pageNum, @PathVariable("pageSize") Integer pageSize) {
        return cmsFeign.cmsCommentFindPage(pageNum, pageSize);
    }


    /**
     * ====================================  cms 分类
     */
    @GetMapping("/category/{pageNum}/{pageSize}")
    @ApiOperation(value = "分页查询类目信息", notes = "分页查询类目信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", required = true, value = "当前页", paramType = "path", dataType = "Long"),
            @ApiImplicitParam(name = "pageSize", required = true, value = "页大小", paramType = "path", dataType = "Long"),
            @ApiImplicitParam(name = "type", value = "类别类型 ，1 总部视频分类，2 总部 文章分类，3 服务站视频分类，4 服务站文章分类 ", paramType = "query", dataType = "Long"),
            @ApiImplicitParam(name = "platform", value = "端 1 经销商app 2 微信公众号  3 小程序 4 服务站站务系统", paramType = "query", dataType = "Long"),
            @ApiImplicitParam(name = "leve1CategoryId", value = "一级类目ID", paramType = "query", dataType = "Long")
    })
    public Object cmsCategoryFindPage(@PathVariable("pageNum") Integer pageNum,
                                      @PathVariable("pageSize") Integer pageSize,
                                      @RequestParam(value = "platform", required = false) Integer platform,
                                      @RequestParam("type") Integer type,
                                      @RequestParam(value = "leve1CategoryId", required = false) Integer leve1CategoryId) {
        return cmsFeign.cmsCategoryFindPage(pageNum, pageSize, platform, type, leve1CategoryId);
    }


    /**
     * ============================================== card 代言卡和宣传卡
     */
    @GetMapping("/card/{cardId}")
    @ApiOperation(value = "查询单个卡", notes = "查询单个卡")
    @ApiImplicitParam(name = "cardId", value = "单个卡券id", required = true, dataType = "Long", paramType = "path")
    public Object cmsCardFindById(@PathVariable("cardId") Integer cardId) {
        return cmsFeign.cmsCardFindById(cardId);
    }


    @GetMapping("/card/{pageNum}/{pageSize}")
    @ApiOperation(value = "分页查询卡券列表", notes = "分页查询卡券列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", required = true, value = "当前页", paramType = "path", dataType = "Long"),
            @ApiImplicitParam(name = "pageSize", required = true, value = "页大小", paramType = "path", dataType = "Long"),
            @ApiImplicitParam(name = "status", value = "状态", paramType = "query", dataType = "Long"),
            @ApiImplicitParam(name = "typeCode", value = "卡类型", paramType = "query", dataType = "Long"),
            @ApiImplicitParam(name = "title", value = "标题", paramType = "query", dataType = "String")
    })
    public Object cmsCardFindPage(@PathVariable("pageNum") Integer pageNum,
                                  @PathVariable("pageSize") Integer pageSize,
                                  @RequestParam(value = "status", required = false) Integer status,
                                  @RequestParam(value = "typeCode", required = false) Integer typeCode,
                                  @RequestParam(value = "title", required = false) String title) {
        return cmsFeign.cmsCardFindPage(pageNum, pageSize, status, typeCode, title);
    }


//    /**
//     * ==================================== banner 广告信息
//     */
//    @GetMapping("/banner/{pageNum}/{pageSize}")
//    @ApiOperation(value = "分页查询广告信息", notes = "分页查询广告信息")
//    @ApiImplicitParams({
//            @ApiImplicitParam(name = "pageNum", value = "当前页", required = true, paramType = "path", dataType = "Long"),
//            @ApiImplicitParam(name = "pageSize", value = "页大小", required = true, paramType = "path", dataType = "Long"),
//            @ApiImplicitParam(name = "terminal", value = "1-健康e家公众号；2-小猫店小程序；3-经销商APP", required = true, paramType = "query", dataType = "Long"),
//            @ApiImplicitParam(name = "positionCode", value = "位置code", required = true, paramType = "query", dataType = "Long"),
//            @ApiImplicitParam(name = "title", value = "标题", paramType = "query", dataType = "String"),
//            @ApiImplicitParam(name = "status", value = "状态 1-可用 2-删除", paramType = "query", dataType = "Long")
//    })
//    public Object findBannerPage(@PathVariable("pageNum") Integer pageNum,
//                                 @PathVariable("pageSize") Integer pageSize,
//                                 @RequestParam("terminal") Integer terminal,
//                                 @RequestParam("positionCode") Integer positionCode,
//                                 @RequestParam(value = "title", required = false) String title,
//                                 @RequestParam(value = "status", defaultValue = "1") Integer status) {
//        return cmsFeign.cmsBannerFindPage(pageNum, pageSize, terminal, positionCode, title, status);
//    }


    /**
     * @param bannerId
     * @return
     */
    @GetMapping("/banner/{bannerId}")
    @ApiOperation(value = "单个查询广告", notes = "单个查询广告")
    @ApiImplicitParam(name = "bannerId", value = "广告ID", paramType = "path", dataType = "Long")
    public Object findBannerById(@PathVariable("bannerId") Integer bannerId) {
        return cmsFeign.cmsBannerFindById(bannerId);
    }

    /*=============================================新开发==============================================*/

    /**
     * 资讯接口---协议专用
     *
     * @param pageNum
     * @param pageSize
     * @param platform
     * @param categoryId
     * @param status
     * @param type
     * @param contentMode
     * @return
     */
    @GetMapping("/content/{pageNum}/{pageSize}")
    @ApiOperation(value = "查询内容分页信息", notes = "查询内容分页信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", required = true, value = "页码", defaultValue = "1", dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "pageSize", required = true, value = "页大小", defaultValue = "10", dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "parentCategoryId", value = "父类型ID", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "location", value = "位置：1资讯 2公号 3协议", dataType = "Long", paramType = "query", required = true),
            @ApiImplicitParam(name = "userId", value = "当前登录用户e家号", dataType = "Long", paramType = "query")
    })
    public ResponseEntity<PageVO<ContentDTO>> findPage(@PathVariable(value = "pageNum") Integer pageNum,
                                                       @PathVariable(value = "pageSize") Integer pageSize,
                                                       @RequestParam(value = "parentCategoryId", required = false) Integer parentCategoryId,
                                                       @RequestParam(value = "location") Integer location,
                                                       @RequestParam(value = "userId", required = false) Integer userId) {
        PageVO<ContentDTO> page = cmsFeign.getAgreementList(pageNum, pageSize, 1, parentCategoryId, 1, 1, null, location, userId);
        return ResponseEntity.ok(page);
    }

    /**
     * 资讯所有分类
     *
     * @return java.lang.Object
     * @Author lizhiqiang
     * @Date 2019-07-30
     * @Param [type]
     */
    @GetMapping("/content/category")
    @ApiOperation(value = "根据展示位置获取分类")
    @ApiImplicitParam(name = "location", value = "位置：1资讯 2公号 3协议", dataType = "Long", paramType = "query", required = true)
    public Object getCategory(@RequestParam(value = "location") Integer location) {
        CmsCategoryDTO cmsCategory = new CmsCategoryDTO();
        cmsCategory.setLocation(location);
        cmsCategory.setType(2);
        cmsCategory.setPlatform(1);
        return cmsFeign.getCategory(cmsCategory);
    }


    /**
     * 视频所有分类
     *
     * @return java.lang.Object
     * @Author lizhiqiang
     * @Date 2019-07-30
     * @Param [type]
     */
    @GetMapping("/video/first/type")
    @ApiOperation(value = "视频所有分类")
    public Object getVideoCategory() {
        List<VideoTypeDTO> list = cmsFeign.getVideoCategory();
        return ResponseEntity.ok(list);
    }

    @GetMapping(value = {"/video/{pageNum}/{pageSize}"})
    @ApiOperation("视频列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "liveId", value = "直播id（无需）", required = false, dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "liveTypeId", value = "分类id", required = true, dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "liveTypeSubId", value = "二级分类", required = false, dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "recommend", value = "推荐", required = false, dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "liveStatus", value = "状态", required = false, dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "deleteFlag", value = "是否删除", required = false, dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "platform", value = "展示端", required = false, dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "pageNum", value = "页", required = false, dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "pageSize", value = "每页个数", required = false, dataType = "Long", paramType = "path")
    })
    public PageVO<VideoDTO> getVideoByTypeId(@RequestParam(required = false) Integer liveId,
                                             @RequestParam(value = "liveTypeId") Integer liveTypeId,
                                             @RequestParam(required = false) Integer liveTypeSubId,
                                             @RequestParam(required = false) Integer recommend,
                                             @RequestParam(required = false) Integer liveStatus,
                                             @RequestParam(required = false) Integer deleteFlag,
                                             @RequestParam(required = false) Integer platform,
                                             @PathVariable(value = "pageNum") Integer pageNum,
                                             @PathVariable(value = "pageSize") Integer pageSize) {
        return cmsFeign.listVideo(liveId, liveTypeId, liveTypeSubId, recommend, liveStatus, 0, 3, null, pageNum, pageSize);
    }


    @GetMapping("/banner/{pageNum}/{pageSize}")
    @ApiOperation(value = "分页查询广告信息", notes = "分页查询广告信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "当前页", required = true, paramType = "path", dataType = "Long"),
            @ApiImplicitParam(name = "pageSize", value = "页大小", paramType = "path", dataType = "Long", required = true),
            @ApiImplicitParam(name = "terminal", value = "1-健康e家公众号；2-小猫店小程序；3-经销商APP", paramType = "query", dataType = "Long"),
            @ApiImplicitParam(name = "positionCode", value = "位置code", paramType = "query", dataType = "Long"),
            @ApiImplicitParam(name = "title", value = "标题", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "status", value = "状态 1-可用 2-删除", paramType = "query", dataType = "Long")
    })
    public ResponseEntity<PageVO<BannerDTO>> findBannerPage(@PathVariable("pageNum") Integer pageNum,
                                                            @PathVariable("pageSize") Integer pageSize,
                                                            @RequestParam(value = "terminal", required = false) Integer terminal,
                                                            @RequestParam(value = "positionCode", required = false) String positionCode,
                                                            @RequestParam(value = "title", required = false) String title,
                                                            @RequestParam(value = "status", required = false) Integer status) {
        PageVO<BannerDTO> pageVo = cmsFeign.findBannerPage(pageNum, pageSize, terminal, positionCode, title, 1);

        return ResponseEntity.ok(pageVo);
    }

    //总部公告=========================


    // @GetMapping("/content/system/{pageNum}/{pageSize}")
    // @ApiOperation(value = "查询内容分页信息", notes = "查询内容分页信息")
    // @ApiImplicitParams({
    //         @ApiImplicitParam(name = "pageNum", required = true, value = "页码", defaultValue = "1", dataType = "Long", paramType = "path"),
    //         @ApiImplicitParam(name = "pageSize", required = true, value = "页大小", defaultValue = "10", dataType = "Long", paramType = "path"),
    //         @ApiImplicitParam(name = "parentCategoryId", value = "父类型ID", dataType = "Long", paramType = "query")
    // })
    // public ResponseEntity<PageVO<ContentDTO>> systemNews(@PathVariable(value = "pageNum") Integer pageNum,
    //                                                      @PathVariable(value = "pageSize") Integer pageSize,
    //                                                      @RequestParam(value = "parentCategoryId", required = false) Integer parentCategoryId) {
    //     PageVO<ContentDTO> page = cmsFeign.systemNews(pageNum, pageSize, parentCategoryId);
    //     return ResponseEntity.ok(page);
    // }

    /**
     * 观看次数累加
     *
     * @param id 视频id
     */
    @GetMapping(value = {"/video/watch/{id}"})
    public Object videoClickWatch(@PathVariable("id") Integer id) {
        cmsFeign.videoClickWatch(id);
        return ResponseEntity.noContent().build();
    }
}
