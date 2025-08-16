package com.kyj.fmk.sec.service;

import com.kyj.fmk.core.model.dto.ResApiDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
/**
 *  * 2025-08-09
 *  * @author 김용준
 *  * 스프링 시큐리티에서 토큰에 대한 생명주기를 관리하는 서비스 인터페이스
 *  */
public interface TokenService {
    public void addRefresh(String key ,String token);
    public void deleteRefresh(String key,String token);
    public boolean isExist(String key,String token);
    public ResponseEntity<ResApiDTO<Void>> reissueToken(HttpServletRequest request, HttpServletResponse response);
    public void addBlackList(String token);
    public boolean isExistBlackList(String token);
}
