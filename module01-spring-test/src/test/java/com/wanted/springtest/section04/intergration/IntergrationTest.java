package com.wanted.springtest.section04.intergration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/* comment.
*   @SpringBootTest
*   - Spring 의 전체 컨텍스트를 로딩한다.
*   - 즉, IoC 컨테이너를 로딩한다.
*   - 통합테스트 시에 사용을 하게 된다.
* */

@SpringBootTest
public class IntergrationTest {

    private UserService userService;
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        // 실제로는 나중에 @Autowired 로 가져오면 된다.
        // 지금은 학습 편의상 Bean 으로 등록하지 않았기 때문에
        // mock 객체로 만든다.
        userRepository = mock(UserRepository.class);

        // userService 에 Mock 주입
        userService = new UserService(userRepository);
    }

    @Test
    void 이메일_중복_시_예외_발생_테스트() {

        // given : 중복 이메일 상황 설정
        User newUser = new User("김철수", "test@example.com");
        // 해당 이메일이 존재한다는 상황 강제화
        when(userRepository.existsByEmail("test@example.com")).thenReturn(true);

        // Service 에서 Throw 한 예외 발생하는 지 Test
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> userService.createUser(newUser)
        );

        assertEquals("이미 존재하는 이메일입니다: test@example.com", exception.getMessage());
    }

    @Test
    void 유효한_ID로_사용자_조회_테스트() {

        // given : 사용자 데이터 설정
        Long userId = 1L;
        User expectUser = new User(userId, "라쿤", "raccoon@test.com", true);

        // expect
        when(userRepository.findById(userId)).thenReturn(Optional.of(expectUser));

        // when
        Optional<User> actualUser = userService.findUserById(userId);

        // then
        assertEquals("라쿤", actualUser.get().getName());
        assertEquals("raccoon@test.com", actualUser.get().getEmail());

            //주언이 다리는 섹끈해요~ 털도 맛있구요~ 복숭아 뼈도 달콤해요~ 각선미가 끝내주는 주원이 다리는 도깨비 다리~
    }

    @Test
    void 유효하지_않은_ID_조회_시_비어있는_Optional_반환_테스트() {

        // given : 유효하지 않은 사용자 데이터 설정
        Long invalidId = 999L;

        // empty() : 비어있는 의미
        when(userRepository.findById(invalidId)).thenReturn(Optional.empty());

        // when
        Optional<User> result = userService.findUserById(invalidId);

        // then : 비어있는 지 검증
        // isPresent() : 존재
        assertFalse(result.isPresent());

    }

    @Test
    void 존재하지_않는_사용자_비활성화_시_예외_발생_테스트() {

        // 존재하지 않는 사용자
        Long nonExistId = 999L;
        when(userRepository.findById(nonExistId)).thenReturn(Optional.empty());

        // when
        IllegalArgumentException exception = assertThrows(
                // 기대하는 예외 클래스 타입
                IllegalArgumentException.class,
                // 예외 발생 메서드
                () -> userService.deactivateUser(nonExistId)
        );

        // then : 검증
        assertEquals("사용자를 찾을 수 없습니다: " + nonExistId, exception.getMessage());
    }

}
