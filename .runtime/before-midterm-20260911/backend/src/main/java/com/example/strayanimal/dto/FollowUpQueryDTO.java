package com.example.strayanimal.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FollowUpQueryDTO {
    private Long pageNum = 1L;
    private Long pageSize = 10L;
    private Long animalId;
    private Long adopterId;
    private Long applicationId;
    private String keyword;
}
