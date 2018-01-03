package org.springframework.contrib.gae.security.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.contrib.gae.objectify.config.ObjectifyConfigurer;
import org.springframework.contrib.gae.security.entity.RememberMeToken;
import org.springframework.contrib.gae.security.repository.RememberMeTokenRepository;
import org.springframework.contrib.gae.security.rest.RestAuthenticationFailureHandler;
import org.springframework.contrib.gae.security.rest.RestAuthenticationSuccessHandler;
import org.springframework.contrib.gae.security.rest.RestLogoutSuccessHandler;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;

import java.util.Collection;
import java.util.Collections;

@Configuration
public class SecurityAutoConfiguration implements ObjectifyConfigurer {

    @Override
    public Collection<Class<?>> registerObjectifyEntities() {
        return Collections.singletonList(RememberMeToken.class);
    }

    @Bean
    @ConditionalOnMissingBean
    public PasswordEncoder encoder() {
        return new BCryptPasswordEncoder(11);
    }

    @Bean
    @ConditionalOnMissingBean
    public AuthenticationSuccessHandler restAuthenticationSuccessHandler() {
        return new RestAuthenticationSuccessHandler();
    }

    @Bean
    @ConditionalOnMissingBean
    public AuthenticationFailureHandler restAuthenticationFailureHandler() {
        return new RestAuthenticationFailureHandler();
    }

    @Bean
    @ConditionalOnMissingBean
    public LogoutSuccessHandler restLogoutSuccessHandler() {
        return new RestLogoutSuccessHandler();
    }

    @Bean
    @ConditionalOnMissingBean
    public PersistentTokenRepository persistentTokenRepository() {
        return new RememberMeTokenRepository();
    }
}
