package com.wanted.springevent.certificate.entity;

import com.wanted.springevent.enrollment.entity.Enrollment;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "certificates")
public class Certificate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "certificate_id")
    private Long certificateId;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "enrollment_id", nullable = false, unique = true)
    private Enrollment enrollment;

    @Column(name = "verification_code", nullable = false, unique = true, length = 100)
    private String verificationCode;

    @Column(name = "issued_at", nullable = false, updatable = false, insertable = false)
    private LocalDateTime issuedAt;

    protected Certificate() {
    }

    private Certificate(Enrollment enrollment, String verificationCode) {
        this.enrollment = enrollment;
        this.verificationCode = verificationCode;
    }

    // 팩토리 메서드
    // Entity 클래스 인스턴스를 생성할 때 외부에서 new 키워드로 만드는 것이 아닌
    // Entity 클래스 내부에 new 관련 생성 메서드를 만들어두어
    // 규칙없이 함부로 entity 객체를 만들지 못하게 한다.
    public static Certificate issue(Enrollment enrollment, String verificationCode) {
        return new Certificate(enrollment, verificationCode);
    }

    public Long getCertificateId() {
        return certificateId;
    }

    public String getVerificationCode() {
        return verificationCode;
    }
}
