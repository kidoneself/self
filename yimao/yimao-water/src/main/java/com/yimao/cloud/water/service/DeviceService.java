package com.yimao.cloud.water.service;


import com.yimao.cloud.pojo.query.water.WaterDeviceQuery;
import com.yimao.cloud.pojo.vo.PageVO;
import com.yimao.cloud.pojo.vo.water.WaterDeviceVO;

import java.util.Date;

public interface DeviceService {

    PageVO<WaterDeviceVO> queryListByCondition(Integer pageNum, Integer pageSize, WaterDeviceQuery query, Date beginTime, Date endTime);

}
