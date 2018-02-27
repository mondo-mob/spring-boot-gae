package org.springframework.contrib.gae.datastore.client.model;

import com.google.api.client.util.Key;

import java.util.ArrayList;
import java.util.List;

/**
 * https://cloud.google.com/datastore/docs/reference/rest/Shared.Types/EntityFilter
 */
public class EntityFilter {
    @Key
    private List<String> kinds = new ArrayList<>();

    @Key
    private List<String> namespaceIds = new ArrayList<>();

    public List<String> getKinds() {
        return kinds;
    }

    public EntityFilter setKinds(List<String> kinds) {
        this.kinds = kinds;
        return this;
    }

    public List<String> getNamespaceIds() {
        return namespaceIds;
    }

    public EntityFilter setNamespaceIds(List<String> namespaceIds) {
        this.namespaceIds = namespaceIds;
        return this;
    }
}
