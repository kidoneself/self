package com.yimao.cloud.water.service;

import com.yimao.cloud.pojo.dto.water.WaterDeviceCostChangeRecordDTO;
import com.yimao.cloud.pojo.vo.PageVO;

public interface WaterDeviceCostChangeRecordService {

    /**
     * 查询水机计费方式修改记录（分页）
     *
     * @param pageNum  第几页
     * @param pageSize 每页大小
     * @param sn       SN码
     */
    PageVO<WaterDeviceCostChangeRecordDTO> page(Integer pageNum, Integer pageSize, String sn);

}
