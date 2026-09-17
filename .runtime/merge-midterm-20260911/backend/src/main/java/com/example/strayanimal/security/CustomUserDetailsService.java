package com.example.strayanimal.security;

import com.example.strayanimal.entity.SysRole;
import com.example.strayanimal.entity.SysUser;
import com.example.strayanimal.mapper.SysRoleMapper;
import com.example.strayanimal.service.SysUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final SysUserService sysUserService;

    private final SysRoleMapper sysRoleMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        SysUser user = sysUserService.getByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("用户不存在");
        }
        List<String> roles = sysRoleMapper.selectRolesByUserId(user.getId()).stream()
                .map(SysRole::getRoleCode)
                .toList();
        List<SimpleGrantedAuthority> authorities = roles.stream()
                .map(SimpleGrantedAuthority::new)
                .toList();
        return new SecurityUser(user, roles, authorities);
    }
}
