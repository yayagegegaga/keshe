package com.example.strayanimal.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@Getter
@AllArgsConstructor
public enum AnimalGender {

    MALE("MALE", "公"),
    FEMALE("FEMALE", "母"),
    UNKNOWN("UNKNOWN", "未知");

    private final String code;

    private final String name;

    public static boolean contains(String code) {
        return Arrays.stream(values()).anyMatch(item -> item.code.equals(code));
    }
}
