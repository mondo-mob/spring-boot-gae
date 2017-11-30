package org.springframework.contrib.gae.search.metadata.impl;

import org.springframework.contrib.gae.search.metadata.IndexNamingStrategy;

/**
 * Fully qualified index naming strategy.
 * Uses the fully qualified class name as the index name, preventing collisions but resulting in uglier index names.
 */
public class FQIndexNamingStrategy implements IndexNamingStrategy {
    @Override
    public String apply(Class<?> aClass) {
        return aClass.getName();
    }
}
