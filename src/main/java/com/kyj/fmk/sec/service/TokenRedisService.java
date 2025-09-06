package com.kyj.fmk.sec.service;


import com.kyj.fmk.core.exception.custom.KyjAuthException;

import com.kyj.fmk.core.exception.custom.KyjSysException;
import com.kyj.fmk.core.model.dto.ResApiDTO;
import com.kyj.fmk.core.model.enm.CmErrCode;
import com.kyj.fmk.core.util.CookieUtil;

import com.kyj.fmk.sec.jwt.JWTUtil;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;
/**
 *  * 2025-08-09
 *  * @author 김용준
 *  * 스프링 시큐리티에서 토큰과 redis의 연관관계 및  생명주기를 관리하는 서비스
 *  */
@Service
@RequiredArgsConstructor
public class TokenRedisService implements TokenService{
    private final RedisTemplate<String,Object> redisTemplate;
    private final JWTUtil jwtUtil;
    private final String REFRESH_TOKEN_KEY = "refresh:";
    private final String BLACK_LIST_KEY = "blacklist:";

    @Override
    public void addRefresh(String key , String token) {
        TimeUnit timeUnit = TimeUnit.HOURS;
        long ttl = 24L * 7;  // 1주일 = 168시간
        String rediskey = REFRESH_TOKEN_KEY +key;
        redisTemplate.opsForValue().set(rediskey , token,ttl,timeUnit);

    }

    @Override
    public void deleteRefresh(String key , String token) {
        String rediskey = REFRESH_TOKEN_KEY+key;

        deleteRedis(rediskey,token);

    }
    private void deleteRedis(String rediskey , String token){
        String findToken =(String)redisTemplate.opsForValue().get(rediskey);

        if(findToken != null && findToken.equals(token)){
            redisTemplate.delete(rediskey);
        }
    }
    @Override
    public boolean isExist(String key,String token) {
        String redisKey = REFRESH_TOKEN_KEY+key;
        String value = (String)redisTemplate.opsForValue().get(redisKey);

        if(value.equals(token)){
            return  true;
        }

        return false;
    }



    @Override
    //최종 refresh 토큰 발급 서비스
    public ResponseEntity<ResApiDTO<Void>> reissueToken(HttpServletRequest request, HttpServletResponse response) {


        String refresh = null;


        try {

            Cookie[] cookies = request.getCookies();
            if(cookies != null){
                for (Cookie cookie : cookies) {

                    if (cookie.getName().equals("refresh")) {

                        refresh = cookie.getValue();
                    }
                }
            }

        } catch (Exception e) {
            // TODO: handle exception

            throw new KyjSysException(CmErrCode.CM003);

        }

        if (refresh == null) {//만약 refresh가 없다면
            throw new KyjAuthException(CmErrCode.SEC002);

        }

        try {
            jwtUtil.isExpired(refresh);// 유효기간 검증
        } catch (ExpiredJwtException e) {

          ResponseCookie responseCookie= CookieUtil.deleteCookie("refresh","/");//refresh 쿠키제거메서드
            response.setHeader(HttpHeaders.SET_COOKIE,responseCookie.toString());
            String usrId = jwtUtil.getUsrId(refresh);

                redisTemplate.delete(REFRESH_TOKEN_KEY+ usrId);
                throw new KyjAuthException(CmErrCode.SEC007);


        }


        String category = jwtUtil.getCategory(refresh);   // 토큰이 refresh인지 확인 (발급시 페이로드에 명시)

        if (!category.equals("refresh")) {//refresh 토큰이 아니면
                throw new KyjAuthException(CmErrCode.SEC002);
        }




        String chkUsrId=jwtUtil.getUsrId(refresh);


        Boolean isExist = isExist(chkUsrId,refresh);

        if (!isExist) {//없다면

           ResponseCookie responseCookie= CookieUtil.deleteCookie("refresh","/");//refresh 쿠키제거메서드
            response.setHeader(HttpHeaders.SET_COOKIE,responseCookie.toString());

            throw new KyjAuthException(CmErrCode.SEC007);
        }

        Boolean isExistBlackList = isExistBlackList(refresh);

        if(isExistBlackList){
            ResponseCookie responseCookie= CookieUtil.deleteCookie("refresh","/");//refresh 쿠키제거메서드
            response.setHeader(HttpHeaders.SET_COOKIE,responseCookie.toString());

            throw new KyjAuthException(CmErrCode.SEC011);
        }



        String usrId = jwtUtil.getUsrId(refresh);
        String roles = jwtUtil.getRoles(refresh);
        String usrSeqId = jwtUtil.getUsrSeqId(refresh);
        String email = jwtUtil.getEmail(refresh);




        //새로운 jwt 토큰 발급
        String nwAccess = jwtUtil.createJwt("access", usrId, usrSeqId,
                                              email,roles,300000L);//엑세스 토큰
        String nwRefresh = jwtUtil.createJwt("refresh", usrId, usrSeqId,
                                              email,roles,86400000L); //리프레시 토큰


        deleteRefresh(usrId, refresh); //Refresh 토큰 저장 DB에 기존의 Refresh 토큰 삭제 후 새 Refresh 토큰 저장


        addRefresh(usrId,nwRefresh); //새토큰 저장


        //응답 설정

        ResponseCookie responseAccessCookie= CookieUtil.createCookie("Authorization",nwAccess,5*60,"/");
        ResponseCookie responseRefreshCookie= CookieUtil.createCookie("refresh",nwRefresh,604800,"/");

        //성공시 응답
        response.addHeader(HttpHeaders.SET_COOKIE, responseAccessCookie.toString());
        response.addHeader(HttpHeaders.SET_COOKIE, responseRefreshCookie.toString());

        return ResponseEntity.ok(new ResApiDTO<Void>(null));
    }


    @Override
    public void addBlackList(String token){
        TimeUnit timeUnit = TimeUnit.HOURS;
        long ttl = 24L * 7;  // 1주일 = 168시간
        String rediskey = BLACK_LIST_KEY+token;
        redisTemplate.opsForValue().set(rediskey ,"true",ttl,timeUnit);
    }

    @Override
    public boolean isExistBlackList(String token){
        String rediskey = BLACK_LIST_KEY+token;
        String value = (String)redisTemplate.opsForValue().get(rediskey);

        if(value == null){
            return false;
        }

        return true;
    }
}
