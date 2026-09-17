package com.example.strayanimal.controller;

import com.example.strayanimal.common.PageResult;
import com.example.strayanimal.common.Result;
import com.example.strayanimal.dto.VolunteerTaskCancelDTO;
import com.example.strayanimal.dto.VolunteerTaskClaimDTO;
import com.example.strayanimal.dto.VolunteerTaskCreateDTO;
import com.example.strayanimal.dto.VolunteerTaskFinishDTO;
import com.example.strayanimal.dto.VolunteerTaskQueryDTO;
import com.example.strayanimal.dto.VolunteerTaskReviewDTO;
import com.example.strayanimal.dto.VolunteerTaskUpdateDTO;
import com.example.strayanimal.service.VolunteerTaskService;
import com.example.strayanimal.vo.VolunteerTaskDetailVO;
import com.example.strayanimal.vo.VolunteerTaskVO;
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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "志愿者任务接口")
@RestController
@RequestMapping("/api/volunteer-tasks")
@RequiredArgsConstructor
public class VolunteerTaskController {

    private final VolunteerTaskService volunteerTaskService;

    @Operation(summary = "发布志愿者任务")
    @PostMapping
    @PreAuthorize("hasAnyAuthority('ADMIN','SUPER_ADMIN')")
    public Result<VolunteerTaskVO> create(@Valid @RequestBody VolunteerTaskCreateDTO createDTO) {
        return Result.success(volunteerTaskService.create(createDTO));
    }

    @Operation(summary = "分页查询志愿者任务")
    @GetMapping
    @PreAuthorize("hasAnyAuthority('ADMIN','SUPER_ADMIN','VOLUNTEER')")
    public Result<PageResult<VolunteerTaskVO>> page(@ModelAttribute VolunteerTaskQueryDTO queryDTO) {
        return Result.success(volunteerTaskService.pageTasks(queryDTO));
    }

    @Operation(summary = "查询我的志愿者任务")
    @GetMapping("/my")
    @PreAuthorize("hasAuthority('VOLUNTEER')")
    public Result<PageResult<VolunteerTaskVO>> my(@ModelAttribute VolunteerTaskQueryDTO queryDTO) {
        return Result.success(volunteerTaskService.pageMine(queryDTO));
    }

    @Operation(summary = "查询志愿者任务详情")
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN','SUPER_ADMIN','VOLUNTEER')")
    public Result<VolunteerTaskDetailVO> detail(@PathVariable Long id) {
        return Result.success(volunteerTaskService.detail(id));
    }

    @Operation(summary = "修改志愿者任务")
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN','SUPER_ADMIN')")
    public Result<VolunteerTaskVO> update(@PathVariable Long id, @Valid @RequestBody VolunteerTaskUpdateDTO updateDTO) {
        return Result.success(volunteerTaskService.update(id, updateDTO));
    }

    @Operation(summary = "领取志愿者任务")
    @PutMapping("/{id}/claim")
    @PreAuthorize("hasAuthority('VOLUNTEER')")
    public Result<VolunteerTaskVO> claim(@PathVariable Long id,
                                         @RequestBody(required = false) VolunteerTaskClaimDTO claimDTO) {
        return Result.success(volunteerTaskService.claim(id, claimDTO));
    }

    @Operation(summary = "提交志愿者任务完成记录")
    @PutMapping("/{id}/finish")
    @PreAuthorize("hasAuthority('VOLUNTEER')")
    public Result<VolunteerTaskVO> finish(@PathVariable Long id, @Valid @RequestBody VolunteerTaskFinishDTO finishDTO) {
        return Result.success(volunteerTaskService.finish(id, finishDTO));
    }

    @Operation(summary = "审核志愿者任务")
    @PutMapping("/{id}/review")
    @PreAuthorize("hasAnyAuthority('ADMIN','SUPER_ADMIN')")
    public Result<VolunteerTaskVO> review(@PathVariable Long id, @Valid @RequestBody VolunteerTaskReviewDTO reviewDTO) {
        return Result.success(volunteerTaskService.review(id, reviewDTO));
    }

    @Operation(summary = "取消志愿者任务")
    @PutMapping("/{id}/cancel")
    @PreAuthorize("hasAnyAuthority('ADMIN','SUPER_ADMIN')")
    public Result<VolunteerTaskVO> cancel(@PathVariable Long id,
                                          @RequestBody(required = false) VolunteerTaskCancelDTO cancelDTO) {
        return Result.success(volunteerTaskService.cancel(id, cancelDTO));
    }

    @Operation(summary = "删除志愿者任务")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN','SUPER_ADMIN')")
    public Result<Void> delete(@PathVariable Long id) {
        volunteerTaskService.deleteTask(id);
        return Result.success();
    }
}
