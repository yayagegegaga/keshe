package com.example.strayanimal.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.strayanimal.entity.AdoptionReviewLog;
import com.example.strayanimal.mapper.AdoptionReviewLogMapper;
import com.example.strayanimal.service.AdoptionReviewLogService;
import com.example.strayanimal.service.SysUserService;
import com.example.strayanimal.vo.AdoptionReviewLogVO;
import com.example.strayanimal.vo.UserInfoVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdoptionReviewLogServiceImpl extends ServiceImpl<AdoptionReviewLogMapper, AdoptionReviewLog>
        implements AdoptionReviewLogService {

    private final SysUserService sysUserService;

    @Override
    public void record(Long applicationId, Long reviewerId, String oldStatus, String newStatus, String operationType, String remark) {
        AdoptionReviewLog log = new AdoptionReviewLog();
        log.setApplicationId(applicationId);
        log.setReviewerId(reviewerId);
        log.setOldStatus(oldStatus);
        log.setNewStatus(newStatus);
        log.setOperationType(operationType);
        log.setRemark(remark);
        log.setCreateTime(LocalDateTime.now());
        save(log);
    }

    @Override
    public List<AdoptionReviewLogVO> listByApplicationId(Long applicationId) {
        return list(new LambdaQueryWrapper<AdoptionReviewLog>()
                .eq(AdoptionReviewLog::getApplicationId, applicationId)
                .orderByAsc(AdoptionReviewLog::getCreateTime))
                .stream()
                .map(this::toVO)
                .toList();
    }

    private AdoptionReviewLogVO toVO(AdoptionReviewLog log) {
        AdoptionReviewLogVO vo = new AdoptionReviewLogVO();
        vo.setId(log.getId());
        vo.setApplicationId(log.getApplicationId());
        vo.setReviewerId(log.getReviewerId());
        if (log.getReviewerId() != null) {
            UserInfoVO user = sysUserService.getUserInfo(log.getReviewerId());
            vo.setReviewerName(user.getNickname() == null ? user.getUsername() : user.getNickname());
        }
        vo.setOldStatus(log.getOldStatus());
        vo.setNewStatus(log.getNewStatus());
        vo.setOperationType(log.getOperationType());
        vo.setRemark(log.getRemark());
        vo.setCreateTime(log.getCreateTime());
        return vo;
    }
}
