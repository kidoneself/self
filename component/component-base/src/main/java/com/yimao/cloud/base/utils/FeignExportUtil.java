package com.yimao.cloud.base.utils;

import feign.Response;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;

/**
 * @author Chen Hui Yang
 * @date 2019/3/19
 */
public class FeignExportUtil {

    /**
     *
     * @param response
     * @param name excel表名称
     * @return
     */
    public static ResponseEntity<byte[]> export(Response response ,String name){
        ResponseEntity<byte[]> result = null;
        InputStream inputStream = null;
        try {
            Response.Body body = response.body();
            inputStream = body.asInputStream();
            byte[] b = new byte[inputStream.available()];
            inputStream.read(b);
            HttpHeaders heads = new HttpHeaders();
            heads.add(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename="+ URLEncoder.encode(name + ".xls", "UTF-8"));
            heads.add(HttpHeaders.CONTENT_TYPE, "application/octet-stream");
            result = new ResponseEntity<byte[]>(b, heads, HttpStatus.OK);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }

}
