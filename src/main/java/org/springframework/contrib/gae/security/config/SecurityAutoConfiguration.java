package org.springframework.contrib.gae.security.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.contrib.gae.objectify.config.ObjectifyConfigurer;
import org.springframework.contrib.gae.security.GaeUser;
import org.springframework.contrib.gae.security.GaeUserDetailsManager;
import org.springframework.contrib.gae.security.UserAdapter;
import org.springframework.contrib.gae.security.persistence.RememberMeToken;
import org.springframework.contrib.gae.security.persistence.RememberMeTokenRepository;
import org.springframework.contrib.gae.security.rest.RestAuthenticationFailureHandler;
import org.springframework.contrib.gae.security.rest.RestAuthenticationSuccessHandler;
import org.springframework.contrib.gae.security.rest.RestLogoutSuccessHandler;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.authentication.rememberme.PersistentTokenBasedRememberMeServices;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

@Configuration
@EnableConfigurationProperties(SecurityProperties.class)
public class SecurityAutoConfiguration implements ObjectifyConfigurer {
    private static final Logger LOG = LoggerFactory.getLogger(SecurityAutoConfiguration.class);

    private final SecurityProperties properties;

    public SecurityAutoConfiguration(SecurityProperties properties) {
        this.properties = properties;
    }

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

    @Bean
    @ConditionalOnMissingBean
    @SuppressWarnings({"OptionalUsedAsFieldOrParameterType", "unchecked"})
    public UserDetailsManager userDetailsManager(Optional<Class<? extends GaeUser>> gaeUserClass, Optional<UserAdapter<? extends GaeUser>> gaeUserHelper, PasswordEncoder encoder) {
        if (!gaeUserClass.isPresent()) {
            LOG.warn("Cannot create GaeUserDetailsManager: GaeUser implementation missing from context. Did you forget to configure it?");
            return null;
        }
        if (!gaeUserHelper.isPresent()) {
            LOG.warn("Cannot create GaeUserDetailsManager: UserAdapter implementation missing from context. Did you forget to configure it?");
            return null;
        }
        return new GaeUserDetailsManager(gaeUserClass.get(), gaeUserHelper.get(), encoder);
    }

    @Bean
    @ConditionalOnMissingBean
    public AuthenticationProvider daoAuthenticationProvider(UserDetailsManager userDetailsManager, PasswordEncoder passwordEncoder) {
        if (userDetailsManager == null) {
            LOG.warn("Cannot create DaoAuthenticationProvider: UserDetailsManager implementation missing from context. This is usually caused by an earlier configuration error.");
            return null;
        }
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsManager);
        authProvider.setPasswordEncoder(passwordEncoder);
        return authProvider;
    }

    @Bean
    @ConditionalOnMissingBean
    public RememberMeServices rememberMeService(UserDetailsManager userDetailsManager, PersistentTokenRepository persistentTokenRepository) {
        if (userDetailsManager == null) {
            LOG.warn("Cannot create PersistentTokenBasedRememberMeServices: UserDetailsManager implementation missing from context. This is usually caused by an earlier configuration error.");
            return null;
        }
        return new PersistentTokenBasedRememberMeServices(properties.getRememberMe().getKey(), userDetailsManager, persistentTokenRepository);
    }
}
