package com.example.board.repository;

import org.apache.ibatis.annotations.Mapper;

import com.example.board.model.member.Member;

@Mapper
public interface MemberMapper {
	void saveMember(Member member);
	Member findMember(String member_id); 
}
