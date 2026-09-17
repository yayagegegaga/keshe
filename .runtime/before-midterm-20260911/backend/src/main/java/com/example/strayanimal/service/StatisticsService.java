package com.example.strayanimal.service;

import com.example.strayanimal.vo.StatisticsOverviewVO;

public interface StatisticsService {
    StatisticsOverviewVO overview();
    void evictOverviewCache();
}
