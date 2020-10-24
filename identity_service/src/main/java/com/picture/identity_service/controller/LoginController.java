package com.picture.identity_service.controller;

import com.picture.identity_service.config.ServerConfig;
import com.picture.identity_service.entity.User;
import com.picture.identity_service.entity.email.Mail;
import com.picture.identity_service.entity.result.ResultMod;
import com.picture.identity_service.mail.MailService;
import com.picture.identity_service.service.IUserService;
import com.picture.identity_service.utils.JWTUtil;
import com.picture.identity_service.utils.Md5Encoding;
import com.picture.identity_service.utils.RedisUtil;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.context.Context;

import java.util.UUID;

/**
 * 登陆相关接口，实现登录功能以及基本的信息获取
 * 提供权限及信息获取接口，为Vue下实现动态权限分配提供支持
 * 此过程中实现token的签发工作
 *
 * @author Yue Wu
 * @since 2020/9/28
 */
@RestController
@RequestMapping("/identity")
@Api(tags = "登陆、信息获取接口")
public class LoginController {
    @Autowired
    private IUserService userService;

    @Autowired
    private ResultMod resultMod;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private MailService mailService;

    @Autowired
    private ServerConfig serverConfig;

    /**
     * 登陆并签发token
     *
     * @param username 用户名
     * @param password 前端一轮加密处理后的密码字符串
     * @return 结果集
     */
    @PostMapping("/login")
    @ApiOperation(value = "用户登录接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "username", value = "用户名", required = true),
            @ApiImplicitParam(name = "password", value = "用户密码", required = true)
    })
    @ApiResponses({
            @ApiResponse(code = 40010, message = "用户名或密码错误"),
            @ApiResponse(code = 200, message = "登录完成，发放token")
    })
    public ResultMod login(@RequestParam("username") String username, @RequestParam("password") String password) {

        String salt = userService.getSlat(username, "username");

        if (salt == null || "".equals(salt) || salt.length() < 1) {
            return resultMod.fail().message("用户名或密码错误");
        }

        //用户密码加密
        String realPass = Md5Encoding.md5RanSaltEncode(password + salt);
        //登陆操作，对比表单提交的登录信息
        User user = userService.login(username, realPass);

        //登录成功进行 账户状态判断 || 凭证缓存更新
        if (user != null) {
            //账户激活状态判断
            if (user.getBan() == 1) {
                //返回提示
                String uuid = userService.getUUID(username);
                //判断存储的UUID是否过期
                if (!redisUtil.hasKey(uuid)) {
                    redisUtil.set(uuid, user.getPc_id(), 60 * 60 * 24);
                }
                //重新进行用户激活邮件的发送
                Context context = new Context();
                context.setVariable("userId", uuid);
                context.setVariable("url", serverConfig.getUrl());
                Mail mail = mailService.prepareMail(context, user.getEmail());
                mailService.sendActiveMail(mail);
                return resultMod.fail().message("账户未激活，请前往注册邮箱使用最新的验证链接进行激活");
            }
            //账户使用权状态判断
            if (user.getDel_flag() == 1) {
                return resultMod.fail().message("账户已被封禁，请联系管理员");
            }
            //签发 崭新出厂 的凭证
            String token = JWTUtil.createToken(user.getUsername(), String.valueOf(user.getPc_id()),
                    user.getRole().getRole(), user.getRole().getPermission());
            //原 久经沙场 的凭证存在即删除
            if (redisUtil.hasKey(user.getUsername())) {
                redisUtil.del(user.getUsername());
            }
            //缓存 崭新出厂 的凭证
            redisUtil.set(user.getUsername(), token, 60 * 60 * 24);
            return resultMod.success().message(token);
        } else {
            return resultMod.fail().message("用户名或密码错误");
        }
    }

    /**
     * @param token 凭证
     * @return
     */
    @GetMapping("/getInf")
    @ApiOperation(value = "用户信息获取接口")
    @ApiResponses({
            @ApiResponse(code = 40010, message = "token不存在"),
            @ApiResponse(code = 40011, message = "token失效"),
            @ApiResponse(code = 200, message = "返回对应用户信息")
    })
    public ResultMod getInf(@RequestHeader String token) {
        //验证token是否存在，不存在即提示需要进行登陆处理
        if (token == null) {
            return resultMod.fail().message("请先登录");
        } else {
            return userService.getMajorInf(JWTUtil.getUserID(token));
        }
    }
}
