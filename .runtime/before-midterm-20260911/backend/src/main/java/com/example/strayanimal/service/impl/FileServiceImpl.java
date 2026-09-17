package com.example.strayanimal.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.strayanimal.common.PageResult;
import com.example.strayanimal.dto.FileQueryDTO;
import com.example.strayanimal.entity.FileRecord;
import com.example.strayanimal.enums.FileBizType;
import com.example.strayanimal.exception.BusinessException;
import com.example.strayanimal.exception.ErrorCode;
import com.example.strayanimal.mapper.FileRecordMapper;
import com.example.strayanimal.security.SecurityUser;
import com.example.strayanimal.security.UserContext;
import com.example.strayanimal.service.FileService;
import com.example.strayanimal.vo.FileUploadVO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Set;
import java.util.UUID;

@Service
public class FileServiceImpl extends ServiceImpl<FileRecordMapper, FileRecord> implements FileService {

    private static final Set<String> ALLOWED_EXTENSIONS = Set.of("jpg", "jpeg", "png", "gif", "webp", "pdf");

    @Value("${file.upload-dir:uploads}")
    private String uploadDir;

    @Value("${file.access-prefix:/uploads}")
    private String accessPrefix;

    @Value("${file.max-size:10485760}")
    private long maxSize;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public FileUploadVO upload(MultipartFile file, String bizType, Long bizId) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "上传文件不能为空");
        }
        if (!FileBizType.contains(bizType)) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "文件业务类型不合法");
        }
        if (file.getSize() > maxSize) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "文件大小超过限制");
        }
        String originalName = sanitizeOriginalName(file.getOriginalFilename());
        String extension = extension(originalName);
        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "文件类型不合法");
        }
        LocalDate today = LocalDate.now();
        String datePath = today.format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        String storedName = UUID.randomUUID().toString().replace("-", "") + "." + extension;
        Path baseDir = Path.of(uploadDir).toAbsolutePath().normalize();
        Path dir = baseDir.resolve(datePath).normalize();
        Path target = dir.resolve(storedName).toAbsolutePath().normalize();
        if (!target.startsWith(baseDir)) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "文件路径不合法");
        }
        try {
            Files.createDirectories(dir);
            file.transferTo(target);
        } catch (IOException ex) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "文件保存失败");
        }
        String normalizedPrefix = accessPrefix.endsWith("/") ? accessPrefix.substring(0, accessPrefix.length() - 1) : accessPrefix;
        String fileUrl = normalizedPrefix + "/" + datePath.replace("\\", "/") + "/" + storedName;

        FileRecord record = new FileRecord();
        record.setOriginalName(originalName);
        record.setStoredName(storedName);
        record.setFileUrl(fileUrl);
        record.setFilePath(target.toString());
        record.setFileType(bizType);
        record.setBizId(bizId);
        record.setContentType(file.getContentType());
        record.setFileSize(file.getSize());
        record.setUploaderId(UserContext.currentUserId());
        record.setCreateTime(LocalDateTime.now());
        save(record);
        return toVO(record);
    }

    @Override
    public PageResult<FileUploadVO> pageFiles(FileQueryDTO queryDTO) {
        validateQuery(queryDTO);
        long pageNum = queryDTO.getPageNum() == null || queryDTO.getPageNum() < 1 ? 1 : queryDTO.getPageNum();
        long pageSize = queryDTO.getPageSize() == null || queryDTO.getPageSize() < 1 ? 10 : Math.min(queryDTO.getPageSize(), 100);
        Page<FileRecord> page = page(new Page<>(pageNum, pageSize), baseQuery(queryDTO)
                .orderByDesc(FileRecord::getCreateTime));
        return PageResult.of(page.getRecords().stream().map(this::toVO).toList(), page.getTotal(), page.getCurrent(), page.getSize());
    }

    @Override
    public PageResult<FileUploadVO> pageMine(FileQueryDTO queryDTO) {
        validateQuery(queryDTO);
        long pageNum = queryDTO.getPageNum() == null || queryDTO.getPageNum() < 1 ? 1 : queryDTO.getPageNum();
        long pageSize = queryDTO.getPageSize() == null || queryDTO.getPageSize() < 1 ? 10 : Math.min(queryDTO.getPageSize(), 100);
        Page<FileRecord> page = page(new Page<>(pageNum, pageSize), baseQuery(queryDTO)
                .eq(FileRecord::getUploaderId, UserContext.currentUserId())
                .orderByDesc(FileRecord::getCreateTime));
        return PageResult.of(page.getRecords().stream().map(this::toVO).toList(), page.getTotal(), page.getCurrent(), page.getSize());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteFile(Long id) {
        FileRecord record = getById(id);
        if (record == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "文件记录不存在");
        }
        ensureCanDelete(record);
        try {
            Files.deleteIfExists(Path.of(record.getFilePath()));
        } catch (IOException ex) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "本地文件删除失败");
        }
        removeById(id);
    }

    private LambdaQueryWrapper<FileRecord> baseQuery(FileQueryDTO queryDTO) {
        return new LambdaQueryWrapper<FileRecord>()
                .eq(StringUtils.isNotBlank(queryDTO.getFileType()), FileRecord::getFileType, queryDTO.getFileType())
                .eq(queryDTO.getBizId() != null, FileRecord::getBizId, queryDTO.getBizId())
                .eq(queryDTO.getUploaderId() != null, FileRecord::getUploaderId, queryDTO.getUploaderId())
                .like(StringUtils.isNotBlank(queryDTO.getKeyword()), FileRecord::getOriginalName, queryDTO.getKeyword());
    }

    private void ensureCanDelete(FileRecord record) {
        SecurityUser user = UserContext.currentUser();
        if (user.getRoles().contains("ADMIN") || user.getRoles().contains("SUPER_ADMIN")) {
            return;
        }
        if (record.getUploaderId().equals(user.getUserId())) {
            return;
        }
        throw new BusinessException(ErrorCode.FORBIDDEN, "不能删除他人上传的文件");
    }

    private String sanitizeOriginalName(String originalFilename) {
        String name = originalFilename == null ? "unknown" : Path.of(originalFilename).getFileName().toString();
        return name.replace("\\", "").replace("/", "");
    }

    private String extension(String filename) {
        int index = filename.lastIndexOf('.');
        if (index < 0 || index == filename.length() - 1) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "文件后缀不能为空");
        }
        return filename.substring(index + 1).toLowerCase();
    }

    private void validateQuery(FileQueryDTO queryDTO) {
        if (StringUtils.isNotBlank(queryDTO.getFileType()) && !FileBizType.contains(queryDTO.getFileType())) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "文件业务类型不合法");
        }
    }

    private FileUploadVO toVO(FileRecord record) {
        FileUploadVO vo = new FileUploadVO();
        vo.setFileId(record.getId());
        vo.setOriginalName(record.getOriginalName());
        vo.setStoredName(record.getStoredName());
        vo.setFileUrl(record.getFileUrl());
        vo.setFileType(record.getFileType());
        vo.setBizId(record.getBizId());
        vo.setContentType(record.getContentType());
        vo.setFileSize(record.getFileSize());
        vo.setUploaderId(record.getUploaderId());
        vo.setCreateTime(record.getCreateTime());
        return vo;
    }
}
