package com.univhis.entity; // 声明该类所在的包名

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.baomidou.mybatisplus.annotation.TableId;

@Data // 自动为类生成get/set方法、equals、hashCode和toString方法
@TableName("cart") // 指定该实体类对应的数据库表名为cart
public class Cart extends Model<Cart> { // Cart类继承MyBatis-Plus的Model类，泛型为自身，支持ActiveRecord操作
    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO) // 指定主键字段为id，主键自增
    private Long id; // 购物车唯一标识，对应数据库中的主键id

    /**
     * 商品id
     */
    private Long goodsId; // 对应加入购物车的商品的id

    /**
     * 用户id
     */
    private Long userId; // 对应加入购物车的用户的id

    /**
     * 商品数量
     */
    private Integer count; // 购物车中该商品的数量

    private String createTime; // 创建时间，记录购物车项的添加时间

    @TableField(exist = false) // 标注该字段在数据库表中不存在，仅用于业务扩展
    private Goods goods; // 商品对象，封装详细的商品信息，便于业务层直接使用

}