package com.lite.gym.model;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

/**
 * 训练实体类
 */
@Data
@TableName("training")
public class Training {
    
    @TableId(value = "training_id", type = IdType.AUTO)
    private Long trainingId;
    
    private Long userId;
    
    private String trainingDate;
    
    private String name;
    
    private BigDecimal volume;

    private Long elapsedTime;

    private String rangeTime;
    
    @TableField(exist = false)
    private List<TrainingAction> trainingActions;
} 