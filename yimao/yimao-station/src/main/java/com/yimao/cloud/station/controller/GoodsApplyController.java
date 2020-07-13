package com.yimao.cloud.station.controller;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.yimao.cloud.base.cache.UserCache;
import com.yimao.cloud.base.enums.PermissionTypeEnum;
import com.yimao.cloud.base.enums.StationCompanyGoodsApplyStateEnum;
import com.yimao.cloud.base.enums.StationQueryEnum;
import com.yimao.cloud.base.exception.BadRequestException;
import com.yimao.cloud.pojo.dto.station.GoodsLoanApplyDTO;
import com.yimao.cloud.pojo.dto.system.StationCompanyDTO;
import com.yimao.cloud.pojo.dto.system.StationCompanyGoodsApplyDTO;
import com.yimao.cloud.pojo.query.station.GoodsLoanApplyQuery;
import com.yimao.cloud.pojo.vo.PageVO;
import com.yimao.cloud.station.aop.annotation.StationQuery;
import com.yimao.cloud.station.constant.StationConstant;
import com.yimao.cloud.station.feign.SystemFeign;
import com.yimao.cloud.station.mapper.StockLoanApplyMapper;
import com.yimao.cloud.station.service.GoodsLoanApplyService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Date;

/**
 * 物资
 *
 * @author Liu Long Jie
 * @date 2020-6-18 14:37:27
 */
@RestController
@Slf4j
@Api(tags = "GoodsApplyController")
public class GoodsApplyController {

    @Resource
    private SystemFeign systemFeign;
    @Resource
    private UserCache userCache;    
    @Resource
    private GoodsLoanApplyService goodsLoanApplyService;    
    @Resource
	private StockLoanApplyMapper stockLoanApplyMapper;
    
    
    @PostMapping(value = "/goods/apply/station/list/{pageNum}/{pageSize}")
    @ApiOperation(value = "站务系统分页查询物资申请列表", notes = "站务系统分页查询物资申请列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "categoryId", value = "分类id", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "startTime", value = "申请时间（开始）", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "endTime", value = "申请时间（结束）", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "pageNum", value = "分页页码", defaultValue = "1", required = true, dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "pageSize", value = "分页大小", defaultValue = "10", required = true, dataType = "Long", paramType = "path")
    })
    public Object pageGoodsApplyStation(@PathVariable(value = "pageNum") Integer pageNum,
                                        @PathVariable(value = "pageSize") Integer pageSize,
                                        @RequestParam(value = "categoryId", required = false) Integer categoryId,
                                        @RequestParam(value = "startTime", required = false) Date startTime,
                                        @RequestParam(value = "endTime", required = false) Date endTime) {
        Integer stationCompanyId = userCache.getJWTInfo().getStationCompanyId();
        if (stationCompanyId == StationConstant.SUPERCOMPANYID) {
            //超级管理员
            return null;
        }
        return systemFeign.pageGoodsApplyStation(pageNum, pageSize, stationCompanyId, categoryId, startTime, endTime);
    }

    @PostMapping(value = "/goods/apply/station/history/list/{pageNum}/{pageSize}")
    @ApiOperation(value = "站务系统分页查询物资申请历史记录列表", notes = "站务系统分页查询物资申请历史记录列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "categoryId", value = "分类id", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "status", value = "申请状态 0-已取消 5-已完成", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "startTime", value = "申请时间（开始）", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "endTime", value = "申请时间（结束）", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "pageNum", value = "分页页码", defaultValue = "1", required = true, dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "pageSize", value = "分页大小", defaultValue = "10", required = true, dataType = "Long", paramType = "path")
    })
    public Object pageGoodsApplyStationHistory(@PathVariable(value = "pageNum") Integer pageNum,
                                               @PathVariable(value = "pageSize") Integer pageSize,
                                               @RequestParam(value = "categoryId", required = false) Integer categoryId,
                                               @RequestParam(value = "status", required = false) Integer status,
                                               @RequestParam(value = "startTime", required = false) Date startTime,
                                               @RequestParam(value = "endTime", required = false) Date endTime) {

        Integer stationCompanyId = userCache.getJWTInfo().getStationCompanyId();
        if (stationCompanyId == StationConstant.SUPERCOMPANYID) {
            //超级管理员
            return null;
        }
        return systemFeign.pageGoodsApplyStationHistory(pageNum, pageSize, stationCompanyId, categoryId, status, startTime, endTime);
    }

    @PostMapping(value = "/goods/apply/anewSubmit")
    @ApiOperation(value = "重新提交物资申请", notes = "重新提交物资申请")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "dto", value = "物资申请信息", required = true, dataType = "StationCompanyGoodsApplyDTO", paramType = "body")
    })
    public void goodsApplyDeliverGoods(@RequestBody StationCompanyGoodsApplyDTO dto) {
        if (dto.getId() == null) {
            throw new BadRequestException("物资申请订单号不能为空！");
        }
        dto.setApplicantAccount(userCache.getCurrentAdminRealName());
        Integer stationCompanyId = userCache.getJWTInfo().getStationCompanyId();
        if (stationCompanyId == StationConstant.SUPERCOMPANYID) {
            throw new BadRequestException("超级管理员不能进行申请物资操作！");
        }
        StationCompanyDTO stationCompany = systemFeign.getStationCompanyById(stationCompanyId);
        dto.setStationCompanyId(stationCompanyId);
        dto.setStationCompanyName(stationCompany.getName());
        if (stationCompany.getType() == 3) {
            //公司类型为仅售后承包,物资申请交由售后审核
            dto.setIsAfterAudit(true);
        } else {
            dto.setIsAfterAudit(false);
        }
        systemFeign.goodsApplyAnewSubmit(dto);
    }


    @PostMapping(value = "/goods/apply/save")
    @ApiOperation(value = "提交物资申请", notes = "提交物资申请")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "dto", value = "物资申请信息", required = true, dataType = "StationCompanyGoodsApplyDTO", paramType = "body")
    })
    public void goodsApplySave(@RequestBody StationCompanyGoodsApplyDTO dto) {
        dto.setApplicantAccount(userCache.getCurrentAdminRealName());
        Integer stationCompanyId = userCache.getJWTInfo().getStationCompanyId();
        if (stationCompanyId == StationConstant.SUPERCOMPANYID) {
            throw new BadRequestException("超级管理员不能进行申请物资操作！");
        }
        StationCompanyDTO stationCompany = systemFeign.getStationCompanyById(stationCompanyId);
        dto.setStationCompanyId(stationCompanyId);
        dto.setStationCompanyName(stationCompany.getName());
        if (stationCompany.getType() == 3) {
            //公司类型为仅售后承包,物资申请交由售后审核
            dto.setIsAfterAudit(true);
        } else {
            dto.setIsAfterAudit(false);
        }
        Date now = new Date();
        dto.setStatus(StationCompanyGoodsApplyStateEnum.WAITING_AUDIT.value);
        dto.setApplyTime(now);
        dto.setCreateTime(now);
        systemFeign.goodsApplySave(dto);
    }

    @PostMapping(value = "/goods/apply/confirm/{id}")
    @ApiOperation(value = "确认收货", notes = "确认收货")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "申请单号", required = true, dataType = "String", paramType = "path"),
            @ApiImplicitParam(name = "confirmImg", value = "确认收货的图片", required = true, dataType = "String", paramType = "query")
    })
    public void goodsApplyConfirm(@PathVariable(value = "id") String id,
                                  @RequestParam(value = "confirmImg") String confirmImg) {

        systemFeign.goodsApplyConfirm(id, confirmImg);
    }

    @PostMapping(value = "/goods/apply/cancel/{id}")
    @ApiOperation(value = "取消物资申请", notes = "取消物资申请")
    @ApiImplicitParam(name = "id", value = "申请单号", required = true, dataType = "String", paramType = "path")
    public void goodsApplyCancel(@PathVariable(value = "id") String id) {

        systemFeign.goodsApplyCancel(id);
    }
    
    
    @PostMapping(value = "/goods/loan")
    @ApiOperation(value = "申请物资借调")
    @ApiImplicitParam(name = "dto", value = "物资借调类", required = true, dataType = "GoodsLoanApplyDTO", paramType = "body")
    public Object goodsLoanApply(@RequestBody GoodsLoanApplyDTO dto) {
    	
    	goodsLoanApplyService.goodsLoanApply(dto);
    	
    	return ResponseEntity.noContent().build();
    }
    
    @StationQuery(value = StationQueryEnum.ListQuery)
    @PostMapping(value = "/goods/loan/list/{pageNum}/{pageSize}")
    @ApiOperation(value = "物资借调申请列表")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "pageNum", value = "分页页码", defaultValue = "1", required = true, dataType = "Long", paramType = "path"),
        @ApiImplicitParam(name = "pageSize", value = "分页大小", defaultValue = "10", required = true, dataType = "Long", paramType = "path"),
        @ApiImplicitParam(name = "query", value = "列表筛选项", dataType = "GoodsLoanApplyQuery", paramType = "body")
    })
    public PageVO<GoodsLoanApplyDTO> goodsLoanList(@PathVariable("pageNum") Integer pageNum,
    											   @PathVariable("pageSize") Integer pageSize,
    											   @RequestBody GoodsLoanApplyQuery query) {
    	
    	PageHelper.startPage(pageNum, pageSize);
    	
    	Page<GoodsLoanApplyDTO> page= stockLoanApplyMapper.listStockLoanApply(query);
    	
    	return new PageVO<>(pageNum, page);
    }
    
    @PutMapping(value = "/goods/loan/check/{id}")
    @ApiOperation(value = "物资借调审核")
    public Object goodsLoanCheck(@PathVariable("id")Integer id,Integer status) {
    	
    	goodsLoanApplyService.goodsLoanCheck(id,status);
    	
    	return ResponseEntity.noContent().build();
    }
}
