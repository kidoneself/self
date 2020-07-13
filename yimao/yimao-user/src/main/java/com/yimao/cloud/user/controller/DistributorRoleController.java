package com.yimao.cloud.user.controller;

import com.yimao.cloud.base.exception.BadRequestException;
import com.yimao.cloud.pojo.dto.user.DistributorRoleDTO;
import com.yimao.cloud.pojo.vo.PageVO;
import com.yimao.cloud.user.po.DistributorRole;
import com.yimao.cloud.user.service.DistributorRoleService;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * 经销商角色配置
 *
 * @author Lizhqiang
 * @date 2018/12/17
 */
@RestController
public class DistributorRoleController {

    @Resource
    private DistributorRoleService distributorRoleService;

    /**
     * 新增经销商角色配置
     *
     * @param dto 经销商角色配置
     */
    @PostMapping(value = "/distributor/role")
    public void save(@RequestBody DistributorRoleDTO dto) {
        distributorRoleService.save(dto);
    }

    /**
     * 禁用/启用经销商角色配置
     *
     * @param id 经销商角色ID
     */
    @PatchMapping(value = "/distributor/role/{id}")
    public void forbidden(@PathVariable Integer id) {
        distributorRoleService.forbidden(id);
    }

    /**
     * 修改经销商角色配置
     *
     * @param dto 经销商角色配置
     */
    @PutMapping(value = "/distributor/role")
    public void update(@RequestBody DistributorRoleDTO dto) {
        if (dto.getId() == null) {
            throw new BadRequestException("经销商ID不能为空。");
        }
//        DistributorRole distributorRole = new DistributorRole(dto);
        distributorRoleService.update(dto);
    }

    /**
     * 分页查询经销商角色配置信息
     *
     * @param pageNum  页数
     * @param pageSize 每页大小
     */
    @GetMapping(value = "/distributor/roles/{pageNum}/{pageSize}")
    public PageVO<DistributorRoleDTO> page(@PathVariable Integer pageNum, @PathVariable Integer pageSize) {
        return distributorRoleService.page(pageNum, pageSize);
    }

    /**
     * 查询经销商角色配置信息（所有）
     */
    @GetMapping(value = "/distributor/roles")
    public List<DistributorRoleDTO> list() {
        return distributorRoleService.listAll();
    }

    /**
     * 查询经销商角色配置信息（根据level）
     *
     * @param level 经销商角色配置
     */
    @GetMapping(value = "/distributor/role")
    public DistributorRoleDTO getByLevel(@RequestParam Integer level) {
        return distributorRoleService.getByLevel(level);
    }

}
