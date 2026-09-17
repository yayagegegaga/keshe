package com.example.strayanimal.service;

import com.example.strayanimal.vo.StatisticsOverviewVO;

public interface StatisticsService {
    StatisticsOverviewVO overview();
    void evictOverviewCache();
    /** 按指定用户清空首页统计缓存（通知接收者可能不是当前登录用户） */
    void evictOverviewCache(Long userId);
    /** 清空所有管理员（ADMIN/SUPER_ADMIN）的首页统计缓存。志愿者/普通用户操作导致全局统计变化时调用。 */
    void evictAdminOverviewCache();
}
