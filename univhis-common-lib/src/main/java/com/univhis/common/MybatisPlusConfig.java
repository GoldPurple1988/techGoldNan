package com.univhis.common;

import com.baomidou.mybatisplus.annotation.DbType; // 新增导入
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor; // 新增导入
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor; // 新增导入
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @date
 * @description mybatis-plus 分页插件
 */
@Configuration
public class MybatisPlusConfig {

    /**
     * 新版分页插件
     * Mybatis-Plus 3.4.0 及以上版本推荐使用 MybatisPlusInterceptor
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        // 添加分页插件
        // DbType.MYSQL 表示您使用的是 MySQL 数据库，请根据实际情况选择对应的数据库类型
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        return interceptor;
    }

}