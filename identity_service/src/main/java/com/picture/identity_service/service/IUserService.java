package com.picture.identity_service.service;

import com.picture.identity_service.entity.User;
import com.picture.identity_service.entity.result.ResultMod;

/**
 * 登陆、信息获取的相关service层
 *  该层实现类需要考虑信息获取相关接口的负载压力
 *
 * @author Yue Wu
 * @since 2020/9/29
 */
public interface IUserService {
    /**
     * 登陆查询操作，返回封装User实体类
     *  注：此方法返回值中不含password字段
     *
     * @param username 用户名
     * @param password 密码
     * @return User实体类
     */
    User login(String username, String password);

    /**
     * 登陆后信息获取接口，此方法返回用户对应信息
     *
     * @param user_id 用户ID(token中存在的值)
     * @return User实体类
     */
    ResultMod getMajorInf(Integer user_id);

    /**
     * 注册验证方法，用于进行用户的用户名校验
     *  注：该系列方法为前端校验使用，不具备权限控制
     *
     * @param username 准备注册的用户名
     * @return
     */
    Integer verifyUsername(String username);

    /**
     * 注册验证方法，用于进行用户的邮箱校验
     *  注：该系列方法为前端校验使用，不具备权限控制
     *
     * @param email 准备注册的用户名
     * @return
     */
    Integer verifyEmail(String email);

    /**
     * 注册验证方法，用于进行用户的电话校验
     *  注：该系列方法为前端校验使用，不具备权限控制
     *
     * @param phone 准备注册的用户名
     * @return
     */
    Integer verifyPhone(String phone);

    /**
     * 用户激活功能
     *
     * @param pc_id 用户主键
     * @return
     */
    Integer activation(Integer pc_id);

    /**
     * 获取随机盐，为登陆密码校验做准备
     *
     * @param param 用户唯一标识，可选值[用户名，邮箱，电话]
     * @param type 标识类型，可选值：[username,email,phone]
     * @return
     */
    String getSlat(String param,String type);
}
