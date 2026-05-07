package com.wanted.springtest.section03.mockito;

/* comment.
*   Mockito : Test 용 객체를 만드는 것에 도움을 주는 라이브러리
*   - 단위 테스트의 본질은 Spring 전체를 테스트 하는 것이 아닌
*   - 일부 Bean 을 불러와서 테스트 대상 메서드를 실행하는 것이다.
*   - 따라서 Spring Context 를 전체 로드하는 것은 리소스를 많이 낭비하는
*   - 행위이다. Mockito 는 Spring Context 를 전체 로드하는 것이 아닌
*   - 필요한 Bean 만 Mock(가짜) 로 생성해서 불필요한 리소스 낭비를 방지한다.
* */

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

// Mockito 확장 기능을 활성화
@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {

    /* comment.
    *   @Mock : Mock 객체 생성
    *   @InjectMocks : Mock 객체를 주입받는 테스트 대상 생성
    * */

    // @Mock 을 활용해서 실제 구현체 없이도 테스트에서 동작을 정의할 수 있다.
    @Mock
    private PaymentService paymentService;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private OrderService orderService;

    @BeforeEach
    @DisplayName("테스트 환경 Mock 주입 테스트")
    void setUp() {
        // Mock 객체가 정상적으로 생성되었는지 확인
        assertNotNull(orderService);
        assertNotNull(paymentService);
        assertNotNull(notificationService);
    }

    @Test
    void 결제_성공_시_주문_처리가_성공_테스트() {

        // given : 결제 서비스가 성공을 반환
        Long userId = 1L;
        String email = "test@example.com";
        double amount = 100.0;

        /* comment.
        *   현재 테스트 대상은 OrderService 이다.
        *   PaymentService 는 부가적인 요소이며
        *   OrderService 에 영향을 주면 안된다.
        *   - when().thenReturn()
        *   - when 내부에 메서드 호출 시 thenReturn 반환값을 지정할 수 있다.
        * */
        when(paymentService.processPayment(userId, amount)).thenReturn(true);

        // when : 결제가 성공했으며, 주문 처리를 진행
        OrderService.OrderResult result = orderService.processOrder(userId, email, amount);

        // then : 결과 검증
        assertTrue(result.isSuccess());
        assertEquals("주문이 성공적으로 처리되었습니다.", result.getMessage());
        assertEquals(amount, result.getAmount());

    }

    @Test
    void 결제_성공_시_이메일_발송_테스트() {

        // given : 결제 서비스가 성공을 반환
        Long userId = 1L;
        String email = "test@example.com";
        double amount = 100.0;

        // 결제 서비스 성공 강제화
        when(paymentService.processPayment(userId, amount)).thenReturn(true);

        // when : 주문 처리
        orderService.processOrder(userId, email, amount);

        // then : 이메일 발송 메서드가 호출되었는 지 검증
        // 결제 -> 주문 -> 이메일
        // verify() : 메서드 호출 여부를 검증
        // ArgumentMatchers : 파라미터 매칭을 확인하는 도구
        verify(notificationService).sendEmail(eq(email), anyString());
    }

    @Test
    void 결제_실패_시_이메일_발송_테스트() {

        // given : 결제 서비스가 실패를 반환
        Long userId = 1L;
        String email = "test@example.com";
        double amount = 100.0;

        // 결제 서비스 실패 강제화
        when(paymentService.processPayment(userId, amount)).thenReturn(false);

        // when : 주문 처리
        orderService.processOrder(userId, email, amount);

        // then : 이메일 발송 메서드가 호출되지 않았는 지 검증
        // never()
        verify(notificationService, never()).sendEmail(anyString(), anyString());
    }

}
