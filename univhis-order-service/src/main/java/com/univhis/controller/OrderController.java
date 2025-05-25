package com.univhis.controller;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.TypeReference;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.auth0.jwt.JWT;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.univhis.common.Result;
import com.univhis.dto.PreOrderQo;
import com.univhis.entity.*;
import com.univhis.exception.CustomException;
import com.univhis.service.*;
import org.json.JSONException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate; // Import RestTemplate
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import com.fasterxml.jackson.databind.ObjectMapper; // Import ObjectMapper if needed for complex deserialization
import java.util.LinkedHashMap;
import org.springframework.web.util.UriComponentsBuilder; // For building URI with parameters
import cn.hutool.log.Log; // Import Hutool Log
import cn.hutool.log.LogFactory; // Import Hutool LogFactory

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;


@RestController
@RequestMapping("/api/order")
public class OrderController {
    // Declare and initialize the logger instance using Hutool's LogFactory
    private static final Log log = LogFactory.get();

    @Resource
    private OrderService orderService;
    @Resource
    private HttpServletRequest request;
    @Resource
    private CartService cartService; // Keep CartService as it's part of the order service
    @Resource
    private OrderGoodsService orderGoodsService; // Keep OrderGoodsService
    // Remove direct GoodsService and UserService injection

    @Resource
    private RestTemplate restTemplate; // Inject RestTemplate

    private static final String USER_AUTH_SERVICE_NAME = "univhis-user-auth-service";
    private static final String PRODUCT_SERVICE_NAME = "univhis-product-service";

    public User getUser() {
        String token = request.getHeader("token");
        if (StrUtil.isBlank(token)) {
            throw new CustomException("401", "Unauthorized: No token provided");
        }

        HttpHeaders headers = new HttpHeaders();
        headers.set("token", token);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            String username = JWT.decode(token).getAudience().get(0);
            ResponseEntity<Result> response = restTemplate.exchange(
                    "http://" + USER_AUTH_SERVICE_NAME + "/api/user/username/" + username,
                    HttpMethod.GET,
                    entity,
                    Result.class
            );

            if (response.getStatusCode().is2xxSuccessful() && Objects.requireNonNull(response.getBody()).getCode().equals("0")) {
                LinkedHashMap userData = (LinkedHashMap) response.getBody().getData();
                User user = new User();
                user.setId(Long.valueOf(userData.get("id").toString()));
                user.setUsername((String) userData.get("username"));
                user.setAccount(new BigDecimal(userData.get("account").toString()));
                // Map other fields as needed (e.g., password, email, phone etc.)
                return user;
            }
            throw new CustomException("-1", "Failed to retrieve user details from user service.");
        } catch (Exception e) {
            log.error("Error fetching user details from user-auth-service", e);
            throw new CustomException("401", "Authentication failed: " + e.getMessage());
        }
    }

    @Transactional
    @PostMapping
    public Result<?> save(@RequestBody Order order) {
        User currentUser = getUser();
        order.setUserId(currentUser.getId());
        order.setOrderNo(DateUtil.format(new Date(), "yyyyMMddHHmmss") + RandomUtil.randomNumbers(6));
        order.setCreateTime(DateUtil.now());

        String cartsStr = order.getCarts();
        List<Cart> carts = JSONUtil.toBean(cartsStr, new TypeReference<List<Cart>>() {}, true);
        orderService.save(order);

        for (Cart cart : carts) {
            Integer count = cart.getCount();
            Long goodsId = cart.getGoodsId();

            // Fetch goods to deduct stock and update sales via RestTemplate
            try {
                ResponseEntity<Result> goodsResponse = restTemplate.getForEntity(
                        "http://" + PRODUCT_SERVICE_NAME + "/api/goods/" + goodsId,
                        Result.class
                );

                if (!goodsResponse.getStatusCode().is2xxSuccessful() || !Objects.requireNonNull(goodsResponse.getBody()).getCode().equals("0")) {
                    throw new CustomException("-1", "Failed to retrieve goods details for goodsId: " + goodsId);
                }

                LinkedHashMap goodsData = (LinkedHashMap) goodsResponse.getBody().getData();
                Goods goods = new Goods();
                goods.setId(Long.valueOf(goodsData.get("id").toString()));
                goods.setStore((Integer) goodsData.get("store"));
                goods.setSales((Integer) goodsData.get("sales"));

                if (goods.getStore() - cart.getCount() < 0) {
                    throw new CustomException("-1", "库存不足 for goods: " + goods.getName());
                }

                goods.setStore(goods.getStore() - cart.getCount());
                goods.setSales(goods.getSales() + cart.getCount());

                // Update goods stock and sales via RestTemplate
                HttpHeaders headers = new HttpHeaders();
                headers.set("Content-Type", "application/json");
                HttpEntity<Goods> goodsUpdateEntity = new HttpEntity<>(goods, headers);

                ResponseEntity<Result> updateGoodsResponse = restTemplate.exchange(
                        "http://" + PRODUCT_SERVICE_NAME + "/api/goods",
                        HttpMethod.PUT,
                        goodsUpdateEntity,
                        Result.class
                );

                if (!updateGoodsResponse.getStatusCode().is2xxSuccessful() || !Objects.requireNonNull(updateGoodsResponse.getBody()).getCode().equals("0")) {
                    throw new CustomException("-1", "Failed to update goods stock for goodsId: " + goodsId);
                }

            } catch (Exception e) {
                log.error("Error processing goods for order: " + e.getMessage(), e);
                throw new CustomException("-1", "商品处理失败: " + e.getMessage());
            }

            OrderGoods orderGoods = new OrderGoods();
            orderGoods.setOrderId(order.getId());
            orderGoods.setGoodsId(goodsId);
            orderGoods.setCount(count);
            orderGoodsService.save(orderGoods);
        }

        if (order.getType() == 1) {  // 1表示购物车，0表示直接购买
            // Clear user's cart after order submission
            cartService.remove(Wrappers.<Cart>lambdaUpdate().eq(Cart::getUserId, currentUser.getId()));
        }

        return Result.success(order);
    }

    @PutMapping
    public Result<?> update(@RequestBody Order order) {
        orderService.updateById(order);
        return Result.success();
    }

    /**
     * 付款
     * @param id
     * @return
     */
    @Transactional
    @PutMapping("/pay/{id}")
    public Result<?> pay(@PathVariable Long id) {
        Order order = orderService.getById(id);
        BigDecimal totalPrice = order.getTotalPrice();

        User currentUser = getUser();
        Long userId = currentUser.getId();

        // Fetch user from user-auth-service
        HttpHeaders headers = new HttpHeaders();
        headers.set("token", request.getHeader("token"));
        HttpEntity<String> entity = new HttpEntity<>(headers);

        User userFromService = null;
        try {
            ResponseEntity<Result> userResponse = restTemplate.exchange(
                    "http://" + USER_AUTH_SERVICE_NAME + "/api/user/" + userId,
                    HttpMethod.GET,
                    entity,
                    Result.class
            );

            if (userResponse.getStatusCode().is2xxSuccessful() && Objects.requireNonNull(userResponse.getBody()).getCode().equals("0")) {
                LinkedHashMap userData = (LinkedHashMap) userResponse.getBody().getData();
                userFromService = new User();
                userFromService.setId(Long.valueOf(userData.get("id").toString()));
                userFromService.setAccount(new BigDecimal(userData.get("account").toString()));
                userFromService.setUsername((String) userData.get("username")); // For logging
            } else {
                throw new CustomException("-1", "Failed to retrieve user balance from user service.");
            }
        } catch (Exception e) {
            log.error("Error fetching user for payment: " + e.getMessage(), e);
            throw new CustomException("-1", "获取用户余额失败: " + e.getMessage());
        }

        if (userFromService.getAccount().compareTo(totalPrice) < 0) { // Changed to < 0 to correctly check for insufficient balance
            throw new CustomException("-1", "余额不足");
        }

        // Update user account via RestTemplate
        try {
            // Use PUT to update the account, or a dedicated recharge endpoint if available
            // Assuming a dedicated endpoint for recharging/deducting might be better,
            // but for simple update, we can send the updated User object.
            BigDecimal newAccount = userFromService.getAccount().subtract(totalPrice);
            String url = UriComponentsBuilder.fromHttpUrl("http://" + USER_AUTH_SERVICE_NAME + "/api/user/account/-" + totalPrice.toString())
                    .queryParam("userId", userFromService.getId()) // Pass userId as a query parameter if the PUT endpoint expects it
                    .toUriString();

            HttpHeaders updateHeaders = new HttpHeaders();
            updateHeaders.set("token", request.getHeader("token")); // Pass token for authentication
            updateHeaders.set("Content-Type", "application/json");

            HttpEntity<BigDecimal> updateAccountEntity = new HttpEntity<>(newAccount, updateHeaders); // Sending newAccount directly if endpoint supports it

            HttpEntity<String> deductEntity = new HttpEntity<>(updateHeaders);
            ResponseEntity<Result> deductResponse = restTemplate.exchange(
                    "http://" + USER_AUTH_SERVICE_NAME + "/api/user/account/-" + totalPrice.toString(), // Send negative total price to deduct
                    HttpMethod.PUT,
                    deductEntity,
                    Result.class
            );

            if (!deductResponse.getStatusCode().is2xxSuccessful() || !Objects.requireNonNull(deductResponse.getBody()).getCode().equals("0")) {
                throw new CustomException("-1", "Failed to update user account balance.");
            }

        } catch (Exception e) {
            log.error("Error updating user account: " + e.getMessage(), e);
            throw new CustomException("-1", "更新用户余额失败: " + e.getMessage());
        }

        order.setState("待发货");
        orderService.updateById(order);
        return Result.success();
    }


    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        orderService.removeById(id);
        return Result.success();
    }

    /**
     * 获取订单的确认信息
     *
     * @return
     */
    @PostMapping("/pre")
    public Result<?> pre(@RequestBody PreOrderQo preOrderQo) throws JSONException {
        String cartsStr = preOrderQo.getCarts();
        List<Cart> carts = JSONUtil.toBean(cartsStr, new TypeReference<List<Cart>>() {
        }, true);
        Map<String, Object> all = cartService.findAll(carts);
        return Result.success(all);
    }

    @GetMapping("/{id}")
    public Result<?> findById(@PathVariable Long id) {
        return Result.success(orderService.getById(id));
    }

    @GetMapping
    public Result<?> findAll() {
        List<Order> list = orderService.list();
        return Result.success(list);
    }

    @GetMapping("/page")
    public Result<?> findPage(@RequestParam(required = false, defaultValue = "") String name,
                              @RequestParam(required = false, defaultValue = "1") Integer pageNum,
                              @RequestParam(required = false, defaultValue = "10") Integer pageSize) {
        LambdaQueryWrapper<Order> query = Wrappers.<Order>lambdaQuery().orderByDesc(Order::getId);
        if (StrUtil.isNotBlank(name)) {
            query.like(Order::getOrderNo, name);
        }
        IPage<Order> page = orderService.page(new Page<>(pageNum, pageSize), query);
        return Result.success(page);
    }

    /**
     * 前台查询订单列表
     * @param state
     * @param pageNum
     * @param pageSize
     * @return
     */
    @GetMapping("/page/front")
    public Result<?> findPageFront(@RequestParam(required = false, defaultValue = "") String state,
                                   @RequestParam(required = false, defaultValue = "1") Integer pageNum,
                                   @RequestParam(required = false, defaultValue = "10") Integer pageSize) {
        LambdaQueryWrapper<Order> query = Wrappers.<Order>lambdaQuery().orderByDesc(Order::getId);
        User currentUser = getUser();
        if (currentUser != null) {
            query.eq(Order::getUserId, currentUser.getId());
        } else {
            return Result.success(new Page<>()); // Return empty page if user not found
        }
        // 根据状态查询
        if (StrUtil.isNotBlank(state)) {
            query.eq(Order::getState, state);
        }
        IPage<Order> page = orderService.page(new Page<>(pageNum, pageSize), query);

        for (Order order : page.getRecords()) {
            Long orderId = order.getId();
            List<Cart> carts = orderGoodsService.findByOrderId(orderId);
            order.setCarts(JSONUtil.toJsonStr(carts));
        }
        return Result.success(page);
    }

}