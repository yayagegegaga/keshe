package com.example.strayanimal.controller;

import com.example.strayanimal.common.PageResult;
import com.example.strayanimal.common.Result;
import com.example.strayanimal.dto.AdoptionApplyDTO;
import com.example.strayanimal.dto.AdoptionCancelDTO;
import com.example.strayanimal.dto.AdoptionQueryDTO;
import com.example.strayanimal.dto.AdoptionReviewDTO;
import com.example.strayanimal.service.AdoptionService;
import com.example.strayanimal.vo.AdoptionApplicationDetailVO;
import com.example.strayanimal.vo.AdoptionApplicationVO;
import com.example.strayanimal.vo.AdoptionReviewLogVO;
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

import java.util.List;

@Tag(name = "领养申请接口")
@RestController
@RequestMapping("/api/adoptions")
@RequiredArgsConstructor
public class AdoptionController {

    private final AdoptionService adoptionService;

    @Operation(summary = "提交领养申请")
    @PostMapping
    public Result<AdoptionApplicationVO> apply(@Valid @RequestBody AdoptionApplyDTO applyDTO) {
        return Result.success(adoptionService.apply(applyDTO));
    }

    @Operation(summary = "分页查询全部领养申请")
    @GetMapping
    @PreAuthorize("hasAnyAuthority('ADMIN','SUPER_ADMIN')")
    public Result<PageResult<AdoptionApplicationVO>> page(@ModelAttribute AdoptionQueryDTO queryDTO) {
        return Result.success(adoptionService.pageApplications(queryDTO));
    }

    @Operation(summary = "查询我的领养申请")
    @GetMapping("/my")
    public Result<PageResult<AdoptionApplicationVO>> my(@ModelAttribute AdoptionQueryDTO queryDTO) {
        return Result.success(adoptionService.pageMine(queryDTO));
    }

    @Operation(summary = "查询领养申请详情")
    @GetMapping("/{id}")
    public Result<AdoptionApplicationDetailVO> detail(@PathVariable Long id) {
        return Result.success(adoptionService.detail(id));
    }

    @Operation(summary = "初审通过")
    @PutMapping("/{id}/first-approve")
    @PreAuthorize("hasAnyAuthority('ADMIN','SUPER_ADMIN')")
    public Result<AdoptionApplicationVO> firstApprove(@PathVariable Long id,
                                                      @RequestBody(required = false) AdoptionReviewDTO reviewDTO) {
        return Result.success(adoptionService.firstApprove(id, reviewDTO));
    }

    @Operation(summary = "进入面谈")
    @PutMapping("/{id}/interview")
    @PreAuthorize("hasAnyAuthority('ADMIN','SUPER_ADMIN')")
    public Result<AdoptionApplicationVO> interview(@PathVariable Long id,
                                                   @RequestBody(required = false) AdoptionReviewDTO reviewDTO) {
        return Result.success(adoptionService.interview(id, reviewDTO));
    }

    @Operation(summary = "进入试养")
    @PutMapping("/{id}/trial")
    @PreAuthorize("hasAnyAuthority('ADMIN','SUPER_ADMIN')")
    public Result<AdoptionApplicationVO> trial(@PathVariable Long id,
                                               @RequestBody(required = false) AdoptionReviewDTO reviewDTO) {
        return Result.success(adoptionService.trial(id, reviewDTO));
    }

    @Operation(summary = "确认领养成功")
    @PutMapping("/{id}/success")
    @PreAuthorize("hasAnyAuthority('ADMIN','SUPER_ADMIN')")
    public Result<AdoptionApplicationVO> success(@PathVariable Long id,
                                                 @RequestBody(required = false) AdoptionReviewDTO reviewDTO) {
        return Result.success(adoptionService.success(id, reviewDTO));
    }

    @Operation(summary = "拒绝领养申请")
    @PutMapping("/{id}/reject")
    @PreAuthorize("hasAnyAuthority('ADMIN','SUPER_ADMIN')")
    public Result<AdoptionApplicationVO> reject(@PathVariable Long id,
                                                @RequestBody(required = false) AdoptionReviewDTO reviewDTO) {
        return Result.success(adoptionService.reject(id, reviewDTO));
    }

    @Operation(summary = "标记试养失败")
    @PutMapping("/{id}/trial-failed")
    @PreAuthorize("hasAnyAuthority('ADMIN','SUPER_ADMIN')")
    public Result<AdoptionApplicationVO> trialFailed(@PathVariable Long id,
                                                     @RequestBody(required = false) AdoptionReviewDTO reviewDTO) {
        return Result.success(adoptionService.trialFailed(id, reviewDTO));
    }

    @Operation(summary = "取消领养申请")
    @PutMapping("/{id}/cancel")
    public Result<AdoptionApplicationVO> cancel(@PathVariable Long id,
                                                @RequestBody(required = false) AdoptionCancelDTO cancelDTO) {
        return Result.success(adoptionService.cancel(id, cancelDTO));
    }

    @Operation(summary = "查询领养审核日志")
    @GetMapping("/{id}/logs")
    public Result<List<AdoptionReviewLogVO>> logs(@PathVariable Long id) {
        return Result.success(adoptionService.logs(id));
    }
}
