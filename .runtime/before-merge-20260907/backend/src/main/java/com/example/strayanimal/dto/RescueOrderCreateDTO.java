package com.example.strayanimal.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RescueOrderCreateDTO {

    private Long clueId;

    @NotBlank(message = "工单标题不能为空")
    private String title;

    @NotBlank(message = "工单描述不能为空")
    private String description;

    @NotBlank(message = "地点不能为空")
    private String location;

    @NotBlank(message = "紧急程度不能为空")
    private String emergencyLevel;
}
