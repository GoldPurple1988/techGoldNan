package com.univhis.service; // Original package name

import cn.hutool.core.date.DateUtil;
import com.auth0.jwt.JWT;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.univhis.common.Result;
import com.univhis.entity.Log;
import com.univhis.entity.User;
import com.univhis.mapper.LogMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate; // Import RestTemplate
import org.springframework.http.HttpHeaders; // Import HttpHeaders
import org.springframework.http.HttpEntity; // Import HttpEntity
import org.springframework.http.HttpMethod; // Import HttpMethod
import org.springframework.http.ResponseEntity; // Import ResponseEntity

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Objects; // Import Objects

@Service
public class LogService extends ServiceImpl<LogMapper, Log> {

    @Resource
    private LogMapper logMapper;

    @Resource
    private HttpServletRequest request;

    @Resource
    private RestTemplate restTemplate; // Inject RestTemplate

    private static final String USER_AUTH_SERVICE_NAME = "univhis-user-auth-service"; // Nacos service name for user-auth-service

    /**
     * 获取当前登录用户
     * @return 当前登录用户对象，如果获取失败则返回 null
     */
    public User getUser() {
        try {
            String token = request.getHeader("token");
            if (token == null || token.isEmpty()) {
                return null;
            }
            String username = JWT.decode(token).getAudience().get(0);

            // Use RestTemplate to call user-auth-service
            HttpHeaders headers = new HttpHeaders();
            headers.set("token", token); // Forward the token
            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<Result> response = restTemplate.exchange(
                    "http://" + USER_AUTH_SERVICE_NAME + "/api/user/username/" + username,
                    HttpMethod.GET,
                    entity,
                    Result.class
            );

            if (response.getStatusCode().is2xxSuccessful() && Objects.requireNonNull(response.getBody()).getCode().equals("0")) {
                // Assuming the Result.data contains the User object. You might need a more robust deserialization
                // if the User object is nested or needs specific type handling.
                // For simplicity, directly map the 'data' field of the Result.
                LinkedHashMap userData = (LinkedHashMap) response.getBody().getData();
                User user = new User();
                user.setId(Long.valueOf(userData.get("id").toString()));
                user.setUsername((String) userData.get("username"));
                user.setAvatar((String) userData.get("avatar"));
                // Map other fields as needed
                return user;
            }
            return null;
        } catch (Exception e) {
            log.error("Failed to get user info from user-auth-service", e); // Use log from hutool
            return null;
        }
    }

    /**
     * 记录日志
     * @param content 日志内容
     */
    public void log(String content) {
        User currentUser = getUser();
        if (currentUser == null) {
            // Handle case where user is not logged in or cannot be retrieved
            Log logEntry = new Log();
            logEntry.setUser("anonymous"); // Or some default user
            logEntry.setTime(DateUtil.formatDateTime(new Date()));
            logEntry.setIp(getIpAddress());
            logEntry.setContent(content);
            save(logEntry);
            return;
        }
        Log logEntry = new Log();
        logEntry.setUser(currentUser.getUsername());
        logEntry.setTime(DateUtil.formatDateTime(new Date()));
        logEntry.setIp(getIpAddress());
        logEntry.setContent(content);
        save(logEntry);
    }

    /**
     * 记录日志
     * @param username 用户名
     * @param content  日志内容
     */
    public void log(String username, String content) {
        Log logEntry = new Log();
        logEntry.setUser(username);
        logEntry.setTime(DateUtil.formatDateTime(new Date()));
        logEntry.setIp(getIpAddress());
        logEntry.setContent(content);
        save(logEntry);
    }

    /**
     * 获取 IP 地址
     * @return 请求的 IP 地址
     */
    public String getIpAddress() {
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}