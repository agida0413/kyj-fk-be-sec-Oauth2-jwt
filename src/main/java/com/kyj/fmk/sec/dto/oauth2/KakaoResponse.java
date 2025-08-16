package com.kyj.fmk.sec.dto.oauth2;

import java.util.Map;
/**
 *  * 2025-08-09
 *  * @author 김용준
 *  * 스프링 시큐리티에서 OAuth2Response를 구현한 카카오 리스폰스 객체
 *  */
public class KakaoResponse implements OAuth2Response {
    private final Map<String, Object> attribute;

    public KakaoResponse(Map<String, Object> attribute) {
        this.attribute = attribute;
    }

    @Override
    public String getProvider() {
        return UserClientEnum.KAKAO.getValue();
    }

    @Override
    public String getProviderId() {
        return attribute.get("id").toString();
    }

    @Override
    public String getEmail() {
        Map<String, Object> account = (Map<String, Object>) attribute.get("kakao_account");
        return account.get("email").toString();
    }

    @Override
    public String getName() {
        Map<String, Object> account = (Map<String, Object>) attribute.get("kakao_account");
        Map<String, Object> profile = (Map<String, Object>) account.get("profile");
        return profile.get("nickname").toString();
    }
}
