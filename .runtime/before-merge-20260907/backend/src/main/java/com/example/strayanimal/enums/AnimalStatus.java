package com.example.strayanimal.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@Getter
@AllArgsConstructor
public enum AnimalStatus {

    WAIT_RESCUE("WAIT_RESCUE", "待救助"),
    OBSERVING("OBSERVING", "观察中"),
    TREATING("TREATING", "治疗中"),
    ADOPTABLE("ADOPTABLE", "可领养"),
    APPLYING("APPLYING", "申请中"),
    TRIAL("TRIAL", "试养中"),
    ADOPTED("ADOPTED", "已领养"),
    NOT_ADOPTABLE("NOT_ADOPTABLE", "不可领养"),
    LOST("LOST", "已失踪");

    private final String code;

    private final String name;

    public static boolean contains(String code) {
        return Arrays.stream(values()).anyMatch(item -> item.code.equals(code));
    }
}
