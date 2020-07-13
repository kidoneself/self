package com.yimao.cloud.water.controller;

import com.yimao.cloud.pojo.dto.water.WaterDevicePlaceChangeRecordDTO;
import com.yimao.cloud.pojo.vo.PageVO;
import com.yimao.cloud.pojo.vo.water.WaterDevicePlaceChangeRecordVO;
import com.yimao.cloud.water.po.WaterDevicePlaceChangeRecord;
import com.yimao.cloud.water.service.WaterDevicePlaceChangeRecordService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * 描述：水机摆放位置更换记录
 *
 * @Author Zhang Bo
 * @Date 2019/4/26
 */
@RestController
public class WaterDevicePlaceChangeRecordController {

    @Resource
    private WaterDevicePlaceChangeRecordService waterDevicePlaceChangeRecordService;

    /**
     * 保存水机摆放位置更换记录
     *
     */
    @PostMapping(value = "/placechangerecord/save")
    public void save(@RequestBody WaterDevicePlaceChangeRecordDTO waterDevicePlaceChangeRecordDTO) {
        WaterDevicePlaceChangeRecord waterDevicePlaceChangeRecord = new WaterDevicePlaceChangeRecord(waterDevicePlaceChangeRecordDTO);
        waterDevicePlaceChangeRecordService.save(waterDevicePlaceChangeRecord);
    }

    /**
     * 查询水机摆放位置更换记录（分页）
     *
     * @param pageNum  第几页
     * @param pageSize 每页大小
     * @param sn       SN码
     */
    @GetMapping(value = "/placechangerecord/{pageNum}/{pageSize}")
    public PageVO<WaterDevicePlaceChangeRecordVO> page(@PathVariable Integer pageNum, @PathVariable Integer pageSize,
                                                       @RequestParam(required = false) String sn) {
        return waterDevicePlaceChangeRecordService.page(pageNum, pageSize, sn);
    }

    /**
     * @param
     * @return com.yimao.cloud.pojo.dto.water.WaterDevicePlaceChangeRecordDTO
     * @description 根据sn查询水机摆放位置更换记录
     * @author Liu Yi
     * @date 2019/11/14 16:50
     */
    @GetMapping(value = "/placechangerecord/sn")
    public WaterDevicePlaceChangeRecordDTO getWaterDevicePlaceChangeRecordBySn(@RequestParam String sn) {
        return waterDevicePlaceChangeRecordService.getBySn(sn);
    }

}
