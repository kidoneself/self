package com.yimao.cloud.product.mapper;

import com.yimao.cloud.product.po.VirtualProductConfig;
import tk.mybatis.mapper.common.Mapper;

public interface VirtualProductConfigMapper extends Mapper<VirtualProductConfig> {

    Integer update(VirtualProductConfig virtualProductConfig);

}
