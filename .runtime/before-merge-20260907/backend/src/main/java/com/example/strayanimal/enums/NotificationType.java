package com.example.strayanimal.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@Getter
@AllArgsConstructor
public enum NotificationType {
    ADOPTION("ADOPTION", "领养通知"),
    TASK("TASK", "任务通知"),
    RESCUE_ORDER("RESCUE_ORDER", "工单通知"),
    SYSTEM("SYSTEM", "系统通知");

    private final String code;
    private final String name;

    public static boolean contains(String code) {
        return Arrays.stream(values()).anyMatch(item -> item.code.equals(code));
    }
}
