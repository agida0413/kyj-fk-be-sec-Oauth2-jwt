package com.kyj.fmk.sec.dto.oauth2;
/**
 *  * 2025-08-09
 *  * @author 김용준
 *  * 스프링 시큐리티에서 각 소셜 인증서버로 부터 받은 정보를 추출하기 위한 OAuth2Response 인터페이스
 *  */
public interface OAuth2Response {

    //제공자 (Ex. naver, google, ...)
    String getProvider();
    //제공자에서 발급해주는 아이디(번호)
    String getProviderId();
    //이메일
    String getEmail();
    //사용자 실명 (설정한 이름)
    String getName();
}