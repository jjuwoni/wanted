package com.wanted.restapi.section04.hateoas;

import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.Charset;
import java.util.*;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/hateoas")
public class HateoasController {

    // 인메모리 Data
    private List<UserDTO> users;

    public HateoasController() {
        users = new ArrayList<>();

        users.add(new UserDTO(1, "user01", "pass01", "너구리", new Date()));
        users.add(new UserDTO(2, "user02", "pass02", "코알라", new Date()));
        users.add(new UserDTO(3, "user03", "pass03", "호랑이", new Date()));
        users.add(new UserDTO(4, "user04", "pass04", "원숭이", new Date()));
    }

    /* comment.
    *   기존에 조회하던 방식 : Hateoas 가 적용되지 않은 RestAPI 설계 2단계에 해당함.
    *   다만 실무에서도 대부분의 API 는 2단계까지 적용하는 경우가 90% 정도 된다.
    * */
//    @GetMapping("/users/{userNo}")
//    public ResponseEntity<ResponseMessage> findUserByNo(@PathVariable int userNo) {
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(new MediaType("application", "json", Charset.forName("UTF-8")));
//
//        UserDTO foundUser = users.stream()
//                .filter(user -> user.getNo() == userNo)
//                .toList()
//                .get(0);
//        // 응답 바디 설정(담을 값)
//        Map<String, Object> responseMap = new HashMap<>();
//        responseMap.put("user", foundUser);
//
//        // 메서드 체이닝 방식으로 응답(가장 많이 쓰이는 방법)
//        return ResponseEntity
//                .ok()
//                .headers(headers)
//                .body(new ResponseMessage(200, "조회 성공~~", responseMap));
//    }

    /* comment.
    *   2단계 Rest 설계 단계를 넘어 Hateoas 를 적용한 3단계 RestAPI 설계를 구현해보자.
    *   Hateoas 주요 메서드
    *   - linkTo : 컨트롤러 메서드에 대한 링크를 생성
    *   - methodOn : 컨트롤러 메서드를 호출하기 위한 프록시 객체 생성
    *   - withSelfRel : 현재 리소스 자체에 대한 'self' 링크를 추가
    *   - withRel : 관련 된 다른 리소스에 대한 링크를 추가
    *   Hateoas 적용하게 되면 기존 2단계까지는 RestAPI 라고 불리우고
    *   3단계까지 적용하게 되면 RestFulAPI 라고 부른다.
    *   Ful 이라는 것은 충분한 이라는 단어를 의미하며, 단순 API 를 호출할때
    *   그 API 에 대한 정보 뿐만 아니라, 관련 된 리소스를 충분하게 제공하는 것을 의미한다.
    * */
    @GetMapping("/users/{userNo}")
    public ResponseEntity<ResponseMessage> findUserByNo(@PathVariable int userNo) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(new MediaType("application", "json", Charset.forName("UTF-8")));

        UserDTO foundUser = users.stream()
                .filter(user -> user.getNo() == userNo)
                .toList()
                .get(0);

        // EntityModel -> ResponseEntity 응답 포멧 설정 시 링크 관련 응답을 추가하기 위한 클래스
        EntityModel<UserDTO> userWithRel =
                EntityModel.of(
                        foundUser,
                        linkTo(methodOn(HateoasController.class).findUserByNo(foundUser.getNo())).withSelfRel(),
                        linkTo(methodOn(HateoasController.class).findAllUser()).withRel("users")
                );

        // 응답 바디 설정(담을 값)
        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("user", userWithRel);

        // 메서드 체이닝 방식으로 응답(가장 많이 쓰이는 방법)
        return ResponseEntity
                .ok()
                .headers(headers)
                .body(new ResponseMessage(200, "조회 성공~~", responseMap));
    }

    @GetMapping("/users")
    // 응답 시 여러 명의 사람이 개발할 때
    // 공통 응답 템플릿을 만들어두는 것이 좋다.
    public ResponseEntity<ResponseMessage> findAllUser() {

        // 응답 헤더 직접 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(new MediaType("application", "json", Charset.forName("UTF-8")));

        // 응답 바디 설정(담을 값)
        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("users", users);

        ResponseMessage responseMessage = new ResponseMessage(200, "조회 성공!", responseMap);

        return new ResponseEntity<>(responseMessage, headers, HttpStatus.OK);
    }

}
