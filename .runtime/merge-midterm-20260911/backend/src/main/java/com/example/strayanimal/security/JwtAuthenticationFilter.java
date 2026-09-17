package com.example.strayanimal.security;

import com.example.strayanimal.constant.RedisKeyConstant;
import com.example.strayanimal.constant.SecurityConstant;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    private final StringRedisTemplate stringRedisTemplate;

    private final CustomUserDetailsService customUserDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String authorization = request.getHeader(SecurityConstant.AUTHORIZATION_HEADER);
        String token = resolveToken(authorization);
        if (token == null) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            Long userId = jwtUtil.getUserId(token);
            String redisKey = RedisKeyConstant.loginTokenKey(userId, token);
            if (Boolean.TRUE.equals(stringRedisTemplate.hasKey(redisKey))
                    && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = customUserDetailsService.loadUserByUsername(
                        jwtUtil.parseClaims(token).get("username", String.class));
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (JwtException | IllegalArgumentException ignored) {
            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);
    }

    public static String resolveToken(String authorization) {
        if (!StringUtils.hasText(authorization) || !authorization.startsWith(SecurityConstant.TOKEN_PREFIX)) {
            return null;
        }
        return authorization.substring(SecurityConstant.TOKEN_PREFIX.length());
    }
}
