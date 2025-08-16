package com.kyj.fmk.sec.handler;

import com.kyj.fmk.sec.dto.res.SecurityResponse;
import com.kyj.fmk.sec.exception.SecErrCode;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import java.io.IOException;
/**
 *  * 2025-08-09
 *  * @author 김용준
 *  * 스프링 시큐리티에서 사용되는  AccessDeniedHandler구현한 클래스이다. (인가실패)
 *  */
public class CustomAccessDeniedHandler implements AccessDeniedHandler {
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        SecurityResponse.writeErrorRes(response, HttpStatus.FORBIDDEN, SecErrCode.SEC011);
    }
}
