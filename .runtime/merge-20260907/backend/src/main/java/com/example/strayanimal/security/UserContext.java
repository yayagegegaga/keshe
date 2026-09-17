package com.example.strayanimal.security;

import com.example.strayanimal.exception.BusinessException;
import com.example.strayanimal.exception.ErrorCode;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public final class UserContext {

    private UserContext() {
    }

    public static SecurityUser currentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof SecurityUser securityUser)) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED);
        }
        return securityUser;
    }

    public static Long currentUserId() {
        return currentUser().getUserId();
    }
}
