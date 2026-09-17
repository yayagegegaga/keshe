package com.example.strayanimal.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdoptionApplyDTO {
    @NotNull(message = "动物ID不能为空")
    private Long animalId;

    @NotBlank(message = "申请人姓名不能为空")
    private String applicantName;

    @NotBlank(message = "联系电话不能为空")
    private String phone;

    private String address;

    @NotBlank(message = "申请理由不能为空")
    private String reason;

    private String experience;
}
