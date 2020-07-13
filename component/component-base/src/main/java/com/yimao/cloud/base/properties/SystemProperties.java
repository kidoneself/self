package com.yimao.cloud.base.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;


/**
 * @description   app推送通知配置
 * @author Liu Yi
 * @date 2019/10/25 18:55
 */

@Configuration
@ConfigurationProperties("yimao.system")
@Getter
@Setter
public class SystemProperties {
    private final YimaoJpushApp yimaoJpushApp = new YimaoJpushApp();
    private final EngineerXingeApp engineerXingeApp = new EngineerXingeApp();
    private final EngineerJpushApp engineerJpushApp = new EngineerJpushApp();

    public static class YimaoJpushApp{
        public String  masterSecret;
        public String  appKey;

        public String getMasterSecret() {
            return masterSecret;
        }

        public void setMasterSecret(String masterSecret) {
            this.masterSecret = masterSecret;
        }

        public String getAppKey() {
            return appKey;
        }

        public void setAppKey(String appKey) {
            this.appKey = appKey;
        }
    }

    public static class EngineerJpushApp{
        public String  masterSecret;
        public String  appKey;

        public String getMasterSecret() {
            return masterSecret;
        }

        public void setMasterSecret(String masterSecret) {
            this.masterSecret = masterSecret;
        }

        public String getAppKey() {
            return appKey;
        }

        public void setAppKey(String appKey) {
            this.appKey = appKey;
        }
    }

    public static class EngineerXingeApp{
        public Long  androidAccessId;
        public String  androidSecretKey;
        public Long  iosAccessId;
        public String  iosSecretKey;

        public Long getAndroidAccessId() {
            return androidAccessId;
        }

        public void setAndroidAccessId(Long androidAccessId) {
            this.androidAccessId = androidAccessId;
        }

        public String getAndroidSecretKey() {
            return androidSecretKey;
        }

        public void setAndroidSecretKey(String androidSecretKey) {
            this.androidSecretKey = androidSecretKey;
        }

        public Long getIosAccessId() {
            return iosAccessId;
        }

        public void setIosAccessId(Long iosAccessId) {
            this.iosAccessId = iosAccessId;
        }

        public String getIosSecretKey() {
            return iosSecretKey;
        }

        public void setIosSecretKey(String iosSecretKey) {
            this.iosSecretKey = iosSecretKey;
        }
    }

}
