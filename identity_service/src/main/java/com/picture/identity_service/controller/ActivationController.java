package com.picture.identity_service.controller;

import com.picture.identity_service.entity.result.ResultMod;
import com.picture.identity_service.service.IUserService;
import com.picture.identity_service.utils.RedisUtil;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
        if (exist == false) {
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

}
