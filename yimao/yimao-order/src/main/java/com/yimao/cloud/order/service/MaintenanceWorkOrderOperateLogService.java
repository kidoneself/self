package com.yimao.cloud.order.service;

import com.yimao.cloud.pojo.dto.order.MaintenanceWorkOrderOperateLogDTO;

import java.util.List;

/***
 * 功能描述:
 *
 * @param: 维护工单记录服务
 * @auther: liu yi
 * @date: 2019/5/15 11:03
 * @return: 
 */
public interface MaintenanceWorkOrderOperateLogService {
    /***
     * 功能描述:创建维护工单
     *
     * @param: [maintenanceWorkOrderOperateLogDTO]
     * @auther: liu yi
     * @date: 2019/5/15 10:58
     * @return: void
     */
    void save(MaintenanceWorkOrderOperateLogDTO maintenanceWorkOrderOperateLogDTO);
    
    /***
     * 功能描述:根据id获取详情
     *
     * @param: [id]
     * @auther: liu yi
     * @date: 2019/5/15 10:57
     * @return: com.yimao.cloud.pojo.dto.order.WorkOrderMaintenanceOperateLogDTO
     */
    MaintenanceWorkOrderOperateLogDTO getMaintenanceWorkOrderOperateLogById(Integer id);

    /***
     * 功能描述:根据维护id查询相关操作记录
     *
     * @param: [maintenanceWorkOrderId]
     * @auther: liu yi
     * @date: 2019/5/15 15:40
     * @return: java.util.List<com.yimao.cloud.pojo.dto.order.MaintenanceWorkOrderOperateLogDTO>
     */
    List<MaintenanceWorkOrderOperateLogDTO> getListByMaintenanceWorkOrderId(String maintenanceWorkOrderId);

}
