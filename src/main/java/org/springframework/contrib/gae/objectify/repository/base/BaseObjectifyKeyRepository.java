package org.springframework.contrib.gae.objectify.repository.base;

import com.googlecode.objectify.Key;
import org.springframework.contrib.gae.objectify.ObjectifyProxy;
import org.springframework.contrib.gae.search.SearchService;

import javax.annotation.Nullable;

/**
 * Concrete repository class to extend when creating repository instances with a {@link Key}, for example when your entity has a {@link com.googlecode.objectify.annotation.Parent}.
 * @param <E> the type of entity this repository manages.
 */
public class BaseObjectifyKeyRepository<E> extends BaseObjectifyRepository<E, Key<E>> {

    public BaseObjectifyKeyRepository(ObjectifyProxy objectify, @Nullable SearchService searchService, Class<E> entityType, Class<Key<E>> idType) {
        super(objectify, searchService, entityType, idType);
    }

}
