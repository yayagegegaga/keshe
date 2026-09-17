package com.example.strayanimal.vo;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class AdoptionApplicationDetailVO extends AdoptionApplicationVO {
    private AnimalVO animal;
    private UserInfoVO applicant;
    private List<AdoptionReviewLogVO> logs;
}
