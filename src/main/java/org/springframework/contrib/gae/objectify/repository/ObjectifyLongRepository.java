package org.springframework.contrib.gae.objectify.repository;

import com.googlecode.objectify.Key;
import org.springframework.contrib.gae.objectify.ObjectifyProxy;
import org.springframework.contrib.gae.search.SearchService;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;

/**
 * Concrete repository class to extend when creating repository instances with a {@link Long} key.
 * @param <E> he type of entity this repository manages.
 */
public abstract class ObjectifyLongRepository<E> extends BaseObjectifyRepository<E, Long> {

    public ObjectifyLongRepository(ObjectifyProxy objectify, @Nullable SearchService searchService, Class<E> entityType) {
        super(objectify, searchService, entityType, Long.class);
    }

    @Nonnull
    public Optional<E> findById(Long id) {
        return findByKey(toKey(id));
    }

    @Nonnull
    public E getById(Long id) {
        return getByKey(toKey(id));
    }

    public void delete(Long id) {
        deleteByKey(toKey(id));
    }

    private Key<E> toKey(Long id) {
        return Key.create(getEntityType(), id);
    }

}
