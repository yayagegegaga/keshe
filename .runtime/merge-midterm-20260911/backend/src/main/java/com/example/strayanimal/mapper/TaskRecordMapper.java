package com.example.strayanimal.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.strayanimal.entity.TaskRecord;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface TaskRecordMapper extends BaseMapper<TaskRecord> {
}
