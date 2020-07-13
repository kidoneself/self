package com.yimao.cloud.order.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.servlet.http.HttpServletRequest;

import lombok.extern.slf4j.Slf4j;
/***
 * @desc http工具类
 * @author zhangbaobao
 * @date 2019/9/24
 */
@Slf4j
public class HttpUtils {
	 /**
     * 从HTTP请求中获取微信返回信息
     */
    public static String getResponse(HttpServletRequest request) {
        InputStream inStream = null;
        ByteArrayOutputStream outSteam = null;
        try {
            inStream = request.getInputStream();
            outSteam = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len;
            while ((len = inStream.read(buffer)) != -1) {
                outSteam.write(buffer, 0, len);
            }
            outSteam.flush();
            outSteam.close();
            inStream.close();
            return new String(outSteam.toByteArray(), "utf-8");
        } catch (Exception e) {
            log.error("微信回调发生异常：" + e.getMessage(), e);
            return null;
        } finally {
            if (outSteam != null) {
                try {
                    outSteam.close();
                } catch (IOException e) {
                    log.error("微信回调关闭输出流时发生异常：");
                }
            }
            if (inStream != null) {
                try {
                    inStream.close();
                } catch (IOException e) {
                    log.error("微信回调关闭输入流时发生异常：");
                }
            }
        }
    }
}
