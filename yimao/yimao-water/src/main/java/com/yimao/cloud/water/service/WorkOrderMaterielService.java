package com.yimao.cloud.water.service;


import com.yimao.cloud.pojo.dto.water.WaterDeviceWorkOrderMaterielDTO;

import java.util.List;

/***
 * 功能描述:工单耗材信息
 *
 * @auther: liu yi
 * @date: 2019/3/28 10:32
 */
public interface WorkOrderMaterielService{

    /***
     * 功能描述:根据id获取工单耗材信息
     *
     * @param: [id]
     * @auther: liu yi
     * @date: 2019/3/28 10:29
     * @return: com.yimao.cloud.pojo.dto.order.WaterDeviceWorkOrderMaterielDTO
     */
    WaterDeviceWorkOrderMaterielDTO getById(Integer id);

    /***
     * 功能描述:创建工单耗材信息
     *
     * @param: [entity]
     * @auther: liu yi
     * @date: 2019/3/28 10:29
     * @return: void
     */
    void create(WaterDeviceWorkOrderMaterielDTO entity);

    /***
     * 功能描述:变更工单耗材信息
     *
     * @param: [entity]
     * @auther: liu yi
     * @date: 2019/3/28 10:30
     * @return: void
     */
    void update(WaterDeviceWorkOrderMaterielDTO entity);

    /***
     * 功能描述:根据工单code获取工单耗材信息
     *
     * @param: [workcode, workOrderIndex]
     * @auther: liu yi
     * @date: 2019/3/28 10:30
     * @return: java.util.List<com.yimao.cloud.pojo.dto.order.WaterDeviceWorkOrderMaterielDTO>
     */
    List<WaterDeviceWorkOrderMaterielDTO> getWaterDeviceWorkOrderMaterielByWorkCode(String workCode, String workOrderIndex) ;

    /***
     * 功能描述:根据id删除工单耗材维修信息
     *
     * @param: [id]
     * @auther: liu yi
     * @date: 2019/3/28 10:31
     * @return: void
     */
    void delete(Integer id);

    /***
     * 功能描述:根据工单id删除工单耗材维修信息
     *
     * @param: [workcode, workOrderIndex]
     * @auther: liu yi
     * @date: 2019/3/28 10:31
     * @return: void
     */
    void deleteByWorkCode(String workCode, String workOrderIndex) ;

    /*boolean exists(String id);*/

    /***
     * 功能描述:批量创建工单耗材信息
     *
     * @param: [materiels, operationUser]
     * @auther: liu yi
     * @date: 2019/3/28 10:32
     * @return: void
     */
    void batchCreate(List<WaterDeviceWorkOrderMaterielDTO> materiels);
}
