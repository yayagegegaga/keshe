package com.example.strayanimal.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.strayanimal.entity.RescueOrderLog;
import com.example.strayanimal.enums.RescueOrderOperationType;
import com.example.strayanimal.vo.RescueOrderLogVO;

import java.util.List;

public interface RescueOrderLogService extends IService<RescueOrderLog> {

    void record(Long orderId, Long operatorId, String oldStatus, String newStatus,
                RescueOrderOperationType operationType, String remark);

    List<RescueOrderLogVO> listByOrderId(Long orderId);
}
