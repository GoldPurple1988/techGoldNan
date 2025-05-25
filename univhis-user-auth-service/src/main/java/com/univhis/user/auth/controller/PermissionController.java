package com.univhis.user.auth.controller;


import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.univhis.common.Result;
import com.univhis.entity.Log;
import com.univhis.entity.Permission;
import com.univhis.entity.Role;
import com.univhis.service.LogService; // Using the LogService from common-lib which is a proxy
import com.univhis.user.auth.service.PermissionService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate; // Import RestTemplate
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import com.auth0.jwt.JWT; // To get token from request
import javax.servlet.http.HttpServletRequest; // To get token from request

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.LinkedHashMap; // For deserialization
import java.util.Objects;


@RestController
@RequestMapping("/api/permission")
public class PermissionController {
    @Resource
    private PermissionService permissionService;

    // LogService will be called through RestTemplate in a real microservice setup,
    // but for now, it's directly injected from the common-lib.
    // If LogService itself was a microservice, then this would be a RestTemplate call.
    @Resource
    private LogService logService; // Keep this direct injection as it's from the common lib

    @Resource
    private HttpServletRequest request; // To get token for logging

    @Resource
    private RestTemplate restTemplate; // For inter-service calls if needed
    private static final String NOTIFICATION_LOG_SERVICE_NAME = "univhis-notification-log-service";

    @PostMapping
    public Result<?> save(@RequestBody Permission permission) {
        // Call LogService via RestTemplate (if LogService was its own microservice endpoint)
        // For now, it's a direct call to the LogService injected from common-lib
        // If logService was a remote call, it would look like this:
        // logRemote(StrUtil.format("新增权限菜单：{}", permission.getName()));
        logService.log(StrUtil.format("新增权限菜单：{}", permission.getName())); // Direct call to local LogService instance
        return Result.success(permissionService.save(permission));
    }

    @PutMapping
    public Result<?> update(@RequestBody Permission permission) {
        logService.log(StrUtil.format("更新权限菜单：{}", permission.getName()));
        return Result.success(permissionService.updateById(permission));
    }

    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        Permission permission = permissionService.getById(id);
        if (permission != null) {
            logService.log(StrUtil.format("删除权限菜单：{}", permission.getName()));
        }
        permissionService.delete(id);
        return Result.success();
    }

    @GetMapping("/{id}")
    public Result<Permission> findById(@PathVariable Long id) {
        return Result.success(permissionService.getById(id));
    }

    @GetMapping
    public Result<List<Permission>> findAll() {
        return Result.success(permissionService.list());
    }

    @GetMapping("/page")
    public Result<IPage<Permission>> findPage(@RequestParam(required = false, defaultValue = "") String name,
                                              @RequestParam(required = false, defaultValue = "1") Integer pageNum,
                                              @RequestParam(required = false, defaultValue = "10") Integer pageSize) {
        return Result.success(permissionService.page(new Page<>(pageNum, pageSize), Wrappers.<Permission>lambdaQuery().like(Permission::getName, name)));
    }

    // This method needs to be called from external services (e.g., UserService)
    // So the implementation of getByRoles should be within PermissionService,
    // and UserService will call PermissionService's remote endpoint.
    @PostMapping("/getByRoles")
    public Result<List<Permission>> getByRoles(@RequestBody List<Role> roles) {
        // This endpoint is primarily for other services to call.
        // The actual logic of getting permissions from roles is in PermissionService.
        // So, this controller simply delegates.
        return Result.success(permissionService.getByRoles(roles));
    }

    // Helper method to make remote log call, if logService was a separate microservice
    private void logRemote(String content) {
        String token = request.getHeader("token");
        String username = null;
        try {
            username = JWT.decode(token).getAudience().get(0);
        } catch (Exception e) {
            // handle error, e.g., anonymous log
            username = "anonymous";
        }

        Log log = new Log();
        log.setUser(username);
        log.setContent(content);
        log.setTime(DateUtil.formatDateTime(new Date())); // Need to import DateUtil and Date
        // log.setIp(logService.getIpAddress()); // This would be problematic as logService is remote.
        // Instead, the LogService on the other end should get the IP.

        HttpHeaders headers = new HttpHeaders();
        headers.set("token", token);
        HttpEntity<Log> entity = new HttpEntity<>(log, headers);

        try {
            restTemplate.postForEntity(
                    "http://" + NOTIFICATION_LOG_SERVICE_NAME + "/api/log",
                    entity,
                    Result.class
            );
        } catch (Exception e) {
            // Log this failure, but don't block the main flow
            System.err.println("Failed to send log to notification service: " + e.getMessage());
        }
    }

}