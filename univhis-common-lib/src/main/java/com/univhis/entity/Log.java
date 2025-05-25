package com.univhis.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;

@Data // Lombok 注解：自动为当前类生成 Getter/Setter、toString、equals 和 hashCode 等方法，简化代码编写。
@TableName("t_log") // MyBatis-Plus 注解：指定当前实体类对应的数据库表名为 "t_log"。
public class Log extends Model<Log> { // 定义一个公共类 Log，继承自 MyBatis-Plus 提供的 Model<Log>，支持 ActiveRecord 风格的数据库操作。

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO) // MyBatis-Plus 注解：指定 id 字段为主键，主键类型为自增（AUTO）。
    private Long id; // 定义主键属性，类型为 Long，对应数据库表的主键 id 列。

    /**
     * 操作内容
     */
    private String content; // 定义操作内容属性，类型为 String，记录操作的具体内容。

    /**
     * 操作时间
     */
    private String time; // 定义操作时间属性，类型为 String，记录操作发生的时间。

    /**
     * 操作人
     */
    private String user; // 定义操作人属性，类型为 String，记录进行该操作的用户信息。

    private String ip; // 定义 IP 属性，类型为 String，记录操作时的 IP 地址。

}
