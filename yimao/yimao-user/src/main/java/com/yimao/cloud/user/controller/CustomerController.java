package com.yimao.cloud.user.controller;

import com.yimao.cloud.pojo.dto.user.*;
import com.yimao.cloud.pojo.vo.PageVO;
import com.yimao.cloud.user.service.CustomerService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;

/**
 * @description: 客户相关
 * @author: yu chunlei
 * @create: 2019-01-17 10:04:13
 **/
@RestController
@Slf4j
public class CustomerController {

    @Resource
    private CustomerService customerService;



    /**
     * @Author ycl
     * @Description 地址管理-客户地址列表
     * @Date 15:57 2019/9/17
     * @Param
    **/
    @GetMapping("/customers/{pageNum}/{pageSize}")
    public Object pageQueryCustomer(@PathVariable(value = "pageNum") Integer pageNum,
                                    @PathVariable(value = "pageSize") Integer pageSize,
                                    @RequestParam(value = "userId") Integer userId){
        PageVO<CustomerAddressDTO> page = customerService.pageQueryCustomerAddress(pageNum,pageSize,userId);
        return page;
    }


    /**
     * @Author ycl
     * @Description 查询客户地址详情
     * @Date 17:14 2019/9/18
     * @Param
    **/
    @GetMapping(value = "/customers/detail/info/{id}")
    public Object queryCustomerInfo(@PathVariable(value = "id") Integer id,
                                    @RequestParam(value = "type") Integer type){
        return customerService.queryCustomerInfo(id,type);
    }



    /**
     * @Author ycl
     * @Description 地址管理-客户地址-新增个人客户
     * @Date 17:18 2019/9/17
     * @Param
    **/
    @PostMapping("/customers/person")
    public void savePersonCustomer(@RequestBody PersonCustomerDTO personCustomerDTO) {
        customerService.savePersonCustomer(personCustomerDTO);
    }

    /**
     * @Author ycl
     * @Description 地址管理-客户地址-修改个人客户
     * @Date 19:04 2019/9/17
     * @Param
    **/
    @PutMapping(value = "/customers/person")
    public void updatePersonCustomer(@RequestBody PersonCustomerDTO personCustomerDTO){
        customerService.updatePersonCustomer(personCustomerDTO);
    }

    /**
     * @Author ycl
     * @Description 逻辑删除个人客户
     * @Date 19:17 2019/9/17
     * @Param
    **/
    @DeleteMapping(value = "/customers/person/{id}")
    public void deletePersonCustomer(@PathVariable(value = "id") Integer id){
        customerService.deletePersonCustomer(id);
    }

    /**
     * @Author ycl
     * @Description 地址管理-客户地址-新增企业客户
     * @Date 17:27 2019/9/17
     * @Param
    **/
    @PostMapping("/customers/company")
    public void saveCompanyCustomer(@RequestBody CompanyCustomerDTO companyCustomerDTO) {
        customerService.saveCompanyCustomer(companyCustomerDTO);
    }


    /**
     * @Author ycl
     * @Description 地址管理-客户地址-修改企业客户
     * @Date 19:08 2019/9/17
     * @Param
    **/
    @PutMapping(value = "/customers/company")
    public void updateCompanyCustomer(@RequestBody CompanyCustomerDTO companyCustomerDTO){
        customerService.updateCompanyCustomer(companyCustomerDTO);
    }


    /**
     * @Author ycl
     * @Description 逻辑删除企业客户
     * @Date 19:23 2019/9/17
     * @Param
    **/
    @DeleteMapping(value = "/customers/company/{id}")
    public void deleteCompanyCustomer(@PathVariable(value = "id") Integer id){
        customerService.deleteCompanyCustomer(id);
    }

}
