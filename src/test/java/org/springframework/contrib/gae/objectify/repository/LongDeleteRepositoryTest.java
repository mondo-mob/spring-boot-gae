package org.springframework.contrib.gae.objectify.repository;

import com.googlecode.objectify.Key;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.contrib.gae.objectify.TestLongEntity;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SuppressWarnings("ConstantConditions")
public class LongDeleteRepositoryTest extends AbstractLongRepositoryTest {

    @Autowired
    private DeleteRepository<TestLongEntity, Long> repository;

    @Test
    public void delete()  {
        TestLongEntity[] entities = fixture.get(3);
        ofy().save().entities(entities).now();

        TestLongEntity beforeDelete = load(2L);
        Assertions.assertThat(beforeDelete).isNotNull();
        Assertions.assertThat(beforeDelete.getName()).isEqualTo("entity2");
        repository.delete(beforeDelete);

        TestLongEntity afterDelete = load(2L);
        Assertions.assertThat(afterDelete).isNull();
    }

    @Test
    public void delete_willThrowException_whenInputIsNull()  {
        thrown.expect(NullPointerException.class);
        repository.delete((TestLongEntity) null);
    }

    @Test
    public void delete_willThrowException_whenInputIdIsNull()  {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("You cannot create a Key for an object with a null @Id");
        repository.delete(new TestLongEntity(null));
    }

    @Test
    public void deleteCollection()  {
        TestLongEntity[] entities = fixture.get(3);
        ofy().save().entities(entities).now();

        List<TestLongEntity> listBeforeDelete = ofy().load().type(TestLongEntity.class).list();
        Assertions.assertThat(listBeforeDelete).hasSize(3);

        repository.delete(
                Arrays.asList(entities[0], entities[1])
        );

        List<TestLongEntity> listAfterDelete = ofy().load().type(TestLongEntity.class).list();
        Assertions.assertThat(listAfterDelete).hasSize(1);
        Assertions.assertThat(listAfterDelete).containsExactly(entities[2]);
    }

    @Test
    public void deleteCollection_willThrowException_whenInputContainsNull()  {
        thrown.expect(NullPointerException.class);
        repository.delete(
                Arrays.asList(
                        new TestLongEntity(1L),
                        new TestLongEntity(2L),
                        null
                )
        );
    }

    @Test
    public void deleteCollection_willThrowException_whenInputContainsEntityWithoutId()  {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("You cannot create a Key for an object with a null @Id");
        repository.delete(
                Arrays.asList(
                        new TestLongEntity(1L),
                        new TestLongEntity(2L),
                        new TestLongEntity(null)
                )
        );
    }

    @Test
    public void deleteVarargs()  {
        TestLongEntity[] entities = fixture.get(3);
        ofy().save().entities(entities).now();

        List<TestLongEntity> listBeforeDelete = ofy().load().type(TestLongEntity.class).list();
        Assertions.assertThat(listBeforeDelete).hasSize(3);

        repository.delete(
                entities[0],
                entities[1]
        );

        List<TestLongEntity> listAfterDelete = ofy().load().type(TestLongEntity.class).list();
        Assertions.assertThat(listAfterDelete).hasSize(1);
        Assertions.assertThat(listAfterDelete).containsExactly(entities[2]);
    }

    @Test
    public void deleteVarargs_willThrowException_whenInputContainsNull()  {
        thrown.expect(NullPointerException.class);
        repository.delete(
                new TestLongEntity(1L),
                new TestLongEntity(2L),
                null
        );
    }

    @Test
    public void deleteVarargs_willThrowException_whenInputContainsEntityWithoutId()  {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("You cannot create a Key for an object with a null @Id");
        repository.delete(
                new TestLongEntity(1L),
                new TestLongEntity(2L),
                new TestLongEntity(null)
        );
    }

    @Test
    public void deleteByKey()  {
        TestLongEntity[] entities = fixture.get(3);
        ofy().save().entities(entities).now();

        TestLongEntity beforeDelete = load(2L);
        assertThat(beforeDelete).isNotNull();
        assertThat(beforeDelete.getName()).isEqualTo("entity2");
        repository.deleteByKey(Key.create(TestLongEntity.class, 2L));

        TestLongEntity afterDelete = load(2L);
        assertThat(afterDelete).isNull();
    }

    @Test
    public void deleteByKey_willThrowException_whenInputIsNull()  {
        thrown.expect(NullPointerException.class);
        repository.delete((TestLongEntity) null);
    }

    @Test
    public void deleteByKey_willThrowException_whenInputIdIsNull()  {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("You cannot create a Key for an object with a null @Id");
        repository.delete(new TestLongEntity(null));
    }

    @Test
    public void deleteByKeyCollection()  {
        TestLongEntity[] entities = fixture.get(3);
        ofy().save().entities(entities).now();

        List<TestLongEntity> listBeforeDelete = ofy().load().type(TestLongEntity.class).list();
        assertThat(listBeforeDelete).hasSize(3);

        repository.deleteByKey(
                Arrays.asList(
                        Key.create(TestLongEntity.class, 1L),
                        Key.create(TestLongEntity.class, 2L)
                )
        );

        List<TestLongEntity> listAfterDelete = ofy().load().type(TestLongEntity.class).list();
        assertThat(listAfterDelete).hasSize(1);
        assertThat(listAfterDelete).containsExactly(entities[2]);
    }

    @Test
    public void deleteByKeyCollection_willThrowException_whenInputContainsNull()  {
        thrown.expect(NullPointerException.class);
        repository.deleteByKey(
                Arrays.asList(
                        Key.create(TestLongEntity.class, 1L),
                        Key.create(TestLongEntity.class, 2L),
                        null
                )
        );
    }

    @SuppressWarnings("unchecked")
    @Test
    public void deleteByKeyVarargs()  {
        TestLongEntity[] entities = fixture.get(3);
        ofy().save().entities(entities).now();

        List<TestLongEntity> listBeforeDelete = ofy().load().type(TestLongEntity.class).list();
        assertThat(listBeforeDelete).hasSize(3);

        repository.deleteByKey(
                Key.create(TestLongEntity.class, 1L),
                Key.create(TestLongEntity.class, 2L)
        );

        List<TestLongEntity> listAfterDelete = ofy().load().type(TestLongEntity.class).list();
        assertThat(listAfterDelete).hasSize(1);
        assertThat(listAfterDelete).containsExactly(entities[2]);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void deleteByKeyVarargs_willThrowException_whenInputContainsNull()  {
        thrown.expect(NullPointerException.class);
        repository.deleteByKey(
                Key.create(TestLongEntity.class, 1L),
                Key.create(TestLongEntity.class, 2L),
                null
        );
    }
}
