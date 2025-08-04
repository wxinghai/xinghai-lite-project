package com.lite.gym.model;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.util.Date;

/**
 * 用户实体类
 */
@Data
@TableName("users")
public class User {
    
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    
    private String username;
    
    private String email;
    
    private String hashedPassword;
    
    private String fullName;
    
    private Boolean isActive;
    
    @TableField(fill = FieldFill.INSERT)
    private Date createdAt;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updatedAt;
} 