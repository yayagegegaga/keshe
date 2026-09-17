package com.example.strayanimal.vo;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class VolunteerTaskDetailVO extends VolunteerTaskVO {
    private UserInfoVO publisher;
    private UserInfoVO volunteer;
    private List<TaskRecordVO> records;
}
