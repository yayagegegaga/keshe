package com.example.strayanimal.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.strayanimal.entity.TaskRecord;
import com.example.strayanimal.mapper.TaskRecordMapper;
import com.example.strayanimal.service.TaskRecordService;
import com.example.strayanimal.vo.TaskRecordVO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskRecordServiceImpl extends ServiceImpl<TaskRecordMapper, TaskRecord> implements TaskRecordService {
    @Override
    public List<TaskRecordVO> listByTaskId(Long taskId) {
        return lambdaQuery()
                .eq(TaskRecord::getTaskId, taskId)
                .orderByDesc(TaskRecord::getCreateTime)
                .list()
                .stream()
                .map(this::toVO)
                .toList();
    }

    private TaskRecordVO toVO(TaskRecord record) {
        TaskRecordVO vo = new TaskRecordVO();
        vo.setId(record.getId());
        vo.setTaskId(record.getTaskId());
        vo.setVolunteerId(record.getVolunteerId());
        vo.setContent(record.getContent());
        vo.setImageUrl(record.getImageUrl());
        vo.setCreateTime(record.getCreateTime());
        return vo;
    }
}
