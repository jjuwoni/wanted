package com.wanted.section03;

import com.wanted.section02.Menu;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class EntityLifeCycle {

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

    @AfterEach
    void closeManager() {
        manager.close();
    }

    @AfterAll
    static void closeFactory() {
        factory.close();
    }

    @Test
    void 비영속_테스트_메서드() {

        /* comment.
        *   객체를 생성하면(new), 영속성 컨텍스트와는 전혀 관련 없는 비영속 상태이다.
        * */

        // given
        Menu foundMenu = manager.find(Menu.class, 1);

        Menu newMenu = new Menu();
        newMenu.setMenuCode(foundMenu.getMenuCode());
        newMenu.setMenuName(foundMenu.getMenuName());
        newMenu.setMenuPrice(foundMenu.getMenuPrice());
        newMenu.setCategoryCode(foundMenu.getCategoryCode());
        newMenu.setOrderableStatus(foundMenu.getOrderableStatus());
        // 자료형 같고, 값도 똑같음.
        System.out.println("newMenu = " + newMenu);
        System.out.println("foundMenu = " + foundMenu);

        // when
        boolean isTrue = (foundMenu == newMenu);

        // then
        Assertions.assertFalse(isTrue);
    }

    @Test
    void 영속성_테스트_메서드() {

        // given
        Menu foundMenu = manager.find(Menu.class, 1);
        Menu newMenu = manager.find(Menu.class, 1);


        // when
        boolean isTrue = (foundMenu == newMenu);

        // then
        Assertions.assertTrue(isTrue);
    }

    @Test
    void 준영속_detach_테스트() {

        // given
        Menu foundMenu1 = manager.find(Menu.class, 11);
        Menu foundMenu2 = manager.find(Menu.class, 12);

        // when
        manager.detach(foundMenu2);
        foundMenu1.setMenuPrice(5000);
        foundMenu2.setMenuPrice(5000);


        // then
        assertEquals(5000, manager.find(Menu.class, 11).getMenuPrice());
//        assertEquals(5000, manager.find(Menu.class, 12).getMenuPrice());

    }

    @Test
    void 삭제_remove_테스트() {
        /* comment.
        *   remove() : 엔티티를 영속성 컨텍스트 및 DB 에서 삭제한다.
        *   단, 트랜젝션을 제어하지 않으면 영구 반영되지 않는다.
        * */

        // 그릇에 밥을 담았다.
        Menu foundMenu = manager.find(Menu.class, 2);
        System.out.println("foundMenu.hashCode() = " + foundMenu.hashCode());

        // 그릇을 깨서 없앴다.
        manager.remove(foundMenu);
        // 밥은 남아있음
        System.out.println("삭제한 foundMenu.hashCode() = " + foundMenu.hashCode());

        // 깨서 버린 그릇을 가져올 순 없다.
        Menu refoundMenu = manager.find(Menu.class, 2);
//        System.out.println("다시 찾은 refoundMenu.hashCode() = " + refoundMenu.hashCode());

        assertEquals(2, foundMenu.getMenuCode());
        assertEquals(null, refoundMenu);
    }

    @Test
    void 병합_merge_수정_테스트() {

        // 밥그릇에 밥을 담았다
        Menu detachMenu = manager.find(Menu.class, 2);
        // 그릇을 다른 테이블에 잠시 놔둠
        manager.detach(detachMenu);

        // 그 그릇에 밥을 콩밥으로 바꿨다
        detachMenu.setMenuName("콩밥");

        // 새로운 그릇을 가져왔다.
        Menu refoundMenu = manager.find(Menu.class, 2);

        System.out.println("detachMenu.hashCode() = " + detachMenu.hashCode());
        System.out.println("refoundMenu.hashCode() = " + refoundMenu.hashCode());

        // 새로운 그릇에 아까 그 콩밥을 옮겨 담았다.
        manager.merge(detachMenu);

        // 그 그릇 가져오기
        Menu mergeMenu = manager.find(Menu.class , 2);
        System.out.println("mergeMenu.hashCode() = " + mergeMenu.hashCode());

        assertEquals("콩밥", mergeMenu.getMenuName());
    }
}
