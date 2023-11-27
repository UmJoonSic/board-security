package com.example.board.config;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.example.board.model.member.Member;

import lombok.Getter;

@Getter
public class UserInfo implements UserDetails{
	
	private Member member;
	
	public UserInfo(Member member) {
		this.member = member;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		// 사용자의 권한을 리턴
		Collection<GrantedAuthority> collect = new ArrayList<>();
		collect.add(new SimpleGrantedAuthority(member.getRole().name()));
		
		return collect;
	}

	@Override
	public String getPassword() {
		// 패스워드를 리턴
		return member.getPassword();
	}

	@Override
	public String getUsername() {
		// 아이디를 리턴
		return member.getMember_id();
	}

	@Override
	public boolean isAccountNonExpired() {
		// 계정의 기한 만료 여부
		// false : 만료가 됐다 -> 로그인 불가능
		// true : 만료가 안됐다 -> 로그인 가능
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		// 계정의 잠금 여부
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		// 접속 권한 만료 여부
		return true;
	}

	@Override
	public boolean isEnabled() {
		// 계정 사용 가능 여부
		return true;
	}
	
}
