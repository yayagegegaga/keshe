package com.example.strayanimal.vo;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class TaskRecordVO {
    private Long id;
    private Long taskId;
    private Long volunteerId;
    private String content;
    private String imageUrl;
    private LocalDateTime createTime;
}
