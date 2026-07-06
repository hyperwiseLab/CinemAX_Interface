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
        ms.setHost("smtp.gmail.com");
        ms.setPort(587);
        ms.setUsername("hyperwise98@gmail.com");
        ms.setPassword("eoohyudznmdnajrp");  // Gmail 앱 비밀번호 (일반 비번 아님)
        ms.setDefaultEncoding("UTF-8");

        Properties ps = ms.getJavaMailProperties();
        ps.put("mail.transport.protocol", "smtp");
        ps.put("mail.smtp.auth", "true");
        ps.put("mail.smtp.starttls.enable", "true");
        ps.put("mail.smtp.starttls.required", "true");
        ps.put("mail.smtp.ssl.trust", "smtp.gmail.com");
        ps.put("mail.smtp.host", "smtp.gmail.com");
        ps.put("mail.smtp.port", "587");
        ps.put("mail.smtp.connectiontimeout", "5000");
        ps.put("mail.smtp.timeout", "5000");
        ps.put("mail.smtp.writetimeout", "5000");

        return ms;
    }
}