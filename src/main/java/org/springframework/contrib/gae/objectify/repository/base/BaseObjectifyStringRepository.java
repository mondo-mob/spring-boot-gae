package org.springframework.contrib.gae.objectify.repository.base;

import org.springframework.contrib.gae.objectify.ObjectifyProxy;
import org.springframework.contrib.gae.objectify.repository.ObjectifyStringRepository;
import org.springframework.contrib.gae.search.SearchService;

import javax.annotation.Nullable;

/**
 * Concrete repository class to extend when creating repository instances with a {@link String} key.
 * @param <E> the type of entity this repository manages.
 */
public abstract class BaseObjectifyStringRepository<E> extends BaseObjectifyRepository<E, String> implements ObjectifyStringRepository<E> {

    public BaseObjectifyStringRepository(ObjectifyProxy objectify, @Nullable SearchService searchService, Class<E> entityType) {
        super(objectify, searchService, entityType, String.class);
    }

}
