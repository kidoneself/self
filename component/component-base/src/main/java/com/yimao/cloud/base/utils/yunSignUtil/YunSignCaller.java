package com.yimao.cloud.base.utils.yunSignUtil;

import com.yimao.cloud.base.properties.YunSignProperties;
import com.yimao.cloud.base.utils.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.axiom.om.*;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.client.Options;
import org.apache.axis2.client.ServiceClient;
import org.apache.commons.httpclient.protocol.Protocol;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Iterator;

@Slf4j
@Component
public class YunSignCaller {

    @Resource
    private YunSignProperties yunSignProperties;

    public YunSignCaller() {
    }

    public String sign(String paramStr) {
        String appId = yunSignProperties.getServiceAppid();
        String appSecretKey = yunSignProperties.getServiceKey();
        String md5str = appId + "&" + paramStr + "&" + appSecretKey;
        return StringUtil.encodeMD5(md5str);
    }

    public static YunSignResult callYunSign(String endpoint, String query, String[] strs, String[] val) throws Exception {
        ServiceClient serviceClient = new ServiceClient();
        YunSignProtocolSocketFactory socketfactory = new YunSignProtocolSocketFactory();
        Protocol protocol = new Protocol("https", socketfactory, 443);
        Options options = serviceClient.getOptions();
        options.setProperty("CUSTOM_PROTOCOL_HANDLER", protocol);
        OMElement ret = null;
        EndpointReference targetEPR = new EndpointReference(endpoint);
        options.setTo(targetEPR);
        serviceClient.setOptions(options);
        OMFactory fac = OMAbstractFactory.getOMFactory();
        OMNamespace omNs = fac.createOMNamespace("http://wsdl.com/", query);
        OMElement eleData = fac.createOMElement(query, omNs);
        OMNamespace emOmNs = fac.createOMNamespace("", "");

        for(int i = 0; i < strs.length; ++i) {
            OMElement inner = fac.createOMElement(strs[i], emOmNs);
            inner.setText(val[i]);
            eleData.addChild(inner);
        }

        ret = serviceClient.sendReceive(eleData);
        String result = "";
        Iterator iterator = ret.getChildElements();

        while(iterator.hasNext()) {
            OMNode omNode = (OMNode)iterator.next();
            if (omNode.getType() == 1) {
                OMElement omElement = (OMElement)omNode;
                if (omElement.getLocalName().toLowerCase().equals("return")) {
                    result = omElement.getText();
                }
            }
        }

        YunSignResult yunSignResult = YunSignResult.initFromJson(query, result);
        if (!yunSignResult.isSuccess()) {
            try {
                log.error("云签请求失败失败", new Object[]{eleData.toString()});
            } catch (Exception var18) {
                ;
            }
        }

        return yunSignResult;
    }
}
