package com.yimao.cloud.water.service;

import com.yimao.cloud.base.enums.WorkOrderTypeEnum;
import com.yimao.cloud.pojo.dto.water.WaterDeviceRepairFactFaultDescribeInfoDTO;
import com.yimao.cloud.pojo.vo.PageVO;
import com.yimao.cloud.water.po.WaterDeviceRepairFactFaultDescribeInfo;

import java.util.List;
/***
 * 功能描述:设备故障信息及解决方式服务
 *
 * @param:
 * @auther: liu yi
 * @date: 2019/4/10 14:42
 * @return:
 */
public interface WaterDeviceRepairFactFaultDescribeInfoService {
    /***
     * 功能描述:根据id查询解决措施
     *
     * @param: [id]
     * @auther: liu yi
     * @date: 2019/4/10 14:42
     * @return: com.yimao.cloud.pojo.dto.order.WaterDeviceRepairFactFaultDescribeInfo
     */
    WaterDeviceRepairFactFaultDescribeInfo getById(Integer id);

    /***
     * 功能描述:创建
     *
     * @param: [entity]
     * @auther: liu yi
     * @date: 2019/4/10 14:43
     * @return: void
     */
    void create(WaterDeviceRepairFactFaultDescribeInfoDTO entity);

    /***
     * 功能描述:根据id删除
     *
     * @param: [id]
     * @auther: liu yi
     * @date: 2019/4/10 14:43
     * @return: void
     */
    void delete(Integer id);

    /***
     * 功能描述:根据workCode删除
     *
     * @param: [workCode]
     * @auther: liu yi
     * @date: 2019/4/10 14:43
     * @return: void
     */
    void deleteByWorkCode(String workCode);

    /***
     * 功能描述:根据id查询是否存在
     *
     * @param: [id]
     * @auther: liu yi
     * @date: 2019/4/10 14:43
     * @return: boolean
     */
    boolean exists(Integer id);

    /***
     * 功能描述:分页查询
     *
     * @param: [pageNum, pageSize, id, workCode]
     * @auther: liu yi
     * @date: 2019/4/10 14:44
     * @return: com.yimao.cloud.pojo.vo.PageVO<com.yimao.cloud.pojo.dto.order.WaterDeviceRepairFactFaultDescribeInfoDTO>
     */
    PageVO<WaterDeviceRepairFactFaultDescribeInfoDTO> page(Integer pageNum, Integer pageSize, Integer id, String workCode);

    /***
     * 功能描述:查询列表
     *
     * @param: [id, workCode]
     * @auther: liu yi
     * @date: 2019/4/10 14:44
     * @return: java.util.List<com.yimao.cloud.pojo.dto.order.WaterDeviceRepairFactFaultDescribeInfoDTO>
     */
    List<WaterDeviceRepairFactFaultDescribeInfoDTO> list(Integer id, String workCode);

    /***
     * 功能描述:批量创建
     *
     * @param: [factFaultInfos, deviceId, sncode, workcode, workOrderTypeEnum, operationUserId]
     * @auther: liu yi
     * @date: 2019/4/10 14:44
     * @return: void
     */
    //void create(String factFaultInfos, String deviceId, String sncode, String workcode, WorkOrderTypeEnum workOrderTypeEnum, Integer operationUserId);
}
