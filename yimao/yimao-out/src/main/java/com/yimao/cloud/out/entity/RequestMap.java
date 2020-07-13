package com.yimao.cloud.out.entity;

import com.alibaba.fastjson.JSONObject;
import lombok.Getter;
import lombok.Setter;

/**
 * 描述：TODO
 *
 * @Author Zhang Bo
 * @Date 2019/3/6 16:50
 * @Version 1.0
 */
@Getter
@Setter
public class RequestMap {

    // 请求的回话标识
    private String reqToken;
    // 请求的信息密文
    private String reqInfo;
    // 请求的密钥
    private String reqSecret;
    // 请求参数明文
    private JSONObject reqMap;

}
