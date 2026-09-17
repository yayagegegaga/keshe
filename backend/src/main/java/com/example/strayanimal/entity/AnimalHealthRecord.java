package com.example.strayanimal.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@TableName("animal_health_record")
public class AnimalHealthRecord {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long animalId;

    private String healthStatus;

    private String hospital;

    private String treatmentContent;

    private LocalDateTime recordTime;

    private String remark;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
