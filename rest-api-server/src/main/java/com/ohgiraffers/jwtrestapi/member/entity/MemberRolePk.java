package com.ohgiraffers.jwtrestapi.member.entity;

import lombok.*;

import java.io.Serializable;

/* 설명. 복합키 타입을 정의할 때는 Serializable을 반드시 구현 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class MemberRolePk implements Serializable {

    private int memberNo;
    private int authorityCode;

}

