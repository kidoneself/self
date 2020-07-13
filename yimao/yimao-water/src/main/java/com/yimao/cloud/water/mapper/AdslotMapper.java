package com.yimao.cloud.water.mapper;

import com.github.pagehelper.Page;
import com.yimao.cloud.pojo.dto.water.AdslotDTO;
import com.yimao.cloud.water.po.Adslot;
import tk.mybatis.mapper.common.Mapper;

public interface AdslotMapper extends Mapper<Adslot> {

    Page<AdslotDTO> pageQueryList(AdslotDTO adslotDTO);

    Integer selectMaxId();
}
