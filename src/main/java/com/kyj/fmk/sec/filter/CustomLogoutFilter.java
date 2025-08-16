package com.kyj.fmk.sec.filter;

import com.kyj.fmk.sec.dto.res.SecurityResponse;
import com.kyj.fmk.sec.exception.SecErrCode;


import com.kyj.fmk.sec.jwt.JWTUtil;
import com.kyj.fmk.sec.service.TokenService;
import com.kyj.fmk.core.util.CookieUtil;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;
/**
 *  * 2025-08-09
 *  * @author 김용준
 *  * 스프링 시큐리티에서 로그아웃을 위한 필터이다.
 *  */
@RequiredArgsConstructor
public class CustomLogoutFilter extends GenericFilterBean {

    private final JWTUtil jwtUtil;
    private final TokenService tokenService;


    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        doFilter((HttpServletRequest) request, (HttpServletResponse) response, chain);
    }

    private void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {


        String requestUri = request.getRequestURI();//요청의 request url

        //api 요청이 /api/logout 이 아닐경우 다음필터로 넘김
        if (!requestUri.matches("^\\/api/v1/member/logout$")) {

            filterChain.doFilter(request, response);
            return;
        }
        String requestMethod = request.getMethod(); //post? get? put?

//        if (!requestMethod.equals("POST")) {
//            //만약 post 요청이 아닐경우 다음 필터로 넘김
//            filterChain.doFilter(request, response);
//            return;
//        }

        //쿠키에서 refresh토큰을 가져옴


        String refresh = null;
        String access = null;
        try {
            Cookie[] cookies = request.getCookies();
            if(cookies != null){
                for (Cookie cookie : cookies) {

                    if (cookie.getName().equals("Authorization")) {

                        access = cookie.getValue();
                    } else if (cookie.getName().equals("refresh")) {
                        refresh=cookie.getValue();
                    }
                }
            }
            //쿠키를 읽어오는 메서드
        } catch (Exception e) {
            // TODO: handle exception
            //쿠키 읽는 과정 에러 발생시
            SecurityResponse.writeErrorRes(response, HttpStatus.INTERNAL_SERVER_ERROR, SecErrCode.SEC006);
            return;
        }
        //토큰이 없을 경우

//        if (refresh == null || access == null) {
//
//            SecurityResponse.writeErrorRes(response, HttpStatus.UNAUTHORIZED, SecErrCode.SEC002);
//            return;
//        }

        //유효기간 검증
//        try {
//            jwtUtil.isExpired(refresh);
//            jwtUtil.isExpired(access);
//        } catch (ExpiredJwtException e) {
//
//            //refresh 쿠키제거메서드
//            ResponseCookie responseCookie1 = CookieUtil.deleteCookie("refresh", "/");
//            ResponseCookie responseCookie2 = CookieUtil.deleteCookie("Authorization", "/");
//
//            response.addHeader(HttpHeaders.SET_COOKIE,responseCookie1.toString());
//            response.addHeader(HttpHeaders.SET_COOKIE,responseCookie2.toString());
//
//            SecurityResponse.writeErrorRes(response,HttpStatus.UNAUTHORIZED,SecErrCode.SEC007);  //세션이 만료되었습니다.
//
//            return;
//        }
//
//        // 토큰이 refresh인지 확인 (발급시 페이로드에 명시)
//        String category = jwtUtil.getCategory(refresh);
//        String category2 = jwtUtil.getCategory(access);
//        if (!category.equals("refresh")) {
//
//            SecurityResponse.writeErrorRes(response, HttpStatus.UNAUTHORIZED, SecErrCode.SEC002);
//            return;
//
//        } else if (!category2.equals("access")) {
//            SecurityResponse.writeErrorRes(response, HttpStatus.UNAUTHORIZED, SecErrCode.SEC002);
//            return;
//        }
//
//
//        String usrId = jwtUtil.getUsername(refresh); // 레디스 키값
//        //DB에 저장되어 있는지 확인
//        Boolean isExist = tokenService.isExist(usrId,refresh);
//        if (!isExist) {
//
//            //refresh 쿠키제거메서드
//            ResponseCookie responseCookie1 = CookieUtil.deleteCookie("refresh", "/");
//            ResponseCookie responseCookie2 = CookieUtil.deleteCookie("Authorization", "/");
//            response.addHeader(HttpHeaders.SET_COOKIE,responseCookie1.toString());
//            response.addHeader(HttpHeaders.SET_COOKIE,responseCookie2.toString());
//
//            SecurityResponse.writeErrorRes(response, HttpStatus.UNAUTHORIZED, SecErrCode.SEC002);
//            return;
//        }

        //로그아웃 진행
        //Refresh 토큰 DB에서 제거
        if(refresh != null){
            String usrId = jwtUtil.getUsrId(refresh); // 레디스 키값
            tokenService.deleteRefresh(usrId, refresh);
            //refresh 쿠키제거메서드
            ResponseCookie responseCookie1 = CookieUtil.deleteCookie("refresh", "/");
            response.addHeader(HttpHeaders.SET_COOKIE,responseCookie1.toString());
        }



        if(access != null){
            ResponseCookie responseCookie2 = CookieUtil.deleteCookie("Authorization", "/");
            response.addHeader(HttpHeaders.SET_COOKIE,responseCookie2.toString());
        }

        //블랙리스트 등록
        tokenService.addBlackList(refresh);

        //성공 응답값
        SecurityResponse.writeSuccessRes(response);

    }




}
