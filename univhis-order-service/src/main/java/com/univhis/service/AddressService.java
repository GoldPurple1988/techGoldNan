package com.univhis.service;

import com.univhis.entity.Address;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.univhis.mapper.AddressMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service // 使用 Spring 的 @Service 注解，将 AddressService 标记为一个服务组件，纳入 Spring 容器管理
public class AddressService extends ServiceImpl<AddressMapper, Address> { // AddressService 继承自 ServiceImpl，实现了 Address 实体类的通用 CRUD 操作，并指定了对应的 Mapper 为 AddressMapper

    @Resource // 使用 JSR-250 规范中的 @Resource 注解，将 AddressMapper 类型的 Bean 注入到 AddressService 中
    private AddressMapper addressMapper; // 声明私有的 AddressMapper 类型的成员变量 addressMapper，用于进行数据库操作

}


