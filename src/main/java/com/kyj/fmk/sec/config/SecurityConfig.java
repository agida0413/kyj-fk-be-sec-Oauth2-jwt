package com.kyj.fmk.sec.config;


import com.kyj.fmk.sec.aware.EndpointUrlCollector;
import com.kyj.fmk.sec.filter.PreCheckHandlerMappingFilter;
import com.kyj.fmk.sec.handler.CustomAuthenticationEntryPoint;
import com.kyj.fmk.sec.filter.CustomLogoutFilter;
import com.kyj.fmk.sec.filter.JwtFilter;
import com.kyj.fmk.sec.handler.CustomLogoutSuccessHandler;
import com.kyj.fmk.sec.jwt.JWTUtil;
import com.kyj.fmk.sec.handler.CustomSuccessHandler;
import com.kyj.fmk.sec.service.CustomOauth2UserService;
import com.kyj.fmk.sec.service.TokenService;
import jakarta.servlet.http.HttpServletRequest;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.client.web.OAuth2LoginAuthenticationFilter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.servlet.HandlerMapping;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
/**
 *  * 2025-08-09
 *  * @author 김용준
 *  * 스프링 시큐리티에 filter chain및 설정을 하는 클래스
 *  */
@EnableWebSecurity
@Configuration
public class SecurityConfig {

    private final CustomOauth2UserService customOauth2UserService;
    private final JWTUtil jwtUtil;
    private final CustomSuccessHandler customSuccessHandler;
    private final TokenService tokenService;
    private final List<HandlerMapping> handlerMappings;
    private Environment env;

    private final EndpointUrlCollector endpointUrlCollector;




    public SecurityConfig(CustomOauth2UserService customOauth2UserService,
                          JWTUtil jwtUtil,
                          CustomSuccessHandler customSuccessHandler,
                          TokenService tokenService, List<HandlerMapping> handlerMappings,
                          EndpointUrlCollector endpointUrlCollector,
                          Environment env){
        this.customOauth2UserService = customOauth2UserService;
        this.jwtUtil = jwtUtil;
        this.customSuccessHandler = customSuccessHandler;
        this.tokenService = tokenService;
        this.handlerMappings = handlerMappings;
        this.endpointUrlCollector = endpointUrlCollector;
        this.env=env;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{

        List<String> publicUrls = endpointUrlCollector.getPublicUrls();
        List<String> privateUrls = endpointUrlCollector.getPrivateUrls();


            //로컬에서만 동작
        boolean isLocal = Arrays.asList(env.getActiveProfiles()).contains("local");

        if(isLocal){
            http
                    .cors(corsCustomizer -> corsCustomizer.configurationSource(new CorsConfigurationSource() {

                        @Override
                        public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {

                            CorsConfiguration configuration = new CorsConfiguration();

                            configuration.setAllowedOrigins(Collections.singletonList("http://localhost:3000"));
                            configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
                            configuration.setAllowCredentials(true);
                            configuration.setAllowedHeaders(Collections.singletonList("*"));
                            configuration.setMaxAge(3600L);


                            // 노출할 헤더 (쿠키 + 인증 토큰 등)
                            configuration.setExposedHeaders(Arrays.asList("Set-Cookie", "Authorization"));

                            return configuration;
                        }
                    }));
        }

        //csrf disable
        http
                .csrf((auth) -> auth.disable());

        //From 로그인 방식 disable
        http
                .formLogin((auth)->auth.disable());

        //HTTP Basic 인증 방식 disable
        http
                .httpBasic((auth) -> auth.disable());

        //oauth2
        http
                .oauth2Login((oauth2) -> oauth2
                        .userInfoEndpoint((userInfoEndpointConfig) -> userInfoEndpointConfig
                                .userService(customOauth2UserService))
                        .successHandler(customSuccessHandler)
//                       .defaultSuccessUrl("http://localhost:8080/oauth-success", true) // 프론트엔드 SPA 경로
                );
        //예외처리
        http
                .exceptionHandling((ex)->
                        ex.authenticationEntryPoint(new CustomAuthenticationEntryPoint()));
        //로그아웃
        http    .logout((lg) ->
                        lg.logoutSuccessHandler(new CustomLogoutSuccessHandler()));
        //JWTFilter 추가
        http
                .addFilterAfter(new JwtFilter(jwtUtil), OAuth2LoginAuthenticationFilter.class);
        //커스텀한 로그아웃 필터를 등록 =>기존 필터위치에
        http
                .addFilterBefore(new CustomLogoutFilter(jwtUtil, tokenService), LogoutFilter.class);
        //404응답을 위한 필터(핸들러매핑을 찾아 없는 url이면 404반환)
        http
                .addFilterBefore(new PreCheckHandlerMappingFilter(handlerMappings), FilterSecurityInterceptor.class);
        //경로별 인가 작업
        http
                .authorizeHttpRequests((auth) -> auth
                        .requestMatchers(publicUrls.toArray(new String[0])).permitAll()
                        .requestMatchers(privateUrls.toArray(new String[0])).authenticated()
                        .anyRequest().permitAll());

        //세션 설정 : STATELESS
        http
                .sessionManagement((session) -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS));


        return http.build();
    }
}
