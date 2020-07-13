package com.yimao.cloud.cms.controller;

import com.yimao.cloud.base.cache.UserCache;
import com.yimao.cloud.base.constant.CmsConstant;
import com.yimao.cloud.base.exception.BadRequestException;
import com.yimao.cloud.base.exception.NotFoundException;
import com.yimao.cloud.base.exception.YimaoException;
import com.yimao.cloud.cms.feign.SystemFeign;
import com.yimao.cloud.cms.service.BannerService;
import com.yimao.cloud.pojo.dto.cms.BannerDTO;
import com.yimao.cloud.pojo.dto.system.DictionaryDTO;
import com.yimao.cloud.pojo.vo.PageVO;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * 广告banner
 *
 * @author liu.lin
 * @since 2018-12-29 16:00:28
 */
@RestController
public class BannerController {

    @Resource
    private BannerService bannerService;
    @Resource
    private UserCache userCache;
    @Resource
    private SystemFeign systemFeign;

    @PostMapping("/banner")
    @ApiOperation(value = "添加广告信息", notes = "添加广告信息")
    @ApiImplicitParam(name = "dto", value = "banner 信息", paramType = "body", dataType = "BannerDTO")
    public Object saveBanner(@RequestBody BannerDTO dto) {
        String realName = userCache.getCurrentAdminRealName();
        dto.setCreator(realName);
        dto.setUpdater(realName);
        Integer count = bannerService.save(dto);
        if (count > 0) {
            return ResponseEntity.ok(dto);
        }
        throw new YimaoException("保存banner 信息异常");
    }

    @PutMapping("/banner")
    @ApiOperation(value = "更新广告信息", notes = "更新广告信息")
    @ApiImplicitParam(name = "bannerDTO", value = "广告信息", paramType = "body", dataType = "BannerDTO")
    public Object updateBanner(@RequestBody BannerDTO bannerDTO) {
        String adminRealName = userCache.getCurrentAdminRealName();
        bannerDTO.setUpdateTime(new Date());
        bannerDTO.setUpdater(adminRealName);
        Integer count = bannerService.update(bannerDTO);
        if (count > 0) {
            return ResponseEntity.ok(bannerDTO);
        }
        throw new YimaoException("更新banner 信息失败");
    }


    @PatchMapping("/banner/batchUpdate")
    @ApiOperation(value = "批量更新广告信息", notes = "banner 批量更新广告信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "bannerIds", value = "广告ids集合(数组)", paramType = "query", allowMultiple = true, dataType = "Integer"),
            @ApiImplicitParam(name = "status", value = "状态码", paramType = "query", dataType = "Long")})
    public Object batchUpdate(@RequestParam("bannerIds") List<Integer> bannerIds,
                              @RequestParam("status") Integer status) {
        if (null == bannerIds) {
            throw new BadRequestException("参数异常");
        }
        String adminRealName = userCache.getCurrentAdminRealName();
        BannerDTO bannerDTO = new BannerDTO();
        bannerDTO.setUpdater(adminRealName);
        bannerDTO.setUpdateTime(new Date());
        bannerDTO.setStatus(status);
        Integer count = bannerService.batchUpdate(bannerIds, bannerDTO);
        if (count > 0) {
            return ResponseEntity.noContent().build();
        }
        throw new YimaoException("更新banner 信息失败");
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
        BannerDTO banner = new BannerDTO();
        banner.setTerminal(terminal);
        banner.setStatus(status);
        banner.setPositionCode(positionCode);
        banner.setTitle(title);
        PageVO<BannerDTO> pageVo = bannerService.findBannerPage(pageNum, pageSize, banner);
        if (pageVo != null) {
            return ResponseEntity.ok(pageVo);
        }
        throw new NotFoundException("没有找到相关记录");
    }

    @GetMapping("/banner/{bannerId}")
    @ApiOperation(value = "单个查询广告", notes = "单个查询广告")
    @ApiImplicitParam(name = "bannerId", value = "广告ID", paramType = "path", dataType = "Long")
    public ResponseEntity<BannerDTO> findBannerById(@PathVariable("bannerId") Integer bannerId) {
        BannerDTO banner = bannerService.findById(bannerId);
        return ResponseEntity.ok(banner);
    }

    @GetMapping("/allBannerCode")
    @ApiOperation(value = "获取所有广告位", notes = "获取所有广告位")
    public ResponseEntity<List<DictionaryDTO>> findUserBranchAuth() {
        PageVO<DictionaryDTO> pageVO = systemFeign.findDictionaryByType(1, 100,null,null,CmsConstant.BANNERCODE,null );
        if (pageVO != null) {
            return ResponseEntity.ok(pageVO.getResult());
        }
        throw new NotFoundException("没有找到相关记录");
    }

    /**
     * 更新广告位排序
     *
     * @param id
     * @param sorts
     * @return
     */
    @PatchMapping("/banner/{id}")
    @ApiOperation(value = "更新广告位排序", notes = "更新广告位排序")
    @ApiImplicitParams({@ApiImplicitParam(name = "id", value = "id", required = true, paramType = "path", dataType = "Long"),
            @ApiImplicitParam(name = "sorts", value = "排序", required = true, paramType = "query", dataType = "Long")}
    )
    public ResponseEntity updateBannerSorts(@PathVariable("id") Integer id, @RequestParam("sorts") Integer sorts) {
        Integer i = bannerService.updateBannerSorts(id, sorts);
        if (i < 0) {
            throw new YimaoException("更新banner排序失败");
        } else {
            return ResponseEntity.noContent().build();
        }
    }

    @GetMapping("/banner/engineerApp/imgae/list")
    @ApiOperation(value = "获取安装工app首页轮播图", notes = "获取安装工app首页轮播图")
    public ResponseEntity getEngineerAppImage() {
        List<BannerDTO> images = bannerService.getEngineerAppImage();
        return ResponseEntity.ok(images);
    }
}
