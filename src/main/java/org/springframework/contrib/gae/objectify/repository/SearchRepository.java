package org.springframework.contrib.gae.objectify.repository;

import com.google.common.collect.Lists;
import com.googlecode.objectify.Key;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.contrib.gae.search.SearchIndex;
import org.springframework.contrib.gae.search.SearchService;
import org.springframework.contrib.gae.search.query.Query;
import org.springframework.contrib.gae.search.query.QueryBuilder;
import org.springframework.contrib.gae.search.query.Result;
import org.springframework.data.repository.NoRepositoryBean;

import javax.annotation.Nonnull;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * A searchable repository.
 * Extends the functionality of {@link SaveRepository} and {@link AsyncSaveRepository}, indexing the managed entity
 * and providing a mechanism to search on {@link SearchIndex} annotated fields.
 *
 * @param <E> Entity type.
 * @param <I> Entity Id Type.
 */
@NoRepositoryBean
public interface SearchRepository<E, I extends Serializable> extends LoadRepository<E, I>, SaveRepository<E, I>, DeleteRepository<E, I> {
    Logger LOG = LoggerFactory.getLogger(SearchRepository.class);
    int BATCH_SIZE = 200;

    /**
     * @return Search service.
     */
    @Nonnull
    SearchService getSearchService();

    /**
     * Begin a search filter.
     *
     * @return Query builder.
     */
    @Nonnull
    default QueryBuilder<E> search() {
        return getSearchService()
                .createQuery(getEntityType())
                .retrieveIdsOnly(); //Only ids are needed by default, we load the entities from these ids. 
    }

    /**
     * Clear all records in the search index.
     * @return Number of entries removed.
     */
    default int clearSearchIndex() {
        return getSearchService().clear(getEntityType());
    }

    /**
     *
     * Reindex data and search without performing any transformations.
     *
     * @return the number of entities reindexed.
     */
    default int reindexDataAndSearch() {
        return reindex(b -> b);
    }

    /**
     * Find all entities and reindex their associated docs in the Search Index. If
     * a non-null ReindexOperation is supplied then entities will have that operation
     * applied to them prior to being saved in DataStore and saved in the Search Index.
     * <p>
     * A null reindexOperation will result in no DataStore updates, but only Search Index updates.
     * <p>
     * Use with care i.e. if there are heaps of
     * entities then consider triggering this from a queue or a backend (so that the request
     * has more time to complete). Otherwise, craft your own mechanism to reindex a batch
     * then create a queue task to continue on (from a cursor).
     *
     * @param reindexOperation If provided, the entities will be updated. Allows caller to perform transformations.
     *
     * @return number of entities reindexed
     */
    default int reindex(Function<List<E>, List<E>> reindexOperation) {
        List<Key<E>> keys = ofy()
                .load()
                .type(getEntityType())
                .keys()
                .list();
        return reindex(keys, BATCH_SIZE, reindexOperation);
    }

    /**
     * Reindexes all the entities matching the given list of keys. The given reindexOperation, if present will
     * be applied to each batch of entities.
     *
     * @param keys  Keys of the entities to reindex
     * @param batchSize Size of batches to perform operations in.
     * @param reindexOperation If provided, the entities will be updated. Allows caller to perform transformations.
     * @return the overall count of re-indexed entities.
     */
    default int reindex(List<Key<E>> keys, int batchSize, Function<List<E>, List<E>> reindexOperation) {
        int count = 0;
        List<List<Key<E>>> batches = Lists.partition(keys, batchSize);
        for (List<Key<E>> batchKeys : batches) {
            List<E> batch = findAll(batchKeys);

            batch = reindexOperation == null ? batch : reindexOperation.apply(batch);

            if (reindexOperation != null) {
                // we only re-save the batch when a re-index op is supplied, otherwise the data can't have changed.
                save(batch);
            } else {
                index(batch).run();
            }

            count += batch.size();
            ofy().clear(); // Clear the Objectify cache to free memory for next batch
            LOG.info("Reindexed {} entities of type {}, {} of {}", batch.size(), getEntityType().getSimpleName(), count, keys.size());
        }

        return count;
    }

    /**
     * Execute a search query.
     *
     * @param query Search query.
     * @return Search result.
     */
    default Result<E> execute(Query<E> query) {
        return getSearchService().execute(query, new SearchResultLoader<>(this::findAllByWebSafeKey));
    }

    /**
     * Create search indexes for an entity.
     * If the search service is not configured, no operation will be performed.
     *
     * @param entity The entity to index.
     * @return Runnable that can be used to synchronously complete the indexing operation.
     */
    @Nonnull
    default Runnable index(E entity) {
        return getSearchService().indexAsync(entity, getKey(entity).toWebSafeString());
    }

    /**
     * Create search indexes for a batch of entities.
     * If the search service is not configured, no operation will be performed.
     *
     * @param batch Entities to index.
     * @return Runnable that can be used to synchronously complete the indexing operation.
     */
    @Nonnull
    default Runnable index(Collection<E> batch) {
        Map<String, E> toIndex = new HashMap<>();
        toKeyMap(batch).forEach((key, entity) -> toIndex.put(key.toWebSafeString(), entity));

        return getSearchService().indexAsync(toIndex);
    }

    /**
     * Create search indexes for a batch of entities.
     * If the search service is not configured, no operation will be performed.
     *
     * @param batch Entities to index.
     * @return Runnable that can be used to synchronously complete the indexing operation.
     */
    @Nonnull
    @SuppressWarnings("unchecked")
    default Runnable index(E... batch) {
        return index(Arrays.asList(batch));
    }

    /**
     * Remove the given entity from search indexes.
     * If the search service is not configured, no operation will be performed.
     *
     * @param entity The entity to remove.
     */
    default void unIndex(E entity) {
        unindexByKey(getKey(entity));
    }

    /**
     * Remove the given entities from search indexes.
     * If the search service is not configured, no operation will be performed.
     *
     * @param entities The entities to remove.
     */
    default void unindex(Collection<E> entities) {
        unindexByKey(getKey(entities));
    }

    /**
     * Remove the given entities from search indexes.
     * If the search service is not configured, no operation will be performed.
     *
     * @param entities The entities to remove.
     */
    @SuppressWarnings("unchecked")
    default void unindex(E... entities) {
        unindex(Arrays.asList(entities));
    }

    /**
     * Remove the entity with the given key from search indexes.
     * If the search service is not configured, no operation will be performed.
     *
     * @param key Key of the entity to remove.
     */
    default void unindexByKey(Key<E> key) {
        getSearchService().unindex(getEntityType(), key.toWebSafeString());
    }

    /**
     * Remove entities with the given keys from search indexes.
     * If the search service is not configured, no operation will be performed.
     *
     * @param keys Keys of the entities to remove.
     */
    default void unindexByKey(Collection<Key<E>> keys) {
        getSearchService().unindex(getEntityType(), keys.stream().map(Key::toWebSafeString));
    }

    /**
     * Remove entities with the given keys from search indexes.
     * If the search service is not configured, no operation will be performed.
     *
     * @param keys Keys of the entities to remove.
     */
    @SuppressWarnings("unchecked")
    default void unIndexByKey(Key<E>... keys) {
        unindexByKey(Arrays.asList(keys));
    }

    /*--------- AsyncSaveRepository ---------*/

    @Nonnull
    @Override
    default Supplier<E> saveAsync(final E entity) {
        boolean needsId = hasNoId(entity);

        final Supplier<E> saveOperation = SaveRepository.super.saveAsync(entity);

        // if the entity has no id we need the save to complete so we can index by the generated id.
        if (needsId) {
            saveOperation.get();
        }

        final Runnable indexOperation = index(entity);

        return () -> {
            indexOperation.run();

            saveOperation.get();
            return entity;
        };
    }

    @Nonnull
    @Override
    default Supplier<List<E>> saveAsync(final Collection<E> entities) {
        final List<I> ids = getId(entities);

        final Supplier<List<E>> saveOperation = SaveRepository.super.saveAsync(entities);

        // if any entity has no id we need the save to complete so we can index by the generated id.
        if (ids.contains(null)) {
            saveOperation.get();
        }

        final Runnable indexOperation = index(entities);

        return () -> {
            indexOperation.run();

            saveOperation.get();
            return new ArrayList<>(entities);
        };
    }

    @Nonnull
    @Override
    @SuppressWarnings("unchecked")
    default Supplier<List<E>> saveAsync(final E... entities) {
        return saveAsync(Arrays.asList(entities));
    }

    /*--------- AsyncDeleteRepository ---------*/

    @Nonnull
    @Override
    default Runnable deleteAsync(E entity) {
        unIndex(entity);
        return DeleteRepository.super.deleteAsync(entity);
    }

    @Nonnull
    @Override
    default Runnable deleteAsync(Collection<E> entities) {
        unindex(entities);
        return DeleteRepository.super.deleteAsync(entities);
    }

    @Nonnull
    @Override
    @SuppressWarnings("unchecked")
    default Runnable deleteAsync(E... entities) {
        return deleteAsync(Arrays.asList(entities));
    }

    @Nonnull
    @Override
    default Runnable deleteByKeyAsync(Key<E> key) {
        unindexByKey(key);
        return DeleteRepository.super.deleteByKeyAsync(key);
    }

    @Nonnull
    @Override
    default Runnable deleteByKeyAsync(Collection<Key<E>> keys) {
        unindexByKey(keys);
        return DeleteRepository.super.deleteByKeyAsync(keys);
    }

    @Nonnull
    @Override
    @SuppressWarnings("unchecked")
    default Runnable deleteByKeyAsync(Key<E>... keys) {
        return deleteByKeyAsync(Arrays.asList(keys));
    }

    /*--------- SaveRepository ---------*/

    @Nonnull
    @Override
    default E save(final E entity) {
        return saveAsync(entity).get();
    }

    @Nonnull
    @Override
    default List<E> save(final Collection<E> entities) {
        return saveAsync(entities).get();
    }

    @Nonnull
    @Override
    @SuppressWarnings("unchecked")
    default List<E> save(E... entities) {
        return saveAsync(entities).get();
    }

    /*--------- DeleteRepository ---------*/


    @Override
    default void delete(E entity) {
        deleteAsync(entity).run();
    }

    @Override
    default void delete(Collection<E> entities) {
        deleteAsync(entities).run();
    }

    @Override
    @SuppressWarnings("unchecked")
    default void delete(E... entities) {
        deleteAsync(entities).run();
    }

    @Override
    default void deleteByKey(Key<E> key) {
        deleteByKeyAsync(key).run();
    }

    @Override
    default void deleteByKey(Collection<Key<E>> keys) {
        deleteByKeyAsync(keys).run();
    }

    @Override
    @SuppressWarnings("unchecked")
    default void deleteByKey(Key<E>... keys) {
        deleteByKeyAsync(keys).run();
    }
}
