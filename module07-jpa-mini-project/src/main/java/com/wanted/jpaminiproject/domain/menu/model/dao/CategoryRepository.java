package com.wanted.jpaminiproject.domain.menu.model.dao;

import com.wanted.jpaminiproject.domain.menu.model.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer> {

    /* comment. 해당 메서드는 JPA 쿼리메소드가 아닌 내가 직접 만든 메서드이다.
    *   Join 이 여러개 이거나, 조건이 복잡한 경우 JPA 쿼리메소드로 작성하게 되면
    *   세밀하게 조절이 암 되는 경우가 있다.
    *   위 상황에서는 우리가 직접 SQL 구문을 작성할 수 있다.
    *   - jpql : entity 클래스를 대상으로 sql 구문을 작성하는 것. -> from 절에 entity 명
    *   - native query : 실제 sql 구문을 작성하는 것. -> from 절에 실제 테이블 명
    * */
    @Query(value = "select * from tbl_category order by category_code", nativeQuery = true)
    List<Category> findAllCategory();
}
