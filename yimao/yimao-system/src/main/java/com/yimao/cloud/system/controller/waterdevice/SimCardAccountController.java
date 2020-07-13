package com.yimao.cloud.system.controller.waterdevice;

import com.yimao.cloud.base.exception.BadRequestException;
import com.yimao.cloud.pojo.dto.water.SimCardAccountDTO;
import com.yimao.cloud.pojo.vo.PageVO;
import com.yimao.cloud.system.feign.WaterFeign;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * 描述：SIM运营商分配的权限账号
 *
 * @Author Zhang Bo
 * @Date 2019/4/25
 */
@RestController
@Api(tags = "SimCardAccountController")
public class SimCardAccountController {

    @Resource
    private WaterFeign waterFeign;

    /**
     * 创建SIM运营商分配的权限账号
     *
     * @param dto SIM运营商分配的权限账号
     */
    @PostMapping(value = "/simcard/account")
    @ApiOperation(value = "创建SIM运营商分配的权限账号")
    @ApiImplicitParam(name = "dto", value = "SIM运营商分配的权限账号", required = true, dataType = "SimCardAccountDTO", paramType = "body")
    public void save(@RequestBody SimCardAccountDTO dto) {
        waterFeign.saveSimCardAccount(dto);
    }

    /**
     * 修改SIM运营商分配的权限账号
     *
     * @param dto SIM运营商分配的权限账号
     */
    @PutMapping(value = "/simcard/account")
    @ApiOperation(value = "修改SIM运营商分配的权限账号")
    @ApiImplicitParam(name = "dto", value = "SIM运营商分配的权限账号", required = true, dataType = "SimCardAccountDTO", paramType = "body")
    public void update(@RequestBody SimCardAccountDTO dto) {
        if (dto.getId() == null) {
            throw new BadRequestException("修改对象不存在。");
        } else {
            waterFeign.updateSimCardAccount(dto);
        }
    }

    /**
     * 查询SIM运营商分配的权限账号（分页）
     *
     * @param pageNum  第几页
     * @param pageSize 每页大小
     */
    @GetMapping(value = "/simcard/account/{pageNum}/{pageSize}")
    @ApiOperation(value = "查询SIM运营商分配的权限账号（分页）")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "第几页", required = true, dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "pageSize", value = "每页大小", required = true, dataType = "Long", paramType = "path")
    })
    public PageVO<SimCardAccountDTO> page(@PathVariable Integer pageNum, @PathVariable Integer pageSize) {
        return waterFeign.pageSimCardAccount(pageNum, pageSize);
    }

    /**
     * 获取所有SIM运营商
     */
    @GetMapping(value = "/simcard/account/all")
    @ApiOperation(value = "获取所有SIM运营商")
    public List<SimCardAccountDTO> list() {
        return waterFeign.listSimCardAccount();
    }

}
