package org.springframework.contrib.gae.storage.service;

import org.junit.Before;
import org.junit.Test;
import org.springframework.contrib.gae.storage.config.CloudStorageAutoConfiguration;

public class CloudStorageAutoConfigurationTest {

    private CloudStorageAutoConfiguration cloudStorageAutoConfiguration;

    @Before
    public void before() {
        cloudStorageAutoConfiguration = new CloudStorageAutoConfiguration();
    }

    @Test(expected = IllegalArgumentException.class)
    public void springCloudStorageConfiguration_throwsException_whenNoBucketNameProvided() {
        cloudStorageAutoConfiguration.cloudStorageService(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void springCloudStorageConfiguration_throwsException_whenEmptyBucketNameProvided() {
        cloudStorageAutoConfiguration.cloudStorageService(" ");
    }
}