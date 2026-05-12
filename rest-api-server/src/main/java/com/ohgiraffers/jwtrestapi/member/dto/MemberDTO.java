package com.ohgiraffers.jwtrestapi.member.dto;

import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class MemberDTO implements UserDetails {

    private int memberCode;
    private String memberId;
    private String memberPassword;
    private String memberName;
    private String memberEmail;
    private List<MemberRoleDTO> memberRole;

    // ============================================================================

    /* 설명. UserDetails 상속
     *  UserDetails는 Spring Security에서 사용자 정보를 저장하는 인터페이스다.
     *  - 사용자의 유효성 여부(isEnabled, isAccountNonExpired 등)
     *  - 사용자 비밀번호, 사용자명
     *  - 사용자에게 부여된 권한(GrantedAuthority)
     *  ===========================================================================
     *  UserDetails를 상속받으면 필수로 오버라이딩 해야하는 추상 메소드들이 있다.
     *  단축키 'ctrl + O'를 입력해 오버라이딩 해야하는 메소드 7개를 모두 선택한다.
     *  물론, 이들 중 필요한 추상 메소드들만 재정의하여 사용할 것이다.
     * */

    /* 설명.
     *  여기서 authorities는 해당 사용자에게 부여된 권한의 집합을 저장한다.
     *  GrantedAuthority 인터페이스는 권한 표현을 위해 주로 SimpleGrantedAuthority 클래스를 사용해 구현한다.
     *  관리자, 일반 사용자, 방문자 등과 같은 역할을 나타내는 'ROLE_'으로 시작하는 문자열로 표현되곤 한다.
     *  권한이 여러 개인 경우 콤마(,)로 분리할 수 있다.
     * */
    private Collection<GrantedAuthority> authorities;

    /* 설명.
     *  해당 사용자에 권한 목록을 설정할 때 사용할 setter()를 추가해야 한다.
     *  Spring Security는 인증된 사용자가 요청하는 특정 자원에 대한 권한이 있는지를 이 권한 목록을 통해 판단한다.
     * */
    public void setAuthorities(Collection<GrantedAuthority> authorities) {
        this.authorities = authorities;
    }

    /* 설명.
     *  현재 사용자가 부여받은 권한 컬렉션을 반환한다.(상속받은 UserDetails 인터페이스 사용)
     *  Spring Security는 이 정보(권한 컬렉션)를 사용해 접근 제어 결정을 판단한다.
     * */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.authorities;
    }

    /* 설명.
     *  사용자의 비밀번호를 반환한다.
     *  (입력받은 사용자 비밀번호와 데이터베이스에 저장된 비밀번호를 비교하여 인증하는 데 사용)
     * */
    @Override
    public String getPassword() {
        return this.memberPassword;
    }

    /* 설명.
     *  사용자의 아이디(username)를 반환한다.
     *  (입력받은 사용자 아이디를 가지고 사용자를 검색하는데 사용)
     * */
    @Override
    public String getUsername() {
        return this.memberId;   // memberName이 아니라 memberId를 사용해야 한다는 것을 주의!
    }

    /* 설명. 이하 추상 메소드들은 그대로 두고 사용하지 않는다. */
    @Override
    public boolean isAccountNonExpired() {
        return false;
    }

    @Override
    public boolean isAccountNonLocked() {
        return false;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return false;
    }

    @Override
    public boolean isEnabled() {
        return false;
    }
}
