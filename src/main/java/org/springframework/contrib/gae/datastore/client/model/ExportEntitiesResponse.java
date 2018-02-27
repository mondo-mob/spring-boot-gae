package org.springframework.contrib.gae.datastore.client.model;

import com.google.api.client.util.Key;

/**
 * https://cloud.google.com/datastore/docs/reference/rest/Shared.Types/ExportEntitiesResponse
 */
public class ExportEntitiesResponse {
    @Key
    private String outputUrl;

    public String getOutputUrl() {
        return outputUrl;
    }

    public ExportEntitiesResponse setOutputUrl(String outputUrl) {
        this.outputUrl = outputUrl;
        return this;
    }
}
