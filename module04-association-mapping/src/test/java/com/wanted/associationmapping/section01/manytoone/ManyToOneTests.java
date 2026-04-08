package com.wanted.associationmapping.section01.manytoone;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.stream.Stream;

@SpringBootTest
public class ManyToOneTests {

    @Autowired
    private ManyToOneService service;

    /* comment.
    *   연관 관계가 있는 객체 관계에서 실제로 연관을 가지는 객체의 수에 따라 부르는 명칭이 달라진다.
    *   - 1 : 1(OneToOne)
    *   - 1 : N (OneToMany)
    *   - N : 1 (ManyToOne)
    *   - N : M (ManyToMany)
    *   ManyToOne 은 다수의 엔티티(메뉴) 가 하나의 엔티티(카테고리)
    *   를 참조하는 상황에서 사용된다.
    * */
    @DisplayName("N:1 관계의 객체 그래프 탐색을 이용한 조회")
    @Test
    void manyToOneFind() {
        // 테스트용 값
        int menuCode = 10;

        Menu foundMenu = service.findMenu(menuCode);

        Category category = foundMenu.getCategory();

        System.out.println("category = " + category);

        Assertions.assertNotNull(category);

    }

    @DisplayName("N:1 연관관계 객체지향쿼리 이용 테스트")
    @Test
    void manyToOneJPQL() {

        int menuCode = 11;

        String categoryName = service.findCategoryName(menuCode);

        System.out.println("categoryName = " + categoryName);

        Assertions.assertNotNull(categoryName);
    }

    /* comment.
    *   commit() 을 하게 되면 , Entity 객체를 insert 하는
    *   쿼리문이 발생한다.
    *   단 여기서 고려해야 하는 것은 영속성 전이라는 것이 필요하다.
    * */
    private static Stream<Arguments> getInfo() {
        return Stream.of(
                Arguments.of(110, "돈까스덮밥", 30000, 1234, "퓨전분식", "Y")
        );
    }

    @DisplayName("N:1 연관관계 객체 삽입 테스트")
    @ParameterizedTest
    @MethodSource("getInfo")
    void manyToOneInsert(int menuCode, String menuName, int menuPrice,
                         int categoryCode, String categoryName,
                         String orderableStatus) {

        MenuDTO menuInfo = new MenuDTO(
                menuCode, menuName, menuPrice,
                new CategoryDTO(categoryCode, categoryName, null),
                orderableStatus
        );

        Assertions.assertDoesNotThrow(
                () -> service.registMenu(menuInfo)
        );
    }

}
