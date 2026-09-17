package com.example.strayanimal.controller;

import com.example.strayanimal.common.PageResult;
import com.example.strayanimal.common.Result;
import com.example.strayanimal.dto.FollowUpCreateDTO;
import com.example.strayanimal.dto.FollowUpQueryDTO;
import com.example.strayanimal.service.FollowUpService;
import com.example.strayanimal.vo.FollowUpRecordDetailVO;
import com.example.strayanimal.vo.FollowUpRecordVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "回访记录接口")
@RestController
@RequestMapping("/api/follow-ups")
@RequiredArgsConstructor
public class FollowUpController {

    private final FollowUpService followUpService;

    @Operation(summary = "新增回访记录")
    @PostMapping
    @PreAuthorize("hasAnyAuthority('ADMIN','SUPER_ADMIN')")
    public Result<FollowUpRecordVO> create(@Valid @RequestBody FollowUpCreateDTO createDTO) {
        return Result.success(followUpService.create(createDTO));
    }

    @Operation(summary = "分页查询回访记录")
    @GetMapping
    @PreAuthorize("hasAnyAuthority('ADMIN','SUPER_ADMIN')")
    public Result<PageResult<FollowUpRecordVO>> page(@ModelAttribute FollowUpQueryDTO queryDTO) {
        return Result.success(followUpService.pageFollowUps(queryDTO));
    }

    @Operation(summary = "查询我的回访记录")
    @GetMapping("/my")
    public Result<PageResult<FollowUpRecordVO>> my(@ModelAttribute FollowUpQueryDTO queryDTO) {
        return Result.success(followUpService.pageMine(queryDTO));
    }

    @Operation(summary = "查询回访详情")
    @GetMapping("/{id}")
    public Result<FollowUpRecordDetailVO> detail(@PathVariable Long id) {
        return Result.success(followUpService.detail(id));
    }

    @Operation(summary = "删除回访记录")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN','SUPER_ADMIN')")
    public Result<Void> delete(@PathVariable Long id) {
        followUpService.deleteFollowUp(id);
        return Result.success();
    }
}
