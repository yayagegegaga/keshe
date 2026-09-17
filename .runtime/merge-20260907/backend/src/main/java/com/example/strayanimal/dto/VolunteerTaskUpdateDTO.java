package com.example.strayanimal.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VolunteerTaskUpdateDTO {
    @NotBlank(message = "任务标题不能为空")
    private String title;
    @NotBlank(message = "任务类型不能为空")
    private String taskType;
    private String description;
    @NotBlank(message = "任务地点不能为空")
    private String location;
}
