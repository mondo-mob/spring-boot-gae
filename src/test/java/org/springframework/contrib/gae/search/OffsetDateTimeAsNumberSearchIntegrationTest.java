package org.springframework.contrib.gae.search;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.contrib.gae.search.query.Query;

import java.time.OffsetDateTime;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.contrib.gae.search.Operator.EQUAL;
import static org.springframework.contrib.gae.search.Operator.GREATER_THAN;
import static org.springframework.contrib.gae.search.Operator.GREATER_THAN_OR_EQUAL;

public class OffsetDateTimeAsNumberSearchIntegrationTest extends SearchTest {

	@Autowired
	private SearchService searchService;

	@Test
	public void findADocumentByExplicitMatch() {
		searchService.index(Arrays.asList(
			new TestEntity("id1", OffsetDateTime.parse("2017-01-01T01:02:03Z")),
			new TestEntity("id2", OffsetDateTime.parse("2017-01-02T01:02:03Z")),
			new TestEntity("id3", OffsetDateTime.parse("2017-01-03T01:02:03Z"))
		));

		Query<TestEntity> query = searchService.createQuery(TestEntity.class)
			.filter("value", EQUAL, OffsetDateTime.parse("2017-01-02T01:02:03Z"))
			.build();

		assertThat(searchService.execute(query))
			.extractingResultOf("getId")
			.containsExactly("id2");
	}

	@Test
	public void findADocumentByExplicitMatch_MilliPrecision() {
		searchService.index(Arrays.asList(
			new TestEntity("id1", OffsetDateTime.parse("2050-01-01T01:02:03.100Z")),
			new TestEntity("id2", OffsetDateTime.parse("2050-01-01T01:02:03.101Z")),
			new TestEntity("id3", OffsetDateTime.parse("2050-01-01T01:02:03.102Z"))
		));

		Query<TestEntity> query = searchService.createQuery(TestEntity.class)
			.filter("value", EQUAL, OffsetDateTime.parse("2050-01-01T01:02:03.101Z"))
			.build();

		assertThat(searchService.execute(query))
			.extractingResultOf("getId")
			.containsExactly("id2");
	}

	@Test
	public void findADocumentByGreaterThanMatch_withSecondPrecision() {
		searchService.index(Arrays.asList(
			new TestEntity("id1", OffsetDateTime.parse("2017-01-01T01:02:03Z")),
			new TestEntity("id2", OffsetDateTime.parse("2017-01-02T01:02:03Z")),
			new TestEntity("id3", OffsetDateTime.parse("2017-01-02T01:02:04Z")),
			new TestEntity("id4", OffsetDateTime.parse("2017-01-03T01:02:03Z"))
		));

		Query<TestEntity> query = searchService.createQuery(TestEntity.class)
			.filter("value", GREATER_THAN, OffsetDateTime.parse("2017-01-02T01:02:03Z"))
			.build();

		assertThat(searchService.execute(query))
			.extractingResultOf("getId")
			.containsExactlyInAnyOrder("id3", "id4");
	}

	@Test
	public void findADocumentByGreaterThanMatch_withMilliPrecision() {
		searchService.index(Arrays.asList(
			new TestEntity("id1", OffsetDateTime.parse("2017-01-02T01:02:03.123Z")),
			new TestEntity("id2", OffsetDateTime.parse("2017-01-02T01:02:03.124Z")),
			new TestEntity("id3", OffsetDateTime.parse("2017-01-02T01:02:03.125Z")),
			new TestEntity("id4", OffsetDateTime.parse("2017-01-02T01:02:03.126Z"))
		));

		Query<TestEntity> query = searchService.createQuery(TestEntity.class)
			.filter("value", GREATER_THAN, OffsetDateTime.parse("2017-01-02T01:02:03.124Z"))
			.build();

		assertThat(searchService.execute(query))
			.extractingResultOf("getId")
			.containsExactlyInAnyOrder("id3", "id4");
	}

	@Test
	public void findADocumentByGreaterThanOrEqualMatch() {
		searchService.index(Arrays.asList(
			new TestEntity("id1", OffsetDateTime.parse("2017-01-01T01:02:03Z")),
			new TestEntity("id2", OffsetDateTime.parse("2017-01-02T01:02:03Z")),
			new TestEntity("id3", OffsetDateTime.parse("2017-01-02T01:02:04Z")),
			new TestEntity("id4", OffsetDateTime.parse("2017-01-03T01:02:03Z"))
		));

		Query<TestEntity> query = searchService.createQuery(TestEntity.class)
			.filter("value", GREATER_THAN_OR_EQUAL, OffsetDateTime.parse("2017-01-02T01:02:03Z"))
			.build();

		assertThat(searchService.execute(query))
			.extractingResultOf("getId")
			.containsExactlyInAnyOrder("id2", "id3", "id4");
	}

	@Test
	public void findADocumentByExplicitMatchLongAgo() {
		searchService.index(Arrays.asList(
			new TestEntity("id1", OffsetDateTime.parse("0500-01-02T01:02:01Z")),
			new TestEntity("id2", OffsetDateTime.parse("0500-01-02T01:02:02Z")),
			new TestEntity("id3", OffsetDateTime.parse("0500-01-02T01:02:03Z"))
		));

		Query<TestEntity> query = searchService.createQuery(TestEntity.class)
			.filter("value", EQUAL, OffsetDateTime.parse("0500-01-02T01:02:02Z"))
			.build();

		assertThat(searchService.execute(query))
			.extractingResultOf("getId")
			.containsExactly("id2");
	}

	@Test
	public void findADocumentByExplicitMatchFarIntoFuture() {
		searchService.index(Arrays.asList(
			new TestEntity("id1", OffsetDateTime.parse("3018-01-02T01:02:01Z")),
			new TestEntity("id2", OffsetDateTime.parse("3018-01-02T01:02:02Z")),
			new TestEntity("id3", OffsetDateTime.parse("3018-01-02T01:02:03Z"))
		));

		Query<TestEntity> query = searchService.createQuery(TestEntity.class)
			.filter("value", EQUAL, OffsetDateTime.parse("3018-01-02T01:02:02Z"))
			.build();

		assertThat(searchService.execute(query))
			.extractingResultOf("getId")
			.containsExactly("id2");
	}

	@SuppressWarnings("unused")
	private static final class TestEntity {

		@SearchId
		private String id;

		@SearchIndex(type = IndexType.NUMBER)
		private OffsetDateTime value;

		public TestEntity(String id, OffsetDateTime value) {
			this.id = id;
			this.value = value;
		}
	}
}
