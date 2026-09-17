package com.example.strayanimal.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.example.strayanimal.common.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@TableName("rescue_order")
public class RescueOrder extends BaseEntity {

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

    private LocalDateTime closeTime;
}
