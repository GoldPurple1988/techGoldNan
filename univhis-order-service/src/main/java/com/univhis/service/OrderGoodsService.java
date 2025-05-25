package com.univhis.service;

import com.univhis.entity.Cart;
import com.univhis.entity.Goods;
import com.univhis.entity.OrderGoods;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.univhis.mapper.OrderGoodsMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service // 使用 Spring 的 @Service 注解，将 OrderGoodsService 标记为一个服务组件，纳入 Spring 容器管理
public class OrderGoodsService extends ServiceImpl<OrderGoodsMapper, OrderGoods> { // OrderGoodsService 继承自 ServiceImpl，实现了 OrderGoods 实体类的通用 CRUD 操作，并指定了对应的 Mapper 为 OrderGoodsMapper

    @Resource // 使用 JSR-250 规范中的 @Resource 注解，将 OrderGoodsMapper 类型的 Bean 注入到 OrderGoodsService 中
    private OrderGoodsMapper orderGoodsMapper; // 声明私有的 OrderGoodsMapper 类型的成员变量 orderGoodsMapper，用于进行数据库操作

    @Resource // 使用 JSR-250 规范中的 @Resource 注解，将 GoodsService 类型的 Bean 注入到 OrderGoodsService 中
    private GoodsService goodsService; // 声明私有的 GoodsService 类型的成员变量 goodsService，用于调用 GoodsService 中的方法

    /**
     * 根据订单id查询订单关联的商品列表
     * @param orderId 订单ID
     * @return 订单关联的商品列表，封装在 Cart 对象中
     */
    public List<Cart> findByOrderId(Long orderId) { // 定义一个公共方法 findByOrderId，接收订单ID作为参数，返回订单关联的商品列表
        List<Cart> carts = new ArrayList<>(); // 创建一个空的 Cart 对象列表，用于存储查询结果
        List<OrderGoods> orderGoods = orderGoodsMapper.findGoodsByOrderId(orderId); // 调用 orderGoodsMapper 的 findGoodsByOrderId 方法，根据订单ID查询订单关联的商品信息
        for (OrderGoods orderGood : orderGoods) { // 遍历查询到的订单商品关联信息
            Long goodsId = orderGood.getGoodsId(); // 获取关联的商品ID
            Goods goods = goodsService.getById(goodsId); // 调用 goodsService 的 getById 方法，根据商品ID查询商品详细信息
            Cart cart = new Cart(); // 创建一个新的 Cart 对象，用于封装单个商品的信息
            cart.setGoods(goods); // 将查询到的商品信息设置到 Cart 对象中
            cart.setGoodsId(goodsId); // 将商品ID设置到 Cart 对象中
            cart.setCount(orderGood.getCount()); // 将订单中商品的数量设置到 Cart 对象中
            carts.add(cart); // 将封装好的 Cart 对象添加到列表中
        }
        return carts; // 返回封装了订单关联商品信息的 Cart 对象列表
    }
}

