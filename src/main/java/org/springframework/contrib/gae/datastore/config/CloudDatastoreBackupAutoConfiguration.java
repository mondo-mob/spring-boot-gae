package org.springframework.contrib.gae.datastore.config;

import com.google.api.client.extensions.appengine.http.UrlFetchTransport;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.utils.SystemProperty;
import com.google.common.io.Resources;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.contrib.gae.datastore.client.GoogleCloudDatastoreExportClient;
import org.springframework.contrib.gae.datastore.entity.BackupOperation;
import org.springframework.contrib.gae.objectify.config.ObjectifyConfigurer;
import org.springframework.core.env.Environment;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static java.util.Collections.singletonList;

@Configuration
@ComponentScan("org.springframework.contrib.gae.datastore")
@ConditionalOnProperty(prefix = "spring.contrib.gae.datastore.backup", name = {"bucket", "queue"})
@EnableConfigurationProperties(DatastoreBackupProperties.class)
public class CloudDatastoreBackupAutoConfiguration implements ObjectifyConfigurer {

    private static final Logger LOG = LoggerFactory.getLogger(CloudDatastoreBackupAutoConfiguration.class);
    private static final List<String> DATASTORE_SCOPES = singletonList("https://www.googleapis.com/auth/datastore");

    private DatastoreBackupProperties properties;

    public CloudDatastoreBackupAutoConfiguration(DatastoreBackupProperties properties) {
        this.properties = properties;
    }

    @Override
    public Collection<Class<?>> registerObjectifyEntities() {
        return Collections.singletonList(BackupOperation.class);
    }

    @Bean
    @ConditionalOnMissingBean
    public HttpTransport httpTransport() {
        return UrlFetchTransport.getDefaultInstance();
    }

    @Bean
    @ConditionalOnMissingBean
    public JsonFactory jsonFactory() {
        return JacksonFactory.getDefaultInstance();
    }

    @Bean
    public Queue backupQueue() {
        return QueueFactory.getQueue(properties.getQueue());
    }

    @Bean
    @ConditionalOnMissingBean(name = "cloudDatastoreCredential")
    public GoogleCredential cloudDatastoreCredential(HttpTransport httpTransport, JsonFactory jsonFactory) {

        if (StringUtils.isBlank(properties.getCredential())) {
            try {
                LOG.info("Using application default credential");
                return GoogleCredential
                        .getApplicationDefault(httpTransport, jsonFactory)
                        .createScoped(DATASTORE_SCOPES);
            } catch (IOException e) {
                throw new ConfigurationException(e, "Cloud datastore client configuration failed: %s", e.getMessage());
            }
        } else {
            LOG.info("Loading service account credential from file {}", properties.getCredential());
            try (InputStream jsonCredentials = Resources.getResource(properties.getCredential()).openStream()) {
                return GoogleCredential
                        .fromStream(jsonCredentials, httpTransport, jsonFactory)
                        .createScoped(DATASTORE_SCOPES);
            } catch (IOException e) {
                throw new ConfigurationException(e,
                        "Cloud datastore credential configuration failed. Ensure you have a credentials file created in src/main/resources/%s." +
                                "See https://developers.google.com/identity/protocols/application-default-credentials.",
                        properties.getCredential());
            }
        }
    }

    @Bean
    public GoogleCloudDatastoreExportClient datastoreExportClient(Environment environment, GoogleCredential cloudDatastoreCredential) {

        String project = properties.getProject();
        if (StringUtils.isBlank(project)) {

            if (environment.acceptsProfiles("local")) {
                LOG.info("Not configuring datastore export client for local environment");
                return null;
            }

            LOG.info("Using deployed applicationId for backup project");
            project = SystemProperty.applicationId.get();
        }

        LOG.info("Using backup project {}", project);
        return new GoogleCloudDatastoreExportClient(project, cloudDatastoreCredential);
    }
}
