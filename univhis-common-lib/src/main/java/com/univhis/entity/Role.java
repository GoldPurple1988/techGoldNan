package com.univhis.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.univhis.common.handler.ListHandler;
import lombok.Data;

import java.util.List;

// Lombok注解，自动生成getter/setter等常用方法
@Data
// MyBatis-Plus注解，指定数据库表名以及自动映射复杂类型
@TableName(value = "t_role", autoResultMap = true)
public class Role extends Model<Role> { // 继承MyBatis-Plus提供的AR基类
    /**
     * 主键id
     */
    @TableId(value = "id", type = IdType.AUTO) // 指定主键字段及自增策略
    private Long id;            // 角色唯一标识

    private String name;        // 角色名称

    private String description; // 角色描述

    /**
     * 角色拥有的权限ID列表（JSON数组存储，用ListHandler处理）
     */
    @TableField(typeHandler = ListHandler.class) // 指定类型处理器，将List<Long>和数据库字段转换
    private List<Long> permission; // 权限ID集合
}
