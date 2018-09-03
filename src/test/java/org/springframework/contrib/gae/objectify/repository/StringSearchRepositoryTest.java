package org.springframework.contrib.gae.objectify.repository;

import com.googlecode.objectify.Key;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.contrib.gae.objectify.TestStringEntity;
import org.springframework.contrib.gae.search.Operator;
import org.springframework.contrib.gae.search.query.Query;
import org.springframework.contrib.gae.search.query.Result;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@SuppressWarnings("ConstantConditions")
public class StringSearchRepositoryTest extends AbstractStringRepositoryTest {

    @Autowired
    private SearchRepository<TestStringEntity, String> repository;

    @Test
    public void save_willIndexInSearchService() {
        TestStringEntity target = new TestStringEntity("id2").setName("name2");

        repository.save(
                new TestStringEntity("id1").setName("name1"),
                target,
                new TestStringEntity("id3").setName("name3")
        );

        assertSearchByName("name2", target);

        repository.findAll().forEach(e -> assertThat(e.isReindexed()).isEqualTo(false));
    }

    @Test
    public void save_willUpdateIndex_whenSavedTwice() {
        TestStringEntity target = new TestStringEntity("id2").setName("name2");
        repository.save(target);

        assertSearchByName("name2", target);

        target.setName("name2-updated");
        repository.save(target);

        assertSearchByName("name2-updated", target);

        assertThat(searchByName("name2"))
                .isEmpty();

        repository.findAll().forEach(e -> assertThat(e.isReindexed()).isEqualTo(false));
    }

    @Test
    public void delete_willRemoveEntryFromSearchService() {
        TestStringEntity target = new TestStringEntity("id3").setName("target");

        repository.save(
                new TestStringEntity("id1").setName("name1"),
                new TestStringEntity("id2").setName("name2"),
                target
        );

        assertSearchByName(target.getName(), target);

        repository.delete(target);

        assertThat(searchByName(target.getName()))
                .isEmpty();
    }

    @Test
    public void clearSearchIndex_willRemoveAllSearchEntries() {
        TestStringEntity target = new TestStringEntity("id3").setName("target");

        repository.save(
                new TestStringEntity("id1").setName("name1"),
                new TestStringEntity("id2").setName("name2"),
                target
        );

        assertSearchByName(target.getName(), target);

        repository.clearSearchIndex();

        assertThat(searchByName(target.getName()))
                .isEmpty();
        assertThat(searchByName("name1"))
                .isEmpty();
        assertThat(searchByName("name2"))
                .isEmpty();

        repository.findAll().forEach(e -> assertThat(e.isReindexed()).isEqualTo(false));
    }

    @Test
    public void reindexDataAndSearch_willReindex() {
        repository.save(
                new TestStringEntity("id1").setName("name1"),
                new TestStringEntity("id2").setName("name2")
        );
        repository.clearSearchIndex();

        assertThat(searchByName("name1"))
                .isEmpty();

        repository.reindexDataAndSearch();

        assertThat(searchByName("name1"))
                .isNotEmpty();
        assertThat(searchByName("name2"))
                .isNotEmpty();

        repository.findAll().forEach(e -> assertThat(e.isReindexed()).isEqualTo(true));
    }

    @Test
    public void reindex_willUpdateEntities_whenReindexOperationChangesValues() {
        repository.save(
                new TestStringEntity("id1").setName("name1"),
                new TestStringEntity("id2").setName("name2")
        );

        repository.reindex((vals) ->
                vals.stream()
                        .peek(v -> v.setName(v.getName() + "-updated"))
                        .collect(Collectors.toList()));

        assertThat(searchByName("name1-updated"))
                .isNotEmpty();
        assertThat(searchByName("name2-updated"))
                .isNotEmpty();

        assertThat(searchByName("name1"))
                .isEmpty();
        assertThat(searchByName("name2"))
                .isEmpty();

        repository.findAll().forEach(e -> assertThat(e.isReindexed()).isEqualTo(true));
    }

    private void assertSearchByName(String name, TestStringEntity... expectedEntities) {
        Result<TestStringEntity> searchResult = searchByName(name);
        assertThat(searchResult).containsExactly(expectedEntities);
        assertThat(searchResult.getKeys()).containsAll(
                Stream.of(expectedEntities)
                        .map(Key::create)
                        .collect(Collectors.toList())
        );
    }

    private Result<TestStringEntity> searchByName(String name) {
        Query<TestStringEntity> query = repository.search()
                .filter("name", Operator.EQ, name)
                .build();
        return repository.execute(query);
    }

}
