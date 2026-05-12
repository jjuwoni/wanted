package com.ohgiraffers.jwtrestapi.member.dto;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class MemberRoleDTO {

    private int memberNo;
    /* 설명. 엔티티를 작성하고 복합키 설정에 용이하기 위함이자 MemberRole INSERT 및 UPDATE 상황에서 필수! */
    private int authorityCode;

    private AuthorityDTO authority;

}
