package org.springframework.contrib.gae.search.query;

import com.google.appengine.api.search.GeoPoint;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.contrib.gae.objectify.ObjectifyTest;
import org.springframework.contrib.gae.search.Operator;
import org.springframework.contrib.gae.search.TestSearchEntity;
import org.springframework.contrib.gae.search.conversion.DefaultSearchConversionService;
import org.springframework.contrib.gae.search.metadata.SearchMetadata;
import org.springframework.core.convert.ConversionService;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.data.domain.Sort.Direction.DESC;

public class QueryStringCompilerIntegrationTest extends ObjectifyTest {
    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Autowired
    private SearchMetadata searchMetadata;

    private ConversionService conversionService = new DefaultSearchConversionService();

    private QueryStringCompiler compiler;

    @Before
    public void setUp() {
        compiler = new QueryStringCompiler(searchMetadata, conversionService);
    }

    @Test
    public void apply() {
        Query<TestSearchEntity> query = query()
                .filter("stringField", Operator.EQUAL, "stringValue")
                .filter("longField", Operator.GREATER_THAN, 3)
                .filterIn("id", "id1", "id2")
                .filter("stringArrayField", Operator.STEM, "likeValue")
                .order("stringField", DESC)
                .skip(1)
                .accuracy(2)
                .limit(3)
                .build();

        assertThat(compiler.apply(query))
                .isEqualTo("stringField=\"stringValue\" longField>3.0 id:(\"id1\" OR \"id2\") stringArrayField=~\"likeValue\"");
    }

    @Test
    public void apply_geoPointField() {
        Query<TestSearchEntity> query = query()
                .filter("geoPointField", Operator.EQUAL, new GeoPoint(-33.8624313, 151.1956613))
                .build();

        assertThat(compiler.apply(query))
                .isEqualTo("geoPointField=geopoint(-33.8624313, 151.1956613)");
    }

    @Test
    public void apply_offsetDates() {
        Query<TestSearchEntity> query = query()
                .filter("offsetDateTimeField", Operator.EQUAL, OffsetDateTime.parse("2017-01-01T01:02:03Z"))
                .filter("offsetDateTimeField", Operator.EQUAL, OffsetDateTime.parse("2017-01-01T01:02:03.567Z"))
                .filter("offsetDateTimeAsDateField", Operator.EQUAL, OffsetDateTime.parse("2017-01-01T01:02:03Z"))
                .build();

        assertThat(compiler.apply(query))
                .isEqualTo("offsetDateTimeField=1483232.523 offsetDateTimeField=1483232.523567 offsetDateTimeAsDateField=2017-01-01");
    }

    @Test
    public void apply_zonedDates() {
        Query<TestSearchEntity> query = query()
                .filter("zonedDateTimeField", Operator.EQUAL, ZonedDateTime.parse("2017-01-01T01:02:03Z"))
                .filter("zonedDateTimeField", Operator.EQUAL, ZonedDateTime.parse("2017-01-01T01:02:03.567Z"))
                .filter("zonedDateTimeAsDateField", Operator.EQUAL, ZonedDateTime.parse("2017-01-01T01:02:03Z"))
                .build();

        assertThat(compiler.apply(query))
                .isEqualTo("zonedDateTimeField=1483232.523 zonedDateTimeField=1483232.523567 zonedDateTimeAsDateField=2017-01-01");
    }

    @Test
    public void apply_localDates() {
        Query<TestSearchEntity> query = query()
                .filter("localDateField", Operator.EQUAL, LocalDate.parse("2017-06-01"))
                .filter("localDateField", Operator.GREATER_THAN, LocalDate.parse("2017-06-01"))
                .build();

        assertThat(compiler.apply(query))
                .isEqualTo("localDateField=2017-06-01 localDateField>2017-06-01");
    }

    private QueryImpl<TestSearchEntity> query() {
        return new QueryImpl<>(TestSearchEntity.class);
    }
}
