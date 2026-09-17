package com.example.strayanimal.controller;

import com.example.strayanimal.common.Result;
import com.example.strayanimal.service.SysUserService;
import com.example.strayanimal.vo.UserInfoVO;
import com.example.strayanimal.vo.UserOptionVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "用户接口")
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final SysUserService sysUserService;

    @Operation(summary = "根据ID查询用户信息")
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN','ADMIN')")
    public Result<UserInfoVO> getById(@PathVariable Long id) {
        return Result.success(sysUserService.getUserInfo(id));
    }

    @Operation(summary = "查询所有启用状态的志愿者（供管理员分配工单/任务下拉选择）")
    @GetMapping("/volunteers")
    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN','ADMIN')")
    public Result<List<UserOptionVO>> listVolunteers() {
        return Result.success(sysUserService.listVolunteers());
    }
}
