package com.example.strayanimal.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@TableName("adoption_review_log")
public class AdoptionReviewLog {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long applicationId;
    private Long reviewerId;
    private String oldStatus;
    private String newStatus;
    private String operationType;
    private String remark;
    private LocalDateTime createTime;
}
