package com.yimao.cloud.system.controller;

import com.yimao.cloud.base.constant.RabbitConstant;
import com.yimao.cloud.base.enums.ExportRecordStatus;
import com.yimao.cloud.base.exception.BadRequestException;
import com.yimao.cloud.base.msg.CommResult;
import com.yimao.cloud.pojo.dto.hra.*;
import com.yimao.cloud.pojo.dto.product.ProductDTO;
import com.yimao.cloud.pojo.dto.system.ExportRecordDTO;
import com.yimao.cloud.pojo.query.hra.HraPhysicalQuery;
import com.yimao.cloud.pojo.vo.PageVO;
import com.yimao.cloud.pojo.vo.hra.HraCardVO;
import com.yimao.cloud.system.feign.HraFeign;
import com.yimao.cloud.system.feign.ProductFeign;
import com.yimao.cloud.system.service.ExportRecordService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @description: 管理系统HRA评估模块
 * @author: yu chunlei
 * @create: 2019-02-12 10:35:16
 **/
@RestController
@Slf4j
@Api(tags = "HraTicketController")
public class HraTicketController {

    @Resource
    private HraFeign hraFeign;
    @Resource
    private ProductFeign productFeign;

    @Resource
    private RabbitTemplate rabbitTemplate;

    @Resource
    private ExportRecordService exportRecordService;


    /**
     * HRA评估-预约列表(已预约的)
     *
     * @param pageNum
     * @param pageSize
     * @param beginTime     预约开始时间
     * @param endTime       预约结束时间
     * @param userSource    用户来源
     * @param mobile        手机号
     * @param ticketNo      评估卡
     * @param province      省
     * @param city          市
     * @param region        区
     * @param name          姓名
     * @param reserveStatus 预约状态  1-预约中 2-预约到期
     * @return
     * @author hhf
     * @date 2018/1/11
     */
    @GetMapping(value = {"/ticket/reservation/{pageNum}/{pageSize}"})
    @ApiOperation(value = "预约列表(已预约的)", notes = "预约列表(已预约的)")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "分页页码", defaultValue = "1", required = true, dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "pageSize", value = "分页大小", defaultValue = "10", required = true, dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "beginTime", value = "开始时间", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "endTime", value = "结束时间", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "userSource", value = "用户来源", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "mobile", value = "电话", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "ticketNo", value = "体检卡号", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "province", value = "省", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "city", value = "市", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "region", value = "区", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "name", value = "姓名", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "reserveStatus", value = "预约状态  1-预约中 2-预约到期", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "userId", value = "用户e家号", dataType = "Long", paramType = "query")
    })
    public ResponseEntity<PageVO<HraTicketResultDTO>> listReserve(@PathVariable(value = "pageNum") Integer pageNum,
                                                                  @PathVariable(value = "pageSize") Integer pageSize,
                                                                  @RequestParam(required = false) String beginTime,
                                                                  @RequestParam(required = false) String endTime,
                                                                  @RequestParam(required = false) Integer userSource,
                                                                  @RequestParam(required = false) String mobile,
                                                                  @RequestParam(required = false) String ticketNo,
                                                                  @RequestParam(required = false) String province,
                                                                  @RequestParam(required = false) String city,
                                                                  @RequestParam(required = false) String region,
                                                                  @RequestParam(required = false) String name,
                                                                  @RequestParam(required = false) Integer reserveStatus,
                                                                  @RequestParam(required = false) Integer userId) {


        PageVO<HraTicketResultDTO> page = hraFeign.listTicket(pageNum, pageSize, beginTime, endTime, userSource, mobile, ticketNo, province, city, region, name, reserveStatus, reserveStatus, null, userId, null);
        return ResponseEntity.ok(page);
    }


    /**
     * 评估列表(已体检的)
     *
     * @param pageNum       分页页码
     * @param pageSize      分页大小
     * @param beginTime     开始时间
     * @param endTime       结束时间
     * @param userSource    用户来源
     * @param mobile        手机号
     * @param ticketNo      评估卡
     * @param province      省
     * @param city          市
     * @param region        区
     * @param name          姓名
     * @param reserveStatus 预约状态
     * @param hasUpload     是否上传
     * @param userId        用户ID
     * @param ticketType    体检卡型号
     * @Description: HRA评估-评估列表(已体检的)
     * @author ycl
     * @Return: org.springframework.http.ResponseEntity
     * @Create: 2019/2/12 11:21
     */
    @GetMapping(value = {"/ticket/use/{pageNum}/{pageSize}"})
    @ApiOperation(value = "评估列表(已体检的)", notes = "评估列表(已体检的)")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "分页页码", defaultValue = "1", required = true, dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "pageSize", value = "分页大小", defaultValue = "10", required = true, dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "beginTime", value = "开始时间", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "endTime", value = "结束时间", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "userSource", value = "用户来源", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "mobile", value = "电话", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "ticketNo", value = "体检卡号", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "province", value = "省", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "city", value = "市", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "region", value = "区", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "name", value = "姓名", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "reserveStatus", value = "预约状态  1-预约中 2-预约到期", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "hasUpload", value = "是否上传", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "userId", value = "用户ID", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "ticketType", value = "体检卡型号", dataType = "String", paramType = "query")
    })
    public ResponseEntity<PageVO<HraTicketResultDTO>> list(@PathVariable(value = "pageNum") Integer pageNum,
                                                           @PathVariable(value = "pageSize") Integer pageSize,
                                                           @RequestParam(required = false) String beginTime,
                                                           @RequestParam(required = false) String endTime,
                                                           @RequestParam(required = false) String userSource,
                                                           @RequestParam(required = false) String mobile,
                                                           @RequestParam(required = false) String ticketNo,
                                                           @RequestParam(required = false) String province,
                                                           @RequestParam(required = false) String city,
                                                           @RequestParam(required = false) String region,
                                                           @RequestParam(required = false) String name,
                                                           @RequestParam(required = false) Integer reserveStatus,
                                                           @RequestParam(required = false) Integer hasUpload,
                                                           @RequestParam(required = false) Integer userId,
                                                           @RequestParam(required = false) String ticketType) {
        PageVO<HraTicketResultDTO> page = hraFeign.list(pageNum, pageSize, beginTime, endTime, userSource, mobile, ticketNo, province, city, region, name, 1, reserveStatus, hasUpload, userId, ticketType);
        return ResponseEntity.ok(page);
    }


    /**
     * 评估卡管理-体检卡列表
     *
     * @author ycl
     * @param: province 省
     * @Return: org.springframework.http.ResponseEntity
     * @Create: 2019/2/19 9:45
     */
    @PostMapping(value = {"/ticket/physical/{pageNum}/{pageSize}"})
    @ApiOperation(value = "评估卡管理-体检卡列表", notes = "评估卡管理-体检卡列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "query", value = "查询信息", dataType = "HraQueryDTO", paramType = "body"),
            @ApiImplicitParam(name = "pageNum", value = "分页页码", defaultValue = "1", required = true, dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "pageSize", value = "分页大小", defaultValue = "10", required = true, dataType = "Long", paramType = "path")
    })
    public ResponseEntity<PageVO<HraPhysicalDTO>> physical(@RequestBody HraQueryDTO query,
                                                           @PathVariable(value = "pageNum") Integer pageNum,
                                                           @PathVariable(value = "pageSize") Integer pageSize) {

        PageVO<HraPhysicalDTO> dtoPageVO = hraFeign.physical(query, pageNum, pageSize);
        return ResponseEntity.ok(dtoPageVO);
    }


    /**
     * 评估卡管理-F卡管理(服务站专用卡)
     *
     * @param pageNum     第几页
     * @param pageSize    每页显示条数
     * @Description: 评估卡管理-F卡管理
     * @author ycl
     * @Create: 2019/2/14 10:08
     */
    @PostMapping(value = {"/ticket/special/{pageNum}/{pageSize}"})
    @ApiOperation(value = "评估卡管理-F卡管理", notes = "评估卡管理-F卡管理(服务站专用卡)")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "query", value = "查询信息", dataType = "HraQueryDTO", paramType = "body"),
            @ApiImplicitParam(name = "pageNum", value = "分页页码", defaultValue = "1", required = true, dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "pageSize", value = "分页大小", defaultValue = "10", required = true, dataType = "Long", paramType = "path")
    })
    public Object allotTicketList(@RequestBody HraQueryDTO query,
                                  @PathVariable(value = "pageNum") Integer pageNum,
                                  @PathVariable(value = "pageSize") Integer pageSize) {
        PageVO<HraCardVO> ticketDTOPage = hraFeign.allotTicket(query, pageNum, pageSize);
        return ResponseEntity.ok(ticketDTOPage);
    }

    /**
     * @Description: 体检卡列表-分配卡
     * @author ycl
     * @Return: org.springframework.http.ResponseEntity
     * @Create: 2019/2/19 14:01
     */
    @PostMapping("/assign")
    @ApiOperation(value = "体检卡列表-分配卡", notes = "体检卡列表-分配卡")
    @ApiImplicitParam(name = "hraAssignDTO", value = "分配卡信息", required = true, dataType = "HraAssignDTO", paramType = "body")
    public ResponseEntity assignCard(@RequestBody HraAssignDTO hraAssignDTO) {
        if (hraAssignDTO.getUserId() == null) {
            log.error("体检卡分配失败,E家号不能为空");
            throw new BadRequestException("E家号不能为空");
        }
        if (hraAssignDTO.getCardCount() == null || hraAssignDTO.getCardCount() < 1) {
            log.error("体检卡分配失败,发放数量不能低于一张");
            throw new BadRequestException("发放数量不能低于一张");
        }
        if (hraAssignDTO.getCardCount() > 1000) {
            log.error("体检卡分配失败,发放数量不能大于1000张");
            throw new BadRequestException("发放数量不能大于1000张");
        }
        if (hraAssignDTO.getProductId() == null) {
            log.error("体检卡分配失败,产品不能为空");
            throw new BadRequestException("产品不能为空");
        }
        hraFeign.assignCard(hraAssignDTO);
        return ResponseEntity.noContent().build();
    }


    /**
     * @Description: 体检卡列表-编辑
     * @author ycl
     * @param: id
     * @Return: org.springframework.http.ResponseEntity
     * @Create: 2019/2/27 15:06
     */
    @PutMapping("/ticket/physical")
    @ApiOperation(value = "体检卡列表-编辑", notes = "体检卡列表-编辑")
    @ApiImplicitParam(name = "dto", value = "分配卡信息", required = true, dataType = "HraTicketDTO", paramType = "body")
    public ResponseEntity update(@RequestBody HraTicketDTO hraTicketDTO) {
        hraFeign.update(hraTicketDTO);
        return ResponseEntity.noContent().build();
    }


    /**
     * @Description: 选择体检卡-M、Y下的商品
     * @author ycl
     * @param: * @param
     * @Return: org.springframework.http.ResponseEntity
     * @Create: 2019/2/23 16:03
     */
    @GetMapping("/product/MYFCard")
    @ApiOperation(value = "查询M/Y/F卡下的商品", notes = "查询M/Y/F卡下的商品")
    public ResponseEntity getProductByYandM(@RequestParam("categoryName") String categoryName) {
        List<ProductDTO> productDTOList = productFeign.findMYCardProductList(categoryName);
        return ResponseEntity.ok(productDTOList);
    }


    @GetMapping("/product/FCard")
    @ApiOperation(value = "查询F卡的电子卡券产品", notes = "查询F卡的电子卡券产品")
    @ApiImplicitParams({})
    public ResponseEntity findFCardProductList() {
        List<ProductDTO> list = productFeign.findFCardProductList();
        return ResponseEntity.ok(list);
    }


    /**
     * @Description: F卡管理-分配F卡
     * @author ycl
     * @param: stationId
     * @Return: org.springframework.http.ResponseEntity
     * @Create: 2019/2/19 16:18
     */
    @PostMapping("/ticket/allot")
    @ApiOperation(value = "F卡管理-分配F卡", notes = "F卡管理-分配F卡")
    @ApiImplicitParam(name = "dto", value = "分配卡信息", required = true, dataType = "HraFCardDTO", paramType = "body")
    public ResponseEntity allot(@RequestBody HraFCardDTO hraFCardDTO) {
        if (hraFCardDTO.getCardCount() > 1000) {
            log.error("体检卡分配失败,发放数量不能大于1000张");
            throw new BadRequestException("发放数量不能大于1000张");
        }
        hraFeign.allot(hraFCardDTO);
        return ResponseEntity.noContent().build();
    }


    /**
     * @Description: F卡管理-是否禁用功能
     * @author ycl
     * @param: hraAllotTicketDTO
     * @Return: org.springframework.http.ResponseEntity
     * @Create: 2019/2/28 14:59
     */
    @PutMapping("/ticket/status")
    @ApiOperation(value = "F卡管理-是否禁用功能", notes = "F卡管理-是否禁用功能")
    @ApiImplicitParam(name = "dto", value = "分配卡信息", required = true, dataType = "HraAllotTicketDTO", paramType = "body")
    public ResponseEntity updateStatus(@RequestBody HraAllotTicketDTO hraAllotTicketDTO) {
        hraFeign.updateStatus(hraAllotTicketDTO);
        return ResponseEntity.noContent().build();
    }







    /*====================================================================导出=========================================================================================================*/
    /*====================================================================导出==========================================================================================================*/
    /*====================================================================导出==========================================================================================================*/
    /*====================================================================导出===========================================================================================================*/

    @PostMapping(value = "/reservation/export")
    @ApiOperation(value = "预约列表导出", notes = "预约列表导出")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "ticketType", value = "体检卡型号", dataType = "String", paramType = "query")
    })
    public Object listReserve(@RequestBody(required = false) HraExportQuery query) {
        query.setFlag(2);//预约列表导出
    	//保存导出记录，如果短时间操作多次，会提示操作过于频繁
        String url = "/reservation/export";
        ExportRecordDTO record = exportRecordService.save(url, "预约列表");
        // HraExportQuery query = new HraExportQuery(beginTime, endTime, userSource, mobile, ticketNo, province, city, region, name, reserveStatus, userId, ticketType);
        //导出记录添加成功，并且状态为"等待导出"，才入列
        if (record.getStatus() == ExportRecordStatus.WAITING.value) {
            //异步队列执行导出
            Map<String, Object> map = new HashMap<>();
            map.put("exportRecordDTO", record);
            map.put("query", query);
            rabbitTemplate.convertAndSend(RabbitConstant.EXPORT_ACTION_HRA, map);
        }
        return CommResult.ok(record.getId());
    	/*List<HraExportReservationDTO> resultList = hraFeign.exportReservation(beginTime, endTime, userSource, mobile, ticketNo, province, city, region, name, reserveStatus, userId, ticketType);
        if (CollectionUtil.isEmpty(resultList)) {
            throw new NotFoundException("未找到数据");
        }
        String header = "预约列表";
        String[] beanProperties = new String[]{"stationProvince", "stationCity", "stationRegion", "ticketNo", "userId", "customerUserName", "customerIdCard", "customerPhone",
                "customerSex", "customerHeight", "customerWeight", "customerBirthDate", "reserveTime", "reserveStatus", "reserveFrom"};
        String[] titles = new String[]{"预约区域（省）", "预约区域（市）", "预约区域（区）", "体检卡号", "用户ID", "体检人", "身份证号",
                "手机号", "性别", "身高", "体重", "出生日期", "预约体检日期", "预约状态", "用户来源"};
        boolean boo = ExcelUtil.exportSXSSF(resultList, header, titles.length - 1, titles, beanProperties, response);
        if (boo) {
            return ResponseEntity.noContent().build();
        }
        throw new YimaoException("导出失败");*/
    }

    /**
     * @Author ycl
     * @Description 评估列表导出
     * @Date 19:33 2019/11/26
     * @Param
    **/
    @PostMapping(value = {"/ticket/used/export"})
    @ApiOperation(value = "评估列表导出", notes = "评估列表导出")
    @ApiImplicitParam(name = "query", value = "评估列表导出", dataType = "HraExportQuery", paramType = "body")
    public Object listUsed(@RequestBody(required = false) HraExportQuery query) {
        query.setFlag(1);//评估列表导出
        //保存导出记录，如果短时间操作多次，会提示操作过于频繁
        String url = "/ticket/used/export";
        ExportRecordDTO record = exportRecordService.save(url, "评估列表");

        //导出记录添加成功，并且状态为"等待导出"，才入列
        if (record.getStatus() == ExportRecordStatus.WAITING.value) {
            //异步队列执行导出
            Map<String, Object> map = new HashMap<>();
            map.put("exportRecordDTO", record);
            map.put("query", query);
            rabbitTemplate.convertAndSend(RabbitConstant.EXPORT_ACTION_HRA, map);
        }
        return CommResult.ok(record.getId());
        /*List<HraExportReservationDTO> resultList = hraFeign.getTicketInfoToExport(beginTime, endTime, userSource, mobile, ticketNo, province, city, region, name, reserveStatus, userId, ticketType);
        if (CollectionUtil.isEmpty(resultList)) {
            throw new NotFoundException("未找到数据");
        }
        String header = "评估列表";
        String[] beanProperties = new String[]{"stationProvince", "stationCity", "stationRegion", "ticketNo", "userId", "customerUserName", "customerIdCard", "customerPhone",
                "customerSex", "customerHeight", "customerWeight", "customerBirthDate", "examDate", "ticketStatus", "ticketType", "reserveFrom"};
        String[] titles = new String[]{"评估区域（省）", "评估区域（市）", "评估区域（区）", "体检卡号", "用户ID", "体检人", "身份证号",
                "手机号", "性别", "身高", "体重", "出生日期", "实际体检日期", "体检卡状态", "体检卡型号", "用户来源"};
        boolean boo = ExcelUtil.exportSXSSF(resultList, header, titles.length - 1, titles, beanProperties, response);
        if (boo) {
            return ResponseEntity.noContent().build();
        }
        throw new YimaoException("导出失败");*/
    }

    /**
     * @Author ycl
     * @Description 评估卡管理-体检卡导出
     * @Date 19:33 2019/11/26
     * @Param
    **/
    @PostMapping(value = {"/ticket/physical/export"})
    @ApiOperation(value = "评估卡管理-体检卡导出", notes = "评估卡管理-体检卡导出")
    @ApiImplicitParam(name = "query", value = "评估卡管理-体检卡导出", dataType = "HraQueryDTO", paramType = "body")
    public Object exportPhysical(@RequestBody(required = false) HraQueryDTO query) {

        //保存导出记录，如果短时间操作多次，会提示操作过于频繁
        String url = "/ticket/physical/export";
        ExportRecordDTO record = exportRecordService.save(url, "体检卡列表");

        //导出记录添加成功，并且状态为"等待导出"，才入列
        if (record.getStatus() == ExportRecordStatus.WAITING.value) {
            //异步队列执行导出
            Map<String, Object> map = new HashMap<>();
            map.put("exportRecordDTO", record);
            map.put("query", query);
            rabbitTemplate.convertAndSend(RabbitConstant.EXPORT_ACTION_HRA, map);
        }
        return CommResult.ok(record.getId());

        /*List<HraExportPhysicalDTO> resultList = hraFeign.exportPhysical(province, city, region, stationName, currentUserId, cardId, userType, minSurplus, maxSurplus, ticketStatus, beginTime, endTime, cardType);
        if (CollectionUtil.isEmpty(resultList)) {
            throw new NotFoundException("未找到数据");
        }
        String header = "评估卡";
        String[] beanProperties = new String[]{"createUserId", "currentUserId", "cardType", "ticketStatus",
                "endTime", "setTime", "reserveFrom", "orderId", "pay", "payTime"};
        String[] titles = new String[]{"创建时用户ID", "当前用户ID", "体检卡型号", "体检卡状态",
                "到期日期", "创建时间", "分配端", "订单号", "支付状态", "支付时间"};
        boolean boo = ExcelUtil.exportSXSSF(resultList, header, titles.length - 1, titles, beanProperties, response);
        if (boo) {
            return ResponseEntity.noContent().build();
        }
        throw new YimaoException("导出失败");*/
    }


    /**
     * F卡导出
     */
    @PostMapping(value = {"/ticket/special/export"})
    @ApiOperation(value = "F卡导出", notes = "F卡导出")
    @ApiImplicitParam(name = "query", value = "评估卡管理-F卡导出", dataType = "HraQueryDTO", paramType = "body")
    public Object exportSpecialTicket(@RequestBody(required = false) HraQueryDTO query) {
        //保存导出记录，如果短时间操作多次，会提示操作过于频繁
        String url = "/ticket/special/export";
        ExportRecordDTO record = exportRecordService.save(url, "F卡列表");

        //导出记录添加成功，并且状态为"等待导出"，才入列
        if (record.getStatus() == ExportRecordStatus.WAITING.value) {
            //异步队列执行导出
            Map<String, Object> map = new HashMap<>();
            map.put("exportRecordDTO", record);
            map.put("query", query);
            rabbitTemplate.convertAndSend(RabbitConstant.EXPORT_ACTION_HRA, map);
        }
        return CommResult.ok(record.getId());

        /*List<HraAllotTicketExportDTO> resultList = hraFeign.exportSpecialTicket(province, city, region, stationName, isExpire, minSurplus, maxSurplus, state, beginTime, endTime, ticketNo);
        if (CollectionUtil.isEmpty(resultList)) {
            throw new NotFoundException("未找到数据");
        }
        String header = "F卡导出";
        String[] beanProperties = new String[]{"stationProvince", "stationCity", "stationRegion", "stationName", "ticketNo", "days", "validEndTime", "expired", "total",
                "useCount", "selfStation", "createTime", "forbidden"};
        String[] titles = new String[]{"门店区域（省）", "门店区域（市）", "门店区域（区）", "服务站门店名称", "体检卡号", "有效期", "到期时间",
                "是否到期", "总可用次数", "剩余可用次数", "是否限制仅该服务站使用", "分配时间", "是否禁用"};
        boolean boo = ExcelUtil.exportSXSSF(resultList, header, titles.length - 1, titles, beanProperties, response);
        if (boo) {
            return ResponseEntity.noContent().build();
        }
        throw new YimaoException("导出失败");*/
    }
}
