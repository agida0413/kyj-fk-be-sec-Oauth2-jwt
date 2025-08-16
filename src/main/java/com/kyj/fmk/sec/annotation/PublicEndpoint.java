package com.kyj.fmk.sec.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
/**
 *  * 2025-08-09
 *  * @author 김용준
 *  * 스프링 시큐리티에서 public endpoint로 컨트롤러를 설정하기위한 어노테이션
 *  */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface PublicEndpoint {
}
