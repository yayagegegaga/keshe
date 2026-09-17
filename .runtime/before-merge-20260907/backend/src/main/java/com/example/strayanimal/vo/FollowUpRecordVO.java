package com.example.strayanimal.vo;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class FollowUpRecordVO {
    private Long id;
    private Long applicationId;
    private Long animalId;
    private String animalName;
    private Long adopterId;
    private String adopterName;
    private LocalDateTime followTime;
    private String content;
    private String animalCondition;
    private String imageUrl;
    private String remark;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
