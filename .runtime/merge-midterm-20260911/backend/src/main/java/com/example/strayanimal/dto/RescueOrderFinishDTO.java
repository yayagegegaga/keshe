package com.example.strayanimal.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RescueOrderFinishDTO {

    @NotBlank(message = "处理结果不能为空")
    private String processResult;

    private String processImage;

    private String remark;
}
