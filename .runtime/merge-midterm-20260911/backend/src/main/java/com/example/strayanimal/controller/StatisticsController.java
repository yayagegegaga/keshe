package com.example.strayanimal.controller;

import com.example.strayanimal.common.Result;
import com.example.strayanimal.service.StatisticsService;
import com.example.strayanimal.vo.StatisticsOverviewVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "统计接口")
@RestController
@RequestMapping("/api/statistics")
@RequiredArgsConstructor
public class StatisticsController {

    private final StatisticsService statisticsService;

    @Operation(summary = "首页统计概览")
    @GetMapping("/overview")
    public Result<StatisticsOverviewVO> overview() {
        return Result.success(statisticsService.overview());
    }
}
