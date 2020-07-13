package com.yimao.cloud.system.controller;


import com.alibaba.fastjson.JSON;
import com.yimao.cloud.base.cache.UserCache;
import com.yimao.cloud.base.constant.RabbitConstant;
import com.yimao.cloud.base.enums.ExportRecordStatus;
import com.yimao.cloud.base.enums.SystemType;
import com.yimao.cloud.base.exception.BadRequestException;
import com.yimao.cloud.base.msg.CommResult;
import com.yimao.cloud.base.utils.BeanHelper;
import com.yimao.cloud.base.utils.CollectionUtil;
import com.yimao.cloud.pojo.dto.station.SystemAreaTypeDto;
import com.yimao.cloud.pojo.dto.system.ExportRecordDTO;
import com.yimao.cloud.pojo.dto.system.StationCompanyDTO;
import com.yimao.cloud.pojo.dto.system.StationDTO;
import com.yimao.cloud.pojo.dto.system.StationServiceAreaDTO;
import com.yimao.cloud.pojo.query.station.StationAreaTypeQuery;
import com.yimao.cloud.pojo.query.system.StationQuery;
import com.yimao.cloud.pojo.vo.PageVO;
import com.yimao.cloud.system.mapper.StationCompanyMapper;
import com.yimao.cloud.system.mapper.StationMapper;
import com.yimao.cloud.system.po.Station;
import com.yimao.cloud.system.service.ExportRecordService;
import com.yimao.cloud.system.service.StationService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.*;

/**
 * @author Lizhqiang
 * @date 2019/1/22
 */
@RestController
@Slf4j
@Api(tags = "StationController")
public class StationController {

    @Resource
    private StationService stationService;
    @Resource
    private StationMapper stationMapper;
    @Resource
    private StationCompanyMapper stationCompanyMapper;
    @Resource
    private ExportRecordService exportRecordService;
    @Resource
    private RabbitTemplate rabbitTemplate;
    @Resource
    private UserCache userCache;

    /**
     * 新增服务站门店
     *
     * @param dto 服务站门店
     */
    @PostMapping(value = "/station")
    @ApiOperation(value = "新增服务站门店")
    @ApiImplicitParam(name = "dto", value = "服务站门店信息", dataType = "StationDTO", paramType = "body")
    public void saveStation(@RequestBody StationDTO dto) {
        List<Integer> stationCompanyIds = dto.getStationCompanyIds();
        if (CollectionUtil.isEmpty(stationCompanyIds)) {
            throw new BadRequestException("请选择区县级公司。");
        }
        List<StationServiceAreaDTO> serviceAreaList = dto.getServiceAreas();
        List<Integer> areaIds = new ArrayList<>();
        if (CollectionUtil.isNotEmpty(serviceAreaList)) {
            if (dto.getOnline() != null && dto.getOnline() == 1) {
                if (dto.getMasterDistributorId() == null) {
                    throw new BadRequestException("未设置服务站门店站长信息，不允许进行上线操作！");
                }
                //设置上线时间
                dto.setOnlineTime(new Date());
            }
            for (StationServiceAreaDTO serviceAreaDTO : serviceAreaList) {
                areaIds.add(serviceAreaDTO.getAreaId());
            }
        } else {
            if (dto.getOnline() != null && dto.getOnline() == 1) {
                throw new BadRequestException("未设置服务区域，不能进行上线操作！");
            }
            if (dto.getMasterDistributorId() != null) {
                throw new BadRequestException("未设置服务区域，不允许绑定站长信息！");
            }
        }

        Station station = new Station(dto);
        stationService.saveStation(station, areaIds, serviceAreaList, stationCompanyIds);
    }

    /**
     * 修改服务站门店
     *
     * @param dto 服务站门店
     */
    @PutMapping(value = "/station")
    @ApiOperation(value = "修改服务站门店")
    @ApiImplicitParam(name = "dto", value = "服务站门店信息", required = true, dataType = "StationDTO", paramType = "body")
    public void updateStation(@RequestBody StationDTO dto) {
        if (dto.getId() == null) {
            throw new BadRequestException("服务站门店ID不能为空。");
        }
        List<StationServiceAreaDTO> serviceAreaList = dto.getServiceAreas();
        List<Integer> areaIds = new ArrayList<>();
        if (CollectionUtil.isNotEmpty(serviceAreaList)) {
            for (StationServiceAreaDTO serviceAreaDTO : serviceAreaList) {
                areaIds.add(serviceAreaDTO.getAreaId());
            }
        } else {
            if (dto.getMasterDistributorId() != null) {
                throw new BadRequestException("未设置服务区域，不允许绑定站长信息！");
            }
        }
        Station station = new Station(dto);
        stationService.updateStation(station, areaIds, serviceAreaList);
    }

    /**
     * 查询服务站门店信息（分页）-app调用
     *
     * @param pageNum  页码
     * @param pageSize 页面大小
     * @param query    查询条件
     */
    @GetMapping(value = "/station/{pageNum}/{pageSize}")
    @ApiOperation(value = "查询服务站门店信息（分页）")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "页码", defaultValue = "1", dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "pageSize", value = "页面大小", defaultValue = "10", dataType = "Long", paramType = "path"),
    })
    public PageVO<StationDTO> pageStation(@PathVariable Integer pageNum,
                                          @PathVariable Integer pageSize,
                                          StationQuery query) {
        if (query.getOnline() != null) {
            query.setDisplay(query.getOnline());
            query.setOnline(null);
        }
        return stationService.pageStation(pageNum, pageSize, query);
    }

    /**
     * 查询服务站门店信息（分页）-业务系统调用
     *
     * @param pageNum  页码
     * @param pageSize 页面大小
     * @param query    查询条件
     */
    @GetMapping(value = "/station/sys/{pageNum}/{pageSize}")
    @ApiOperation(value = "查询服务站门店信息（分页）")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "页码", defaultValue = "1", dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "pageSize", value = "页面大小", defaultValue = "10", dataType = "Long", paramType = "path"),
    })
    public PageVO<StationDTO> pageSysStation(@PathVariable Integer pageNum,
                                             @PathVariable Integer pageSize,
                                             StationQuery query) {
        return stationService.pageStation(pageNum, pageSize, query);
    }

    /**
     * 服务站门店是否展示
     *
     * @param id 服务站门店ID
     */
    @PatchMapping(value = "/station/{id}/show")
    @ApiOperation(value = "服务站展示设置")
    @ApiImplicitParam(name = "id", value = "服务站门店id", required = true, dataType = "Long", paramType = "path")
    public void showStation(@PathVariable Integer id) {
        stationService.showStation(id);
    }

    /**
     * 站务系统-查询用户所在服务站门店信息（分页）
     *
     * @param pageNum  页码
     * @param pageSize 页面大小
     * @param query    查询条件
     */
    @GetMapping(value = "/station/admin/{pageNum}/{pageSize}")
    @ApiOperation(value = "查询服务站门店信息（分页）")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "页码", defaultValue = "1", dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "pageSize", value = "页面大小", defaultValue = "10", dataType = "Long", paramType = "path"),
    })
    public PageVO<StationDTO> adminStationList(@PathVariable Integer pageNum,
                                               @PathVariable Integer pageSize,
                                               @RequestBody StationQuery query) {
        return stationService.pageStation(pageNum, pageSize, query);
    }

    /**
     * 查询服务站门店信息（列表）
     *
     * @param query 查询条件
     */
    @GetMapping(value = "/station")
    @ApiOperation(value = "查询服务站门店信息（列表）")
    public List<StationDTO> listStation(StationQuery query) {
        return stationService.listStation(query);
    }

    /**
     * 查询服务站门店信息（单个）
     *
     * @param id 服务站门店ID
     */
    @GetMapping(value = "/station/{id}")
    @ApiOperation(value = "查询服务站门店信息（单个）")
    public StationDTO getStationById(@PathVariable Integer id) {
        StationQuery query = new StationQuery();
        query.setId(id);
        List<StationDTO> list = stationService.listStation(query);
        if (CollectionUtil.isEmpty(list)) {
            return null;
        }
        StationDTO station = list.get(0);
        station.setStationCompanyIds(stationMapper.getStationCompanyId(id));
        return station;
    }

    /**
     * 服务站门店上下线
     *
     * @param id 服务站门店ID
     */
    @PatchMapping(value = "/station/{id}/online")
    @ApiOperation(value = "服务站上下线")
    @ApiImplicitParam(name = "id", value = "服务站门店id", required = true, dataType = "Long", paramType = "path")
    public void onlineOffline(@PathVariable Integer id) {
        stationService.onlineOffline(id);
    }

    /**
     * 服务站推荐
     *
     * @param id 服务站门店ID
     */
    @PatchMapping(value = "/station/{id}/recommend")
    @ApiOperation(value = "服务站推荐")
    @ApiImplicitParam(name = "id", value = "服务站门店id", required = true, dataType = "Long", paramType = "path")
    public void recommend(@PathVariable Integer id) {
        stationService.recommend(id);
    }

    /**
     * 修改服务站门店承包信息
     *
     * @param dto 服务站门店承包信息
     */
    @PatchMapping(value = "/station/contract")
    @ApiOperation(value = "修改服务站门店承包信息")
    @ApiImplicitParam(name = "dto", value = "服务站门店承包信息", required = true, dataType = "StationDTO", paramType = "body")
    public void updateStationContract(@RequestBody StationDTO dto) {
        Station station = new Station(dto);
        stationService.updateStationContractInfo(station);
    }

    /**
     * 修改服务站门店经营信息
     *
     * @param dto 服务站门店经营信息
     */
    @PatchMapping(value = "/station/management")
    @ApiOperation(value = "修改服务站门店经营信息")
    @ApiImplicitParam(name = "dto", value = "服务站门店经营信息", required = true, dataType = "StationDTO", paramType = "body")
    public void updateManage(@RequestBody StationDTO dto) {
        Station station = new Station(dto);
        stationService.updateStationManagementInfo(station);
    }

    /**
     * 获取附近的服务站门店
     *
     * @param lng 经度
     * @param lat 纬度
     */
    @GetMapping(value = "/station/nearby")
    @ApiOperation(value = "获取附近的服务站门店")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "lng", value = "服务站门店lng", required = true, dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "lat", value = "服务站门店lat", required = true, dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "online", value = "是否上线 1- 上线 0 未上线", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "hraIsOnline", value = "服务站hra上线状态", required = false, dataType = "Boolean", paramType = "query")

    })
    public List<StationDTO> getNearbyStation(@RequestParam Double lng,
                                             @RequestParam Double lat,
                                             @RequestParam(required = false) Integer online,
                                             @RequestParam(required = false) Boolean hraIsOnline) {
        return stationService.findStationByLngAndLat(lng, lat, online, hraIsOnline);
    }

    /**
     * 根据省市区获取服务站ID和NAME
     *
     * @param province 省
     * @param city     市
     * @param region   区
     */
    @GetMapping(value = "/station/information")
    @ApiOperation(value = "根据省市区查询服务站门店信息", notes = "根据省市区查询服务站门店信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "province", value = "省", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "city", value = "市", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "region", value = "区", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "type", value = "服务站门店权限类型 0-售前+售后；1-售前 ； 2-售后", dataType = "Long", paramType = "query")
    })
    public Object getStationByPRC(@RequestParam String province, @RequestParam String city, @RequestParam String region, @RequestParam Integer type) {
        return stationService.getStationByPCR(province, city, region, type);
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
        StationDTO stationDTO = stationService.referrerStation();
        return ResponseEntity.ok(stationDTO);
    }


    /**
     * 根据省市区获取服务站ids
     *
     * @param province 省
     * @param city     市
     * @param region   区
     */
    @GetMapping(value = "/station/ids")
    @ApiOperation(value = "根据省市区获取服务站ids", notes = "根据省市区获取服务站ids")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "province", value = "省", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "city", value = "市", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "region", value = "区", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "stationName", value = "名称", dataType = "String", paramType = "query")
    })
    public List<Integer> findStationIdsByPCR(@RequestParam(value = "province", required = false) String province,
                                             @RequestParam(value = "city", required = false) String city,
                                             @RequestParam(value = "region", required = false) String region,
                                             @RequestParam(value = "stationName", required = false) String stationName) {

        return stationService.findStationIdsByPCR(province, city, region, stationName);
    }


    @PostMapping("/station/name/ids")
    @ApiOperation(value = "根据服务站ids查询服务站名称", notes = "根据服务站ids查询服务站名称")
    public List<StationServiceAreaDTO> getStationNameByIds(@RequestParam("stationIds") Set<Integer> stationIds) {
        return stationService.getStationNameByIds(stationIds);
    }


    @PostMapping(value = "/station/export/info")
    @ApiOperation(value = "服务站导出", notes = "服务站导出")
    @ApiImplicitParam(name = "stationName", value = "服务站门店名称", dataType = "String", paramType = "query")
    public Object exportStationInfo(@RequestBody StationQuery query) {
        String url = "/station/export/info";
        ExportRecordDTO record = exportRecordService.save(url, "服务站门店");
        //导出记录添加成功，并且状态为"等待导出"，才入列
        if (record.getStatus() == ExportRecordStatus.WAITING.value) {
            //异步队列执行导出
            Map<String, Object> map = new HashMap<>();
            map.put("exportRecordDTO", record);
            map.put("query", query);
            rabbitTemplate.convertAndSend(RabbitConstant.EXPORT_ACTION_SYSTEM, map);
        }
        return CommResult.ok(record.getId());
    }

    @GetMapping(value = "/station/company/name/{stationId}")
    @ApiOperation(value = "根据服务站id查询服务站公司名称", notes = "根据服务站id查询服务站公司名称")
    public String getStationCompanyNameById(@PathVariable("stationId") Integer stationId) {
        return stationService.getStationCompanyNameById(stationId);
    }

    /**
     * 根据省市区获取服务站门店和服务站公司
     *
     * @param province 省
     * @param city     市
     * @param region   区
     */
    @GetMapping(value = "/stationandcompany/bypcr")
    @ApiOperation(value = "根据省市区获取服务站门店和服务站公司")
    public Object getStationAndCompanyByPCR(@RequestParam String province, @RequestParam String city, @RequestParam String region) {
        Map<String, Object> map = new HashMap<>();
        List<StationCompanyDTO> companyList = stationCompanyMapper.getCompanyByPCR(province, city, region);
        map.put("company", companyList);
        return map;
    }

    /**
     * 根据服务站公司id获取服务站门店
     *
     * @param stationCompanyId 服务站公司id
     * @return
     */
    @GetMapping(value = "/station/byCompanyId")
    @ApiOperation(value = "根据服务站公司id获取服务站门店")
    @ApiImplicitParam(name = "stationCompanyId", value = "服务站公司id", required = true, dataType = "Long", paramType = "query")
    public Object getStationByStationCompanyId(@RequestParam("stationCompanyId") Integer stationCompanyId) {
        return ResponseEntity.ok(stationService.getStationByStationCompanyId(stationCompanyId));
    }
    
    /**
     * 根据服务站公司id获取售后权限的服务站门店
     *
     * @param stationCompanyId 服务站公司id
     * @return
     */
    @GetMapping(value = "/station/afterservice/byCompanyId")
    @ApiOperation(value = "根据服务站公司id获取服务站门店")
    @ApiImplicitParam(name = "stationCompanyId", value = "服务站公司id", required = true, dataType = "Long", paramType = "query")
    public Object getStationByStation(@RequestParam("stationCompanyId") Integer stationCompanyId,@RequestParam("type") Integer type) {
        return ResponseEntity.ok(stationMapper.getAfterServiceStationByStationCompanyId(stationCompanyId,type));
    }

    /**
     * 根据服务站公司id获取服务站门店和服务站服务区域
     *
     * @param stationCompanyId 服务站公司id
     * @return
     */
    @GetMapping(value = "/stationAndServiceArea/byCompanyId")
    @ApiOperation(value = "根据服务站公司id获取服务站门店和服务站服务区域")
    public Object getStationAndServiceArea(@RequestParam("stationCompanyId") Integer stationCompanyId) {
        //根据服务站公司id获取服务站
        List<Station> stations = stationService.getStationByStationCompanyId(stationCompanyId);
        List<StationDTO> stationDTOS = new ArrayList<>();
        if (CollectionUtil.isNotEmpty(stations)) {
            stationDTOS = BeanHelper.copyWithCollection(stations, StationDTO.class);
            for (StationDTO station : stationDTOS) {
                //根据服务站id获取服务站服务区域
                List<StationServiceAreaDTO> stationServiceAreas = stationService.getServiceAreaByStationId(station.getId());
                station.setServiceAreas(stationServiceAreas);
            }
        }
        return ResponseEntity.ok(stationDTOS);
    }


    @GetMapping(value = "/station/recommendId")
    public StationDTO getStationByDistributorId(@RequestParam(name = "recommendId") Integer recommendId) {
        return stationService.getStationByDistributorId(recommendId);
    }

    /**
     * 根据老服务站id查询服务站门店信息
     *
     * @param oldId 老服务站门店ID
     */
    @GetMapping(value = "/station/old/{oldId}")
    @ApiOperation(value = "查询服务站门店信息（单个）")
    public StationDTO getStationByOldId(@PathVariable String oldId) {
        Station query = new Station();
        query.setOldId(oldId);
        Station station = stationMapper.selectOne(query);
        StationDTO stationDTO = new StationDTO();
        station.convert(stationDTO);
        return stationDTO;
    }


    /**
     * 根据站长经销商id查询服务站是否上线
     *
     * @param id
     * @return
     */
    @GetMapping(value = "/station/distributor/{id}")
    @ApiOperation(value = "查询服务站门店信息（单个）")
    public Boolean getStationStatusByDistributorId(@PathVariable Integer id) {
        return stationService.getStationStatusByDistributorId(id);
    }

    /**
     * 修改站长的基本信息
     *
     * @param dto
     */
    @PutMapping(value = "/station/master/info")
    @ApiOperation(value = "修改站长信息")
    @ApiImplicitParam(name = "dto", value = "服务站门店信息", required = true, dataType = "StationDTO", paramType = "body")
    public void updateStationMasterInfo(@RequestBody StationDTO dto) {
        Station station = new Station(dto);
        if (Objects.isNull(station.getId())) {//修改站长姓名，idcard，phone等信息
            Example example = new Example(Station.class);
            Example.Criteria criteria = example.createCriteria();
            criteria.andEqualTo("masterDistributorId", dto.getMasterDistributorId());
            stationMapper.updateByExampleSelective(station, example);
        } else {
            //更换站长
            stationMapper.updateByPrimaryKeySelective(station);
        }
    }

    /**
     * 站务系统- 获取所有服务站及其服务区域
     */
    @GetMapping(value = "/allStation")
    @ApiOperation(value = "查询服务站门店信息（列表）")
    public List<StationDTO> getAllStation() {

        //判断是否为站务系统调用
        Integer type = userCache.getJWTInfo().getType();
        if (type == SystemType.STATION.value) {
            return stationService.getAllStation();
        }
        return null;
    }

    /**
     * 站务系统-服务站--服务站门店管理--详情--修改联系人
     *
     * @return
     */
    @PostMapping(value = "/station/editContactInfo")
    public void editStationContactInfo(@RequestBody StationDTO update) {
        stationService.updateContactInfo(update);
    }

    /**
     * 站务系统-根据id集合查询服务站基本信息
     *
     * @param stationQuery
     * @return
     */
    @PostMapping(value = "/station/getStationListByIds")
    public List<StationDTO> getStationListByIds(@RequestBody StationQuery stationQuery) {
        if (CollectionUtil.isEmpty(stationQuery.getIds())) {
            return null;
        }

        return stationService.getStationListByIds(stationQuery);
    }

    /**
     * 站务系统- 根据绑定门店查询绑定区域的售前售后属性
     *
     * @param areaList
     * @return
     */
    @PostMapping(value = "/station/getAdminStationAreaType")
    public List<SystemAreaTypeDto> getAdminStationAreaType(@RequestBody StationAreaTypeQuery query) {
        //根据服务站门店id查询改门店的区域的售前售后属性
        List<StationServiceAreaDTO> list = stationMapper.getAreaTypeByStationId(query.getStationId());

        //List<Integer> stationAreas = new ArrayList<>();

        List<SystemAreaTypeDto> result = new ArrayList<>();
        if (CollectionUtil.isEmpty(list)) {
            return null;
        } else {
            //站务系统用户绑定该门店的绑定区域
            Set<Integer> areas = query.getAreaIds();
            for (StationServiceAreaDTO stationServiceAreaDTO : list) {
                //stationAreas.add(stationServiceAreaDTO.getAreaId());
                //校验站务系统系统用户绑定的区域，并设置售前售后属性
                if (areas.contains(stationServiceAreaDTO.getAreaId())) {
                    //设置该区域售前售后属性
                    SystemAreaTypeDto systemAreaTypeDto = new SystemAreaTypeDto();
                    systemAreaTypeDto.setAresId(stationServiceAreaDTO.getAreaId());
                    systemAreaTypeDto.setServiceType(stationServiceAreaDTO.getServiceType());
                    result.add(systemAreaTypeDto);
                }
            }

            //剔除服务站门店不包含的区域
//            Iterator<SystemAreaTypeDto> iterator = result.iterator();
//            while (iterator.hasNext()) {
//                SystemAreaTypeDto systemAreaTypeDto = iterator.next();
//
//                if (!stationAreas.contains(systemAreaTypeDto.getAresId())) {
//                    iterator.remove();
//                }
//            }
            log.info("服务站用户区域类型={}", JSON.toJSONString(result));
            return result;
        }

    }
}
