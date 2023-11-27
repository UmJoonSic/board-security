package com.example.board.config;

import javax.servlet.Filter;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.example.board.filter.LogFilter;
import com.example.board.filter.LoginCheckFilter;


@Configuration
public class WebConfig implements WebMvcConfigurer{
	
}
