package com.yimao.cloud.system.controller.waterdevice;

import com.yimao.cloud.base.exception.BadRequestException;
import com.yimao.cloud.base.utils.StringUtil;
import com.yimao.cloud.pojo.dto.water.TdsUploadRecordDTO;
import com.yimao.cloud.pojo.query.water.TdsUploadRecordQuery;
import com.yimao.cloud.pojo.vo.PageVO;
import com.yimao.cloud.pojo.vo.water.TdsUploadRecordVO;
import com.yimao.cloud.system.feign.WaterFeign;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 描述：设备TDS值上传记录
 *
 * @Author Zhang Bo
 * @Date 2019/4/25
 */
@RestController
@Slf4j
@Api(tags = "TdsUploadRecordController")
public class TdsUploadRecordController {

    @Resource
    private WaterFeign waterFeign;

    /**
     * 创建设备TDS值上传记录
     *
     * @param dto TDS值上传记录
     */
    @PostMapping(value = "/tds/record")
    @ApiOperation(value = "创建设备TDS值上传记录")
    @ApiImplicitParam(name = "dto", value = "设备TDS值上传记录", required = true, dataType = "TdsUploadRecordDTO", paramType = "body")
    public void save(@RequestBody TdsUploadRecordDTO dto) {
        waterFeign.saveTdsUploadRecord(dto);
    }

    /**
     * 修改设备TDS值上传记录
     *
     * @param dto TDS值上传记录
     */
    @PutMapping(value = "/tds/record")
    @ApiOperation(value = "修改设备TDS值上传记录")
    @ApiImplicitParam(name = "dto", value = "设备TDS值上传记录", required = true, dataType = "TdsUploadRecordDTO", paramType = "body")
    public void update(@RequestBody TdsUploadRecordDTO dto) {
        if (dto.getId() == null) {
            throw new BadRequestException("修改对象不存在。");
        } else {
            waterFeign.updateTdsUploadRecord(dto);
        }
    }

    /**
     * 恢复设备TDS
     *
     * @param sn 设备SN码
     */
    @PostMapping(value = "/tds/record/reset")
    @ApiOperation(value = "恢复设备TDS")
    @ApiImplicitParam(name = "sn", value = "设备SN码", required = true, dataType = "String", paramType = "query")
    public void reset(@RequestParam String sn) {
        if (StringUtil.isBlank(sn)) {
            throw new BadRequestException("设备SN码不能为空。");
        }
        waterFeign.resetTdsUploadRecord(sn);
    }

    /**
     * 审核
     *
     * @param id           TDS值上传记录ID
     * @param verifyResult 审核结果：Y-审核通过；N-审核不通过；
     * @param verifyReason 审核原因
     */
    @PostMapping(value = "/tds/record/{id}/verify")
    @ApiOperation(value = "审核")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "TDS值上传记录ID", required = true, dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "verifyResult", value = "审核结果：Y-审核通过；N-审核不通过；", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "verifyReason", value = "审核原因", dataType = "String", paramType = "query")
    })
    public void verify(@PathVariable Integer id, @RequestParam String verifyResult, @RequestParam(required = false) String verifyReason) {
        waterFeign.verifyTdsUploadRecord(id, verifyResult, verifyReason);
    }

    /**
     * 查询设备TDS值上传记录（分页）
     *
     * @param pageNum  第几页
     * @param pageSize 每页大小
     * @param query    查询条件
     */
    @PostMapping(value = "/tds/record/{pageNum}/{pageSize}")
    @ApiOperation(value = "查询设备TDS值上传记录（分页）")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "第几页", required = true, dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "pageSize", value = "每页大小", required = true, dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "query", value = "查询条件", dataType = "TdsUploadRecordQuery", paramType = "body")
    })
    public PageVO<TdsUploadRecordVO> page(@PathVariable Integer pageNum, @PathVariable Integer pageSize, @RequestBody TdsUploadRecordQuery query) {
        return waterFeign.pageTdsUploadRecord(pageNum, pageSize, query);
    }

    /**
     * 详情
     *
     * @param id TDS值上传记录ID
     */
    @GetMapping(value = "/tds/record/{id}/detail")
    @ApiOperation(value = "详情")
    @ApiImplicitParam(name = "id", value = "TDS值上传记录ID", required = true, dataType = "Long", paramType = "path")
    public TdsUploadRecordVO detail(@PathVariable Integer id) {
        return waterFeign.getTdsUploadRecordDetail(id);
    }

}
