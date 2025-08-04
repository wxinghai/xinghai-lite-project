package com.lite.gym.model;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;

/**
 * 训练组实体类
 */
@Data
@TableName("training_set")
public class TrainingSet {
    
    @TableId(value = "set_id", type = IdType.AUTO)
    private Long setId;
    
    private Long actionId;
    
    private Boolean completed;
    
    private BigDecimal weight;
    
    private Integer reps;
} 