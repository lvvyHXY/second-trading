package com.campus.controller;

import com.campus.constants.JwtClaimsConstant;
import com.campus.dto.UserDTO;
import com.campus.dto.UserRegisterDTO;
import com.campus.entity.JWTProperties;
import com.campus.entity.User;
import com.campus.service.UserService;
import com.campus.utils.CurrentHolder;
import com.campus.utils.JwtUtil;
import com.campus.utils.Result;
import com.campus.utils.UserHolder;
import com.campus.vo.UserLoginVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private JWTProperties jwtProperties;

    @PostMapping("/login")
    public Result<UserLoginVO> login(@RequestBody UserDTO userDTO) {
        log.info("正在执行登录逻辑，用户名：{}", userDTO.getUsername());
        User user =  userService.login(userDTO);
        if(user == null){
            return Result.fail("用户名或密码错误");
        }
        //登录成功则创建JWT令牌
        Map<String, Object> claims = new HashMap<>();
        claims.put(JwtClaimsConstant.USER_ID, user.getId());
        String token = JwtUtil.createJWT(
                jwtProperties.getUserSecretKey(),
                jwtProperties.getUserTtl(),
                claims);

        UserLoginVO userLoginVO = UserLoginVO.builder()
                .id(user.getId())
                .userName(user.getUsername())
                .token(token)
                .build();
        log.info("登录成功，生成JWT令牌：{}", token);
        return Result.success(userLoginVO);
    }

    @PostMapping("/register")
    public Result<UserLoginVO> register(@RequestBody UserRegisterDTO userRegisterDTO) {
        log.info("正在执行注册逻辑，用户名：{}", userRegisterDTO.getUsername());
        try {
            User user = userService.register(userRegisterDTO);
            //注册成功则创建JWT令牌
            Map<String, Object> claims = new HashMap<>();
            claims.put(JwtClaimsConstant.USER_ID, user.getId());
            String token = JwtUtil.createJWT(
                    jwtProperties.getUserSecretKey(),
                    jwtProperties.getUserTtl(),
                    claims);

            UserLoginVO userLoginVO = UserLoginVO.builder()
                    .id(user.getId())
                    .userName(user.getUsername())
                    .token(token)
                    .build();
            log.info("注册成功，生成JWT令牌：{}", token);
            return Result.success(userLoginVO);
        } catch (RuntimeException e) {
            log.error("注册失败：{}", e.getMessage());
            return Result.fail(e.getMessage());
        }
    }

    @PostMapping("/logout")
    public Result<Boolean> logout() {
        return Result.success(true);
    }

    @GetMapping("/profile")
    public Result<User> profile() {
        // 使用CurrentHolder获取用户ID，而不是UserHolder
        Long userId = CurrentHolder.getCurrentId();
        log.info("正在查询用户信息，用户ID：{}", userId);
        if(userId == null) {
            return Result.fail("用户未登录");
        }
        User user = userService.getById(userId);
        return Result.success(user);
    }


}
