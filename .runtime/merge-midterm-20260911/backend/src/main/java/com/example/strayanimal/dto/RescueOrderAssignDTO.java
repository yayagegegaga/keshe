package com.example.strayanimal.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RescueOrderAssignDTO {

    @NotNull(message = "处理人不能为空")
    private Long handlerId;

    private String remark;
}
