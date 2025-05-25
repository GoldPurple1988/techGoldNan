package com.univhis.user.auth.service;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.univhis.common.Result; // Import common Result
import com.univhis.entity.Permission;
import com.univhis.entity.Role;
import com.univhis.entity.User;
import com.univhis.exception.CustomException;
import com.univhis.user.auth.mapper.UserMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate; // Import RestTemplate
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import java.util.LinkedHashMap;
import java.util.Objects;


import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service
public class UserService extends ServiceImpl<UserMapper, User> {

    @Resource
    private UserMapper userMapper;

    @Resource
    private RestTemplate restTemplate; // Inject RestTemplate

    // RoleService and PermissionService are within the same microservice (user-auth-service)
    // So, direct injection is fine. If they were separate, they would be called via RestTemplate.
    @Resource
    private RoleService roleService;
    @Resource
    private PermissionService permissionService;

    private static final String USER_AUTH_SERVICE_NAME = "univhis-user-auth-service"; // Self-reference for clarity

    /**
     * 用户登录
     * @param user 包含用户名和密码的用户对象
     * @return 登录成功的用户对象，包含权限信息
     */
    public User login(User user) {
        LambdaQueryWrapper<User> queryWrapper = Wrappers.<User>lambdaQuery().eq(User::getUsername, user.getUsername()).eq(User::getPassword, user.getPassword());
        User one = getOne(queryWrapper);
        if (one == null) {
            throw new CustomException("-1", "账号或密码错误");
        }
        one.setPermission(getPermissions(one.getId()));
        return one;
    }

    /**
     * 用户注册
     * @param user 包含用户注册信息的用户对象
     * @return 注册成功的用户对象
     */
    public User register(User user) {
        User one = getOne((Wrappers.<User>lambdaQuery().eq(User::getUsername, user.getUsername())));
        if (one != null) {
            throw new CustomException("-1", "用户已注册");
        }
        if (user.getPassword() == null) {
            user.setPassword("123456");
        }
        user.setRole(CollUtil.newArrayList(2L));  // 设置默认角色为普通用户 (角色ID为 2)
        save(user);
        return getOne((Wrappers.<User>lambdaQuery().eq(User::getUsername, user.getUsername())));
    }

    /**
     * 获取用户权限列表
     * @param userId 用户ID
     * @return 用户的权限列表
     */
    public List<Permission> getPermissions(Long userId) {
        User user = getById(userId);
        List<Permission> permissions = new ArrayList<>();
        List<Long> roleIds = user.getRole(); // Changed variable name to roleIds for clarity
        if (roleIds != null) {
            for (Object roleIdObj : roleIds) { // Iterate over Object as it might be stored as an Object
                Long roleId = Long.valueOf(roleIdObj.toString());

                // Call RoleService to get the role details.
                // Since RoleService is in the same microservice, direct injection is fine.
                Role realRole = roleService.getById(roleId);

                if (realRole != null && CollUtil.isNotEmpty(realRole.getPermission())) {
                    for (Object permissionIdObj : realRole.getPermission()) {
                        Long permissionId = Long.valueOf(permissionIdObj.toString());

                        // Call PermissionService to get permission details.
                        // Since PermissionService is in the same microservice, direct injection is fine.
                        Permission permission = permissionService.getById(permissionId);
                        if (permission != null && permissions.stream().noneMatch(p -> p.getPath().equals(permission.getPath()))) {
                            permissions.add(permission);
                        }
                    }
                }
            }
            user.setPermission(permissions);
        }
        return permissions;
    }

    /**
     * 根据用户名获取用户
     * @param username 用户名
     * @return 用户对象，包含权限信息
     */
    public User getbyUsername(String username) {
        User one = getOne((Wrappers.<User>lambdaQuery().eq(User::getUsername, username)));
        if (one != null) {
            one.setPermission(getPermissions(one.getId()));
        }
        return one;
    }

    /**
     * 根据用户ID获取用户
     * @param id 用户ID
     * @return 用户对象，包含权限信息
     */
    public User findById(Long id) {
        User user = getById(id);
        if (user != null) {
            user.setPermission(getPermissions(id));
        }
        return user;
    }
}