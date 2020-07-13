// package com.yimao.cloud.water.utils;
//
// import com.sun.xml.wss.ProcessingContext;
// import com.sun.xml.wss.XWSSProcessor;
// import com.sun.xml.wss.XWSSProcessorFactory;
// import com.sun.xml.wss.XWSSecurityException;
// import com.sun.xml.wss.impl.callback.PasswordCallback;
// import com.sun.xml.wss.impl.callback.UsernameCallback;
// import com.yimao.cloud.base.utils.StringUtil;
// import com.yimao.cloud.pojo.dto.water.SimCardDTO;
// import com.yimao.cloud.water.enums.SimStatus;
// import lombok.extern.slf4j.Slf4j;
// import org.w3c.dom.Node;
// import org.w3c.dom.NodeList;
//
// import javax.security.auth.callback.Callback;
// import javax.security.auth.callback.CallbackHandler;
// import javax.security.auth.callback.UnsupportedCallbackException;
// import javax.xml.soap.MessageFactory;
// import javax.xml.soap.Name;
// import javax.xml.soap.SOAPBodyElement;
// import javax.xml.soap.SOAPConnection;
// import javax.xml.soap.SOAPConnectionFactory;
// import javax.xml.soap.SOAPElement;
// import javax.xml.soap.SOAPEnvelope;
// import javax.xml.soap.SOAPException;
// import javax.xml.soap.SOAPFault;
// import javax.xml.soap.SOAPMessage;
// import java.io.IOException;
// import java.io.InputStream;
// import java.net.MalformedURLException;
// import java.net.URL;
// import java.net.URLConnection;
// import java.net.URLStreamHandler;
//
// /**
//  * 描述：SIM卡工具类。
//  *
//  * @Author Zhang Bo
//  * @Date 2019/4/26
//  */
// @Slf4j
// public class SimCardUtil {
//
//     private static final String WSDL_TERMINAL_URL = "https://api.10646.cn/ws/service/terminal";
//     private static final String NAMESPACE_URI = "http://api.jasperwireless.com/ws/schema";
//     private static final String EDIT_TERMINAL_URI = "http://api.jasperwireless.com/ws/service/terminal/EditTerminal";
//     private static final String TERMINAL_DETAIL_URI = "http://api.jasperwireless.com/ws/service/terminal/GetTerminalDetails";
//     private static final String SIM_CHANGE_TYPE = "3";
//     private static final String SIM_TARGET_VALUE = "ACTIVATED_NAME";
//     private static final String PREFIX = "sch";
//     private static final String SIM_DEACTIVATED_VALUE = "DEACTIVATED_NAME";
//
//     public static boolean deactivatedTerminal(String iccid, String licensekey, String username, String password) {
//         SOAPConnection connection = null;
//         try {
//             SOAPMessage request = createEditTerminalRequest(iccid, SIM_DEACTIVATED_VALUE, SIM_CHANGE_TYPE, licensekey);
//             request = secureMessage(request, username, password);
//             SOAPConnectionFactory connectionFactory = SOAPConnectionFactory.newInstance();
//             connection = connectionFactory.createConnection();
//             URL endpoint = createURL();
//             SOAPMessage response = connection.call(request, endpoint);
//             if (!response.getSOAPBody().hasFault()) {
//                 return true;
//             } else {
//                 SOAPFault fault = response.getSOAPBody().getFault();
//                 System.err.println("Received SOAP Fault");
//                 System.err.println("SOAP Fault Code :" + fault.getFaultCode());
//             }
//         } catch (Exception e) {
//             log.error(e.getMessage(), e);
//         } finally {
//             try {
//                 if (null != connection) {
//                     connection.close();
//                 }
//             } catch (SOAPException se) {
//                 log.error(se.getMessage(), se);
//             }
//         }
//         return false;
//     }
//
//     public static boolean callEditTerminal(String iccid, String licensekey, String username, String password) {
//         log.info("sim卡激活：" + iccid);
//         SOAPConnection connection = null;
//         try {
//             SOAPMessage request = createEditTerminalRequest(iccid, SIM_TARGET_VALUE, SIM_CHANGE_TYPE, licensekey);
//             request = secureMessage(request, username, password);
//             SOAPConnectionFactory connectionFactory = SOAPConnectionFactory.newInstance();
//             connection = connectionFactory.createConnection();
//             URL endpoint = createURL();
//             SOAPMessage response = connection.call(request, endpoint);
//             if (!response.getSOAPBody().hasFault()) {
//                 return true;
//             } else {
//                 SOAPFault fault = response.getSOAPBody().getFault();
//                 System.err.println("Received SOAP Fault" + fault.getFaultString());
//                 System.err.println("SOAP Fault Code :" + fault.getFaultCode());
//             }
//         } catch (Exception e) {
//             log.error(e.getMessage(), e);
//             return false;
//         } finally {
//             try {
//                 if (connection != null) {
//                     connection.close();
//                 }
//             } catch (SOAPException se) {
//                 log.error(se.getMessage(), se);
//             }
//         }
//         return false;
//     }
//
//     /**
//      * 查询SIM卡信息
//      *
//      * @param iccid
//      * @param licensekey
//      * @param username
//      * @param password
//      * @return
//      */
//     public static SimCardDTO callGetDetail(String iccid, String licensekey, String username, String password) {
//         SimCardDTO simCard = new SimCardDTO();
//         SOAPConnection connection = null;
//
//         try {
//             SOAPMessage request = createGetDetailRequest(iccid, licensekey);
//             request = secureMessage(request, username, password);
//             SOAPConnectionFactory connectionFactory = SOAPConnectionFactory.newInstance();
//             connection = connectionFactory.createConnection();
//             URL endpoint = createURL();
//             SOAPMessage response = connection.call(request, endpoint);
//             if (!response.getSOAPBody().hasFault()) {
//                 writeTerminalResponse(response, simCard);
//             } else {
//                 SOAPFault fault = response.getSOAPBody().getFault();
//                 System.err.println("Received SOAP Fault");
//                 System.err.println("SOAP Fault Code :" + fault.getFaultCode());
//             }
//         } catch (Exception e) {
//             log.error(e.getMessage(), e);
//         } finally {
//             try {
//                 if (null != connection) {
//                     connection.close();
//                 }
//             } catch (SOAPException se) {
//                 log.error(se.getMessage(), se);
//             }
//
//         }
//
//         return simCard;
//     }
//
//     private static URL createURL() throws MalformedURLException {
//         return new URL(null, WSDL_TERMINAL_URL, new URLStreamHandler() {
//             @Override
//             protected URLConnection openConnection(URL url) throws IOException {
//                 URL target = new URL(url.toString());
//                 URLConnection connection = target.openConnection();
//                 connection.setConnectTimeout(6000);
//                 connection.setReadTimeout(6000);
//                 return connection;
//             }
//         });
//     }
//
//     private static SOAPMessage createEditTerminalRequest(String iccid, String targetValue, String changeType, String licensekey) throws SOAPException {
//         MessageFactory messageFactory = MessageFactory.newInstance();
//         SOAPMessage message = messageFactory.createMessage();
//         message.getMimeHeaders().addHeader("SOAPAction", EDIT_TERMINAL_URI);
//         SOAPEnvelope envelope = message.getSOAPPart().getEnvelope();
//         Name terminalRequestName = envelope.createName("EditTerminalRequest", PREFIX, NAMESPACE_URI);
//         SOAPBodyElement terminalRequestElement = message.getSOAPBody().addBodyElement(terminalRequestName);
//         Name msgId = envelope.createName("messageId", PREFIX, NAMESPACE_URI);
//         SOAPElement msgElement = terminalRequestElement.addChildElement(msgId);
//         msgElement.setValue("TCE-100-ABC-34084");
//         Name version = envelope.createName("version", PREFIX, NAMESPACE_URI);
//         SOAPElement versionElement = terminalRequestElement.addChildElement(version);
//         versionElement.setValue("1.0");
//         Name license = envelope.createName("licenseKey", PREFIX, NAMESPACE_URI);
//         SOAPElement licenseElement = terminalRequestElement.addChildElement(license);
//         licenseElement.setValue(licensekey);
//         Name iccidName = envelope.createName("iccid", PREFIX, NAMESPACE_URI);
//         SOAPElement iccidElement = terminalRequestElement.addChildElement(iccidName);
//         iccidElement.setValue(iccid);
//         Name target = envelope.createName("targetValue", PREFIX, NAMESPACE_URI);
//         SOAPElement targetElement = terminalRequestElement.addChildElement(target);
//         targetElement.setValue(targetValue);
//         Name changetype = envelope.createName("changeType", PREFIX, NAMESPACE_URI);
//         SOAPElement changetypeElement = terminalRequestElement.addChildElement(changetype);
//         changetypeElement.setValue(changeType);
//         return message;
//     }
//
//     private static SOAPMessage createGetDetailRequest(String iccid, String licensekey) throws SOAPException {
//         MessageFactory messageFactory = MessageFactory.newInstance();
//         SOAPMessage message = messageFactory.createMessage();
//         message.getMimeHeaders().addHeader("SOAPAction", TERMINAL_DETAIL_URI);
//         SOAPEnvelope envelope = message.getSOAPPart().getEnvelope();
//         Name terminalRequestName = envelope.createName("GetTerminalDetailsRequest", PREFIX, NAMESPACE_URI);
//         SOAPBodyElement terminalRequestElement = message.getSOAPBody().addBodyElement(terminalRequestName);
//         Name msgId = envelope.createName("messageId", PREFIX, NAMESPACE_URI);
//         SOAPElement msgElement = terminalRequestElement.addChildElement(msgId);
//         msgElement.setValue("TCE-100-ABC-34084");
//         Name version = envelope.createName("version", PREFIX, NAMESPACE_URI);
//         SOAPElement versionElement = terminalRequestElement.addChildElement(version);
//         versionElement.setValue("1.0");
//         Name license = envelope.createName("licenseKey", PREFIX, NAMESPACE_URI);
//         SOAPElement licenseElement = terminalRequestElement.addChildElement(license);
//         licenseElement.setValue(licensekey);
//         Name iccids = envelope.createName("iccids", PREFIX, NAMESPACE_URI);
//         SOAPElement iccidsElement = terminalRequestElement.addChildElement(iccids);
//         Name iccidName = envelope.createName("iccid", PREFIX, NAMESPACE_URI);
//         SOAPElement iccidElement = iccidsElement.addChildElement(iccidName);
//         iccidElement.setValue(iccid);
//         return message;
//     }
//
//     private static void writeTerminalResponse(SOAPMessage message, SimCardDTO simCard) throws SOAPException {
//         SOAPEnvelope envelope = message.getSOAPPart().getEnvelope();
//         Name terminalResponseName = envelope.createName("GetTerminalDetailsResponse", PREFIX, NAMESPACE_URI);
//         SOAPBodyElement terminalResponseElement = (SOAPBodyElement) message.getSOAPBody().getChildElements(terminalResponseName).next();
//         Name terminals = envelope.createName("terminals", PREFIX, NAMESPACE_URI);
//         Name terminal = envelope.createName("terminal", PREFIX, NAMESPACE_URI);
//         SOAPBodyElement terminalsElement = (SOAPBodyElement) terminalResponseElement.getChildElements(terminals).next();
//         SOAPBodyElement terminalElement = (SOAPBodyElement) terminalsElement.getChildElements(terminal).next();
//         NodeList list = terminalElement.getChildNodes();
//
//         for (int i = 0; i < list.getLength(); ++i) {
//             Node n = list.item(i);
//             if ("iccid".equalsIgnoreCase(n.getLocalName())) {
//                 simCard.setIccid(n.getFirstChild().getNodeValue());
//             }
//
//             if ("status".equalsIgnoreCase(n.getLocalName())) {
//                 simCard.setStatus(SimStatus.getValue(n.getFirstChild().getNodeValue()));
//             }
//
//             if ("dateActivated".equalsIgnoreCase(n.getLocalName())) {
//                 simCard.setActivatingTime(n.getFirstChild().getNodeValue().substring(0, 10));
//             }
//
//             if ("monthToDateDataUsage".equalsIgnoreCase(n.getLocalName())) {
//                 simCard.setMonthDataFlowUsed(n.getFirstChild().getNodeValue());
//             }
//
//             if (StringUtil.isNotEmpty(simCard.getMonthDataFlowUsed())) {
//                 break;
//             }
//         }
//
//     }
//
//     private static SOAPMessage secureMessage(SOAPMessage message, final String username, final String password) throws IOException, XWSSecurityException {
//         CallbackHandler callbackHandler = callbacks -> {
//             for (Callback callback1 : callbacks) {
//                 if (callback1 instanceof UsernameCallback) {
//                     UsernameCallback callback = (UsernameCallback) callback1;
//                     callback.setUsername(username);
//                 } else {
//                     if (!(callback1 instanceof PasswordCallback)) {
//                         throw new UnsupportedCallbackException(callback1);
//                     }
//                     PasswordCallback callbackx = (PasswordCallback) callback1;
//                     callbackx.setPassword(password);
//                 }
//             }
//         };
//         InputStream policyStream = null;
//         XWSSProcessor processor = null;
//         try {
//             policyStream = SimCardUtil.class.getClassLoader().getResourceAsStream("securityPolicy.xml");
//             XWSSProcessorFactory processorFactory = XWSSProcessorFactory.newInstance();
//             processor = processorFactory.createProcessorForSecurityConfiguration(policyStream, callbackHandler);
//         } finally {
//             if (policyStream != null) {
//                 policyStream.close();
//             }
//         }
//         ProcessingContext context = processor.createProcessingContext(message);
//         return processor.secureOutboundMessage(context);
//     }
// }
