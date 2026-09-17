package com.example.strayanimal.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@Getter
@AllArgsConstructor
public enum VolunteerTaskType {
    FEEDING("FEEDING", "投喂任务"),
    CLEANING("CLEANING", "清理任务"),
    MEDICAL("MEDICAL", "送医任务"),
    FOLLOW_UP("FOLLOW_UP", "回访任务"),
    TEMP_RESCUE("TEMP_RESCUE", "临时救助任务");

    private final String code;
    private final String name;

    public static boolean contains(String code) {
        return Arrays.stream(values()).anyMatch(item -> item.code.equals(code));
    }
}
