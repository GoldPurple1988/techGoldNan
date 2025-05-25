package com.univhis.mapper;

import com.univhis.entity.Collect;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

@Mapper // 使用 MyBatis 的 @Mapper 注解，将 CollectMapper 接口标记为一个 MyBatis 映射器（Mapper），MyBatis 会通过该注解扫描到这个接口，并为其生成代理对象，从而实现与数据库的交互。同时，这个注解也使得 Spring 能够管理该接口的实例。
public interface CollectMapper extends BaseMapper<Collect> { // 定义 CollectMapper 接口，并使其继承自 MyBatis-Plus 提供的 BaseMapper<Collect> 接口。BaseMapper 是 MyBatis-Plus 提供的通用 Mapper 接口，它内置了对 Collect 实体类对应的数据库表进行基本 CRUD（创建、读取、更新、删除）操作的常用方法，开发者可以直接使用这些方法，无需编写重复的 SQL 语句。
}
