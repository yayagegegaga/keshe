package com.example.strayanimal.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AdoptionReviewOperationType {

    SUBMIT("SUBMIT", "提交申请"),
    FIRST_APPROVE("FIRST_APPROVE", "初审通过"),
    INTERVIEW("INTERVIEW", "面谈"),
    TRIAL("TRIAL", "试养"),
    SUCCESS("SUCCESS", "领养成功"),
    REJECT("REJECT", "拒绝"),
    CANCEL("CANCEL", "取消"),
    TRIAL_FAILED("TRIAL_FAILED", "试养失败");

    private final String code;

    private final String name;
}
