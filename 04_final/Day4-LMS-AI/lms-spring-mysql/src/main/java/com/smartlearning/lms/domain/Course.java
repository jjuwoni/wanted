package com.smartlearning.lms.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** ChromaDB(Vector DB)의 강좌 ID(C001~C050)와 연결되는 열쇠. schema.sql 주석 참고 */
    private String code;

    private String title;
    private String category;

    @Column(name = "level")
    private String level;
}
