package com.univhis.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.univhis.entity.Message;
import com.univhis.entity.User;
import com.univhis.mapper.MessageMapper;
import com.univhis.user.auth.service.UserService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service // 使用 Spring 的 @Service 注解，将 MessageService 标记为一个服务组件，纳入 Spring 容器管理
public class MessageService extends ServiceImpl<MessageMapper, Message> { // MessageService 继承自 ServiceImpl，实现了 Message 实体类的通用 CRUD 操作，并指定了对应的 Mapper 为 MessageMapper

    @Resource // 使用 JSR-250 规范中的 @Resource 注解，将 MessageMapper 类型的 Bean 注入到 MessageService 中
    private MessageMapper messageMapper; // 声明私有的 MessageMapper 类型的成员变量 messageMapper，用于进行数据库操作

    @Resource // 使用 JSR-250 规范中的 @Resource 注解，将 UserService 类型的 Bean 注入到 MessageService 中
    private UserService userService; // 声明私有的 UserService 类型的成员变量 userService，用于调用 UserService 中的方法

    /**
     * 根据外键ID查询消息列表，并封装相关用户信息和父消息
     * @param foreignId 外键ID
     * @return 包含用户信息和父消息的消息列表
     */
    public List<Message> findByForeign(Long foreignId) { // 定义一个公共方法 findByForeign，接收外键ID作为参数，返回消息列表
        // 使用 MyBatis-Plus 的 LambdaQueryWrapper 构建查询条件，根据外键ID查询消息，并按照ID降序排列
        LambdaQueryWrapper<Message> queryWrapper = Wrappers.<Message>lambdaQuery().eq(Message::getForeignId, foreignId).orderByDesc(Message::getId);
        List<Message> list = list(queryWrapper); // 调用父类 ServiceImpl 的 list 方法，根据查询条件获取消息列表
        for (Message Message : list) { // 遍历消息列表
            User one = userService.getOne(Wrappers.<User>lambdaQuery().eq(User::getUsername, Message.getUsername())); // 根据消息中的用户名查询对应的用户
            Message.setAvatar("http://localhost:9999/files/" + one.getAvatar()); // 设置消息发送者的头像 URL
            Long parentId = Message.getParentId(); // 获取消息的父消息ID
            list.stream().filter(c -> c.getId().equals(parentId)).findFirst().ifPresent(Message::setParentMessage); // 从消息列表中找到父消息，并设置到当前消息对象中
        }
        return list; // 返回处理后的消息列表
    }
}

