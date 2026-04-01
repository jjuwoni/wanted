package com.wanted.aop.section01.problem;

import java.util.HashMap;
import java.util.Map;

public class MemberService {

    // DB 역할을 대신하는 인메모리 데이터베이스
    private Map<String, MemberDTO> memberRepository = new HashMap<>();

    /**
     * 회원 가입 처리 메소드
     * - 성능 측정, 로깅, 트랜잭션 처리 코드가 비즈니스 로직과 섞여있다.
     */
    public void registerMember(MemberDTO member) {
        // 성능 측정 시작 - 횡단 관심사 #1
        long startTime = System.currentTimeMillis();

        try {
            // 로깅 - 횡단 관심사 #2
            System.out.println("[로그] 회원 가입 시작: " + member.getEmail());

            // 트랜잭션 시작 (의사코드) - 횡단 관심사 #3
            // beginTransaction();

            // ----- 핵심 비즈니스 로직 시작 -----
            // 데이터 유효성 검증
            if (member.getEmail() == null || member.getEmail().isEmpty()) {
                throw new IllegalArgumentException("이메일은 필수입니다.");
            }

            if (member.getPassword() == null || member.getPassword().length() < 8) {
                throw new IllegalArgumentException("비밀번호는 8자 이상이어야 합니다.");
            }

            // 중복 회원 확인
            if (memberRepository.containsKey(member.getEmail())) {
                throw new IllegalArgumentException("이미 등록된 이메일입니다: " + member.getEmail());
            }

            // 회원 저장
            memberRepository.put(member.getEmail(), member);
            System.out.println("회원 정보 저장 완료: " + member.getEmail());
            // ----- 핵심 비즈니스 로직 종료 -----

            // 트랜잭션 커밋 (의사코드) - 횡단 관심사 #3
            // commitTransaction();

            // 로깅 - 횡단 관심사 #2
            System.out.println("[로그] 회원 가입 성공: " + member.getEmail());
        } catch (Exception e) {
            // 트랜잭션 롤백 (의사코드) - 횡단 관심사 #3
            // rollbackTransaction();

            // 로깅 - 횡단 관심사 #2
            System.out.println("[로그] 회원 가입 실패: " + e.getMessage());
            throw e;
        } finally {
            // 성능 측정 종료 - 횡단 관심사 #1
            long endTime = System.currentTimeMillis();
            System.out.println("[성능] 회원 가입 처리 시간: " + (endTime - startTime) + "ms");
        }
    }

    /**
     * 회원 조회 메소드
     * - 성능 측정, 로깅 코드가 비즈니스 로직과 섞여있다.
     */
    public MemberDTO getMember(String email) {
        // 성능 측정 시작 - 횡단 관심사 #1
        long startTime = System.currentTimeMillis();

        try {
            // 로깅 - 횡단 관심사 #2
            System.out.println("[로그] 회원 조회 시작: " + email);

            // ----- 핵심 비즈니스 로직 시작 -----
            // 데이터 유효성 검증
            if (email == null || email.isEmpty()) {
                throw new IllegalArgumentException("이메일은 필수입니다.");
            }

            // 회원 조회
            MemberDTO member = memberRepository.get(email);

            if (member == null) {
                throw new IllegalArgumentException("존재하지 않는 회원입니다: " + email);
            }
            // ----- 핵심 비즈니스 로직 종료 -----

            // 로깅 - 횡단 관심사 #2
            System.out.println("[로그] 회원 조회 성공: " + email);
            return member;
        } catch (Exception e) {
            // 로깅 - 횡단 관심사 #2
            System.out.println("[로그] 회원 조회 실패: " + e.getMessage());
            throw e;
        } finally {
            // 성능 측정 종료 - 횡단 관심사 #1
            long endTime = System.currentTimeMillis();
            System.out.println("[성능] 회원 조회 처리 시간: " + (endTime - startTime) + "ms");
        }
    }

    /**
     * 비밀번호 변경 메소드
     * - 성능 측정, 로깅, 트랜잭션 처리 코드가 비즈니스 로직과 섞여있다.
     */
    public void updatePassword(String email, String currentPassword, String newPassword) {
        // 성능 측정 시작 - 횡단 관심사 #1
        long startTime = System.currentTimeMillis();

        try {
            // 로깅 - 횡단 관심사 #2
            System.out.println("[로그] 비밀번호 변경 시작: " + email);

            // 트랜잭션 시작 (의사코드) - 횡단 관심사 #3
            // beginTransaction();

            // ----- 핵심 비즈니스 로직 시작 -----
            // 데이터 유효성 검증
            if (email == null || email.isEmpty()) {
                throw new IllegalArgumentException("이메일은 필수입니다.");
            }

            if (newPassword == null || newPassword.length() < 8) {
                throw new IllegalArgumentException("새 비밀번호는 8자 이상이어야 합니다.");
            }

            // 회원 조회
            MemberDTO member = memberRepository.get(email);

            if (member == null) {
                throw new IllegalArgumentException("존재하지 않는 회원입니다: " + email);
            }

            // 현재 비밀번호 확인
            if (!member.getPassword().equals(currentPassword)) {
                throw new IllegalArgumentException("현재 비밀번호가 일치하지 않습니다.");
            }

            // 비밀번호 변경
            member.setPassword(newPassword);
            memberRepository.put(email, member);
            // ----- 핵심 비즈니스 로직 종료 -----

            // 트랜잭션 커밋 (의사코드) - 횡단 관심사 #3
            // commitTransaction();

            // 로깅 - 횡단 관심사 #2
            System.out.println("[로그] 비밀번호 변경 성공: " + email);
        } catch (Exception e) {
            // 트랜잭션 롤백 (의사코드) - 횡단 관심사 #3
            // rollbackTransaction();

            // 로깅 - 횡단 관심사 #2
            System.out.println("[로그] 비밀번호 변경 실패: " + e.getMessage());
            throw e;
        } finally {
            // 성능 측정 종료 - 횡단 관심사 #1
            long endTime = System.currentTimeMillis();
            System.out.println("[성능] 비밀번호 변경 처리 시간: " + (endTime - startTime) + "ms");
        }
    }

    /**
     * 회원 탈퇴 메소드
     * - 성능 측정, 로깅, 트랜잭션 처리 코드가 비즈니스 로직과 섞여있다.
     */
    public void deleteMember(String email) {
        // 성능 측정 시작 - 횡단 관심사 #1
        long startTime = System.currentTimeMillis();

        try {
            // 로깅 - 횡단 관심사 #2
            System.out.println("[로그] 회원 탈퇴 시작: " + email);

            // 트랜잭션 시작 (의사코드) - 횡단 관심사 #3
            // beginTransaction();

            // ----- 핵심 비즈니스 로직 시작 -----
            // 데이터 유효성 검증
            if (email == null || email.isEmpty()) {
                throw new IllegalArgumentException("이메일은 필수입니다.");
            }

            // 회원 존재 확인
            if (!memberRepository.containsKey(email)) {
                throw new IllegalArgumentException("존재하지 않는 회원입니다: " + email);
            }

            // 회원 삭제
            memberRepository.remove(email);
            // ----- 핵심 비즈니스 로직 종료 -----

            // 트랜잭션 커밋 (의사코드) - 횡단 관심사 #3
            // commitTransaction();

            // 로깅 - 횡단 관심사 #2
            System.out.println("[로그] 회원 탈퇴 성공: " + email);
        } catch (Exception e) {
            // 트랜잭션 롤백 (의사코드) - 횡단 관심사 #3
            // rollbackTransaction();

            // 로깅 - 횡단 관심사 #2
            System.out.println("[로그] 회원 탈퇴 실패: " + e.getMessage());
            throw e;
        } finally {
            // 성능 측정 종료 - 횡단 관심사 #1
            long endTime = System.currentTimeMillis();
            System.out.println("[성능] 회원 탈퇴 처리 시간: " + (endTime - startTime) + "ms");
        }
    }
}
