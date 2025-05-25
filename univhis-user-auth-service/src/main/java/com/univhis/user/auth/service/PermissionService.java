package com.univhis.user.auth.service;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.univhis.common.Result; // Import common Result
import com.univhis.entity.Permission;
import com.univhis.entity.Role;
import com.univhis.user.auth.mapper.PermissionMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate; // Import RestTemplate
import org.springframework.http.ResponseEntity; // Import ResponseEntity
import java.util.LinkedHashMap; // Import LinkedHashMap
import java.util.Objects;


import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service
public class PermissionService extends ServiceImpl<PermissionMapper, Permission> {

    @Resource
    private PermissionMapper permissionMapper;

    @Resource
    private RestTemplate restTemplate; // Inject RestTemplate
    // Remove direct RoleService injection as we'll call it via RestTemplate if it's a separate service.
    // However, in this case, RoleService is also in user-auth-service, so we *can* directly inject it
    // if we choose to keep internal service calls direct within the same microservice.
    // For true microservice separation, even within the same module, calling via RestTemplate is ideal
    // if RoleService was a separate microservice. Since they are in the same module, direct injection is fine.
    // For demonstration, let's keep it direct.
    @Resource
    private RoleService roleService; // Keep direct for now as it's within the same microservice

    private static final String USER_AUTH_SERVICE_NAME = "univhis-user-auth-service"; // Self-reference

    /**
     * 根据角色列表获取权限列表
     * @param roles 角色列表
     * @return 权限列表
     */
    public List<Permission> getByRoles(List<Role> roles) {
        List<Permission> permissions = new ArrayList<>();
        for (Role role : roles) {
            // If RoleService was a different microservice, this would be a RestTemplate call.
            // Example if RoleService was remote:
            // ResponseEntity<Result> roleResponse = restTemplate.getForEntity("http://" + USER_AUTH_SERVICE_NAME + "/api/role/" + role.getId(), Result.class);
            // LinkedHashMap roleData = (LinkedHashMap) Objects.requireNonNull(roleResponse.getBody()).getData();
            // Role r = new Role();
            // r.setId(Long.valueOf(roleData.get("id").toString()));
            // r.setPermission((List<Long>) roleData.get("permission")); // Requires careful casting if ListHandler is used

            // For now, keep it direct as RoleService is in the same microservice
            Role r = roleService.getById(role.getId());
            if (r != null && CollUtil.isNotEmpty(r.getPermission())) {
                for (Object permissionId : r.getPermission()) {
                    // This call can be direct since PermissionService is self.
                    Permission permission = getById(Long.valueOf(permissionId.toString()));
                    if (permission != null && permissions.stream().noneMatch(p -> p.getPath().equals(permission.getPath()))) {
                        permissions.add(permission);
                    }
                }
            }
        }
        return permissions;
    }

    /**
     * 删除权限，并更新相关角色的权限列表
     * @param id 权限ID
     */
    @Transactional
    public void delete(Long id) {
        removeById(id);

        // Delete role allocated menus
        List<Role> list = roleService.list();
        for (Role role : list) {
            List<Long> newP = new ArrayList<>();
            // Ensure permissions list is not null to avoid NullPointerException
            if (role.getPermission() != null) {
                for (Object p : role.getPermission()) {
                    Long pl = Long.valueOf(p.toString());
                    if (!id.equals(pl)) {
                        newP.add(pl);
                    }
                }
            }
            role.setPermission(newP);
            roleService.updateById(role);
        }
    }
}