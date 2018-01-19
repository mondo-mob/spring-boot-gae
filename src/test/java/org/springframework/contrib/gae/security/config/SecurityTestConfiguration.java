package org.springframework.contrib.gae.security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.contrib.gae.objectify.config.ObjectifyConfigurer;
import org.springframework.contrib.gae.security.TestUserEntity;
import org.springframework.contrib.gae.security.TestUserEntityAdapter;
import org.springframework.contrib.gae.security.UserAdapter;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.spy;

@Configuration
public class SecurityTestConfiguration implements ObjectifyConfigurer {
    @Override
    public List<Class<?>> registerObjectifyEntities() {
        return Arrays.asList(
                TestUserEntity.class
        );
    }

    @Bean
    public Class<TestUserEntity> gaeUserClass() {
        return TestUserEntity.class;
    }

    @Bean
    public UserAdapter<TestUserEntity> gaeUserHelper() {
        return spy(new TestUserEntityAdapter());
    }
}
