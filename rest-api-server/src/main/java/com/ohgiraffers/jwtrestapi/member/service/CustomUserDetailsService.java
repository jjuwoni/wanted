package com.ohgiraffers.jwtrestapi.member.service;

import com.ohgiraffers.jwtrestapi.member.dto.MemberDTO;
import com.ohgiraffers.jwtrestapi.member.entity.Member;
import com.ohgiraffers.jwtrestapi.member.entity.MemberRole;
import com.ohgiraffers.jwtrestapi.member.repository.MemberRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/* 설명.
 *  Spring Security의 UserDetailsService를 구현해 사용자 인증 정보를 제공하고,
 *  사용자 권한을 설정하는 책임을 수행한다.
 *  앞서 먼저 배워봤던 Session 방식의 인증/인가 시스템에서도 UserDetailsService를 사용했었다.
 * */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public CustomUserDetailsService(MemberRepository memberRepository, ModelMapper modelMapper) {
        this.memberRepository = memberRepository;
        this.modelMapper = modelMapper;
    }

    @Transactional
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        // 사용자 ID로 데이터베이스에서 사용자 엔티티 조회
        Member member = memberRepository.findByMemberId(username);

        // 조회된 사용자 엔티티를 DTO로 매핑.
        // 이 MemberDTO는 엔티티를 옮겨 담는 DTO역할도 수행하지만, 결국 내부적으로 UserDetails가 구현되어 있다.
        MemberDTO memberDTO = modelMapper.map(member, MemberDTO.class);

        /* 처음 조회한 엔티티 째로는 사용자에게 할당된 권한 정보를 얻어낼 수 없다.
         * MemberDTO에 추가한 Collection<GrantedAuthority> authorities 필드 변수를 사용해야 한다.
         * 먼저 MemberDTO의 authorities 필드에 값을 할당할 빈 배열을 준비한다.
         * */
        List<GrantedAuthority> authorities = new ArrayList<>();

        // Member 엔티티 내 memberRole에서 회원별 권한을 추출해 빈 배열에 옮겨 담는다.
        for(MemberRole memberRole : member.getMemberRole()) {
            String authorityName = memberRole.getAuthority().getAuthorityName();
            authorities.add(new SimpleGrantedAuthority(authorityName));
        }

        // 모두 옮겨담은 권한 리스트를 MemberDTO에 주입해준다.
        memberDTO.setAuthorities(authorities);

        return memberDTO;
    }
}
