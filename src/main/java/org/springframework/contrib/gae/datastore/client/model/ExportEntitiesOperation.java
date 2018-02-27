package org.springframework.contrib.gae.datastore.client.model;

import com.google.api.client.util.Key;

/**
 * A Datastore Export Operation
 * <pre>
 * {
 *   "name": "projects/myproject-dev/operations/ASA0MDAwOTg5OTIJGnRsdWFmZWQHEmxhcnRuZWNzdS1zYm9qLW5pbWRhFAosEg",
 *   "metadata": {
 *     "@type": "type.googleapis.com/google.datastore.admin.v1beta1.ExportEntitiesMetadata",
 *     "common": {
 *       "startTime": "2018-01-19T01:21:49.774950Z",
 *       "endTime": "2018-01-19T01:22:10.934850Z",
 *       "operationType": "EXPORT_ENTITIES",
 *       "state": "SUCCESSFUL"
 *     },
 *     "progressEntities": {
 *       "workCompleted": "4528",
 *       "workEstimated": "4431"
 *     },
 *     "progressBytes": {
 *       "workCompleted": "4541740",
 *       "workEstimated": "4180776"
 *     },
 *     "entityFilter": {},
 *     "outputUrlPrefix": "gs://myproject-bucket/20180119-122146"
 *   },
 *   "done": true,
 *   "response": {
 *     "@type": "type.googleapis.com/google.datastore.admin.v1beta1.ExportEntitiesResponse",
 *     "outputUrl": "gs://myproject-bucket/20180119-122146/20180119-122146.overall_export_metadata"
 *   }
 * }
 * </pre>
 */
public class ExportEntitiesOperation {
    @Key
    private String name;

    @Key
    private ExportEntitiesMetadata metadata;

    @Key
    private boolean done;

    @Key
    private ExportEntitiesResponse response;

    @Key
    private ResponseError error;

    public String getName() {
        return name;
    }

    public ExportEntitiesOperation setName(String name) {
        this.name = name;
        return this;
    }

    public ExportEntitiesMetadata getMetadata() {
        return metadata;
    }

    public ExportEntitiesOperation setMetadata(ExportEntitiesMetadata metadata) {
        this.metadata = metadata;
        return this;
    }

    public boolean isDone() {
        return done;
    }

    public ExportEntitiesOperation setDone(boolean done) {
        this.done = done;
        return this;
    }

    public ExportEntitiesResponse getResponse() {
        return response;
    }

    public ExportEntitiesOperation setResponse(ExportEntitiesResponse response) {
        this.response = response;
        return this;
    }

    public ResponseError getError() {
        return error;
    }

    public ExportEntitiesOperation setError(ResponseError error) {
        this.error = error;
        return this;
    }
}
