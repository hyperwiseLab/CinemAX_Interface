package com.cinemax.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Configuration
public class EmailConfig {

    @Bean
    public JavaMailSender javaMailSender() {
        JavaMailSenderImpl ms = new JavaMailSenderImpl();
        ms.setHost("outbound.daouoffice.com");
        ms.setPort(25);
        ms.setUsername("support@hyperwise.co.kr");
        ms.setPassword("hw0908!@");

        Properties ps = ms.getJavaMailProperties();
        ps.put("mail.smtp.auth", "true");
        ps.put("mail.smtp.starttls.enable", "false");
        ps.put("mail.smtp.ssl.enable", "false");
        ps.put("mail.smtp.host", "outbound.daouoffice.com");
        ps.put("mail.smtp.port", "25");

        return ms;
    }
}