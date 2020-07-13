package com.yimao.cloud.base.utils;

import com.alibaba.fastjson.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

/**
 * 描述：RestTemplate工具类
 *
 * @Author Zhang Bo
 * @Date 2019/3/7 14:26
 * @Version 1.0
 */
public class RestTemplateUtil {

    private static RestTemplate restTemplate = new RestTemplate();

    /**
     * POST请求，接收方使用@RequestBody形式接收请求参数
     *
     * @param url  请求地址
     * @param data 请求参数
     */
    public static JSONObject post(String url, JSONObject data) {
        //一定要设置header
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        //Payload方式，提交的内容一定要是String
        HttpEntity<String> requestEntity = new HttpEntity<>(data.toString(), headers);
        return restTemplate.postForEntity(url, requestEntity, JSONObject.class).getBody();
    }
}
