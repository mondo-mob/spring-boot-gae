package org.springframework.contrib.gae.objectify.support;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.contrib.gae.objectify.ObjectifyProxy;
import org.springframework.contrib.gae.search.SearchService;
import org.springframework.data.mapping.context.MappingContext;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.core.support.RepositoryFactoryBeanSupport;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;
import org.springframework.util.Assert;

import java.io.Serializable;

/**
 * {@link org.springframework.beans.factory.FactoryBean} adapter for factories that produce Objectify repositories.
 *
 * @param <T> Base repository type.
 */
@SuppressWarnings("SpringJavaAutowiredMembersInspection")
public class ObjectifyRepositoryFactoryBean<T extends Repository<E, I>, E, I extends Serializable>
        extends RepositoryFactoryBeanSupport<T, E, I> {

    private ObjectifyProxy objectify;
    private SearchService searchService;

    /**
     * Creates a new {@link ObjectifyRepositoryFactoryBean} for the given repository interface.
     *
     * @param repositoryInterface must not be {@literal null}.
     */
    public ObjectifyRepositoryFactoryBean(Class<? extends T> repositoryInterface) {
        super(repositoryInterface);
    }

    /**
     * Set the reference to the Objectify proxy.
     *
     * @param objectify Objectify proxy.
     */
    @Autowired
    public void setObjectify(ObjectifyProxy objectify) {
        this.objectify = objectify;
    }

    /**
     * Set the reference to the search service.
     *
     * @param searchService Search service.
     */
    @Autowired
    public void setSearchService(SearchService searchService) {
        this.searchService = searchService;
    }

    @Override
    public void setMappingContext(MappingContext<?, ?> mappingContext) {
        super.setMappingContext(mappingContext);
    }

    @Override
    public void afterPropertiesSet() {
        Assert.notNull(objectify, String.format("No %s bean registered", ObjectifyProxy.class));
        super.afterPropertiesSet();
    }

    @Override
    protected RepositoryFactorySupport createRepositoryFactory() {
        return new ObjectifyRepositoryFactory(objectify, searchService);
    }
}
