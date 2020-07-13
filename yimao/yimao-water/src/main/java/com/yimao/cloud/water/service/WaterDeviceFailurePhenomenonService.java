package com.yimao.cloud.water.service;


import com.yimao.cloud.base.enums.WorkOrderTypeEnum;
import com.yimao.cloud.pojo.dto.water.WaterDeviceFailurePhenomenonDTO;

import java.util.List;

/***
 * 功能描述:工单故障原因记录服务
 *
 * @auther: liu yi
 * @date: 2019/4/10 14:38
 */
public interface WaterDeviceFailurePhenomenonService {
    /***
     * 功能描述:根据工单id获取工单故障原因记录
     *
     * @param: [workCode, workOrderTypeEnum]
     * @auther: liu yi
     * @date: 2019/4/10 14:38
     * @return: java.util.List<com.yimao.cloud.pojo.dto.order.WaterDeviceFailurePhenomenonDTO>
     */
    List<WaterDeviceFailurePhenomenonDTO> getByWorkCode(String workCode, WorkOrderTypeEnum workOrderTypeEnum);

    /***
     * 功能描述:创建工单故障原因记录
     *
     * @param: [list, workCode, workOrderTypeEnum]
     * @auther: liu yi
     * @date: 2019/4/10 14:38
     * @return: void
     */
    void batchSave(List<WaterDeviceFailurePhenomenonDTO> list, String workCode);

    /***
     * 功能描述:根据工单id删除工单故障原因记录
     *
     * @param: [workCode, workOrderType]
     * @auther: liu yi
     * @date: 2019/4/10 14:39
     * @return: void
     */
    void delete(String workCode, String workOrderType);
}
