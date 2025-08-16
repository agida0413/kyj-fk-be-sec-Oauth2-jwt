package com.kyj.fmk.sec.jwt;


import com.kyj.fmk.sec.dto.res.SecurityResponse;
import com.kyj.fmk.sec.exception.SecErrCode;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

import java.util.Date;
/**
 *  * 2025-08-09
 *  * @author 김용준
 *  * 스프링 시큐리티에서 사용되는 JWTUtil
 *  */
@Component
public final class JWTUtil {

    private final SecretKey secretKey;

    public JWTUtil(@Value("${spring.jwt.secret}") String secret) {

        //application.properties에 저장된 secretkey 암호 알고리즘 통해 생성사를 통해 secretkey 생성
        secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), Jwts.SIG.HS256.key().build().getAlgorithm());
    }




    public Boolean isExpired(String token) {

        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().getExpiration().before(new Date());
    }

    public boolean validate(String token, HttpServletResponse response) {
        try {
            Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token);
        } catch (SignatureException e) {
            SecurityResponse.writeErrorRes(response, HttpStatus.UNAUTHORIZED,SecErrCode.SEC002);
            return false;
        } catch (MalformedJwtException e) {
            SecurityResponse.writeErrorRes(response, HttpStatus.UNAUTHORIZED,SecErrCode.SEC002);
            return false;
        } catch (ExpiredJwtException e) {
            SecurityResponse.writeErrorRes(response, HttpStatus.GONE,SecErrCode.SEC003);
            return false;
        } catch (UnsupportedJwtException e) {
            SecurityResponse.writeErrorRes(response, HttpStatus.UNAUTHORIZED,SecErrCode.SEC002);
            return false;
        } catch (IllegalArgumentException e) {
            SecurityResponse.writeErrorRes(response, HttpStatus.UNAUTHORIZED,SecErrCode.SEC002);
            return false;
        } catch (Exception e){
            SecurityResponse.writeErrorRes(response, HttpStatus.UNAUTHORIZED,SecErrCode.SEC002);
            return false;
        }

        return true;
    }



    public String getUsrId(String token) {

        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("usrId", String.class);
    }

    public String getCategory(String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("category", String.class);
    }

    public String getNickname(String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("nickname", String.class);
    }


    public String getUsrSeqId(String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("usrSeqId", String.class);

    }


    public String getRoles(String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("roles", String.class);
    }

    public String getSkillCds(String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("skillCds", String.class);
    }

    public String getEmail(String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("email", String.class);
    }

    public String getDtyCd(String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("dtyCd", String.class);
    }

    public String getCareer(String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("career", String.class);
    }

    //토큰을 만듬
    public String createJwt(String category, String usrId,String usrSeqId ,
                            String nickname, String roles , String skillCds ,
                            String email,String dtyCd,String career, Long expiredMs) {

        return Jwts.builder()
                .claim("category", category) //refresh토큰인지 access 토큰인지
                .claim("usrId", usrId) //이름
                .claim("usrSeqId", usrSeqId)//아이디
                .claim("nickname", nickname)//닉네임
                .claim("skillCds", skillCds)
                .claim("email", email)
                .claim("roles",roles)
                .claim("dtyCd",dtyCd)
                .claim("career",career)
                .issuedAt(new Date(System.currentTimeMillis()))//만든날
                .expiration(new Date(System.currentTimeMillis() + expiredMs))//유효기간
                .signWith(secretKey)//시크릿키
                .compact();
    }


    //토큰을 만듬
    public String createJoinJwt(String category, String usrId, Long expiredMs) {

        return Jwts.builder()
                .claim("category", category)
                .claim("usrId", usrId) //이름
                .issuedAt(new Date(System.currentTimeMillis()))//만든날
                .expiration(new Date(System.currentTimeMillis() + expiredMs))//유효기간
                .signWith(secretKey)//시크릿키
                .compact();
    }

}
