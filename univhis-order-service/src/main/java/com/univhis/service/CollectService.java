package com.univhis.service;

import com.univhis.entity.Collect;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.univhis.mapper.CollectMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service // 使用 Spring 的 @Service 注解，将 CollectService 标记为一个服务组件，纳入 Spring 容器管理
public class CollectService extends ServiceImpl<CollectMapper, Collect> { // CollectService 继承自 ServiceImpl，实现了 Collect 实体类的通用 CRUD 操作，并指定了对应的 Mapper 为 CollectMapper

    @Resource // 使用 JSR-250 规范中的 @Resource 注解，将 CollectMapper 类型的 Bean 注入到 CollectService 中
    private CollectMapper collectMapper; // 声明私有的 CollectMapper 类型的成员变量 collectMapper，用于进行数据库操作

}

