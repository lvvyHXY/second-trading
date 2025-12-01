package com.campus.entity;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "campus.jwt")
public class JWTProperties {
    private String userSecretKey = "default-secret-key";
    private long userTtl = 43200000; // 默认12小时
    private String userTokenName = "token";
}
