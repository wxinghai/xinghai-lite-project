package com.lite.gym.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lite.gym.model.Training;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * 训练数据访问接口
 */
@Mapper
@Repository
public interface TrainingDao extends BaseMapper<Training> {
    
} 