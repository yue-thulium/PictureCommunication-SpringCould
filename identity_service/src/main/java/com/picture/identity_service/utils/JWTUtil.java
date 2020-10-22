package com.picture.identity_service.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.interfaces.DecodedJWT;

import java.util.Date;

/**
 * JWT工具类，用于生成登录授权模块所依赖的token凭证 <br>
 *
 *
 * @author Yue Wu
 * @since 2020/9/28
 */
public final class JWTUtil {
    // 过期时间 24 小时
    private static final long EXPIRE_TIME = 60 * 24 * 60 * 1000;
    // 密钥
    private static final String SECRET = "XIONGJIANG";

    /**
     * 生成 token, 24h后过期
     *
     * @param username 用户名
     * @param id       用户id
     * @return 加密的token
     */
    public static String createToken(String username, String id) {
        Date date = new Date(System.currentTimeMillis() + EXPIRE_TIME);
        Algorithm algorithm = Algorithm.HMAC256(SECRET);
        // 附带username&id信息
        return JWT.create()
                .withClaim("username", username)
                .withClaim("id", id)
                //到期时间
                .withExpiresAt(date)
                //创建一个新的JWT，并使用给定的算法进行标记
                .sign(algorithm);
    }

    /**
     * 生成 token, 24h后过期
     *
     * @param username   用户名
     * @param id         用户id
     * @param role       用户身份
     * @param permission 用户身份对应权限
     * @return 加密的token
     */
    public static String createToken(String username, String id, String role, String permission) {
        Date date = new Date(System.currentTimeMillis() + EXPIRE_TIME);
        Algorithm algorithm = Algorithm.HMAC256(SECRET);
        // 附带username&id信息
        return JWT.create()
                .withClaim("username", username)
                .withClaim("id", id)
                .withClaim("role", role)
                .withClaim("permission", permission)
                //到期时间
                .withExpiresAt(date)
                //创建一个新的JWT，并使用给定的算法进行标记
                .sign(algorithm);
    }

    /**
     * 校验 token 是否正确
     *
     * @param token    密钥
     * @param username 用户名
     * @return 是否正确
     */
    public static boolean verify(String token, String username) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(SECRET);
            //在token中附带了username信息
            JWTVerifier verifier = JWT.require(algorithm)
                    .withClaim("username", username)
                    .build();
            //验证 token
            verifier.verify(token);
            return true;
        } catch (Exception exception) {
            return false;
        }
    }

    /**
     * 获得token中的信息，无需secret解密也能获得
     *
     * @return token中包含的用户名
     */
    public static String getUsername(String token) {
        try {
            DecodedJWT jwt = JWT.decode(token);
            return jwt.getClaim("username").asString();
        } catch (JWTDecodeException e) {
            return null;
        }
    }

    /**
     * 获得token中的信息，无需secret解密也能获得
     *
     * @return token中包含的用户ID
     */
    public static Integer getUserID(String token) {
        try {
            DecodedJWT jwt = JWT.decode(token);
            return jwt.getClaim("id").asInt();
        } catch (JWTDecodeException e) {
            return null;
        }
    }

    /**
     * 获得token中的信息，无需secret解密也能获得
     *
     * @return token中包含的用户role
     */
    public static String getUserRole(String token) {
        try {
            DecodedJWT jwt = JWT.decode(token);
            return jwt.getClaim("role").asString();
        } catch (JWTDecodeException e) {
            return null;
        }
    }

    /**
     * 获得token中的信息，无需secret解密也能获得
     *
     * @return token中包含的用户permission
     */
    public static String getUserPermission(String token) {
        try {
            DecodedJWT jwt = JWT.decode(token);
            return jwt.getClaim("permission").asString();
        } catch (JWTDecodeException e) {
            return null;
        }
    }
}
