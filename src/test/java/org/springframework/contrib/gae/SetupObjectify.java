package org.springframework.contrib.gae;

import com.google.appengine.api.taskqueue.dev.QueueStateInfo;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalMemcacheServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalSearchServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.appengine.tools.development.testing.LocalTaskQueueTestConfig;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.impl.translate.Translators;
import com.googlecode.objectify.impl.translate.opt.BigDecimalLongTranslatorFactory;
import com.googlecode.objectify.util.Closeable;
import org.junit.rules.ExternalResource;
import org.springframework.contrib.gae.objectify.translator.Jsr310Translators;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SetupObjectify extends ExternalResource {
    private LocalServiceTestHelper helper;
    private Closeable ofyService;
    private List<Class<?>> entities;

    public SetupObjectify(Class<?>... entities) {
        this.entities = Stream.of(entities).collect(Collectors.toList());
    }

    public void registerAdditionalEntities(Class<?>... entities) {
        for (Class<?> entity : entities) {
            ObjectifyService.register(entity);
            this.entities.add(entity);
        }
    }

    @Override
    protected void before() {
        helper = createLocalServiceTestHelper();
        helper.setUp();
        ofyService = ObjectifyService.begin();

        registerTranslators(ObjectifyService.factory().getTranslators());

        for(Class<?> entity: entities) {
            ObjectifyService.register(entity);
        }
    }

    @Override
    protected void after() {
        helper.tearDown();
        ofyService.close();
    }

    public QueueStateInfo getTaskQueue(String queueName) {
        return LocalTaskQueueTestConfig.getLocalTaskQueue().getQueueStateInfo().get(queueName);
    }

    private void registerTranslators(Translators translators) {
        Jsr310Translators.addTo(translators);
        translators.add(new BigDecimalLongTranslatorFactory());
    }

    private LocalServiceTestHelper createLocalServiceTestHelper() {
        return new LocalServiceTestHelper(
            new LocalDatastoreServiceTestConfig().setApplyAllHighRepJobPolicy(),
            new LocalTaskQueueTestConfig().setQueueXmlPath("src/main/webapp/WEB-INF/queue.xml"),
            new LocalMemcacheServiceTestConfig(),
            new LocalSearchServiceTestConfig()
        );
    }

}
