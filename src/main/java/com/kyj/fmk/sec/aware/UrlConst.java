package com.kyj.fmk.sec.aware;

import java.util.List;

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
            API_BASE_URL+"login",
            API_BASE_URL+"reissue"
    );

    public static final List<String> privateUrls = List.of(
            API_BASE_URL+"logout"
    );

}
