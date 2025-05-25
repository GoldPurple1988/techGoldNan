package com.univhis.service;

import cn.hutool.core.date.DateUtil;
import com.auth0.jwt.JWT;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.univhis.entity.Log;
import com.univhis.entity.User;
import com.univhis.mapper.LogMapper;
import com.univhis.user.auth.service.UserService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;

@Service // 使用 Spring 的 @Service 注解，将 LogService 标记为一个服务组件，纳入 Spring 容器管理
public class LogService extends ServiceImpl<LogMapper, Log> { // LogService 继承自 ServiceImpl，实现了 Log 实体类的通用 CRUD 操作，并指定了对应的 Mapper 为 LogMapper

    @Resource // 使用 JSR-250 规范中的 @Resource 注解，将 LogMapper 类型的 Bean 注入到 LogService 中
    private LogMapper logMapper; // 声明私有的 LogMapper 类型的成员变量 logMapper，用于进行数据库操作

    @Resource // 使用 JSR-250 规范中的 @Resource 注解，将 HttpServletRequest 类型的 Bean 注入到 LogService 中
    private HttpServletRequest request; // 声明私有的 HttpServletRequest 类型的成员变量 request，用于获取 HTTP 请求信息

    @Resource // 使用 JSR-250 规范中的 @Resource 注解，将 UserService 类型的 Bean 注入到 LogService 中
    private UserService userService; // 声明私有的 UserService 类型的成员变量 userService，用于调用 UserService 中的方法

    /**
     * 获取当前登录用户
     * @return 当前登录用户对象，如果获取失败则返回 null
     */
    public User getUser() { // 定义一个公共方法 getUser，用于获取当前登录用户
        try {
            String token = request.getHeader("token"); // 从 HTTP 请求头中获取名为 "token" 的值，通常是用户的身份验证令牌
            String username = JWT.decode(token).getAudience().get(0); // 使用 JWT 库解码令牌，并从中获取用户名
            return userService.getOne(Wrappers.<User>lambdaQuery().eq(User::getUsername, username)); // 调用 userService 的 getOne 方法，根据用户名查询用户，并返回用户对象
        } catch (Exception e) { // 捕获异常，例如令牌无效或解析失败
            return null; // 如果获取用户信息失败，则返回 null
        }
    }

    /**
     * 记录日志
     * @param content 日志内容
     */
    public void log(String content) { // 定义一个公共方法 log，接收日志内容作为参数，用于记录日志
        Log log = new Log(); // 创建一个新的 Log 对象
        log.setUser(getUser().getUsername()); // 设置日志记录的用户名为当前登录用户的用户名
        log.setTime(DateUtil.formatDateTime(new Date())); // 设置日志记录时间为当前时间，并格式化为字符串
        log.setIp(getIpAddress()); // 设置日志记录的 IP 地址
        log.setContent(content); // 设置日志内容
        save(log); // 调用父类 ServiceImpl 的 save 方法，将日志信息保存到数据库
    }

    /**
     * 记录日志
     * @param username 用户名
     * @param content  日志内容
     */
    public void log(String username, String content) { // 定义一个公共方法 log，接收用户名和日志内容作为参数，用于记录日志
        Log log = new Log(); // 创建一个新的 Log 对象
        log.setUser(username);    // 设置日志记录的用户
        log.setTime(DateUtil.formatDateTime(new Date())); // 设置日志记录时间
        log.setIp(getIpAddress()); // 设置日志记录的 IP 地址
        log.setContent(content); // 设置日志内容
        save(log);  // 保存日志信息
    }

    /**
     * 获取 IP 地址
     * @return 请求的 IP 地址
     */
    public String getIpAddress() { // 定义一个公共方法 getIpAddress，用于获取请求的 IP 地址

        String ip = request.getHeader("x-forwarded-for"); // 尝试从 "x-forwarded-for" 请求头中获取 IP 地址，通常用于代理服务器

        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) { // 如果 "x-forwarded-for" 为空或未知
            ip = request.getHeader("Proxy-Client-IP"); // 尝试从 "Proxy-Client-IP" 请求头中获取 IP 地址，用于代理服务器
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) { // 如果 "Proxy-Client-IP" 为空或未知
            ip = request.getHeader("WL-Proxy-Client-IP"); // 尝试从 "WL-Proxy-Client-IP" 请求头中获取 IP 地址，用于 WebLogic 代理服务器
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) { // 如果以上都为空或未知
            ip = request.getRemoteAddr(); // 使用 request.getRemoteAddr() 获取直接连接的客户端 IP 地址
        }
        return ip; // 返回获取到的 IP 地址
    }
}
