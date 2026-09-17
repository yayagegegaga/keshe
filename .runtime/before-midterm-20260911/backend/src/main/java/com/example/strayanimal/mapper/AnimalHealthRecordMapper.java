package com.example.strayanimal.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.strayanimal.entity.AnimalHealthRecord;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AnimalHealthRecordMapper extends BaseMapper<AnimalHealthRecord> {
}
