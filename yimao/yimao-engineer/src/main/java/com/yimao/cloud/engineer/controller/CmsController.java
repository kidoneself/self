package com.yimao.cloud.engineer.controller;

import com.yimao.cloud.engineer.feign.CmsFeign;
import com.yimao.cloud.pojo.dto.cms.BannerDTO;
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
 * 首页banner
 *
 * @author Lizhqiang
 * @date 2020/7/2
 */


@RestController
@Api(tags = "EngineerApiController")
@Slf4j
public class CmsController {

    @Resource
    private CmsFeign cmsFeign;

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


    /**
     * 获取安装工app首页轮播图
     * @return
     */
    @GetMapping("/banner/engineerApp/imgae/list")
    @ApiOperation(value = "获取安装工app首页轮播图", notes = "获取安装工app首页轮播图")
    public List<BannerDTO> getEngineerAppImage() {
        return cmsFeign.getEngineerAppImage();
    }


}
