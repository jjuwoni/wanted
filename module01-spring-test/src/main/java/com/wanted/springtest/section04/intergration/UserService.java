package com.wanted.springtest.section04.intergration;

import java.util.List;
import java.util.Optional;

public class UserService {

    /* 설명. 사용자 데이터 접근을 위한 Repository이다. */
    private final UserRepository userRepository;

    /* 설명. 생성자 주입 방식으로 의존성을 주입받는다. */
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * 사용자를 생성한다.
     *
     * @param user 생성할 사용자
     * @return 생성된 사용자
     * @throws IllegalArgumentException 사용자 정보가 유효하지 않은 경우
     */
    public User createUser(User user) {
        /* 설명. 필수 정보 검증 */
        if (user == null || user.getEmail() == null || user.getName() == null) {
            throw new IllegalArgumentException("사용자 정보가 유효하지 않습니다.");
        }

        /* 설명. 이메일 중복 검증 */
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("이미 존재하는 이메일입니다: " + user.getEmail());
        }

        return userRepository.save(user);
    }

    /**
     * 사용자를 조회한다.
     *
     * @param id 사용자 ID
     * @return 사용자 정보
     */
    public Optional<User> findUserById(Long id) {
        if (id == null || id <= 0) {
            return Optional.empty();
        }
        return userRepository.findById(id);
    }

    /**
     * 이메일로 사용자를 조회한다.
     *
     * @param email 이메일
     * @return 사용자 정보
     */
    public Optional<User> findUserByEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return Optional.empty();
        }
        return userRepository.findByEmail(email);
    }

    /**
     * 모든 활성 사용자를 조회한다.
     *
     * @return 활성 사용자 목록
     */
    public List<User> findAllActiveUsers() {
        return userRepository.findByActiveTrue();
    }

    /**
     * 사용자를 비활성화한다.
     *
     * @param id 사용자 ID
     * @throws IllegalArgumentException 사용자가 존재하지 않는 경우
     */
    public void deactivateUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다: " + id));

        user.setActive(false);
        userRepository.save(user);
    }

    /**
     * 전체 사용자 수를 조회한다.
     *
     * @return 전체 사용자 수
     */
    public long getUserCount() {
        return userRepository.count();
    }

    /**
     * 활성 사용자 수를 조회한다.
     *
     * @return 활성 사용자 수
     */
    public long getActiveUserCount() {
        return userRepository.countByActiveTrue();
    }
}
