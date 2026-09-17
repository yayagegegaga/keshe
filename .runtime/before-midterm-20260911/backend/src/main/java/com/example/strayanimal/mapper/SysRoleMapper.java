package com.example.strayanimal.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.strayanimal.entity.SysRole;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SysRoleMapper extends BaseMapper<SysRole> {

    @Select("""
            SELECT r.id, r.role_code, r.role_name, r.description, r.create_time, r.update_time
            FROM sys_role r
            INNER JOIN sys_user_role ur ON ur.role_id = r.id
            WHERE ur.user_id = #{userId}
            """)
    List<SysRole> selectRolesByUserId(Long userId);
}
