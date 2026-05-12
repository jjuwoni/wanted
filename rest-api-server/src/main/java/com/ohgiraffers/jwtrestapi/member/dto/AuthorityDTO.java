package com.ohgiraffers.jwtrestapi.member.dto;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class AuthorityDTO {

    private int code;
    private String name;
    private String desc;

}
