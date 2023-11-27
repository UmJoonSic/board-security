package com.example.board.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.board.config.UserInfo;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class HomeController {

	//메인페이지 이동
	@GetMapping("/")
  public String home(@AuthenticationPrincipal UserInfo userInfo,
  									Model model) {
		model.addAttribute("loginUser", userInfo);
		
	  return "index";
  }
	
	@GetMapping("admin")
	public String adminHome(@AuthenticationPrincipal UserInfo userInfo, Model model) {
		model.addAttribute("adminUser", userInfo);
		
		return "/admin/index";
	}
	
}
