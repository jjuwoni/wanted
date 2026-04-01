package com.wanted.aop.section02;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class MemberDTO {

    private String email;
    private String password;
    private String name;
    private String phone;
    private String role;
}
