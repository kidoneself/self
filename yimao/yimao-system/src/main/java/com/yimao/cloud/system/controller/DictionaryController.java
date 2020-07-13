package com.yimao.cloud.system.controller;

import com.yimao.cloud.base.exception.BadRequestException;
import com.yimao.cloud.pojo.dto.system.DictionaryDTO;
import com.yimao.cloud.pojo.vo.PageVO;
import com.yimao.cloud.system.po.Dictionary;
import com.yimao.cloud.system.service.DictionaryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * 字典
 *
 * @author Zhang Bo
 * @date 2019/3/21.
 */
@RestController
@Slf4j
@Api(tags = "DictionaryController")
public class DictionaryController {

    @Resource
    private DictionaryService dictionaryService;

    /**
     * 添加字典
     *
     * @param dto 字典
     */
    @PostMapping(value = "/dictionary")
    @ApiOperation(value = "添加字典")
    @ApiImplicitParam(name = "dto", value = "字典信息", required = true, dataType = "DictionaryDTO", paramType = "body")
    public void saveDictionary(@RequestBody DictionaryDTO dto) {
        Dictionary dictionary = new Dictionary(dto);
        dictionaryService.saveDictionary(dictionary);
    }

    /**
     * 删除字典
     *
     * @param id 字典ID
     */
    @DeleteMapping(value = "/dictionary/{id}")
    @ApiOperation(value = "删除字典")
    @ApiImplicitParam(name = "id", value = "字典ID", required = true, dataType = "Long", paramType = "path")
    public void deleteDictionary(@PathVariable Integer id) {
        dictionaryService.deleteDictionary(id);
    }

    /**
     * 修改字典
     *
     * @param dto 字典
     */
    @PutMapping(value = "/dictionary")
    @ApiOperation(value = "修改字典")
    @ApiImplicitParam(name = "dto", value = "字典信息", required = true, dataType = "DictionaryDTO", paramType = "body")
    public void updateDictionary(@RequestBody DictionaryDTO dto) {
        if (dto.getId() == null) {
            throw new BadRequestException("修改对象ID不能为空。");
        } else {
            Dictionary dictionary = new Dictionary(dto);
            dictionaryService.updateDictionary(dictionary);
        }
    }

    /**
     * 查询字典数据（所有符合条件的数据）
     *
     * @param name      字典名称
     * @param code      CODE
     * @param groupCode 分组CODE
     * @param pid       父级ID
     */
    @GetMapping(value = "/dictionary")
    @ApiOperation(value = "查询字典数据（所有符合条件的数据）")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "name", value = "字典名称", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "code", value = "CODE", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "groupCode", value = "分组CODE", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "pid", value = "父级ID", dataType = "Long", paramType = "query"),
    })
    public List<DictionaryDTO> listDictionary(@RequestParam(required = false) String name,
                                              @RequestParam(required = false) String code,
                                              @RequestParam(required = false) String groupCode,
                                              @RequestParam(required = false) Integer pid) {
        PageVO<DictionaryDTO> pageVO = dictionaryService.pageDictionary(0, 1000, name, code, groupCode, pid);
        return pageVO.getResult();
    }

    /**
     * 查询字典数据（分页）
     *
     * @param pageNum   页码
     * @param pageSize  每页大小
     * @param name      字典名称
     * @param code      CODE
     * @param groupCode 分组CODE
     * @param pid       父级ID
     */
    @GetMapping(value = "/dictionary/{pageNum}/{pageSize}")
    @ApiOperation(value = "查询字典数据（分页）")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "页码", dataType = "Long", required = true, paramType = "path"),
            @ApiImplicitParam(name = "pageSize", value = "每页大小", dataType = "Long", required = true, paramType = "path"),
            @ApiImplicitParam(name = "name", value = "字典名称", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "code", value = "CODE", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "groupCode", value = "分组CODE", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "pid", value = "父级ID", dataType = "Long", paramType = "query")
    })
    public PageVO<DictionaryDTO> listDictionary(@PathVariable("pageNum") Integer pageNum,
                                                @PathVariable("pageSize") Integer pageSize,
                                                @RequestParam(required = false, value = "name") String name,
                                                @RequestParam(required = false, value = "code") String code,
                                                @RequestParam(required = false, value = "groupCode") String groupCode,
                                                @RequestParam(required = false, value = "pid") Integer pid) {
        return dictionaryService.pageDictionary(pageNum, pageSize, name, code, groupCode, pid);
    }

}
