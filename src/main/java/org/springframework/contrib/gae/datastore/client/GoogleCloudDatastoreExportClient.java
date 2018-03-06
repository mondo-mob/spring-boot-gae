package org.springframework.contrib.gae.datastore.client;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpContent;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.json.JsonHttpContent;
import com.google.api.client.json.jackson2.JacksonFactory;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.contrib.gae.datastore.client.model.ExportEntitiesOperation;
import org.springframework.contrib.gae.datastore.client.model.ExportEntitiesRequest;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Google Cloud Datastore client that uses the JSON API to provide export functionality.
 * Intentionally left out import for now as that's more likely something you'd do from
 * within GCP rather than via the webapp.
 *
 * @see <a href="https://cloud.google.com/datastore/docs/reference/admin/rest/">https://cloud.google.com/datastore/docs/reference/admin/rest/</a>
 */
public class GoogleCloudDatastoreExportClient {

    private static final Logger LOG = LoggerFactory.getLogger(GoogleCloudDatastoreExportClient.class);
    private static final DateTimeFormatter BUCKET_DATE_FORMATTER = formatter("YYYY/MM/YYYYMMdd-HHmmss");
    private static final String BASE_API_URL = "https://datastore.googleapis.com";

    private final String cloudDatastoreProject;
    private final HttpRequestFactory httpRequestFactory;

    public GoogleCloudDatastoreExportClient(String cloudDatastoreProject, GoogleCredential cloudDatastoreCredential) {
        this.cloudDatastoreProject = cloudDatastoreProject;
        HttpRequestInitializer requestInitializer = new JsonApiRequestInitializer(cloudDatastoreCredential);
        this.httpRequestFactory = cloudDatastoreCredential.getTransport().createRequestFactory(requestInitializer);
    }

    /**
     * Starts a datastore export
     * https://cloud.google.com/datastore/docs/reference/admin/rest/v1/projects/export
     *
     * @param exportName   name for this backup - will be included in backup folder
     * @param bucket       gcs bucket to output to
     * @param kinds        list of kinds to filter by or null/empty list for all
     * @param namespaceIds list of namespaceIds to filter by or null/empty list for all
     * @return The export operation details returned by the api
     */
    public ExportEntitiesOperation startExport(String exportName, String bucket, List<String> kinds, List<String> namespaceIds) {
        if (StringUtils.isBlank(cloudDatastoreProject)) {
            throw new IllegalArgumentException("No datastore project configured - cannot start export");
        }

        String uploadUrl = String.format("%s/v1/projects/%s:export", BASE_API_URL, cloudDatastoreProject);
        GenericUrl url = new GenericUrl(uploadUrl);

        String name = StringUtils.isBlank(exportName) ? "DatastoreExport" : exportName;
        List<String> filterKinds = kinds != null ? kinds : new ArrayList<>();
        List<String> filterNamespaceIds = namespaceIds != null ? namespaceIds : new ArrayList<>();

        LOG.info(
                "Requesting export for project '{}', kinds {} and namespaceIds {} to bucket '{}'",
                cloudDatastoreProject, filterKinds, filterNamespaceIds, bucket);

        ExportEntitiesRequest exportEntitiesRequest = new ExportEntitiesRequest()
                .setOutputUrlPrefix(createExportFolder(name, bucket))
                .setKinds(filterKinds)
                .setNamespaceIds(filterNamespaceIds);

        HttpResponse response = apiPost(url, exportEntitiesRequest);
        ExportEntitiesOperation operation = parseResponse(response, ExportEntitiesOperation.class);
        LOG.info("Datastore export operation {} started", operation.getName());
        return operation;
    }

    /**
     * Get details for a specific operation
     * https://cloud.google.com/datastore/docs/reference/rest/v1/projects.operations/get
     * https://datastore.googleapis.com/v1/{name=operationName}
     * @param operationName the name/id of the operation to lookup
     * @return the export operation details
     */
    public ExportEntitiesOperation getExportOperation(String operationName) {

        String uploadUrl = String.format("%s/v1/%s", BASE_API_URL, operationName);
        GenericUrl url = new GenericUrl(uploadUrl);

        HttpResponse response = apiGet(url);
        return parseResponse(response, ExportEntitiesOperation.class);
    }

    private HttpResponse apiGet(GenericUrl url) {
        try {
            return httpRequestFactory
                    .buildGetRequest(url)
                    .execute();
        } catch (IOException e) {
            throw new GoogleCloudDatastoreException(e, "Error calling datastore api: %s", e.getMessage());
        }
    }

    private HttpResponse apiPost(GenericUrl url, Object data) {
        try {
            HttpContent jsonContent = new JsonHttpContent(JacksonFactory.getDefaultInstance(), data);
            return httpRequestFactory
                    .buildPostRequest(url, jsonContent)
                    .execute();
        } catch (IOException e) {
            throw new GoogleCloudDatastoreException(e, "Error calling datastore api: %s", e.getMessage());
        }
    }

    private static <T> T parseResponse(HttpResponse response, Class<T> target) {
        try {
            return response.parseAs(target);
        } catch (IOException e) {
            throw new GoogleCloudDatastoreException(e, "Unable to parse response from api", e.getMessage());
        }
    }

    private static DateTimeFormatter formatter(String pattern) {
        return DateTimeFormatter.ofPattern(pattern).withZone(ZoneId.of("Australia/NSW"));
    }

    private String createExportFolder(String exportName, String bucket) {
        return String.format("gs://%s/%s/%s", bucket, exportName, BUCKET_DATE_FORMATTER.format(LocalDateTime.now()));
    }
}
