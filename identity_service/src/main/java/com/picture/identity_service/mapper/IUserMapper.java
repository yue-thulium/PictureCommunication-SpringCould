package com.picture.identity_service.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.picture.identity_service.entity.User;

/**
 * 登陆及信息获取Mapper类
 *
 * @author Yue Wu
 * @since 2020/9/28
 */
public interface IUserMapper extends BaseMapper<User> {
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
    User getMajorInf(Integer user_id);

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
     * 注册功能，用于对用户进行注册功能的实现
     *  注：该方法实体类中属性后端不实现校验工作
     * @param user 注册用户实体
     * @return
     */
    Integer register(User user);

    /**
     * 获取随机盐，为登陆密码校验做准备
     *
     * @param param 用户唯一标识，可选值[用户名，邮箱，电话]
     * @param type 标识类型，可选值：[username,email,phone]
     * @return
     */
    String getSlat(String param,String type);

    /**
     * 用户激活功能
     *
     * @param pc_id 用户主键
     * @return
     */
    Integer activation(Integer pc_id);
}
