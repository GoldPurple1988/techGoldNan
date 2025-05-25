// 定义了该Java文件所属的包名，通常用于组织和管理Java类
package com.univhis.entity;

// 引入MyBatis-Plus的@TableField注解，用于标识非主键的数据库表字段，或指定字段的特殊属性（如exist=false表示非表字段）
import com.baomidou.mybatisplus.annotation.TableField;
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

// 引入Java的BigDecimal类，用于精确表示和计算货币金额等浮点数
import java.math.BigDecimal;
// 引入Java的List接口，用于表示集合列表（虽然在此Order类中未直接使用List类型的字段，但通常会引入以备不时之需或子类使用）
import java.util.List;

// Lombok注解：自动为此类生成getter, setter, toString, equals, hashCode等标准方法
@Data
// MyBatis-Plus注解：将此Java类映射到数据库中的 "t_order" 表
@TableName("t_order")
// 定义一个名为 Order 的公共类，它继承自 MyBatis-Plus 的 Model<Order> 类，从而支持ActiveRecord模式
public class Order extends Model<Order> {
    /**
     * 主键
     */
    // MyBatis-Plus注解：标识 'id' 字段为表的主键，并设置其值为 "id"，类型为自增长 (IdType.AUTO)
    @TableId(value = "id", type = IdType.AUTO)
    // 定义一个私有的长整型 (Long) 变量 'id'，作为订单的主键
    private Long id;

    /**
     * 订单编号
     */
    // 定义一个私有的字符串 (String) 变量 'orderNo'，用于存储订单的唯一编号
    private String orderNo;

    /**
     * 总价
     */
    // 定义一个私有的 BigDecimal 变量 'totalPrice'，用于存储订单的总金额，使用BigDecimal保证精度
    private BigDecimal totalPrice;

    /**
     * 下单人id
     */
    // 定义一个私有的长整型 (Long) 变量 'userId'，用于存储下单用户的ID
    private Long userId;

    /**
     * 联系人
     */
    // 定义一个私有的字符串 (String) 变量 'linkUser'，用于存储订单的联系人姓名
    private String linkUser;

    /**
     * 联系电话
     */
    // 定义一个私有的字符串 (String) 变量 'linkPhone'，用于存储订单的联系人电话
    private String linkPhone;

    /**
     * 送货地址
     */
    // 定义一个私有的字符串 (String) 变量 'linkAddress'，用于存储订单的送货地址
    private String linkAddress;

    /**
     * 状态
     */
    // 定义一个私有的字符串 (String) 变量 'state'，用于存储订单的当前状态 (例如：待支付、已发货、已完成等)
    private String state;

    /**
     * 创建时间
     */
    // 定义一个私有的字符串 (String) 变量 'createTime'，用于存储订单的创建时间 (注意：通常建议使用 Date 或 LocalDateTime 类型)
    private String createTime;

    // MyBatis-Plus注解：标识 'carts' 字段不是数据库表中的一个列 (exist = false)
    @TableField(exist = false)
    // 定义一个私有的字符串 (String) 变量 'carts'，可能用于在创建订单时临时存储购物车信息（JSON格式）
    private String carts;

    // MyBatis-Plus注解：标识 'type' 字段不是数据库表中的一个列 (exist = false)
    @TableField(exist = false)
    // 定义一个私有的整型 (Integer) 变量 'type'，可能用于区分订单类型或操作类型，不在数据库中持久化
    private Integer type;

}
