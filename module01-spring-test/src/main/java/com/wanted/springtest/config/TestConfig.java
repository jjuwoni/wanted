package com.wanted.springtest.config;

import com.wanted.springtest.section01.basic.CalculatorService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
public class TestConfig {

    /**
     * 테스트 환경에서만 사용되는 특별한 계산기 서비스를 등록한다.
     *
     * @Profile("test") 어노테이션을 사용하여
     * 'test' 프로파일이 활성화될 때만 이 Bean이 등록된다.
     *
     * 이는 테스트 환경에서 특별한 동작이 필요한 경우에 활용된다.
     * 예를 들어, 외부 API 호출을 Mock으로 대체하거나,
     * 테스트용 데이터베이스 설정을 적용할 때 사용한다.
     *
     * @return 테스트용 계산기 서비스
     */
    @Bean
    @Profile("test")
    public CalculatorService testCalculatorService() {

        /* 설명.
         *  CalculatorService를 상속받은 익명 클래스를 생성한다.
         *  필요한 메서드만 오버라이드하여 테스트 환경에 맞는 동작을 구현할 수 있다.
         * */
        return new CalculatorService() {
            /**
             * 테스트용 오버라이드 메서드
             * 실제 계산 대신 항상 고정값을 반환하여
             * 테스트의 예측 가능성을 높인다.
             */
            @Override
            public int add(int a, int b) {
                /* 설명. 테스트 환경에서는 디버깅을 위해 로깅을 추가한다. */
                System.out.println("테스트 환경에서 덧셈 실행: " + a + " + " + b);
                return super.add(a, b);
            }

            @Override
            public int divide(int a, int b) {
                System.out.println("테스트 환경에서 나눗셈 실행: " + a + " / " + b);
                return super.divide(a, b);
            }
        };
    }

    /**
     * 테스트 환경에서 사용할 설정 정보를 담는 Bean이다.
     *
     * 이 Bean은 테스트에서 환경별 설정값을 확인하는 데 사용된다.
     * 예를 들어, 테스트 환경에서는 다른 타임아웃 값이나
     * 다른 URL을 사용해야 하는 경우에 활용한다.
     *
     * @return 테스트 설정 정보
     */
    @Bean
    @Profile("test")
    public TestProperties testProperties() {

        TestProperties properties = new TestProperties();

        /* 설명. 테스트 환경에 맞는 설정값들을 지정한다. */
        properties.setMaxCalculationRange(1000);
        properties.setTimeoutSeconds(5);
        properties.setDebugMode(true);

        return properties;
    }

    /**
     * 테스트 설정 정보를 담는 내부 클래스이다.
     *
     * 실제 프로젝트에서는 @ConfigurationProperties를 사용하여
     * application.yaml에서 값을 읽어오는 것이 일반적이다.
     */
    public static class TestProperties {

        private int maxCalculationRange;
        private int timeoutSeconds;
        private boolean debugMode;

        // getter & setter
        public int getMaxCalculationRange() { return maxCalculationRange; }
        public void setMaxCalculationRange(int maxCalculationRange) {
            this.maxCalculationRange = maxCalculationRange;
        }

        public int getTimeoutSeconds() { return timeoutSeconds; }
        public void setTimeoutSeconds(int timeoutSeconds) {
            this.timeoutSeconds = timeoutSeconds;
        }

        public boolean isDebugMode() { return debugMode; }
        public void setDebugMode(boolean debugMode) {
            this.debugMode = debugMode;
        }
    }

}
