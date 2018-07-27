package org.springframework.contrib.gae.search.query;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.springframework.contrib.gae.search.IndexType;
import org.springframework.contrib.gae.search.Operator;
import org.springframework.contrib.gae.search.TestSearchEntity;
import org.springframework.contrib.gae.search.metadata.SearchFieldMetadata;
import org.springframework.contrib.gae.search.metadata.SearchMetadata;
import org.springframework.contrib.gae.search.metadata.impl.FieldSearchFieldMetadata;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.TypeDescriptor;

import java.lang.reflect.Field;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

public class QueryFragmentCompilerTest {

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Mock
    private SearchMetadata searchMetadata;

    @Mock
    private ConversionService conversionService;

    private QueryFragmentCompiler compiler;

    @Before
    public void setUp() throws Exception {
        compiler = new QueryFragmentCompiler(TestSearchEntity.class, searchMetadata, conversionService);

        Field field = TestSearchEntity.class.getDeclaredField("stringField");
        SearchFieldMetadata searchFieldMetadata = new FieldSearchFieldMetadata(TestSearchEntity.class, field, IndexType.TEXT);

        when(searchMetadata.getField(TestSearchEntity.class, "stringField")).thenReturn(searchFieldMetadata);
        when(conversionService.convert(anyString(), eq(String.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(conversionService.convert(any(String[].class), any(TypeDescriptor.class), any(TypeDescriptor.class)))
                .thenAnswer(invocation -> Arrays.asList(invocation.getArgument(0)));
    }

    @Test
    public void apply_willConvert_whenInputIsRawFragment() {
        Query.Fragment fragment = new ValueFragment("Some filter");

        assertThat(compiler.apply(fragment)).isEqualTo("Some filter");
    }

    @Test
    public void apply_willConvert_whenInputIsPredicateFragment() {
        Query.Fragment fragment = new PredicateQueryFragment("stringField", Operator.EQUAL, "value");

        assertThat(compiler.apply(fragment)).isEqualTo("stringField=\"value\"");
    }

    @Test
    public void apply_willConvert_whenInputIsPredicateFragmentWithQuotes() {
        Query.Fragment fragment = new PredicateQueryFragment("stringField", Operator.EQUAL, "va\"l\"ue");

        assertThat(compiler.apply(fragment)).isEqualTo("stringField=\"va\\\"l\\\"ue\"");
    }

    @Test
    public void apply_willConvert_whenInputIsPredicateFragmentWithArrayValue() {
        Query.Fragment fragment = new PredicateQueryFragment("stringField", Operator.EQUAL, new String[]{"value1", "value2", "value3"});

        assertThat(compiler.apply(fragment)).isEqualTo("stringField:(\"value1\" OR \"value2\" OR \"value3\")");
    }

    @Test
    public void apply_willConvert_whenInputIsPredicateFragmentWithCollectionValue() {
        Query.Fragment fragment = new PredicateQueryFragment("stringField", Operator.EQUAL, Arrays.asList("value1", "value2", "value3"));

        assertThat(compiler.apply(fragment)).isEqualTo("stringField:(\"value1\" OR \"value2\" OR \"value3\")");
    }

    @Test
    public void apply_willConvert_whenInputIsPredicateFragmentWithCollectionValueWithQuotes() {
        Query.Fragment fragment = new PredicateQueryFragment("stringField", Operator.EQUAL, new String[]{"value1", "value2", "\"quote\""});

        assertThat(compiler.apply(fragment)).isEqualTo("stringField:(\"value1\" OR \"value2\" OR \"\\\"quote\\\"\")");
    }
}
