package com.univhis.entity;

import lombok.Data;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.baomidou.mybatisplus.annotation.TableId;

@Data // 使用Lombok的@Data注解，为所有字段自动生成getter/setter、equals、hashCode、toString等方法
@TableName("category") // 指定数据库表名为category
public class Category extends Model<Category> { // 定义Category实体类，继承Model类，实现ActiveRecord风格操作

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO) // 标识主键id字段，主键类型为自增
    private Long id; // 商品分类的主键id，对应category表的id字段

    /**
     * 分类名称
     */
    private String name; // 分类名称字段，用于存储该分类的名称

    /**
     * 分类编号
     */
    private String no; // 分类编号字段，用于存储该分类的编号

}
