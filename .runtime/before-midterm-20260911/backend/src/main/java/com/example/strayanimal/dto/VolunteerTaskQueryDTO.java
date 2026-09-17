package com.example.strayanimal.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VolunteerTaskQueryDTO {
    private Long pageNum = 1L;
    private Long pageSize = 10L;
    private String status;
    private String taskType;
    private Long volunteerId;
    private String keyword;
}
