package com.example.strayanimal.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.strayanimal.entity.SysUser;
import com.example.strayanimal.vo.UserOptionVO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDateTime;
import java.util.List;

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

    /**
     * 查询所有状态启用且拥有 VOLUNTEER 角色的用户简要信息，供管理员分配工单/任务时下拉选择。
     */
    @Select("""
            SELECT u.id, u.username, u.nickname, u.phone
            FROM sys_user u
            INNER JOIN sys_user_role ur ON ur.user_id = u.id
            INNER JOIN sys_role r ON r.id = ur.role_id
            WHERE r.role_code = 'VOLUNTEER'
              AND u.status = 1
            ORDER BY u.id ASC
            """)
    List<UserOptionVO> selectEnabledVolunteers();
}
