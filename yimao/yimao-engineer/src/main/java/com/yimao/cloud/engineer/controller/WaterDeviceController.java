package com.yimao.cloud.engineer.controller;

import com.yimao.cloud.base.cache.UserCache;
import com.yimao.cloud.base.exception.BadRequestException;
import com.yimao.cloud.engineer.feign.WaterFeign;
import com.yimao.cloud.pojo.dto.water.WaterDeviceCompleteDTO;
import com.yimao.cloud.pojo.dto.water.WaterDeviceCostChangeRecordDTO;
import com.yimao.cloud.pojo.dto.water.WaterDeviceDTO;
import com.yimao.cloud.pojo.dto.water.WaterDeviceFilterChangeRecordQueryDTO;
import com.yimao.cloud.pojo.vo.PageVO;
import com.yimao.cloud.pojo.vo.water.WaterDeviceFaultVO;
import com.yimao.cloud.pojo.vo.water.WaterDevicePlaceChangeRecordVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import java.util.Objects;

/**
 * @ClassName WaterDeviceController
 * @Description 工单已完成水机设备相关
 * @Author yuchunlei
 * @Date 2020/7/3 14:12
 * @Version 1.0
 */
@RestController
@Slf4j
@Api(tags = "WaterDeviceController")
public class WaterDeviceController {

    @Resource
    private UserCache userCache;

    @Resource
    private WaterFeign waterFeign;



    /**
     * 查询已完成安装工单的设备信息
     * @param pageNum
     * @param pageSize
     * @param key
     * @param engineerId
     * @return
     */
    @GetMapping(value = "/workorder/complete/{pageNum}/{pageSize}")
    @ApiOperation(value = "查询已完成安装工单的设备信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "页码", defaultValue = "1", dataType = "Long", required = true, paramType = "path"),
            @ApiImplicitParam(name = "pageSize", value = "页数", defaultValue = "10", dataType = "Long", required = true, paramType = "path"),
            @ApiImplicitParam(name = "key", value = "搜索关键词", dataType = "key", paramType = "query"),
            @ApiImplicitParam(name = "engineerId", value = "安装工id", dataType = "Long", paramType = "query")})
    public PageVO<WaterDeviceCompleteDTO> getCompleteWorkOrderList(@PathVariable(value = "pageNum") Integer pageNum,
                                                                   @PathVariable(value = "pageSize") Integer pageSize,
                                                                   @RequestParam(value = "key",required = false) String key,
                                                                   @RequestParam(value = "engineerId") Integer engineerId){
        Integer currentEngineerId = userCache.getCurrentEngineerId();
        if(Objects.isNull(currentEngineerId)){
            throw new BadRequestException("参数错误");
        }
        return waterFeign.getCompleteWorkOrderList(pageNum,pageSize,key,engineerId);
    }


    /**
     *服务统计-已安装设备-设备详情
     * @param id
     * @return
     */
    @GetMapping(value = "/waterdevice/{id}")
    @ApiOperation(value = "服务统计-已安装设备-设备详情")
    @ApiImplicitParam(name = "id", value = "设备ID", dataType = "Long", paramType = "path", required = true)
    public WaterDeviceDTO getDetailById(@PathVariable Integer id) {
        return waterFeign.getWaterDeviceById(id);
    }


    /**
     * 已安装设备-设备详情-地址变动记录
     * @param pageNum
     * @param pageSize
     * @param sn
     * @return
     */
    @GetMapping(value = "/placechangerecord/{pageNum}/{pageSize}")
    @ApiOperation(value = "已安装设备-设备详情-地址变动记录")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "第几页", required = true, dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "pageSize", value = "每页大小", required = true, dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "sn", value = "SN码", dataType = "String", paramType = "query"),
    })
    public PageVO<WaterDevicePlaceChangeRecordVO> page(@PathVariable Integer pageNum, @PathVariable Integer pageSize,
                                                       @RequestParam(required = false) String sn){
        return waterFeign.page(pageNum,pageSize,sn);
    }



    /**
     * 已安装设备-设备详情-计费更改记录
     * @param pageNum
     * @param pageSize
     * @param sn
     * @return
     */
    @GetMapping(value = "/costchangerecord/{pageNum}/{pageSize}")
    @ApiOperation(value = "已安装设备-设备详情-计费更改记录")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "第几页", required = true, dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "pageSize", value = "每页大小", required = true, dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "sn", value = "SN码", dataType = "String", paramType = "query"),
    })
    public PageVO<WaterDeviceCostChangeRecordDTO> getCostChangeRecordList(@PathVariable Integer pageNum, @PathVariable Integer pageSize,
                                                                          @RequestParam(required = false) String sn){
        return waterFeign.getCostChangeRecordList(pageNum,pageSize,sn);
    }


    /**
     * 已安装设备-设备详情-设备故障查询
     * @param pageNum
     * @param pageSize
     * @param sn
     * @return
     */
    @GetMapping(value = "/faultrecord/{pageNum}/{pageSize}")
    @ApiOperation(value = "已安装设备-设备详情-设备故障查询")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "第几页", required = true, dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "pageSize", value = "每页大小", required = true, dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "sn", value = "SN码", dataType = "String", paramType = "query"),
    })
    public PageVO<WaterDeviceFaultVO> pageDeviceFault(@PathVariable Integer pageNum, @PathVariable Integer pageSize,
                                                      @RequestParam(required = false) String sn){
        return waterFeign.pageDeviceFault(pageNum,pageSize,sn);
    }



    /**
     * 已安装设备-设备详情-设备故障查询
     * @param queryDTO
     * @param pageNum
     * @param pageSize
     * @return
     */
    @PostMapping(value = "/waterdevice/filterChangeRecord/{pageNum}/{pageSize}")
    @ApiOperation(value = "已安装设备-设备详情-设备故障查询", notes = "已安装设备-设备详情-设备故障查询")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "queryDTO", value = "查询条件", dataType = "WaterDeviceFilterChangeRecordQueryDTO", paramType = "body"),
            @ApiImplicitParam(name = "pageNum", value = "第几页", defaultValue = "1", required = true, dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "pageSize", value = "每页大小", defaultValue = "10", required = true, dataType = "Long", paramType = "path")
    })
    public Object page(@RequestBody WaterDeviceFilterChangeRecordQueryDTO queryDTO,
                       @PathVariable("pageNum") Integer pageNum,
                       @PathVariable("pageSize") Integer pageSize){
        return waterFeign.waterDeviceFilterChangeRecordist(queryDTO,pageNum,pageSize);
    }



}
