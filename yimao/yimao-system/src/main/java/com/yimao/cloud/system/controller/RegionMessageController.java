package com.yimao.cloud.system.controller;

import com.yimao.cloud.pojo.dto.system.RegionMessageDTO;
import com.yimao.cloud.pojo.vo.PageVO;
import com.yimao.cloud.system.po.RegionMessage;
import com.yimao.cloud.system.service.RegionMessageService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author Lizhqiang
 * @date 2019/1/16
 */


@RestController
@Slf4j
@Api(tags = "RegionMessageController")
public class RegionMessageController {

    @Resource
    private RegionMessageService regionMessageService;


    /**
     * 省/市/区分页显示
     *
     * @param pageNum
     * @param pageSize
     * @param query
     * @return
     */
    @GetMapping(value = "/region/message/{pageNum}/{pageSize}")
    @ApiOperation(value = "省/市/区分页显示", notes = "省/市/区分页显示")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "分页页码", defaultValue = "1", required = true, dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "pageSize", value = "分页大小", defaultValue = "10", required = true, dataType = "Long", paramType = "path")
    })
    public ResponseEntity pageQueryRegionMessage(@PathVariable(required = true) Integer pageNum,
                                                 @PathVariable(required = true) Integer pageSize,
                                                 RegionMessageDTO query) {
        PageVO<RegionMessageDTO> page = regionMessageService.pageQueryRegionMessage(query, pageNum, pageSize);
        return ResponseEntity.ok(page);
    }


    /**
     * 更新地区信息
     *
     * @param dto 地区信息
     * @return
     */
    @PutMapping(value = "/region/message")
    @ApiOperation(value = "更新地区信息", notes = "更新地区信息")
    @ApiImplicitParam(name = "dto", value = "地区信息", required = true, dataType = "RegionMessageDTO", paramType = "body")
    public Object update(@RequestBody RegionMessageDTO dto) {
        RegionMessage regionMessage = new RegionMessage(dto);
        regionMessageService.update(regionMessage);
        return ResponseEntity.noContent().build();
    }
}
