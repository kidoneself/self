package com.yimao.cloud.base.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;

import javax.xml.ws.WebServiceException;

import org.apache.commons.lang.StringUtils;

import net.sf.json.JSONObject;

/****
 * 百度地图api调用,每天有调用次数限制
 * @author zhangbaobao
 *
 */
public class BaiduMapUtil {
	private static String ak = "G6vAr9QDOaFzOP38IkLUFjWMvfp9P6Yn";

	/*****
	 * 根据地址获取经纬度
	 * @param address 详细地址
	 * @return
	 * @throws ConnectException
	 */
	public static Map<String, Double> getLngAndLatByAddress(String address) throws ConnectException {
		Map<String, Double> map = new HashMap<String, Double>();
		String url = "http://api.map.baidu.com/geocoder/v2/?address=" + address + "&output=json&ak=" + ak;
		String json = httpRequest(url, null);
		if (StringUtils.isNotEmpty(json)) {
			JSONObject obj = JSONObject.fromObject(json);
			String status = obj.get("status").toString();
			if ("0".equals(status)) {
				double lng = obj.getJSONObject("result").getJSONObject("location").getDouble("lng");
				double lat = obj.getJSONObject("result").getJSONObject("location").getDouble("lat");
				map.put("lng", lng);
				map.put("lat", lat);
				return map;
			} else if ("302".equals(status)) {
				throw new ConnectException("连接百度服务器次数已达上限，请明天再试。");
			}
		}
		return null;
	}

	/****
	 * 根据经纬度查询区域
	 * @param Lng
	 * @param Lat
	 * @return
	 * @throws ConnectException
	 */
	public static JSONObject getLocationByLngAndLat(String Lng, String Lat) throws ConnectException {
		String url = "http://api.map.baidu.com/geocoder/v2/?location=" + Lng + "," + Lat + "&output=json&pois=1&ak="+ ak;
		String json = httpRequest(url, null);
		if (StringUtils.isNotEmpty(json)) {
			JSONObject obj = JSONObject.fromObject(json);
			String status = obj.get("status").toString();
			if ("0".equals(status)) {
				obj = obj.getJSONObject("result").getJSONObject("addressComponent");
				if (StringUtils.isNotEmpty(obj.getString("district"))) {
					return obj;
				} else {
					return null;
				}
			} else if ("302".equals(status)) {
				throw new ConnectException("连接百度服务器次数已达上限，请明天再试。");
			}
		}
		return null;
	}

	/****
	 * http请求
	 * @param requestUrl
	 * @param requestData
	 * @return
	 * @throws WebServiceException
	 */
	private static String httpRequest(String requestUrl, String requestData) throws WebServiceException {
		BufferedReader in = null;
		PrintWriter out = null;
		StringBuffer result = new StringBuffer();
		try {
			URL url = new URL(requestUrl);
			URLConnection urlconn = url.openConnection();
			// 设置通用的请求属性
			urlconn.setRequestProperty("accept", "*/*");
			urlconn.setRequestProperty("connection", "Keep-Alive");
			urlconn.setRequestProperty("Content-Type", "text/xml; charset=utf-8");
			urlconn.setConnectTimeout(30000);
			urlconn.setReadTimeout(30000);
			// 发送POST请求必须设置如下两行
			urlconn.setDoOutput(true);
			urlconn.setDoInput(true);
			// 获取URLConnection对象对应的输出流
			out = new PrintWriter(urlconn.getOutputStream());
			out.print(requestData);
			// flush输出流的缓冲
			out.flush();
			// 定义BufferedReader输入流来读取URL的响应
			in = new BufferedReader(new InputStreamReader(urlconn.getInputStream()));
			String line;
			while ((line = in.readLine()) != null) {
				result.append(line);
			}
		} catch (Exception e) {
			throw new WebServiceException(e);
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException ex) {
				}
			}
			if (out != null) {
				out.close();
			}
		}
		return result.toString();
	}
	
	public static void main(String[] args) throws IOException {
		// testTime();
		Map<String, Double> map = getLngAndLatByAddress("上海市嘉定区沪宜公路1101号南翔智地三期越界产业园11幢A座130");// 32.068604,118.76506
		System.out.println("经度：" + map.get("lng") + "---纬度：" + map.get("lat"));
	}

}
