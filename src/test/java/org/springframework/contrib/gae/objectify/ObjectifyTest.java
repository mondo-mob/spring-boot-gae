package org.springframework.contrib.gae.objectify;

import com.googlecode.objectify.Objectify;
import org.junit.Rule;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.contrib.gae.SetupAppengine;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {ObjectifyTestConfiguration.class})
public abstract class ObjectifyTest {
    @Rule
    public SetupAppengine setupAppengine = new SetupAppengine();

    @Rule
    public ObjectifyRollbackRule objectifyRollbackRule = new ObjectifyRollbackRule();

    @Rule
    public ExpectedException thrown = ExpectedException.none();


    @Autowired
    protected ObjectifyProxy objectify;

    public Objectify ofy() {
        return objectify.ofy();
    }

    public ExpectedException thrown() {
        return thrown;
    }

    protected final <E> E save(E entity) {
        ofy().save().entity(entity).now();
        return entity;
    }

    @SafeVarargs
    protected final <E> E[] save(E... entities) {
        ofy().save().entities(entities).now();
        return entities;
    }
}
