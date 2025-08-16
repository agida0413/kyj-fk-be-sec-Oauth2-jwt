package com.kyj.fmk.sec.aware;

import com.kyj.fmk.sec.annotation.PrivateEndpoint;
import com.kyj.fmk.sec.annotation.PublicEndpoint;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.util.*;
/**
 *  * 2025-08-09
 *  * @author 김용준
 *  * 스프링 시큐리티에서 endpoint에 대한 인가설정을 urlconst와 requestmapping의 어노테이션 리플렉션 정보를 읽어와 설정한다.
 *  */
@Component
public class EndpointUrlCollector implements ApplicationContextAware {

    private static final Set<String> publicUrls =Collections.synchronizedSet(new HashSet<>());
    private static final Set<String> privateUrls = Collections.synchronizedSet(new HashSet<>());
    private static final Set<String> defaultPrivateUrls = Collections.synchronizedSet(new HashSet<>());

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        RequestMappingHandlerMapping mapping = applicationContext.getBean("requestMappingHandlerMapping", RequestMappingHandlerMapping.class);
        Map<RequestMappingInfo, HandlerMethod> handlerMethods = mapping.getHandlerMethods();

        for (Map.Entry<RequestMappingInfo, HandlerMethod> entry : handlerMethods.entrySet()) {
            HandlerMethod handlerMethod = entry.getValue();
            Set<String> patterns = entry.getKey().getPatternValues();

            boolean isPublic = handlerMethod.hasMethodAnnotation(PublicEndpoint.class);
            boolean isPrivate = handlerMethod.hasMethodAnnotation(PrivateEndpoint.class);

            if (isPublic) {
                publicUrls.addAll(patterns);
            } else if (isPrivate) {
                privateUrls.addAll(patterns);
            } else {
                defaultPrivateUrls.addAll(patterns);
            }
        }

        // 💡 1. 정적 URL 병합
        publicUrls.addAll(UrlConst.publicUrls);    // 퍼블릭 정적 URL
        privateUrls.addAll(UrlConst.privateUrls);  // 프라이빗 정적 URL

        // 💡 2. 어노테이션 없는 핸들러 → 프라이빗으로 취급
        privateUrls.addAll(defaultPrivateUrls);

        // 💡 3. 중복 제거: public이 우선, private에서 public URL 제거
        privateUrls.removeAll(publicUrls);
    }

    public static List<String> getPublicUrls() {
        return publicUrls.stream().sorted().toList();
    }



    public static List<String> getPrivateUrls() {
        return privateUrls.stream().sorted().toList();
    }
}


