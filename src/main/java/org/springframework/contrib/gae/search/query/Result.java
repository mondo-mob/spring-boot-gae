package org.springframework.contrib.gae.search.query;

import com.google.appengine.api.search.Results;
import com.google.appengine.api.search.ScoredDocument;
import com.googlecode.objectify.Key;

import javax.annotation.Nonnull;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Result from a search execution.
 *
 * @param <T> Result type.
 */
public interface Result<T> extends Iterable<T> {
    /**
     * @return List of results.
     */
    List<T> getList();

    /**
     * Default method to get keys, assuming the result type is a registered entity.
     *
     * @return Objectify keys, avoiding loading all entities if they are not needed.
     */
    @SuppressWarnings("unchecked")
    default List<Key<T>> getKeys() {
        return getMetadata().getResults().stream()
                .map(ScoredDocument::getId)
                .map(string -> (Key<T>) Key.create(string))
                .collect(Collectors.toList());
    }

    /**
     * @return Metadata and raw search results.
     */
    Results<ScoredDocument> getMetadata();

    /**
     * @return Total number of records found.
     */
    default long getTotal() {
        return getMetadata().getNumberFound();
    }

    /**
     * @return Number of records returned.
     */
    default int getCount() {
        return getMetadata().getNumberReturned();
    }

    @Nonnull
    @Override
    default Iterator<T> iterator() {
        return getList().iterator();
    }
}
