package com.classbooking.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.util.Optional;

@Configuration
@EnableJpaAuditing
public class JpaAuditingConfig {

    @Bean
    public AuditorAware<String> auditorProvider() {
        return () -> Optional.of("system"); // 실제로는 SecurityContext에서 현재 사용자 정보를 가져와야 합니다.
        /**
         * todo: README에서 이렇게 설명:
         * 인증 정보가 없는 환경을 가정하여 기본값으로 "system"을 사용했습니다.
         */
    }
}
