package com.example.strayanimal.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.strayanimal.entity.TaskRecord;
import com.example.strayanimal.vo.TaskRecordVO;

import java.util.List;

public interface TaskRecordService extends IService<TaskRecord> {
    List<TaskRecordVO> listByTaskId(Long taskId);
}
