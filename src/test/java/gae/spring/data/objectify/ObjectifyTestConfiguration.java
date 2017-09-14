package gae.spring.data.objectify;

import gae.spring.data.objectify.config.ObjectifyConfigurer;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableAutoConfiguration
public class ObjectifyTestConfiguration implements ObjectifyConfigurer {
    @Override
    public List<Class<?>> registerObjectifyEntities() {
        return Arrays.asList(
                TestStringEntity.class,
                TestLongEntity.class
        );
    }
}
