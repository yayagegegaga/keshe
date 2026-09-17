package com.example.strayanimal.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.strayanimal.entity.SysUser;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDateTime;

@Mapper
public interface SysUserMapper extends BaseMapper<SysUser> {

    @Insert("""
            INSERT INTO sys_user_role(user_id, role_id)
            VALUES(#{userId}, #{roleId})
            """)
    int insertUserRole(@Param("userId") Long userId, @Param("roleId") Long roleId);

    @Update("""
            UPDATE sys_user
            SET last_login_time = #{lastLoginTime}
            WHERE id = #{userId}
            """)
    int updateLastLoginTime(@Param("userId") Long userId, @Param("lastLoginTime") LocalDateTime lastLoginTime);
}
