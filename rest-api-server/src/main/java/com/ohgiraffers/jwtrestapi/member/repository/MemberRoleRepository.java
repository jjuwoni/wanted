package com.ohgiraffers.jwtrestapi.member.repository;

import com.ohgiraffers.jwtrestapi.member.entity.MemberRole;
import com.ohgiraffers.jwtrestapi.member.entity.MemberRolePk;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRoleRepository extends JpaRepository<MemberRole, MemberRolePk> {
}
