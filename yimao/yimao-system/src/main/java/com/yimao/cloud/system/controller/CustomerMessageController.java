package com.yimao.cloud.system.controller;

import com.yimao.cloud.pojo.dto.system.CustomerMessageDTO;
import com.yimao.cloud.system.po.CustomerMessage;
import com.yimao.cloud.system.service.CustomerMessageService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * 留言消息
 *
 * @author lizhiqiang
 * @date 2019/1/15
 */
@RestController
@Slf4j
@Api(tags = {"CustomerMessageController"})
public class CustomerMessageController {

    @Resource
    private CustomerMessageService customerMessageService;

    /**
     * 分页查询留言咨询
     *
     * @param province     省
     * @param city         市
     * @param region       区
     * @param customerName 姓名
     * @param mobile       手机号
     * @param joinType     加盟类型
     * @param beginTime    开始时间
     * @param endTime      结束时间
     * @param pageNum      当前页
     * @param pageSize     每页显示条数
     * @return
     */
    @GetMapping("/customer/message/{pageNum}/{pageSize}")
    @ApiOperation(value = "分页查询留言列表", notes = "分页查询留言列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "province", value = "省", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "city", value = "市", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "region", value = "区", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "customerName", value = "姓名", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "mobile", value = "手机号", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "joinType", value = "加盟类型", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "beginTime", value = "开始时间", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "endTime", value = "结束时间", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "terminal", value = "咨询来源端", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "pageNum", value = "页码", dataType = "Long", required = true, paramType = "path"),
            @ApiImplicitParam(name = "pageSize", value = "页数", dataType = "Long", required = true, paramType = "path")
    })
    public Object list(@RequestParam(value = "province", required = false) String province,
                       @RequestParam(value = "city", required = false) String city,
                       @RequestParam(value = "region", required = false) String region,
                       @RequestParam(value = "customerName", required = false) String customerName,
                       @RequestParam(value = "mobile", required = false) String mobile,
                       @RequestParam(value = "joinType", required = false) Integer joinType,
                       @RequestParam(value = "beginTime", required = false) String beginTime,
                       @RequestParam(value = "endTime", required = false) String endTime,
                       @RequestParam(value = "terminal", required = false) Integer terminal,
                       @PathVariable(value = "pageNum") Integer pageNum,
                       @PathVariable(value = "pageSize") Integer pageSize) {
        return ResponseEntity.ok(customerMessageService.listCustomerMessage(province, city, region, customerName, mobile, joinType, beginTime, endTime, terminal, pageNum, pageSize));
    }


    /*   *//**
     * 保存客户留言
     *
     * @param dto
     * @return
     *//*
    @PostMapping(value = "/customer/manager")
    @ApiOperation(value = "保存客户留言", notes = "保存客户留言")
    @ApiImplicitParam(name = "dto", value = "客户留言消息", dataType = "CustomerMessageDTO", required = true, paramType = "body")
    public Object save(@RequestBody CustomerMessageDTO dto) {
        CustomerMessage customerMessage = new CustomerMessage(dto);
        customerMessageService.saveCustomerMessage(customerMessage);
        dto.setId(customerMessage.getId());
        return ResponseEntity.ok(dto);
    }*/
}
