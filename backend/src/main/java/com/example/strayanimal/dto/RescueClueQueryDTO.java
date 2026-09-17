package com.example.strayanimal.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RescueClueQueryDTO {

    private Long pageNum = 1L;

    private Long pageSize = 10L;

    private String status;

    private String animalType;

    private String emergencyLevel;

    private String keyword;

    private Long submitterId;
}
