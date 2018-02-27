package org.springframework.contrib.gae.datastore.entity;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;
import org.springframework.contrib.gae.datastore.client.model.CommonMetadata;
import org.springframework.contrib.gae.datastore.client.model.EntityFilter;
import org.springframework.contrib.gae.datastore.client.model.ExportEntitiesMetadata;
import org.springframework.contrib.gae.datastore.client.model.ExportEntitiesOperation;
import org.springframework.contrib.gae.datastore.client.model.ExportEntitiesResponse;
import org.springframework.contrib.gae.datastore.client.model.ResponseError;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
public class BackupOperation {

    public static class Fields {
        public static final String Done = "done";
        public static final String State = "state";
        public static final String StartTime = "startTime";
    }

    @Id
    private String name;

    private String operationType;

    @Index
    private String state;

    @Index
    private boolean done;

    @Index
    private OffsetDateTime startTime;
    private OffsetDateTime endTime;
    private String outputUrl;
    private String outputUrlPrefix;
    private List<String> kinds = new ArrayList<>();
    private List<String> namespaceIds = new ArrayList<>();
    private Long errorCode;
    private String errorMessage;

    public BackupOperation() {
    }

    public BackupOperation(String name) {
        this.name = name;
    }

    public static BackupOperation fromExport(ExportEntitiesOperation export) {

        BackupOperation backupOperation = new BackupOperation(export.getName());
        backupOperation.setDone(export.isDone());

        ExportEntitiesMetadata metadata = export.getMetadata();
        backupOperation.setOutputUrlPrefix(metadata.getOutputUrlPrefix());

        CommonMetadata commonMetadata = metadata.getCommon();
        backupOperation.setOperationType(commonMetadata.getOperationType());
        backupOperation.setState(commonMetadata.getState());

        if (commonMetadata.getStartTime() != null) {
            backupOperation.setStartTime(OffsetDateTime.parse(commonMetadata.getStartTime()));
        }

        if (commonMetadata.getEndTime() != null) {
            backupOperation.setEndTime(OffsetDateTime.parse(commonMetadata.getEndTime()));
        }

        EntityFilter entityFilter = metadata.getEntityFilter();
        if (entityFilter.getKinds() != null) {
            backupOperation.setKinds(entityFilter.getKinds());
        }

        if (entityFilter.getNamespaceIds() != null) {
            backupOperation.setNamespaceIds(entityFilter.getNamespaceIds());
        }

        ExportEntitiesResponse response = export.getResponse();
        if (response != null) {
            backupOperation.setOutputUrl(response.getOutputUrl());
        }

        ResponseError responseError = export.getError();
        if (responseError != null) {
            backupOperation.setErrorCode(responseError.getCode());
            backupOperation.setErrorMessage(responseError.getMessage());
        }

        return backupOperation;
    }

    public String getName() {
        return name;
    }

    public BackupOperation setName(String name) {
        this.name = name;
        return this;
    }

    public String getOperationType() {
        return operationType;
    }

    public BackupOperation setOperationType(String operationType) {
        this.operationType = operationType;
        return this;
    }

    public String getState() {
        return state;
    }

    public BackupOperation setState(String state) {
        this.state = state;
        return this;
    }

    public boolean isDone() {
        return done;
    }

    public BackupOperation setDone(boolean done) {
        this.done = done;
        return this;
    }

    public OffsetDateTime getStartTime() {
        return startTime;
    }

    public BackupOperation setStartTime(OffsetDateTime startTime) {
        this.startTime = startTime;
        return this;
    }

    public OffsetDateTime getEndTime() {
        return endTime;
    }

    public BackupOperation setEndTime(OffsetDateTime endTime) {
        this.endTime = endTime;
        return this;
    }

    public String getOutputUrlPrefix() {
        return outputUrlPrefix;
    }

    public BackupOperation setOutputUrlPrefix(String outputUrlPrefix) {
        this.outputUrlPrefix = outputUrlPrefix;
        return this;
    }

    public String getOutputUrl() {
        return outputUrl;
    }

    public BackupOperation setOutputUrl(String outputUrl) {
        this.outputUrl = outputUrl;
        return this;
    }

    public List<String> getKinds() {
        return kinds;
    }

    public BackupOperation setKinds(List<String> kinds) {
        this.kinds = kinds;
        return this;
    }

    public List<String> getNamespaceIds() {
        return namespaceIds;
    }

    public BackupOperation setNamespaceIds(List<String> namespaceIds) {
        this.namespaceIds = namespaceIds;
        return this;
    }

    public Long getErrorCode() {
        return errorCode;
    }

    public BackupOperation setErrorCode(Long errorCode) {
        this.errorCode = errorCode;
        return this;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public BackupOperation setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
        return this;
    }
}
