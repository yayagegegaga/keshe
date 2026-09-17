package com.example.strayanimal.vo;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FollowUpRecordDetailVO extends FollowUpRecordVO {
    private AdoptionApplicationVO application;
    private AnimalVO animal;
    private UserInfoVO adopter;
}
