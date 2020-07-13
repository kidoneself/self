package com.yimao.cloud.system.controller.waterdevice;

import com.yimao.cloud.base.exception.BadRequestException;
import com.yimao.cloud.pojo.dto.water.SimCardNumberDTO;
import com.yimao.cloud.pojo.query.water.SimCardNumberQuery;
import com.yimao.cloud.pojo.vo.PageVO;
import com.yimao.cloud.pojo.vo.water.SimCardNumberVO;
import com.yimao.cloud.system.feign.WaterFeign;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 描述：SIM号码段
 *
 * @Author Zhang Bo
 * @Date 2019/4/25
 */
@RestController
@Api(tags = "SimCardNumberController")
public class SimCardNumberController {

    @Resource
    private WaterFeign waterFeign;

    /**
     * 创建SIM号码段
     *
     * @param dto SIM号码段
     */
    @PostMapping(value = "/simcard/number")
    @ApiOperation(value = "创建SIM号码段")
    @ApiImplicitParam(name = "dto", value = "SIM号码段", required = true, dataType = "SimCardNumberDTO", paramType = "body")
    public void save(@RequestBody SimCardNumberDTO dto) {
        waterFeign.saveSimCardNumber(dto);
    }

    /**
     * 修改SIM号码段
     *
     * @param dto SIM号码段
     */
    @PutMapping(value = "/simcard/number")
    @ApiOperation(value = "修改SIM号码段")
    @ApiImplicitParam(name = "dto", value = "SIM号码段", required = true, dataType = "SimCardNumberDTO", paramType = "body")
    public void update(@RequestBody SimCardNumberDTO dto) {
        if (dto.getId() == null) {
            throw new BadRequestException("修改对象不存在。");
        } else {
            waterFeign.updateSimCardNumber(dto);
        }
    }

    /**
     * 查询SIM号码段（分页）
     *
     * @param pageNum  第几页
     * @param pageSize 每页大小
     * @param query    查询条件
     */
    @PostMapping(value = "/simcard/number/{pageNum}/{pageSize}")
    @ApiOperation(value = "查询SIM号码段（分页）")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "第几页", required = true, dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "pageSize", value = "每页大小", required = true, dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "query", value = "查询条件", dataType = "SimCardNumberQuery", paramType = "body")
    })
    public PageVO<SimCardNumberVO> page(@PathVariable Integer pageNum, @PathVariable Integer pageSize, @RequestBody SimCardNumberQuery query) {
        return waterFeign.pageSimCardNumber(pageNum, pageSize, query);
    }

}
