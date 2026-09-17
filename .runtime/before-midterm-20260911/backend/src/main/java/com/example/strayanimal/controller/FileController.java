package com.example.strayanimal.controller;

import com.example.strayanimal.common.PageResult;
import com.example.strayanimal.common.Result;
import com.example.strayanimal.dto.FileQueryDTO;
import com.example.strayanimal.service.FileService;
import com.example.strayanimal.vo.FileUploadVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "文件上传接口")
@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;

    @Operation(summary = "上传文件")
    @PostMapping("/upload")
    public Result<FileUploadVO> upload(@RequestParam("file") MultipartFile file,
                                       @RequestParam("biz_type") String bizType,
                                       @RequestParam(value = "biz_id", required = false) Long bizId) {
        return Result.success(fileService.upload(file, bizType, bizId));
    }

    @Operation(summary = "分页查询文件记录")
    @GetMapping
    @PreAuthorize("hasAnyAuthority('ADMIN','SUPER_ADMIN')")
    public Result<PageResult<FileUploadVO>> page(@ModelAttribute FileQueryDTO queryDTO) {
        return Result.success(fileService.pageFiles(queryDTO));
    }

    @Operation(summary = "查询我的文件记录")
    @GetMapping("/my")
    public Result<PageResult<FileUploadVO>> my(@ModelAttribute FileQueryDTO queryDTO) {
        return Result.success(fileService.pageMine(queryDTO));
    }

    @Operation(summary = "删除文件")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        fileService.deleteFile(id);
        return Result.success();
    }
}
