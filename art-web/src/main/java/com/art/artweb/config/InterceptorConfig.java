package com.art.artweb.config;

import com.art.artweb.interceptor.JWTInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Slf4j
@Configuration
public class InterceptorConfig implements WebMvcConfigurer {

    @Value("${dev-pattern.devInfo}")
    private String mode;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        log.info("以{}环境启动...",mode);
        if (!mode.equals("debug")) {
            registry.addInterceptor(new JWTInterceptor())
                    .addPathPatterns("/api/**")
                    .excludePathPatterns("/api/user/register")
                    .excludePathPatterns("/api/user/sendCode")
                    .excludePathPatterns("/api/user/verifyCode");
        }else {
            registry.addInterceptor(new JWTInterceptor())
                    .addPathPatterns("/api/**")
                    .excludePathPatterns("/api/**");
        }
    }
}
