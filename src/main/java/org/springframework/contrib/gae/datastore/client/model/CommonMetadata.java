package org.springframework.contrib.gae.datastore.client.model;

import com.google.api.client.util.Key;

/**
 * https://cloud.google.com/datastore/docs/reference/rest/Shared.Types/CommonMetadata
 */
public class CommonMetadata {
    @Key
    private String startTime;

    @Key
    private String endTime;

    @Key
    private String operationType;

    @Key
    private String state;

    public String getStartTime() {
        return startTime;
    }

    public CommonMetadata setStartTime(String startTime) {
        this.startTime = startTime;
        return this;
    }

    public String getEndTime() {
        return endTime;
    }

    public CommonMetadata setEndTime(String endTime) {
        this.endTime = endTime;
        return this;
    }

    public String getOperationType() {
        return operationType;
    }

    public CommonMetadata setOperationType(String operationType) {
        this.operationType = operationType;
        return this;
    }

    public String getState() {
        return state;
    }

    public CommonMetadata setState(String state) {
        this.state = state;
        return this;
    }
}
