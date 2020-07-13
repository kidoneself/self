package com.yimao.cloud.user.controller;

import com.yimao.cloud.base.exception.NotFoundException;
import com.yimao.cloud.base.utils.BeanHelper;
import com.yimao.cloud.pojo.dto.system.TransferAreaInfoDTO;
import com.yimao.cloud.pojo.dto.system.UpdateEngineerServiceAreaDataInfo;
import com.yimao.cloud.pojo.dto.user.EngineerChangeRecordDTO;
import com.yimao.cloud.pojo.dto.user.EngineerDTO;
import com.yimao.cloud.pojo.dto.user.EngineerServiceAreaDTO;
import com.yimao.cloud.pojo.query.station.StationEngineerQuery;
import com.yimao.cloud.pojo.query.user.EngineerQuery;
import com.yimao.cloud.pojo.vo.PageVO;
import com.yimao.cloud.pojo.vo.station.EngineerVO;
import com.yimao.cloud.user.mapper.EngineerServiceAreaMapper;
import com.yimao.cloud.user.po.Engineer;
import com.yimao.cloud.user.service.EngineerService;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Set;

/**
 * @author Zhang Bo
 * @date 2018/7/22.
 */
@RestController
public class EngineerController {

    @Resource
    private EngineerService engineerService;

    @Resource
    private EngineerServiceAreaMapper engineerServiceAreaMapper;

    /**
     * 创建安装工
     *
     * @param dto 安装工信息
     */
    @PostMapping(value = "/engineer")
    public void save(@RequestBody EngineerDTO dto) {
        Engineer engineer = new Engineer(dto);
        engineerService.save(engineer);
    }

    /**
     * 更新安装工信息
     */
    @PutMapping(value = "/engineer")
    public void update(@RequestBody EngineerDTO dto) {
        Engineer engineer = new Engineer(dto);
        engineerService.update(engineer);
    }

    /**
     * 安装工改密码
     */
    @PatchMapping(value = "/engineer/password")
    public void updatePassword(@RequestBody EngineerDTO dto) {
        Engineer engineer = new Engineer(dto);
        engineerService.updatePassword(engineer);
    }

    /**
     * 禁用/启用安装工账号
     *
     * @param id 安装工ID
     */
    @PatchMapping(value = "/engineer/{id}")
    public void forbidden(@PathVariable Integer id) {
        Engineer engineer = engineerService.getById(id);
        if (engineer == null) {
            throw new NotFoundException("未找到安装工信息。");
        }
        engineerService.forbidden(engineer);
    }

    /**
     * 解绑（安装工账号和手机ICCID的绑定）
     *
     * @param id   老安装工ID
     * @param type 请求端 1-业务管理系统 2-售后系统
     */
    @PatchMapping(value = "/engineer/{id}/unbind")
    public void unbind(@PathVariable("id") String id, @RequestParam("type") Integer type) {
        Engineer engineer = engineerService.getEngineerByOldId(id);
        if (engineer == null) {
            throw new NotFoundException("未找到安装工信息。");
        }
        engineerService.unbind(engineer, type);
    }

    /**
     * 绑定ICCID
     */
    @PatchMapping(value = "/engineer/{id}/binding")
    public void binding(@PathVariable Integer id, @RequestParam String iccid) {
        Engineer engineer = engineerService.getById(id);
        if (engineer == null) {
            throw new NotFoundException("未找到安装工信息。");
        }
        engineerService.binding(engineer, iccid);
    }

    /**
     * 根据安装工ID获取安装工信息
     *
     * @param id 安装工ID
     */
    @GetMapping(value = "/engineer/{id}")
    public EngineerDTO getById(@PathVariable Integer id) {
        Engineer engineer = engineerService.getById(id);
        if (engineer == null) {
            return null;
        }
        EngineerDTO dto = new EngineerDTO();
        engineer.convert(dto);
        //获取安装工服务区域id集合
        dto.setServiceAreaList(getServiceAreaListById(engineer.getId()));
        return dto;
    }

    /**
     * 根据安装工ID获取安装工信息（消息推送时只需获取很少的几个字段）
     *
     * @param id 安装工ID
     */
    @GetMapping(value = "/engineer/{id}/formsgpushinfo")
    public EngineerDTO getBasicInfoByIdForMsgPushInfo(@PathVariable Integer id) {
        return engineerService.getBasicInfoByIdForMsgPushInfo(id);
    }

    /**
     * 获取安装工详细信息
     *
     * @param id 安装工ID
     */
    @GetMapping(value = "/engineer/{id}/detail")
    public EngineerDTO getDetailById(@PathVariable Integer id) {
        Engineer engineer = engineerService.getById(id);
        if (engineer == null) {
            return null;
        }
        EngineerDTO dto = new EngineerDTO();
        engineer.convert(dto);
        dto.setPassword(null);

        //获取安装工服务区域id集合
        dto.setServiceAreaList(getServiceAreaListById(engineer.getId()));
        // 查询安装工信息变更记录
        List<EngineerChangeRecordDTO> changeRecords = engineerService.getEngineerChangeRecords(id);
        dto.setChangeRecords(changeRecords);
        return dto;
    }

    /***
     * 获取安装工服务区域
     * @param id
     * @return
     */
    private List<EngineerServiceAreaDTO> getServiceAreaListById(Integer id) {
        return engineerServiceAreaMapper.selectAreaListByEngineerId(id);
    }

    /**
     * 用户--安装工--根据id获取安装工详细信息（站务系统调用）
     *
     * @param id 安装工ID
     */
    @GetMapping(value = "/engineer/station/{id}/detail")
    public Object getDetailByIdToStation(@PathVariable(value = "id") Integer id) {
        Engineer engineer = engineerService.getById(id);
        if (engineer == null) {
            return null;
        }

        EngineerVO engineerVO = BeanHelper.copyProperties(engineer, EngineerVO.class);
        engineerVO.setServiceAreaList(getServiceAreaListById(engineer.getId()));
        return ResponseEntity.ok(engineerVO);
    }

    /**
     * 分页查询安装工
     *
     * @param pageNum  当前页
     * @param pageSize 每页大小
     * @param query    查询条件
     */
    @PostMapping(value = "/engineers/{pageNum}/{pageSize}")
    public PageVO<EngineerDTO> page(@PathVariable Integer pageNum, @PathVariable Integer pageSize,
                                    @RequestBody EngineerQuery query) {
        return engineerService.page(pageNum, pageSize, query);
    }

    /**
     * 用户--安装工分页查询（站务系统调用）
     *
     * @param pageNum  当前页
     * @param pageSize 每页大小
     * @param query    查询条件
     */
    @PostMapping(value = "/engineers/station/{pageNum}/{pageSize}")
    public Object pageEngineerInfoToStation(@PathVariable Integer pageNum, @PathVariable Integer pageSize,
                                            @RequestBody StationEngineerQuery query) {
        return ResponseEntity.ok(engineerService.pageEngineerInfoToStation(pageNum, pageSize, query));
    }

    /**
     * 根据用户名查询安装工
     *
     * @param userName 安装工用户名
     */
    @GetMapping(value = "/engineer")
    public EngineerDTO getByUserName(@RequestParam(required = false) String userName,
                                     @RequestParam(required = false) String oldId) {
        Engineer engineer = engineerService.getByUserName(userName, oldId);
        if (engineer == null) {
            return null;
        }
        EngineerDTO dto = new EngineerDTO();
        engineer.convert(dto);
        return dto;
    }

    /**
     * 根据手机号查询安装工
     *
     * @param phone 安装工手机号
     */
    @GetMapping(value = "/engineer/phone")
    public EngineerDTO getEngineerByPhone(@RequestParam(value = "phone") String phone) {
        Engineer engineer = engineerService.getByUserPhone(phone);
        if (engineer == null) {
            return null;
        }
        EngineerDTO dto = new EngineerDTO();
        engineer.convert(dto);
        return dto;
    }

    /**
     * 检查iccid是否已经存在
     *
     * @param iccid SIM卡卡号
     */
    @GetMapping(value = "/engineer/check/iccid")
    public Boolean checkEngineerIccid(@RequestParam String iccid) {
        return engineerService.checkEngineerIccid(iccid);
    }

    /**
     * 根据省市区查询安装工列表
     *
     * @param province 省
     * @param city     市
     * @param region   区
     *//*
     * @GetMapping(value = "/engineers") public List<EngineerDTO>
     * listEngineerByPCR(@RequestParam(value = "province", required = false) String
     * province, @RequestParam(value = "city", required = false) String
     * city, @RequestParam(value = "region", required = false) String region) {
     * return engineerService.getEngineerByArea(province, city, region); }
     */

    /**
     * 根据区域id安装工数量（包含已经禁用的）
     */
    @GetMapping(value = "/engineers/count")
    public int countEngineerByPCR(@RequestParam(value = "areaId") Integer areaId) {
        return engineerService.countEngineerByArea(areaId);
    }

    /**
     * 根据区域id 校验安装工是否存在
     *
     * @param province 省
     * @param city     市
     * @param region   区
     */
    @GetMapping(value = "/engineer/exists")
    public Object checkEngineerExistsByPCR(@RequestParam Integer areaId) {
        return engineerService.checkEngineerExistsByPCR(areaId);
    }

    /**
     * 根据老的安装工ID获取安装工信息
     *
     * @param oldId 老的安装工ID
     */
    @GetMapping(value = "/engineer/old/{oldId}")
    @ApiOperation(value = "根据老的安装工ID获取安装工信息")
    @ApiImplicitParam(name = "oldId", value = "老的安装工ID", required = true, dataType = "String", paramType = "path")
    public EngineerDTO getEngineerByOldId(@PathVariable String oldId) {
        Engineer engineer = engineerService.getEngineerByOldId(oldId);
        if (engineer == null) {
            return null;
        }
        EngineerDTO dto = new EngineerDTO();
        engineer.convert(dto);
        dto.setPassword(null);
        return dto;
    }

    /**
     * @Author Liu Long Jie
     * @Description 根据服务地区id 获取安装工id集合
     * @Date 2020-2-13 15:13:25
     * @Param
     **/
    @PostMapping(value = "/engineer/ids/areas")
    public ResponseEntity<List<Integer>> getEngineerIdsByAreaIds(
            @RequestBody Set<Integer> areaIds) {
        return ResponseEntity.ok(engineerService.getEngineerIdsByAreaIds(areaIds));
    }

    /**
     * 站务系统--订单--安装工单--安装工程师筛选条件
     *
     * @param query
     * @return
     */
    @GetMapping(value = "/user/engineer/station/list")
    public Object getEngineerList(@RequestBody StationEngineerQuery query) {

        return ResponseEntity.ok(engineerService.getEngineerListByStationIds(query.getStations()));
    }

    /***
     * 获取当前安装工所属服务站的所有未禁用的安装工信息
     * @return
     */
    @GetMapping(value = "/user/engineerlist/station/{id}")
    public List<EngineerDTO> getEngineerListByEngineerId(@PathVariable Integer id) {
        return engineerService.getEngineerListByEngineerId(id);
    }

    /**
     * 根据服务站id获取该服务站下的所有安装工
     *
     * @return
     */
    @GetMapping(value = "/user/engineerlist/byStationId/{stationId}")
    public Object getEngineerListByStationId(@PathVariable Integer stationId) {
        return ResponseEntity.ok(engineerService.getEngineerListByStationId(stationId));
    }

    /***
     * 安装工转让
     * @param oldId
     * @param newId
     */
    @RequestMapping(value = "/engineer/transfer/{oldId}", method = RequestMethod.PATCH)
    public void transferEngineer(@PathVariable(value = "oldId") Integer oldId, @RequestParam(value = "newId", required = false) Integer newId) {
        engineerService.transferEngineer(oldId, newId);
    }

    /**
     * 将指定服务区域下的安装工转给指定服务站门店
     *
     * @param transferAreaInfo 转让区域及承包方信息
     */
    @PostMapping(value = "/engineer/transfer")
    public Object transferEngineerToNewStationByServiceArea(@RequestBody TransferAreaInfoDTO transferAreaInfo) {
        engineerService.transferEngineerToNewStationByServiceArea(transferAreaInfo);
        return ResponseEntity.noContent().build();
    }

    /**
     * 将原服务于指定服务区域的安装工对该区域的服务权限删除，给指定安装工新增对该地区的服务权限
     *
     * @param transferAreaInfo 转让区域及承包方信息
     */
    @PostMapping(value = "/engineer/updateServiceArea")
    public Object engineerUpdateServiceArea(@RequestBody TransferAreaInfoDTO transferAreaInfo) {
        engineerService.engineerUpdateServiceArea(transferAreaInfo);
        return ResponseEntity.noContent().build();
    }

    /****
     * 根据areaId获取安装工
     *
     * @param areaId
     * @return
     */
    @GetMapping(value = "/engineers/area")
    public List<EngineerDTO> getEngineerDataByArea(@RequestParam(value = "areaId") Integer areaId) {
        return engineerService.getEngineerListByArea(areaId);
    }

    /**
     * 修改绑定指定服务站门店的安装工的服务区域
     *
     * @param updateEngineerServiceAreaDataInfo
     */
    @PutMapping(value = "/user/engineer/updateEngineerServiceArea")
    public Object updateEngineerServiceArea(@RequestBody UpdateEngineerServiceAreaDataInfo updateEngineerServiceAreaDataInfo) {
        engineerService.updateEngineerServiceArea(updateEngineerServiceAreaDataInfo.getServiceAreaList(), updateEngineerServiceAreaDataInfo.getStationId());
        return ResponseEntity.noContent().build();
    }


    @GetMapping(value = "/engineer/service/area/{id}")
    public Object getEngineerServiceArea(@PathVariable Integer id) {
        return engineerService.getEngineerServiceArea(id);
    }


    /**
     * 更新安装工头像
     */
    @PutMapping(value = "/engineer/head")
    public void updateHeadImg(@RequestBody EngineerDTO dto) {
        engineerService.updateHeadImg(dto);
    }

}
