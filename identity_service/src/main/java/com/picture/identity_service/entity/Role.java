package com.picture.identity_service.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * Created on 2020/9/29
 *
 * @author Yue Wu
 * @since 2020/9/29
 */
@Data
@TableName(value = "pc_role")
@ApiModel(value = "权限实体")
public class Role implements Serializable {
    @ApiModelProperty("权限ID")
    private Integer pr_id;
    @ApiModelProperty(value = "权限名称")
    private String role;
    @ApiModelProperty(value = "权限细分")
    private String permission;
}
