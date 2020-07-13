package com.yimao.cloud.order.mapper;

import com.github.pagehelper.Page;
import com.yimao.cloud.order.po.WorkOrderBack;
import com.yimao.cloud.pojo.dto.order.MapOrderDTO;
import com.yimao.cloud.pojo.dto.order.RenewDTO;
import com.yimao.cloud.pojo.dto.order.WorkOrderBackDTO;
import com.yimao.cloud.pojo.query.order.WorkOrderBackQueryDTO;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;
import java.util.Map;

public interface WorkOrderBackMapper extends Mapper<WorkOrderBack> {

	/**
	 * 根据条件查询安装工单信息
	 */
	Page<WorkOrderBackDTO> getWorkOrderBackList(WorkOrderBackQueryDTO workOrderBackQueryDTO);

	List<Map<String, Object>> getWorkOrderBackCountByEngineerId(Integer engineerId);

    List<RenewDTO> queryWorkOrderBackList(@Param("completeTime") String completeTime, @Param("engineerId") Integer engineerId, @Param("timeType") Integer timeType);

	Integer getWorkOrderBackCount(@Param("engineerId") Integer engineerId, @Param("status") Integer status);

	int updateStationInfoByPCR(WorkOrderBackDTO updateWorkOrderBack);

	int updateEngineerInfoByPCR(WorkOrderBackDTO updateWorkOrderBack);

	int updateEngineerInfo(WorkOrderBackDTO wob);

	Integer getBackModelTotalCount(@Param("engineerId") Integer engineerId);

	List<MapOrderDTO> getBackWaterDeviceOrder(Integer engineerId);

	/***
	 * 安装工app新版本-查询工单列表
	 * @param query
	 * @return
	 */
	/*Page<WorkOrderRsDTO> getWorkOrderListForEngineer(WorkOrderReqDTO query);*/
}
