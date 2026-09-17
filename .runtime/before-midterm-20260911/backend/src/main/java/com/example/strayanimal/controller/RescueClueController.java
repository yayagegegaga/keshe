package com.example.strayanimal.controller;

import com.example.strayanimal.common.PageResult;
import com.example.strayanimal.common.Result;
import com.example.strayanimal.dto.RescueClueCreateDTO;
import com.example.strayanimal.dto.RescueClueQueryDTO;
import com.example.strayanimal.dto.RescueClueReviewDTO;
import com.example.strayanimal.service.RescueClueService;
import com.example.strayanimal.vo.RescueClueDetailVO;
import com.example.strayanimal.vo.RescueClueVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "救助线索接口")
@RestController
@RequestMapping("/api/rescue-clues")
@RequiredArgsConstructor
public class RescueClueController {

    private final RescueClueService rescueClueService;

    @Operation(summary = "提交救助线索")
    @PostMapping
    public Result<RescueClueVO> create(@Valid @RequestBody RescueClueCreateDTO createDTO) {
        return Result.success(rescueClueService.create(createDTO));
    }

    @Operation(summary = "分页查询全部救助线索")
    @GetMapping
    @PreAuthorize("hasAnyAuthority('ADMIN','SUPER_ADMIN')")
    public Result<PageResult<RescueClueVO>> pageAll(@ModelAttribute RescueClueQueryDTO queryDTO) {
        return Result.success(rescueClueService.pageAll(queryDTO));
    }

    @Operation(summary = "分页查询我的救助线索")
    @GetMapping("/my")
    public Result<PageResult<RescueClueVO>> pageMine(@ModelAttribute RescueClueQueryDTO queryDTO) {
        return Result.success(rescueClueService.pageMine(queryDTO));
    }

    @Operation(summary = "查询救助线索详情")
    @GetMapping("/{id}")
    public Result<RescueClueDetailVO> detail(@PathVariable Long id) {
        return Result.success(rescueClueService.detail(id));
    }

    @Operation(summary = "审核救助线索")
    @PutMapping("/{id}/review")
    @PreAuthorize("hasAnyAuthority('ADMIN','SUPER_ADMIN')")
    public Result<RescueClueVO> review(@PathVariable Long id, @Valid @RequestBody RescueClueReviewDTO reviewDTO) {
        return Result.success(rescueClueService.review(id, reviewDTO));
    }

    @Operation(summary = "取消救助线索")
    @PutMapping("/{id}/cancel")
    public Result<RescueClueVO> cancel(@PathVariable Long id) {
        return Result.success(rescueClueService.cancel(id));
    }
}
