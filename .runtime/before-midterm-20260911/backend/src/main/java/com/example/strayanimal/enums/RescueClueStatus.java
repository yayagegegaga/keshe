package com.example.strayanimal.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@Getter
@AllArgsConstructor
public enum RescueClueStatus {

    PENDING("PENDING", "待审核"),
    CONVERTED("CONVERTED", "已转工单"),
    INVALID("INVALID", "无效线索"),
    CANCELED("CANCELED", "已取消");

    private final String code;

    private final String name;

    public static boolean contains(String code) {
        return Arrays.stream(values()).anyMatch(item -> item.code.equals(code));
    }
}
