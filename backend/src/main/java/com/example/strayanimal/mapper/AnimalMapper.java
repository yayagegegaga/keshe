package com.example.strayanimal.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.strayanimal.entity.Animal;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AnimalMapper extends BaseMapper<Animal> {
}
