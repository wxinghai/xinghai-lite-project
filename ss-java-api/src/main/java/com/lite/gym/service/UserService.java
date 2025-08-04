 package com.lite.gym.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lite.gym.model.User;

/**
 * 用户服务接口
 */
public interface UserService extends IService<User> {
    
    /**
     * 根据用户名查找用户
     * @param username 用户名
     * @return 用户对象
     */
    User findByUsername(String username);
    
    /**
     * 根据邮箱查找用户
     * @param email 邮箱
     * @return 用户对象
     */
    User findByEmail(String email);
    
    /**
     * 用户注册
     * @param user 用户信息
     * @return 注册后的用户信息
     */
    User register(User user);
} 