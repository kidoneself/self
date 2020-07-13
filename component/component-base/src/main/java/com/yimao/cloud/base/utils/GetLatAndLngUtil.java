package com.yimao.cloud.base.utils;

import com.yimao.cloud.base.constant.Constant;
import net.sf.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;

/**
 * 获取经纬度
 * 密钥:这里的密钥是在baidu的后台申请的服务端的key
 */
public class GetLatAndLngUtil {
    /**
     * @param addr 查询的地址
     * @return
     * @throws IOException
     */
    public static Object[] getCoordinate(String addr) throws IOException {
        String lng = null;// 经度
        String lat = null;// 纬度
        String address = null;
        try {
            address = java.net.URLEncoder.encode(addr, "UTF-8");
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        }
        String key = "我靠你大爷";
        String url = String.format("http://api.map.baidu.com/geocoder?address=%s&output=json&key=%s", address, key);
        URL myURL = null;
        URLConnection httpsConn = null;
        try {
            myURL = new URL(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        InputStreamReader insr = null;
        BufferedReader br = null;
        try {
            httpsConn = (URLConnection) myURL.openConnection();// 不使用代理
            if (httpsConn != null) {
                insr = new InputStreamReader(httpsConn.getInputStream(), "UTF-8");
                br = new BufferedReader(insr);
                String data = null;
                int count = 1;
                while ((data = br.readLine()) != null && (!data.equals("INVALID_PARAMETERS"))) {
                    if (count == 5) {
                        lng = (String) data.subSequence(data.indexOf(":") + 1, data.indexOf(","));// 经度
                        count++;
                    } else if (count == 6) {
                        lat = data.substring(data.indexOf(":") + 1);// 纬度
                        count++;
                    } else {
                        count++;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (insr != null) {
                insr.close();
            }
            if (br != null) {
                br.close();
            }
        }
        return new Object[]{lng, lat};
    }

    private static double rad(double d) {
        return d * Math.PI / 180.0;
    }

    public static double getDistance(double long1, double lat1, double long2, double lat2) {
        double a, b, d, sa2, sb2;
        lat1 = rad(lat1);
        lat2 = rad(lat2);
        a = lat1 - lat2;
        b = rad(long1 - long2);

        sa2 = Math.sin(a / 2.0);
        sb2 = Math.sin(b / 2.0);
        d = 2 * Constant.EARTH_RADIUS
                * Math.asin(Math.sqrt(sa2 * sa2 + Math.cos(lat1)
                * Math.cos(lat2) * sb2 * sb2));
        return d;
    }

    public static void main(String[] args) throws IOException {
        GetLatAndLngUtil getLatAndLngUtil = new GetLatAndLngUtil();
        Object[] o = getLatAndLngUtil.getCoordinate("上海市嘉定区南翔镇银翔路中暨大厦");
        System.out.println(o[1]);
        System.out.println(o[0]);

        Map<String, Double> map = BaiduMapUtil.getLngAndLatByAddress("上海市嘉定区南翔镇银翔路中暨大厦");
        System.out.println("map = " + map);
        JSONObject json = BaiduMapUtil.getLocationByLngAndLat("121.325874", "31.294193");
        System.out.println("json = " + json);

        double distance = getDistance(121.325874, 31.294193, 111.74746, 30.429651);
        System.out.println("distance = " + distance);
    }

}
