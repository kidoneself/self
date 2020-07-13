package com.yimao.cloud.user.controller;

import com.yimao.cloud.base.enums.SexType;
import com.yimao.cloud.base.exception.BadRequestException;
import com.yimao.cloud.base.exception.DialogException;
import com.yimao.cloud.base.msg.CommResult;
import com.yimao.cloud.base.utils.StringUtil;
import com.yimao.cloud.framework.aop.annotation.ExecutionTime;
import com.yimao.cloud.pojo.dto.station.StationScheduleDTO;
import com.yimao.cloud.pojo.dto.system.StationDistributorQueryDTO;
import com.yimao.cloud.pojo.dto.user.*;
import com.yimao.cloud.pojo.query.station.DistributorQuery;
import com.yimao.cloud.pojo.vo.PageVO;
import com.yimao.cloud.pojo.vo.station.DistributorVO;
import com.yimao.cloud.user.mapper.DistributorMapper;
import com.yimao.cloud.user.mapper.UserMapper;
import com.yimao.cloud.user.po.Distributor;
import com.yimao.cloud.user.service.DistributorService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author hhf
 * @description 经销商信息
 * @date 2018/12/17
 */
@Slf4j
@RestController
@Api(tags = "DistributorController")
public class DistributorController {

    @Resource
    private DistributorService distributorService;
    @Resource
    private DistributorMapper distributorMapper;
    @Resource
    private UserMapper userMapper;

    /**
     * 创建经销商
     *
     * @param dto 经销商信息
     */
    @PostMapping(value = "/distributor")
    @ApiOperation(value = "创建经销商信息")
    @ApiImplicitParam(name = "dto", value = "经销商信息", required = true, dataType = "DistributorDTO", paramType = "body")
    public void save(@RequestBody DistributorDTO dto) {

        distributorService.save(dto);
    }

    /**
     * 删除经销商账号
     *
     * @param id 经销商ID
     */
    @DeleteMapping(value = "/distributor/{id}")
    @ApiOperation(value = "删除经销商账号")
    @ApiImplicitParam(name = "id", value = "经销商ID", required = true, dataType = "Long", paramType = "path")
    public void delete(@PathVariable Integer id) {
        distributorService.delete(id);
    }

    /**
     * 修改经销商信息
     *
     * @param dto 经销商信息
     */
    @PutMapping(value = "/distributor")
    @ApiOperation(value = "修改经销商信息")
    @ApiImplicitParam(name = "dto", value = "经销商信息", required = true, dataType = "DistributorDTO", paramType = "body")
    public void update(@RequestBody DistributorDTO dto) {
        distributorService.update(dto);
    }

    /**
     * 分页查询经销商信息
     *
     * @param pageNum  分页页数
     * @param pageSize 分页大小
     * @param query    查询条件
     */
    @PostMapping(value = "/distributor/{pageNum}/{pageSize}")
    @ApiOperation(value = "分页查询经销商信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "分页页码", defaultValue = "1", required = true, dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "pageSize", value = "分页大小", defaultValue = "10", required = true, dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "query", value = "查询信息", required = true, dataType = "DistributorQueryDTO", paramType = "body")
    })
    public PageVO<DistributorDTO> pageQueryDistributor(@PathVariable Integer pageNum, @PathVariable Integer pageSize,
                                                       @RequestBody DistributorQueryDTO query) {
        return distributorService.pageQueryDistributor(query, pageNum, pageSize);
    }

    /**
     * 用户--经销商代理商分页查询（站务系统调用）
     *
     * @param pageNum  分页页数
     * @param pageSize 分页大小
     * @param query    查询条件
     */
    @PostMapping(value = "/distributor/station/{pageNum}/{pageSize}")
    public Object stationPageQueryDistributor(@PathVariable Integer pageNum,
                                              @PathVariable Integer pageSize,
                                              @RequestBody DistributorQuery query) {

        return ResponseEntity.ok(distributorService.pageQueryDistributorToStation(query, pageNum, pageSize));
    }

    /**
     * 禁用/启用经销商账号
     *
     * @param id 经销商ID
     */
    @PatchMapping(value = "/distributor/{id}")
    @ApiOperation(value = "禁用/启用经销商账号")
    @ApiImplicitParam(name = "id", value = "经销商ID", required = true, dataType = "Long", paramType = "path")
    public void forbidden(@PathVariable Integer id) {
        distributorService.forbidden(id);
    }

    /**
     * 禁止/启用经销商下单
     *
     * @param id 经销商ID
     */
    @PatchMapping(value = "/distributor/order/{id}")
    @ApiOperation(value = "禁止/启用经销商下单")
    @ApiImplicitParam(name = "id", value = "经销商ID", required = true, dataType = "Long", paramType = "path")
    public void forbiddenOrder(@PathVariable Integer id) {
        distributorService.forbiddenOrder(id);
    }

    /**
     * 根据经销商ID查询经销商信息（只返回基本信息）
     *
     * @param id 经销商ID
     */
    @GetMapping(value = "/distributor/{id}")
    @ApiOperation(value = "根据经销商ID查询经销商（单表信息）")
    @ApiImplicitParam(name = "id", value = "经销商ID", required = true, dataType = "Long", paramType = "path")
    public DistributorDTO getBasicInfoById(@PathVariable Integer id) {
        return distributorService.getBasicInfoById(id);
    }

    /**
     * 根据经销商ID查询经销商信息（消息推送时只需获取很少的几个字段）
     *
     * @param id 经销商ID
     */
    @GetMapping(value = "/distributor/{id}/formsgpushinfo")
    @ApiOperation(value = "根据经销商ID查询经销商（消息推送时只需获取很少的几个字段）")
    @ApiImplicitParam(name = "id", value = "经销商ID", required = true, dataType = "Long", paramType = "path")
    public DistributorDTO getBasicInfoByIdForMsgPushInfo(@PathVariable Integer id) {
        return distributorService.getBasicInfoByIdForMsgPushInfo(id);
    }

    /**
     * 根据经销商ID查询经销商信息（返回基本信息+扩展信息）
     *
     * @param id 经销商ID
     */
    @GetMapping(value = "/distributor/{id}/expansion")
    @ApiOperation(value = "根据经销商ID查询经销商信息（返回基本信息+扩展信息）")
    @ApiImplicitParam(name = "id", value = "经销商ID", required = true, dataType = "Long", paramType = "path")
    public DistributorDTO getExpansionInfoById(@PathVariable("id") Integer id) {
        log.info("查询用户详情用户id=" + id);
        return distributorService.getDistirbutorInfoById(id);
    }

    /**
     * 用户--经销商/代理商--根据经销商ID查询详情信息（站务系统调用）
     *
     * @param id 经销商ID
     */
    @GetMapping(value = "/distributor/station/{id}/expansion")
    public DistributorVO getExpansionInfoByIdToStation(@PathVariable("id") Integer id) {
        log.info("查询用户详情用户id=" + id);
        return distributorService.getDistributorInfoByIdToStation(id);
    }

    /**
     * 经销商转让
     *
     * @param id          经销商Id
     * @param transferDTO 变更受理人信息
     */
    @PostMapping(value = "/distributor/transfer")
    @ApiOperation(value = "经销商转让")
    @ApiImplicitParam(name = "transferDTO", value = "经销商信息", required = true, dataType = "TransferDistributorDTO", paramType = "body")
    public void transferDistributor(@RequestParam("oriDistributorId") Integer id, @RequestBody TransferDistributorDTO transferDTO) {
//        Integer createAccountType = transferDTO.getCreateAccountType();
//        if (createAccountType == null || CreateDistributorAccountType.find(createAccountType) == null) {
//            throw new BadRequestException("请选择系统自动创建或手动创建经销商账号。");
//        }
        if (StringUtil.isBlank(transferDTO.getRealName())) {
            throw new BadRequestException("姓名不能为空。");
        }
        if (transferDTO.getSex() == null) {
            throw new BadRequestException("性别不能为空。");
        }
        if (SexType.find(transferDTO.getSex()) == null) {
            throw new BadRequestException("性别填写错误。");
        }
        if (StringUtil.isBlank(transferDTO.getPhone())) {
            throw new BadRequestException("手机号不能为空。");
        }
        if (StringUtil.isBlank(transferDTO.getIdCard())) {
            throw new BadRequestException("身份证号码不能为空。");
        }

        if (StringUtil.isBlank(transferDTO.getUserName())) {
            throw new BadRequestException("账号不能为空。");
        }
        distributorService.transferDistributor(id, transferDTO);
    }

    /**
     * 根据经销商账号查询经销商是否存在
     *
     * @param account 经销商账号
     * @return org.springframework.http.ResponseEntity
     * @author hhf
     * @date 2018/1/8
     */
    @ExecutionTime
    @GetMapping(value = "/distributor/verify/{account}")
    @ApiOperation(value = "根据经销商账号查询经销商是否存在", notes = "根据经销商账号查询经销商是否存在")
    @ApiImplicitParam(name = "account", value = "经销商账号", required = true, dataType = "String", paramType = "path")
    public ResponseEntity existDistributorAccount(@PathVariable("account") String account) {
        Boolean existDistributorAccount = distributorService.existDistributorAccount(account);
        return ResponseEntity.ok(existDistributorAccount);
    }

    /**
     * 根据经销商账号统计经销商信息
     *
     * @param id 经销商Id
     * @return org.springframework.http.ResponseEntity
     * @author hhf
     * @date 2019/1/21
     */
    @GetMapping(value = "/distributor/statistics/{id}")
    @ApiOperation(value = "根据经销商账号统计经销商信息", notes = "根据经销商账号统计经销商信息")
    @ApiImplicitParam(name = "id", value = "经销商账号", required = true, dataType = "Long", paramType = "path")
    public ResponseEntity countDistributorById(@PathVariable("id") Integer id) {
        DistributorCountDTO dto = distributorService.countDistributorById(id);
        return ResponseEntity.ok(dto);
    }


//    /**
//     * @Description:翼猫榜-经销商排名
//     * @author ycl
//     * @param distId
//     * @Return: org.springframework.http.ResponseEntity
//     * @Create: 2019/1/23 11:55
//    */
//    @GetMapping(value = "/distributor/biling/{distId}")
//    @ApiOperation(value = "翼猫榜经销商排名", notes = "翼猫榜经销商排名")
//    @ApiImplicitParam(name = "distId", value = "经销商ID", required = true, dataType = "Long", paramType = "path")
//    public ResponseEntity getDistributorBiling(Integer distId){
//
//        return null;
//    }

    /**
     * 根据地址信息（省市区/省市）获取发起人信息
     *
     * @param province 省
     * @param city     市
     * @param region   区
     * @return com.yimao.cloud.pojo.dto.user.DistributorDTO
     * @author hhf
     * @date 2019/1/24
     */
    @GetMapping(value = "/distributor/originator")
    @ApiOperation(value = "根据地址信息（省市区）获取发起人信息", notes = "根据地址信息（省市区）获取发起人信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "province", value = "省", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "city", value = "市", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "region", value = "区", dataType = "String", paramType = "query")
    })
    public ResponseEntity getOriginatorByAddress(@RequestParam(value = "province") String province,
                                                 @RequestParam(value = "city", required = false) String city,
                                                 @RequestParam(value = "region", required = false) String region) {
        DistributorDTO dto = distributorService.getOriginatorByAddress(province, city, region);
        return ResponseEntity.ok(dto);
    }


    /**
     * 根据经销商ID获取推荐人信息
     *
     * @param distributorId 经销商ID
     * @return com.yimao.cloud.pojo.dto.user.DistributorDTO
     * @author hhf
     * @date 2019/1/24
     */
    @GetMapping(value = "/distributor/recommend")
    @ApiOperation(value = "根据经销商ID获取推荐人信息", notes = "根据经销商ID获取推荐人信息")
    @ApiImplicitParam(name = "distributorId", value = "经销商Id", dataType = "Integer", paramType = "query")
    public ResponseEntity getRecommendByDistributorId(@RequestParam Integer distributorId) {
        DistributorDTO dto = distributorService.getRecommendByDistributorId(distributorId);
        return ResponseEntity.ok(dto);
    }

    /**
     * 根据经销商ID获取经销商主账号信息
     *
     * @param distributorId 经销商ID
     * @return com.yimao.cloud.pojo.dto.user.DistributorDTO
     * @author hhf
     * @date 2019/1/24
     */
    @GetMapping(value = "/distributor/main")
    @ApiOperation(value = "根据经销商ID获取经销商主账号信息", notes = "根据经销商ID获取经销商主账号信息")
    @ApiImplicitParam(name = "distributorId", value = "经销商Id", dataType = "Integer", paramType = "query")
    public ResponseEntity getMainAccountByDistributorId(@RequestParam("distributorId") Integer distributorId) {
        DistributorDTO dto = distributorService.getMainAccountByDistributorId(distributorId);
        return ResponseEntity.ok(dto);
    }

    /**
     * 根据身份证验证经销商是否已经注册
     *
     * @param idCard 身份证账号
     * @return org.springframework.http.ResponseEntity
     * @author hhf
     * @date 2018/1/26
     */
    @ExecutionTime
    @GetMapping(value = "/distributor/verify")
    @ApiOperation(value = "根据身份证验证经销商是否已经注册", notes = "根据身份证验证经销商是否已经注册")
    @ApiImplicitParam(name = "idCard", value = "身份证账号", required = true, dataType = "String", paramType = "query")
    public ResponseEntity existDistributorByIdCard(@RequestParam("idCard") String idCard) {
        Boolean byIdCard = distributorService.existDistributorByIdCard(idCard);
        return ResponseEntity.ok(byIdCard);
    }

    /**
     * 根据参数获取经销商id
     *
     * @param distributorType    经销商类型
     * @param distributorAccount 经销商账号
     * @param distributorName    经销商名称
     * @param province           省
     * @param city               市
     * @param region             区
     * @param recommendName      推荐人姓名
     * @param recommendAccount   推荐人账号
     * @return org.springframework.http.ResponseEntity
     * @author hhf
     * @date 2019/1/30
     */
    @GetMapping(value = "/distributor/ids")
    @ApiOperation(value = "根据参数获取经销商id", notes = "根据参数获取经销商id")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "用户e家号", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "distributorType", value = "经销商类型", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "distributorAccount", value = "经销商账号", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "distributorName", value = "经销商名称", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "province", value = "省", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "city", value = "市", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "region", value = "区", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "recommendName", value = "推荐人姓名", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "recommendAccount", value = "推荐人账号", dataType = "String", paramType = "query"),
    })
    public ResponseEntity getDistributorIdByParam(@RequestParam(value = "userId", required = false) Integer userId,
                                                  @RequestParam(value = "distributorType", required = false) Integer distributorType,
                                                  @RequestParam(value = "distributorAccount", required = false) String distributorAccount,
                                                  @RequestParam(value = "distributorName", required = false) String distributorName,
                                                  @RequestParam(value = "province", required = false) String province,
                                                  @RequestParam(value = "city", required = false) String city,
                                                  @RequestParam(value = "region", required = false) String region,
                                                  @RequestParam(value = "recommendName", required = false) String recommendName,
                                                  @RequestParam(value = "recommendAccount", required = false) String recommendAccount) {
        List<Integer> ids = distributorService.getDistributorIdByParam(userId, distributorType, distributorAccount, distributorName, province, city, region, recommendName, recommendAccount);
        return ResponseEntity.ok(ids);
    }


    /**
     * 根据经销商ID集合获取经销商信息
     *
     * @param distributorIds 经销商集合
     * @return org.springframework.http.ResponseEntity
     * @author hhf
     * @date 2019/2/21
     */
    @GetMapping(value = "/distributor/list")
    @ApiOperation(value = "根据经销商ID集合获取经销商信息", notes = "根据经销商ID集合获取经销商信息")
    @ApiImplicitParam(name = "distributorIds", value = "经销商Id", dataType = "Integer", paramType = "query", allowMultiple = true)
    public ResponseEntity getDistributorByDistributorIds(@RequestParam("distributorIds") List<Integer> distributorIds) {
        List<DistributorDTO> dtos = distributorService.getDistributorByDistributorIds(distributorIds);
        return ResponseEntity.ok(dtos);
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
        Boolean agentRanking = distributorService.checkAgentRanking(agentLevel, ranking);
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
        List<DistributorDTO> dto = distributorService.getRecommendByAddress(province, city, region);
        return ResponseEntity.ok(dto);
    }

    /**
     * 根据用户ID获取上级经销商信息（返回基本信息+扩展信息）
     *
     * @param userId 用户e家号
     * @author hhf
     * @date 2019/5/7
     */
    @GetMapping(value = "/distributor/expansion")
    @ApiOperation(value = "根据用户ID获取上级经销商信息（返回基本信息+扩展信息）")
    @ApiImplicitParam(name = "id", value = "经销商ID", required = true, dataType = "Long", paramType = "query")
    public DistributorDTO getExpansionInfoByUserId(@RequestParam Integer userId) {
        return distributorService.getExpansionInfoByUserId(userId);
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
        return distributorService.getChangeInfoByUserId(userId);
    }

    /**
     * 用户--经销商/代理商--变更记录（站务系统调用）
     *
     * @param id
     * @return
     */
    @GetMapping(value = "/distributor/station/changeInfo")
    public Object stationGetChangeInfoByDistributorId(@RequestParam Integer id) {

        return ResponseEntity.ok(distributorService.getChangeInfoByUserIdToStation(id));
    }

    /**
     * 经销商导出
     *
     * @param query 查询条件
     */
    @PostMapping(value = "/distributor/export")
    @ApiOperation(value = "经销商导出")
    @ApiImplicitParam(name = "query", value = "查询信息", required = true, dataType = "DistributorQueryDTO", paramType = "body")
    public List<DistributorExportDTO> distributorExport(@RequestBody DistributorQueryDTO query) {
        return distributorService.distributorExport(query);
    }

    /**
     * 更新经销商信息
     *
     * @param dto 经销商信息
     */
    @PutMapping(value = "/distributor/update")
    @ApiOperation(value = "更新经销商信息")
    @ApiImplicitParam(name = "dto", value = "经销商信息", required = true, dataType = "DistributorDTO", paramType = "body")
    public void updateDistributor(@RequestBody DistributorDTO dto) {
        distributorService.updateDistributor(dto);
    }

    /**
     * 根据主账号ID集合获取企业版子账号的经销商Id集合
     *
     * @param mid 主账号ID
     * @return List
     * @author hhf
     * @date 2019/5/29
     */
    @GetMapping(value = "/distributor/child")
    @ApiOperation(value = "根据经销商ID集合获取经销商信息", notes = "根据经销商ID集合获取经销商信息")
    @ApiImplicitParam(name = "mid", value = "主账号ID", dataType = "Integer", paramType = "query")
    public List<DistributorDTO> getSonDistributorByMid(@RequestParam Integer mid) {
        return distributorService.getSonDistributorByMid(mid);
    }

    /**
     * 模糊查询经销商信息 （给服务站使用）
     *
     * @author hhf
     * @date 2019/6/13
     */
    @PostMapping(value = "/distributor/station")
    public List<DistributorDTO> getDistributorByParams(@RequestBody StationDistributorQueryDTO query) {
        return distributorService.getDistributorByParams(query.getParam(), query.getProvinces(), query.getCitys(), query.getRegions());
    }

    /**
     * 根据经销商oldId获取经销商信息
     *
     * @param oldId 经销商oldId
     */
    @GetMapping(value = "/distributor")
    public DistributorDTO getDistributorByOldId(@RequestParam String oldId) {
        Distributor query = new Distributor();
        query.setOldId(oldId);
        Distributor distributor = distributorMapper.selectOne(query);
        if (distributor == null) {
            return null;
        }
        DistributorDTO dto = new DistributorDTO();
        distributor.convert(dto);
        return dto;
    }

    /**
     * @Author ycl
     * @Description 账号查询经销商
     * @Date 15:56 2019/7/17
     * @Param
     **/
    @GetMapping(value = "/distributor/account")
    public DistributorDTO getDistributorByUserName(@RequestParam String userName) {
        return distributorService.getDistributorByUserName(userName);
    }


    /**
     * @Author ycl
     * @Description 用户中心--我的子账号
     * @Date 9:17 2019/8/6
     * @Param
     **/
    @GetMapping(value = "/user/sonAccount/info/{id}")
    public Object getSonAccountInfo(@PathVariable(value = "id") Integer id) {
        return distributorService.getSonAccountInfo(id);
    }

    /**
     * @Author liuyi
     * @Description app-经销商当前身份信息(获取经销商当前身份和剩余配额和总配额 我的合同)
     * @Date 9:17 2019/8/19
     * @Param
     **/
    @GetMapping(value = "/distributor/info/app/{distributorId}")
    @ApiImplicitParam(name = "distributorId", value = "经销商id", required = true, dataType = "Long", paramType = "path")
    public Object getAccountInfoForAPP(@PathVariable(value = "distributorId") Integer distributorId) {
        return distributorService.getDistributorAccountInfoForApp(distributorId);
    }

    /**
     * @param
     * @return com.yimao.cloud.pojo.dto.user.UserDTO
     * @description 经销商app注册经销商
     * @author Liu Yi
     * @date 2019/8/22 10:32
     */
    @PostMapping(value = "/distributor/app/register")
    @ApiImplicitParam(name = "userDistributorRegisterDTO", value = "注册信息", dataType = "UserDistributorRegisterDTO", paramType = "body")
    public Map<String, Object> registDistributorForApp(@RequestBody UserDistributorRegisterDTO userDistributorRegisterDTO) {
        if (userDistributorRegisterDTO == null) {
            throw new BadRequestException("注册信息不能为空！");
        }
        if (StringUtil.isBlank(userDistributorRegisterDTO.getMobile())) {
            throw new BadRequestException("手机号不能为空！");
        }
        if (userDistributorRegisterDTO.getRegistLevel() == null) {
            throw new BadRequestException("注册经销商类型不能为空！");
        }
        if (StringUtil.isBlank(userDistributorRegisterDTO.getSmsCode())) {
            throw new BadRequestException("验证码不能为空！");
        }
        if (StringUtil.isBlank(userDistributorRegisterDTO.getCountryCode())) {
            throw new BadRequestException("国家代码不能为空！");
        }

        return distributorService.registDistributorForApp(userDistributorRegisterDTO.getMobile(), userDistributorRegisterDTO.getCountryCode(), userDistributorRegisterDTO.getSmsCode(), userDistributorRegisterDTO.getRegistLevel(), userDistributorRegisterDTO.getUserCompany());
    }

    /**
     * @Author ycl
     * @Description 我的代理-我的经销商(区代下的经销商)
     * @Date 11:14 2019/9/24
     * @Param
     **/
    @GetMapping(value = "agent/distributors/app/{pageNum}/{pageSize}")
    public Object getMyDistributors(@PathVariable(value = "pageNum") Integer pageNum,
                                    @PathVariable(value = "pageSize") Integer pageSize,
                                    @RequestParam(value = "distributorId") Integer distributorId,
                                    @RequestParam(value = "distributorType", required = false) Integer distributorType,
                                    @RequestParam(value = "province", required = false) String province,
                                    @RequestParam(value = "city", required = false) String city,
                                    @RequestParam(value = "region", required = false) String region) {
        PageVO<DistributorDTO> distributorDTOList = distributorService.getMyDistributors(pageNum, pageSize, distributorId, distributorType, province, city, region);
        Map map = new HashMap(8);
        map.put("distributorDTOList", distributorDTOList);
        if (null != distributorDTOList) {
            map.put("count", distributorDTOList.getTotal());
        }
        return map;
    }


    /**
     * @Author ycl
     * @Description 企业版经销商发展子账号
     * @Date 15:03 2019/8/5
     * @Param
     **/
    @GetMapping(value = "/user/develop/sonAccount")
    public Object developSonAccount(@RequestParam(value = "realName") String realName,
                                    @RequestParam(value = "sex") Integer sex,
                                    @RequestParam(value = "idCard") String idCard,
                                    @RequestParam(value = "email", required = false) String email,
                                    @RequestParam(value = "userId") Integer userId) {
        Object obj = distributorService.developSonAccount(realName, sex, idCard, email, userId);
        return ResponseEntity.ok(obj);
    }


    /**
     * 企业版主账号和子账号列表
     */
    @GetMapping(value = "/distributor/mid/child")
    @ApiOperation(value = "企业版主账号和子账号列表", notes = "企业版主账号和子账号列表")
    @ApiImplicitParam(name = "mid", value = "主账号ID", dataType = "Integer", paramType = "query")
    public List<DistributorDTO> getDistributorAndSonByMid(@RequestParam Integer mid) {
        return distributorService.getDistributorAndSonByMid(mid);
    }


    @GetMapping(value = "/distributor/wx/account")
    public List<UserAccountDTO> queryDistributors(@RequestParam(value = "distributorIds") List<Integer> distributorIds) {
        return distributorService.queryDistributors(distributorIds);
    }


    /**
     * @Author ycl
     * @Description H5-经销商自注册-注册信息校验
     * @Date 11:11 2019/9/24
     * @Param
     **/
    @PostMapping(value = "/h5/distributor/check")
    public CommResult checkDistributor(@RequestBody OwnerDistributorDTO ownerDistributorDTO) {
        try {
            return distributorService.checkDistributor(ownerDistributorDTO);
        } catch (Exception e) {
            if (e instanceof DialogException) {
                //对话框消息提示
                return CommResult.dialogError(e.getMessage());
            } else {
                throw e;
            }
        }
    }


    /**
     * @Author ycl
     * @Description H5-经销商自注册-获取短信验证码
     * @Date 11:12 2019/9/24
     * @Param
     **/
    @PostMapping(value = "/h5/distributor/sendsmscode")
    public CommResult distributorSendSmsCode(@RequestBody OwnerDistributorDTO ownerDistributorDTO,
                                             @RequestParam(value = "key") String key) {
        return distributorService.distributorSendSmsCode(ownerDistributorDTO, key);
    }


    /**
     * @Author ycl
     * @Description 经销商注册校验短信验证码
     * @Date 11:12 2019/9/24
     * @Param
     **/
    @PostMapping(value = "/h5/distributor/checksmscode")
    public CommResult distributorCheckSmsCode(@RequestBody OwnerDistributorDTO ownerDistributorDTO,
                                              @RequestParam(value = "smsCode") String smsCode,
                                              @RequestParam(value = "key") String key) {
        return distributorService.distributorCheckSmsCode(ownerDistributorDTO, smsCode, key);
    }

    /**
     * @Author ycl
     * @Description H5-经销商自注册-确认经销商信息
     * @Date 11:13 2019/9/24
     * @Param
     **/
    @PostMapping(value = "/h5/distributor/determine")
    public CommResult determineDistributor(@RequestBody OwnerDistributorDTO ownerDistributorDTO,
                                           @RequestParam(value = "key") String key,
                                           @RequestParam(value = "smsCode") String smsCode) {
        return distributorService.determineDistributor(ownerDistributorDTO, key, smsCode);
    }


    /**
     * @Author ycl
     * @Description H5-主账号发展子账号-子账号信息校验
     * @Date 19:13 2019/9/20
     * @Param
     **/
    @PostMapping(value = "/h5/distributor/subaccount/check")
    public CommResult checkSubaccount(@RequestBody SubDistributorAccountDTO subAccountDTO) {
        try {
            return distributorService.checkSubaccount(subAccountDTO);
        } catch (Exception e) {
            if (e instanceof DialogException) {
                //对话框消息提示
                return CommResult.dialogError(e.getMessage());
            } else {
                throw e;
            }
        }
    }


    /**
     * @Author ycl
     * @Description H5-主账号发展子账号-发送短信验证码
     * @Date 19:41 2019/9/20
     * @Param
     **/
    @PostMapping(value = "/h5/distributor/subaccount/sendsmscode")
    public CommResult subaccountSendsmscode(@RequestBody SubDistributorAccountDTO subAccountDTO, @RequestParam(value = "key") String key) {
        return distributorService.subaccountSendsmscode(subAccountDTO, key);
    }


    /**
     * @Author ycl
     * @Description H5-主账号发展子账号-校验短信验证码
     * @Date 9:10 2019/9/23
     * @Param
     **/
    @PostMapping(value = "/h5/distributor/subaccount/checksmscode")
    public CommResult subaccountCheckSmsCode(@RequestBody SubDistributorAccountDTO subAccountDTO,
                                             @RequestParam(value = "key") String key,
                                             @RequestParam(value = "smsCode") String smsCode) {
        return distributorService.subaccountCheckSmsCode(subAccountDTO, key, smsCode);
    }


    /**
     * @Author ycl
     * @Description H5-主账号发展子账号-子账号信息确认
     * @Date 9:26 2019/9/23
     * @Param
     **/
    @PostMapping(value = "/h5/distributor/subaccount/determine")
    public CommResult determineSubaccount(@RequestBody SubDistributorAccountDTO subAccountDTO,
                                          @RequestParam(value = "key") String key,
                                          @RequestParam(value = "smsCode") String smsCode) {
        return distributorService.determineSubaccount(subAccountDTO, key, smsCode);
    }

    /**
     * @Author Liu Long Jie
     * @Description 根据areaId 获取经销商id
     * @Date 2020-2-13 15:13:25
     * @Param
     **/
    @PostMapping(value = "/distributor/ids/area")
    public Object getDistributorIdsByAreaIds(@RequestBody Set<Integer> areaIds) {
        return ResponseEntity.ok(distributorService.getDistributorIdsByAreaIds(areaIds));
    }

    /**
     * 站务系统-控制台-待办事项(区县级代理商，经销商，会员用户，普通用户）
     *
     * @param areas
     * @return
     */
    @PostMapping(value = "/distributor/station/stationDistributorNum")
    StationScheduleDTO getStationDistributorNum(@RequestBody Set<Integer> areas) {

        StationScheduleDTO res = new StationScheduleDTO();

        //经销商与区级代理商
        StationScheduleDTO distributor = distributorMapper.getStationDistributorNum(areas);

        List<Integer> distributorIds = distributorService.getDistributorIdsByAreaIds(areas);
        //会员用户与普通用户
        StationScheduleDTO user = userMapper.getStationGeneralUserNumAndVipUserNum(distributorIds);

        res.setAgentNum(distributor.getAgentNum());
        res.setDistributorNum(distributor.getDistributorNum());
        res.setCommonUserNum(user.getCommonUserNum());
        res.setVipUserNum(user.getVipUserNum());

        return res;

    }

    /****
     * 根据areaId获取经销商信息
     * @param areaIds
     * @return
     */
     @GetMapping(value = "/distributor/ids/area/app")
     public Object getDistributorIdsByAreaIdsForApp(@RequestParam(value = "areaIds") List<Integer> areaIds) {
         return ResponseEntity.ok(distributorMapper.getDistributorIdsByAreaIdsForApp(areaIds));
     }

}
