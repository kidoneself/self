package com.yimao.cloud.system.controller;

import com.yimao.cloud.base.auth.JWTInfo;
import com.yimao.cloud.base.cache.UserCache;
import com.yimao.cloud.base.exception.BadRequestException;
import com.yimao.cloud.base.exception.YimaoException;
import com.yimao.cloud.base.utils.SFTPUtil;
import com.yimao.cloud.pojo.dto.system.AdvertDTO;
import com.yimao.cloud.pojo.vo.PageVO;
import com.yimao.cloud.system.po.Advert;
import com.yimao.cloud.system.service.AdvertService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;
import java.util.Date;

/**
 * 广告
 *
 * @author KID
 * @date 2018/11/12
 */
@RestController
@Slf4j
@Api(tags = "AdvertController")
public class AdvertController {

    @Resource
    private AdvertService advertService;
    @Resource
    private UserCache userCache;

    /**
     * 添加广告
     *
     * @param dto
     * @return
     */
    @PostMapping(value = {"/advert"})
    @ApiOperation(value = "添加广告")
    public Object save(@RequestBody AdvertDTO dto) {
        Advert advert = new Advert(dto);
        JWTInfo jwtInfo = userCache.getJWTInfo();
        advert.setCreator(jwtInfo.getRealName());
        Advert ad = advertService.saveAdvert(advert);
        ad.convert(dto);
        return ResponseEntity.ok(dto);
    }

    /**
     * 删除广告
     *
     * @param id
     * @return
     */
    @DeleteMapping(value = "/advert/{id}")
    @ApiOperation(value = "删除广告")
    public Object delete(@PathVariable("id") Integer id) {
        Advert advert = advertService.queryAdvert(id);
        advertService.removeAdvert(advert);
        return ResponseEntity.noContent().build();
    }

    /**
     * 更新广告
     *
     * @param dto
     * @return
     */
    @PutMapping(value = {"/advert"})
    @ApiOperation(value = "更新广告")
    public Object update(@RequestBody AdvertDTO dto) {
        JWTInfo jwtInfo = userCache.getJWTInfo();
        Advert advert = new Advert(dto);
        advert.setUpdateTime(new Date());
        advert.setUpdater(jwtInfo.getRealName());
        Advert ad = advertService.updateAdvert(advert);
        ad.convert(dto);
        return ResponseEntity.ok(dto);
    }

    /**
     * 广告 上线/下线
     *
     * @param id
     * @return
     */
    @PatchMapping(value = {"/advert/{id}"})
    @ApiOperation(value = " 广告 上线/下线")
    @ApiImplicitParam(name = "id", value = "id", required = true, dataType = "Long", paramType = "path")
    public Object updateAdvertState(@PathVariable(required = false) Integer id) {
        Advert advert = advertService.updateAdvertState(id);
        AdvertDTO dto = new AdvertDTO();
        advert.convert(dto);
        return ResponseEntity.ok(dto);
    }

    /**
     * 上传广告图
     *
     * @param request
     * @return
     */
    @PostMapping(value = "/advert/image")
    @ApiOperation(value = "上传广告图")
    public Object uploadAdImage(@RequestParam MultipartFile image, HttpServletRequest request) throws Exception {
        Part part = request.getPart("image");
        if (part != null) {
            long fileSize = part.getSize();
            if (fileSize > 2 * 1024 * 1024) {
                throw new BadRequestException("文件大小不能超过2M。");
            }
            String url = SFTPUtil.upload(part.getInputStream(), "adImages", null, null);
            if (StringUtils.isNotEmpty(url)) {
                return url;
            }
            throw new YimaoException("操作失败。");
        }
        throw new BadRequestException("请选择图片。");
    }

    /**
     * 获取广告列表
     *
     * @param apId
     * @param title
     * @param conditions
     * @param pageNum
     * @param pageSize
     * @return
     */
    @GetMapping(value = {"/advert/{pageNum}/{pageSize}"})
    @ApiOperation(value = "获取广告列表", notes = "获取广告列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "apId", value = "广告id", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "title", value = "活动名称", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "conditions", value = "情况", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "pageNum", value = "页码", required = true, dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "pageSize", value = "每页大小", required = true, dataType = "Long", paramType = "path")
    })
    public Object list(@RequestParam(value = "apId", required = false) Integer apId,
                       @RequestParam(value = "title", required = false) String title,
                       @RequestParam(value = "conditions", required = false) Integer conditions,
                       @PathVariable(value = "pageNum") Integer pageNum,
                       @PathVariable(value = "pageSize") Integer pageSize) {
        PageVO<AdvertDTO> advertDTOPageVO = advertService.listAdvert(apId, title, conditions, pageNum, pageSize);
        return ResponseEntity.ok(advertDTOPageVO);
    }

}