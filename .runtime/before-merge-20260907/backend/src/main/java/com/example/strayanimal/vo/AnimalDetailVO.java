package com.example.strayanimal.vo;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class AnimalDetailVO extends AnimalVO {

    private List<AnimalImageVO> images;

    private List<AnimalHealthRecordVO> healthRecords;
}
