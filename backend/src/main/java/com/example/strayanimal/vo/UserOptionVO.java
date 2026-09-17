package com.example.strayanimal.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 下拉选择用的用户简要信息。
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserOptionVO {

    private Long id;

    private String username;

    private String nickname;

    private String phone;
}
