package com.example.strayanimal.controller;

import com.example.strayanimal.common.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "健康检查")
@RestController
@RequestMapping("/api/health")
public class HealthController {

    @Operation(summary = "后端健康检查")
    @GetMapping
    public Result<String> health() {
        return Result.success("stray-animal backend is running");
    }
}
