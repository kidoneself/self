package com.yimao.cloud.water.service;

import com.yimao.cloud.pojo.dto.water.ServiceStationWaterDeviceDTO;
import com.yimao.cloud.pojo.vo.PageVO;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface ServiceStationWaterDeviceService {

    // /**
    //  * 创建服务站水机
    //  */
    // void save();

    PageVO<ServiceStationWaterDeviceDTO> queryListByCondition(Integer areaId, String model, Boolean online, Integer connectionType,
                                                              String keyWord, String snCode,
                                                              Date beginTime, Date endTime, Integer pageNum, Integer pageSize);

    int count(List<Map<String, String>> areaList, List<String> models, Integer connectionType);

    ServiceStationWaterDeviceDTO selectBySn(String snCode);
}
