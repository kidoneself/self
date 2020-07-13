package com.yimao.cloud.wechat.controller;

import com.yimao.cloud.pojo.dto.cms.LiveDTO;
import com.yimao.cloud.pojo.vo.PageVO;
import com.yimao.cloud.wechat.feign.CmsFeign;
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

/**
 * @author Lizhqiang
 * @date 2019/4/4
 */
@Slf4j
@RestController
public class LiveController {

    @Resource
    private CmsFeign cmsFeign;

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
            @ApiImplicitParam(name = "status", value = "状态 是否删除：1已发布 2未发布，3已删除 ,", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "serviceStationId", value = "站点ID", paramType = "query", dataType = "Long")})
    public Object findPage(@PathVariable("pageNum") Integer pageNum,
                           @PathVariable("pageSize") Integer pageSize,
                           @RequestParam(value = "title", required = false) String title,
                           @RequestParam(value = "platform", required = false) Integer platform,
                           @RequestParam(value = "categoryId", required = false) Integer categoryId,
                           @RequestParam(value = "parentCategoryId", required = false) Integer parentCategoryId,
                           @RequestParam(value = "recommend", required = false) Integer recommend,
                           @RequestParam(value = "status", required = false) Integer status,
                           @RequestParam(value = "serviceStationId", required = false) Integer serviceStationId) {
        PageVO page = cmsFeign.findLivePage(pageNum, pageSize, title, platform, categoryId, parentCategoryId, recommend, status, serviceStationId);
        return ResponseEntity.ok(page);
    }
}
