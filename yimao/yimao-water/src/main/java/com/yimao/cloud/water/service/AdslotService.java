package com.yimao.cloud.water.service;

import com.yimao.cloud.pojo.dto.water.AdslotDTO;
import com.yimao.cloud.pojo.vo.PageVO;
import com.yimao.cloud.water.po.Adslot;

import java.util.List;
import java.util.Set;

public interface AdslotService {

    /**
     * 创建广告位
     */
    void save(Adslot adslot);

    void updateById(Adslot adslot);


    PageVO<AdslotDTO> queryListByCondition(Integer platform, Integer position, Integer forbidden, Integer pageNum, Integer pageSize);

    Set<String> selectAdslotName(Set<String> adslotIdList);

    int selectCount(Adslot adslot);

    String getAdslotName(Integer position);
}
