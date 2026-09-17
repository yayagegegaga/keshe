package com.example.strayanimal.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    SUCCESS(200, "success"),
    BAD_REQUEST(400, "请求参数错误"),
    UNAUTHORIZED(401, "未登录或登录已过期"),
    FORBIDDEN(403, "无权限访问"),
    NOT_FOUND(404, "数据不存在"),
    BUSINESS_ERROR(5001, "业务处理失败"),
    SYSTEM_ERROR(500, "系统异常");

    private final Integer code;

    private final String message;
}
