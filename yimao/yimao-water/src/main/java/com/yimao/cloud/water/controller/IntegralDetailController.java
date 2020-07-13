package com.yimao.cloud.water.controller;

import com.yimao.cloud.base.exception.YimaoException;
import com.yimao.cloud.base.utils.DateUtil;
import com.yimao.cloud.base.utils.ExcelUtil;
import com.yimao.cloud.pojo.dto.water.IntegralDetailDTO;
import com.yimao.cloud.pojo.dto.water.IntegralDetailExportDTO;
import com.yimao.cloud.pojo.vo.PageVO;
import com.yimao.cloud.water.service.IntegralDetailService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.List;

/***
 * 功能描述:积分详情
 *
 * @auther: liu yi
 * @date: 2019/6/3 12:02
 */
@RestController
@Api(tags = "IntegralDetailController")
public class IntegralDetailController {
    @Resource
    private IntegralDetailService integralDetailService;

    /*@PostMapping(value = "/integralDetail")
    @ApiOperation(value = "创建积分详情")
    @ApiImplicitParam(name = "dto", value = "创建积分详情", required = true, dataType = "IntegralDetailDTO", paramType = "body")
    public Object save(@RequestBody IntegralDetailDTO dto) {
        integralDetailService.save(dto);
        return ResponseEntity.noContent().build();
    }*/

    /**
     * 根据id获取积分详情详情
     *
     * @param id 设备ID
     */
    @GetMapping(value = "/integralDetail/{id}")
    @ApiOperation(value = "根据ID获取积分详情")
    public Object getById(@PathVariable Integer id) {
        IntegralDetailDTO dto = integralDetailService.getById(id);
        return ResponseEntity.ok(dto);
    }

    /***
     * 功能描述:查询活动积分统计列表（分页）
     *
     * @param: [ruleId, sn, province, city, region, pageNum, pageSize]
     * @auther: liu yi
     * @date: 2019/6/3 17:29
     * @return: java.lang.Object
     */
    @GetMapping(value = "/integralDetail/{pageNum}/{pageSize}/ruleId")
    @ApiOperation(value = "查询活动积分详情列表（分页）")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "ruleId", value = "ruleId", required = true, dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "sn", value = "sn", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "province", value = "省", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "city", value = "市", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "region", value = "区", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "pageNum", value = "页数", defaultValue = "1", dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "pageSize", value = "页码", defaultValue = "10", dataType = "Long", paramType = "path")
    })
    public Object getDetailListByRuleId(@RequestParam(value = "ruleId") Integer ruleId,
                                        @RequestParam(value = "sn", required = false) String sn,
                                        @RequestParam(value = "province", required = false) String province,
                                        @RequestParam(value = "city", required = false) String city,
                                        @RequestParam(value = "region", required = false) String region,
                                        @PathVariable(value = "pageNum") Integer pageNum,
                                        @PathVariable(value = "pageSize") Integer pageSize) {
        PageVO<IntegralDetailDTO> list = integralDetailService.getDetailListByRuleId(ruleId, sn, province, city, region, pageNum, pageSize);
        return ResponseEntity.ok(list);
    }

    /***
     * 功能描述:查询设备统计积分
     *
     * @param: [sn, startTime, endTime]
     * @auther: liu yi
     * @date: 2019/6/3 17:29
     * @return: java.lang.Object
     */
    @GetMapping(value = "/integralDetail/sn")
    @ApiOperation(value = "查询设备统计积分")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "sn", value = "sn", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "startTime", value = "开始时间", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "endTime", value = "结束时间", dataType = "String", paramType = "query")
    })
    public Object getCountBySn(@RequestParam(value = "sn") String sn,
                               @RequestParam(value = "startTime", required = false) Date startTime,
                               @RequestParam(value = "endTime", required = false) Date endTime) {
        int count = integralDetailService.getCountBySn(sn, startTime, endTime);
        return ResponseEntity.ok(count);
    }


    /***
     * 功能描述:查询累计积分详情列表（分页）
     *
     * @param: [sn, startTime, endTime, pageNum, pageSize]
     * @auther: liu yi
     * @date: 2019/6/3 17:29
     * @return: java.lang.Object
     */
    @GetMapping(value = "/integralDetail/{pageNum}/{pageSize}/sn")
    @ApiOperation(value = "查询累计积分详情列表（分页）")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "sn", value = "sn", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "startTime", value = "结束时间", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "endTime", value = "开始时间", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "pageNum", value = "页数", defaultValue = "1", dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "pageSize", value = "页码", defaultValue = "10", dataType = "Long", paramType = "path")
    })
    public Object getDetailListBySn(@RequestParam(value = "sn", required = false) String sn,
                                    @RequestParam(value = "startTime", required = false) Date startTime,
                                    @RequestParam(value = "endTime", required = false) Date endTime,
                                    @PathVariable(value = "pageNum", required = false) Integer pageNum,
                                    @PathVariable(value = "pageSize") Integer pageSize) {
        PageVO<IntegralDetailDTO> list = integralDetailService.getDetailListBySn(sn, startTime, endTime, pageNum, pageSize);
        return ResponseEntity.ok(list);
    }

    /**
     * 根据水机设备ID删除积分详情
     *
     * @param id 规则ID
     */
    /*@DeleteMapping(value = "/integralDetail/{id}")
    @ApiOperation(value = "根据ID删除积分详情")
    public void delete(@PathVariable(value = "id") Integer id) {
        integralDetailService.delete(id);
    }*/

    /***
     * 功能描述:根据条件查询导出活动详情
     *
     * @param: [queryDTO, response]
     * @auther: liu yi
     * @date: 2019/5/15 14:22
     * @return: java.lang.Object
     */
    @PostMapping(value = "/waterdevice/integralDetail/ruleId/export")
    @ApiOperation(value = "根据条件查询导出活动详情", notes = "根据条件查询导出活动详情")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "ruleId", value = "ruleId", required = true, dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "sn", value = "sn", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "province", value = "开始时间", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "city", value = "开始时间", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "region", value = "开始时间", dataType = "String", paramType = "query")
    })
    public Object exportDetailByRuleId(@RequestParam(value = "ruleId") Integer ruleId,
                                       @RequestParam(value = "sn", required = false) String sn,
                                       @RequestParam(value = "province", required = false) String province,
                                       @RequestParam(value = "city", required = false) String city,
                                       @RequestParam(value = "region", required = false) String region,
                                       HttpServletResponse response) {
        List<IntegralDetailDTO> dtoList = integralDetailService.getExportDetailListByRuleId(ruleId, sn, province, city, region);

        String header = "活动详情_" + DateUtil.getCurrentTimeStr(DateUtil.DATEFORMAT_01);
        String[] beanPropertys = new String[]{"integralRuleId", "integralRuleName", "province", "city", "region", "address", "model", "sn", "totalNum"};
        String[] titles = new String[]{"规则ID", "规则名称", "省", "市", "区", "详细地址", "型号", "SN码", "累计积分"};
        boolean boo = ExcelUtil.exportSXSSF(dtoList, header, titles.length - 1, titles, beanPropertys, response);
        if (!boo) {
            throw new YimaoException("导出滤芯维护记录失败!");
        }

        return ResponseEntity.noContent().build();
    }

    /***
     * 功能描述:根据条件查询导出累计详情
     *
     * @param: [queryDTO, response]
     * @auther: liu yi
     * @date: 2019/5/15 14:22
     * @return: java.lang.Object
     */
    @PostMapping(value = "/waterdevice/integralDetail/sn/export")
    @ApiOperation(value = "根据条件查询导出累计详情", notes = "根据条件查询导出累计详情")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "sn", value = "sn", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "startTime", value = "结束时间", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "endTime", value = "开始时间", dataType = "String", paramType = "query")
    })
    public Object exportDetailListBySn(@RequestParam(value = "sn") String sn,
                                       @RequestParam(value = "startTime", required = false) Date startTime,
                                       @RequestParam(value = "endTime", required = false) Date endTime,
                                       HttpServletResponse response) {
        List<IntegralDetailExportDTO> dtoList = integralDetailService.getExportDetailListBySn(sn, startTime, endTime);

        String header = "累计详情_" + DateUtil.getCurrentTimeStr(DateUtil.DATEFORMAT_01);
        String[] beanPropertys = new String[]{"sn", "integralRuleName", "type", "createTime", "num"};
        String[] titles = new String[]{"sn", "积分达成项规则", "积分达成项类型", "日期", "奖励积分"};
        boolean boo = ExcelUtil.exportSXSSF(dtoList, header, titles.length - 1, titles, beanPropertys, response);
        if (!boo) {
            throw new YimaoException("导出滤芯维护记录失败!");
        }

        return ResponseEntity.noContent().build();
    }
}
