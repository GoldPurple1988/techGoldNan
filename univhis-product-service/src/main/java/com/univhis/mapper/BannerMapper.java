package com.univhis.mapper;

import com.univhis.entity.Banner;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

@Mapper // 使用 MyBatis 的 @Mapper 注解，将 BannerMapper 接口声明为一个 MyBatis 映射器（Mapper），使得 MyBatis 能够代理该接口的方法，并将其与 XML 配置文件或注解中定义的 SQL 语句关联起来。同时，Spring 容器也能识别并管理这个 Mapper 接口的实例。
public interface BannerMapper extends BaseMapper<Banner> { // 定义 BannerMapper 接口，使其继承自 MyBatis-Plus 提供的 BaseMapper<Banner> 接口。BaseMapper 是 MyBatis-Plus 提供的通用 Mapper 接口，它包含了对 Banner 实体类对应的数据库表进行基本 CRUD（创建、读取、更新、删除）操作的常用方法，从而减少了开发者编写重复 SQL 的工作量。
}
