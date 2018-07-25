package org.springframework.contrib.gae.datastore.service;


import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.google.common.io.Resources;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.contrib.gae.datastore.client.GoogleCloudDatastoreExportClient;
import org.springframework.contrib.gae.datastore.client.model.ExportEntitiesOperation;
import org.springframework.contrib.gae.datastore.config.DatastoreBackupProperties;
import org.springframework.contrib.gae.datastore.entity.BackupOperation;
import org.springframework.contrib.gae.datastore.repository.BackupOperationRepository;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.google.appengine.api.taskqueue.TaskOptions.Method.GET;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DatastoreBackupServiceTest {

    @Mock
    private Queue backupQueue;

    @Mock
    private BackupOperationRepository backupOperationRepository;

    @Mock
    private GoogleCloudDatastoreExportClient datastoreExportClient;

    private DatastoreBackupService datastoreBackupService;

    private List<String> kinds;
    private List<String> namespaceIds;

    @Before
    public void setUp() {
        DatastoreBackupProperties properties = new DatastoreBackupProperties();
        properties.setBucket("my-backup-bucket");
        kinds = new ArrayList<>();
        namespaceIds = new ArrayList<>();
        datastoreBackupService = new DatastoreBackupService(backupQueue, properties, backupOperationRepository, datastoreExportClient);
    }

    private ExportEntitiesOperation processingOperation() {
        return loadOperation("backup/export-operation-processing.json");
    }

    private ExportEntitiesOperation successfulOperation() {
        return loadOperation("backup/export-operation-successful.json");
    }

    private ExportEntitiesOperation loadOperation(String filename) {
        try {
            String processingJson = Resources.toString(Resources.getResource(filename), StandardCharsets.UTF_8);
            return JacksonFactory.getDefaultInstance().createJsonParser(processingJson).parse(ExportEntitiesOperation.class);
        } catch (IOException e) {
            throw new RuntimeException("Error loading json", e);
        }
    }

    @Test
    public void startBackup_willSkipExportIfNoBucketConfigured() {
        DatastoreBackupProperties properties = new DatastoreBackupProperties();
        properties.setBucket("");
        datastoreBackupService = new DatastoreBackupService(backupQueue, properties, backupOperationRepository, datastoreExportClient);

        BackupOperation result = datastoreBackupService.startBackup("Backup", kinds, namespaceIds);

        assertThat(result, nullValue());
    }

    @Test
    public void startBackup_willStartExportToSpecifiedBucketAndSaveBackupOperation() {
        givenStartExportWillReturn(processingOperation());

        datastoreBackupService.startBackup("Backup", kinds, namespaceIds);

        verify(datastoreExportClient).startExport("Backup", "my-backup-bucket", kinds, namespaceIds);
        verify(backupOperationRepository).save(any(BackupOperation.class));
    }

    @Test
    public void startBackup_willQueueUpdateCheckFor5MinutesLater() {
        givenStartExportWillReturn(processingOperation());

        BackupOperation operation = datastoreBackupService.startBackup("Backup", kinds, namespaceIds);

        TaskOptions taskOptions = TaskOptions.Builder
                .withUrl("/task/backup/update-status")
                .param("backupId", operation.getName())
                .method(GET)
                .countdownMillis(5 * 60 * 1000);
        verify(backupQueue).add(taskOptions);
    }

    @Test
    public void updateOperation_willUpdateEntityWithLatestData() {
        BackupOperation backupOperation = BackupOperation.fromExport(processingOperation());
        givenGetExportOperationWillReturn(successfulOperation());
        when(backupOperationRepository.findById(backupOperation.getName())).thenReturn(Optional.of(backupOperation));

        BackupOperation updated = datastoreBackupService.updateOperation(backupOperation.getName());

        assertThat(updated.getName(), is(backupOperation.getName()));
        assertThat(updated.isDone(), is(true));
        verify(backupOperationRepository).save(updated);
    }

    private void givenStartExportWillReturn(ExportEntitiesOperation exportOperation) {
        when(datastoreExportClient.startExport(anyString(), anyString(), anyList(), anyList())).thenReturn(exportOperation);
    }

    private void givenGetExportOperationWillReturn(ExportEntitiesOperation exportOperation) {
        when(datastoreExportClient.getExportOperation(anyString())).thenReturn(exportOperation);
    }
}
