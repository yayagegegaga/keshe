package com.example.strayanimal.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.strayanimal.dto.AnimalImageCreateDTO;
import com.example.strayanimal.entity.AnimalImage;
import com.example.strayanimal.vo.AnimalImageVO;

import java.util.List;

public interface AnimalImageService extends IService<AnimalImage> {

    AnimalImageVO create(Long animalId, AnimalImageCreateDTO createDTO);

    List<AnimalImageVO> listByAnimalId(Long animalId);
}
