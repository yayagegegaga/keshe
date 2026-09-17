package com.example.strayanimal.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Set;

@Getter
@AllArgsConstructor
public enum AdoptionApplicationStatus {

    PENDING("PENDING", "待审核"),
    FIRST_APPROVED("FIRST_APPROVED", "初审通过"),
    INTERVIEWING("INTERVIEWING", "面谈中"),
    TRIAL("TRIAL", "试养中"),
    SUCCESS("SUCCESS", "领养成功"),
    REJECTED("REJECTED", "已拒绝"),
    CANCELED("CANCELED", "已取消"),
    TRIAL_FAILED("TRIAL_FAILED", "试养失败");

    private static final Set<String> ACTIVE_STATUSES = Set.of(
            PENDING.code,
            FIRST_APPROVED.code,
            INTERVIEWING.code,
            TRIAL.code
    );

    private final String code;

    private final String name;

    public static boolean contains(String code) {
        return Arrays.stream(values()).anyMatch(item -> item.code.equals(code));
    }

    public static boolean isActive(String code) {
        return ACTIVE_STATUSES.contains(code);
    }
}
