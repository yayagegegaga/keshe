package com.example.strayanimal.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum RoleCode {

    SUPER_ADMIN("SUPER_ADMIN", "系统管理员"),
    ADMIN("ADMIN", "救助站管理员"),
    USER("USER", "普通用户"),
    VOLUNTEER("VOLUNTEER", "志愿者");

    private final String code;

    private final String name;
}
