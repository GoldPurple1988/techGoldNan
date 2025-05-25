package com.univhis.user.auth.common;

import cn.hutool.core.util.StrUtil; // 导入 Hutool 工具包中的字符串工具类
import com.auth0.jwt.JWT; // 导入 JWT 库中的 JWT 类，用于解析和创建 JWT
import com.auth0.jwt.JWTVerifier; // 导入 JWT 库中的 JWTVerifier 类，用于验证 JWT
import com.auth0.jwt.algorithms.Algorithm; // 导入 JWT 库中的 Algorithm 类，用于指定 JWT 签名算法
import com.auth0.jwt.exceptions.JWTDecodeException; // 导入 JWT 库中 JWT 解码异常类
import com.auth0.jwt.exceptions.JWTVerificationException; // 导入 JWT 库中 JWT 验证异常类
import com.baomidou.mybatisplus.core.toolkit.Wrappers; // 导入 MyBatis-Plus 中的 Wrappers 工具类，用于构建查询条件
import com.univhis.entity.User; // 导入用户实体类
import com.univhis.exception.CustomException; // 导入自定义异常类
import com.univhis.user.auth.service.UserService; // 导入用户服务接口或实现类，用于查询用户信息
import org.springframework.web.servlet.HandlerInterceptor; // 导入 Spring MVC 的 HandlerInterceptor 接口

import javax.annotation.Resource; // 导入 Java EE 资源注入注解
import javax.servlet.http.HttpServletRequest; // 导入 Servlet API 中的 HttpServletRequest，用于获取 HTTP 请求信息
import javax.servlet.http.HttpServletResponse; // 导入 Servlet API 中的 HttpServletResponse，用于设置 HTTP 响应信息

/**
 * 拦截器
 * 这是一个 Spring MVC 拦截器，用于在请求处理之前进行身份验证和权限校验
 */
public class AuthInterceptor implements HandlerInterceptor { // 定义 AuthInterceptor 类，实现 HandlerInterceptor 接口

    @Resource // 自动注入 UserService 实例
    private UserService userService; // 声明用户服务实例

    @Override // 重写 HandlerInterceptor 接口的 preHandle 方法
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) { // 在请求处理之前进行预处理
        String token = request.getHeader("token"); // 从 HTTP 请求头中获取名为 "token" 的值

        if (StrUtil.isBlank(token)) { // 判断 token 是否为空或只包含空白字符
            throw new CustomException("401", "未获取到token, 请重新登录"); // 如果 token 不存在，抛出自定义异常，提示未登录
        }

        String username; // 声明用于存储用户名的变量
        try { // 尝试解析 JWT 获取用户名
            username = JWT.decode(token).getAudience().get(0); // 解码 token，并获取 Audience（通常存储用户名）列表的第一个元素
        } catch (JWTDecodeException j) { // 捕获 JWT 解码异常
            throw new CustomException("401", "权限验证失败, 请重新登录"); // 如果解码失败，抛出自定义异常，提示权限验证失败
        }

        // 根据用户名从数据库查询用户信息
        User user = userService.getOne(Wrappers.<User>lambdaQuery().eq(User::getUsername, username)); // 使用 MyBatis-Plus 的条件构造器查询用户
        if (user == null) { // 如果未找到用户
            throw new CustomException("401", "用户不存在, 请重新登录"); // 抛出自定义异常，提示用户不存在
        }

        // 验证 token
        // 使用用户的密码作为 HMAC256 算法的密钥来构建 JWT 验证器
        JWTVerifier jwtVerifier = JWT.require(Algorithm.HMAC256(user.getPassword())).build();
        try { // 尝试验证 token
            jwtVerifier.verify(token); // 使用验证器对 token 进行校验
        } catch (JWTVerificationException e) { // 捕获 JWT 验证异常
            throw new CustomException("401", "token不合法, 请重新登录"); // 如果验证失败，抛出自定义异常，提示 token 不合法
        }

        return true; // 如果所有验证通过，返回 true，表示请求继续处理
    }

}
