package com.picture.identity_service.service.impl;

import com.alibaba.fastjson.JSON;
import com.picture.identity_service.entity.User;
import com.picture.identity_service.entity.result.ResultMod;
import com.picture.identity_service.mapper.IUserMapper;
import com.picture.identity_service.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

/**
 * IUserService接口实现类
 *      备用切面编程及多线程处理
 *
 * @author Yue Wu
 * @since 2020/9/29
 */
@Service
public class UserServiceImpl implements IUserService {

    @Autowired
    private IUserMapper userMapper;
    @Resource(name = "createThreadPool")
    private ExecutorService getInfoPool;
    @Autowired
    private ResultMod resultMod;

    /**
     * 登陆相关Service层接口
     *      暂未进行特殊处理
     * @param username 用户名
     * @param password 密码
     * @return
     */
    @Override
    public User login(String username, String password) {
        return userMapper.login(username, password);
    }

    /**
     * 信息获取接口
     *
     *      实现所需role的格式化传递:["role","permission"]
     *      以及相关User信息的存储(主要保存的核心信息：[username,email,phone,pet_name,role等])
     *      其余信息由前端设计自选，此处借口仅进行登录授权相关信息获取
     *      和用户中心相关信息无任何关联
     *
     * @param user_id 用户ID(token中存在的值)
     * @return
     */
    @Override
    public ResultMod getMajorInf(Integer user_id) {
        //查询User实体类中的信息，以及格式化返回role相关信息功能，线程方式实现
        Future<ResultMod> submit = getInfoPool.submit(new Callable<ResultMod>() {
            @Override
            public ResultMod call() throws Exception {
                User majorInf = userMapper.getMajorInf(user_id);
                if (majorInf != null) {
                    List<String> list = new ArrayList<>();
                    list.add(majorInf.getRole().getRole());
                    list.add(majorInf.getRole().getPermission());
                    return resultMod.success().message(JSON.toJSONString(majorInf)).put("role", list);
                } else {
                    return resultMod.fail().code(40011).message("token失效");
                }
            }
        });

        //防止future的get()方法阻塞，进行执行完毕与否判断
        if (submit.isDone()) {
            try {
                return submit.get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }

        //全部方法失效时的特殊处理
        return resultMod.fail().code(40011).message("token失效");
    }

    @Override
    public Integer verifyUsername(String username) {
        return userMapper.verifyUsername(username);
    }

    @Override
    public Integer verifyEmail(String email) {
        return userMapper.verifyEmail(email);
    }

    @Override
    public Integer verifyPhone(String phone) {
        return userMapper.verifyPhone(phone);
    }

    @Override
    public Integer activation(Integer pc_id) {
        return userMapper.activation(pc_id);
    }

    @Override
    public String getSlat(String param, String type) {
        return userMapper.getSlat(param,type);
    }
}
