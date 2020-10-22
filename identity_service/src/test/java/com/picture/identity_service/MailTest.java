package com.picture.identity_service;

import com.picture.identity_service.entity.email.Mail;
import com.picture.identity_service.mail.MailService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.UUID;

/**
 * Created on 2020/10/9
 *
 * @author Yue Wu
 * @since 2020/10/9
 */
@SpringBootTest
public class MailTest {

    @Autowired
    private MailService mailService;

    @Autowired
    private TemplateEngine templateEngine;

    @Test
    public void send() {
        Context context = new Context();
        context.setVariable("userId", UUID.randomUUID());
        Mail mail = mailService.prepareMail(context,"1359707019@qq.com");
        mailService.sendActiveMail(mail);
    }
}
