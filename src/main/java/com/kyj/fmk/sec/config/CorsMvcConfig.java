package com.kyj.fmk.sec.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
/**
 *  * 2025-08-09
 *  * @author 김용준
 *  * 스프링 시큐리티에서 cors설정
 *  */
@Configuration
@Profile("local")
public class CorsMvcConfig implements WebMvcConfigurer {


    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost", "http://127.0.0.1")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowCredentials(true) // 쿠키 포함 허용
                .maxAge(3600);
    }
}
