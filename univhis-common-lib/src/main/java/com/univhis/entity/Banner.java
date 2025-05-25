package com.univhis.entity;

import lombok.Data;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.baomidou.mybatisplus.annotation.TableId;

@Data // 应用Lombok的@Data注解，为该类自动生成getter/setter等常用方法
@TableName("banner") // 指定该实体类对应的数据库表名为banner
public class Banner extends Model<Banner> { // 声明Banner实体类，并继承MyBatis-Plus的Model类，实现ActiveRecord模式

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO) // 标注主键字段，指定为自增主键
    private Long id; // 轮播图的主键id，对应banner表的id字段

    /**
     * 图片地址
     */
    private String img; // 存储轮播图的图片地址（图片的URL或存储路径）

    /**
     * 关联url
     */
    private String url; // 存储轮播图点击后的跳转链接或关联的URL

}
