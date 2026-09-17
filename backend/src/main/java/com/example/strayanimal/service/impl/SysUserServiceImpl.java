package com.example.strayanimal.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.strayanimal.entity.SysRole;
import com.example.strayanimal.entity.SysUser;
import com.example.strayanimal.exception.BusinessException;
import com.example.strayanimal.exception.ErrorCode;
import com.example.strayanimal.mapper.SysRoleMapper;
import com.example.strayanimal.mapper.SysUserMapper;
import com.example.strayanimal.service.SysUserService;
import com.example.strayanimal.vo.UserInfoVO;
import com.example.strayanimal.vo.UserOptionVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {

    private final SysRoleMapper sysRoleMapper;

    @Override
    public SysUser getByUsername(String username) {
        return lambdaQuery()
                .eq(SysUser::getUsername, username)
                .one();
    }

    @Override
    public UserInfoVO getUserInfo(Long userId) {
        SysUser user = getById(userId);
        if (user == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "用户不存在");
        }
        List<String> roles = sysRoleMapper.selectRolesByUserId(userId).stream()
                .map(SysRole::getRoleCode)
                .toList();
        return new UserInfoVO(
                user.getId(),
                user.getUsername(),
                user.getNickname(),
                user.getPhone(),
                user.getEmail(),
                user.getAvatar(),
                user.getStatus(),
                roles
        );
    }

    @Override
    public List<UserOptionVO> listVolunteers() {
        return baseMapper.selectEnabledVolunteers();
    }
}
