package com.yimao.cloud.system.controller.waterdevice;

import com.yimao.cloud.base.constant.RabbitConstant;
import com.yimao.cloud.base.enums.ExportRecordStatus;
import com.yimao.cloud.base.exception.BadRequestException;
import com.yimao.cloud.base.msg.CommResult;
import com.yimao.cloud.pojo.dto.system.ExportRecordDTO;
import com.yimao.cloud.pojo.dto.water.ManualPadCostDTO;
import com.yimao.cloud.pojo.query.water.ManualPadCostQuery;
import com.yimao.cloud.pojo.vo.PageVO;
import com.yimao.cloud.pojo.vo.water.ManualPadCostVO;
import com.yimao.cloud.system.feign.WaterFeign;
import com.yimao.cloud.system.service.ExportRecordService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * 描述：手动修改水机配额
 *
 * @Author Zhang Bo
 * @Date 2019/5/7
 */
@RestController
@Api(tags = "ManualPadCostController")
public class ManualPadCostController {

    @Resource
    private WaterFeign waterFeign;

    @Resource
    private ExportRecordService exportRecordService;

    @Resource
    private RabbitTemplate rabbitTemplate;

    /**
     * 水机物联-设备金额管理-添加
     *
     * @param dto dto
     */
    @PostMapping(value = "/padcost")
    @ApiOperation(value = "水机物联-设备金额管理-添加")
    @ApiImplicitParam(name = "dto", value = "对象信息", required = true, dataType = "ManualPadCostDTO", paramType = "body")
    public void save(@RequestBody ManualPadCostDTO dto) {
        waterFeign.saveManualPadCost(dto);
    }

    /**
     * 水机物联-设备金额管理-修改
     *
     * @param dto dto
     */
    @PutMapping(value = "/padcost")
    @ApiOperation(value = "水机物联-设备金额管理-修改")
    @ApiImplicitParam(name = "dto", value = "对象信息", required = true, dataType = "ManualPadCostDTO", paramType = "body")
    public void update(@RequestBody ManualPadCostDTO dto) {
        if (dto.getId() == null) {
            throw new BadRequestException("操作对象ID不能为空。");
        }
        waterFeign.updateManualPadCost(dto);
    }

    /**
     * 水机物联-设备金额管理-查询
     *
     * @param pageNum  第几页
     * @param pageSize 每页大小
     * @param query    查询条件
     */
    @PostMapping(value = "/padcost/{pageNum}/{pageSize}")
    @ApiOperation(value = "水机物联-设备金额管理-查询")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "第几页", dataType = "Long", paramType = "path", required = true, defaultValue = "1"),
            @ApiImplicitParam(name = "pageSize", value = "每页大小", dataType = "Long", paramType = "path", required = true, defaultValue = "10"),
            @ApiImplicitParam(name = "query", value = "查询条件", dataType = "ManualPadCostQuery", paramType = "body")
    })
    public PageVO<ManualPadCostVO> page(@PathVariable Integer pageNum, @PathVariable Integer pageSize,
                                        @RequestBody ManualPadCostQuery query) {
        return waterFeign.pageManualPadCost(pageNum, pageSize, query);
    }

    /**
     * 水机物联-设备金额管理-导出
     */
    @PostMapping(value = "/padcost/export")
    @ApiOperation(value = "水机物联-设备金额管理-导出")
    @ApiImplicitParam(name = "query", value = "查询条件", dataType = "ManualPadCostQuery", paramType = "body")
    public Object export(@RequestBody ManualPadCostQuery query, HttpServletResponse response) {
        //保存导出记录，如果短时间操作多次，会提示操作过于频繁
        String url = "/padcost/export";
        ExportRecordDTO record = exportRecordService.save(url, "后台修改水机余额");

        //导出记录添加成功，并且状态为"等待导出"，才入列
        if (record.getStatus() == ExportRecordStatus.WAITING.value) {
            //异步队列执行导出
            Map<String, Object> map = new HashMap<>();
            map.put("exportRecordDTO", record);
            map.put("query", query);
            rabbitTemplate.convertAndSend(RabbitConstant.EXPORT_ACTION_WATER, map);
        }
        return CommResult.ok(record.getId());

    /*    List<ManualPadCostExport> list = waterFeign.listManualPadCostExport(query);
        if (CollectionUtil.isEmpty(list)) {
            throw new NotFoundException("没有数据可以导出");
        }
        String header = DateUtil.getCurrentTimeStr(DateUtil.DATEFORMAT_01);
        String[] titles = new String[]{
                "SN码", "余额", "已使用流量", "是否开启", "同步时间", "同步状态", "同步失败原因", "创建时间"
        };
        String[] properties = new String[]{
                "sn", "balance", "discharge", "open", "syncTime", "syncStatus", "syncFailReason", "createTime"
        };

        header = header + "后台修改水机余额";
        boolean boo = ExcelUtil.exportSXSSF(list, header, titles.length - 1, titles, properties, response);
        if (!boo) {
            throw new YimaoException("导出失败");
        }*/
    }

}
