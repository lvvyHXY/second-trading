package com.campus.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.campus.entity.Review;
import com.campus.entity.Trade;
import com.campus.mapper.ReviewMapper;
import com.campus.mapper.TradeMapper;
import com.campus.service.ReviewService;
import com.campus.utils.CurrentHolder;
import com.campus.utils.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class ReviewServiceImpl extends ServiceImpl<ReviewMapper, Review> implements ReviewService {

    @Autowired
    private TradeMapper tradeMapper;

    @Override
    public Result<Boolean> saveReview(Review review) {
        // 1.获取当前登陆的用户ID
        Long currentUserId = CurrentHolder.getCurrentId();
        if (currentUserId == null) {
            return Result.fail("用户未登录");
        }
        review.setReviewerId(currentUserId);

        // 2.验证交易是否存在且状态为COMPLETED
        if (review.getOrderId() == null) {
            return Result.fail("订单ID不能为空");
        }
        Trade trade = tradeMapper.selectTradeById(review.getOrderId());
        if (trade == null) {
            return Result.fail("交易不存在");
        }
        if (!"COMPLETED".equals(trade.getStatus())) {
            return Result.fail("只能对已完成的交易进行评价");
        }

        // 3.检查是否已评价过该交易
        QueryWrapper<Review> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("order_id", review.getOrderId());
        queryWrapper.eq("reviewer_id", currentUserId);
        Review existingReview = getOne(queryWrapper);
        if (existingReview != null) {
            return Result.fail("您已经评价过该交易");
        }

        // 4.设置被评价者ID（如果是买家评价，被评价者是卖家；如果是卖家评价，被评价者是买家）
        if (currentUserId.equals(trade.getBuyerId())) {
            review.setRevieweeId(trade.getSellerId());
        } else if (currentUserId.equals(trade.getSellerId())) {
            review.setRevieweeId(trade.getBuyerId());
        } else {
            return Result.fail("您无权评价此交易");
        }

        // 5.设置商品ID
        review.setProductId(trade.getProductId());

        // 6.保存评论信息到数据库
        boolean issuccess = save(review);
        // 7.判断保存是否成功并返回
        if (!issuccess) {
            log.error("保存评价失败，订单ID: {}, 评价者ID: {}", review.getOrderId(), currentUserId);
            return Result.fail(500, "保存失败，请重试");
        }
        log.info("评价保存成功，订单ID: {}, 评价者ID: {}", review.getOrderId(), currentUserId);
        return Result.success(true, "评价成功");
    }

    @Override
    public Result<List<Review>> getReviewsByProductId(Long productId) {
        if (productId == null) {
            return Result.fail("商品ID不能为空");
        }
        QueryWrapper<Review> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("product_id", productId);
        queryWrapper.orderByDesc("create_time");
        List<Review> reviews = list(queryWrapper);
        return Result.success(reviews, "查询成功");
    }

    @Override
    public Result<List<Review>> getReviewsByRevieweeId(Long revieweeId) {
        if (revieweeId == null) {
            return Result.fail("用户ID不能为空");
        }
        QueryWrapper<Review> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("reviewee_id", revieweeId);
        queryWrapper.orderByDesc("create_time");
        List<Review> reviews = list(queryWrapper);
        return Result.success(reviews, "查询成功");
    }

    @Override
    public Result<List<Review>> getReviewsByReviewerId(Long reviewerId) {
        if (reviewerId == null) {
            return Result.fail("用户ID不能为空");
        }
        QueryWrapper<Review> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("reviewer_id", reviewerId);
        queryWrapper.orderByDesc("create_time");
        List<Review> reviews = list(queryWrapper);
        return Result.success(reviews, "查询成功");
    }

    @Override
    public Result<Review> getReviewByOrderId(Long orderId) {
        if (orderId == null) {
            return Result.fail("订单ID不能为空");
        }
        QueryWrapper<Review> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("order_id", orderId);
        Review review = getOne(queryWrapper);
        if (review == null) {
            return Result.fail("未找到该交易的评价");
        }
        return Result.success(review, "查询成功");
    }
}
