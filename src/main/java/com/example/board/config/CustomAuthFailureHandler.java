package com.example.board.config;

import java.io.IOException;
import java.net.URLEncoder;

import javax.security.auth.login.CredentialExpiredException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class CustomAuthFailureHandler extends SimpleUrlAuthenticationFailureHandler{
	
	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException exception) throws IOException, ServletException {
		log.info("실패 핸들러 호출 : {}" , exception);
		
		String errorMessage;
		
		if(exception instanceof BadCredentialsException) {
			errorMessage = "아이디 또는 패스워드가 맞지 않습니다.";
		} else if (exception instanceof LockedException) {
			errorMessage = "잠겨있는 계정입니다.";
		} else {
			errorMessage = "알 수 없는 오류로 로그인을 실패했습니다.";
		}
		
		errorMessage = URLEncoder.encode(errorMessage, "UTF-8");
		
		setDefaultFailureUrl("/member/login?error=true&message=" + errorMessage);
		super.onAuthenticationFailure(request, response, exception);
	}
	
	
	
	
	
	
}
