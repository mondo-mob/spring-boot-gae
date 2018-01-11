package org.springframework.contrib.gae.security.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;

/**
 * GAE security configuration properties.
 */
@Component
@ConfigurationProperties("spring.contrib.gae.security")
public class SecurityProperties {
    /**
     * Configuration for the remember me (persistent login) service.
     */
    private RememberMe rememberMe = new RememberMe();

    public RememberMe getRememberMe() {
        return rememberMe;
    }

    public SecurityProperties setRememberMe(RememberMe rememberMe) {
        this.rememberMe = rememberMe;
        return this;
    }

    public static class RememberMe {
        /**
         * The random seed to use when generating remember me series values.
         */
        @Nonnull
        private String key;

        public String getKey() {
            return key;
        }

        public RememberMe setKey(String key) {
            this.key = key;
            return this;
        }
    }
}
