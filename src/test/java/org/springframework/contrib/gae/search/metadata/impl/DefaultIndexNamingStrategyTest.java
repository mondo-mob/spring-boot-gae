package org.springframework.contrib.gae.search.metadata.impl;

import com.googlecode.objectify.annotation.Entity;
import org.junit.Test;
import org.springframework.contrib.gae.search.metadata.IndexNamingStrategy;

import static org.assertj.core.api.Assertions.assertThat;

public class DefaultIndexNamingStrategyTest {
    private IndexNamingStrategy strategy = new DefaultIndexNamingStrategy();

    @Test
    public void apply_returnsClassSimpleName_whenNoEntityNameOnClass() {
        assertThat(strategy.apply(String.class)).isEqualTo("String");
        assertThat(strategy.apply(DefaultIndexNamingStrategy.class)).isEqualTo("DefaultIndexNamingStrategy");
        assertThat(strategy.apply(InnerClass.class)).isEqualTo("InnerClass");
        assertThat(strategy.apply(InnerClassWithEntity.class)).isEqualTo("InnerClassWithEntity");
    }

    @Test
    public void apply_returnsEntityName_whenPresent() {
        assertThat(strategy.apply(InnerClassWithEntityName.class)).isEqualTo("EntityAlias");
    }

    private static final class InnerClass {

    }

    @Entity
    private static final class InnerClassWithEntity {

    }

    @Entity(name = "EntityAlias")
    private static final class InnerClassWithEntityName {

    }
}