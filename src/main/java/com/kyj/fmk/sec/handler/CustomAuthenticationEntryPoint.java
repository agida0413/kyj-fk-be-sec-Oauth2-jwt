package com.kyj.fmk.sec.handler;

import com.kyj.fmk.sec.dto.res.SecurityResponse;
import com.kyj.fmk.sec.exception.SecErrCode;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import java.io.IOException;
/**
 *  * 2025-08-09
 *  * @author 김용준
 *  * 스프링 시큐리티에서 사용되는  AuthenticationEntryPoint를 구현한 클래스이다. 인증에 실패했을 때 실행된다.
 *  */
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        SecurityResponse.writeErrorRes(response, HttpStatus.UNAUTHORIZED, SecErrCode.SEC010);
    }
}
