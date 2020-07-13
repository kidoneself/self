package com.yimao.cloud.base.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author Zhang Bo
 * @date 2019/5/21
 */
@Configuration
@ConfigurationProperties("yimao.wechat")
@Getter
@Setter
public class WechatProperties {

    private final MessageTemplate messageTemplate = new MessageTemplate();
    private final Jsapi jsapi = new Jsapi();
    private final App app = new App();
    private final Cat cat = new Cat();
    private final Health health = new Health();

    public static class MessageTemplate {
        private String bindSuccess;
        private String clientInvalid;
        private String handselSuccess;
        private String dealResult;
        private String orderPaySuccess;
        private String orderCheck;
        private String hireMaturity;
        private String refundSuccess;
        private String levelUpgrade;
        private String onlineMessage;
        private String reportUpload;

        public String getBindSuccess() {
            return bindSuccess;
        }

        public void setBindSuccess(String bindSuccess) {
            this.bindSuccess = bindSuccess;
        }

        public String getClientInvalid() {
            return clientInvalid;
        }

        public void setClientInvalid(String clientInvalid) {
            this.clientInvalid = clientInvalid;
        }

        public String getHandselSuccess() {
            return handselSuccess;
        }

        public void setHandselSuccess(String handselSuccess) {
            this.handselSuccess = handselSuccess;
        }

        public String getDealResult() {
            return dealResult;
        }

        public void setDealResult(String dealResult) {
            this.dealResult = dealResult;
        }

        public String getOrderPaySuccess() {
            return orderPaySuccess;
        }

        public void setOrderPaySuccess(String orderPaySuccess) {
            this.orderPaySuccess = orderPaySuccess;
        }

        public String getOrderCheck() {
            return orderCheck;
        }

        public void setOrderCheck(String orderCheck) {
            this.orderCheck = orderCheck;
        }

        public String getHireMaturity() {
            return hireMaturity;
        }

        public void setHireMaturity(String hireMaturity) {
            this.hireMaturity = hireMaturity;
        }

        public String getRefundSuccess() {
            return refundSuccess;
        }

        public void setRefundSuccess(String refundSuccess) {
            this.refundSuccess = refundSuccess;
        }

        public String getLevelUpgrade() {
            return levelUpgrade;
        }

        public void setLevelUpgrade(String levelUpgrade) {
            this.levelUpgrade = levelUpgrade;
        }

        public String getOnlineMessage() {
            return onlineMessage;
        }

        public void setOnlineMessage(String onlineMessage) {
            this.onlineMessage = onlineMessage;
        }

        public String getReportUpload() {
            return reportUpload;
        }

        public void setReportUpload(String reportUpload) {
            this.reportUpload = reportUpload;
        }
    }

    public static class Jsapi {
        private String appid;
        private String secret;
        private String mchId;
        private String key;

        public String getAppid() {
            return appid;
        }

        public void setAppid(String appid) {
            this.appid = appid;
        }

        public String getSecret() {
            return secret;
        }

        public void setSecret(String secret) {
            this.secret = secret;
        }

        public String getMchId() {
            return mchId;
        }

        public void setMchId(String mchId) {
            this.mchId = mchId;
        }

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }
    }

    public static class App {
        private String appid;
        private String secret;
        private String mchId;
        private String key;

        public String getAppid() {
            return appid;
        }

        public void setAppid(String appid) {
            this.appid = appid;
        }

        public String getSecret() {
            return secret;
        }

        public void setSecret(String secret) {
            this.secret = secret;
        }

        public String getMchId() {
            return mchId;
        }

        public void setMchId(String mchId) {
            this.mchId = mchId;
        }

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }
    }

    public static class Cat {
        private String appid;
        private String secret;

        public String getAppid() {
            return appid;
        }

        public void setAppid(String appid) {
            this.appid = appid;
        }

        public String getSecret() {
            return secret;
        }

        public void setSecret(String secret) {
            this.secret = secret;
        }
    }

    public static class Health {
        private String appid;
        private String secret;

        public String getAppid() {
            return appid;
        }

        public void setAppid(String appid) {
            this.appid = appid;
        }

        public String getSecret() {
            return secret;
        }

        public void setSecret(String secret) {
            this.secret = secret;
        }
    }

}
