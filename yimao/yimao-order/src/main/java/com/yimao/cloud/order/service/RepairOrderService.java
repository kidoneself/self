package com.yimao.cloud.order.service;

import java.util.Map;

import com.yimao.cloud.pojo.dto.order.WorkRepairOrderDTO;
import com.yimao.cloud.pojo.query.order.WorkRepairOrderQuery;
import com.yimao.cloud.pojo.vo.PageVO;
import com.yimao.cloud.pojo.vo.station.WorkRepairOrderVO;

public interface RepairOrderService {

	void createAppRepairOrder(WorkRepairOrderDTO dto);

	void createSystemRepairOrder(WorkRepairOrderDTO dto);

	PageVO<WorkRepairOrderVO> page(Integer pageNum, Integer pageSize, WorkRepairOrderQuery search);

	WorkRepairOrderVO processRepairOrderChange(WorkRepairOrderDTO dto);

	WorkRepairOrderVO continueRepairServiveInfo(Integer id, Integer step);

	void createDevicePushRepairOrder(WorkRepairOrderDTO dto);
	
	Map<String, Integer> getRepairOrderCount(Integer engineerId);

	void replaceRepairEngineer(String workOrderNo, Integer engineerIdm,Integer sourceType,String operator);

	Integer getRepairModelTotalCount(Integer engineerId);
}
