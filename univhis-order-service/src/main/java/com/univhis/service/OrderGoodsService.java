package com.univhis.service;

import com.univhis.entity.Cart;
import com.univhis.entity.Goods;
import com.univhis.entity.OrderGoods;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.univhis.mapper.OrderGoodsMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate; // Import RestTemplate
import org.springframework.http.ResponseEntity;
import com.univhis.common.Result;

import java.math.BigDecimal;
import java.util.LinkedHashMap; // Import LinkedHashMap

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class OrderGoodsService extends ServiceImpl<OrderGoodsMapper, OrderGoods> {

    @Resource
    private OrderGoodsMapper orderGoodsMapper;

    @Resource
    private RestTemplate restTemplate; // Inject RestTemplate
    // Remove direct GoodsService injection

    private static final String PRODUCT_SERVICE_NAME = "univhis-product-service";

    /**
     * 根据订单id查询订单关联的商品列表
     * @param orderId 订单ID
     * @return 订单关联的商品列表，封装在 Cart 对象中
     */
    public List<Cart> findByOrderId(Long orderId) {
        List<Cart> carts = new ArrayList<>();
        List<OrderGoods> orderGoods = orderGoodsMapper.findGoodsByOrderId(orderId);
        for (OrderGoods orderGood : orderGoods) {
            Long goodsId = orderGood.getGoodsId();

            // Use RestTemplate to call product-service to get goods details
            try {
                ResponseEntity<Result> goodsResponse = restTemplate.getForEntity(
                        "http://" + PRODUCT_SERVICE_NAME + "/api/goods/" + goodsId,
                        Result.class
                );

                if (goodsResponse.getStatusCode().is2xxSuccessful() && Objects.requireNonNull(goodsResponse.getBody()).getCode().equals("0")) {
                    LinkedHashMap goodsData = (LinkedHashMap) goodsResponse.getBody().getData();
                    Goods goods = new Goods();
                    goods.setId(Long.valueOf(goodsData.get("id").toString()));
                    goods.setName((String) goodsData.get("name"));
                    goods.setImgs((String) goodsData.get("imgs"));
                    goods.setPrice(new BigDecimal(goodsData.get("price").toString()));
                    goods.setDiscount(Double.valueOf(goodsData.get("discount").toString()));
                    // Map other fields as needed

                    Cart cart = new Cart();
                    cart.setGoods(goods);
                    cart.setGoodsId(goodsId);
                    cart.setCount(orderGood.getCount());
                    carts.add(cart);
                } else {
                    log.error("Failed to get goods details for orderGoodsId: " + orderGood.getId() + ". Response: " + goodsResponse.getBody());
                    // Handle error if goods details can't be fetched
                }
            } catch (Exception e) {
                log.error("Error calling product-service for goodsId: " + goodsId + " in order: " + orderId, e);
                // Handle network errors or service unavailability
            }
        }
        return carts;
    }
}