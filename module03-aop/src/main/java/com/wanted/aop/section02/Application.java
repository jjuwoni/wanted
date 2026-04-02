package com.wanted.aop.section02;

import com.wanted.aop.section02.config.AppConfig;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class Application {

    public static void main(String[] args) {
        System.out.println("==========AOP 적용 후==========");

        ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);

        MemberService service = context.getBean(MemberService.class);

        try {
            System.out.println("[테스트 1] 회원 가입");
            MemberDTO member = new MemberDTO("user@example.com", "pass0123", "홍길동", "010-1111-1111", "USER");
            service.registerMember(member);
            System.out.println("=================");

            System.out.println("[테스트 2] 회원 조회");
            MemberDTO foundMember = service.getMember("user@example.com");
            System.out.println("조회 된 회원 = " + foundMember);
            System.out.println("=================");

            System.out.println("[테스트 3] 비밀번호 변경");
            service.updatePassword("user@example.com", "pass0123", "pass0234");
            System.out.println("=================");

            System.out.println("[테스트 4] 회원 탈퇴");
            service.deleteMember("user@example.com");
            System.out.println("=================");

            // 일부러 예외를 발생
            System.out.println("[테스트 5] 존재하지 않는 회원 조회");
            service.getMember("user@example.com");
            System.out.println("=================");

        } catch (Exception e) {
            System.out.println("예외 발생!! : " + e.getMessage());
        }

        /* comment.
        *   section01 번의 문제점
        *   1. 코드 중복 : 로깅, 성능측정, 트랜잭션 관리 관련 코드가 메서드 마다 존재한다.
        *   2. 낮은 응집도 : 각 메서드가 비즈니스 로직 외에 다른 코드들을 실행한다. (핵심 비즈니스에 집중이 안 된다.)
        *   3. 유지보수의 어려움 : 횡단 관심사 변경 시 여러 메소드 안에 내용들을 모두 수정해야 한다.
        * */
    }
}
