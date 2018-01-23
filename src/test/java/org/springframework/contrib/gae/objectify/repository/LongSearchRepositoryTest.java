package org.springframework.contrib.gae.objectify.repository;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.contrib.gae.objectify.TestLongEntity;
import org.springframework.contrib.gae.search.Operator;
import org.springframework.contrib.gae.search.query.Query;
import org.springframework.contrib.gae.search.query.Result;

import static org.assertj.core.api.Assertions.assertThat;

@SuppressWarnings("ConstantConditions")
public class LongSearchRepositoryTest extends AbstractLongRepositoryTest {

    @Autowired
    private SearchRepository<TestLongEntity, Long> repository;

    @Test
    public void save_willIndexInSearchService() {
        TestLongEntity target = new TestLongEntity(2L).setName("name2");
        repository.save(
                new TestLongEntity(1L).setName("name1"),
                target,
                new TestLongEntity(3L).setName("name3")
        );

        assertThat(searchByName("name2")).containsExactly(target);
    }

    @Test
    public void save_willUpdateIndex_whenSavedTwice() {
        TestLongEntity target = new TestLongEntity(2L).setName("name2");
        repository.save(target);

        assertThat(searchByName("name2"))
                .containsExactly(target);

        target.setName("name2-updated");
        repository.save(target);

        assertThat(searchByName("name2-updated"))
                .containsExactly(target);
        assertThat(searchByName("name2"))
                .isEmpty();
    }

    @Test
    public void delete_willRemoveEntryFromSearchService() {
        TestLongEntity target = new TestLongEntity(3L).setName("target");

        repository.save(
                new TestLongEntity(1L).setName("name1"),
                new TestLongEntity(2L).setName("name2"),
                target
        );

        assertThat(searchByName(target.getName())).containsExactly(target);

        repository.delete(target);
        assertThat(searchByName(target.getName())).isEmpty();
    }

    private Result<TestLongEntity> searchByName(String name) {
        Query<TestLongEntity> query = repository.search()
                .filter("name", Operator.EQ, name)
                .build();
        return repository.execute(query);
    }

}
