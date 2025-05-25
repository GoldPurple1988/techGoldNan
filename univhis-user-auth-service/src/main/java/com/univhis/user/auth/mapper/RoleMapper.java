package com.univhis.user.auth.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.univhis.entity.Role;
import org.apache.ibatis.annotations.Mapper;

@Mapper // 标识该接口为 MyBatis 的 Mapper 接口
public interface RoleMapper extends BaseMapper<Role> { // 定义 RoleMapper 接口，继承 BaseMapper，泛型为 Role 实体类
    // 此接口将自动拥有 CRUD 操作的方法，无需额外定义
}

