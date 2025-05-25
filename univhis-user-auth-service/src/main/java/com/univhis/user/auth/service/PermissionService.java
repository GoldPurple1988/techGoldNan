package com.univhis.user.auth.service;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.univhis.entity.Permission;
import com.univhis.entity.Role;
import com.univhis.user.auth.mapper.PermissionMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service // 使用 Spring 的 @Service 注解，将 PermissionService 标记为一个服务组件，纳入 Spring 容器管理
public class PermissionService extends ServiceImpl<PermissionMapper, Permission> { // PermissionService 继承自 ServiceImpl，实现了 Permission 实体类的通用 CRUD 操作，并指定了对应的 Mapper 为 PermissionMapper

    @Resource // 使用 JSR-250 规范中的 @Resource 注解，将 PermissionMapper 类型的 Bean 注入到 PermissionService 中
    private PermissionMapper permissionMapper; // 声明私有的 PermissionMapper 类型的成员变量 permissionMapper，用于进行数据库操作

    @Resource // 使用 JSR-250 规范中的 @Resource 注解，将 RoleService 类型的 Bean 注入到 PermissionService 中
    private RoleService roleService; // 声明私有的 RoleService 类型的成员变量 roleService，用于调用 RoleService 中的方法

    /**
     * 根据角色列表获取权限列表
     * @param roles 角色列表
     * @return 权限列表
     */
    public List<Permission> getByRoles(List<Role> roles) { // 定义一个公共方法 getByRoles，接收角色列表作为参数，返回权限列表
        List<Permission> permissions = new ArrayList<>(); // 创建一个新的 ArrayList，用于存储获取到的权限
        for (Role role : roles) { // 遍历角色列表
            Role r = roleService.getById(role.getId()); // 根据角色 ID 从数据库中获取完整的角色信息（包含权限 ID 列表）
            if (CollUtil.isNotEmpty(r.getPermission())) { // 判断当前角色是否拥有权限
                for (Object permissionId : r.getPermission()) { // 遍历角色拥有的权限 ID 列表
                    Permission permission = getById((int) permissionId); // 根据权限 ID 从数据库中获取权限信息
                    // 使用 stream().noneMatch() 避免重复添加权限
                    if (permissions.stream().noneMatch(p -> p.getPath().equals(permission.getPath()))) {
                        permissions.add(permission); // 如果权限列表中不存在相同路径的权限，则将其添加到权限列表中
                    }
                }
            }
        }
        return permissions; // 返回获取到的权限列表
    }

    /**
     * 删除权限，并更新相关角色的权限列表
     * @param id 权限ID
     */
    @Transactional // 使用 Spring 的 @Transactional 注解，声明该方法是一个事务方法，保证数据一致性
    public void delete(Long id) { // 定义一个公共方法 delete，接收权限 ID 作为参数，用于删除权限及其关联的角色权限
        removeById(id); // 调用父类 ServiceImpl 的 removeById 方法，根据权限 ID 删除权限记录

        // 删除角色分配的菜单
        List<Role> list = roleService.list(); // 获取所有角色
        for (Role role : list) { // 遍历所有角色
            // 重新分配权限
            List<Long> newP = new ArrayList<>(); // 创建一个新的权限 ID 列表，用于存储更新后的权限 ID
            for (Object p : role.getPermission()) { // 遍历角色原有的权限 ID 列表
                Long pl = Long.valueOf(p + ""); // 将权限 ID 转换为 Long 类型
                if (!id.equals(pl)) { // 如果当前遍历的权限 ID 不是要删除的权限 ID
                    newP.add(Long.valueOf(p + "")); // 将该权限 ID 添加到新的权限 ID 列表中
                }
            }
            role.setPermission(newP); // 将新的权限 ID 列表设置到角色对象中
            roleService.updateById(role); // 调用 RoleService 的 updateById 方法，更新角色信息
        }
    }
}


