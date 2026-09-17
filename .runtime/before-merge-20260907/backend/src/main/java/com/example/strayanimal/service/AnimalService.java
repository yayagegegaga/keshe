package com.example.strayanimal.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.strayanimal.common.PageResult;
import com.example.strayanimal.dto.AnimalCreateDTO;
import com.example.strayanimal.dto.AnimalQueryDTO;
import com.example.strayanimal.dto.AnimalStatusUpdateDTO;
import com.example.strayanimal.dto.AnimalUpdateDTO;
import com.example.strayanimal.entity.Animal;
import com.example.strayanimal.vo.AnimalDetailVO;
import com.example.strayanimal.vo.AnimalVO;

public interface AnimalService extends IService<Animal> {

    PageResult<AnimalVO> pageAnimals(AnimalQueryDTO queryDTO);

    AnimalDetailVO detail(Long id);

    AnimalVO create(AnimalCreateDTO createDTO);

    AnimalVO update(Long id, AnimalUpdateDTO updateDTO);

    AnimalVO updateStatus(Long id, AnimalStatusUpdateDTO statusUpdateDTO);

    void deleteAnimal(Long id);

    void ensureExists(Long id);
}
