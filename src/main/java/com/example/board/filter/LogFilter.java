package com.example.board.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LogFilter implements Filter{
	
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		log.info("LogFilter Init");
	}
	
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		log.info("LogFilter 실행--------------------");
		try {
			HttpServletRequest httpRequest = (HttpServletRequest)request;
			String requestURI = httpRequest.getRequestURI();
			log.info("requestURI : {}", requestURI);
			
			chain.doFilter(request, response);
		} catch(Exception e) {
			
		} finally {
			log.info("LogFilter 종료--------------------");
		}
	}
	
	@Override
	public void destroy() {
		log.info("LogFilter Destroy");
	}

}
