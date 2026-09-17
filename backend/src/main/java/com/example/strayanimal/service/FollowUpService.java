package com.example.strayanimal.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.strayanimal.common.PageResult;
import com.example.strayanimal.dto.FollowUpCreateDTO;
import com.example.strayanimal.dto.FollowUpQueryDTO;
import com.example.strayanimal.entity.FollowUpRecord;
import com.example.strayanimal.vo.FollowUpRecordDetailVO;
import com.example.strayanimal.vo.FollowUpRecordVO;

public interface FollowUpService extends IService<FollowUpRecord> {
    FollowUpRecordVO create(FollowUpCreateDTO createDTO);
    PageResult<FollowUpRecordVO> pageFollowUps(FollowUpQueryDTO queryDTO);
    PageResult<FollowUpRecordVO> pageMine(FollowUpQueryDTO queryDTO);
    FollowUpRecordDetailVO detail(Long id);
    void deleteFollowUp(Long id);
}
