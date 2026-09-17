package com.example.strayanimal.vo;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class FileUploadVO {
    private Long fileId;
    private String originalName;
    private String storedName;
    private String fileUrl;
    private String fileType;
    private Long bizId;
    private String contentType;
    private Long fileSize;
    private Long uploaderId;
    private LocalDateTime createTime;
}
