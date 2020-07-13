package com.yimao.cloud.system.controller;

import com.yimao.cloud.base.utils.CollectionUtil;
import com.yimao.cloud.pojo.dto.station.StationHelperDataDTO;
import com.yimao.cloud.pojo.dto.system.CustomerAssistantDTO;
import com.yimao.cloud.pojo.dto.system.CustomerAssistantTypeDTO;
import com.yimao.cloud.pojo.vo.PageVO;
import com.yimao.cloud.system.po.CustomerAssistant;
import com.yimao.cloud.system.service.CustomerAssistantService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 问答列表
 *
 * @author lizhiqiang
 * @date 2019-1-14
 */
@RestController
@Slf4j
@Api(tags = "CustomerAssistantController")
public class CustomerAssistantController {

    @Resource
    private CustomerAssistantService customerAssistantService;


    /**
     * 分页查询客服问答列表
     *
     * @param typeCodes
     * @param questions
     * @param publish
     * @param recommend
     * @param terminal
     * @param typeName
     * @param pageNum
     * @param pageSize
     * @return
     */
    @GetMapping(value = "/customer/assistant/{pageNum}/{pageSize}")
    @ApiOperation(value = "分页查询客服问答列表", notes = "分页查询客服问答列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "页码", dataType = "Long", required = true, paramType = "path"),
            @ApiImplicitParam(name = "pageSize", value = "页数", dataType = "Long", required = true, paramType = "path"),
            @ApiImplicitParam(name = "typeCodes", value = "类型code", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "questions", value = "问题", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "publish", value = "状态", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "recommend", value = "是否推荐", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "terminal", value = "展示端", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "typeName", value = "分类名", dataType = "String", paramType = "query")
    })
    public Object list(@RequestParam(value = "typeCodes", required = false) Integer typeCodes,
                       @RequestParam(value = "questions", required = false) String questions,
                       @RequestParam(value = "publish", required = false) Integer publish,
                       @RequestParam(value = "recommend", required = false) Integer recommend,
                       @RequestParam(value = "terminal", required = false) Integer terminal,
                       @RequestParam(value = "typeName", required = false) String typeName,
                       @PathVariable(value = "pageNum") Integer pageNum,
                       @PathVariable(value = "pageSize") Integer pageSize) {

        PageVO<CustomerAssistantDTO> page = customerAssistantService.listCustomerAssistant(typeCodes, questions, publish, recommend, terminal, typeName, pageNum, pageSize);
        return ResponseEntity.ok(page);
    }

    /**
     * 新增客服问答
     *
     * @param dto
     * @return
     */
    @PostMapping(value = "/customer/assistant")
    @ApiOperation(value = "新增客服问答", notes = "新增客服问答")
    @ApiImplicitParam(name = "dto", value = "问答", dataType = "CustomerAssistantDTO", required = true, paramType = "body")
    public Object save(@RequestBody CustomerAssistantDTO dto) {
        customerAssistantService.saveCustomerAssistant(dto);
        return ResponseEntity.noContent().build();
    }

    /**
     * 修改客服问答
     *
     * @param dto
     * @return
     */
    @PutMapping(value = "/customer/assistant")
    @ApiOperation(value = "修改客服问答", notes = "修改客服问答")
    @ApiImplicitParam(name = "dto", value = "问答", dataType = "CustomerAssistantDTO", required = true, paramType = "body")
    public void update(@RequestBody CustomerAssistantDTO dto) {
        CustomerAssistant customerAssistant = new CustomerAssistant(dto);
        customerAssistantService.updateCustomerAssistant(customerAssistant);
    }

    /**
     * 客服问答发布
     *
     * @param id
     * @return
     */
    @PatchMapping(value = "/customer/assistant/issue/{id}")
    @ApiOperation(value = "客服问答发布", notes = "客服问答发布")
    @ApiImplicitParam(name = "id", value = "客服问答发布", dataType = "Long", required = true, paramType = "path")
    public Object issue(@PathVariable("id") Integer id) {
        customerAssistantService.issueCustomerAssistant(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * 逻辑删除客服问答
     *
     * @param id
     * @return
     */
    @PatchMapping(value = "/customer/assistant/remove/{id}")
    @ApiOperation(value = "逻辑删除客服问答", notes = "逻辑删除客服问答")
    @ApiImplicitParam(name = "id", value = "问答", dataType = "Long", required = true, paramType = "path")
    public Object remove(@PathVariable("id") Integer id) {
        customerAssistantService.removeCustomerAssistant(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * 设置推荐
     *
     * @param id
     * @return
     */
    @PatchMapping(value = "/customer/assistant/recommend/{id}")
    @ApiOperation(value = "设为推荐", notes = "设为推荐")
    @ApiImplicitParam(name = "id", value = "问答", dataType = "Long", required = true, paramType = "path")
    public Object recommend(@PathVariable("id") Integer id) {
        customerAssistantService.recommendCustomerAssistant(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * 批量逻辑删除客服问答
     *
     * @param ids
     * @return
     */
    @PatchMapping(value = "/customer/assistant/remove")
    @ApiOperation(value = "批量逻辑删除客服问答", notes = "批量逻辑删除客服问答")
    @ApiImplicitParam(name = "ids", value = "问答id", dataType = "Long", required = true, allowMultiple = true, paramType = "query")
    public Object batchRemove(@RequestParam List<Integer> ids) {
        customerAssistantService.batchRemoveCustomerAssistant(ids);
        return ResponseEntity.noContent().build();
    }

    /**
     * 批量发布客服问答
     *
     * @param ids
     * @return
     */
    @PatchMapping(value = "/customer/assistant/issue")
    @ApiOperation(value = "批量发布客服问答", notes = "批量发布客服问答")
    @ApiImplicitParam(name = "ids", value = "问答id", dataType = "Long", required = true, allowMultiple = true, paramType = "query")
    public Object batchRecommend(@RequestParam List<Integer> ids) {
        customerAssistantService.batchRecommendCustomerAssistant(ids);
        return ResponseEntity.noContent().build();
    }


    /**
     * 客服问答列表
     *
     * @return
     */
    @GetMapping(value = "/customer/list")
    @ApiOperation(value = "客服问答列表", notes = "客服问答列表")
    public Object listAssistant(@RequestParam(required = false) Integer terminal) {
        List<CustomerAssistantTypeDTO> assistantList = customerAssistantService.listAssistant(terminal);
        if (CollectionUtil.isNotEmpty(assistantList)) {
            return ResponseEntity.ok(assistantList);
        }
        return ResponseEntity.ok();
    }
    
    /**
     * 站务系统-帮助中心-更多-根据分类查询所有问题列表
     * @param pageNum
     * @param pageSize
     * @param typeCode
     * @return
     */
    @GetMapping(value = "/station/helpCenter/more/{typeCode}/{pageSize}/{pageNum}")
    public Object helpCenterMoreByTypeCode(@PathVariable("pageNum") Integer pageNum,
             @PathVariable("pageSize") Integer pageSize,@PathVariable("typeCode")Integer typeCode) {
    	
    	   PageVO<CustomerAssistantDTO> page = customerAssistantService.helpCenterMoreByTypeCode(pageNum,pageSize,typeCode);
           return ResponseEntity.ok(page);
    }
    
    /**
     *  站务系统-帮助中心-默认展示问题类及其推荐问题
     * @return
     */
    @GetMapping(value = "/station/helpCenter/list")
    public List<StationHelperDataDTO> helpCenterData() {
    	return customerAssistantService.helpCenterData();
    }
    
    /**
     * 站务系统-帮助中心-搜索
     * @param keywords 问题关键字
     * @return
     */
    @GetMapping(value = "/station/helpCenter/searchList/{pageSize}/{pageNum}")
    public PageVO<CustomerAssistantDTO> helpCenterQuestionSearchList(@PathVariable("pageNum") Integer pageNum,
            @PathVariable("pageSize") Integer pageSize,String keywords) {
    	return customerAssistantService.helpCenterQuestionSearchList(pageNum,pageSize,keywords);
    }
    
    /**
     * 站务系统-全局帮助与服务展示列表(展示问答分类排序第一下的前五推荐问答)
     * @return
     */
    @GetMapping("/station/helpAndService/list")
    public List<CustomerAssistantDTO> getHelpAndServiceList() {
    	return customerAssistantService.getHelpAndServiceList();
    }
}
