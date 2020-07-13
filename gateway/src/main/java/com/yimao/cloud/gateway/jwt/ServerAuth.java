// package com.yimao.cloud.gateway.jwt;
//
// import com.yimao.cloud.base.msg.BaseResponse;
// import com.yimao.cloud.gateway.feign.AuthFeign;
// import lombok.extern.slf4j.Slf4j;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.beans.factory.annotation.Value;
// import org.springframework.scheduling.annotation.Scheduled;
// import org.springframework.stereotype.Component;
// import org.springframework.util.StringUtils;
//
// @Component
// @Slf4j
// public class ServerAuth {
//
//     @Value("${spring.application.name}")
//     private String serverName;
//
//     @Resource
//     private AuthFeign authFeign;
//
//     private String serverToken;
//
//     /**
//      * 定时刷新该微服务的JWT token
//      */
//     @Scheduled(cron = "0 0/5 * * * ?")
//     public void refreshServerToken() {
//         log.info("refresh server token...");
//         BaseResponse<String> resp = authFeign.getAccessToken(serverName);
//         if (resp.getCode() == 0 && !StringUtils.isEmpty(resp.getData())) {
//             this.serverToken = resp.getData();
//         }
//     }
//
//     public String getServerToken() {
//         if (this.serverToken == null) {
//             this.refreshServerToken();
//         }
//         return serverToken;
//     }
//
// }