package com.gorkem.livebettingapi.infrastructure.intercaptor;

import com.gorkem.livebettingapi.domain.context.CustomerContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@RequiredArgsConstructor
@Slf4j
public class CustomerInterceptor implements HandlerInterceptor {
    public static final String CUSTOMER_CONTEXT_ATTRIBUTE = "customerContext";
    public static final String CUSTOMER_ID = "x-customer-id";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String requestURI = request.getRequestURI();

        String customerId = request.getHeader(CUSTOMER_ID);
        if (customerId.isBlank()) {
            throw new RuntimeException("x-customer-id is required");
        }

        CustomerContext customerContext = CustomerContext.builder()
                .id(customerId)
                .build();

        request.setAttribute(CUSTOMER_CONTEXT_ATTRIBUTE, customerContext);
        request.setAttribute(CUSTOMER_ID, customerId);

        log.debug("Customer id: {} accessed {}", customerId, requestURI);

        return true;
    }

    private boolean isPublicPath(String requestURI) {
        return requestURI.startsWith("/api/v1/bet") ||
                requestURI.startsWith("/swagger-ui") ||
                requestURI.startsWith("/v3/api-docs") ||
                requestURI.equals("/api/v1/health");
    }

}
