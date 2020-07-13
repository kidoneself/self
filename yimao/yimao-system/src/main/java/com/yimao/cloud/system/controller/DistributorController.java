package com.yimao.cloud.system.controller;

import com.yimao.cloud.base.constant.RabbitConstant;
import com.yimao.cloud.base.enums.ExportRecordStatus;
import com.yimao.cloud.base.exception.RemoteCallException;
import com.yimao.cloud.base.msg.CommResult;
import com.yimao.cloud.framework.aop.annotation.ExecutionTime;
import com.yimao.cloud.pojo.dto.system.ExportRecordDTO;
import com.yimao.cloud.pojo.dto.system.StationDistributorQueryDTO;
import com.yimao.cloud.pojo.dto.user.DistributorDTO;
import com.yimao.cloud.pojo.dto.user.DistributorQueryDTO;
import com.yimao.cloud.pojo.dto.user.TransferDistributorDTO;
import com.yimao.cloud.pojo.dto.user.UserChangeRecordListDTO;
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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 经销商信息
 *
 * @author hhf
 * @date 2019/1/24
 */
@RestController
@Slf4j
@Api(tags = "DistributorController")
public class DistributorController {

    @Resource
    private UserFeign userFeign;

    @Resource
    private ExportRecordService exportRecordService;

    @Resource
    private RabbitTemplate rabbitTemplate;

    /**
     * 创建经销商
     *
     * @param dto 经销商信息
     * @return ResponseEntity
     * @author hhf
     * @date 2019/1/24
     */
    @PostMapping(value = "/distributor")
    @ApiOperation(value = "创建经销商信息", notes = "创建经销商信息")
    @ApiImplicitParam(name = "dto", value = "经销商信息", required = true, dataType = "DistributorDTO", paramType = "body")
    public ResponseEntity save(@RequestBody DistributorDTO dto) {
        userFeign.saveDistributor(dto);
        return ResponseEntity.noContent().build();
    }

    /**
     * 更新经销商
     *
     * @param dto 经销商信息
     * @return ResponseEntity
     * @author hhf
     * @date 2019/1/24
     */
    @PutMapping(value = "/distributor")
    @ApiOperation(value = "更新经销商信息", notes = "更新经销商信息")
    @ApiImplicitParam(name = "dto", value = "经销商信息", required = true, dataType = "DistributorDTO", paramType = "body")
    public ResponseEntity update(@RequestBody DistributorDTO dto) {
        userFeign.updateDistributor(dto);
        return ResponseEntity.noContent().build();
    }

    /**
     * 分页查询经销商信息
     *
     * @param pageNum  分页页数
     * @param pageSize 分页大小
     * @param query    查询信息
     * @return org.springframework.http.ResponseEntity
     * @author hhf
     * @date 2019/1/24
     */
    @PostMapping(value = "/distributor/{pageNum}/{pageSize}")
    @ApiOperation(value = "分页查询经销商信息", notes = "分页查询经销商信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "分页页码", defaultValue = "1", required = true, dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "pageSize", value = "分页大小", defaultValue = "10", required = true, dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "query", value = "查询信息", required = true, dataType = "DistributorQueryDTO", paramType = "body")
    })
    public ResponseEntity<PageVO<DistributorDTO>> pageQueryDistributor(@PathVariable(value = "pageNum") Integer pageNum,
                                                                       @PathVariable(value = "pageSize") Integer pageSize,
                                                                       @RequestBody DistributorQueryDTO query) {

        PageVO<DistributorDTO> page = userFeign.pageQueryDistributor(pageNum, pageSize, query);
        if (page == null) {
            throw new RemoteCallException();
        }
        return ResponseEntity.ok(page);
    }

    /**
     * 删除经销商账号
     *
     * @param id 经销商ID
     * @return org.springframework.http.ResponseEntity
     * @author hhf
     * @date 2019/1/25
     */
    @DeleteMapping(value = "/distributor/{id}")
    @ApiOperation(value = "删除经销商账号", notes = "删除经销商账号")
    @ApiImplicitParam(name = "id", value = "经销商ID", required = true, dataType = "Long", paramType = "path")
    public ResponseEntity delete(@PathVariable("id") Integer id) {
        userFeign.deleteDistributor(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * 禁用/启用经销商账号
     *
     * @param id 经销商ID
     * @return org.springframework.http.ResponseEntity
     * @author hhf
     * @date 2019/1/25
     */
    @PatchMapping(value = "/distributor/{id}")
    @ApiOperation(value = "禁用/启用经销商账号", notes = "禁用/启用经销商账号")
    @ApiImplicitParam(name = "id", value = "经销商ID", required = true, dataType = "Long", paramType = "path")
    public ResponseEntity forbidden(@PathVariable("id") Integer id) {
        userFeign.forbidden(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * 禁止/启用经销商下单
     *
     * @param id 经销商ID
     * @return org.springframework.http.ResponseEntity
     * @author hhf
     * @date 2019/1/25
     */
    @PatchMapping(value = "/distributor/order/{id}")
    @ApiOperation(value = "禁止/启用经销商下单", notes = "禁止/启用经销商下单")
    @ApiImplicitParam(name = "id", value = "经销商ID", required = true, dataType = "Long", paramType = "path")
    public ResponseEntity forbiddenOrder(@PathVariable("id") Integer id) {
        userFeign.forbiddenOrder(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * 根据经销商ID查询经销商详情
     *
     * @param id 经销商Id
     * @return org.springframework.http.ResponseEntity
     * @author hhf
     * @date 2019/1/25
     */
    @ExecutionTime
    @GetMapping(value = "/distributor/{id}/expansion")
    @ApiOperation(value = "根据经销商ID查询经销商详情", notes = "根据经销商ID查询经销商详情")
    @ApiImplicitParam(name = "id", value = "经销商ID", required = true, dataType = "Long", paramType = "path")
    public DistributorDTO getDistributorById(@PathVariable("id") Integer id) {
        return userFeign.getExpansionInfoById(id);
    }

    /**
     * 经销商信息转让
     *
     * @param oriDistributorId 经销商Id
     * @param dto              经销商信息
     * @return org.springframework.http.ResponseEntity
     * @author hhf
     * @date 2019/1/25
     */
    @PostMapping(value = "/distributor/transfer")
    @ApiOperation(value = "经销商信息转让", notes = "经销商信息转让")
    @ApiImplicitParam(name = "transferDTO", value = "经销商信息", required = true, dataType = "TransferDistributorDTO", paramType = "body")
    public ResponseEntity transferDistributor(@RequestParam("oriDistributorId") Integer oriDistributorId,
                                              @RequestBody TransferDistributorDTO transferDTO) {

        userFeign.transferDistributor(oriDistributorId, transferDTO);
        return ResponseEntity.noContent().build();
    }

    /**
     * 根据经销商账号查询经销商是否存在
     *
     * @param account 经销商账号
     * @return org.springframework.http.ResponseEntity
     * @author hhf
     * @date 2019/1/25
     */
    @ExecutionTime
    @GetMapping(value = "/distributor/verify/{account}")
    @ApiOperation(value = "根据经销商账号查询经销商是否存在", notes = "根据经销商账号查询经销商是否存在")
    @ApiImplicitParam(name = "account", value = "经销商账号", required = true, dataType = "String", paramType = "path")
    public ResponseEntity existDistributorAccount(@PathVariable("account") String account) {
        Boolean existDistributorAccount = userFeign.existDistributorAccount(account);
        return ResponseEntity.ok(existDistributorAccount);
    }

    /**
     * 验证代理排名的值是否存在
     *
     * @param agentLevel 代理类型1-省代；2-市代；4-区代
     * @param ranking    排名
     * @return org.springframework.http.ResponseEntity
     * @author hhf
     * @date 2019/4/3
     */
    @GetMapping(value = "/distributor/rank")
    @ApiOperation(value = "验证代理排名的值是否存在", notes = "验证代理排名的值是否存在")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "agentLevel", value = "代理类型1-省代；2-市代；4-区代", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "ranking", value = "排名", dataType = "Long", paramType = "query")
    })
    public ResponseEntity checkAgentRanking(@RequestParam("agentLevel") Integer agentLevel, @RequestParam("ranking") Integer ranking) {
        Boolean agentRanking = userFeign.checkAgentRanking(agentLevel, ranking);
        return ResponseEntity.ok(agentRanking);
    }

    /**
     * 根据省市区获取推荐人信息
     *
     * @param province 省
     * @param city     市
     * @param region   区
     * @author hhf
     * @date 2019/4/3
     */
    @GetMapping(value = "/distributor/recommend/{province}/{city}/{region}")
    @ApiOperation(value = "根据省市区获取推荐人信息", notes = "根据省市区获取推荐人信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "province", value = "省", dataType = "String", paramType = "path"),
            @ApiImplicitParam(name = "city", value = "市", dataType = "String", paramType = "path"),
            @ApiImplicitParam(name = "region", value = "区", dataType = "String", paramType = "path")
    })
    public ResponseEntity<List<DistributorDTO>> getRecommendByAddress(@PathVariable(value = "province") String province,
                                                                      @PathVariable(value = "city") String city,
                                                                      @PathVariable(value = "region") String region) {
        List<DistributorDTO> dto = userFeign.getRecommendByAddress(province, city, region);
        return ResponseEntity.ok(dto);
    }


    /**
     * 经销商导出
     *
     * @param query 查询条件
     */
    @PostMapping(value = "/distributor/export")
    @ApiOperation(value = "经销商导出")
    @ApiImplicitParam(name = "query", value = "查询信息", required = true, dataType = "DistributorQueryDTO", paramType = "body")
    public Object distributorExport(@RequestBody DistributorQueryDTO query) {
        Boolean subAccount = query.getSubAccount();
        String title;
        if (!subAccount) {
            title = "经销商代理商";
        } else {
            title = "经销商子账号";
        }
        //保存导出记录，如果短时间操作多次，会提示操作过于频繁
        String url = "/distributor/export";
        ExportRecordDTO record = exportRecordService.save(url, title);

        //导出记录添加成功，并且状态为"等待导出"，才入列
        if (record.getStatus() == ExportRecordStatus.WAITING.value) {
            //异步队列执行导出
            Map<String, Object> map = new HashMap<>();
            map.put("exportRecordDTO", record);
            map.put("query", query);
            rabbitTemplate.convertAndSend(RabbitConstant.EXPORT_ACTION_USER, map);
        }
        return CommResult.ok(record.getId());
    	/*Boolean subAccount = query.getSubAccount();
        Boolean boo;
        List<DistributorExportDTO> list = userFeign.distributorExport(query);
        if (CollectionUtil.isEmpty(list)) {
            throw new NotFoundException("没有数据可以导出");
        }
 
        if(!subAccount){
            String[] titles = {"经销商ID","经销商账号","经销商姓名","性别","身份证号","联系方式","角色类型","代理商级别","经销商类型","经销商省","经销商市","经销商区","企业公司名称","子账号个数","是否为站长",
                    "是否为发起人","是否主账号","来源端","来源方式","总配额","总置换金额","水机剩余配额","剩余置换金额","支付总金额","智慧助理","智慧助理所在区域","推荐人姓名",
                    "经销商账号（推荐人）","推荐人身份证号","推荐人所在区域","推荐人智慧助理","推荐人智慧助理所在地","注册经销商时间","免费体验首次升级时间","升级支付时间(微创)",
                    "升级支付时间(个人)","升级支付时间(企业)","升级支付金额(微创)","升级支付金额(个人)","升级支付金额(企业)","续费次数","是否溢价","省代时间","市代时间","区代时间","是否是省发起人","是否是市发起人",
                    "是否是区发起人","是否禁用","是否禁止下单","创建时间"};
            String[] beanProperties = {"userId", "userName", "realName", "sex", "idCard", "phone", "type", "agentLevel", "roleName", "province","city","region","companyName","count","stationMaster",
                    "sponsor", "mainAccount","terminal","sourceType","quota","replacementAmount","remainingQuota","remainingReplacementAmount","amount","userAssistant","userAssistantArea","recommendName",
                    "recommendAccount","recommendIdCard","recommendArea","recommendAssistant","recommendAssistantArea","createTime","firstUpdateTime","payTimeforMin",
                    "payTimeforPerson","payTimeforEnterprise","payAmountforMin","payAmountforPerson","payAmountforEnterprise","renewalCount","premium","provinceTime","cityTime","regionTime","isProvinceSponsor",
                    "isCitySponsor","isRegionSponsor","forbidden","forbiddenOrder","createTime"};
            String header = DateUtil.getCurrentTimeStr(DateUtil.DATEFORMAT_01) + "经销商代理商";
            boo = ExcelUtil.exportSXSSF(list, header, titles.length - 1, titles, beanProperties, response);
        }else{
            String[] titles = {"经销商ID","经销商账号","经销商姓名","性别","身份证号","联系方式","经销商主账号","主账号姓名","经销商省","经销商市","经销商区",
                    "企业公司名称","来源端","来源方式","是否禁用","是否禁止下单","创建时间"};
            String[] beanProperties = {"userId", "userName", "realName", "sex", "idCard", "phone", "mainUserName", "mainName", "province","city","region",
                    "companyName","terminal","sourceType","forbidden","forbiddenOrder","createTime"};
            String header = DateUtil.getCurrentTimeStr(DateUtil.DATEFORMAT_01) + "经销商子账号";
            boo = ExcelUtil.exportSXSSF(list, header, titles.length - 1, titles, beanProperties, response);
        }

        if (!boo) {
            throw new YimaoException("导出失败");
        }*/
    }

    @PostMapping(value = "/distributor/station")
    @ApiOperation(value = "站长模糊搜索", notes = "站长模糊搜索")
    @ApiImplicitParam(name = "query", value = "参数", dataType = "StationDistributorQueryDTO", paramType = "body")
    public List<DistributorDTO> getDistributorByParams(@RequestBody StationDistributorQueryDTO query) {
        return userFeign.getDistributorByParams(query);
    }

    /**
     * 根据用户ID获取用户变更纪录
     *
     * @param userId
     * @return
     */
    @GetMapping(value = "/distributor/changeInfo")
    @ApiOperation(value = "根据用户ID获取用户变更记录")
    @ApiImplicitParam(name = "userId", value = "经销商ID", required = true, dataType = "Long", paramType = "query")
    public UserChangeRecordListDTO getChangeInfoByUserId(@RequestParam Integer userId) {
        return userFeign.getChangeInfoByUserId(userId);
    }

}
