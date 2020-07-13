package com.yimao.cloud.out.feign;

import com.yimao.cloud.base.constant.Constant;
import com.yimao.cloud.pojo.dto.water.*;
import com.yimao.cloud.pojo.vo.PageVO;
import com.yimao.cloud.pojo.vo.water.TdsUploadRecordVO;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Date;
import java.util.List;

/**
 * 描述：水机微服务的接口列表
 *
 * @author Zhang Bo
 * @date 2019/3/9.
 */
@FeignClient(name = Constant.MICROSERVICE_WATER)
public interface WaterFeign {
    /**
     * @description   安装工app-设备管理-查询
     * @author Liu Yi
     * @date 2019/12/13 9:31
     * @return java.lang.Object
     */
    @GetMapping(value = "/waterdevice/engineerApp/{pageNum}/{pageSize}")
    @ApiOperation(value = "安装工app-设备管理-查询", notes = "安装工app-设备管理-查询")
    PageVO<WaterDeviceDTO> pageDeviceForEngineerApp(@PathVariable(value = "pageNum") Integer pageNum,
                                           @PathVariable(value = "pageSize") Integer pageSize,
                                           @RequestParam(value = "engineerId", required = false) Integer engineerId,
                                           @RequestParam(value = "search", required = false) String search);

    @RequestMapping(value = "/waterdevice/{id}", method = RequestMethod.GET)
    WaterDeviceDTO getWaterDeviceById(@PathVariable(value = "id") Integer id);

    /**
     * 根据水机设备SN获取设备信息
     *
     * @param sn 设备sn编码
     */
    @GetMapping(value = "/waterdevice")
    WaterDeviceDTO getWaterDeviceBySnCode(@RequestParam("sn") String sn);

    /**
     * 根据TDS值上传记录ID查详情
     *
     * @param id TDS值上传记录ID
     */
    @GetMapping(value = "/tds/record/{id}/detail")
    TdsUploadRecordVO getTdsUploadRecorddetail(@PathVariable("id") Integer id);

    /**
     * 根据水机设备iccid获取设备信息
     *
     * @param iccid 设备sim卡号
     */
    @GetMapping(value = "/waterdevice/iccid")
    WaterDeviceDTO getWaterDeviceByIccid(@RequestParam("iccid") String iccid);

    @GetMapping(value = "/waterdevice/dynamic/cipher/record")
    WaterDeviceDynamicCipherRecordDTO getDynamicCipherRecordBySnCode(@RequestParam("sn") String sn);

    @GetMapping(value = "/waterdevice/dynamic/cipher/config")
    WaterDeviceDynamicCipherConfigDTO getDynamicCipherConfig();

    @PostMapping(value = "/waterdevice/dynamic/cipher/record", consumes = MediaType.APPLICATION_JSON_VALUE)
    WaterDeviceDynamicCipherRecordDTO createDynamicCipherRecord(@RequestBody WaterDeviceDynamicCipherRecordDTO record);

    @GetMapping(value = "/waterdevice/dynamic/cipher/record/{pageNum}/{pageSize}")
    List<WaterDeviceDynamicCipherRecordDTO> pageDynamicCipherRecord(@PathVariable(value = "pageNum") Integer pageNum,
                                                                    @PathVariable(value = "pageSize") Integer pageSize,
                                                                    @RequestParam("engineerId") Integer engineerId);

    @PatchMapping(value = "/waterdevice/dynamic/cipher/record", consumes = MediaType.APPLICATION_JSON_VALUE)
    void updateDynamicCipherRecord(@RequestBody WaterDeviceDynamicCipherRecordDTO update);

    @PatchMapping(value = "/waterdevice/dynamic/cipher/record/inValid")
    void setDeviceAllDynamicCipherInValid(@RequestParam("sn") String sn);


    /**
     * 检查sn是否存在
     *
     * @param id 设备ID
     */
    @GetMapping(value = "/waterdevice/checkSnExists")
    Boolean checkSnExists(@RequestParam("id") Integer id, @RequestParam("sncode") String sncode);

    /**
     * 检查sim是否存在
     *
     * @param id 设备ID
     */
    @GetMapping(value = "/waterdevice/checkIccidExists")
    Boolean checkIccidExists(@RequestParam("id") Integer id, @RequestParam("iccid") String iccid);

    /**
     * 更新设备信息
     *
     * @param waterDeviceDTO
     */
    @PatchMapping(value = "/waterdevice", consumes = MediaType.APPLICATION_JSON_VALUE)
    void updateDevice(@RequestBody WaterDeviceDTO waterDeviceDTO);

    /**
     * 水机设备列表-更换设备
     *
     * @param deviceId 设备ID
     */
    @PatchMapping(value = "/waterdevice/replace")
    void replaceWaterDevice(@RequestParam(value = "id", required = false) Integer id,
                            @RequestParam("oldSn") String oldSn,
                            @RequestParam("newSn") String newSn,
                            @RequestParam("oldIccid") String oldIccid,
                            @RequestParam("newIccid") String newIccid,
                            @RequestParam("oldBatchCode") String oldBatchCode,
                            @RequestParam("newBatchCode") String newBatchCode);

    /***
     * 功能描述:根据SN和时间查询水机滤芯更换记录
     *
     * @param: [deviceSncode, createTime]
     * @auther: liu yi
     * @date: 2019/5/18 10:10
     * @return: java.lang.Object
     */
    @GetMapping(value = "/waterdevice/filterChangeRecord/sncode")
    @ApiOperation(value = "根据SN和时间查询水机滤芯更换记录", notes = "根据SN和时间查询水机滤芯更换记录")
    List<WaterDeviceFilterChangeRecordDTO> getFilterChangeRecordBySnCode(@RequestParam(value = "deviceSncode") String deviceSncode,
                                                                         @RequestParam(value = "createTime") Date createTime,
                                                                         @RequestParam(value = "source",required = false)  Integer source);

    /**
     * 创建水机设备信息
     *
     * @param dto 水机设备信息
     */
    @PostMapping(value = "/waterdevice", consumes = MediaType.APPLICATION_JSON_VALUE)
    WaterDeviceDTO createWaterDevice(@RequestBody WaterDeviceDTO dto);

    /**
     * @description   根据sn查询水机摆放位置更换记录
     * @author Liu Yi
     * @date 2019/11/14 16:50
     * @param
     * @return com.yimao.cloud.pojo.dto.water.WaterDevicePlaceChangeRecordDTO
     */
    @GetMapping(value = "/placechangerecord/sn")
    WaterDevicePlaceChangeRecordDTO getWaterDevicePlaceChangeRecordBySn(@RequestParam("sn") String sn);

    /**故障原因----------------------start----------------------------*/
    /**
     * @param workCode
     * @return
     * @description 查询维修工单故障原因
     * @author Liu Yi
     */
    @GetMapping(value = "/waterdevice/waterDeviceFailurePhenomenon")
    List<WaterDeviceFailurePhenomenonDTO> getWaterDeviceFailurePhenomenonListByWorkCode(@RequestParam("workCode") String workCode);

    /**
     * @param workCode
     * @return
     * @description 新增维修工单故障原因
     * @author Liu Yi
     */
    @PostMapping(value = "/waterdevice/waterDeviceFailurePhenomenon", consumes = MediaType.APPLICATION_JSON_VALUE)
    void createWaterDeviceFailurePhenomenon(@RequestParam("workCode") String workCode, @RequestBody List<WaterDeviceFailurePhenomenonDTO> list);

    /**
     * @param workCode
     * @return
     * @description 删除维修工单故障原因
     * @author Liu Yi
     */
    @DeleteMapping(value = "/waterdevice/waterDeviceFailurePhenomenon")
    void deleteWaterDeviceFailurePhenomenonByWorkCode(@RequestParam("workCode") String workCode, @RequestParam("workOrderType") String workOrderType);

    /**故障原因----------------------end----------------------------*/

    /**
     * @param workCode
     * @param workCode
     * @return
     * @description 查询维修工单耗材列表
     * @author Liu Yi
     */
    @GetMapping(value = "/waterdevice/workOrderMateriel/workCode")
    List<WaterDeviceWorkOrderMaterielDTO> getWaterDeviceWorkOrderMaterielListByWorkCode(@RequestParam(value = "workCode") String workCode,
                                                                                        @RequestParam(value = "workOrderIndex") String workOrderIndex);

    /**
     * @param workCode
     * @param workOrderIndex
     * @return
     * @description 根据workCode查询删除工单耗材信息
     * @author Liu Yi
     */
    @DeleteMapping(value = "/waterdevice/workOrderMateriel/workCode")
    void deleteWorkOrderMaterielByWorkCode(@RequestParam(value = "workCode") String workCode, @RequestParam(value = "workOrderIndex") String workOrderIndex);

    @PostMapping(value = "/waterdevice/workOrderMateriel/batch", consumes = MediaType.APPLICATION_JSON_VALUE)
    void batchCreateWaterDeviceWorkOrderMateriel(@RequestBody @ApiParam(value = "新增维修工单耗材list") List<WaterDeviceWorkOrderMaterielDTO> waterDeviceWorkOrderMaterielDTOs);

    /**工单耗材信息------------------end--------------------------*/

    /**工单解决措施------------------start---------------------------------*/
    /**
     * @param workCode
     * @return
     * @description 根据工单号删除解决措施
     * @author Liu Yi
     */
    @DeleteMapping(value = "/waterdevice/repairFactFaultDescribeInfo")
    void deleteFactFaultDescribeInfoByWorkCode(@RequestParam(value = "workCode") String workCode);

    /**
     * @return
     * @description 查询工单耗材信息列表
     * @author Liu Yi
     */
    @GetMapping(value = "/waterdevice/repairFactFaultDescribeInfo/list")
    List<WaterDeviceRepairFactFaultDescribeInfoDTO> findWaterDeviceRepairFactFaultDescribeInfoList(@RequestParam(value = "id", required = false) Integer id,
                                                                                                   @RequestParam(value = "workCode", required = false) String workCode);

    @PostMapping(value = "/waterdevice/repairFactFaultDescribeInfo", consumes = MediaType.APPLICATION_JSON_VALUE)
    void createWaterDeviceRepairFactFaultDescribeInfo(@RequestBody WaterDeviceRepairFactFaultDescribeInfoDTO dto);

    @PatchMapping(value = "/waterdevice/{id}/activatingsimcard")
    void activatingSimCard(@PathVariable("id") Integer id, @RequestParam(value = "iccid", required = false) String iccid);

    /**
     * 创建水机耗材
     *
     * @param dto 水机耗材
     */
    @PostMapping(value = "/consumable", consumes = MediaType.APPLICATION_JSON_VALUE)
    void saveWaterDeviceConsumable(@RequestBody WaterDeviceConsumableDTO dto);

    /**
     * 修改水机耗材
     *
     * @param dto 水机耗材
     */
    @PutMapping(value = "/consumable", consumes = MediaType.APPLICATION_JSON_VALUE)
    void updateWaterDeviceConsumable(@RequestBody WaterDeviceConsumableDTO dto);

    /**
     * 根据百得耗材id查询耗材
     *
     * @param id 百得耗材id
     */
    @GetMapping(value = "/consumable/{oldId}/oldId")
    WaterDeviceConsumableDTO getConsumableByOldId(@PathVariable(value = "oldId") String oldId);


    @PatchMapping(value = "/waterdevice/{id}/deactivatedsimcard")
    void deactivatedSimCard(@PathVariable("id") Integer id);

    /**
     * 根据水机设备ID删除设备
     *
     * @param id 设备ID
     */
    @DeleteMapping(value = "/waterdevice/{id}")
    void deleteWaterDevice(@PathVariable(value = "id") Integer id);

}
