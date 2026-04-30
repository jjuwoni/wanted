package com.wanted.springtest.section01.basic;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/calculator")
public class CalculatorController {
    /**
     * 계산기 서비스 의존성 주입
     *
     * Spring의 의존성 주입(DI)을 통해 CalculatorService를 주입받는다.
     * @Autowired 어노테이션을 사용하여 자동으로 Bean을 주입한다.
     *
     * 생성자 주입 방식을 사용하면 더 안전하지만,
     * 학습 목적으로 필드 주입을 사용한다.
     */
    @Autowired
    private CalculatorService calculatorService;

    /**
     * 덧셈 연산을 수행한다.
     *
     * HTTP GET 요청으로 두 숫자를 받아서 덧셈 결과를 반환한다.
     *
     * 예시 요청: GET /api/calculator/add?a=10&b=5
     * 예시 응답: {"result": 15}
     *
     * @param a 첫 번째 숫자
     * @param b 두 번째 숫자
     * @return 덧셈 결과를 포함한 JSON 응답
     */
    @GetMapping("/add")
    public ResponseEntity<CalculationResult> add(
            @RequestParam int a,
            @RequestParam int b) {

        int result = calculatorService.add(a, b);
        return ResponseEntity.ok(new CalculationResult(result, "addition", a, b));
    }

    /**
     * 뺄셈 연산을 수행한다.
     *
     * @param a 피감수
     * @param b 감수
     * @return 뺄셈 결과를 포함한 JSON 응답
     */
    @GetMapping("/subtract")
    public ResponseEntity<CalculationResult> subtract(
            @RequestParam int a,
            @RequestParam int b) {

        int result = calculatorService.subtract(a, b);
        return ResponseEntity.ok(new CalculationResult(result, "subtraction", a, b));
    }

    /**
     * 곱셈 연산을 수행한다.
     *
     * @param a 첫 번째 숫자
     * @param b 두 번째 숫자
     * @return 곱셈 결과를 포함한 JSON 응답
     */
    @GetMapping("/multiply")
    public ResponseEntity<CalculationResult> multiply(
            @RequestParam int a,
            @RequestParam int b) {

        int result = calculatorService.multiply(a, b);
        return ResponseEntity.ok(new CalculationResult(result, "multiplication", a, b));
    }

    /**
     * 나눗셈 연산을 수행한다.
     *
     * 0으로 나누는 경우 예외가 발생할 수 있으므로
     * 예외 처리 테스트 학습에 활용된다.
     *
     * @param a 피제수
     * @param b 제수
     * @return 나눗셈 결과를 포함한 JSON 응답
     */
    @GetMapping("/divide")
    public ResponseEntity<CalculationResult> divide(
            @RequestParam int a,
            @RequestParam int b) {

        try {
            int result = calculatorService.divide(a, b);
            return ResponseEntity.ok(new CalculationResult(result, "division", a, b));
        } catch (ArithmeticException e) {
            /* 설명. 예외 발생 시 Bad Request 응답과 함께 에러 정보를 반환한다. */
            return ResponseEntity.badRequest()
                    .body(new CalculationResult(0, "error: " + e.getMessage(), a, b));
        }
    }

    /**
     * 서비스 상태를 확인하는 헬스체크 엔드포인트이다.
     *
     * Spring Actuator와 별도로 커스텀 헬스체크를 제공한다.
     * 테스트에서 애플리케이션이 정상적으로 실행되는지 확인하는 데 사용된다.
     *
     * @return 서비스 상태 정보
     */
    @GetMapping("/health")
    public ResponseEntity<HealthStatus> health() {
        return ResponseEntity.ok(new HealthStatus("UP", "Calculator service is running"));
    }

    /**
     * 계산 결과를 담는 내부 클래스이다.
     *
     * JSON 응답 형식을 일관되게 유지하기 위해 사용한다.
     * 실제 프로젝트에서는 별도의 DTO 패키지에 위치시킨다.
     */
    public static class CalculationResult {

        private int result;
        private String operation;
        private int operand1;
        private int operand2;

        public CalculationResult(int result, String operation, int operand1, int operand2) {
            this.result = result;
            this.operation = operation;
            this.operand1 = operand1;
            this.operand2 = operand2;
        }

        /* 설명. Getter 메서드들이다. Jackson이 JSON 직렬화 시 이 메서드들을 사용한다. */
        public int getResult() { return result; }
        public String getOperation() { return operation; }
        public int getOperand1() { return operand1; }
        public int getOperand2() { return operand2; }
    }

    /**
     * 헬스체크 상태를 담는 내부 클래스이다.
     */
    public static class HealthStatus {

        private String status;
        private String message;

        public HealthStatus(String status, String message) {
            this.status = status;
            this.message = message;
        }

        public String getStatus() { return status; }
        public String getMessage() { return message; }
    }
}
