package com.example.strayanimal.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.strayanimal.common.PageResult;
import com.example.strayanimal.dto.RescueOrderAssignDTO;
import com.example.strayanimal.dto.RescueOrderCancelDTO;
import com.example.strayanimal.dto.RescueOrderCloseDTO;
import com.example.strayanimal.dto.RescueOrderCreateDTO;
import com.example.strayanimal.dto.RescueOrderFinishDTO;
import com.example.strayanimal.dto.RescueOrderQueryDTO;
import com.example.strayanimal.entity.RescueClue;
import com.example.strayanimal.entity.RescueOrder;
import com.example.strayanimal.entity.RescueOrderLog;
import com.example.strayanimal.enums.EmergencyLevel;
import com.example.strayanimal.enums.NotificationType;
import com.example.strayanimal.enums.RescueClueStatus;
import com.example.strayanimal.enums.RescueOrderOperationType;
import com.example.strayanimal.enums.RescueOrderStatus;
import com.example.strayanimal.exception.BusinessException;
import com.example.strayanimal.exception.ErrorCode;
import com.example.strayanimal.mapper.RescueClueMapper;
import com.example.strayanimal.mapper.RescueOrderMapper;
import com.example.strayanimal.security.SecurityUser;
import com.example.strayanimal.security.UserContext;
import com.example.strayanimal.service.RescueOrderLogService;
import com.example.strayanimal.service.RescueOrderService;
import com.example.strayanimal.service.NotificationService;
import com.example.strayanimal.service.SysUserService;
import com.example.strayanimal.vo.RescueClueVO;
import com.example.strayanimal.vo.RescueOrderDetailVO;
import com.example.strayanimal.vo.RescueOrderLogVO;
import com.example.strayanimal.vo.RescueOrderVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class RescueOrderServiceImpl extends ServiceImpl<RescueOrderMapper, RescueOrder> implements RescueOrderService {

    private final RescueClueMapper rescueClueMapper;

    private final RescueOrderLogService rescueOrderLogService;

    private final SysUserService sysUserService;

    private final NotificationService notificationService;

    private static final Map<String, Set<String>> ALLOWED_TRANSITIONS = Map.of(
            RescueOrderStatus.WAIT_ASSIGN.getCode(), Set.of(RescueOrderStatus.ASSIGNED.getCode(), RescueOrderStatus.CANCELED.getCode(), RescueOrderStatus.CLOSED.getCode()),
            RescueOrderStatus.ASSIGNED.getCode(), Set.of(RescueOrderStatus.PROCESSING.getCode(), RescueOrderStatus.CANCELED.getCode()),
            RescueOrderStatus.PROCESSING.getCode(), Set.of(RescueOrderStatus.WAIT_CONFIRM.getCode()),
            RescueOrderStatus.WAIT_CONFIRM.getCode(), Set.of(RescueOrderStatus.CLOSED.getCode())
    );

    @Override
    @Transactional(rollbackFor = Exception.class)
    public RescueOrderVO create(RescueOrderCreateDTO createDTO) {
        validateEmergencyLevel(createDTO.getEmergencyLevel());
        if (createDTO.getClueId() != null) {
            RescueClue clue = rescueClueMapper.selectById(createDTO.getClueId());
            if (clue == null) {
                throw new BusinessException(ErrorCode.NOT_FOUND, "救助线索不存在");
            }
            if (RescueClueStatus.CONVERTED.getCode().equals(clue.getStatus())) {
                throw new BusinessException(ErrorCode.BUSINESS_ERROR, "线索已转工单，不能重复创建");
            }
        }
        RescueOrder order = createOrder(createDTO.getClueId(), createDTO.getTitle(), createDTO.getDescription(),
                createDTO.getLocation(), createDTO.getEmergencyLevel(), UserContext.currentUserId());
        return toVO(order);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public RescueOrder createFromClue(Long clueId, String title, String description, String location, String emergencyLevel, Long creatorId) {
        return createOrder(clueId, title, description, location, emergencyLevel, creatorId);
    }

    @Override
    public PageResult<RescueOrderVO> pageOrders(RescueOrderQueryDTO queryDTO) {
        validateQuery(queryDTO);
        SecurityUser user = UserContext.currentUser();
        long pageNum = queryDTO.getPageNum() == null || queryDTO.getPageNum() < 1 ? 1 : queryDTO.getPageNum();
        long pageSize = queryDTO.getPageSize() == null || queryDTO.getPageSize() < 1 ? 10 : Math.min(queryDTO.getPageSize(), 100);
        LambdaQueryWrapper<RescueOrder> wrapper = new LambdaQueryWrapper<RescueOrder>()
                .eq(StringUtils.isNotBlank(queryDTO.getStatus()), RescueOrder::getStatus, queryDTO.getStatus())
                .eq(StringUtils.isNotBlank(queryDTO.getEmergencyLevel()), RescueOrder::getEmergencyLevel, queryDTO.getEmergencyLevel())
                .eq(queryDTO.getHandlerId() != null && isAdmin(user), RescueOrder::getHandlerId, queryDTO.getHandlerId())
                .eq(isVolunteerOnly(user), RescueOrder::getHandlerId, user.getUserId())
                .and(StringUtils.isNotBlank(queryDTO.getKeyword()), w -> w
                        .like(RescueOrder::getTitle, queryDTO.getKeyword())
                        .or()
                        .like(RescueOrder::getLocation, queryDTO.getKeyword())
                        .or()
                        .like(RescueOrder::getDescription, queryDTO.getKeyword()))
                .orderByDesc(RescueOrder::getCreateTime);
        Page<RescueOrder> page = page(new Page<>(pageNum, pageSize), wrapper);
        return PageResult.of(page.getRecords().stream().map(this::toVO).toList(), page.getTotal(), page.getCurrent(), page.getSize());
    }

    @Override
    public RescueOrderDetailVO detail(Long id) {
        RescueOrder order = getExisting(id);
        ensureCanView(order);
        RescueOrderDetailVO vo = toDetailVO(order);
        if (order.getClueId() != null) {
            RescueClue clue = rescueClueMapper.selectById(order.getClueId());
            if (clue != null) {
                RescueClueVO clueVO = new RescueClueVO();
                clueVO.setId(clue.getId());
                clueVO.setSubmitterId(clue.getSubmitterId());
                clueVO.setAnimalType(clue.getAnimalType());
                clueVO.setLocation(clue.getLocation());
                clueVO.setDescription(clue.getDescription());
                clueVO.setEmergencyLevel(clue.getEmergencyLevel());
                clueVO.setImageUrl(clue.getImageUrl());
                clueVO.setContact(clue.getContact());
                clueVO.setStatus(clue.getStatus());
                clueVO.setReviewerId(clue.getReviewerId());
                clueVO.setReviewRemark(clue.getReviewRemark());
                clueVO.setCreateTime(clue.getCreateTime());
                clueVO.setUpdateTime(clue.getUpdateTime());
                vo.setClue(clueVO);
            }
        }
        if (order.getHandlerId() != null) {
            vo.setHandler(sysUserService.getUserInfo(order.getHandlerId()));
        }
        vo.setLogs(rescueOrderLogService.listByOrderId(id));
        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public RescueOrderVO assign(Long id, RescueOrderAssignDTO assignDTO) {
        RescueOrder order = getExisting(id);
        validateTransition(order.getStatus(), RescueOrderStatus.ASSIGNED.getCode());
        ensureVolunteer(assignDTO.getHandlerId());
        String oldStatus = order.getStatus();
        order.setHandlerId(assignDTO.getHandlerId());
        order.setStatus(RescueOrderStatus.ASSIGNED.getCode());
        updateById(order);
        rescueOrderLogService.record(order.getId(), UserContext.currentUserId(), oldStatus, order.getStatus(),
                RescueOrderOperationType.ASSIGN, assignDTO.getRemark());
        notificationService.createNotification(assignDTO.getHandlerId(), "新的救助工单",
                "你有一个新的救助工单待处理：" + order.getTitle(), NotificationType.RESCUE_ORDER.getCode());
        return toVO(order);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public RescueOrderVO start(Long id) {
        RescueOrder order = getExisting(id);
        validateTransition(order.getStatus(), RescueOrderStatus.PROCESSING.getCode());
        if (order.getHandlerId() == null) {
            throw new BusinessException(ErrorCode.BUSINESS_ERROR, "未分配志愿者不能开始处理");
        }
        ensureHandlerOrAdmin(order);
        String oldStatus = order.getStatus();
        order.setStatus(RescueOrderStatus.PROCESSING.getCode());
        updateById(order);
        rescueOrderLogService.record(order.getId(), UserContext.currentUserId(), oldStatus, order.getStatus(),
                RescueOrderOperationType.START, "开始处理");
        return toVO(order);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public RescueOrderVO finish(Long id, RescueOrderFinishDTO finishDTO) {
        RescueOrder order = getExisting(id);
        validateTransition(order.getStatus(), RescueOrderStatus.WAIT_CONFIRM.getCode());
        ensureHandlerOrAdmin(order);
        String oldStatus = order.getStatus();
        order.setStatus(RescueOrderStatus.WAIT_CONFIRM.getCode());
        order.setProcessResult(finishDTO.getProcessResult());
        order.setProcessImage(finishDTO.getProcessImage());
        updateById(order);
        rescueOrderLogService.record(order.getId(), UserContext.currentUserId(), oldStatus, order.getStatus(),
                RescueOrderOperationType.FINISH, finishDTO.getRemark());
        return toVO(order);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public RescueOrderVO close(Long id, RescueOrderCloseDTO closeDTO) {
        RescueOrder order = getExisting(id);
        validateTransition(order.getStatus(), RescueOrderStatus.CLOSED.getCode());
        String oldStatus = order.getStatus();
        order.setStatus(RescueOrderStatus.CLOSED.getCode());
        order.setCloseTime(LocalDateTime.now());
        updateById(order);
        rescueOrderLogService.record(order.getId(), UserContext.currentUserId(), oldStatus, order.getStatus(),
                RescueOrderOperationType.CLOSE, closeDTO == null ? null : closeDTO.getRemark());
        return toVO(order);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public RescueOrderVO cancel(Long id, RescueOrderCancelDTO cancelDTO) {
        RescueOrder order = getExisting(id);
        validateTransition(order.getStatus(), RescueOrderStatus.CANCELED.getCode());
        String oldStatus = order.getStatus();
        order.setStatus(RescueOrderStatus.CANCELED.getCode());
        updateById(order);
        rescueOrderLogService.record(order.getId(), UserContext.currentUserId(), oldStatus, order.getStatus(),
                RescueOrderOperationType.CANCEL, cancelDTO == null ? null : cancelDTO.getReason());
        return toVO(order);
    }

    @Override
    public List<RescueOrderLogVO> logs(Long id) {
        RescueOrder order = getExisting(id);
        ensureCanView(order);
        return rescueOrderLogService.listByOrderId(id);
    }

    @Override
@Transactional(rollbackFor = Exception.class)
public void deleteOrder(Long id) {
    if (!isAdmin(UserContext.currentUser())) {
        throw new BusinessException(ErrorCode.FORBIDDEN, "只有管理员可以删除工单");
    }
    RescueOrder order = getOne(new LambdaQueryWrapper<RescueOrder>()
            .eq(RescueOrder::getId, id).last("FOR UPDATE"));
    if (order == null) {
        throw new BusinessException(ErrorCode.NOT_FOUND, "救助工单不存在");
    }
    // ===== 修改：去掉状态限制，任何状态都可以删除 =====
    // 原限制：if (!RescueOrderStatus.WAIT_ASSIGN.getCode().equals(order.getStatus())) { throw ... }
    // 现已移除

    // 删除工单的所有流转日志
    rescueOrderLogService.remove(new LambdaQueryWrapper<RescueOrderLog>()
            .eq(RescueOrderLog::getOrderId, id));

    // 删除工单
    removeById(id);

    // 如果工单关联了线索，且该线索没有其他工单了，则把线索还原为待审核
    if (order.getClueId() != null && count(new LambdaQueryWrapper<RescueOrder>()
            .eq(RescueOrder::getClueId, order.getClueId())) == 0) {
        rescueClueMapper.update(null, new com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper<RescueClue>()
                .eq(RescueClue::getId, order.getClueId())
                .eq(RescueClue::getStatus, RescueClueStatus.CONVERTED.getCode())
                .set(RescueClue::getStatus, RescueClueStatus.PENDING.getCode())
                .set(RescueClue::getReviewerId, null)
                .set(RescueClue::getReviewRemark, null));
    }
}

    private RescueOrder createOrder(Long clueId, String title, String description, String location, String emergencyLevel, Long creatorId) {
        validateEmergencyLevel(emergencyLevel);
        RescueOrder order = new RescueOrder();
        order.setClueId(clueId);
        order.setTitle(title);
        order.setDescription(description);
        order.setLocation(location);
        order.setEmergencyLevel(emergencyLevel);
        order.setStatus(RescueOrderStatus.WAIT_ASSIGN.getCode());
        order.setCreatorId(creatorId);
        save(order);
        rescueOrderLogService.record(order.getId(), creatorId, null, order.getStatus(), RescueOrderOperationType.CREATE, "创建工单");
        return order;
    }

    private void validateTransition(String oldStatus, String newStatus) {
        if (!RescueOrderStatus.contains(newStatus) || !ALLOWED_TRANSITIONS.getOrDefault(oldStatus, Set.of()).contains(newStatus)) {
            throw new BusinessException(ErrorCode.BUSINESS_ERROR, "工单状态流转非法：" + oldStatus + " -> " + newStatus);
        }
    }

    private RescueOrder getExisting(Long id) {
        RescueOrder order = getById(id);
        if (order == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "救助工单不存在");
        }
        return order;
    }

    private void ensureVolunteer(Long userId) {
        if (sysUserService.getById(userId) == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "处理人不存在");
        }
        if (!sysUserService.getUserInfo(userId).getRoles().contains("VOLUNTEER")) {
            throw new BusinessException(ErrorCode.BUSINESS_ERROR, "处理人必须具有志愿者角色");
        }
    }

    private void ensureHandlerOrAdmin(RescueOrder order) {
        SecurityUser user = UserContext.currentUser();
        if (isAdmin(user)) {
            return;
        }
        if (!order.getHandlerId().equals(user.getUserId())) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "当前用户不是该工单处理人");
        }
    }

    private void ensureCanView(RescueOrder order) {
        SecurityUser user = UserContext.currentUser();
        if (isAdmin(user)) {
            return;
        }
        if (isVolunteerOnly(user) && order.getHandlerId() != null && order.getHandlerId().equals(user.getUserId())) {
            return;
        }
        throw new BusinessException(ErrorCode.FORBIDDEN, "无权查看该工单");
    }

    private boolean isAdmin(SecurityUser user) {
        return user.getRoles().contains("ADMIN") || user.getRoles().contains("SUPER_ADMIN");
    }

    private boolean isVolunteerOnly(SecurityUser user) {
        return user.getRoles().contains("VOLUNTEER") && !isAdmin(user);
    }

    private void validateQuery(RescueOrderQueryDTO queryDTO) {
        if (StringUtils.isNotBlank(queryDTO.getStatus()) && !RescueOrderStatus.contains(queryDTO.getStatus())) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "工单状态不合法");
        }
        if (StringUtils.isNotBlank(queryDTO.getEmergencyLevel())) {
            validateEmergencyLevel(queryDTO.getEmergencyLevel());
        }
    }

    private void validateEmergencyLevel(String emergencyLevel) {
        if (!EmergencyLevel.contains(emergencyLevel)) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "紧急程度不合法");
        }
    }

    private RescueOrderDetailVO toDetailVO(RescueOrder order) {
        RescueOrderDetailVO vo = new RescueOrderDetailVO();
        copy(order, vo);
        return vo;
    }

    private RescueOrderVO toVO(RescueOrder order) {
        RescueOrderVO vo = new RescueOrderVO();
        copy(order, vo);
        return vo;
    }

    private void copy(RescueOrder order, RescueOrderVO vo) {
        vo.setId(order.getId());
        vo.setClueId(order.getClueId());
        vo.setTitle(order.getTitle());
        vo.setDescription(order.getDescription());
        vo.setLocation(order.getLocation());
        vo.setEmergencyLevel(order.getEmergencyLevel());
        vo.setStatus(order.getStatus());
        vo.setCreatorId(order.getCreatorId());
        vo.setHandlerId(order.getHandlerId());
        vo.setProcessResult(order.getProcessResult());
        vo.setProcessImage(order.getProcessImage());
        vo.setCreateTime(order.getCreateTime());
        vo.setUpdateTime(order.getUpdateTime());
        vo.setCloseTime(order.getCloseTime());
    }
}
