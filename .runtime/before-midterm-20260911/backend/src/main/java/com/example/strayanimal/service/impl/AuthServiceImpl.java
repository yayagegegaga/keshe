package com.example.strayanimal.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.example.strayanimal.constant.RedisKeyConstant;
import com.example.strayanimal.dto.LoginDTO;
import com.example.strayanimal.dto.RegisterDTO;
import com.example.strayanimal.dto.RegisterVolunteerDTO;
import com.example.strayanimal.entity.SysRole;
import com.example.strayanimal.entity.SysUser;
import com.example.strayanimal.enums.RoleCode;
import com.example.strayanimal.enums.UserStatus;
import com.example.strayanimal.exception.BusinessException;
import com.example.strayanimal.exception.ErrorCode;
import com.example.strayanimal.mapper.SysRoleMapper;
import com.example.strayanimal.mapper.SysUserMapper;
import com.example.strayanimal.security.JwtAuthenticationFilter;
import com.example.strayanimal.security.JwtUtil;
import com.example.strayanimal.security.UserContext;
import com.example.strayanimal.service.AuthService;
import com.example.strayanimal.service.SysUserService;
import com.example.strayanimal.vo.LoginVO;
import com.example.strayanimal.vo.UserInfoVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;

    private final PasswordEncoder passwordEncoder;

    private final SysUserService sysUserService;

    private final SysUserMapper sysUserMapper;

    private final SysRoleMapper sysRoleMapper;

    private final JwtUtil jwtUtil;

    private final StringRedisTemplate stringRedisTemplate;

    @Override
    public LoginVO login(LoginDTO loginDTO) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    loginDTO.getUsername(), loginDTO.getPassword()));
        } catch (BadCredentialsException ex) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "用户名或密码错误");
        } catch (InternalAuthenticationServiceException ex) {
            log.error("认证服务内部异常，username={}", loginDTO.getUsername(), ex);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "认证服务异常，请检查数据库初始化或连接配置");
        } catch (AuthenticationException ex) {
            log.warn("认证失败，username={}", loginDTO.getUsername(), ex);
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "登录失败");
        }

        SysUser user = sysUserService.getByUsername(loginDTO.getUsername());
        String token = jwtUtil.generateToken(user.getId(), user.getUsername());
        try {
            stringRedisTemplate.opsForValue().set(
                    RedisKeyConstant.loginTokenKey(user.getId(), token),
                    user.getUsername(),
                    jwtUtil.getExpireHours(),
                    TimeUnit.HOURS
            );
        } catch (Exception ex) {
            log.error("登录 Token 写入 Redis 失败，userId={}", user.getId(), ex);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "Redis 未启动或连接失败，请启动 Redis 后重试登录");
        }
        sysUserMapper.updateLastLoginTime(user.getId(), LocalDateTime.now());

        UserInfoVO userInfo = sysUserService.getUserInfo(user.getId());
        return new LoginVO(token, userInfo, userInfo.getRoles());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserInfoVO register(RegisterDTO registerDTO) {
        if (sysUserService.getByUsername(registerDTO.getUsername()) != null) {
            throw new BusinessException(ErrorCode.BUSINESS_ERROR, "用户名已存在");
        }

        SysRole userRole = sysRoleMapper.selectOne(Wrappers.<SysRole>lambdaQuery()
                .eq(SysRole::getRoleCode, RoleCode.USER.getCode()));
        if (userRole == null) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "默认角色不存在");
        }

        SysUser user = new SysUser();
        user.setUsername(registerDTO.getUsername());
        user.setPassword(passwordEncoder.encode(registerDTO.getPassword()));
        user.setNickname(registerDTO.getNickname());
        user.setPhone(registerDTO.getPhone());
        user.setEmail(registerDTO.getEmail());
        user.setStatus(UserStatus.ENABLED.getCode());
        sysUserService.save(user);
        sysUserMapper.insertUserRole(user.getId(), userRole.getId());
        return sysUserService.getUserInfo(user.getId());
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserInfoVO registerVolunteer(RegisterVolunteerDTO registerDTO) {
        if (sysUserService.getByUsername(registerDTO.getUsername()) != null) {
            throw new BusinessException(ErrorCode.BUSINESS_ERROR, "用户名已存在");
        }

        SysRole volunteerRole = sysRoleMapper.selectOne(Wrappers.<SysRole>lambdaQuery()
                .eq(SysRole::getRoleCode, RoleCode.VOLUNTEER.getCode()));
        if (volunteerRole == null) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "志愿者角色不存在，请联系管理员");
        }

        SysUser user = new SysUser();
        user.setUsername(registerDTO.getUsername());
        user.setPassword(passwordEncoder.encode(registerDTO.getPassword()));
        user.setNickname(registerDTO.getNickname());
        user.setPhone(registerDTO.getPhone());
        user.setEmail(registerDTO.getEmail());
        user.setStudentId(registerDTO.getStudentId());   // ← 学号
        user.setRealName(registerDTO.getRealName());     // ← 真实姓名
        user.setMajor(registerDTO.getMajor());           // ← 专业
        user.setStatus(UserStatus.ENABLED.getCode());
        sysUserService.save(user);
        sysUserMapper.insertUserRole(user.getId(), volunteerRole.getId());
        return sysUserService.getUserInfo(user.getId());
    }


    @Override
    public void logout(String authorizationHeader) {
        String token = JwtAuthenticationFilter.resolveToken(authorizationHeader);
        if (token == null) {
            return;
        }
        Long userId = jwtUtil.getUserId(token);
        stringRedisTemplate.delete(RedisKeyConstant.loginTokenKey(userId, token));
    }

    @Override
    public UserInfoVO me() {
        return sysUserService.getUserInfo(UserContext.currentUserId());
    }
}
