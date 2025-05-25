package com.univhis.service; // Original package name

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.univhis.entity.Message;
import com.univhis.entity.User;
import com.univhis.mapper.MessageMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate; // Import RestTemplate
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import cn.hutool.log.Log; // Import Hutool Log
import cn.hutool.log.LogFactory; // Import Hutool LogFactory

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest; // Import HttpServletRequest
import java.util.List;
import java.util.Objects;
import java.util.LinkedHashMap; // Import LinkedHashMap
import java.util.ArrayList; // Import ArrayList
import com.univhis.common.Result; // Import your common Result class

@Service
public class MessageService extends ServiceImpl<MessageMapper, Message> {

    // Declare and initialize the logger instance using Hutool's LogFactory
    private static final Log log = LogFactory.get();

    @Resource
    private MessageMapper messageMapper;

    @Resource
    private RestTemplate restTemplate; // Inject RestTemplate

    @Resource
    private HttpServletRequest request; // To get the token

    private static final String USER_AUTH_SERVICE_NAME = "univhis-user-auth-service";
    private static final String FILE_SERVICE_NAME = "univhis-file-service"; // Nacos service name for file-service

    /**
     * 根据外键ID查询消息列表，并封装相关用户信息和父消息
     * @param foreignId 外键ID
     * @return 包含用户信息和父消息的消息列表
     */
    public List<Message> findByForeign(Long foreignId) {
        LambdaQueryWrapper<Message> queryWrapper = Wrappers.<Message>lambdaQuery().eq(Message::getForeignId, foreignId).orderByDesc(Message::getId);
        List<Message> list = list(queryWrapper);

        HttpHeaders headers = new HttpHeaders();
        // Assuming the current request has a token that might be needed for user service
        String token = request.getHeader("token");
        if (token != null && !token.isEmpty()) {
            headers.set("token", token);
        }
        HttpEntity<String> entity = new HttpEntity<>(headers);

        for (Message message : list) {
            // Call user-auth-service to get user details
            try {
                ResponseEntity<Result> userResponse = restTemplate.exchange(
                        "http://" + USER_AUTH_SERVICE_NAME + "/api/user/username/" + message.getUsername(),
                        HttpMethod.GET,
                        entity,
                        Result.class
                );

                if (userResponse.getStatusCode().is2xxSuccessful() && Objects.requireNonNull(userResponse.getBody()).getCode().equals("0")) {
                    LinkedHashMap userData = (LinkedHashMap) userResponse.getBody().getData();
                    User user = new User();
                    user.setId(Long.valueOf(userData.get("id").toString()));
                    user.setUsername((String) userData.get("username"));
                    user.setAvatar((String) userData.get("avatar"));

                    // Set avatar URL from file service
                    // Assuming file service URL is http://univhis-file-service/files/{flag}
                    message.setAvatar("http://" + FILE_SERVICE_NAME + "/files/" + user.getAvatar());
                }
            } catch (Exception e) {
                // Corrected logging call: now `log` is properly defined and the two-argument `warn` method is valid.
                log.warn("Failed to get user details for message from user-auth-service: " + message.getUsername(), e);
                // Set a default avatar or handle error
                message.setAvatar("http://" + FILE_SERVICE_NAME + "/files/default_avatar.png");
            }

            Long parentId = message.getParentId();
            if (parentId != null) {
                list.stream().filter(c -> c.getId().equals(parentId)).findFirst().ifPresent(message::setParentMessage);
            }
        }
        return list;
    }
}