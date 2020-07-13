package com.yimao.cloud.system.controller.waterdevice;

import com.yimao.cloud.base.constant.RabbitConstant;
import com.yimao.cloud.base.enums.ExportRecordStatus;
import com.yimao.cloud.base.msg.CommResult;
import com.yimao.cloud.pojo.dto.system.ExportRecordDTO;
import com.yimao.cloud.pojo.dto.user.CustomerContidionDTO;
import com.yimao.cloud.pojo.dto.user.DistributorDTO;
import com.yimao.cloud.pojo.dto.user.WaterDeviceUserDTO;
import com.yimao.cloud.pojo.vo.PageVO;
import com.yimao.cloud.system.feign.UserFeign;
import com.yimao.cloud.system.service.ExportRecordService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/***
 * @description 工单控制层
 * @author zhilin.he
 * @date 2019/4/29 16:22
 **/
@RestController
@Slf4j
@Api(tags = "WaterDeviceUserController")
public class WaterDeviceUserController {

    @Resource
    private UserFeign userFeign;

    @Resource
    private RabbitTemplate rabbitTemplate;

    @Resource
    private ExportRecordService exportRecordService;

    /**
     * @Author ycl
     * @Description 业务管理系统-水机订单客户列表
     * @Date 15:42 2019/8/15
     * @Param
     **/
    @PostMapping("/customers/{pageNum}/{pageSize}")
    @ApiOperation(value = "水机订单客户列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "页码", required = true, defaultValue = "1", dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "pageSize", value = "页数", required = true, defaultValue = "10", dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "query", value = "条件", defaultValue = "10", dataType = "CustomerContidionDTO", paramType = "body")
    })
    public ResponseEntity<PageVO<WaterDeviceUserDTO>> pageQueryCustomer(@PathVariable(value = "pageNum") Integer pageNum,
                                                                        @PathVariable(value = "pageSize") Integer pageSize,
                                                                        @RequestBody(required = false) CustomerContidionDTO query) {
        return ResponseEntity.ok(userFeign.pageQueryCustomer(pageNum, pageSize, query));
    }


    /**
     * @Author ycl
     * @Description 业务管理系统-水机订单客户信息
     * @Date 10:55 2019/8/16
     * @Param
     **/
    @GetMapping("/customers/{id}")
    @ApiOperation(value = "获取客户信息")
    @ApiImplicitParam(name = "id", value = "客户ID", dataType = "Long", paramType = "path", required = true)
    public Object getDeviceUserDTOInfo(@PathVariable(value = "id") Integer id) {
        WaterDeviceUserDTO deviceUser = userFeign.getDeviceUserDTOInfo(id);
        if (deviceUser != null) {
            if (deviceUser.getDistributorId() != null) {
                DistributorDTO distributor = userFeign.getDistributorById(deviceUser.getDistributorId());
                if (distributor != null) {
                    deviceUser.setRoleName(distributor.getRoleName());
                    deviceUser.setDistributorPhone(distributor.getPhone());
                    deviceUser.setDistributorIdCard(distributor.getIdCard());
                    deviceUser.setDistributorProvince(distributor.getProvince());
                    deviceUser.setDistributorCity(distributor.getCity());
                    deviceUser.setDistributorRegion(distributor.getRegion());
                    deviceUser.setDistributorAddress(distributor.getAddress());
                    deviceUser.setDistributorAccount(distributor.getUserName());
                    deviceUser.setDistributorName(distributor.getRealName());
                }
            }
        }
        return deviceUser;
    }


    /**
     * @Author ycl
     * @Description 水机订单客户编辑
     * @Date 11:35 2019/8/16
     * @Param
     **/
    @PatchMapping(value = "/customers/deviceUser")
    @ApiOperation(value = "水机订单客户编辑")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "deviceUserDTO", value = "客户信息", dataType = "WaterDeviceUserDTO", paramType = "body")
    })
    public ResponseEntity updateDeviceUserInfo(@RequestBody WaterDeviceUserDTO deviceUserDTO) {
        userFeign.updateDeviceUserInfo(deviceUserDTO);
        return ResponseEntity.noContent().build();
    }


    /**
     * @Author ycl
     * @Description 客户列表导出
     * @Date 16:32 2019/8/20
     * @Param
     **/
    @PostMapping(value = "/customers/deviceUser/export")
    @ApiOperation(value = "客户列表导出")
    @ApiImplicitParam(name = "query", value = "查询信息", dataType = "CustomerContidionDTO", paramType = "body")
    public Object customersExport(@RequestBody(required = false) CustomerContidionDTO query) {
        //保存导出记录，如果短时间操作多次，会提示操作过于频繁
        String url = "/customers/deviceUser/export";
        ExportRecordDTO record = exportRecordService.save(url, "客户列表");

        //导出记录添加成功，并且状态为"等待导出"，才入列
        if (record.getStatus() == ExportRecordStatus.WAITING.value) {
            //异步队列执行导出
            Map<String, Object> map = new HashMap<>();
            map.put("exportRecordDTO", record);
            map.put("query", query);
            rabbitTemplate.convertAndSend(RabbitConstant.EXPORT_ACTION_USER, map);
        }
        return CommResult.ok(record.getId());

        /*List<WaterDeviceUserExport> deviceUserDTOList = userFeign.customersExportList(query);
        if (CollectionUtil.isEmpty(deviceUserDTOList)) {
            throw new NotFoundException("没有数据可以导出");
        }

        String header = DateUtil.getCurrentTimeStr(DateUtil.DATEFORMAT_01);
        String[] titles = new String[]{
                "联系人姓名", "性别", "手机号", "身份证号", "来源端", "创建时间", "客户类型", "公司行业", "场景标签",
                "日均服务人数", "公司名称", "省", "市", "区", "地址", "经销商ID", "经销商账号", "经销商姓名",
                "经销商类型", "经销商手机号", "身份证号", "经销商所属省", "经销商所属市", "经销商所属区", "经销商地址"
        };

        String[] beanPropertys = new String[]{
                "realName", "sex", "phone", "idCard", "originTerminal", "createTime", "type",
                "companyIndustry", "sceneTag", "serviceNum", "companyName", "province", "city", "region",
                "address", "distributorId", "distributorAccount", "distributorName", "roleName", "distributorPhone",
                "distributorIdCard", "distributorProvince", "distributorCity", "distributorRegion", "distributorAddress"
        };

        header = header + "业务管理系统-客户列表";
        boolean boo = ExcelUtil.exportSXSSF(deviceUserDTOList, header, titles.length - 1, titles, beanPropertys, response);
        if (!boo) {
            throw new YimaoException("导出失败");
        }*/
    }
}
