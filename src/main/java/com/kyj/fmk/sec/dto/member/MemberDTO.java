package com.kyj.fmk.sec.dto.member;

import com.kyj.fmk.sec.jwt.JWTUtil;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
/**
 *  * 2025-08-09
 *  * @author 김용준
 *  * 스프링 시큐리티에서 로그인,회원가입,시큐리티 컨텍스트 홀더에 담기위한 유저객체
 *  */
@Getter
@Setter
public class MemberDTO {
    private long usrSeqId;
    private String email;
    private String usrId;
    private String nickname;
    private String dtyCd;
    private int career;
    private String isDel;
    private String role;
    private List<String> skillCds;


}
