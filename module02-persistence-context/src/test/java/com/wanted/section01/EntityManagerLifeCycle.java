package com.wanted.section01;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class EntityManagerLifeCycle {

    /* comment.
    *   엔티티 매니저란?
    *   - 엔티티 매니저는 엔티티를 저장하는 메모리상의 DB 를 관리하는 인스턴스이다.
    *   - 엔티티를 저장, 수정, 삭제, 조회 하는 등의 엔티티 관련 모든 일을 해준다.
    *   - 엔티티 매니저는 thread-safe 하지 않기 때문에 동시성 문제가 발생할 수 있다.
    *   - 고로, 1개의 요청 당 1개의 매니저를 통해 DB 와의 연결을 맺어야 한다.
    *   영속성 컨텍스트란?
    *   - 엔티티 매니저를 통해 엔티티를 저장, 조회를 하면 매니저는 영속성 컨텍스트에
    *   - 엔티티를 보관 및 관리를 해준다.
    *   - 영속성 컨텍스트는 엔티티를 Key-Value 형태로 저장하며
    *   - 매니저를 통해서만 접근할 수 있으며 관리할 수 있다.
    * */
    private static EntityManagerFactory factory;
    private EntityManager manager;

    @BeforeAll
    static void initFactory() {
        factory = Persistence.createEntityManagerFactory("jpatest");
    }

    @BeforeEach
    void initManager() {
        manager = factory.createEntityManager();
    }

    @Test
    @DisplayName("엔티티 매니저 팩토리와 엔티티 매니저의 생명주기 확인(1)")
    void testLifeCycle() {
        System.out.println("factory.hashCode() = " + factory.hashCode());
        System.out.println("manager.hashCode() = " + manager.hashCode());
    }

    @Test
    @DisplayName("엔티티 매니저 팩토리와 엔티티 매니저의 생명주기 확인(2)")
    void testLifeCycle2() {
        System.out.println("factory.hashCode() = " + factory.hashCode());
        System.out.println("manager.hashCode() = " + manager.hashCode());
    }
}
