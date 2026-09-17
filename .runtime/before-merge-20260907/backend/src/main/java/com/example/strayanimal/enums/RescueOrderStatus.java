package com.example.strayanimal.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@Getter
@AllArgsConstructor
public enum RescueOrderStatus {

    WAIT_ASSIGN("WAIT_ASSIGN", "待分配"),
    ASSIGNED("ASSIGNED", "已分配"),
    PROCESSING("PROCESSING", "处理中"),
    WAIT_CONFIRM("WAIT_CONFIRM", "待确认"),
    CLOSED("CLOSED", "已关闭"),
    CANCELED("CANCELED", "已取消");

    private final String code;

    private final String name;

    public static boolean contains(String code) {
        return Arrays.stream(values()).anyMatch(item -> item.code.equals(code));
    }
}
