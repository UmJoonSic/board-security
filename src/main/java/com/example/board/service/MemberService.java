package com.example.board.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.board.model.member.Member;
import com.example.board.model.member.RoleType;
import com.example.board.repository.BoardMapper;
import com.example.board.repository.MemberMapper;
import com.example.board.util.FileService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly=true)
public class MemberService {
	
	//데이터베이스 접근을 위한 BoardMapper 필드 선언
  private final MemberMapper memberMapper;
  private final PasswordEncoder passwordEncoder;
  
  public Member findMember(String member_id) {
  	
  	return memberMapper.findMember(member_id);
  }
  
  @Transactional
	public void saveMember(Member member) {
  	String rawPassword = member.getPassword();
  	// 비크립트 방식으로 암호화
  	String encPassword = passwordEncoder.encode(rawPassword);
  	member.setPassword(encPassword);
  	log.info("encpassword : {}", encPassword);
  	
  	// 기본 ROLE 부여
  	member.setRole(RoleType.ROLE_USER);
  	
		memberMapper.saveMember(member);
	}
	
	
	
	
	
	
	
	
}
