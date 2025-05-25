package com.univhis.service;

import com.univhis.entity.Category;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.univhis.mapper.CategoryMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service // 使用 Spring 的 @Service 注解，将 CategoryService 标记为一个服务组件，纳入 Spring 容器管理
public class CategoryService extends ServiceImpl<CategoryMapper, Category> { // CategoryService 继承自 ServiceImpl，实现了 Category 实体类的通用 CRUD 操作，并指定了对应的 Mapper 为 CategoryMapper

    @Resource // 使用 JSR-250 规范中的 @Resource 注解，将 CategoryMapper 类型的 Bean 注入到 CategoryService 中
    private CategoryMapper categoryMapper; // 声明私有的 CategoryMapper 类型的成员变量 categoryMapper，用于进行数据库操作

}


