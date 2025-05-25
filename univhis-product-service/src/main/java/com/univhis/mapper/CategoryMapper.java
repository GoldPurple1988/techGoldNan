package com.univhis.mapper;

import com.univhis.entity.Category;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

@Mapper // 使用 MyBatis 的 @Mapper 注解，将 CategoryMapper 接口标记为一个 MyBatis 的 Mapper 接口，方便 Spring 容器进行管理和扫描。
public interface CategoryMapper extends BaseMapper<Category> { // 定义 CategoryMapper 接口，并使其继承自 MyBatis-Plus 提供的 BaseMapper 接口。通过继承 BaseMapper<Category>，该接口自动拥有了对 Category 实体类对应的数据库表进行基本 CRUD（创建、读取、更新、删除）操作的能力。
}

