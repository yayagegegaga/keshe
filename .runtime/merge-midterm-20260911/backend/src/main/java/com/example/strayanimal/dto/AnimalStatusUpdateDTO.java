package com.example.strayanimal.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AnimalStatusUpdateDTO {

    @NotBlank(message = "动物状态不能为空")
    private String status;

    private String remark;
}
