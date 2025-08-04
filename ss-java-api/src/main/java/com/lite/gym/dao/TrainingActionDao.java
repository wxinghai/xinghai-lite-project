package com.lite.gym.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lite.gym.model.TrainingAction;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * 训练动作数据访问接口
 */
@Mapper
public interface TrainingActionDao extends BaseMapper<TrainingAction> {
    
} 