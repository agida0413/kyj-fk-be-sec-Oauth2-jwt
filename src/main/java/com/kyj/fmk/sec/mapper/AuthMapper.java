package com.kyj.fmk.sec.mapper;

import com.kyj.fmk.sec.dto.member.MemberDTO;
import org.apache.ibatis.annotations.Mapper;
/**
 *  * 2025-08-09
 *  * @author 김용준
 *  * 스프링 시큐리티에서 인증관련 mapper
 *  */
@Mapper
public interface AuthMapper {
    public Boolean isExist(MemberDTO memberDTO);
    public MemberDTO findByUsrId(MemberDTO memberDTO);
    public void insertMember(MemberDTO memberDTO);
    public void updateMember(MemberDTO memberDTO);

}
