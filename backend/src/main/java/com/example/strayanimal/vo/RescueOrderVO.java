package com.example.strayanimal.vo;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class RescueOrderVO {

    private Long id;
    private Long clueId;
    private String title;
    private String description;
    private String location;
    private String emergencyLevel;
    private String status;
    private Long creatorId;
    private Long handlerId;
    private String processResult;
    private String processImage;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private LocalDateTime closeTime;
}
