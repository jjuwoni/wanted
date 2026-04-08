package com.wanted.entitymapping.section01.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity(name = "member")
@Table(name = "tbl_member")
public class Member {

    @Id
    @Column(name = "member_no")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int memberNo;

    @Column(name = "member_id", nullable = false,
            unique = true, columnDefinition = "varchar(10)")
    private String memberId;

    @Column(name = "member_pwd", nullable = false)
    private String memberPwd;

    @Column(name = "member_name")
    private String memberName;

    @Column(name = "phone", unique = true)
    private String phone;

    @Column(name = "address", length = 900)
    private String address;

    /*
     * 📌 날짜 및 시간 타입 매핑
     *
     * ▶ LocalDate: 날짜만 저장 (yyyy-MM-dd)
     * ▶ LocalDateTime: 날짜 + 시간 저장 (yyyy-MM-dd HH:mm:ss)
     *
     * 🎯 JPA는 Hibernate 5 이상에서 java.time 패키지를 자동 지원함
     */
    @Column(name = "enroll_date")
    private LocalDateTime enrollDate;

    /* comment.
     *   @Enumerated
     *   - enum 타입 매핑 시 사용
     *   - ORDINAL : Enum 타입을 필드 순서로 매핑
     *   - 장점 : 0, 1 이런 식으로 저장되기 때문에 메모리 효율성 좋음
     *   - 단점 : 가독성이 떨어지며 필드 추가, 순서 변경 시 데이터 혼돈
     *   - STRING : Enum 타입을 문자열로 매핑
     *   - 권장하는 방식이다. 실무에서는 Enum 타입을 매핑할때 String 으로 한다.
     *   - 장점 : 가독성이 좋으며, 이름으로 저장하기 때문에 순서 변경 지장 없음
     *   - 단점 : 데이터 크기가 숫자보다 크다. <- 하지만 큰 단점은 아니다.
     *  */
    @Column(name = "member_role")
    @Enumerated(EnumType.ORDINAL)
    private MemberRole memberRole;

    @Column(name = "status", columnDefinition = "char(1) default 'Y'")
    private String status;

    public Member() {}

    public Member(String memberId, String memberPwd, String memberName, String phone, String address, LocalDateTime enrollDate, MemberRole memberRole, String status) {
        this.memberId = memberId;
        this.memberPwd = memberPwd;
        this.memberName = memberName;
        this.phone = phone;
        this.address = address;
        this.enrollDate = enrollDate;
        this.memberRole = memberRole;
        this.status = status;
    }

}
