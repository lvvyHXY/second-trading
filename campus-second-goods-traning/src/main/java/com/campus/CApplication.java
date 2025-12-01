package com.campus;

import com.campus.entity.JWTProperties;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@ServletComponentScan
@SpringBootApplication
@Slf4j
@EnableTransactionManagement //开启注解方式的事务管理
@EnableCaching  //开启springCache,启动类中添加
@EnableScheduling //开启springTask,启动类中添加
@MapperScan("com.campus.mapper")
@EnableConfigurationProperties(JWTProperties.class)
public class CApplication {
    public static void main(String[] args) {
        SpringApplication.run(CApplication.class, args);
        log.info("server started");
    }
}
