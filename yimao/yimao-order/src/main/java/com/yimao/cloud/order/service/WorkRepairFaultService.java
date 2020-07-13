package com.yimao.cloud.order.service;

import com.yimao.cloud.order.po.WorkRepairFault;
import com.yimao.cloud.pojo.vo.PageVO;

public interface WorkRepairFaultService {

	void create(WorkRepairFault fault);

	PageVO<WorkRepairFault> page(Integer pageNum, Integer pageSize, Integer type);

	void update(WorkRepairFault fault);

	void delete(Integer id);

}
