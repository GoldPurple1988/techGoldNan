package com.univhis.user.auth.controller;


import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.univhis.common.Result;
import com.univhis.entity.Log; // Import Log entity for remote logging example
import com.univhis.entity.Order;
import com.univhis.entity.User;
import com.univhis.exception.CustomException;
import com.univhis.service.LogService; // Using the LogService from common-lib which is a proxy
import com.univhis.user.auth.service.UserService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate; // Import RestTemplate
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import cn.hutool.core.date.DateUtil; // For DateUtil
import java.util.Date; // For Date


import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;


@RestController
@RequestMapping("/api/user")
public class UserController {

    @Resource
    private UserService userService;

    @Resource
    private LogService logService; // Keep this direct injection as it's from the common lib

    @Resource
    private HttpServletRequest request;

    @Resource
    private RestTemplate restTemplate; // For inter-service calls
    private static final String NOTIFICATION_LOG_SERVICE_NAME = "univhis-notification-log-service";


    public User getUser() {
        String token = request.getHeader("token");
        if (StrUtil.isBlank(token)) {
            // This method might be called internally or by an interceptor that already validated the token.
            // If this is a direct endpoint call that needs user info, this check is valid.
            // Otherwise, an interceptor should handle 401.
            throw new CustomException("401", "未获取到token, 请重新登录");
        }
        String username = JWT.decode(token).getAudience().get(0);
        return userService.getOne(Wrappers.<User>lambdaQuery().eq(User::getUsername, username));
    }

    /**
     * 登录
     *
     * @param user
     * @return
     */
    @PostMapping("/login")
    public Result<User> login(@RequestBody User user) {
        User res = userService.login(user);
        // 生成token
        String token = JWT.create().withAudience(res.getUsername()).sign(Algorithm.HMAC256(res.getPassword()));
        res.setToken(token);

        // Call LogService via RestTemplate (if LogService was its own microservice endpoint)
        // For now, it's a direct call to the LogService injected from common-lib
        logService.log(user.getUsername(), StrUtil.format("用户 {} 登录系统", user.getUsername()));
        return Result.success(res);
    }

    /**
     * 注册
     *
     * @param user
     * @param request
     * @return
     */
    @PostMapping("/register")
    public Result<User> register(@RequestBody User user, HttpServletRequest request) {
        if (user.getPassword() == null) {
            user.setPassword("123456");
        }
        user.setAccount(new BigDecimal(0));
        User dbUser = userService.register(user);
        request.getSession().setAttribute("user", user); // Session usage is not common in microservices without sticky sessions

        logService.log(user.getUsername(), StrUtil.format("用户 {} 注册账号成功", user.getUsername()));
        return Result.success(dbUser);
    }


    @PostMapping
    public Result<?> save(@RequestBody User user) {
        if (user.getPassword() == null) {
            user.setPassword("123456");
        }
        logService.log(StrUtil.format("新增用户：{}", user.getUsername()));
        return Result.success(userService.save(user));
    }

    @PutMapping("/reset")
    public Result<?> reset(@RequestBody User user) {
        if (StrUtil.isBlank(user.getUsername()) || StrUtil.isBlank(user.getPhone()) || StrUtil.isBlank(user.getPassword())) {
            throw new CustomException("-1", "参数错误");
        }
        logService.log(user.getUsername(), StrUtil.format("{} 用户重置密码",user.getUsername()));
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username",user.getUsername());
        User one = userService.getOne(queryWrapper);
        if (one == null) {
            throw new CustomException("-1", "未找到用户");
        }
        one.setPassword(user.getPassword());
        return Result.success(userService.updateById(one));
    }

    @PutMapping
    public Result<?> update(@RequestBody User user) {
        logService.log(StrUtil.format("更新用户：{}", user.getUsername()));
        return Result.success(userService.updateById(user));
    }

    /**
     * 更新账户余额
     * @param money  The amount to add or deduct. Positive for add, negative for deduct.
     * @return
     */
    @PutMapping("/account/{money}")
    public Result<?> recharge(@PathVariable BigDecimal money) {
        User user = getUser(); // Get the current user from token
        // Fetch the user's current account from the database to ensure correctness
        User dbUser = userService.getById(user.getId());
        if (dbUser == null) {
            throw new CustomException("-1", "User not found for account update.");
        }

        dbUser.setAccount(dbUser.getAccount().add(money)); // Add or deduct the money
        userService.updateById(dbUser);
        logService.log(StrUtil.format("更新用户 {} 账户：{}", dbUser.getUsername(), money));
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        User user = userService.getById(id);
        if (user != null) {
            logService.log(StrUtil.format("删除用户 {}", user.getUsername()));
        }
        userService.removeById(id);
        return Result.success();
    }

    @GetMapping("/{id}")
    public Result<User> findById(@PathVariable Long id) {
        return Result.success(userService.findById(id));
    }

    // New endpoint to get user by username, used by other services (e.g., LogService, MessageService, OrderService)
    @GetMapping("/username/{username}")
    public Result<User> findByUsername(@PathVariable String username) {
        // This endpoint should be accessible internally by other microservices.
        // It's good to avoid directly exposing sensitive data without authentication/authorization
        // if this endpoint is directly exposed to external clients.
        // For internal microservice communication (assuming token is passed and validated by AuthInterceptor),
        // it's generally fine.
        User user = userService.getOne(Wrappers.<User>lambdaQuery().eq(User::getUsername, username));
        if (user == null) {
            throw new CustomException("-1", "User not found with username: " + username);
        }
        // Potentially remove sensitive info like password before returning to other services
        user.setPassword(null);
        return Result.success(user);
    }


    @GetMapping
    public Result<List<User>> findAll() {
        return Result.success(userService.list());
    }

    @GetMapping("/page")
    public Result<IPage<User>> findPage(@RequestParam(required = false, defaultValue = "") String name,
                                        @RequestParam(required = false, defaultValue = "1") Integer pageNum,
                                        @RequestParam(required = false, defaultValue = "10") Integer pageSize) {
        LambdaQueryWrapper<User> wrapper = Wrappers.<User>lambdaQuery().ne(User::getUsername,"admin").like(User::getUsername,name).orderByDesc(User::getId);
        return Result.success(userService.page(new Page<>(pageNum, pageSize), wrapper));
    }

    @GetMapping("/export")
    public void export(HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = CollUtil.newArrayList();

        List<User> all = userService.list();
        for (User user : all) {
            Map<String, Object> rowl = new LinkedHashMap<>();
            rowl.put("名称", user.getUsername());
            rowl.put("手机", user.getPhone());
            rowl.put("邮箱", user.getEmail());
            list.add(rowl);
        }

        // 2. 写Excel
        ExcelWriter writer = ExcelUtil.getWriter(true);
        writer.write(list, true);

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=utf-8");
        String fileName = URLEncoder.encode("用户信息","UTF-8");
        response.setHeader("Content-Disposition","attachment;filename=" + fileName + ".xlsx");

        ServletOutputStream out = response.getOutputStream();
        writer.flush(out, true);
        writer.close();
        IoUtil.close(System.out);
    }
}