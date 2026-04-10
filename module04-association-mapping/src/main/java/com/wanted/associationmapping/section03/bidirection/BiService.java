package com.wanted.associationmapping.section03.bidirection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BiService {

    @Autowired
    private BiRepository repository;

    public Menu findMenu(int menuCode) {
        return repository.findMenu(menuCode);
    }

    /* comment.
    *   oneToMany 관계는 Lazy Loading 이 기본값이다.
    *   따라서 트랜젝션이 없다고 하면 Menu 가 필요한 상황에서 데이터를 조회하다가 오류가 발생할 수 있다.
    *   트랜젝션 안에서는 변경 된 내용이 자동으로 반영된다 (변경 감지)
    *   여러 객체를 함께 바꿀 때 중간에 실패하면 전부 rollback 을 하는 것이
    *   Transaction 의 기능이기 때문에 LazyLoading 시에는 반드시 Transaction 을 작성하자.
    * */
    @Transactional(readOnly = true)
    public Category findCategory(int categoryCode) {

        Category foundCategory = repository.findCategory(categoryCode);

        // 지연로딩 시 menu 를 달라!!!!
        System.out.println(foundCategory.getMenuList());

        return foundCategory;
    }

    @Transactional
    public void registMenu(Menu newMenu) {
        repository.saveMenu(newMenu);
    }

    @Transactional
    public void registCategory(Category category) {
        repository.saveCategory(category);
    }
}
