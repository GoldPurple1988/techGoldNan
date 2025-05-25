package com.univhis.entity;

import lombok.Data;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.baomidou.mybatisplus.annotation.TableId;

@Data // 使用Lombok的@Data注解，自动生成所有字段的getter/setter、equals、hashCode和toString方法
@TableName("address") // 指定该实体类对应的数据库表名为address
public class Address extends Model<Address> { // 定义Address类并继承MyBatis-Plus的Model类，泛型为自身，实现ActiveRecord风格操作
    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO) // 标识id为主键，自增类型
    private Long id; // 地址表的主键id字段，对应数据库中的id列

    /**
     * 联系人
     */
    private String linkUser; // 联系人姓名字段，用于存储收货人姓名

    /**
     * 联系地址
     */
    private String linkAddress; // 联系地址字段，用于存储收货地址的详细信息

    /**
     * 联系电话
     */
    private String linkPhone; // 联系电话字段，用于存储收货人的联系电话

    private Long userId; // 用户id字段，表示该地址所属的用户id
}
