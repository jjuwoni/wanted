package com.wanted.associationmapping.section01.manytoone;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@ToString
@Entity(name = "menu_and_category")
@Table(name = "tbl_menu")
public class Menu {

    @Id
    @Column(name = "menu_code")
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int menuCode;

    @Column(name = "menu_name")
    private String menuName;

    @Column(name = "menu_price")
    private int menuPrice;

//    이 구조는 SQL 친화적인 구조이며, JPA 객체 관점에서는 잘못 된 구조이다.
//    private int categoryCode;

    /* comment.
    *   [영속성 전이]
    *   - 특정 엔티티를 영속화(P.C 등록) 할 때,
    *   - 연관관계에 있는 엔티티도 같이 영속화 한다는 의미이다.
    *   - 지금 상황에서는 1개의 메뉴를 영속화 할 때,
    *   - 그 메뉴에 포함된 Category 도 같이 영속화를 한다는
    *   - 의미이다.
    * */
    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "category_code")
    private Category category;

    @Column(name = "orderable_status")
    private String orderableStatus;


}
