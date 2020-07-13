package com.yimao.cloud.wechat.controller;


import com.yimao.cloud.base.exception.YimaoException;
import com.yimao.cloud.pojo.dto.system.StationDTO;
import com.yimao.cloud.pojo.vo.PageVO;
import com.yimao.cloud.wechat.feign.SystemFeign;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author Lizhqiang
 * @date 2019/1/28
 */
@RestController
@Slf4j
@Api(tags = "StationController")
public class StationController {

    @Resource
    private SystemFeign systemFeign;

    /**
     * 带省市区或者hra上下线的服务站门店获取分页
     *
     * @param province
     * @param city
     * @param region
     * @param hraIsOnline
     * @param pageNum
     * @param pageSize
     * @return
     */
    @GetMapping(value = "/station/list/{pageNum}/{pageSize}")
    @ApiOperation(value = "获取附近的服务站门店")
    @ApiImplicitParams({@ApiImplicitParam(name = "province", value = "服务站门店省", required = false, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "city", value = "服务站门店市", required = false, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "region", value = "服务站门店区", required = false, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "hraIsOnline", value = "服务站hra上线状态", required = false, dataType = "Boolean", paramType = "query"),
            @ApiImplicitParam(name = "pageNum", defaultValue = "1", value = "页码", required = true, dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "pageSize", defaultValue = "10", value = "页面显示数量", required = true, dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "online", value = "是否上线 1- 上线 0 未上线", dataType = "Long", paramType = "query")})
    public Object listStation(@RequestParam(required = false) String province,
                              @RequestParam(required = false) String city,
                              @RequestParam(required = false) String region,
                              @RequestParam(required = false) Boolean hraIsOnline,
                              @PathVariable(required = true) Integer pageNum,
                              @PathVariable(required = true) Integer pageSize,
                              @RequestParam(required = true) Integer online) {
        PageVO<StationDTO> page = systemFeign.listStation(province, city, region, hraIsOnline, online, pageNum, pageSize);
        if (page == null) {
            throw new YimaoException("未找到相应服务站门店");
        }
        return ResponseEntity.ok(page);

    }


    /**
     * 获取附近的服务站门店
     *
     * @param lng
     * @param lat
     * @return
     */
    @GetMapping(value = "/station/nearby")
    @ApiOperation(value = "获取附近的服务站门店")
    @ApiImplicitParams({@ApiImplicitParam(name = "lng", value = "服务站门店lng", required = true, dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "lat", value = "服务站门店lat", required = true, dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "online", value = "是否上线 1- 上线 0 未上线", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "hraIsOnline", value = "服务站hra上线状态", required = false, dataType = "Boolean", paramType = "query")
    })
    public Object getNearbyStation(@RequestParam(value = "lng") Double lng,
                                   @RequestParam(value = "lat") Double lat,
                                   @RequestParam(value = "online") Integer online,
                                   @RequestParam(required = false) Boolean hraIsOnline
    ) {
        List<StationDTO> stationByLngAndLat = systemFeign.findStationByLngAndLat(lng, lat, online, hraIsOnline);
        return ResponseEntity.ok(stationByLngAndLat);

    }

    /**
     * 销售主体
     *
     * @param
     * @param
     * @return
     */
    @GetMapping("/station/referrerStation")
    @ApiOperation(value = "根据省市区查询服务站门店信息", notes = "根据省市区查询服务站门店信息")
    public Object referrerStation() {
        StationDTO stationDTO = systemFeign.referrerStation();
        return ResponseEntity.ok(stationDTO);
    }

    /**
     * @Author ycl
     * @Description 根据省市区获取服务站信息
     * @Date 9:46 2019/9/12
     * @Param
    **/
    @GetMapping(value = "/station/information")
    @ApiOperation(value = "根据省市区获取服务站信息", notes = "根据省市区获取服务站信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "province", value = "省", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "city", value = "市", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "region", value = "区", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "type", value = "服务站门店权限类型 0-售前+售后；1-售前 ； 2-售后", dataType = "Long", paramType = "query"
            )
    })
    public Object getStationByPRC(@RequestParam(value = "province") String province,
                                  @RequestParam(value = "city") String city,
                                  @RequestParam(value = "region") String region,
                                  @RequestParam(value = "type") Integer type) {
        return systemFeign.getStationByPCR(province, city, region,type);
    }


}
