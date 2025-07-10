package com.kyj.fmk.sec.filter;

import com.kyj.fmk.sec.config.UrlConst;
import com.kyj.fmk.sec.dto.CustomOAuth2User;
import com.kyj.fmk.sec.dto.UserDTO;
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

@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JWTUtil jwtUtil;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String requestURI = request.getRequestURI();

        // UrlConst.publicUrls 리스트 안의 패턴과 비교해서 하나라도 매칭되면 필터 제외
        for (String pattern : UrlConst.publicUrls) {
            if (pathMatcher.match(pattern, requestURI)) {
                return true;  // 필터를 수행하지 않음 (즉, 필터 제외)
            }
        }

        return false; // 그 외는 필터 수행
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, java.io.IOException {

        String accessToken = null;



        // 헤더에서 access키에 담긴 토큰을 꺼냄
        Cookie[] cookies = request.getCookies();
        if(cookies != null){
            for (Cookie cookie : cookies) {

                if (cookie.getName().equals("Authorization")) {

                    accessToken = cookie.getValue();
                }
            }
        }



        // 토큰이 없다면 다음 필터로 넘김
        if (accessToken == null) {

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



        String username = jwtUtil.getUsername(accessToken);
        String role = jwtUtil.getRoles(accessToken);

        //userDTO를 생성하여 값 set
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername(username);
        userDTO.setRole(role);


        //UserDetails에 회원 정보 객체 담기
        CustomOAuth2User customOAuth2User = new CustomOAuth2User(userDTO);

        //스프링 시큐리티 인증 토큰 생성
        Authentication authToken = new UsernamePasswordAuthenticationToken(customOAuth2User, null, customOAuth2User.getAuthorities());
        //세션에 사용자 등록
        SecurityContextHolder.getContext().setAuthentication(authToken);

        filterChain.doFilter(request, response);
        // 🔁 체인 이후에도 SecurityContext 유지 확인
        Authentication postAuth = SecurityContextHolder.getContext().getAuthentication();
        if (postAuth != null) {
            System.out.println("🔍 필터 이후에도 인증 유지: " + postAuth.getName() + " / " + postAuth.getAuthorities());
        } else {
            System.out.println("⚠️ 필터 이후 인증 정보 없음 (SecurityContext 비어 있음)");
        }
    }
}