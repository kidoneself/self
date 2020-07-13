package com.yimao.cloud.system.controller;

import com.yimao.cloud.base.constant.RabbitConstant;
import com.yimao.cloud.base.enums.ExportRecordStatus;
import com.yimao.cloud.base.msg.CommResult;
import com.yimao.cloud.framework.aop.annotation.ExecutionTime;
import com.yimao.cloud.pojo.dto.system.ExportRecordDTO;
import com.yimao.cloud.pojo.dto.user.UserChangeRecordListDTO;
import com.yimao.cloud.pojo.dto.user.UserContidionDTO;
import com.yimao.cloud.pojo.dto.user.UserDTO;
import com.yimao.cloud.pojo.dto.user.UserOverviewDTO;
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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @description: 用户相关
 * @author: yu chunlei
 * @create: 2019-03-07 15:40:23
 **/
@RestController
@Slf4j
@Api(tags = "UserController")
public class UserController {

    @Resource
    private UserFeign userFeign;

    @Resource
    private ExportRecordService exportRecordService;
    @Resource
    private RabbitTemplate rabbitTemplate;


    /**
     * @param pageSize
     * @param query
     * @Description: 业务管理系统-用户列表
     * @author ycl
     * @param: pageNum
     * @Return: org.springframework.http.ResponseEntity<com.yimao.cloud.pojo.vo.PageVO       <       com.yimao.cloud.pojo.dto.user.UserDTO>>
     * @Create: 2019/3/7 15:54
     */
    @PostMapping(value = "/user/{pageNum}/{pageSize}")
    @ApiOperation(value = "分页查询用户信息", notes = "分页查询用户信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "分页页码", defaultValue = "1", required = true, dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "pageSize", value = "分页步长", defaultValue = "10", required = true, dataType = "Long", paramType = "path")
    })
    public ResponseEntity<PageVO<UserDTO>> pageQueryUser(@PathVariable(value = "pageNum") Integer pageNum,
                                                         @PathVariable(value = "pageSize") Integer pageSize,
                                                         @RequestBody UserContidionDTO query) {
        PageVO<UserDTO> pageVO = userFeign.pageQueryUser(pageNum, pageSize, query);
        return ResponseEntity.ok(pageVO);
    }

    /**
     * 管理系统-用户详细信息
     *
     * @param id
     * @return
     */
    @GetMapping(value = "/user/distributor/{id}")
    @ApiOperation(value = "根据用户ID查询用户信息", notes = "根据用户ID查询用户信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "用户ID", required = true, dataType = "Long", paramType = "path")
    })
    public ResponseEntity<UserDTO> getUserInfo(@PathVariable(value = "id") Integer id) {
        return ResponseEntity.ok(userFeign.getUserInfo(id));
    }


    /**
     * @Description: 用户列表-解绑手机号
     * @author ycl
     * @param: userId
     * @Return: org.springframework.http.ResponseEntity
     * @Create: 2019/3/7 16:01
     */
    @PatchMapping(value = "/user/unBindPhone/{userId}")
    @ApiOperation(value = "解绑用户手机号", notes = "解绑用户手机号")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "用户ID", required = true, dataType = "Long", paramType = "path")
    })
    public ResponseEntity unBindPhone(@PathVariable Integer userId) {
        userFeign.unBindPhone(userId);
        return ResponseEntity.noContent().build();
    }


    /**
     * @Description: 用户信息变更记录
     * @author ycl
     * @param: userId
     * @Return: org.springframework.http.ResponseEntity<java.util.List       <       com.yimao.cloud.pojo.dto.user.UserChangeRecordDTO>>
     * @Create: 2019/3/7 16:03
     */
    @GetMapping("/user/record/{userId}")
    @ApiOperation(value = "用户信息变更记录", notes = "用户信息变更记录")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "用户ID", required = true, dataType = "Long", paramType = "path")
    })
    public ResponseEntity<UserChangeRecordListDTO> getUserChangeRecord(@PathVariable("userId") Integer userId) {
        UserChangeRecordListDTO record = userFeign.getUserChangeRecord(userId);
        return ResponseEntity.ok(record);
    }

    /**
     * @Description: 用户列表编辑
     * @author ycl
     * @param: dto
     * @Return: org.springframework.http.ResponseEntity
     * @Create: 2019/3/27 18:14
     */
    @PutMapping(value = "/user")
    @ApiOperation(value = "更新用户信息")
    @ApiImplicitParam(name = "dto", value = "用户信息", required = true, dataType = "UserDTO", paramType = "body")
    public void updateUser(@RequestBody UserDTO dto) {
        userFeign.updateUser(dto);
    }

    /**
     * 用户概况
     *
     * @return org.springframework.http.ResponseEntity
     * @author hhf
     * @date 2019/3/19
     */
    @ExecutionTime
    @GetMapping("/user/overview")
    @ApiOperation(value = "用户概况", notes = "用户概况")
    //@ApiImplicitParam(name = "userId", value = "用户ID", required = true, dataType = "Long", paramType = "path")
    public ResponseEntity<UserOverviewDTO> exit1() {
        UserOverviewDTO userOverviewDTO = userFeign.overview();
        return ResponseEntity.ok(userOverviewDTO);
    }

    @GetMapping(value = "/user")
    @ApiOperation(value = "站长模糊搜索", notes = "站长模糊搜索")
    public List<UserDTO> getUserByUserName(@RequestParam String username) {
        return userFeign.getUserByUserName(username);
    }


    //用户列表导出
    @PostMapping(value = "/user/export")
    @ApiOperation(value = "用户列表导出")
    @ApiImplicitParam(name = "query", value = "查询信息", dataType = "UserContidionDTO", paramType = "body")
    public Object userExport(@RequestBody UserContidionDTO query) {
        //保存导出记录，如果短时间操作多次，会提示操作过于频繁
        String url = "/user/export";
        ExportRecordDTO record = exportRecordService.save(url, "用户列表");

        //导出记录添加成功，并且状态为"等待导出"，才入列
        if (record.getStatus() == ExportRecordStatus.WAITING.value) {
            //异步队列执行导出
            Map<String, Object> map = new HashMap<>();
            map.put("exportRecordDTO", record);
            map.put("query", query);
            rabbitTemplate.convertAndSend(RabbitConstant.EXPORT_ACTION_USER, map);
        }
        return CommResult.ok(record.getId());
    }


    /**
     * @Author ycl
     * @Description 管理系统--解绑微信
     * @Date 13:23 2019/8/12
     * @Parfam
     **/
    @GetMapping(value = "/user/bind/wechat")
    @ApiOperation(value = "管理系统--解绑微信")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "用户ID", required = true, dataType = "Long", paramType = "query")
    })
    public ResponseEntity unBindWechat(@RequestParam(value = "userId") Integer userId) {
        return ResponseEntity.ok(userFeign.unBindWechat(userId));
    }


    /**
     * @Author ycl
     * @Description 管理系统--更换健康大使
     * @Date 15:58 2019/8/12
     * @Param
     **/
    @GetMapping(value = "/user/change/ambassador")
    @ApiOperation(value = "管理系统--更换健康大使")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "用户ID", required = true, dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "ambassadorId", value = "健康大使ID", required = true, dataType = "Long", paramType = "query")
    })
    public ResponseEntity changeAmbassador(@RequestParam(value = "userId") Integer userId,
                                           @RequestParam(value = "ambassadorId") Integer ambassadorId) {
        userFeign.changeAmbassador(userId, ambassadorId);
        return ResponseEntity.noContent().build();
    }


}
