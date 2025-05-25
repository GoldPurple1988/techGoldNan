package com.univhis.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.univhis.entity.OrderGoods;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 订单商品信息 Mapper 接口
 */
@Mapper // 使用 @Mapper 注解将 OrderGoodsMapper 接口标记为一个 MyBatis 的 Mapper 接口
public interface OrderGoodsMapper extends BaseMapper<OrderGoods> { // OrderGoodsMapper 接口继承自 MyBatis-Plus 提供的 BaseMapper 接口，BaseMapper<OrderGoods> 提供了针对 OrderGoods 实体类的基本 CRUD 操作

    /**
     * 根据订单ID查询订单下的所有商品信息
     *
     * @param orderId 订单ID // 方法参数，表示要查询的订单的唯一标识符
     * @return 订单商品信息列表 // 方法返回值，是一个包含多个 OrderGoods 对象的 List 集合，每个 OrderGoods 对象代表一个订单中的商品信息
     */
    @Select("select * from order_goods where order_id = #{orderId}") // 使用 @Select 注解定义 SQL 查询语句，该语句查询 order_goods 表中所有 order_id 等于传入的 #{orderId} 的记录
    List<OrderGoods> findGoodsByOrderId(@Param("orderId") Long orderId); // 定义接口方法 findGoodsByOrderId，该方法接收一个 Long 类型的 orderId 参数，并使用 @Param("orderId") 注解将其命名为 orderId，以便在 SQL 语句中引用。该方法返回一个 List<OrderGoods>，表示查询到的订单商品信息列表
}