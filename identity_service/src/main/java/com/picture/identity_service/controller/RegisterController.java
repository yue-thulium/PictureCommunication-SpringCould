package com.picture.identity_service.controller;

import com.alibaba.fastjson.JSON;
import com.picture.identity_service.config.ServerConfig;
import com.picture.identity_service.entity.User;
import com.picture.identity_service.entity.email.Mail;
import com.picture.identity_service.entity.result.ResultMod;
import com.picture.identity_service.mail.MailService;
import com.picture.identity_service.mapper.IUserMapper;
import com.picture.identity_service.service.IUserService;
import com.picture.identity_service.utils.Md5Encoding;
import com.picture.identity_service.utils.MinioUtil;
import com.picture.identity_service.utils.RedisUtil;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.context.Context;

import java.util.Random;
import java.util.UUID;

/**
 * 注册功能的核心实现
 * |
 * |--/register : 核心注册功能
 * |--/registerIcon : 带头像的注册功能
 * |
 * |--verify系列功能 : 表单验证后端提供的验证接口（以下为url路径名[方法路由名，均为GET请求]）
 * |----/verifyUsername 用户名验证
 * |----/verifyEmail 邮箱验证
 * |----/verifyPhone 电话验证
 * <p>
 * 用户名及密码为前端注册必填选项，注册后的用户必须要进行身份的激活
 * 邮箱为必填项（数据库中未进行邮箱以及电话的相关约束，此约束需要在前端及后端综合考虑
 *
 * @author Yue Wu
 * @since 2020/9/30
 */
@RestController
@RequestMapping(value = "/identity")
@Api(tags = "注册、表单验证获取接口")
public class RegisterController {

    @Autowired
    private IUserMapper userMapper;
    @Autowired
    private ResultMod resultMod;
    @Autowired
    private IUserService userService;
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private MailService mailService;
    @Autowired
    private ServerConfig serverConfig;

    /**
     * 注册方法，此处全套不进行实体内部的值的判断
     * 所有校验交由前端进行处理！
     * <p>
     * **此注册方法为使用默认头像进行注册的方法接口
     * <p>
     * 数据库中允许为空的字段：
     * icon(用户头像，提供默认头像)
     * pet_name(用户昵称，为空则提供随机生成)
     * phone(直接null值即可)
     * [email,phone](两者取其一为空，涉及到用户激活处理) => 此处暂不能实现phone的相关激活功能实现
     * 其余字段：
     * username
     * password
     * pc_role
     * 均不可为空
     * 用户管理权限核心由管理员分配，注册只允许注册普通用户权限！
     * =>数据库默认值，同时后端处理也会强制修改为用户权限对应值
     *
     * @param userStr User对应的JSON字符串
     * @return 注册提示信息
     */
    @PostMapping("/register")
    @ApiOperation(value = "用户注册接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "user", value = "User对象", required = true),
            @ApiImplicitParam(name = "icon", value = "头像图片", required = true)
    })
    @ApiResponses({
            @ApiResponse(code = 40010, message = "服务器发生未知错误"),
            @ApiResponse(code = 200, message = "注册成功")
    })
    public ResultMod register(@RequestParam("user") String userStr, @RequestParam(value = "icon", required = false) MultipartFile file) {
        //判空，防止抛出异常
        if (userStr == null || "".equals(userStr)) {
            return resultMod.fail().code(40010).message("服务器内部错误");
        }
        //JSON转换对象
        User user = JSON.parseObject(userStr, User.class);
        //强制配置权限
        user.setPc_role(1);
        //默认用户昵称
        if (user.getPet_name() == null || "".equals(user.getPet_name())) {
            user.setPet_name("用户" + new Random().nextInt(1000));
        }
        //UUID为键，存储在Redis中，供激活使用
        String userUUID = UUID.randomUUID().toString();
        user.setActiveUUID(userUUID);
        //密码加密处理
        String salt = Md5Encoding.md5RandomSaltGenerate();
        user.setSalt(salt);
        user.setPassword(Md5Encoding.md5RanSaltEncode(user.getPassword() + salt));
        System.out.println(user);
        int insert = 0;
        //判断是否有用户头像的输入
        if (file != null) {
            user.setIcon(MinioUtil.getInstance().upLoadFile(file));
            //插入操作
            insert = userMapper.register(user);
        } else {
            //插入操作
            insert = userMapper.register(user);
        }
        //注册成功后的激活邮箱发送逻辑
        if (insert > 0) {
            //Reds保存(UUID，userID)键值对
            redisUtil.set(userUUID, user.getPc_id(), 60 * 60 * 24);
            //邮箱验证发送整体逻辑
            Context context = new Context();
            context.setVariable("userId", userUUID);
            context.setVariable("url", serverConfig.getUrl());
            Mail mail = mailService.prepareMail(context, user.getEmail());
            mailService.sendActiveMail(mail);
            return resultMod.success().message("注册成功！");
        }
        //所有方法均为匹配，特殊处理返回
        return resultMod.fail().code(40010).message("服务器内部错误");
    }

    /**
     * 表单校验支持接口 —— username
     *
     * @param username 用户名
     * @return 是否可用 => 200 可用 => 201 不可用
     */
    @GetMapping("/verifyUsername")
    @ApiOperation(value = "用户名校验接口")
    @ApiImplicitParam(name = "username", value = "用户名", required = true)
    @ApiResponses({
            @ApiResponse(code = 201, message = "已被使用"),
            @ApiResponse(code = 200, message = "可用")
    })
    public ResultMod verifyUsername(@RequestParam("username") String username) {
        if (userService.verifyUsername(username) < 1) {
            return resultMod.success().message("可用");
        }
        return resultMod.success().code(201).message("已被使用");
    }

    /**
     * 表单校验支持接口 —— email
     *
     * @param email 用户名
     * @return 是否可用 => 200 可用 => 201 不可用
     */
    @GetMapping("/verifyEmail")
    @ApiOperation(value = "用户邮箱校验接口")
    @ApiImplicitParam(name = "email", value = "用户邮箱", required = true)
    @ApiResponses({
            @ApiResponse(code = 201, message = "已被使用"),
            @ApiResponse(code = 200, message = "可用")
    })
    public ResultMod verifyEmail(@RequestParam("email") String email) {
        if (userService.verifyEmail(email) < 1) {
            return resultMod.success().message("可用");
        }
        return resultMod.success().code(201).message("已被使用");
    }

    /**
     * 表单校验支持接口 —— phone
     *
     * @param phone 用户名
     * @return 是否可用 => 200 可用 => 201 不可用
     */
    @GetMapping("/verifyPhone")
    @ApiOperation(value = "用户电话校验接口")
    @ApiImplicitParam(name = "phone", value = "用户电话", required = true)
    @ApiResponses({
            @ApiResponse(code = 201, message = "已被使用"),
            @ApiResponse(code = 200, message = "可用")
    })
    public ResultMod verifyPhone(@RequestParam("phone") String phone) {
        if (userService.verifyPhone(phone) < 1) {
            return resultMod.success().message("可用");
        }
        return resultMod.success().code(201).message("已被使用");
    }
}
