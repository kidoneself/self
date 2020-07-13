package com.yimao.cloud.water.controller;

import com.yimao.cloud.pojo.dto.water.WaterDeviceCostChangeRecordDTO;
import com.yimao.cloud.pojo.vo.PageVO;
import com.yimao.cloud.water.service.WaterDeviceCostChangeRecordService;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * 描述：水机计费方式修改记录
 *
 * @Author Zhang Bo
 * @Date 2019/4/26
 */
@RestController
@Api(tags = "WaterDeviceCostChangeRecordController")
public class WaterDeviceCostChangeRecordController {

    @Resource
    private WaterDeviceCostChangeRecordService waterDeviceCostChangeRecordService;

    /**
     * 查询水机计费方式修改记录（分页）
     *
     * @param pageNum  第几页
     * @param pageSize 每页大小
     * @param sn       SN码
     */
    @GetMapping(value = "/costchangerecord/{pageNum}/{pageSize}")
    public PageVO<WaterDeviceCostChangeRecordDTO> page(@PathVariable Integer pageNum, @PathVariable Integer pageSize,
                                                       @RequestParam(required = false) String sn) {
        return waterDeviceCostChangeRecordService.page(pageNum, pageSize, sn);
    }

}
