package com.cinemax.domain.auth.service.impl;

import com.cinemax.core.constants.CommonConstants;
import com.cinemax.domain.auth.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 * 이메일 서비스 구현체
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    @Value("${app.email.from:noreply@cinemax.com}")
    private String fromEmail;

    @Value("${app.email.from-name:Cinemax}")
    private String fromName;

    // 이메일 인증 코드 발송
    @Override
    public void sendEmailVerificationCode(String email, String verificationCode, LocalDateTime expiresAt) {
        log.info("이메일 인증 코드 발송: {}", email);

        Map<String, Object> variables = new HashMap<>();
        variables.put("verificationCode", verificationCode);
        variables.put("expiresAt", expiresAt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));

        sendHtmlEmail(email, "[Cinemax] 이메일 인증 코드", "email/email-verification", variables);
    }

    // 보안 알림 이메일 발송
    @Override
    public void sendSecurityAlertEmail(String email, String eventType, String details) {
        log.info("보안 알림 이메일 발송: {}", email);

        Map<String, Object> variables = new HashMap<>();
        variables.put("eventType", eventType);
        variables.put("details", details);
        variables.put("occurredAt", LocalDateTime.now().format(DateTimeFormatter.ofPattern(CommonConstants.DATETIME_FORMAT)));

        sendHtmlEmail(email, "[Cinemax] 보안 알림", "email/security-alert", variables);
    }

    // 임시 비밀번호 발송
    @Override
    public void sendTemporaryPassword(String email, String tempPassword) {
        log.info("임시 비밀번호 이메일 발송: {}", email);

        Map<String, Object> variables = new HashMap<>();
        variables.put("tempPassword", tempPassword);

        sendHtmlEmail(email, "[Cinemax] 임시 비밀번호 안내", "email/temp-password", variables);
    }

    // HTML 이메일 발송 (범용)
    private void sendHtmlEmail(String email, String subject, String templateName, Map<String, Object> templateVariables) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            // 발신자 설정
            helper.setFrom(fromEmail, fromName);
            helper.setTo(email);
            helper.setSubject(subject);

            // 템플릿 변수 설정
            Context context = new Context();
            templateVariables.forEach(context::setVariable);

            // HTML 템플릿 처리
            String htmlContent = templateEngine.process(templateName, context);
            helper.setText(htmlContent, true);

            // 이메일 발송
            mailSender.send(mimeMessage);

        } catch (MessagingException e) {
            log.error("HTML 이메일 발송 실패: {} -> {}", fromEmail, email, e);
            throw new RuntimeException("이메일 발송에 실패했습니다.", e);
        } catch (Exception e) {
            log.error("이메일 템플릿 처리 실패: {}", templateName, e);
            throw new RuntimeException("이메일 템플릿 처리에 실패했습니다.", e);
        }
    }
}
