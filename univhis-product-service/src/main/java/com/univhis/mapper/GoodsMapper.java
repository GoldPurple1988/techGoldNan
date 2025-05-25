package com.univhis.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.univhis.entity.Goods;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper // 标识该接口为 MyBatis 的 Mapper 接口
public interface GoodsMapper extends BaseMapper<Goods> { // 定义 GoodsMapper 接口，继承 BaseMapper，泛型为 Goods 实体类

    // 根据名称进行分页查询
    IPage<Goods> findPage(Page<Goods> page, @Param("name") String name);

    // 根据类别 ID 进行分页查询
    @Select("select * from goods where category_id = #{id}")
    IPage<Goods> pageByCategory(Page<Goods> page, @Param("id") Long id);

    // 获取推荐商品列表
    List<Goods> getRecommend();

    // 按销售量降序查询商品列表
    @Select("select * from goods order by sales desc")
    List<Goods> sales();

    // 查询所有商品
    List<Goods> findAll();
}
