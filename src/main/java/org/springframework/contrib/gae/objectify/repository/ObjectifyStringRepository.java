package org.springframework.contrib.gae.objectify.repository;

import com.googlecode.objectify.Key;
import org.springframework.data.repository.NoRepositoryBean;

import javax.annotation.Nonnull;
import java.util.Optional;

/**
 * Base repository type for {@link org.springframework.stereotype.Repository} injected objectify repositories with {@link String} id.
 *
 * @param <E> Entity type.
 */
@NoRepositoryBean
public interface ObjectifyStringRepository<E> extends SearchRepository<E, String> {

    @Nonnull
    default Optional<E> findById(String id) {
        return findByKey(toKey(id));
    }

    @Nonnull
    default E getById(String id) {
        return getByKey(toKey(id));
    }

    default void delete(String id) {
        deleteByKey(toKey(id));
    }

    default Key<E> toKey(String id) {
        return Key.create(getEntityType(), id);
    }

}
