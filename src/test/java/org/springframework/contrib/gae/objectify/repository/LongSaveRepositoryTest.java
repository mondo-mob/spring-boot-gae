package org.springframework.contrib.gae.objectify.repository;

import org.assertj.core.api.Assertions;
import org.assertj.core.api.SoftAssertions;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.contrib.gae.objectify.TestLongEntity;

import java.util.Arrays;
import java.util.List;

@SuppressWarnings("ConstantConditions")
public class LongSaveRepositoryTest extends AbstractLongRepositoryTest {

    @Autowired
    private SaveRepository<TestLongEntity, Long> repository;

    @Test
    public void save()  {
        TestLongEntity saved = repository.save(new TestLongEntity(1L).setName("name"));

        TestLongEntity loaded = load(1L);

        Assertions.assertThat(loaded.getId()).isEqualTo(saved.getId());
        Assertions.assertThat(loaded.getName()).isEqualTo(saved.getName());
    }

    @Test
    public void save_willThrowException_whenInputIsNull()  {
        thrown.expect(NullPointerException.class);

        repository.save((TestLongEntity) null);
    }

    @Test
    public void save_willGenerateLongId_whenInputHasNoId()  {
        TestLongEntity saved = repository.save(new TestLongEntity(null).setName("name"));

        TestLongEntity loaded = load(1L);

        Assertions.assertThat(loaded.getId()).isEqualTo(saved.getId());
        Assertions.assertThat(loaded.getName()).isEqualTo(saved.getName());
    }

    @Test
    public void saveCollection()  {
        List<TestLongEntity> saved = repository.save(
                Arrays.asList(fixture.get(3))
        );
        Assertions.assertThat(saved).hasSize(3);

        verifyTestEntityCollectionSaved();
    }

    @Test
    public void saveCollection_willGenerateLongIds_whenInputContainsEntityWithoutId()  {
        List<TestLongEntity> entities = Arrays.asList(
                new TestLongEntity(null).setName("entity1"),
                new TestLongEntity(null).setName("entity2"),
                new TestLongEntity(null).setName("entity3")
        );

        List<TestLongEntity> saved = repository.save(entities);
        Assertions.assertThat(saved).hasSize(3);

        verifyTestEntityCollectionSaved();
    }

    @Test
    public void saveVarargs()  {

        List<TestLongEntity> saved = repository.save(
                new TestLongEntity(1L).setName("entity1"),
                new TestLongEntity(2L).setName("entity2"),
                new TestLongEntity(3L).setName("entity3")
        );
        Assertions.assertThat(saved).hasSize(3);

        verifyTestEntityCollectionSaved();
    }

    @Test
    public void saveVarargs_willGenerateLongIds_whenInputContainsEntityWithoutId()  {
        List<TestLongEntity> saved = repository.save(
                new TestLongEntity(null).setName("entity1"),
                new TestLongEntity(null).setName("entity2"),
                new TestLongEntity(null).setName("entity3")
        );
        Assertions.assertThat(saved).hasSize(3);

        verifyTestEntityCollectionSaved();
    }

    @SuppressWarnings("Duplicates")
    protected void verifyTestEntityCollectionSaved() {
        SoftAssertions softly = new SoftAssertions();

        TestLongEntity loaded1 = load(1L);
        softly.assertThat(loaded1.getId()).isEqualTo(1L);
        softly.assertThat(loaded1.getName()).isEqualTo("entity1");

        TestLongEntity loaded2 = load(2L);
        softly.assertThat(loaded2.getId()).isEqualTo(2L);
        softly.assertThat(loaded2.getName()).isEqualTo("entity2");

        TestLongEntity loaded3 = load(3L);
        softly.assertThat(loaded3.getId()).isEqualTo(3L);
        softly.assertThat(loaded3.getName()).isEqualTo("entity3");

        softly.assertAll();
    }
}
