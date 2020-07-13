// package com.yimao.cloud.wechat.controller;
//
// import org.apache.commons.codec.digest.DigestUtils;
// import org.springframework.web.bind.annotation.RequestMapping;
// import org.springframework.web.bind.annotation.ResponseBody;
// import org.springframework.web.bind.annotation.RestController;
//
// import javax.servlet.http.HttpServletRequest;
// import javax.servlet.http.HttpServletResponse;
// import java.io.IOException;
// import java.io.PrintWriter;
// import java.util.Arrays;
//
// /**
//  * 描述：TODO
//  *
//  * @Author Zhang Bo
//  * @Date 2019/4/22
//  */
// @RestController
// public class WxEventInitController {
//
//     private static final String token = "e183e3a272534d17a742bc196f0bc3bc";
//
//     /**
//      * 微信验证
//      *
//      * @param request
//      * @param response
//      */
//     @RequestMapping(value = "/wxgzh/event")
//     @ResponseBody
//     public void wxgzhEvent(HttpServletRequest request, HttpServletResponse response) throws IOException {
//         System.out.println("===tokenCheck===");
//         String signature = request.getParameter("signature");
//         String timestamp = request.getParameter("timestamp");
//         String nonce = request.getParameter("nonce");
//         String echostr = request.getParameter("echostr");
//         System.out.println("{signature : " + signature + ", timestamp : " + timestamp + ", nonce : " + nonce + ", echostr : " + echostr + "}");
//         PrintWriter out = response.getWriter();
//         if (this.checkSignature(signature, timestamp, nonce)) {
//             System.out.println("check ok");
//             out.print(echostr);
//         }
//         out.close();
//     }
//
//     private boolean checkSignature(String signature, String timestamp, String nonce) {
//         String[] arr = new String[]{token, timestamp, nonce};
//         // sort
//         Arrays.sort(arr);
//         // generate String
//         String content = arr[0] + arr[1] + arr[2];
//         // shal code
//         String temp = DigestUtils.sha1Hex(content).toUpperCase();
//         return temp.equalsIgnoreCase(signature);
//     }
//
// }
