package com.yimao.cloud.wechat.controller;

import com.yimao.cloud.base.exception.NotFoundException;
import com.yimao.cloud.wechat.feign.OrderFeign;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Map;

/**
 * 公众号收益
 */
@RestController
@Slf4j
@Api(tags = "OrderIncomeController")
public class OrderIncomeController {

    @Resource
    private OrderFeign orderFeign;

    /**
     * 收益管理查询
     *
     * @param userId 用户ID
     * @return java.lang.Object
     * @author hhf
     * @date 2019/4/8
     */
    @GetMapping(value = "/order/income/main")
    @ApiOperation(value = "收益管理查询", notes = "收益管理查询")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "用户ID", required = true, dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "incomeType", value = "收益类型 1-产品收益  2-续费收益 默认产品收益", required = true, dataType = "Long", paramType = "query")
    })
    public Object incomeList(@RequestParam("userId") Integer userId, @RequestParam("incomeType") Integer incomeType) {

        Map<String, Object> map = orderFeign.listIncomeByProductType(userId, incomeType);
        if (map == null) {
            throw new NotFoundException("未找到收益信息");
        }
        return ResponseEntity.ok(map);
    }

    /**
     * 用户第一次进入收益明细,查询收益明细
     *
     * @param userId      用户id
     * @param productType 产品类型id
     * @param pageNum     查询页页码
     * @param pageSize    每页记录数
     * @return map
     */
    @GetMapping(value = "/order/income/details/first")
    @ApiOperation(value = "用户第一次进入收益明细,查询收益明细")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "分页页码", defaultValue = "1", required = true, dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "分页大小", defaultValue = "10", required = true, dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "userId", value = "用户ID", required = true, dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "productType", value = "产品类型id", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "year", value = "年", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "month", value = "月", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "incomeType", value = "收益类型 1-产品收益 2-续费收益 默认1", dataType = "Long", paramType = "query")
    })
    public Map<String, Object> incomeDetailListFirst(@RequestParam("userId") Integer userId,
                                                     @RequestParam(value = "productType", required = false) Integer productType,
                                                     @RequestParam(value = "year", required = false) String year,
                                                     @RequestParam(value = "month", required = false) String month,
                                                     @RequestParam(value = "incomeType", required = false, defaultValue = "1") Integer incomeType,
                                                     @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                                     @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        return orderFeign.incomeDetailListFirst(userId, productType, year, month, incomeType, pageNum, pageSize);
    }

    /**
     * 用户在收益明细中查询子用户的收益明细,查询收益明细
     *
     * @param distributorId 经销商id
     * @param productType   产品类型id
     * @param pageNum       查询页页码
     * @param pageSize      每页记录数
     * @return map
     */
    @GetMapping(value = "/order/income/details/second")
    @ApiOperation(value = "用户在收益明细中查询子用户的收益明细,查询收益明细")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "分页页码", defaultValue = "1", required = true, dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "分页大小", defaultValue = "10", required = true, dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "distributorId", value = "经销商id", required = true, dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "productType", value = "产品类型id", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "year", value = "年", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "month", value = "月", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "incomeType", value = "收益类型 1-产品收益 2-续费收益 默认1", dataType = "Long", paramType = "query")
    })
    public Map<String, Object> incomeDetailListSecond(@RequestParam("distributorId") Integer distributorId,
                                                      @RequestParam(value = "productType", required = false) Integer productType,
                                                      @RequestParam(value = "year", required = false) String year,
                                                      @RequestParam(value = "month", required = false) String month,
                                                      @RequestParam(value = "incomeType", required = false) Integer incomeType,
                                                      @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                                      @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {

        return orderFeign.incomeDetailListSecond(distributorId, productType, year, month, incomeType, pageNum, pageSize);
    }
}
