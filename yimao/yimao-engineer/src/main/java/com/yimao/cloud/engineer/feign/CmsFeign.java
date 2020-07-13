package com.yimao.cloud.engineer.feign;

import com.yimao.cloud.base.constant.Constant;
import com.yimao.cloud.pojo.dto.cms.*;
import com.yimao.cloud.pojo.vo.PageVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @auther: liu.Long jie
 * @date: 2019/12/18
 */
@FeignClient(name = Constant.MICROSERVICE_CMS)
public interface CmsFeign {

    @GetMapping("/banner/engineerApp/imgae/list")
    List<BannerDTO> getEngineerAppImage();

    @RequestMapping(value = "/content/engineer/noticeMessage/{pageSize}/{pageNum}", method = RequestMethod.GET)
    PageVO<ContentDTO> getNoticeMessage(@PathVariable("pageNum") Integer pageNum, @PathVariable("pageSize") Integer pageSize);

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
     * 更新公告状态
     * @param id
     * @return
     */
    @PatchMapping("/engineer/notice/read/{id}")
    Object updateContentToRead(@PathVariable(value = "id") Integer id);


    /**
     * 删除公告
     * @param ids
     * @return
     */
    @DeleteMapping(value = {"/engineer/notice/{ids}/batch"})
    Object deleteNotice(@PathVariable("ids") String ids);

}
