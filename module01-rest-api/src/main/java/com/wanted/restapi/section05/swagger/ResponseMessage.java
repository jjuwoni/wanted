package com.wanted.restapi.section05.swagger;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.Map;

// 해당 클래스는 응답 템플릿이며
// 여러 명의 사람들이 각기 다른 형태의 응답을 하는 것이 아닌
// 공통 표준의 응답을 하기 위해 만들어두는 클래스이다.
// ex) 이슈템플릿, PR 템플릿 등
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class ResponseMessage {

    @Schema(description = "HTTP 응답 상태코드", example = "200")
    private int httpStatus;
    @Schema(description = "HTTP 응답 메세지", example = "성공")
    private String message;
    // 문자열을 표기하는 "" 내부에 ""를 사용하게 되면 컴파일 에러가 발생한다.
    // 이럴때는 \ 역슬래쉬(이스케이프 문자) 를 사용해서 단순 문자열값으로 판단하게 해야한다.
    @Schema(description = "HTTP 응답 데이터", example = "{ \"id\" : 1, \"name\" : \"홍길동\" }")
    private Map<String, Object> result;

}
