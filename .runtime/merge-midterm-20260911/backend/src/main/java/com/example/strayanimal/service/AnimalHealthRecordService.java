package com.example.strayanimal.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.strayanimal.dto.AnimalHealthRecordCreateDTO;
import com.example.strayanimal.entity.AnimalHealthRecord;
import com.example.strayanimal.vo.AnimalHealthRecordVO;

import java.util.List;

public interface AnimalHealthRecordService extends IService<AnimalHealthRecord> {

    AnimalHealthRecordVO create(Long animalId, AnimalHealthRecordCreateDTO createDTO);

    List<AnimalHealthRecordVO> listByAnimalId(Long animalId);
}
