package org.springframework.contrib.gae.search;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.contrib.gae.search.query.Query;

import java.time.ZonedDateTime;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.contrib.gae.search.Operator.EQUAL;
import static org.springframework.contrib.gae.search.Operator.GREATER_THAN;
import static org.springframework.contrib.gae.search.Operator.GREATER_THAN_OR_EQUAL;

public class ZonedDateTimeSearchIntegrationTest extends SearchTest {

    @Autowired
    private SearchService searchService;

    @Test
    public void findADocumentByExplicitMatch() {
        searchService.index(Arrays.asList(
                new TestEntity("id1", ZonedDateTime.parse("2017-01-01T01:02:03Z")),
                new TestEntity("id2", ZonedDateTime.parse("2017-01-02T01:02:03Z")),
                new TestEntity("id3", ZonedDateTime.parse("2017-01-03T01:02:03Z"))
        ));

        Query<TestEntity> query = searchService.createQuery(TestEntity.class)
                .filter("value", EQUAL, ZonedDateTime.parse("2017-01-02T01:02:03Z"))
                .build();

        assertThat(searchService.execute(query))
                .extractingResultOf("getId")
                .containsExactly("id2");
    }

    @Test
    public void findADocumentByGreaterThanMatch() {
        searchService.index(Arrays.asList(
                new TestEntity("id1", ZonedDateTime.parse("2017-01-01T01:02:03Z")),
                new TestEntity("id2", ZonedDateTime.parse("2017-01-02T01:02:03Z")),
                new TestEntity("id3", ZonedDateTime.parse("2017-01-02T01:02:04Z")),
                new TestEntity("id4", ZonedDateTime.parse("2017-01-03T01:02:03Z"))
        ));

        Query<TestEntity> query = searchService.createQuery(TestEntity.class)
                .filter("value", GREATER_THAN, ZonedDateTime.parse("2017-01-02T01:02:03Z"))
                .build();

        assertThat(searchService.execute(query))
                .extractingResultOf("getId")
                .containsExactlyInAnyOrder("id4");
    }

    @Test
    public void findADocumentByGreaterThanOrEqualMatch() {
        searchService.index(Arrays.asList(
                new TestEntity("id1", ZonedDateTime.parse("2017-01-01T01:02:03Z")),
                new TestEntity("id2", ZonedDateTime.parse("2017-01-02T01:02:03Z")),
                new TestEntity("id3", ZonedDateTime.parse("2017-01-02T01:02:04Z")),
                new TestEntity("id4", ZonedDateTime.parse("2017-01-03T01:02:03Z"))
        ));

        Query<TestEntity> query = searchService.createQuery(TestEntity.class)
                .filter("value", GREATER_THAN_OR_EQUAL, ZonedDateTime.parse("2017-01-02T01:02:03Z"))
                .build();

        assertThat(searchService.execute(query))
                .extractingResultOf("getId")
                .containsExactlyInAnyOrder("id2", "id3", "id4");
    }

    @SuppressWarnings("unused")
    private static final class TestEntity {

        @SearchId
        private String id;

        @SearchIndex
        private ZonedDateTime value;

        public TestEntity(String id, ZonedDateTime value) {
            this.id = id;
            this.value = value;
        }
    }
}