package org.springframework.contrib.gae.objectify.repository;

import com.googlecode.objectify.Key;
import org.springframework.contrib.gae.objectify.ObjectifyProxy;
import org.springframework.contrib.gae.search.SearchService;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;

/**
 * Concrete repository class to extend when creating repository instances with a {@link String} key.
 * @param <E> he type of entity this repository manages.
 */
public abstract class ObjectifyStringRepository<E> extends BaseObjectifyRepository<E, String> {

    public ObjectifyStringRepository(ObjectifyProxy objectify, @Nullable SearchService searchService, Class<E> entityType) {
        super(objectify, searchService, entityType, String.class);
    }

    @Nonnull
    public Optional<E> findById(String id) {
        return findByKey(toKey(id));
    }

    @Nonnull
    public E getById(String id) {
        return getByKey(toKey(id));
    }

    public void delete(String id) {
        deleteByKey(toKey(id));
    }

    private Key<E> toKey(String id) {
        return Key.create(getEntityType(), id);
    }

}
