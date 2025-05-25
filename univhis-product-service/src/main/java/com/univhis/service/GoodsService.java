package com.univhis.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.univhis.entity.Goods;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.univhis.mapper.GoodsMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service // 使用 Spring 的 @Service 注解，将 GoodsService 标记为一个服务组件，纳入 Spring 容器管理
public class GoodsService extends ServiceImpl<GoodsMapper, Goods> { // GoodsService 继承自 ServiceImpl，实现了 Goods 实体类的通用 CRUD 操作，并指定了对应的 Mapper 为 GoodsMapper

    @Resource // 使用 JSR-250 规范中的 @Resource 注解，将 GoodsMapper 类型的 Bean 注入到 GoodsService 中
    private GoodsMapper goodsMapper; // 声明私有的 GoodsMapper 类型的成员变量 goodsMapper，用于进行数据库操作

    /**
     * 分页查询商品，支持按名称搜索
     * @param page 分页对象，封装了分页参数
     * @param name 商品名称，用于模糊查询
     * @return 包含分页结果的 IPage 对象
     */
    public IPage<Goods> findPage(Page<Goods> page, String name) { // 定义一个公共方法 findPage，接收分页对象和商品名称作为参数，返回包含分页结果的 IPage 对象
        return goodsMapper.findPage(page, name); // 调用 goodsMapper 的 findPage 方法，执行分页查询，并将结果返回
    }

    /**
     * 根据分类ID分页查询商品
     * @param page 分页对象，封装了分页参数
     * @param id 分类ID
     * @return 包含分页结果的 IPage 对象
     */
    public IPage<Goods> pageByCategory(Page<Goods> page, Long id) { // 定义一个公共方法 pageByCategory，接收分页对象和分类ID作为参数，返回包含分页结果的 IPage 对象
        return goodsMapper.pageByCategory(page, id); // 调用 goodsMapper 的 pageByCategory 方法，执行按分类分页查询，并将结果返回
    }

    /**
     * 获取推荐商品列表
     * @return 推荐商品列表
     */
    public List<Goods> recommend() { // 定义一个公共方法 recommend，用于获取推荐商品列表
        return goodsMapper.getRecommend(); // 调用 goodsMapper 的 getRecommend 方法，获取推荐商品列表，并将结果返回
    }

    /**
     * 获取销量最高的商品列表
     * @return 销量最高的商品列表
     */
    public List<Goods> sales() { // 定义一个公共方法 sales，用于获取销量最高的商品列表
        return goodsMapper.sales(); // 调用 goodsMapper 的 sales 方法，获取销量最高的商品列表，并将结果返回
    }

    /**
     * 获取所有商品列表
     * @return 所有商品列表
     */
    public List<Goods> findAll() {
        return goodsMapper.findAll();
    }
}


