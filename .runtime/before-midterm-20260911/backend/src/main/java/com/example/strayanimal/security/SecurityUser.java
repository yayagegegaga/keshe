package com.example.strayanimal.security;

import com.example.strayanimal.entity.SysUser;
import com.example.strayanimal.enums.UserStatus;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Getter
public class SecurityUser implements UserDetails {

    private final Long userId;

    private final String username;

    private final String password;

    private final Integer status;

    private final List<String> roles;

    private final Collection<? extends GrantedAuthority> authorities;

    public SecurityUser(SysUser user, List<String> roles, Collection<? extends GrantedAuthority> authorities) {
        this.userId = user.getId();
        this.username = user.getUsername();
        this.password = user.getPassword();
        this.status = user.getStatus();
        this.roles = roles;
        this.authorities = authorities;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return UserStatus.ENABLED.getCode().equals(status);
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return UserStatus.ENABLED.getCode().equals(status);
    }
}
