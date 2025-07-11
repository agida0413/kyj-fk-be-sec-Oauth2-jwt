//package com.kyj.fmk.sec.aware;
//
//import com.kyj.fmk.sec.annotation.PublicEndpoint;
//import com.kyj.fmk.sec.aware.UrlConst;
//import org.springframework.beans.BeansException;
//import org.springframework.context.ApplicationContext;
//import org.springframework.context.ApplicationContextAware;
//import org.springframework.stereotype.Component;
//import org.springframework.web.method.HandlerMethod;
//import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
//import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
//
//import java.util.*;
//
//@Component
//public class PublicUrlCollector implements ApplicationContextAware {
//
//    private final List<String> publicUrls = new ArrayList<>(UrlConst.publicUrls);
//
//    @Override
//    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
//        RequestMappingHandlerMapping mapping = applicationContext.getBean(RequestMappingHandlerMapping.class);
//        Map<RequestMappingInfo, HandlerMethod> handlerMethods = mapping.getHandlerMethods();
//
//        for (Map.Entry<RequestMappingInfo, HandlerMethod> entry : handlerMethods.entrySet()) {
//            HandlerMethod handlerMethod = entry.getValue();
//            if (handlerMethod.hasMethodAnnotation(PublicEndpoint.class)) {
//                Set<String> patterns = entry.getKey().getPatternValues();
//                publicUrls.addAll(patterns);
//            }
//        }
//    }
//
//
//    public List<String> getPublicUrls() {
//        return Collections.unmodifiableList(publicUrls.stream().distinct().toList());
//    }
//}
