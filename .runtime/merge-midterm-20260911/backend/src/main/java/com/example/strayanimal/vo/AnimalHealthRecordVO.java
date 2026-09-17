package com.example.strayanimal.vo;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class AnimalHealthRecordVO {

    private Long id;

    private Long animalId;

    private String healthStatus;

    private String hospital;

    private String treatmentContent;

    private LocalDateTime recordTime;

    private String remark;

    private LocalDateTime createTime;
}
