package com.lite.gym.model;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.util.List;

/**
 * 训练动作实体类
 */
@Data
@TableName("training_action")
public class TrainingAction {
    
    @TableId(value = "action_id", type = IdType.AUTO)
    private Long actionId;
    
    private Long trainingId;
    
    private String name;
    
    private String unit;

    @TableField(exist = false)
    private List<TrainingSet> trainingSets;
} 