package com.yimao.cloud.system.controller;

import com.yimao.cloud.base.constant.ExportUrlConstant;
import com.yimao.cloud.base.constant.RabbitConstant;
import com.yimao.cloud.base.enums.ExportRecordStatus;
import com.yimao.cloud.base.exception.BadRequestException;
import com.yimao.cloud.base.msg.CommResult;
import com.yimao.cloud.base.utils.CollectionUtil;
import com.yimao.cloud.pojo.dto.system.*;
import com.yimao.cloud.pojo.query.system.StationCompanyQuery;
import com.yimao.cloud.pojo.vo.PageVO;
import com.yimao.cloud.system.service.ExportRecordService;
import com.yimao.cloud.system.service.StationCompanyService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.*;

/**
 * 区县级公司
 *
 * @author Lizhqiang
 * @date 2019/1/17
 */
@RestController
@Api(tags = "StationCompanyController")
public class StationCompanyController {

    @Resource
    private StationCompanyService stationCompanyService;
    @Resource
    private ExportRecordService exportRecordService;
    @Resource
    private RabbitTemplate rabbitTemplate;

    /**
     * 新增门店--根据选择的服务站公司列出该服务站公司下的所有服务区域及售前售后权限
     *
     * @return id 服务站公司id
     */
    @GetMapping(value = "/station/company/ServiceArea/serviceType/{id}")
    @ApiOperation(value = "根据选择的服务站公司列出该服务站公司下的所有服务区域及售前售后权限")
    @ApiImplicitParam(name = "id", value = "服务站公司id", required = true, dataType = "Long", paramType = "path")
    public Object getStationCompanyServiceAreaAndServiceType(@PathVariable Integer id) {
        return ResponseEntity.ok(stationCompanyService.getStationCompanyServiceAreaAndServiceType(id));
    }

    /**
     * 服务站公司服务区域承包转让
     *
     * @param serviceAreaContractInfo
     * @return
     */
    @PostMapping(value = "/station/company/serviceAreaContractMakeOver")
    @ApiOperation(value = "服务站公司服务区域承包转让")
    @ApiImplicitParam(name = "serviceAreaContractInfo", value = "服务站公司服务区域承包转让前端传参信息封装类", dataType = "ServiceAreaContractInfoDTO", paramType = "body")
    public Object serviceAreaContractMakeOver(@RequestBody ServiceAreaContractInfoDTO serviceAreaContractInfo) {
        List<StationCompanyServiceAreaDTO> originalStationCompanyServiceAreaList = serviceAreaContractInfo.getOriginalStationCompanyServiceAreaList();
        if (CollectionUtil.isEmpty(originalStationCompanyServiceAreaList)) {
            throw new BadRequestException("不满足承包转让条件，服务站公司转让的服务区域不能为空！");
        }
        ContractStationAndStationCompanyDTO contractStationAndStationCompany = serviceAreaContractInfo.getContractStationAndStationCompany();
        Integer stationCompanyId = contractStationAndStationCompany.getStationCompanyId();
        if (stationCompanyId == null) {
            throw new BadRequestException("不满足承包转让条件，请选择承包方服务站公司。");
        }
        Integer stationId = contractStationAndStationCompany.getStationId();
        if (stationId == null) {
            throw new BadRequestException("不满足承包转让条件，请选择承包方服务站门店。");
        }
        // 转让标识（1-转让方转了所有售后服务区域；2-转让方仅转让部分售后服务区域）
        Integer type = serviceAreaContractInfo.getType();
        Integer engineerId = contractStationAndStationCompany.getEngineerId();
        if (type == null) {
            throw new BadRequestException("转让标识未传！");
        }
        if (type == 2) {
            //仅转让部分售后服务区域，则需指定安装工接收转让方的工单、设备
            if (engineerId == null) {
                throw new BadRequestException("不满足承包转让条件，请选择服务人员。");
            }
        }
        stationCompanyService.serviceAreaContractMakeOver(originalStationCompanyServiceAreaList, stationCompanyId, stationId, engineerId, type);
        return ResponseEntity.noContent().build();
    }

    /**
     * 新增编辑服务站公司选择省市区后返回具体的服务区域信息
     *
     * @param province
     * @param city
     * @param region
     * @return
     */
    @GetMapping(value = "/station/company/getStationCompanyServiceArea")
    @ApiOperation(value = "新增编辑服务站公司选择省市区后返回具体的服务区域信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "province", value = "省", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "city", value = "市", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "region", value = "区", required = true, dataType = "String", paramType = "query")
    })
    public Object getStationCompanyServiceAreaByPCR(@RequestParam String province,
                                                    @RequestParam String city,
                                                    @RequestParam String region) {

        return stationCompanyService.getStationCompanyServiceAreaByPCR(province, city, region);
    }

    /**
     * 查询服务站公司售后服务区域
     *
     * @param id 区县级公司id
     * @return 服务站公司的售后服务区域
     */
    @GetMapping(value = "/station/company/getAfterSaleServiceArea/{id}")
    @ApiOperation(value = "查询服务站公司售后服务区域")
    @ApiImplicitParam(name = "id", value = "服务站公司id", required = true, dataType = "Long", paramType = "path")
    public Object getAfterSaleServiceArea(@PathVariable(value = "id") Integer id) {
        return stationCompanyService.getAfterSaleServiceArea(id);
    }

    /**
     * 区县级公司注册云签用户
     *
     * @param id 区县级公司id
     * @return 注册成功与否，成功返回true
     */
    @PostMapping(value = "/station/company/registerYunSign/{id}")
    @ApiOperation(value = "区县级公司注册云签用户")
    @ApiImplicitParam(name = "id", value = "服务站公司id", required = true, dataType = "Long", paramType = "path")
    public Object registerCompanyUser(@PathVariable(value = "id") Integer id) {
        return stationCompanyService.registerCompanyUser(id);
    }

    /**
     * 新增区县级公司
     *
     * @param dto 区县级公司
     */
    @PostMapping(value = "/station/company")
    @ApiOperation(value = "新增区县级公司")
    @ApiImplicitParam(name = "dto", value = "服务站公司信息", dataType = "StationCompanyDTO", paramType = "body")
    public void saveStationCompany(@RequestBody StationCompanyDTO dto) {
        //校验区县级公司服务区域
        List<StationCompanyServiceAreaDTO> serviceAreaDTOList = dto.getServiceAreas();
        List<Integer> areaIds = new ArrayList<>();
        //判断是否选择设置服务，如选是则服务区域为必传
        if (dto.getIsSetServiceArea() != null && dto.getIsSetServiceArea()) {
            //设置服务选择了是
            if (CollectionUtil.isEmpty(serviceAreaDTOList)) {
                throw new BadRequestException("请选择区县级公司服务区域。");
            } else {
                for (StationCompanyServiceAreaDTO serviceAreaDTO : serviceAreaDTOList) {
                    areaIds.add(serviceAreaDTO.getAreaId());
                }
                if (dto.getOnline() != null && dto.getOnline()) {
                    //设置上线时间
                    dto.setOnlineTime(new Date());
                }
            }
        } else {
            //设置服务选择了 否，则无法立即上线
            if (dto.getOnline() != null && dto.getOnline()) {
                throw new BadRequestException("服务区域为空，不能进行上线操作！");
            }
        }
        stationCompanyService.saveStationCompany(dto, areaIds);
    }

    /**
     * 修改区县级公司
     *
     * @param dto 区县级公司
     */
    @PutMapping(value = "/station/company")
    @ApiOperation(value = "修改区县级公司")
    @ApiImplicitParam(name = "dto", value = "服务站公司信息", dataType = "StationCompanyDTO", paramType = "body")
    public void updateStationCompany(@RequestBody StationCompanyDTO dto) {
        if (dto.getId() == null) {
            throw new BadRequestException("区县级公司ID不能为空。");
        }
        //校验区县级公司服务区域
        List<StationCompanyServiceAreaDTO> serviceAreaDTOList = dto.getServiceAreas();
        List<Integer> areaIds = new ArrayList<>();
        if (CollectionUtil.isNotEmpty(serviceAreaDTOList)) {
            for (StationCompanyServiceAreaDTO serviceAreaDTO : serviceAreaDTOList) {
                areaIds.add(serviceAreaDTO.getAreaId());
            }
        } else {
            if (dto.getOnline() != null && dto.getOnline()) {
                throw new BadRequestException("服务区域为空，不能进行上线操作！");
            }
        }
        stationCompanyService.updateStationCompany(dto, areaIds);
    }

    /**
     * 查询区县级公司（分页）
     *
     * @param pageNum  页码
     * @param pageSize 页面大小
     * @param query    查询条件
     */
    @GetMapping(value = "/station/company/{pageNum}/{pageSize}")
    @ApiOperation(value = "查询区县级公司（分页）")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "页码", required = true, defaultValue = "1", dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "pageSize", value = "页面大小", required = true, defaultValue = "10", dataType = "Long", paramType = "path")
    })
    public PageVO<StationCompanyDTO> pageStationCompany(@PathVariable Integer pageNum,
                                                        @PathVariable Integer pageSize,
                                                        StationCompanyQuery query) {
        return stationCompanyService.pageStationCompany(pageNum, pageSize, query);
    }

    /******
     * 根据省市区查询服务站公司
     * @param province
     * @param city
     * @param region
     * @param type 1:售前,2:售后
     * @return
     */
    @GetMapping(value = "/station/company/information")
    @ApiOperation(value = "根据省市区查询服务站公司")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "province", value = "省", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "city", value = "市", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "region", value = "区", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "type", value = "服务类型", required = true, dataType = "Long", paramType = "query")
    })
    public StationCompanyDTO getStationCompanyByPCR(@RequestParam String province,
                                                    @RequestParam String city,
                                                    @RequestParam String region,
                                                    @RequestParam Integer type) {
        return stationCompanyService.getStationCompanyByPCR(province, city, region, type);
    }

    /**
     * 根据省市区查询所在地在该地区的服务站公司
     */
    @GetMapping(value = "/station/company/byLocation")
    @ApiOperation(value = "根据省市区查询所在地在该地区的服务站公司")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "province", value = "省", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "city", value = "市", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "region", value = "区", required = true, dataType = "String", paramType = "query")
    })
    public Object getStationCompanyByLocation(@RequestParam String province,
                                              @RequestParam String city,
                                              @RequestParam String region) {
        return ResponseEntity.ok(stationCompanyService.getStationCompanyByLocation(province, city, region));
    }

    /**
     * 查询区县级公司（单个）
     *
     * @param id 区县级公司ID
     */
    @GetMapping(value = "/station/company/{id}")
    @ApiOperation(value = "查询区县级公司（单个）")
    @ApiImplicitParam(name = "id", value = "区县级公司ID", required = true, dataType = "Long", paramType = "path")
    public StationCompanyDTO getStationCompanyById(@PathVariable Integer id) {
        StationCompanyQuery query = new StationCompanyQuery();
        query.setId(id);
        List<StationCompanyDTO> list = stationCompanyService.listStationCompany(query);
        if (CollectionUtil.isEmpty(list)) {
            return null;
        }
        return list.get(0);
    }

    /**
     * 区县级公司上下线
     *
     * @param id 区县级公司ID
     */
    @PatchMapping(value = "/station/company/{id}/online")
    @ApiOperation(value = "区县级公司上下线")
    @ApiImplicitParam(name = "id", value = "区县级公司ID", required = true, dataType = "Long", paramType = "path")
    public void onlineOffline(@PathVariable Integer id) {
        stationCompanyService.onlineOffline(id);
    }

    /**
     * 判断公司服务区域是否使用
     *
     * @param area 区域
     */
    @GetMapping(value = "/station/company/area")
    @ApiOperation(value = "判断公司服务区域是否使用")
    @ApiImplicitParam(name = "area", value = "区域", required = true, dataType = "String", paramType = "query")
    public Object isAreaUsed(@RequestParam String area) {
        boolean bool = stationCompanyService.isAreaUsed(area);
        return ResponseEntity.ok(bool);
    }

    /**
     * @param province
     * @param city
     * @param region
     * @param name
     * @param contact
     * @param contactPhone
     * @param areaId
     * @param online
     * @param startTime
     * @param endTime
     * @return
     */
    @PostMapping(value = "/company/export/info")
    @ApiOperation(value = "服务站公司导出", notes = "服务站公司导出")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "name", value = "服务站公司名称", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "contact", value = "联系人", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "contactPhone", value = "联系人电话", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "areaId", value = "服务区域", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "province", value = "服务区域省", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "city", value = "服务区域市", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "region", value = "服务区域区", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "online", value = "是否上线：0-未上线；1-已上线；", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "signUp", value = "是否注册云签：0-未注册；1-已注册；", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "startTime", value = "开始时间", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "endTime", value = "结束时间", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "locationProvince", value = "所在地省", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "locationCity", value = "所在地市", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "locationRegion", value = "所在地区", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "serviceType", value = "服务类型", dataType = "Long", paramType = "query"),
    })
    public Object exportStationInfo(@RequestParam(required = false) String province,
                                    @RequestParam(required = false) String city,
                                    @RequestParam(required = false) String region,
                                    @RequestParam(required = false) String name,
                                    @RequestParam(required = false) String contact,
                                    @RequestParam(required = false) String contactPhone,
                                    @RequestParam(required = false) Integer areaId,
                                    @RequestParam(required = false) Integer online,
                                    @RequestParam(required = false) Integer signUp,
                                    @RequestParam(required = false) Date startTime,
                                    @RequestParam(required = false) Date endTime,
                                    @RequestParam(required = false) String locationProvince,
                                    @RequestParam(required = false) String locationCity,
                                    @RequestParam(required = false) String locationRegion,
                                    @RequestParam(required = false) Integer serviceType,
                                    @RequestParam(required = false) Integer type) {

      /*  List<StationCompanyExportDTO> resultList = stationCompanyService.getStationCompanyInfoToExport(province, city, region, name, contact, contactPhone,
                areaId, online,signUp, startTime, endTime);
        if (CollectionUtil.isEmpty(resultList)) {
            throw new NotFoundException("未找到数据");
        }
        String header = "服务站公司信息";
        String[] beanProperties = new String[]{"code", "name", "type", "province", "city", "region", "address", "serviceProvince",
                "serviceCity", "serviceRegion", "contact", "contactIdCard", "contactPhone", "email", "creditCode", "legalPerson", "bank", "bankAccount", "bankNumber", "taxNumber", "onlineTime","signUp","yunSignTime"};
        String[] titles = new String[]{"服务站门店编号", "服务站公司名称", "服务站公司类型", "所在区域（省）", "所在区域（市）", "所在区域（区）",
                "详细地址", "服务区域（省）", "	服务区域（市）	", "服务区域（区）", "联系人", "身份证号", "联系方式", "服务站公司邮箱", "统一社会信用码", "法人代表姓名", "公司开户行",
                "开户行账号", "开户行行号", "开户行税号", "上线时间","是否注册过云签","注册云签时间"};
        boolean boo = ExcelUtil.exportSXSSF(resultList, header, titles.length - 1, titles, beanProperties, response);
        if (boo) {
            return ResponseEntity.noContent().build();
        }*/
        //保存导出记录，如果短时间操作多次，会提示操作过于频繁
        ExportRecordDTO record = exportRecordService.save(ExportUrlConstant.EXPORT_STATION_COMPANY_URL, "服务站公司");
        StationCompanyQuery query = new StationCompanyQuery();
        query.setName(name);
        query.setContact(contact);
        query.setContactPhone(contactPhone);
        query.setAreaId(areaId);
        query.setProvince(province);
        query.setCity(city);
        query.setRegion(region);
        query.setSignUp(signUp);
        query.setOnline(online);
        query.setStartTime(startTime);
        query.setEndTime(endTime);
        query.setServiceType(serviceType);
        query.setLocationProvince(locationProvince);
        query.setLocationCity(locationCity);
        query.setLocationRegion(locationRegion);
        query.setType(type);

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

    @GetMapping("/station/company/findByStationId")
    @ApiOperation(value = "根据服务站门店id查询服务站公司信息", notes = "根据服务站门店id查询服务站公司信息")
    @ApiImplicitParam(name = "stationId", value = "服务站门店id", required = true, dataType = "Long", paramType = "query")
    public Object getStationCompanyByStationId(@RequestParam("stationId") Integer stationId) {
        List<StationCompanyDTO> list = stationCompanyService.getStationCompanyByStationId(stationId);
        return ResponseEntity.ok(list);
    }


    /**
     * 站务系统-服务站--服务站公司-修改联系人
     *
     * @return
     */
    @PostMapping(value = "/station/company/editContactInfo")
    public void editStationCompanyContactInfo(@RequestBody StationCompanyDTO update) {
        stationCompanyService.updateContactInfo(update);
    }

    /**
     * 根据省市区查询售前售后门店集合
     *
     * @param province
     * @param city
     * @param region
     * @return
     */
    @GetMapping(value = "/station/companyList")
    public List<StationCompanyDTO> getStationCompanyListByPCR(@RequestParam(name = "province") String province,
                                                              @RequestParam(name = "city") String city,
                                                              @RequestParam(name = "region") String region) {
        return stationCompanyService.getStationCompanyListByPCR(province, city, region);
    }
}
