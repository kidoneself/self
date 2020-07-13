package com.yimao.cloud.base.utils;

import com.yimao.cloud.base.constant.WechatConstant;
import org.apache.commons.codec.digest.DigestUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.Map;
import java.util.Objects;
import java.util.SortedMap;
import java.util.TreeMap;


public class WXPayUtil {

    /**
     * XML格式字符串转换为Map
     *
     * @param strXML XML字符串
     * @return XML数据转换后的Map
     * @throws Exception
     */
    public static SortedMap<String, String> xmlToMap(String strXML) throws Exception {
//        Map<String, String> data = new HashMap<>();
        SortedMap<String, String> data = new TreeMap<>();
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        // 微信安全隐患XML外部实体注入漏洞应对  2018-07-04
        documentBuilderFactory.setExpandEntityReferences(false);
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        InputStream stream = new ByteArrayInputStream(strXML.getBytes("UTF-8"));
        Document doc = documentBuilder.parse(stream);
        doc.getDocumentElement().normalize();
        NodeList nodeList = doc.getDocumentElement().getChildNodes();
        for (int idx = 0; idx < nodeList.getLength(); ++idx) {
            Node node = nodeList.item(idx);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) node;
                data.put(element.getNodeName(), element.getTextContent());
            }
        }
        try {
            stream.close();
        } catch (Exception ex) {
            // do nothing
        }
        return data;

    }

    /**
     * 将Map转换为XML格式的字符串
     *
     * @param data Map类型数据
     * @return XML格式的字符串
     * @throws Exception
     */
    public static String mapToXml(SortedMap<String, Object> data) throws Exception {
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        // 微信安全隐患XML外部实体注入漏洞应对  2018-07-04
        documentBuilderFactory.setExpandEntityReferences(false);
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        Document document = documentBuilder.newDocument();
        Element root = document.createElement("xml");
        document.appendChild(root);
        for (String key : data.keySet()) {
            Object value = data.get(key);
            if (value == null) {
                value = "";
            }
            value = value.toString().trim();
            Element filed = document.createElement(key);
            filed.appendChild(document.createTextNode(value.toString()));
            root.appendChild(filed);
        }
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer = tf.newTransformer();
        DOMSource source = new DOMSource(document);
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        StringWriter writer = new StringWriter();
        StreamResult result = new StreamResult(writer);
        transformer.transform(source, result);
        String output = writer.getBuffer().toString();
        try {
            writer.close();
        } catch (Exception ex) {
        }
        return output;
    }

    /**
     * @param map
     * @return
     * @throws Exception
     */
    private static String mapToString(SortedMap<String, Object> map) {
        StringBuffer sign = new StringBuffer();
        boolean isNotFirst = false;

        for (Map.Entry<String, Object> entry : map.entrySet()) {
            if (entry.getValue() != null) {
                if (isNotFirst) {
                    sign.append("&");
                } else {
                    isNotFirst = true;
                }
                sign.append(entry.getKey()).append("=").append(entry.getValue());
            }
        }

        return sign.toString();
    }

    /**
     * 签名
     *
     * @param signMap
     * @return
     * @throws Exception
     */
    public static String createSign(SortedMap<String, Object> signMap, boolean useKey, String signType, String key) {

        String sign = mapToString(signMap);
        if (useKey) {
            sign += "&key=" + key;
        }

        if (Objects.equals(WechatConstant.SHA1, signType)) {
            return DigestUtils.sha1Hex(sign).toUpperCase();
        } else {
            return DigestUtils.md5Hex(sign).toUpperCase();
        }
    }


    /**
     * 生成收到支付结果的确认信息
     *
     * @param returnCode 返回状态码
     * @param returnMsg  返回信息
     * @return
     * @throws Exception
     */
    public static String getPayCallback(String returnCode, String returnMsg) {
        return "<xml>"
                + "<return_code><![CDATA[" + returnCode + "]]></return_code>"
                + "<return_msg><![CDATA[" + returnMsg + "]]></return_msg>"
                + "</xml>";
    }

    /**
     * 微信支付回调签名校验
     *
     * @param map
     * @param key
     * @return
     */
    public static boolean signValid(SortedMap<String, String> map, String key) {
        StringBuffer signBuffer = new StringBuffer();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            if (!Objects.equals(entry.getKey(), "sign") && !Objects.equals(entry.getKey(), "key") && StringUtil.isNotEmpty(entry.getValue())) {
                signBuffer.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
            }
        }
        signBuffer.append("key=").append(key);
        String sign = signBuffer.toString();
        sign = DigestUtils.md5Hex(sign).toUpperCase();
        String validSign = map.get("sign").toUpperCase();
        return Objects.equals(sign, validSign);
    }
}
