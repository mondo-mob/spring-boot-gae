package org.springframework.contrib.gae.search.query;

import com.google.appengine.api.search.QueryOptions;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.springframework.contrib.gae.search.TestSearchEntity;
import org.springframework.contrib.gae.search.metadata.SearchMetadata;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class QueryOptionsCompilerTest {
    @InjectMocks
    private QueryOptionsCompiler queryOptionsCompiler;

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Mock
    private SearchMetadata searchMetadata;

    @Test
    public void apply_willAddSkipToLimit_whenBothSupplied() {

        Query<TestSearchEntity> query = query()
                .skip(10)
                .limit(99)
                .build();

        QueryOptions options = queryOptionsCompiler.apply(query);

        assertThat(options.getLimit(), is(109));
        verify(searchMetadata, never()).getDefaultLimit();
    }

    @Test
    public void apply_willSetLimitDefault_whenLimitNotSupplied() {
        int defaultLimit = 99;
        when(searchMetadata.getDefaultLimit()).thenReturn(defaultLimit);

        Query<TestSearchEntity> query = query()
                .build();

        QueryOptions options = queryOptionsCompiler.apply(query);

        assertThat(options.getLimit(), is(defaultLimit));
    }

    @Test
    public void apply_willSetLimitDefault_andAddSkip_whenLimitNotSupplied_andSkipSupplied() {
        int defaultLimit = 99;
        when(searchMetadata.getDefaultLimit()).thenReturn(defaultLimit);

        Query<TestSearchEntity> query = query()
                .skip(10)
                .build();

        QueryOptions options = queryOptionsCompiler.apply(query);

        assertThat(options.getLimit(), is(defaultLimit + 10));
    }

    private QueryImpl<TestSearchEntity> query() {
        return new QueryImpl<>(TestSearchEntity.class);
    }

}