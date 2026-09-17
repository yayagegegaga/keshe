package com.example.strayanimal.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.strayanimal.common.PageResult;
import com.example.strayanimal.dto.RescueClueCreateDTO;
import com.example.strayanimal.dto.RescueClueQueryDTO;
import com.example.strayanimal.dto.RescueClueReviewDTO;
import com.example.strayanimal.entity.RescueClue;
import com.example.strayanimal.vo.RescueClueDetailVO;
import com.example.strayanimal.vo.RescueClueVO;

public interface RescueClueService extends IService<RescueClue> {

    RescueClueVO create(RescueClueCreateDTO createDTO);

    PageResult<RescueClueVO> pageAll(RescueClueQueryDTO queryDTO);

    PageResult<RescueClueVO> pageMine(RescueClueQueryDTO queryDTO);

    RescueClueDetailVO detail(Long id);

    RescueClueVO review(Long id, RescueClueReviewDTO reviewDTO);

    RescueClueVO cancel(Long id);
}
