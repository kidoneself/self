package com.yimao.cloud.water.controller;

import com.yimao.cloud.base.enums.AreaTypeEnum;
import com.yimao.cloud.base.exception.BadRequestException;
import com.yimao.cloud.base.utils.StringUtil;
import com.yimao.cloud.pojo.dto.system.AreaDTO;
import com.yimao.cloud.pojo.query.water.WaterDeviceQuery;
import com.yimao.cloud.water.enums.ProductModelEnum;
import com.yimao.cloud.water.handler.SystemFeignHandler;
import com.yimao.cloud.water.mapper.WaterDeviceLocationMapper;
import com.yimao.cloud.water.service.DeviceService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Date;
import java.util.Objects;
import java.util.Set;

/**
 * 查询云平台mongo数据库设备相关信息。
 *
 * @author Zhang Bo
 * @date 2017/12/15.
 */
@RestController
@Slf4j
@Api(tags = "DeviceController")
public class DeviceController {

    @Resource
    private SystemFeignHandler systemFeignHandler;
    @Resource
    private DeviceService deviceService;
    @Resource
    private WaterDeviceLocationMapper waterDeviceLocationMapper;

    /**
     * 根据条件分页获取设备信息
     *
     * @param pageNum        第几页
     * @param pageSize       每页大小
     * @param areaId         区域ID
     * @param model          水机型号
     * @param online         水机是否在线
     * @param connectionType 网络连接类型
     * @param keyWord        关键词
     * @param location       位置标签
     * @param stock          是否有库存
     * @param beginTime      最后在线开始时间
     * @param endTime        最后在线结束时间
     * @return
     */
    @GetMapping(value = "/devices/{pageNum}/{pageSize}")
    @ApiOperation(value = "根据条件分页获取设备信息", notes = "根据条件分页获取设备信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "页码", required = true, defaultValue = "1", dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "pageSize", value = "每页显示数量", required = true, defaultValue = "10", dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "areaId", value = "区域ID，多级只需传最下级的区域ID", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "model", value = "水机型号，选'全部'时不传值", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "online", value = "水机是否在线，选'全部'时不传值", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "connectionType", value = "网络连接类型：1-WIFI；3-3G，选'全部'时不传值", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "keyWord", value = "关键词搜索", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "location", value = "位置标签", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "stock", value = "是否有库存", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "snCode", value = "设备编码SN", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "beginTime", value = "可投开始时间", dataType = "Date", required = true, format = "yyyy-MM-dd HH:mm:ss", paramType = "query"),
            @ApiImplicitParam(name = "endTime", value = "可投结束时间", dataType = "Date", required = true, format = "yyyy-MM-dd HH:mm:ss", paramType = "query")
    })
    public Object pageDevice(@PathVariable(value = "pageNum") Integer pageNum,
                             @PathVariable(value = "pageSize") Integer pageSize,
                             @RequestParam(value = "areaId", required = false) Integer areaId,
                             @RequestParam(value = "model", required = false) String model,
                             @RequestParam(value = "online", required = false) Boolean online,
                             @RequestParam(value = "connectionType", required = false) Integer connectionType,
                             @RequestParam(value = "keyWord", required = false) String keyWord,
                             @RequestParam(value = "location", required = false) String location,
                             @RequestParam(value = "stock", required = false) Boolean stock,
                             @RequestParam(value = "snCode", required = false) String snCode,
                             @RequestParam(value = "beginTime") Date beginTime,
                             @RequestParam(value = "endTime") Date endTime) {
        WaterDeviceQuery query = new WaterDeviceQuery();
        //省市区
        if (Objects.nonNull(areaId)) {
            AreaDTO area = systemFeignHandler.getAreaById(areaId);
            if (Objects.nonNull(area)) {
                if (area.getLevel() == AreaTypeEnum.PROVINCE.value) {
                    query.setProvince(area.getName());
                } else if (area.getLevel() == AreaTypeEnum.CITY.value) {
                    AreaDTO province = systemFeignHandler.getAreaById(area.getPid());
                    query.setProvince(province.getName());
                    query.setCity(area.getName());
                } else if (area.getLevel() == AreaTypeEnum.REGION.value) {
                    AreaDTO city = systemFeignHandler.getAreaById(area.getPid());
                    AreaDTO province = systemFeignHandler.getAreaById(city.getPid());
                    query.setProvince(province.getName());
                    query.setCity(city.getName());
                    query.setRegion(area.getName());
                }
            }
        }
        //水机型号
        if (StringUtil.isNoneBlank(model)) {
            if (!model.equalsIgnoreCase(ProductModelEnum.MODEL_1601T.value)
                    && !model.equalsIgnoreCase(ProductModelEnum.MODEL_1602T.value)
                    && !model.equalsIgnoreCase(ProductModelEnum.MODEL_1603T.value)
                    && !model.equalsIgnoreCase(ProductModelEnum.MODEL_1601L.value)) {
                throw new BadRequestException("错误的型号。");
            }
            query.setDeviceModel(model);
        }
        //水机设备是否在线
        if (Objects.nonNull(online)) {
            query.setOnline(online);
        }
        //网络连接类型：1-WIFI；3-3G
        if (Objects.nonNull(connectionType)) {
            query.setConnectionType(connectionType);
        }
        //关键词
        if (StringUtil.isNoneBlank(keyWord)) {
            query.setKeyWords(keyWord);
        }
        //位置标签
        if (StringUtil.isNoneBlank(location)) {
            query.setPlace(location);
        }
        //设备编码
        if (StringUtil.isNoneBlank(snCode)) {
            query.setSn(snCode);
        }
        /*//最后在线开始时间
        if (Objects.nonNull(beginTime)) {
            query.setLastOnlineBeginTime(beginTime);
        }
        //最后在线结束时间
        if (Objects.nonNull(endTime)) {
            query.setLastOnlineEndTime(endTime);
        }
        //是否有库存，即是否有可以投放的广告位
       */
        if (Objects.nonNull(stock)) {
            //TODO
        }

        return deviceService.queryListByCondition(pageNum, pageSize, query, beginTime, endTime);
    }

    /**
     * 查询所有的设备位置标签
     */
    @GetMapping(value = "/devices/location")
    @ApiOperation(value = "查询所有的设备位置标签", notes = "查询所有的设备位置标签")
    public Set<String> devicelocation() {
        return waterDeviceLocationMapper.selectLocation();
    }
}
