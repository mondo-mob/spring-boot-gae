package org.springframework.contrib.gae.datastore;

import com.google.api.client.http.HttpTransport;
import com.google.api.client.testing.http.MockLowLevelHttpRequest;
import com.google.api.client.testing.http.MockLowLevelHttpResponse;
import com.google.appengine.api.utils.SystemProperty;
import com.google.common.io.Resources;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.contrib.gae.datastore.entity.BackupOperation;
import org.springframework.contrib.gae.datastore.repository.BackupOperationRepository;
import org.springframework.contrib.gae.objectify.ObjectifyTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("backup")
@SpringBootTest(classes = {CloudDatastoreBackupTestConfiguration.class})
public class CloudDatastoreBackupTest extends ObjectifyTest {

	@Autowired
	private WebApplicationContext webApplicationContext;

	@Autowired
	private BackupOperationRepository backupOperationRepository;

	@Autowired
	private HttpTransport httpTransport;

	private MockMvc mvc;
	private String operationId = "projects/my-project/operations/ASA5NDAwOTE4MjMJGnRsdWFmZWQHEmxhcnRuZWNzdS1zYm9qLW5pbWRhFAosEg";

	@BeforeClass
	public static void setupClass() {
		SystemProperty.applicationId.set("my-project");
	}

	@Before
	public void setUp() {
		mvc = MockMvcBuilders
			.webAppContextSetup(webApplicationContext)
			.build();
	}

	@Test
	public void startFullBackup_willStartBackupAndSaveEntity() throws Exception {
		givenApiWillReturn(processingOperation());

		// Url match and status ok
		mvc.perform(get("/task/backup/start")
			.param("name", "FullBackup"))
			.andExpect(status().isOk());

		// Entity is saved
		BackupOperation backupOperation = backupOperationRepository.getById(operationId);
		assertThat(backupOperation, notNullValue());
		assertThat(backupOperation.getState(), is("PROCESSING"));

		// Request sent to correct api endpoint
		MockLowLevelHttpRequest httpRequest = ((EditableMockHttpTransport) httpTransport).getLastRequest();
		assertThat(httpRequest.getUrl(), is("https://datastore.googleapis.com/v1/projects/my-project:export"));
	}

	@Test
	public void updateBackupStatus_willFetchStatusAndUpdateEntity() throws Exception {
		BackupOperation backupOperation = new BackupOperation(operationId);
		backupOperationRepository.save(backupOperation);
		givenApiWillReturn(successfulOperation());

		// Url match and status ok
		mvc.perform(get("/task/backup/update-status")
			.param("backupId", operationId))
			.andExpect(status().isOk())
			.andExpect(content().string(String.format("Backup operation %s complete", operationId)));

		// Entity is updated
		backupOperation = backupOperationRepository.getById(operationId);
		assertThat(backupOperation, notNullValue());
		assertThat(backupOperation.getState(), is("SUCCESSFUL"));

		// Request sent to correct api endpoint
		MockLowLevelHttpRequest httpRequest = ((EditableMockHttpTransport) httpTransport).getLastRequest();
		assertThat(httpRequest.getUrl(), is("https://datastore.googleapis.com/v1/" + operationId));
	}

	private void givenApiWillReturn(String responseJson) {
		MockLowLevelHttpResponse httpResponse = new MockLowLevelHttpResponse()
			.setContent(responseJson);
		((EditableMockHttpTransport) httpTransport).setNextResponse(httpResponse);
	}

	private String successfulOperation() throws IOException {
		return loadOperation("backup/export-operation-successful.json");
	}

	private String processingOperation() throws IOException {
		return loadOperation("backup/export-operation-processing.json");
	}

	private String loadOperation(String filename) throws IOException {
		return Resources.toString(Resources.getResource(filename), StandardCharsets.UTF_8);
	}
}
