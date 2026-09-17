package com.example.strayanimal.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum RescueOrderOperationType {

    CREATE("CREATE", "创建工单"),
    ASSIGN("ASSIGN", "分配工单"),
    START("START", "开始处理"),
    FINISH("FINISH", "提交完成"),
    CLOSE("CLOSE", "关闭工单"),
    CANCEL("CANCEL", "取消工单");

    private final String code;

    private final String name;
}
