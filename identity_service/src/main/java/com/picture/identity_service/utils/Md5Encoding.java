package com.picture.identity_service.utils;

import org.springframework.util.DigestUtils;

import java.util.UUID;

/**
 * Created on 2020/9/29
 *
 * @author Yue Wu
 * @since 2020/9/29
 */
public final class Md5Encoding {
    private static String salt="1x2j3x4y5w6t";

    /**
     * md5加盐加密算法
     * 固定盐值
     * @param password 密码
     * @return 加密后字符串
     */
    public static String md5FixSaltEncode(String password) {
        String str = "" + salt.charAt(3) + salt + salt.charAt(7) + password + salt.charAt(9);
        return DigestUtils.md5DigestAsHex(str.getBytes());
    }

    public static String md5RandomSaltGenerate() {
        return UUID.randomUUID().toString();
    }

    /**
     * 预留随机盐加密算法
     *
     * @param mixPassword 密码
     * @return 加密后字符串
     */
    public static String md5RanSaltEncode(String mixPassword) {
        return DigestUtils.md5DigestAsHex(mixPassword.getBytes());
    }
}
