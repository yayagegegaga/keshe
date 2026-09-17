package com.example.strayanimal.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RescueClueReviewDTO {

    @NotNull(message = "审核结果不能为空")
    private Boolean approved;

    private String reviewRemark;

    private Boolean createOrder = true;

    private String orderTitle;

    private String orderDescription;
}
