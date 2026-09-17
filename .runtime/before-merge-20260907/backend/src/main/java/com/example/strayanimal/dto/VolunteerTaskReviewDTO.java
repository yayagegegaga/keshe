package com.example.strayanimal.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VolunteerTaskReviewDTO {
    @NotNull(message = "审核结果不能为空")
    private Boolean approved;
    private String reviewRemark;
}
