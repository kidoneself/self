package com.yimao.cloud.water.service;

import com.yimao.cloud.pojo.dto.water.FilterSettingDTO;
import com.yimao.cloud.pojo.query.water.FilterSettingQuery;
import com.yimao.cloud.pojo.vo.PageVO;
import com.yimao.cloud.water.po.FilterSetting;

public interface FilterSettingService {
    void save(FilterSetting setting);

    void update(FilterSetting setting);

    PageVO<FilterSettingDTO> page(Integer pageNum, Integer pageSize, FilterSettingQuery query);

    FilterSettingDTO getById(Integer id);

    FilterSetting getByPCR(String province, String city, String region, String deviceModel);
}
