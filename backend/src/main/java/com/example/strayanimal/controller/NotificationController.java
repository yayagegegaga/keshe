package com.example.strayanimal.controller;

import com.example.strayanimal.common.PageResult;
import com.example.strayanimal.common.Result;
import com.example.strayanimal.dto.NotificationQueryDTO;
import com.example.strayanimal.service.NotificationService;
import com.example.strayanimal.vo.NotificationVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "系统通知接口")
@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @Operation(summary = "查询我的通知")
    @GetMapping("/my")
    public Result<PageResult<NotificationVO>> my(@ModelAttribute NotificationQueryDTO queryDTO) {
        return Result.success(notificationService.pageMine(queryDTO));
    }

    @Operation(summary = "标记单条通知已读")
    @PutMapping("/{id}/read")
    public Result<NotificationVO> read(@PathVariable Long id) {
        return Result.success(notificationService.markRead(id));
    }

    @Operation(summary = "全部通知标记已读")
    @PutMapping("/read-all")
    public Result<Void> readAll() {
        notificationService.readAll();
        return Result.success();
    }

    @Operation(summary = "查询未读通知数量")
    @GetMapping("/unread-count")
    public Result<Long> unreadCount() {
        return Result.success(notificationService.unreadCount());
    }
}
