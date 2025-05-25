package com.univhis.entity;

import lombok.Data;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.baomidou.mybatisplus.annotation.TableId;

@Data // Lombok 注解：为该类自动生成所有字段的 getter/setter 方法、equals()、hashCode()、toString() 方法以及一个接收所有 final 字段的构造器。
@TableName("t_notice") // MyBatis-Plus 注解：指定这个实体类 Notice 对应数据库中的表名为 "t_notice"。
public class Notice extends Model<Notice> { // 定义一个名为 Notice 的公共类，它继承自 MyBatis-Plus 的 Model<Notice> 类，从而拥有 ActiveRecord 功能。
    /**
     * 主键 // 描述：这个字段是数据库表的主键。
     */
    @TableId(value = "id", type = IdType.AUTO) // MyBatis-Plus 注解：标识 'id' 属性为表的主键。
    // value = "id" 指明数据库表中主键列的名称是 "id"。
    // type = IdType.AUTO 指明主键的生成策略是数据库自增长。
    private Long id; // 声明一个私有的长整型 (Long) 属性，名为 id，用于存储公告的唯一标识。

    /**
     * 标题  // 描述：这个字段存储公告的标题。
     */
    private String title; // 声明一个私有的字符串类型 (String) 属性，名为 title，用于存储公告的标题。

    /**
     * 内容  // 描述：这个字段存储公告的具体内容。
     */
    private String content; // 声明一个私有的字符串类型 (String) 属性，名为 content，用于存储公告的详细内容。

    /**
     * 发布时间  // 描述：这个字段存储公告的发布时间。
     */
    private String time; // 声明一个私有的字符串类型 (String) 属性，名为 time，用于存储公告的发布时间或创建时间。

}
