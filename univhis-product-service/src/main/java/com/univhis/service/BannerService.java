package com.univhis.service;

import com.univhis.entity.Banner;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.univhis.mapper.BannerMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service // 使用 Spring 的 @Service 注解，将 BannerService 标记为一个服务组件，纳入 Spring 容器管理
public class BannerService extends ServiceImpl<BannerMapper, Banner> { // BannerService 继承自 ServiceImpl，实现了 Banner 实体类的通用 CRUD 操作，并指定了对应的 Mapper 为 BannerMapper

    @Resource // 使用 JSR-250 规范中的 @Resource 注解，将 BannerMapper 类型的 Bean 注入到 BannerService 中
    private BannerMapper bannerMapper; // 声明私有的 BannerMapper 类型的成员变量 bannerMapper，用于进行数据库操作

}

