package com.campus.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.campus.dto.UserDTO;
import com.campus.dto.UserRegisterDTO;
import com.campus.entity.User;
import com.campus.utils.Result;


public interface UserService extends IService<User> {

    User login(UserDTO userDTO);
    
    User register(UserRegisterDTO userRegisterDTO);

    Result<User> logout();

    User profile();
}
