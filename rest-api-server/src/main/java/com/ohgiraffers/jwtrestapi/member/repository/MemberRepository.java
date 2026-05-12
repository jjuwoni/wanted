package com.ohgiraffers.jwtrestapi.member.repository;

import com.ohgiraffers.jwtrestapi.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface MemberRepository extends JpaRepository<Member, Integer> {

    Member findByMemberId(String memberId);
    Member findByMemberEmail(String memberEmail);

    /* 설명. JPQL과 @Query를 활용한 구문 */
    @Query("SELECT MAX(m.memberCode) FROM Member m")    // 설명. JPQL에서 엔티티 이름은 대소문자까지 완벽히 일치할 것!
    int maxMemberCode();

    /* 설명. purchase 도메인 추가하면서 추가한 메소드 */
    @Query("SELECT m.memberCode FROM Member m WHERE m.memberId = ?1")
    int findMemberCodeByMemberId(String orderMemberId);
}
