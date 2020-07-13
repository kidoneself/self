package com.yimao.cloud.system.controller;

import com.yimao.cloud.pojo.dto.system.CustomerAssistantTypeDTO;
import com.yimao.cloud.pojo.dto.system.CustomerMessageDTO;
import com.yimao.cloud.pojo.vo.PageVO;
import com.yimao.cloud.system.po.CustomerAssistantType;
import com.yimao.cloud.system.service.CustomerAssistantTypeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.impl.execchain.TunnelRefusedException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author lizhiqiang
 * @date 2019/1/14
 */
@RestController
@Slf4j
@Api(tags = {"CustomerAssistantTypeController"})
public class CustomerAssistantTypeController {

    @Resource
    private CustomerAssistantTypeService customerAssistantTypeService;


    /**
     * 添加问答分类
     *
     * @param dto
     * @return
     */
    @PostMapping("/customer/classify")
    @ApiOperation(value = "添加问答分类", notes = "添加问答分类")
    @ApiImplicitParam(name = "dto", value = "问答分类", dataType = "CustomerAssistantTypeDTO", required = true, paramType = "body")
    public Object addClassify(@RequestBody CustomerAssistantTypeDTO dto) {
        CustomerAssistantType customerAssistantType = new CustomerAssistantType(dto);
        customerAssistantTypeService.saveCustomerAssistantType(customerAssistantType);
        return ResponseEntity.noContent().build();
    }


    /**
     * 获取展示端下所有分类
     *
     * @param terminal
     * @return
     */
    @GetMapping("/customer/classify/{terminal}")
    @ApiOperation(value = "展示端分类列表", notes = "展示端分类列表")
    @ApiImplicitParam(name = "terminal", value = "展示端", dataType = "Long", required = true, paramType = "path")
    public Object getClassifyList(@PathVariable(required = true) Integer terminal) {
        List<CustomerAssistantTypeDTO> classifyList = customerAssistantTypeService.getClassifyList(terminal);
        return ResponseEntity.ok(classifyList);
    }

    /**
     * 修改展示端分类
     *
     * @param
     * @return
     */
    @PutMapping("/customer/classify")
    @ApiOperation(value = "修改展示端分类", notes = "修改展示端分类")
    @ApiImplicitParam(name = "dto", value = "展示端分类", dataType = "CustomerAssistantTypeDTO", required = true, paramType = "body")
    public Object update(@RequestBody CustomerAssistantTypeDTO dto) {
        CustomerAssistantType customerAssistantType = new CustomerAssistantType(dto);
        customerAssistantTypeService.updateCustomerAssistantType(customerAssistantType);
        return ResponseEntity.ok(dto);
    }

    /**
     * 删除展示端分类
     *
     * @param id
     * @return
     */
    @DeleteMapping("/customer/classify/{id}")
    @ApiOperation(value = "删除展示端分类", notes = "删除展示端分类")
    @ApiImplicitParam(name = "id", value = "分类ID", dataType = "Long", required = true, paramType = "path")
    public Object delete(@PathVariable(required = true) Integer id) {
        customerAssistantTypeService.deleteClassifyById(id);
        return ResponseEntity.noContent().build();
    }


    /**
     * 分页显示问答分类
     *
     * @param pageNum
     * @param pageSize
     * @param terminal
     * @param typeName
     * @return
     */
    @GetMapping("/customer/classify/{pageNum}/{pageSize}")
    @ApiOperation(value = "分页显示问答分类", notes = "分页显示问答分类")
    @ApiImplicitParams({@ApiImplicitParam(name = "pageNum", value = "页码", defaultValue = "1", dataType = "Long", required = true, paramType = "path"),
            @ApiImplicitParam(name = "pageSize", value = "页数", defaultValue = "10", dataType = "Long", required = true, paramType = "path"),
            @ApiImplicitParam(name = "terminal", value = "展示端", dataType = "Long", required = false, paramType = "query"),
            @ApiImplicitParam(name = "typeName", value = "问答分类名称", dataType = "String", required = false, paramType = "query")
    })
    public Object page(@PathVariable(required = true) Integer pageNum,
                       @PathVariable(required = true) Integer pageSize,
                       @RequestParam(required = false) Integer terminal,
                       @RequestParam(required = false) String typeName) {
        PageVO<CustomerAssistantTypeDTO> page = customerAssistantTypeService.page(pageNum, pageSize, terminal, typeName);
        return ResponseEntity.ok(page);
    }
}
