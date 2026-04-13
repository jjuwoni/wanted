package com.wanted.jpaminiproject.domain.menu.model.service;

import com.wanted.jpaminiproject.domain.menu.model.dao.CategoryRepository;
import com.wanted.jpaminiproject.domain.menu.model.dao.MenuRepository;
import com.wanted.jpaminiproject.domain.menu.model.dto.CategoryDTO;
import com.wanted.jpaminiproject.domain.menu.model.dto.MenuDTO;
import com.wanted.jpaminiproject.domain.menu.model.entity.Category;
import com.wanted.jpaminiproject.domain.menu.model.entity.Menu;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MenuService {

    private final MenuRepository menuRepository;
    private final CategoryRepository categoryRepository;
    private final ModelMapper modelMapper;

    /* 1. 메뉴코드로 특정 메뉴 조회하기*/
    public MenuDTO findMenuByMenuCode(int menuCode) {

        // Entity 등장!
        Menu foundMenu = menuRepository.findById(menuCode)
                                       .orElseThrow(IllegalArgumentException::new);

        // map(변환 대상, 변환 할 타입)
        MenuDTO menuDTO = modelMapper.map(foundMenu, MenuDTO.class);

        return menuDTO;
    }

    public List<MenuDTO> findMenuByPrice(int menuPrice) {

        // Entity 등장!
        List<Menu> menuList = menuRepository.findByMenuPriceGreaterThanOrderByMenuPrice(menuPrice);

        System.out.println("menuList = " + menuList);
        
        return menuList.stream()
                .map(menu -> modelMapper.map(menu, MenuDTO.class))
                .collect(Collectors.toList()); // List<MenuDTO>
                                                                    
    }

    public List<CategoryDTO> findAllcategory() {

        List<Category> categoryList = categoryRepository.findAllCategory();

        return categoryList.stream()
                .map(category -> modelMapper.map(category, CategoryDTO.class))
                .collect(Collectors.toList());
    }

    @Transactional
    public int registNewMenu(MenuDTO registMenu) {

        // save 시에는 Entity 타입을 넣어야 한다.
        // 하지만 전달받고 있는 객체 타입은 DTO 타입이기 때문에
        // 마찬가지로 modelMapper 로 DTO -> Entity 로 바꿔줄 것이다.

//        menuRepository.save(modelMapper.map(registMenu, Menu.class)); -> 아래 구문과 동일

        Menu menu = modelMapper.map(registMenu, Menu.class);
        menuRepository.save(menu);

        System.out.println("menu = " + menu);

        return menu.getMenuCode();
    }

    @Transactional
    public void modifyMenuName(int menuCode, String menuName) {

        // 수정 대상 엔티티 객체 찾아오기
        Menu foundMenu = menuRepository.findById(menuCode)
                                       .orElseThrow(IllegalArgumentException::new);

        System.out.println("영속성 컨텍스트에서 찾아온 foundMenu = " + foundMenu);
        // foundMenu 변수에는 이제 수정 대상 엔티티가 담겨있다.

        /* 1. setter 사용해서 update - setter 사용은 지양한다. */
//        foundMenu.setMenuName(menuName);

        /* 2. @Builder 어노테이션을 사용한 update 기능 */
//        foundMenu = foundMenu.toBuilder()
//                             .menuName(menuName).build();
        // 새로운 인스턴스가 대입 되는 것은
        // 영속성 컨텍스트가 아직 새로운 인스턴스를 알지 못 하는 상황이다.
//        menuRepository.save(foundMenu);

        /* 3. Entity 내부에 직접 Builder 패턴을 구현 */
        foundMenu = foundMenu.changeMenuName(menuName).builder();

        menuRepository.save(foundMenu);

    }
}
