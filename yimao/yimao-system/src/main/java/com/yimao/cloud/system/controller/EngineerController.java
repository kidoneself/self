package com.yimao.cloud.system.controller;

import com.yimao.cloud.base.constant.RabbitConstant;
import com.yimao.cloud.base.enums.ExportRecordStatus;
import com.yimao.cloud.base.msg.CommResult;
import com.yimao.cloud.base.utils.MD5Util;
import com.yimao.cloud.pojo.dto.system.ExportRecordDTO;
import com.yimao.cloud.pojo.dto.user.EngineerDTO;
import com.yimao.cloud.pojo.query.user.EngineerQuery;
import com.yimao.cloud.pojo.vo.PageVO;
import com.yimao.cloud.system.feign.UserFeign;
import com.yimao.cloud.system.service.AreaService;
import com.yimao.cloud.system.service.ExportRecordService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 描述：TODO
 *
 * @Author Zhang Bo
 * @Date 2019/7/8
 */
@RestController
@Api(tags = "EngineerController")
public class EngineerController {

    @Resource
    private UserFeign userFeign;
    @Resource
    private ExportRecordService exportRecordService;
    @Resource
    private RabbitTemplate rabbitTemplate;
    
    @Resource
    private AreaService areaService;

    /**
     * 创建安装工
     *
     * @param dto 安装工信息
     */
    @PostMapping(value = "/engineer")
    @ApiOperation(value = "创建安装工")
    @ApiImplicitParam(name = "dto", value = "安装工信息", required = true, dataType = "EngineerDTO", paramType = "body")
    public void save(@RequestBody EngineerDTO dto) {
        userFeign.saveEngineer(dto);
    }

    /**
     * 更新安装工信息
     *
     * @param dto 安装工信息
     */
    @PutMapping(value = "/engineer")
    @ApiOperation(value = "更新安装工信息")
    @ApiImplicitParam(name = "dto", value = "安装工信息", required = true, dataType = "EngineerDTO", paramType = "body")
    public void update(@RequestBody EngineerDTO dto) {
        if (dto.getPassword() != null) {
            //对前端传的密码进行md5加密进行保存
            dto.setPassword(MD5Util.encodeMD5(dto.getPassword()));
        }
        userFeign.updateEngineer(dto);
    }

    /**
     * 禁用/启用安装工账号
     *
     * @param id 安装工ID
     */
	/*
	 * @PatchMapping(value = "/engineer/{id}")
	 * 
	 * @ApiOperation(value = "禁用/启用安装工账号")
	 * 
	 * @ApiImplicitParam(name = "id", value = "安装工ID", required = true, dataType =
	 * "Long", paramType = "path") public void forbidden(@PathVariable Integer id) {
	 * userFeign.forbiddenEngineer(id); }
	 */

    /**
     * 解绑（安装工账号和手机ICCID的绑定）
     *
     * @param id 安装工ID
     */
    @PatchMapping(value = "/engineer/{id}/unbind")
    @ApiOperation(value = "解绑（安装工账号和手机ICCID的绑定）")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "老安装工ID", required = true, dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "type", value = "请求端 1-业务管理系统 2-售后系统", required = true, dataType = "Long", paramType = "query"),
    })
    public void unbind(@PathVariable("id") String id,
                       @RequestParam("type") Integer type) {
        userFeign.unbindIccid(id, type);
    }

    /**
     * 绑定ICCID
     */
    @PatchMapping(value = "/engineer/{id}/binding")
    @ApiOperation(value = "绑定ICCID")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "安装工ID", required = true, dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "iccid", value = "iccid", required = true, dataType = "String", paramType = "query"),
    })
    public void unbind(@PathVariable Integer id, @RequestParam String iccid) {
        userFeign.bindingIccid(id, iccid);
    }

    /**
     * 获取安装工详细信息
     *
     * @param id 安装工ID
     */
    @GetMapping(value = "/engineer/{id}/detail")
    @ApiOperation(value = "获取安装工详细信息")
    @ApiImplicitParam(name = "id", value = "安装工ID", required = true, dataType = "Long", paramType = "path")
    public EngineerDTO detail(@PathVariable Integer id) {
        return userFeign.getEngineerDetailById(id);
    }

    /**
     * 分页查询安装工
     *
     * @param pageNum  当前页
     * @param pageSize 每页大小
     * @param query    查询条件
     */
    @PostMapping(value = "/engineers/{pageNum}/{pageSize}")
    @ApiOperation(value = "分页查询安装工")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "第几页", required = true, dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "pageSize", value = "每页大小", required = true, dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "query", value = "查询条件", dataType = "StationEngineerQuery", paramType = "body")
    })
    public PageVO<EngineerDTO> page(@PathVariable Integer pageNum, @PathVariable Integer pageSize, @RequestBody EngineerQuery query) {
        return userFeign.pageEngineer(pageNum, pageSize, query);
    }

    /**
     * 导出安装工列表
     */
    @PostMapping(value = "/engineers/export")
    @ApiOperation(value = "导出安装工列表")
    @ApiImplicitParams({@ApiImplicitParam(name = "userName", value = "安装工账号", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "realName", value = "安装工姓名", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "phone", value = "联系方式", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "stationCompanyName", value = "服务站公司", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "stationName", value = "服务站门店", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "province", value = "省", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "city", value = "市", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "region", value = "区", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "forbidden", value = "是否禁用：0-否，1-是", dataType = "Boolean", paramType = "query"),
            @ApiImplicitParam(name = "startTime", value = "开始时间", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "endTime", value = "结束时间", dataType = "String", paramType = "query")
    })
    public Object exportEngineer(@RequestParam(value = "userName", required = false) String userName,
                                 @RequestParam(value = "realName", required = false) String realName,
                                 @RequestParam(value = "phone", required = false) String phone,
                                 @RequestParam(value = "stationCompanyName", required = false) String stationCompanyName,
                                 @RequestParam(value = "stationName", required = false) String stationName,
                                 @RequestParam(value = "province", required = false) String province,
                                 @RequestParam(value = "city", required = false) String city,
                                 @RequestParam(value = "region", required = false) String region,
                                 @RequestParam(value = "forbidden", required = false) Boolean forbidden,
                                 @RequestParam(value = "startTime", required = false) Date startTime,
                                 @RequestParam(value = "endTime", required = false) Date endTime,
                                 @RequestParam(value = "areaId", required = false) Integer areaId) {
        EngineerQuery query = new EngineerQuery();
        query.setUserName(userName);
        query.setRealName(realName);
        query.setPhone(phone);
        query.setStationCompanyName(stationCompanyName);
        query.setStationName(stationName);
		/*
		 * query.setProvince(province); query.setCity(city); query.setRegion(region);
		 */
        query.setForbidden(forbidden);
        query.setStartTime(startTime);
        query.setEndTime(endTime);
        query.setAreaId(areaId);
        //保存导出记录，如果短时间操作多次，会提示操作过于频繁
        String url = "/engineers/export";
        ExportRecordDTO record = exportRecordService.save(url, "安装工列表");

        //导出记录添加成功，并且状态为"等待导出"，才入列
        if (record.getStatus() == ExportRecordStatus.WAITING.value) {
            //异步队列执行导出
            Map<String, Object> map = new HashMap<>();
            map.put("exportRecordDTO", record);
            map.put("query", query);
            rabbitTemplate.convertAndSend(RabbitConstant.EXPORT_ACTION_USER, map);
        }
        return CommResult.ok(record.getId());
    }

    /***
     * 根据省市区查询安装工信息
     *
     * @param province
     * @param city
     * @param region
     * @return
     */
    @PostMapping(value = "/engineers/list")
    @ApiOperation(value = "根据城市区域查询安装工信息")
    @ApiImplicitParams({@ApiImplicitParam(name = "province", value = "省", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "city", value = "市", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "region", value = "区", dataType = "String", paramType = "query")})
    public ResponseEntity<List<EngineerDTO>> getEngineer(
            @RequestParam(value = "province", required = false) String province,
            @RequestParam(value = "city", required = false) String city,
            @RequestParam(value = "region", required = false) String region) {
    	
    	//先根据省市区获取areaId,
    	Integer areaId=areaService.getRegionIdByPCR(province, city, region);
        
    	List<EngineerDTO> data =null;
    	if(null!=areaId) {
        	data = userFeign.listEngineerByRegion(areaId);
        }
    	
        return ResponseEntity.ok(data);
    }

    /***
     * 获取当前安装工所属服务站下的所有未禁用的安装工信息
     * @param id
     * @return
     */
    @ApiOperation(value = "获取当前安装工所属服务站下的所有未禁用的安装工信息")
    @ApiImplicitParam(name = "id", value = "安装工ID", required = true, dataType = "Long", paramType = "path")
    @GetMapping(value = "/engineers/station/{id}")
    public Object getEngineerListByEngineerId(@PathVariable Integer id) {
        return ResponseEntity.ok(userFeign.getEngineerListByEngineerId(id));

    }

    /**
     * 安装工转让后禁用/启用(3.0小版本)
     *
     * @param id 安装工ID
     */
    @PatchMapping(value = "/engineer/{oldId}/transfer")
    @ApiOperation(value = "安装工转让并禁用")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "oldId", value = "老的安装工ID", required = true, dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "newId", value = "新安装工id", dataType = "Long", paramType = "query"),
    })
    public void transferEngineer(@PathVariable Integer oldId, @RequestParam(value = "newId", required = false) Integer newId) {
        userFeign.transferEngineer(oldId, newId);
    }

    /**
     * 根据服务站id获取该服务站下的所有安装工
     *
     * @return
     */
    @GetMapping(value = "/user/engineerlist/byStationId/{stationId}")
    @ApiOperation(value = "根据服务站id获取该服务站下的所有安装工")
    @ApiImplicitParam(name = "stationId", value = "服务站id", required = true, dataType = "Long", paramType = "path")
    public Object getEngineerListByStationId(@PathVariable Integer stationId) {
        return userFeign.getEngineerListByStationId(stationId);
    }

}