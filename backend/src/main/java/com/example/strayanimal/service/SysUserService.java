package com.example.strayanimal.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.strayanimal.entity.SysUser;
import com.example.strayanimal.vo.UserInfoVO;
import com.example.strayanimal.vo.UserOptionVO;

import java.util.List;

public interface SysUserService extends IService<SysUser> {

    SysUser getByUsername(String username);

    UserInfoVO getUserInfo(Long userId);

    /** 查询所有启用状态的志愿者简要信息，供管理员下拉选择。 */
    List<UserOptionVO> listVolunteers();
}
