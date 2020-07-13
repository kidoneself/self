package com.yimao.cloud.water.controller;

import com.yimao.cloud.pojo.dto.water.AdslotDTO;
import com.yimao.cloud.pojo.vo.PageVO;
import com.yimao.cloud.water.po.Adslot;
import com.yimao.cloud.water.service.AdslotService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * 描述：广告位
 *
 * @Author Zhang Bo
 * @Date 2019/1/30 14:36
 * @Version 1.0
 */
@RestController
@Api(tags = "AdslotController")
@Slf4j
public class AdslotController {

    @Resource
    private AdslotService adslotService;


    @PostMapping(value = "/adslot")
    @ApiOperation(value = "创建广告位", notes = "创建广告位")
    @ApiImplicitParam(name = "dto", value = "广告位信息", required = true, dataType = "AdslotDTO", paramType = "body")
    public Object save(@RequestBody AdslotDTO dto) {
        Adslot config = new Adslot(dto);
        adslotService.save(config);
        return ResponseEntity.noContent().build();
    }

    @PutMapping(value = "/adslot")
    @ApiOperation(value = "根据广告id更新广告信息", notes = "根据广告id更新广告信息")
    @ApiImplicitParam(name = "dto", value = "用户信息", required = true, dataType = "AdslotDTO", paramType = "body")
    public Object updateById(@RequestBody AdslotDTO dto) {
        Adslot adslot = new Adslot(dto);
        adslotService.updateById(adslot);
        return ResponseEntity.noContent().build();
    }


    @GetMapping(value = "/adslot/{pageNum}/{pageSize}")
    @ApiOperation(value = "查询广告位列表", notes = "查询广告位列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "platform", value = "第三方广告平台", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "position", value = "大小屏", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "forbidden", value = "0:启用,1:禁用", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "pageNum", value = "页码", dataType = "Long", required = true, paramType = "path"),
            @ApiImplicitParam(name = "pageSize", value = "每页显示数量", dataType = "Long", required = true, paramType = "path")
    })
    public Object queryListByCondition(@RequestParam(name = "platform", required = false) Integer platform,
                                       @RequestParam(name = "position", required = false) Integer position,
                                       @RequestParam(name = "forbidden", required = false) Integer forbidden,
                                       @PathVariable(value = "pageNum") Integer pageNum,
                                       @PathVariable(value = "pageSize") Integer pageSize) {
        PageVO<AdslotDTO> list = adslotService.queryListByCondition(platform, position, forbidden, pageNum, pageSize);
        return ResponseEntity.ok(list);
    }

    @GetMapping(value = "/adslot/name")
    @ApiOperation(value = "获取广告位名称", notes = "获取广告位名称")
    @ApiImplicitParam(name = "position", value = "屏幕位置：1-大屏；2-小屏", dataType = "Long", required = true, paramType = "query")
    public Object getAdslotName(@RequestParam(name = "position") Integer position) {
        String name = adslotService.getAdslotName(position);
        return ResponseEntity.ok(name);
    }


}
