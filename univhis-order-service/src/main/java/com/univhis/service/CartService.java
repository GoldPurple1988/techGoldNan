package com.univhis.service;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.univhis.entity.Cart;
import com.univhis.entity.Goods;
import com.univhis.mapper.CartMapper;
import org.json.JSONException;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service // 使用 Spring 的 @Service 注解，将 CartService 标记为一个服务组件，纳入 Spring 容器管理
public class CartService extends ServiceImpl<CartMapper, Cart> { // CartService 继承自 ServiceImpl，实现了 Cart 实体类的通用 CRUD 操作，并指定了对应的 Mapper 为 CartMapper

    @Resource // 使用 JSR-250 规范中的 @Resource 注解，将 GoodsService 类型的 Bean 注入到 CartService 中
    private GoodsService goodsService; // 声明私有的 GoodsService 类型的成员变量 goodsService，用于调用 GoodsService 中的方法

    /**
     * 计算购物车商品总价和优惠金额
     * @param carts 购物车列表
     * @return 包含购物车详细信息、总价和优惠金额的 Map 对象
     * @throws JSONException 当处理 JSON 数据时发生错误
     */
    public Map<String, Object> findAll(List<Cart> carts) throws org.json.JSONException { // 定义一个公共方法 findAll，接收购物车列表作为参数，返回包含计算结果的 Map 对象，并声明可能抛出 JSONException
        BigDecimal totalPrice = new BigDecimal(0); // 初始化总价为 0
        BigDecimal originPrice = new BigDecimal(0); // 初始化原价为 0
        Map<String, Object> res = new HashMap<>(); // 创建一个 HashMap，用于存储计算结果

        for (Cart cart : carts) { // 遍历购物车列表
            Long goodsId = cart.getGoodsId(); // 获取购物车中商品的 ID
            Goods goods = goodsService.getById(goodsId); // 调用 goodsService 的 getById 方法，根据商品 ID 获取商品详细信息
            goods.setRealPrice(goods.getPrice().multiply(BigDecimal.valueOf(goods.getDiscount()))); // 计算商品的实际价格（价格乘以折扣）
            cart.setGoods(goods); // 将包含实际价格的商品信息设置回 Cart 对象中

            totalPrice = totalPrice.add(goods.getRealPrice().multiply(BigDecimal.valueOf(cart.getCount()))); // 累加商品实际价格乘以数量到总价
            originPrice = originPrice.add(goods.getPrice().multiply(BigDecimal.valueOf(cart.getCount()))); // 累加商品原价格乘以数量到原价
        }

        res.put("list", carts);  // 将包含商品详细信息的购物车列表放入结果 Map 中
        res.put("totalPrice", totalPrice);  // 将计算得到的总价放入结果 Map 中
        res.put("discount", originPrice.subtract(totalPrice));    // 计算折扣优惠金额，并放入结果 Map 中
        return res; // 返回包含购物车详细信息、总价和优惠金额的 Map 对象
    }
}
