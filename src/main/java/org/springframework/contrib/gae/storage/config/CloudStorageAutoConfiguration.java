package org.springframework.contrib.gae.storage.config;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.contrib.gae.storage.service.CloudStorageService;

@Configuration
@ComponentScan("org.springframework.contrib.gae.storage")
public class CloudStorageAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty("spring.contrib.gae.storage.bucket")
    @Profile({"gae"})
    public CloudStorageService cloudStorageService(@Value("${spring.contrib.gae.storage.bucket:#{null}}") String bucketName) {
        if (StringUtils.isBlank(bucketName)) {
            throw new IllegalArgumentException("${spring.contrib.gae.storage.bucket} must have a value");
        }

        return new CloudStorageService(bucketName);
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty("spring.contrib.gae.storage.bucket")
    @Profile({"!gae"})
    public CloudStorageService localCloudStorageService(@Value("${spring.contrib.gae.storage.bucket:#{null}}") String bucketName,
                                                   @Value("${spring.contrib.gae.storage.credentials:/dev-gcs-credentials.json}") String gcsCredentials,
                                                   @Value("${app.id}") String projectId) {
        if (StringUtils.isBlank(bucketName)) {
            throw new IllegalArgumentException("${spring.contrib.gae.storage.bucket} must have a value");
        }

        if (StringUtils.isBlank(projectId)) {
            throw new IllegalArgumentException("${app.id} must have a value");
        }

        return new CloudStorageService(bucketName, gcsCredentials, projectId);
    }

}
