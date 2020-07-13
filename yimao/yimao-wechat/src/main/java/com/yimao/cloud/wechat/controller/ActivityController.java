package com.yimao.cloud.wechat.controller;

import com.yimao.cloud.pojo.dto.system.ActivityDTO;
import com.yimao.cloud.wechat.feign.SystemFeign;
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

/**
 * 活动兑换列表
 *
 * @Author lizhiqiang
 * @Date 2019/4/2
 * @Param
 * @return
 */
@RestController
@Slf4j
@Api(tags = {"ActivityController"})
public class ActivityController {

    @Resource
    private SystemFeign systemFeign;

    /**
     * app:获取所有活动
     *
     * @param acType     1：普通活动  2：京东兑换活动
     * @param side       端 1-公众号  2-小程序
     * @param title      标题
     * @param deleteFlag 是否发布 1:是  0：否
     */
    @GetMapping(value = "/activity/{pageNum}/{pageSize}")
    @ApiOperation(value = "分页查询所有活动", notes = "分页查询所有活动")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "side", value = "端 1-公众号 2-小程序", defaultValue = "1", required = true, dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "acType", value = "活动类型 1:普通活动  2:计时兑换hra活动", dataType = "Long", defaultValue = "2", paramType = "query"),
            @ApiImplicitParam(name = "title", value = "标题", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "deleteFlag", value = "是否发布  1：是  0：否", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "pageNum", value = "页码", required = true, defaultValue = "1", dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "pageSize", value = "每页大小", required = true, dataType = "Long", defaultValue = "10", paramType = "path")
    })
    public Object listActivity(@RequestParam(value = "side", required = false) Integer side,
                               @RequestParam(value = "acType", required = false) Integer acType,
                               @RequestParam(value = "title", required = false) String title,
                               @RequestParam(value = "deleteFlag", required = false) Integer deleteFlag,
                               @PathVariable(value = "pageNum") Integer pageNum,
                               @PathVariable(value = "pageSize") Integer pageSize) {
        return ResponseEntity.ok(systemFeign.listActivity(side, acType, title, deleteFlag, pageNum, pageSize));
    }

    /**
     * app:根据活动id查询活动
     *
     * @param id 活动ID
     * @return activity
     */
    @GetMapping(value = "/activity/{id}")
    @ApiOperation(value = "根据id查询活动", notes = "根据id查询活动")
    @ApiImplicitParam(name = "id", value = "活动id", required = true, dataType = "Long", paramType = "path")
    public Object activityById(@PathVariable("id") Integer id) {
        ActivityDTO dto = systemFeign.activityById(id);
        return ResponseEntity.ok(dto);
    }

}
