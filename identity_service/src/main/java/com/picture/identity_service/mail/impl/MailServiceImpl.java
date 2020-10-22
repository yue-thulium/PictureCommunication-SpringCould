package com.picture.identity_service.mail.impl;

import com.picture.identity_service.entity.email.Mail;
import com.picture.identity_service.mail.MailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.validation.constraints.NotNull;

/**
 * 邮件发送接口实现方法
 *
 * @author Yue Wu
 * @since 2020/10/9
 */
@Slf4j
@Component
public class MailServiceImpl implements MailService {

    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    private TemplateEngine templateEngine;

    public Mail prepareMail(Context context, String sendTo) {
        String emailContent = templateEngine.process("senderTemplate", context);
        Mail mail = new Mail();
        mail.setFrom("thulium0601@163.com");
        mail.setTo(sendTo);
        mail.setSubject("Please active account click under emial...");
        mail.setMailContent(emailContent);
        return mail;
    }

    @Override
    public void sendActiveMail(@NotNull Mail mail) {
        MimeMessage message = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(mail.getFrom());
            helper.setTo(mail.getTo());
            helper.setSubject(mail.getSubject());
            helper.setText(mail.getMailContent(), true);
            javaMailSender.send(message);
            log.info("激活验证邮件发送成功...");
        } catch (MessagingException e) {
            log.error("验证邮件发送失败...", e);
        }
    }
}
