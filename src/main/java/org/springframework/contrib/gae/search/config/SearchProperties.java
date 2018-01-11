package org.springframework.contrib.gae.search.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.contrib.gae.search.metadata.IndexNamingStrategy;
import org.springframework.contrib.gae.search.metadata.impl.DefaultIndexNamingStrategy;
import org.springframework.stereotype.Component;

/**
 * GAE Search configuration properties.
 */
@Component
@ConfigurationProperties("spring.contrib.gae.search")
public class SearchProperties {

    /**
     * Search index naming strategy to use.
     */
    private Class<? extends IndexNamingStrategy> indexNamingStrategy = DefaultIndexNamingStrategy.class;

    public Class<? extends IndexNamingStrategy> getIndexNamingStrategy() {
        return indexNamingStrategy;
    }

    public SearchProperties setIndexNamingStrategy(Class<? extends IndexNamingStrategy> indexNamingStrategy) {
        this.indexNamingStrategy = indexNamingStrategy;
        return this;
    }
}
