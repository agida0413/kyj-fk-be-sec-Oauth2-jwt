package com.kyj.fmk.sec.service;

import com.kyj.fmk.sec.dto.member.MemberDTO;
import com.kyj.fmk.sec.dto.oauth2.*;
import com.kyj.fmk.sec.repository.AuthRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
/**
 *  * 2025-08-09
 *  * @author 김용준
 *  * 스프링 시큐리티에서 ouath2 로부터 리다이렉트된 정보를 추출하여 처리하는 서비스
 *  */
@Service
@RequiredArgsConstructor
public class CustomOauth2UserService extends DefaultOAuth2UserService {

    private final AuthRepository authRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
       OAuth2User oAuth2User = super.loadUser(userRequest);

        OAuth2Response oAuth2Response = null;
        String registrationId = userRequest.getClientRegistration().getRegistrationId();

        if(registrationId.equals(UserClientEnum.NAVER.getValue())){
            oAuth2Response = new NaverResponse(oAuth2User.getAttributes());
        } else if (registrationId.equals(UserClientEnum.GOOGLE.getValue())) {
            oAuth2Response = new GoogleResponse(oAuth2User.getAttributes());
        } else if (registrationId.equals(UserClientEnum.KAKAO.getValue())){
            oAuth2Response = new KakaoResponse(oAuth2User.getAttributes());
        }  else{
            return null;
        }
        MemberDTO mem =  new MemberDTO();

        String usrId = oAuth2Response.getProvider()+"_"+oAuth2Response.getProviderId();
        Boolean isExist = authRepository.isExist(usrId);
        mem.setUsrId(usrId);

        if (isExist == null || !isExist) {
            //usrid값 멤버dto담기
            //추가정보입력 플래그 true

            return new CustomOAuth2User(mem,true);
        }
        else {

            //회원정보 셀렉트
            //usr_id 업데이트
            //추가정보입력 플래그 False

            return new CustomOAuth2User(mem,false);
        }

    }
}
