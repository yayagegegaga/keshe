package com.example.strayanimal.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.strayanimal.dto.AnimalImageCreateDTO;
import com.example.strayanimal.entity.Animal;
import com.example.strayanimal.entity.AnimalImage;
import com.example.strayanimal.enums.AnimalImageType;
import com.example.strayanimal.exception.BusinessException;
import com.example.strayanimal.exception.ErrorCode;
import com.example.strayanimal.mapper.AnimalImageMapper;
import com.example.strayanimal.service.AnimalImageService;
import com.example.strayanimal.service.AnimalService;
import com.example.strayanimal.vo.AnimalImageVO;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AnimalImageServiceImpl extends ServiceImpl<AnimalImageMapper, AnimalImage> implements AnimalImageService {

    @Lazy
    private final AnimalService animalService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AnimalImageVO create(Long animalId, AnimalImageCreateDTO createDTO) {
        animalService.ensureExists(animalId);
        if (!AnimalImageType.contains(createDTO.getImageType())) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "图片类型不合法");
        }
        AnimalImage image = new AnimalImage();
        image.setAnimalId(animalId);
        image.setImageUrl(createDTO.getImageUrl());
        image.setImageType(createDTO.getImageType());
        save(image);

        if (AnimalImageType.COVER.getCode().equals(createDTO.getImageType())) {
            Animal animal = animalService.getById(animalId);
            animal.setCoverImage(createDTO.getImageUrl());
            animalService.updateById(animal);
        }
        return toVO(image);
    }

    @Override
    public List<AnimalImageVO> listByAnimalId(Long animalId) {
        return lambdaQuery()
                .eq(AnimalImage::getAnimalId, animalId)
                .orderByDesc(AnimalImage::getCreateTime)
                .list()
                .stream()
                .map(this::toVO)
                .toList();
    }

    private AnimalImageVO toVO(AnimalImage image) {
        AnimalImageVO vo = new AnimalImageVO();
        vo.setId(image.getId());
        vo.setAnimalId(image.getAnimalId());
        vo.setImageUrl(image.getImageUrl());
        vo.setImageType(image.getImageType());
        vo.setCreateTime(image.getCreateTime());
        return vo;
    }
}
