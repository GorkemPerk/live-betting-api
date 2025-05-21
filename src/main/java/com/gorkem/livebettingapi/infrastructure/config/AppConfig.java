package com.gorkem.livebettingapi.infrastructure.config;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@RequiredArgsConstructor
@Configuration
@ConfigurationProperties(prefix = "app")
public class AppConfig {
    private Double maxOdds;
    private Double minOdds;
    private String updateOddsCronExpression;
    private Integer timeoutMillisecond;
    private Double maxInvestmentLimit;
    private Integer maxCouponCount;
}
