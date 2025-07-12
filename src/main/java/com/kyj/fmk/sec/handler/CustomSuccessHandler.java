package com.kyj.fmk.sec.handler;

import com.kyj.fmk.core.util.CookieUtil;
import com.kyj.fmk.sec.dto.CustomOAuth2User;
import com.kyj.fmk.sec.dto.res.SecurityResponse;
import com.kyj.fmk.sec.exception.SecErrCode;
import com.kyj.fmk.sec.jwt.JWTUtil;
import com.kyj.fmk.sec.service.TokenRedisService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class CustomSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JWTUtil jwtUtil;
    private final TokenRedisService tokenRedisService;



    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        CustomOAuth2User customUserDetails = (CustomOAuth2User) authentication.getPrincipal();

        deleteOriginalInfo(request,response);

        String username = customUserDetails.getUsername();
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

        String roles = authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));


        //새로운 jwt 토큰 발급
        String access = jwtUtil.createJwt("access", username,roles,300000L);//엑세스 토큰
        String refresh = jwtUtil.createJwt("refresh",  username,roles,86400000L); //리프레시 토큰

        tokenRedisService.addRefresh(username,refresh);

        ResponseCookie responseAccessCookie= CookieUtil.createCookie("Authorization",access, 5 * 60,"/");
        ResponseCookie responseRefreshCookie= CookieUtil.createCookie("refresh",refresh,604800,"/");

        //성공시 응답
        response.addHeader(HttpHeaders.SET_COOKIE, responseAccessCookie.toString());
        response.addHeader(HttpHeaders.SET_COOKIE, responseRefreshCookie.toString());
        response.sendRedirect("http://localhost:8080/my");
//        response.sendRedirect("http://localhost:8080/my"); 쿠키로 이동할 페이지를 받아 파라미터로 받아 넘겨준다.
    }


    private void deleteOriginalInfo(HttpServletRequest request,
                                    HttpServletResponse response){
        String access = null;
        String refresh = null;

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


        if(access != null || refresh != null ){
            List<String> target = new ArrayList<>();

            if(access != null){
                target.add(access);
            }
            if(refresh != null){
                target.add(refresh);
            }

            if(target.size()>0){

                for (String token : target){
                    String username = jwtUtil.getUsername(token);
                    String category = jwtUtil.getCategory(token);

                    if(category.equals("refresh")){
                        tokenRedisService.deleteRefresh(username,refresh);
                        ResponseCookie responseCookie= CookieUtil.deleteCookie("refresh","/");//refresh 쿠키제거메
                        response.addHeader(HttpHeaders.SET_COOKIE, responseCookie.toString());

                    } else if (category.equals("access")) {
                        ResponseCookie responseCookie= CookieUtil.deleteCookie("Authorization","/");//refresh 쿠키제거
                        response.addHeader(HttpHeaders.SET_COOKIE, responseCookie.toString());

                    }

                }
            }



        }

    }
}
