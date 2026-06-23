package com.wanted.elasticbeanstalk;

import org.springframework.data.jpa.repository.JpaRepository;

public interface MenuRepository extends JpaRepository<Menu, Integer> {
    Menu findByMenuCode(int menuCode);
}
