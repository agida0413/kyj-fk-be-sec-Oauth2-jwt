package com.kyj.fmk.sec.repository;

import com.kyj.fmk.sec.dto.member.MemberDTO;
import com.kyj.fmk.sec.mapper.AuthMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
/**
 *  * 2025-08-09
 *  * @author 김용준
 *  * 스프링 시큐리티에서 인증관련 repo
 *  */
@Repository
@RequiredArgsConstructor
public class AuthRepository {

    private final AuthMapper authMapper;

    /**
     * 해당 유저아이디에 해당하는 데이터베이스가 존재하는지 확인하는 repo
     * @param usrId
     * @return
     */
    public Boolean isExist(String usrId){
        return authMapper.isExist(usrId);
    }

    /**
     * 유저아이디를 이용해 회원의 정보를 가져오는 repo
     * @param usrId
     * @return
     */
    public MemberDTO findByUsrId(String usrId){
        return authMapper.findByUsrId(usrId);
    }

    /**
     * 회원가입(member 테이블)
     * @param memberDTO
     */
    public void insertMember(MemberDTO memberDTO){
        authMapper.insertMember(memberDTO);
    }

    /**
     * 회원정보수정(member)
     * @param memberDTO
     */
    public void updateMember(MemberDTO memberDTO){
        authMapper.updateMember(memberDTO);
    }

}
