package org.springframework.contrib.gae.datastore.controller;


import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.contrib.gae.datastore.entity.BackupOperation;
import org.springframework.contrib.gae.datastore.service.DatastoreBackupService;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.Collections;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
public class BackupControllerTest {

    @Mock
    private DatastoreBackupService datastoreBackupService;

    @InjectMocks
    private BackupController backupController;

    private MockMvc mvc;

    @Before
    public void setup() {
        mvc = MockMvcBuilders
                .standaloneSetup(backupController)
                .alwaysDo(print())
                .build();
    }

    @Test
    public void startBackup_WillStartNewBackup() throws Exception {
        mvc.perform(get("/task/backup/start")
                .param("name", "FullBackup"))
                .andExpect(status().isOk());

        verify(datastoreBackupService).startBackup("FullBackup", Collections.emptyList(), Collections.emptyList());
    }

    @Test
    public void startBackup_willAllowKindsToBeSentAsCommaSeparatedList() throws Exception {
        mvc.perform(get("/task/backup/start")
                .param("name", "Backup")
                .param("kinds", "E1,E2,E3")
                .param("kinds", "E4,E5"))
                .andExpect(status().isOk());

        verify(datastoreBackupService).startBackup("Backup", Arrays.asList("E1", "E2", "E3", "E4", "E5"), Collections.emptyList());
    }

    @Test
    public void startBackup_willAllowNamespaceIdsToBeSentAsCommaSeparatedList() throws Exception {
        mvc.perform(get("/task/backup/start")
                .param("name", "Backup")
                .param("namespaceIds", "N1,N2,N3")
                .param("namespaceIds", "N4,N5"))
                .andExpect(status().isOk());

        verify(datastoreBackupService).startBackup("Backup", Collections.emptyList(), Arrays.asList("N1", "N2", "N3", "N4", "N5"));
    }

    @Test
    public void updateBackupStatus_willReturnSuccessWhenBackupIsDone() throws Exception {
        BackupOperation backupOperation = new BackupOperation()
                .setName("backupId")
                .setDone(true);
        when(datastoreBackupService.updateOperation("backupId")).thenReturn(backupOperation);

        mvc.perform(get("/task/backup/update-status")
                .param("backupId", "backupId"))
                .andExpect(status().isOk())
                .andExpect(content().string("Backup operation backupId complete"));
    }

    @Test
    public void updateBackupStatus_willReturn418WhenBackupIsNotDone() throws Exception {
        BackupOperation backupOperation = new BackupOperation()
                .setName("backupId")
                .setDone(false);
        when(datastoreBackupService.updateOperation("backupId")).thenReturn(backupOperation);

        mvc.perform(get("/task/backup/update-status")
                .param("backupId", "backupId"))
                .andExpect(status().isIAmATeapot());
    }
}
