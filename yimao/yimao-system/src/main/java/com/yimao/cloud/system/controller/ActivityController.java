package com.yimao.cloud.system.controller;

import com.yimao.cloud.base.cache.UserCache;
import com.yimao.cloud.base.exception.BadRequestException;
import com.yimao.cloud.base.utils.StringUtil;
import com.yimao.cloud.pojo.dto.system.ActivityDTO;
import com.yimao.cloud.pojo.dto.system.DictionaryDTO;
import com.yimao.cloud.system.po.Activity;
import com.yimao.cloud.system.po.Dictionary;
import com.yimao.cloud.system.service.ActivityService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * created by liuhao@yimaokeji.com
 * 2018/4/28
 */
@RestController
@Slf4j
@Api(tags = "ActivityController")
public class ActivityController {

    @Resource
    private ActivityService activityService;
    @Resource
    private UserCache userCache;

    /**
     * 新增活动
     *
     * @param dto 活动对象
     * @return object
     */
    @PostMapping(value = "/activity")
    @ApiOperation(value = "新增活动", notes = "新增活动")
    @ApiImplicitParam(name = "dto", value = "活动对象", required = true, dataType = "ActivityDTO", paramType = "body")
    public Object save(@RequestBody ActivityDTO dto) {
        Activity activity = new Activity(dto);
        activityService.saveActivity(activity, userCache.getCurrentAdminRealName());
        return ResponseEntity.noContent().build();
    }

    /**
     * 删除活动
     *
     * @param id 活动对象
     */
    @DeleteMapping(value = "/activity/{id}")
    @ApiOperation(value = "删除活动", notes = "删除活动")
    @ApiImplicitParam(name = "id", value = "活动ID", required = true, dataType = "Long", paramType = "path")
    public Object delete(@PathVariable("id") Integer id) {
        activityService.deleteActivity(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * 修改活动
     *
     * @param dto 活动对象
     * @return object
     */
    @PutMapping(value = "/activity")
    @ApiOperation(value = "修改活动", notes = "修改活动")
    @ApiImplicitParam(name = "dto", value = "活动对象", required = true, dataType = "ActivityDTO", paramType = "body")
    public Object update(@RequestBody ActivityDTO dto) {
        String creator = userCache.getCurrentAdminRealName();
        Activity activity = new Activity(dto);
        activityService.updateActivity(activity, creator);
        return ResponseEntity.noContent().build();
    }


    /**
     * 清除活动中已下架的推荐产品
     *
     * @param id 活动id
     */
    @DeleteMapping(value = "/activity/move/{id}")
    @ApiOperation(value = "清除活动中已下架的推荐产品", notes = "清除活动中已下架的推荐产品")
    @ApiImplicitParam(name = "id", value = "活动id", required = true, dataType = "Long", paramType = "path")
    public Object remove(@PathVariable(value = "id") Integer id) {
        userCache.getCurrentAdminRealName();
        activityService.removeActivity(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * 上线活动/下线活动
     *
     * @param id 活动对象
     */
    @PatchMapping(value = "/activity/online/{id}")
    @ApiOperation(value = "上线活动/下线活动", notes = "上线活动/下线活动")
    @ApiImplicitParam(name = "id", value = "活动id", required = true, dataType = "Long", paramType = "path")
    public Object outOff(@PathVariable("id") Integer id) {
        activityService.outOffActivity(id, userCache.getCurrentAdminRealName());
        return ResponseEntity.noContent().build();
    }

    /**
     * 根据活动id查询活动
     *
     * @param id 活动ID
     * @return ActivityDTO
     */
    @GetMapping(value = "/activity/{id}")
    @ApiOperation(value = "根据id查询活动", notes = "根据id查询活动")
    @ApiImplicitParam(name = "id", value = "活动id", required = true, dataType = "long", paramType = "path")
    public Object activityById(@PathVariable("id") Integer id) {
        Activity activity = activityService.activityById(id);
        ActivityDTO dto = new ActivityDTO();
        activity.convert(dto);
        return ResponseEntity.ok(dto);
    }

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
            @ApiImplicitParam(name = "side", value = "端", required = true, dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "acType", value = "活动类型", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "title", value = "标题", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "deleteFlag", value = "是否发布", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "pageNum", value = "页码", required = true, dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "pageSize", value = "每页大小", required = true, dataType = "Long", paramType = "path")
    })
    public Object listActivity(@RequestParam(value = "side", required = false) Integer side,
                               @RequestParam(value = "acType", required = false) Integer acType,
                               @RequestParam(value = "title", required = false) String title,
                               @RequestParam(value = "deleteFlag", required = false) Integer deleteFlag,
                               @PathVariable(value = "pageNum") Integer pageNum,
                               @PathVariable(value = "pageSize") Integer pageSize) {
        return ResponseEntity.ok(activityService.listActivity(side, acType, title, deleteFlag, pageNum, pageSize));
    }

    /**
     * 修改字典
     *
     * @return
     */
    @PostMapping("/activity/dictionary/update")
    @ApiOperation(value = "活动设置")
    @ApiImplicitParam(name = "dto", value = "字典", dataType = "Dictionary", paramType = "body")
    public Object updateDictionary(@RequestBody DictionaryDTO dictionaryDTO) {
        if (dictionaryDTO == null || dictionaryDTO.getId() == null) {
            throw new BadRequestException("操作对象不存在");
        }
        if (dictionaryDTO.getCode() == null || StringUtil.isEmpty(dictionaryDTO.getGroupCode()) || StringUtil.isEmpty(dictionaryDTO.getName())) {
            throw new BadRequestException("字典代码不能为空");
        }
        Dictionary dictionary = new Dictionary(dictionaryDTO);
        activityService.updateDictionary(dictionary);
        return ResponseEntity.noContent().build();
    }

}
