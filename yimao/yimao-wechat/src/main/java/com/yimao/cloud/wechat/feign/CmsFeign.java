package com.yimao.cloud.wechat.feign;

import com.yimao.cloud.base.constant.Constant;
import com.yimao.cloud.pojo.dto.cms.BannerDTO;
import com.yimao.cloud.pojo.dto.cms.CardDTO;
import com.yimao.cloud.pojo.dto.cms.ContentDTO;
import com.yimao.cloud.pojo.dto.system.DictionaryDTO;
import com.yimao.cloud.pojo.vo.PageVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author Lizhqiang
 * @date 2019/4/1
 */
@FeignClient(name = Constant.MICROSERVICE_CMS)
public interface CmsFeign {

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

    /**
     * 获取代言卡
     *
     * @param pageNum
     * @param pageSize
     * @param cardType
     * @param status
     * @param typeCode
     * @param title
     * @return
     */
    @RequestMapping(value = "/card/{pageNum}/{pageSize}", method = RequestMethod.GET)
    PageVO<CardDTO> findPage(@PathVariable(value = "pageNum") Integer pageNum,
                             @PathVariable(value = "pageSize") Integer pageSize,
                             @RequestParam(value = "cardType", required = false) Integer cardType,
                             @RequestParam(value = "status", required = false) Integer status,
                             @RequestParam(value = "typeCode", required = false) String typeCode,
                             @RequestParam(value = "title", required = false) String title);

    /**
     * 查询所有卡的code
     *
     * @param groupCode 分组类型
     * @return
     */
    @GetMapping("/card/endorsement/cardCode")
    List<DictionaryDTO> findEndorsementCardCode(@RequestParam(value = "groupCode") String groupCode);

    /**
     * 分页查询视频
     *
     * @param pageNum
     * @param pageSize
     * @param title
     * @param platform
     * @param categoryId
     * @param parentCategoryId
     * @param recommend
     * @param status
     * @param serviceStationId
     * @return
     */
    @RequestMapping(value = "/live/{pageNum}/{pageSize}", method = RequestMethod.GET)
    PageVO findLivePage(@PathVariable(value = "pageNum") Integer pageNum,
                        @PathVariable(value = "pageSize") Integer pageSize,
                        @RequestParam(value = "title", required = false) String title,
                        @RequestParam(value = "platform", required = false) Integer platform,
                        @RequestParam(value = "categoryId", required = false) Integer categoryId,
                        @RequestParam(value = "parentCategoryId", required = false) Integer parentCategoryId,
                        @RequestParam(value = "recommend", required = false) Integer recommend,
                        @RequestParam(value = "status", required = false) Integer status,
                        @RequestParam(value = "serviceStationId", required = false) Integer serviceStationId);

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

    @RequestMapping(value = "/card/{cardId}", method = RequestMethod.GET)
    CardDTO queryById(@PathVariable(value = "cardId") Integer cardId);


    //================视频======================

    @GetMapping(value = {"/video/{pageNum}/{pageSize}"})
    Object list(@RequestParam(value = "liveId", required = false) Integer liveId,
                @RequestParam(value = "liveTypeId", required = false) Integer liveTypeId,
                @RequestParam(value = "liveTypeSubId", required = false) Integer liveTypeSubId,
                @RequestParam(value = "recommend", required = false) Integer recommend,
                @RequestParam(value = "liveStatus", required = false) Integer liveStatus,
                @RequestParam(value = "deleteFlag", required = false) Integer deleteFlag,
                @PathVariable(value = "pageNum") Integer pageNum,
                @PathVariable(value = "pageSize") Integer pageSize);

    /**
     * 视频分类(加上推荐分类)
     *
     * @return
     */
    @RequestMapping(value = {"/video/category"}, method = RequestMethod.GET)
    Object videoTypeList();

    /**
     * 根据视频id查询视频详情
     *
     * @param id
     * @return
     */
    @RequestMapping(value = {"/video/{id}"}, method = RequestMethod.GET)
    Object videoById(@PathVariable("id") Integer id);


    /**
     * 点赞数累加
     *
     * @param id 视频id
     */
    @RequestMapping(value = {"/video/like/{id}"}, method = RequestMethod.GET)
    Object videoClickLike(@PathVariable("id") Integer id);

    /**
     * 分享数累加
     *
     * @param id 视频id
     */
    @RequestMapping(value = {"/video/share/{id}"}, method = RequestMethod.GET)
    Object videoClickShare(@PathVariable("id") Integer id);

    /**
     * 观看次数累加
     *
     * @param id 视频id
     */
    @RequestMapping(value = {"/video/watch/{id}"}, method = RequestMethod.GET)
    Object videoClickWatch(@PathVariable("id") Integer id);

    @GetMapping(value = "/card/brand")
    Integer getBrandCardId();

}
