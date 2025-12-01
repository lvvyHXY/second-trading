package com.campus.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.campus.dto.UserDTO;
import com.campus.dto.UserRegisterDTO;
import com.campus.entity.User;
import com.campus.mapper.UserMapper;
import com.campus.service.UserService;
import com.campus.utils.Result;
import com.campus.utils.UserHolder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    // 默认信用分
    private static final Integer DEFAULT_CREDIT_SCORE = 100;

    @Override
    public User login(UserDTO userDTO) {
        // 根据用户名查询用户
        User user = this.getOne(new QueryWrapper<User>().eq("username", userDTO.getUsername()));
        
        // 验证用户是否存在
        if (user == null) {
            return null;
        }
        
        // 验证密码是否正确（注意：实际项目中应该使用密码加密验证）
        if (!user.getPassword().equals(userDTO.getPassword())) {
            return null;
        }
        
        return user;
    }
    
    @Override
    public User register(UserRegisterDTO userRegisterDTO) {
        // 检查用户名是否已存在
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", userRegisterDTO.getUsername());
        User existingUser = this.getOne(queryWrapper);
        if (existingUser != null) {
            throw new RuntimeException("用户名已存在");
        }
        
        // 创建新用户
        User user = new User();
        user.setUsername(userRegisterDTO.getUsername());
        user.setPassword(userRegisterDTO.getPassword());
        user.setEmail(userRegisterDTO.getEmail());
        user.setPhoneNumber(userRegisterDTO.getPhoneNumber());
        user.setRealName(userRegisterDTO.getRealName());
        user.setSchoolId(userRegisterDTO.getSchoolId());
        user.setCreditScore(DEFAULT_CREDIT_SCORE);
        
        // 保存用户信息
        this.save(user);
        
        return user;
    }

    @Override
    public Result<User> logout() {
        return null;
    }

    @Override
    public User profile() {
        // 获取当前用户信息
        UserDTO userDTO = UserHolder.getUser();
        Long userId = userDTO.getId();
        User user = this.getById(userId);
        if (user != null) {
            return user;
        }
        return null;
    }
}
