package com.lite.gym.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lite.gym.common.LogicalException;
import com.lite.gym.dao.UserDao;
import com.lite.gym.model.User;
import com.lite.gym.service.UserService;
import com.lite.gym.util.CodeUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

/**
 * 用户服务实现类
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserDao, User> implements UserService {

    @Override
    public User findByUsername(String username) {
        if (!StringUtils.hasText(username)) {
            return null;
        }
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUsername, username);
        return getOne(queryWrapper);
    }

    @Override
    public User findByEmail(String email) {
        if (!StringUtils.hasText(email)) {
            return null;
        }
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getEmail, email);
        return getOne(queryWrapper);
    }

    @Override
    @Transactional
    public User register(User user) {
        // 检查用户名是否已存在
        User existUser = findByUsername(user.getUsername());
        if (existUser != null) {
            throw new LogicalException("用户名已被使用");
        }
        
        // 检查邮箱是否已存在
        existUser = findByEmail(user.getEmail());
        if (existUser != null) {
            throw new LogicalException("邮箱已被注册");
        }
        
        // 密码加密
        user.setHashedPassword(CodeUtil.encryptPassword(user.getHashedPassword()));
        user.setIsActive(true);
        
        // 保存用户
        save(user);
        
        return user;
    }
} 