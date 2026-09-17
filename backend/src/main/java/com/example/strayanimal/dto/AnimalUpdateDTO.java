package com.example.strayanimal.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AnimalUpdateDTO {

    @NotBlank(message = "动物名称不能为空")
    private String name;

    @NotBlank(message = "动物类型不能为空")
    private String type;

    private String gender;

    private String ageStage;

    private String color;

    private String healthStatus;

    private String sterilizationStatus;

    private String vaccineStatus;

    private String area;

    @NotBlank(message = "动物状态不能为空")
    private String status;

    private String description;

    private String coverImage;
}
