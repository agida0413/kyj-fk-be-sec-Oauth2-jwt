package com.kyj.fmk.sec.controller;

import com.kyj.fmk.core.model.dto.ResApiDTO;
import com.kyj.fmk.sec.service.TokenService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class TokenController {
    private final TokenService tokenService;

    @RequestMapping("/api/v1/member/reissue")
    public ResponseEntity<ResApiDTO<Void>> reissueToken(HttpServletRequest request, HttpServletResponse response){
        return tokenService.reissueToken(request,response);
    }
}
