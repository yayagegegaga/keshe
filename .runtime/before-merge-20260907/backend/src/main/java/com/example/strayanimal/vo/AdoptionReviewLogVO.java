package com.example.strayanimal.vo;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class AdoptionReviewLogVO {
    private Long id;
    private Long applicationId;
    private Long reviewerId;
    private String reviewerName;
    private String oldStatus;
    private String newStatus;
    private String operationType;
    private String remark;
    private LocalDateTime createTime;
}
