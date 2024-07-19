package com.yedam.app.board.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer{
	@Value("${file.upload.path}")
	private String uploadPath;
	
	//경로등록
	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/images/**")	//URL 하위경로로 매핑하는 의미(경로표시할땐 ** / *하위 모든대상)
				.addResourceLocations("file:///"+uploadPath,"");	//실제 경로 프로토콜기반 http 윈도우 환경문제(///)
	}
	
}
