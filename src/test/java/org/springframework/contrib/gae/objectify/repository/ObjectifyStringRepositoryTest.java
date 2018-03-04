package org.springframework.contrib.gae.objectify.repository;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.contrib.gae.objectify.TestStringEntity;
import org.springframework.contrib.gae.objectify.repository.base.BaseObjectifyStringRepository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SuppressWarnings("ConstantConditions")
public class ObjectifyStringRepositoryTest extends AbstractStringRepositoryTest {

    @Autowired
    private BaseObjectifyStringRepository<TestStringEntity> repository;

    @Test
    public void findById()  {
        TestStringEntity entity = new TestStringEntity("id").setName("the name");
        ofy().save().entity(entity).now();

        Optional<TestStringEntity> result = repository.findById("id");
        assertThat(result.get())
                .isNotNull()
                .hasFieldOrPropertyWithValue("id", "id")
                .hasFieldOrPropertyWithValue("name", "the name");
    }

    @Test
    public void findById_willReturnEmptyOptional_whenIdDoesNotExist()  {
        Optional<TestStringEntity> result = repository.findById("bad-id");

        assertThat(result.isPresent()).isEqualTo(false);
    }

    @Test
    public void getById()  {
        TestStringEntity entity = new TestStringEntity("id").setName("the name");
        ofy().save().entity(entity).now();

        TestStringEntity result = repository.getById("id");
        assertThat(result)
                .isNotNull()
                .hasFieldOrPropertyWithValue("id", "id")
                .hasFieldOrPropertyWithValue("name", "the name");
    }

    @Test
    public void getById_willThrowException_whenEntityNotFound()  {
        thrown.expect(EntityNotFoundException.class);
        thrown.expectMessage("No entity was found matching the key: Key<?>(TestStringEntity(\"bad-id\")");

        repository.getById("bad-id");
    }

    @Test
    public void delete()  {
        TestStringEntity[] entities = fixture.get(3);
        ofy().save().entities(entities).now();

        TestStringEntity beforeDelete = load("id2");
        assertThat(beforeDelete).isNotNull();
        assertThat(beforeDelete.getName()).isEqualTo("entity2");

        repository.delete(beforeDelete.getId());

        TestStringEntity afterDelete = load("id2");
        assertThat(afterDelete).isNull();
    }

}