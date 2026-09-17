package com.example.strayanimal.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@Getter
@AllArgsConstructor
public enum AnimalAgeStage {

    BABY("BABY", "幼年"),
    YOUNG("YOUNG", "青年"),
    ADULT("ADULT", "成年"),
    OLD("OLD", "老年"),
    UNKNOWN("UNKNOWN", "未知");

    private final String code;

    private final String name;

    public static boolean contains(String code) {
        return Arrays.stream(values()).anyMatch(item -> item.code.equals(code));
    }
}
