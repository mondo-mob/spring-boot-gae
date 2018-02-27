package org.springframework.contrib.gae.datastore.service;

import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.TaskOptions;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.contrib.gae.datastore.client.GoogleCloudDatastoreExportClient;
import org.springframework.contrib.gae.datastore.client.model.ExportEntitiesOperation;
import org.springframework.contrib.gae.datastore.config.DatastoreBackupProperties;
import org.springframework.contrib.gae.datastore.entity.BackupOperation;
import org.springframework.contrib.gae.datastore.repository.BackupOperationRepository;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import static com.google.appengine.api.taskqueue.TaskOptions.Method.GET;

@Service
public class DatastoreBackupService {

    private static final Logger LOG = LoggerFactory.getLogger(DatastoreBackupService.class);

    private static final String STATUS_UPDATE_PATH = "/task/backup/update-status";
    private static final long STATUS_UPDATE_WAIT_MINS = 5;

    private final Queue backupQueue;
    private final String gcsBackupBucket;
    private final BackupOperationRepository backupOperationRepository;
    private final GoogleCloudDatastoreExportClient datastoreExportClient;

    public DatastoreBackupService(Queue backupQueue,
                                  DatastoreBackupProperties properties,
                                  BackupOperationRepository backupOperationRepository,
                                  GoogleCloudDatastoreExportClient datastoreExportClient) {
        this.backupQueue = backupQueue;
        this.gcsBackupBucket = properties.getBucket();
        this.backupOperationRepository = backupOperationRepository;
        this.datastoreExportClient = datastoreExportClient;
    }

    public BackupOperation startBackup(String name, List<String> kinds, List<String> namespaceIds) {
        if (StringUtils.isBlank(gcsBackupBucket)) {
            LOG.info("No backup bucket configured, skipping backup");
            return null;
        }

        LOG.info("Starting datastore backup '{}' for kinds {}, namespaceIds {}", name, kinds, namespaceIds);
        ExportEntitiesOperation exportOperation = datastoreExportClient.startExport(name, gcsBackupBucket, kinds, namespaceIds);

        BackupOperation backupOperation = BackupOperation.fromExport(exportOperation);
        backupOperationRepository.save(backupOperation);

        queueUpdateCheck(backupOperation);
        return backupOperation;
    }

    public BackupOperation updateOperation(String operationId) {
        Optional<BackupOperation> backupOperation = backupOperationRepository.findById(operationId);
        if (!backupOperation.isPresent()) {
            throw new IllegalArgumentException(String.format("BackupOperation '%s' does not exist", operationId));
        }

        LOG.info("Looking up details for operation {}", backupOperation.get().getName());
        ExportEntitiesOperation exportOperation = datastoreExportClient.getExportOperation(backupOperation.get().getName());
        BackupOperation updated = BackupOperation.fromExport(exportOperation);
        backupOperationRepository.save(updated);
        return updated;
    }

    private void queueUpdateCheck(BackupOperation backupOperation) {
        LOG.info("Queuing task to check backup operation status");
        TaskOptions taskOptions = TaskOptions.Builder
                .withUrl(STATUS_UPDATE_PATH)
                .param("backupId", backupOperation.getName())
                .method(GET)
                .countdownMillis(Duration.of(STATUS_UPDATE_WAIT_MINS, ChronoUnit.MINUTES).toMillis());
        this.backupQueue.add(taskOptions);
    }
}
