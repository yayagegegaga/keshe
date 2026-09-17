package com.example.strayanimal.service;

import com.example.strayanimal.dto.LoginDTO;
import com.example.strayanimal.dto.RegisterDTO;
import com.example.strayanimal.vo.LoginVO;
import com.example.strayanimal.vo.UserInfoVO;

public interface AuthService {

    LoginVO login(LoginDTO loginDTO);

    UserInfoVO register(RegisterDTO registerDTO);

    void logout(String authorizationHeader);

    UserInfoVO me();
}
