package com.example.strayanimal.controller;

import com.example.strayanimal.common.Result;
import com.example.strayanimal.constant.SecurityConstant;
import com.example.strayanimal.dto.LoginDTO;
import com.example.strayanimal.dto.RegisterDTO;
import com.example.strayanimal.dto.RegisterVolunteerDTO;
import com.example.strayanimal.service.AuthService;
import com.example.strayanimal.vo.LoginVO;
import com.example.strayanimal.vo.UserInfoVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "认证接口")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "用户登录")
    @PostMapping("/login")
    public Result<LoginVO> login(@Valid @RequestBody LoginDTO loginDTO) {
        return Result.success(authService.login(loginDTO));
    }

    @Operation(summary = "普通用户注册")
    @PostMapping("/register")
    public Result<UserInfoVO> register(@Valid @RequestBody RegisterDTO registerDTO) {
        return Result.success(authService.register(registerDTO));
    }

    @Operation(summary = "志愿者注册")
    @PostMapping("/register-volunteer")
    public Result<UserInfoVO> registerVolunteer(@Valid @RequestBody RegisterVolunteerDTO registerDTO) {
        return Result.success(authService.registerVolunteer(registerDTO));
    }

    @Operation(summary = "退出登录")
    @PostMapping("/logout")
    public Result<Void> logout(@RequestHeader(value = SecurityConstant.AUTHORIZATION_HEADER, required = false)
                               String authorization) {
        authService.logout(authorization);
        return Result.success();
    }

    @Operation(summary = "当前登录用户")
    @GetMapping("/me")
    public Result<UserInfoVO> me() {
        return Result.success(authService.me());
    }
}
