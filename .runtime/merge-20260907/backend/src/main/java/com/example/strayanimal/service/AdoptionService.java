package com.example.strayanimal.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.strayanimal.common.PageResult;
import com.example.strayanimal.dto.AdoptionApplyDTO;
import com.example.strayanimal.dto.AdoptionCancelDTO;
import com.example.strayanimal.dto.AdoptionQueryDTO;
import com.example.strayanimal.dto.AdoptionReviewDTO;
import com.example.strayanimal.entity.AdoptionApplication;
import com.example.strayanimal.vo.AdoptionApplicationDetailVO;
import com.example.strayanimal.vo.AdoptionApplicationVO;
import com.example.strayanimal.vo.AdoptionReviewLogVO;

import java.util.List;

public interface AdoptionService extends IService<AdoptionApplication> {

    AdoptionApplicationVO apply(AdoptionApplyDTO applyDTO);

    PageResult<AdoptionApplicationVO> pageApplications(AdoptionQueryDTO queryDTO);

    PageResult<AdoptionApplicationVO> pageMine(AdoptionQueryDTO queryDTO);

    AdoptionApplicationDetailVO detail(Long id);

    AdoptionApplicationVO firstApprove(Long id, AdoptionReviewDTO reviewDTO);

    AdoptionApplicationVO interview(Long id, AdoptionReviewDTO reviewDTO);

    AdoptionApplicationVO trial(Long id, AdoptionReviewDTO reviewDTO);

    AdoptionApplicationVO success(Long id, AdoptionReviewDTO reviewDTO);

    AdoptionApplicationVO reject(Long id, AdoptionReviewDTO reviewDTO);

    AdoptionApplicationVO trialFailed(Long id, AdoptionReviewDTO reviewDTO);

    AdoptionApplicationVO cancel(Long id, AdoptionCancelDTO cancelDTO);

    List<AdoptionReviewLogVO> logs(Long id);
}
