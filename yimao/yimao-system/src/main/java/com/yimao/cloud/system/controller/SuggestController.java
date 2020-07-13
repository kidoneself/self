package com.yimao.cloud.system.controller;

import com.yimao.cloud.base.exception.BadRequestException;
import com.yimao.cloud.pojo.dto.system.SuggestDTO;
import com.yimao.cloud.pojo.dto.system.SuggestTypeDTO;
import com.yimao.cloud.pojo.query.system.SuggestQuery;
import com.yimao.cloud.system.service.SuggestService;
import com.yimao.cloud.system.service.SystemFileService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@Slf4j
@Api(tags = "SuggestController")
public class SuggestController {

    @Resource
    private SuggestService suggestService;
    @Resource
    private SystemFileService systemFileService;

    @PostMapping("/suggest")
    @ApiOperation(value = "保存建议反馈", notes = "保存建议反馈")
    @ApiImplicitParam(name = "dto", value = "建议反馈信息", dataType = "SuggestDTO", paramType = "body")
    public void save(@RequestBody SuggestDTO dto) {

        suggestService.save(dto);
    }

    /**
     * 客服--建议反馈--建议列表查询
     *
     * @param pageNum  第几页
     * @param pageSize 每页大小
     * @param query    查询条件
     * @return
     */
    @RequestMapping(value = "/suggest/{pageNum}/{pageSize}", method = RequestMethod.POST)
    @ApiOperation(value = "建议列表查询", notes = "建议列表查询")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "第几页", required = true, dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "pageSize", value = "每页大小", required = true, dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "query", value = "查询条件", dataType = "SuggestQuery", paramType = "body")
    })
    public Object page(@PathVariable Integer pageNum,
                       @PathVariable Integer pageSize,
                       @RequestBody SuggestQuery query) {

        return ResponseEntity.ok(suggestService.page(query, pageNum, pageSize));
    }

    /**
     * 客服--建议反馈--回复建议
     *
     * @param suggestId
     * @param replyContent
     * @param accessory
     */
    @RequestMapping(value = "/suggest/reply", method = RequestMethod.POST)
    @ApiOperation(value = "回复建议", notes = "回复建议")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "suggestId", value = "建议反馈id", required = true, dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "replyContent", value = "回复内容", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "accessory", value = "回复附件", dataType = "String", paramType = "query"),
    })
    public void replySuggest(@RequestParam Integer suggestId,
                             @RequestParam String replyContent,
                             @RequestParam(required = false) String accessory) {

        suggestService.replySuggest(suggestId, replyContent, accessory);
    }

    /**
     * 客服--反馈设置--添加反馈类型
     *
     * @param dto
     */
    @PostMapping("/suggestType")
    @ApiOperation(value = "添加反馈类型", notes = "添加反馈类型")
    @ApiImplicitParam(name = "dto", value = "反馈类型信息", dataType = "SuggestTypeDTO", paramType = "body")
    public void saveSuggestType(@RequestBody SuggestTypeDTO dto) {

        suggestService.saveSuggestType(dto);
    }

    /**
     * 客服--反馈设置--建议类型列表查询
     *
     * @param pageNum  第几页
     * @param pageSize 每页大小
     * @return
     */
    @RequestMapping(value = "/suggestType/{pageNum}/{pageSize}", method = RequestMethod.POST)
    @ApiOperation(value = "建议类型列表查询", notes = "建议类型列表查询")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "第几页", required = true, dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "pageSize", value = "每页大小", required = true, dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "terminal", value = "展示端", dataType = "Long", paramType = "query")
    })
    public Object pageSuggestType(@PathVariable Integer pageNum,
                                  @PathVariable Integer pageSize,
                                  @RequestParam(required = false) Integer terminal) {

        return ResponseEntity.ok(suggestService.pageSuggestType(terminal, pageNum, pageSize));
    }

    /**
     * 客服--反馈设置--修改建议类型
     *
     * @param dto
     * @return
     */
    @RequestMapping(value = "/suggestType", method = RequestMethod.PUT)
    @ApiOperation(value = "修改建议类型", notes = "修改建议类型")
    @ApiImplicitParam(name = "dto", value = "建议类型信息", required = true, dataType = "SuggestTypeDTO", paramType = "body")
    public Object updateSuggestType(@RequestBody SuggestTypeDTO dto) {
        if (dto.getId() == null) {
            throw new BadRequestException("修改对象不存在。");
        } else {
            suggestService.updateSuggestType(dto);
        }
        return ResponseEntity.noContent().build();
    }

    /**
     * 客服--反馈设置--删除建议类型
     *
     * @param id 建议类型ID
     * @return
     */
    @RequestMapping(value = "/suggestType/{id}", method = RequestMethod.DELETE)
    @ApiOperation(value = "删除建议类型", notes = "删除建议类型")
    @ApiImplicitParam(name = "id", value = "建议类型ID", required = true, dataType = "Long", paramType = "path")
    public Object deleteSuggestType(@PathVariable Integer id) {
        suggestService.deleteSuggestType(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * 客服--反馈设置--获取建议类型展示端结构
     */
    @RequestMapping(value = "/suggestType/tree", method = RequestMethod.GET)
    @ApiOperation(value = "获取建议类型展示端结构", notes = "获取建议类型展示端结构")
    public List<SuggestTypeDTO> listSuggestTypeTree() {

        return suggestService.listSuggestTypeTree();
    }

    /**
     * 建议类型筛选条件下拉框
     */
    @RequestMapping(value = "/suggestType/list", method = RequestMethod.GET)
    @ApiOperation(value = "建议类型筛选条件下拉框", notes = "建议类型筛选条件下拉框")
    @ApiImplicitParam(name = "terminal", value = "展示端", required = true, dataType = "String", paramType = "query")
    public Object listSuggestType(@RequestParam Integer terminal) {

        return suggestService.listSuggestType(terminal);
    }

    /**
     * 获取附件名称
     *
     * @param path   文件路径(相对路径)
     * @param folder 文件目录
     */
    @RequestMapping(value = "/suggest/getFileName", method = RequestMethod.GET)
    @ApiOperation(value = "获取附件名称", notes = "获取附件名称")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "path", value = "文件路径(相对路径)", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "folder", value = "文件目录", required = true, dataType = "String", paramType = "query")
    })
    public Object getFileName(@RequestParam String path,
                              @RequestParam String folder) {

        return systemFileService.getFileName(path, folder);
    }

}
