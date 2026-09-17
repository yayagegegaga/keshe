package com.example.strayanimal.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.strayanimal.common.PageResult;
import com.example.strayanimal.dto.NotificationQueryDTO;
import com.example.strayanimal.entity.Notification;
import com.example.strayanimal.enums.NotificationType;
import com.example.strayanimal.exception.BusinessException;
import com.example.strayanimal.exception.ErrorCode;
import com.example.strayanimal.mapper.NotificationMapper;
import com.example.strayanimal.security.UserContext;
import com.example.strayanimal.service.NotificationService;
import com.example.strayanimal.service.StatisticsService;
import com.example.strayanimal.vo.NotificationVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl extends ServiceImpl<NotificationMapper, Notification> implements NotificationService {

    private final StatisticsService statisticsService;

    @Override
    public void createNotification(Long userId, String title, String content, String type) {
        if (userId == null) {
            return;
        }
        validateType(type);
        Notification notification = new Notification();
        notification.setUserId(userId);
        notification.setTitle(title);
        notification.setContent(content);
        notification.setType(type);
        notification.setIsRead(0);
        notification.setCreateTime(LocalDateTime.now());
        save(notification);
        // 产生新未读通知 → 立即失效接收者的首页统计缓存
        statisticsService.evictOverviewCache(userId);
    }

    @Override
    public PageResult<NotificationVO> pageMine(NotificationQueryDTO queryDTO) {
        validateQuery(queryDTO);
        long pageNum = queryDTO.getPageNum() == null || queryDTO.getPageNum() < 1 ? 1 : queryDTO.getPageNum();
        long pageSize = queryDTO.getPageSize() == null || queryDTO.getPageSize() < 1 ? 10 : Math.min(queryDTO.getPageSize(), 100);
        Page<Notification> page = page(new Page<>(pageNum, pageSize), new LambdaQueryWrapper<Notification>()
                .eq(Notification::getUserId, UserContext.currentUserId())
                .eq(queryDTO.getIsRead() != null, Notification::getIsRead, queryDTO.getIsRead())
                .eq(StringUtils.isNotBlank(queryDTO.getType()), Notification::getType, queryDTO.getType())
                .orderByDesc(Notification::getCreateTime));
        return PageResult.of(page.getRecords().stream().map(this::toVO).toList(), page.getTotal(), page.getCurrent(), page.getSize());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public NotificationVO markRead(Long id) {
        Notification notification = getExisting(id);
        if (!notification.getUserId().equals(UserContext.currentUserId())) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "不能标记他人的通知");
        }
        if (!Integer.valueOf(1).equals(notification.getIsRead())) {
            notification.setIsRead(1);
            notification.setReadTime(LocalDateTime.now());
            updateById(notification);
            // 未读→已读，未读数变化 → 失效首页统计缓存
            statisticsService.evictOverviewCache(notification.getUserId());
        }
        return toVO(notification);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void readAll() {
        LocalDateTime now = LocalDateTime.now();
        list(new LambdaQueryWrapper<Notification>()
                .eq(Notification::getUserId, UserContext.currentUserId())
                .eq(Notification::getIsRead, 0))
                .forEach(item -> {
                    item.setIsRead(1);
                    item.setReadTime(now);
                    updateById(item);
                });
        // 全部已读 → 未读数归零 → 失效首页统计缓存
        statisticsService.evictOverviewCache(UserContext.currentUserId());
    }

    @Override
    public long unreadCount() {
        return count(new LambdaQueryWrapper<Notification>()
                .eq(Notification::getUserId, UserContext.currentUserId())
                .eq(Notification::getIsRead, 0));
    }

    private Notification getExisting(Long id) {
        Notification notification = getById(id);
        if (notification == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "通知不存在");
        }
        return notification;
    }

    private void validateQuery(NotificationQueryDTO queryDTO) {
        if (StringUtils.isNotBlank(queryDTO.getType())) {
            validateType(queryDTO.getType());
        }
        if (queryDTO.getIsRead() != null && queryDTO.getIsRead() != 0 && queryDTO.getIsRead() != 1) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "通知已读状态不合法");
        }
    }

    private void validateType(String type) {
        if (!NotificationType.contains(type)) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "通知类型不合法");
        }
    }

    private NotificationVO toVO(Notification notification) {
        NotificationVO vo = new NotificationVO();
        vo.setId(notification.getId());
        vo.setUserId(notification.getUserId());
        vo.setTitle(notification.getTitle());
        vo.setContent(notification.getContent());
        vo.setType(notification.getType());
        vo.setIsRead(notification.getIsRead());
        vo.setCreateTime(notification.getCreateTime());
        vo.setReadTime(notification.getReadTime());
        return vo;
    }
}
