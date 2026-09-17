package com.example.strayanimal.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdoptionQueryDTO {
    private Long pageNum = 1L;
    private Long pageSize = 10L;
    private String status;
    private Long animalId;
    private Long applicantId;
    private String keyword;
}
