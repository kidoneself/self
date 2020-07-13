package com.yimao.cloud.order.utils;

import java.io.File;
import java.io.FileInputStream;
import java.security.KeyStore;

import javax.net.ssl.SSLContext;

import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import com.esotericsoftware.minlog.Log;

/***
 * @desc 微信退款需要获取双向证书工具类
 * @author zhangbaobao
 * @date 2019/9/23
 */
public class CertHttpUtil {
    
    /**
     * 加载证书
     * 
     * @param mchId 商户ID
     * @param certPath 证书位置
     * @throws Exception
     */
    public static CloseableHttpClient initCert(String mchId, String certPath) throws Exception {
        // 证书密码，默认为商户ID
        String key = mchId;
        // 证书的路径
        String path = certPath;
        // 指定读取证书格式为PKCS12
        KeyStore keyStore = KeyStore.getInstance("PKCS12");
        // 读取本机存放的PKCS12证书文件
        FileInputStream instream = new FileInputStream(new File(path));
        try {
            // 指定PKCS12的密码(商户ID)
            keyStore.load(instream, key.toCharArray());
        }catch(Exception e){
        	Log.error("=======加载证书异常==========+"+e.getMessage());
        	throw new Exception("加载证书异常");
        } finally {
            instream.close();
        }
        @SuppressWarnings("deprecation")
		SSLContext sslcontext = SSLContexts.custom().loadKeyMaterial(keyStore, key.toCharArray()).build();
        @SuppressWarnings("deprecation")
		SSLConnectionSocketFactory sslsf =
                new SSLConnectionSocketFactory(sslcontext, new String[] {"TLSv1"}, null,
                        SSLConnectionSocketFactory.BROWSER_COMPATIBLE_HOSTNAME_VERIFIER);
        CloseableHttpClient httpClient = HttpClients.custom().setSSLSocketFactory(sslsf).build();
		return httpClient;
    }
}
