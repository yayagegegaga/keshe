package com.example.strayanimal.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.strayanimal.common.PageResult;
import com.example.strayanimal.dto.NotificationQueryDTO;
import com.example.strayanimal.entity.Notification;
import com.example.strayanimal.vo.NotificationVO;

public interface NotificationService extends IService<Notification> {
    void createNotification(Long userId, String title, String content, String type);
    PageResult<NotificationVO> pageMine(NotificationQueryDTO queryDTO);
    NotificationVO markRead(Long id);
    void readAll();
    long unreadCount();
}
