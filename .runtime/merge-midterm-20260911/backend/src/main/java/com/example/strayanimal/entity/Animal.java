package com.example.strayanimal.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.example.strayanimal.common.BaseEntity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@TableName("animal")
public class Animal extends BaseEntity {

    private String animalNo;

    private String name;

    private String type;

    private String gender;

    private String ageStage;

    private String color;

    private String healthStatus;

    private String sterilizationStatus;

    private String vaccineStatus;

    private String area;

    private String status;

    private String description;

    private String coverImage;
}
