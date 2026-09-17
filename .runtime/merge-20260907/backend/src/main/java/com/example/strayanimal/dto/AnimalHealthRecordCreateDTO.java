package com.example.strayanimal.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class AnimalHealthRecordCreateDTO {

    @NotBlank(message = "健康状态不能为空")
    private String healthStatus;

    private String hospital;

    private String treatmentContent;

    @NotNull(message = "记录时间不能为空")
    private LocalDateTime recordTime;

    private String remark;
}
