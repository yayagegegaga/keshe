package com.example.strayanimal.constant;

public final class RedisKeyConstant {

    private RedisKeyConstant() {
    }

    public static final String LOGIN_TOKEN_PREFIX = "login:token:";

    public static String loginTokenKey(Long userId, String token) {
        return LOGIN_TOKEN_PREFIX + userId + ":" + token;
    }
}
