package org.springframework.contrib.gae.search.query;

import org.springframework.contrib.gae.objectify.ObjectifyTest;
import org.springframework.contrib.gae.search.Operator;
import org.springframework.contrib.gae.search.TestSearchEntity;
import org.springframework.contrib.gae.search.conversion.DefaultSearchConversionService;
import org.springframework.contrib.gae.search.metadata.SearchMetadata;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;

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
    public void setUp()  {
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
                .isEqualTo("stringField=\"stringValue\" longField>\"3\" id:(\"id1\" OR \"id2\") stringArrayField=~\"likeValue\"");
    }

    private QueryImpl<TestSearchEntity> query() {
        return new QueryImpl<>(TestSearchEntity.class);
    }
}
