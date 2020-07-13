package com.yimao.cloud.base.mail;

import com.yimao.cloud.base.context.SpringContextHolder;
import com.yimao.cloud.base.enums.EnvironmentEnum;
import com.yimao.cloud.base.utils.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Component;

import java.util.Properties;

/**
 * 发送邮件
 */
@Component
@Slf4j
public class MailSender {

    private static final String STMP_HOST = "smtp.mxhichina.com";
    private static final int STMP_PORT = 465;
    private static final String STMP_ACCOUNT = "ymtk@yimaokeji.com";
    private static final String STMP_PASSWORD = "3Plus2-five*0=?";
    private static final String RECEIVER = "zhangbo1@yimaokeji.com;sutingting@yimaokeji.com;liuyi@yimaokeji.com";

    /**
     * 发送
     *
     * @param receiver 邮箱
     * @param subject  主体
     * @param content  内容
     */
    public void send(String receiver, String subject, String content) {
        if (EnvironmentEnum.PRO.code.equalsIgnoreCase(SpringContextHolder.getEnvironment())) {
            log.info("发送邮件开始：内容主体为：" + subject);

            JavaMailSenderImpl javaMailSender = new JavaMailSenderImpl();
            javaMailSender.setHost(STMP_HOST);
            javaMailSender.setPort(STMP_PORT);
            javaMailSender.setUsername(STMP_ACCOUNT);
            javaMailSender.setPassword(STMP_PASSWORD);
            Properties properties = new Properties();
            properties.setProperty("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
            properties.setProperty("mail.smtp.port", "465");
            properties.setProperty("mail.smtp.socketFactory.port", "465");
            properties.setProperty("mail.smtp.socketFactory.fallback", "false");
            properties.put("mail.smtp.auth", "true");
            properties.put("mail.smtp.starttls.enable", "true");
            properties.put("mail.smtp.timeout", 25000);
            javaMailSender.setJavaMailProperties(properties);

            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(STMP_ACCOUNT);
            if (StringUtil.isEmpty(receiver)) {
                receiver = RECEIVER;
            }
            String[] receivers = receiver.split(";");
            message.setTo(receivers);
            message.setSubject(subject);
            message.setText(content);
            javaMailSender.send(message);
        }
    }
}
