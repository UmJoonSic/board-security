package com.example.board.model.member;
/**
 *  서로 연관된 상수들의 집합
 */
public enum GenderType {
	MALE("남성"),
	FEMALE("여성");
	
	private String description;
	
	private GenderType(String description) {
		this.description = description;
	}
	
	public String getDescription() {
		return description;
	}
}
