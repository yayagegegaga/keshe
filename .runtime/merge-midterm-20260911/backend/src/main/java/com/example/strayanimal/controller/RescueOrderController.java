package com.example.strayanimal.controller;

import com.example.strayanimal.common.PageResult;
import com.example.strayanimal.common.Result;
import com.example.strayanimal.dto.RescueOrderAssignDTO;
import com.example.strayanimal.dto.RescueOrderCancelDTO;
import com.example.strayanimal.dto.RescueOrderCloseDTO;
import com.example.strayanimal.dto.RescueOrderCreateDTO;
import com.example.strayanimal.dto.RescueOrderFinishDTO;
import com.example.strayanimal.dto.RescueOrderQueryDTO;
import com.example.strayanimal.service.RescueOrderService;
import com.example.strayanimal.vo.RescueOrderDetailVO;
import com.example.strayanimal.vo.RescueOrderLogVO;
import com.example.strayanimal.vo.RescueOrderVO;
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

import java.util.List;

@Tag(name = "救助工单接口")
@RestController
@RequestMapping("/api/rescue-orders")
@RequiredArgsConstructor
public class RescueOrderController {

    private final RescueOrderService rescueOrderService;

    @Operation(summary = "创建救助工单")
    @PostMapping
    @PreAuthorize("hasAnyAuthority('ADMIN','SUPER_ADMIN')")
    public Result<RescueOrderVO> create(@Valid @RequestBody RescueOrderCreateDTO createDTO) {
        return Result.success(rescueOrderService.create(createDTO));
    }

    @Operation(summary = "分页查询救助工单")
    @GetMapping
    @PreAuthorize("hasAnyAuthority('ADMIN','SUPER_ADMIN','VOLUNTEER')")
    public Result<PageResult<RescueOrderVO>> page(@ModelAttribute RescueOrderQueryDTO queryDTO) {
        return Result.success(rescueOrderService.pageOrders(queryDTO));
    }

    @Operation(summary = "查询救助工单详情")
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN','SUPER_ADMIN','VOLUNTEER')")
    public Result<RescueOrderDetailVO> detail(@PathVariable Long id) {
        return Result.success(rescueOrderService.detail(id));
    }

    @Operation(summary = "分配救助工单")
    @PutMapping("/{id}/assign")
    @PreAuthorize("hasAnyAuthority('ADMIN','SUPER_ADMIN')")
    public Result<RescueOrderVO> assign(@PathVariable Long id, @Valid @RequestBody RescueOrderAssignDTO assignDTO) {
        return Result.success(rescueOrderService.assign(id, assignDTO));
    }

    @Operation(summary = "开始处理工单")
    @PutMapping("/{id}/start")
    @PreAuthorize("hasAnyAuthority('ADMIN','SUPER_ADMIN','VOLUNTEER')")
    public Result<RescueOrderVO> start(@PathVariable Long id) {
        return Result.success(rescueOrderService.start(id));
    }

    @Operation(summary = "提交工单完成结果")
    @PutMapping("/{id}/finish")
    @PreAuthorize("hasAnyAuthority('ADMIN','SUPER_ADMIN','VOLUNTEER')")
    public Result<RescueOrderVO> finish(@PathVariable Long id, @Valid @RequestBody RescueOrderFinishDTO finishDTO) {
        return Result.success(rescueOrderService.finish(id, finishDTO));
    }

    @Operation(summary = "关闭工单")
    @PutMapping("/{id}/close")
    @PreAuthorize("hasAnyAuthority('ADMIN','SUPER_ADMIN')")
    public Result<RescueOrderVO> close(@PathVariable Long id, @RequestBody(required = false) RescueOrderCloseDTO closeDTO) {
        return Result.success(rescueOrderService.close(id, closeDTO));
    }

    @Operation(summary = "取消工单")
    @PutMapping("/{id}/cancel")
    @PreAuthorize("hasAnyAuthority('ADMIN','SUPER_ADMIN')")
    public Result<RescueOrderVO> cancel(@PathVariable Long id, @RequestBody(required = false) RescueOrderCancelDTO cancelDTO) {
        return Result.success(rescueOrderService.cancel(id, cancelDTO));
    }

    @Operation(summary = "查询工单流转日志")
    @GetMapping("/{id}/logs")
    @PreAuthorize("hasAnyAuthority('ADMIN','SUPER_ADMIN','VOLUNTEER')")
    public Result<List<RescueOrderLogVO>> logs(@PathVariable Long id) {
        return Result.success(rescueOrderService.logs(id));
    }

    // ===== 删除工单（只保留这一个） =====
    @Operation(summary = "删除工单")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN','SUPER_ADMIN')")
    public Result<Void> delete(@PathVariable Long id) {
        rescueOrderService.deleteOrder(id);
        return Result.success();
    }
}