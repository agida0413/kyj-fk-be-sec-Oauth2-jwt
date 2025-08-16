package com.kyj.fmk.sec.aware;

import java.util.List;
/**
 *  * 2025-08-09
 *  * @author 김용준
 *  * 스프링 시큐리티에서 인가설정을 위한 public/private을 정적으로 선언하는 클래스
 *  */
public final class UrlConst {

    private static final String API_BASE_URL = "/api/v1/member/";
    public static final List<String> publicUrls = List.of(
            "/index.html",
            "/css/**",
            "/js/**",
            "/images/**",
            "/favicon.ico",
            "/fonts/**",
            "/img/**",
            "/",
            "/login" ,   // 개발시 변경
            "/error",
            "/actuator/health/readiness",
            "/actuator/health/liveness",
            API_BASE_URL+"login",
            API_BASE_URL+"reissue"
    );

    public static final List<String> privateUrls = List.of(
            API_BASE_URL+"logout"
    );

}
