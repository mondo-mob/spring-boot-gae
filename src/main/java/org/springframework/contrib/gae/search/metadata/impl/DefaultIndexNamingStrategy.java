package org.springframework.contrib.gae.search.metadata.impl;

import com.googlecode.objectify.annotation.Entity;
import org.apache.commons.lang3.StringUtils;
import org.springframework.contrib.gae.search.metadata.IndexNamingStrategy;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * Default index naming strategy.
 * Uses the class simple name as the index name, or the {@link Entity} name value
 * if one has been set.
 */
public class DefaultIndexNamingStrategy implements IndexNamingStrategy {
    private Map<Class, String> nameCache = new HashMap<>();

    @Override
    public String apply(Class<?> aClass) {
        return nameCache.computeIfAbsent(aClass, this::calculateIndexName);
    }

    private String calculateIndexName(Class<?> aClass) {
        return Stream.of(aClass.getAnnotationsByType(Entity.class))
                .findFirst()
                .flatMap(annotation -> Optional.ofNullable(StringUtils.trimToNull(annotation.name())))
                .orElseGet(aClass::getSimpleName);
    }
}
