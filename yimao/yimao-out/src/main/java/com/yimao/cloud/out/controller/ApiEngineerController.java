package com.yimao.cloud.out.controller;

import com.alibaba.fastjson.JSONObject;
import com.yimao.cloud.base.utils.RestTemplateUtil;
import com.yimao.cloud.out.entity.RequestMap;
import com.yimao.cloud.out.entity.ResponseMap;
import com.yimao.cloud.out.utils.RequestDataUtil;
import com.yimao.cloud.out.utils.ResponseDataUtil;
import com.yimao.cloud.pojo.dto.user.EngineerDTO;
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

/**
 * 安装工信息
 *
 * @author Zhang Bo
 * @date 2017/12/15.
 */
@RestController
@Slf4j
@Api(tags = "ApiEngineerController")
public class ApiEngineerController {

    // private static final String url = "http://service.yimaokeji.com:81";
    private static final String url = "http://192.168.60.23:16007";

    /**
     * 同步安装工信息到售后系统
     *
     * @param type     创建更新删除：1-创建；2-删除；3-更新
     * @param engineer
     * @return
     */
    @PostMapping(value = "/engineer/sync/{type}")
    @ApiOperation(value = "同步安装工信息到售后系统", notes = "同步安装工信息到售后系统")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "type", value = "类型", required = true, dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "engineer", value = "安装工信息", required = true, dataType = "EngineerDTO", paramType = "body")
    })
    public ResponseEntity<Object> syncEngineer(@PathVariable Integer type, @RequestBody EngineerDTO engineer) {
        JSONObject data = new JSONObject();
        if (type == 2) {
            data.put("type", "2");
            data.put("id", "");
        } else {
            if (type == 1) {
                data.put("type", "1");
            } else {
                data.put("type", "3");
            }

            data.put("id", engineer.getOldId());
            data.put("province", engineer.getProvince());
            data.put("city", engineer.getCity());
            data.put("region", engineer.getRegion());
            // map.put("siteId", engineer.getStationId());//TODO
            data.put("siteName", engineer.getStationName());
            data.put("idCard", engineer.getWorkId());
            if (engineer.getSex() == 1) {
                data.put("sex", "M");
            } else {
                data.put("sex", "F");
            }
            if (engineer.getForbidden()) {
                data.put("forbidden", "1");
            } else {
                data.put("forbidden", "0");
            }
            data.put("loginName", engineer.getUserName());
            data.put("realName", engineer.getRealName());
            data.put("phone", engineer.getPhone());
        }
        //加密请求数据
        JSONObject req = RequestDataUtil.encryptRequestData(data);
        JSONObject resp = RestTemplateUtil.post(url + "/api/engineer/synchronizeEngineer", req);
        //解密返回数据
        JSONObject respData = ResponseDataUtil.decryptResponseData(resp);
        System.out.println("解密后的返回值为：" + respData);
        return ResponseEntity.noContent().build();
    }

    /**
     * 测试发送请求给售后系统
     *
     * @param code 工单号
     */
    @GetMapping(value = "/test")
    @ApiOperation(value = "测试发送请求给售后系统", notes = "测试发送请求给售后系统")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "code", value = "类型", required = true, dataType = "String", paramType = "query")
    })
    public ResponseEntity<Object> test(@RequestParam String code, @RequestParam String url) {
        JSONObject data = new JSONObject();
        data.put("code", code);
        //加密请求数据
        JSONObject req = RequestDataUtil.encryptRequestData(data);
        System.out.println("req=" + req);
        JSONObject resp = RestTemplateUtil.post(url, req);
        System.out.println("发送方接收到的接口返回为：" + resp);
        //解密返回数据
        JSONObject respData = ResponseDataUtil.decryptResponseData(resp);
        System.out.println("解密后的返回值为：" + respData);
        return ResponseEntity.noContent().build();
    }

    /**
     * 模拟售后系统接口接收请求
     *
     * @param data 售后系统封装的请求参数
     */
    @PostMapping(value = "/test/response")
    @ApiOperation(value = "模拟接口", notes = "模拟接口")
    @ApiImplicitParam(name = "requestMap", value = "共通请求参数", required = true, dataType = "RequestMap", paramType = "body")
    public ResponseEntity<Object> response(@RequestBody RequestMap data) {
        //解密请求参数
        RequestDataUtil.decryptRequestData(data);
        JSONObject req = data.getReqMap();
        System.out.println("接收方接收到的接口请求参数为：" + req.toString());
        ResponseMap resp = ResponseMap.getInstance();
        resp.setRtnInfo("我接收到请求了！！！");
        //加密返回数据
        ResponseDataUtil.encryptResponseData(resp);
        return ResponseEntity.ok(resp);
    }

}
