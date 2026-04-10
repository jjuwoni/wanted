package com.wanted.jpaminiproject.domain.menu.model.dao;

import com.wanted.jpaminiproject.domain.menu.model.entity.Menu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/* comment.
*   Repository 인터페이스
*   - 데이터 Access 의 추상화
*   - DB 작업의 상세 구현을 숨기고, 어떤 것을 할 지 작성하는 인터페이스
*   - 개발자는 인터페이스의 구현 클래스를 만들지 않으며
*   - Spring Data JPA 가 어플리케이션 시작 시 자동으로 구현 객체를 만들어
*   - Spring Bean 으로 등록해준다.
*   - JpaRepository<관리 할 Entity, 해당 Entity 의 @Id 필드 자료형>
* */

@Repository
public interface MenuRepository extends JpaRepository<Menu, Integer> {
    List<Menu> findByMenuPriceGreaterThanOrderByMenuPrice(int menuPriceIsGreaterThan);
}
