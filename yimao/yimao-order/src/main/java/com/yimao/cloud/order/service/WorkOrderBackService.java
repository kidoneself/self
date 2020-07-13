package com.yimao.cloud.order.service;

import com.yimao.cloud.pojo.dto.order.RenewDTO;
import com.yimao.cloud.pojo.dto.order.WorkOrderBackDTO;
import com.yimao.cloud.pojo.query.order.WorkOrderBackQueryDTO;
import com.yimao.cloud.pojo.vo.PageVO;

import java.util.List;
import java.util.Map;

/**
 * @description 退机服务接口
 * @author Liu Yi
 * @date 2020/6/22 11:13
 */
public interface WorkOrderBackService {

	/**
	 * 创建退机工单
	 *
	 * @param workOrderBack 工单对象
	 */
	void createWorkOrderBack(WorkOrderBackDTO workOrderBack);

	/**
	 * 编辑退机工单
	 * @param workOrderBack 工单对象
	 */
	void updateWorkOrderBack(WorkOrderBackDTO workOrderBack);

	/**
	 * @description   根据安装工获取退机工单数量
	 * @author Liu Yi
	 * @date 2020/6/30 9:16
	 * @param
	 * @return java.util.List<java.util.Map<java.lang.String,java.lang.Object>>
	 */
	List<Map<String, Object>> getWorkOrderBackCountByEngineerId();

	/**
	 * 根据条件查询退机工单信息
	 */
	PageVO<WorkOrderBackDTO> getWorkOrderBackList(WorkOrderBackQueryDTO workOrderQueryDTO, Integer pageNum, Integer pageSize);

	/**
	 * 描述：根据工单id获取退机工单信息
	 *
	 * @param id 退单工单ID
	 **/
	WorkOrderBackDTO getWorkOrderBackById(Integer id);
	/**
	 * @description   完成退机工单
	 * @author Liu Yi
	 * @date 2020/6/30 10:39
	 * @param
	 * @return void
	 */
	void finishWorkOrderBack(Integer id, String snCode);

	List<RenewDTO> queryWorkOrderBackList(String completeTime, Integer engineerId,Integer timeType);

    Map<String, Integer> getWorkOrderBackCount(Integer engineerId);

    void workOrderBackChangeEngineer(Integer id, Integer engineerId, List<Integer> engineerIds, Integer source, String operator);

    Integer getBackModelTotalCount(Integer engineerId);
}
