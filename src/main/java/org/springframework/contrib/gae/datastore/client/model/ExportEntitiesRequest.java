package org.springframework.contrib.gae.datastore.client.model;

import com.google.api.client.util.Key;

import java.util.Collections;
import java.util.List;

public class ExportEntitiesRequest {
    @Key
    private String outputUrlPrefix;

    @Key
    private EntityFilter entityFilter = new EntityFilter();

    public String getOutputUrlPrefix() {
        return outputUrlPrefix;
    }

    public ExportEntitiesRequest setOutputUrlPrefix(String outputUrlPrefix) {
        this.outputUrlPrefix = outputUrlPrefix;
        return this;
    }

    public EntityFilter getEntityFilter() {
        return entityFilter;
    }

    public ExportEntitiesRequest setEntityFilter(EntityFilter entityFilter) {
        this.entityFilter = entityFilter;
        return this;
    }

    public ExportEntitiesRequest setKinds(List<String> kinds) {
        this.entityFilter.setKinds(kinds == null ? Collections.emptyList() : kinds);
        return this;
    }

    public ExportEntitiesRequest setNamespaceIds(List<String> namespaceIds) {
        this.entityFilter.setNamespaceIds(namespaceIds == null ? Collections.emptyList() : namespaceIds);
        return this;
    }
}
