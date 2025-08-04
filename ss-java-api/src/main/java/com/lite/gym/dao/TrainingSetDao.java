package com.lite.gym.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lite.gym.model.TrainingSet;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * 训练组数据访问接口
 */
@Mapper
public interface TrainingSetDao extends BaseMapper<TrainingSet> {

}
