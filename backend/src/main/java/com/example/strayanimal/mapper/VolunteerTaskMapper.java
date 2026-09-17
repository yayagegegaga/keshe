package com.example.strayanimal.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.strayanimal.entity.VolunteerTask;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface VolunteerTaskMapper extends BaseMapper<VolunteerTask> {

    @Update("""
            UPDATE volunteer_task
            SET status = 'CLAIMED',
                volunteer_id = #{volunteerId},
                start_time = NOW(),
                update_time = NOW(),
                version = version + 1
            WHERE id = #{taskId}
              AND status = 'WAIT_CLAIM'
            """)
    int claimIfWaitClaim(@Param("taskId") Long taskId, @Param("volunteerId") Long volunteerId);
}
