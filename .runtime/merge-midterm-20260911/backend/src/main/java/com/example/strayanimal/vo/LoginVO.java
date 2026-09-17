package com.example.strayanimal.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoginVO {

    private String token;

    private UserInfoVO userInfo;

    private List<String> roles;
}
