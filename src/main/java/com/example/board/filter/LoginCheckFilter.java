package com.example.board.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.util.PatternMatchUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LoginCheckFilter implements Filter{
	
	
	
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		
		HttpServletRequest httpRequest = (HttpServletRequest)request;
		HttpServletResponse httpResponse = (HttpServletResponse)response;
		
		try {
			log.info("LoginCheckFilter 실행");
			String requestURI = httpRequest.getRequestURI();
			
			if(isLoginCheckPath(requestURI)) {
				HttpSession session = httpRequest.getSession(false);
				
				if(session == null || session.getAttribute("loginMember") == null) {
					// 로그인하지 않은 상태
					log.info("로그인하지 않은 사용자의 요청");
					httpResponse.sendRedirect("/member/login");
					return;
				}
				
			}
			
			chain.doFilter(request, response);
		} catch(Exception e) {
			
		} finally {
			log.info("LoginCheckFilter 종료");
		}
		
	}
	
	private boolean isLoginCheckPath(String requestURI) {
		// 로그인 체크 X. 로그인을 하지 않아도 들어갈 수 있는 경로들
		String[] whiteList = {"/", "/member/login", "/member/join", "/*.css"};
		
		// 매치되는 값이 있으면 false, 매치되는 값이 없으면 false
		return !PatternMatchUtils.simpleMatch(whiteList, requestURI);
		
	}
	
	
	
	
	
	
	
}
