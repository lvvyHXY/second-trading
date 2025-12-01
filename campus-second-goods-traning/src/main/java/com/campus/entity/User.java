package com.campus.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("users")
public class User {
    /*
    * user_id INT AUTO_INCREMENT PRIMARY KEY COMMENT '用户ID，主键',
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名，唯一',
    password VARCHAR(255) NOT NULL COMMENT '密码',
    email VARCHAR(100) COMMENT '邮箱',
    phone_number VARCHAR(20) COMMENT '手机号',
    avatar_url VARCHAR(255) COMMENT '头像链接',
    real_name VARCHAR(50) COMMENT '真实姓名',
    school_id VARCHAR(20) COMMENT '学号',
    credit_score INT DEFAULT 100 COMMENT '信用分，默认100',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
    *
    */
    @TableId(type = IdType.AUTO)
    private Long userId;
    private String username;
    private String password;
    private String email;
    private String phoneNumber;
    private String avatarUrl;
    private String realName;
    private String schoolId;
    private Integer creditScore;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    @TableField(exist = false)  // 标记为不存在于数据库的字段
    private String token; //令牌（仅用于业务逻辑，不存储在数据库）

    public String getNickname() {
        return username;
    }

    public String getAvatar() {
        return avatarUrl;
    }

    public Long getId() {
        return userId;
    }
}
