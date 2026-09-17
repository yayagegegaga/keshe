package com.example.strayanimal.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@TableName("file_record")
public class FileRecord {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String originalName;
    private String storedName;
    private String fileUrl;
    private String filePath;
    private String fileType;
    private Long bizId;
    private String contentType;
    private Long fileSize;
    private Long uploaderId;
    private LocalDateTime createTime;
}
