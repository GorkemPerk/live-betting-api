package com.gorkem.livebettingapi;

import com.gorkem.livebettingapi.infrastructure.config.AppConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableJpaRepositories
@EnableJpaAuditing
@EnableScheduling
@EnableConfigurationProperties(AppConfig.class)
public class BulletinManagementApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(BulletinManagementApiApplication.class, args);
    }

}
