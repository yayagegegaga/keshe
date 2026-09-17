package com.example.strayanimal.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.strayanimal.common.PageResult;
import com.example.strayanimal.dto.VolunteerTaskCancelDTO;
import com.example.strayanimal.dto.VolunteerTaskClaimDTO;
import com.example.strayanimal.dto.VolunteerTaskCreateDTO;
import com.example.strayanimal.dto.VolunteerTaskFinishDTO;
import com.example.strayanimal.dto.VolunteerTaskQueryDTO;
import com.example.strayanimal.dto.VolunteerTaskReviewDTO;
import com.example.strayanimal.dto.VolunteerTaskUpdateDTO;
import com.example.strayanimal.entity.VolunteerTask;
import com.example.strayanimal.vo.VolunteerTaskDetailVO;
import com.example.strayanimal.vo.VolunteerTaskVO;

public interface VolunteerTaskService extends IService<VolunteerTask> {
    VolunteerTaskVO create(VolunteerTaskCreateDTO createDTO);
    PageResult<VolunteerTaskVO> pageTasks(VolunteerTaskQueryDTO queryDTO);
    PageResult<VolunteerTaskVO> pageMine(VolunteerTaskQueryDTO queryDTO);
    VolunteerTaskDetailVO detail(Long id);
    VolunteerTaskVO update(Long id, VolunteerTaskUpdateDTO updateDTO);
    VolunteerTaskVO claim(Long id, VolunteerTaskClaimDTO claimDTO);
    VolunteerTaskVO finish(Long id, VolunteerTaskFinishDTO finishDTO);
    VolunteerTaskVO review(Long id, VolunteerTaskReviewDTO reviewDTO);
    VolunteerTaskVO cancel(Long id, VolunteerTaskCancelDTO cancelDTO);
    void deleteTask(Long id);
}
