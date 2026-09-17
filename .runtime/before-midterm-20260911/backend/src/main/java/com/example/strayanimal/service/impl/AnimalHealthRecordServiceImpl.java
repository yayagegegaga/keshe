package com.example.strayanimal.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.strayanimal.dto.AnimalHealthRecordCreateDTO;
import com.example.strayanimal.entity.Animal;
import com.example.strayanimal.entity.AnimalHealthRecord;
import com.example.strayanimal.mapper.AnimalHealthRecordMapper;
import com.example.strayanimal.service.AnimalHealthRecordService;
import com.example.strayanimal.service.AnimalService;
import com.example.strayanimal.vo.AnimalHealthRecordVO;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AnimalHealthRecordServiceImpl extends ServiceImpl<AnimalHealthRecordMapper, AnimalHealthRecord>
        implements AnimalHealthRecordService {

    @Lazy
    private final AnimalService animalService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AnimalHealthRecordVO create(Long animalId, AnimalHealthRecordCreateDTO createDTO) {
        animalService.ensureExists(animalId);
        AnimalHealthRecord record = new AnimalHealthRecord();
        record.setAnimalId(animalId);
        record.setHealthStatus(createDTO.getHealthStatus());
        record.setHospital(createDTO.getHospital());
        record.setTreatmentContent(createDTO.getTreatmentContent());
        record.setRecordTime(createDTO.getRecordTime());
        record.setRemark(createDTO.getRemark());
        save(record);

        Animal animal = animalService.getById(animalId);
        animal.setHealthStatus(createDTO.getHealthStatus());
        animalService.updateById(animal);
        return toVO(record);
    }

    @Override
    public List<AnimalHealthRecordVO> listByAnimalId(Long animalId) {
        return lambdaQuery()
                .eq(AnimalHealthRecord::getAnimalId, animalId)
                .orderByDesc(AnimalHealthRecord::getRecordTime)
                .list()
                .stream()
                .map(this::toVO)
                .toList();
    }

    private AnimalHealthRecordVO toVO(AnimalHealthRecord record) {
        AnimalHealthRecordVO vo = new AnimalHealthRecordVO();
        vo.setId(record.getId());
        vo.setAnimalId(record.getAnimalId());
        vo.setHealthStatus(record.getHealthStatus());
        vo.setHospital(record.getHospital());
        vo.setTreatmentContent(record.getTreatmentContent());
        vo.setRecordTime(record.getRecordTime());
        vo.setRemark(record.getRemark());
        vo.setCreateTime(record.getCreateTime());
        return vo;
    }
}
