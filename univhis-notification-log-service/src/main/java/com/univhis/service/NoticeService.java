package com.univhis.service;

import com.univhis.entity.Notice;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.univhis.mapper.NoticeMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service // 使用 Spring 的 @Service 注解，将 NoticeService 标记为一个服务组件，纳入 Spring 容器管理
public class NoticeService extends ServiceImpl<NoticeMapper, Notice> { // NoticeService 继承自 ServiceImpl，实现了 Notice 实体类的通用 CRUD 操作，并指定了对应的 Mapper 为 NoticeMapper

    @Resource // 使用 JSR-250 规范中的 @Resource 注解，将 NoticeMapper 类型的 Bean 注入到 NoticeService 中
    private NoticeMapper noticeMapper; // 声明私有的 NoticeMapper 类型的成员变量 noticeMapper，用于进行数据库操作

}

