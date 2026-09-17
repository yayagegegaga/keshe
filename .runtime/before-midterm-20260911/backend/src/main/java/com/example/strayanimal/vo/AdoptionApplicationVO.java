package com.example.strayanimal.vo;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class AdoptionApplicationVO {
    private Long id;
    private Long animalId;
    private String animalName;
    private String animalNo;
    private String animalStatus;
    private Long applicantId;
    private String applicantName;
    private String phone;
    private String address;
    private String reason;
    private String experience;
    private String status;
    private String reviewRemark;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
