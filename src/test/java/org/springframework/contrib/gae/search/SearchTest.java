package org.springframework.contrib.gae.search;

import com.google.appengine.api.search.Index;
import com.google.appengine.api.search.IndexSpec;
import com.google.appengine.api.search.SearchServiceFactory;
import org.springframework.contrib.gae.SetupAppengine;
import org.springframework.contrib.gae.objectify.ObjectifyRollbackRule;
import org.springframework.contrib.gae.search.config.SearchTestConfiguration;
import org.springframework.contrib.gae.search.metadata.SearchMetadata;
import org.junit.Rule;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {SearchTestConfiguration.class})
public abstract class SearchTest {
    @Rule
    public SetupAppengine setupAppengine = new SetupAppengine();

    @Rule
    public ObjectifyRollbackRule objectifyRollbackRule = new ObjectifyRollbackRule();

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Autowired
    protected SearchMetadata searchMetadata;

    protected Index getIndex(Class<?> entityClass) {
        return SearchServiceFactory.getSearchService().getIndex(IndexSpec.newBuilder().setName(searchMetadata.getIndexName(entityClass)));
    }
}
