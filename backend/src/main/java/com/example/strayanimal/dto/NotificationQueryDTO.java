package com.example.strayanimal.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NotificationQueryDTO {
    private Long pageNum = 1L;
    private Long pageSize = 10L;
    private Integer isRead;
    private String type;
}
