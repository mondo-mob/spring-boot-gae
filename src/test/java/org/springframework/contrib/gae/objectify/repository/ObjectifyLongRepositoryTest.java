package org.springframework.contrib.gae.objectify.repository;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.contrib.gae.objectify.TestLongEntity;
import org.springframework.contrib.gae.objectify.TestStringEntity;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

public class ObjectifyLongRepositoryTest extends AbstractLongRepositoryTest {

    @Autowired
    protected ObjectifyLongRepository<TestLongEntity> repository;

    @Test
    public void findById()  {
        TestLongEntity entity = new TestLongEntity(1L).setName("the name");
        ofy().save().entity(entity).now();

        Optional<TestLongEntity> result = repository.findById(1L);
        assertThat(result.get())
                .isNotNull()
                .hasFieldOrPropertyWithValue("id", 1L)
                .hasFieldOrPropertyWithValue("name", "the name");
    }

    @Test
    public void findById_willReturnEmptyOptional_whenIdDoesNotExist()  {
        Optional<TestLongEntity> result = repository.findById(999L);

        assertThat(result.isPresent()).isEqualTo(false);
    }

    @Test
    public void getById()  {
        TestLongEntity entity = new TestLongEntity(1L).setName("the name");
        ofy().save().entity(entity).now();

        TestLongEntity result = repository.getById(1L);
        assertThat(result)
                .isNotNull()
                .hasFieldOrPropertyWithValue("id", 1L)
                .hasFieldOrPropertyWithValue("name", "the name");
    }

    @Test
    public void getById_willThrowException_whenEntityNotFound()  {
        thrown.expect(EntityNotFoundException.class);
        thrown.expectMessage("No entity was found matching the key: Key<?>(TestLongEntity(999)");

        repository.getById(999L);
    }

    @Test
    public void delete()  {
        TestLongEntity[] entities = fixture.get(3);
        ofy().save().entities(entities).now();

        TestLongEntity beforeDelete = load(2L);
        assertThat(beforeDelete).isNotNull();
        assertThat(beforeDelete.getName()).isEqualTo("entity2");

        repository.delete(beforeDelete.getId());

        TestLongEntity afterDelete = load(2L);
        assertThat(afterDelete).isNull();
    }    

}