package com.univhis.user.auth.controller;


import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.univhis.common.Result;
import com.univhis.entity.Log; // Import Log entity for remote logging example
import com.univhis.entity.Role;
import com.univhis.service.LogService; // Using the LogService from common-lib which is a proxy
import com.univhis.user.auth.service.RoleService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate; // Import RestTemplate
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import com.auth0.jwt.JWT; // To get token from request
import cn.hutool.core.date.DateUtil; // For DateUtil
import javax.servlet.http.HttpServletRequest; // To get token from request
import java.util.Date; // For Date

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/api/role")
public class RoleController {
    @Resource
    private RoleService roleService;

    @Resource
    private LogService logService; // Keep this direct injection as it's from the common lib

    @Resource
    private HttpServletRequest request; // To get token for logging

    @Resource
    private RestTemplate restTemplate; // For inter-service calls if needed
    private static final String NOTIFICATION_LOG_SERVICE_NAME = "univhis-notification-log-service";


    @PostMapping
    public Result<?> save(@RequestBody Role role) {
        logService.log(StrUtil.format("新增角色：{}", role.getName()));
        return Result.success(roleService.save(role));
    }

    @PutMapping
    public Result<?> update(@RequestBody Role role) {
        logService.log(StrUtil.format("更新角色：{}", role.getName()));
        return Result.success(roleService.updateById(role));
    }

    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        Role role = roleService.getById(id);
        if (role != null) {
            logService.log(StrUtil.format("删除角色：{}", role.getName()));
        }
        roleService.removeById(id);
        return Result.success();
    }

    @GetMapping("/{id}")
    public Result<Role> findById(@PathVariable Long id) {
        return Result.success(roleService.getById(id));
    }

    @GetMapping
    public Result<List<Role>> findAll() {
        return Result.success(roleService.list());
    }

    @GetMapping("/page")
    public Result<IPage<Role>> findPage(@RequestParam(required = false, defaultValue = "") String name,
                                        @RequestParam(required = false, defaultValue = "1") Integer pageNum,
                                        @RequestParam(required = false, defaultValue = "10") Integer pageSize) {
        return Result.success(roleService.page(new Page<>(pageNum, pageSize), Wrappers.<Role>lambdaQuery().like(Role::getName, name)));
    }

    // Helper method to make remote log call, if logService was a separate microservice
    private void logRemote(String content) {
        String token = request.getHeader("token");
        String username = null;
        try {
            username = JWT.decode(token).getAudience().get(0);
        } catch (Exception e) {
            username = "anonymous";
        }

        Log log = new Log();
        log.setUser(username);
        log.setContent(content);
        log.setTime(DateUtil.formatDateTime(new Date()));

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
            System.err.println("Failed to send log to notification service: " + e.getMessage());
        }
    }
}