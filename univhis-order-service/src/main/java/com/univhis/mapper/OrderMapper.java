package com.univhis.mapper;

import com.univhis.entity.Order;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

@Mapper // 标识该接口为 MyBatis 的 Mapper 接口
public interface OrderMapper extends BaseMapper<Order> { // 定义 OrderMapper 接口，继承 BaseMapper，泛型为 Order 实体类
    // 此接口将自动拥有 CRUD 操作的方法，无需额外定义
}
