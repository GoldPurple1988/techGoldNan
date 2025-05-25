package com.univhis.user.auth.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.univhis.entity.Role;
import com.univhis.user.auth.mapper.RoleMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource; // 导入 JSR-250 规范中的 @Resource 注解，用于依赖注入

@Service // 使用 Spring 的 @Service 注解，将 RoleService 类注册到 Spring 容器中，使其成为一个可被 Spring 管理的 Bean
public class RoleService extends ServiceImpl<RoleMapper, Role> { // RoleService 继承自 ServiceImpl，实现了 Role 实体类的通用 CRUD 操作，并指定了对应的 Mapper 为 RoleMapper

    @Resource // 使用 @Resource 注解，将 RoleMapper 类型的 Bean 注入到 RoleService 中
    private RoleMapper roleMapper; // 声明私有的 RoleMapper 类型的成员变量 roleMapper，用于进行数据库操作

}

