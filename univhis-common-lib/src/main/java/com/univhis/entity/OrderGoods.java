// 定义了该Java文件所属的包名，用于组织和管理Java类
package com.univhis.entity;

// 引入Lombok库的@Data注解，它会自动为类生成getter、setter、equals、hashCode、toString等方法
import lombok.Data;
// 引入MyBatis-Plus的@TableName注解，用于指定实体类对应的数据库表名
import com.baomidou.mybatisplus.annotation.TableName;
// 引入MyBatis-Plus的IdType枚举，用于指定主键的生成策略（如自增长、UUID等）
import com.baomidou.mybatisplus.annotation.IdType;
// 引入MyBatis-Plus的Model类，实体类继承它可以开启ActiveRecord模式，使实体对象拥有CRUD操作能力
import com.baomidou.mybatisplus.extension.activerecord.Model;
// 引入MyBatis-Plus的@TableId注解，用于标识实体类的主键字段
import com.baomidou.mybatisplus.annotation.TableId;


// Lombok注解：自动为此类生成getter, setter, toString, equals, hashCode等标准方法
@Data
// MyBatis-Plus注解：将此Java类映射到数据库中的 "order_goods" 表，这通常是订单和商品之间的关联表（多对多关系）
@TableName("order_goods")
// 定义一个名为 OrderGoods 的公共类，它继承自 MyBatis-Plus 的 Model<OrderGoods> 类，从而支持ActiveRecord模式
public class OrderGoods extends Model<OrderGoods> {
    /**
     * 主键
     */
    // MyBatis-Plus注解：标识 'id' 字段为表的主键，并设置其值为 "id"，类型为自增长 (IdType.AUTO)
    @TableId(value = "id", type = IdType.AUTO)
    // 定义一个私有的长整型 (Long) 变量 'id'，作为订单商品关联记录的主键
    private Long id;

    /**
     * 订单id
     */
    // 定义一个私有的长整型 (Long) 变量 'orderId'，用于存储关联的订单ID
    private Long orderId;

    /**
     * 商品id
     */
    // 定义一个私有的长整型 (Long) 变量 'goodsId'，用于存储关联的商品ID
    private Long goodsId;

    /**
     * 数量
     */
    // 定义一个私有的整型 (Integer) 变量 'count'，用于存储该订单中对应商品的数量
    private Integer count;

}
