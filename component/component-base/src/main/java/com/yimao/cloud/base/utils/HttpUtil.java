package com.yimao.cloud.base.utils;

import com.alibaba.fastjson.JSONObject;
import com.yimao.cloud.base.entity.FileItem;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * HTTP请求工具类
 *
 * @author zhangqiang
 */
public class HttpUtil {

    private static Log logger = LogFactory.getLog(HttpUtil.class);

    /**
     * Get方式获取数据
     *
     * @param url GET方式URL
     */
    public static String executeGet(String url) throws Exception {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(url);
        try (CloseableHttpResponse response = httpclient.execute(httpGet)) {
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                HttpEntity entity = response.getEntity();
                BufferedReader reader = new BufferedReader(new InputStreamReader(entity.getContent(), "UTF-8"));
                StringBuilder sb = new StringBuilder();
                for (String temp = reader.readLine(); temp != null; temp = reader.readLine()) {
                    sb.append(temp);
                }
                return sb.toString().trim();
            }
            return null;
        } catch (Exception e) {
            logger.debug("调用GET接口数据时发生异常：" + e.getMessage(), e);
            throw new Exception(e.getMessage());
        }
    }

    /**
     * 发送JSON数据，POST方式
     *
     * @param data
     */
    public static String postJSONData(String url, JSONObject data) throws Exception {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(url);
        String inputParam = data.toJSONString();
        logger.debug("POST接口发送内容：" + inputParam);
        httpPost.setEntity(new StringEntity(inputParam, "UTF-8"));
        CloseableHttpResponse response = null;

        String result = null;
        try {
            response = httpclient.execute(httpPost);
            result = EntityUtils.toString(response.getEntity(), "UTF-8");
            logger.debug("发送POST接口数据返回内容：" + result);
        } catch (Exception ex) {
            logger.debug("调用POST接口数据时发生异常：" + ex.getMessage(), ex);
            throw new Exception(ex.getMessage());
        } finally {
            if (null != response) {
                try {
                    response.close();
                } catch (Exception e) {
                }
            }
        }
        return result;
    }


    /**
     * 发送POST请求参数
     *
     * @param url
     * @param paramMap
     * @return
     * @throws Exception
     */
    public static String postData(String url, Map<String, String> paramMap) throws Exception {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpResponse response = null;
        String result = null;
        try {
            // 构造请求
            HttpPost httpPost = new HttpPost(url);
            // 封装参数
            List<BasicNameValuePair> nvps = new ArrayList<BasicNameValuePair>();
            if (null != paramMap) {
                for (Entry<String, String> item : paramMap.entrySet()) {
                    nvps.add(new BasicNameValuePair(item.getKey(), item.getValue()));
                }
            }
            httpPost.setEntity(new UrlEncodedFormEntity(nvps, "utf-8"));
            // 发起请求
            response = httpClient.execute(httpPost);
            // 获取响应数据
            result = EntityUtils.toString(response.getEntity(), Charset.forName("UTF-8"));
            logger.debug("接收到的POST响应内容：" + result);
        } catch (Exception ex) {
            String msg = "POST请求发送失败:" + ex.getMessage();
            logger.error(msg, ex);
            throw new Exception(ex.getMessage());
        } finally {
            try {
                response.close();
                httpClient.close();
            } catch (Exception e) {
            }
        }
        return result;
    }
    /**
     * httpClient post 发送文件
     *
     * @param url
     * @param fileItems
     * @param paramMap
     * @return
     * @throws Exception
     */
    public static String postFile(String url, List<FileItem> fileItems, Map<String, String> paramMap) throws Exception {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpResponse response = null;
        try {
            HttpPost httpPost = new HttpPost(url);

            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
            if (CollectionUtil.isNotEmpty(fileItems)) {
                for (FileItem item : fileItems) {
//                    builder.addBinaryBody(item.getName(), item.getInputStream(), ContentType.create("multipart/form-data", "UTF-8"), item.getFileName());// 文件流
                    builder.addBinaryBody(item.getFieldName(), item.getFile());// 文件流
                }
            }
            if (paramMap != null) {
                for (Entry<String, String> entry : paramMap.entrySet()) {
                    builder.addTextBody(entry.getKey(), entry.getValue(), ContentType.create("multipart/form-data", "UTF-8"));
                }
            }

            HttpEntity entity = builder.build();
            httpPost.setEntity(entity);
            response = httpClient.execute(httpPost);// 执行提交
            HttpEntity responseEntity = response.getEntity();
            if (responseEntity != null) {
                // 将响应内容转换为字符串
                return EntityUtils.toString(responseEntity, Charset.forName("UTF-8"));
            }
            return null;
        } catch (Exception e) {
            String msg = "POST请求发送失败:" + e.getMessage();
            logger.error(msg, e);
            throw new Exception(e.getMessage());
        } finally {
            try {
                if (response != null) {
                    response.close();
                }
                httpClient.close();
            } catch (IOException e) {
            }
        }
    }




}
