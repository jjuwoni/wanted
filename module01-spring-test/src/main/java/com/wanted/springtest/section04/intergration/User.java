package com.wanted.springtest.section04.intergration;

public class User {

    private Long id;
    private String name;
    private String email;
    private boolean active;

    // 기본 생성자
    public User() {
        this.active = true;
    }

    // 전체 생성자
    public User(Long id, String name, String email, boolean active) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.active = active;
    }

    // 편의 생성자 (ID 제외)
    public User(String name, String email) {
        this.name = name;
        this.email = email;
        this.active = true;
    }

    // Getter 메서드들
    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }



    public boolean isActive() {
        return active;
    }

    // Setter 메서드들
    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }



    public void setActive(boolean active) {
        this.active = active;
    }
}
