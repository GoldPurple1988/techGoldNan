package com.univhis.user.auth.service;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.univhis.entity.Permission;
import com.univhis.entity.Role;
import com.univhis.entity.User;
import com.univhis.exception.CustomException;
import com.univhis.user.auth.mapper.UserMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service // 使用 Spring 的 @Service 注解，将 UserService 标记为一个服务组件，纳入 Spring 容器管理
public class UserService extends ServiceImpl<UserMapper, User> { // UserService 继承自 ServiceImpl，实现了 User 实体类的通用 CRUD 操作，并指定了对应的 Mapper 为 UserMapper

    @Resource // 使用 JSR-250 规范中的 @Resource 注解，将 UserMapper 类型的 Bean 注入到 UserService 中
    private UserMapper userMapper; // 声明私有的 UserMapper 类型的成员变量 userMapper，用于进行数据库操作

    @Resource // 使用 JSR-250 规范中的 @Resource 注解，将 RoleService 类型的 Bean 注入到 UserService 中
    private RoleService roleService; // 声明私有的 RoleService 类型的成员变量 roleService，用于调用 RoleService 中的方法

    @Resource // 使用 JSR-250 规范中的 @Resource 注解，将 PermissionService 类型的 Bean 注入到 UserService 中
    private PermissionService permissionService; // 声明私有的 PermissionService 类型的成员变量 permissionService，用于调用 PermissionService 中的方法

    /**
     * 用户登录
     * @param user 包含用户名和密码的用户对象
     * @return 登录成功的用户对象，包含权限信息
     */
    public User login(User user) { // 定义一个公共方法 login，接收包含用户名和密码的用户对象作为参数，返回登录成功的用户对象
        // 使用 MyBatis-Plus 的 LambdaQueryWrapper 构建查询条件，根据用户名和密码查询用户
        LambdaQueryWrapper<User> queryWrapper = Wrappers.<User>lambdaQuery().eq(User::getUsername, user.getUsername()).eq(User::getPassword, user.getPassword());
        User one = getOne(queryWrapper); // 调用父类 ServiceImpl 的 getOne 方法，根据查询条件获取单个用户对象
        if (one == null) { // 如果查询结果为空，表示用户不存在或密码错误
            throw new CustomException("-1", "账号或密码错误"); // 抛出一个自定义异常，表示登录失败
        }
        one.setPermission(getPermissions(one.getId())); // 调用 getPermissions 方法，获取用户的权限列表，并设置到用户对象中
        return one; // 返回登录成功的用户对象，包含权限信息
    }

    /**
     * 用户注册
     * @param user 包含用户注册信息的用户对象
     * @return 注册成功的用户对象
     */
    public User register(User user) {  // 定义一个公共方法 register，接收包含用户注册信息的用户对象作为参数，返回注册成功的用户对象
        User one = getOne((Wrappers.<User>lambdaQuery().eq(User::getUsername, user.getUsername()))); // 检查用户名是否已存在
        if (one != null) {
            throw new CustomException("-1", "用户已注册");
        }
        if (user.getPassword() == null) { // 如果用户没有设置密码
            user.setPassword("123456"); // 设置默认密码为 "123456"
        }
        user.setRole(CollUtil.newArrayList(2L));  // 设置默认角色为普通用户 (角色ID为 2)
        save(user); // 调用父类 ServiceImpl 的 save 方法，保存用户注册信息到数据库
        return getOne((Wrappers.<User>lambdaQuery().eq(User::getUsername, user.getUsername()))); // 返回注册成功的用户对象
    }

    /**
     * 获取用户权限列表
     * @param userId 用户ID
     * @return 用户的权限列表
     */
    public List<Permission> getPermissions(Long userId) { // 定义一个公共方法 getPermissions，接收用户ID作为参数，返回用户的权限列表
        User user = getById(userId); // 根据用户ID获取用户对象
        List<Permission> permissions = new ArrayList<>(); // 创建一个空的权限列表
        List<Long> role = user.getRole(); // 获取用户拥有的角色ID列表
        if (role != null) { // 如果用户拥有角色
            for (Object roleId : role) { // 遍历用户的角色ID列表
                Role realRole = roleService.getById((int) roleId); // 根据角色ID获取角色对象
                if (CollUtil.isNotEmpty(realRole.getPermission())) { // 如果角色拥有权限
                    for (Object permissionId : realRole.getPermission()) { // 遍历角色拥有的权限ID列表
                        Permission permission = permissionService.getById((int) permissionId); // 根据权限ID获取权限对象
                        if (permission != null && permissions.stream().noneMatch(p -> p.getPath().equals(permission.getPath()))) { // 避免重复添加权限
                            permissions.add(permission); // 将权限添加到权限列表中
                        }
                    }
                }
            }
            user.setPermission(permissions); // 将权限列表设置到用户对象中
        }
        return permissions; // 返回用户的权限列表
    }

    /**
     * 根据用户名获取用户
     * @param username 用户名
     * @return 用户对象，包含权限信息
     */
    public User getbyUsername(String username) { // 定义一个公共方法 getbyUsername，接收用户名作为参数，返回包含权限信息的用户对象
        User one = getOne((Wrappers.<User>lambdaQuery().eq(User::getUsername, username))); // 根据用户名查询用户
        one.setPermission(getPermissions(one.getId())); // 获取用户的权限列表
        return one; // 返回用户对象
    }

    /**
     * 根据用户ID获取用户
     * @param id 用户ID
     * @return 用户对象，包含权限信息
     */
    public User findById(Long id) { // 定义一个公共方法 findById，接收用户ID作为参数，返回包含权限信息的用户对象
        User user = getById(id); // 根据用户ID获取用户对象
        user.setPermission(getPermissions(id)); // 获取用户的权限列表
        return user; // 返回用户对象
    }
}


