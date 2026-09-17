package com.example.strayanimal.vo;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class VolunteerTaskVO {
    private Long id;
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
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
