package com.campus.controller;

import com.campus.entity.Review;
import com.campus.service.ReviewService;
import com.campus.utils.CurrentHolder;
import com.campus.utils.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/review")
public class ReviewController {
    @Autowired
    private ReviewService reviewService;

    /**
     * 发布评价
     */
    @PostMapping
    public Result<Boolean> saveReview(@RequestBody Review review) {
        return reviewService.saveReview(review);
    }

    /**
     * 根据商品ID查询评价列表
     */
    @GetMapping("/product/{productId}")
    public Result<List<Review>> getReviewsByProductId(@PathVariable Long productId) {
        return reviewService.getReviewsByProductId(productId);
    }

    /**
     * 根据用户ID查询收到的评价列表
     */
    @GetMapping("/user/{userId}")
    public Result<List<Review>> getReviewsByUserId(@PathVariable Long userId) {
        return reviewService.getReviewsByRevieweeId(userId);
    }

    /**
     * 根据交易ID查询评价
     */
    @GetMapping("/order/{orderId}")
    public Result<Review> getReviewByOrderId(@PathVariable Long orderId) {
        return reviewService.getReviewByOrderId(orderId);
    }

    /**
     * 查询我的评价列表（我发出的评价）
     */
    @GetMapping("/my")
    public Result<List<Review>> getMyReviews() {
        Long currentUserId = CurrentHolder.getCurrentId();
        if (currentUserId == null) {
            return Result.fail("用户未登录");
        }
        return reviewService.getReviewsByReviewerId(currentUserId);
    }
}
