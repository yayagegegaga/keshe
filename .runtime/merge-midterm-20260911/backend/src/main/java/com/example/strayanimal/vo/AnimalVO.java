package com.example.strayanimal.vo;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class AnimalVO {

    private Long id;

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

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
