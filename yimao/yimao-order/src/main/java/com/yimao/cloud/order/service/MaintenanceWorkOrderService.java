package com.yimao.cloud.order.service;

import com.github.pagehelper.Page;
import com.yimao.cloud.order.po.MaintenanceWorkOrder;
import com.yimao.cloud.pojo.dto.order.*;
import com.yimao.cloud.pojo.query.station.StationMaintenanceOrderQuery;
import com.yimao.cloud.pojo.vo.PageVO;

import java.util.List;
import java.util.Map;

/***
 * 功能描述:维护工单服务
 *
 * @auther: liu yi
 * @date: 2019/4/10 14:10
 */
public interface MaintenanceWorkOrderService {

    /***
     * 功能描述:根据id获取维护工单
     *
     * @param: [id]
     * @auther: liu yi
     * @date: 2019/4/10 14:11
     * @return: com.yimao.cloud.pojo.dto.order.MaintenanceWorkOrderDTO
     */
    MaintenanceWorkOrderDTO getWorkOrderMaintenanceById(String id);

    /***
     * 功能描述:根据workCode获取维护工单
     *
     * @param: [workCode]
     * @auther: liu yi
     * @date: 2019/7/12 11:04
     * @return: com.yimao.cloud.pojo.dto.order.MaintenanceWorkOrderDTO
     */
    MaintenanceWorkOrderDTO getWorkOrderMaintenanceByWorkCode(String workCode);

    /***
     * 功能描述:根据条件查询维护工单
     *
     * @param: [sncode, state, workOrderCompleteStatus]
     * @auther: liu yi
     * @date: 2019/4/10 14:11
     * @return: java.util.List<com.yimao.cloud.pojo.dto.order.WaterDeviceWorkOrderMaintenanceDTO>
     */
    List<MaintenanceWorkOrderDTO> getWorkOrderMaintenanceBySnCode(String sncode, Integer state, String workOrderCompleteStatus, Integer source);

    /***
     * 功能描述:创建维护工单
     *
     * @param: [dto]
     * @auther: liu yi
     * @date: 2019/4/10 14:12
     * @return: void
     */
    void createWorkOrderMaintenance(MaintenanceWorkOrderDTO dto);

    /***
     * 功能描述:统计维护工单
     *
     * @param: [engineerId, state]
     * @auther: liu yi
     * @date: 2019/4/10 14:12
     * @return: int
     */
    int getWorkOrderMaintenanceCount(Integer engineerId, Integer state);

    /***
     * 功能描述:根据id检查维护工单是否存在
     *
     * @param: [id]
     * @auther: liu yi
     * @date: 2019/4/10 14:12
     * @return: java.lang.Boolean
     */
    Boolean exists(String id);

    /***
     * 功能描述:更新维护工单
     *
     * @param: [waterDeviceWorkOrderMaintenance]
     * @auther: liu yi
     * @date: 2019/4/10 14:13
     * @return: void
     */
    void updateWorkOrderMaintenance(MaintenanceWorkOrderDTO waterDeviceWorkOrderMaintenance);

    /***
     * 功能描述:系统后台编辑维护工单
     *
     * @param: [id, materielDetailIds, materielDetailNames]
     * @auther: liu yi
     * @date: 2019/6/26 17:08
     * @return: void
     */
    void editworkOrderMaintenanceBySystem(String id, String materielDetailIds, String materielDetailNames);

    /***
     * 功能描述:安装工app分页查询维护工单列表
     *
     * @param: [page, limit, state, distributorId, engineerId, search]
     * @auther: liu yi
     * @date: 2019/4/1 14:06
     * @return: com.yimao.cloud.pojo.vo.PageVO<com.yimao.cloud.pojo.dto.order.MaintenanceWorkOrderDTO>
     */
    List<MaintenanceDTO> listMaintenanceWorkOrderForClient(Integer pageNum, Integer pageSize, Integer state, String distributorId, Integer engineerId, String search, Double longitude, Double latitude);


    /***
     * 功能描述:分页查询维护工单列表
     *
     * @param: [pageNum, pageSize, queryDTO]
     * @auther: liu yi
     * @date: 2019/5/16 15:29
     * @return: com.yimao.cloud.pojo.vo.PageVO<com.yimao.cloud.pojo.dto.order.MaintenanceWorkOrderDTO>
     */
    PageVO<MaintenanceWorkOrderDTO> listMaintenanceWorkOrder(Integer pageNum, Integer pageSize, MaintenanceWorkOrderQueryDTO queryDTO);

    /***
     * 功能描述:检查没有完成的维护工单
     *
     * @param: [deviceSncode, materiels, engineerId]
     * @auther: liu yi
     * @date: 2019/4/10 15:46
     * @return: java.lang.Boolean
     */
    List<MaintenanceWorkOrderDTO> getNotCompleteWorkOrderMaintenance(String deviceSncode, Integer engineerId, Integer source);

    /***
     * 功能描述:根据id删除维护工单
     *
     * @param: [id]
     * @auther: liu yi
     * @date: 2019/5/16 15:40
     * @return: void
     */
    void deleteMaintenanceWorkOrderById(String id);

    /***
     * 功能描述:审核
     *
     * @param: [id, recordIds, effective]
     * @auther: liu yi
     * @date: 2019/5/16 17:02
     * @return: void
     */
    void auditMaintenanceWorkOrder(String id, Integer[] recordIds, Integer effective);

    /***
     * 功能描述:同步更新百得的最后完成时间
     *
     * @param: [hour]
     * @auther: liu yi
     * @date: 2019/5/30 17:28
     * @return: void
     */
    void updateLasteFinishTime(Integer hour);

    List<RenewDTO> getMaintenanceWorkOrderList(String completeTime, Integer engineerId, Integer timeType);

    Map<String, Integer> getMaintenanceWorkOrderCount(Integer engineerId);

    /**
     * 改约挂单
     *
     * @param maintenanceDTO 维护工单对象
     * @auther liuhao@yimaokeji.com
     */
    void hangMaintenanceWorkOrder(MaintenanceDTO maintenanceDTO);

    /**
     * 维护工单完成列表
     *
     * @param engineerId 安装工ID
     * @return 维护工单对象
     */
    Page<MaintenanceDTO> maintenanceWorkOrderCompleteList(Integer engineerId, Integer sortType, Integer pageNum, Integer pageSize);

    /**
     * 维护记录
     *
     * @param engineerId   安装工ID
     * @param deviceSncode SN
     * @return 维护工单对象
     */
    List<MaintenanceDTO> maintenanceWorkOrderRecordDetail(Integer engineerId, String deviceSncode);

    /**
     * 站务系统维护工单列表查询
     *
     * @param pageNum
     * @param pageSize
     * @param query
     * @return
     */
	PageVO<MaintenanceWorkOrderDTO> listMaintenanceOrderToStation(Integer pageNum, Integer pageSize,
			StationMaintenanceOrderQuery query);

    /**
     * 维护模块总工单数量查询
     * @param engineerId
     * @return
     */
    Integer getMaintenanceModelWorkOrderTotalCount(Integer engineerId);
}
