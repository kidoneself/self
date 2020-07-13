package com.yimao.cloud.app.feign;

import com.yimao.cloud.base.constant.Constant;
import com.yimao.cloud.pojo.dto.cms.BannerDTO;
import com.yimao.cloud.pojo.dto.cms.CardDTO;
import com.yimao.cloud.pojo.dto.cms.CmsCategoryDTO;
import com.yimao.cloud.pojo.dto.cms.CommentDTO;
import com.yimao.cloud.pojo.dto.cms.ContentDTO;
import com.yimao.cloud.pojo.dto.cms.LiveDTO;
import com.yimao.cloud.pojo.dto.cms.VideoDTO;
import com.yimao.cloud.pojo.dto.cms.VideoTypeDTO;
import com.yimao.cloud.pojo.vo.PageVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

/**
 * @auther: liu.lin
 * @date: 2019/1/22
 */
@FeignClient(name = Constant.MICROSERVICE_CMS)
public interface CmsFeign {

    /**
     * 保存视频信息
     *
     * @param dto
     * @return
     * @author liulin
     */
    @PostMapping("/live")
    Object cmsLiveSave(@RequestBody LiveDTO dto);


    /**
     * 更新视频
     *
     * @param dto
     * @return
     * @author liulin
     */
    @PutMapping("/live")
    Object cmsLiveUpdate(@RequestBody LiveDTO dto);


    /**
     * 批量更新视频状态
     *
     * @param liveIds
     * @param dto
     * @return
     * @author liulin
     */
    @PatchMapping("/live")
    Object cmsLiveBatchUpdate(@RequestParam("liveIds") List<Integer> liveIds, @RequestBody LiveDTO dto);


    /**
     * 视频分页查询
     *
     * @param pageNum
     * @param pageSize
     * @param title
     * @param platform
     * @param categoryId
     * @param parentCategoryId
     * @param recommend
     * @param status
     * @return
     * @author liulin
     */
    @GetMapping("/live/{pageNum}/{pageSize}")
    Object cmsLiveFindPage(@PathVariable("pageNum") Integer pageNum,
                           @PathVariable("pageSize") Integer pageSize,
                           @RequestParam("title") String title,
                           @RequestParam("platform") Integer platform,
                           @RequestParam("categoryId") Integer categoryId,
                           @RequestParam("parentCategoryId") Integer parentCategoryId,
                           @RequestParam("recommend") Integer recommend,
                           @RequestParam("status") Integer status);

    /**
     * 视频单个查询
     *
     * @param liveId
     * @return
     * @author liulin
     */
    @GetMapping("/live/{liveId}")
    Object cmsLiveFindById(@PathVariable("liveId") Integer liveId);


    /**
     * 资讯保存
     *
     * @param dto
     * @return
     * @author liulin
     */
    @PostMapping("/content")
    Object cmsContentSave(@RequestBody ContentDTO dto);


    /**
     * 资讯更新
     *
     * @param dto
     * @return
     * @author liulin
     */
    @PutMapping("/content")
    Object cmsContentUpdate(@RequestBody ContentDTO dto);


    /**
     * 内容资讯批量更新
     *
     * @param contentIds
     * @param dto
     * @return
     * @author liulin
     */
    @PatchMapping("/content")
    Object cmsContentBatchUpdate(@RequestParam("contentIds") List<Integer> contentIds, @RequestBody ContentDTO dto);


    /**
     * 资讯单个查询
     *
     * @param id
     * @return
     * @author liulin
     */
    @GetMapping("/content/{contentId}")
    Object cmsContentFindById(@PathVariable("contentId") Integer id);


    /**
     * 添加评论信息
     *
     * @param dto
     * @return
     * @author liulin
     */
    @PostMapping("/comment")
    Object cmsCommentAdd(@RequestBody CommentDTO dto);


    /**
     * 评论更新
     *
     * @param dto
     * @return
     * @author liulin
     */
    @PutMapping("/comment")
    Object cmsCommentUpdate(@RequestBody CommentDTO dto);

    /**
     * 内容评论 单个查询
     *
     * @param commentId
     * @return
     * @author liulin
     */
    @GetMapping("/comment/{commentId}")
    Object cmsCommentFindById(@PathVariable("commentId") Integer commentId);

    /**
     * 内容评论 分页查询
     *
     * @param pageNum
     * @param pageSize
     * @return
     * @author liulin
     */
    @GetMapping("/comment/{pageNum}/{pageSize}")
    Object cmsCommentFindPage(@PathVariable("pageNum") Integer pageNum, @PathVariable("pageSize") Integer pageSize);


    /**
     * 内容分类保存
     *
     * @param dto
     * @return
     * @author liulin
     */
    @PostMapping("/category")
    Object cmsCategorySave(@RequestBody CmsCategoryDTO dto);

    /**
     * 内容分类删除
     *
     * @param categoryId
     * @return
     * @author liulin
     */
    @DeleteMapping("/category/{categoryId}")
    Object cmsCategoryDelete(@RequestParam("categoryId") Integer categoryId);

    /**
     * 内容分类更新
     *
     * @param dto
     * @return
     * @author liulin
     */
    @PutMapping("/category")
    Object cmsCategoryUpdate(@RequestBody CmsCategoryDTO dto);


    /**
     * 内容分类单个查询
     *
     * @param id
     * @return
     * @author liulin
     */
    @GetMapping("/category/{id}")
    Object cmsCategoryFindById(@PathVariable("id") Integer id);


    /**
     * 产品分类分页查询
     *
     * @param pageNum
     * @param pageSize
     * @param platform
     * @param type
     * @param leve1CategoryId
     * @return
     * @author liulin
     */
    @GetMapping("/category/{pageNum}/{pageSize}")
    Object cmsCategoryFindPage(@PathVariable("pageNum") Integer pageNum,
                               @PathVariable("pageSize") Integer pageSize,
                               @RequestParam("platform") Integer platform,
                               @RequestParam("type") Integer type,
                               @RequestParam("leve1CategoryId") Integer leve1CategoryId);


    /**
     * 添加代言卡/宣传卡
     *
     * @param card
     * @return
     * @author liulin
     */
    @PostMapping("/card")
    Object cmsCardAdd(@RequestBody CardDTO card);

    /**
     * 批量更新card信息
     *
     * @param cardIds
     * @param card
     * @return
     * @author liulin
     */
    @PatchMapping("/card")
    Object cmsCardbatchUpdate(@RequestParam("cardIds") List<Integer> cardIds, @RequestBody CardDTO card);


    /**
     * 更新card信息
     *
     * @param card
     * @return
     * @author liulin
     */
    @PutMapping("/card")
    Object cmsCardUpdate(@RequestBody CardDTO card);

    /**
     * 单个查询crad 信息
     *
     * @param cardId
     * @return
     * @author liulin
     */
    @GetMapping("/card/{cardId}")
    Object cmsCardFindById(@PathVariable("cardId") Integer cardId);

    /**
     * 分页查询card信息
     *
     * @param pageNum
     * @param pageSize
     * @param status
     * @param typeCode
     * @param title
     * @return
     * @author liulin
     */
    @GetMapping("/card/{pageNum}/{pageSize}")
    Object cmsCardFindPage(@PathVariable("pageNum") Integer pageNum,
                           @PathVariable("pageSize") Integer pageSize,
                           @RequestParam("status") Integer status,
                           @RequestParam("typeCode") Integer typeCode,
                           @RequestParam("title") String title);

    /**
     * 保存广告信息
     *
     * @param dto
     * @return
     */
    @PostMapping("/banner")
    Object cmsBannerSave(@RequestBody BannerDTO dto);

    /**
     * 批量更新广告状态
     *
     * @param bannerIds
     * @param status
     * @return
     */
    @PatchMapping("/banner/batchUpdate")
    Object cmsBannerbatchUpdate(@RequestParam("bannerIds") String bannerIds, @RequestParam("status") Integer status);

    /**
     * 更新广告信息
     *
     * @param bannerDTO
     * @return
     */
    @PutMapping("/banner")
    Object cmsBannerUpdate(@RequestBody BannerDTO bannerDTO);
//
//    /**
//     * 分页查询 广告信息
//     *
//     * @param pageNum
//     * @param pageSize
//     * @param terminal
//     * @param positionCode
//     * @param title
//     * @param status
//     * @return
//     */
//    @GetMapping("/banner/{pageNum}/{pageSize}")
//    Object cmsBannerFindPage(@PathVariable("pageNum") Integer pageNum,
//                             @PathVariable("pageSize") Integer pageSize,
//                             @RequestParam("terminal") Integer terminal,
//                             @RequestParam("positionCode") Integer positionCode,
//                             @RequestParam("title") String title,
//                             @RequestParam("status") Integer status);


    /**
     * 广告单个查询
     *
     * @param bannerId
     * @return
     */
    @GetMapping("/banner/{bannerId}")
    Object cmsBannerFindById(@PathVariable("bannerId") Integer bannerId);

    /**
     * ==============================新============================*/


    /**
     * 翼猫协议列表
     *
     * @param pageNum
     * @param pageSize
     * @param platform
     * @param categoryId
     * @param status
     * @param type
     * @return
     */
    @RequestMapping(value = "/content/{pageNum}/{pageSize}", method = RequestMethod.GET)
    PageVO<ContentDTO> getAgreementList(@PathVariable(value = "pageNum") Integer pageNum,
                                        @PathVariable(value = "pageSize") Integer pageSize,
                                        @RequestParam(value = "platform", required = false) Integer platform,
                                        @RequestParam(value = "parentCategoryId", required = false) Integer parentCategoryId,
                                        @RequestParam(value = "status", required = false) Integer status,
                                        @RequestParam(value = "type", required = false) Integer type,
                                        @RequestParam(value = "contentMode", required = false) Integer contentMode,
                                        @RequestParam(value = "location") Integer location,
                                        @RequestParam(value = "userId", required = false) Integer userId);


    @PostMapping(value = "/content/category", consumes = MediaType.APPLICATION_JSON_VALUE)
    List<CmsCategoryDTO> getCategory(@RequestBody CmsCategoryDTO cmsCategory);


    /**
     * banner轮播
     *
     * @param pageNum
     * @param pageSize
     * @param terminal
     * @param positionCode
     * @param title
     * @param status
     * @return
     */
    @RequestMapping(value = "/banner/{pageNum}/{pageSize}", method = RequestMethod.GET)
    PageVO<BannerDTO> findBannerPage(@PathVariable(value = "pageNum") Integer pageNum,
                                     @PathVariable(value = "pageSize") Integer pageSize,
                                     @RequestParam(value = "terminal", required = false) Integer terminal,
                                     @RequestParam(value = "positionCode", required = false) String positionCode,
                                     @RequestParam(value = "title", required = false) String title,
                                     @RequestParam(value = "status", required = false) Integer status);

    /**
     * 获取视频所有分类
     *
     * @return java.util.List<com.yimao.cloud.pojo.dto.cms.VideoTypeDTO>
     * @Author lizhiqiang
     * @Date 2019-07-30
     * @Param []
     */
    @RequestMapping(value = "/video/first/type", method = RequestMethod.GET)
    List<VideoTypeDTO> getVideoCategory();

    @RequestMapping(value = "/video/{pageNum}/{pageSize}", method = RequestMethod.GET)
    PageVO<VideoDTO> listVideo(@RequestParam(value = "liveId") Integer liveId,
                               @RequestParam(value = "liveTypeId") Integer liveTypeId,
                               @RequestParam(value = "liveTypeSubId") Integer liveTypeSubId,
                               @RequestParam(value = "recommend") Integer recommend,
                               @RequestParam(value = "liveStatus") Integer liveStatus,
                               @RequestParam(value = "deleteFlag") Integer deleteFlag,
                               @RequestParam(value = "platform") Integer platform,
                               @RequestParam(value = "status") Integer status,
                               @PathVariable(value = "pageNum") Integer pageNum,
                               @PathVariable(value = "pageSize") Integer pageSize);

    @RequestMapping(value = "/user/read", method = RequestMethod.PUT)
    void userRead(@RequestParam(value = "contentId") Integer contentId, @RequestParam(value = "type") Integer type);

    @RequestMapping(value = "/user/read/count", method = RequestMethod.GET)
    Map<String, Integer> userReadCount();

    // @RequestMapping(value = "/content/system/{pageNum}/{pageSize}", method = RequestMethod.GET)
    // PageVO<ContentDTO> systemNews(@PathVariable(value = "pageNum") Integer pageNum, @PathVariable(value = "pageSize") Integer pageSize, @RequestParam(value = "parentCategoryId") Integer parentCategoryId);

    /**
     * 观看次数累加
     *
     * @param id 视频id
     */
    @RequestMapping(value = "/video/watch/{id}", method = RequestMethod.GET)
    Object videoClickWatch(@PathVariable("id") Integer id);

}
