package com.nexusbank.admin.config;

import feign.Logger;
import feign.RequestInterceptor;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Configuration
public class FeignConfig {
    @Bean
    Logger.Level feignLoggerLevel() {
        return Logger.Level.FULL;
    }

    @Bean
    public RequestInterceptor requestInterceptor() {
        return requestTemplate -> {
            RequestAttributes attrs = RequestContextHolder.getRequestAttributes();
            if (attrs instanceof ServletRequestAttributes servletAttrs) {
                HttpServletRequest request = servletAttrs.getRequest();
                String auth = request.getHeader(HttpHeaders.AUTHORIZATION);
                String userName = request.getHeader("X-User-Name");
                String userRole = request.getHeader("X-User-Role");
                if (auth != null) {
                    requestTemplate.header(HttpHeaders.AUTHORIZATION, auth);
                }
                if (userName != null) {
                    requestTemplate.header("X-User-Name", userName);
                }
                if (userRole != null) {
                    requestTemplate.header("X-User-Role", userRole);
                }
            }
        };
    }
}