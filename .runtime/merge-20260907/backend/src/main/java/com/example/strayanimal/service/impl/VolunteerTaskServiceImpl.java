package com.example.strayanimal.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.strayanimal.common.PageResult;
import com.example.strayanimal.dto.VolunteerTaskCancelDTO;
import com.example.strayanimal.dto.VolunteerTaskClaimDTO;
import com.example.strayanimal.dto.VolunteerTaskCreateDTO;
import com.example.strayanimal.dto.VolunteerTaskFinishDTO;
import com.example.strayanimal.dto.VolunteerTaskQueryDTO;
import com.example.strayanimal.dto.VolunteerTaskReviewDTO;
import com.example.strayanimal.dto.VolunteerTaskUpdateDTO;
import com.example.strayanimal.entity.TaskRecord;
import com.example.strayanimal.entity.VolunteerTask;
import com.example.strayanimal.enums.VolunteerTaskStatus;
import com.example.strayanimal.enums.VolunteerTaskType;
import com.example.strayanimal.enums.NotificationType;
import com.example.strayanimal.exception.BusinessException;
import com.example.strayanimal.exception.ErrorCode;
import com.example.strayanimal.mapper.VolunteerTaskMapper;
import com.example.strayanimal.security.SecurityUser;
import com.example.strayanimal.security.UserContext;
import com.example.strayanimal.service.SysUserService;
import com.example.strayanimal.service.TaskRecordService;
import com.example.strayanimal.service.NotificationService;
import com.example.strayanimal.service.VolunteerTaskService;
import com.example.strayanimal.vo.VolunteerTaskDetailVO;
import com.example.strayanimal.vo.VolunteerTaskVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class VolunteerTaskServiceImpl extends ServiceImpl<VolunteerTaskMapper, VolunteerTask> implements VolunteerTaskService {

    private final TaskRecordService taskRecordService;
    private final SysUserService sysUserService;
    private final NotificationService notificationService;

    private static final Map<String, Set<String>> ALLOWED_TRANSITIONS = Map.of(
            VolunteerTaskStatus.WAIT_CLAIM.getCode(), Set.of(VolunteerTaskStatus.CLAIMED.getCode(), VolunteerTaskStatus.CANCELED.getCode()),
            VolunteerTaskStatus.CLAIMED.getCode(), Set.of(VolunteerTaskStatus.FINISHED.getCode(), VolunteerTaskStatus.CANCELED.getCode()),
            VolunteerTaskStatus.FINISHED.getCode(), Set.of(VolunteerTaskStatus.REVIEWED.getCode(), VolunteerTaskStatus.CLAIMED.getCode())
    );

    @Override
    @Transactional(rollbackFor = Exception.class)
    public VolunteerTaskVO create(VolunteerTaskCreateDTO createDTO) {
        validateTaskType(createDTO.getTaskType());
        VolunteerTask task = new VolunteerTask();
        task.setTitle(createDTO.getTitle());
        task.setTaskType(createDTO.getTaskType());
        task.setDescription(createDTO.getDescription());
        task.setLocation(createDTO.getLocation());
        task.setStatus(VolunteerTaskStatus.WAIT_CLAIM.getCode());
        task.setPublisherId(UserContext.currentUserId());
        task.setVersion(0);
        save(task);
        return toVO(task);
    }

    // ========== 修改：pageTasks 方法，志愿者可以看到所有任务 ==========
    @Override
    public PageResult<VolunteerTaskVO> pageTasks(VolunteerTaskQueryDTO queryDTO) {
        validateQuery(queryDTO);
        SecurityUser current = UserContext.currentUser();
        long pageNum = queryDTO.getPageNum() == null || queryDTO.getPageNum() < 1 ? 1 : queryDTO.getPageNum();
        long pageSize = queryDTO.getPageSize() == null || queryDTO.getPageSize() < 1 ? 10 : Math.min(queryDTO.getPageSize(), 100);
        
        // 志愿者可以看到所有任务，不再限制只看到WAIT_CLAIM
        LambdaQueryWrapper<VolunteerTask> wrapper = baseQuery(queryDTO)
                .eq(queryDTO.getVolunteerId() != null && isAdmin(current), VolunteerTask::getVolunteerId, queryDTO.getVolunteerId())
                .orderByDesc(VolunteerTask::getCreateTime);
        
        Page<VolunteerTask> page = page(new Page<>(pageNum, pageSize), wrapper);
        return PageResult.of(page.getRecords().stream().map(this::toVO).toList(),
                page.getTotal(), page.getCurrent(), page.getSize());
    }
    // ========== 修改结束 ==========

    @Override
    public PageResult<VolunteerTaskVO> pageMine(VolunteerTaskQueryDTO queryDTO) {
        validateQuery(queryDTO);
        long pageNum = queryDTO.getPageNum() == null || queryDTO.getPageNum() < 1 ? 1 : queryDTO.getPageNum();
        long pageSize = queryDTO.getPageSize() == null || queryDTO.getPageSize() < 1 ? 10 : Math.min(queryDTO.getPageSize(), 100);
        LambdaQueryWrapper<VolunteerTask> wrapper = baseQuery(queryDTO)
                .eq(VolunteerTask::getVolunteerId, UserContext.currentUserId())
                .orderByDesc(VolunteerTask::getCreateTime);
        Page<VolunteerTask> page = page(new Page<>(pageNum, pageSize), wrapper);
        return PageResult.of(page.getRecords().stream().map(this::toVO).toList(),
                page.getTotal(), page.getCurrent(), page.getSize());
    }

    @Override
    public VolunteerTaskDetailVO detail(Long id) {
        VolunteerTask task = getExisting(id);
        ensureCanView(task);
        VolunteerTaskDetailVO vo = toDetailVO(task);
        vo.setPublisher(sysUserService.getUserInfo(task.getPublisherId()));
        if (task.getVolunteerId() != null) {
            vo.setVolunteer(sysUserService.getUserInfo(task.getVolunteerId()));
        }
        vo.setRecords(taskRecordService.listByTaskId(id));
        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public VolunteerTaskVO update(Long id, VolunteerTaskUpdateDTO updateDTO) {
        VolunteerTask task = getExisting(id);
        if (!VolunteerTaskStatus.WAIT_CLAIM.getCode().equals(task.getStatus())) {
            throw new BusinessException(ErrorCode.BUSINESS_ERROR, "只有待领取任务允许修改");
        }
        validateTaskType(updateDTO.getTaskType());
        task.setTitle(updateDTO.getTitle());
        task.setTaskType(updateDTO.getTaskType());
        task.setDescription(updateDTO.getDescription());
        task.setLocation(updateDTO.getLocation());
        updateById(task);
        return toVO(task);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public VolunteerTaskVO claim(Long id, VolunteerTaskClaimDTO claimDTO) {
        VolunteerTask task = getExisting(id);
        validateTransition(task.getStatus(), VolunteerTaskStatus.CLAIMED.getCode());
        int rows = baseMapper.claimIfWaitClaim(id, UserContext.currentUserId());
        if (rows == 0) {
            throw new BusinessException(ErrorCode.BUSINESS_ERROR, "任务已被领取或状态已变化");
        }
        return toVO(getById(id));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public VolunteerTaskVO finish(Long id, VolunteerTaskFinishDTO finishDTO) {
        VolunteerTask task = getExisting(id);
        validateTransition(task.getStatus(), VolunteerTaskStatus.FINISHED.getCode());
        ensureTaskVolunteer(task);
        task.setStatus(VolunteerTaskStatus.FINISHED.getCode());
        task.setFinishTime(LocalDateTime.now());
        updateById(task);
        TaskRecord record = new TaskRecord();
        record.setTaskId(task.getId());
        record.setVolunteerId(UserContext.currentUserId());
        record.setContent(finishDTO.getContent());
        record.setImageUrl(finishDTO.getImageUrl());
        taskRecordService.save(record);
        return toVO(task);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public VolunteerTaskVO review(Long id, VolunteerTaskReviewDTO reviewDTO) {
        VolunteerTask task = getExisting(id);
        if (!VolunteerTaskStatus.FINISHED.getCode().equals(task.getStatus())) {
            throw new BusinessException(ErrorCode.BUSINESS_ERROR, "只有已完成任务可以审核");
        }
        String newStatus = Boolean.TRUE.equals(reviewDTO.getApproved())
                ? VolunteerTaskStatus.REVIEWED.getCode()
                : VolunteerTaskStatus.CLAIMED.getCode();
        validateTransition(task.getStatus(), newStatus);
        task.setStatus(newStatus);
        task.setReviewRemark(reviewDTO.getReviewRemark());
        if (VolunteerTaskStatus.CLAIMED.getCode().equals(newStatus)) {
            task.setFinishTime(null);
        }
        updateById(task);
        if (VolunteerTaskStatus.REVIEWED.getCode().equals(newStatus)) {
            notificationService.createNotification(task.getVolunteerId(), "志愿者任务审核通过",
                    "你的志愿者任务已审核通过：" + task.getTitle(), NotificationType.TASK.getCode());
        }
        return toVO(task);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public VolunteerTaskVO cancel(Long id, VolunteerTaskCancelDTO cancelDTO) {
        VolunteerTask task = getExisting(id);
        validateTransition(task.getStatus(), VolunteerTaskStatus.CANCELED.getCode());
        task.setStatus(VolunteerTaskStatus.CANCELED.getCode());
        task.setReviewRemark(cancelDTO == null ? null : cancelDTO.getReason());
        updateById(task);
        return toVO(task);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteTask(Long id) {
        VolunteerTask task = getExisting(id);
        if (!VolunteerTaskStatus.WAIT_CLAIM.getCode().equals(task.getStatus())
                && !VolunteerTaskStatus.CANCELED.getCode().equals(task.getStatus())) {
            throw new BusinessException(ErrorCode.BUSINESS_ERROR, "只有待领取或已取消任务可以删除");
        }
        taskRecordService.remove(new LambdaQueryWrapper<TaskRecord>().eq(TaskRecord::getTaskId, id));
        removeById(id);
    }

    private LambdaQueryWrapper<VolunteerTask> baseQuery(VolunteerTaskQueryDTO queryDTO) {
        return new LambdaQueryWrapper<VolunteerTask>()
                .eq(StringUtils.isNotBlank(queryDTO.getStatus()), VolunteerTask::getStatus, queryDTO.getStatus())
                .eq(StringUtils.isNotBlank(queryDTO.getTaskType()), VolunteerTask::getTaskType, queryDTO.getTaskType())
                .and(StringUtils.isNotBlank(queryDTO.getKeyword()), w -> w
                        .like(VolunteerTask::getTitle, queryDTO.getKeyword())
                        .or()
                        .like(VolunteerTask::getLocation, queryDTO.getKeyword())
                        .or()
                        .like(VolunteerTask::getDescription, queryDTO.getKeyword()));
    }

    private VolunteerTask getExisting(Long id) {
        VolunteerTask task = getById(id);
        if (task == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "志愿者任务不存在");
        }
        return task;
    }

    private void validateQuery(VolunteerTaskQueryDTO queryDTO) {
        if (StringUtils.isNotBlank(queryDTO.getStatus()) && !VolunteerTaskStatus.contains(queryDTO.getStatus())) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "任务状态不合法");
        }
        if (StringUtils.isNotBlank(queryDTO.getTaskType())) {
            validateTaskType(queryDTO.getTaskType());
        }
    }

    private void validateTaskType(String taskType) {
        if (!VolunteerTaskType.contains(taskType)) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "任务类型不合法");
        }
    }

    private void validateTransition(String oldStatus, String newStatus) {
        if (!ALLOWED_TRANSITIONS.getOrDefault(oldStatus, Set.of()).contains(newStatus)) {
            throw new BusinessException(ErrorCode.BUSINESS_ERROR, "任务状态流转非法：" + oldStatus + " -> " + newStatus);
        }
    }

    private void ensureTaskVolunteer(VolunteerTask task) {
        if (task.getVolunteerId() == null || !task.getVolunteerId().equals(UserContext.currentUserId())) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "当前用户不是该任务领取人");
        }
    }

    private void ensureCanView(VolunteerTask task) {
        SecurityUser user = UserContext.currentUser();
        if (isAdmin(user)) {
            return;
        }
        // 志愿者可以查看所有任务
        if (isVolunteerOnly(user)) {
            return;
        }
        throw new BusinessException(ErrorCode.FORBIDDEN, "无权查看该任务");
    }

    private boolean isAdmin(SecurityUser user) {
        return user.getRoles().contains("ADMIN") || user.getRoles().contains("SUPER_ADMIN");
    }

    private boolean isVolunteerOnly(SecurityUser user) {
        return user.getRoles().contains("VOLUNTEER") && !isAdmin(user);
    }

    private VolunteerTaskDetailVO toDetailVO(VolunteerTask task) {
        VolunteerTaskDetailVO vo = new VolunteerTaskDetailVO();
        copy(task, vo);
        return vo;
    }

    private VolunteerTaskVO toVO(VolunteerTask task) {
        VolunteerTaskVO vo = new VolunteerTaskVO();
        copy(task, vo);
        return vo;
    }

    private void copy(VolunteerTask task, VolunteerTaskVO vo) {
        vo.setId(task.getId());
        vo.setTitle(task.getTitle());
        vo.setTaskType(task.getTaskType());
        vo.setDescription(task.getDescription());
        vo.setLocation(task.getLocation());
        vo.setStatus(task.getStatus());
        vo.setPublisherId(task.getPublisherId());
        vo.setVolunteerId(task.getVolunteerId());
        vo.setStartTime(task.getStartTime());
        vo.setFinishTime(task.getFinishTime());
        vo.setReviewRemark(task.getReviewRemark());
        vo.setVersion(task.getVersion());
        vo.setCreateTime(task.getCreateTime());
        vo.setUpdateTime(task.getUpdateTime());
    }
}