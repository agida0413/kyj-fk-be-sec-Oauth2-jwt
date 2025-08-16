package com.kyj.fmk.sec.filter;

import jakarta.annotation.PostConstruct;
import jakarta.servlet.Filter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.stereotype.Component;


//import java.util.List;
///**
// *  * 2025-08-09
// *  * @author 김용준
// *  * 스프링 시큐리티에서 현재 등록된 필터를 확인
// *  */
//@Component
//@Slf4j
//public class FilterChainInspector {
//
//    @Autowired
//    private FilterChainProxy filterChainProxy;
//
//    @PostConstruct
//    public void logFilters() {
//        List<SecurityFilterChain> filterChains = filterChainProxy.getFilterChains();
//
//        for (int i = 0; i < filterChains.size(); i++) {
//            SecurityFilterChain chain = filterChains.get(i);
//            System.out.println("🔗 Filter Chain #" + (i + 1));
//            for (Filter filter : chain.getFilters()) {
//                System.out.println(" - " + filter.getClass().getSimpleName());
//            }
//        }
//    }
//}