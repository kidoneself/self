package com.yimao.cloud.system.mapper;

import com.github.pagehelper.Page;
import com.yimao.cloud.pojo.dto.system.CompanyRegionDTO;
import com.yimao.cloud.pojo.dto.system.RegionMessageDTO;
import com.yimao.cloud.system.po.RegionMessage;
import tk.mybatis.mapper.common.Mapper;

/**
 * @author Lizhqiang
 * @date 2019/1/16
 */
public interface RegionMessageMapper extends Mapper<RegionMessage> {

    Page<RegionMessageDTO> pageQueryRegionMessage(RegionMessageDTO query);

    CompanyRegionDTO getCorporationStatus(Integer id);

    void update(Integer id);

    void reduceServiceArea(String region);

    void updateServiceState(String region, Integer state);
}
