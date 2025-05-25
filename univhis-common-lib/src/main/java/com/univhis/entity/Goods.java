package com.univhis.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.univhis.common.handler.ListHandler;
import lombok.Data;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.baomidou.mybatisplus.annotation.TableId;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.type.JdbcType;

import java.math.BigDecimal;
import java.util.List;

// Lombok 注解：自动生成所有属性的 getter、setter、toString等方法
@Data
// MyBatis-Plus 注解：指定该实体对应的数据库表名为 "goods"，autoResultMap = true 支持复杂类型自动映射
@TableName(value = "goods", autoResultMap = true)
// 定义商品实体类，继承自 MyBatis-Plus 的 Model，实现 ActiveRecord 模式
public class Goods extends Model<Goods> {
    /**
     * 主键
     */
    // MyBatis-Plus 注解：指定 id 字段为主键，并采用自增策略
    @TableId(value = "id", type = IdType.AUTO)
    // 商品主键ID
    private Long id;

    /**
     * 商品名称
     */
    // 商品名称
    private String name;

    /**
     * 商品描述
     */
    // 商品描述信息
    private String description;

    /**
     * 商品编号
     */
    // 商品唯一编号
    private String no;

    /**
     * 原价
     */
    // 商品原价（BigDecimal类型，保证价格精度）
    private BigDecimal price;

    // 标记为非数据库表字段，仅作为业务临时字段使用
    @TableField(exist = false)
    // 商品实际销售价格（临时字段，根据折扣等计算得出）
    private BigDecimal realPrice;

    /**
     * 折扣
     */
    // 商品折扣（如 0.8 表示打八折）
    private Double discount;

    /**
     * 库存
     */
    // 商品库存数量
    private Integer store;

    /**
     * 点赞数
     */
    // 商品被点赞的数量
    private Integer praise;

    /**
     * 销量
     */
    // 商品销售数量
    private Integer sales;

    /**
     * 分类id
     */
    // 商品所属分类的ID
    private Long categoryId;

    // 标记为非数据库表字段，仅作为业务临时字段使用
    @TableField(exist = false)
    // 商品所属分类的名称（临时字段，便于前端展示）
    private String categoryName;

    /**
     * 商品图片
     */
    // 商品图片地址或图片集（通常存储为逗号分隔的字符串或JSON）
    private String imgs;

    /**
     * 创建时间
     */
    // 商品创建时间（字符串，建议实际项目用 Date 或 LocalDateTime 类型）
    private String createTime;

    // 商品是否为推荐商品（true为推荐，false为普通）
    private Boolean recommend;

}
