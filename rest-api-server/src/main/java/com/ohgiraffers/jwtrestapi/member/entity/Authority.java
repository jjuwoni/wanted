package com.ohgiraffers.jwtrestapi.member.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@ToString
@Table(name = "tbl_authority")
@Builder(toBuilder = true)
public class Authority {

	/* 설명. @GeneratedValue 생략
	 *  향후, 프로젝트의 설계 방향에 따라 회원의 권한을 추가하는 로직이 생길 수 있다.
	 *  (ex: '관리자 또는 인사팀 권한을 가진 사용자는 사용자에게 부여되는 새로운 권한을 추가할 수 있다.')
	 *  (ex: '영업팀이 신설되어 영업팀에 소속된 사용자들에게 부여할 SALES 권한을 추가해야 한다.')
	 *  그러면 해당 테이블에 새로운 권한이 INSERT 될 것인데, 이 때 @GeneratedValue를 추가해 사용하면 된다.
	 * */
	@Id
	@Column(name = "authority_code")
	private int authorityCode;
	
	@Column(name = "authority_name")
	private String authorityName;
	
	@Column(name = "authority_desc")
	private String authorityDesc;


}





