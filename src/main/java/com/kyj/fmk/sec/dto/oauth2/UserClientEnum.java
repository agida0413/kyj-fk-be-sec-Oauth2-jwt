package com.kyj.fmk.sec.dto.oauth2;

import lombok.Getter;
/**
 *  * 2025-08-09
 *  * @author 김용준
 *  * 스프링 시큐리티에서 각 소셜 인증주체를 판별하기 위한 이넘
 *  */
@Getter
public enum UserClientEnum {

    NAVER("naver"),
    GOOGLE("google"),
    KAKAO("kakao"),
    NORMAL("normal");

    private final String value;

    UserClientEnum(String value) {
        this.value = value;
    }
}
