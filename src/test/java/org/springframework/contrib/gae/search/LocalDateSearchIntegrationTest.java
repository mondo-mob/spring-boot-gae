package org.springframework.contrib.gae.search;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.contrib.gae.search.query.Query;

import java.time.LocalDate;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.contrib.gae.search.Operator.EQUAL;
import static org.springframework.contrib.gae.search.Operator.GREATER_THAN;
import static org.springframework.contrib.gae.search.Operator.GREATER_THAN_OR_EQUAL;

public class LocalDateSearchIntegrationTest extends SearchTest {

    @Autowired
    private SearchService searchService;

    @Test
    public void findADocumentByExplicitMatch() {
        searchService.index(Arrays.asList(
                new TestEntity("id1", LocalDate.parse("2017-01-01")),
                new TestEntity("id2", LocalDate.parse("2017-01-02")),
                new TestEntity("id3", LocalDate.parse("2017-01-03"))
        ));

        Query<TestEntity> query = searchService.createQuery(TestEntity.class)
                .filter("value", EQUAL, LocalDate.parse("2017-01-02"))
                .build();

        assertThat(searchService.execute(query))
                .extractingResultOf("getId")
                .containsExactly("id2");
    }

    @Test
    public void findADocumentByGreaterThanMatch() {
        searchService.index(Arrays.asList(
                new TestEntity("id1", LocalDate.parse("2017-01-01")),
                new TestEntity("id2", LocalDate.parse("2017-01-02")),
                new TestEntity("id3", LocalDate.parse("2017-01-02")),
                new TestEntity("id4", LocalDate.parse("2017-01-03"))
        ));

        Query<TestEntity> query = searchService.createQuery(TestEntity.class)
                .filter("value", GREATER_THAN, LocalDate.parse("2017-01-02"))
                .build();

        assertThat(searchService.execute(query))
                .extractingResultOf("getId")
                .containsExactlyInAnyOrder("id4");
    }

    @Test
    public void findADocumentByGreaterThanOrEqualMatch() {
        searchService.index(Arrays.asList(
                new TestEntity("id1", LocalDate.parse("2017-01-01")),
                new TestEntity("id2", LocalDate.parse("2017-01-02")),
                new TestEntity("id3", LocalDate.parse("2017-01-02")),
                new TestEntity("id4", LocalDate.parse("2017-01-03"))
        ));

        Query<TestEntity> query = searchService.createQuery(TestEntity.class)
                .filter("value", GREATER_THAN_OR_EQUAL, LocalDate.parse("2017-01-02"))
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
        private LocalDate value;

        public TestEntity(String id, LocalDate value) {
            this.id = id;
            this.value = value;
        }
    }
}
