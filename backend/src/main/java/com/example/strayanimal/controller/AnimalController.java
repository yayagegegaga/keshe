package com.example.strayanimal.controller;

import com.example.strayanimal.common.PageResult;
import com.example.strayanimal.common.Result;
import com.example.strayanimal.dto.AnimalCreateDTO;
import com.example.strayanimal.dto.AnimalHealthRecordCreateDTO;
import com.example.strayanimal.dto.AnimalImageCreateDTO;
import com.example.strayanimal.dto.AnimalQueryDTO;
import com.example.strayanimal.dto.AnimalStatusUpdateDTO;
import com.example.strayanimal.dto.AnimalUpdateDTO;
import com.example.strayanimal.service.AnimalHealthRecordService;
import com.example.strayanimal.service.AnimalImageService;
import com.example.strayanimal.service.AnimalService;
import com.example.strayanimal.vo.AnimalDetailVO;
import com.example.strayanimal.vo.AnimalHealthRecordVO;
import com.example.strayanimal.vo.AnimalImageVO;
import com.example.strayanimal.vo.AnimalVO;
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

@Tag(name = "动物档案接口")
@RestController
@RequestMapping("/api/animals")
@RequiredArgsConstructor
public class AnimalController {

    private final AnimalService animalService;

    private final AnimalImageService animalImageService;

    private final AnimalHealthRecordService animalHealthRecordService;

    @Operation(summary = "分页查询动物列表")
    @GetMapping
    public Result<PageResult<AnimalVO>> page(@ModelAttribute AnimalQueryDTO queryDTO) {
        return Result.success(animalService.pageAnimals(queryDTO));
    }

    @Operation(summary = "查询动物详情")
    @GetMapping("/{id}")
    public Result<AnimalDetailVO> detail(@PathVariable Long id) {
        return Result.success(animalService.detail(id));
    }

    @Operation(summary = "新增动物档案")
    @PostMapping
    @PreAuthorize("hasAnyAuthority('ADMIN','SUPER_ADMIN')")
    public Result<AnimalVO> create(@Valid @RequestBody AnimalCreateDTO createDTO) {
        return Result.success(animalService.create(createDTO));
    }

    @Operation(summary = "修改动物档案")
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN','SUPER_ADMIN')")
    public Result<AnimalVO> update(@PathVariable Long id, @Valid @RequestBody AnimalUpdateDTO updateDTO) {
        return Result.success(animalService.update(id, updateDTO));
    }

    @Operation(summary = "修改动物状态")
    @PutMapping("/{id}/status")
    @PreAuthorize("hasAnyAuthority('ADMIN','SUPER_ADMIN')")
    public Result<AnimalVO> updateStatus(@PathVariable Long id,
                                         @Valid @RequestBody AnimalStatusUpdateDTO statusUpdateDTO) {
        return Result.success(animalService.updateStatus(id, statusUpdateDTO));
    }

    @Operation(summary = "删除动物档案")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN','SUPER_ADMIN')")
    public Result<Void> delete(@PathVariable Long id) {
        animalService.deleteAnimal(id);
        return Result.success();
    }

    @Operation(summary = "新增动物图片")
    @PostMapping("/{id}/images")
    @PreAuthorize("hasAnyAuthority('ADMIN','SUPER_ADMIN')")
    public Result<AnimalImageVO> addImage(@PathVariable Long id,
                                          @Valid @RequestBody AnimalImageCreateDTO createDTO) {
        return Result.success(animalImageService.create(id, createDTO));
    }

    @Operation(summary = "新增健康记录")
    @PostMapping("/{id}/health-records")
    @PreAuthorize("hasAnyAuthority('ADMIN','SUPER_ADMIN')")
    public Result<AnimalHealthRecordVO> addHealthRecord(@PathVariable Long id,
                                                        @Valid @RequestBody AnimalHealthRecordCreateDTO createDTO) {
        return Result.success(animalHealthRecordService.create(id, createDTO));
    }
}
