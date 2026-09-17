package com.example.strayanimal.vo;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class AnimalImageVO {

    private Long id;

    private Long animalId;

    private String imageUrl;

    private String imageType;

    private LocalDateTime createTime;
}
