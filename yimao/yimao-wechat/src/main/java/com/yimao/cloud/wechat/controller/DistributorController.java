package com.yimao.cloud.wechat.controller;


import com.yimao.cloud.base.msg.CommResult;
import com.yimao.cloud.pojo.dto.user.OwnerDistributorDTO;
import com.yimao.cloud.pojo.dto.user.SubDistributorAccountDTO;
import com.yimao.cloud.wechat.feign.UserFeign;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;

/**
 * @description: H5经销商自注册
 * @author: yu chunlei
 * @create: 2019-09-11 09:32:28
 **/
@RestController
@Slf4j
@Api(tags = {"DistributorController"})
public class DistributorController {

    @Resource
    private UserFeign userFeign;

    /**
     * @Author ycl
     * @Description H5-经销商自注册-注册信息校验
     * @Date 9:17 2019/9/20
     * @Param
    **/
    @PostMapping(value = "/h5/distributor/check")
    @ApiOperation(value = "H5-经销商自注册-注册信息校验", notes = "H5-经销商自注册-注册信息校验")
    @ApiImplicitParam(name = "ownerDidtributorDTO", value = "经销商信息实体类", dataType = "OwnerDidtributorDTO", paramType = "body")
    public Object checkDistributor(@RequestBody OwnerDistributorDTO ownerDistributorDTO) {
        return userFeign.checkDistributor(ownerDistributorDTO);
    }



    /**
     * @Author ycl
     * @Description H5-经销商自注册-获取短信验证码
     * @Date 11:31 2019/9/20
     * @Param
    **/
    @PostMapping(value = "/h5/distributor/sendsmscode")
    @ApiOperation(value = "H5-经销商自注册-获取短信验证码", notes = "H5-经销商自注册-获取短信验证码")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "ownerDidtributorDTO", value = "经销商信息实体类", dataType = "OwnerDidtributorDTO", paramType = "body"),
            @ApiImplicitParam(name = "key", value = "前一步接口返回的key", dataType = "String", paramType = "query", required = true),
    })
    public Object distributorSendSmsCode(@RequestBody OwnerDistributorDTO ownerDistributorDTO,
                                             @RequestParam(value = "key") String key){
        return userFeign.distributorSendSmsCode(ownerDistributorDTO,key);
    }




    /**
     * @Author ycl
     * @Description H5-经销商自注册-校验短信验证码
     * @Date 11:59 2019/9/20
     * @Param
    **/
    @PostMapping(value = "/h5/distributor/checksmscode")
    @ApiOperation(value = "经销商注册校验短信验证码", notes = "经销商注册校验短信验证码")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "ownerDidtributorDTO", value = "经销商信息实体类", dataType = "OwnerDidtributorDTO", paramType = "body"),
            @ApiImplicitParam(name = "key", value = "前一步接口返回的key", dataType = "String", paramType = "query", required = true),
            @ApiImplicitParam(name = "smsCode", value = "短信验证码", dataType = "String", paramType = "query", required = true)
    })
    public Object distributorCheckSmsCode(@RequestBody OwnerDistributorDTO ownerDistributorDTO,
                                              @RequestParam(value = "smsCode") String smsCode,
                                              @RequestParam(value = "key") String key) {
        return userFeign.distributorCheckSmsCode(ownerDistributorDTO,smsCode, key);
    }


    /**
     * @Author ycl
     * @Description H5-经销商自注册-确认经销商信息
     * @Date 16:16 2019/9/20
     * @Param
    **/
    @PostMapping(value = "/h5/distributor/determine")
    @ApiOperation(value = "H5-经销商自注册-确认经销商信息", notes = "H5-经销商自注册-确认经销商信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "ownerDidtributorDTO", value = "经销商信息实体类", dataType = "OwnerDidtributorDTO", paramType = "body"),
            @ApiImplicitParam(name = "key", value = "前一步接口返回的key", dataType = "String", paramType = "query", required = true),
            @ApiImplicitParam(name = "smsCode", value = "短信验证码", dataType = "String", paramType = "query", required = true),
    })
    public Object determineDistributor(@RequestBody OwnerDistributorDTO ownerDistributorDTO,
                                            @RequestParam(value = "key") String key,
                                           @RequestParam(value = "smsCode") String smsCode){
        return userFeign.determineDistributor(ownerDistributorDTO,key,smsCode);
    }




    /**
     * @Author ycl
     * @Description H5-主账号发展子账号-子账号信息校验
     * @Date 9:17 2019/9/20
     * @Param
     **/
    @PostMapping(value = "/h5/distributor/subaccount/check")
    @ApiOperation(value = "H5-主账号发展子账号-子账号信息校验", notes = "H5-主账号发展子账号-子账号信息校验")
    @ApiImplicitParam(name = "subAccountDTO", value = "子账号信息实体类", dataType = "SubDistributorAccountDTO", paramType = "body")
    public Object checkSubaccount(@RequestBody SubDistributorAccountDTO subAccountDTO) {
        return userFeign.checkSubaccount(subAccountDTO);
    }



    /**
     * @Author ycl
     * @Description H5-主账号发展子账号-发送短信验证码
     * @Date 19:38 2019/9/20
     * @Param
    **/
    @PostMapping(value = "/h5/distributor/subaccount/sendsmscode")
    @ApiOperation(value = "H5-主账号发展子账号-发送短信验证码", notes = "H5-主账号发展子账号-发送短信验证码")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "subAccountDTO", value = "子账号信息实体类", dataType = "SubDistributorAccountDTO", paramType = "body"),
            @ApiImplicitParam(name = "key", value = "上一个接口返回的key", dataType = "String", paramType = "query")
    })
    public Object subaccountSendsmscode(@RequestBody SubDistributorAccountDTO subAccountDTO,
                                            @RequestParam(value = "key") String key) {
        return userFeign.subaccountSendsmscode(subAccountDTO,key);
    }



    /**
     * @Author ycl
     * @Description H5-主账号发展子账号-校验短信验证码
     * @Date 9:08 2019/9/23
     * @Param
    **/
    @PostMapping(value = "/h5/distributor/subaccount/checksmscode")
    @ApiOperation(value = "H5-主账号发展子账号-校验短信验证码", notes = "H5-主账号发展子账号-校验短信验证码")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "subAccountDTO", value = "子账号信息实体类", dataType = "SubDistributorAccountDTO", paramType = "body"),
            @ApiImplicitParam(name = "key", value = "上一个接口返回的key", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "smsCode", value = "验证码", dataType = "String", paramType = "query")
    })
    public Object subaccountCheckSmsCode(@RequestBody SubDistributorAccountDTO subAccountDTO,
                                             @RequestParam(value = "key") String key,
                                             @RequestParam(value = "smsCode") String smsCode){
        return userFeign.subaccountCheckSmsCode(subAccountDTO,key,smsCode);
    }



    /**
     * @Author ycl
     * @Description H5-主账号发展子账号-子账号信息确认
     * @Date 9:25 2019/9/23
     * @Param
    **/
    @PostMapping(value = "/h5/distributor/subaccount/determine")
    @ApiOperation(value = "H5-主账号发展子账号-子账号信息确认", notes = "H5-主账号发展子账号-子账号信息确认")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "subAccountDTO", value = "子账号信息实体类", dataType = "SubDistributorAccountDTO", paramType = "body"),
            @ApiImplicitParam(name = "key", value = "上一个接口返回的key", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "smsCode", value = "验证码", dataType = "String", paramType = "query")
    })
    public Object determineSubaccount(@RequestBody SubDistributorAccountDTO subAccountDTO,
                                          @RequestParam(value = "key") String key,
                                          @RequestParam(value = "smsCode") String smsCode){
        return userFeign.determineSubaccount(subAccountDTO,key,smsCode);
    }




}
