package com.yimao.cloud.task.controller;

import com.yimao.cloud.base.constant.Constant;
import com.yimao.cloud.base.utils.DateUtil;
import com.yimao.cloud.pojo.dto.water.WaterDeviceDTO;
import com.yimao.cloud.task.feign.WaterFeign;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 查询MYSQL数据库设备相关信息。
 *
 * @author Zhang Bo
 * @date 2017/12/15.
 */
@RestController
@Api(tags = "WaterDeviceController")
public class WaterDeviceController {

    @Resource
    private WaterFeign waterFeign;

    /**
     * 修改设备创建时间和金额（测试专用）
     *
     * @param sn 设备sn编码
     */
    @PatchMapping(value = "/waterdevice/testonly")
    @ApiOperation(value = "修改设备创建时间和金额（测试专用）")
    public void updateDeivce(@RequestParam String sn, @RequestParam Integer days, @RequestParam BigDecimal money) {
        if (!Constant.PRO_ENVIRONMENT) {
            WaterDeviceDTO dto = waterFeign.getBySnCode(sn);
            WaterDeviceDTO update = new WaterDeviceDTO();
            update.setId(dto.getId());
            Date date = DateUtil.dayAfter(dto.getCreateTime(), days);
            update.setCreateTime(date);
            update.setMoney(money);
            waterFeign.updateWaterDevice(update);
        }
    }

}
