package com.yimao.cloud.water.controller;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.yimao.cloud.pojo.dto.water.WaterDeviceDTO;
import com.yimao.cloud.pojo.vo.PageVO;
import com.yimao.cloud.water.mapper.WaterDeviceMapper;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * APP端查询设备相关信息
 *
 * @author Zhang Bo
 * @date 2019/08/26
 */
@RestController
public class AppWaterDeviceController {

    @Resource
    private WaterDeviceMapper waterDeviceMapper;

    /**
     * 翼猫APP-我的-水机续费-查询列表（栏目：10-新安装；20-待续费；30-已续费；）
     */
    @GetMapping(value = "/my/waterdevice/{pageNum}/{pageSize}")
    public Object page(@RequestParam Integer column, @RequestParam Integer distributorId,
                       @PathVariable Integer pageNum, @PathVariable Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        Page<WaterDeviceDTO> page = waterDeviceMapper.selectDeviceRenewInfoForApp(column, distributorId);
        return new PageVO<>(pageNum, page);
    }

}
