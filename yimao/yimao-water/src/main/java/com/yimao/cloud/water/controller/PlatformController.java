package com.yimao.cloud.water.controller;

import com.yimao.cloud.pojo.dto.water.PlatformDTO;
import com.yimao.cloud.pojo.vo.PageVO;
import com.yimao.cloud.water.po.Platform;
import com.yimao.cloud.water.service.PlatformService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * 描述：第三方广告平台。
 *
 * @Author Zhang Bo
 * @Date 2019/1/30 14:36
 * @Version 1.0
 */
@RestController
@Api(tags = "PlatformController")
@Slf4j
public class PlatformController {

    @Resource
    private PlatformService platformService;

    /**
     * 创建第三方广告平台
     *
     * @param dto 广告平台信息
     * @return
     */
    @PostMapping(value = "/platform")
    @ApiOperation(value = "创建第三方广告平台", notes = "创建第三方广告平台")
    @ApiImplicitParam(name = "dto", value = "广告平台信息", required = true, dataType = "PlatformDTO", paramType = "body")
    public Object save(@RequestBody PlatformDTO dto) {
        Platform platform = new Platform(dto);
        platformService.save(platform);
        return ResponseEntity.noContent().build();
    }

    /**
     * 更新第三方广告平台
     *
     * @param dto 广告平台信息
     * @return
     */
    @PutMapping(value = "/platform")
    @ApiOperation(value = "更新第三方广告平台", notes = "更新第三方广告平台")
    @ApiImplicitParam(name = "dto", value = "广告平台信息", required = true, dataType = "PlatformDTO", paramType = "body")
    public Object update(@RequestBody PlatformDTO dto) {
        Platform platform = new Platform(dto);
        platformService.update(platform);
        return ResponseEntity.noContent().build();
    }

    /**
     * 删除第三方平台
     *
     * @param id
     * @return
     */
    @DeleteMapping(value = {"/platform/{id}"})
    @ApiOperation(value = "根据ID删除第三方平台", notes = "根据ID删除第三方平台")
    @ApiImplicitParam(name = "id", value = "平台ID", dataType = "Long", required = true, paramType = "path")
    public Object deletePlatform(@PathVariable("id") Integer id) {
        Platform platform=new Platform();
        platform.setId(id);
        platformService.deletePlatform(platform);
        return ResponseEntity.noContent().build();
    }


    /**
     * 分页查询第三方广告平台
     *
     * @param pageNum  页码
     * @param pageSize 每页显示数量
     * @return
     */
    @GetMapping(value = "/platform/{pageNum}/{pageSize}")
    @ApiOperation(value = "分页查询第三方广告平台", notes = "分页查询第三方广告平台")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "页码", required = true, defaultValue = "1", dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "pageSize", value = "每页显示数量", required = true, defaultValue = "10", dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "forbidden", value = "是否第三方,1:是,2:否", dataType = "Long", paramType = "query")
    })
    public Object page(@PathVariable(value = "pageNum") Integer pageNum,
                       @PathVariable(value = "pageSize") Integer pageSize,
                       @RequestParam(name = "forbidden", required = false) Integer forbidden
                       ) {
        PageVO<PlatformDTO> page = platformService.page(pageNum, pageSize,forbidden);
        return ResponseEntity.ok(page);
    }


    @GetMapping(value = "/platform/{id}")
    @ApiOperation(value = "通过平台ID查询详情", notes = "通过平台ID查询详情")
    @ApiImplicitParam(name = "id", value = "平台ID", required = true, dataType = "Long", paramType = "path")
    public Object getById(@PathVariable(value = "id") Integer id) {
        PlatformDTO platformDTO = platformService.getById(id);
        return ResponseEntity.ok(platformDTO);
    }


}
