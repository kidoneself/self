package com.yimao.cloud.system.controller;

import com.yimao.cloud.base.exception.BadRequestException;
import com.yimao.cloud.pojo.dto.user.DistributorRoleDTO;
import com.yimao.cloud.pojo.vo.PageVO;
import com.yimao.cloud.system.feign.UserFeign;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * 经销商配置
 *
 * @author hhf
 * @date 2019/4/1
 */
@RestController
@Api(tags = "DistributorRoleController")
public class DistributorRoleController {

    @Resource
    private UserFeign userFeign;

    /**
     * 新增经销商角色配置
     *
     * @param dto 经销商角色配置
     */
    @PostMapping(value = "/distributor/role")
    @ApiOperation(value = "新增经销商角色配置")
    @ApiImplicitParam(name = "dto", value = "经销商角色配置", required = true, dataType = "DistributorRoleDTO", paramType = "body")
    public void save(@RequestBody DistributorRoleDTO dto) {
        userFeign.saveDistributorRole(dto);
    }

    /**
     * 禁用/启用经销商角色配置
     *
     * @param id 经销商角色ID
     */
    @PatchMapping(value = "/distributor/role/{id}")
    @ApiOperation(value = "禁用/启用经销商角色配置")
    @ApiImplicitParam(name = "id", value = "经销商角色ID", required = true, dataType = "Long", paramType = "query")
    public void forbidden(@PathVariable Integer id) {
        userFeign.forbiddenDistributorRole(id);
    }

    /**
     * 修改经销商角色配置
     *
     * @param dto 经销商角色配置
     */
    @PutMapping(value = "/distributor/role")
    @ApiOperation(value = "修改经销商角色配置")
    @ApiImplicitParam(name = "dto", value = "经销商角色配置", required = true, dataType = "DistributorRoleDTO", paramType = "body")
    public void update(@RequestBody DistributorRoleDTO dto) {
        if (dto.getId() == null) {
            throw new BadRequestException("经销商ID不能为空。");
        }
        userFeign.updateDistributorRole(dto);
    }

    /**
     * 分页查询经销商角色配置信息
     *
     * @param pageNum  页数
     * @param pageSize 每页大小
     */
    @GetMapping(value = "/distributor/roles/{pageNum}/{pageSize}")
    @ApiOperation(value = "分页查询经销商角色配置信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "页数", required = true, defaultValue = "1", dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "pageSize", value = "每页大小", required = true, defaultValue = "10", dataType = "Long", paramType = "path")
    })
    public PageVO<DistributorRoleDTO> page(@PathVariable Integer pageNum, @PathVariable Integer pageSize) {
        return userFeign.pageDistributorRole(pageNum, pageSize);
    }

    /**
     * 查询经销商角色配置信息（所有）
     */
    @GetMapping(value = "/distributor/roles")
    @ApiOperation(value = "查询经销商角色配置信息（所有）")
    public List<DistributorRoleDTO> list() {
        return userFeign.listAllDistributorRole();
    }

}
