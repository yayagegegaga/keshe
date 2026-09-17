package com.example.strayanimal.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RescueOrderQueryDTO {

    private Long pageNum = 1L;

    private Long pageSize = 10L;

    private String status;

    private String emergencyLevel;

    private Long handlerId;

    private String keyword;
}
