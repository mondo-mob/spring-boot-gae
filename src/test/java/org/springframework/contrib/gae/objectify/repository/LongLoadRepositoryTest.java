package org.springframework.contrib.gae.objectify.repository;

import com.googlecode.objectify.Key;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.contrib.gae.objectify.TestLongEntity;
import org.springframework.contrib.gae.objectify.TestStringEntity;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@SuppressWarnings("ConstantConditions")
public class LongLoadRepositoryTest extends AbstractLongRepositoryTest {

    @Autowired
    protected LoadRepository<TestLongEntity, Long> repository;

    @Test
    public void findAll()  {
        TestLongEntity[] entities = fixture.get(3);
        ofy().save().entities(entities).now();

        assertThat(repository.findAll())
                .containsExactlyInAnyOrder(entities);
    }

    @Test
    public void findAll_willReturnEmptyList_whenThereAreNoEntities()  {
        assertThat(repository.findAll())
                .isEmpty();
    }

    @Test
    public void findAllKeys() {
        TestLongEntity[] entities = fixture.get(3);
        ofy().save().entities(entities).now();
        List<Key<TestLongEntity>> expectedKeys = Stream.of(entities).map(Key::create).collect(Collectors.toList());

        assertThat(repository.findAllKeys())
                .containsExactlyInAnyOrderElementsOf(expectedKeys);
    }

    @Test
    public void findAllKeys_willReturnEmptyList_whenThereAreNoEntities()  {
        assertThat(repository.findAllKeys())
                .isEmpty();
    }

    @Test
    public void findAllWithCount()  {
        TestLongEntity[] entities = fixture.get(3);
        ofy().save().entities(entities).now();

        List<TestLongEntity> result = repository.findAll(2);
        assertThat(result)
                .hasSize(2)
                .containsExactlyInAnyOrder(entities[0], entities[1]);
        result.forEach(entity -> assertThat(entities).contains(entity));
    }

    @Test
    public void findAllWithCount_willReturnEmptyList_whenThereAreNoEntities()  {
        assertThat(repository.findAll(69))
                .isEmpty();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void findAllKeysWithCount()  {
        TestLongEntity[] entities = fixture.get(3);
        ofy().save().entities(entities).now();
        Key<TestLongEntity>[] keys = Stream.of(entities).map(Key::create).toArray(Key[]::new);

        List<Key<TestLongEntity>> result = repository.findAllKeys(2);
        assertThat(result)
                .hasSize(2)
                .containsExactlyInAnyOrder(keys[0], keys[1]);
        result.forEach(key -> assertThat(keys).contains(key));
    }

    @Test
    public void findAllKeysWithCount_willReturnEmptyList_whenThereAreNoEntities()  {
        assertThat(repository.findAllKeys(69))
                .isEmpty();
    }

    @Test
    public void findAllCollection()  {
        TestLongEntity[] entities = fixture.get(3);
        ofy().save().entities(entities).now();

        List<TestLongEntity> result = repository.findAll(
                Arrays.asList(
                        Key.create(TestLongEntity.class, 1L),
                        Key.create(TestLongEntity.class, 2L),
                        Key.create(TestLongEntity.class, 3L)
                )
        );

        assertThat(result)
                .isNotNull()
                .hasSize(3)
                .containsExactly(entities);
    }

    @Test
    public void findAllCollection_willReturnEmpty_whenNoKeysArePassed()  {
        List<TestLongEntity> result = repository.findAll(
                Collections.emptyList()
        );

        assertThat(result)
                .isNotNull()
                .isEmpty();
    }

    @Test
    public void findAllCollection_willNotContainMissingEntities_whenKeyDoesNotExist()  {
        TestLongEntity[] entities = fixture.get(2);
        ofy().save().entities(entities).now();

       List<TestLongEntity> result = repository.findAll(
                Arrays.asList(
                        Key.create(TestLongEntity.class, 1L),
                        Key.create(TestLongEntity.class, 2L),
                        Key.create(TestLongEntity.class, 999L)
                )
        );

        assertThat(result)
                .isNotNull()
                .hasSize(2)
                .containsExactly(entities);
    }

    @Test
    public void findAllCollection_willThrowException_whenInputIsNull()  {
        thrown.expect(NullPointerException.class);
        repository.findAll((Collection<Key<TestLongEntity>>) null);
    }

    @Test
    public void findAllCollection_willThrowException_whenInputContainsNull()  {
        thrown.expect(NullPointerException.class);
        repository.findAll(
                Arrays.asList(
                        Key.create(TestLongEntity.class, 1L),
                        Key.create(TestLongEntity.class, 2L),
                        null
                )
        );
    }

    @Test
    @SuppressWarnings("unchecked")
    public void findAllVarargs()  {
        TestLongEntity[] entities = fixture.get(3);
        ofy().save().entities(entities).now();

        List<TestLongEntity> result = repository.findAll(
                Key.create(TestLongEntity.class, 1L),
                Key.create(TestLongEntity.class, 2L),
                Key.create(TestLongEntity.class, 3L)
        );

        assertThat(result)
                .isNotNull()
                .hasSize(3)
                .containsExactly(entities);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void findAllVarargs_willReturnEmpty_whenNoKeysArePassed()  {
        List<TestLongEntity> result = repository.findAll((Key<TestLongEntity>[]) new Key[]{});

        assertThat(result)
                .isNotNull()
                .isEmpty();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void findAllVarargs_willNotContainMissingEntities_whenKeyDoesNotExist()  {
        TestLongEntity[] entities = fixture.get(2);
        ofy().save().entities(entities).now();

        List<TestLongEntity> result = repository.findAll(
                Key.create(TestLongEntity.class, 1L),
                Key.create(TestLongEntity.class, 2L),
                Key.create(TestLongEntity.class, 999L)
        );

        assertThat(result)
                .isNotNull()
                .hasSize(2)
                .containsExactly(entities);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void findAllVarargs_willThrowException_whenInputIsNull()  {
        thrown.expect(NullPointerException.class);
        repository.findAll((Key<TestLongEntity>) null);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void findAllVarargs_willThrowException_whenInputContainsNull()  {
        thrown.expect(NullPointerException.class);
        repository.findAll(
                Key.create(TestLongEntity.class, 1L),
                Key.create(TestLongEntity.class, 2L),
                null
        );
    }

    @Test
    public void findAllByField()  {
        TestLongEntity[] entities = fixture.get(3);
        entities[0].setName("Bob");
        entities[1].setName("Bob");
        entities[2].setName("Sue");

        ofy().save().entities(entities).now();

        assertThat(repository.findAllByField("name", "Bob"))
                .containsExactlyInAnyOrder(entities[0], entities[1])
                .doesNotContain(entities[2]);
    }

    @Test
    public void findAllByField_willNotReturnMatches_whenCaseIsMismatched()  {
        TestLongEntity[] entities = fixture.get(3);
        entities[0].setName("Bob");
        entities[1].setName("bob");
        entities[2].setName("BoB");

        ofy().save().entities(entities).now();

        assertThat(repository.findAllByField("name", "Bob"))
                .containsExactlyInAnyOrder(entities[0])
                .doesNotContain(entities[1], entities[2]);
    }

    @Test
    public void findAllByField_willThrowException_whenFieldIsNull()  {
        thrown.expect(NullPointerException.class);

        repository.findAllByField(null, "Bob");
    }

    @Test
    public void findAllByField_willHandleNullSearch()  {
        TestLongEntity[] entities = fixture.get(3);
        entities[0].setName(null);
        entities[1].setName("Bob");
        entities[2].setName("Sue");

        ofy().save().entities(entities).now();

        assertThat(repository.findAllByField("name", (Object) null))
                .contains(entities[0])
                .doesNotContain(entities[1], entities[2]);
    }

    @Test
    public void findAllByField_willReturnEmptyList_whenThereAreNoMatches()  {
        TestLongEntity[] entities = fixture.get(3);
        entities[0].setName("Mark");
        entities[1].setName("Bob");
        entities[2].setName("Sue");

        ofy().save().entities(entities).now();

        assertThat(repository.findAllByField("name", "Greg"))
                .isEmpty();
    }

    @Test
    public void findAllByField_willNotFail_whenSearchTypeDoesNotMatchFieldType()  {
        TestLongEntity[] entities = fixture.get(3);
        entities[0].setName("Mark");
        entities[1].setName("Bob");
        entities[2].setName("Sue");

        ofy().save().entities(entities).now();

        assertThat(repository.findAllByField("name", 1L))
                .isEmpty();
    }

    @Test
    public void findAllByFieldCollection()  {
        TestLongEntity[] entities = fixture.get(5);
        entities[0].setName("Bob");
        entities[1].setName("Bob");
        entities[2].setName("Sue");
        entities[3].setName("Mark");
        entities[4].setName("Greg");

        ofy().save().entities(entities).now();

        assertThat(repository.findAllByField("name", Arrays.asList("Bob", "Mark")))
                .containsExactlyInAnyOrder(entities[0], entities[1], entities[3])
                .doesNotContain(entities[2], entities[4]);
    }

    @Test
    public void findAllByFieldCollection_willReturnEmptyList_whenFieldIsNull()  {
        TestLongEntity[] entities = fixture.get(5);
        entities[0].setName("Bob");
        entities[1].setName("Tabatha");
        entities[2].setName("Sue");
        entities[4].setName("Greg");

        ofy().save().entities(entities).now();
        List<TestLongEntity> result = repository.findAllByField(null, Arrays.asList("Bob", "Tabatha"));

        assertThat(result).isEmpty();
    }


    @Test
    public void findAllByFieldCollection_willHandleNullSearch()  {
        TestLongEntity[] entities = fixture.get(5);
        entities[0].setName(null);
        entities[1].setName(null);
        entities[2].setName("Sue");
        entities[3].setName("Mark");
        entities[4].setName("Greg");

        ofy().save().entities(entities).now();

        assertThat(repository.findAllByField("name", Arrays.asList("Mark", null)))
                .containsExactlyInAnyOrder(entities[0], entities[1], entities[3])
                .doesNotContain(entities[2], entities[4]);
    }

    @Test
    public void findAllByFieldCollection_willReturnEmptyList_whenThereAreNoMatches()  {
        TestLongEntity[] entities = fixture.get(3);
        entities[0].setName("Mark");
        entities[1].setName("Bob");
        entities[2].setName("Sue");

        ofy().save().entities(entities).now();

        assertThat(repository.findAllByField("name", Arrays.asList("Greg", "Tabatha")))
                .isEmpty();
    }

    @Test
    public void findAllByFieldCollection_willNotFail_whenSearchTypeDoesNotMatchFieldType()  {
        TestLongEntity[] entities = fixture.get(3);
        entities[0].setName("Mark");
        entities[1].setName("Bob");
        entities[2].setName("Sue");

        ofy().save().entities(entities).now();

        assertThat(repository.findAllByField("name", Arrays.asList(1L, 2L)))
                .isEmpty();
    }

    @Test
    public void findAllByFieldVarargs()  {
        TestLongEntity[] entities = fixture.get(5);
        entities[0].setName("Bob");
        entities[1].setName("Bob");
        entities[2].setName("Sue");
        entities[3].setName("Mark");
        entities[4].setName("Greg");

        ofy().save().entities(entities).now();

        assertThat(repository.findAllByField("name", "Bob", "Mark"))
                .containsExactlyInAnyOrder(entities[0], entities[1], entities[3])
                .doesNotContain(entities[2], entities[4]);
    }

    @Test
    public void findAllByFieldVarargs_willReturnEmptyList_whenFieldIsNull()  {
        TestLongEntity[] entities = fixture.get(5);
        entities[0].setName("Bob");
        entities[1].setName("Tabatha");
        entities[3].setName("Mark");
        entities[4].setName("Greg");

        ofy().save().entities(entities).now();

        List<TestLongEntity> result = repository.findAllByField(null, "Bob", "Tabatha");
        assertThat(result.isEmpty());
    }


    @Test
    public void findAllByFieldVarargs_willHandleNullSearch()  {
        TestLongEntity[] entities = fixture.get(5);
        entities[0].setName(null);
        entities[1].setName(null);
        entities[2].setName("Sue");
        entities[3].setName("Mark");
        entities[4].setName("Greg");

        ofy().save().entities(entities).now();

        assertThat(repository.findAllByField("name", "Mark", null))
                .containsExactlyInAnyOrder(entities[0], entities[1], entities[3])
                .doesNotContain(entities[2], entities[4]);
    }

    @Test
    public void findAllByFieldVarargs_willReturnEmptyList_whenThereAreNoMatches()  {
        TestLongEntity[] entities = fixture.get(3);
        entities[0].setName("Mark");
        entities[1].setName("Bob");
        entities[2].setName("Sue");

        ofy().save().entities(entities).now();

        assertThat(repository.findAllByField("name", "Greg", "Tabatha"))
                .isEmpty();
    }

    @Test
    public void findAllByFieldVarargs_willNotFail_whenSearchTypeDoesNotMatchFieldType()  {
        TestLongEntity[] entities = fixture.get(3);
        entities[0].setName("Mark");
        entities[1].setName("Bob");
        entities[2].setName("Sue");

        ofy().save().entities(entities).now();

        assertThat(repository.findAllByField("name", 1L, 2L))
                .isEmpty();
    }

    @Test
    public void findByWebSafeKey()  {
        TestLongEntity entity = new TestLongEntity(1L).setName("the name");
        ofy().save().entity(entity).now();

        Optional<TestLongEntity> result = repository.findByWebSafeKey(Key.create(TestLongEntity.class, 1L).toWebSafeString());
        assertThat(result.get())
                .isNotNull()
                .hasFieldOrPropertyWithValue("id", 1L)
                .hasFieldOrPropertyWithValue("name", "the name");
    }

    @Test
    public void findByWebSafeKey_willReturnEmptyOptional_whenKeyDoesNotExist()  {
        Optional<TestLongEntity> result = repository.findByWebSafeKey(Key.create(TestLongEntity.class, 999L).toWebSafeString());

        assertThat(result.isPresent()).isEqualTo(false);
    }

    @Test
    public void findByKey()  {
        TestLongEntity entity = new TestLongEntity(1L).setName("the name");
        ofy().save().entity(entity).now();

        Optional<TestLongEntity> result = repository.findByKey(Key.create(TestLongEntity.class, 1L));
        assertThat(result.get())
                .isNotNull()
                .hasFieldOrPropertyWithValue("id", 1L)
                .hasFieldOrPropertyWithValue("name", "the name");
    }

    @Test
    public void findByKey_willReturnEmptyOptional_whenKeyDoesNotExist()  {
        Optional<TestLongEntity> result = repository.findByKey(Key.create(TestLongEntity.class, 999L));

        assertThat(result.isPresent()).isEqualTo(false);
    }


    @Test
    public void findByKey_willThrowException_whenInputIsNull()  {
        thrown.expect(NullPointerException.class);
        repository.findByKey(null);
    }

    @Test
    public void getByKey()  {
        TestLongEntity entity = new TestLongEntity(1L).setName("the name");
        ofy().save().entity(entity).now();

        TestLongEntity result = repository.getByKey(Key.create(TestLongEntity.class, 1L));
        assertThat(result)
                .isNotNull()
                .hasFieldOrPropertyWithValue("id", 1L)
                .hasFieldOrPropertyWithValue("name", "the name");
    }

    @Test
    public void getByKey_willThrowException_whenEntityNotFound()  {
        thrown.expect(EntityNotFoundException.class);
        thrown.expectMessage("No entity was found matching the key: Key<?>(TestLongEntity(999)");

        repository.getByKey(Key.create(TestLongEntity.class, 999L));
    }    
    
}
