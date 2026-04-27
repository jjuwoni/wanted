package com.wanted.restapi.section02.responseentity;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.nio.charset.Charset;
import java.util.*;

@RestController
@RequestMapping("/entity")
public class ResponseEntityController {

    // DB 역할을 하는 필드
    private List<UserDTO> users;

    public ResponseEntityController() {
        this.users = new ArrayList<>();
        users.add(new UserDTO(1, "user01", "pass01", "너구리", new Date()));
        users.add(new UserDTO(2, "user02", "pass02", "코알라", new Date()));
        users.add(new UserDTO(3, "user03", "pass03", "호랑이", new Date()));
        users.add(new UserDTO(4, "user04", "pass04", "원숭이", new Date()));
    }

    /* comment. ResponseEntity
    *   ResponseEntity 란?
    *   - Spring 에서 HTTP 응답을 보다 정밀하게 제어하기 위해 사용되는 클래스
    *   - 응답 헤더, 상태 코드, 응답 바디 등의 HTTP 응답을 포함할 수 있다.
    * */

    // 유저 전체조회
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

    // 유저 no 로 유저 조회하기
    @GetMapping("/users/{userNo}")
    public ResponseEntity<ResponseMessage> findUserByNo(@PathVariable int userNo) {

        // 응답 헤더 직접 설정
        // 헤더를 직접 조정하는 경우는 별로 없다.
        // 다만, JSON 타입이 아닌 데이터를 응답할 때는 직접 조정할 필요가 있기 때문에
        // 현재는 직접 명시하고 있다. ex) 이미지 응답, JSON 외 다른 데이터 응답
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(new MediaType("application", "json", Charset.forName("UTF-8")));

        UserDTO foundUser = users.stream()
                                 .filter(user -> user.getNo() == userNo)
                                 .toList()
                                 .get(0);
        // 응답 바디 설정(담을 값)
        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("user", foundUser);

        // 메서드 체이닝 방식으로 응답(가장 많이 쓰이는 방법)
        return ResponseEntity
                .ok()
                .headers(headers)
                .body(new ResponseMessage(200, "조회 성공~~", responseMap));
    }

    // 신규 유저 등록하기
    @PostMapping("/users")
    // @RequestBody 는 JSON 타입의 데이터를 응답 받을 때 사용한다.
    // 또한 Jackson 라이브러리가 기본적으로 동작하기 때문에
    // 넘어오는 데이터의 key 와 담을 DTO 의 변수명을 맞춰주는 것이 중요하다.
    public ResponseEntity<?> registUser(@RequestBody UserDTO newUser) {

        // JSON 타입으로 요청을 받는다.
        System.out.println("newUser = " + newUser);

        // 현재 users 에서 마지막 회원 4번 회원의 no 값 추출
        int lastUserNo = users.get(users.size() - 1).getNo();
        int newUserNo = lastUserNo + 1;

        newUser.setNo(newUserNo);
        newUser.setEnrollAt(new Date());

        users.add(newUser);

        /* comment.
        *   POST, PUT, DELETE 의 경우는 응답 Body 에 값을 넣을 수도,
        *   안 넣을 수도 있다.
        *   이럴 때는 ResponseEntity<?> 와일드 카드를 정의해서 유연하게
        *   대응하는 것이 좋다.
        *   1. 리소스를 반환하는 경우
        *   2. 리소스를 반환하지 않는 경우
        * */

        // 1번
//        return ResponseEntity
//                .created(URI.create("/entity/users/" + newUserNo))
//                .body(newUser);

        // 2번
        return ResponseEntity
                .created(URI.create("/entity/users/" + newUserNo))
                .build();
    }

    // 유저 정보 수정
    @PutMapping("/users/{userNo}")
    public ResponseEntity<?> modifyUser(@RequestBody UserDTO modifyData, @PathVariable int userNo) {

        // 수정 할 1명의 데이터를 userNo로 찾아오기
        UserDTO foundUser = users.stream()
                .filter(user -> user.getNo() == userNo)
                .toList()
                .get(0);

        foundUser.setId(modifyData.getId());
        foundUser.setPwd(modifyData.getPwd());
        foundUser.setName(modifyData.getName());

        // 수정도 새로운 리소스가 만들어진 것이다.
        return ResponseEntity
                .created(URI.create("/entity/users/" + userNo))
                .build();
    }

}
