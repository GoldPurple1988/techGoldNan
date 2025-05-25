package com.univhis.mapper;

import com.univhis.entity.Address;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

@Mapper // 标识该接口为 MyBatis 的 Mapper 接口
public interface AddressMapper extends BaseMapper<Address> { // 继承 MyBatis-Plus 的 BaseMapper，泛型为 Address 实体类
    // 此接口将自动拥有 CRUD 操作的方法，无需额外定义
}
