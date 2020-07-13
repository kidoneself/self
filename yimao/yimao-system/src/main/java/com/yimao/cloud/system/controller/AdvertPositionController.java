package com.yimao.cloud.system.controller;

import com.yimao.cloud.pojo.dto.system.AdvertPositionDTO;
import com.yimao.cloud.pojo.vo.PageVO;
import com.yimao.cloud.system.po.AdvertPosition;
import com.yimao.cloud.system.service.AdvertPositionService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 广告位
 *
 * @author KID
 * @date 2018/11/13
 */

@RestController
@Slf4j
@Api(tags = "AdvertPositionController")
public class AdvertPositionController {

    @Resource
    private AdvertPositionService advertPositionService;

    /**
     * 广告位查询
     *
     * @param pageNum
     * @param pageSize
     * @return
     */
    @GetMapping(value = {"/advert/position/{pageNum}/{pageSize}"})
    @ApiOperation(value = "获取广告位列表", notes = "获取广告位列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "页码", required = true, dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "pageSize", value = "每页大小", required = true, dataType = "Long", paramType = "path")
    })
    public Object list(@PathVariable(value = "pageNum") Integer pageNum,
                       @PathVariable(value = "pageSize") Integer pageSize) {
        PageVO<AdvertPositionDTO> page = advertPositionService.listAdvertPosition(pageNum, pageSize);
        return ResponseEntity.ok(page);
    }

    /**
     * 添加广告位
     *
     * @param dto
     * @return
     */
    @PostMapping(value = {"/advert/position"})
    @ApiOperation(value = "保存广告位信息", notes = "保存广告位信息")
    public Object save(@RequestBody AdvertPositionDTO dto) {
        AdvertPosition advertPosition = new AdvertPosition(dto);
        AdvertPosition position = advertPositionService.saveAdvertPosition(advertPosition);
        position.convert(dto);
        return ResponseEntity.ok(dto);
    }

    /**
     * 删除广告位
     *
     * @param id
     * @return
     */
    @DeleteMapping(value = {"/advert/position/{id}"})
    @ApiOperation(value = "删除广告位", notes = "删除广告位")
    public Object delete(@PathVariable Integer id) {
        advertPositionService.removeAdvertPosition(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * 广告位更新
     *
     * @param dto
     * @return
     */
    @PatchMapping(value = {"/advert/position"})
    @ApiOperation(value = "更新广告位", notes = "更新广告位")
    public Object update(@RequestBody AdvertPositionDTO dto) {
        AdvertPosition advertPosition = new AdvertPosition(dto);
        AdvertPosition position = advertPositionService.updateAdvertPosition(advertPosition);
        position.convert(dto);
        return ResponseEntity.ok(dto);
    }
}