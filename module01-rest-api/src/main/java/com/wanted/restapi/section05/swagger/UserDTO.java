package com.wanted.restapi.section05.swagger;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class UserDTO {

    @Schema(description = "사용자 번호", example = "1")
    private int no;
    @Schema(description = "아이디", example = "user01")
    private String id;
    @Schema(description = "비밀번호", example = "pass01")
    private String pwd;
    @Schema(description = "이름", example = "홍길동")
    private String name;
    @Schema(description = "가입일", example = "2024-01-01")
    private Date enrollAt;
}
