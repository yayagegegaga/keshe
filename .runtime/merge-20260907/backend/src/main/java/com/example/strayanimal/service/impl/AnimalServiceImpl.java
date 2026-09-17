package com.example.strayanimal.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.strayanimal.common.PageResult;
import com.example.strayanimal.dto.AnimalCreateDTO;
import com.example.strayanimal.dto.AnimalQueryDTO;
import com.example.strayanimal.dto.AnimalStatusUpdateDTO;
import com.example.strayanimal.dto.AnimalUpdateDTO;
import com.example.strayanimal.entity.Animal;
import com.example.strayanimal.entity.AnimalHealthRecord;
import com.example.strayanimal.entity.AnimalImage;
import com.example.strayanimal.enums.AnimalAgeStage;
import com.example.strayanimal.enums.AnimalGender;
import com.example.strayanimal.enums.AnimalStatus;
import com.example.strayanimal.enums.AnimalType;
import com.example.strayanimal.exception.BusinessException;
import com.example.strayanimal.exception.ErrorCode;
import com.example.strayanimal.mapper.AnimalHealthRecordMapper;
import com.example.strayanimal.mapper.AnimalImageMapper;
import com.example.strayanimal.mapper.AnimalMapper;
import com.example.strayanimal.service.AnimalService;
import com.example.strayanimal.vo.AnimalDetailVO;
import com.example.strayanimal.vo.AnimalHealthRecordVO;
import com.example.strayanimal.vo.AnimalImageVO;
import com.example.strayanimal.vo.AnimalVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AnimalServiceImpl extends ServiceImpl<AnimalMapper, Animal> implements AnimalService {

    private final AnimalImageMapper animalImageMapper;

    private final AnimalHealthRecordMapper animalHealthRecordMapper;

    @Override
    public PageResult<AnimalVO> pageAnimals(AnimalQueryDTO queryDTO) {
        validateOptionalEnums(queryDTO.getType(), queryDTO.getGender(), queryDTO.getStatus());
        long pageNum = queryDTO.getPageNum() == null || queryDTO.getPageNum() < 1 ? 1 : queryDTO.getPageNum();
        long pageSize = queryDTO.getPageSize() == null || queryDTO.getPageSize() < 1 ? 10 : Math.min(queryDTO.getPageSize(), 100);

        LambdaQueryWrapper<Animal> wrapper = new LambdaQueryWrapper<Animal>()
                .and(StringUtils.isNotBlank(queryDTO.getKeyword()), w -> w
                        .like(Animal::getName, queryDTO.getKeyword())
                        .or()
                        .like(Animal::getAnimalNo, queryDTO.getKeyword()))
                .eq(StringUtils.isNotBlank(queryDTO.getType()), Animal::getType, queryDTO.getType())
                .eq(StringUtils.isNotBlank(queryDTO.getStatus()), Animal::getStatus, queryDTO.getStatus())
                .eq(StringUtils.isNotBlank(queryDTO.getGender()), Animal::getGender, queryDTO.getGender())
                .like(StringUtils.isNotBlank(queryDTO.getArea()), Animal::getArea, queryDTO.getArea())
                .orderByDesc(Animal::getCreateTime);

        Page<Animal> page = page(new Page<>(pageNum, pageSize), wrapper);
        return PageResult.of(page.getRecords().stream().map(this::toVO).toList(),
                page.getTotal(), page.getCurrent(), page.getSize());
    }

    @Override
    public AnimalDetailVO detail(Long id) {
        Animal animal = getExistingAnimal(id);
        AnimalDetailVO vo = toDetailVO(animal);
        vo.setImages(animalImageMapper.selectList(new LambdaQueryWrapper<AnimalImage>()
                        .eq(AnimalImage::getAnimalId, id)
                        .orderByDesc(AnimalImage::getCreateTime))
                .stream()
                .map(this::toImageVO)
                .toList());
        vo.setHealthRecords(animalHealthRecordMapper.selectList(new LambdaQueryWrapper<AnimalHealthRecord>()
                        .eq(AnimalHealthRecord::getAnimalId, id)
                        .orderByDesc(AnimalHealthRecord::getRecordTime))
                .stream()
                .map(this::toHealthRecordVO)
                .toList());
        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AnimalVO create(AnimalCreateDTO createDTO) {
        validateRequiredEnums(createDTO.getType(), createDTO.getGender(), createDTO.getAgeStage(), createDTO.getStatus());
        if (lambdaQuery().eq(Animal::getAnimalNo, createDTO.getAnimalNo()).exists()) {
            throw new BusinessException(ErrorCode.BUSINESS_ERROR, "动物编号已存在");
        }
        Animal animal = new Animal();
        fillAnimal(animal, createDTO);
        save(animal);
        return toVO(animal);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AnimalVO update(Long id, AnimalUpdateDTO updateDTO) {
        Animal animal = getExistingAnimal(id);
        validateRequiredEnums(updateDTO.getType(), updateDTO.getGender(), updateDTO.getAgeStage(), updateDTO.getStatus());
        fillAnimal(animal, updateDTO);
        updateById(animal);
        return toVO(animal);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AnimalVO updateStatus(Long id, AnimalStatusUpdateDTO statusUpdateDTO) {
        Animal animal = getExistingAnimal(id);
        if (!AnimalStatus.contains(statusUpdateDTO.getStatus())) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "动物状态不合法");
        }
        if (AnimalStatus.ADOPTED.getCode().equals(animal.getStatus())
                && AnimalStatus.ADOPTABLE.getCode().equals(statusUpdateDTO.getStatus())) {
            throw new BusinessException(ErrorCode.BUSINESS_ERROR, "已领养动物不能直接改回可领养");
        }
        animal.setStatus(statusUpdateDTO.getStatus());
        updateById(animal);
        return toVO(animal);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteAnimal(Long id) {
        ensureExists(id);
        animalImageMapper.delete(new LambdaQueryWrapper<AnimalImage>().eq(AnimalImage::getAnimalId, id));
        animalHealthRecordMapper.delete(new LambdaQueryWrapper<AnimalHealthRecord>().eq(AnimalHealthRecord::getAnimalId, id));
        removeById(id);
    }

    @Override
    public void ensureExists(Long id) {
        if (getById(id) == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "动物档案不存在");
        }
    }

    private Animal getExistingAnimal(Long id) {
        Animal animal = getById(id);
        if (animal == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "动物档案不存在");
        }
        return animal;
    }

    private void validateRequiredEnums(String type, String gender, String ageStage, String status) {
        if (!AnimalType.contains(type)) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "动物类型不合法");
        }
        if (StringUtils.isNotBlank(gender) && !AnimalGender.contains(gender)) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "动物性别不合法");
        }
        if (StringUtils.isNotBlank(ageStage) && !AnimalAgeStage.contains(ageStage)) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "年龄阶段不合法");
        }
        if (!AnimalStatus.contains(status)) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "动物状态不合法");
        }
    }

    private void validateOptionalEnums(String type, String gender, String status) {
        if (StringUtils.isNotBlank(type) && !AnimalType.contains(type)) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "动物类型不合法");
        }
        if (StringUtils.isNotBlank(gender) && !AnimalGender.contains(gender)) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "动物性别不合法");
        }
        if (StringUtils.isNotBlank(status) && !AnimalStatus.contains(status)) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "动物状态不合法");
        }
    }

    private void fillAnimal(Animal animal, AnimalCreateDTO dto) {
        animal.setAnimalNo(dto.getAnimalNo());
        animal.setName(dto.getName());
        animal.setType(dto.getType());
        animal.setGender(dto.getGender());
        animal.setAgeStage(dto.getAgeStage());
        animal.setColor(dto.getColor());
        animal.setHealthStatus(dto.getHealthStatus());
        animal.setSterilizationStatus(dto.getSterilizationStatus());
        animal.setVaccineStatus(dto.getVaccineStatus());
        animal.setArea(dto.getArea());
        animal.setStatus(dto.getStatus());
        animal.setDescription(dto.getDescription());
        animal.setCoverImage(dto.getCoverImage());
    }

    private void fillAnimal(Animal animal, AnimalUpdateDTO dto) {
        animal.setName(dto.getName());
        animal.setType(dto.getType());
        animal.setGender(dto.getGender());
        animal.setAgeStage(dto.getAgeStage());
        animal.setColor(dto.getColor());
        animal.setHealthStatus(dto.getHealthStatus());
        animal.setSterilizationStatus(dto.getSterilizationStatus());
        animal.setVaccineStatus(dto.getVaccineStatus());
        animal.setArea(dto.getArea());
        animal.setStatus(dto.getStatus());
        animal.setDescription(dto.getDescription());
        animal.setCoverImage(dto.getCoverImage());
    }

    private AnimalVO toVO(Animal animal) {
        AnimalVO vo = new AnimalVO();
        copyBase(animal, vo);
        return vo;
    }

    private AnimalDetailVO toDetailVO(Animal animal) {
        AnimalDetailVO vo = new AnimalDetailVO();
        copyBase(animal, vo);
        return vo;
    }

    private void copyBase(Animal animal, AnimalVO vo) {
        vo.setId(animal.getId());
        vo.setAnimalNo(animal.getAnimalNo());
        vo.setName(animal.getName());
        vo.setType(animal.getType());
        vo.setGender(animal.getGender());
        vo.setAgeStage(animal.getAgeStage());
        vo.setColor(animal.getColor());
        vo.setHealthStatus(animal.getHealthStatus());
        vo.setSterilizationStatus(animal.getSterilizationStatus());
        vo.setVaccineStatus(animal.getVaccineStatus());
        vo.setArea(animal.getArea());
        vo.setStatus(animal.getStatus());
        vo.setDescription(animal.getDescription());
        vo.setCoverImage(animal.getCoverImage());
        vo.setCreateTime(animal.getCreateTime());
        vo.setUpdateTime(animal.getUpdateTime());
    }

    private AnimalImageVO toImageVO(AnimalImage image) {
        AnimalImageVO vo = new AnimalImageVO();
        vo.setId(image.getId());
        vo.setAnimalId(image.getAnimalId());
        vo.setImageUrl(image.getImageUrl());
        vo.setImageType(image.getImageType());
        vo.setCreateTime(image.getCreateTime());
        return vo;
    }

    private AnimalHealthRecordVO toHealthRecordVO(AnimalHealthRecord record) {
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
