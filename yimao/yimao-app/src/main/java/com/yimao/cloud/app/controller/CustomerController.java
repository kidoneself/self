package com.yimao.cloud.app.controller;

import com.yimao.cloud.app.feign.UserFeign;
import com.yimao.cloud.base.exception.BadRequestException;
import com.yimao.cloud.base.utils.StringUtil;
import com.yimao.cloud.pojo.dto.user.CompanyCustomerDTO;
import com.yimao.cloud.pojo.dto.user.CustomerAddressDTO;
import com.yimao.cloud.pojo.dto.user.PersonCustomerDTO;
import com.yimao.cloud.pojo.vo.PageVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import java.util.Objects;

/**
 * @description: 客户相关
 * @author: yu chunlei
 * @create: 2019-01-22 11:21:23
 **/
@RestController
@Slf4j
@Api(tags = "CustomerController")
public class CustomerController {

    @Resource
    private UserFeign userFeign;

    /**
     * @Author ycl
     * @Description 地址管理-客户地址列表
     * @Date 17:03 2019/9/17
     * @Param
    **/
    @GetMapping("/customers/{pageNum}/{pageSize}")
    @ApiOperation(value = "客户地址列表", notes = "客户地址列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "分页页码", defaultValue = "1", required = true, dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "pageSize", value = "每页条数", defaultValue = "10", required = true, dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "userId", value = "经销商的e家号", required = true, dataType = "Long", paramType = "query"
    )
    })
    public Object pageQueryCustomer(@PathVariable(value = "pageNum") Integer pageNum,
                                    @PathVariable(value = "pageSize") Integer pageSize,
                                    @RequestParam(value = "userId") Integer userId){
        PageVO<CustomerAddressDTO> page = userFeign.pageQueryCustomer(pageNum,pageSize,userId);
        return ResponseEntity.ok(page);
    }


    /**
     * @Author ycl
     * @Description 查询客户地址详情
     * @Date 17:21 2019/9/18
     * @Param
    **/
    @GetMapping(value = "/customers/detail/info/{id}")
    @ApiOperation(value = "查询客户地址详情",notes = "查询客户地址详情")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id",value = "客户id",required = true,dataType = "Long",paramType = "path"),
            @ApiImplicitParam(name = "type",value = "1:个人客户 2:企业客户",required = true,dataType = "Long",paramType = "query")
    })
    public Object queryCustomerInfo(@PathVariable(value = "id") Integer id,
                                    @RequestParam(value = "type") Integer type){
        Object obj = userFeign.queryCustomerInfo(id,type);
        return ResponseEntity.ok(obj);
    }


    /**
     * @Author ycl
     * @Description 地址管理-客户地址-新增个人客户
     * @Date 17:25 2019/9/17
     * @Param
    **/
    @PostMapping("/customers/person")
    @ApiOperation(value = "客户地址-新增个人客户", notes = "客户地址-新增个人客户")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "personCustomerDTO", value = "个人客户信息", required = true, dataType = "PersonCustomerDTO", paramType = "body")
    })
    public ResponseEntity savePersonCustomer(@RequestBody PersonCustomerDTO personCustomerDTO){
        this.checkPersonCustomer(personCustomerDTO);
        userFeign.savePersonCustomer(personCustomerDTO);
        return ResponseEntity.noContent().build();
    }

    /**
     * @Author ycl
     * @Description 地址管理-客户地址-修改个人客户
     * @Date 19:11 2019/9/17
     * @Param
    **/
    @PutMapping(value = "/customers/person")
    @ApiOperation(value = "客户地址-修改个人客户", notes = "客户地址-修改个人客户")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "personCustomerDTO", value = "个人客户信息", required = true, dataType = "PersonCustomerDTO", paramType = "body")
    })
    public ResponseEntity updatePersonCustomer(@RequestBody PersonCustomerDTO personCustomerDTO){
        this.checkPersonCustomer(personCustomerDTO);
        userFeign.updatePersonCustomer(personCustomerDTO);
        return ResponseEntity.noContent().build();
    }


    /**
     * @Author ycl
     * @Description 地址管理-客户地址-删除个人客户
     * @Date 9:35 2019/9/18
     * @Param
    **/
    @DeleteMapping(value = "/customers/person/{id}")
    @ApiOperation(value = "地址管理-客户地址-删除个人客户", notes = "地址管理-客户地址-删除个人客户")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "个人客户id", required = true, dataType = "Long", paramType = "path")
    })
    public ResponseEntity deletePersonCustomer(@PathVariable(value = "id") Integer id){
        userFeign.deletePersonCustomer(id);
        return ResponseEntity.noContent().build();
    }


    /**
     * @Author ycl
     * @Description 地址管理-客户地址-新增企业客户
     * @Date 17:31 2019/9/17
     * @Param
    **/
    @PostMapping("/customers/company")
    @ApiOperation(value = "客户地址-新增企业客户", notes = "客户地址-新增企业客户")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "companyCustomerDTO", value = "企业客户信息", required = true, dataType = "CompanyCustomerDTO", paramType = "body")
    })
    public ResponseEntity saveCompanyCustomer(@RequestBody CompanyCustomerDTO companyCustomerDTO){
        this.checkCompanyCustomer(companyCustomerDTO);
        userFeign.saveCompanyCustomer(companyCustomerDTO);
        return ResponseEntity.noContent().build();
    }


    /**
     * @Author ycl
     * @Description 地址管理-客户地址-修改企业客户
     * @Date 19:14 2019/9/17
     * @Param
    **/
    @PutMapping(value = "/customers/company")
    @ApiOperation(value = "客户地址-修改企业客户", notes = "客户地址-修改企业客户")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "companyCustomerDTO", value = "企业客户信息", required = true, dataType = "CompanyCustomerDTO", paramType = "body")
    })
    public ResponseEntity updateCompanyCustomer(@RequestBody CompanyCustomerDTO companyCustomerDTO){
        this.checkCompanyCustomer(companyCustomerDTO);
        userFeign.updateCompanyCustomer(companyCustomerDTO);
        return ResponseEntity.noContent().build();
    }


    /**
     * @Author ycl
     * @Description 地址管理-客户地址-删除企业客户
     * @Date 9:41 2019/9/18
     * @Param
    **/
    @DeleteMapping(value = "/customers/company/{id}")
    @ApiOperation(value = "客户地址-删除企业客户", notes = "客户地址-删除企业客户")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "企业客户id", required = true, dataType = "Long", paramType = "path")
    })
    public ResponseEntity deleteCompanyCustomer(@PathVariable(value = "id") Integer id){
        userFeign.deleteCompanyCustomer(id);
        return ResponseEntity.noContent().build();
    }


    public void checkPersonCustomer(PersonCustomerDTO personCustomerDTO){
        if(StringUtil.isEmpty(personCustomerDTO.getContact())){
            throw new BadRequestException("收货人为空");
        }
        if(Objects.isNull(personCustomerDTO.getSex())){
            throw new BadRequestException("未选择性别");
        }
        if(StringUtil.isEmpty(personCustomerDTO.getMobile())){
            throw new BadRequestException("联系方式为空");
        }
        if(StringUtil.isEmpty(personCustomerDTO.getProvince()) ||
                StringUtil.isEmpty(personCustomerDTO.getCity()) ||
                StringUtil.isEmpty(personCustomerDTO.getRegion())){
            throw new BadRequestException("收货地址为空");
        }
    }

    public void checkCompanyCustomer(CompanyCustomerDTO companyCustomerDTO){
        if(StringUtil.isEmpty(companyCustomerDTO.getCompanyName())){
            throw new BadRequestException("公司名称为空");
        }
        if(StringUtil.isEmpty(companyCustomerDTO.getProvince()) ||
                StringUtil.isEmpty(companyCustomerDTO.getCity()) ||
                StringUtil.isEmpty(companyCustomerDTO.getRegion())){
            throw new BadRequestException("收货地址为空");
        }
        if(StringUtil.isEmpty(companyCustomerDTO.getIndustry())){
            throw new BadRequestException("行业为空");
        }
        if(StringUtil.isEmpty(companyCustomerDTO.getLabel())){
            throw new BadRequestException("场景标签为空");
        }
        if(StringUtil.isEmpty(companyCustomerDTO.getNumber())){
            throw new BadRequestException("服务人数为空");
        }
        if(StringUtil.isEmpty(companyCustomerDTO.getContact())){
            throw new BadRequestException("收货人为空");
        }
        if(Objects.isNull(companyCustomerDTO.getSex())){
            throw new BadRequestException("未选择性别");
        }
        if(StringUtil.isEmpty(companyCustomerDTO.getMobile())){
            throw new BadRequestException("联系方式为空");
        }
    }

}
