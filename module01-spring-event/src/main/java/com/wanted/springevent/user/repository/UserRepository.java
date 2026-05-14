package com.wanted.springevent.user.repository;

import com.wanted.springevent.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
