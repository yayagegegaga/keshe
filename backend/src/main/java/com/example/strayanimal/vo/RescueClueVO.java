package com.example.strayanimal.vo;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class RescueClueVO {

    private Long id;
    private Long submitterId;
    private String animalType;
    private String location;
    private String description;
    private String emergencyLevel;
    private String imageUrl;
    private String contact;
    private String status;
    private Long reviewerId;
    private String reviewRemark;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
