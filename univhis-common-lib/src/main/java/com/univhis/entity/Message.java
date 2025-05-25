package com.univhis.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;

@Data // Lombok 注解，自动为本类所有属性生成 getter、setter、toString、equals 和 hashCode 方法。
@TableName("t_message") // MyBatis-Plus 注解，指定本实体类对应数据库表名为 t_message。
public class Message extends Model<Message> { // 定义一个名为 Message 的公共类，并继承 MyBatis-Plus 的 Model<Message>，获得 ActiveRecord 操作能力。
    /**
     * 主键 // 用于存储消息的唯一标识。
     */
    @TableId(value = "id", type = IdType.AUTO) // MyBatis-Plus 注解，指定 id 字段为主键，数据库自增。
    private Long id; // 声明主键属性，类型为 Long。

    /**
     * 内容 // 存储消息的文本内容。
     */
    private String content; // 消息内容，类型为字符串。

    /**
     * 评论人 // 存储评论（消息）发布者的用户名。
     */
    private String username; // 评论人用户名，类型为字符串。

    /**
     * 评论时间 // 存储消息（评论）的发布时间。
     */
    private String time; // 评论时间，类型为字符串，一般存储时间戳或格式化时间。

    /**
     * 父ID // 存储父消息的主键id，用于实现消息的层级（如回复）。
     */
    private Long parentId; // 父级消息的 id，类型为 Long。

    @TableField(exist = false) // MyBatis-Plus 注解，指定本属性在数据库表中不存在，仅作为业务属性使用。
    private Message parentMessage; // 父级消息对象，用于消息嵌套显示，类型为 Message。

    /**
     * 关联id // 存储与消息相关联的外部数据的 id（如所属帖子、文章等的 id）。
     */
    private Long foreignId; // 关联数据的 id，类型为 Long。

    @TableField(exist = false) // MyBatis-Plus 注解，指定该属性不是数据库表中的字段，只在业务中用到。
    private String avatar; // 用户头像地址或名称，类型为字符串。

}
