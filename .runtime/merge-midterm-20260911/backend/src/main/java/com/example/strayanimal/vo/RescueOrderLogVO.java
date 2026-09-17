package com.example.strayanimal.vo;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class RescueOrderLogVO {

    private Long id;
    private Long orderId;
    private Long operatorId;
    private String operatorName;
    private String oldStatus;
    private String newStatus;
    private String operationType;
    private String remark;
    private LocalDateTime createTime;
}
