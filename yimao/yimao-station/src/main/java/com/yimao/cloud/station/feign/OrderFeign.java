package com.yimao.cloud.station.feign;

import com.yimao.cloud.base.constant.Constant;
import com.yimao.cloud.pojo.dto.order.*;
import com.yimao.cloud.pojo.dto.station.FlowStatisticsDTO;
import com.yimao.cloud.pojo.dto.station.ProductTabulateDataDTO;
import com.yimao.cloud.pojo.dto.station.RenewStatisticsDTO;
import com.yimao.cloud.pojo.dto.station.StationScheduleDTO;
import com.yimao.cloud.pojo.query.order.MoveWaterDeviceOrderQuery;
import com.yimao.cloud.pojo.query.order.RenewOrderQuery;
import com.yimao.cloud.pojo.query.order.WorkOrderBackQueryDTO;
import com.yimao.cloud.pojo.query.order.WorkRepairOrderQuery;
import com.yimao.cloud.pojo.query.station.StationMaintenanceOrderQuery;
import com.yimao.cloud.pojo.query.station.StationOrderGeneralSituationQuery;
import com.yimao.cloud.pojo.query.station.StationWaterDeviceQuery;
import com.yimao.cloud.pojo.query.station.StatisticsQuery;
import com.yimao.cloud.pojo.query.station.WorkOrderQuery;
import com.yimao.cloud.pojo.vo.PageVO;
import com.yimao.cloud.pojo.vo.order.EngineerWorkOrderVO;
import com.yimao.cloud.pojo.vo.order.OrderRenewVO;
import com.yimao.cloud.pojo.vo.station.OrderGeneralSituationVO;
import com.yimao.cloud.pojo.vo.station.ProductSalesStatusAndTwoCategoryPicResVO;
import com.yimao.cloud.pojo.vo.station.WorkRepairOrderVO;
import com.yimao.feign.configuration.MultipartSupportConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = Constant.MICROSERVICE_ORDER, configuration = MultipartSupportConfig.class)
public interface OrderFeign {

    /**
     * @param orderConditionDTO 订单查询条件
     * @param pageNum           当前页
     * @param pageSize          每页显示条数
     * @return java.lang.Object
     * @description 查询订单列表根据订单查询条件
     * @author zhilin.he
     * @date 2019/1/12 13:48
     */

    @RequestMapping(value = "/order/sub/{pageNum}/{pageSize}", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    PageVO<OrderSubDTO> orderSubList(@RequestBody OrderConditionDTO orderConditionDTO,
                                     @PathVariable(value = "pageNum") Integer pageNum,
                                     @PathVariable(value = "pageSize") Integer pageSize);

    /**
     * 退款列表
     *
     * @param pageNum
     * @param pageSize
     * @param dto
     * @return
     */
    @RequestMapping(value = "/refund/rental/{pageNum}/{pageSize}", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    PageVO<AfterSalesOrderDTO> rentalGoodsList(@PathVariable(value = "pageNum") Integer pageNum,
                                               @PathVariable(value = "pageSize") Integer pageSize,
                                               @RequestBody AfterSalesConditionDTO dto);

    /**
     * 订单管理-续费列表-列表
     */
    @PostMapping(value = "/order/renew/{pageNum}/{pageSize}", consumes = MediaType.APPLICATION_JSON_VALUE)
    PageVO<OrderRenewVO> getOrderRenewList(@RequestBody RenewOrderQuery query,
                                           @PathVariable(value = "pageNum") Integer pageNum,
                                           @PathVariable(value = "pageSize") Integer pageSize);

    /**
     * 订单管理-续费列表-详情
     */
    @GetMapping(value = "/order/renew/{id}/detail")
    OrderRenewVO getOrderRenewDetail(@PathVariable(value = "id") String id);

    /**
     * 订单-工单列表-列表
     *
     * @param pageNum
     * @param pageSize
     * @param query
     * @return
     */
    @PostMapping(value = "/workorder/station/{pageNum}/{pageSize}", consumes = MediaType.APPLICATION_JSON_VALUE)
    PageVO<WorkOrderResultDTO> getStationWorkOrderList(@PathVariable(value = "pageNum") Integer pageNum,
                                                       @PathVariable(value = "pageSize") Integer pageSize,
                                                       @RequestBody WorkOrderQuery query);


    /**
     * 描述：根据工单id获取工单信息
     *
     * @param workOrderId 工单ID
     **/
    @GetMapping(value = "/workorder/{workOrderId}")
    WorkOrderDTO getWorkOrderById(@PathVariable(value = "workOrderId") String workOrderId);

    /**
     * @param id 订单号
     * @return java.lang.Object
     * @description 根据订单号查询订单
     * @author zhilin.he
     * @date 2019/1/12 13:46
     */
    @GetMapping(value = "/order/sub/{id}/detail")
    OrderSubDTO findOrderDetailById(@PathVariable(value = "id") Long id);

    /**
     * 退款订单详情
     *
     * @param id 退款订单号
     * @return
     */
    @GetMapping(value = "/refund/rental/{id}")
    AfterSalesOrderDTO getSalesDetailById(@PathVariable(value = "id") Long id);

    /**
     * 流水统计-产品销量统计
     *
     * @param query
     * @return
     */
    @GetMapping(value = "/order/station/productAndHraSaleData", consumes = MediaType.APPLICATION_JSON_VALUE)
    FlowStatisticsDTO getProductAndHraSaleData(@RequestBody StatisticsQuery query);

    /**
     * 获取安装工的工单
     *
     * @param engineerId
     * @return
     */
    @GetMapping(value = "/workorder/engineer/{engineerId}")
    List<EngineerWorkOrderVO> listWorkOrderByEngineerId(@PathVariable(value = "engineerId") Integer engineerId);

    /**
     * 统计-续费统计-查询每天续费数（图表）
     *
     * @param waterDeviceQuery
     * @return
     */
    @PostMapping(value = "/order/renew/station/isRenewPicData", consumes = MediaType.APPLICATION_JSON_VALUE)
    List<RenewStatisticsDTO> getIsRenewPicData(@RequestBody StationWaterDeviceQuery waterDeviceQuery);

    /**
     * 控制台-待办事项(工单数与续费数）
     *
     * @param query
     * @return
     */
    @PostMapping(value = "/workorder/station/workerOrderAndRenewNum", consumes = MediaType.APPLICATION_JSON_VALUE)
    StationScheduleDTO getStationWorkerOrderAndRenewNum(@RequestBody WorkOrderQuery query);

    /**
     * 站务系统-统计-商品统计 -汇总数据（站务系统调用）
     *
     * @param query
     * @return
     */
    @GetMapping(value = "/order/station/getProductTabulateData", consumes = MediaType.APPLICATION_JSON_VALUE)
    List<ProductTabulateDataDTO> getProductStatisticsInfoToStation(@RequestBody StatisticsQuery query);

    /**
     * 站务系统-统计-商品统计-根据一级类目名称获取二级分类图表以及商品销售情况（站务系统调用）
     *
     * @param query
     * @param categoryName
     * @return
     */
    @GetMapping(value = "/order/station/getProductSalesStatusAndTwoCategoryPicRes", consumes = MediaType.APPLICATION_JSON_VALUE)
    ProductSalesStatusAndTwoCategoryPicResVO getProductSalesStatusAndTwoCategoryPicRes(@RequestBody StatisticsQuery query, @RequestParam("categoryName") String categoryName);

    /**
     * 站务系统-订单-概况（站务系统调用）
     *
     * @param query
     * @return
     */
    @GetMapping(value = "/order/station/generalSituation", consumes = MediaType.APPLICATION_JSON_VALUE)
    OrderGeneralSituationVO getOrderGeneralSituation(@RequestBody StationOrderGeneralSituationQuery query);

    /**
     * 站务系统 - 移机工单 - 更换安装工
     *
     * @param id          移机工单号
     * @param engineerId  替换原服务人员的安装工id
     * @param engineerIds 操作人管理的售后服务站门店下的安装工
     * @param type        1-更换拆机服务人员；2-更换装机服务人员
     * @param source      来源端
     * @param operator    操作人
     */
    @PostMapping(value = "/move/water/device/order/changeEngineer/{id}")
    Void changeEngineer(@PathVariable(value = "id") String id,
                        @RequestParam(value = "engineerId") Integer engineerId,
                        @RequestParam(value = "engineerIds") List<Integer> engineerIds,
                        @RequestParam(value = "type") Integer type,
                        @RequestParam(value = "source") Integer source,
                        @RequestParam(value = "operator") String operator);

    /**
     * 根据条件查询退机工单信息
     */
    @GetMapping(value = "/workorderBack/{pageNum}/{pageSize}", consumes = MediaType.APPLICATION_JSON_VALUE)
    PageVO<WorkOrderBackDTO> getWorkOrderBackList(@RequestBody WorkOrderBackQueryDTO query, @PathVariable(value = "pageNum") Integer pageNum, @PathVariable(value = "pageSize") Integer pageSize);

    /**
     * 描述：根据工单id获取退机工单信息
     *
     * @param id 退机工单ID
     **/
    @GetMapping(value = "/workorderBack/{id}")
    WorkOrderBackDTO getWorkOrderBackById(@PathVariable(value = "id") Integer id);

    /**
     * 站务系统 - 退机工单 - 更换安装工
     *
     * @param id          移机工单号
     * @param engineerId  替换原服务人员的安装工id
     * @param engineerIds 操作人管理的售后服务站门店下的安装工
     * @param source      来源端
     * @param operator    操作人
     */
    @PostMapping(value = "/workorderBack/changeEngineer/{id}")
    Void workOrderBackChangeEngineer(@PathVariable(value = "id") Integer id,
                                     @RequestParam(value = "engineerId") Integer engineerId,
                                     @RequestParam(value = "engineerIds") List<Integer> engineerIds,
                                     @RequestParam(value = "source") Integer source,
                                     @RequestParam(value = "operator") String operator);

    /**
     * 维修工单列表
     *
     * @param search
     * @param pageNum
     * @param pageSize
     * @return
     */
    @PostMapping(value = "/repair/workorder/{pageNum}/{pageSize}", consumes = MediaType.APPLICATION_JSON_VALUE)
    PageVO<WorkRepairOrderVO> pageRepairOrders(@RequestBody WorkRepairOrderQuery search,
                                               @PathVariable("pageNum") Integer pageNum,
                                               @PathVariable("pageSize") Integer pageSize);

    /**
     * 根据维修单号查询维修单
     *
     * @param workOrderNo
     * @return
     */
    @GetMapping(value = "/repair/workorder/getRepairOrderByWorkOrderNo")
    WorkRepairOrderVO getRepairOrderByWorkOrderNo(@RequestParam("workOrderNo") String workOrderNo);

    /**
     * 移机工单分页展示
     *
     * @param pageNum
     * @param pageSize
     * @param query
     * @return
     */
    @PostMapping(value = "/move/water/device/order/{pageNum}/{pageSize}", consumes = MediaType.APPLICATION_JSON_VALUE)
    PageVO<MoveWaterDeviceOrderDTO> pageMoveWaterDeviceOrder(@PathVariable(value = "pageNum") Integer pageNum,
                                                             @PathVariable(value = "pageSize") Integer pageSize,
                                                             @RequestBody MoveWaterDeviceOrderQuery query);

    @GetMapping(value = "/move/water/device/order/{id}")
    MoveWaterDeviceOrderDTO getMoveWaterDeviceOrderDetailsById(@PathVariable(value = "id") String id);

    /**
     * 维修更换安装工
     *
     * @param workOrderNo
     * @param engineerId
     */
    @PutMapping("/repair/workorder/replace")
    void replaceRepairEngineer(@RequestParam("workOrderNo") String workOrderNo,
                               @RequestParam("engineerId") Integer engineerId,
                               @RequestParam("sourceType") Integer sourceType,
                               @RequestParam("operator") String operator);

    /**
     * 维修工单详情
     *
     * @param id
     * @return
     */
    @GetMapping(value = "/order/maintenanceWorkOrder/{id}")
    Object getWorkOrderMaintenanceById(@PathVariable("id") String id);

    /**
     * 分页查询维护工单列表
     *
     * @param query
     * @param pageNum
     * @param pageSize
     * @return
     */
    @PostMapping(value = "/order/maintenanceWorkOrder/station/{pageNum}/{pageSize}", consumes = MediaType.APPLICATION_JSON_VALUE)
    PageVO<MaintenanceWorkOrderDTO> listMaintenanceOrderToStation(@RequestBody StationMaintenanceOrderQuery query,
                                                                  @PathVariable("pageNum") Integer pageNum,
                                                                  @PathVariable("pageSize") Integer pageSize);

}
