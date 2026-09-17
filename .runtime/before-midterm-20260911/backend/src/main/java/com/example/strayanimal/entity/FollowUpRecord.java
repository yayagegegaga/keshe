package com.example.strayanimal.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.example.strayanimal.common.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@TableName("follow_up_record")
public class FollowUpRecord extends BaseEntity {
    private Long applicationId;
    private Long animalId;
    private Long adopterId;
    private LocalDateTime followTime;
    private String content;
    private String animalCondition;
    private String imageUrl;
    private String remark;
}
