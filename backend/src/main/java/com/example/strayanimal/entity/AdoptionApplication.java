package com.example.strayanimal.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.example.strayanimal.common.BaseEntity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@TableName("adoption_application")
public class AdoptionApplication extends BaseEntity {
    private Long animalId;
    private Long applicantId;
    private String applicantName;
    private String phone;
    private String address;
    private String reason;
    private String experience;
    private String status;
    private String reviewRemark;
}
