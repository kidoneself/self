package com.yimao.cloud.system.feign;

import com.yimao.cloud.base.constant.Constant;
import com.yimao.cloud.pojo.dto.station.StationMenuDTO;
import com.yimao.cloud.pojo.dto.station.StationPermissionDTO;
import com.yimao.cloud.pojo.dto.system.StationServiceAreaDTO;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

/**
 * 描述：系统微服务调用服务站站务系统微服务的接口列表
 *
 * @author Liu Long Jie
 * @date 2020-1-2
 */
@FeignClient(name = Constant.MICROSERVICE_STATION)
public interface StationFeign {

    /**
     * 添加菜单 (服务站站务系统)
     *
     * @param dto
     * @return
     */
    @RequestMapping(value = "/stationMenu", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    Void saveStationMenu(@RequestBody StationMenuDTO dto);

    /**
     * 删除菜单(服务站站务系统)
     *
     * @param id 菜单ID
     */
    @RequestMapping(value = "/stationMenu/{id}", method = RequestMethod.DELETE)
    Void deleteStationMenu(@PathVariable("id") Integer id);

    /**
     * 更新菜单(服务站站务系统)
     *
     * @param dto 部门信息
     */
    @RequestMapping(value = "/stationMenu", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE)
    Void updateStationMenu(@RequestBody StationMenuDTO dto);

    /**
     * 创建权限(服务站站务系统)
     *
     * @param dto 权限信息
     */
    @PostMapping(value = "/stationPermission", consumes = MediaType.APPLICATION_JSON_VALUE)
    Void saveStationPermission(@RequestBody StationPermissionDTO dto);

    /**
     * 删除权限账号(服务站站务系统)
     *
     * @param id 权限ID
     */
    @DeleteMapping(value = "/stationPermission/{id}")
    Void deleteStationPermission(@PathVariable("id") Integer id);

    /**
     * 更新权限(服务站站务系统)
     *
     * @param dto 权限信息
     */
    @PutMapping(value = "/stationPermission", consumes = MediaType.APPLICATION_JSON_VALUE)
    Void updateStationPermission(@RequestBody StationPermissionDTO dto);

    
    /**
     *
     * system调用（用于服务站服务区域变更）
     * @param obj
     */
    @PostMapping(value = "/admins/stationAreaChange/{stationId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    void stationAreaChange(@PathVariable("stationId") Integer stationId,@RequestBody List<StationServiceAreaDTO> areas);
    

}
