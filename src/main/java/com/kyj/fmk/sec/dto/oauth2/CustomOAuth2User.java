package com.kyj.fmk.sec.dto.oauth2;

import com.kyj.fmk.sec.dto.member.MemberDTO;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.*;

/**
 *  * 2025-08-09
 *  * @author 김용준
 *  * 스프링 시큐리티에서 OAuth2User를 구현한 커스텀 OAuth2User객체
 *  */
public class CustomOAuth2User implements OAuth2User {

    private final MemberDTO memberDTO;

    public CustomOAuth2User(MemberDTO memberDTO,boolean additionalInfo) {
     this.memberDTO = memberDTO;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return Collections.emptyMap();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        Collection<GrantedAuthority> collection = new ArrayList<>();

        collection.add(new GrantedAuthority() {


            @Override
            public String getAuthority() {
                return memberDTO.getRole();
            }
        });

        return collection;
    }

    @Override
    public String getName() {
        return memberDTO.getUsrId();
    }

    public  String getUsrId(){
        return memberDTO.getUsrId();
    }
    public Long getUsrSeqId(){
        return memberDTO.getUsrSeqId();
    }
    public  String getEmail(){
        return memberDTO.getEmail();
    }

}
