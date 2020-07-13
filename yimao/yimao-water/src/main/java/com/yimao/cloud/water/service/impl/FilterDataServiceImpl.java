package com.yimao.cloud.water.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.yimao.cloud.base.utils.StringUtil;
import com.yimao.cloud.water.mapper.FilterDataMapper;
import com.yimao.cloud.water.po.FilterData;
import com.yimao.cloud.water.service.FilterDataService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;

/**
 * 描述：水机滤芯流量数据
 *
 * @Author Zhang Bo
 * @Date 2019/5/8
 */
@Service
public class FilterDataServiceImpl implements FilterDataService {

    @Resource
    private FilterDataMapper filterDataMapper;

    /**
     * 保存设备滤芯数据
     *
     * @param deviceId      设备ID
     * @param sncode        SN码
     * @param restFilterMap 设备传递的滤芯数据
     */
    @Override
    public FilterData save(Integer deviceId, String sncode, String restFilterMap) {
        FilterData query = new FilterData();
        query.setDeviceId(deviceId);
        FilterData filterData = filterDataMapper.selectOneByDeviceId(deviceId);
        if (StringUtil.isNotEmpty(restFilterMap)) {
            boolean flag = false;
            if (filterData == null) {
                filterData = new FilterData();
                filterData.setCreateTime(new Date());
                flag = true;
            }
            JSONObject json = JSONObject.parseObject(restFilterMap);
            filterData.setSn(sncode);
            filterData.setDeviceId(deviceId);
            filterData.setPpFlow(json.getInteger("PP"));
            filterData.setUdfFlow(json.getInteger("UDF"));
            filterData.setCtoFlow(json.getInteger("CTO"));
            filterData.setThreeFlow(json.getInteger("T33"));
            filterData.setRoFlow(json.getInteger("RO"));
            if (flag) {
                filterDataMapper.insert(filterData);
            } else {
                filterDataMapper.updateByPrimaryKeySelective(filterData);
            }
        }
        return filterData;
    }

}
