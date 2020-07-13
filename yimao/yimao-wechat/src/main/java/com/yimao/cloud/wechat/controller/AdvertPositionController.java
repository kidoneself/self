package com.yimao.cloud.wechat.controller;

import com.yimao.cloud.pojo.dto.system.AdvertPositionDTO;
import com.yimao.cloud.pojo.vo.PageVO;
import com.yimao.cloud.wechat.feign.SystemFeign;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
@RequestMapping
@Slf4j
@Api(tags = "AdvertPositionController")
public class AdvertPositionController {

    @Resource
    private SystemFeign systemFeign;

    /**
     * 广告位查询
     *
     * @param page
     * @param size
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
        PageVO<AdvertPositionDTO> pageList = systemFeign.listAdvertPosition(pageNum, pageSize);
        return ResponseEntity.ok(pageList);
    }

}