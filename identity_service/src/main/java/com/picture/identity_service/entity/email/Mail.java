package com.picture.identity_service.entity.email;

import lombok.Data;

import java.io.Serializable;

/**
 * 邮件实体类
 *
 * @author Yue Wu
 * @since 2020/10/9
 */
@Data
public class Mail implements Serializable {
    /**
     * 邮件发送方
     */
    private String from;
    /**
     * 邮件接收方
     */
    private String to;
    /**
     * 邮件主题
     */
    private String subject;
    /**
     * 邮件内容
     */
    private String mailContent;
}
