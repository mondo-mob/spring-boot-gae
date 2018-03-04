package org.springframework.contrib.gae.objectify.repository.base;

import org.springframework.contrib.gae.objectify.ObjectifyProxy;
import org.springframework.contrib.gae.objectify.repository.ObjectifyLongRepository;
import org.springframework.contrib.gae.search.SearchService;

import javax.annotation.Nullable;

/**
 * Concrete repository class to extend when creating repository instances with a {@link Long} key.
 * @param <E> the type of entity this repository manages.
 */
public abstract class BaseObjectifyLongRepository<E> extends BaseObjectifyRepository<E, Long> implements ObjectifyLongRepository<E> {

    public BaseObjectifyLongRepository(ObjectifyProxy objectify, @Nullable SearchService searchService, Class<E> entityType) {
        super(objectify, searchService, entityType, Long.class);
    }

}
