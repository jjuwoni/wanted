package com.wanted.associationmapping.section03.bidirection;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
//@ToString
@Entity(name = "section03category")
@Table(name = "tbl_category")
public class Category {

    @Id
    @Column(name = "category_code")
    private int categoryCode;

    @Column(name = "category_name")
    private String categoryName;

    @Column(name = "ref_category_code")
    private Integer refCategoryCode;

    /* comment.
    *   메뉴 -> 카테고리 참조
    *   카테고리 -> 메뉴 참조
    *   객체의 참조는 둘인데, 외래키는 하나인 상황을 해결하기 위해
    *   두 연관관계 중 하나를 정해 테이블의 외래키를 관리한다.
    * */

    // mappedBy 설정은 주인이 아닌 쪽에서 주인의 필드명 변수를 작성한다.
    @OneToMany(mappedBy = "category")
    private List<Menu> menuList = new ArrayList<>();

    public Category(int categoryCode, String categoryName, Integer refCategoryCode) {
        this.categoryCode = categoryCode;
        this.categoryName = categoryName;
        this.refCategoryCode = refCategoryCode;
    }
}
