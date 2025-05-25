package com.univhis.entity;

// 引入MyBatis-Plus的主键策略枚举
import com.baomidou.mybatisplus.annotation.IdType;
// 引入MyBatis-Plus的表字段注解
import com.baomidou.mybatisplus.annotation.TableField;
// 引入MyBatis-Plus的主键注解
import com.baomidou.mybatisplus.annotation.TableId;
// 引入MyBatis-Plus的表名注解
import com.baomidou.mybatisplus.annotation.TableName;
// 引入MyBatis-Plus的ActiveRecord基类
import com.baomidou.mybatisplus.extension.activerecord.Model;
// 引入自定义的List类型处理器
import com.univhis.common.handler.ListHandler;
// Lombok的@Data注解, 自动生成getter/setter等方法
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

// Lombok注解，自动生成getter/setter等常用方法
@Data
// MyBatis-Plus注解，指定数据库表名以及自动映射复杂类型
@TableName(value = "t_user", autoResultMap = true)
public class User extends Model<User> { // 继承MyBatis-Plus提供的AR基类
    /**
     * 主键id
     */
    @TableId(value = "id", type = IdType.AUTO) // 指定主键字段及自增策略
    private Long id; // 用户唯一标识

    private String username;    // 用户名
    private String nickName;    // 昵称

    private String password;    // 密码

    private String email;       // 邮箱

    private String phone;       // 手机号

    private String avatar;      // 头像
    private String address;     // 地址
    private String age;         // 年龄
    private BigDecimal account; // 账户余额

    @TableField(exist = false) // 标记为非数据库表字段，仅在程序中使用
    private String token;       // 用户临时登录令牌

    /**
     * 用户拥有的角色ID列表（JSON数组存储，用ListHandler处理）
     */
    @TableField(typeHandler = ListHandler.class) // 指定类型处理器，将List<Long>和数据库字段转换
    private List<Long> role;    // 角色ID集合

    @TableField(exist = false) // 标记为非数据库表字段，仅在程序中使用
    private List<Permission> permission; // 当前用户拥有的权限列表（详细权限对象）
}
