package com.wanted.jpaminiproject.domain.menu.model.entity;

import jakarta.persistence.*;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@ToString
@Entity
/* 2. @Builder 어노테이션을 사용한 update 기능 */
//@Builder(toBuilder = true)
@Table(name = "tbl_menu")
public class Menu {

    @Id
    @Column(name = "menu_code")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int menuCode;

    @Column(name = "menu_name")
    private String menuName;

    @Column(name = "menu_price")
    private int menuPrice;

    @Column(name = "category_code")
    private int categoryCode;

    @Column(name = "orderable_status")
    private String orderableStatus;

    /* 1. setter 사용해서 update - setter 사용은 지양한다. */
//    public void setMenuName(String menuName) {
//        this.menuName = menuName;
//    }

    /* 3. Entity 내부에 직접 Builder 패턴을 구현 */
    public Menu changeMenuName(String newMenuName) {
        this.menuName = newMenuName;
        return this;
    }

    public Menu builder() {
        return new Menu(menuCode, menuName, menuPrice, categoryCode, orderableStatus);
    }

}
