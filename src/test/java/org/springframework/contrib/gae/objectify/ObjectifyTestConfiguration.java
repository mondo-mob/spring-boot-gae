package org.springframework.contrib.gae.objectify;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.contrib.gae.objectify.config.ObjectifyConfigurer;

import java.util.Arrays;
import java.util.List;

@Configuration
@ComponentScan("org.springframework.contrib.gae.objectify.repository")
@EnableAutoConfiguration
public class ObjectifyTestConfiguration implements ObjectifyConfigurer {
    @Override
    public List<Class<?>> registerObjectifyEntities() {
        return Arrays.asList(
                TestStringEntity.class,
                TestLongEntity.class,
                TestStringEntity.class
        );
    }
}
