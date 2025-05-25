package com.univhis.user.auth.common;

import cn.hutool.core.util.StrUtil;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.univhis.common.Result; // Import common Result
import com.univhis.entity.User;
import com.univhis.exception.CustomException;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.client.RestTemplate; // Import RestTemplate
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import java.util.LinkedHashMap;
import java.util.Objects;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 拦截器
 * 这是一个 Spring MVC 拦截器，用于在请求处理之前进行身份验证和权限校验
 */
public class AuthInterceptor implements HandlerInterceptor {

    // Remove direct UserService injection as this service will call itself or external services
    // @Resource
    // private UserService userService;

    @Resource
    private RestTemplate restTemplate; // Inject RestTemplate

    private static final String USER_AUTH_SERVICE_NAME = "univhis-user-auth-service"; // Self-reference for clarity

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String token = request.getHeader("token");

        if (StrUtil.isBlank(token)) {
            throw new CustomException("401", "未获取到token, 请重新登录");
        }

        String username;
        try {
            username = JWT.decode(token).getAudience().get(0);
        } catch (JWTDecodeException j) {
            throw new CustomException("401", "权限验证失败, 请重新登录");
        }

        // Use RestTemplate to call user-auth-service (self) to get user details
        User user = null;
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("token", token); // Forward the token to the self-call (important for recursive validation)
            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<Result> userResponse = restTemplate.exchange(
                    "http://" + USER_AUTH_SERVICE_NAME + "/api/user/username/" + username,
                    HttpMethod.GET,
                    entity,
                    Result.class
            );

            if (userResponse.getStatusCode().is2xxSuccessful() && Objects.requireNonNull(userResponse.getBody()).getCode().equals("0")) {
                LinkedHashMap userData = (LinkedHashMap) userResponse.getBody().getData();
                user = new User();
                user.setId(Long.valueOf(userData.get("id").toString()));
                user.setUsername((String) userData.get("username"));
                user.setPassword((String) userData.get("password")); // Need password for JWT verification
            }
        } catch (Exception e) {
            // Log the error
            throw new CustomException("401", "用户不存在或获取用户信息失败, 请重新登录: " + e.getMessage());
        }

        if (user == null) {
            throw new CustomException("401", "用户不存在, 请重新登录");
        }

        // 验证 token
        // Use the user's password as HMAC256 algorithm's key to build JWT verifier
        JWTVerifier jwtVerifier = JWT.require(Algorithm.HMAC256(user.getPassword())).build();
        try {
            jwtVerifier.verify(token);
        } catch (JWTVerificationException e) {
            throw new CustomException("401", "token不合法, 请重新登录");
        }

        return true;
    }
}