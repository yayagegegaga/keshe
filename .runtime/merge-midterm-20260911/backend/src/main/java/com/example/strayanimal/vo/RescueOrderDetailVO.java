package com.example.strayanimal.vo;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class RescueOrderDetailVO extends RescueOrderVO {

    private RescueClueVO clue;

    private UserInfoVO handler;

    private List<RescueOrderLogVO> logs;
}
