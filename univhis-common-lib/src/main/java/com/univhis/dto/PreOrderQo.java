package com.univhis.dto;

import lombok.Data;

/**
 * 用于封装与预订单相关的查询参数或输入信息。
 * 它主要携带了一个名为 carts 的字符串属性。
 * 提供了一种结构化的方式来封装预订单所需的相关信息，
 * 便于在不同的应用层之间进行数据交换和处理
 */
@Data
public class PreOrderQo {
    private String carts;   //代表了一个或多个购物车的信息
}
