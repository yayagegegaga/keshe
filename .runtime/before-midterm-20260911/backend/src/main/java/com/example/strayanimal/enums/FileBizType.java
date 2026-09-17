package com.example.strayanimal.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@Getter
@AllArgsConstructor
public enum FileBizType {
    ANIMAL_IMAGE("ANIMAL_IMAGE", "动物照片"),
    RESCUE_CLUE_IMAGE("RESCUE_CLUE_IMAGE", "救助线索图片"),
    RESCUE_ORDER_IMAGE("RESCUE_ORDER_IMAGE", "工单处理图片"),
    TASK_IMAGE("TASK_IMAGE", "任务完成图片"),
    ADOPTION_IMAGE("ADOPTION_IMAGE", "领养材料图片"),
    FOLLOW_UP_IMAGE("FOLLOW_UP_IMAGE", "回访图片"),
    OTHER("OTHER", "其他");

    private final String code;
    private final String name;

    public static boolean contains(String code) {
        return Arrays.stream(values()).anyMatch(item -> item.code.equals(code));
    }
}
