//package com.kyj.fmk.sec.aware;
//
//import com.kyj.fmk.sec.annotation.PrivateEndpoint;
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
//public class PrivateUrlCollector implements ApplicationContextAware {
//
//    private List<String> privateUrls = new ArrayList<>(UrlConst.privateUrls);
//
//    @Override
//    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
//        RequestMappingHandlerMapping mapping = applicationContext.getBean(RequestMappingHandlerMapping.class);
//        Map<RequestMappingInfo, HandlerMethod> handlerMethods = mapping.getHandlerMethods();
//
//        for (Map.Entry<RequestMappingInfo, HandlerMethod> entry : handlerMethods.entrySet()) {
//            HandlerMethod handlerMethod = entry.getValue();
//            if (handlerMethod.hasMethodAnnotation(PrivateEndpoint.class)) {
//                Set<String> patterns = entry.getKey().getPatternValues();
//                privateUrls.addAll(patterns);
//            }
//        }
//    }
//
//    public List<String> getPublicUrls() {
//        return Collections.unmodifiableList(privateUrls.stream().distinct().toList());
//    }
//}
//
