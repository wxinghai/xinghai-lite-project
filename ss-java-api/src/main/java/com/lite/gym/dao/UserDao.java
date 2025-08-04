package com.lite.gym.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lite.gym.model.User;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * 用户数据访问接口
 */
@Mapper
@Repository
public interface UserDao extends BaseMapper<User> {
    
} 