package org.springframework.contrib.gae.datastore.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * GAE security configuration properties.
 */
@ConfigurationProperties("spring.contrib.gae.datastore.backup")
public class DatastoreBackupProperties {
    private String bucket;
    private String credential;
    private String project;
    private String queue;

    public String getBucket() {
        return bucket;
    }

    public DatastoreBackupProperties setBucket(String bucket) {
        this.bucket = bucket;
        return this;
    }

    public String getCredential() {
        return credential;
    }

    public DatastoreBackupProperties setCredential(String credential) {
        this.credential = credential;
        return this;
    }

    public String getProject() {
        return project;
    }

    public DatastoreBackupProperties setProject(String project) {
        this.project = project;
        return this;
    }

    public String getQueue() {
        return queue;
    }

    public DatastoreBackupProperties setQueue(String queue) {
        this.queue = queue;
        return this;
    }
}
