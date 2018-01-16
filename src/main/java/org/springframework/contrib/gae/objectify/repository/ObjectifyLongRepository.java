package org.springframework.contrib.gae.objectify.repository;

import com.googlecode.objectify.Key;
import org.springframework.data.repository.NoRepositoryBean;

import javax.annotation.Nonnull;
import java.util.Optional;

/**
 * Base repository type for {@link org.springframework.stereotype.Repository} injected objectify repositories with {@link Long} id.
 *
 * @param <E> Entity type.
 */
@NoRepositoryBean
public interface ObjectifyLongRepository<E> extends SearchRepository<E, Long> {

    @Nonnull
    default Optional<E> findById(Long id) {
        return findByKey(toKey(id));
    }

    @Nonnull
    default E getById(Long id) {
        return getByKey(toKey(id));
    }

    default void delete(Long id) {
        deleteByKey(toKey(id));
    }

    default Key<E> toKey(Long id) {
        return Key.create(getEntityType(), id);
    }
    
}
