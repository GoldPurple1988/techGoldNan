package com.univhis.entity;

import lombok.Data;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.baomidou.mybatisplus.annotation.TableId;


@Data // Lombok 注解。它会自动为类中的所有字段生成 getter 方法、为所有非 final 字段生成 setter 方法，并生成 toString(), equals(), hashCode() 方法以及一个接收所有 final 字段的构造函数。
@TableName("collect") // MyBatis-Plus 注解。它指定了这个实体类 Collect 对应数据库中的表名为 "collect"。如果表名与类名（忽略大小写）相同，则可以省略此注解。
public class Collect extends Model<Collect> { // 定义一个公共的（public）Java 类，类名为 Collect。这个类继承了 MyBatis-Plus 的 Model<Collect> 类，这意味着 Collect 类的实例将具备 ActiveRecord 功能，可以直接进行数据库操作。
    /**
     * 主键 // 描述：这个字段是表的主键。
     */
    @TableId(value = "id", type = IdType.AUTO) // MyBatis-Plus 注解，用于标识 id 属性是表的主键。
    // value = "id" 指定了数据库表中主键列的名称是 "id"。
    // type = IdType.AUTO 指定了主键的生成策略为数据库自增。
    private Long id; // 声明一个私有的（private）长整型（Long）属性，名为 id。它将存储收藏记录的唯一标识符。

    /**
     * 商品名称  // 描述：这个字段存储收藏的商品名称。
     */
    private String goodsName; // 声明一个私有的（private）字符串类型（String）属性，名为 goodsName。它将存储收藏的商品名称。

    /**
     * 商品图片  // 描述：这个字段存储收藏的商品图片的URL或路径。
     */
    private String goodsImg; // 声明一个私有的（private）字符串类型（String）属性，名为 goodsImg。它将存储收藏的商品图片的链接或标识。

    /**
     * 商品id  // 描述：这个字段存储被收藏商品的唯一标识符。
     */
    private String goodsId; // 声明一个私有的（private）字符串类型（String）属性，名为 goodsId。它将存储关联的商品的ID。

    /**
     * 用户id  // 描述：这个字段存储进行收藏操作的用户的唯一标识符。
     */
    private String userId; // 声明一个私有的（private）字符串类型（String）属性，名为 userId。它将存储进行收藏操作的用户的ID。

    /**
     * 收藏时间  // 描述：这个字段存储该商品被收藏的时间。
     */
    private String createTime; // 声明一个私有的（private）字符串类型（String）属性，名为 createTime。它将存储这条收藏记录的创建时间。

}
