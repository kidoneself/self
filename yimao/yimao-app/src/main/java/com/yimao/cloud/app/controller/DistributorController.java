package com.yimao.cloud.app.controller;

import com.yimao.cloud.app.feign.UserFeign;
import com.yimao.cloud.base.exception.RemoteCallException;
import com.yimao.cloud.pojo.dto.user.DistributorCountDTO;
import com.yimao.cloud.pojo.dto.user.DistributorDTO;
import com.yimao.cloud.pojo.dto.user.DistributorQueryDTO;
import com.yimao.cloud.pojo.dto.user.UserDistributorRegisterDTO;
import com.yimao.cloud.pojo.vo.PageVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * 经销商信息
 *
 * @author hhf
 * @date 2019/1/22
 */
@RestController
@Slf4j
@Api(tags = "DistributorController")
public class DistributorController {

    @Resource
    private UserFeign userFeign;

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
        DistributorCountDTO dto = userFeign.countDistributorById(id);
        return ResponseEntity.ok(dto);
    }


//    /**
//     * @Description:翼猫榜-经销商排名
//     * @author ycl
//     * @param distId
//     * @Return: org.springframework.http.ResponseEntity
//     * @Create: 2019/1/23 11:51
//    */
//    @GetMapping(value = "/distributor/biling/{distId}")
//    @ApiOperation(value = "翼猫榜经销商排名", notes = "翼猫榜经销商排名")
//    @ApiImplicitParam(name = "distId", value = "经销商ID", required = true, dataType = "Long", paramType = "path")
//    public ResponseEntity distributorBiling(@PathVariable("distId") Integer distId){
//        Map<String,Object> map = userFeign.getDistributorBiling(distId);
//        return ResponseEntity.ok(map);
//    }

    /**
     * 根据身份证验证经销商是否已经注册
     *
     * @param idCard 身份证账号
     * @return org.springframework.http.ResponseEntity
     * @author hhf
     * @date 2018/1/26
     */
    @GetMapping(value = "/distributor/verify")
    @ApiOperation(value = "根据身份证验证经销商是否已经注册", notes = "根据身份证验证经销商是否已经注册")
    @ApiImplicitParam(name = "idCard", value = "身份证账号", required = true, dataType = "String", paramType = "query")
    public ResponseEntity existDistributorByIdCard(@RequestParam("idCard") String idCard) {
        Boolean byIdCard = userFeign.existDistributorByIdCard(idCard);
        return ResponseEntity.ok(byIdCard);
    }


    /**
     * 分页查询经销商信息
     *
     * @param pageNum  分页页数
     * @param pageSize 分页大小
     * @param query    查询信息
     * @return org.springframework.http.ResponseEntity
     * @author hhf
     * @date 2019/1/26
     */
    @GetMapping(value = "/distributor/{pageNum}/{pageSize}")
    @ApiOperation(value = "分页查询经销商信息", notes = "分页查询经销商信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "分页页码", defaultValue = "1", required = true, dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "pageSize", value = "分页大小", defaultValue = "10", required = true, dataType = "Long", paramType = "path")
    })
    public ResponseEntity pageQueryDistributor(@PathVariable(value = "pageNum") Integer pageNum,
                                               @PathVariable(value = "pageSize") Integer pageSize,
                                               DistributorQueryDTO query) {

        PageVO<DistributorDTO> page = userFeign.pageQueryDistributor(pageNum, pageSize, query);
        if (page == null) {
            throw new RemoteCallException();
        }
        return ResponseEntity.ok(page);
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
        return userFeign.getBasicInfoById(id);
    }

    /**
     * 根据企业版主账号ID获取企业版子账号集合
     *
     * @param mid 主账号ID
     * @return List
     * @author hhf
     * @date 2019/5/29
     */
    @GetMapping(value = "/distributor/child")
    @ApiOperation(value = "根据企业版主账号ID获取企业版子账号集合")
    @ApiImplicitParam(name = "mid", value = "主账号ID", dataType = "Long", paramType = "query")
    public List<DistributorDTO> getSonDistributorByMid(@RequestParam Integer mid) {
        return userFeign.getSonDistributorByMid(mid);
    }

    /**
     * @Author liuyi
     * @Description app-经销商当前身份信息(获取经销商当前身份和剩余配额和总配额 我的合同)
     * @Date 9:17 2019/8/19
     * @Param
     **/
    @GetMapping(value = "/distributor/info/app/{distributorId}")
    @ApiOperation(value = "app-经销商当前身份信息(获取经销商当前身份和剩余配额和总配额 我的合同)", notes = "app-经销商当前身份信息(获取经销商当前身份和剩余配额和总配额 我的合同)")
    @ApiImplicitParam(name = "distributorId", value = "经销商id", required = true, dataType = "Long", paramType = "path")
    public Object getAccountInfoForAPP(@PathVariable(value = "distributorId") Integer distributorId) {
        return userFeign.getDistributorAccountInfoForApp(distributorId);
    }

    /**
     * @param
     * @return com.yimao.cloud.pojo.dto.user.UserDTO
     * @description 经销商app注册经销商
     * @author Liu Yi
     * @date 2019/8/22 10:32
     */
    @PostMapping(value = "/distributor/app/register")
    @ApiOperation(value = "经销商app注册经销商", notes = "经销商app注册经销商")
    @ApiImplicitParam(name = "userDistributorRegisterDTO", value = "注册信息", dataType = "UserDistributorRegisterDTO", paramType = "body")
    public Map<String, Object> registDistributorForApp(@RequestBody UserDistributorRegisterDTO userDistributorRegisterDTO) {

        return userFeign.registDistributorForApp(userDistributorRegisterDTO);
    }


    /**
     * @Author ycl
     * @Description 我的代理-我的经销商(区代下的经销商)
     * @Date 11:59 2019/8/27
     * @Param
     **/
    @GetMapping(value = "agent/distributors/app/{pageNum}/{pageSize}")
    @ApiOperation(value = "我的代理-我的经销商(区代下的经销商)", notes = "我的代理-我的经销商(区代下的经销商)")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "分页页码", defaultValue = "1", required = true, dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "pageSize", value = "分页大小", defaultValue = "10", required = true, dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "distributorId", value = "经销商id", dataType = "Long", required = true, paramType = "query"),
            @ApiImplicitParam(name = "distributorType", value = "经销商类型：950-企业版（主）,1000-企业版（子）,650-个人版,50-体验版,350-微创版", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "province", value = "省", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "city", value = "市", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "region", value = "区", dataType = "String", paramType = "query")
    })
    public Object getMyDistributors(@PathVariable(value = "pageNum") Integer pageNum,
                                    @PathVariable(value = "pageSize") Integer pageSize,
                                    @RequestParam(value = "distributorId") Integer distributorId,
                                    @RequestParam(value = "distributorType", required = false) Integer distributorType,
                                    @RequestParam(value = "province", required = false) String province,
                                    @RequestParam(value = "city", required = false) String city,
                                    @RequestParam(value = "region", required = false) String region) {
        return userFeign.getMyDistributors(pageNum, pageSize, distributorId, distributorType, province, city, region);
    }



    /**
     * 企业版主账号和子账号列表
     */
    @GetMapping(value = "/distributor/mid/child")
    @ApiOperation(value = "企业版主账号和子账号列表", notes = "企业版主账号和子账号列表")
    @ApiImplicitParam(name = "mid", value = "主账号ID", dataType = "Integer", paramType = "query")
    public List<DistributorDTO> getDistributorAndSonByMid(@RequestParam Integer mid) {
        return userFeign.getDistributorAndSonByMid(mid);
    }

}
