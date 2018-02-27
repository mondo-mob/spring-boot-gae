package org.springframework.contrib.gae.datastore;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.testing.auth.oauth2.MockGoogleCredential;
import com.google.api.client.http.HttpTransport;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableAutoConfiguration
public class CloudDatastoreBackupTestConfiguration {

    @Bean
    public HttpTransport httpTransport() {
        return new EditableMockHttpTransport();
    }

    @Bean
    public GoogleCredential cloudDatastoreCredential(HttpTransport httpTransport) {
        return new MockGoogleCredential.Builder()
                .setTransport(httpTransport)
                .build();
    }
}
