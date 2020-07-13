package com.yimao.cloud.system.controller;

import com.yimao.cloud.base.exception.BadRequestException;
import com.yimao.cloud.base.utils.StringUtil;
import com.yimao.cloud.pojo.dto.system.StationCompanyGoodsApplyDTO;
import com.yimao.cloud.system.service.StationCompanyGoodsApplyService;
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
 * 物资申请控制层
 *
 * @author Liu Long Jie
 * @date 2020-6-18 14:37:27
 */
@RestController
@Slf4j
@Api(tags = "StationCompanyGoodsApplyController")
public class StationCompanyGoodsApplyController {

    @Resource
    private StationCompanyGoodsApplyService stationCompanyGoodsApplyService;


    @PostMapping(value = "/goods/apply/firstAudit/list/{pageNum}/{pageSize}")
    @ApiOperation(value = "分页查询物资初审列表", notes = "分页查询物资初审列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "province", value = "省", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "city", value = "市", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "region", value = "区", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "categoryId", value = "分类id", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "pageNum", value = "分页页码", defaultValue = "1", required = true, dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "pageSize", value = "分页大小", defaultValue = "10", required = true, dataType = "Long", paramType = "path")
    })
    public Object pageGoodsApplyFirstAudit(@PathVariable(value = "pageNum") Integer pageNum,
                                           @PathVariable(value = "pageSize") Integer pageSize,
                                           @RequestParam(value = "province", required = false) String province,
                                           @RequestParam(value = "city", required = false) String city,
                                           @RequestParam(value = "region", required = false) String region,
                                           @RequestParam(value = "categoryId", required = false) Integer categoryId) {

        return ResponseEntity.ok(stationCompanyGoodsApplyService.pageGoodsApplyFirstAudit(pageNum, pageSize, province, city, region, categoryId));
    }

    @PostMapping(value = "/goods/apply/afterAudit/list/{pageNum}/{pageSize}")
    @ApiOperation(value = "分页查询物资售后审核列表", notes = "分页查询物资售后审核列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "province", value = "省", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "city", value = "市", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "region", value = "区", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "categoryId", value = "分类id", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "pageNum", value = "分页页码", defaultValue = "1", required = true, dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "pageSize", value = "分页大小", defaultValue = "10", required = true, dataType = "Long", paramType = "path")
    })
    public Object pageGoodsApplyAfterAudit(@PathVariable(value = "pageNum") Integer pageNum,
                                           @PathVariable(value = "pageSize") Integer pageSize,
                                           @RequestParam(value = "province", required = false) String province,
                                           @RequestParam(value = "city", required = false) String city,
                                           @RequestParam(value = "region", required = false) String region,
                                           @RequestParam(value = "categoryId", required = false) Integer categoryId) {

        return ResponseEntity.ok(stationCompanyGoodsApplyService.pageGoodsApplyAfterAudit(pageNum, pageSize, province, city, region, categoryId));
    }

    @PostMapping(value = "/goods/apply/twoAudit/list/{pageNum}/{pageSize}")
    @ApiOperation(value = "分页查询物资复核列表", notes = "分页查询物资复核列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "province", value = "省", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "city", value = "市", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "region", value = "区", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "categoryId", value = "分类id", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "status", value = "物资申请状态", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "isAfterAudit", value = "是否是售后审核 0-否 1-是", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "pageNum", value = "分页页码", defaultValue = "1", required = true, dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "pageSize", value = "分页大小", defaultValue = "10", required = true, dataType = "Long", paramType = "path")
    })
    public Object pageGoodsApplyTwoAudit(@PathVariable(value = "pageNum") Integer pageNum,
                                         @PathVariable(value = "pageSize") Integer pageSize,
                                         @RequestParam(value = "province", required = false) String province,
                                         @RequestParam(value = "city", required = false) String city,
                                         @RequestParam(value = "region", required = false) String region,
                                         @RequestParam(value = "categoryId", required = false) Integer categoryId,
                                         @RequestParam(value = "status", required = false) Integer status,
                                         @RequestParam(value = "isAfterAudit", required = false) Integer isAfterAudit) {

        return ResponseEntity.ok(stationCompanyGoodsApplyService.pageGoodsApplyTwoAudit(pageNum, pageSize, province, city, region, categoryId, status, isAfterAudit));
    }

    @PostMapping(value = "/goods/apply/history/list/{pageNum}/{pageSize}")
    @ApiOperation(value = "分页查询物资申请历史记录列表", notes = "分页查询物资申请历史记录列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "province", value = "省", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "city", value = "市", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "region", value = "区", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "isAfterAudit", value = "是否是售后审核 0-否 1-是", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "pageNum", value = "分页页码", defaultValue = "1", required = true, dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "pageSize", value = "分页大小", defaultValue = "10", required = true, dataType = "Long", paramType = "path")
    })
    public Object pageGoodsApplyTwoAudit(@PathVariable(value = "pageNum") Integer pageNum,
                                         @PathVariable(value = "pageSize") Integer pageSize,
                                         @RequestParam(value = "province", required = false) String province,
                                         @RequestParam(value = "city", required = false) String city,
                                         @RequestParam(value = "region", required = false) String region,
                                         @RequestParam(value = "categoryId", required = false) Integer isAfterAudit) {

        return ResponseEntity.ok(stationCompanyGoodsApplyService.pageGoodsApplyHistory(pageNum, pageSize, province, city, region, isAfterAudit));
    }

    @PostMapping(value = "/goods/apply/station/list/{pageNum}/{pageSize}")
    public Object pageGoodsApplyStation(@PathVariable(value = "pageNum") Integer pageNum,
                                        @PathVariable(value = "pageSize") Integer pageSize,
                                        @RequestParam(value = "stationCompanyId") Integer stationCompanyId,
                                        @RequestParam(value = "categoryId", required = false) Integer categoryId,
                                        @RequestParam(value = "startTime", required = false) Date startTime,
                                        @RequestParam(value = "endTime", required = false) Date endTime) {

        return ResponseEntity.ok(stationCompanyGoodsApplyService.pageGoodsApplyStation(pageNum, pageSize, stationCompanyId, categoryId, startTime, endTime));
    }

    @PostMapping(value = "/goods/apply/station/history/list/{pageNum}/{pageSize}")
    public Object pageGoodsApplyStationHistory(@PathVariable(value = "pageNum") Integer pageNum,
                                               @PathVariable(value = "pageSize") Integer pageSize,
                                               @RequestParam(value = "stationCompanyId") Integer stationCompanyId,
                                               @RequestParam(value = "categoryId", required = false) Integer categoryId,
                                               @RequestParam(value = "status", required = false) Integer status,
                                               @RequestParam(value = "startTime", required = false) Date startTime,
                                               @RequestParam(value = "endTime", required = false) Date endTime) {

        return ResponseEntity.ok(stationCompanyGoodsApplyService.pageGoodsApplyStationHistory(pageNum, pageSize, stationCompanyId, categoryId, status, startTime, endTime));
    }

    @PostMapping(value = "/goods/apply/firstAudit/{id}")
    @ApiOperation(value = "物资申请初审", notes = "物资申请初审")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "申请单号", required = true, dataType = "String", paramType = "path"),
            @ApiImplicitParam(name = "isPass", value = "审核是否通过 0-不通过 1-通过", required = true, dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "cause", value = "审核不通过原因", dataType = "String", paramType = "query")
    })
    public Object goodsApplyFirstAudit(@PathVariable(value = "id") String id,
                                       @RequestParam(value = "isPass") Integer isPass,
                                       @RequestParam(value = "cause", required = false) String cause) {
        stationCompanyGoodsApplyService.goodsApplyFirstAudit(id, isPass, cause, 2);
        return ResponseEntity.noContent().build();
    }

    @PostMapping(value = "/goods/apply/afterAudit/{id}")
    @ApiOperation(value = "物资申请售后审核", notes = "物资申请售后审核")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "申请单号", required = true, dataType = "String", paramType = "path"),
            @ApiImplicitParam(name = "isPass", value = "审核是否通过 0-不通过 1-通过", required = true, dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "cause", value = "审核不通过原因", dataType = "String", paramType = "query")
    })
    public Object goodsApplyAfterAudit(@PathVariable(value = "id") String id,
                                       @RequestParam(value = "isPass") Integer isPass,
                                       @RequestParam(value = "cause", required = false) String cause) {
        stationCompanyGoodsApplyService.goodsApplyFirstAudit(id, isPass, cause, 1);
        return ResponseEntity.noContent().build();
    }

    @PostMapping(value = "/goods/apply/twoAudit/{id}")
    @ApiOperation(value = "物资申请复核", notes = "物资申请复核")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "申请单号", required = true, dataType = "String", paramType = "path"),
            @ApiImplicitParam(name = "isPass", value = "审核是否通过 0-不通过 1-通过", required = true, dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "cause", value = "审核不通过原因", dataType = "String", paramType = "query")
    })
    public Object goodsApplyTwoAudit(@PathVariable(value = "id") String id,
                                     @RequestParam(value = "isPass") Integer isPass,
                                     @RequestParam(value = "cause", required = false) String cause) {
        stationCompanyGoodsApplyService.goodsApplyTwoAudit(id, isPass, cause);
        return ResponseEntity.noContent().build();
    }

    @PostMapping(value = "/goods/apply/deliverGoods/{id}")
    @ApiOperation(value = "发货", notes = "发货")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "申请单号", required = true, dataType = "String", paramType = "path")
    })
    public Object goodsApplyDeliverGoods(@PathVariable(value = "id") String id) {
        stationCompanyGoodsApplyService.goodsApplyDeliverGoods(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping(value = "/goods/apply/anewSubmit")
    public void goodsApplyAnewSubmit(@RequestBody StationCompanyGoodsApplyDTO dto) {
        if (dto.getId() == null) {
            throw new BadRequestException("物资申请订单号不能为空！");
        }
        //必传参数校验
        paramCheck(dto);
        stationCompanyGoodsApplyService.goodsApplyAnewSubmit(dto);
    }

    @PostMapping(value = "/goods/apply/save")
    public void goodsApplySave(@RequestBody StationCompanyGoodsApplyDTO dto) {
        //必传参数校验
        paramCheck(dto);
        stationCompanyGoodsApplyService.goodsApplySave(dto);
    }

    @PostMapping(value = "/goods/apply/confirm/{id}")
    @ApiOperation(value = "确认收货", notes = "确认收货")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "申请单号", required = true, dataType = "String", paramType = "path"),
            @ApiImplicitParam(name = "confirmImg", value = "确认收货的图片", required = true, dataType = "String", paramType = "query")
    })
    public void goodsApplyConfirm(@PathVariable(value = "id") String id,
                                  @RequestParam(value = "confirmImg") String confirmImg) {

        stationCompanyGoodsApplyService.goodsApplyConfirm(id, confirmImg);
    }

    @PostMapping(value = "/goods/apply/cancel/{id}")
    public void goodsApplyCancel(@PathVariable(value = "id") String id) {

        stationCompanyGoodsApplyService.goodsApplyCancel(id);
    }

    private void paramCheck(StationCompanyGoodsApplyDTO dto) {
        if (dto.getStationCompanyId() == null) {
            throw new BadRequestException("服务站公司id不能为空！");
        }
        if (StringUtil.isEmpty(dto.getStationCompanyName())) {
            throw new BadRequestException("服务站公司名称不能为空！");
        }
        if (StringUtil.isEmpty(dto.getApplicantName())) {
            throw new BadRequestException("申请人姓名不能为空！");
        }
        if (StringUtil.isEmpty(dto.getProvince())) {
            throw new BadRequestException("收货地址省不能为空！");
        }
        if (StringUtil.isEmpty(dto.getCity())) {
            throw new BadRequestException("收货地址市不能为空！");
        }
        if (StringUtil.isEmpty(dto.getRegion())) {
            throw new BadRequestException("收货地址区不能为空！");
        }
        if (StringUtil.isEmpty(dto.getAddress())) {
            throw new BadRequestException("收货地址不能为空！");
        }
        if (dto.getFirstCategoryId() == null) {
            throw new BadRequestException("一级分类id不能为空！");
        }
        if (StringUtil.isEmpty(dto.getFirstCategoryName())) {
            throw new BadRequestException("一级分类名称不能为空！");
        }
        if (dto.getTwoCategoryId() == null) {
            throw new BadRequestException("二级分类id不能为空！");
        }
        if (StringUtil.isEmpty(dto.getTwoCategoryName())) {
            throw new BadRequestException("二级分类名称不能为空！");
        }
        if (dto.getGoodsId() == null) {
            throw new BadRequestException("物资id不能为空！");
        }
        if (dto.getGoodsName() == null) {
            throw new BadRequestException("物资名称不能为空！");
        }
        if (dto.getCount() == null) {
            throw new BadRequestException("物资申请数量不能为空！");
        }
        if (dto.getCount() < 0) {
            throw new BadRequestException("申请物资的数量不能小于0！");
        }
        if (StringUtil.isEmpty(dto.getAccessory())) {
            throw new BadRequestException("请上传附件！");
        }
    }
}
