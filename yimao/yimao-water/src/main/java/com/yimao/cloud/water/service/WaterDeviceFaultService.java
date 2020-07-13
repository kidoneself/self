package com.yimao.cloud.water.service;

import com.yimao.cloud.pojo.dto.water.WaterDeviceFaultDTO;
import com.yimao.cloud.pojo.vo.PageVO;
import com.yimao.cloud.pojo.vo.water.WaterDeviceFaultVO;

public interface WaterDeviceFaultService {

    /**
     * 新增或更新水机设备故障
     */
    void saveOrUpdate(Integer deviceId, String sn, Integer type, String filterType, String fault);

    /**
     * 解除水机设备故障
     */
    void resolve(Integer deviceId, String sn, Integer type, String filterType);

    /**
     * 查询设备故障信息
     */
    WaterDeviceFaultDTO getByDeviceIdAndSn(Integer deviceId, String sn);

    /**
     * 查询水机故障记录（分页）
     *
     * @param pageNum  第几页
     * @param pageSize 每页大小
     * @param sn       SN码
     */
    PageVO<WaterDeviceFaultVO> page(Integer pageNum, Integer pageSize, String sn);

    boolean existsWith(Integer deviceId, String sn, Integer type, String filterType);

}
