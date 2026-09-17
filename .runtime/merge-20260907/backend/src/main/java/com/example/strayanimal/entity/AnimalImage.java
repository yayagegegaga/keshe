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
@TableName("animal_image")
public class AnimalImage {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long animalId;

    private String imageUrl;

    private String imageType;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
