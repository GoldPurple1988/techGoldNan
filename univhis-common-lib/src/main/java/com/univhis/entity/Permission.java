package com.univhis.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;

// Lombok注解，自动生成getter/setter等常用方法
@Data
// MyBatis-Plus注解，指定对应的数据库表名
@TableName("t_permission")
public class Permission extends Model<Permission> { // 继承MyBatis-Plus提供的AR基类
    /**
     * 主键id
     */
    @TableId(value = "id", type = IdType.AUTO) // 指定主键字段及自增策略
    private Long id;            // 权限唯一标识

    private String name;        // 权限名称

    private String path;        // 权限对应的接口路径或资源路径

    private String description; // 权限描述
    private String icon;        // 权限图标
}
