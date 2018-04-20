package org.springframework.contrib.gae.storage.config;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.contrib.gae.storage.service.CloudStorageService;

@Configuration
@ComponentScan("org.springframework.contrib.gae.storage")
public class CloudStorageAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty("spring.contrib.gae.storage.bucket")
    public CloudStorageService cloudStorageService(@Value("${spring.contrib.gae.storage.bucket:#{null}}") String bucketName) {
        if (StringUtils.isBlank(bucketName)) {
            throw new IllegalArgumentException("${spring.contrib.gae.storage.bucket} must have a value");
        }

        return new CloudStorageService(bucketName);
    }
}
