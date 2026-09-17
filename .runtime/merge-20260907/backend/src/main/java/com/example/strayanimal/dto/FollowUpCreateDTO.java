package com.example.strayanimal.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class FollowUpCreateDTO {
    @NotNull(message = "领养申请ID不能为空")
    private Long applicationId;
    private LocalDateTime followTime;
    @NotBlank(message = "回访内容不能为空")
    private String content;
    private String animalCondition;
    private String imageUrl;
    private String remark;
}
