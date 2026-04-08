package com.wanted.associationmapping.section01.manytoone;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

@Repository
public class ManyToOneRepository {

    @PersistenceContext
    private EntityManager manager;

    public Menu find(int menuCode) {

        return manager.find(Menu.class, menuCode);
    }

    /* comment. JPQL */
    public String findCategoryName(int menuCode) {

        String jpql = "SELECT c.categoryName " +
                "FROM menu_and_category m " +
                "JOIN m.category c " +
                "WHERE m.menuCode = :menuCode";

        return manager.createQuery(jpql, String.class)
                .setParameter("menuCode", menuCode)
                .getSingleResult();
    }

    public void regist(Menu menu) {
        manager.persist(menu);
    }
}
