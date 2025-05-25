package com.univhis.mapper;

import com.univhis.entity.Cart;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

@Mapper // 使用 MyBatis 的 @Mapper 注解，将 CartMapper 接口标识为一个 Mapper 接口，供 Spring 容器扫描并管理其生命周期。
public interface CartMapper extends BaseMapper<Cart> { // 定义 CartMapper 接口，并使其继承自 MyBatis-Plus 提供的 BaseMapper 接口。
    // BaseMapper<Cart> 提供了针对 Cart 实体类的常用数据库操作方法（如增、删、改、查）。
    // 这里的 <Cart> 表明了这个 Mapper 接口是专门用来操作 Cart 实体类对应的数据库表的。
}
