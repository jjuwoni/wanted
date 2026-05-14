package com.wanted.springevent.user.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "password_hash")
    private String passwordHash;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private UserStatus status;

    @Column(name = "last_login_at")
    private LocalDateTime lastLoginAt;

    @Column(name = "created_at", nullable = false, updatable = false, insertable = false)
    private LocalDateTime createdAt;

    protected User() {
    }

    private User(String email, String passwordHash, String name, UserStatus status) {
        this.email = email;
        this.passwordHash = passwordHash;
        this.name = name;
        this.status = status;
    }

    public static User create(String email, String passwordHash, String name, UserStatus status) {
        return new User(email, passwordHash, name, status);
    }

    public Long getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }
}
