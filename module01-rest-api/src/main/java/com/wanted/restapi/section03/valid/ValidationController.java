package com.wanted.restapi.section03.valid;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequestMapping("/valid")
public class ValidationController {
    /* comment.
    *   Valid -> 검증
    *   - 유효성 검사() ex) 비밀번호는 8글자 이상, 특수문자 포함 등
    *   - 유효성 검사의 경우 프론트 단에서 조건에 만족하지 못하면
    *   - 버튼을 비활성화 or required 속성을 통해서 반드시 입력하게
    *   - 만들 수 있지만, 개발자 도구 혹은 URL 을 통한 침투를 하게 되면
    *   - 유효하지 못한 값이 백엔드 서버에 넘어올 수 있다.
    *   - 따라서 백엔드에서도 @Valid 를 통해서 유효하지 않은 값을
    *   - 2차적으로 방어해주어야 한다.
    * */
    
    @PostMapping("/users")
    // @NotNull 이런 식의 Valid 어노테이션을 활성화 시키기 위해서는
    // @Valid 어노테이션을 명시해야 동작한다.
    public ResponseEntity<?> registUser(@Valid @RequestBody UserDTO user) {

        System.out.println("user = " + user);
        
        return ResponseEntity.created(URI.create("/entity/users/" + "userNo"))
                .build();
    }
    
}
