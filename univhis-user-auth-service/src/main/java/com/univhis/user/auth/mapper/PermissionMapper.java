package com.univhis.user.auth.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper; // 导入 MyBatis-Plus 的基础 Mapper
import com.univhis.entity.Permission; // 导入 Permission 实体类
import org.apache.ibatis.annotations.Mapper; // 导入 MyBatis 的 Mapper 注解

@Mapper // 标识该接口为 MyBatis 的 Mapper 接口
public interface PermissionMapper extends BaseMapper<Permission> { // 继承 MyBatis-Plus 的 BaseMapper，泛型为 Permission 实体类
    // 此接口将自动拥有 CRUD 操作的方法，无需额外定义
}

