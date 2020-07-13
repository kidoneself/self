package com.yimao.cloud.hra.controller;

import com.yimao.cloud.base.utils.CollectionUtil;
import com.yimao.cloud.hra.po.HraDevice;
import com.yimao.cloud.hra.service.HraDeviceService;
import com.yimao.cloud.pojo.dto.hra.HraDeviceDTO;
import com.yimao.cloud.pojo.dto.hra.HraDeviceExportDTO;
import com.yimao.cloud.pojo.dto.hra.HraDeviceQuery;
import com.yimao.cloud.pojo.vo.PageVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @description: HRA设备
 * @author: yu chunlei
 * @create: 2019-01-26 16:40:10
 **/
@RestController
@Slf4j
@Api(tags = "HraDeviceController")
public class HraDeviceController {

    @Resource
    private HraDeviceService hraDeviceService;


    /**
     * HRA设备管理
     *
     * @param province 省
     * @param city     市
     * @param region   区
     * @param online   上线状态
     * @param deviceId 设备ID
     * @param minTime  开始时间
     * @param maxTime  结束时间
     * @param pageNum  第几页
     * @param pageSize 每页显示条数
     * @Description: HRA评估-HRA设备管理
     * @author ycl
     * @Return: org.springframework.http.ResponseEntity
     * @Create: 2019/1/12 17:03
     */
    @PostMapping(value = {"/device/{pageNum}/{pageSize}"})
    @ApiOperation(value = "HRA设备管理", notes = "HRA设备管理")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "dto", value = "设备查询", dataType = "HraDeviceQuery", paramType = "body"),
            @ApiImplicitParam(name = "pageNum", value = "分页页码", defaultValue = "1", required = true, dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "pageSize", value = "分页大小", defaultValue = "10", required = true, dataType = "Long", paramType = "path")
    })
    public ResponseEntity<PageVO<HraDeviceDTO>> queryStationOnline(@RequestBody(required = false) HraDeviceQuery query,
                                                                   @PathVariable(value = "pageNum") Integer pageNum,
                                                                   @PathVariable(value = "pageSize") Integer pageSize) {
        PageVO<HraDeviceDTO> soPage = hraDeviceService.queryStationOnline(pageNum, pageSize, query);
        return ResponseEntity.ok(soPage);
    }

    /**
     * @param stationId
     * @Description: 根据服务站ID获取HRA设备
     * @author ycl
     * @Create: 2019/1/26 16:43
     */
    @GetMapping(value = "/device/{stationId}/station")
    @ApiOperation(value = "服务站ID获取HRA设备", notes = "服务站ID获取HRA设备")
    @ApiImplicitParam(name = "stationId", value = "服务站id", dataType = "Long", required = true, paramType = "query")
    public ResponseEntity getHraDeviceByStationId(@PathVariable(value = "stationId") Integer stationId) {
        List<HraDeviceDTO> hraDeviceDTOList = hraDeviceService.getHraDeviceByStationId(stationId);
        return ResponseEntity.ok(hraDeviceDTOList);
    }

    /**
     * @Description: 删除设备
     * @author ycl
     * @param: * @param
     * @Return: org.springframework.http.ResponseEntity
     * @Create: 2019/2/21 9:51
     */
    @DeleteMapping(value = "/device/{id}")
    @ApiOperation(value = "删除设备", notes = "删除设备")
    @ApiImplicitParam(name = "id", value = "设备ID", required = true, dataType = "Long", paramType = "path")
    public ResponseEntity deleteDevice(@PathVariable("id") Integer id) {
        hraDeviceService.deleteDevice(id);
        return ResponseEntity.noContent().build();
    }


    /**
     * @Description: HRA设备管理-设备上线
     * @author ycl
     * @param: hraCardDTO
     * @Return: org.springframework.http.ResponseEntity
     * @Create: 2019/2/18 10:05
     */
    @PostMapping(value = {"/device/online"})
    @ApiOperation(value = "HRA设备管理-设备上线", notes = "HRA设备管理-设备上线")
    @ApiImplicitParam(name = "dto", value = "设备信息", required = true, dataType = "HraDeviceOnlineDTO", paramType = "body")
    public ResponseEntity saveDeviceOnline(@RequestBody HraDeviceDTO dto) {
        hraDeviceService.saveDeviceOnline(dto);
        return ResponseEntity.ok(dto);
    }

    /**
     * @Description: 修改设备上下线
     * @author ycl
     * @param: * @param onlineId
     * @Return: org.springframework.http.ResponseEntity
     * @Create: 2019/2/21 10:03
     */
    @PutMapping(value = "/device")
    @ApiOperation(value = "修改设备上下线", notes = "修改设备上下线")
    @ApiImplicitParam(name = "dto", value = "设备信息", required = true, dataType = "HraDeviceOnlineDTO", paramType = "body")
    public ResponseEntity updateDevice(@RequestBody HraDeviceDTO dto) {
        //查询hraDevice
        log.info("设备ID==" + dto.getId());
        HraDevice hraDevice = hraDeviceService.findHraDeviceById(dto.getId());
        hraDeviceService.updateDevice(hraDevice);
        return ResponseEntity.noContent().build();
    }


    /**
     * @Description: 设备管理-批量删除功能
     * @author ycl
     * @param: * @param ids
     * @Return: org.springframework.http.ResponseEntity
     * @Create: 2019/3/1 10:25
     */
    @DeleteMapping("/device")
    @ApiOperation(value = "批量删除功能", notes = "批量删除功能")
    @ApiImplicitParam(name = "ids", value = "设备ID数组集", required = true, paramType = "query", allowMultiple = true, dataType = "String")
    public ResponseEntity batchDelete(@RequestParam("ids") Integer[] ids) {
        hraDeviceService.batchDelete(ids);
        return ResponseEntity.noContent().build();
    }

    /**
     * @Description: 根据服务站ID获取HRA设备状态是否可用
     * @author ycl
     * @Return: org.springframework.http.ResponseEntity
     * @Create: 2019/1/26 16:43
     */
    @GetMapping(value = "/device/{stationId}/enabled")
    @ApiOperation(value = "服务站ID获取HRA设备", notes = "服务站ID获取HRA设备")
    @ApiImplicitParam(name = "stationId", value = "服务站id", dataType = "Long", required = true, paramType = "query")
    public ResponseEntity<Boolean> getHraDeviceStatusByStationId(@PathVariable(value = "stationId") Integer stationId) {
        List<HraDeviceDTO> hraDeviceDTOList = hraDeviceService.getHraDeviceByStationId(stationId);
        if (CollectionUtil.isNotEmpty(hraDeviceDTOList)) {
            for (HraDeviceDTO deviceDTO : hraDeviceDTOList) {
                if (deviceDTO.getDeviceStatus().equals(1)) {
                    return ResponseEntity.ok(true);
                }
            }
        }
        return ResponseEntity.ok(false);
    }

    /**
     * 查询HRA设备信息
     *
     * @param id HRA设备ID
     */
    @GetMapping(value = "/device/{id}")
    @ApiOperation(value = "查询HRA设备信息")
    @ApiImplicitParam(name = "id", value = "HRA设备ID", dataType = "String", required = true, paramType = "query")
    public HraDeviceDTO getHraDeviceByDeviceId(@PathVariable("id") String id) {
        HraDevice hraDevice = hraDeviceService.getHraDeviceByDeviceId(id);
        if (hraDevice == null) {
            return null;
        }
        HraDeviceDTO dto = new HraDeviceDTO();
        hraDevice.convert(dto);
        return dto;
    }


    /**
     * @param stationId
     * @return
     */
    @GetMapping("/device/status/{stationId}")
    @ApiOperation(value = "查询HRA设备信息", notes = "查询HRA设备信息")
    @ApiImplicitParam(name = "stationId", value = "服務站id", required = true, paramType = "path", dataType = "Long")
    public Object getDeviceStatus(@PathVariable("stationId") Integer stationId) {
        return hraDeviceService.getDeviceStatus(stationId);
    }


    @GetMapping("/device/station")
    @ApiOperation(value = "查询HRA设备服务站", notes = "查询HRA设备服务站")
    public List<Integer> getHraStationIds() {
        return hraDeviceService.getHraStationIds();
    }
    /*==========================================HRA设备管理导出--lizhiqiang===========================================================*/


    /**
     * HRA设备管理
     *
     * @Description: HRA评估-HRA设备管理
     * @author ycl
     * @Return: org.springframework.http.ResponseEntity
     * @Create: 2019/1/12 17:03
     */
    @PostMapping(value = {"/device/export"})
    @ApiOperation(value = "HRA设备管理", notes = "HRA设备管理")
    @ApiImplicitParam(name = "dto", value = "设备查询", dataType = "HraDeviceQuery", paramType = "body")
    public ResponseEntity<List<HraDeviceExportDTO>> exportDevice(@RequestBody HraDeviceQuery hraDeviceQuery) {
        List<HraDeviceExportDTO> list = hraDeviceService.exportDevice(hraDeviceQuery);
        return ResponseEntity.ok(list);
    }


}
