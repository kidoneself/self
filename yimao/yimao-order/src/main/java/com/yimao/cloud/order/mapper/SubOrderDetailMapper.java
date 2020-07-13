package com.yimao.cloud.order.mapper;

import org.apache.ibatis.annotations.Param;

import com.yimao.cloud.order.po.SubOrderDetail;
import com.yimao.cloud.pojo.dto.order.SubOrderDetailDTO;

import tk.mybatis.mapper.common.Mapper;

public interface SubOrderDetailMapper extends Mapper<SubOrderDetail> {
    SubOrderDetail selectProductInfoById(@Param("id") Long id);

	int updateEngineerInfo(SubOrderDetailDTO sod);
}
