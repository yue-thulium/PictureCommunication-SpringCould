package com.picture.identity_service.controller;

import com.picture.identity_service.config.ServerConfig;
import com.picture.identity_service.entity.email.Mail;
import com.picture.identity_service.entity.result.ResultMod;
import com.picture.identity_service.mail.MailService;
import com.picture.identity_service.service.IUserService;
import com.picture.identity_service.utils.RedisUtil;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.context.Context;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 账户激活相关接口，包含激活以及重新发送激活码方法
 *
 * @author Yue Wu
 * @since 2020/10/9
 */
@RestController
@RequestMapping("/identity")
@Api(tags = "账户验证接口")
@Slf4j
public class ActivationController {

    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private ResultMod resultMod;
    @Autowired
    private IUserService userService;
    @Autowired
    private ServerConfig serverConfig;
    @Autowired
    private MailService mailService;

    /**
     * 激活接口，激活后跳转到制定的登录界面
     *
     * @param response
     * @param userID
     * @return
     * @throws IOException
     */
    @GetMapping("/activation/{userID}")
    @ApiOperation(value = "账户激活接口")
    @ApiImplicitParam(name = "userID", value = "激活识别码", required = true)
    public ResultMod activation(HttpServletResponse response, @PathVariable("userID") String userID) throws IOException {
        boolean exist = redisUtil.hasKey(userID);
        if (!exist) {
            return resultMod.fail().message("验证码已过期，请重新登录获取激活邮件");
        } else {
            Integer pc_id = (Integer) redisUtil.get(userID);
            if (userService.activation(pc_id) > 0) {
                redisUtil.del(userID);
                //重定向到登录页面
                response.sendRedirect("http://www.baidu.com");
                return null;
            } else {
                return resultMod.fail().message("服务器内部错误");
            }
        }
    }

    @PostMapping("/activation/changeEmail")
    @ApiOperation(value = "账户邮箱更换接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "username", value = "用户名", required = true),
            @ApiImplicitParam(name = "email", value = "新邮箱", required = true)
    })
    public ResultMod changeEmail(@RequestParam("username") String username, @RequestParam("email") String email) {
        String uuid = userService.getUUID(username);
        Integer userID = userService.getUserID(username);
        //判断Redis中当前是否存在缓存
        if (!redisUtil.hasKey(uuid)) {
            redisUtil.set(uuid, userID, 60 * 60 * 24);
        }
        //修改数据库中的字段
        if (userService.changeEmail(username,email) <= 0) {
            return resultMod.fail().message("服务器内部错误");
        }
        //准备进行邮件发送
        Context context = new Context();
        context.setVariable("userId", uuid);
        context.setVariable("url", serverConfig.getUrl());
        Mail mail = mailService.prepareMail(context, email);
        mailService.sendActiveMail(mail);
        return resultMod.success().message("发送成功！");
    }

}
