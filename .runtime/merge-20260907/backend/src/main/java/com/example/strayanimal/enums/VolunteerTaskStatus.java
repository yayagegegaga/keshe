package com.example.strayanimal.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@Getter
@AllArgsConstructor
public enum VolunteerTaskStatus {
    WAIT_CLAIM("WAIT_CLAIM", "待领取"),
    CLAIMED("CLAIMED", "已领取"),
    FINISHED("FINISHED", "已完成"),
    REVIEWED("REVIEWED", "已审核"),
    CANCELED("CANCELED", "已取消");

    private final String code;
    private final String name;

    public static boolean contains(String code) {
        return Arrays.stream(values()).anyMatch(item -> item.code.equals(code));
    }
}
