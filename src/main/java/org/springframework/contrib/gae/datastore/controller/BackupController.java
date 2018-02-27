package org.springframework.contrib.gae.datastore.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.contrib.gae.datastore.client.GoogleCloudDatastoreExportClient;
import org.springframework.contrib.gae.datastore.entity.BackupOperation;
import org.springframework.contrib.gae.datastore.service.DatastoreBackupService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@RestController
public class BackupController {

    private static final Logger LOG = LoggerFactory.getLogger(GoogleCloudDatastoreExportClient.class);

    private final DatastoreBackupService datastoreBackupService;

    public BackupController(DatastoreBackupService datastoreBackupService) {
        this.datastoreBackupService = datastoreBackupService;
    }

    @GetMapping("/task/backup/start")
    public String startBackup(@RequestParam(value = "name", required = false) String name,
                              @RequestParam(value = "kinds", required = false) List<String> kinds,
                              @RequestParam(value = "namespaceIds", required = false) List<String> namespaceIds) {
        // Support params being sent as comma delimited list and/or as separate HTTP params
        // i.e. url?kinds=Entity1,Entity2 or url?kinds=Entity1&kinds=Entity2
        List<String> allKinds = splitParams(kinds);
        List<String> allNamespaceIds = splitParams(namespaceIds);

        BackupOperation backupOperation = datastoreBackupService.startBackup(name, allKinds, allNamespaceIds);
        if (backupOperation != null) {
            return String.format("Backup operation %s started", backupOperation.getName());
        }
        return "Backup not started";
    }

    @GetMapping("/task/backup/update-status")
    public ResponseEntity<String> updateBackupStatus(@RequestParam("backupId") String backupId) {
        if (backupId == null) {
            throw new IllegalArgumentException("backupId must be provided");
        }
        BackupOperation backupOperation = datastoreBackupService.updateOperation(backupId);

        if (backupOperation.isDone()) {
            LOG.info("Backup operation {} complete", backupOperation.getName());
            return ResponseEntity.ok(String.format("Backup operation %s complete", backupOperation.getName()));
        }

        LOG.info("Backup operation {} still running - will retry", backupOperation.getName());
        return ResponseEntity
                .status(HttpStatus.I_AM_A_TEAPOT)
                .body("Retry");
    }

    private List<String> splitParams(List<String> strings) {
        return strings == null ? new ArrayList<>() : strings.stream()
                .flatMap(Pattern.compile(",")::splitAsStream)
                .collect(Collectors.toList());
    }
}
