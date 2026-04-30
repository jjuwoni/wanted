package com.ohgiraffers.stability.section02.logging.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * 도서관 회원 정보를 관리하는 엔티티 클래스
 * 
 * <p>도서관 시스템에서 회원의 기본 정보를 저장하고 관리한다.
 * 회원 ID, 이름, 이메일, 전화번호, 가입일 등의 정보를 포함한다.</p>
 */
@Entity
@Table(name = "tbl_member")
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long memberId;

    @Column(name = "member_name", nullable = false, length = 50)
    private String memberName;

    @Column(name = "email", unique = true, nullable = false, length = 100)
    private String email;

    @Column(name = "phone", length = 20)
    private String phone;

    @Column(name = "join_date", nullable = false)
    private LocalDateTime joinDate;

    @Column(name = "active", nullable = false)
    private Boolean active;

    protected Member() {}

    public Member(String memberName, String email, String phone) {
        this.memberName = memberName;
        this.email = email;
        this.phone = phone;
        this.joinDate = LocalDateTime.now();
        this.active = true;
    }

    // Getters
    public Long getMemberId() {
        return memberId;
    }

    public String getMemberName() {
        return memberName;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public LocalDateTime getJoinDate() {
        return joinDate;
    }

    public Boolean getActive() {
        return active;
    }

    // Setters
    public void setMemberName(String memberName) {
        this.memberName = memberName;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    /**
     * 회원을 비활성화한다.
     */
    public void deactivate() {
        this.active = false;
    }

    /**
     * 회원을 활성화한다.
     */
    public void activate() {
        this.active = true;
    }
} 