package org.springframework.contrib.gae.search.metadata;

import org.springframework.contrib.gae.search.IndexType;

import java.lang.reflect.Type;
import java.util.function.Function;

/**
 * Determine the {@link IndexType} for fields that do not explicitly specify an index type.
 */
public interface IndexTypeRegistry extends Function<Type, IndexType> {

    /**
     * Add a new type mapping to the lookup.
     *
     * @param type      Java type.
     * @param indexType Index type.
     */
    void addMapping(Class<?> type, IndexType indexType);
}
