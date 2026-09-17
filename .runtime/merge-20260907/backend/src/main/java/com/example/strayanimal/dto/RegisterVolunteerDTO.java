package com.example.strayanimal.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterVolunteerDTO {

    @NotBlank(message = "用户名不能为空")
    @Size(min = 4, max = 30, message = "用户名长度需为4到30位")
    private String username;

    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 30, message = "密码长度需为6到30位")
    private String password;

    @Size(max = 50, message = "昵称不能超过50个字符")
    private String nickname;

    @Pattern(regexp = "^$|^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;

    @Email(message = "邮箱格式不正确")
    private String email;

    @NotBlank(message = "学号不能为空")
    private String studentId;

    @NotBlank(message = "真实姓名不能为空")
    private String realName;

    @NotBlank(message = "专业不能为空")
    private String major;
}