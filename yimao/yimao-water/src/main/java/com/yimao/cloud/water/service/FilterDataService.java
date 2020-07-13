package com.yimao.cloud.water.service;

import com.yimao.cloud.water.po.FilterData;

public interface FilterDataService {

    /**
     * 保存设备滤芯数据
     *
     * @param deviceId      设备ID
     * @param sncode        SN码
     * @param restFilterMap 设备传递的滤芯数据
     */
    FilterData save(Integer deviceId, String sncode, String restFilterMap);
}
