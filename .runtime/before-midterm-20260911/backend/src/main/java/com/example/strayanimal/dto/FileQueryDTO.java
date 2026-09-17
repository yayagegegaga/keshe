package com.example.strayanimal.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FileQueryDTO {
    private Long pageNum = 1L;
    private Long pageSize = 10L;
    private String fileType;
    private Long bizId;
    private Long uploaderId;
    private String keyword;
}
