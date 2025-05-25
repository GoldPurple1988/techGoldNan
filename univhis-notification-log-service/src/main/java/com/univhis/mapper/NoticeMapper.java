package com.univhis.mapper;

import com.univhis.entity.Notice;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

@Mapper // 使用 MyBatis 的 @Mapper 注解，将 NoticeMapper 接口标记为一个 MyBatis 映射器（Mapper），MyBatis 框架会扫描并管理带有此注解的接口，使其能够与数据库进行交互。同时，Spring 容器也能识别并管理该接口的实例。
public interface NoticeMapper extends BaseMapper<Notice> { // 定义 NoticeMapper 接口，并使其继承自 MyBatis-Plus 提供的 BaseMapper<Notice> 接口。BaseMapper 是 MyBatis-Plus 提供的通用 Mapper 接口，它内置了针对 Notice 实体类对应的数据库表进行基本 CRUD（创建、读取、更新、删除）操作的常用方法，开发者可以直接使用这些方法而无需编写重复的 SQL 语句。
}
