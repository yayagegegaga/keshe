package com.example.strayanimal.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.strayanimal.entity.SysUser;
import com.example.strayanimal.vo.UserInfoVO;

public interface SysUserService extends IService<SysUser> {

    SysUser getByUsername(String username);

    UserInfoVO getUserInfo(Long userId);
}
