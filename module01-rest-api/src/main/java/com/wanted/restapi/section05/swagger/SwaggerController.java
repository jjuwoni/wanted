package com.wanted.restapi.section05.swagger;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.nio.charset.Charset;
import java.util.*;

@Tag(name="SWAGGER 테스트  컨트롤러", description = "Section05.User 관련 REST-API")
@RestController
@RequestMapping("/swagger")
public class SwaggerController {

    // DB 역할을 하는 필드
    private List<UserDTO> users;

    public SwaggerController() {
        this.users = new ArrayList<>();
        users.add(new UserDTO(1, "user01", "pass01", "너구리", new Date()));
        users.add(new UserDTO(2, "user02", "pass02", "코알라", new Date()));
        users.add(new UserDTO(3, "user03", "pass03", "호랑이", new Date()));
        users.add(new UserDTO(4, "user04", "pass04", "원숭이", new Date()));
    }

    @Operation(summary = "전체 사용자를 조회", description = "모든 사용자 정보를 조회하는 메서드")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공", content = @Content(schema = @Schema(implementation = ResponseMessage.class))),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류", content = @Content(schema = @Schema(hidden = true)))
    })
    @GetMapping("/users")
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

    @Operation(summary = "특정 사용자 조회", description = "사용자 번호를 통해 사용자 정보 조회한다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공", content = @Content(schema = @Schema(implementation = ResponseMessage.class))),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음", content = @Content(schema = @Schema(hidden = true)))
    })
    @Parameter(name = "userNo", description = "조회할 사용자 번호", required = true)
    @GetMapping("/users/{userNo}")
    public ResponseEntity<ResponseMessage> findUserByNo(@PathVariable int userNo) {

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

    @PostMapping("/users")
    public ResponseEntity<?> registUser(@RequestBody UserDTO newUser) {

        // JSON 타입으로 요청을 받는다.
        System.out.println("newUser = " + newUser);

        // 현재 users 에서 마지막 회원 4번 회원의 no 값 추출
        int lastUserNo = users.get(users.size() - 1).getNo();
        int newUserNo = lastUserNo + 1;

        newUser.setNo(newUserNo);
        newUser.setEnrollAt(new Date());

        users.add(newUser);

        // 1번
//        return ResponseEntity
//                .created(URI.create("/entity/users/" + newUserNo))
//                .body(newUser);

        // 2번
        return ResponseEntity
                .created(URI.create("/entity/users/" + newUserNo))
                .build();
    }

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

    @Operation(summary = "특정 사용자 삭제", description = "특정 사용자를 삭제하는 메서드")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "삭제 성공", content = @Content(schema = @Schema(implementation = ResponseMessage.class))),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음", content = @Content(schema = @Schema(hidden = true)))
    })
    @DeleteMapping("/users/{userNo}/delete")
    public ResponseEntity<?> deleteUser(@PathVariable int userNo) {

        UserDTO foundUser = users.stream()
                .filter(user -> user.getNo() == userNo)
                .toList()
                .get(0);

        users.remove(foundUser);

        return ResponseEntity
                .ok()
                .body(new ResponseMessage(204, "삭제 성공~~", null));
    }
}
