package org.springframework.contrib.gae.objectify.config;

import com.googlecode.objectify.annotation.Entity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.util.StopWatch;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Scan all classes annotated with {@link Entity} within a base package or its descendants. Allows
 * configuring additional classes manually that may fall outside of this package (e.g. from external libraries).
 */
public class ObjectifyEntityScanner {
    private static final Logger LOG = LoggerFactory.getLogger(ObjectifyEntityScanner.class);

    private final String basePackage;
    private final Set<Class<?>> additionalClasses = new HashSet<>();

    public ObjectifyEntityScanner(String basePackage) {
        this.basePackage = basePackage;
    }

    public ObjectifyEntityScanner withAdditionalClasses(Class... classes) {
        Stream.of(classes)
                .forEach(additionalClasses::add);
        return this;
    }

    public Set<Class<?>> getEntityClasses() {
        return Stream.concat(getAnnotatedClasses(), additionalClasses.stream())
                .collect(Collectors.toSet());
    }

    private Stream<Class<?>> getAnnotatedClasses() {
        return loggedAction("scan entity classes", () -> {
            final ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false);
            scanner.addIncludeFilter(new AnnotationTypeFilter(Entity.class, false, false));
            return scanner.findCandidateComponents(basePackage).stream()
                    .map(this::beanClass);
        });
    }

    private <T> T loggedAction(String name, Supplier<T> action) {
        StopWatch stopWatch = new StopWatch();
        if (LOG.isDebugEnabled()) {
            stopWatch.start(name);
        }
        T result = action.get();
        if (LOG.isDebugEnabled()) {
            stopWatch.stop();
            LOG.debug("{} took {} milliseconds", name, stopWatch.getTotalTimeMillis());
        }

        return result;
    }

    @SuppressWarnings("unchecked")
    private Class<?> beanClass(BeanDefinition beanDefinition) {
        try {
            return Class.forName(beanDefinition.getBeanClassName());
        } catch (ClassNotFoundException e) {
            throw new InitializationException(String.format("Class not found %s", beanDefinition.getBeanClassName()), e);
        }
    }
}
