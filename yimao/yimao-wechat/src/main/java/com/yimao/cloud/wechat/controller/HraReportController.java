package com.yimao.cloud.wechat.controller;

import com.yimao.cloud.pojo.dto.hra.ReportDTO;
import com.yimao.cloud.pojo.vo.PageVO;
import com.yimao.cloud.wechat.feign.HraFeign;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * 体检报告
 *
 * @author liuhao@yimaokeji.com
 * @version v2.0
 * @date 2019/4/3
 */
@RestController
@Slf4j
@Api(tags = "HraReportController")
public class HraReportController {

    @Resource
    private HraFeign hraFeign;

    /**
     * 评估报告列表
     *
     * @param pageNum  页码
     * @param pageSize 页数
     * @return page
     * @author liuhao@yimaokeji.com
     */
    @GetMapping(value = {"/report/{pageNum}/{pageSize}"})
    @ApiOperation(value = "评估报告列表", notes = "评估报告列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "页数", required = true, dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "pageSize", value = "每页显示数量", required = true, dataType = "Long", paramType = "path")
    })
    public ResponseEntity<PageVO<ReportDTO>> reportRecordList(@PathVariable(value = "pageNum") Integer pageNum, @PathVariable(value = "pageSize") Integer pageSize) {
        PageVO<ReportDTO> basePage = hraFeign.reportRecordList(pageNum, pageSize);
        return ResponseEntity.ok(basePage);
    }


    /**
     * 添加评估报告
     *
     * @param phone    手机号
     * @param ticketNo 体检劵
     * @return int
     * @author liuhao@yimaokeji.com
     */
    @PostMapping(value = {"/report"})
    @ApiOperation(value = "添加评估报告", notes = "添加评估报告")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "phone", value = "手机号", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "ticketNo", value = "评估卡号", required = true, dataType = "String", paramType = "query")
    })
    public ResponseEntity addReportRecord(@RequestParam("phone") String phone, @RequestParam("ticketNo") String ticketNo) {
        hraFeign.addReportRecord(phone, ticketNo);
        return ResponseEntity.noContent().build();
    }


    /**
     * 删除体检报告记录
     *
     * @param id 体检报告记录id
     * @return int
     * @author liuhao@yimaokeji.com
     */
    @DeleteMapping(value = "/report/{id}")
    @ApiOperation(value = "删除体检报告记录", notes = "删除体检报告记录")
    @ApiImplicitParam(name = "id", value = "体检报告ID", required = true, dataType = "Long", paramType = "path")
    public ResponseEntity deleteRecord(@PathVariable("id") Integer id) {
        hraFeign.deleteRecord(id);
        return ResponseEntity.noContent().build();
    }
}
