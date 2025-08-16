package com.kyj.fmk.sec.handler;


import com.kyj.fmk.core.redis.RedisKey;
import com.kyj.fmk.core.util.CookieUtil;
import com.kyj.fmk.sec.dto.oauth2.CustomOAuth2User;
import com.kyj.fmk.sec.dto.res.SecurityResponse;
import com.kyj.fmk.sec.exception.SecErrCode;
import com.kyj.fmk.sec.jwt.JWTUtil;
import com.kyj.fmk.sec.service.TokenRedisService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
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
/**
 *  * 2025-08-09
 *  * @author 김용준
 *  * 스프링 시큐리티에서 사용되는 인증에 성공했을 때 호출되는 핸들러이다 .
 *  */
@Component
@RequiredArgsConstructor
public class CustomSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JWTUtil jwtUtil;
    private final TokenRedisService tokenRedisService;

    @Value("${spring.security.oauth2.login.success-url}")
    private String successUrl;

    @Value("${spring.security.oauth2.login.addition-info-url}")
    private String additionInfoUrl;

    /**
     * ouath2인증 성공시 추가정보입력 혹은 성공페이지 리다이렉트 분기하는 핸들러
     * @param request
     * @param response
     * @param authentication
     * @throws IOException
     * @throws ServletException
     */
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        CustomOAuth2User customUserDetails = (CustomOAuth2User) authentication.getPrincipal();

        boolean isAdditionalInfo = customUserDetails.isAdditionalInfo();

        if(isAdditionalInfo){
            isAddtionInfoToJoin(request, response, authentication);
        }else{
            isExistMemberToLogin(request, response, authentication);
        }
    }


    /**
     * 추가정보 입력페이지
     * @param request
     * @param response
     * @param authentication
     * @throws IOException
     */
    private void isAddtionInfoToJoin(HttpServletRequest request,
                                     HttpServletResponse response,
                                     Authentication authentication) throws IOException {

        CustomOAuth2User customUserDetails = (CustomOAuth2User) authentication.getPrincipal();

        String joinJwt = jwtUtil.createJoinJwt("joinJwt",customUserDetails.getUsrId(),10 * 60 * 1000L);
        //회원가입 토큰 레디스저장

        tokenRedisService.addRefresh(RedisKey.MEMBER_ADDITIONL_INFO,joinJwt);
        //회원가입 추가정보입력을 위한 토큰 쿠키전송
        ResponseCookie joinJwtCookie= CookieUtil.createCookie("joinJwt",joinJwt,600,"/");

        response.addHeader(HttpHeaders.SET_COOKIE, joinJwtCookie.toString());
        response.sendRedirect(additionInfoUrl);

    }

    /**
     * 이미 존재하는 회원 , 성공리다이렉트
     * @param request
     * @param response
     * @param authentication
     * @throws IOException
     */
    private void isExistMemberToLogin( HttpServletRequest request,
                                       HttpServletResponse response,
                                       Authentication authentication) throws IOException {
        CustomOAuth2User customUserDetails = (CustomOAuth2User) authentication.getPrincipal();

        deleteOriginalInfo(request,response);

        String usrId = customUserDetails.getUsrId();
        String usrSeqId = String.valueOf(customUserDetails.getUsrSeqId());
        String email = customUserDetails.getEmail();
        String nickname = customUserDetails.getNickname();
        String dtyCd = customUserDetails.getDtyCd();
        String career = String.valueOf(customUserDetails.getCareer());

        List<String> skillCds = customUserDetails.getSkillCds();
        String skillCdsStr = (skillCds != null) ? String.join(",", skillCds) : "";

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        String roles = authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));


        //새로운 jwt 토큰 발급
        String access = jwtUtil.createJwt("access", usrId, usrSeqId,nickname,skillCdsStr,
                email,roles,dtyCd,career,300000L);//엑세스 토큰
        String refresh = jwtUtil.createJwt("refresh", usrId, usrSeqId,nickname,skillCdsStr,
                email,roles,dtyCd,career,86400000L); //리프레시 토큰

        tokenRedisService.addRefresh(usrId,refresh);

        ResponseCookie responseAccessCookie= CookieUtil.createCookie("Authorization",access, 5 * 60,"/");
        ResponseCookie responseRefreshCookie= CookieUtil.createCookie("refresh",refresh,604800,"/");

        //성공시 응답
        response.addHeader(HttpHeaders.SET_COOKIE, responseAccessCookie.toString());
        response.addHeader(HttpHeaders.SET_COOKIE, responseRefreshCookie.toString());
        response.sendRedirect(successUrl);
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
                    String usrId = jwtUtil.getUsrId(token);
                    String category = jwtUtil.getCategory(token);

                    if(category.equals("refresh")){
                        tokenRedisService.deleteRefresh(usrId,refresh);
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
