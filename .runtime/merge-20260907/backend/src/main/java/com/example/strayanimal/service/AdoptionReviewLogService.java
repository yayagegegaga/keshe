package com.example.strayanimal.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.strayanimal.entity.AdoptionReviewLog;
import com.example.strayanimal.vo.AdoptionReviewLogVO;

import java.util.List;

public interface AdoptionReviewLogService extends IService<AdoptionReviewLog> {

    void record(Long applicationId, Long reviewerId, String oldStatus, String newStatus, String operationType, String remark);

    List<AdoptionReviewLogVO> listByApplicationId(Long applicationId);
}
