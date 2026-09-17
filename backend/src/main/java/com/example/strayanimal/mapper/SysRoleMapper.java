package com.example.strayanimal.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.strayanimal.entity.SysRole;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
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

    /**
     * 根据角色编码集合查询拥有这些角色的用户ID列表（去重）。
     * 用于首页统计缓存失效时定位所有管理员。
     */
    @Select("""
            <script>
            SELECT DISTINCT ur.user_id
            FROM sys_user_role ur
            INNER JOIN sys_role r ON r.id = ur.role_id
            WHERE r.role_code IN
            <foreach collection='roleCodes' item='code' open='(' separator=',' close=')'>
                #{code}
            </foreach>
            </script>
            """)
    List<Long> selectUserIdsByRoleCodes(@Param("roleCodes") List<String> roleCodes);
}
