package com.univhis.user.auth.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.univhis.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper // 标识该接口为 MyBatis 的 Mapper 接口
public interface UserMapper extends BaseMapper<User> { // 继承 MyBatis-Plus 的 BaseMapper，泛型为 User 实体类
    // 此接口将自动拥有 CRUD 操作的方法，无需额外定义
}
