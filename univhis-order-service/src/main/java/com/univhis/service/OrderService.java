package com.univhis.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.univhis.entity.Order;
import com.univhis.mapper.OrderMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service // 使用 Spring 的 @Service 注解，将 OrderService 标记为一个服务组件，纳入 Spring 容器管理
public class OrderService extends ServiceImpl<OrderMapper, Order> { // OrderService 继承自 ServiceImpl，实现了 Order 实体类的通用 CRUD 操作，并指定了对应的 Mapper 为 OrderMapper

    @Resource // 使用 JSR-250 规范中的 @Resource 注解，将 OrderMapper 类型的 Bean 注入到 OrderService 中
    private OrderMapper orderMapper; // 声明私有的 OrderMapper 类型的成员变量 orderMapper，用于进行数据库操作

}

