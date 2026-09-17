package com.example.strayanimal.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.strayanimal.common.PageResult;
import com.example.strayanimal.dto.FileQueryDTO;
import com.example.strayanimal.entity.FileRecord;
import com.example.strayanimal.vo.FileUploadVO;
import org.springframework.web.multipart.MultipartFile;

public interface FileService extends IService<FileRecord> {
    FileUploadVO upload(MultipartFile file, String bizType, Long bizId);
    PageResult<FileUploadVO> pageFiles(FileQueryDTO queryDTO);
    PageResult<FileUploadVO> pageMine(FileQueryDTO queryDTO);
    void deleteFile(Long id);
}
