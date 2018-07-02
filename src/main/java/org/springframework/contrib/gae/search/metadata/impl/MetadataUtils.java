package org.springframework.contrib.gae.search.metadata.impl;

import com.google.common.collect.Sets;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.Ref;
import org.apache.commons.lang3.reflect.TypeUtils;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Set;

/**
 * Utilities for managing object types.
 */
public class MetadataUtils {

    private static final Set<Class<?>> ALLOWED_PARAM_TYPES = Sets.newHashSet(Ref.class, Key.class);

    /**
     * Get the raw type of a given type.
     *
     * @param type The type.
     * @return Taw type.
     */
    public static Class<?> getRawType(Type type) {
        System.out.println("Getting type: " + type);
        if (type instanceof Class) {
            return (Class<?>) type;
        } else if (type instanceof ParameterizedType) {
            Type rawType = ((ParameterizedType) type).getRawType();
            if (rawType instanceof Class && ALLOWED_PARAM_TYPES.contains(rawType)) {
                return (Class<?>) rawType;
            }
        }
        throw new IllegalArgumentException("Unsupported type: " + type);
    }

    /**
     * Return whether the given type is a collection type.
     * Arrays are considered a collection type.
     *
     * @param type The type.
     * @return Whether the given type is a collection.
     */
    public static boolean isCollectionType(Type type) {
        return TypeUtils.isAssignable(type, Collection.class)
                || TypeUtils.isArrayType(type);
    }

    /**
     * Return the type of the content of the given collection type.
     *
     * @param type The collection type.
     * @return Collection type.
     */
    public static Class<?> getCollectionType(Type type) {
        if (TypeUtils.isAssignable(type, Collection.class)) {
            if (type instanceof ParameterizedType) {
                Type genericType = ((ParameterizedType) type).getActualTypeArguments()[0];

                if (genericType instanceof Class) {
                    return (Class<?>) genericType;
                }
            } else {
                throw new IllegalArgumentException("Cannot infer index type for non-parameterized type: " + type);
            }
        } else if (TypeUtils.isArrayType(type)) {
            return (Class<?>) TypeUtils.getArrayComponentType(type);
        }
        throw new IllegalArgumentException("Unsupported type: " + type);
    }
}
