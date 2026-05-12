package com.ohgiraffers.jwtrestapi.member.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@ToString
@Table(name = "tbl_member_role")
@IdClass(MemberRolePk.class)
@Builder(toBuilder = true)
public class MemberRole {

	@Id
	@Column(name = "member_code")
	private int memberNo;
	
	@Id
	@Column(name = "authority_code")
	private int authorityCode;
	
	/* 설명. Authority 타입의 속성은 조회할 때 JOIN용도로 사용하되, INSERT 또는 UPDATE 할 때는 무시하도록 설정. */
	@ManyToOne
	@JoinColumn(name = "authority_code", insertable = false, updatable = false)
	private Authority authority;

	public MemberRole(int memberNo, int authorityCode) {
		this.memberNo = memberNo;
		this.authorityCode = authorityCode;
	}
}
