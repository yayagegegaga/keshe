package com.example.strayanimal.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@Getter
@AllArgsConstructor
public enum AnimalImageType {

    COVER("COVER", "封面"),
    DETAIL("DETAIL", "详情"),
    RESCUE("RESCUE", "救助现场"),
    HEALTH("HEALTH", "健康记录");

    private final String code;

    private final String name;

    public static boolean contains(String code) {
        return Arrays.stream(values()).anyMatch(item -> item.code.equals(code));
    }
}
