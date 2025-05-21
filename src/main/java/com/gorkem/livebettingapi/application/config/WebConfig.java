package com.gorkem.livebettingapi.application.config;


import com.gorkem.livebettingapi.infrastructure.intercaptor.CustomerInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    private final CustomerInterceptor customerInterceptor;

    public WebConfig(CustomerInterceptor customerInterceptor) {
        this.customerInterceptor = customerInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(customerInterceptor)
                .addPathPatterns("/api/v1/bet/**")
                .excludePathPatterns("/swagger-ui/**", "/v3/api-docs/**");
    }
}
