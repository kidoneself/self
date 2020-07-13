package com.yimao.cloud.user.controller;

import com.yimao.cloud.base.exception.BadRequestException;
import com.yimao.cloud.base.exception.NotFoundException;
import com.yimao.cloud.pojo.dto.order.AgentSalesOverviewDTO;
import com.yimao.cloud.pojo.dto.order.PayRecordDTO;
import com.yimao.cloud.pojo.dto.order.SalesStatsDTO;
import com.yimao.cloud.pojo.dto.order.SalesStatsQueryDTO;
import com.yimao.cloud.pojo.dto.order.SalesStatsResultDTO;
import com.yimao.cloud.pojo.dto.station.FlowStatisticsDTO;
import com.yimao.cloud.pojo.dto.user.DistributorOrderAllInfoDTO;
import com.yimao.cloud.pojo.dto.user.DistributorOrderDTO;
import com.yimao.cloud.pojo.dto.user.DistributorOrderExportDTO;
import com.yimao.cloud.pojo.dto.user.DistributorProtocolDTO;
import com.yimao.cloud.pojo.dto.user.SalePerformRankDTO;
import com.yimao.cloud.pojo.dto.user.UserCompanyApplyDTO;
import com.yimao.cloud.pojo.query.station.StatisticsQuery;
import com.yimao.cloud.pojo.query.user.DistributorOrderQueryDTO;
import com.yimao.cloud.pojo.vo.PageVO;
import com.yimao.cloud.pojo.vo.station.DistributorOrderVO;
import com.yimao.cloud.user.service.DistributorOrderService;
import com.yimao.cloud.user.service.DistributorProtocolService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * @author Lizhqiang
 * @date 2018/12/17
 */
@RestController
@Api(tags = "DistributorOrderController")
public class DistributorOrderController {
    @Resource
    private DistributorOrderService distributorOrderService;
    @Resource
    private DistributorProtocolService distributorProtocolService;

    /**
     * 新增经销商订单(主业务系统使用)
     *
     * @param dto
     * @author Liu Yi
     * @date 2019/1/9
     */
    @PostMapping(value = "user/distributor/order", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "新增经销商订单")
    @ApiImplicitParam(name = "dto", value = "经销商DTO", required = true, dataType = "DistributorOrderDTO", paramType = "body")
    public Object insert(@RequestBody DistributorOrderDTO dto) {
        distributorOrderService.insertOrderBySystem(dto);
        return ResponseEntity.noContent().build();
    }

    /**
     * 经销商订单提交支付凭证
     *
     * @param dto
     * @author Liu Yi
     * @date 2019/1/9
     */
    @PutMapping(value = "user/distributor/order/{id}/credential")
    @ApiOperation(value = "经销商订单提交支付凭证")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "订单id", required = true, dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "payType", value = "支付类型：1-微信；2-支付宝；3-POS机；4-转账；", required = true, dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "payCredential", value = "支付凭证", required = true, dataType = "String", paramType = "query")
    })
    public Object submitCredential(@PathVariable(value = "id") Long id,
                                   @RequestParam(value = "payType") Integer payType,
                                   @RequestParam(value = "payCredential") String payCredential) {
        distributorOrderService.submitCredential(id,payType,payCredential);
        return ResponseEntity.noContent().build();
    }

    /**
     * 升级经销商订单
     *
     * @param dto
     * @author Liu Yi
     * @date 2019/1/9
     */
    @PostMapping(value = "user/distributor/order/upgrade", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "升级经销商订单")
    @ApiImplicitParam(name = "dto", value = "经销商DTO", required = true, dataType = "DistributorOrderDTO", paramType = "body")
    public Object upgradeOrder(@RequestBody DistributorOrderDTO dto) {
        Map<String, Object> map=distributorOrderService.upgradeOrder(dto);
        return ResponseEntity.ok(map);
    }

    /**
     * 续费经销商订单
     *
     * @param dto
     * @author Liu Yi
     * @date 2019/1/9
     */
    @PostMapping(value = "user/distributor/order/renew", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "续费经销商订单")
    @ApiImplicitParam(name = "dto", value = "经销商DTO", required = true, dataType = "DistributorOrderDTO", paramType = "body")
    public Object renewOrder(@RequestBody DistributorOrderDTO dto) {
        Map<String, Object> map=distributorOrderService.renewOrder(dto);
        return ResponseEntity.ok(map);
    }

    /**
     * 分页查询经销商订单
     *
     * @param pageNum
     * @param pageSize
     * @param query
     */
    @PostMapping(value = "distributor/order/{pageSize}/{pageNum}")
    @ApiOperation(value = "分页查询经销商订单")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "页码", defaultValue = "1", required = true, dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "pageSize", value = "每页显示数量", defaultValue = "10", required = true, dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "query", value = "经销商DTO", required = true, dataType = "DistributorOrderQueryDTO", paramType = "body")
    })
    public Object page(@PathVariable(value = "pageNum") Integer pageNum,
                       @PathVariable(value = "pageSize") Integer pageSize,
                       @RequestBody DistributorOrderQueryDTO query) {
        PageVO<DistributorOrderDTO> page = distributorOrderService.page(query, pageNum, pageSize);
        if (page == null) {
            throw new NotFoundException("未找到经销商订单信息。");
        }
        return ResponseEntity.ok(page);
    }

    /**
     * @description   导出经销商订单
     * @author Liu Yi
     * @date 2019/8/26 9:57
     * @param
     * @return java.lang.Object
     */
    @GetMapping(value = "distributor/order/export")
    @ApiOperation(value = "导出经销商订单")
    @ApiImplicitParam(name = "orderQuery", value = "订单查询实体", dataType = "DistributorOrderQueryDTO", paramType = "body")
    public Object listExport(@RequestBody  DistributorOrderQueryDTO orderQuery) {
        List<DistributorOrderExportDTO> list = distributorOrderService.listOrderExport(orderQuery);
        return ResponseEntity.ok(list);
    }

    /**
     * 经销商订单详情
     *
     * @param id
     */
    @GetMapping(value = "distributor/order/{id}/basis")
    @ApiOperation(value = "经销商订单详情")
    @ApiImplicitParam(name = "id", value = "订单号", required = true, dataType = "Long", paramType = "path")
    public Object findBasisDistributorOrderById(@PathVariable(value = "id") Long id) {
        DistributorOrderDTO order = distributorOrderService.findBasicDistributorOrderById(id);
        return ResponseEntity.ok(order);
    }

    /**
     * 经销商订单详情
     *
     * @param orderId
     */
    @GetMapping(value = "distributor/order/{orderId}")
    @ApiOperation(value = "经销商订单详情")
   @ApiImplicitParam(name = "orderId", value = "订单号", required = true, dataType = "Long", paramType = "path")
    public Object findDistributorOrderById(@PathVariable(value = "orderId") Long orderId) {
        DistributorOrderAllInfoDTO order = distributorOrderService.findDistributorOrderAllInfoByOrderId(orderId);

        return ResponseEntity.ok(order);
    }

    /**
     * 查询订单所需价格
     *
     * @param origLevel
     * @param destLevel
     * @param distributorId
     * @author Liu Yi
     * @date 2019/1/9
     */
    @GetMapping(value = "user/distributor/order/price")
    @ApiOperation(value = "查询升级所需价格")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "distributorId", value = "经销商id", required = true, dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "origLevel", value = "原经销商等级", required = true, dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "destLevel", value = "升级经销商等级", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "orderType", value = "订单类型:0-注册、1-升级、2-续费", required = true, dataType = "Long", paramType = "query")
    })
    public Object getPrice(@RequestParam(value = "distributorId") Integer distributorId,
                           @RequestParam(value = "origLevel") Integer origLevel,
                           @RequestParam(value = "destLevel",required = false) Integer destLevel,
                           @RequestParam(value = "orderType") Integer orderType) {
        BigDecimal price = distributorOrderService.getOrderPrice(origLevel, destLevel, distributorId,orderType);

        return ResponseEntity.ok(price);
    }

    /**
     * 待办事项统计（企业信息审核，支付审核）
     *
     * @author hhf
     * @date 2019/3/23
     */
    @GetMapping(value = "/distributor/overview")
    @ApiOperation(value = "待办事项统计（企业信息审核，支付审核）")
    public Object distributorOrderOverview() {
        Map<String, Long> map = distributorOrderService.distributorOrderOverview();
        return ResponseEntity.ok(map);
    }

    /**
     * @description   获取签署合同信息
     * @author Liu Yi
     * @date 2019/8/22 10:32
     * @param
     * @return com.yimao.cloud.pojo.dto.user.UserDTO
     */
    @GetMapping(value = "/distributor/app/contract/{orderId}/preview")
    @ApiImplicitParam(name = "orderId", value = "订单id", required = true, dataType = "Long", paramType = "path")
    public Object previewContract(@PathVariable(value = "orderId" ) Long orderId) {
        //获取合同信息
        DistributorProtocolDTO dto=distributorProtocolService.getDistributorProtocolByOrderId(orderId);

        return ResponseEntity.ok(dto);
    }

    /**
     * @description   经销商app签署合同
     * @author Liu Yi
     * @date 2019/8/22 10:32
     * @param
     * @return com.yimao.cloud.pojo.dto.user.UserDTO
     */
    @PatchMapping(value = "/distributor/app/contract/{orderId}/sign")
    @ApiImplicitParam(name = "orderId", value = "订单id", required = true, dataType = "Long", paramType = "path")
    public DistributorOrderDTO signContract(@PathVariable(value = "orderId" ) Long orderId) {
       //变更合同状态
        if(orderId == null){
            throw new BadRequestException("经销商订单id不能为空！");
        }

        distributorProtocolService.sign(orderId);
        return null;
    }

    /**
     * @description   获取经销商升级或者续费提示信息
     * @author Liu Yi
     * @date 2019/8/23 13:31
     * @param orderType 订单类型1-升级 2-续费
     * @param distributorLevel 升级级别，续费时不用传
     * @return java.lang.String
     */
    @GetMapping(value = "/distributor/app/order/remindMessage")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "distributorLevel", value = "升级等级", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "orderType", value = "订单类型：1-升级、2-续费", defaultValue = "1", required = true, dataType = "Long", paramType = "query")
    })
    public Object getDistributorOrderRemindMessage(@RequestParam(value = "orderType") Integer orderType,
                                                   @RequestParam(value = "distributorLevel",required = false) Integer distributorLevel){
        String message= distributorOrderService.getDistributorOrderRemindMessage(orderType,distributorLevel);
        return ResponseEntity.ok(message);
    }

    /**
     * @description  根据创建人（经销商）查询未完成订单
     * @author Liu Yi
     * @date 14:53 2019/8/20
     * @param distributorId 经销商id
     * @return java.util.List<com.yimao.cloud.user.po.DistributorOrder>
     **/
    @GetMapping(value = "/distributor/app/order/unfinished")
    @ApiImplicitParam(name = "distributorId", value = "创建者", required = true, dataType = "Long", paramType = "query")
    public Object unfinishedOrderListByCreator(@RequestParam(value = "distributorId") Integer distributorId){
        List<DistributorOrderDTO> list=distributorOrderService.unfinishedOrderListByCreator(distributorId);
        return ResponseEntity.ok(list);
    }

    /**
     * 上传营业执照
     *
     * @return
     */
    @RequestMapping(value = "/distributor/businessLicenseImage", method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ApiOperation(value = "上传营业执照", notes = "上传营业执照")
    public ResponseEntity uploadBusinessLicenseImage(@RequestPart("image") MultipartFile file) {
        String imageUrl = distributorOrderService.uploadBusinessLicenseImage(file);
        return ResponseEntity.ok(imageUrl);
    }

    /**
     * 重新提交企业审核
     *
     * @param dto
     * @author Liu Yi
     * @date 2019/1/9
     */
    @PostMapping(value = "user/distributor/order/companyApply/renewCommit", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "重新提交企业审核")
    @ApiImplicitParam(name = "dto", value = "企业", required = true, dataType = "UserCompanyApplyDTO", paramType = "body")
    public Object renewCommitCompanyApply(@RequestBody UserCompanyApplyDTO dto) {
        distributorOrderService.renewCommitCompanyApply(dto);
        return ResponseEntity.noContent().build();
    }

    /**
     * @description   经销商订单支付回调
     * @author Liu Yi
     * @date 2019/9/16 17:49
     * @param record
     * @return void
     */
    @PostMapping(value = "user/distributor/order/payCallback", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "经销商订单支付回调")
    @ApiImplicitParam(name = "record", value = "支付记录", required = true, dataType = "PayRecordDTO", paramType = "body")
    public Object  distributorOrderPayCallback(@RequestBody PayRecordDTO record){
        distributorOrderService.distributorOrderPayCallback(record);
        return ResponseEntity.noContent().build();
    }

    
    /**
     *站务系统- 流水统计-经销商订单销量
     * @param query
     * @return
     */
    @GetMapping("/distributor/station/distributorOrderData")
	public FlowStatisticsDTO getDistributorOrderData(@RequestBody StatisticsQuery query) {
    	return distributorOrderService.getDistributorOrderData(query);
    }
    
    /**
     * 用户--根据条件查询经销商订单列表(站务系统调用)
     *
     * @return query
     */
    @PostMapping("/distributor/order/station/list/{pageNum}/{pageSize}")
    public Object stationGetDistributorOrderInfo(@PathVariable(value = "pageNum") Integer pageNum,
                                                 @PathVariable(value = "pageSize") Integer pageSize,
                                                 @RequestBody DistributorOrderQueryDTO query) {
        PageVO<DistributorOrderVO> page = distributorOrderService.stationGetDistributorOrderInfo(query, pageNum, pageSize);
        return ResponseEntity.ok(page);
    }

    /**
     *app-招商销售额柱状图、增长趋势图、各类型占比图
     * @param query
     * @return
     */
    @PostMapping(value = "/distributor/investment/statsInfo", consumes = MediaType.APPLICATION_JSON_VALUE)
	public SalesStatsResultDTO getInvestmentSalesStats(@RequestBody SalesStatsQueryDTO query) {
    	return distributorOrderService.getInvestmentSalesStats(query);
    }

    /**
     *app-(根据年、月、日)统计销售额
     * @param query
     * @return
     */
    @PostMapping(value = "/distributor/investment/sale/statsInfo", consumes = MediaType.APPLICATION_JSON_VALUE)
	public List<SalesStatsDTO> getInvestmentSalesAmountStats(@RequestBody SalesStatsQueryDTO query) {
    	return distributorOrderService.getInvestmentSalesAmountStats(query);
    }

    /**
     *app-(根据年、月)统计各类型销售增长趋势统计
     * @param query
     * @return
     */
    @PostMapping(value = "/distributor/increase/trend/statsInfo", consumes = MediaType.APPLICATION_JSON_VALUE)
	public List<SalesStatsDTO> getDistributorIncreaseTrendStats(@RequestBody SalesStatsQueryDTO query) {
    	return distributorOrderService.getDistributorIncreaseTrendStats(query);
    }

    /****
     * 根据日期获取获取销售业绩排行数据
     * @param startTime
     * @param endTime
     * @return
     */
    @GetMapping(value = "/distributor/perform/data")
    public List<SalePerformRankDTO> getDistributorPerformRank(@RequestParam(value = "startTime") String startTime,
                                                              @RequestParam(value = "endTime") String endTime) {
		return distributorOrderService.getDistributorPerformRank(startTime,endTime);

    }



    /**
     * @param
     * @description 经销商app-经营报表-汇总统计
     * @author Liu Yi
     * @date 2020/4/27 15:21
     */
    @PostMapping(value = "/distributor/investment/home/report", consumes = MediaType.APPLICATION_JSON_VALUE)
    public AgentSalesOverviewDTO getOrderSalesHomeReport(@RequestBody SalesStatsQueryDTO query) {
        return distributorOrderService.getOrderSalesHomeReport(query);
    }

    /**
     * @param
     * @description 经销商app-经营报表-累计销售金额统计表
     * @author Liu Yi
     * @date 2020/4/27 15:21
     */
    @PostMapping(value = "/distributor/investment/trend/report", consumes = MediaType.APPLICATION_JSON_VALUE)
    public List<SalesStatsDTO> getOrderSalesTotalReport(@RequestBody SalesStatsQueryDTO query) {
        return distributorOrderService.getOrderSalesTotalReport(query);
    }


    /**
     * 获取经销商订单类型名称
     * @return
     */
    @GetMapping(value = "/distributor/order/type/name")
    public List<String> queryDistributorOrderTypeNames(){
        return distributorOrderService.queryDistributorOrderTypeNames();
    }
}