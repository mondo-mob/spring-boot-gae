package org.springframework.contrib.gae.datastore.client;

import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.json.jackson2.JacksonFactory;

import java.io.IOException;

public class JsonApiRequestInitializer implements HttpRequestInitializer {

    private HttpRequestInitializer googleCredential;

    public JsonApiRequestInitializer(HttpRequestInitializer googleCredential) {
        if (googleCredential == null) {
            throw new IllegalArgumentException("Credential initializer required to call Google JSON API");
        }
        this.googleCredential = googleCredential;
    }

    @Override
    public void initialize(HttpRequest request) throws IOException {
        googleCredential.initialize(request);
        request.setParser(JacksonFactory.getDefaultInstance().createJsonObjectParser());
        request.setCurlLoggingEnabled(false);
    }
}
