package com.picture.identity_service.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * Created on 2020/9/28
 *
 * @author Yue Wu
 * @since 2020/9/28
 */
@Data
@TableName(value = "pc")
@ApiModel(value = "用户实体对象")
public class User implements Serializable {
    @TableId(value = "AUTO")
    @ApiModelProperty(value = "用户ID")
    private Integer pc_id;
    @ApiModelProperty(value = "用户名")
    private String username;
    @ApiModelProperty(value = "用户密码")
    private String password;
    @ApiModelProperty(value = "用户邮箱")
    private String email;
    @ApiModelProperty(value = "用户电话")
    private String phone;
    @ApiModelProperty(value = "用户权限")
    private Integer pc_role;
    @ApiModelProperty(value = "用户头像")
    private String icon;
    @ApiModelProperty(value = "用户昵称")
    private String pet_name;
    @ApiModelProperty(value = "账户状态-是否激活")
    private Integer ban;
    @ApiModelProperty(value = "账户状态-是否禁用")
    private Integer del_flag;
    @ApiModelProperty(value = "加密盐")
    private String salt;
    @ApiModelProperty(value = "激活用键")
    private String activeUUID;
    @TableField(exist = false)
    @ApiModelProperty(value = "权限对象")
    private Role role;
}
