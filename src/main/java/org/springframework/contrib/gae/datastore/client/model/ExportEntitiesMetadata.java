package org.springframework.contrib.gae.datastore.client.model;

import com.google.api.client.util.Key;

/**
 * https://cloud.google.com/datastore/docs/reference/rest/Shared.Types/ExportEntitiesMetadata
 */
public class ExportEntitiesMetadata {
    @Key
    private CommonMetadata common;

    @Key
    private EntityFilter entityFilter;

    @Key
    private String outputUrlPrefix;

    public CommonMetadata getCommon() {
        return common;
    }

    public ExportEntitiesMetadata setCommon(CommonMetadata common) {
        this.common = common;
        return this;
    }

    public EntityFilter getEntityFilter() {
        return entityFilter;
    }

    public ExportEntitiesMetadata setEntityFilter(EntityFilter entityFilter) {
        this.entityFilter = entityFilter;
        return this;
    }

    public String getOutputUrlPrefix() {
        return outputUrlPrefix;
    }

    public ExportEntitiesMetadata setOutputUrlPrefix(String outputUrlPrefix) {
        this.outputUrlPrefix = outputUrlPrefix;
        return this;
    }
}
