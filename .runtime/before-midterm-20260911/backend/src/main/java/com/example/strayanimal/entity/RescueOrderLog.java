package com.example.strayanimal.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@TableName("rescue_order_log")
public class RescueOrderLog {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long orderId;

    private Long operatorId;

    private String oldStatus;

    private String newStatus;

    private String operationType;

    private String remark;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
