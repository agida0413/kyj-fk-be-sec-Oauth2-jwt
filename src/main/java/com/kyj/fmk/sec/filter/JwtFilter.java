package com.kyj.fmk.sec.filter;

import com.kyj.fmk.sec.aware.EndpointUrlCollector;
import com.kyj.fmk.sec.dto.member.MemberDTO;
import com.kyj.fmk.sec.dto.oauth2.CustomOAuth2User;
import com.kyj.fmk.sec.dto.res.SecurityResponse;
import com.kyj.fmk.sec.exception.SecErrCode;
import com.kyj.fmk.sec.jwt.JWTUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.util.Arrays;
/**
 *  * 2025-08-09
 *  * @author 김용준
 *  * 스프링 시큐리티에서 사용되는 jwt 필터이다.
 *  */
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JWTUtil jwtUtil;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String requestURI = request.getRequestURI();

        // UrlConst.publicUrls 리스트 안의 패턴과 비교해서 하나라도 매칭되면 필터 제외
        for (String pattern : EndpointUrlCollector.getPublicUrls()) {
            if (pathMatcher.match(pattern, requestURI)) {
                return true;  // 필터를 수행하지 않음 (즉, 필터 제외)
            }
        }

        return false; // 그 외는 필터 수행
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, java.io.IOException {

        String accessToken = null;
        String refreshToken = null;


        // 헤더에서 access키에 담긴 토큰을 꺼냄
        Cookie[] cookies = request.getCookies();
        if(cookies != null){
            for (Cookie cookie : cookies) {

                if (cookie.getName().equals("Authorization")) {

                    accessToken = cookie.getValue();
                }
                if (cookie.getName().equals("refresh")) {

                    refreshToken = cookie.getValue();
                }
            }
        }



        // 토큰이 없다면 다음 필터로 넘김
        if (accessToken == null) {

            if(refreshToken != null ){
                SecurityResponse.writeErrorRes(response, HttpStatus.GONE,SecErrCode.SEC003);
                return;
            }

            filterChain.doFilter(request, response);

            return;
        }



        //토큰 검증
        boolean result = jwtUtil.validate(accessToken,response);

        if(!result){
            return;
        }


        // 토큰이 access인지 확인 (발급시 페이로드에 명시)
        String category = jwtUtil.getCategory(accessToken);

        if (!category.equals("access")) {
            //액세스토큰이 아닐시
            SecurityResponse.writeErrorRes(response, HttpStatus.UNAUTHORIZED,SecErrCode.SEC002);
            return;
        }

        String usrId = jwtUtil.getUsrId(accessToken);
        String roles = jwtUtil.getRoles(accessToken);
        String usrSeqId = jwtUtil.getUsrSeqId(accessToken);
        String email = jwtUtil.getEmail(accessToken);

        //Memberdto를 생성하여 값 set
        MemberDTO memberDTO = new MemberDTO();
        memberDTO.setUsrSeqId(Long.parseLong(usrSeqId));
        memberDTO.setRole(roles);
        memberDTO.setUsrId(usrId);
        memberDTO.setEmail(email);




        //UserDetails에 회원 정보 객체 담기
        CustomOAuth2User customOAuth2User = new CustomOAuth2User(memberDTO,false);

        //스프링 시큐리티 인증 토큰 생성
        Authentication authToken = new UsernamePasswordAuthenticationToken(customOAuth2User, null, customOAuth2User.getAuthorities());
        //세션에 사용자 등록
        SecurityContextHolder.getContext().setAuthentication(authToken);

        filterChain.doFilter(request, response);

    }


}