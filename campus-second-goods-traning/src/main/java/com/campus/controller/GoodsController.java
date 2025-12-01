package com.campus.controller;

import com.campus.entity.Goods;
import com.campus.entity.User;
import com.campus.mapper.UserMapper;
import com.campus.service.GoodsService;
import com.campus.utils.CurrentHolder;
import com.campus.utils.Result;
import com.campus.vo.GoodsDetailVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/goods")
@Slf4j
public class GoodsController {
    
    @Autowired
    private GoodsService goodsService;
    
    @Autowired
    private UserMapper userMapper;
    
    /**
     * 创建商品
     */
    @PostMapping("/create")
    public Result<Boolean> createGoods(@Valid @RequestBody Goods goods) {
        try {
            // 从ThreadLocal获取当前用户ID
            Long currentUserId = CurrentHolder.getCurrentId();
            if (currentUserId == null) {
                log.warn("未获取到当前用户ID");
                return Result.fail("用户未登录");
            }
            
            // 设置卖家ID为当前用户
            goods.setSellerId(currentUserId);
            log.info("创建商品请求: {}, 卖家ID: {}", goods.getName(), currentUserId);
            
            boolean success = goodsService.createGoods(goods);
            if (success) {
                log.info("商品创建成功: {}", goods.getName());
                return Result.success(true, "商品创建成功");
            } else {
                log.warn("商品创建失败: {}", goods.getName());
                return Result.fail("商品创建失败");
            }
        } catch (Exception e) {
            log.error("商品创建异常: {}", goods.getName(), e);
            return Result.fail("商品创建失败: " + e.getMessage());
        }
    }
    
    /**
     * 修改商品
     */
    @PutMapping("/update")
    public Result<Boolean> updateGoods(@Valid @RequestBody Goods goods) {
        try {
            // 从ThreadLocal获取当前用户ID
            Long currentUserId = CurrentHolder.getCurrentId();
            if (currentUserId == null) {
                log.warn("未获取到当前用户ID");
                return Result.fail("用户未登录");
            }
            
            // 检查商品是否存在
            Goods existingGoods = goodsService.getGoodsById(goods.getId());
            if (existingGoods == null) {
                return Result.fail("商品不存在");
            }
            
            // 权限校验：只有卖家本人可以修改
            if (!existingGoods.getSellerId().equals(currentUserId)) {
                log.warn("用户{}尝试修改不属于自己的商品{}", currentUserId, goods.getId());
                return Result.fail("您无权修改此商品");
            }
            
            boolean success = goodsService.updateGoods(goods);
            if (success) {
                log.info("商品修改成功: 商品ID={}, 用户ID={}", goods.getId(), currentUserId);
                return Result.success(true, "商品修改成功");
            } else {
                return Result.fail("商品修改失败");
            }
        } catch (Exception e) {
            log.error("商品修改异常", e);
            return Result.fail("商品修改失败: " + e.getMessage());
        }
    }
    
    /**
     * 根据ID获取商品详情（包含卖家信息）
     */
    @GetMapping("/{id}")
    public Result<GoodsDetailVo> getGoodsById(@PathVariable Long id) {
        try {
            Goods goods = goodsService.getGoodsById(id);
            if (goods == null) {
                return Result.fail("商品不存在");
            }
            
            // 构建返回对象
            GoodsDetailVo detailVo = new GoodsDetailVo();
            detailVo.setGoods(goods);
            
            // 获取卖家信息
            User seller = userMapper.selectById(goods.getSellerId());
            if (seller != null) {
                GoodsDetailVo.SellerInfo sellerInfo = new GoodsDetailVo.SellerInfo();
                sellerInfo.setSellerId(goods.getSellerId());
                sellerInfo.setUsername(seller.getUsername());
                sellerInfo.setAvatarUrl(seller.getAvatarUrl());
                sellerInfo.setCreditScore(seller.getCreditScore());
                detailVo.setSeller(sellerInfo);
            }
            
            return Result.success(detailVo, "获取商品详情成功");
        } catch (Exception e) {
            log.error("获取商品详情失败", e);
            return Result.fail("获取商品详情失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取所有商品
     */
    @GetMapping("/list")
    public Result<List<Goods>> getAllGoods() {
        try {
            List<Goods> goodsList = goodsService.getAllGoods();
            return Result.success(goodsList, "获取商品列表成功");
        } catch (Exception e) {
            return Result.fail("获取商品列表失败: " + e.getMessage());
        }
    }
    
    /**
     * 根据分类获取商品
     */
    @GetMapping("/category/{category}")
    public Result<List<Goods>> getGoodsByCategory(@PathVariable String category) {
        try {
            List<Goods> goodsList = goodsService.getGoodsByCategory(category);
            return Result.success(goodsList, "获取分类商品成功");
        } catch (Exception e) {
            return Result.fail("获取分类商品失败: " + e.getMessage());
        }
    }
    
    /**
     * 根据卖家ID获取商品
     */
    @GetMapping("/seller/{sellerId}")
    public Result<List<Goods>> getGoodsBySellerId(@PathVariable Long sellerId) {
        try {
            List<Goods> goodsList = goodsService.getGoodsBySellerId(sellerId);
            return Result.success(goodsList, "获取卖家商品成功");
        } catch (Exception e) {
            return Result.fail("获取卖家商品失败: " + e.getMessage());
        }
    }
    
    /**
     * 删除商品
     */
    @DeleteMapping("/{id}")
    public Result<Boolean> deleteGoods(@PathVariable Long id) {
        try {
            // 从ThreadLocal获取当前用户ID
            Long currentUserId = CurrentHolder.getCurrentId();
            if (currentUserId == null) {
                log.warn("未获取到当前用户ID");
                return Result.fail("用户未登录");
            }
            
            // 检查商品是否存在
            Goods existingGoods = goodsService.getGoodsById(id);
            if (existingGoods == null) {
                return Result.fail("商品不存在");
            }
            
            // 权限校验：只有卖家本人可以删除
            if (!existingGoods.getSellerId().equals(currentUserId)) {
                log.warn("用户{}尝试删除不属于自己的商品{}", currentUserId, id);
                return Result.fail("您无权删除此商品");
            }
            
            boolean success = goodsService.deleteGoods(id);
            if (success) {
                log.info("商品删除成功: 商品ID={}, 用户ID={}", id, currentUserId);
                return Result.success(true, "商品删除成功");
            } else {
                return Result.fail("商品删除失败");
            }
        } catch (Exception e) {
            log.error("商品删除异常", e);
            return Result.fail("商品删除失败: " + e.getMessage());
        }
    }
}
