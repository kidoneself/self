package com.yimao.cloud.water.controller;

import com.yimao.cloud.pojo.dto.water.ConditionalAdvertisingDTO;
import com.yimao.cloud.pojo.vo.PageVO;
import com.yimao.cloud.water.po.ConditionalAdvertising;
import com.yimao.cloud.water.service.ConditionalAdvertisingService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.Map;

/**
 * 描述：广告条件投放。
 *
 * @Author Zhang Bo
 * @Date 2019/1/30 14:43
 * @Version 1.0
 */
@RestController
@Api(tags = "ConditionalAdvertisingController")
@Slf4j
public class ConditionalAdvertisingController {

    @Resource
    private ConditionalAdvertisingService conditionalAdvertisingService;

    /**
     * 创建广告条件投放配置
     *
     * @param dto 配置
     * @return
     */
    @PostMapping(value = "/advertising")
    @ApiOperation(value = "创建广告条件投放配置", notes = "创建广告条件投放配置")
    @ApiImplicitParam(name = "dto", value = "配置", required = true, dataType = "ConditionalAdvertisingDTO", paramType = "body")
    public Object save(@RequestBody ConditionalAdvertisingDTO dto) {
        ConditionalAdvertising advertising = new ConditionalAdvertising(dto);
        conditionalAdvertisingService.save(advertising);
        return ResponseEntity.noContent().build();
    }

    /**
     * 条件投放---筛选水机
     *
     * @param dto
     * @return
     */
    @PostMapping(value = "/advertising/filter")
    @ApiOperation(value = "筛选水机", notes = "筛选水机")
    @ApiImplicitParam(name = "dto", value = "配置实体", required = true, dataType = "ConditionalAdvertisingDTO", paramType = "body")
    public Object filter(@RequestBody ConditionalAdvertisingDTO dto) {
        Map<String, Object> map = conditionalAdvertisingService.filter(dto);
        return ResponseEntity.ok(map);
    }


    /**
     * 更新广告条件投放配置
     *
     * @param dto 配置
     * @return
     */
    @PutMapping(value = "/advertising")
    @ApiOperation(value = "更新广告条件投放配置", notes = "更新广告条件投放配置")
    @ApiImplicitParam(name = "dto", value = "配置", required = true, dataType = "ConditionalAdvertisingDTO", paramType = "body")
    public Object update(@RequestBody ConditionalAdvertisingDTO dto) {
        ConditionalAdvertising advertising = new ConditionalAdvertising(dto);
        conditionalAdvertisingService.update(advertising);
        return ResponseEntity.noContent().build();
    }

    /**
     * @param platform
     * @param pageNum
     * @param pageSize
     * @return
     */
    @GetMapping("/advertising/{pageNum}/{pageSize}")
    @ApiOperation(value = "分页查询投放列表", notes = "查询投放列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "platform", value = "第三方广告平台", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "screenLocation", value = "投放位置(大屏、小屏)", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "startTime", value = "投放开始时间", dataType = "Date", paramType = "query"),
            @ApiImplicitParam(name = "endTime", value = "投放结束时间", dataType = "Date", paramType = "query"),
            @ApiImplicitParam(name = "name", value = "物料名称", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "type", value = "1:自有,2:第三方,3:自有记录", dataType = "String", required = true, paramType = "query"),
            @ApiImplicitParam(name = "pageNum", value = "页码", dataType = "Long", required = true, paramType = "path"),
            @ApiImplicitParam(name = "pageSize", value = "每页显示数量", dataType = "Long", required = true, paramType = "path")
    })
    public ResponseEntity<PageVO<ConditionalAdvertisingDTO>> page(@RequestParam(name = "platform", required = false) Integer platform,
                                                                  @RequestParam(name = "screenLocation", required = false) Integer screenLocation,
                                                                  @RequestParam(name = "startTime", required = false) Date startTime,
                                                                  @RequestParam(name = "endTime", required = false) Date endTime,
                                                                  @RequestParam(name = "name", required = false) String name,
                                                                  @RequestParam(name = "type") String type,
                                                                  @PathVariable(value = "pageNum") Integer pageNum,
                                                                  @PathVariable(value = "pageSize") Integer pageSize) {

        PageVO<ConditionalAdvertisingDTO> list = new PageVO<>();
        if (type.equals("1") || type.equals("3")) {
            list = conditionalAdvertisingService.pageOwnList(screenLocation, startTime, endTime, name, type, pageNum, pageSize);
        } else if (type.equals("2")) {
            list = conditionalAdvertisingService.page(platform, pageNum, pageSize);
        }
        return ResponseEntity.ok(list);
    }

    @GetMapping("/advertising/export/have")
    @ApiOperation(value = "导出自有投放", notes = "导出自有投放")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "platform", value = "第三方广告平台", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "screenLocation", value = "投放位置(大屏、小屏)", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "startTime", value = "投放开始时间", dataType = "Date", paramType = "query"),
            @ApiImplicitParam(name = "endTime", value = "投放结束时间", dataType = "Date", paramType = "query"),
            @ApiImplicitParam(name = "type", value = "1:自有,3:自有记录", dataType = "String", required = true, paramType = "query"),
            @ApiImplicitParam(name = "name", value = "物料名称", dataType = "String", paramType = "query")
    })
    public Object exportHave(@RequestParam(name = "platform", required = false) Integer platform,
                             @RequestParam(name = "screenLocation", required = false) Integer screenLocation,
                             @RequestParam(name = "startTime", required = false) Date startTime,
                             @RequestParam(name = "endTime", required = false) Date endTime,
                             @RequestParam(name = "name", required = false) String name,
                             @RequestParam(name = "type") String type,
                             HttpServletResponse response) {
        conditionalAdvertisingService.exportHave(platform, screenLocation, startTime, endTime, name, type, response);
        return ResponseEntity.noContent().build();
    }


    @DeleteMapping(value = {"/advertising/{id}"})
    @ApiOperation(value = "根据投放ID删除投放配置", notes = "根据投放ID删除投放配置")
    @ApiImplicitParam(name = "id", value = "投放ID", dataType = "Long", required = true, paramType = "path")
    public Object deleteConditionalAdvertising(@PathVariable("id") Integer id) {
        ConditionalAdvertising advertising = new ConditionalAdvertising();
        advertising.setId(id);
        conditionalAdvertisingService.deleteConditionalAdvertising(advertising);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping(value = {"/advertising/{id}"})
    @ApiOperation(value = "根据投放ID下架投放配置", notes = "根据投放ID下架投放配置")
    @ApiImplicitParam(name = "id", value = "投放ID", dataType = "Long", required = true, paramType = "path")
    public Object lowerAdvertising(@PathVariable("id") Integer id) {
        ConditionalAdvertising advertising = new ConditionalAdvertising();
        advertising.setId(id);
        conditionalAdvertisingService.lowerAdvertising(advertising);
        return ResponseEntity.noContent().build();
    }


    @GetMapping(value = {"/advertising/{id}"})
    @ApiOperation(value = "根据投放ID查询详情", notes = "根据投放ID查询详情")
    @ApiImplicitParam(name = "id", value = "投放ID", dataType = "Long", required = true, paramType = "path")
    public Object selectAdById(@PathVariable("id") Integer id) {
        return ResponseEntity.ok(conditionalAdvertisingService.selectAdById(id));
    }


    /**
     * 根据条件判断是否有绑定投放策略，false表示没有绑定
     *
     * @return
     */
    @GetMapping(value = "/advertising/judge")
    @ApiOperation(value = "根据条件判断是否有绑定投放策略", notes = "根据条件判断是否有绑定投放策略")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "platform", value = "第三方广告平台", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "ownAdslotId", value = "广告位编码", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "adslotId", value = "物料id", dataType = "String", paramType = "query")
    })
    public Object judgeAdvertsingExists(@RequestParam(name = "platform", required = false) Integer platform,
                                        @RequestParam(name = "ownAdslotId", required = false) String ownAdslotId,
                                        @RequestParam(name = "adslotId", required = false) String adslotId) {
        Boolean boo = conditionalAdvertisingService.judgeAdvertsingExists(platform, ownAdslotId, adslotId);
        return ResponseEntity.ok(boo);
    }


}
