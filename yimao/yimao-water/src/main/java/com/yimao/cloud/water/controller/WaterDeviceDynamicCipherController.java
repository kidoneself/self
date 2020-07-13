package com.yimao.cloud.water.controller;

import com.yimao.cloud.base.utils.CollectionUtil;
import com.yimao.cloud.pojo.dto.water.WaterDeviceDynamicCipherConfigDTO;
import com.yimao.cloud.pojo.dto.water.WaterDeviceDynamicCipherRecordDTO;
import com.yimao.cloud.pojo.vo.PageVO;
import com.yimao.cloud.water.po.WaterDeviceDynamicCipherConfig;
import com.yimao.cloud.water.po.WaterDeviceDynamicCipherRecord;
import com.yimao.cloud.water.service.WaterDeviceDynamicCipherService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 描述：TODO
 *
 * @Author Zhang Bo
 * @Date 2019/3/12
 */
@RestController
@Slf4j
@Api(tags = "WaterDeviceDynamicCipherController")
public class WaterDeviceDynamicCipherController {

    @Resource
    private WaterDeviceDynamicCipherService waterDeviceDynamicCipherService;

    /**
     * 创建设备动态密码
     *
     * @param dto 设备动态密码记录
     */
    @PostMapping(value = "/waterdevice/dynamic/cipher/record")
    @ApiOperation(value = "创建设备动态密码")
    public Object createDynamicCipherRecord(@RequestBody WaterDeviceDynamicCipherRecordDTO dto) {
        WaterDeviceDynamicCipherRecord record = new WaterDeviceDynamicCipherRecord(dto);
        waterDeviceDynamicCipherService.saveDynamicCipherRecord(record);
        dto.setId(record.getId());
        return dto;
    }

    /**
     * 更新设备动态密码
     *
     * @param dto 设备动态密码记录
     */
    @PatchMapping(value = "/waterdevice/dynamic/cipher/record")
    @ApiOperation(value = "更新设备动态密码")
    public void updateDynamicCipherRecord(@RequestBody WaterDeviceDynamicCipherRecordDTO dto) {
        WaterDeviceDynamicCipherRecord record = new WaterDeviceDynamicCipherRecord(dto);
        waterDeviceDynamicCipherService.updateDynamicCipherRecord(record);
    }

    /**
     * 还在有效期内的密码全部置为无效
     */
    @PatchMapping(value = "/waterdevice/dynamic/cipher/record/inValid")
    public void setDeviceAllDynamicCipherInValid(@RequestParam String sn) {
        waterDeviceDynamicCipherService.setDeviceAllDynamicCipherInValid(sn);
    }

    /**
     * 根据水机设备SN获取设备动态密码
     *
     * @param sn 设备SN码
     */
    @GetMapping(value = "/waterdevice/dynamic/cipher/record")
    @ApiOperation(value = "根据水机设备SN获取设备动态密码")
    public Object getDynamicCipherRecordBySnCode(@RequestParam("sn") String sn) {
        WaterDeviceDynamicCipherRecord record = waterDeviceDynamicCipherService.getDynamicCipherRecordBySnCode(sn);
        if (record == null) {
            return null;
        }
        WaterDeviceDynamicCipherRecordDTO dto = new WaterDeviceDynamicCipherRecordDTO();
        record.convert(dto);
        return dto;
    }

    /**
     * 根据水机设备SN获取设备动态密码
     *
     * @param pageNum    页码
     * @param pageSize   页面大小
     * @param engineerId 安装工ID
     */
    @GetMapping(value = "/waterdevice/dynamic/cipher/record/{pageNum}/{pageSize}")
    @ApiOperation(value = "根据水机设备SN获取设备动态密码")
    public Object pageDynamicCipherRecord(@PathVariable(value = "pageNum") Integer pageNum,
                                          @PathVariable(value = "pageSize") Integer pageSize,
                                          @RequestParam("engineerId") Integer engineerId) {
        PageVO<WaterDeviceDynamicCipherRecordDTO> page = waterDeviceDynamicCipherService.pageDynamicCipherRecord(pageNum, pageSize, engineerId);
        if (page == null || CollectionUtil.isEmpty(page.getResult())) {
            return null;
        }
        return page.getResult();
    }

    /**
     * 获取水机设备动态密码配置
     */
    @GetMapping(value = "/waterdevice/dynamic/cipher/config")
    @ApiOperation(value = "获取水机设备动态密码配置")
    public Object getDynamicCipherConfig() {
        WaterDeviceDynamicCipherConfig config = waterDeviceDynamicCipherService.getDynamicCipherConfig();
        if (config == null) {
            return null;
        }
        WaterDeviceDynamicCipherConfigDTO dto = new WaterDeviceDynamicCipherConfigDTO();
        config.convert(dto);
        return dto;
    }

}
