package com.wanted.restapi.section04.hateoas;

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

    private int httpStatus;
    private String message;
    private Map<String, Object> result;

}
