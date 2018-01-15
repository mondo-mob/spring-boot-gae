package org.springframework.contrib.gae.search.metadata.impl;

import org.junit.Test;
import org.springframework.contrib.gae.search.metadata.IndexNamingStrategy;

import static org.assertj.core.api.Assertions.assertThat;

public class DefaultIndexNamingStrategyTest {
    private IndexNamingStrategy strategy = new DefaultIndexNamingStrategy();

    @Test
    public void apply() {
        assertThat(strategy.apply(String.class)).isEqualTo("String");
        assertThat(strategy.apply(DefaultIndexNamingStrategy.class)).isEqualTo("DefaultIndexNamingStrategy");
        assertThat(strategy.apply(InnerClass.class)).isEqualTo("InnerClass");
    }

    private static final class InnerClass {

    }
}