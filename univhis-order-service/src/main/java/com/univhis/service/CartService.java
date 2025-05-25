package com.univhis.service;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.univhis.entity.Cart;
import com.univhis.entity.Goods;
import com.univhis.mapper.CartMapper;
import org.json.JSONException;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate; // Import RestTemplate
import org.springframework.http.ResponseEntity;
import com.univhis.common.Result;
import java.util.LinkedHashMap; // Import LinkedHashMap

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
public class CartService extends ServiceImpl<CartMapper, Cart> {

    @Resource
    private CartMapper cartMapper; // Keep the mapper for direct cart operations

    @Resource
    private RestTemplate restTemplate; // Inject RestTemplate

    private static final String PRODUCT_SERVICE_NAME = "univhis-product-service"; // Nacos service name for product-service

    /**
     * 计算购物车商品总价和优惠金额
     * @param carts 购物车列表
     * @return 包含购物车详细信息、总价和优惠金额的 Map 对象
     * @throws JSONException 当处理 JSON 数据时发生错误
     */
    public Map<String, Object> findAll(List<Cart> carts) throws org.json.JSONException {
        BigDecimal totalPrice = new BigDecimal(0);
        BigDecimal originPrice = new BigDecimal(0);
        Map<String, Object> res = new HashMap<>();

        for (Cart cart : carts) {
            Long goodsId = cart.getGoodsId();

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
                    goods.setPrice(new BigDecimal(goodsData.get("price").toString()));
                    goods.setDiscount(Double.valueOf(goodsData.get("discount").toString()));
                    goods.setStore((Integer) goodsData.get("store"));
                    goods.setSales((Integer) goodsData.get("sales"));
                    goods.setImgs((String) goodsData.get("imgs"));
                    // Map other fields as needed

                    goods.setRealPrice(goods.getPrice().multiply(BigDecimal.valueOf(goods.getDiscount())));
                    cart.setGoods(goods);

                    totalPrice = totalPrice.add(goods.getRealPrice().multiply(BigDecimal.valueOf(cart.getCount())));
                    originPrice = originPrice.add(goods.getPrice().multiply(BigDecimal.valueOf(cart.getCount())));
                } else {
                    log.error("Failed to get goods details for goodsId: " + goodsId + " from product-service. Response: " + goodsResponse.getBody());
                    // Handle case where goods details cannot be fetched, e.g., skip this cart item or throw an exception
                }
            } catch (Exception e) {
                log.error("Error calling product-service for goodsId: " + goodsId, e);
                // Handle network errors or service unavailability
            }
        }

        res.put("list", carts);
        res.put("totalPrice", totalPrice);
        res.put("discount", originPrice.subtract(totalPrice));
        return res;
    }
}