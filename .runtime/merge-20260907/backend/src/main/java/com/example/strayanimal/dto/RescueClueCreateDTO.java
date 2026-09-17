package com.example.strayanimal.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RescueClueCreateDTO {

    @NotBlank(message = "动物类型不能为空")
    private String animalType;

    @NotBlank(message = "发现地点不能为空")
    private String location;

    @NotBlank(message = "线索描述不能为空")
    private String description;

    @NotBlank(message = "紧急程度不能为空")
    private String emergencyLevel;

    private String imageUrl;

    private String contact;
}
