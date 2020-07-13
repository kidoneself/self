package com.yimao.cloud.water.service;

import com.yimao.cloud.pojo.dto.water.WaterDeviceDynamicCipherRecordDTO;
import com.yimao.cloud.pojo.vo.PageVO;
import com.yimao.cloud.water.po.WaterDeviceDynamicCipherConfig;
import com.yimao.cloud.water.po.WaterDeviceDynamicCipherRecord;

public interface WaterDeviceDynamicCipherService {

    void saveDynamicCipherRecord(WaterDeviceDynamicCipherRecord record);

    void updateDynamicCipherRecord(WaterDeviceDynamicCipherRecord record);

    WaterDeviceDynamicCipherRecord getDynamicCipherRecordBySnCode(String sn);

    PageVO<WaterDeviceDynamicCipherRecordDTO> pageDynamicCipherRecord(Integer pageNum, Integer pageSize, Integer engineerId);

    WaterDeviceDynamicCipherConfig getDynamicCipherConfig();

    void setDeviceAllDynamicCipherInValid(String sn);
}
