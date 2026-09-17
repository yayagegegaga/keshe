package com.example.strayanimal.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.example.strayanimal.common.BaseEntity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@TableName("rescue_clue")
public class RescueClue extends BaseEntity {

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
}
