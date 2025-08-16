package com.kyj.fmk.sec.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.HandlerMapping;

import java.io.IOException;
import java.util.List;
/**
 *  * 2025-08-09
 *  * @author 김용준
 *  * 404 응답을 내리기위한 필터이다.
 *  */
@Component
@RequiredArgsConstructor
@Slf4j
public class PreCheckHandlerMappingFilter extends OncePerRequestFilter {

    private final List<HandlerMapping> handlerMappings;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        boolean matched = false;
        for (HandlerMapping mapping : handlerMappings) {
            // 정적 리소스 핸들러는 무시
            try {
                HandlerExecutionChain handler = mapping.getHandler(request);
                if (handler != null) {
                    matched = true;
                    break;
                }
            } catch (Exception e) {
                // 필요하면 로그 찍기
                log.debug("Handler check failed: {}", e.getMessage());
            }
        }

        if (!matched) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        filterChain.doFilter(request, response);
    }
}
