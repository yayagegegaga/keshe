package com.example.strayanimal.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VolunteerTaskFinishDTO {
    @NotBlank(message = "完成内容不能为空")
    private String content;
    private String imageUrl;
}
