package com.yimao.cloud.system.controller;

import com.yimao.cloud.pojo.dto.system.CustomerAreaManagerDTO;
import com.yimao.cloud.pojo.vo.PageVO;
import com.yimao.cloud.system.po.CustomerAreaManager;
import com.yimao.cloud.system.service.CustomerAreaManagerService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.persistence.Id;

/**
 * 区域经理
 *
 * @author lizhiqiang
 * @date 2019/1/16
 */
@RestController
@Slf4j
@Api(tags = {"CustomerAreaManagerController"})
public class CustomerAreaManagerController {

    @Resource
    private CustomerAreaManagerService customerAreaManagerService;

    /**
     * 分页查看区域经理
     *
     * @param province
     * @param pageNum
     * @param pageSize
     * @return
     */
    @GetMapping(value = "/customer/area/manager/{pageNum}/{pageSize}")
    @ApiOperation(value = "分页查询区域经理列表", notes = "分页查询区域经理列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "页码", dataType = "Long", required = true, paramType = "path"),
            @ApiImplicitParam(name = "pageSize", value = "页数", dataType = "Long", required = true, paramType = "path"),
            @ApiImplicitParam(name = "province", value = "省", dataType = "String", required = false, paramType = "query"),
            @ApiImplicitParam(name = "name", value = "姓氏", dataType = "String", required = false, paramType = "query")
    })
    public Object list(@RequestParam(value = "province", required = false) String province,
                       @RequestParam(value = "name", required = false) String name,
                       @PathVariable(value = "pageNum") Integer pageNum,
                       @PathVariable(value = "pageSize") Integer pageSize) {
        PageVO<CustomerAreaManagerDTO> page = customerAreaManagerService.listCustomerAreaManager(province, name, pageNum, pageSize);
        return ResponseEntity.ok(page);
    }


    /**
     * 新增区域经理
     *
     * @param dto
     * @return
     */
    @PostMapping(value = "/customer/area/manager")
    @ApiOperation(value = "新增区域经理", notes = "新增区域经理")
    @ApiImplicitParam(name = "dto", value = "问答", dataType = "CustomerAreaManagerDTO", required = true, paramType = "body")
    public Object save(@RequestBody CustomerAreaManagerDTO dto) {
        CustomerAreaManager customerAreaManager = new CustomerAreaManager(dto);
        customerAreaManagerService.saveCustomerAreaManager(customerAreaManager);
        dto.setId(customerAreaManager.getId());
        return ResponseEntity.noContent().build();
    }


    /**
     * 修改区域经理
     *
     * @param dto
     * @return
     */
    @PutMapping(value = "/customer/area/manager")
    @ApiOperation(value = "修改区域经理", notes = "修改区域经理")
    @ApiImplicitParam(name = "dto", value = "问答", dataType = "CustomerAreaManagerDTO", required = true, paramType = "body")
    public Object update(@RequestBody CustomerAreaManagerDTO dto) {
        CustomerAreaManager customerAreaManager = new CustomerAreaManager(dto);
        customerAreaManagerService.updateCustomerAreaManager(customerAreaManager);
        return ResponseEntity.ok(dto);
    }


    /**
     * 删除区域经理
     *
     * @param id
     * @return
     */
    @DeleteMapping(value = "/customer/area/manager/{id}")
    @ApiOperation(value = "删除区域经理", notes = "删除区域经理")
    @ApiImplicitParam(name = "id", value = "id", dataType = "Long", required = true, paramType = "path")
    public Object delete(@PathVariable("id") Integer id) {
        customerAreaManagerService.deleteCustomerAreaManager(id);
        return ResponseEntity.noContent().build();
    }


    /**
     * 查看单个区域经理信息
     *
     * @param id
     * @return
     */
    @GetMapping(value = "/customer/area/manager/{id}")
    @ApiOperation(value = "查看单个区域经理", notes = "根据地区查询区域经理")
    @ApiImplicitParam(name = "id", value = "区域经理id", dataType = "Long", required = true, paramType = "path")
    public Object customerAreaManagerByArea(@PathVariable(value = "id") Integer id) {
        CustomerAreaManager customerAreaManager = customerAreaManagerService.listCustomerAreaManager(id);
        CustomerAreaManagerDTO dto = new CustomerAreaManagerDTO();
        customerAreaManager.convert(dto);
        return ResponseEntity.ok(dto);
    }
}
