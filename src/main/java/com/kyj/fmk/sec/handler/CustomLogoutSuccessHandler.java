package com.kyj.fmk.sec.handler;

import com.kyj.fmk.sec.dto.res.SecurityResponse;
import com.kyj.fmk.sec.service.TokenService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import java.io.IOException;
/**
 *  * 2025-08-09
 *  * @author 김용준
 *  * 스프링 시큐리티에서 사용되는 로그아웃에 성공하였을때 실행되는 핸들러이다.
 *  */
@RequiredArgsConstructor
public class CustomLogoutSuccessHandler implements LogoutSuccessHandler {


    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        SecurityResponse.writeSuccessRes(response);
    }

}
