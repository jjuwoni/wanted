package com.wanted.springtest.section04.intergration;

import java.util.List;
import java.util.Optional;

public interface UserRepository {

    /**
     * 사용자를 저장한다.
     *
     * @param user 저장할 사용자
     * @return 저장된 사용자
     */
    User save(User user);

    /**
     * ID로 사용자를 조회한다.
     *
     * @param id 사용자 ID
     * @return 사용자 정보
     */
    Optional<User> findById(Long id);

    /**
     * 이메일로 사용자를 조회한다.
     *
     * @param email 이메일
     * @return 사용자 정보
     */
    Optional<User> findByEmail(String email);

    /**
     * 이메일 존재 여부를 확인한다.
     *
     * @param email 이메일
     * @return 존재 여부
     */
    boolean existsByEmail(String email);

    /**
     * 활성 사용자 목록을 조회한다.
     *
     * @return 활성 사용자 목록
     */
    List<User> findByActiveTrue();

    /**
     * 전체 사용자 수를 조회한다.
     *
     * @return 전체 사용자 수
     */
    long count();

    /**
     * 활성 사용자 수를 조회한다.
     *
     * @return 활성 사용자 수
     */
    long countByActiveTrue();

    /**
     * 이메일에 특정 문자열이 포함된 사용자 목록을 조회한다.
     *
     * @param emailPart 이메일에 포함될 문자열
     * @return 해당하는 사용자 목록
     */
    List<User> findByEmailContaining(String emailPart);

    /**
     * 모든 사용자를 조회한다.
     *
     * @return 전체 사용자 목록
     */
    List<User> findAll();

    /**
     * 사용자를 삭제한다.
     *
     * @param id 삭제할 사용자 ID
     */
    void deleteById(Long id);
}
