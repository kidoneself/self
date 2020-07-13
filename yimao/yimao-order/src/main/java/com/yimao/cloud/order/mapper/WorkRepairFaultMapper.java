package com.yimao.cloud.order.mapper;

import org.apache.ibatis.annotations.Param;

import com.github.pagehelper.Page;
import com.yimao.cloud.order.po.WorkRepairFault;

import tk.mybatis.mapper.common.Mapper;

public interface WorkRepairFaultMapper extends Mapper<WorkRepairFault>{

	Page<WorkRepairFault> listWorkRepairFault(Integer type);

	int faultRenameCount(@Param("faultName")String faultName, @Param("id")Integer id);

}