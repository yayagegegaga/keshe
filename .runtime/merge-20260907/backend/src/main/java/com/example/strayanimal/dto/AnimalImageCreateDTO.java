package com.example.strayanimal.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AnimalImageCreateDTO {

    @NotBlank(message = "图片地址不能为空")
    private String imageUrl;

    @NotBlank(message = "图片类型不能为空")
    private String imageType;
}
