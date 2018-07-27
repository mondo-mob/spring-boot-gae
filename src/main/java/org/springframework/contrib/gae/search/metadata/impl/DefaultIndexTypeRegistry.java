package org.springframework.contrib.gae.search.metadata.impl;

import com.google.appengine.api.search.GeoPoint;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.Ref;
import org.springframework.contrib.gae.search.IndexType;
import org.springframework.contrib.gae.search.metadata.IndexTypeRegistry;

import java.lang.reflect.Type;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;

/**
 * {@link DefaultIndexTypeRegistry}.
 */
public class DefaultIndexTypeRegistry implements Function<Type, IndexType>, IndexTypeRegistry {
    private Map<Class<?>, IndexType> mappings = new HashMap<>();

    /**
     * Create a new instance.
     */
    public DefaultIndexTypeRegistry() {
        addDefaultMappings();
    }

    /**
     * Add a custom type mapping.
     *
     * @param type      Java type.
     * @param indexType Index type.
     */
    @Override
    public void addMapping(Class<?> type, IndexType indexType) {
        mappings.put(type, indexType);
    }

    @Override
    @SuppressWarnings("SuspiciousMethodCalls")
    public IndexType apply(Type type) {
        if (MetadataUtils.isCollectionType(type)) {
            return getIndexType(
                    MetadataUtils.getCollectionType(type)
            );
        }
        return getIndexType(type);
    }

    private IndexType getIndexType(Type type) {
        Class<?> rawType = MetadataUtils.getRawType(type);
        Optional<IndexType> indexType = Optional.ofNullable(mappings.get(rawType));

        return indexType.orElseGet(() -> {
            for (Map.Entry<Class<?>, IndexType> entry : mappings.entrySet()) {
                if (entry.getKey().isAssignableFrom(rawType)) {
                    IndexType result = entry.getValue();
                    mappings.put(rawType, result);
                    return result;
                }
            }

            throw new IllegalArgumentException(
                    String.format("Unknown type: %s", type)
            );
        });
    }

    protected void addDefaultMappings() {
        mappings.put(Number.class, IndexType.NUMBER);
        mappings.put(Short.class, IndexType.NUMBER);
        mappings.put(Integer.class, IndexType.NUMBER);
        mappings.put(short.class, IndexType.NUMBER);
        mappings.put(int.class, IndexType.NUMBER);
        mappings.put(long.class, IndexType.NUMBER);
        mappings.put(float.class, IndexType.NUMBER);
        mappings.put(double.class, IndexType.NUMBER);

        mappings.put(boolean.class, IndexType.IDENTIFIER);
        mappings.put(Boolean.class, IndexType.IDENTIFIER);
        mappings.put(Enum.class, IndexType.IDENTIFIER);
        mappings.put(UUID.class, IndexType.IDENTIFIER);

        mappings.put(Ref.class, IndexType.IDENTIFIER);
        mappings.put(Key.class, IndexType.IDENTIFIER);

        mappings.put(CharSequence.class, IndexType.TEXT);

        mappings.put(Date.class, IndexType.DATE);
        mappings.put(OffsetDateTime.class, IndexType.NUMBER);
        mappings.put(ZonedDateTime.class, IndexType.NUMBER);

        mappings.put(GeoPoint.class, IndexType.GEOPOINT);
    }
}
