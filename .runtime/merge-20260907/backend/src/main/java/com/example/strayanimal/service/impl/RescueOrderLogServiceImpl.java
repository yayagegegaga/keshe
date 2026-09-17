package com.example.strayanimal.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.strayanimal.entity.RescueOrderLog;
import com.example.strayanimal.entity.SysUser;
import com.example.strayanimal.enums.RescueOrderOperationType;
import com.example.strayanimal.mapper.RescueOrderLogMapper;
import com.example.strayanimal.service.RescueOrderLogService;
import com.example.strayanimal.service.SysUserService;
import com.example.strayanimal.vo.RescueOrderLogVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RescueOrderLogServiceImpl extends ServiceImpl<RescueOrderLogMapper, RescueOrderLog>
        implements RescueOrderLogService {

    private final SysUserService sysUserService;

    @Override
    public void record(Long orderId, Long operatorId, String oldStatus, String newStatus,
                       RescueOrderOperationType operationType, String remark) {
        RescueOrderLog log = new RescueOrderLog();
        log.setOrderId(orderId);
        log.setOperatorId(operatorId);
        log.setOldStatus(oldStatus);
        log.setNewStatus(newStatus);
        log.setOperationType(operationType.getCode());
        log.setRemark(remark);
        save(log);
    }

    @Override
    public List<RescueOrderLogVO> listByOrderId(Long orderId) {
        return lambdaQuery()
                .eq(RescueOrderLog::getOrderId, orderId)
                .orderByAsc(RescueOrderLog::getCreateTime)
                .list()
                .stream()
                .map(this::toVO)
                .toList();
    }

    private RescueOrderLogVO toVO(RescueOrderLog log) {
        RescueOrderLogVO vo = new RescueOrderLogVO();
        vo.setId(log.getId());
        vo.setOrderId(log.getOrderId());
        vo.setOperatorId(log.getOperatorId());
        SysUser user = log.getOperatorId() == null ? null : sysUserService.getById(log.getOperatorId());
        vo.setOperatorName(user == null ? null : user.getNickname());
        vo.setOldStatus(log.getOldStatus());
        vo.setNewStatus(log.getNewStatus());
        vo.setOperationType(log.getOperationType());
        vo.setRemark(log.getRemark());
        vo.setCreateTime(log.getCreateTime());
        return vo;
    }
}
