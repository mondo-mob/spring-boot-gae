package org.springframework.contrib.gae.search.config;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.contrib.gae.objectify.config.ObjectifyConfigurer;
import org.springframework.contrib.gae.search.TestSearchEntity;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableAutoConfiguration
public class SearchTestConfiguration implements ObjectifyConfigurer {

    @Override
    public List<Class<?>> registerObjectifyEntities() {
        return Arrays.asList(
                TestSearchEntity.class
        );
    }

}
