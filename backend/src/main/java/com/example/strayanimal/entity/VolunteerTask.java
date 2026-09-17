package com.example.strayanimal.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.example.strayanimal.common.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@TableName("volunteer_task")
public class VolunteerTask extends BaseEntity {
    private String title;
    private String taskType;
    private String description;
    private String location;
    private String status;
    private Long publisherId;
    private Long volunteerId;
    private LocalDateTime startTime;
    private LocalDateTime finishTime;
    private String reviewRemark;
    private Integer version;
}
